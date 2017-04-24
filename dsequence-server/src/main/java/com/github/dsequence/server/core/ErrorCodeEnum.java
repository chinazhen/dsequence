package com.github.dsequence.server.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 简介
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    PARAMETER_INVALID("PARAMETER_INVALID","参数交易错误"),

    SYSTEM_ERROR("SYSTEM_ERROR","系统内部错误"),

    SEQUENCE_EXISTED("SEQUENCE_EXISTED","序列已存在"),

    SEQUENCE_NOT_FOUND("SEQUENCE_NOT_FOUND","序列不存在"),

    DATA_BASE_OPERATOR_EXCEPTION("DATA_BASE_OPERATOR_EXCEPTION","数据库操作异常");


    private String code;

    private String message;

}
