/**
 * 
 */
package com.dcc.app.entity;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class Look implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int lookid;
	private String lookname;
	private String description;
	private String username;
	private String userphone;
	private String category;
	private Date put_time;
	public int getLookid() {
		return lookid;
	}
	public void setLookid(int lookid) {
		this.lookid = lookid;
	}
	public String getLookname() {
		return lookname;
	}
	public void setLookname(String lookname) {
		this.lookname = lookname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserphone() {
		return userphone;
	}
	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Date getPut_time() {
		return put_time;
	}
	public void setPut_time(Date put_time) {
		this.put_time = put_time;
	}
	@Override
	public String toString() {
		return "Look [lookid=" + lookid + ", lookname=" + lookname
				+ ", description=" + description + ", username=" + username
				+ ", userphone=" + userphone + ", category=" + category
				+ ", put_time=" + put_time + "]";
	}
	
}
