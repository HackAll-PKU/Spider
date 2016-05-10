package com.hackerall.Spider;

import javax.swing.*;
import java.awt.*;

/**
 * 主controller类
 * Created by ChenLetian on 16/5/9.
 */
public class ClosestLinkMain implements SearchEndDelegate {

    private JPanel ClosetLinkMain;
    private JTextField fromTextField;
    private JTextField toTextField;
    private JTextArea resultTextArea;
    private JButton findClosestButton;
    private JScrollPane resultScrollPane;
    private JButton cancelButton;

    private Timer searchAnimationTimer;
    private int searchAnimationCounter;

    SearchWorker worker;

    public ClosestLinkMain() {
        // 因为后面要画字符的箭头,所以要用等宽字体
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));

        findClosestButton.addActionListener(e -> {
            String fromURL = fromTextField.getText();
            String toURL = toTextField.getText();
            fromTextField.setEnabled(false);
            toTextField.setEnabled(false);
            findClosestButton.setEnabled(false);
            cancelButton.setEnabled(true);
            searchAnimationCounter = 0;
            searchAnimationTimer = new Timer(300, e1 -> {
                searchAnimationCounter++;
                String searchText = "搜索中";
                for (int i = 0;i <searchAnimationCounter; i++) {
                    searchText += ".";
                }
                final String outputText = searchText;
                SwingUtilities.invokeLater(() -> resultTextArea.setText(outputText));
                if (searchAnimationCounter == 3) {
                    searchAnimationCounter = 0;
                }
            });
            worker = new SearchWorker(fromURL, toURL, this);
            searchAnimationTimer.start();
            worker.startSearch();
        });
        cancelButton.addActionListener(e -> {
            worker.ended = true;
            searchDidSuccessWithResult("用户取消");
        });
        cancelButton.setEnabled(false);
    }

    @Override
    public void searchDidSuccessWithResult(String result) {
        searchAnimationTimer.stop();
        SwingUtilities.invokeLater(() -> {
            resultTextArea.setText(result);
            fromTextField.setEnabled(true);
            toTextField.setEnabled(true);
            findClosestButton.setEnabled(true);
            cancelButton.setEnabled(false);
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClosestLinkMain");
        frame.setContentPane(new ClosestLinkMain().ClosetLinkMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
