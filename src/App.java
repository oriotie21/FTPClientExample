import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;



public class App {

    void print(String s){
        System.out.println(s);
    }

    static void guiMain(){
        
            JFrame frame = new JFrame("FTP program");
            JPanel panel = new JPanel();
            JButton upload = new JButton("Upload");
            JButton download = new JButton("Download");
    
            frame.add(panel);
            panel.add(upload);
            panel.add(download);
    
            // Add action listener for the upload button
            upload.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame upFrame = new JFrame("Upload");
                    upFrame.add(new UploadPanel());
    
                }

            });
    
            // Add action listener for the download button
            download.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFrame downFrame = new JFrame("Download");
                    downFrame.add(new DownloadPanel());
    
                }
            });
    
            frame.setVisible(true);
            frame.setSize(300, 100);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
    static void test(){

        //연결 수립
        FTPSession session = new FTPSession("172.20.153.75", 21, new ErrorCallback() {
            public void onError(Exception e){
                System.out.println("!!Error!!");
                //System.out.println(e.getMessage());
            }
        }, new FileEventListener() {

            @Override
            public void onProgressChanged(int currentByte) {
                // TODO 진행도 바뀌었을때
                
            }

            @Override
            public void onProgressFinished() {
                // TODO 다운로드/업로드 끝났을때
            }
            
        });
        boolean conn = session.connect();
        if(conn){
        //로그인
        session.login("oriotie", "12345678");
        //CWD
        String cwd = session.cwd("~/bin");
        //다운로드
        session.retrieveFile("sc3.png", null);
        //업로드
        //session.store("dogs.jpg", null);

        //진행도 알려주는 기능 -> FileEventListener
        //파일 목록 보여주는거(ls와 유사, 파일크기도 구해야함)

        //에러처리(로그인, 경로, 권한, 등등 + 파일 다운받다가 중단됐을때)
        //암호화 요구 시 예외처리(표준에는 없어서 구현 안할예정)
        //익명계정 로그인(교수님 테스트 대비)



        //QUIT
        session.quit();
        }else{
            System.out.println("conn failed");
        }

    }
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        guiMain();
        //test();
    }


}
