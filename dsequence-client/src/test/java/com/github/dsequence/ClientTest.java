package com.github.dsequence;

import com.github.dsequence.client.SequenceFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 简介
 *
 * @author shuzhen(mu.zhi)
 * @version 5.0 createTime: 2017/4/24
 */
@Slf4j
public class ClientTest {

    @Test
    public void getInstance() {
        SequenceFactory sequenceFactory = SequenceFactory.getInstance("http://127.0.0.1:9099/dsequence/api/v1/sequence");
        log.info("Test call getInstance :{}", sequenceFactory.getSequence("SHUZHEN","TEST_ID"));
    }
}
