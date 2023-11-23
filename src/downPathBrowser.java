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
    // 리스트 모델
    DefaultListModel<String> listModel;
    // 파일 목록을 보여주는 JList
    JList<String> fileList;

    FTPSession session = App.session;
    // 선택한 파일의 전체 경로를 저장하는 변수
    static String fullPath = null;

    public downPathBrowser() {
        // 다운로드 창에 붙이는 프레임과 패널 라벨 버튼
        JFrame upBroFrame = new JFrame("select file");
        JPanel upBroPanel = new JPanel();
        JButton listFilesButton = new JButton("List Files");
        JButton upButton = new JButton("Go Up");
        JButton selectButton = new JButton("Select"); // 추가: 폴더 선택 버튼
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);

        // 라벨 버튼의 크기와 위치 선정
        listFilesButton.setBounds(10, 10, 100, 20);
        upButton.setBounds(280, 10, 100, 20);
        fileList.setBounds(10, 40, 370, 170);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(10, 40, 370, 170);

        selectButton.setBounds(280, 220, 100, 20);

        // 파일 목록 갱신 버튼에 대한 액션 리스너
        listFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 한번 출력하면 버튼 비활성화
                listFilesButton.setEnabled(false);
                UserFTPResponse response = session.nlst();
                if (response != null && response.success) {
                    listModel.clear();
                    String[] lines = response.message.split("\r\n");
                    for (String line : lines) {
                        // 폴더 또는 파일인지 확인
                        int type = session.cd(line); 
                        try {
                            Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                        } catch (InterruptedException ex) {
                            // 예외 처리가 필요할 수 있습니다.
                        }
                        // 폴더면
                        if (type == 0) {
                            listModel.addElement("folder - " + line);
                            int cdResult = session.cd("..");
                            // 서버응답오류
                            if(cdResult == -1){
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                        // 파일이면
                        else if (type == 1) {
                            listModel.addElement("file - " + line);
                        }
                        // 예기치 못한 오류
                        else{
                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                        }
                    }
                }
                // 서버응답오류
                else {
                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the "Go Up" button
        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 상위 폴더로 이동
                String currentPath = session.cwd(".."); // cwd : 성공시 디렉토리 반환, 실패시 null 반환
                try {
                    Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                } catch (InterruptedException ex) {
                    // 예외 처리가 필요할 수 있습니다.
                }
                // 상위 폴더가 있으면
                if (currentPath != null) {
                    listModel.clear();
                    // 현재 디렉토리에 있는 모든 파일과 폴더 나열
                    UserFTPResponse response = session.nlst();
                    if (response != null && response.success) {
                        String[] lines = response.message.split("\r\n");
                        for (String line : lines) {
                            int type = session.cd(line); // Determine if it's a folder or a file
                            try {
                                Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                            } catch (InterruptedException ex) {
                                // 예외 처리가 필요할 수 있습니다.
                            }
                            // 폴더이면
                            if (type == 0) {
                                listModel.addElement("folder - " + line);
                                int cdResult = session.cd("..");
                                if(cdResult == -1)
                                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                            }
                            // 파일이면
                            else if (type == 1) {
                                listModel.addElement("file - " + line);
                            }
                            // 서버응답끊길때
                            else{
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                    }
                } 
                // 상위 폴더가 없으면
                else {
                    JOptionPane.showMessageDialog(null, "상위 폴더가 존재하지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the "Select" button
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = fileList.getSelectedIndex();
                // Extract the directory or file name from the selected line
                String selectedLine = listModel.getElementAt(selectedIndex);
                String line = selectedLine.substring(selectedLine.lastIndexOf(" - ") + 3);
                int type = session.cd(line); // Determine if it's a folder or a file
                if (selectedIndex >= 0 && type == 1) {
                    fullPath = line;
                    // 이제 selectedFilePath에 선택한 파일의 전체 경로가 저장되어 있습니다.
                    //System.out.println("Selected Folder Path: " + fullPath);
                    JTextArea text = downloadFileGUI.downloadPathText;
                    text.setText(line);
                } else if (type == 0) {
                    // 파일을 선택한 경우
                    JOptionPane.showMessageDialog(null, "폴더는 선택할 수 없습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
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
                                    try {
                                        Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                                    } catch (InterruptedException ex) {
                                        // 예외 처리가 필요할 수 있습니다.
                                    }
                                    if (subType == 0) {
                                        listModel.addElement("folder - " + subLine);
                                        int cdResult = session.cd("..");
                                        if (cdResult == -1){
                                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    }
                                    else if (subType == 1) {
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
