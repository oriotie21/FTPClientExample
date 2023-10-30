
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class mainGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("FTP program");
        JPanel panel = new JPanel();
        JButton upload = new JButton("Upload");
        JButton download = new JButton("Download");

        frame.add(panel);
        panel.add(upload);
        panel.add(download);

        // Add action listener for the upload button
        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame upFrame = new JFrame("Upload");
                upFrame.add(new UploadPanel());

            }
        });

        // Add action listener for the download button
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame downFrame = new JFrame("Download");
                downFrame.add(new DownloadPanel());

            }
        });

        frame.setVisible(true);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
