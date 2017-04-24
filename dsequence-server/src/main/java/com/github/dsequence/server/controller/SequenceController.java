package com.github.dsequence.server.controller;

import com.github.dsequence.commons.modules.*;
import com.github.dsequence.server.ApiUrls;
import com.github.dsequence.server.facade.SequenceFacade;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 序列服务controller
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2016/10/28
 */
@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SequenceController {

    @Autowired
    private SequenceFacade sequenceFacadeImpl;

    @RequestMapping(value = ApiUrls.CRE_SEQUENCE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "创建序列")
    public Result<Boolean> create(@ApiParam(value = "应用名称") @RequestParam("appName") String appName,
                                  @ApiParam(value = "序列名称") @RequestParam("seqName") String seqName,
                                  @ApiParam(value = "最大序列") @RequestParam("maxSize") Long maxSize,
                                  @ApiParam(value = "初始序列") @RequestParam(name = "nextSeq",required = false) Long nextSeq,
                                  @ApiParam(value = "创建人") @RequestParam("createdBy") String createdBy
                                  ){


        return sequenceFacadeImpl.createBatchSequence(new CreSeqReqDTO(appName,seqName,nextSeq,maxSize,createdBy));

    }

    @RequestMapping(value = ApiUrls.DEL_SEQUENCE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "删除序列")
    public Result<Boolean> delete(@ApiParam(value = "应用名称") @RequestParam("appName") String appName,
                                  @ApiParam(value = "序列名称") @RequestParam("seqName") String seqName
                                  ){


        return sequenceFacadeImpl.deleteBatchSequence(new DeleteSeqReqDTO(appName,seqName));

    }

    @RequestMapping(value = ApiUrls.GET_SEQUENCE, method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "获取序列")
    public Result<GetSeqResDTO> get(@ApiParam(value = "应用名称") @RequestParam("appName") String appName,
                                    @ApiParam(value = "序列名称") @RequestParam("seqName") String seqName,
                                    @ApiParam(value = "批次大小") @RequestParam("limit") Integer limit
                                  ){

        return sequenceFacadeImpl.getBatchSequence(new GetSeqReqDTO(appName,seqName,limit));

    }

    @RequestMapping(value = ApiUrls.QRY_SEQUENCE,method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "查询序列信息")
    public Result<List<QrySeqResDTO>> query(@ApiParam(value = "应用名称") @RequestParam("appName") String appName,
                                            @ApiParam(value = "序列名称") @RequestParam(name = "seqName",required = false) String seqName
                                  ){

        return sequenceFacadeImpl.querySequences(new QrySeqReqDTO(appName,seqName));

    }

}
