import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    int PORT_INC_BASE = 19000; //지정할 data port의 시작 번호
    int PORT_INC_CNT = 1; //연결 시 마다 지정할 data port 증가 수 


    boolean connected = false;
    TCPSession tcpSession;
    TCPServerSession dataSession = null;
    FileEventListener fileEventListener = null;




    public FTPSession(String _ip, int _port, ErrorCallback _callback, FileEventListener _listener){
        
    //연결을 위한 정보 받기
    tcpSession = new TCPSession(_ip, _port, _callback);
    fileEventListener = _listener;

    }

    boolean connect(){ 
        //연결
        tcpSession.connect();
        if(tcpSession == null){
            //Raise Connection Failed Error;

        }
        //응답받기
        int code = tcpSession.getResponse().code;
        //성공 여부 반환
        
        if(code == STATUS_SERVICE_READY){
            connected = true;
        }
        return connected;
    }

    boolean login(String _username, String _password){
        boolean loginSuccess = false;
        tcpSession.sendCmd(CMD_USER, _username);
        if(tcpSession.getResponse().code == STATUS_NEED_PW){
         tcpSession.sendCmd(CMD_PASS, _password);
         if(tcpSession.getResponse().code == STATUS_LOGIN_SUCCSS){
            loginSuccess = true;
         }   
        }
        
        return loginSuccess;
    }

    String cwd(String _path){
        if(!isInputReady())
            return null;
        tcpSession.sendCmd(CMD_CWD, _path);
        if(tcpSession.getResponse().code == STATUS_ACTION_OK){ //XPWD 명령사용도 가능(하지만 사용 안함)
            return _path;
        }else{
            return null;
        }
    }
    void quit(){
        if(!isInputReady())
            return;
        tcpSession.sendCmd(CMD_QUIT, "");
        tcpSession.getResponse();
        connected = false;

    }
    UserFTPResponse setPort(int p){
        boolean r = true;
        String arg = tcpSession.getMyIP().replace('.', ',');
        arg = arg + ',' + (p / 256);
        arg = arg + ',' + (p % 256);
        FTPResponse res = request(CMD_PORT, arg);
        if(res.code != 200)
            r = false;
        return new UserFTPResponse(r, res.code, res.message);
        
    }

    UserFTPResponse retrieveRaw(OutputStream outf, FileEventListener listener){
        return retrieve("", outf, listener);
    }
    UserFTPResponse retrieveFile(String fname, FileEventListener listener){
        
        File file = new File(fname);
        if(file.exists()){
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
    private UserFTPResponse retrieve(String fname, OutputStream outf, FileEventListener listener){
        int dport = getDataPort();
        

        //포트 1개 오픈
        dataSession = new TCPServerSession(dport, outf, fileEventListener);
        dataSession.download();
        //PORT 명령으로 클라이언트 포트 전달
        UserFTPResponse ur = setPort(dport);
        if(ur.code != 200)
            return ur;
        //RETR <fname> 입력
        UserFTPResponse r = waitForTrasfer(CMD_RETR, fname);
        return r;
    }
    UserFTPResponse nlst(/*  */){
        //TODO : 파일 목록 리스팅하는 기능 구현
        return null; //임시
    }
    UserFTPResponse store(String fname, FileEventListener listener){
        //입력 대기 
        if(!isInputReady()){
            return null;
        }
        //데이터 전송 설정
        int uport = getDataPort();
        UserFTPResponse r;
        r = setPort(uport);
        if(r.code != 200)
            return r;
        
		try {
            //업로드 준비
			File file = new File(fname);
            FileInputStream fis = new FileInputStream(file);
            dataSession = new TCPServerSession(uport, fis, fileEventListener);
            dataSession.upload();
            //업로드 명령 전송
            r = waitForTrasfer(CMD_STOR, fname);
            

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return r;

    }

    UserFTPResponse waitForTrasfer(String cmd, String fname){
        FTPResponse r = request(cmd, fname);
        if(r.code == 150){
            r = tcpSession.getResponse();
            boolean recvok = false;
            /* 
            *<- 이 사이에서 파일 전송이 이루어짐 ->
            * 파일 다운로드 완료 시 응답코드 리턴
            */
            if(r.code == 226)
                 recvok = true;
            return new UserFTPResponse(recvok, r.code, r.message);
        }else{
            return new UserFTPResponse(false, r.code, r.message);
        }
    }
    boolean isInputReady(){
        return isAlive() && !isTransfering();
    }
    boolean isTransfering(){
        boolean transferInProgress = false;
        if(dataSession != null){
            transferInProgress = !dataSession.isTransferFinished();
            if(!transferInProgress)
                dataSession = null;

        }
        return transferInProgress;
    }

    FTPResponse request(String cmd, String arg){
        tcpSession.sendCmd(cmd, arg);
        return tcpSession.getResponse();
    }

    boolean isAlive(){
        return tcpSession.isAlive();
        //마지막 Response 받아오기(있다면?) -> 필수 아님 
        //최종적으로 소켓 연결여부 확인해서 반환 isConnected() ?

    }
    private int getDataPort(){
        PORT_INC_CNT++;
        return PORT_INC_BASE + PORT_INC_CNT;
    }








}
