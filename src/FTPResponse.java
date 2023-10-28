public class FTPResponse { //FTP 응답 클래스화
   public static int code; //응답코드
   public static String message; //메시지 
   public FTPResponse(int c, String m){
    code = c;
    message = m;
   }
}
