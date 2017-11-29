package guyue.hu;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author guyue
 * @date 2017年11月28日 下午9:37:04
 * @class describ:
 */
public class ChatServer {
	private ServerSocket serverSocket = null;
	private boolean isConnect = false; //客户端是否连接
	private List<Client> clients = new ArrayList<Client>();//收集各个连接上来的客户端，以向各客户端发送数据
	
	public static void main(String[] args) {
		new ChatServer().launch();
	}
	
	//启动Server端时启动ServerSocket
	public void launch() {
		try {
			serverSocket = new ServerSocket(18888);
			isConnect = true;
			//从启动开始一直接收客户端
			while(isConnect) {
				Socket socket = serverSocket.accept();
				Client c = new Client(socket);
				clients.add(c);
				new Thread(c).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//每连接一个客户端，起一个线程，以下是线程类
	private class Client implements Runnable {
		private Socket socket = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean isReceive = false;
		
		public Client(Socket socket) {
			this.socket = socket;
			this.initialize();
		}
		
		//初始化
		public void initialize() {
			try {
				isReceive = true;
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//发送数据
		public void send(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(isReceive) {
					String str = dis.readUTF();
System.out.println(str);
					for(int i=0; i<clients.size(); i++) {
						Client c = clients.get(i);
						c.send(str);
					}
				}
			} catch (EOFException e) {
				System.out.println("A client quit!");
			}
			catch (SocketException e) {
				clients.remove(this);
				System.out.println("A client quit!");
			}
			catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					isReceive = false;
					if(dis != null) {
						dis.close();
					}
					if(dos != null) {
						dos.close();
					}
					if(socket != null) {
						socket.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		
	}
	
}
