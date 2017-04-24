package com.github.dsequence.server;

import com.github.dsequence.BaseSpringTest;
import com.github.dsequence.commons.modules.CreSeqReqDTO;
import com.github.dsequence.commons.modules.GetSeqReqDTO;
import com.github.dsequence.commons.modules.GetSeqResDTO;
import com.github.dsequence.commons.modules.Result;
import com.github.dsequence.server.facade.SequenceFacade;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 简介
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Slf4j
public class SequenceTest extends BaseSpringTest {

    @Autowired
    private SequenceFacade sequenceFacadeImpl;

    @Test
    public void createSequence(){

        CreSeqReqDTO reqDTO = new CreSeqReqDTO();
        reqDTO.setMaxSeq(9999999L);
        reqDTO.setAppName("XIEYANKE");
        reqDTO.setSeqName("BAL_DETAIL_ID");
        reqDTO.setCreatedBy("舒蓁");

        Result<Boolean> result = sequenceFacadeImpl.createBatchSequence(reqDTO);

        assert result.isSuccess();

    }

    @Test
    public void getSequence() {

        GetSeqReqDTO reqDTO = new GetSeqReqDTO();
        reqDTO.setAppName("XIEYANKE");
        reqDTO.setSeqName("BAL_DETAIL_ID");
        reqDTO.setLimit(100);

        for (int i=0;i<50;i++) {
            Result<GetSeqResDTO> result = sequenceFacadeImpl.getBatchSequence(reqDTO);
            log.info("num:{},result:{}", i, result);
            /*try {
                Thread.sleep(30L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        assert result.getSuccess();
    }

}
