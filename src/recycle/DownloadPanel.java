package recycle;


import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DownloadPanel extends JPanel {
	public DownloadPanel() {
		// Initialize and configure components for the download panel
		JFrame downFrame = new JFrame("Download");
		JPanel downPanel = new JPanel();

		JLabel downHost = new JLabel("Host:");
		JTextArea downHostText = new JTextArea(1, 30);

		JLabel downPort = new JLabel("Port:");
		JTextArea downPortText = new JTextArea(1, 30);

		JLabel downUser = new JLabel("Username:");
		JTextArea downUserText = new JTextArea(1, 30);

		JLabel downPW = new JLabel("Password:");
		JTextArea downPWText = new JTextArea(1, 30);

		JLabel downDownloadPath = new JLabel("DownloadPath:");
		JTextArea downDownloadPathText = new JTextArea(1, 30);

		JLabel downChoDirec = new JLabel("Choose a file:");
		JTextArea downChoDirecText = new JTextArea(1, 30);
		downChoDirecText.disable();
		JButton downChoDirecBtn = new JButton("browse");

		JButton downDownloadBtn = new JButton("Downlaod");
		// 프로그레스 바
		JProgressBar downDownloadBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

		downFrame.add(downPanel);
		downPanel.setLayout(null);

		downHost.setBounds(10, 10, 50, 20); // Set bounds for label
		downHostText.setBounds(100, 10, 265, 20); // Set bounds for text area

		downPort.setBounds(10, 40, 50, 20);
		downPortText.setBounds(100, 40, 265, 20);

		downUser.setBounds(10, 70, 80, 20);
		downUserText.setBounds(100, 70, 265, 20);

		downPW.setBounds(10, 100, 70, 20);
		downPWText.setBounds(100, 100, 265, 20);

		downDownloadPath.setBounds(10, 130, 90, 20);
		downDownloadPathText.setBounds(100, 130, 265, 20);

		downChoDirec.setBounds(10, 160, 100, 20);
		downChoDirecText.setBounds(100, 160, 185, 20);
		downChoDirecBtn.setBounds(290, 160, 80, 20);

		downDownloadBtn.setBounds(155, 185, 100, 25);
		downDownloadBar.setBounds(10, 215, 365, 40);

		downPanel.add(downHost);
		downPanel.add(downHostText);

		downPanel.add(downPort);
		downPanel.add(downPortText);

		downPanel.add(downUser);
		downPanel.add(downUserText);

		downPanel.add(downPW);
		downPanel.add(downPWText);

		downPanel.add(downDownloadPath);
		downPanel.add(downDownloadPathText);

		downPanel.add(downChoDirec);
		downPanel.add(downChoDirecText);
		downPanel.add(downChoDirecBtn);

		downPanel.add(downDownloadBtn);
		downPanel.add(downDownloadBar);

		// 파일 다운로드 버튼 실행시
		downChoDirecBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(); // 파일 탐색기 객체 생성
				// 파일 선택 다이얼로그 열기
				int returnValue = fileChooser.showOpenDialog(null);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile(); // 선택한 파일 가져오기

					// Get the selected file's name and path
					String selectedFileName = selectedFile.getName();
					String selectedFilePath = selectedFile.getAbsolutePath();

					// Set the file name and path in the upDire JTextArea
					downChoDirecText.setText(selectedFilePath + selectedFileName);
				} else {
					JOptionPane.showMessageDialog(null, "Load canceled", "Load canceled",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// 다운로드 버튼 클릭시 발생 이벤트
		downDownloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});

		downFrame.setVisible(true);
		downFrame.setSize(400, 300);
		downFrame.setLocationRelativeTo(null);
		downFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
