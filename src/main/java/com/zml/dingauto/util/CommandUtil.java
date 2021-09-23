package com.zml.dingauto.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/9/18 9:51
 */
@Slf4j
public class CommandUtil {

    public static String executeCommand(String command) throws InterruptedException {

        try {
            Runtime run = Runtime.getRuntime();
            log.info("执行命令 : {}", command);
            Process process = run.exec(command);
            String result = inputStream2String(process.getInputStream());
            log.info("返回结果 : {}", result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String executeAdbCommand(String command) throws InterruptedException {
        command = "adb shell " + command;
        Thread.sleep(1000);
        return executeCommand(command);
    }

    /**
     * 执行cmd 并输出文件
     * @param command
     * @throws InterruptedException
     */
    public static void executeCommandFile(String command, String filePath) throws InterruptedException {
        Thread.sleep(1000);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(command);
            FileUtil.writeFromStream(process.getInputStream(), filePath);
        } catch (Exception unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        }
    }

    public static String inputStream2String(InputStream inputStream) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\r\n");
        }


        return sb.toString();
    }
}
