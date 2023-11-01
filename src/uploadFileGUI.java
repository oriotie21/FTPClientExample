package GUI1;
import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class uploadFileGUI extends JPanel {
	public uploadFileGUI() {
		JFrame upFileFrame = new JFrame("Upload File");
		JPanel upFilePanel = new JPanel();
		
		JLabel upUploadPath = new JLabel("Upload Path:");
		JTextArea upUploadPathText = new JTextArea(1, 10);
		JButton upUploadPathBtn = new JButton("browse");
		upUploadPathText.disable();
		
		JLabel upChoDirec = new JLabel("Choose a file:");
		JTextArea upChoDirecText = new JTextArea(1, 100);
		JButton upChoDirecBtn = new JButton("browse");
		upChoDirecText.disable();
		
		JButton upUploadBtn = new JButton("Uplaod");
		// 프로그레스 바
		JProgressBar upUploadBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

		upUploadPath.setBounds(10, 10, 80, 20);
		upUploadPathText.setBounds(100, 10, 180, 20);
		upUploadPathBtn.setBounds(300, 10, 80, 20);

		upChoDirec.setBounds(10, 40, 80, 20);
		upChoDirecText.setBounds(100, 40, 180, 20);
		upChoDirecBtn.setBounds(300, 40, 80, 20);

		upUploadBtn.setBounds(165, 100, 80, 25);
		upUploadBar.setBounds(10, 130, 365, 40);
		
		upFileFrame.add(upFilePanel);
		upFilePanel.setLayout(null);
		
		upFilePanel.add(upUploadPath);
		upFilePanel.add(upUploadPathText);
		upFilePanel.add(upUploadPathBtn);

		upFilePanel.add(upChoDirec);
		upFilePanel.add(upChoDirecText);
		upFilePanel.add(upChoDirecBtn);

		upFilePanel.add(upUploadBtn);
		upFilePanel.add(upUploadBar);
		
		// 업로드할 서버 디렉토리 선택
		upUploadPathBtn.addActionListener(new ActionListener() {
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
		upUploadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// 업로드 버튼 누르면 버튼 비활성화 이후 업로드 완료시 다시 활성화
				upUploadBtn.setEnabled(false);
				SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
					@Override
					protected Void doInBackground() throws Exception {
						// Simulate a task
						for (int i = 0; i <= 100; i++) {
							Thread.sleep(100); // Simulate some work
							publish(i); // Update the progress
						}
						// 업로드 완료시 업로드 버튼 다시 활성화
	                    SwingUtilities.invokeLater(new Runnable() {
	                        @Override
	                        public void run() {
	                            upUploadBtn.setEnabled(true);
	                        }
	                    });
						return null;
					}
					@Override
					protected void process(java.util.List<Integer> chunks) {
						int latestValue = chunks.get(chunks.size() - 1);
						upUploadBar.setValue(latestValue);
					}
					@Override
					protected void done() {
						upUploadBar.setValue(100); // Ensure the progress reaches 100% when the task is done
					}
				};
				worker.execute();	
			}
		});
		upFileFrame.setVisible(true);
		upFileFrame.setSize(400, 220);
		upFileFrame.setLocationRelativeTo(null);
		upFileFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
