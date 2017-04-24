package com.github.dsequence.server.manager;

import com.github.dsequence.commons.modules.CreSeqReqDTO;
import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import com.github.dsequence.server.dal.SequenceInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 创建序列功能管理模块
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/8
 */
@Slf4j
@Component
public class CreateSequenceManager {

    /**
     * 序列号数据库操作接口
     */
    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;

    /**
     * 创建序列
     *
     * @param reqDTO        序列初始化信息封装
     */
    public void createSequence(CreSeqReqDTO reqDTO) {

        try {

            long started = System.currentTimeMillis();

            log.debug("创建序列请求信息:{}",reqDTO);

            SequenceInfoDO sequenceInfoDO = new SequenceInfoDO();
            sequenceInfoDO.setSeqName(reqDTO.getSeqName());
            sequenceInfoDO.setAppName(reqDTO.getAppName());
            sequenceInfoDO.setLastSeq(0L);
            if (Objects.isNull(reqDTO.getNextSeq())) {
                sequenceInfoDO.setNextSeq(1L);
            } else {
                sequenceInfoDO.setNextSeq(reqDTO.getNextSeq());
            }
            sequenceInfoDO.setMaxSeq(reqDTO.getMaxSeq());
            sequenceInfoDO.setCreatedBy(reqDTO.getCreatedBy());
            sequenceInfoDO.setUpdatedBy(reqDTO.getCreatedBy());

            SequenceInfoDO existed = sequenceInfoMapper.select(reqDTO.getAppName(),reqDTO.getSeqName());
            if (Objects.nonNull(existed) ) {
                log.debug("创建序列->{} 成功,已创建过,耗时:{}",reqDTO,System.currentTimeMillis() - started);
                return;
            }

            try {
                int rows = sequenceInfoMapper.insert(sequenceInfoDO);
                if (rows != 1) {
                    throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
                }
            } catch (DuplicateKeyException e) {
                log.debug("序列 -> {} {} 已存在.",sequenceInfoDO.getAppName(),sequenceInfoDO.getSeqName());
            }

            log.debug("创建序列->{} 成功,耗时:{}",reqDTO,System.currentTimeMillis() - started);

        } catch (SequenceException t) {
            log.error("创建序列失败,数据库操作异常:{}",t.getMessage());
            throw t;
        } catch (DataAccessException t) {
            log.error("创建序列失败,数据库操作异常:{}",t.getMessage());
            throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
        }

    }

}
