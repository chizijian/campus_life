package com.dcc.app.common;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dcc.app.entity.ItemList;
import com.dcc.app.entity.Message;
/**
 *
 */
public class PageModel<T> implements Serializable {
	private static final long serialVersionUID = -5259112447890337702L;

	private int pageSize = 10;
	private int pageNo = 1;
	private long recordCount;
	private int pageCount;
	private List<T> data;

	public PageModel() {
	}

	public PageModel(int pageSize, int pageNo) {
		super();
		this.pageSize = pageSize;
		this.pageNo = pageNo;
	}

	public static PageModel<ItemList> jsonConvert(String json){//json数据格式转换成pm模型
		ItemList itemList = null;
		List<ItemList> list = new ArrayList<ItemList>();
		PageModel<ItemList> pm = new PageModel<ItemList>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			pm.setPageNo(jsonObject.getInt("pageNo"));
			pm.setPageSize(jsonObject.getInt("pageSize"));
			pm.setRecordCount(jsonObject.getLong("recordCount"));
			JSONArray array = jsonObject.getJSONArray("data");
			for (int i=0; i < array.length();i++) {

				JSONObject obj = array.getJSONObject(i);
				itemList = new ItemList();
				itemList.setShopId(obj.optInt("shopId"));
				itemList.setCategory(obj.optString("category"));
				itemList.setCourt(obj.optString("court"));
				itemList.setDescription(obj.optString("description"));
				itemList.setPicture(obj.optString("picture"));
				itemList.setPrice(obj.optString("price"));
				Long time = obj.optLong("put_time");
				Date date = new Date(time);
				itemList.setPut_time(date);
				itemList.setSchool(obj.optString("school"));
				itemList.setShopname(obj.optString("shopname"));
				itemList.setUserName(obj.optString("userName"));
				itemList.setUserPhone(obj.optString("userPhone"));
				list.add(itemList);
			}

			pm.setData(list);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pm;
	}
	public static PageModel<Message> jsonConvertMsg(String json){//json数据格式转换成pm模型
		Message message = null;
		List<Message> list = new ArrayList<Message>();
		PageModel<Message> pm = new PageModel<Message>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			pm.setPageNo(jsonObject.getInt("pageNo"));
			pm.setPageSize(jsonObject.getInt("pageSize"));
			pm.setRecordCount(jsonObject.getLong("recordCount"));
			JSONArray array = jsonObject.getJSONArray("data");
			for (int i=0; i < array.length();i++) {

				JSONObject obj = array.getJSONObject(i);
				message = new Message();
				message.setContent(obj.getString("content"));
				Long time = obj.optLong("leave_time");
				Date date = new Date(time);
				message.setLeave_time(date);
				message.setUsername(obj.getString("username"));
				message.setReceivename(obj.getString("receivename"));
				list.add(message);
			}

			pm.setData(list);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pm;
	}
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public int getPageCount() {
		pageCount = (int)((recordCount + pageSize - 1) / pageSize);

		return pageCount;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "PageModel [pageSize=" + pageSize + ", pageNo=" + pageNo
				+ ", recordCount=" + recordCount + ", pageCount=" + pageCount
				+ ", data=" + data + "]";
	}
}
