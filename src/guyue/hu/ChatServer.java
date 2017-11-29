package guyue.hu;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author guyue
 * @date 2017��11��28�� ����9:37:04
 * @class describ:
 */
public class ChatServer {
	private ServerSocket serverSocket = null;
	private boolean isConnect = false; //�ͻ����Ƿ�����
	private List<Client> clients = new ArrayList<Client>();//�ռ��������������Ŀͻ��ˣ�������ͻ��˷�������
	
	public static void main(String[] args) {
		new ChatServer().launch();
	}
	
	//����Server��ʱ����ServerSocket
	public void launch() {
		try {
			serverSocket = new ServerSocket(18888);
			isConnect = true;
			//��������ʼһֱ���տͻ���
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
	
	//ÿ����һ���ͻ��ˣ���һ���̣߳��������߳���
	private class Client implements Runnable {
		private Socket socket = null;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean isReceive = false;
		
		public Client(Socket socket) {
			this.socket = socket;
			this.initialize();
		}
		
		//��ʼ��
		public void initialize() {
			try {
				isReceive = true;
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//��������
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
