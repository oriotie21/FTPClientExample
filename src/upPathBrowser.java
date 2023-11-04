import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

public class upPathBrowser extends JPanel {
    DefaultListModel<String> listModel;
    JList<String> fileList;

    FTPSession session = App.session;

    public upPathBrowser() {
        JFrame upBroFrame = new JFrame("path");
        JPanel upBroPanel = new JPanel();
        JButton listFilesButton = new JButton("List Files");
        JButton upButton = new JButton("Go Up");
        JButton selectButton = new JButton("Select"); // 추가: 폴더 선택 버튼
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);

        listFilesButton.setBounds(10, 10, 100, 20);
        upButton.setBounds(280, 10, 100, 20);
        fileList.setBounds(10, 40, 370, 170);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(10, 40, 370, 170);

        selectButton.setBounds(280, 220, 100, 20);

        listFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UserFTPResponse response = session.nlst();
                if (response != null && response.success) {
                    listModel.clear();
                    String[] lines = response.message.split("\r\n");
                    for (String line : lines) {
                        int type = session.cd(line); // Determine if it's a folder or a file
                        if (type == 0) {
                            listModel.addElement("folder - " + line);
                            session.cd("..");
                        } else if (type == 1) {
                            listModel.addElement("file - " + line);
                        }
                    }
                }
            }
        });

        // Add action listener to the "Go Up" button
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentPath = session.cwd(".."); // cwd : 성공시 디렉토리 반환, 실패시 null 반환
                if (currentPath != null) {
                    listModel.clear();
                    UserFTPResponse response = session.nlst();
                    if (response != null && response.success) {
                        String[] lines = response.message.split("\r\n");
                        for (String line : lines) {
                            int type = session.cd(line); // Determine if it's a folder or a file
                            if (type == 0) {
                                listModel.addElement("folder - " + line);
                                session.cd("..");
                            } else if (type == 1) {
                                listModel.addElement("file - " + line);
                            }
                        }
                    }
                }
            }
        });

        // Add action listener to the "Select" button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserFTPResponse path = session.pwd();
                upBroFrame.dispose();
            }
        });
        // Add mouse listener to the file list
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = fileList.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        String selectedLine = listModel.getElementAt(selectedIndex);

                        // Extract the directory or file name from the selected line
                        String line = selectedLine.substring(selectedLine.lastIndexOf(" - ") + 3);
                        int type = session.cd(line); // Determine if it's a folder or a file
                        if (type == 0) {
                            // If it's a folder, list its contents and update the UI
                            UserFTPResponse response = session.nlst();
                            if (response != null && response.success) {
                                listModel.clear();
                                String[] lines = response.message.split("\r\n");
                                for (String subLine : lines) {
                                    int subType = session.cd(subLine);
                                    if (subType == 0) {
                                        listModel.addElement("folder - " + subLine);
                                        session.cd("..");
                                    } else if (subType == 1) {
                                        listModel.addElement("file - " + subLine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        upBroFrame.add(upBroPanel);
        upBroPanel.setLayout(null);
        upBroPanel.add(listFilesButton);
        upBroPanel.add(upButton);
        upBroPanel.add(scrollPane);
        upBroPanel.add(selectButton); // 추가: 폴더 선택 버튼

        upBroFrame.setSize(400, 300);
        upBroFrame.setLocationRelativeTo(null);
        upBroFrame.setVisible(true);
    }
}