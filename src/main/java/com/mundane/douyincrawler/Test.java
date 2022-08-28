package com.mundane.douyincrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String text = "https://www.douyin.com/video/7133928797753085195?previous_page=app_code_link";
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String id = matcher.group();
            System.out.println("id = " + id);
        }
    }
}
