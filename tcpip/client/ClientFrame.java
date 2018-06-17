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
	// 创建多行文本域对象
	private TextArea textarea = new TextArea();
	// 创建一个单行文本框对象
	private TextField messageInput = new TextField();

	public ClientFrame(String username, ObjectOutputStream os,ObjectInputStream is) {
		this.username = username;
		this.os=os;
		this.is=is;

		// 绘制窗口
		draw();
		// 新建UDP线程并启动
		new MessageReceiver().start();
	}

	private void draw() {
		this.setTitle(username);
		// 设置坐标
		this.setLocation(300, 200);
		// 设置宽高
		this.setSize(400, 400);
		// 添加文本域
		this.add(textarea, BorderLayout.NORTH);
		textarea.setEditable(false);

		// 创建一个panel面板，指定一个BorderLayout布局管理器
		Panel panel = new Panel(new BorderLayout());

		// 将panel添加到窗口上面,指定位置
		this.add(panel, BorderLayout.SOUTH);

		panel.add(messageInput, BorderLayout.CENTER);

		// 创建按钮对象
		Button sendButton = new Button("发送");
		// 将按钮和输入框添加到panel上面,指定位置
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
						// 发送消息
						os.writeObject(message);
						os.flush();
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		this.pack();// 自适应大小

		/** 添加窗口监听器，重写窗口关闭事件 */
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//发送退出消息
				Message message = new Message(username, "下线了",Constants.EXIT);
				try {
					// 发送消息
					os.writeObject(message);
					os.flush();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				System.exit(0);
			}
		});

		// 设置窗口可见
		this.setVisible(true);
	}

	// 接收连接（3）
	class MessageReceiver extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					Message message = (Message) is.readObject();
					// 向文本域添加信息
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
