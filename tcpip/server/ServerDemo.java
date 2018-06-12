package com.atguigu.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.atguigu.vo.Constants;
import com.atguigu.vo.Message;

public class ServerDemo {
	//�̰߳�ȫ��
	private static Vector<Socket> clients = new Vector<Socket>();
	
	private static HashMap<Socket,ObjectOutputStream> maps=new HashMap<Socket,ObjectOutputStream>();

	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(9999);
			while(true) {
				Socket s = ss.accept();
				ObjectInputStream is = new ObjectInputStream(s.getInputStream());
				Message m=(Message) is.readObject();
				
				if(m!=null && m.getType()==Constants.REGISTER){
					
					//���ͻ��˷���ע����
					ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
					Message message=null;
					boolean flag=false;
					//�ж��Ƿ�ע��ɹ����鿴�û����Ƿ��Ѿ�����
					Constants type = null;
					if(flag){
						type = Constants.REGISTER_SUCCESS;
					}else{
						type = Constants.REGISTER_FAILURE;
					}
					
					message=new Message(m.getFromUser(),m.getContent(),type);
					os.writeObject(message);
					os.flush();
					s.close();
				}else if(m!=null && m.getType()==Constants.LOGIN){
					ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
					Message message=null;
					//�ж��Ƿ��¼�ɹ�
					boolean flag=true;
					
					
					if(flag){
						message=new Message(m.getFromUser(),m.getContent(),Constants.LOGIN_SUCCESS);
						os.writeObject(message);
						os.flush();
						//����ÿһ�����ӵ��û�
						clients.add(s);
						maps.put(s, os);
						//Ϊ�ÿͻ��˴���һ����Ϣ�����߳�
						new MsgHandler(s,is).start();
						
					}else{
						message=new Message(m.getFromUser(),m.getContent(),Constants.LOGIN_FAILURE);
						os.writeObject(message);
						os.flush();
						os.close();
						s.close();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	static class MsgHandler extends Thread {
		private Socket socket = null;
		private ObjectInputStream dis = null;
		private String name;
		
		public MsgHandler(Socket s,ObjectInputStream dis) {
			socket = s;
			this.dis=dis;
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					//���յ�ǰsocket����������Ϣ
					Message message=(Message) dis.readObject();
					
					if(name==null){
						name=message.getFromUser();
					}
					
					System.out.println(message);
					
					
					//����clients���ϣ�����Ϣ�������͸�ÿ���ͻ���
					synchronized(clients) {
						ArrayList<Socket> offline = new ArrayList<Socket>();//���߶���
						
						for(Socket s : clients) {
							if(s == socket) {//���˵���ǰsocket
								continue;
							}
							try {
								ObjectOutputStream os=maps.get(s);
								os.writeObject(message);
								os.flush();
								
								
							} catch(Exception e){
								offline.add(s);
							}
						}
						
						for(Socket s : offline) {
							clients.remove(s);
						}
						offline = null;
					}
					if(message.getType()==Constants.EXIT){
						clients.remove(socket);
						break;
					}
				} catch (IOException e) {
					synchronized(clients) {
						clients.remove(socket);
						maps.remove(socket);
						System.out.println(name+"�����ˡ�");
					}
					return ;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
