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

        JFrame loginFrame = new JFrame("Welcome to FTP Program");
        JPanel loginPanel = new JPanel();

        JLabel host = new JLabel("Host:");
        JTextArea hostText = new JTextArea(1, 100);

        JLabel port = new JLabel("Port:");
        JTextArea portText = new JTextArea(1, 100);

        JLabel user = new JLabel("Username:");
        JTextArea userText = new JTextArea(1, 100);

        JLabel pw = new JLabel("Password:");
        JPasswordField pwText = new JPasswordField(20);

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
                    JOptionPane.showMessageDialog(null, "Connection Fail", "Connection Fail",
                            JOptionPane.ERROR_MESSAGE);
                    hostText.setText(null);
                    portText.setText(null);
                }
            }
        });

        loginFrame.setVisible(true);
        loginFrame.setSize(400, 200);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    // 입력 창에서 tab누를 시 다음 입력 창으로 가게하는 함수
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
    public static void main(String[] args) throws Exception {
        guiMain();
    }
}
