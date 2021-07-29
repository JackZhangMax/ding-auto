package com.zml.dingauto.controller;

import cn.hutool.extra.servlet.ServletUtil;
import com.zml.dingauto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/6/4 10:42
 */
@Slf4j
@RestController
@RequestMapping("/")
public class AutoController {

    @Resource
    private AutoService autoService;

    @GetMapping("start")
    public String start(HttpServletRequest request, String key) throws InterruptedException {
        if (!"nicaibudaoa".equals(key)){
            return "你大爷";
        }
        log.info("start ip: {}", ServletUtil.getClientIP(request));
        autoService.start();
        return "成功啦";
    }

    @GetMapping("kill")
    public String kill(HttpServletRequest request){
        log.info("kill ip: {}", ServletUtil.getClientIP(request));
        autoService.kill();
        return "成功";
    }

}
