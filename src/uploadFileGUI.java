

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class uploadFileGUI extends JPanel {
	public uploadFileGUI() {
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

					// Set the file name and path in the upDire JTextArea
					upChoDirecText.setText(selectedFilePath + selectedFileName);
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
				// TODO Auto-generated method stub
				// 업로드 버튼 누르면 버튼 비활성화 이후 업로드 완료시 다시 활성화
				uploadBtn.setEnabled(false);
				
				//실행... 프로그래스 바 새창 열기
				JFrame progBar = new JFrame();
				progBar.add(new progressBarGUI(uploadBtn));
				
			}
		});
		upFileFrame.setVisible(true);
		upFileFrame.setSize(400, 220);
		upFileFrame.setLocationRelativeTo(null);
	}
}
