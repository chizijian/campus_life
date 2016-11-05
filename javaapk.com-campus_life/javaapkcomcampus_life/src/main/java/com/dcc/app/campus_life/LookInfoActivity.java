/**
 * 
 */
package com.dcc.app.campus_life;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.ItemList;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author YHX 2014-2-28下午4:03:26
 */
public class LookInfoActivity extends Activity {
	private MyApplication myApplication;
	private Button btn_back, btn_right;
	private TextView title, createTime, tx_title, type, location,
			describe, linkMan, link, tx_toPhone, tx_toMsg, tx_toLeaveMessage;
	private ItemList itemList;// 商品详细信息集合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.look_info);
		TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
		Intent intent = getIntent();
		itemList = (ItemList) intent.getSerializableExtra("shopinfo");
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LookInfoActivity.this,
						MainActivity.class);
				LookInfoActivity.this.startActivity(intent);
				LookInfoActivity.this.finish();
			}
		});
		btn_right = (Button) this.findViewById(R.id.button_right);
		btn_right.setVisibility(View.GONE);		
		title = (TextView) this.findViewById(R.id.title_tv);
		title.setText("求购信息详情");
		tx_title = (TextView) this.findViewById(R.id.tx_title);
		createTime = (TextView) this.findViewById(R.id.tx_createTime);
		type = (TextView) this.findViewById(R.id.tx_itemType);
		location = (TextView) this.findViewById(R.id.tx_location);
		describe = (TextView) this.findViewById(R.id.tx_describe);
		link = (TextView) this.findViewById(R.id.t_link);
		linkMan = (TextView) this.findViewById(R.id.t_linkMan);
		tx_title.setText(itemList.getShopname());
		Date date = itemList.getPut_time();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		createTime.setText(df.format(date));
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
				LookInfoActivity.this.startActivity(intent);
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
				LookInfoActivity.this.startActivity(intent);
			}
		});
		// 发送留言
		tx_toLeaveMessage = (TextView) this.findViewById(R.id.tx_toLeaveMsg);
		tx_toLeaveMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LookInfoActivity.this,
						LeaveMessageActivity.class);
				intent.putExtra("username", itemList.getUserName());
				LookInfoActivity.this.startActivity(intent);
			}
		});
	}
}
