package com.github.dsequence.server.dal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.Date;

/**
 * 序列号信息封装
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Getter
@Setter
@ToString(exclude = {"createdBy","createdAt","updatedBy","updatedAt"})
public class SequenceInfoDO implements Serializable {

    private static final long serialVersionUID = -8951064588831180405L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 序列名称
     */
    private String seqName;

    /**
     * 上次使用sequence
     */
    private Long lastSeq;

    /**
     * 下次使用sequence
     */
    private Long nextSeq;

    /**
     * 最大使用sequence
     */
    private Long maxSeq;

    /**
     * 分页大小
     */
    private Integer limit;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 最后更新时间
     */
    private Date updatedAt;

    /**
     * 最后更新人
     */
    private String updatedBy;

}
