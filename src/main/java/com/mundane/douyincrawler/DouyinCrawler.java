package com.mundane.douyincrawler;

import com.mundane.douyincrawler.dto.Video;
import com.mundane.douyincrawler.utils.ParseUtil;

import java.util.List;

public class DouyinCrawler {


    public static final String PREFIX = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo/?item_ids=";

    public static void main(String[] args) {
//        String text = "6.41 Vyg:/ 困住我的不是你 是我自己# 白羊座 https://v.douyin.com/jsTDbJt/ 复制此链接，打开Dou音搜索，直接观看视频！";
//        String text = "6.69 MJi:/ 复制打开抖音，看看【理理小不点的作品】买调料 给你做饭 https://v.douyin.com/jss3aS6/";
//        String text = "9.46 pqe:/ 复制打开抖音，看看【媛的图文作品】心若有所向往 何惧道阻且长# 日出云海 # 徒步 # 登... https://v.douyin.com/6dhS5Eg/";
        String text = "6.64 aAG:/ 复制打开抖音，看看【一个阿锐的作品】# 汉服小姐姐 # 现学现卖 排练间隙~ https://v.douyin.com/6vrqrhj/";
//        String text = "https://v.douyin.com/6bFUWMG/";
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
        DownloadUtils.downloadVideoOrPic(videoId);

    }


}
