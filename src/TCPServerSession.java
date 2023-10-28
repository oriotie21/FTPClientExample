import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerSession extends Thread{
    ServerSocket tcpServerSock;
    Socket tcpSock;
    int port;
    OutputStream outf = null;
    InputStream inf = null;
    boolean isTransfering = false;
    FileEventListener fileEventListener;

    public TCPServerSession(int _port, OutputStream _outf, FileEventListener _listener){
        port = _port;
        outf = _outf;
        fileEventListener = _listener;
    }

    public TCPServerSession(int _port, InputStream _inf, FileEventListener _listener){
        port = _port;
        inf = _inf;
        fileEventListener = _listener;

    }



    void saveBytesToFile(){
        byte[] buf= new byte[512];
        int rbytes = 0;
        int bytesTotal = 0;
        try{
            isTransfering = true;
            while(!tcpSock.isConnected() || tcpSock.getInputStream().available() > 0){
            rbytes = tcpSock.getInputStream().read(buf);
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
            tcpSock.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void sendBytes(){
        //inputstream에서 가져옴

        //소켓 outputstream 가져오기
        try {
            byte[] buf = new byte[512];
            int wbytes = 0;
            int totalBytes = 0;
			DataOutputStream oStream = new DataOutputStream(tcpSock.getOutputStream());
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
            tcpSock.close();
            fileEventListener.onProgressFinished();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

    }

    public void run(){
        

        listen();
        if(inf == null && outf != null) //매개변수가 OutputStream일때
        saveBytesToFile();
        else if(outf == null && inf != null) //매개변수가 InputStream일때
        sendBytes();

    }
    private void listen(){
        try {
            tcpServerSock = new ServerSocket(port);
            tcpSock = tcpServerSock.accept();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void download(){
        this.start();
    }
    public void upload(){
        this.start();
    }
    public boolean isTransferFinished(){
        if(tcpSock != null)
            return tcpSock.isClosed();
        return true;
    }
}
