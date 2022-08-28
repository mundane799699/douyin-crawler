package com.mundane.douyincrawler.utils;

import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    public static void downloadVideo(Video video) {
        String originVideoAddress = video.getVideoAddress();
        String desc = video.getDesc();
        if (video.getWidth() > 720) {
            String videoAddress1080 = originVideoAddress.replace("720p", "1080p");
            if (!download(videoAddress1080, desc)) {
                download(originVideoAddress, desc);
            }
        } else {
            download(originVideoAddress, desc);
        }
    }

    private static boolean download(String videoAddress, String desc) {
        System.out.println("videoAddress = " + videoAddress);
        try {
            Connection.Response document = Jsoup.connect(videoAddress)
                    .ignoreContentType(true)
                    .maxBodySize(30000000)
                    .timeout(10000)
                    .execute();
            BufferedInputStream stream = document.bodyStream();
            File fileSavePath = new File("D:/douyin/" + desc + ".mp4");
            // 如果保存文件夹不存在,那么则创建该文件夹
            File fileParent = fileSavePath.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (fileSavePath.exists()) { //如果文件存在，则删除原来的文件
                fileSavePath.delete();
            }
            FileUtils.copyInputStreamToFile(stream, fileSavePath);
            //此方法建议可以使用下面copy大文件的方法性能会更好些
            //IOUtils.copyLarge(stream, new FileOutputStream("D:\\123.mp4"));
            System.out.println("\n-----视频保存路径-----\n" + fileSavePath.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
