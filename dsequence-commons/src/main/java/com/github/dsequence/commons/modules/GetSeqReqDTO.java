package com.github.dsequence.commons.modules;

import lombok.*;

import java.io.Serializable;

/**
 * 获取序列号信息封装
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetSeqReqDTO implements Serializable {

    private static final long serialVersionUID = -8951064588831180405L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 序列名称
     */
    private String seqName;

    /**
     * 本次获取多少条
     */
    private int limit;

}
