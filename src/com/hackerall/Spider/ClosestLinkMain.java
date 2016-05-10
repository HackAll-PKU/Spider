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

    public ClosestLinkMain() {
        // 因为后面要画字符的箭头,所以要用等宽字体
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        findClosestButton.addActionListener(e -> {
            String fromURL = fromTextField.getText();
            String toURL = toTextField.getText();
            fromTextField.setEnabled(false);
            toTextField.setEnabled(false);
            findClosestButton.setEnabled(false);
            resultTextArea.setText("搜索中...");
            SearchWorker worker = new SearchWorker(fromURL, toURL, this);
            worker.startSearch();
        });
    }

    @Override
    public void searchDidSuccessWithResult(String result) {
        SwingUtilities.invokeLater(() -> {
            resultTextArea.setText(result);
            fromTextField.setEnabled(true);
            toTextField.setEnabled(true);
            findClosestButton.setEnabled(true);
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
