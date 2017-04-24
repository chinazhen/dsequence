package com.github.dsequence.client;

import com.github.dsequence.client.util.HttpClientUtil;
import com.github.dsequence.client.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.github.dsequence.commons.modules.*;


/**
 * 序列号工厂
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Slf4j
public class SequenceFactory {

    /** 默认最大序列 */
    private static final Long _default_max_seq = 999999999999999999L;

    /** 默认创建人 */
    private static final String _default_created_by = "SYSTEM";

    /** 序列队列缓存 */
    private static final ConcurrentHashMap<String,BlockingQueue<Long>> sequenceMap = new ConcurrentHashMap<>();

    /** 序列服务网关地址 */
    private static String _server_gateway;

    /** 单例对象 */
    private static SequenceFactory sequenceFactory = null;

    /** 锁 */
    private static final Byte lock = 'l';

    /** 默认序列队列大小 */
    private static final int _default_queue_size = 10000;

    /**
     * 构造函数-私有（单例）
     */
    private SequenceFactory() {}

    /**
     * 获取对象
     *
     * @param serverUrl     序列服务地址
     * @return              单例对象
     */
    public static SequenceFactory getInstance(String serverUrl){

        synchronized (SequenceFactory.lock) {

            if (Objects.nonNull(sequenceFactory)) {
                return sequenceFactory;
            }

            sequenceFactory = new SequenceFactory();
            SequenceFactory._server_gateway = serverUrl;

            sequenceFactory.scanHungry();

            return sequenceFactory;
        }

    }

    /**
     * 获取序列号
     *
     * @param appName       应用名称
     * @param seqName       序列名称
     * @return              序列号
     */
    public Long getSequence(String appName,String seqName) {


        long started = System.currentTimeMillis();

        String key = appName + ":" +seqName;

        BlockingQueue<Long> sequenceQueue;

        synchronized (sequenceMap) {
            sequenceQueue = sequenceMap.get(key);
            boolean isNull = Objects.isNull(sequenceQueue);
            if (isNull) {
                sequenceQueue = new ArrayBlockingQueue<>(_default_queue_size);
                sequenceMap.put(key,sequenceQueue);
                createSeq(appName,seqName);
            }
        }

        try {

            if (sequenceQueue.isEmpty()) {
                log.warn("=========== Iam ({}) empty .....",appName);
            }

            Long sequence = sequenceQueue.poll(3000,TimeUnit.MILLISECONDS);
            long endTimes = System.currentTimeMillis();

            if (endTimes - started > 200) {
                log.warn("获取序列耗时->{}",endTimes-started);
            }

            return sequence;

        } catch (InterruptedException e) {
            log.error("获取序为空,线程等待超时 {} {}",e.getMessage(),e);
        }

        throw new NullPointerException("获取序列异常,结果为null");

    }

    private void scanHungry() {

        new Thread(new hungryScanner()).start();

    }

    /**
     * 填充序列队列（当队列中缓存的序列消耗完后从序列服务获取一批）
     *
     * @param sequenceQueue     序列队列
     * @param appName           序列应用名称
     * @param seqName           序列名称
     */
    private static void fillQueue(BlockingQueue<Long> sequenceQueue,String appName,String seqName,int limit) {

        Map<String,String> params = new HashMap<>();
        params.put("appName",appName);
        params.put("seqName",seqName);
        params.put("limit",String.valueOf(limit));

        try {

            String response = HttpClientUtil.getInstance().sendHttpPost(_server_gateway + "/get",params);


            Result result = JsonUtil.toObject(response, Result.class);

            if (!result.isSuccess()) {
                log.info("从序列服务获取序列响应失败:{}",result);
                if (!"SEQUENCE_NOT_FOUND".equals(result.getErrorCode())) {
                    return;
                }

                createSeq(appName,seqName);

                long started = System.currentTimeMillis();

                response = HttpClientUtil.getInstance().sendHttpPost(_server_gateway + "/get",params);

                log.info("从服务端获取一批序列耗时:{}",System.currentTimeMillis() - started);
                System.out.println("从服务端获取一批序列耗时:" + (System.currentTimeMillis() - started));

                result = JsonUtil.toObject(response, Result.class);

                if (!result.isSuccess()) {
                    return;
                }

            }

            GetSeqResDTO resDTO = JsonUtil.toObject(result.getResult().toString(),GetSeqResDTO.class);

            Long seqBegin = resDTO.getSeqBegin();
            Long seqEnd = resDTO.getSeqEnd();

            for (;seqBegin < seqEnd;seqBegin++) {

                while (true) {
                    try {
                        boolean joined = sequenceQueue.offer(seqBegin,500, TimeUnit.MILLISECONDS);
                        if (joined) {
                            break;
                        }
                    } catch (Throwable t) {
                        Thread.sleep(10);
                        log.warn("队列->{}-{} 已满,等待放入",appName,seqName);
                    }
                }
            }

        } catch (Throwable t) {
            log.error(t.getMessage(),t);
        }

    }

    /**
     * 创建序列
     *
     * @param appName       应用名称
     * @param seqName       序列名称
     */
    private static void createSeq(String appName,String seqName) {

        Map<String,String> params = new HashMap<>();
        params.put("appName",appName);
        params.put("seqName",seqName);
        params.put("maxSize",String.valueOf(_default_max_seq));
        params.put("createdBy",_default_created_by);

        try {

            String response = HttpClientUtil.getInstance().sendHttpPost(_server_gateway + "/create",params);

            Result result = JsonUtil.toObject(response, Result.class);

            if (result.isSuccess()) {
                log.info("创建应用->{} 序列->{}成功,响应对象->{}",appName,seqName,result);
                return;
            }

            log.warn("创建应用->{} 序列->{}失败,响应对象->{}",appName,seqName,result);

        } catch (Throwable t) {
            log.error(t.getMessage(),t);
        }


    }

    private class hungryScanner implements Runnable {

        @Override
        public void run() {

            int hungrySize = _default_queue_size / 10 * 8;

            List<String> keys = new ArrayList<>();

            Map<String,Thread> seqScannerThread = new HashMap<>();

            while (true) {

                try {

                    List<String> tempKeys = new ArrayList<>(sequenceMap.keySet());

                    tempKeys.removeAll(keys);

                    if (!tempKeys.isEmpty()) {
                        keys.addAll(tempKeys);
                    }

                    keys.forEach(k ->{

                        Thread thread = seqScannerThread.get(k);
                        if (Objects.isNull(thread)) {

                            log.info("增加序列->{} 的饥饿扫描线程", k);

                            thread = new Thread(() -> {

                               while (true) {
                                   try {

                                       BlockingQueue<Long> v = sequenceMap.get(k);

                                       if (v.size() <= hungrySize) {

                                           String appName = k.split(":")[0];
                                           String seqName = k.split(":")[1];
                                           log.debug("============> I am({}) hungry ...", k);
                                           fillQueue(v, appName, seqName, _default_queue_size - v.size());

                                       }

                                       Thread.sleep(10);

                                   } catch (Throwable e) {
                                       log.error(e.getMessage(),e);
                                   }
                               }

                            });

                            seqScannerThread.put(k, thread);

                            thread.start();

                            log.info("启动序列->{} 的饥饿扫描线程", k);
                        }

                    });

                } catch (Throwable t1) {
                    log.error(t1.getMessage(), t1);
                }

            }

        }

    }

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0;i<1;i++) {

            SequenceFactory sequenceFactory = SequenceFactory.getInstance("http://127.0.0.1:9099/dsequence/api/v1/sequence");
            System.out.println(sequenceFactory.getSequence("SHUZHEN","TEST_ID"));
            new Thread(()->{

                long started = System.currentTimeMillis();

                for (int times = 0;times < 1000000;times++) {
                    System.out.println(sequenceFactory.getSequence("SHUZHEN","TEST_ID"));
                }

                System.out.println("spend times "+ (System.currentTimeMillis() - started));

            }).start();

        }

        Thread.sleep(5000);

    }


}
