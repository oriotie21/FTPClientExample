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
    int flag = 0;

    UserFTPResponse direct = null;

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
                direct = session.pwd();

                UserFTPResponse response = session.nlst();

                listFilesButton.setEnabled(false);
                if (response != null && response.success) {
                    listModel.clear();
                    String[] lines = response.message.split("\r\n");
                    for (String line : lines) {
                        int type = session.cd(line); // Determine if it's a folder or a file
                        try {
                            Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        if (type == 0) {
                            listModel.addElement("folder - " + line);
                            int cdResult = session.cd("..");
                            if(cdResult == -1){
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        } else if (type == 1) {
                            listModel.addElement("file - " + line);
                        }else{
                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                }else {
                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the "Go Up" button
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = 1;
                String currentPath = session.cwd(".."); // cwd : 성공시 디렉토리 반환, 실패시 null 반환
                try {
                    Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                if (currentPath != null) {
                    listModel.clear();
                    UserFTPResponse response = session.nlst();
                    if (response != null && response.success) {
                        String[] lines = response.message.split("\r\n");
                        for (String line : lines) {
                            int type = session.cd(line); // Determine if it's a folder or a file
                            try {
                                Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            if (type == 0) {
                                listModel.addElement("folder - " + line);
                                int cdResult = session.cd("..");
                                if(cdResult == -1){
                                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                            } else if (type == 1) {
                                listModel.addElement("file - " + line);
                            }else{
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "상위 폴더가 존재하지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the "Select" button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea text = uploadFileGUI.uploadPathText;

                UserFTPResponse ufr = session.pwd();
                String message = ufr.message;
                String extractedValue = null;

                int start = message.indexOf("\""); // 따옴표의 시작 위치 찾기
                if (start != -1) {
                    int end = message.indexOf("\"", start + 1); // 두 번째 따옴표의 위치 찾기
                    if (end != -1) {
                        // 따옴표 사이의 문자열 추출
                        extractedValue = message.substring(start + 1, end);
                    }
                }
                text.setText(extractedValue);
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
                                    try {
                                        Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    if (subType == 0) {
                                        listModel.addElement("folder - " + subLine);
                                        int cdResult = session.cd("..");
                                        if(cdResult == -1){
                                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    } else if (subType == 1) {
                                        listModel.addElement("file - " + subLine);
                                    }else{
                                        JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                }
                            }
                        }else{
                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
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