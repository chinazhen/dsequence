package com.github.dsequence.server.dal;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 序列号信息数据库操作接口
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
public interface SequenceInfoMapper {

    /**
     * 新增序列号信息
     *
     * @param sequenceInfoDO    序列号信息封装
     * @return                  受影响行数
     */
    int insert(SequenceInfoDO sequenceInfoDO);

    /**
     * 判断是否已存在
     *
     * @param appName           应用名称
     * @param seqName           序列名称
     * @return                  序列号ID
     */
    int isExisted(@Param("appName") String appName, @Param("seqName") String seqName);

    /**
     * 删除序列
     *
     * @param appName           应用名称
     * @param seqName           序列名称
     * @return                  受影响行数
     */
    int delete(@Param("appName") String appName, @Param("seqName") String seqName);

    /**
     * 查询序列信息集合
     *
     * @param appName           应用名称
     * @param seqName           序列名称
     * @return                  序列信息集合
     */
    List<SequenceInfoDO> selectList(@Param("appName") String appName, @Param("seqName") String seqName);

    /**
     * 查询所有序列信息集合
     *
     * @return                  序列信息集合
     */
    List<SequenceInfoDO> selectAll();

    /**
     * 根据应用名称及序列名称查询
     *
     * @param appName           应用名称
     * @param seqName           序列名称
     * @return                  序列号ID
     */
    Long selectIdForUpdate(@Param("appName") String appName, @Param("seqName") String seqName);

    /**
     * 根据ID查询
     *
     * @param id                序列ID
     * @return                  序列号信息封装
     */
    SequenceInfoDO selectById(@Param("id") Long id);

    /**
     * 根据应用名称及序列名称查询
     *
     * @param appName           应用名称
     * @param seqName           序列名称
     * @return                  序列号信息封装
     */
    SequenceInfoDO select(@Param("appName") String appName, @Param("seqName") String seqName);

    /**
     * 更新序列号信息
     *
     * @param id                序列id
     * @param limit             批次梳理
     * @return                  受影响行数
     */
    int update(@Param("id") Long id,@Param("limit") int limit);

    /**
     * 更新序列号信息
     *
     * @param sequenceInfoDO    序列号信息封装
     * @return                  受影响行数
     */
    int reset(SequenceInfoDO sequenceInfoDO);

}
