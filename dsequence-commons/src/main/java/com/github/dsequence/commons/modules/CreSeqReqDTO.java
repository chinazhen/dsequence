package com.github.dsequence.commons.modules;

import lombok.*;

import java.io.Serializable;

/**
 * 创建序列号信息封装
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreSeqReqDTO implements Serializable {

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
     * 下次序列号
     */
    private Long nextSeq;

    /**
     * 最大使用sequence
     */
    private Long maxSeq;

    /**
     * 创建人
     */
    private String createdBy;

}
