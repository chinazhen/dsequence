package com.github.dsequence.server.manager;

import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import com.github.dsequence.server.dal.SequenceInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 序列号管理服务
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Slf4j
@Component
public class SequenceManager {

    /**
     * 序列号数据库操作接口
     */
    @Autowired
    private SequenceInfoMapper sequenceInfoMapper;

    /**
     * 默认最大序列号（18位）
     */
    public static final Long _default_max_seq = 999999999999999999L;

    /**
     * 默认最小序列号（5位）
     */
    public static final Long _default_min_seq = 99999L;

    /**
     * 默认批次获取序列大小
     */
    private static final Integer _default_limit = 2000;

    /**
     * 默认创建人
     */
    private static final String _default_created_by = "SYSTEM";

    private static final Byte fillThreadStatusLock = 'L';

    private static int fillThreadStatus = 0;

    /**
     * Spring 事物模板
     */
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 获取序列缓存队列 - 用于减少客户端每次获取耗时
     */
    static final ConcurrentHashMap<String,SequenceInfoDO> seqQueueMap = new ConcurrentHashMap<>();

    /**
     * 获取序列号
     *
     * @param appName       应用名称
     * @param seqName       序列名称
     * @param limit         获取数量
     * @return              序列信息
     */
    private SequenceInfoDO getBatchSequenceInfo(String appName,String seqName,int limit) {

        try {

            return transactionTemplate.execute(status -> {

                Long id = sequenceInfoMapper.selectIdForUpdate(appName,seqName);
                if (null == id) {
                    SequenceInfoDO reqDO = new SequenceInfoDO();
                    reqDO.setAppName(appName);
                    reqDO.setSeqName(seqName);
                    reqDO.setLastSeq(0L);
                    reqDO.setNextSeq(1L);
                    reqDO.setMaxSeq(_default_max_seq);
                    reqDO.setCreatedBy(_default_created_by);
                    reqDO.setUpdatedBy(_default_created_by);
                    try {
                        int rows = sequenceInfoMapper.insert(reqDO);
                        if (rows != 1) {
                            throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
                        }
                        id = reqDO.getId();
                    } catch (DuplicateKeyException e) {
                        log.debug("序列 -> {} {} 已存在.",appName,seqName);
                        id = sequenceInfoMapper.selectIdForUpdate(appName,seqName);
                    }
                }

                log.debug("根据应用名称->{} 及序列名称->{} 查询到序列ID->{}",appName,seqName,id);

                SequenceInfoDO updateDO = new SequenceInfoDO();
                updateDO.setId(id);
                updateDO.setAppName(appName);
                updateDO.setSeqName(seqName);

                int rows = sequenceInfoMapper.update(id,limit);
                if (rows != 1) {
                    throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
                }

                log.debug("根据ID->{} 更新序列成功,受影响行数->{}",id,rows);

                SequenceInfoDO sequenceInfoDO = sequenceInfoMapper.selectById(id);

                if (sequenceInfoDO.getMaxSeq() <= sequenceInfoDO.getNextSeq()) {
                    sequenceInfoDO.setLastSeq(sequenceInfoDO.getNextSeq());
                    sequenceInfoDO.setNextSeq((long) limit);
                    log.warn("序列->{}{} 达到最大值,回归到1",appName,seqName);
                    rows = sequenceInfoMapper.reset(sequenceInfoDO);
                    if (rows != 1) {
                        throw new SequenceException(ErrorCodeEnum.DATA_BASE_OPERATOR_EXCEPTION);
                    }
                }

                sequenceInfoDO.setLimit((int) (sequenceInfoDO.getNextSeq()-sequenceInfoDO.getLastSeq()));
                log.debug("根据ID->{} 更新序列成功,更新后结果->{}",id,sequenceInfoDO);

                return sequenceInfoDO;

            });

        } catch (Throwable t) {
            log.error("获取序列异常:{}{}", t.getMessage(),t);
            throw t;
        }

    }

    /**
     * 初始化函数
     */
    public void initData() {

        try {

            List<SequenceInfoDO> list = sequenceInfoMapper.selectAll();

            if (list.isEmpty()) {
                return;
            }

            list.forEach(seq->{

                String key = seq.getAppName() + ":" + seq.getSeqName();

                if (Objects.isNull(seqQueueMap.get(key))) {
                    SequenceInfoDO cacheInfo = new SequenceInfoDO();
                    cacheInfo.setAppName(seq.getAppName());
                    cacheInfo.setSeqName(seq.getSeqName());
                    cacheInfo.setLastSeq(0L);
                    cacheInfo.setNextSeq(0L);
                    cacheInfo.setMaxSeq(seq.getMaxSeq());
                    cacheInfo.setLimit(_default_limit * 100);
                    seqQueueMap.put(key,cacheInfo);
                }

            });


        } catch (Throwable t) {
            log.error(t.getMessage(),t);
        } finally {
            fillSeqMap();
        }

    }

    void fillSeqMap() {

        synchronized (SequenceManager.fillThreadStatusLock) {

            if (fillThreadStatus == 1) {
                return;
            }

            SequenceManager.fillThreadStatus = 1;
        }

        new Thread(()->{

            while (true) {

                try {

                    seqQueueMap.keySet().forEach(k -> {
                        SequenceInfoDO v = seqQueueMap.get(k);
                        long hungrySize = (v.getLimit() / 10) * 8;
                        long surplus = 0L;
                        if (Objects.nonNull(v.getNextSeq()) && v.getNextSeq() > 0) {
                            surplus = v.getNextSeq() - v.getLastSeq();
                            if (surplus > hungrySize) {
                                return;
                            }
                        }

                        int limit = (int) (v.getNextSeq() - v.getLastSeq());

                        if (0 >= v.getNextSeq()) {
                            limit = v.getLimit();
                        }

                        long started = System.currentTimeMillis();
                        log.info("服务端缓存序列请求信息: AppName->{} SeqName->{},Limit->{}", k.split(":")[0],k.split(":")[1],limit);
                        SequenceInfoDO seq = getBatchSequenceInfo(k.split(":")[0],k.split(":")[1],limit);
                        log.info("服务端缓存序列信息耗时: {} {}", System.currentTimeMillis() - started, seq);
                        seq.setLimit(v.getLimit());

                        if (Objects.isNull(v.getLastSeq()) || v.getLastSeq() <= 0) {
                            v.setLastSeq(seq.getLastSeq());
                        }
                        v.setMaxSeq(seq.getMaxSeq());
                        v.setNextSeq(seq.getNextSeq());
                        seqQueueMap.put(k,v);

                    });

                    Thread.sleep(10);

                } catch (Throwable t) {
                    log.error(t.getMessage(),t);
                }

            }

        }).start();

    }

}
