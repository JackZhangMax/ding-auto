package com.zml.dingauto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author a
 */
@SpringBootApplication
@EnableAsync
public class DingAutoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DingAutoApplication.class, args);
    }

}
