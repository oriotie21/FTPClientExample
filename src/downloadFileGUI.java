import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class downloadFileGUI extends JPanel {
    String filePath = null; //다운받으려는 파일이 있는곳
    String downLoadFilePath = null; // 파일을 다운받으려고 하는 경로
    static JTextArea downloadPathText = new JTextArea();

    public downloadFileGUI() {
        // App.java에서 만든 FTPSession session 가져오기
        FTPSession session = App.session;

        // 다운로드 창 생성 및 라벨, 버튼 생성
        JFrame downFileFrame = new JFrame("Download File");
        JPanel downFilePanel = new JPanel();

        JLabel downloadPath = new JLabel("Choose a file:");
        downloadPathText = new JTextArea(1, 10);
        JButton downloadPathBtn = new JButton("browse");
        downloadPathText.disable();

        JLabel downChoDirec = new JLabel("Download Path:");
        JTextArea downChoDirecText = new JTextArea(1, 100);
        JButton downChoDirecBtn = new JButton("browse");
        downChoDirecText.disable();

        JButton downloadBtn = new JButton("Downlaod");

        // 다운로드 패널에 붙일 라벨과 버튼 크기 및 위치 지정
        downloadPath.setBounds(10, 10, 100, 20);
        downloadPathText.setBounds(100, 10, 180, 20);
        downloadPathBtn.setBounds(300, 10, 80, 20);

        downChoDirec.setBounds(10, 40, 100, 20);
        downChoDirecText.setBounds(100, 40, 180, 20);
        downChoDirecBtn.setBounds(300, 40, 80, 20);

        downloadBtn.setBounds(155, 100, 100, 25);

        downFileFrame.add(downFilePanel);
        downFilePanel.setLayout(null);

        downFilePanel.add(downloadPath);
        downFilePanel.add(downloadPathText);
        downFilePanel.add(downloadPathBtn);

        downFilePanel.add(downChoDirec);
        downFilePanel.add(downChoDirecText);
        downFilePanel.add(downChoDirecBtn);

        downFilePanel.add(downloadBtn);

        // 다운로드 할 파일선택
        downloadPathBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                downloadPathText.setText("");
                JFrame downPath = new JFrame("select");
                // downPathBrowser 호출
                downPath.add(new downPathBrowser());
            }
        });

        // 내 PC 어디에 저장할지 선택 버튼 이벤트 리스너
        downChoDirecBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 폴더 선택을 위한 파일 선택기 생성
                JFileChooser fileChooser = new JFileChooser();

                // 폴더 선택 모드로 설정
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 설정을 폴더 선택 모드로 변경
                
                // 파일 선택기를 통해 폴더 선택
                int returnVal = fileChooser.showOpenDialog(downloadFileGUI.this); // OpenDialog를 사용하여 폴더를 선택하도록 함
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedDirectory = fileChooser.getSelectedFile(); // 선택한 폴더
                    downChoDirecText.setText(selectedDirectory.getAbsolutePath()); // 선택한 폴더의 경로를 표시
                    // downLoadFilePath 변수에 경로 저장
                    downLoadFilePath = downChoDirecText.getText();
                    downLoadFilePath = downLoadFilePath.replace("\\", "\\\\");
                }
            }
        });

        // 다운로드 버튼 이벤트 리스너
        downloadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 다운로드 경로와 대상 경로 확인
                if (downLoadFilePath != null && downloadPathText.getText() != null) {
                    // 다운로드할 파일의 경로 설정
                    filePath = downPathBrowser.fullPath;
                    filePath = filePath.replace("/", "\\\\");
                    // 파일 다운로드
                    UserFTPResponse downloadResponse = session.retrieveFile(filePath, new FileEventListener() {
                        @Override
                        public void onProgressChanged(int currentByte) {
                            // 파일 다운로드 진행 중인 경우의 처리
                            // 여기에 진행 정보 업데이트 로직을 추가할 수 있습니다.
                        }

                        @Override
                        public void onProgressFinished() {
                            // 파일 다운로드 완료 후의 처리
                            Path source = Paths.get(filePath); // 다운로드한 파일의 경로를 Path로 변환
                            Path dest = Paths.get(downLoadFilePath).resolve(source.getFileName()); // 복사 대상 파일의 경로를 downLoadFilePath로 설정
                            fileCopy(filePath, dest.toString());
                        }
                    });
                    // 다운로드 결과 확인 및 처리
                    if (downloadResponse != null && downloadResponse.success) {
                        // 복사 성공 메시지 출력
                        JOptionPane.showMessageDialog(null, "Download Success", "Download Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // 파일 다운로드 실패 메시지 출력
                        JOptionPane.showMessageDialog(null, "Download Fail", "Download Fail",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    // 필요없는 파일 삭제
                    File file = new File(filePath);
                    boolean isFileDeleted = file.delete();
                } else {
                    // 경로가 지정되지 않은 경우 메시지 표시
                    JOptionPane.showMessageDialog(null, "Please select a file to download and a destination path",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        downFileFrame.setVisible(true);
        downFileFrame.setSize(400, 220);
        downFileFrame.setLocationRelativeTo(null);
    }

    // 파일 복사하는 함수: inFilePath의 파일을 outFilePath로 복사
    public static boolean fileCopy(String inFilePath, String outFilePath) {
        try {
            FileInputStream infile = new FileInputStream(inFilePath);
            FileOutputStream outfile = new FileOutputStream(outFilePath);

            byte[] b = new byte[1024];
            int len;
            while ((len = infile.read(b, 0, 1024)) > 0) {
                outfile.write(b, 0, len);
            }
            infile.close();
            outfile.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
