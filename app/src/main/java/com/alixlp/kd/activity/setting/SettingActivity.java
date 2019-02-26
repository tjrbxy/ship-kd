package com.alixlp.kd.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alixlp.kd.R;
import com.alixlp.kd.activity.BaseActivity;
import com.alixlp.kd.activity.IndexMainActivity;
import com.alixlp.kd.biz.UserBiz;
import com.alixlp.kd.config.Config;
import com.alixlp.kd.util.SPUtils;
import com.alixlp.kd.util.T;
import com.suke.widget.SwitchButton;


public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity-app";
    protected Button mBtSetting;
    protected EditText mEtApiUrl;
    protected SwitchButton mLanguage;
    private Button mBtLogout;
    protected UserBiz mUserBiz;

    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        mBtSetting = (Button) findViewById(R.id.id_bt_save);
        mEtApiUrl = (EditText) findViewById(R.id.id_et_api_url); // api url;
        mLanguage = (SwitchButton) findViewById(R.id.id_language);
        mBtLogout = (Button) findViewById(R.id.id_bt_logout);
        // 设置默认
        Boolean language = (Boolean) SPUtils.getInstance().get(Config.LANGUAGE, true);
        mLanguage.setChecked(language);
        String apiUrl = (String) SPUtils.getInstance().get(Config.APIURL, "");
        mEtApiUrl.setText(apiUrl);
        // 事件区域
        mBtSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiUrl = mEtApiUrl.getText().toString();
                if (TextUtils.isEmpty(apiUrl)) {
                    T.showToast("填写域名信息");
                    return;
                }
                // 重新设置后，让用户重新登录
                SPUtils.getInstance().put(Config.USERID, 0);
                SPUtils.getInstance().put(Config.APIURL, apiUrl);
                // 跳轉登錄
                Intent intent = new Intent(SettingActivity.this, IndexMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mLanguage.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    T.showToast("开启粤语提示");
                } else {
                    T.showToast("开启普通话提示");
                }
                Log.d(TAG, "onCheckedChanged: " + isChecked);
                SPUtils.getInstance().put(Config.LANGUAGE, isChecked);
            }
        });
        // 退出
        mBtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showToast("退出成功！");
                SPUtils.getInstance().put(Config.USERID, 0);
                SPUtils.getInstance().put(Config.TOKEN, "");
                SPUtils.getInstance().put(Config.KUAIDIID, 0);
                startActivity(new Intent(SettingActivity.this, IndexMainActivity.class));
/*                mUserBiz.logout(new CommonCallback<List>() {
                    @Override
                    public void onError(Exception e) {
                        T.showToast(e.getMessage());
                    }

                    @Override
                    public void onSuccess(List response, String info) {
                        if (!info.equals("ok")) {
                            T.showToast(info);
                        }
                    }
                });*/
            }
        });
    }

    @Override
    protected void onRequestData() {

    }
}
