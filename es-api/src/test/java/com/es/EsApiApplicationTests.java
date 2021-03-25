package com.es;

import com.alibaba.fastjson.JSON;
import com.es.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.io.IOException;

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //创建索引
    @Test
    void createIndex() throws IOException {
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("jd_goods");
        //执行请求
        CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(request);
    }

    //获取索引
    @Test
    void getIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("kuang_index");
        boolean response = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }

    //删除索引
    @Test
    void deleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test3");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
    }

    //添加文档
    @Test
    void addDoc() throws IOException {
        User user = new User("狂神说", 3);
        IndexRequest request = new IndexRequest("kuang_index");

        //规则 put /kuang_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //将对象转成json，封装请求
        request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求
        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }

    //获取文档，判断是否存在
    @Test
    void isExistsDoc() throws IOException {
        GetRequest request = new GetRequest("kuang_index", "1");
        //不获取返回的_source上下文
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");

        System.out.println(restHighLevelClient.exists(request, RequestOptions.DEFAULT));
    }

    //获取文档信息
    @Test
    void getDoc() throws IOException {
        GetRequest request = new GetRequest("kuang_index", "1");
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        //打印文档内容
        System.out.println(response.getSourceAsString());
    }

    //更新文档信息
    @Test
    void updateDoc() throws IOException {
        UpdateRequest request = new UpdateRequest("kuang_index", "1");
        request.timeout("1s");
        User user = new User("狂神说java", 31);
        request.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
        //打印文档内容
        System.out.println(response);
    }

    //删除
    @Test
    void deleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("kuang_index", "1");
        DeleteResponse delete = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    //批量插入数据
    @Test
    void bulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        List<User> arrayList = new ArrayList<>();
        arrayList.add(new User("a", 1));
        arrayList.add(new User("aa", 11));
        arrayList.add(new User("aaa", 111));
        arrayList.add(new User("aaaa", 1111));
        //批处理
        for (int i = 0; i < arrayList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("kuang_index")
                            .id("" + (i + 1))
                            .source(JSON.toJSONString(arrayList.get(i)), XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    //查询
    //searchRequest 搜索请求
    //SearchSourceBuilder 条件构造
    //HighlightBuilder 高亮
    //TermQueryBuilder 精确查询
    @Test
    void search() throws IOException {
        SearchRequest searchRequest = new SearchRequest("kuang_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //查询条件
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "a");
        sourceBuilder.query(termQueryBuilder);

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);

        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(search.getHits()));
        System.out.println("=======");
        for (SearchHit documentFields : search.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }

    }

}
