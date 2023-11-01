import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class progressBarGUI extends JPanel {
	int checkFlag = 0;
    public progressBarGUI(JButton btn) {
        // TODO Auto-generated constructor stub
        JFrame proFrame = new JFrame("ing");
        JPanel proPanel = new JPanel();
        // 프로그레스 바
        JProgressBar proBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        proBar.setBounds(5, 5, 220, 50);
        proBar.setStringPainted(true); // 활성화하여 진행률 문자열 표시
        proBar.setString("0%"); // 초기 문자열 설정

        proFrame.add(proPanel);
        proPanel.setLayout(null);
        proPanel.add(proBar);

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate a task
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(10); // Simulate some work
                    publish(i); // Update the progress
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latestValue = chunks.get(chunks.size() - 1);
                proBar.setValue(latestValue);
                proBar.setString(latestValue + "%"); // 진행률 문자열 업데이트
            }

            @Override
            protected void done() {
            	if(checkFlag == 0) {
                    proBar.setValue(100); // Ensure the progress reaches 100% when the task is done
                    JOptionPane.showMessageDialog(null, "Success", "Success", JOptionPane.INFORMATION_MESSAGE);
            	}else {
            		JOptionPane.showMessageDialog(null, "Canceled", "Canceled", JOptionPane.ERROR_MESSAGE);
            	}
                proFrame.dispose(); // Close the progress frame when done
                // 업로드 완료시 업로드 버튼 다시 활성화
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        btn.setEnabled(true);
                    }
                });	
            }
        };
        worker.execute();

        proFrame.setSize(250, 100);
        proFrame.setLocationRelativeTo(null);
        proFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the frame without exiting the application
        proFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	// 프로그래스 바 중지
            	checkFlag = 1;
            	proBar.setValue(0);
            	if (!worker.isCancelled()) {
                    worker.cancel(true); // SwingWorker를 취소
                }
            }
        });

        proFrame.setVisible(true);
    }
}
