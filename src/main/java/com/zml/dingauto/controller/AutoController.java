package com.zml.dingauto.controller;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.zml.dingauto.service.AutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    @Autowired
    private ThreadPoolTaskExecutor executor;

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

    @GetMapping("getScreen/{filePath}")
    public void getScreen(@PathVariable("filePath") String filePath, HttpServletResponse response) throws IOException {
        autoService.getScreen(response, filePath);
    }

    @GetMapping("test")
    public List<String> test() throws ExecutionException, InterruptedException {
        List<String> a = new ArrayList<>();
        a.add("https://www.cnblogs.com/pxzbky/p/14214436.html");
        a.add("https://fat-seller.haoqipei.com/#/quotes/list");
        a.add("https://v5.modao.cc/app/3f2ad9988e664fafa05f0f01c97fcace6fc455c0?simulator_type=device&sticky#screen=sks4immvwv039ed");
        a.add("https://www.52pojie.cn/");
        a.add("https://movie.douban.com/");

        List<String> b = new ArrayList<>();
        List<Future<String>> futureList = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            int finalI = i;
            futureList.add(executor.submit(()-> HttpUtil.get(a.get(finalI))));
        }

        for (Future<String> stringFuture : futureList) {
            b.add(stringFuture.get());
        }
        System.out.println(b);
        return b;
    }

    @GetMapping("switchState")
    public String switchState() {
        return autoService.switchState();
    }

}
