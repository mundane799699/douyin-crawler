package com.mundane.douyincrawler;

import com.mundane.douyincrawler.dto.Video;
import com.mundane.douyincrawler.utils.ParseUtil;

import java.util.List;

public class DouyinCrawler {


    public static final String PREFIX = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

    public static void main(String[] args) {
//        String text = "6.41 Vyg:/ 困住我的不是你 是我自己# 白羊座 https://v.douyin.com/jsTDbJt/ 复制此链接，打开Dou音搜索，直接观看视频！";
//        String text = "6.69 MJi:/ 复制打开抖音，看看【理理小不点的作品】买调料 给你做饭 https://v.douyin.com/jss3aS6/";
        String text = "9.46 pqe:/ 复制打开抖音，看看【媛的图文作品】心若有所向往 何惧道阻且长# 日出云海 # 徒步 # 登... https://v.douyin.com/6dhS5Eg/";
        String url = ParseUtil.parseUrl(text);
        if (url == null) {
            System.out.println("url is null");
            return;
        }
        System.out.println("url = " + url);
        String videoId = ParseUtil.parseVideoId(url);
        if (videoId == null) {
            System.out.println("videoId is null");
            return;
        }
        System.out.println("videoId = " + videoId);
        url = PREFIX + videoId;
        String jsonStr = ParseUtil.getJsonStr(url);
        if (jsonStr == null) {
            System.out.println("jsonStr is null");
            return;
        }
        int awemeType = ParseUtil.getAwemeType(jsonStr);
        System.out.println("awesomeType = " + awemeType);
        if (awemeType == 4) {
            Video video = ParseUtil.getVideo(jsonStr);
            if (video == null) {
                System.out.println("video is null");
                return;
            }
            ParseUtil.downloadVideo(video);
        } else if (awemeType == 2) {
            System.out.println("download pic");
            List<String> picList = ParseUtil.getPicList(jsonStr);
            ParseUtil.downloadPic(videoId, picList);
        }

    }


}
