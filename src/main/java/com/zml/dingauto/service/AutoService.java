package com.zml.dingauto.service;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baidu.aip.ocr.AipOcr;
import com.zml.dingauto.util.CommandUtil;
import com.zml.dingauto.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
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
    private static final Boolean clockInSwitch = Boolean.TRUE;

    private static final String SCREEN_PATH = "D:/work/ding-auto/screen/";

    private static final String EXEC_CMD = "adb exec-out screencap -p";

    private static final String PNG_SUFFIX = ".png";

    private static BigDecimal length = null;

    private static BigDecimal width = null;

    @Value("${penetrate.url}")
    private String penetrateUrl;
    @Value("${baidu.ocr.appId}")
    private String appId;
    @Value("${baidu.ocr.apiKey}")
    private String apiKey;
    @Value("${baidu.ocr.secretKey}")
    private String secretKey;

    private static Boolean status = Boolean.TRUE;

    @Async
    public void start() throws InterruptedException {
        if (!status){
            return;
        }
        // 获取分辨率
        getPhysicalSize();
        startStatus = Boolean.TRUE;
        // 获取休眠随机九分钟之内的值
        Random r = new Random();
        log.info("任务开始!");
        // 解锁手机
        unlock();
        log.info("解锁手机完成");
        home();
        log.info("已回到主界面");
        countdown(10L);
        log.info("钉钉将在10秒后打开");
        openDing();
        log.info("已打开钉钉");
        checkResult();
        log.info("校验打卡结果完成");
        long killDingTime = r.nextInt(20 - 10 + 1) + 10;
        log.info("将在" + killDingTime + "秒后杀死进程");
        countdown(killDingTime);
        // 杀死钉钉
        killDing();
        log.info("任务结束");
    }


    /**
     * 倒计时
     * @param time 时间(秒)
     * @throws InterruptedException
     */
    public static void countdown(Long time) throws InterruptedException {
        Thread.sleep(time * 1000);
    }

    /**
     * 校验打卡状态
     */
    public Integer checkClockInStatus() throws InterruptedException {
        // 截图
        log.info("正在校验打卡结果");
        // 原图路径
        String originalPath = saveScreenshot("checkOriginal");
        // 判断文件大小 如果不大于零则发送失败通知
        if (FileUtil.size(FileUtil.file(originalPath)) == 0) {
            // 结束进程通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/截图失败");
            // 杀死钉钉
            killDing();
        }
        String date = DateUtil.getDate() + "/";
        String fileName = "checkScreenshot" + "_" + DateUtil.getNowTime("HH_mm_ss") + PNG_SUFFIX;
        // 截取后路径
        String screenshotPath = SCREEN_PATH + date + fileName;
        // 等待十秒
        countdown(10L);
        // 截取图片
        ImgUtil.cut(
                FileUtil.file(originalPath),
                FileUtil.file(screenshotPath),
                //裁剪的矩形区域
                new Rectangle(100, 700, 1200, 250)
        );
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(appId, apiKey, secretKey);
        JSONObject res = client.basicGeneral(screenshotPath, options);
        log.info("basicGeneral resp = {}", res);
        BaiduOcrResp baiduOcrResp = JSONUtil.toBean(res.toString(), BaiduOcrResp.class);
        List<String> num = baiduOcrResp.getWords_result().stream().map(BaiduOcrResp.WordResult::getWords).filter(words -> words.contains("已打卡")).collect(Collectors.toList());
        if (num.size() > 0) {
            // 打卡成功通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡成功" + num.size() + "次/内容:"+num.toString());
        } else {
            // 打卡失败通知
            HttpUtil.get("https://api.day.app/w8LtxK8JtqnF6LoyJrALg8/打卡失败?url=" + penetrateUrl + "/getScreen/" + fileName);
        }
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
    public void killDing() throws InterruptedException {
        // 进入多任务界面
        CommandUtil.executeAdbCommand("input keyevent 187");
        log.info("正在杀死进程:");
        countdown(3L);
        // 杀死进程(需要自己定位)
        CommandUtil.executeAdbCommand("input tap " + getLocation(0.493, 0.874));
        log.info("正在准备锁屏:");
        // 十秒后锁屏
        countdown(10L);
        CommandUtil.executeAdbCommand("input keyevent 26");
    }

    /**
     * 解锁手机
     */
    public void unlock() {
        try {
            // 判断屏幕是否已经点亮
            if (CommandUtil.executeAdbCommand("dumpsys power | grep 'Display Power: state='").contains("state=OFF")) {
                CommandUtil.executeAdbCommand("input keyevent 26");
                countdown(2L);
            }
            // 判断是否解锁
            if (CommandUtil.executeAdbCommand("dumpsys window | grep isStatusBarKeyguard").contains("isStatusBarKeyguard=true")) {
                // 解锁手机
                CommandUtil.executeAdbCommand("input keyevent 82");
                countdown(2L);
                CommandUtil.executeAdbCommand("input text 089089");
                countdown(2L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveScreenshot("unlock");
    }

    /**
     * 主界面
     */
    public void home() {
        try {
            CommandUtil.executeAdbCommand("input keyevent 3");
            countdown(2L);
            CommandUtil.executeAdbCommand("input keyevent 3");
            countdown(2L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveScreenshot("home");
    }

    /**
     * 打开钉钉
     */
    public void openDing() {

        try {
            // 打开钉钉(模拟点击 需自己填写)
            CommandUtil.executeAdbCommand("input tap " + getLocation(0.075, 0.563));
//            CommandUtil.executeAdbCommand("input tap " + getLocation(0.648, 0.385));
            // 判断是否登录 如果未登录输入密码登录
            String output = CommandUtil.executeAdbCommand("dumpsys activity top | grep ACTIVITY");
            if (output.contains("SignUpWithPwdActivity")) {
                // 点击密码框
                CommandUtil.executeAdbCommand("input tap " + getLocation(0.208, 0.379));
                countdown(3L);
                // 输入密码
                CommandUtil.executeAdbCommand("input text qq5211314");
                countdown(3L);
                // 返回
                CommandUtil.executeAdbCommand("input keyevent 4");
                countdown(3L);
                // 点击同意协议
                CommandUtil.executeAdbCommand("input tap " + getLocation(0.096, 0.551));
                // 点击登录
                CommandUtil.executeAdbCommand("input tap " + getLocation(0.465, 0.455));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveScreenshot("openDing");
    }

    /**
     * 校验打卡结果
     */
    public void checkResult() {
        try {
            countdown(6L);
            // 点击工作台
            CommandUtil.executeAdbCommand("input tap " + getLocation(0.486, 0.961));
            // 休眠五秒
            countdown(5L);
            // 点击考勤打卡
            CommandUtil.executeAdbCommand("input tap " + getLocation(0.139, 0.585));
            // 休眠五秒
            countdown(5L);
            // 点击打卡
//        CommandUtil.executeAdbCommand("input tap 700 1450");
            // 休眠十秒
//        countdown(10L);
            // 校验打卡是否成功
            checkClockInStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
        saveScreenshot("checkResult");
    }

    /**
     * 保存截图
     * @param name
     */
    private static String saveScreenshot(String name) {
        try {
            String date = DateUtil.getDate() + "/";
            String path = SCREEN_PATH + date + name + "_" + DateUtil.getNowTime("HH_mm_ss") + PNG_SUFFIX;
            CommandUtil.executeCommandFile(EXEC_CMD, path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取手机尺寸
     * @throws InterruptedException
     */
    public void getPhysicalSize() throws InterruptedException {
        if (length == null) {
            String output = CommandUtil.executeAdbCommand("wm size");
            String value = output.substring(15, output.length() - 2);
            String[] physicalSize = value.split("x");
            width = new BigDecimal(physicalSize[0]);
            length = new BigDecimal(physicalSize[1]);
        }
    }

    /**
     * 获取点击位置
     * @param x
     * @param y
     * @return
     */
    public String getLocation(Double x, Double y) {
        return new BigDecimal(x).multiply(width).setScale(0, BigDecimal.ROUND_UP).toString() + " " + new BigDecimal(y).multiply(length).setScale(0, BigDecimal.ROUND_UP);
    }


    public void getScreen(HttpServletResponse response, String filePath) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        String date = DateUtil.getDate() + "/";
        String path = SCREEN_PATH + date + filePath;
        outputStream.write(FileUtil.readBytes(path));
        outputStream.close();
    }

    public String switchState() {
        status = !status;
        return status.toString();
    }
}
