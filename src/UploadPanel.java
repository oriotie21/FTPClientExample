

import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class UploadPanel extends JPanel {
	public UploadPanel() {
		// Initialize and configure components for the upload panel
		JFrame upFrame = new JFrame("Upload");
		JPanel upPanel = new JPanel();

		JLabel upHost = new JLabel("Host:");
		JTextArea upHostText = new JTextArea(1, 30);

		JLabel upPort = new JLabel("Port:");
		JTextArea upPortText = new JTextArea(1, 30);

		JLabel upUser = new JLabel("Username:");
		JTextArea upUserText = new JTextArea(1, 30);

		JLabel upPW = new JLabel("Password:");
		JTextArea upPWText = new JTextArea(1, 30);

		JLabel upUploadPath = new JLabel("UploadPath:");
		JTextArea upUploadPathText = new JTextArea(1, 30);

		JLabel upChoDirec = new JLabel("Choose a file:");
		JTextArea upChoDirecText = new JTextArea(1, 30);
		JButton upChoDirecBtn = new JButton("browse");
		upChoDirecText.disable();

		JButton upUploadBtn = new JButton("Uplaod");
		// 프로그레스 바
		JProgressBar upUploadBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);

		upFrame.add(upPanel);
		upPanel.setLayout(null);

		upHost.setBounds(10, 10, 50, 20); // Set bounds for label
		upHostText.setBounds(100, 10, 265, 20); // Set bounds for text area

		upPort.setBounds(10, 40, 50, 20);
		upPortText.setBounds(100, 40, 265, 20);

		upUser.setBounds(10, 70, 80, 20);
		upUserText.setBounds(100, 70, 265, 20);

		upPW.setBounds(10, 100, 70, 20);
		upPWText.setBounds(100, 100, 265, 20);

		upUploadPath.setBounds(10, 130, 90, 20);
		upUploadPathText.setBounds(100, 130, 265, 20);

		upChoDirec.setBounds(10, 160, 100, 20);
		upChoDirecText.setBounds(100, 160, 185, 20);
		upChoDirecBtn.setBounds(290, 160, 80, 20);

		upUploadBtn.setBounds(165, 185, 80, 25);
		upUploadBar.setBounds(10, 215, 365, 40);

		upPanel.add(upHost);
		upPanel.add(upHostText);

		upPanel.add(upPort);
		upPanel.add(upPortText);

		upPanel.add(upUser);
		upPanel.add(upUserText);

		upPanel.add(upPW);
		upPanel.add(upPWText);

		upPanel.add(upUploadPath);
		upPanel.add(upUploadPathText);

		upPanel.add(upChoDirec);
		upPanel.add(upChoDirecText);
		upPanel.add(upChoDirecBtn);

		upPanel.add(upUploadBtn);
		upPanel.add(upUploadBar);

		// 파일 업로드 버튼 실행시
		upChoDirecBtn.addActionListener(new ActionListener() {
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
					upChoDirecText.setText(selectedFilePath + selectedFileName);
				} else {
					JOptionPane.showMessageDialog(null, "Load canceled", "Load canceled",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		// 업로드 버튼 클릭시 발생 이벤트
		upUploadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String serverAddress = upHostText.getText();
				String username = upUserText.getText();
				String password = upPWText.getText();
				// 서버 주소 잘못되었을경우 에러처리 (지금은 비어있을경우만 해당 어느 변수에서 끌어올지 아직모름)
				if (serverAddress.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Server address, username, or password is empty or invalid.",
							"Error", JOptionPane.ERROR_MESSAGE);
					return; // Exit the actionPerformed method to prevent further execution
				}
				// 사용자이름, pw 잘못됨 (지금은 비어있을경우만 해당 어느 변수에서 끌어올지 아직모름)
				if (username.isEmpty() || password.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Server address, username, or password is empty or invalid.",
							"Error", JOptionPane.ERROR_MESSAGE);
					return; // Exit the actionPerformed method to prevent further execution
				}
				SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
					@Override
					protected Void doInBackground() throws Exception {
						// Simulate a task
						for (int i = 0; i <= 100; i++) {
							Thread.sleep(100); // Simulate some work
							publish(i); // Update the progress
						}
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

		upFrame.setVisible(true);
		upFrame.setSize(400, 300);
		upFrame.setLocationRelativeTo(null);
		upFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
