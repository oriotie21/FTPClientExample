import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

public class App {

	void print(String s) {
		// System.out.println(s);
	}
	static FTPSession session = new FTPSession(null, 0, null, null);
	static void guiMain() {

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
		loginFrame.getRootPane().setDefaultButton(loginBtn);

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

		setFocusTraversalKeys(hostText, portText);
		setFocusTraversalKeys(portText, userText);
		setFocusTraversalKeys(userText, pwText);
		setFocusTraversalKeys(pwText, loginBtn);

		loginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// TODO Auto-generated method stub
				String host = hostText.getText();
				String port = portText.getText();
				String user = userText.getText();
				String pw = pwText.getText();
				// int checkLogin = test(host,port,user,pw);
				int checkFlag = 0;

				try {
					int iport = Integer.parseInt(port);
					session = new FTPSession(host, iport, new ErrorCallback() {
						public void onError(Exception e) {
							System.out.println("!!Error!!");
						}
					}, new FileEventListener() {
						@Override
						public void onProgressChanged(int currentByte) {
							// TODO 진행도 바뀌었을때

						}

						@Override
						public void onProgressFinished() {
							// TODO 다운로드/업로드 끝났을때
						}
					});
					boolean conn = session.connect();
					// 서버 연결 성공시
					if (conn) {
						boolean logg = session.login(user, pw);
						// 로그인 성공시
						if (logg) {
							System.out.println("login succ");
							checkFlag = 1;
						}
						// 로그인 실패시
						else {
							System.out.println("login fail");
							checkFlag = 0;
						}
					}
					// 서버 연결 실패
					else {
						System.out.println("server fail");
						checkFlag = -1;
					}
				} catch (NumberFormatException e1) {
					// TODO: handle exception
					checkFlag = -1;
				}

				// 로그인 실패시 에러 메세지 후 텍스트창 비우기
				if (checkFlag == 0) {
					JOptionPane.showMessageDialog(null, "Login Fail", "Login Fail", JOptionPane.ERROR_MESSAGE);
					userText.setText(null);
					pwText.setText(null);
				} else if (checkFlag == -1) {
					JOptionPane.showMessageDialog(null, "Connection Fail", "Connection Fail",
							JOptionPane.ERROR_MESSAGE);
					hostText.setText(null);
					portText.setText(null);
				}

				// 로그인 성공시
				// 로그인 성공시 서버 접속 함수 불러오기
				// connectToServer();
				else {
					// 로그인 성공시 uploadFileGUI 호출
					JFrame selectUpDownload = new JFrame("Select Load");
					selectUpDownload.add(new selectUpDownloadGUI());
				}
			}
		});

		loginFrame.setVisible(true);
		loginFrame.setSize(400, 200);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private static void setFocusTraversalKeys(JComponent fromComponent, JComponent toComponent) {
		Set<AWTKeyStroke> forwardKeys = new HashSet<>(
				fromComponent.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
		forwardKeys.add(KeyStroke.getKeyStroke("TAB"));
		fromComponent.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

		Set<AWTKeyStroke> backwardKeys = new HashSet<>(
				toComponent.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
		backwardKeys.add(KeyStroke.getKeyStroke("shift TAB"));
		toComponent.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
	}

	/*
	 * static int test(String host, String sport, String userid, String pw){
	 * // string으로 받아온 sport번호를 integer로 형변환
	 * 
	 * try {
	 * int port = Integer.parseInt(sport); // 문자열을 정수로 변환
	 * 
	 * FTPSession session = new FTPSession(host, port, new ErrorCallback() {
	 * public void onError(Exception e){
	 * //System.out.println("!!Error!!");
	 * //System.out.println(e.getMessage());
	 * }
	 * }, new FileEventListener() {
	 * 
	 * @Override
	 * public void onProgressChanged(int currentByte) {
	 * // TODO 진행도 바뀌었을때
	 * 
	 * }
	 * 
	 * @Override
	 * public void onProgressFinished() {
	 * // TODO 다운로드/업로드 끝났을때
	 * }
	 * 
	 * });
	 * boolean conn = session.connect();
	 * if(conn){
	 * //로그인
	 * if(!session.login(userid, pw)){
	 * return 0;
	 * }
	 * //session.login("ftpuser", "ftp");
	 * //CWD
	 * //String cwd = session.cwd("~/aaaaaa");
	 * //String cwd = session.cwd("");
	 * //다운로드
	 * //session.retrieveFile("sc3.png", null);
	 * //session.retrieveFile("pochacco.png", null);
	 * //업로드
	 * //session.store("dogs.jpg", null);
	 * //session.store("pochacco.png", null);
	 * 
	 * //진행도 알려주는 기능 -> FileEventListener
	 * //파일 목록 보여주는거(ls와 유사, 파일크기도 구해야함)
	 * session.nlst();
	 * //에러처리(로그인, 경로, 권한, 등등 + 파일 다운받다가 중단됐을때)
	 * //암호화 요구 시 예외처리(표준에는 없어서 구현 안할예정)
	 * //익명계정 로그인(교수님 테스트 대비)
	 * //QUIT
	 * session.quit();
	 * return 1;
	 * }else{
	 * System.out.println("conn failed");
	 * return -1;
	 * }
	 * } catch (NumberFormatException e) {
	 * // 문자열을 정수로 변환할 수 없는 경우 예외가 발생함
	 * // JOptionPane.showMessageDialog(null, "Port Fail", "Port Fail",
	 * JOptionPane.ERROR_MESSAGE);
	 * // System.err.println("포트 번호가 올바르지 않습니다. 정수 값을 입력하세요.");
	 * return -1;
	 * // 또는 다른 에러 처리 로직을 수행할 수 있습니다.
	 * }
	 * //연결 수립
	 * 
	 * 
	 * }
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Hello, World!");
		guiMain();
	}

}
