package com.mundane.douyincrawler.utils;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtil {

    public static Map<String, String> headers = new HashMap<String, String>() {{
        put("user-agent", "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36 Edg/110.0.1587.41'");
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

    public static JSONObject parseVideoId(String url) {
        try {
            Document document = Jsoup.connect(url).headers(headers).get();
            String location = document.location();
            System.out.println("localtion1 = " + location);
            headers.put("cookie", "ttwid=1|Udxr5TJi8rMkH6nyoGdUyWPxRbrRfhDHSmYNNhCN06s|1675693365|85039e91f5d9b2db99b49de1f2caf02019ddea8ac163a56638a3b181aa0b8675;__ac_nonce=063ecb18600d85d0e99a8;__ac_signature=_02B4Z6wo00f016FV-JAAAIDDIVcC04mr9BOhdfwAAIvC64;__ac_referer=__ac_blank");
            document = Jsoup.connect(location).headers(headers).get();
            location = document.location();
            System.out.println("location2 = " + location);

            if (location.contains("?")) {
                location = location.split("\\?")[0];
            }
            System.out.println("location = " + location);
            headers.put("referer", "https://www.iesdouyin.com/");
            document = Jsoup
                    .connect(url)
                    .headers(headers)
                    .ignoreContentType(true)
                    .get();
            Element element = document.selectFirst("script#RENDER_DATA[type=application/json]");
            String html = element.html();
            String decodedStr = URLDecoder.decode(html, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(decodedStr);
            for (Map.Entry<String, Object> entry : json.entrySet()) {
                System.out.println(entry.getKey());
                Object value = entry.getValue();
                if (value instanceof JSONObject) {
                    JSONObject obj = (JSONObject) value;
                    if (obj.containsKey("aweme")) {
                        return obj;
                    }
                }
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
