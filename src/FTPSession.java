import java.io.*;
import java.util.Random;

import java.util.stream.Stream;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/*
 * README
 *
 * ftp의 모든 명령어는 메소드 하나로 구현
 * - 반환형태는 UserFTPResponse
 * - UserFTPResponse의 구조
 *  -> boolean success : 명령어 성공 여부
 *  -> int code : 응답 코드
 *  -> String message : 응답 메시지
 *
 * retrieveRaw(OutputStream, FileEventListener) : 데이터포트로 받아야할 데이터 있을경우 사용
 *  -> OutputStremam : 전달받은 데이터가 들어갈 스트림
 *  -> FileEventListener : 진행상황 변경시마다 호출
 *      [-] : 매개변수는 현재 주거나 받은 총 바이트 수가 제공되지만, 필요하면 매개변수 추가해도됨
 * request(String cmd, String arg) : ftp 서버에 명령어 전송할 때 사용
 *  -> 리턴은 FTPResponse 클래스{int code, String message}
 *
 */
public class FTPSession {
    static final int STATUS_DATA_CONNECTION_FAIL = 425;
    static final int STATUS_CONNECTIONS_CLOSE_TRANSMISSION_STOP = 426;
    static final int STATUS_USING_FILE_FAIL = 450;
    static final int STATUS_ACTION_FAIL = 451;
    static final int STATUS_STORAGE_FAIL = 452;
    static final int STATUS_LOGIN_FAIL = 530;
    static final int STATUS_NEED_ACCOUNT_STORE_FILE = 532;
    static final int STATUS_FILE_NOT_USE = 550; //파일 없음, 액세스 못함(권한X)


    static final String NO_FILE = "No such file";
    static final String NO_FILE_OR_FOLDER = "No such file or folder";
    static final String NO_PERMISSION = "Permission Denied";


    static final int STATUS_TRANSFER_READY = 150;
    static final int STATUS_TRANSFER_OK = 226;
    static final int STATUS_FILENAME_OK = 257;
    static final int STATUS_CMD_OK = 200;
    static final int STATUS_SERVICE_READY = 220;
    static final int STATUS_ACTION_OK = 250;
    static final int STATUS_LOGIN_SUCCSS = 230;
    static final int STATUS_NEED_PW = 331;
    static final int STATUS_TIMEOUT = 421;
    static final int STATUS_GOODBYE = 221;
    static final int STATUS_FILE_STAT = 213;

    static String CMD_CWD = "CWD";
    static String CMD_PASS = "PASS";
    static String CMD_USER = "USER";
    static String CMD_QUIT = "QUIT";
    static String CMD_PORT = "PORT";
    static String CMD_RETR = "RETR";
    static String CMD_STOR = "STOR";
    static String CMD_OPTS = "OPTS";
    static String CMD_LIST = "LIST";
    static String CMD_NLST = "NLST";
    static String CMD_PWD = "PWD";

    static String ENCODE_TYPE = "UTF8 ON";

    int PORT_INC_BASE = 19000; //지정할 data port의 시작 번호
    int PORT_INC_CNT = 1; //연결 시 마다 지정할 data port 증가 수
    int PORT_MAX = 65000; //max is 65535

    boolean connected = false;
    TCPSession tcpSession;
    TCPServerSession dataSession = null;
    FileEventListener fileEventListener = null;
    ErrorCallback errorCallback;

    String _username;
    String _password;


    public FTPSession(String _ip, int _port, ErrorCallback _errorCallback, FileEventListener _listener) {

        //연결을 위한 정보 받기
        tcpSession = new TCPSession(_ip, _port, _errorCallback);
        errorCallback = _errorCallback;
        fileEventListener = _listener;

    }

    boolean connect() {
        //연결
        if (!tcpSession.connect()) {
            return false;
        }

        //응답받기
        int code = tcpSession.getResponse().code;
        //성공 여부 반환

        if (code == STATUS_SERVICE_READY) {
            connected = true;
        }
        return connected;
    }

    boolean login(String _username, String _password) {
        this._username = _username;
        this._password = _password;
        boolean loginSuccess = false;
        FTPResponse r = request(CMD_USER, _username);

        if (r.code == STATUS_NEED_PW) {
            r = request(CMD_PASS, _password);
            if (r.code == STATUS_LOGIN_SUCCSS) {
                request(CMD_OPTS, ENCODE_TYPE); //enable UTF-8 encoding
                loginSuccess = true; //_username과 _password가 일치할 떄에만 true값을 가질 수 있음
            }
        }
        return loginSuccess;
    }

    String cwd(String _path) {
        if (!isInputReady())
            return null;
        FTPResponse r = request(CMD_CWD, _path);

        if (r.code == STATUS_ACTION_OK) { //XPWD 명령사용도 가능(하지만 사용 안함)
            return _path;
        } else {
            return null;
        }
    }

    UserFTPResponse pwd() {
        if (!isInputReady())
            return null;
        UserFTPResponse result;
        FTPResponse r = request(CMD_PWD, "");
        if (r.code != STATUS_FILENAME_OK) {
            result = new UserFTPResponse(false, r.code, r.message);
            return result;
        }
        String path = r.message.split("\"")[1];
        result = new UserFTPResponse(true, r.code, path);
        return result;
    }

    void quit() {
        if (!isInputReady())
            return;
        tcpSession.sendCmd(CMD_QUIT, "");
        tcpSession.getResponse();
        connected = false;
    }

    int cd(String _path) {
        if (!isInputReady())
            return -1;
        FTPResponse r = request(CMD_CWD, _path);

        if (r.code == STATUS_ACTION_OK) {
            return 0; // 폴더
        } else if (r.code == STATUS_FILE_NOT_USE) {
            return 1; //파일
        } else {
            return -1; //에러
        }
    }


    UserFTPResponse setPort(int p) {
        boolean r = true;
        String arg = tcpSession.getMyIP().replace('.', ',');
        arg = arg + ',' + (p / 256);
        arg = arg + ',' + (p % 256);
        FTPResponse res = request(CMD_PORT, arg);
        if (res.code != 200)
            r = false;
        return new UserFTPResponse(r, res.code, res.message);

    }

    UserFTPResponse retrieveRaw(OutputStream outf, FileEventListener listener) {
        return retrieve("", outf, listener);
    }

    UserFTPResponse retrieveFile(String fname, FileEventListener listener) {

        File file = new File(fname);
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        OutputStream oStream;
        try {
            oStream = new FileOutputStream(fname);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            oStream = null;
            e.printStackTrace();
        }
        return retrieve(fname, oStream, listener);


    }

    private UserFTPResponse retrieve(String fname, OutputStream outf, FileEventListener listener) {
        int dport = getDataPort();

        //포트 1개 오픈
        dataSession = new TCPServerSession(dport, outf, errorCallback, listener);
        dataSession.download();
        //PORT 명령으로 클라이언트 포트 전달
        UserFTPResponse ur = setPort(dport);
        if (!ur.success)
            return ur;
        //RETR <fname> 입력
        UserFTPResponse r;
        r = waitForTrasfer(dataSession, CMD_RETR, fname, new FileEventListener() {
            @Override
            public void onProgressChanged(int currentByte) {

            }

            @Override
            public void onProgressFinished() {

            }
        });
        return r;
    }

    UserFTPResponse nlst() {
        if (!isInputReady()) {
            return null;
        }

        int uport = getDataPort();
        UserFTPResponse r;
        r = setPort(uport);
        if (!r.success)
            return r;

        final byte[][] output = {new byte[512]}; // 폴더 및 파일명 저장 inputStream
        ByteArrayOutputStream ous = new ByteArrayOutputStream();

        dataSession = new TCPServerSession(uport, ous, errorCallback, fileEventListener);
        dataSession.nlst();
        final String[] outputStr = {""};
        r = waitForTrasfer(dataSession, CMD_NLST, "", new FileEventListener() {
            @Override
            public void onProgressChanged(int currentByte) {

            }

            @Override
            public void onProgressFinished() {
                output[0] = ous.toByteArray();
                try {
                    outputStr[0] = new String(output[0], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    errorCallback.onError(e);
                }
            }
        });

        //성공시
        r.success = true;
        r.code = STATUS_TRANSFER_OK;
        r.message = outputStr[0];
        return r;

    }

    UserFTPResponse store(String fname, FileEventListener listener) {
        //입력 대기
        if (!isInputReady()) {
            return null;
        }
        //데이터 전송 설정
        int uport = getDataPort();
        UserFTPResponse r;
        r = setPort(uport);
        if (!r.success)
            return r;

        try {
            //업로드 준비
            File file = new File(fname);
            if (!file.exists()) { //선택한 파일이 존재하지 않는 경우
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            dataSession = new TCPServerSession(uport, fis, errorCallback, fileEventListener);
            dataSession.upload();
            waitForTrasfer(dataSession, CMD_STOR, fname, new FileEventListener() {
                @Override
                public void onProgressChanged(int currentByte) {

                }

                @Override
                public void onProgressFinished() {

                }
            });
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return r;
    }

    UserFTPResponse waitForTrasfer(TCPServerSession session, String cmd, String fname, FileEventListener listener) {
        FTPResponse r = request(cmd, fname);
        if (r.code == STATUS_TRANSFER_READY) {
            r = tcpSession.getResponse();
            if (r == null) { //연결 끊긴 경우 연결 종료
                //System.out.println("전송이 중단되었습니다.");
                quit();
            }
            boolean recvok = false;
            /*
             *<- 이 사이에서 파일 전송이 이루어짐 ->
             * 파일 다운로드 완료 시 응답코드 리턴
             */
            session.setEOF(true);
            if (r.code == STATUS_TRANSFER_OK) {
                System.out.println("transfer success");
                recvok = true;
            }
            listener.onProgressFinished();
            return new UserFTPResponse(recvok, r.code, r.message);
        } else {
            listener.onProgressFinished();
            return new UserFTPResponse(false, r.code, r.message);
        }
    }

    boolean isInputReady() {
        return isAlive() && !isTransfering();
    }

    boolean isTransfering() {
        boolean transferInProgress = false;
        if (dataSession != null) {
            transferInProgress = !dataSession.isTransferFinished();
            if (!transferInProgress)
                dataSession = null;

        }
        return transferInProgress;
    }

    FTPResponse request(String cmd, String arg) {
        tcpSession.sendCmd(cmd, arg);
        return tcpSession.getResponse();
    }

    boolean isAlive() {
        return tcpSession.isAlive();
        //마지막 Response 받아오기(있다면?) -> 필수 아님
        //최종적으로 소켓 연결여부 확인해서 반환 isConnected() ?
    }

    private int getDataPort() {
        //Random random = new Random();
        //random.setSeed(System.currentTimeMillis());
        int randPort = new Random().nextInt(PORT_MAX - PORT_INC_BASE) + PORT_INC_BASE;
        return randPort;
    }
}
