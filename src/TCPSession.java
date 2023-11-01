import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



interface ErrorCallback {
    public void onError(Exception e);
}

public class TCPSession {
    
    String ASC_SP = " ";
    String ASC_CR = "\r";
    String ASC_LF = "\n";

    String ip;
    String hostIP = "";
    int port;
    
    private Socket tcpSock;
    private DataOutputStream outStream;
    private BufferedReader reader;
    private boolean isTransfering = false;

    ErrorCallback callBack;

    public TCPSession(String _ip, int _port, ErrorCallback _callBack){
        ip = _ip;
        port = _port;
        callBack = _callBack;
    }
    //sendRaw
    void connect(){
        try{
        tcpSock = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(tcpSock.getInputStream()));
        outStream = new DataOutputStream(tcpSock.getOutputStream());
        hostIP = tcpSock.getLocalAddress().getHostAddress();
        } //소켓 연결 시도 후 실패하면 
        catch(Exception e){
            callBack.onError(e);
        }


    }
    void sendCmd(String cmd, String arg){
        
        String content = cmd + ASC_SP + arg + ASC_CR + ASC_LF; //cmd와 arg 사이에는 <SP>로 구분을 해야함, 명령어 끝은 <CLRF>로 구분
        sendRaw(content.getBytes());
    
        
    }
    void sendRaw(byte[] content){
        try{
            isTransfering = true;
            outStream.write(content);
            
        }catch(Exception e){

        } 
        isTransfering = false;
    }
    void asyncSendRaw(byte[] content){
        Thread sendThread = new Thread(){
            public void run(){
                sendRaw(content);
            }
        };
    }
    FTPResponse getResponse(){
        String content = "";
        try {
            content = reader.readLine();
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            callBack.onError(e);
        }
        FTPResponse r = parseResp(content);
        return r;  
    }

    boolean isAlive(){
        return tcpSock.isConnected();
    }
    boolean isTransfering(){
        return isTransfering;
    }
    void disconnect(){
        if(tcpSock.isConnected()){
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
    

    private FTPResponse parseResp(String resp){
        FTPResponse r;

        boolean startsWithNumber = true;
        for (int i = 0; i < Math.min(3, resp.length()); i++) {
            if (!Character.isDigit(resp.charAt(i))) {
                startsWithNumber = false;
                break;
            }
        }

        if(startsWithNumber){
            int code = Integer.parseInt(resp.substring(0, 3));
            String message= resp.substring(3 + 1);
            r = new FTPResponse(code, message);
        }else{
            r = new FTPResponse(0, "Error");
        }
        
        return r;
    }
    public String getMyIP(){
        return hostIP;
    }
    public String getIP(){
        return ip;
    }
    public int getPort(){
        return port;
    }
}
