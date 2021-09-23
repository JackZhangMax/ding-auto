package com.zml.dingauto.service;

import cn.hutool.http.HttpUtil;
import com.zml.dingauto.util.CommandUtil;
import org.springframework.beans.factory.annotation.Value;
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


    /**
     * 检查内网穿透状态
     * @return
     */
    public Boolean checkPenetrateStatus() {
        String result = HttpUtil.get(penetrateUrl);
        String errorContent = "Tunnel zml.vaiwan.com not found\n";
        if (errorContent.equals(result)){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 启动内网穿透
     * @throws InterruptedException
     */
    public void startPenetrate() throws InterruptedException {
        CommandUtil.executeCommand("ding -config=D:\\work\\pierced\\windows_64\\ding.cfg -subdomain=zml 80");
    }
}
