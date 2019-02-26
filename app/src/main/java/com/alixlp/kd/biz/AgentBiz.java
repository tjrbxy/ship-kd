package com.alixlp.kd.biz;

import com.alixlp.kd.bean.Agent;
import com.alixlp.kd.config.Config;
import com.alixlp.kd.net.CommonCallback;
import com.alixlp.kd.util.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;

public class AgentBiz {

    public void agentInfo(String id, CommonCallback<Agent> commonCallback) {

        String baseUrl = "http://" + SPUtils.getInstance().get(Config.APIURL, "") +
                "/api.php";
        String token = (String) SPUtils.getInstance().get(Config.TOKEN, "");
        OkHttpUtils
                .get()
                .url(baseUrl + "/user/agentInfo")
                .addParams("id", id + "")
                .addParams("token", token)
                .build()
                .execute(commonCallback);

    }
}
