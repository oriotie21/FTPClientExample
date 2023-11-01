import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class downloadLoginGUI extends JPanel{
	public downloadLoginGUI() {
		// TODO Auto-generated constructor stub
		// Initialize and configure components for the upload panel
				JFrame downFrame = new JFrame("Download");
				JPanel downPanel = new JPanel();

				JLabel downHost = new JLabel("Host:");
				JTextArea downHostText = new JTextArea(1, 100);

				JLabel downPort = new JLabel("Port:");
				JTextArea downPortText = new JTextArea(1, 100);

				JLabel downUser = new JLabel("Username:");
				JTextArea downUserText = new JTextArea(1, 100);

				JLabel downPW = new JLabel("Password:");
				JTextArea downPWText = new JTextArea(1, 100);

				JButton downLoginBtn = new JButton("Login");
				
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

				downLoginBtn.setBounds(290,130,80,20);
				

				downPanel.add(downHost);
				downPanel.add(downHostText);

				downPanel.add(downPort);
				downPanel.add(downPortText);

				downPanel.add(downUser);
				downPanel.add(downUserText);

				downPanel.add(downPW);
				downPanel.add(downPWText);

				downPanel.add(downLoginBtn);

				downLoginBtn.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String host = downHostText.getText();
						String port = downPortText.getText();
						String user = downUserText.getText();
						String pw = downPWText.getText();
						
						// 로그인 실패시 에러 메세지 후 텍스트창 비우기
						JOptionPane.showMessageDialog(null, "Login Fail", "Login Fail",JOptionPane.ERROR_MESSAGE);
						downHostText.setText(null);
						downPortText.setText(null);
						downUserText.setText(null);
						downPWText.setText(null);
						// 로그인 성공시
						// 로그인 성공시 서버 접속 함수 불러오기
						// connectToServer();
						
						// 로그인 성공시 uploadFileGUI 호출
						JFrame downFileFrame = new JFrame("Download File");
						downFileFrame.add(new downloadFileGUI());
					}
				});
				
				
				downFrame.setVisible(true);
				downFrame.setSize(400, 200);
				downFrame.setLocationRelativeTo(null);
	}
}
