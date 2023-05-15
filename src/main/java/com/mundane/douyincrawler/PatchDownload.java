package com.mundane.douyincrawler;

import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.utils.ParseUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PatchDownload {


    public static void main(String[] args) throws Exception {
        String text = "9- 长按复制此条消息，打开抖音搜索，查看TA的更多作品。 https://v.douyin.com/UNrayGp/";
        String url = ParseUtil.parseUrl(text);
        if (url == null) {
            System.out.println("url is null");
            return;
        }
        System.out.println("url = " + url);
        String awesomeUrl = ParseUtil.getAwesomeUrl(url);
        if (awesomeUrl == null) {
            System.out.println("awesomeUrl is null");
            return;
        }
        System.out.println("awesomeUrl = " + awesomeUrl);
        JSONObject data = ParseUtil.getData(awesomeUrl);

        JSONObject postInfo = ParseUtil.getPostInfo(data);
        JSONObject post = postInfo.getJSONObject("post");
        String uid = postInfo.getStr("uid");
        System.out.println(uid);
        Long maxCursor = post.getLong("maxCursor");
        System.out.println(maxCursor);

        Map<String, String> payload = new LinkedHashMap<>();
        payload.put("device_platform", "webapp");
        payload.put("aid", "6383");
        payload.put("channel", "channel_pc_web");
        payload.put("sec_user_id", uid);
        payload.put("max_cursor", maxCursor.toString());
        payload.put("locate_query", "false");
        payload.put("show_live_replay_strategy", "1");
        payload.put("count", "10");
        payload.put("publish_video_strategy_type", "2");
        payload.put("pc_client_type", "1");
        payload.put("version_code", "170400");
        payload.put("version_name", "17.4.0");
        payload.put("cookie_enabled", "true");
        payload.put("screen_width", "1920");
        payload.put("screen_height", "1080");
        payload.put("browser_language", "zh-CN");
        payload.put("browser_platform", "Win32");
        payload.put("browser_name", "Chrome");
        payload.put("browser_version", "109.0.0.0");
        payload.put("browser_online", "true");
        payload.put("engine_name", "Blink");
        payload.put("engine_version", "109.0.0.0");
        payload.put("os_name", "Windows");
        payload.put("os_version", "10");
        payload.put("cpu_core_num", "8");
        payload.put("device_memory", "8");
        payload.put("platform", "PC");
        payload.put("downlink", "10");
        payload.put("effective_type", "4g");
        payload.put("round_trip_time", "50");
        String queryParams = getQueryParams(payload);
        System.out.println(queryParams);
        String xBogus = ParseUtil.getXb(queryParams);
        System.out.println(xBogus);
        payload.put("X-Bogus", xBogus);
        ParseUtil.getPageInfo(payload, awesomeUrl);

    }

    public static String getQueryParams(Map<String, String> params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
