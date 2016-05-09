package com.hackerall.Spider;

/**
 * Created by ChenLetian on 16/5/9.
 */

/**
 * 搜索的节点类
 */
public class SearchNode {

    /**
     * 从父亲那里过来的锚里面的内容
     */
    public String fromLinkerTitle;
    /**
     * 节点的url
     */
    public String url;
    /**
     * 节点的父亲所在的索引
     */
    public int fatherIndex;

    public SearchNode(String fromLinkerTitle, String url, int fatherIndex) {
        this.fromLinkerTitle = fromLinkerTitle;
        this.url = url;
        this.fatherIndex = fatherIndex;
    }
}
