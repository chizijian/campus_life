package com.dcc.app.campus_life;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.dcc.app.common.TranslucentBar;
import com.dcc.app.service.MyApplication;
import com.yhx.app.campus_life.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {
	private int[] images = { R.drawable.gv_1, R.drawable.gv_2, R.drawable.gv_3,
			R.drawable.gv_4, R.drawable.gv_5, R.drawable.gv_6, R.drawable.gv_7,
			R.drawable.gv_8, R.drawable.gv_9 };// gridview的图片
	private String[] titles = { "体育用品","生活用品", "自行车", "电子产品", "图书",
			"办公用品","电脑配件",  "个人中心", "联系我们" };// gridview的标题
	private GridView gridView;
	private Button title_back, title_right;
	private MyApplication myApplication;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TranslucentBar.setTranslucent(this);
		myApplication = (MyApplication) this.getApplicationContext();
		myApplication.addActivity(this);
		title_back = (Button) this.findViewById(R.id.button_back);
		title_back.setVisibility(View.GONE);
		title_right = (Button) this.findViewById(R.id.button_right);
		title_right.setVisibility(View.GONE);
		gridView = (GridView) this.findViewById(R.id.sub_gv);
		gridView.setAdapter(getMenuAdapter(titles, images));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "体育用品");
					MainActivity.this.startActivity(intent);
					break;
				case 1:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "生活用品");
					MainActivity.this.startActivity(intent);
					break;

				case 2:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "自行车");
					MainActivity.this.startActivity(intent);
					break;

				case 3:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "电子产品");
					MainActivity.this.startActivity(intent);
					break;

				case 4:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "图书");
					MainActivity.this.startActivity(intent);
					break;

				case 5:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "办公用品");
					MainActivity.this.startActivity(intent);
					break;

				case 6:
					intent = new Intent(MainActivity.this,
							ShopListActivity.class);
					intent.putExtra("type", "电脑配件");
					MainActivity.this.startActivity(intent);
					break;

				case 7:
					intent = new Intent(MainActivity.this,
							PersonnalActivity.class);
					startActivity(intent);
					break;
				case 8:

					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * @param titles
	 *            gridview的文字数组
	 * @param images
	 *            gridview的图片数组
	 * @return
	 */
	private SimpleAdapter getMenuAdapter(String[] titles, int[] images) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < titles.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", images[i]);
			map.put("itemText", titles[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.girdview_item,
				new String[] { "itemImage", "itemText" }, new int[] {
						R.id.mygv_iv, R.id.mygv_tv });
		return simperAdapter;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
