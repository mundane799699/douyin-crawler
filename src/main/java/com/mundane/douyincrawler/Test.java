package com.mundane.douyincrawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        String text = "{\n" +
                "            \"@type\": \"ListItem\",\n" +
                "            \"position\": 2,\n" +
                "            \"name\": \"钰钰\",\n" +
                "            \"item\": \"https://www.douyin.com/user/MS4wLjABAAAAiK0274pDg-CiVvDA1ZINZBuQ6iZFet-gKVbmnNyBHLY\"\n" +
                "          }";
        String regex = "https://www.douyin.com/user/[-A-Za-z0-9+&@#/%?=~_|!:,.;]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String id = matcher.group();
            System.out.println("id = " + id);
        }
    }
}
