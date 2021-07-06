package com.zml.dingauto.service;

import lombok.Data;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/7/6 9:50
 */
@Data
public class TimorResp {

    private Integer code;

    private Type type;

    private String holiday;

    @Data
    public static class Type {
        private Integer type;

        private String name;

        private Integer week;

    }


}
