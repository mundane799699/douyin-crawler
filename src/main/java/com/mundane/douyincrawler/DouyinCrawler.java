package com.mundane.douyincrawler;

import cn.hutool.json.JSONObject;
import com.mundane.douyincrawler.utils.DownloadUtils;
import com.mundane.douyincrawler.utils.ParseUtil;

public class DouyinCrawler {

    public static final String VIDEO_URL = "https://www.douyin.com/video/";

    public static final String NOTE_URL = "https://www.douyin.com/note/";


    public static void main(String[] args) {
//        String text = "6.41 Vyg:/ 困住我的不是你 是我自己# 白羊座 https://v.douyin.com/jsTDbJt/ 复制此链接，打开Dou音搜索，直接观看视频！";
//        String text = "6.69 MJi:/ 复制打开抖音，看看【理理小不点的作品】买调料 给你做饭 https://v.douyin.com/jss3aS6/";


        // 图文
         String text = "9.46 pqe:/ 复制打开抖音，看看【媛的图文作品】心若有所向往 何惧道阻且长# 日出云海 # 徒步 # 登... https://v.douyin.com/6dhS5Eg/";

        // 视频
        // String text = "6.10 HvF:/ 复制打开抖音，看看【祁舞品牌舞蹈的作品】我喜欢这个调调你呢# 成人古典舞 # 今日份舞蹈打... https://v.douyin.com/kkaHH4Y/";

        // 主页
//        String text = "9- 长按复制此条消息，打开抖音搜索，查看TA的更多作品。 https://v.douyin.com/UNrayGp/";

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

        JSONObject awesomeInfo = ParseUtil.getAwesomeInfo(data);
        if (awesomeInfo == null) {
            System.out.println("videoInfo is null");
            return;
        }
        if (awesomeUrl.startsWith(VIDEO_URL)) {
            DownloadUtils.downloadVideo(awesomeInfo);
        } else if (awesomeUrl.startsWith(NOTE_URL)) {
            DownloadUtils.downloadPic(awesomeInfo);
        } else {
            JSONObject postInfo = ParseUtil.getPostInfo(data);
            DownloadUtils.downloadUser(postInfo);
        }

    }


}
