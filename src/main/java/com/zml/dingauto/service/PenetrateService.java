package com.zml.dingauto.service;

import cn.hutool.http.HttpUtil;
import com.zml.dingauto.util.CommandUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/9/18 9:45
 */
@Service
public class PenetrateService {

    @Value("${penetrate.url}")
    private String penetrateUrl;
    @Value("${penetrate.path}")
    private String penetratePath;
    @Value("${penetrate.prefix}")
    private String penetratePrefix;
    @Value("${server.port}")
    private String serverPort;


    /**
     * 检查内网穿透状态
     * @return
     */
    public Boolean checkPenetrateStatus() {
        String result = HttpUtil.get(penetrateUrl);
        String errorContent = "Tunnel " + penetratePrefix + ".vaiwan.com not found\n";
        if (errorContent.equals(result)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 启动内网穿透
     * @throws InterruptedException
     */
    @Async
    public void startPenetrate() throws InterruptedException {
        CommandUtil.executeCommand("ding -config=" + penetratePath + " -subdomain=" + penetratePrefix + " " + serverPort);
    }
}
