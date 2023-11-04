import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class App {
<<<<<<< Updated upstream

    void print(String s){
        //System.out.println(s);
    }

    static void guiMain(){
        
            JFrame frame = new JFrame("FTP program");
            JPanel panel = new JPanel();
            JButton upload = new JButton("Upload");
            JButton download = new JButton("Download");
    
            frame.add(panel);
            panel.add(upload);
            panel.add(download);
    
            // Add action listener for the upload button
            upload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame upFrame = new JFrame("Upload");
                    upFrame.add(new UploadPanel());
    
                }
=======
	static FTPSession session = new FTPSession(null, 0, null, null);
	
	void print(String s) {
		// System.out.println(s);
	}static void guiMain() {
>>>>>>> Stashed changes

            });
    
            // Add action listener for the download button
            download.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame downFrame = new JFrame("Download");
                    downFrame.add(new DownloadPanel());
    
                }
            });
    
            frame.setVisible(true);
            frame.setSize(300, 100);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    static void test(){

        //연결 수립
        FTPSession session = new FTPSession("172.22.71.20", 21, new ErrorCallback() {
        //FTPSession session = new FTPSession("172.18.3.85", 21, new ErrorCallback() {
            public void onError(Exception e){
                System.out.println("!!Error!!");
                //System.out.println(e.getMessage());
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
        if(conn){
        //로그인
        session.login("oriotie", "1234567");
        //session.login("ftpuser", "ftp");
        //CWD
        //String cwd = session.cwd("~/aaaaaa");
        //String cwd = session.cwd("");
        //다운로드
        //session.retrieveFile("sc3.png", null);
        //session.retrieveFile("pochacco.png", null);
        //업로드
        //session.store("dogs.jpg", null);
        //session.store("pochacco.png", null);

        //진행도 알려주는 기능 -> FileEventListener
        //파일 목록 보여주는거(ls와 유사, 파일크기도 구해야함)
        session.nlst(null);
        //에러처리(로그인, 경로, 권한, 등등 + 파일 다운받다가 중단됐을때)
        //암호화 요구 시 예외처리(표준에는 없어서 구현 안할예정)
        //익명계정 로그인(교수님 테스트 대비)



        //QUIT
        session.quit();
        }else{
            System.out.println("conn failed");
        }

    }
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        guiMain();
        test();
    }

<<<<<<< Updated upstream
=======
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
					System.out.println("server fail");
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
				else { // checkFlag == 1
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
>>>>>>> Stashed changes

}
