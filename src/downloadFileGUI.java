package GUI1;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class downloadFileGUI extends JPanel {
	public downloadFileGUI() {
		JFrame downFileFrame = new JFrame("Download File");
		JPanel downFilePanel = new JPanel();
		
		JLabel downloadPath = new JLabel("Download Path:");
		JTextArea downloadPathText = new JTextArea(1, 10);
		JButton downloadPathBtn = new JButton("browse");
		downloadPathText.disable();
		
		JLabel downChoDirec = new JLabel("Choose a file:");
		JTextArea downChoDirecText = new JTextArea(1, 100);
		JButton downChoDirecBtn = new JButton("browse");
		downChoDirecText.disable();
		
		JButton downloadBtn = new JButton("Downlaod");
		// 프로그레스 바
		JProgressBar downloadBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

		downloadPath.setBounds(10, 10, 100, 20);
		downloadPathText.setBounds(100, 10, 180, 20);
		downloadPathBtn.setBounds(300, 10, 80, 20);

		downChoDirec.setBounds(10, 40, 100, 20);
		downChoDirecText.setBounds(100, 40, 180, 20);
		downChoDirecBtn.setBounds(300, 40, 80, 20);

		downloadBtn.setBounds(155, 100, 100, 25);
		downloadBar.setBounds(10, 130, 365, 40);
		
		downFileFrame.add(downFilePanel);
		downFilePanel.setLayout(null);
		
		downFilePanel.add(downloadPath);
		downFilePanel.add(downloadPathText);
		downFilePanel.add(downloadPathBtn);

		downFilePanel.add(downChoDirec);
		downFilePanel.add(downChoDirecText);
		downFilePanel.add(downChoDirecBtn);

		downFilePanel.add(downloadBtn);
		downFilePanel.add(downloadBar);
		
		// 업로드할 서버 디렉토리 선택
		downloadPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// 내 pc에서 업로드할 파일 선택
		downChoDirecBtn.addActionListener(new ActionListener() {
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
					downChoDirecText.setText(selectedFilePath + selectedFileName);
				} else {
					JOptionPane.showMessageDialog(null, "Load canceled", "Load canceled",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// 다운로드하기
		// 프로그래스 바 움직임구현
		downloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// 다운로드 버튼 누르면 버튼 비활성화 이후 다운로드 완료시 다시 활성화
				downloadBtn.setEnabled(false);
				SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
					@Override
					protected Void doInBackground() throws Exception {
						// Simulate a task
						for (int i = 0; i <= 100; i++) {
							Thread.sleep(100); // Simulate some work
							publish(i); // Update the progress
						}
						// 다운로드 완료시 다운로드 버튼 다시 활성화
	                    SwingUtilities.invokeLater(new Runnable() {
	                        @Override
	                        public void run() {
	                            downloadBtn.setEnabled(true);
	                        }
	                    });
						return null;
					}
					@Override
					protected void process(java.util.List<Integer> chunks) {
						int latestValue = chunks.get(chunks.size() - 1);
						downloadBar.setValue(latestValue);
					}
					@Override
					protected void done() {
						downloadBar.setValue(100); // Ensure the progress reaches 100% when the task is done
					}
				};
				worker.execute();	
			}
		});
		downFileFrame.setVisible(true);
		downFileFrame.setSize(400, 220);
		downFileFrame.setLocationRelativeTo(null);
		downFileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
