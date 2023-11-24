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

    // App.java에서 만든 FTPSession session 가져오기
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

        // List Files 버튼 클릭시 발생 이벤트
        // 최초 서버 파일 경로 기준 파일, 폴더 목록을 보여줌
        listFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                direct = session.pwd();

                // 현재 디렉토리에 있는 모든 파일과 폴더 나열
                UserFTPResponse response = session.nlst();

                listFilesButton.setEnabled(false);
                if (response != null && response.success) {
                    listModel.clear();
                    String[] lines = response.message.split("\r\n");
                    for (String line : lines) {
                        // cd 명령어의 성공 여부로 파일인지 폴더인지 판별
                        int type = session.cd(line);
                        try {
                            Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        // 폴더면 folder-폴더이름 표시
                        if (type == 0) {
                            listModel.addElement("folder - " + line);
                            int cdResult = session.cd("..");
                            // 
                            if(cdResult == -1){
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        } 

                        // 파일이면 file-파일이름 표시
                        else if (type == 1) {
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

        // Go Up 버튼 클릭시 발생 이벤트
        // 상위 폴더로 가는 버튼
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
                // 상위 디렉토리가 있으면
                if (currentPath != null) {
                    listModel.clear();
                    UserFTPResponse response = session.nlst();
                    // 서버 응답 정상연결시
                    if (response != null && response.success) {
                        String[] lines = response.message.split("\r\n");
                        for (String line : lines) {
                            // 파일인지 폴더인지 구별해서
                            int type = session.cd(line); // Determine if it's a folder or a file
                            try {
                                Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            // 폴더면 폴더 출력
                            if (type == 0) {
                                listModel.addElement("folder - " + line);
                                int cdResult = session.cd("..");
                                if(cdResult == -1){
                                    JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                    break;
                                }
                            } 
                            // 파일이면 파일 출력
                            else if (type == 1) {
                                listModel.addElement("file - " + line);
                            }
                            // 서버응답 없을때 에러 출력
                            else{
                                JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                    }
                    // 서버 연결 끊겼을시
                    else{
                        JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } 
                // 상위 디렉토리가 없으며
                else {
                    JOptionPane.showMessageDialog(null, "상위 폴더가 존재하지 않습니다.", "에러", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Select 버튼 클릭시 발생 이벤트
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
                // 추출한 문자열로 textarea에 값 삽입
                text.setText(extractedValue);
                upBroFrame.dispose();
            }
        });

        // 폴더 클릭시 발생 이벤트
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 더블 클릭시
                if (e.getClickCount() == 2) {
                    int selectedIndex = fileList.getSelectedIndex();
                    // 유효한 항목이 선택되었는지 확인
                    if (selectedIndex >= 0) {
                        // 리스트 모델에서 선택한 라인 가져오기
                        String selectedLine = listModel.getElementAt(selectedIndex);

                        // 선택한 라인에서 디렉토리 또는 파일 이름 추출
                        String line = selectedLine.substring(selectedLine.lastIndexOf(" - ") + 3);
                        int type = session.cd(line); // Determine if it's a folder or a file
                        // 폴더인지 확인
                        if (type == 0) {
                            // 폴더의 내용을 나열하고 UI 업데이트
                            UserFTPResponse response = session.nlst();
                            if (response != null && response.success) {
                                // 기존의 리스트 모델 지우기
                                listModel.clear();
                                // 응답 메시지를 라인으로 분할
                                String[] lines = response.message.split("\r\n");
                                for (String subLine : lines) {
                                    int subType = session.cd(subLine);
                                    try {
                                        Thread.sleep(50); // 0.05초 (50 밀리초) 대기
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    // 폴더 항목이면
                                    if (subType == 0) {
                                        // 폴더 항목을 리스트 모델에 추가
                                        listModel.addElement("folder - " + subLine);
                                        // 현재 디렉토리를 상위 디렉토리로 변경
                                        int cdResult = session.cd("..");
                                        if(cdResult == -1){
                                            // 디렉토리 변경 실패시 오류 메시지 표시
                                            JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                            break;
                                        }
                                    } 
                                    // 파일인지 확인
                                    else if (subType == 1) {
                                        // 파일 항목을 리스트 모델에 추가
                                        listModel.addElement("file - " + subLine);
                                    }
                                    // 예상치 못한 응답 처리
                                    else{
                                        JOptionPane.showMessageDialog(null, "Server Response NULL", "Error", JOptionPane.ERROR_MESSAGE);
                                        break;
                                    }
                                }
                            }
                        }
                        // 폴더가 아닐시 에러처리
                        else{
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