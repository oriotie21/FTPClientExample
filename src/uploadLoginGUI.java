import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
public class uploadLoginGUI extends JPanel{
	public uploadLoginGUI() {
		// TODO Auto-generated constructor stub
		// Initialize and configure components for the upload panel
				JFrame upFrame = new JFrame("Upload");
				JPanel upPanel = new JPanel();

				JLabel upHost = new JLabel("Host:");
				JTextArea upHostText = new JTextArea(1, 100);

				JLabel upPort = new JLabel("Port:");
				JTextArea upPortText = new JTextArea(1, 100);

				JLabel upUser = new JLabel("Username:");
				JTextArea upUserText = new JTextArea(1, 100);

				JLabel upPW = new JLabel("Password:");
				JTextArea upPWText = new JTextArea(1, 100);

				JButton upLoginBtn = new JButton("Login");
				
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

				upLoginBtn.setBounds(290,130,80,20);
				

				upPanel.add(upHost);
				upPanel.add(upHostText);

				upPanel.add(upPort);
				upPanel.add(upPortText);

				upPanel.add(upUser);
				upPanel.add(upUserText);

				upPanel.add(upPW);
				upPanel.add(upPWText);

				upPanel.add(upLoginBtn);

				upLoginBtn.addActionListener(new ActionListener() {					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String host = upHostText.getText();
						String port = upPortText.getText();
						String user = upUserText.getText();
						String pw = upPWText.getText();
						
						// 로그인 실패시 에러 메세지 후 텍스트창 비우기
						JOptionPane.showMessageDialog(null, "Login Fail", "Login Fail",JOptionPane.ERROR_MESSAGE);
						upHostText.setText(null);
						upPortText.setText(null);
						upUserText.setText(null);
						upPWText.setText(null);
						// 로그인 성공시
						// 로그인 성공시 서버 접속 함수 불러오기
						// connectToServer();
						
						// 로그인 성공시 uploadFileGUI 호출
						JFrame upFileFrame = new JFrame("Upload File");
						upFileFrame.add(new uploadFileGUI());
					}
				});
				
				
				upFrame.setVisible(true);
				upFrame.setSize(400, 200);
				upFrame.setLocationRelativeTo(null);
	}
}
