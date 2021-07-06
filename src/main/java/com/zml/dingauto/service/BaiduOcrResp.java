package com.zml.dingauto.service;

import lombok.Data;

import java.util.List;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/6/7 15:35
 */
@Data
public class BaiduOcrResp {
    private long log_id;
    private int words_result_num;
    private List<WordResult> words_result;


    @Data
    public static class WordResult {
        private String words;
    }

}
