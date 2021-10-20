package com.zml.dingauto.config;

import com.zml.dingauto.service.PenetrateService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/10/14 11:06
 */
@Component
public class PenetrateRunner implements ApplicationRunner {

    @Resource
    private PenetrateService penetrateService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        penetrateService.startPenetrate();
    }
}
