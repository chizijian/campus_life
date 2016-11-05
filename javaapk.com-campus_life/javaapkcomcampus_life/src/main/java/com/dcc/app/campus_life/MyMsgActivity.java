/**
 * 
 */
package com.dcc.app.campus_life;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dcc.app.adapter.MessageAdapter;
import com.dcc.app.common.AppException;
import com.dcc.app.common.Constants;
import com.dcc.app.common.HttpHelper;
import com.dcc.app.common.HttpHelper.Callback;
import com.dcc.app.common.PageModel;
import com.dcc.app.common.TranslucentBar;
import com.dcc.app.entity.Users;
import com.dcc.app.service.MyApplication;
import com.dcc.app.view.PullDownView;
import com.dcc.app.view.PullDownView.UpdateHandler;
import com.yhx.app.campus_life.R;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 */
public class MyMsgActivity extends Activity implements UpdateHandler{
	private PullDownView pdv;
	private ListView lv;
	private LinearLayout footer;// listview的底部
	private ProgressBar listview_foot_progress;// listview的底部进度条
	private TextView listview_foot_more,title;
	private LinearLayout delete;
	private  MessageAdapter adapter;
	private List<com.dcc.app.entity.Message> listDatas = new ArrayList<com.dcc.app.entity.Message>();
	private Button btn_back, btn_right;
	private int pageNo = 1;
	private String string;
	private boolean hasMore = false;
	private int lastItem;
	private MyApplication myApplication;
	private String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mymsg);
		TranslucentBar.setTranslucent(this);

		myApplication = (MyApplication) this
				.getApplicationContext();
		myApplication.addActivity(this);
		Users users = (Users) myApplication.userMap.get("user");
		if (users != null) {
			username = users.getUserName();
		}
		initPullDownView();
		loadData(username,pageNo);
		title = (TextView) this.findViewById(R.id.title_tv);
		title.setText("我的留言");
		// 返回按钮监听
		btn_back = (Button) this.findViewById(R.id.button_back);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent =  new Intent(MyMsgActivity.this,MainActivity.class);
				MyMsgActivity.this.startActivity(intent);
				MyMsgActivity.this.finish();
			}
		});
		btn_right = (Button) this.findViewById(R.id.button_right);
		btn_right.setText("刷新");
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pdv.startUpdate();
				onUpdate();
			}
		});
		delete = (LinearLayout) this.findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = getLayoutInflater().inflate(R.layout.my_dialog,
						null, false);
				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText("温馨提示");
				TextView message = (TextView) view
						.findViewById(R.id.message);
				message.setVisibility(View.VISIBLE);
				message.setText("确定：删除全部选项");
				TextView gallery = (TextView) view
						.findViewById(R.id.gallery);
				gallery.setVisibility(View.GONE);
				TextView camera = (TextView) view.findViewById(R.id.camera);
				camera.setVisibility(View.GONE);
				final Dialog dialog = new Dialog(MyMsgActivity.this,
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
						adapter.setMsgList(null);
						adapter.notifyDataSetChanged();
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("username", username);
						HttpHelper.asyncPost(Constants.URL + "/second-hand/msg_delete.do", params, new Callback() {
							
							@Override
							public void dataLoaded(Message msg) {
								if(HttpStatus.SC_OK != msg.what){
									AppException.http(msg.what).makeToast(
											MyMsgActivity.this);
									return;
								}else {
									Toast.makeText(MyMsgActivity.this,
											msg.obj.toString(),
											Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});

			}
		});
	}

	// 初始化下拉刷新
	private void initPullDownView() {
		pdv = (PullDownView) this.findViewById(R.id.pdv);
		pdv.setUpdateHandler(this);// 设置下拉刷新处理器
		lv = (ListView) pdv.findViewById(R.id.lv);
		// 初始化底部视图
		this.footer = ((LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.listview_footer, null));
		listview_foot_progress = (ProgressBar) footer
				.findViewById(R.id.listview_foot_progress);
		listview_foot_more = (TextView) footer
				.findViewById(R.id.listview_foot_more);

		lv.addFooterView(footer);// 添加底部视图 必须在setAdapter前
		lv.setFooterDividersEnabled(false);

		// /////////////////////////////
		adapter = new MessageAdapter(this);
		lv.setAdapter(adapter);

		lv.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 数据为空
				if (listDatas.isEmpty()) {
					return;
				}

				// 判断是否滚动到底部
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
						&& lastItem == adapter.getCount()) {
					if (hasMore) {
						listview_foot_progress.setVisibility(View.VISIBLE);
						listview_foot_more.setText("加载中...");
						loadData(username,++pageNo);
					}
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount - 1;
			}
		});
	}

	// 回调方法
	public void onUpdate() {
		pageNo = 1;
		lv.setSelection(0);
		loadData(username,pageNo);
	}

	// 加载数据
	public void loadData(String string,int pn) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("pageNo", pn);
		params.put("receivename", string);
		if (pageNo == 1) {
			listDatas.clear();
			footer.setVisibility(View.GONE);
			lv.setFooterDividersEnabled(false);
		} else {
			footer.setVisibility(View.VISIBLE);
			lv.setFooterDividersEnabled(true);
		}
		HttpHelper.asyncPost(Constants.URL + "/second-hand/msg_list.do",
				params, new Callback() {
					@Override
					public void dataLoaded(Message msg) {
						footer.setVisibility(View.GONE);
						if (HttpStatus.SC_OK != msg.what) {
							AppException.http(msg.what).makeToast(
									MyMsgActivity.this);
							pdv.endUpdate();
							return;
						}
						String json = (String) msg.obj;
						Log.v("dddddddddddddddddddddd", json);
						PageModel<com.dcc.app.entity.Message> pm = PageModel.jsonConvertMsg(json);
						listDatas.addAll(pm.getData());
						adapter.setMsgList(listDatas);
						adapter.notifyDataSetChanged();
						pdv.endUpdate();

						if (pageNo < pm.getPageCount()) {
							hasMore = true;
						} else {
							hasMore = false;
							footer.setVisibility(View.VISIBLE);
							listview_foot_progress
									.setVisibility(View.INVISIBLE);
							listview_foot_more.setText("已加载全部");
						}
					}
				});
	}
}
