package com.atguigu.client;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import com.atguigu.vo.Constants;
import com.atguigu.vo.Message;


public class LoginFrame extends Frame{
	private LoginFrame loginWindow;
	private TextField username;
	private JPasswordField password;

	public LoginFrame(){
		initialize();
	}
	
	private void initialize() {
		loginWindow=this;
		this.setTitle("登录");
		this.setSize(314, 180);
		this.setResizable(false);//窗体大小不可变
		//设置居中
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int x = (int)((d.getWidth()-314)/2);
		int y = (int)((d.getHeight()-180)/2);
		this.setLocation(x, y);
		//设置图标
		URL url=ClassLoader.getSystemClassLoader().getResource("res/qq.png");
		Image image=Toolkit.getDefaultToolkit().getImage(url);
		this.setIconImage(image);
		
		//绘制窗体
		draw();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				
			}
		});
		//设置可见
		this.setVisible(true);
	}
	
	private void draw(){
		GridLayout gridLayout = new GridLayout();
		gridLayout.setRows(3);
		gridLayout.setHgap(2);
		gridLayout.setVgap(2);
		gridLayout.setColumns(1);
		Panel content=new Panel();
		content.setLayout(gridLayout);
		
		Panel usernamePanel=new Panel();
//		usernamePanel.setLayout(new FlowLayout());
		Label usernameLabel=new Label("用户名：");
		usernamePanel.add(usernameLabel);
//		TextField username=new TextField();
		username=new TextField();
		username.setColumns(20);
		usernamePanel.add(username);
		
		Panel passwordPanel=new Panel();
//		usernamePanel.setLayout(new FlowLayout());
		Label passwordLabel=new Label("密  码：");
		passwordPanel.add(passwordLabel);
//		TextField password=new TextField();
//		password.setColumns(20);
//		JPasswordField password = new JPasswordField();
		password = new JPasswordField();
		password.setColumns(15);
		passwordPanel.add(password);
		
		Panel buttonPanel=new Panel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,50,0));
		Button login=new Button("登  录");
		Button canel=new Button("取  消");
		Button register=new Button("注  册");
		
		buttonPanel.add(login);
		buttonPanel.add(canel);
		buttonPanel.add(register);
		
		content.add(usernamePanel);
		content.add(passwordPanel);
		content.add(buttonPanel);
		this.add(content);
		
		//设置登录按钮
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameStr=username.getText();
				String passwordStr=password.getText();
				if(usernameStr!=null && !"".equals(usernameStr)){
					usernameStr=usernameStr.trim();
				}
				if(passwordStr!=null && !"".equals(passwordStr)){
					passwordStr=passwordStr.trim();
				}
				if(!"".equals(usernameStr) && !"".equals(passwordStr)){
					Socket socket=null;
					try {
						// 创建一个socket对象
						socket = new Socket("127.0.0.1", 9999);
						ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
						Message message=new Message(usernameStr,passwordStr,Constants.LOGIN);
						os.writeObject(message);
						os.flush();
						
						ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
						Message m=(Message) is.readObject();
						if(m==null || m.getType()==Constants.LOGIN_FAILURE){
							JOptionPane.showMessageDialog(register.getParent(),"登录失败，用户名或密码错误", "警告信息", JOptionPane.WARNING_MESSAGE);
						}else if(m!=null && m.getType()==Constants.LOGIN_SUCCESS){
							//隐藏登录窗口
							loginWindow.setVisible(false);
							
							//打开聊天窗口
							new ClientFrame(usernameStr,os,is);
						}
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		
		//设置取消按钮
		canel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		
		//设置注册按钮
		register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String usernameStr=username.getText();
				String passwordStr=password.getText();
				if(usernameStr!=null && !"".equals(usernameStr)){
					usernameStr=usernameStr.trim();
				}
				if(passwordStr!=null && !"".equals(passwordStr)){
					passwordStr=passwordStr.trim();
				}
				if(!"".equals(usernameStr) && !"".equals(passwordStr)){
					Socket socket=null;
					try {
						// 创建一个socket对象
						socket = new Socket("127.0.0.1", 9999);
						
						//给服务器端发送注册信息
						ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
						Message message=new Message(usernameStr,passwordStr,Constants.REGISTER);
						os.writeObject(message);
						os.flush();
						
						ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
						Message m=(Message) is.readObject();
						if(m==null || m.getType()==Constants.REGISTER_FAILURE){
							JOptionPane.showMessageDialog(register.getParent(),"注册失败，用户名已存在", "警告信息", JOptionPane.WARNING_MESSAGE);
						}else if(m!=null && m.getType()==Constants.REGISTER_SUCCESS){
							 JOptionPane.showMessageDialog(register.getParent(),"注册成功", "提示消息", JOptionPane.INFORMATION_MESSAGE); 
						}
						socket.close();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
	}
	
	public static void main(String[] args) {
		new LoginFrame();
	}

}
