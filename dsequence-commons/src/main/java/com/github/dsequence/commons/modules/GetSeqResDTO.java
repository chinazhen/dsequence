package com.github.dsequence.commons.modules;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
public class GetSeqResDTO implements Serializable {

    private static final long serialVersionUID = -8951064588831180405L;

    /**
     * 起始sequence
     */
    private Long seqBegin;

    /**
     * 结束sequence
     */
    private Long seqEnd;

    /**
     * 最大使用sequence
     */
    private Long maxSeq;

}
