package com.mundane.douyincrawler;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.utils.DownloadUtils;
import com.mundane.douyincrawler.utils.ParseUtil;

public class PatchDownload {
    public static final String PREFIX = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";


    public static void main(String[] args) {
        String text = "0.02 HII:/ 复制打开抖音，看看【一个阿锐的作品】# 只属于秋季的浪漫 # 风和自由 爱到底有没有必要在... https://v.douyin.com/M1TT76n/";
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
