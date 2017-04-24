package com.github.dsequence.server.manager;

import com.github.dsequence.commons.modules.DeleteSeqReqDTO;
import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import com.github.dsequence.server.dal.SequenceInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * 删除序列功能管理模块
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/8
 */
@Slf4j
@Component
public class DeleteSequenceManager {

    /**
     * 序列号数据库操作接口
     */
    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;


    /**
     * 删除序列
     *
     * @param reqDTO        序列初始化信息封装
     */
    public void deleteSequence(DeleteSeqReqDTO reqDTO) {

        try {

            long started = System.currentTimeMillis();

            log.debug("删除序列请求信息:{}",reqDTO);

            SequenceInfoDO sequenceInfoDO = new SequenceInfoDO();
            sequenceInfoDO.setSeqName(reqDTO.getSeqName());
            sequenceInfoDO.setAppName(reqDTO.getAppName());

            int rows = sequenceInfoMapper.delete(reqDTO.getAppName(),reqDTO.getSeqName());
            if (rows != 0 ) {
                throw new SequenceException(ErrorCodeEnum.SEQUENCE_NOT_FOUND);
            }

            log.debug("删除序列->{} 成功,耗时:{}",reqDTO,System.currentTimeMillis() - started);

        } catch (SequenceException t) {
            log.error("删除序列失败,数据库操作异常:{}",t.getMessage());
            throw t;
        } catch (DataAccessException t) {
            log.error("删除序列失败,数据库操作异常:{}",t.getMessage());
            throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
        }

    }

}
