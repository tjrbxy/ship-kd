package com.alixlp.kd.biz;

import com.alixlp.kd.bean.User;
import com.alixlp.kd.config.Config;
import com.alixlp.kd.net.CommonCallback;
import com.alixlp.kd.util.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

public class UserBiz {
    private static final String TAG = "UserBiz-app";

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param commonCallback
     */
    public void login(String username, String password, CommonCallback<User> commonCallback) {
        String baseUrl = "http://" + SPUtils.getInstance().get(Config.APIURL, "") +
                "/api.php";
        OkHttpUtils
                .post()
                .url(baseUrl + "/login")
                .tag(this)
                .addParams("username", username)
                .addParams("password", password)
                .build()
                .execute(commonCallback);
    }

    /**
     * 退出登錄
     *
     * @param commonCallback
     */
    public void logout(CommonCallback<List> commonCallback) {
        String baseUrl = "http://" + SPUtils.getInstance().get(Config.APIURL, "") +
                "/api.php";
        String token = (String) SPUtils.getInstance().get(Config.TOKEN, "");

        OkHttpUtils
                .get()
                .url(baseUrl + "/login/logout")
                .tag(this)
                .addParams("token", token)
                .build()
                .execute(commonCallback);
    }
}
