import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

public class downPathBrowser extends JPanel {
    DefaultListModel<String> listModel;
    JList<String> fileList;

    FTPSession session = App.session;
    static String fullPath = null;
    public downPathBrowser() {
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
                else{
                    JOptionPane.showMessageDialog(null, "상위 폴더가 존재하지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the "Select" button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = fileList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    UserFTPResponse path = session.pwd();
                    String message = path.message;
               
                    // Extract the directory or file name from the selected line
                    String selectedLine = listModel.getElementAt(selectedIndex);     
                    String line = selectedLine.substring(selectedLine.lastIndexOf(" - ") + 3);


                    
                    String pattern = "\"(.*?)\"";
                    Pattern r = Pattern.compile(pattern);

                    Matcher messageMatcher = r.matcher(message);

                    String messageContent = "";
                    
                    // message에서 따옴표 안의 값을 추출
                    if (messageMatcher.find()) {
                        messageContent = messageMatcher.group(1);
                    }
                    // 따옴표 안의 값을 합쳐서 경로 문자열 생성
                    //fullPath = messageContent + "/" + line;  
                    fullPath = line;

                    
                    // 이제 selectedFilePath에 선택한 파일의 전체 경로가 저장되어 있습니다.
                    System.out.println("Selected File Path: " + line);
                    JTextArea text = downloadFileGUI.downloadPathText;
                    text.setText(line);
                }
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
