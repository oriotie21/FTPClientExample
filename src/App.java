import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class App {
    static FTPSession session = new FTPSession(null, 0, null, null);

    static void guiMain() {

        // 최초 로그인창 Frame 생성
        JFrame loginFrame = new JFrame("Welcome to FTP Program");
        JPanel loginPanel = new JPanel();

        // Host 입력받는 라벨과 텍스트창 생성
        JLabel host = new JLabel("Host:");
        JTextArea hostText = new JTextArea(1, 100);

        // Port 입력받는 라벨과 텍스트창 생성
        JLabel port = new JLabel("Port:");
        JTextArea portText = new JTextArea(1, 100);

        // User ID 입력받는 라벨과 텍스트창 생성
        JLabel user = new JLabel("Username:");
        JTextArea userText = new JTextArea(1, 100);

        // User Password 입력받는 라벨과 텍스트창 생성, 암호화 위해 JPasswordField 사용
        JLabel pw = new JLabel("Password:");
        JPasswordField pwText = new JPasswordField(20);

        // 로그인 시도 버튼
        JButton loginBtn = new JButton("Login");
        loginFrame.getRootPane().setDefaultButton(loginBtn);

        // Frame창에 Panel부착
        loginFrame.add(loginPanel);
        
        // Panel에 setLayout을 설정함으로써 패널위에 올라가는 라벨들의 위치, 크기 지정가능 
        loginPanel.setLayout(null);

        // 라벨과 텍스트들의 위치, 크기 설정
        host.setBounds(10, 10, 50, 20); // Set bounds for label
        hostText.setBounds(100, 10, 265, 20); // Set bounds for text area

        port.setBounds(10, 40, 50, 20);
        portText.setBounds(100, 40, 265, 20);

        user.setBounds(10, 70, 80, 20);
        userText.setBounds(100, 70, 265, 20);

        pw.setBounds(10, 100, 70, 20);
        pwText.setBounds(100, 100, 265, 20);

        loginBtn.setBounds(290, 130, 80, 20);

        // 라벨과 텍스트 panel에 부착
        loginPanel.add(host);
        loginPanel.add(hostText);

        loginPanel.add(port);
        loginPanel.add(portText);

        loginPanel.add(user);
        loginPanel.add(userText);

        loginPanel.add(pw);
        loginPanel.add(pwText);

        loginPanel.add(loginBtn);

        // Tab(다음칸) 또는 Shift+Tab(이전칸) 사용 가능하도록 설정
        setFocusTraversalKeys(hostText, portText);
        setFocusTraversalKeys(portText, userText);
        setFocusTraversalKeys(userText, pwText);
        setFocusTraversalKeys(pwText, loginBtn);

        // 로그인버튼 클릭시 발생하는 클릭 이벤트
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // TODO Auto-generated method stub

                // textarea에 적었던 host, port, user, pw 값을 받아와 String으로 저장
                String host = hostText.getText();
                String port = portText.getText();
                String user = userText.getText();
                String pw = pwText.getText();
                // int checkLogin = test(host,port,user,pw);

                // 미사용 변수
                int checkFlag = 0;

                // String인 port 번호를 Integer로 변환
                int iport = Integer.parseInt(port);

                // static FTPSession session 생성
                session = new FTPSession(host, iport, new ErrorCallback() {
                    // 생성 실패시 
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
                // 클라이언트 - 서버 연결시도
                boolean conn = session.connect();
                // 서버 연결 성공시
                if (conn) {
                    boolean logg = session.login(user, pw);
                    // 로그인 성공시
                    if (logg) {
                        JFrame selectUpDownload = new JFrame("Select Load");
                        selectUpDownload.add(new selectUpDownloadGUI());
                    }
                    // 로그인 실패시
                    else {
                        JOptionPane.showMessageDialog(null, "Login Fail", "Login Fail", JOptionPane.ERROR_MESSAGE);
                        userText.setText(null);
                        pwText.setText(null);
                    }
                }
                // 서버 연결 실패
                else {
                    JOptionPane.showMessageDialog(null, "Connection Fail", "Connection Fail", JOptionPane.ERROR_MESSAGE);
                    hostText.setText(null);
                    portText.setText(null);
                }
            }
        });
        // GUI 프레임 실체화, 사이즈, 위치, 창 닫을시 프로그램 종료 설정
        loginFrame.setVisible(true);
        loginFrame.setSize(400, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // 로그인 창에서 Tab(다음칸) 또는 Shift+Tab(이전칸) 사용 가능하게 설정하는 함수
    private static void setFocusTraversalKeys(JComponent fromComponent, JComponent toComponent) {
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(fromComponent.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forwardKeys.add(KeyStroke.getKeyStroke("TAB"));
        fromComponent.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        Set<AWTKeyStroke> backwardKeys = new HashSet<>(toComponent.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backwardKeys.add(KeyStroke.getKeyStroke("shift TAB"));
        toComponent.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
    }
    public static void main(String[] args) throws Exception {
        guiMain();
    }
}
