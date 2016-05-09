package com.hackerall.Spider;

/**
 * Created by ChenLetian on 16/5/9.
 */
public class SearchNode {

    public String fromLinkerTitle;
    public String url;
    public int fatherIndex;

    public SearchNode(String fromLinkerTitle, String url, int fatherIndex) {
        this.fromLinkerTitle = fromLinkerTitle;
        this.url = url;
        this.fatherIndex = fatherIndex;
    }
}
