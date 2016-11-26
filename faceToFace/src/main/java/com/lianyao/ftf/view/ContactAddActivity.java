package com.lianyao.ftf.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lianyao.ftf.R;
import com.lianyao.ftf.base.BaseActivity;
import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.entry.Contact;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.CommonUtil;
import com.lianyao.ftf.util.ToastUtil;
import com.lianyao.ftf.util.ValidateUtil;
import com.lianyao.ftf.util.http.RestInterface;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ContactAddActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_return;
    private TextView tv_finish;
    private EditText edt_mobile;
    private EditText edt_username;
    private DbUtils db;

    @Override
    protected int layoutId() {
        return R.layout.activity_contact_add;
    }

    @Override
    protected void setViews() {
        db = DbUtils.create(this, Constants.CONTACT_DB, 1, null);
        tv_return = (TextView) findViewById(R.id.img_return);
        tv_finish = (TextView) findViewById(R.id.tv_finish);
        edt_username = (EditText) findViewById(R.id.edt_username);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile);

        tv_return.setOnClickListener(this);
        tv_finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_return:
                this.setResult(0, getIntent());
                finish();
                break;
            case R.id.tv_finish:
                String username = edt_username.getText().toString();
                String mobile = edt_mobile.getText().toString();
                if (CommonUtil.isEmpty(username)) {
                    ToastUtil.showShort(this, "请输入联系人姓名");
                    return;
                } else if (!ValidateUtil.isMobileNO(mobile) && !ValidateUtil.isBoxNO(mobile)) {
                    ToastUtil.showShort(this, "手机号或盒子号码格式不正确");
                    return;
                }

                JSONObject param = new JSONObject();
                param.put("mobile", mobile);

                getRequest().setRestPost(this, "getUserInfoByMobile.json", param);
                break;
            default:
                break;
        }
    }

    public void onSuccess(JSONObject json) {
        String result = json.getString("result");

        if (result != null && result.equals("1")) {
            try {
                JSONObject userJson = json.getJSONObject("obj");
                Contact contact = db.findFirst(Selector.from(Contact.class).where(
                        "mobile", "=", userJson.getString("name")));
                if (contact != null) {
                    contact.setCreateDate(new SimpleDateFormat("yyyyMMddHHmmss")
                            .format(new Date()));
                    db.deleteById(Contact.class, contact.getUserId());
                }
                contact = new Contact();
                contact.setCreateDate(new SimpleDateFormat("yyyyMMddHHmmss")
                        .format(new Date()));
                contact.setUserId(userJson.getLong("id"));
                contact.setMobile(userJson.getString("name"));
//                contact.setNickname(userJson.getString("nickname"));
                contact.setNickname(edt_username.getText().toString());
                contact.setTid(userJson.getString("tid"));
                contact.setUid(userJson.getString("uid"));
                db.save(contact);
            }catch (DbException ex) {
                ToastUtil.showShort(ContactAddActivity.this, "联系人添加失败");
            }
            this.setResult(1, getIntent());
            finish();
        } else {
            ToastUtil.showShort(ContactAddActivity.this, "该号码未注册");
        }
    }

    public void onFailure(HttpException error, String msg) {

    }

}
