package com.mundane.douyincrawler;


import com.mundane.douyincrawler.utils.HttpUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;


public class HttpUtilsTest {
    private static final String TEST_URL = "https://www.douyin.com/user/MS4wLjABAAAAiK0274pDg-CiVvDA1ZINZBuQ6iZFet-gKVbmnNyBHLY";
    @Test
    public void testGetHtmlPageResponseAsDocument() {
        HttpUtils httpUtils = HttpUtils.getInstance();
        httpUtils.setTimeout(30000);
        httpUtils.setWaitForBackgroundJavaScript(30000);
        try {
            Document document = httpUtils.getHtmlPageResponseAsDocument(TEST_URL);
            //TODO
            //System.out.println(document);

            Element element = document.getElementById("pagelet-user-info");//获取元素节点等

            //System.out.println(element);
            System.out.println("头像url："+element.getElementsByTag("img").attr("src"));
            System.out.println("昵称："+element.getElementsByTag("p").get(0).text());
            System.out.println(element.getElementsByTag("p").get(1).text());
            System.out.println("签名："+element.getElementsByTag("p").get(2).text());

            System.out.println("关注："+element.getElementsByTag("p").get(3).getElementsByAttributeValue("class", "num").get(0).text());
            System.out.println("粉丝："+element.getElementsByTag("p").get(3).getElementsByAttributeValue("class", "num").get(1).text());
            System.out.println("赞："+element.getElementsByTag("p").get(3).getElementsByAttributeValue("class", "num").get(2).text());

            System.out.println("作品："+element.getElementsByAttributeValue("class", "video-tab").first().getElementsByAttributeValue("class", "num").get(0).text());
            System.out.println("喜欢："+element.getElementsByAttributeValue("class", "video-tab").first().getElementsByAttributeValue("class", "num").get(1).text());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


