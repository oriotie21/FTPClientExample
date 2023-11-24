import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;


interface ErrorCallback {
    public void onError(Exception e);
}

public class TCPSession {

    char ASC_SP = ' ';
    char ASC_CR = '\r';
    char ASC_LF = '\n';

    String ip;
    String hostIP = "";
    int port;

    private Socket tcpSock;
    private DataOutputStream outStream;
    private BufferedReader reader;
    private boolean isTransfering = false;

    ErrorCallback callBack;

    public TCPSession(String _ip, int _port, ErrorCallback _callBack) {
        ip = _ip;
        port = _port;
        callBack = _callBack;
    }

    //sendRaw
    boolean connect() {
        try {
            tcpSock = new Socket();
            tcpSock.connect(new InetSocketAddress(ip, port), 1000); //timeout을 1초로 설정 -> 연결 재시도를 계속 하는 것이 아니라 1초의 제한을 두는 것임
            reader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
            outStream = new DataOutputStream(tcpSock.getOutputStream());
            hostIP = tcpSock.getLocalAddress().getHostAddress();
            return true;
        } //소켓 연결 시도 후 실패하면 
        catch (Exception e) {
            return false;
        }
    }

    void sendCmd(String cmd, String arg) {
        String content = cmd + ASC_SP + arg + ASC_CR + ASC_LF; //cmd와 arg 사이에는 <SP>로 구분을 해야함, 명령어 끝은 <CLRF>로 구분
        sendRaw(content.getBytes());
    }

    void sendRaw(byte[] content) { //소켓으로 데이터 전송
        try {
            isTransfering = true;
            outStream.write(content);

        } catch (Exception e) {

        }
        isTransfering = false;
    }

    void asyncSendRaw(byte[] content) { //별도의 스레드에서 데이터 전송
        Thread sendThread = new Thread() {
            public void run() {
                sendRaw(content);
            }
        };
    }

    FTPResponse getResponse() {
        String content = "";
        boolean endOfResponse = false;
        try {
            do {
                content = reader.readLine();
                if(content == null) return null;
                String contents[] = content.split(Character.toString(ASC_CR) + Character.toString(ASC_LF));
                for (int i = 0; i < contents.length; i++) {
                    if (contents[i].length() >= 4) {
                        // 응답메시지의 마지막줄 구조는 NNN<SP>[Message]
                        //첫 3글자가 숫자고 네번째 글자가 공백일경우 마지막 문장으로 인식
                        if (Common.isNumeric(contents[i].substring(0, 3)) && contents[i].charAt(3) == ASC_SP) {
                            endOfResponse = true;
                            content = contents[i];
                            break;
                        }
                    }
                }
            } while (!endOfResponse);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            callBack.onError(e);
        }
        FTPResponse r = parseResp(content);
        return r;
    }

    boolean isAlive() {
        return tcpSock.isConnected();
    }

    boolean isTransfering() {
        return isTransfering;
    }


    void disconnect() {
        if (tcpSock.isConnected()) {
            try {
                tcpSock.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            outStream = null;
            reader = null;
        }
    }


    private FTPResponse parseResp(String resp) { //응답메시지 파싱 후 FTPResponse 클래스로 변환
        FTPResponse r;

        if (resp == null || resp == "0") { //연결 끊긴 경우
            r = null;            
            JOptionPane.showMessageDialog(null, "서버와의 연결이 끊겼습니다.", "연결 끊김", JOptionPane.WARNING_MESSAGE);
            return r;
        }

        boolean startsWithNumber = true;
        for (int i = 0; i < Math.min(3, resp.length()); i++) { //첫 세자리가 숫자인지 확인, 유효한 응답인지 검증용
            if (!Character.isDigit(resp.charAt(i))) {
                startsWithNumber = false;
                break;
            }
        }

        if (startsWithNumber) { //유효한 응답이면, 응답코드 응답메시지 구분해서 클래스에 저장
            int code = Integer.parseInt(resp.substring(0, 3));
            String message = resp.substring(3 + 1);
            r = new FTPResponse(code, message);
        } else {
            r = new FTPResponse(0, "Error");
        }

        return r;
    }

    public String getMyIP() {
        return hostIP;
    }

    public String getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
