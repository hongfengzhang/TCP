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
		this.setTitle("��¼");
		this.setSize(314, 180);
		this.setResizable(false);//�����С���ɱ�
		//���þ���
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();
		int x = (int)((d.getWidth()-314)/2);
		int y = (int)((d.getHeight()-180)/2);
		this.setLocation(x, y);
		//����ͼ��
		URL url=ClassLoader.getSystemClassLoader().getResource("res/qq.png");
		Image image=Toolkit.getDefaultToolkit().getImage(url);
		this.setIconImage(image);
		
		//���ƴ���
		draw();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				
			}
		});
		//���ÿɼ�
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
		Label usernameLabel=new Label("�û�����");
		usernamePanel.add(usernameLabel);
//		TextField username=new TextField();
		username=new TextField();
		username.setColumns(20);
		usernamePanel.add(username);
		
		Panel passwordPanel=new Panel();
//		usernamePanel.setLayout(new FlowLayout());
		Label passwordLabel=new Label("��  �룺");
		passwordPanel.add(passwordLabel);
//		TextField password=new TextField();
//		password.setColumns(20);
//		JPasswordField password = new JPasswordField();
		password = new JPasswordField();
		password.setColumns(15);
		passwordPanel.add(password);
		
		Panel buttonPanel=new Panel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,50,0));
		Button login=new Button("��  ¼");
		Button canel=new Button("ȡ  ��");
		Button register=new Button("ע  ��");
		
		buttonPanel.add(login);
		buttonPanel.add(canel);
		buttonPanel.add(register);
		
		content.add(usernamePanel);
		content.add(passwordPanel);
		content.add(buttonPanel);
		this.add(content);
		
		//���õ�¼��ť
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
						// ����һ��socket����
						socket = new Socket("127.0.0.1", 9999);
						ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
						Message message=new Message(usernameStr,passwordStr,Constants.LOGIN);
						os.writeObject(message);
						os.flush();
						
						ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
						Message m=(Message) is.readObject();
						if(m==null || m.getType()==Constants.LOGIN_FAILURE){
							JOptionPane.showMessageDialog(register.getParent(),"��¼ʧ�ܣ��û������������", "������Ϣ", JOptionPane.WARNING_MESSAGE);
						}else if(m!=null && m.getType()==Constants.LOGIN_SUCCESS){
							//���ص�¼����
							loginWindow.setVisible(false);
							
							//�����촰��
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
		
		//����ȡ����ť
		canel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
				
			}
		});
		
		//����ע�ᰴť
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
						// ����һ��socket����
						socket = new Socket("127.0.0.1", 9999);
						
						//���������˷���ע����Ϣ
						ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
						Message message=new Message(usernameStr,passwordStr,Constants.REGISTER);
						os.writeObject(message);
						os.flush();
						
						ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
						Message m=(Message) is.readObject();
						if(m==null || m.getType()==Constants.REGISTER_FAILURE){
							JOptionPane.showMessageDialog(register.getParent(),"ע��ʧ�ܣ��û����Ѵ���", "������Ϣ", JOptionPane.WARNING_MESSAGE);
						}else if(m!=null && m.getType()==Constants.REGISTER_SUCCESS){
							 JOptionPane.showMessageDialog(register.getParent(),"ע��ɹ�", "��ʾ��Ϣ", JOptionPane.INFORMATION_MESSAGE); 
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
