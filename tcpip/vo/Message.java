package com.atguigu.vo;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String fromUser;//消息发送者
	private String content;//消息内容
	private Constants type;//表示消息类型，取值在Constants类中

	public Message(String fromUser, String content) {
		super();
		this.fromUser = fromUser;
		this.content = content;
	}
	public Message(String fromUser, String content, Constants type) {
		super();
		this.fromUser = fromUser;
		this.content = content;
		this.type = type;
	}
	public Message() {
		super();
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public Constants getType() {
		return type;
	}
	public void setType(Constants type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return fromUser +"   说：  " + content;
	}
}
