import java.io.*;
import java.util.Random;

import java.util.stream.Stream;
import java.lang.reflect.Method;
import java.util.Scanner;

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
    static final int STATUS_CMD_OK = 200;
    static final int STATUS_SERVICE_READY = 220;
    static final int STATUS_ACTION_OK = 250;
    static final int STATUS_LOGIN_SUCCSS = 230;
    static final int STATUS_NEED_PW = 331;
    static final int STATUS_TIMEOUT = 421;
    static final int STATUS_GOODBYE = 221;


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
        tcpSession.connect();
        if (tcpSession == null) {
            //Raise Connection Failed Error;

        }
        //응답받기
        int code = tcpSession.getResponse().code;
        //성공 여부 반환

        if (code == STATUS_SERVICE_READY) {
            connected = true;
        } else { // 예외처리
            connectionErrorHandling(code);
        }
        return connected;
    }

    boolean login(String _username, String _password) {
        this._username = _username;
        this._password = _password;
        boolean loginSuccess = false;
        tcpSession.sendCmd(CMD_USER, _username);

        if (tcpSession.getResponse().code == STATUS_NEED_PW) {
            tcpSession.sendCmd(CMD_PASS, _password);
            if (tcpSession.getResponse().code == STATUS_LOGIN_SUCCSS) {
                request(CMD_OPTS, ENCODE_TYPE);
                loginSuccess = true;
            } else {
                loginErrorHandling(tcpSession.getResponse().code); // 에러처리 추가
            }
        } else {
            loginErrorHandling(tcpSession.getResponse().code); // 에러처리 추가
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
            transmissionErrorHandling(r, _path, null, null, null);
            loginErrorHandling(r.code);
            return null;
        }
    }

    void quit() {
        if (!isInputReady())
            return;
        tcpSession.sendCmd(CMD_QUIT, "");
        tcpSession.getResponse();
        connected = false;

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
        dataSession = new TCPServerSession(dport, outf, errorCallback, fileEventListener);
        dataSession.download();
        //PORT 명령으로 클라이언트 포트 전달
        UserFTPResponse ur = setPort(dport);
        if (!ur.success)
            return ur;
        //RETR <fname> 입력
        UserFTPResponse r = waitForTrasfer(dataSession, CMD_RETR, fname);
        //에러 여부 확인 및 처리
        transmissionErrorHandling(r, null, fname, outf, listener);
        loginErrorHandling(r.code);
        return r;
    }

    UserFTPResponse nlst(FileEventListener listener) {
        if (!isInputReady()) {
            return null;
        }

        int uport = getDataPort();
        UserFTPResponse r;
        r = setPort(uport);
        if (!r.success)
            return r;

        //InputStream inf = null
        byte[] output = new byte[512];
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        
        dataSession = new TCPServerSession(uport, ous, errorCallback, fileEventListener);
        dataSession.nlst();
        r = waitForTrasfer(dataSession, CMD_NLST, "");

        
        output = ous.toByteArray();
        String outputStr = "*";
        try {
            outputStr = new String(output, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            errorCallback.onError(e);
        }
        System.out.println("code : "+r.code+"output : "+outputStr);
        // 상태코드 r 에러처리

        


        //에러 여부 확인 및 처리
        transmissionErrorHandling(r, null, null, null, listener);
        loginErrorHandling(r.code);

        //성공시
        r.code = STATUS_TRANSFER_OK;
        r.message = outputStr;
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
            FileInputStream fis = new FileInputStream(file);
            dataSession = new TCPServerSession(uport, fis, "store", errorCallback, fileEventListener);
            dataSession.upload();
            //업로드 명령 전송
            r = waitForTrasfer(dataSession, CMD_STOR, fname);
            transmissionErrorHandling(r, null, fname, null, listener);
            loginErrorHandling(r.code);
        } catch (FileNotFoundException e) {
            // 에러 확인 및 처리
            transmissionErrorHandling(r, null, fname, null, listener);
            loginErrorHandling(r.code);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return r;
    }

    UserFTPResponse waitForTrasfer(TCPServerSession session, String cmd, String fname) {
        FTPResponse r = request(cmd, fname);
        if (r.code == STATUS_TRANSFER_READY) {
            r = tcpSession.getResponse();
            boolean recvok = false;
            /*
             *<- 이 사이에서 파일 전송이 이루어짐 ->
             * 파일 다운로드 완료 시 응답코드 리턴
             */
            session.setEOF(true);
            if (r.code == STATUS_TRANSFER_OK)
                recvok = true;
            return new UserFTPResponse(recvok, r.code, r.message);
        } else {
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


    private void connectionErrorHandling(int statusCode) {
        if (statusCode == STATUS_DATA_CONNECTION_FAIL) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("다시 연결 하시겠습니까? 하려면 1 , 연결을 종료하려면 0을 입력하세요");
            int userInput = scanner.nextInt();
            scanner.close();

            if (userInput == 1) {
                //다시 연결 구현
                connect();
            } else if (userInput == 0) {
                //연결 종료 구현
                System.out.println("end");
            } else {
                System.out.println("잘못된 입력 값입니다.");
            }
        }
    }

    private void transmissionErrorHandling(FTPResponse r, String _path, String fname, OutputStream outf, FileEventListener listener) {
        Scanner scanner = new Scanner(System.in);
        Method callingMethod = getCallingMethod();

        if (r.code == STATUS_CONNECTIONS_CLOSE_TRANSMISSION_STOP) {  //연결이 닫히고 전송이 중단된 경우(전송이 강제 종료된 경우)
            System.out.println("전송이 중단되었습니다. 다시 시도(전송) 하시겠습니까? 하려면 1 , 연결을 종료하려면 0을 입력하세요");
            int userInput = scanner.nextInt();
            scanner.close();

            if (userInput == 1) {
                //다시 연결 후 전송
                connect();

                switch (callingMethod.getName()) {
                    case "store" -> store(fname, listener);
                    case "retrieve" -> retrieve(fname, outf, listener);
                    case "nlst" -> nlst(listener);
                }
            } else if (userInput == 0) {
                quit();
            } else {
                System.out.println("잘못된 입력 값입니다.");
            }
        } else if (r.code == STATUS_USING_FILE_FAIL) { // 요청된 파일 동작 실패. 파일 사용할 수 없는 경우(ex. 파일 사용 중)
            System.out.println("해당 파일을 사용할 수 없습니다.");
        } else if (r.code == STATUS_ACTION_FAIL) { //요청된 동작 중단. 처리 중 로컬 오류 발생
            System.out.println("로컬오류가 발생하여 요청된 동작이 중단되었습니다. 다시 시도 하시겠습니까? 하려면 1 , 연결을 종료하려면 0을 입력하세요");
            int userInput = scanner.nextInt();
            scanner.close();

            if (userInput == 1) {
                //다시 연결을 해야하나?

                //다시 동작 구현
                switch (callingMethod.getName()) {
                    case "cwd" -> cwd(_path);
                    case "store" -> store(fname, listener);
                    case "retrieve" -> retrieve(fname, outf, listener);
                    case "nlst" -> nlst(listener);
                }
            } else if (userInput == 0) {
                //연결 종료 구현
                quit();
            } else {
                System.out.println("잘못된 입력 값입니다.");
            }
        } else if (r.code == STATUS_STORAGE_FAIL) {//요청된 동작 수행X. 시스템 저장 공간 부족
            // 시스템 저장 공간 부족 하다는 알림 후 동작 종료
            System.out.println("저장공간이 부족합니다.");
        } else if (r.code == STATUS_NEED_ACCOUNT_STORE_FILE) {//파일 저장하는데 계정이 필요
            //유효한 사용자 계정으로 로그인할지 or 연결 종료할지 질의
            System.out.println("인증된 계정이 필요합니다. 유효한 사용자 계정으로 로그인 하려면 1, 연결을 종료하려면 0을 입력해 주세요");
            int userInput = scanner.nextInt();
            scanner.close();

            if (userInput == 1) {
                //연결 종료 후 다시 로그인 시도
                quit();
                login(_username, _password);
            } else if (userInput == 0) {
                //연결 종료 구현
                quit();
            } else {
                System.out.println("잘못된 입력 값입니다.");
            }
        } else if (r.code == STATUS_FILE_NOT_USE) {//잘못된 경로(파일 없음, 액세스 권한 없음)
            //잘못된 경로 or 존재하지 않는 파일 or 액세스 권한 없다는 알림 후 동작 종료
            
            System.out.println(r.code + ": 잘못된 파일 엑세스");
            switch (r.message) {
                case NO_FILE -> System.out.println("존재하지 않는 파일 입니다.");
                case NO_FILE_OR_FOLDER -> System.out.println("존재하지 않는 파일 또는 디렉토리입니다.");
                case NO_PERMISSION -> System.out.println("액세스 권한이 없습니다.");
            }
        }
    }

    private void loginErrorHandling(int statusCode) {
        Scanner scanner = new Scanner(System.in);
        int userInput;

        if (statusCode == STATUS_LOGIN_FAIL) {
            System.out.println("로그인에 실패하였습니다. 다시 시도하여 주세요.");
            login(_username, _password);
        } else if (statusCode == STATUS_NEED_ACCOUNT_STORE_FILE) {
            System.out.println("인증된 계정이 필요합니다. 유효한 사용자 계정으로 로그인 하려면 1, 연결을 종료하려면 0을 입력해 주세요");
            userInput = scanner.nextInt();
            scanner.close();

            if (userInput == 1) {
                //연결 종료 후 다시 로그인 시도
                quit();
                login(_username, _password);
            } else if (userInput == 0) {
                //연결 종료 구현
                quit();
            } else {
                System.out.println("잘못된 입력 값입니다.");
            }
        }
    }

    //자신을 호출한 메소드의 정보를 가져오는 메소드
    public static Method getCallingMethod() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length >= 3) {
            String className = stackTrace[3].getClassName();
            String methodName = stackTrace[3].getMethodName();
            try {
                Class<?> callingClass = Class.forName(className);
                Method callingMethod = callingClass.getDeclaredMethod(methodName);
                return callingMethod;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
                System.out.println("method name : "+methodName);
            }
        }
        return null;
    }
    /**
     * 연결 중 나타날 수 있는 에러
     * 425: 다시 연결 시도 할지 사용자에게 질의
     *
     * 전송 중 나타날 수 있는 에러
     * 426: 다시 연결 후 전송 할지 연결 종료 할지 질의
     * 450: 파일을 사용할 수 없다' 라고 알림 후 동작 종료
     * 451: 로컬 오류 발생했다는 알림 후 다시 동작 요청할 지 연결 종료할 지 사용자에게 질의
     * 452: 시스템 저장 공간 부족 하다는 알림 후 동작 종료
     * 532: 유효한 사용자 계정으로 로그인할지 or 연결 종료할지 질의
     * 550: 잘못된 경로 or 존재하지 않는 파일 or 액세스 권한 없다는 알림 후 동작 종료
     *
     * 로그인 중 나타날  수 있는 에러
     * 530: 로그인 실패했다는 알림 후 다시 로그인 재시도
     * 532: 유효한 사용자 계정으로 로그인할지 or 연결 종료할지 질의
     */
}
