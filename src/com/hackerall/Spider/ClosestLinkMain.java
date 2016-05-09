package com.hackerall.Spider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ChenLetian on 16/5/9.
 */
public class ClosestLinkMain {

    private JPanel ClosetLinkMain;
    private JTextField fromTextField;
    private JTextField toTextField;
    private JTextArea resultTextArea;
    private JButton findClosestButton;
    private JScrollPane resultScrollPane;

    // 结果的节点集
    private Vector<SearchNode> resultNode = new Vector();
    // 用来标注是否搜索已经结束,用于多线程的控制
    boolean ended = false;

    public ClosestLinkMain() {
        // 因为后面要画字符的箭头,所以要用等宽字体
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        findClosestButton.addActionListener(e -> {
            String fromURL = fromTextField.getText();
            String toURL = toTextField.getText();
            searchLink(fromURL, toURL);
        });
    }

    /**
     * 搜索链接路
     * @param fromURL URL的出发点
     * @param toURL URL的终点
     */
    private void searchLink(String fromURL, String toURL) {
        // 每次搜索前清空上次搜索的结果
        resultNode = new Vector();
        resultTextArea.setText("搜索中...");
        new Thread(() -> {
            /*
            这里需要考虑这样一个问题:
            http://www.pku.edu.cn
            与
            http://www.pku.edu.cn/
            是同一个URL,所以作如下处理(去掉最后的/)
             */
            String toURLString = toURL;
            if (toURL.charAt(toURL.length() - 1) == '/') {
                toURLString = toURL.substring(0, toURL.length() - 1);
            }

            /*
             检测域名,若url为http://www.pku.edu.cn/academics/index.htm#1则域名为http://www.pku.edu.cn
             这是为了后面a中链接出现如/academics/index.html#1这种情况,那么则补上其域名
              */
            String httpDomainString = "(https?://[^/]*).*?";
            Pattern httpDomainPattern = Pattern.compile(httpDomainString, Pattern.CASE_INSENSITIVE);

            /*
            检测<a></a>
             */
            String linkPatternString = "<a[^<>]*href=\"?([^ <>\"]*)\"?[^<>]*>(.*?)</a>";
            Pattern linkPattern = Pattern.compile(linkPatternString, Pattern.CASE_INSENSITIVE);

            /*
            检测非中文非英文非数字非下划线字符,其中中文为\u4E00-\u9FA5A
             */
            String specialCharacterPatternString = "[^\\u4E00-\\u9FA5A-Za-z0-9_]";
            Pattern specialCharacterPattern  = Pattern.compile(specialCharacterPatternString, Pattern.CASE_INSENSITIVE);

            /*
            检测网页的title,暂时没有用到
             */
            String titlePatternString = "<title[^>]*>(.*?)</title>";
            Pattern titlePattern = Pattern.compile(titlePatternString, Pattern.CASE_INSENSITIVE);

            /*
             URL的队列(之所以不用Queue接口下面的队列是因为"出队"的东西不能直接删掉,最后回找链接路的时候还要用的)
             所以用了线程安全的Vector自己维护队列
              */
            Vector<SearchNode> queue = new Vector<>();
            queue.add(new SearchNode(null, fromURL, -1));

            /*
             已经加过的queue的URL就不用加了,所以维护了addedURL保存已经加过queue的URL
             */
            TreeSet addedURL = new TreeSet<String>();
            addedURL.add(fromURL);

            // 队头索引
            int index = 0;

            // BFS
            while (index < queue.size()) {
                String nowURL = queue.get(index).url;
                System.out.println("now visiting: " + nowURL);

                //获取当然url的domain
                String domain;
                Matcher matcher = httpDomainPattern.matcher(nowURL);
                if (matcher.find()) {
                    domain = matcher.group(1);
                }
                else {
                    // 如果url中没有domain,这个url应该是个错误的url,直接跳过
                    index++;
                    continue;
                }
                try {
                    // 获取url里面的内容
                    String content = HttpKit.get(nowURL);
                    Matcher linkMatcher = linkPattern.matcher(content);
                    // 对于每一个<a href>里面的链接
                    while (linkMatcher.find()) {
                        String link = linkMatcher.group(1);
                        // 如果链接是#就无视
                        if (link.equals("#")) continue;
                        // 如果链接是javascript:XXX也无视
                        if (link.contains("javascript:")) continue;
                        // 如果链接长度为0则无视
                        if (link.length() == 0) continue;
                        /*
                         name是<a>和</a>中间的部分,将里面不是中文不是英文不是数字不是下划线的东西去掉
                         不过如果里面如果是个img就暂时没处理
                          */
                        String name = specialCharacterPattern.matcher(linkMatcher.group(2)).replaceAll("");
                        // 如果链接没有://,就说明是一个相对域名的路径,比如/academics/index.html#1,那就给它加上域名
                        if (!link.contains("://")) {
                            String path;
                            if (link.charAt(0) != '/') {
                                path = '/' + link;
                            } else {
                                path = link;
                            }
                            link = domain + path;
                        }
                        // 如果没加过这个URL的话
                        if (!addedURL.contains(link)) {
                            queue.add(new SearchNode(name, link, index));
                            addedURL.add(link);
                            //System.out.println(link);
                            /*
                             同上面说过的那个问题:
                             http://www.pku.edu.cn
                             与
                             http://www.pku.edu.cn/
                             是同一个URL,所以作如下处理(去掉最后的/)
                              */
                            if (link.charAt(link.length() - 1) == '/') {
                                link = link.substring(0, link.length() - 1);
                            }
                            if (toURLString.equals(link)) {
                                // 向前找此url的祖先并保存
                                int linkerIndex = queue.size() - 1;
                                while (linkerIndex > -1) {
                                    resultNode.add(queue.get(linkerIndex));
                                    linkerIndex = queue.get(linkerIndex).fatherIndex;
                                }
                                output();
                                // 标注任务完成
                                ended = true;
                                break;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                }
                index++;
            }
        }).start();
    }

    private void output() {
        String outputText = resultNode.get(resultNode.size() - 1).url + "\n";
        for (int index = resultNode.size() - 2; index >=0; index --) {
            SearchNode node = resultNode.get(index);
            outputText += node.fromLinkerTitle + "\n | \n | \n\\|/\n" + node.url + "\n";
        }
        final String output = outputText;
        SwingUtilities.invokeLater(() -> resultTextArea.setText(output));
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClosestLinkMain");
        frame.setContentPane(new ClosestLinkMain().ClosetLinkMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
