package com.lianyao.ftf.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianyao.ftf.R;
import com.lianyao.ftf.entry.Contact;

public class ContactAdapter extends BaseAdapter {

	private List<Contact> list;
	private Context mContext;
	
	public ContactAdapter(Context mContext, List<Contact> list) {
		this.list = list;
		this.mContext = mContext;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	public void updateAdapter(List<Contact> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
        if (list.size() > position) {
            final Contact contact = list.get(position);
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.fragment_contact_1_item, arg2, false);
                viewHolder.img_photo = (ImageView) view.findViewById(R.id.img_photo);
                viewHolder.tv_nickname = (TextView) view.findViewById(R.id.tv_nickname);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_nickname.setText(contact.getNickname() + "(" + contact.getMobile() + ")");
            switch(position%3) {
            case 0:
            	viewHolder.img_photo.setBackgroundResource(R.drawable.photo_1);
            	break;
            case 1:
            	viewHolder.img_photo.setBackgroundResource(R.drawable.photo_2);
            	break;
            case 2:
            	viewHolder.img_photo.setBackgroundResource(R.drawable.photo_3);
            	break;
            }
        }
        return view;
	}

	final static class ViewHolder {
        TextView tv_nickname;
        ImageView img_photo;
    }
	
}
