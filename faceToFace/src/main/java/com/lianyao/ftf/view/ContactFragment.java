package com.lianyao.ftf.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.adapter.ContactAdapter;
import com.lianyao.ftf.base.BaseFragment;
import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.entry.Contact;
import com.lianyao.ftf.util.SPUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

public class ContactFragment extends BaseFragment implements OnClickListener {

	private EditText edit_search;
	private ListView listview_contact;
	private View view;
	private List<Contact> list = new ArrayList<Contact>();
	private List<Contact> list2 = new ArrayList<Contact>();
	private ContactAdapter adapter;
	private ImageView img_add;
	private DbUtils db;
	
	@Override
	protected int layoutId() {
		return R.layout.fragment_contact_1;
	}

	@Override
	protected void setViews(View rootView) {
		db = DbUtils.create(getActivity(), Constants.CONTACT_DB, 1, null);
		view = rootView;
		edit_search = (EditText) rootView.findViewById(R.id.edit_search);
		img_add = (ImageView) rootView.findViewById(R.id.img_add);
		listview_contact = (ListView) rootView.findViewById(R.id.listview_contact);
		
		listview_contact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Contact contact = list2.get(position);
				Intent intent = new Intent(view.getContext(), ContactDetailActivity.class);
				intent.putExtra("id", contact.getId() + "");
				intent.putExtra("nickname", contact.getNickname());
				intent.putExtra("mobile", contact.getMobile());
				startActivity(intent);
			}
		});
		try {
			list = db.findAll(Contact.class);
			list2 = db.findAll(Contact.class);
			if(list2 == null) {
				list2 = new ArrayList<>();
			}
			if(list == null) {
				list = new ArrayList<>();
			}
			adapter = new ContactAdapter(view.getContext(), list2);
			listview_contact.setAdapter(adapter);
		}catch (DbException ex) {

		}
		/*JSONObject params = new JSONObject();
		params.put("id", SPUtil.get(rootView.getContext(), "id", "-1"));
		getRequest().setRestPost(this, "contact.json", params);
		*/
		
		edit_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					edit_search.setHint("");
				} else {
					edit_search.setHint(R.string.search);
				}
			}
		});
		edit_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String searchText = edit_search.getText().toString();
				list2.clear();
				if(searchText.length() == 0) {
					for(int i = 0; i < list.size(); i++) {
						list2.add(list.get(i));
					}
					adapter.updateAdapter(list2);
				} else {
					for(int i = 0; i < list.size(); i++) {
						if(list.get(i).getMobile().indexOf(searchText) > -1 || list.get(i).getNickname().indexOf(searchText) > -1) {
							list2.add(list.get(i));
						}
					}
					adapter.updateAdapter(list2);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		img_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), ContactAddActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case 0:
				break;
			case 1:
				//添加后，刷新
				try {
					list = db.findAll(Contact.class);
					list2 = db.findAll(Contact.class);
					if (list2 == null) {
						list2 = new ArrayList<>();
					}
					if (list == null) {
						list = new ArrayList<>();
					}
					adapter = new ContactAdapter(view.getContext(), list2);
					listview_contact.setAdapter(adapter);
				}catch (DbException ex) {

				}
				break;
		}
	}

	@Override
	public void onSuccess(JSONObject json) {
		list.clear();
		list2.clear();
		JSONArray jsonUser = json.getJSONArray("obj");
		for(int i = 0, count = jsonUser.size(); i < count; i++) {
			Contact contact = new Contact();
			contact.setId(jsonUser.getJSONObject(i).getLong("id"));
			contact.setMobile(jsonUser.getJSONObject(i).getString("name"));
			contact.setNickname(jsonUser.getJSONObject(i).getString("nickname"));
			contact.setTid(jsonUser.getJSONObject(i).getString("tid"));
			contact.setUid(jsonUser.getJSONObject(i).getString("uid"));
			list.add(contact);
			list2.add(contact);
		}
		adapter.updateAdapter(list2);
	}


}
