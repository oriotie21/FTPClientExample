import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class LoginGUI {
	public static void main(String[] args) {
		JFrame loginFrame = new JFrame("Welcome to FTP Program");
		JPanel loginPanel = new JPanel();

		JLabel host = new JLabel("Host:");
		JTextArea hostText = new JTextArea(1, 100);

		JLabel port = new JLabel("Port:");
		JTextArea portText = new JTextArea(1, 100);

		JLabel user = new JLabel("Username:");
		JTextArea userText = new JTextArea(1, 100);

		JLabel pw = new JLabel("Password:");
		JTextArea pwText = new JTextArea(1, 100);

		JButton loginBtn = new JButton("Login");

		loginFrame.add(loginPanel);
		loginPanel.setLayout(null);

		host.setBounds(10, 10, 50, 20); // Set bounds for label
		hostText.setBounds(100, 10, 265, 20); // Set bounds for text area

		port.setBounds(10, 40, 50, 20);
		portText.setBounds(100, 40, 265, 20);

		user.setBounds(10, 70, 80, 20);
		userText.setBounds(100, 70, 265, 20);

		pw.setBounds(10, 100, 70, 20);
		pwText.setBounds(100, 100, 265, 20);

		loginBtn.setBounds(290, 130, 80, 20);

		loginPanel.add(host);
		loginPanel.add(hostText);

		loginPanel.add(port);
		loginPanel.add(portText);

		loginPanel.add(user);
		loginPanel.add(userText);

		loginPanel.add(pw);
		loginPanel.add(pwText);

		loginPanel.add(loginBtn);

		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String host = hostText.getText();
				String port = portText.getText();
				String user = userText.getText();
				String pw = pwText.getText();

				// 로그인 실패시 에러 메세지 후 텍스트창 비우기
				JOptionPane.showMessageDialog(null, "Login Fail", "Login Fail", JOptionPane.ERROR_MESSAGE);
				hostText.setText(null);
				portText.setText(null);
				userText.setText(null);
				pwText.setText(null);
				// 로그인 성공시
				// 로그인 성공시 서버 접속 함수 불러오기
				// connectToServer();

				// 로그인 성공시 uploadFileGUI 호출
				JFrame selectUpDownload = new JFrame("Select Load");
				selectUpDownload.add(new selectUpDownloadGUI());
			}
		});

		loginFrame.setVisible(true);
		loginFrame.setSize(400, 200);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
