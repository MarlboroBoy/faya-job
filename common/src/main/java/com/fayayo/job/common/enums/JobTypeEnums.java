package com.fayayo.job.common.enums;

import lombok.Getter;

/**
 * @author dalizu on 2018/8/31.
 * @version v1.0
 * @desc 任务类型
 */
@Getter
public enum JobTypeEnums {

    BEAN("BEAN","基于注解的任务")
    ;
    //SPARK("SPARK","SPARK任务");

    private String name;

    private String message;

    JobTypeEnums(String name, String message) {
        this.name = name;
        this.message = message;
    }

}
