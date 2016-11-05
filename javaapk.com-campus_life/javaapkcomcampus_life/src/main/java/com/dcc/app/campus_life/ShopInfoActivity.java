package com.dcc.app.campus_life;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.common.AppException;
import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.ItemList;
import com.dcc.app.entity.Users;
import com.dcc.app.service.MyApplication;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.yhx.app.campus_life.R;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 */
public class ShopInfoActivity extends Activity {
	private MyApplication myApplication;
	private Button btn_back, btn_right;
	private TextView title, createTime, tx_title, price, type, location,
			describe, linkMan, link, tx_toPhone, tx_toMsg, tx_toLeaveMessage;
	private ItemList itemList;// 商品详细信息集合
	private List<Bitmap> listBitmap = new ArrayList<Bitmap>();// 下载的图片集合
	private LinearLayout layout;
	private ViewPager vp;
	private PageIndicator indicator;
	private ArrayList<View> pageViews;
	private View view1, view2, view3;// 图片的布局
	private ImageView image1, image2, image3;// 图片显示
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_main);
		TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
		Intent intent = getIntent();
		itemList = (ItemList) intent.getSerializableExtra("shopinfo");
		layout = (LinearLayout) this.findViewById(R.id.linerlayout);
		// 初始化顶部的图片
		initData();
		// 循环下载图片
		if (itemList.getPicture() != null && !itemList.getPicture().equals("")) {
			String[] strings = itemList.getPicture().split(";");// 获取图片名字
			int i = 0;
			for (String string : strings) {
				i += 1;
				if (string != null) {
					new MyTask().execute(Constants.URL + "/second-hand/images/"
							+ string, i + "");
				}
			}
		}
		vp = (ViewPager) this.findViewById(R.id.viewpager);
		vp.setPageMargin(2); // 页间距
		vp.setAdapter(new GuidePageAdapter());

		indicator = (CirclePageIndicator) this.findViewById(R.id.indicator);
		indicator.setViewPager(vp);
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShopInfoActivity.this,
						MainActivity.class);
				ShopInfoActivity.this.startActivity(intent);
				ShopInfoActivity.this.finish();
			}
		});
		btn_right = (Button) this.findViewById(R.id.button_right);
		btn_right.setText("收藏");
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Users users = (Users) myApplication.userMap.get("user");
				if (users != null) {
					username = users.getUserName();
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("shopid", itemList.getShopId());
					params.put("username", username);
					HttpHelper.asyncPost(Constants.URL + "/second-hand/collection_add.do", params, new Callback() {
						
						@Override
						public void dataLoaded(Message msg) {
							if (HttpStatus.SC_OK != msg.what) {
								AppException.http(msg.what).makeToast(
										ShopInfoActivity.this);
								return;
							}
							Toast.makeText(ShopInfoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					View dialogView = getLayoutInflater().inflate(
							R.layout.my_dialog, null, false);
					TextView title = (TextView) dialogView
							.findViewById(R.id.title);
					title.setText("温馨提示");
					TextView message = (TextView) dialogView
							.findViewById(R.id.message);
					message.setVisibility(View.VISIBLE);
					message.setText("你好没有登录，请先登录");
					TextView gallery = (TextView) dialogView
							.findViewById(R.id.gallery);
					gallery.setVisibility(View.GONE);
					TextView camera = (TextView) dialogView
							.findViewById(R.id.camera);
					camera.setVisibility(View.GONE);
					final Dialog dialog = new Dialog(ShopInfoActivity.this,
							R.style.myDialogTheme);
					dialog.setContentView(dialogView);
					dialog.show();
					Button cancel = (Button) dialogView
							.findViewById(R.id.button_cancel);
					cancel.setVisibility(View.VISIBLE);
					cancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					Button ok = (Button) dialogView
							.findViewById(R.id.button_ok);
					ok.setVisibility(View.VISIBLE);
					ok.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(ShopInfoActivity.this,
									LoginActivity.class);
							ShopInfoActivity.this.startActivityForResult(
									intent, 2);
						}
					});
				}
			}
		});
		title = (TextView) this.findViewById(R.id.title_tv);
		title.setText("商品详情");
		tx_title = (TextView) this.findViewById(R.id.tx_title);
		createTime = (TextView) this.findViewById(R.id.tx_createTime);
		price = (TextView) this.findViewById(R.id.tx_price);
		type = (TextView) this.findViewById(R.id.tx_itemType);
		location = (TextView) this.findViewById(R.id.tx_location);
		describe = (TextView) this.findViewById(R.id.tx_describe);
		link = (TextView) this.findViewById(R.id.t_link);
		linkMan = (TextView) this.findViewById(R.id.t_linkMan);
		tx_title.setText(itemList.getShopname());
		Date date = itemList.getPut_time();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		createTime.setText(df.format(date));
		price.setText(itemList.getPrice());
		type.setText(itemList.getCategory());
		location.setText(itemList.getSchool() + "/" + itemList.getCourt());
		describe.setText(itemList.getDescription());
		linkMan.setText(itemList.getUserName());
		link.setText(itemList.getUserPhone());
		// 拨打电话
		tx_toPhone = (TextView) this.findViewById(R.id.tx_toPhone);
		tx_toPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 系统默认的action，用来打开默认的电话界面
				intent.setAction(Intent.ACTION_DIAL);
				// 需要拨打的号码
				intent.setData(Uri.parse("tel:" + link.getText().toString()));
				ShopInfoActivity.this.startActivity(intent);
			}
		});
		// 发送短信
		tx_toMsg = (TextView) this.findViewById(R.id.tx_toMsg);
		tx_toMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				// 系统默认的action，用来打开默认的短信界面
				intent.setAction(Intent.ACTION_SENDTO);
				// 需要发短息的号码
				intent.setData(Uri.parse("smsto:" + link.getText().toString()));
				ShopInfoActivity.this.startActivity(intent);
			}
		});
		// 发送留言
		tx_toLeaveMessage = (TextView) this.findViewById(R.id.tx_toLeaveMsg);
		tx_toLeaveMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShopInfoActivity.this,
						LeaveMessageActivity.class);
				intent.putExtra("username", itemList.getUserName());
				ShopInfoActivity.this.startActivity(intent);
			}
		});
	}
	/**
	 * 初始化顶部的图片显示
	 */
	private void initData() {
		// 加载引导页
		LayoutInflater inflater = LayoutInflater.from(ShopInfoActivity.this);
		pageViews = new ArrayList<View>();
		view1 = inflater.inflate(R.layout.guide_item_shop1, null);
		image1 = (ImageView) view1.findViewById(R.id.image1);
		view2 = inflater.inflate(R.layout.guide_item_shop2, null);
		image2 = (ImageView) view2.findViewById(R.id.image2);
		view3 = inflater.inflate(R.layout.guide_item_shop3, null);
		image3 = (ImageView) view3.findViewById(R.id.image3);
		if (itemList.getPicture() != null && !itemList.getPicture().equals("")) {
			String[] strings = itemList.getPicture().split(";");
			int total = strings.length;
			for (int i = 0; i < total; i++) {
				if (i == 0) {
					pageViews.add(view1);
				} else if (i == 1) {
					pageViews.add(view2);
				} else {
					pageViews.add(view3);
				}
			}
		} else {
			layout.setVisibility(View.GONE);
		}
	}

	// 引导页面数据适配器
	public class GuidePageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return pageViews == null ? 0 : pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			View view = pageViews.get(arg1);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!listBitmap.isEmpty()) {
						myApplication.setListBitmap(listBitmap);
						Intent intent = new Intent(ShopInfoActivity.this,
								PicturePreview.class);
						ShopInfoActivity.this.startActivity(intent);
					} else {
						Toast.makeText(ShopInfoActivity.this, "没有图片", Toast.LENGTH_SHORT).show();
					}
				}
			});
			((ViewPager) arg0).addView(view);

			return pageViews.get(arg1);
		}
	}

	// 异步加载图片
	public class MyTask extends
			AsyncTask<String, Void, HashMap<String, Object>> {

		@Override
		protected HashMap<String, Object> doInBackground(String... params) {
			try {
				HttpClient httpClient = HttpHelper.getHttpClient();
				String uri = params[0];
				HttpResponse response = httpClient.execute(new HttpGet(uri));
				if (response.getStatusLine().getStatusCode() == 200) {
					InputStream is = (InputStream) response.getEntity()
							.getContent();
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = 2;
					Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
					is.close();
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("bitmap", bitmap);
					hashMap.put("int", params[1]);
					return hashMap;
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> result) {
			if (result == null) {
				Toast.makeText(ShopInfoActivity.this, "加载图片失败", Toast.LENGTH_LONG).show();
				return;
			}
			Bitmap bitmap = (Bitmap) result.get("bitmap");
			String string = (String) result.get("int");
			listBitmap.add(bitmap);
			if (string.equals("1")) {
				image1.setImageBitmap(bitmap);
			} else if (string.equals("2")) {
				image2.setImageBitmap(bitmap);
			} else {
				image3.setImageBitmap(bitmap);
			}

		}

	}

	// 这个activity销毁的时候自动回收bitmap释放资源
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!listBitmap.isEmpty()) {
			for (int i = 0; i < listBitmap.size(); i++) {
				listBitmap.get(i).recycle();
			}
		}
	}
}
