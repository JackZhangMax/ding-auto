package com.zml.dingauto.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/9/30 11:53
 */
@Service
public class NotifyService {

    @Value("${bark.url}")
    private String barkUrl;
    @Value("${sct.url}")
    private String sctUrl;


    public void send(String title) {
        send(title, null, null);
    }

    public void send(String title, String content) {
        send(title, content, null);
    }

    public void send(String title, String content, String url) {
        try {
            // bark通知
            if (!StrUtil.isEmpty(barkUrl)) {
                if (StrUtil.isEmpty(url)) {
                    HttpUtil.get(barkUrl + title + (StrUtil.isEmpty(content) ? "" : "/" + content));
                } else {
                    HttpUtil.get(barkUrl + title + "?url=" + url);
                }
            } else {
                // server酱通知
                URIBuilder urlBuilder = new URIBuilder(sctUrl);
                urlBuilder.addParameter("title", title);
                urlBuilder.addParameter("desp", content);
                HttpUtil.get(urlBuilder.build().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
