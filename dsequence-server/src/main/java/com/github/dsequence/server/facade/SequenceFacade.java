package com.github.dsequence.server.facade;


import com.github.dsequence.commons.modules.*;

import java.util.List;

/**
 * 序列号服务接口
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
public interface SequenceFacade  {

    /**
     * 查询序列信息
     *
     * @param   reqDTO      查询序列信息接口请求参数（应用名称,序列名称）
     * @return              一批sequence（起始,结束）
     */
    Result<List<QrySeqResDTO>> querySequences(QrySeqReqDTO reqDTO);

    /**
     * 获取sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              一批sequence（起始,结束）
     */
    Result<GetSeqResDTO> getBatchSequence(GetSeqReqDTO reqDTO);

    /**
     * 创建sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              状态
     */
    Result<Boolean> createBatchSequence(CreSeqReqDTO reqDTO);

    /**
     * 删除sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              状态
     */
    Result<Boolean> deleteBatchSequence(DeleteSeqReqDTO reqDTO);

}
