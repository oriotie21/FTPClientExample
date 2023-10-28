public class UserFTPResponse extends FTPResponse{ //GUI쪽에 응답 전달할 때 사용
    
    boolean success; //명령어 성공 여부
    //+ 응답코드, 메시지
    public UserFTPResponse(boolean b, int c, String m){
        super(code, message);
        success= b;
    }
}