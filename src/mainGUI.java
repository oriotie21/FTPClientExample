import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class mainGUI {
	
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub
        JFrame frame = new JFrame("FTP Server-Client");
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
                upFrame.add(new uploadLoginGUI());

            }
        });

        // Add action listener for the download button
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame downFrame = new JFrame("Download");
                downFrame.add(new downloadLoginGUI());

            }
        });

        frame.setVisible(true);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
