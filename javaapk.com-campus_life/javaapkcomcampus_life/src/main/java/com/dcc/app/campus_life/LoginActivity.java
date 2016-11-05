/**
 * 
 */
package com.dcc.app.campus_life;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.common.AppException;
import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.Users;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 */
public class LoginActivity extends Activity {
	private Button btn_back, btn_login, btn_registered, btn_right;// 三个按钮
	private HashMap<String, Object> params;// 请求参数
	private TextView tv, et_name, et_pwd, tv_warn;
	private Users user;// 用户实体
	private SharedPreferences sp;
	private MyApplication myApplication;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
		sp = this.getSharedPreferences("User.xml", MODE_PRIVATE);
		tv = (TextView) this.findViewById(R.id.title_tv);
		tv.setText("登陆");
		et_name = (EditText) this.findViewById(R.id.et_name);
		et_pwd = (EditText) this.findViewById(R.id.et_pwd);
		if (sp.getString("user", "") != null
				|| !sp.getString("user", "").equals("")) {
			et_name.setText(sp.getString("user", ""));
			et_pwd.setText(sp.getString("pw", ""));
		}
		tv_warn = (TextView) this.findViewById(R.id.tv_warn);
		btn_right = (Button) this.findViewById(R.id.button_right);
		btn_right.setVisibility(View.INVISIBLE);
		tv_warn = (TextView) this.findViewById(R.id.tv_warn);
		// 标题左边图片点击监听
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent =  new Intent(LoginActivity.this,MainActivity.class);
				LoginActivity.this.startActivity(intent);
				LoginActivity.this.finish();
			}
		});
		// 注册按钮事件监听
		btn_registered = (Button) this.findViewById(R.id.bt_registered);
		btn_registered.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						UserRegisterActivity.class);
				startActivity(intent);
			}
		});
		// 登陆按钮事件监听
		btn_login = (Button) this.findViewById(R.id.bt_login);
		btn_login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (checked()) {
					if (HttpHelper.IsHaveInternet(LoginActivity.this)) {
						View view = getLayoutInflater().inflate(
								R.layout.progress_dialog, null, false);
						TextView tv_dialog = (TextView) view
								.findViewById(R.id.tv_dialog);
						tv_dialog.setText("正在登陆。。");
						dialog = new Dialog(LoginActivity.this,
								R.style.myDialogTheme);
						dialog.setContentView(view);
						dialog.show();
						params = new HashMap<String, Object>();
						params.put("username", et_name.getText().toString());
						params.put("pwd", et_pwd.getText().toString());
						HttpHelper.asyncPost(Constants.URL
								+ "/second-hand/login.do", params,
								new Callback() {

									@Override
									public void dataLoaded(Message msg) {
										if (msg.what == 200) {
											if (msg.obj.toString().equals(
													"error1")) {
												dialog.dismiss();
												tv_warn.setText("用户名不正确");
												
											} else if (msg.obj.toString()
													.equals("error2")) {
												dialog.dismiss();
												tv_warn = (TextView) LoginActivity.this
														.findViewById(R.id.tv_warn);
												tv_warn.setText("密码不正确");
											} else {
												user = new Users();
												try {
													JSONObject json = new JSONObject(
															msg.obj.toString());
													user.setUserId(json
															.getInt("userId"));
													user.setUserName(json
															.getString("userName"));
													user.setEmail(json
															.getString("email"));
													user.setPassword(json
															.getString("password"));
													user.setSchool(json
															.getString("school"));
													user.setCourt(json
															.getString("court"));
													user.setProfessional(json
															.getString("professional"));
													if (!sp.getString("user",
															"")
															.equals(json
																	.getString("userName"))) {
														Editor eidt = sp.edit();
														eidt.putString(
																"user",
																json.getString("userName"));
														eidt.putString(
																"pw",
																json.getString("password"));
														eidt.commit();
													}
													dialog.dismiss();
													myApplication.userMap.put(
															"user", user);
													
													Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
													LoginActivity.this.finish();
												} catch (JSONException e) {
													e.printStackTrace();
												}

											}

										} else {
											dialog.dismiss();
											AppException.http(msg.what).makeToast(
													LoginActivity.this);
										}
									}
								});
					} else {
						Toast.makeText(LoginActivity.this, "无网络连接",
								Toast.LENGTH_LONG).show();
					}
				}

			}
		});
	}

	private boolean checked() {
		if (et_name.getText().toString() == null
				|| et_name.getText().toString().equals("")) {
			tv_warn.setText("用户名不能为空");
			return false;
		} else if (et_pwd.getText().toString() == null
				|| et_pwd.getText().toString().equals("")) {
			tv_warn.setText("密码不能为空");
			return false;
		} else {

			return true;
		}
	}
}
