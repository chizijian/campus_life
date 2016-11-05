package com.dcc.app.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

public class MyApplication extends Application {
	public ConcurrentHashMap<String, Object> userMap;
	private List<Activity> mList = new LinkedList<Activity>();
	private List<Bitmap> listBitmap = new ArrayList<Bitmap>();
	private static MyApplication instance;

	public MyApplication() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		userMap = new ConcurrentHashMap<String, Object>();
	}

	public synchronized static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	public List<Bitmap> getListBitmap() {
		return listBitmap;
	}

	public void setListBitmap(List<Bitmap> listBitmap) {
		this.listBitmap = listBitmap;
	}

	public void destroy() {
		if (!listBitmap.isEmpty()) {
			listBitmap.clear();
		}
	}

	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
