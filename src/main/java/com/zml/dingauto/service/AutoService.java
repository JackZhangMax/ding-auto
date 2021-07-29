package com.zml.dingauto.service;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baidu.aip.ocr.AipOcr;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/6/4 10:43
 */
@Slf4j
@Service
public class AutoService {

    private static Boolean startStatus = Boolean.TRUE;
    private static Boolean clockInSwitch = Boolean.TRUE;

    //设置APPID/AK/SK
    public static final String APP_ID = "24324749";
    public static final String API_KEY = "PPPN52BXGbfjkPtBnYNGDzb7";
    public static final String SECRET_KEY = "aDidSh5kZ2qzYncmDy4O272MADGExEXN";

    @Async
    public void start() throws InterruptedException {
        startStatus = Boolean.TRUE;
        // 获取休眠随机九分钟之内的值
        Random r = new Random();
        log.info("任务初始化···");
        //唤醒屏幕
        log.info(LocalDateTime.now() + ": 任务开始!");
        log.info("正在唤醒屏幕···");
        executeCommand("adb shell input keyevent 26");
        System.out.print("正在解锁手机:");
        countdown(2L);
        // 解锁手机
        executeCommand("adb shell input keyevent 82");
        countdown(1L);
        executeCommand("adb shell input text 089089");
        // 回到主界面
        log.info("正在回到主界面:");
        countdown(2L);
        executeCommand("adb shell input keyevent 3");
        countdown(2L);
        executeCommand("adb shell input keyevent 3");
        log.info("正在打开钉钉:");
        countdown(15L);
        // 打开钉钉(模拟点击)
        executeCommand("adb shell input tap 108 1443");
//        executeCommand("adb shell am start -n com.alibaba.android.rimet/com.alibaba.android.rimet.biz.LaunchHomeActivity");
        // 判断是否登录 如果未登录输入密码登录
        String output = executeCommand("adb shell dumpsys activity top | grep ACTIVITY");
        if (output.contains("SignUpWithPwdActivity")) {
            // 点击密码框
            executeCommand("adb shell input tap 300 970");
            countdown(3L);
            // 输入密码
            executeCommand("adb shell input text qq5211314");
            // 点击登录
            executeCommand("adb shell input tap 670 1165");
        }

        // 点击工作台
        executeCommand("adb shell input tap 700 2460");
        // 休眠五秒
        countdown(5L);
        // 点击考勤打卡
        executeCommand("adb shell input tap 200 1360");
        // 休眠五秒
        countdown(5L);
        // 点击打卡
//        executeCommand("adb shell input tap 700 1450");
        // 休眠十秒
//        countdown(10L);

        // 校验打卡是否成功
        Integer num = checkClockInStatus();
        if (num > 0) {
            // 打卡成功通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡成功" + num + "次");
        }else {
            // 打卡成功通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡失败");
        }
        // 休眠10到20秒
        log.info("正在准备进入多任务界面:");
        long killDingTime = r.nextInt(20 - 10 + 1) + 10;
        countdown(killDingTime);
        // 杀死钉钉
        killDing();

    }

    public static String executeCommand(String command) throws InterruptedException {
        return executeCommand(command, Boolean.TRUE);
    }

    public static String executeCommand(String command, Boolean killStatus) throws InterruptedException {
        Thread.sleep(1000);
        if (!startStatus && killStatus){
            // 结束进程通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/手动结束");
            // 杀死钉钉
            killDing();
            throw new BaseException("退出");
        }
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(command);
            return inputStream2String(process.getInputStream());
        } catch (Exception unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
        return "";
    }

    /**
     * 执行cmd 并输出文件
     * @param command
     * @throws InterruptedException
     */
    public static void executeCommandFile(String command) throws InterruptedException {
        Thread.sleep(1000);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("adb exec-out screencap -p");
            FileUtil.writeFromStream(process.getInputStream(), "D:/work/ding-auto/screen/1.png");
        } catch (Exception unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
    }

    /**
     * 倒计时
     * @param time 时间(秒)
     * @throws InterruptedException
     */
    public static void countdown(Long time) throws InterruptedException {
        for (long i = time; i >= 0; i--) {

            System.out.print('\r');
            Thread.sleep(1000);
            System.out.print("操作将在");
            System.out.print(i);
            System.out.print("秒后执行");
        }
        System.out.print('\r');
        log.info(LocalDateTime.now() + ": done!");
    }

    public static String inputStream2String(InputStream inputStream) throws IOException {
        String line = "";
        StringBuilder sb = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\r\n");
        }


        return sb.toString();
    }

    /**
     * 校验打卡状态
     */
    public static Integer checkClockInStatus() throws InterruptedException {
        // 截图
        log.info("正在校验打卡结果");
        executeCommandFile("adb exec-out screencap -p");
        // 等待十秒
        countdown(10L);
        // 截取图片
        ImgUtil.cut(
                FileUtil.file("D:/work/ding-auto/screen/1.png"),
                FileUtil.file("D:/work/ding-auto/screen/2.png"),
                //裁剪的矩形区域
                new Rectangle(100, 700, 1200, 250)
        );
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");
        // 参数为本地图片路径
        String image = "D:/work/ding-auto/screen/2.png";
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        JSONObject res = client.basicGeneral(image, options);
        log.info("basicGeneral resp = {}", res);
        BaiduOcrResp baiduOcrResp = JSONUtil.toBean(res.toString(), BaiduOcrResp.class);
        List<String> num = baiduOcrResp.getWords_result().stream().map(BaiduOcrResp.WordResult::getWords).filter(words -> words.contains("已打卡")).collect(Collectors.toList());
        return num.size();
    }

    public static void main(String[] args) throws InterruptedException {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            long sleepTime = r.nextInt(420 - 120 + 1) + 120;
            log.info(String.valueOf(sleepTime));
        }


    }

    public void kill() {

        startStatus = Boolean.FALSE;
    }


    /**
     * 杀死钉钉进程
     * @throws InterruptedException
     */
    public static void killDing() throws InterruptedException {
        // 进入多任务界面
        executeCommand("adb shell input keyevent 187", Boolean.FALSE);
        log.info("正在杀死进程:");
        countdown(3L);
        // 杀死进程
        executeCommand("adb shell input tap 710 2239", Boolean.FALSE);
        log.info("正在准备锁屏:");
        // 十秒后锁屏
        countdown(10L);
        executeCommand("adb shell input keyevent 26", Boolean.FALSE);
    }


}
