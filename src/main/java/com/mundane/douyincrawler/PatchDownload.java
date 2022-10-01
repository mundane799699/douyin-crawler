package com.mundane.douyincrawler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import com.mundane.douyincrawler.utils.ParseUtil;

import java.util.List;

public class PatchDownload {
    public static final String PREFIX = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";


    public static void main(String[] args) {
        String text = "1- 长按复制此条消息，打开抖音搜索，查看TA的更多作品。 https://v.douyin.com/6vkr2XG/";
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
        JSONObject json = new JSONObject(listJsonStr);
        JSONArray awemeList = json.getJSONArray("aweme_list");
        for (Object o : awemeList) {
            JSONObject jsonObject = (JSONObject) o;
            String awemeId = jsonObject.getStr("aweme_id");
            DownloadUtils.downloadVideoOrPic(awemeId);
        }
    }

}
