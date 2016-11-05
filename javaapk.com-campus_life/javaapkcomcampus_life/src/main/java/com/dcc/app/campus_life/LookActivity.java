package com.dcc.app.campus_life;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.common.AppException;
import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.Look;
import com.dcc.app.entity.Users;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import org.apache.http.HttpStatus;

import java.util.HashMap;

public class LookActivity extends Activity {
	private Button btn_back, btn_right;
	private Spinner spinner_type;
	private String[] titles = { "体育用品", "办公用品", "生活用品", "自行车", "电子产品", "图书",
			"电脑配件" };
	private View view, dialogView;
	private TextView title, lable;// Spinner的选项，对话框的相册和相机
	private Dialog dialog;
	private Look look;// 发布求购信息实体
	private EditText shopname, userPhone, description;
	private Button release;
	private String type = "fffff";
	private MyApplication myApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look);
		TranslucentBar.setTranslucent(this);

		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
		title = (TextView) this.findViewById(R.id.title_tv);
		title.setText("发布求购信息");
		shopname = (EditText) this.findViewById(R.id.releaseTitle);
		userPhone = (EditText) this.findViewById(R.id.link);
		description = (EditText) this.findViewById(R.id.describe);
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View view = getLayoutInflater().inflate(R.layout.my_dialog,
						null, false);
				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText("温馨提示");
				TextView message = (TextView) view.findViewById(R.id.message);
				message.setVisibility(View.VISIBLE);
				message.setText("你确定要取消发布吗？你填写的内容将丢失");
				TextView gallery = (TextView) view.findViewById(R.id.gallery);
				gallery.setVisibility(View.GONE);
				TextView camera = (TextView) view.findViewById(R.id.camera);
				camera.setVisibility(View.GONE);
				final Dialog dialog = new Dialog(LookActivity.this,
						R.style.myDialogTheme);
				dialog.setContentView(view);
				dialog.show();
				Button cancel = (Button) view.findViewById(R.id.button_cancel);
				cancel.setVisibility(View.VISIBLE);
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				Button ok = (Button) view.findViewById(R.id.button_ok);
				ok.setVisibility(View.VISIBLE);
				ok.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Intent intent = new Intent(LookActivity.this,
								MainActivity.class);
						LookActivity.this.startActivity(intent);
						LookActivity.this.finish();
					}
				});
			}
		});
		// 注册按钮事件监听
		btn_right = (Button) this.findViewById(R.id.button_right);
		btn_right.setVisibility(View.GONE);
		spinner_type = (Spinner) this.findViewById(R.id.spinner_type);
		// 二手类型的spinner的适配器
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, titles) {
			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				view = getLayoutInflater().inflate(R.layout.spinner_item,
						parent, false);
				lable = (TextView) view.findViewById(R.id.label);
				lable.setText(getItem(position));

				if (spinner_type.getSelectedItemPosition() == position) {
					lable.setTextColor(getResources().getColor(
							R.color.selected_fg));
					view.setBackgroundColor(getResources().getColor(
							R.color.selected_bg));
					view.findViewById(R.id.icon).setVisibility(View.VISIBLE);
				}
				return view;
			}
		};
		spinner_type.setAdapter(adapter);
		spinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				type = titles[position];
				Log.v("dddddddddddddddddddddd", type);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// 发布求购信息
		release = (Button) this.findViewById(R.id.release);
		release.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checked()) {
					View view = getLayoutInflater().inflate(
							R.layout.progress_dialog, null, false);
					dialog = new Dialog(LookActivity.this,
							R.style.myDialogTheme);
					dialog.setContentView(view);
					dialog.show();
					Users users = (Users) myApplication.userMap.get("user");
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("lookname", shopname.getText().toString());
					params.put("userName", users.getUserName());
					params.put("userPhone", userPhone.getText().toString());
					params.put("description", description.getText().toString());
					params.put("category", type);
					Log.v("dddddddddddddddddddddd", params.toString());
					HttpHelper.asyncPost(Constants.URL
							+ "/second-hand/look_add.do", params,
							new Callback() {
								@Override
								public void dataLoaded(Message msg) {
									if (HttpStatus.SC_OK != msg.what) {
										dialog.dismiss();
										AppException.http(msg.what).makeToast(
												LookActivity.this);
										return;
									}
									dialog.dismiss();
									String message = (String) msg.obj;
									Toast.makeText(LookActivity.this, message,
											Toast.LENGTH_LONG).show();
									LookActivity.this.finish();
								}
							});
				}
			}
		});
	}

	// 发布信息验证
	private boolean checked() {
		if (shopname.getText().toString() == null
				|| shopname.getText().toString().equals("")) {
			Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (userPhone.getText().toString() == null
				|| userPhone.getText().toString().equals("")
				|| userPhone.length() != 11) {
			Toast.makeText(this, "电话是十一位", Toast.LENGTH_SHORT).show();
			return false;
		} else {

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			View view = getLayoutInflater().inflate(R.layout.my_dialog, null,
					false);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText("温馨提示");
			TextView message = (TextView) view.findViewById(R.id.message);
			message.setVisibility(View.VISIBLE);
			message.setText("你确定要取消发布吗？你填写的内容将丢失");
			TextView gallery = (TextView) view.findViewById(R.id.gallery);
			gallery.setVisibility(View.GONE);
			TextView camera = (TextView) view.findViewById(R.id.camera);
			camera.setVisibility(View.GONE);
			final Dialog dialog = new Dialog(LookActivity.this,
					R.style.myDialogTheme);
			dialog.setContentView(view);
			dialog.show();
			Button cancel = (Button) view.findViewById(R.id.button_cancel);
			cancel.setVisibility(View.VISIBLE);
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			Button ok = (Button) view.findViewById(R.id.button_ok);
			ok.setVisibility(View.VISIBLE);
			ok.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					LookActivity.this.finish();
				}
			});
		}
		return true;
	}

}
