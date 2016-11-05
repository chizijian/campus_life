/**
 */
package com.dcc.app.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;

import com.dcc.app.common.DateAndTimeHepler;
import com.yhx.app.campus_life.R;

/**
 * "下拉刷新"功能的视图类:带手动刷新，解决下拉时“卡顿”问题
 */
public class PullDownView extends FrameLayout implements
		GestureDetector.OnGestureListener, Animation.AnimationListener {
	public static int MAX_LENGHT = 0;
	public static final int STATE_CLOSE = 1;
	public static final int STATE_OPEN = 2;
	public static final int STATE_OPEN_RELEASE = 3;
	public static final int STATE_OPEN_MAX = 4;
	public static final int STATE_OPEN_MAX_RELEASE = 5;
	public static final int STATE_UPDATE = 6;
	public static final int STATE_UPDATE_SCROLL = 7;
	
	private View child;
	private Animation mAnimationDown;
	private Animation mAnimationUp;
	private ImageView mArrow;
	private String mDate;
	private GestureDetector mDetector;
	private Drawable mDownArrow;
	private boolean mEnable = true;
	private Flinger mFlinger = new Flinger();
	private boolean mIsAutoScroller;
	private int mPading;
	private ProgressBar mProgressBar;
	private int mState = STATE_CLOSE;
	private TextView mTitle;
	private Drawable mUpArrow;
	private FrameLayout mUpdateContent;
	private UpdateHandler mUpdateHandler;
	private long prev_times;

	public PullDownView(Context paramContext) {
		super(paramContext);
		mDetector = new GestureDetector(paramContext, this);
		init();
		addUpdateBar();
	}

	public PullDownView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mDetector = new GestureDetector(paramContext, this);
		init();
		addUpdateBar();
	}

	private void addUpdateBar() {
		this.mAnimationUp = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_up);
		this.mAnimationUp.setAnimationListener(this);
		this.mAnimationDown = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_down);
		this.mAnimationDown.setAnimationListener(this);
		
		this.child = LayoutInflater.from(getContext()).inflate(R.layout.pulldownview_header, null);
		this.child.setVisibility(View.INVISIBLE);
		addView(this.child);
		
		this.mArrow = new ImageView(getContext());
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-1, -1);
		this.mArrow.setScaleType(ImageView.ScaleType.FIT_CENTER);
		this.mArrow.setLayoutParams(localLayoutParams);
		this.mArrow.setImageResource(R.drawable.arrow_down);
		
		this.mUpdateContent = ((FrameLayout) getChildAt(0).findViewById(R.id.update_bar_content));
		this.mUpdateContent.addView(this.mArrow);
		localLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		localLayoutParams.gravity = Gravity.CENTER;
		this.mProgressBar = new ProgressBar(getContext(), null,android.R.attr.progressBarStyleSmallInverse);
		
		int i = getResources().getDimensionPixelSize(R.dimen.updatebar_padding);
		this.mProgressBar.setPadding(i, i, i, i);
		this.mProgressBar.setLayoutParams(localLayoutParams);
		this.mUpdateContent.addView(this.mProgressBar);
		this.mTitle = ((TextView) findViewById(R.id.update_bar_title));
	}

	@SuppressWarnings("deprecation")
	private void init() {
		MAX_LENGHT = getResources().getDimensionPixelSize(R.dimen.updatebar_height);
		setDrawingCacheEnabled(false);
		setBackgroundDrawable(null);
		setClipChildren(false);
		this.mDetector.setIsLongpressEnabled(true);
		this.mUpArrow = getContext().getResources().getDrawable(R.drawable.arrow_up);
		this.mDownArrow = getContext().getResources().getDrawable(R.drawable.arrow_down);
	}

	private boolean move(float paramFloat, boolean paramBoolean) {
		boolean flag = false;
		if (this.mState == STATE_UPDATE) {
			if (paramFloat < 0.0F){
				flag = true;
			}
			if (paramBoolean){
				this.mState = STATE_UPDATE_SCROLL;
			}
		}
		
		if ((this.mState != STATE_UPDATE_SCROLL) || (paramFloat >= 0.0F)
				|| (-this.mPading < MAX_LENGHT)) {
			this.mPading = (int) (paramFloat + this.mPading);
			if (this.mPading > 0){
				this.mPading = 0;
			}
			if (paramBoolean) {
				switch (this.mState) {
				case STATE_CLOSE:
					if (this.mPading >= 0){
						break;
					}
					this.mState = STATE_OPEN;
					this.mProgressBar.setVisibility(View.INVISIBLE);
					this.mArrow.setVisibility(View.VISIBLE);
					break;
				case STATE_OPEN:
					if (Math.abs(this.mPading) < MAX_LENGHT) {
						if (this.mPading != 0){
							break;
						}
						this.mState = STATE_CLOSE;
					} else {
						this.mState =STATE_OPEN_MAX;
						this.mProgressBar.setVisibility(View.INVISIBLE);
						this.mArrow.setVisibility(View.VISIBLE);
						this.mArrow.startAnimation(this.mAnimationUp);
					}
					break;
				case STATE_OPEN_RELEASE:
				case STATE_OPEN_MAX_RELEASE:
					if (!paramBoolean) {
						if (this.mPading == 0){
							this.mState = STATE_CLOSE;
						}
					} else if (Math.abs(this.mPading) < MAX_LENGHT) {
						if (Math.abs(this.mPading) >= MAX_LENGHT) {
							if (this.mPading == 0){
								this.mState = STATE_CLOSE;
							}
						} else {
							this.mState = STATE_OPEN;
							this.mProgressBar.setVisibility(View.INVISIBLE);
							this.mArrow.setVisibility(View.VISIBLE);
							this.mArrow.startAnimation(this.mAnimationDown);
						}
					} else {
						this.mState = STATE_OPEN_MAX;
						this.mProgressBar.setVisibility(View.INVISIBLE);
						this.mArrow.setVisibility(View.VISIBLE);
						this.mArrow.startAnimation(this.mAnimationUp);
					}
					invalidate();
					flag = true;
					break;
				case STATE_OPEN_MAX:
					if (Math.abs(this.mPading) >= MAX_LENGHT){
						break;
					}
					this.mState = STATE_OPEN;
					this.mProgressBar.setVisibility(View.INVISIBLE);
					this.mArrow.setVisibility(View.VISIBLE);
					this.mArrow.startAnimation(this.mAnimationDown);
				default:
					flag = true;
					break;
				case STATE_UPDATE:
				}
				if (this.mPading == 0){
					this.mState = STATE_CLOSE;
				}
				invalidate();
				flag = true;
			} else {
				if (this.mState != STATE_OPEN_MAX_RELEASE) {
					if ((this.mState != STATE_UPDATE) || (this.mPading != 0)) {
						if ((this.mState != STATE_OPEN_RELEASE) || (this.mPading != 0)) {
							if ((this.mState == STATE_UPDATE_SCROLL) && (this.mPading == 0)){
								this.mState = STATE_CLOSE;
							}
						} else{
							this.mState = STATE_CLOSE;
						}
					} else{
						this.mState = STATE_CLOSE;
					}
				} else {
					this.mState = STATE_UPDATE;
					if (this.mUpdateHandler != null){
						this.mUpdateHandler.onUpdate();
					}
				}
				invalidate();
				flag = true;
			}
		} else {
			flag = true;
		}
		return flag;
	}

	private boolean release() {
		boolean flag = false;
		if (this.mPading < 0) {
			switch (this.mState) {
			case STATE_OPEN:
			case STATE_OPEN_RELEASE:
				if (Math.abs(this.mPading) < MAX_LENGHT){
					this.mState = STATE_OPEN_RELEASE;
				}
				scrollToClose();
				break;
			case STATE_OPEN_MAX:
			case STATE_OPEN_MAX_RELEASE:
				this.mState = STATE_OPEN_MAX_RELEASE;
				scrollToUpdate();
			}
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	private void scrollToClose() {
		this.mFlinger.startUsingDistance(-this.mPading, 300);
	}

	private void scrollToUpdate() {
		this.mFlinger.startUsingDistance(-this.mPading - MAX_LENGHT, 300);
	}

	private void updateView() {
		View localView1 = getChildAt(0);
		View localView2 = getChildAt(1);
		if (this.mDate == null){
			this.mDate = "";
		}
		int i = localView1.getTop();
		int j = localView2.getTop();
		switch (this.mState) {
		case STATE_CLOSE:
			if (localView1.getVisibility() != View.INVISIBLE){
				localView1.setVisibility(View.INVISIBLE);
			}
			localView2.offsetTopAndBottom(-localView2.getTop());
			break;
		case STATE_OPEN:
		case STATE_OPEN_RELEASE:
			
			localView2.offsetTopAndBottom(-this.mPading - j);
			if (localView1.getVisibility() != View.VISIBLE){
				localView1.setVisibility(View.VISIBLE);
			}
			
			localView1.offsetTopAndBottom(-MAX_LENGHT - this.mPading - i);
			if(this.prev_times > 0){
				this.mDate = DateAndTimeHepler.friendly_time(getContext(), this.prev_times) + getResources().getString(R.string.update_time);
			}
			this.mTitle.setText(getResources().getString(R.string.drop_dowm) + "\n" + this.mDate);
			break;
		case STATE_OPEN_MAX:
		case STATE_OPEN_MAX_RELEASE:
			localView2.offsetTopAndBottom(-this.mPading - j);
			if (localView1.getVisibility() != View.VISIBLE){
				localView1.setVisibility(View.VISIBLE);
			}
			localView1.offsetTopAndBottom(-MAX_LENGHT - this.mPading - i);
			this.mTitle.setText(getResources().getString(R.string.release_update) + "\n"
					+ this.mDate);
			break;
		case STATE_UPDATE:
		case STATE_UPDATE_SCROLL:
			localView2.offsetTopAndBottom(-this.mPading - j);
			if (this.mProgressBar.getVisibility() != View.VISIBLE){
				this.mProgressBar.setVisibility(View.VISIBLE);
			}
			if (this.mArrow.getVisibility() != View.INVISIBLE){
				this.mArrow.setVisibility(View.INVISIBLE);
			}
			
			if(this.prev_times > 0){
				this.mDate = DateAndTimeHepler.friendly_time(getContext(), this.prev_times) + getResources().getString(R.string.update_time);
			}
			this.mTitle.setText(getResources().getString(R.string.doing_update) + "\n" + this.mDate);
			localView1.offsetTopAndBottom(-MAX_LENGHT - this.mPading - i);
			if (localView1.getVisibility() != View.VISIBLE){
				localView1.setVisibility(View.VISIBLE);
			}
		}
		invalidate();
	}

	public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
		
		boolean bool1 = super.dispatchTouchEvent(paramMotionEvent);
		if (this.mEnable) {
			if (!this.mIsAutoScroller) {
				boolean bool2 = this.mDetector.onTouchEvent(paramMotionEvent);
				int i = paramMotionEvent.getAction();
				if (i != MotionEvent.ACTION_UP) {
					if (i == MotionEvent.ACTION_CANCEL){
						bool2 = release();
					}
				} else{
					bool2 = release();
				}
				
				if ((this.mState != STATE_UPDATE) && (this.mState != STATE_UPDATE_SCROLL)) {
					if (((!bool2) && (this.mState != STATE_OPEN) && (this.mState != STATE_OPEN_MAX)
							&& (this.mState != STATE_OPEN_MAX_RELEASE) && (this.mState != STATE_OPEN_RELEASE))
							|| (getChildAt(1).getTop() == 0)) {
						updateView();
						bool1 = super.dispatchTouchEvent(paramMotionEvent);
					} else {
						paramMotionEvent.setAction(MotionEvent.ACTION_CANCEL);
						super.dispatchTouchEvent(paramMotionEvent);
						updateView();
						bool1 = true;
					}
				} else {
					updateView();
					bool1 = super.dispatchTouchEvent(paramMotionEvent);
				}
			} else {
				bool1 = true;
			}
		} 
		return bool1;
		
	}

	protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
			int paramInt3, int paramInt4) {
		getChildAt(0).layout(0, -MAX_LENGHT - this.mPading, getMeasuredWidth(), -this.mPading);
		getChildAt(1).layout(0, -this.mPading, getMeasuredWidth(), getMeasuredHeight() - this.mPading);
	}

	///////////动画事件监听的回调方法/////////////////////////////////
	public void onAnimationStart(Animation paramAnimation) {}
	public void onAnimationEnd(Animation paramAnimation) {
		if ((this.mState != STATE_OPEN) && (this.mState != STATE_OPEN_RELEASE)){
			this.mArrow.setImageDrawable(this.mUpArrow);
		}else{
			this.mArrow.setImageDrawable(this.mDownArrow);
		}
	}
	public void onAnimationRepeat(Animation paramAnimation) {}
	

	//////手势识别事件监听的回调方法////////////////////
	@Override
	public boolean onDown(MotionEvent paramMotionEvent) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return false;
	}
	@Override
	public void onLongPress(MotionEvent paramMotionEvent) {
	}
	@Override
	public boolean onScroll(MotionEvent paramMotionEvent1,
			MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		float f = 0.5f * paramFloat2;
		AdapterView<?> localAdapterView = (AdapterView<?>) getChildAt(1);
		boolean bool = false;
		if ((localAdapterView != null) && (localAdapterView.getCount() != 0)
				&& (localAdapterView.getChildCount() != 0)) {
			//Log.d("PullDownView", "f-->" + f + "getFirstVisiblePosition-->" + localAdapterView.getFirstVisiblePosition() + ",localAdapterView.getChildAt(0).getTop()-->" + localAdapterView.getChildAt(0).getTop());
			
			boolean flag = false;
			if(localAdapterView.getFirstVisiblePosition() == 0 
					&& localAdapterView.getChildAt(0).getTop() == 0){
				flag = true;
			}
			
			if ((f < 0F && flag) || this.mPading < 0){
				bool = move(f, true);
			}else{
				bool = false;
			}
		} else {
			bool = false;
		}
		return bool;
	}

	@Override
	public void onShowPress(MotionEvent paramMotionEvent) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
		return false;
	}
	////////////////////////////////////////

	/**
	 * 设置刷新操作时的回调接口
	 * @param paramUpdateHandler
	 */
	public void setUpdateHandler(UpdateHandler paramUpdateHandler) {
		this.mUpdateHandler = paramUpdateHandler;
	}
	/**
	 * 手动显示“刷新条”
	 */
	public void startUpdate() {
		this.mPading = -(MAX_LENGHT);
		this.mState = STATE_UPDATE_SCROLL;
		postDelayed(new Runnable() {
			public void run() {
				PullDownView.this.updateView();
			}
		}, 10L);
	}
	/**
	 * 刷新操作结束时，用于隐藏刷新条
	 */
	public void endUpdate(){
		prev_times = System.currentTimeMillis();
		if (this.mPading == 0){
			this.mState = STATE_CLOSE;
		}else{
			scrollToClose();
		}
		this.mArrow.setImageResource(R.drawable.arrow_down);
	}


	/**
	 * “刷新”操作的回调接口
	 * @author qiujy
	 */
	public static interface UpdateHandler {
		/**
		 * 执行“刷新”操作时会回调的方法,一般用于异步调用耗时操作
		 */
		public void onUpdate();
	}

	
	private class Flinger implements Runnable {
		private int mLastFlingX;
		private Scroller mScroller = new Scroller(PullDownView.this.getContext());

		public Flinger() {
		}

		private void startCommon() {
			PullDownView.this.removeCallbacks(this);
		}

		public void run() {
			Scroller localScroller = this.mScroller;
			boolean bool = localScroller.computeScrollOffset();
			int i = localScroller.getCurrX();
			int j = this.mLastFlingX - i;
			PullDownView.this.move(j, false);
			PullDownView.this.updateView();
			if (!bool) {
				PullDownView.this.mIsAutoScroller = false;
				PullDownView.this.removeCallbacks(this);
			} else {
				this.mLastFlingX = i;
				PullDownView.this.post(this);
			}

		}

		public void startUsingDistance(int paramInt1, int paramInt2) {
			if (paramInt1 == 0){
				paramInt1--;
			}
			startCommon();
			this.mLastFlingX = 0;
			this.mScroller.startScroll(0, 0, -paramInt1, 0, paramInt2);
			PullDownView.this.mIsAutoScroller = true;
			PullDownView.this.post(this);
		}
	}
}