/**
 * 
 */
package com.dcc.app.entity;

import java.io.Serializable;

/**
 */
public class Users implements Serializable{
	private int userId;
	private String userName;
	private String password;
	private String email;
	private String school;
	private String court;
	private String professional;
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getCourt() {
		return court;
	}
	public void setCourt(String court) {
		this.court = court;
	}
	public String getProfessional() {
		return professional;
	}
	public void setProfessional(String professional) {
		this.professional = professional;
	}
	@Override
	public String toString() {
		return "Users [userId=" + userId + ", userName=" + userName
				+ ", password=" + password + ", email=" + email + ", phone="
				+   ", school=" + school + ", court=" + court
				+ ", professional=" + professional + "]";
	}

}
