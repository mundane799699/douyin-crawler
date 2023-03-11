package com.mundane.douyincrawler.utils;

import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.dto.Video;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadUtils {
    private static final String PREFIX = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

    private static final int TYPE_VIDEO = 4;
    private static final int TYPE_PIC = 2;

    public static Map<String, String> headers = new HashMap<String, String>() {{
        put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        put("pragma", "no-cache");
        put("sec-ch-ua-platform", "\"Windows\"");
    }};

    public static void downloadVideoOrPic(String awemeId) {
        String url = PREFIX + awemeId;
        String videJsonStr = ParseUtil.getJsonStr(url);
        if (videJsonStr == null) {
            System.out.println("jsonStr is null");
        }
        int awemeType = ParseUtil.getAwemeType(videJsonStr);
        System.out.println("awesomeType = " + awemeType);
        if (awemeType == TYPE_VIDEO) {
            Video video = ParseUtil.getVideo(videJsonStr);
            if (video == null) {
                System.out.println("video is null");
                return;
            }
            downloadVideo(video);
        } else if (awemeType == TYPE_PIC) {
            List<String> picList = ParseUtil.getPicList(videJsonStr);
            downloadPic(awemeId, picList);
        }
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
                    .maxBodySize(0)
                    .timeout(0)
                    .execute();
            BufferedInputStream intputStream = document.bodyStream();
            int contentLength = Integer.parseInt(document.header("Content-Length"));
            File fileSavePath = new File("/Users/mundane/Desktop/" + desc + ".mp4");
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

    public static void downloadPic(String awemeId, List<String> picList) {
        for (int index = 0; index < picList.size(); index++) {
            downloadPic(awemeId, index, picList.get(index));
        }
        System.out.println("图片下载完成");
    }

    public static void downloadPic(String aswmeId, int index, String picUrl) {
        try {
            Connection.Response document = Jsoup.connect(picUrl)
                    .ignoreContentType(true)
                    .maxBodySize(30000000)
                    .timeout(10000)
                    .execute();
            BufferedInputStream intputStream = document.bodyStream();
            File fileSavePath = new File("D:/douyin/" + aswmeId + "_" + index + ".png");
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

    public static void downloadVideoByObj(JSONObject obj) {

        JSONObject detail = obj.getJSONObject("aweme").getJSONObject("detail");
        String playApi = detail.getJSONObject("video").getStr("playApi");
        String desc = detail.getStr("desc");
        playApi = "https:" + playApi;
        download(playApi, desc);

    }

}
