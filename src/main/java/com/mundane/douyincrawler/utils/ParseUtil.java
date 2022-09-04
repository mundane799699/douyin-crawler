package com.mundane.douyincrawler.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    public static void downloadVideo(Video video) {
        String originVideoAddress = video.getVideoAddress();
        String desc = video.getDesc();
        String videoAddress1080 = originVideoAddress.replace("720p", "1080p");
        if (getContentLengthByAddress(videoAddress1080) > getContentLengthByAddress(originVideoAddress)) {
            download(videoAddress1080, desc);
        } else {
            download(originVideoAddress, desc);
        }
    }

    private static int getContentLengthByAddress(String videoAddress) {
        int contentLength = 0;
        try {
            Connection.Response document = Jsoup.connect(videoAddress)
                    .ignoreContentType(true)
                    .headers(headers)
                    .timeout(10000)
                    .execute();
            contentLength = Integer.parseInt(document.header("Content-Length"));
            System.out.println("contentLength = " + contentLength);
            return contentLength;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentLength;
    }

    private static boolean download(String videoAddress, String desc) {
        System.out.println("videoAddress = " + videoAddress);
        try {
            Connection.Response document = Jsoup.connect(videoAddress)
                    .ignoreContentType(true)
                    .maxBodySize(30000000)
                    .timeout(30000)
                    .execute();
            BufferedInputStream intputStream = document.bodyStream();
            int contentLength = Integer.parseInt(document.header("Content-Length"));
            File fileSavePath = new File("D:/douyin/" + desc + ".mp4");
            // 如果保存文件夹不存在,那么则创建该文件夹
            File fileParent = fileSavePath.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (fileSavePath.exists()) { //如果文件存在，则删除原来的文件
                fileSavePath.delete();
            }
            FileOutputStream fs = new FileOutputStream(fileSavePath);
            byte[] buffer = new byte[8 * 1024];
            int byteRead;
            int count = 0;
            while ((byteRead = intputStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
                count += byteRead;
                int progress = (int) (count * 100.0 / contentLength);
            }
            intputStream.close();
            fs.close();
            System.out.println("\n-----视频保存路径-----\n" + fileSavePath.getAbsolutePath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    public static void downloadPic(String videoId, List<String> picList) {
        for (int index = 0; index < picList.size(); index++) {
            downloadPic(videoId, index, picList.get(index));
        }
        System.out.println("图片下载完成");
    }

    public static void downloadPic(String videoId, int index, String picUrl) {
        try {
            Connection.Response document = Jsoup.connect(picUrl)
                    .ignoreContentType(true)
                    .maxBodySize(30000000)
                    .timeout(10000)
                    .execute();
            BufferedInputStream intputStream = document.bodyStream();
            File fileSavePath = new File("D:/douyin/" + videoId + "_" + index + ".png");
            // 如果保存文件夹不存在,那么则创建该文件夹
            File fileParent = fileSavePath.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (fileSavePath.exists()) { //如果文件存在，则删除原来的文件
                fileSavePath.delete();
            }
            FileOutputStream fs = new FileOutputStream(fileSavePath);
            byte[] buffer = new byte[8 * 1024];
            int byteRead;
            while ((byteRead = intputStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            intputStream.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
