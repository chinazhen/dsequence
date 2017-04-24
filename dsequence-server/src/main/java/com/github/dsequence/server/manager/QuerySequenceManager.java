package com.github.dsequence.server.manager;

import com.github.dsequence.server.dal.SequenceInfoMapper;
import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 查询序列功能管理模块
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/8
 */
@Slf4j
@Component
public class QuerySequenceManager {

    /**
     * 序列号数据库操作接口
     */
    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;


    /**
     * 查询序列信息
     *
     * @param appName       应用名称
     * @param seqName       序列名称
     * @return              序列信息
     */
    public List<SequenceInfoDO> queryList(String appName, String seqName){

        try {

            long started = System.currentTimeMillis();

            log.debug("查询序列信息请求参数 appName-> {} seqName-> {}" ,appName,seqName);

            List<SequenceInfoDO> results = sequenceInfoMapper.selectList(appName,seqName);

            log.debug("查询序列信息请求参数 appName-> {} seqName-> {} 耗时->{}" ,
                    appName,seqName,System.currentTimeMillis() - started);

            return results;

        } catch (DataAccessException t) {
            log.error("创建序列失败,数据库操作异常:{}",t.getMessage());
            throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
        }
    }

}
