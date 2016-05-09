package com.hackerall.Spider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.TreeSet;
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

    private ArrayList resultNode = new ArrayList<SearchNode>();

    public ClosestLinkMain() {
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        findClosestButton.addActionListener(e -> {
            String fromURL = fromTextField.getText();
            String toURL = toTextField.getText();
            searchLink(fromURL, toURL);
        });
    }

    private void searchLink(String fromURL, String toURL) {
        resultNode = new ArrayList<SearchNode>();
        resultTextArea.setText("");
        new Thread(() -> {
            String toURLString = toURL;
            if (toURL.charAt(toURL.length() - 1) == '/') {
                toURLString = toURL.substring(0, toURL.length() - 1);
            }

            String httpDomainString = "(https?://[^/]*).*?";
            Pattern httpDomainPattern = Pattern.compile(httpDomainString, Pattern.CASE_INSENSITIVE);

            String linkPatternString = "<a[^<>]*href=\"?([^ <>\"]*)\"?[^<>]*>(.*?)</a>";
            Pattern linkPattern = Pattern.compile(linkPatternString, Pattern.CASE_INSENSITIVE);

            String specialCharacterPatternString = "[^\\u4E00-\\u9FA5A-Za-z0-9_]";
            Pattern specialCharacterPattern  = Pattern.compile(specialCharacterPatternString, Pattern.CASE_INSENSITIVE);

            String titlePatternString = "<title[^>]*>(.*?)</title>";
            Pattern titlePattern = Pattern.compile(titlePatternString, Pattern.CASE_INSENSITIVE);

            ArrayList queue = new ArrayList<SearchNode>();
            queue.add(new SearchNode(null, fromURL, -1));
            TreeSet addedURL = new TreeSet<String>();
            addedURL.add(fromURL);

            int index = 0;
            boolean ended = false;
            while (index < queue.size()) {
                String nowURL = ((SearchNode) queue.get(index)).url;
                System.out.println(nowURL);
                String domain;
                Matcher matcher = httpDomainPattern.matcher(nowURL);
                if (matcher.find()) {
                    domain = matcher.group(1);
                }
                else {
                    index++;
                    continue;
                }
                try {
                    String content = HttpKit.get(nowURL);
                    Matcher linkMatcher = linkPattern.matcher(content);

                    while (linkMatcher.find()) {
                        String link = linkMatcher.group(1);
                        if (link.equals("#")) continue;
                        if (link.contains("javascript:")) continue;
                        if (link.length() == 0) continue;
                        String name = specialCharacterPattern.matcher(linkMatcher.group(2)).replaceAll("");
                        if (!link.contains("://")) {
                            String path;
                            if (link.charAt(0) != '/') {
                                path = '/' + link;
                            } else {
                                path = link;
                            }
                            link = domain + path;
                        }
                        if (!addedURL.contains(link)) {
                            queue.add(new SearchNode(name, link, index));
                            addedURL.add(link);
                            //System.out.println(link);
                            if (link.charAt(link.length() - 1) == '/') {
                                link = link.substring(0, link.length() - 1);
                            }
                            if (toURLString.equals(link)) {
                                int linkerIndex = queue.size() - 1;
                                while (linkerIndex > -1) {
                                    resultNode.add(queue.get(linkerIndex));
                                    linkerIndex = ((SearchNode) queue.get(linkerIndex)).fatherIndex;
                                }
                                output();

                                ended = true;
                                break;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                }
                if (ended) break;
                index++;
            }
        }).start();
    }

    private void output() {
        String outputText = ((SearchNode)resultNode.get(resultNode.size() - 1)).url + "\n";
        for (int index = resultNode.size() - 2; index >=0; index --) {
            SearchNode node = (SearchNode) resultNode.get(index);
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
