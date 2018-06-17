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
	//线程安全的
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
					
					//给客户端反馈注册结果
					ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
					Message message=null;
					boolean flag=false;
					//判断是否注册成功，查看用户名是否已经存在
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
					//判断是否登录成功
					boolean flag=true;
					
					
					if(flag){
						message=new Message(m.getFromUser(),m.getContent(),Constants.LOGIN_SUCCESS);
						os.writeObject(message);
						os.flush();
						//保存每一个连接的用户
						clients.add(s);
						maps.put(s, os);
						//为该客户端创建一个消息处理线程
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
					//接收当前socket发过来的消息
					Message message=(Message) dis.readObject();
					
					if(name==null){
						name=message.getFromUser();
					}
					
					System.out.println(message);
					
					
					//遍历clients集合，将消息挨个发送给每个客户端
					synchronized(clients) {
						ArrayList<Socket> offline = new ArrayList<Socket>();//掉线队列
						
						for(Socket s : clients) {
							if(s == socket) {//过滤掉当前socket
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
						System.out.println(name+"下线了。");
					}
					return ;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
