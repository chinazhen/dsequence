package com.github.dsequence.server.manager;

import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;

/**
 * 获取序列功能管理模块
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/8
 */
@Slf4j
@Component
public class GetSequenceManager {

    /**
     * 序列号管理服务
     */
    @Autowired
    private SequenceManager sequenceManager;

    /**
     * 获取序列号
     *
     * @param appName       应用名称
     * @param seqName       序列名称
     * @param limit         获取数量
     * @return              序列信息
     */
    public SequenceInfoDO getSequence(String appName, String seqName, int limit){

        log.info("获取序列号请求参数 appName->{} seqName->{},limit->{}" ,appName,seqName,limit);

        try {

            String key = appName + ":" +seqName;

            while (true) {

                SequenceInfoDO cacheInfo = SequenceManager.seqQueueMap.get(key);

                if (Objects.nonNull(cacheInfo)) {

                    long lastSeq = cacheInfo.getLastSeq();
                    long nextSeq = cacheInfo.getNextSeq();
                    long maxSeq = cacheInfo.getMaxSeq();

                    if (limit > cacheInfo.getLimit()) {
                        cacheInfo.setLimit(limit);
                        SequenceManager.seqQueueMap.put(key,cacheInfo );
                    }

                    // 本次结束
                    long currentNextSeq = lastSeq + limit;
                    if (currentNextSeq > nextSeq) {
                        currentNextSeq = nextSeq;
                    }

                    if (lastSeq < currentNextSeq) {
                        SequenceInfoDO sequenceInfoDO = new SequenceInfoDO();
                        sequenceInfoDO.setLastSeq(lastSeq);
                        sequenceInfoDO.setNextSeq(currentNextSeq);
                        sequenceInfoDO.setMaxSeq(maxSeq);
                        cacheInfo.setLastSeq(currentNextSeq);
                        SequenceManager.seqQueueMap.put(key,cacheInfo );
                        return sequenceInfoDO;
                    }

                    continue;

                }

                cacheInfo = new SequenceInfoDO();
                cacheInfo.setLastSeq(0L);
                cacheInfo.setNextSeq(0L);
                cacheInfo.setMaxSeq(SequenceManager._default_max_seq);
                cacheInfo.setLimit(limit * 100);
                SequenceManager.seqQueueMap.put(key,cacheInfo);

                sequenceManager.fillSeqMap();

            }

        } catch (Throwable t) {
            log.error("获取序列号异常:{} {}",t.getMessage(),t);
            throw new SequenceException(ErrorCodeEnum.SYSTEM_ERROR);
        }

    }

}
