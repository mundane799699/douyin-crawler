package com.mundane.douyincrawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mundane.douyincrawler.utils.ParseUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DouyinUserCrawler {
    public static Map<String, String> headers = new HashMap<String, String>() {{
        put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        put("pragma", "no-cache");
        put("sec-ch-ua-platform", "\"Windows\"");
    }};


    // 使用手机api去请求，sec_uid是用户主页id, max_cursor在上一次的url里有返回
    // https://www.iesdouyin.com/web/api/v2/aweme/post/?reflow_source=reflow_page&sec_uid=MS4wLjABAAAAx-t6bo_IckMYskYOczLJ1cScZLtMUv2B9f-hNxkI37M&count=21&max_cursor=1661342704000
    // https://www.iesdouyin.com/web/api/v2/aweme/post/?reflow_source=reflow_page&sec_uid=MS4wLjABAAAAx-t6bo_IckMYskYOczLJ1cScZLtMUv2B9f-hNxkI37M&count=21&max_cursor=0

    private static final int timeout = 30000;

    public static void main(String[] args) {
//        String userUrl = "https://www.douyin.com/user/MS4wLjABAAAAiK0274pDg-CiVvDA1ZINZBuQ6iZFet-gKVbmnNyBHLY";
//        String userUrl = "https://v.douyin.com/6bFUWMG/";
        String userUrl = "https://v.douyin.com/6bFUWMG/";
        try {
            //构造一个webClient 模拟Chrome 浏览器
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            //支持JavaScript
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(timeout);
            webClient.setJavaScriptTimeout(timeout);//设置JS执行的超时时间

            HtmlPage page = webClient.getPage(userUrl);
            //设置一个运行JavaScript的时间
//            webClient.waitForBackgroundJavaScript(10000);
            String html = page.asXml();
            webClient.close();
//            Document document = Jsoup.parse(html);

            String regex = "https://www.douyin.com/user/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                String url = matcher.group();
                // https://www.douyin.com/user/MS4wLjABAAAAx-t6bo_IckMYskYOczLJ1cScZLtMUv2B9f-hNxkI37M
                System.out.println(url);
            }
//            System.out.println(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
