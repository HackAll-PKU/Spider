package com.hackerall.Spider;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by ChenLetian on 16/5/9.
 */
public class ClosestLinkMain {

    private JPanel ClosetLinkMain;
    private JTextField fromTextField;
    private JTextField toTextField;
    private JTextArea resultTextAtrea;
    private JButton findClosestButton;

    public ClosestLinkMain() {
        findClosestButton.addActionListener(e -> {
            String fromURL = fromTextField.getText();
            String toURL = toTextField.getText();
            searchLink(fromURL, toURL);
        });
    }

    private void searchLink(String fromURL, String toURL) {
        LinkedList queue = new LinkedList<String>();
        queue.add(fromURL);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ClosestLinkMain");
        frame.setContentPane(new ClosestLinkMain().ClosetLinkMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
