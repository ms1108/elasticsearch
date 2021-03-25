package com.es.utils;

import com.es.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {
    public static void main(String[] args) throws Exception {
        String url = "https://search.jd.com/Search?keyword=java";
        //解析网页(返回的就是js页面对象)
        Document document = Jsoup.parse(new URL(url), 3000);
        //js里的所有返回
        Element element = document.getElementById("J_goodsList");

        //获取所有的li标签
        Elements li = element.getElementsByTag("li");
        for (Element el : li) {
            //图片特别多的网站都是延迟加载的
            //String img = el.getElementsByTag("img").eq(0).attr("src");
            //要拿懒加载的图片
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            System.out.println("========");
            System.out.println(img);
            System.out.println(price);
            System.out.println(title);
        }

        new HtmlParseUtil().parseJd("java").forEach(System.out::println);
    }

    public List<Content> parseJd(String keyword) throws Exception {
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        Document document = Jsoup.parse(new URL(url), 3000);
        Element element = document.getElementById("J_goodsList");
        Elements li = element.getElementsByTag("li");
        List<Content> goodsList = new ArrayList<>();

        for (Element el : li) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content = new Content(title, img, price);
            goodsList.add(content);
        }
        return goodsList;
    }
}
