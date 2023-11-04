import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class downloadFileGUI extends JPanel {
	String downLoadFilePath = null;
	public downloadFileGUI() {
		FTPSession session = App.session;

		JFrame downFileFrame = new JFrame("Download File");
		JPanel downFilePanel = new JPanel();

		JLabel downloadPath = new JLabel("Choose a file:");
		JTextArea downloadPathText = new JTextArea(1, 10);
		JButton downloadPathBtn = new JButton("browse");
		downloadPathText.disable();

		JLabel downChoDirec = new JLabel("Download Path:");
		JTextArea downChoDirecText = new JTextArea(1, 100);
		JButton downChoDirecBtn = new JButton("browse");
		downChoDirecText.disable();

		JButton downloadBtn = new JButton("Downlaod");

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
				downPath.add(new upPathBrowser());
				downloadPathText.setText("selected");
			}
		});

		// 내 pc에 어디에 저장할지 선택
		downChoDirecBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 설정을 폴더 선택 모드로 변경
				int returnVal = fileChooser.showOpenDialog(downloadFileGUI.this); // OpenDialog를 사용하여 폴더를 선택하도록 함
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = fileChooser.getSelectedFile(); // 선택한 폴더
					downChoDirecText.setText(selectedDirectory.getAbsolutePath()); // 선택한 폴더의 경로를 표시
					downLoadFilePath = downChoDirecText.getText();
				}
			}
		});

		// 다운로드하기
		// 프로그래스 바 움직임구현
		downloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (downChoDirecText.getText() != null && downloadPathText.getText() != null) {
					// 업로드를 백그라운드 스레드로 실행
					new Thread(() -> {
						// store 함수를 호출하여 파일 업로드
						UserFTPResponse response = session.store(downLoadFilePath, new FileEventListener() {
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
				} else {
					JOptionPane.showMessageDialog(null, "select first", "select first", JOptionPane.ERROR_MESSAGE);
				}
				/*
				 * // 다운로드 버튼 누르면 버튼 비활성화 이후 업로드 완료시 다시 활성화
				 * downloadBtn.setEnabled(false);
				 * 
				 * //실행... 프로그래스 바 새창 열기
				 * JFrame progBar = new JFrame();
				 * progBar.add(new progressBarGUI(downloadBtn));
				 */

			}

		});

		downFileFrame.setVisible(true);
		downFileFrame.setSize(400, 220);
		downFileFrame.setLocationRelativeTo(null);
	}
}
