package com.hackerall.Spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HuShunxin on 16/5/8.
 */
public class Filter {
    public static void main(String[] args) {


        List<String> keywords = new ArrayList<>();
        String inFilename = "keywords.txt";
        try {
            File fin = new File(inFilename);
            BufferedReader in = new BufferedReader(new FileReader(fin));

            String s = in.readLine();
            while (s != null) {
                keywords.add(s.trim());
                s = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = "https://www.baidu.com/s";

        //根据系统资源建立线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (String wd : keywords) {
            fixedThreadPool.execute(new Thread(() -> {
                Map<String, String> queryParas = new HashMap<>();
                queryParas.put("wd", wd);
                String content = HttpKit.get(url, queryParas);

                String patternString = "\\s*(class)\\s*=\\s*\"xlhJ_e[^\"]*\"";//TODO:啊搞不出来了

                Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(content);
                int cnt = 0;
                while (matcher.find()) {
                    cnt++;
                }
                System.out.println(wd + " : " + cnt);
            }));
        }
        fixedThreadPool.shutdown();

    }


}
