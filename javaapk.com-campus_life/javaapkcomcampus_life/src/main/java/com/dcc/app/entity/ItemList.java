/**
 * 
 */
package com.dcc.app.entity;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class ItemList implements Serializable {
	private static final long serialVersionUID = 1L;
	private int shopId;
	private String shopname;
	private String description;
	private String userPhone;
	private String category;
	private String picture;
	private String price;
	private Date put_time;
	private String userName;
	private String school;
	private String court;
	public int getShopId() {
		return shopId;
	}
	public void setShopId(int shopId) {
		this.shopId = shopId;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Date getPut_time() {
		return put_time;
	}
	public void setPut_time(Date put_time) {
		this.put_time = put_time;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	@Override
	public String toString() {
		return "ItemList [shopId=" + shopId + ", shopname=" + shopname
				+ ", description=" + description + ", userPhone=" + userPhone
				+ ", category=" + category + ", picture=" + picture
				+ ", price=" + price + ", put_time=" + put_time + ", userName="
				+ userName + ", school=" + school + ", court=" + court
				+ ", professional="  + "]";
	}
	
}
