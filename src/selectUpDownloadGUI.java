import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class selectUpDownloadGUI extends JPanel{
	public selectUpDownloadGUI() {
		// TODO Auto-generated constructor stub
		JFrame frame = new JFrame("Select Load");
        JPanel panel = new JPanel();
        JButton upload = new JButton("Upload");
        JButton download = new JButton("Download");
        
        frame.add(panel);
        panel.add(upload);
        panel.add(download);
        frame.add(panel);
        panel.add(upload);
        panel.add(download);
        // Add action listener for the upload button
        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame upFrame = new JFrame("Upload");
                upFrame.add(new uploadFileGUI());

            }
        });

        // Add action listener for the download button
        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame downFrame = new JFrame("Download");
                downFrame.add(new downloadFileGUI());

            }
        });

        
		
        frame.setVisible(true);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
	}
}
