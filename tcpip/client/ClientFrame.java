package com.atguigu.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.atguigu.vo.Constants;
import com.atguigu.vo.Message;

public class ClientFrame extends Frame {
	private String username;
	private ObjectOutputStream os;
	private ObjectInputStream is;
	// ���������ı������
	private TextArea textarea = new TextArea();
	// ����һ�������ı������
	private TextField messageInput = new TextField();

	public ClientFrame(String username, ObjectOutputStream os,ObjectInputStream is) {
		this.username = username;
		this.os=os;
		this.is=is;

		// ���ƴ���
		draw();
		// �½�UDP�̲߳�����
		new MessageReceiver().start();
	}

	private void draw() {
		this.setTitle(username);
		// ��������
		this.setLocation(300, 200);
		// ���ÿ��
		this.setSize(400, 400);
		// ����ı���
		this.add(textarea, BorderLayout.NORTH);
		textarea.setEditable(false);

		// ����һ��panel��壬ָ��һ��BorderLayout���ֹ�����
		Panel panel = new Panel(new BorderLayout());

		// ��panel��ӵ���������,ָ��λ��
		this.add(panel, BorderLayout.SOUTH);

		panel.add(messageInput, BorderLayout.CENTER);

		// ������ť����
		Button sendButton = new Button("����");
		// ����ť���������ӵ�panel����,ָ��λ��
		panel.add(sendButton, BorderLayout.EAST);

		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String messageStr = messageInput.getText();
				if (!"".equals(messageStr.trim())) {
					Message message = new Message(username, messageStr);
					textarea.setText(textarea.getText() + message + "\n");
					messageInput.setText("");

					try {
						// ������Ϣ
						os.writeObject(message);
						os.flush();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		this.pack();// ����Ӧ��С

		/** ��Ӵ��ڼ���������д���ڹر��¼� */
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//�����˳���Ϣ
				Message message = new Message(username, "������",Constants.EXIT);
				try {
					// ������Ϣ
					os.writeObject(message);
					os.flush();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.exit(0);
			}
		});

		// ���ô��ڿɼ�
		this.setVisible(true);
	}

	// �������ӣ�3��
	class MessageReceiver extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					Message message = (Message) is.readObject();
					// ���ı��������Ϣ
					textarea.setText(textarea.getText() + message + "\n");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
