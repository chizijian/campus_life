package com.dcc.app.campus_life;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;
import com.yhx.app.campus_life.R;
/**
 * 使用ViewPagerIndicator组件实现的引导页
 */
public class GuideFragment extends Fragment {
	ViewPager vp;
    PageIndicator indicator;
    ArrayList<View> pageViews;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	View  view = inflater.inflate(R.layout.activity_guide, container,false);
		initData();
		
		vp = (ViewPager)view.findViewById(R.id.vp);
		vp.setPageMargin(2); //页间距
		vp.setAdapter(new GuidePageAdapter());

		indicator = (LinePageIndicator)view.findViewById(R.id.indicator);
		indicator.setViewPager(vp);
    	return view;
    }
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		this.setContentView(R.layout.activity_guide);
//		
//		initData();
//
//		vp = (ViewPager)findViewById(R.id.vp);
//		vp.setPageMargin(2); //页间距
//		vp.setAdapter(new GuidePageAdapter());
//
//		indicator = (LinePageIndicator)findViewById(R.id.indicator);
//		indicator.setViewPager(vp);
//	}
	
	private void initData(){
		// 加载引导页
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		pageViews = new ArrayList<View>();
		pageViews.add(inflater.inflate(R.layout.guide_item_01, null));
		pageViews.add(inflater.inflate(R.layout.guide_item_02, null));
		pageViews.add(inflater.inflate(R.layout.guide_item_03, null));
		View end = inflater.inflate(R.layout.guide_item_04, null);
		pageViews.add(end);
//
//		TextView txt_end = (TextView) end.findViewById(R.id.text_end);
//		txt_end.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(GuideActivity.this,MainActivity.class);
//				GuideActivity.this.startActivity(intent);
//				 GuideActivity.this.finish();
//			}
//		});
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
