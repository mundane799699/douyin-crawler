package com.mundane.douyincrawler;

import com.mundane.douyincrawler.utils.ParseUtil;

public class PatchDownload {


    public static void main(String[] args) {
        String text = "9- 长按复制此条消息，打开抖音搜索，查看TA的更多作品。 https://v.douyin.com/UNrayGp/";
        String url = ParseUtil.parseUrl(text);
        if (url == null) {
            System.out.println("url is null");
            return;
        }
        System.out.println("主页url = " + url);
        String location = ParseUtil.getLocation(url);
        if (location == null) {
            System.out.println("location is null");
            return;
        }
        System.out.println("location = " + location);
        String secUid = ParseUtil.getSecUid(location);
        System.out.println("secUid = " + secUid);
        if (secUid == null) {
            System.out.println("secUid is null");
        }
        String listUrl = "https://www.iesdouyin.com/web/api/v2/aweme/post/?reflow_source=reflow_page&sec_uid=" + secUid + "&count=21&max_cursor=0";
        String listJsonStr = ParseUtil.getJsonStr(listUrl);
        if (listJsonStr == null) {
            System.out.println("jsonStr is null");
            return;
        }
        System.out.println("jsonStr = " + listJsonStr);
    }

}
