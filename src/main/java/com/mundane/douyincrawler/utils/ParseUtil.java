package com.mundane.douyincrawler.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

    public static Map<String, String> headers = new HashMap<String, String>() {{
        put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        put("pragma", "no-cache");
        put("sec-ch-ua-platform", "\"Windows\"");
    }};

    public static String parseUrl(String text) {
        String regex = "https://v.douyin.com[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String url = matcher.group();
            return url;
        }
        return null;
    }

    public static String parseVideoId(String url) {
        try {
            Document document = Jsoup.connect(url).headers(headers).get();
            String location = document.location();
            System.out.println("localtion = " + location);
            String regex = "\\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(location);
            if (matcher.find()) {
                String videoId = matcher.group();
                return videoId;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocation(String url) {
        try {
            Document document = Jsoup.connect(url).headers(headers).get();
            String location = document.location();
            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSecUid(String location) {
        String regex = "https://www.douyin.com/user/[-A-Za-z0-9+&@#/%=~_|!:,.;]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(location);
        if (matcher.find()) {
            String url = matcher.group();
            return url.replace("https://www.douyin.com/user/", "");
        }
        return null;
    }

    public static String getJsonStr(String url) {
        System.out.println("json url = " + url);
        try {
            String body = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .execute()
                    .body();
            return body;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Video getVideo(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        String videoAddress = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list").get(0).toString();
        Integer width = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video").getInt("width");
        Integer height = json.getJSONArray("item_list").getJSONObject(0).getJSONObject("video").getInt("height");
        if (videoAddress == null) {
            System.out.println("videoAddress is null");
            return null;
        }
        videoAddress = videoAddress.replace("playwm", "play");
        String desc = json.getJSONArray("item_list").getJSONObject(0).getStr("desc");
        Video video = new Video();
        video.setVideoAddress(videoAddress);
        video.setDesc(desc);
        video.setWidth(width);
        video.setHeight(height);
        return video;
    }

    public static int getAwemeType(String jsonStr) {
        JSONObject json = new JSONObject(jsonStr);
        JSONArray itemList = json.getJSONArray("item_list");
        JSONObject jsonObject = itemList.getJSONObject(0);
        int awesomeType = jsonObject.getInt("aweme_type");
        return awesomeType;
    }

    public static List<String> getPicList(String jsonStr) {
        List<String> picList = new ArrayList<>();

        JSONObject json = new JSONObject(jsonStr);
        JSONArray images = json.getJSONArray("item_list").getJSONObject(0).getJSONArray("images");
        for (int i = 0; i < images.size(); i++) {
            JSONObject jsonObject = images.getJSONObject(i);
            JSONArray urlList = jsonObject.getJSONArray("url_list");
            picList.add(urlList.get(0).toString());
        }
        return picList;
    }


}
