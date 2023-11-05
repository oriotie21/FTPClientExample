import javax.swing.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class downloadFileGUI extends JPanel {
	String filePath = null; //다운받으려는 파일이 있는곳
	String downLoadFilePath = null; // 파일을 다운받으려고 하는 경로
	static JTextArea downloadPathText = new JTextArea();
	public downloadFileGUI() {
		FTPSession session = App.session;

		JFrame downFileFrame = new JFrame("Download File");
		JPanel downFilePanel = new JPanel();

		JLabel downloadPath = new JLabel("Choose a file:");
		downloadPathText = new JTextArea(1, 10);
		JButton downloadPathBtn = new JButton("browse");
		downloadPathText.disable();

		JLabel downChoDirec = new JLabel("Download Path:");
		JTextArea downChoDirecText = new JTextArea(1, 100);
		JButton downChoDirecBtn = new JButton("browse");
		downChoDirecText.disable();

		JButton downloadBtn = new JButton("Downlaod");

		downloadPath.setBounds(10, 10, 100, 20);
		downloadPathText.setBounds(100, 10, 180, 20);
		downloadPathBtn.setBounds(300, 10, 80, 20);

		downChoDirec.setBounds(10, 40, 100, 20);
		downChoDirecText.setBounds(100, 40, 180, 20);
		downChoDirecBtn.setBounds(300, 40, 80, 20);

		downloadBtn.setBounds(155, 100, 100, 25);

		downFileFrame.add(downFilePanel);
		downFilePanel.setLayout(null);

		downFilePanel.add(downloadPath);
		downFilePanel.add(downloadPathText);
		downFilePanel.add(downloadPathBtn);

		downFilePanel.add(downChoDirec);
		downFilePanel.add(downChoDirecText);
		downFilePanel.add(downChoDirecBtn);

		downFilePanel.add(downloadBtn);

		// 다운로드 할 파일선택
		downloadPathBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				downloadPathText.setText("");
				JFrame downPath = new JFrame("select");
				downPath.add(new downPathBrowser());
			}
		});

		// 내 pc에 어디에 저장할지 선택
		downChoDirecBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 설정을 폴더 선택 모드로 변경
				int returnVal = fileChooser.showOpenDialog(downloadFileGUI.this); // OpenDialog를 사용하여 폴더를 선택하도록 함
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedDirectory = fileChooser.getSelectedFile(); // 선택한 폴더
					downChoDirecText.setText(selectedDirectory.getAbsolutePath()); // 선택한 폴더의 경로를 표시
					downLoadFilePath = downChoDirecText.getText();
					downLoadFilePath = downLoadFilePath.replace("\\", "\\\\");
				}
			}
		});

		// 다운로드 버튼 이벤트 리스너
		downloadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 다운로드 경로와 대상 경로 확인
				if (downLoadFilePath != null && downloadPathText.getText() != null) {

					filePath = downPathBrowser.fullPath;
					filePath = filePath.replace("/", "\\\\");
					System.out.println("파일저장경로 : " + downLoadFilePath);
					System.out.println("파일경로 : " + filePath);
					
					
					
					// 백그라운드 스레드에서 파일 다운로드 및 복사 작업 실행
					new Thread(() -> {
						// 파일 다운로드
						UserFTPResponse downloadResponse = session.retrieveFile(filePath, new FileEventListener() {
							@Override
							public void onProgressChanged(int currentByte) {
								// 파일 다운로드 진행 중인 경우의 처리
								// 여기에 진행 정보 업데이트 로직을 추가할 수 있습니다.
							}

							@Override
							public void onProgressFinished() {
								// 파일 다운로드 완료 후의 처리
								// 여기에 완료 후 동작을 추가할 수 있습니다.

								// 업로드를 백그라운드 스레드로 실행
								// 파일 cwd로 복사
								Path source = Paths.get(filePath); // 다운로드한 파일의 경로를 Path로 변환
								Path dest = Paths.get(downLoadFilePath).resolve(source.getFileName()); // 복사 대상 파일의 경로를 downLoadFilePath로 설정
								fileCopy(filePath, dest.toString());
								System.out.println("파일 다운로드 및 복사 성공");
							}
						});

						if (downloadResponse != null && downloadResponse.success) {
							// 복사 성공 메시지 출력
							JOptionPane.showMessageDialog(null, "Download Success", "Download Success",
							JOptionPane.INFORMATION_MESSAGE);
						} else {
							// 파일 다운로드 실패 메시지 출력
							JOptionPane.showMessageDialog(null, "Download Fail", "Download Fail",
							JOptionPane.ERROR_MESSAGE);
							System.out.println("파일 다운로드 실패");
						}
						// 필요없는 파일 삭제
						File file = new File(filePath);
						boolean isFileDeleted = file.delete();
					}).start();
				} else {
					// 경로가 지정되지 않은 경우 메시지 표시
					JOptionPane.showMessageDialog(null, "Please select a file to download and a destination path",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		downFileFrame.setVisible(true);
		downFileFrame.setSize(400, 220);
		downFileFrame.setLocationRelativeTo(null);
	}
	public static boolean fileCopy(String inFilePath, String outFilePath) {
		try {
			FileInputStream infile = new FileInputStream(inFilePath);
			FileOutputStream outfile = new FileOutputStream(outFilePath);

			byte[] b = new byte[1024];
			int len;
			while ((len = infile.read(b, 0, 1024)) > 0) {
				outfile.write(b, 0, len);
			}
			infile.close();
			outfile.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
