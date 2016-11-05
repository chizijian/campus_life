package com.dcc.app.campus_life;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import java.util.HashMap;

public class UserRegisterActivity extends Activity {
	private HashMap<String, Object> params;
	private Button title_back,title_right,button_register;
	private ImageView iv;
	private TextView tv_warn, title_tv;;
	private String email;
	private String username;
	private String pwd;
	private String phone;
	private String school;
	private String court;
	private String professional;
	private MyApplication myApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		//TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this
				.getApplicationContext();
		myApplication.addActivity(this);
		title_tv = (TextView) this.findViewById(R.id.title_tv);
		title_tv.setText("用户注册");
		title_right = (Button) this.findViewById(R.id.button_right);
		title_right.setVisibility(View.GONE);
		title_back = (Button) this.findViewById(R.id.button_back);
		title_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(UserRegisterActivity.this,MainActivity.class);
				UserRegisterActivity.this.startActivity(intent);
				UserRegisterActivity.this.finish();

			}
		});
		button_register = (Button) this.findViewById(R.id.btn_register);
		button_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checked()) {

					params = new HashMap<String, Object>();
					params.put("email", email);
					params.put("username", username);
					params.put("pwd", pwd);
					params.put("school", school);
					params.put("court", court);
					params.put("professional", professional);
					HttpHelper.asyncPost(Constants.URL
							+ "/second-hand/add_user.do", params,
							new Callback() {
								@Override
								public void dataLoaded(Message msg) {
									if (msg.what == 200) {
										if (msg.obj.toString()
												.equals("用户名已被注册")) {
											tv_warn = (TextView) UserRegisterActivity.this
													.findViewById(R.id.tv_warn);
											tv_warn.setText("用户名已被注册");
										} else {
											Toast.makeText(
													UserRegisterActivity.this,
													//msg.obj.toString(),
													"注册成功",
													Toast.LENGTH_LONG).show();
											UserRegisterActivity.this.finish();
										}
									} else {
										Toast.makeText(
												UserRegisterActivity.this,
												"注册失败，连接超时", Toast.LENGTH_LONG)
												.show();
									}

								}
							});

				}
			}
		});

	}

	private boolean checked() {
		email = ((EditText) this.findViewById(R.id.et_email)).getText()
				.toString();
		username = ((EditText) this.findViewById(R.id.et_username)).getText()
				.toString();
		pwd = ((EditText) this.findViewById(R.id.et_user_pwd)).getText()
				.toString();
		school = ((EditText) this.findViewById(R.id.et_school)).getText()
				.toString();
		professional = ((EditText) this.findViewById(R.id.et_professional))
				.getText().toString();

		court = ((EditText) this.findViewById(R.id.et_court)).getText()
				.toString();

		boolean protocol = ((CheckBox) this.findViewById(R.id.cb_protocol))
				.isChecked();
		if (username == null || username.equals("")) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (pwd == null || pwd.equals("")) {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (school == null || school.equals("")) {
			Toast.makeText(this, "学校不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (!protocol) {
			Toast.makeText(this, "必须同意协议", Toast.LENGTH_SHORT).show();
			return false;
		} else {

			return true;
		}
	}

}
