
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class uploadFileGUI extends JPanel {
    // 업로드할 파일경로를 저장할 변수
    String upLoadFilePath = null;
    // 업로드할 파일이름을 저장할 변수
    String upLoadFileName = null;
    static JTextArea uploadPathText = new JTextArea();
    public uploadFileGUI() {

        // App.java에서 만든 FTPSession session 가져오기
        FTPSession session = App.session;

        // 업로드 창 생성 및 라벨, 버튼 생성
        JFrame upFileFrame = new JFrame("Upload File");
        JPanel upFilePanel = new JPanel();

        JLabel uploadPath = new JLabel("Upload Path:");
        uploadPathText = new JTextArea(1, 10);
        JButton uploadPathBtn = new JButton("browse");
        uploadPathText.disable();

        JLabel upChoDirec = new JLabel("Choose a file:");
        JTextArea upChoDirecText = new JTextArea(1, 100);
        JButton upChoDirecBtn = new JButton("browse");
        upChoDirecText.disable();

        JButton uploadBtn = new JButton("Upload");

        // 업로드 패널에 붙일 라벨과 버튼 크기 및 위치 지정
        uploadPath.setBounds(10, 10, 80, 20);
        uploadPathText.setBounds(100, 10, 180, 20);
        uploadPathBtn.setBounds(300, 10, 80, 20);

        upChoDirec.setBounds(10, 40, 80, 20);
        upChoDirecText.setBounds(100, 40, 180, 20);
        upChoDirecBtn.setBounds(300, 40, 80, 20);

        uploadBtn.setBounds(165, 100, 80, 25);

        upFileFrame.add(upFilePanel);
        upFilePanel.setLayout(null);

        upFilePanel.add(uploadPath);
        upFilePanel.add(uploadPathText);
        upFilePanel.add(uploadPathBtn);

        upFilePanel.add(upChoDirec);
        upFilePanel.add(upChoDirecText);
        upFilePanel.add(upChoDirecBtn);

        upFilePanel.add(uploadBtn);

        // 업로드할 경로 선택
        // upload Path browse 버튼 클릭시 발생 이벤트
        uploadPathBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                uploadPathText.setText("");
                JFrame upPath = new JFrame("select");
                // upPathBrowser 호출
                upPath.add(new upPathBrowser());
            }
        });

        // 내 pc에서 업로드할 파일 선택
        // choose a file 버튼 클릭시 발생 이벤트
        upChoDirecBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser fileChooser = new JFileChooser(); // 파일 탐색기 객체 생성
                // 파일 선택 다이얼로그 열기
                int returnValue = fileChooser.showOpenDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile(); // 선택한 파일 가져오기

                    // 선택한 파일로부터 경로와 이름을 받아옴
                    String selectedFileName = selectedFile.getName();
                    String selectedFilePath = selectedFile.getAbsolutePath();
                    upLoadFilePath = selectedFilePath;
                    upLoadFilePath = upLoadFilePath.replace("\\", "\\\\");
                    // upChoDirecText에 선택한 파일경로를 출력함
                    upChoDirecText.setText(upLoadFilePath);
                } else {
                    // 파일 선택하지 않았을때 선택됨
                    JOptionPane.showMessageDialog(null, "Load canceled", "Load canceled",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        // 업로드버튼 클릭시 발생 이벤트
        uploadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (upChoDirecText.getText() != null && upLoadFilePath != null) {
                    // 파일 cwd로 복사
                    Path source = Paths.get(upLoadFilePath);
                    upLoadFileName = String.valueOf(source.getFileName());
                    Path dest = Paths.get(".").resolve(upLoadFileName);
                    fileCopy(upLoadFilePath, dest.toString());

                    // store 함수를 호출하여 파일 업로드
                    UserFTPResponse response = session.store(upLoadFileName, new FileEventListener() {
                        @Override
                        public void onProgressChanged(int currentByte) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onProgressFinished() {
                            // TODO Auto-generated method stub
                        }
                    });

                    // 업로드 성공 여부를 확인하고 필요에 따라 처리
                    if (response != null && response.success) {
                        // 업로드 성공
                        JOptionPane.showMessageDialog(null, "Success", "Success", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        // 업로드 실패
                        JOptionPane.showMessageDialog(null, "Fail", "Fail", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "select first", "select first", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        upFileFrame.setVisible(true);
        upFileFrame.setSize(400, 220);
        upFileFrame.setLocationRelativeTo(null);
    }

    // 파일 복사하는 함수 inFilePath 함수를 outFilePath로 복사함
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
