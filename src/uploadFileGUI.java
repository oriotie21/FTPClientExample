
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
	String upLoadFilePath = null;
	String upLoadFileName = null;

	public uploadFileGUI() {
		FTPSession session = App.session;

		JFrame upFileFrame = new JFrame("Upload File");
		JPanel upFilePanel = new JPanel();

		JLabel uploadPath = new JLabel("Upload Path:");
		JTextArea uploadPathText = new JTextArea(1, 10);
		JButton uploadPathBtn = new JButton("browse");
		uploadPathText.disable();

		JLabel upChoDirec = new JLabel("Choose a file:");
		JTextArea upChoDirecText = new JTextArea(1, 100);
		JButton upChoDirecBtn = new JButton("browse");
		upChoDirecText.disable();

		JButton uploadBtn = new JButton("Upload");

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

		// 업로드할 서버 디렉토리 선택
		uploadPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				uploadPathText.setText("");
				JFrame upPath = new JFrame("select");
				upPath.add(new upPathBrowser());
				uploadPathText.setText("selected");
			}
		});

		// 내 pc에서 업로드할 파일 선택
		upChoDirecBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser fileChooser = new JFileChooser(); // 파일 탐색기 객체 생성
				// 파일 선택 다이얼로그 열기
				int returnValue = fileChooser.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile(); // 선택한 파일 가져오기

					// Get the selected file's name and path
					String selectedFileName = selectedFile.getName();
					String selectedFilePath = selectedFile.getAbsolutePath();
					upLoadFilePath = selectedFilePath;
					upLoadFilePath = upLoadFilePath.replace("\\", "\\\\");
					// Set the file name and path in the upDire JTextArea
					upChoDirecText.setText(upLoadFilePath);
				} else {
					JOptionPane.showMessageDialog(null, "Load canceled", "Load canceled",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// 업로드하기
		// 프로그래스 바 움직임구현
		uploadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (upChoDirecText.getText() != null && upLoadFilePath != null) {
					// 업로드를 백그라운드 스레드로 실행
					new Thread(() -> {
						// 파일 cwd로 복사
						Path source = Paths.get(upLoadFilePath);
						upLoadFileName = String.valueOf(source.getFileName());
						Path dest = Paths.get(".").resolve(source.getFileName());
						try {
							Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
						} catch (IOException ex) {
							throw new RuntimeException(ex);
						}

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
					}).start();

				}
				else{
					JOptionPane.showMessageDialog(null, "select first", "select first",JOptionPane.ERROR_MESSAGE);
				}
				// 실행... 프로그래스 바 새창 열기
				// JFrame progBar = new JFrame();
				// progBar.add(new progressBarGUI(uploadBtn));

			}
		});
		upFileFrame.setVisible(true);
		upFileFrame.setSize(400, 220);
		upFileFrame.setLocationRelativeTo(null);
	}
}
