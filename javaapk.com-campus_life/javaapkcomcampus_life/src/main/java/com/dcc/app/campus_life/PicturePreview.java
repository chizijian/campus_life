/**
 * 
 */
package com.dcc.app.campus_life;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.dcc.app.common.TranslucentBar;
import com.dcc.app.service.MyApplication;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.yhx.app.campus_life.R;

import java.util.ArrayList;

/**
 */
public class PicturePreview extends Activity{
	private MyApplication myApplication;
	private ViewPager vp;
	private PageIndicator indicator;
	private ArrayList<View> pageViews;
    private View view1,view2,view3;// 图片的布局
	private ImageView image1,image2,image3;// 图片显示
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApplication = (MyApplication) this
				.getApplicationContext();
		myApplication.addActivity(this);
		setContentView(R.layout.activity_guide);
		TranslucentBar.setTranslucent(this);

		initData();
		vp = (ViewPager)this.findViewById(R.id.vp);
		vp.setPageMargin(2); //页间距
		vp.setAdapter(new GuidePageAdapter());

		indicator = (LinePageIndicator) this.findViewById(R.id.indicator);
		indicator.setViewPager(vp);
	}
	private void initData(){
		// 加载引导页
		LayoutInflater inflater = LayoutInflater.from(PicturePreview.this);
		pageViews = new ArrayList<View>();
		pageViews = new ArrayList<View>();
		view1 = inflater.inflate(R.layout.guide_item_01, null);
		image1 = (ImageView) view1.findViewById(R.id.image_pieview1);
		view2 = inflater.inflate(R.layout.guide_item_02, null);
		image2 = (ImageView) view2.findViewById(R.id.image_pieview2);
		view3 = inflater.inflate(R.layout.guide_item_03, null);
		image3 = (ImageView) view3.findViewById(R.id.image_pieview3);
		if (myApplication.getListBitmap()!=null) {
		for(int i = 0;i<myApplication.getListBitmap().size();i++){
			if(i == 0){
				pageViews.add(view1);
				image1.setImageBitmap(myApplication.getListBitmap().get(i));
			}else if(i == 1){
				pageViews.add(view2);
				image2.setImageBitmap(myApplication.getListBitmap().get(i));
			}else {
				pageViews.add(view3);
				image3.setImageBitmap(myApplication.getListBitmap().get(i));
			}
		}
		} else {
		}
	}

	// 引导页面数据适配器
	class GuidePageAdapter extends PagerAdapter {
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
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}
	}

}
