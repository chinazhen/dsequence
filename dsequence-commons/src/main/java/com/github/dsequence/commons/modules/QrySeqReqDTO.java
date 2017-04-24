package com.github.dsequence.commons.modules;

import lombok.*;

import java.io.Serializable;

/**
 * 查询序列信息接口请求参数
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/2
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QrySeqReqDTO implements Serializable {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 序列名称
     */
    private String seqName;

}
