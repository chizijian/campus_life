/**
 * 
 */
package com.dcc.app.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dcc.app.campus_life.LeaveMessageActivity;
import com.dcc.app.entity.Message;
import com.yhx.app.campus_life.R;

/**
 */
public class MessageAdapter extends BaseAdapter{
	private List<Message> msgList;
	
	public List<Message> getMsgList() {
		return msgList;
	}
	public void setMsgList(List<Message> msgList) {
		this.msgList = msgList;
	}
	private Context context;
	private LayoutInflater inflater;
	public MessageAdapter(Context context){
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return msgList == null ? 0 : msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if(convertView == null){
			convertView  = inflater.inflate(R.layout.message_list_item, null);
			vh = new ViewHolder();
			vh.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
			vh.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			vh.tv_reply = (TextView) convertView.findViewById(R.id.tv_reply);
			vh.tv_message = (TextView) convertView.findViewById(R.id.tv_message);
			convertView.setTag(vh);
		}else {
			vh = (ViewHolder) convertView.getTag();
		}
		final Message item = msgList.get(position);
		vh.tv_username.setText(item.getUsername());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		vh.tv_time.setText(df.format(item.getLeave_time()));
		vh.tv_message.setText("  "+item.getContent());
		vh.tv_reply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,LeaveMessageActivity.class);
				intent.putExtra("username", item.getUsername());
                context.startActivity(intent);
			}
		});
		return convertView;
	}
	private class ViewHolder {
		TextView tv_username,tv_time,tv_reply,tv_message;
	}
}
