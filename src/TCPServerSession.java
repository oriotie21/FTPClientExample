import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSession extends Thread{
    ServerSocket tcpServerSock;
    Socket tcpSock;
    int port;
    OutputStream outf = null;
    InputStream inf = null;
    boolean isTransfering = false;
    boolean eof = false; //command session이 trasfer finished와 관련된 응답을 받았을 때 설정
    FileEventListener fileEventListener;
    ErrorCallback errorCallback;
    String cmd = null;

    public TCPServerSession(int _port, OutputStream _outf,ErrorCallback _errorCallback, FileEventListener _listener){
        port = _port;
        outf = _outf;
        errorCallback = _errorCallback;
        fileEventListener = _listener;
    }

    public TCPServerSession(int _port, InputStream _inf, String cmd, ErrorCallback _errorCallback, FileEventListener _listener){
        port = _port;
        inf = _inf;
        errorCallback = _errorCallback;
        fileEventListener = _listener;
        this.cmd = cmd; // 이걸로 store랑 nlst 구별해야함
    }

    void saveBytesToFile(){
        byte[] buf= new byte[512];
        int rbytes = 0;
        int bytesTotal = 0;
        try{
            isTransfering = true;
            while(!eof || getDataSocketInputStream().available() > 0){
            rbytes = getDataSocketInputStream().read(buf);
            if(rbytes > 0){
                outf.write(buf, 0, rbytes);
                bytesTotal += rbytes;
                fileEventListener.onProgressChanged(bytesTotal);
            }
            }


        }
        catch(Exception e){

        }
        isTransfering = false;
        fileEventListener.onProgressFinished();
        //close stream and socket
        try {
            outf.close();
            closeDataSocket();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            errorCallback.onError(e);
        }
    }
    void sendBytes(){
        //inputstream에서 가져옴

        //소켓 outputstream 가져오기
        try {
            byte[] buf = new byte[512];
            int wbytes = 0;
            int totalBytes = 0;
			DataOutputStream oStream = new DataOutputStream(getDataSocketOutputStream());
            //inputstream 고갈 전까지 쓰기
            isTransfering = true;
            while(inf.available() > 0){
            wbytes = inf.read(buf);
            oStream.write(buf, 0, wbytes);
            totalBytes += wbytes;
            fileEventListener.onProgressChanged(totalBytes);
            }
            //소켓 닫아서 FIN 패킷 보내기
            isTransfering = false;
            closeDataSocket();
            fileEventListener.onProgressFinished();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			errorCallback.onError(e);
		}


    }
    public void run(){


        listen();
        if(inf == null && outf != null) //매개변수가 OutputStream일때
        saveBytesToFile();
        else if(outf == null && inf != null) //매개변수가 InputStream일때
        sendBytes();
        else
        System.out.println("something went wrong");

    }
    private void listen(){

        try {
            tcpServerSock = new ServerSocket(port);
            System.out.println("Listening on "+Integer.toString(port));
            tcpSock = tcpServerSock.accept();
            System.out.println("ftp data connection established");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            errorCallback.onError(e);
        }
    }
    public void download(){
        this.start();
    }
    public void upload(){
        this.start();
    }
    public void nlst() { this.start(); }
    public void setEOF(boolean b){
        eof = b;
    }
    public boolean isTransferFinished(){
        if(tcpSock != null)
            return tcpSock.isClosed();
        return true;
    }


    // 데이터 소켓의 OutputStream 반환 => tcpSock.getOutputStream() 에러 처리 포함
    private OutputStream getDataSocketOutputStream() throws IOException {
        if (tcpSock != null && tcpSock.isConnected()) {
            return tcpSock.getOutputStream();
        } else {
            throw new IOException("Data socket is not connected.");
        }
    }

    // 데이터 소켓의 InputStream 반환 => tcpSock.getInputStream() 에러 처리 포함
    private InputStream getDataSocketInputStream() throws IOException {
        if (tcpSock != null && tcpSock.isConnected()) {
            return tcpSock.getInputStream();
        } else {
            throw new IOException("Data socket is not connected.");
        }
    }

    // 데이터 소켓 닫기 => tcpSock.close() 예외 처리 포함
    private void closeDataSocket() {
        try {
            if (tcpSock != null && tcpSock.isConnected()) {
                tcpSock.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // 예외 처리
        } finally {
            tcpSock = null; // 메모리 누수 방지
        }
    }
}
