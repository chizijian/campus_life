package com.dcc.app.campus_life;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.common.AppException;
import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.Shop;
import com.dcc.app.entity.Users;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReleaseActivity extends Activity {
	private Button btn_back,btn_right;
	private Spinner spinner_type;
	private String[] titles = { "体育用品", "办公用品","生活用品", "自行车", "电子产品", "图书", "电脑配件" };
	private View view, dialogView;
	private TextView title,lable, gallery, camera;// Spinner的选项，对话框的相册和相机
	private ImageView addImageView;
	private Dialog dialog;
	private LinearLayout addPicLayout;
	private Bitmap bitmap;
	private ArrayList<Bitmap> bitmap_list = new ArrayList<Bitmap>();
	private ArrayList<String> photo_list = new ArrayList<String>();// 图片的路径集合
	private String picturePath = null;// 图片的存放路径
	private Shop shop;// 发布信息实体
	private EditText shopname, price, userPhone, description;
	private Button release;
	private String type = "fffff";
	private MyApplication myApplication;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_release);
		TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this
				.getApplicationContext();
		myApplication.addActivity(this);
		title = (TextView) this.findViewById(R.id.title_tv);
		title.setText("发布商品");
		shopname = (EditText) this.findViewById(R.id.releaseTitle);
		price = (EditText) this.findViewById(R.id.releasePrice);
		userPhone = (EditText) this.findViewById(R.id.link);
		description = (EditText) this.findViewById(R.id.describe);
		addPicLayout = (LinearLayout) this.findViewById(R.id.addPicLayout);
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				View view = getLayoutInflater().inflate(R.layout.my_dialog,
						null, false);
				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText("温馨提示");
				TextView message = (TextView) view
						.findViewById(R.id.message);
				message.setVisibility(View.VISIBLE);
				message.setText("你确定要取消发布吗？你填写的内容将丢失");
				TextView gallery = (TextView) view
						.findViewById(R.id.gallery);
				gallery.setVisibility(View.GONE);
				TextView camera = (TextView) view.findViewById(R.id.camera);
				camera.setVisibility(View.GONE);
				final Dialog dialog = new Dialog(ReleaseActivity.this,
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
						Intent intent =  new Intent(ReleaseActivity.this,MainActivity.class);
						ReleaseActivity.this.startActivity(intent);
						ReleaseActivity.this.finish();
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
		addImageView = (ImageView) this.findViewById(R.id.addImageView);
		addImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogView = getLayoutInflater().inflate(R.layout.my_dialog,
						null, false);
				dialog = new Dialog(ReleaseActivity.this, R.style.myDialogTheme);
				dialog.setContentView(dialogView);
				dialog.show();
				// 从相册选择图片
				gallery = (TextView) dialogView.findViewById(R.id.gallery);
				gallery.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (photo_list.size() == 3) {
							Toast.makeText(ReleaseActivity.this,
									"对不起，内存限制不能再添加图片了～～", Toast.LENGTH_LONG)
									.show();
						} else {
							Intent intent = new Intent(
									Intent.ACTION_GET_CONTENT);
							intent.setType("image/*");
							intent.putExtra("return-data", true);
							startActivityForResult(intent, 2);
						}
						dialog.dismiss();
					}
				});
				// 拍照
				camera = (TextView) dialogView.findViewById(R.id.camera);
				camera.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (photo_list.size() == 3) {
							Toast.makeText(ReleaseActivity.this,
									"对不起，内存限制只能添加少于等于三张图片", Toast.LENGTH_LONG)
									.show();
						} else {
							Intent intent = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							
							startActivityForResult(intent, 1);
						}
						dialog.dismiss();

					}
				});
			}
		});
		// 发布信息
		release = (Button) this.findViewById(R.id.release);
		release.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checked()) {
					View view = getLayoutInflater().inflate(
							R.layout.progress_dialog, null, false);
					dialog = new Dialog(ReleaseActivity.this,
							R.style.myDialogTheme);
					dialog.setContentView(view);
					dialog.show();
					Users users = (Users) myApplication.userMap.get("user");
					String picture = "";
					for (int i = 0; i < photo_list.size(); i++) {
						picture += photo_list.get(i).substring(
								photo_list.get(i).lastIndexOf("/") + 1)
								+ ";";
					}
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("shopname", shopname.getText().toString());
					params.put("price", price.getText().toString());
					params.put("userName", users.getUserName());
					params.put("userPhone", userPhone.getText().toString());
					params.put("description", description.getText().toString());
					params.put("category", type);
					params.put("picture", picture);
					Log.v("dddddddddddddddddddddd", params.toString());
					HashMap<String, File> fileMap = new HashMap<String, File>();
					for (int i = 0; i < photo_list.size(); i++) {
						fileMap.put("fileMap" + i, new File(photo_list.get(i)));
					}
					Log.v("dddddddddddddddddddddd", fileMap.toString());
					HttpHelper.asyncMultipartPost(Constants.URL
							+ "/second-hand/shop_add.do", params, fileMap,
							new Callback() {
								@Override
								public void dataLoaded(Message msg) {
									if (HttpStatus.SC_OK != msg.what) {
										dialog.dismiss();
										AppException.http(msg.what).makeToast(
												ReleaseActivity.this);
										return;
									}
									dialog.dismiss();
									String message = (String) msg.obj;
									Toast.makeText(ReleaseActivity.this,
											message, Toast.LENGTH_LONG).show();
									ReleaseActivity.this.finish();
								}
							});
				}
			}
		});
	}

	// 图片的处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		bitmap = null;
		if (requestCode == 1 && resultCode == RESULT_OK) {

			String sdStatus = Environment.getExternalStorageState();
			if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
				Log.v("TestFile",
						"SD card is not avaiable/writeable right now.");
				return;
			}
			Bundle bundle = data.getExtras();
			bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
			FileOutputStream b = null;
			File file = new File("/sdcard/campus_life/myImage/");
			file.mkdirs();// 创建文件夹
			picturePath = "/sdcard/campus_life/myImage/"
					+ System.currentTimeMillis() + ".jpg";
			try {
				b = new FileOutputStream(picturePath);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					b.flush();
					b.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == 2 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			try {
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				picturePath = cursor.getString(columnIndex);
				cursor.close();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				bitmap = BitmapFactory.decodeFile(picturePath,options);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (bitmap != null && picturePath != null) {
			Log.v("dddddddddddddddddddddd", picturePath);
			photo_list.add(picturePath);
			bitmap_list.add(bitmap);
			showPhoto(bitmap);
		}
	}

	// 图片的显示
	public void showPhoto(final Bitmap bitmap) {
		final View item = LayoutInflater.from(this).inflate(
				R.layout.photo_item, null);
		ImageView photo = (ImageView) item.findViewById(R.id.photo);
		photo.setImageBitmap(bitmap);
		ImageView delete = (ImageView) item.findViewById(R.id.photo_delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				photo_list.remove(picturePath);
				addPicLayout.removeView(item);
				destoryBitmap(bitmap);
			}
		});
		addPicLayout.addView(item);
	}

	// 发布信息验证
	private boolean checked() {
		if (shopname.getText().toString() == null
				|| shopname.getText().toString().equals("")) {
			Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
			return false;
		} else if (price.getText().toString() == null
				|| price.getText().toString().equals("")) {
			Toast.makeText(this, "价格不能为空", Toast.LENGTH_SHORT).show();
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
	// 销毁bitmap 释放内存
	public void destoryBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			View view = getLayoutInflater().inflate(R.layout.my_dialog,
					null, false);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText("温馨提示");
			TextView message = (TextView) view
					.findViewById(R.id.message);
			message.setVisibility(View.VISIBLE);
			message.setText("你确定要取消发布吗？你填写的内容将丢失");
			TextView gallery = (TextView) view
					.findViewById(R.id.gallery);
			gallery.setVisibility(View.GONE);
			TextView camera = (TextView) view.findViewById(R.id.camera);
			camera.setVisibility(View.GONE);
			final Dialog dialog = new Dialog(ReleaseActivity.this,
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
					ReleaseActivity.this.finish();
				}
			});
		}
		return true;
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(bitmap_list != null){
			for(int i = 0;i < bitmap_list.size();i++){
				destoryBitmap(bitmap_list.get(i));
			}
		}
	}
}
