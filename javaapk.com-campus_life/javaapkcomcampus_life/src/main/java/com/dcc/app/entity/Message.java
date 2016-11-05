/**
 * 
 */
package com.dcc.app.entity;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class Message implements Serializable {
	private int messageId;
	private String content;//留言内容
	private String username;//留言者名字
	private String receivename;//接收留言者的名字
	private Date leave_time;
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getReceivename() {
		return receivename;
	}
	public void setReceivename(String receivename) {
		this.receivename = receivename;
	}
	public Date getLeave_time() {
		return leave_time;
	}
	public void setLeave_time(Date leave_time) {
		this.leave_time = leave_time;
	}
	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", content=" + content
				+ ", username=" + username + ", receivename=" + receivename
				+ ", leave_time=" + leave_time + "]";
	}
	
}
