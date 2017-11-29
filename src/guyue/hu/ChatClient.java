package guyue.hu;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * @author guyue
 * @date 2017年11月28日 下午9:16:10
 * @class describ:
 */
public class ChatClient extends Frame {
	private TextField inputField = new TextField();
	private TextArea showArea = new TextArea();
	private Socket socket = null;
	private DataOutputStream dos = null;
	private DataInputStream dis = null;
	private boolean isReceive = false;
	private Thread t = new Thread(new Receive());

	public static void main(String[] args) {
		new ChatClient().launch();
	}

	// 启动
	public void launch() {
		this.setBounds(100, 100, 300, 300);
		this.add(inputField, BorderLayout.SOUTH);
		this.add(showArea, BorderLayout.NORTH);
		showArea.setEditable(false);
		this.pack();
		this.addWindowListener(new Closing());
		inputField.addActionListener(new EnterListener());
		this.connect();
		t.start();
		this.setVisible(true);
	}
	
	public void connect() {
		try {
			socket = new Socket("127.0.0.1", 18888);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			isReceive = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//添加关闭事件
	private class Closing extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				isReceive = false;
				if(!(t.isAlive())) {
					dos.close();
					dis.close();
					socket.close();
				}
				System.exit(0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	//响应TextField的Enter事件
	private class EnterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				String str = inputField.getText();
				dos.writeUTF(str);
				dos.flush();
				inputField.setText("");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//单独起一个线程无限接收Server发送过来的数据
	private class Receive implements Runnable {

		@Override
		public void run() {
			try {
			while(isReceive) {
				String str = dis.readUTF() + "\r\n";
				showArea.setText(showArea.getText() + str);
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
