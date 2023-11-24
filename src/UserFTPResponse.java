public class UserFTPResponse extends FTPResponse{ //GUI쪽에 응답 전달할 때 사용
   //FTPResponse에서 명령어 성공 여부를 추가한 클래스 
   
    boolean success; //명령어 성공 여부
    
    public UserFTPResponse(boolean b, int c, String m){
        super(code, message);
        success= b;
    }
}