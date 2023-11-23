import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class selectUpDownloadGUI extends JPanel{
	public selectUpDownloadGUI() {
		// TODO Auto-generated constructor stub

        // 업, 다운로드 선택할 수 있는 창 생성
		JFrame frame = new JFrame("Select Load");
        JPanel panel = new JPanel();
        JButton upload = new JButton("Upload");
        JButton download = new JButton("Download");
        
        // 창에 패널, 버튼 부착
        frame.add(panel);
        panel.add(upload);
        panel.add(download);
        frame.add(panel);
        panel.add(upload);
        panel.add(download);

        // 업로드 버튼 클릭시 발생 이벤트
        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 업로드 하는 창 만드는 함수 호출
                JFrame upFrame = new JFrame("Upload");
                upFrame.add(new uploadFileGUI());

            }
        });

        // 다운로드 버튼 클릭시 발생 이벤트
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //다운로드 하는 창 만드는 함수 호출
                JFrame downFrame = new JFrame("Download");
                downFrame.add(new downloadFileGUI());

            }
        });

        // GUI 프레임 실체화, 사이즈, 위치 지정
        frame.setVisible(true);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
	}
}
