package com.github.dsequence.server.service;

import com.github.dsequence.commons.modules.*;
import com.github.dsequence.server.facade.SequenceFacade;
import com.github.dsequence.server.manager.*;
import com.github.dsequence.server.core.ErrorCodeEnum;
import com.github.dsequence.server.core.SequenceException;
import com.github.dsequence.server.dal.SequenceInfoDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 序列号服务
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Slf4j
@Service
public class SequenceFacadeImpl implements SequenceFacade {

    /**
     * 获取序列管理服务
     */
    @Autowired
    private GetSequenceManager getSequenceManager;

    /**
     * 查询序列管理服务
     */
    @Autowired
    private QuerySequenceManager querySequenceManager;

    /**
     * 创建序列管理服务
     */
    @Autowired
    private CreateSequenceManager createSequenceManager;

    /**
     * 删除序列管理服务
     */
    @Autowired
    private DeleteSequenceManager deleteSequenceManager;

    /**
     * 查询序列信息
     *
     * @param   reqDTO      查询序列信息接口请求参数（应用名称,序列名称）
     * @return              一批sequence（起始,结束）
     */
    @Override
    public Result<List<QrySeqResDTO>> querySequences(QrySeqReqDTO reqDTO) {

        log.info("Call 查询序列信息 请求参数:{}",reqDTO);

        Result<List<QrySeqResDTO>> result = new Result<>();

        long started = System.currentTimeMillis();

        try {

            if (StringUtils.isBlank(reqDTO.getAppName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"应用名不能为空");
            }

            List<SequenceInfoDO> list =
                    querySequenceManager.queryList(reqDTO.getAppName(),reqDTO.getSeqName());

            List<QrySeqResDTO> response = new ArrayList<>();

            if (!list.isEmpty()) {
                list.forEach(o->{
                    QrySeqResDTO resDTO = new QrySeqResDTO();
                    resDTO.setId(o.getId());
                    resDTO.setAppName(o.getAppName());
                    resDTO.setSeqName(o.getSeqName());
                    resDTO.setLastSeq(o.getLastSeq());
                    resDTO.setNextSeq(o.getNextSeq());
                    resDTO.setMaxSeq(o.getMaxSeq());
                    resDTO.setCreatedAt(o.getCreatedAt());
                    resDTO.setCreatedBy(o.getCreatedBy());
                    resDTO.setUpdatedBy(o.getUpdatedBy());
                    resDTO.setUpdatedAt(o.getUpdatedAt());
                    response.add(resDTO);
                });
            }

            result.setResult(response);

        } catch (SequenceException t) {
            result.setErrorCode(t.getCode());
            result.setErrorMsg(t.getMessage());
            log.error(t.getMessage(),t);
        } catch (Throwable t) {
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getMessage());
            log.error(t.getMessage(),t);
        }

        log.info("Call 查询序列信息 耗时:{} 请求参数:{} 响应结果:{}",
                System.currentTimeMillis() - started, reqDTO,result);

        return result;
    }

    /**
     * 获取sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              一批sequence（起始,结束）
     */
    @Override
    public Result<GetSeqResDTO> getBatchSequence(GetSeqReqDTO reqDTO) {

        log.info("Call 获取sequence 请求参数:{}",reqDTO);

        Result<GetSeqResDTO> result = new Result<>();

        long started = System.currentTimeMillis();

        try {

            if (StringUtils.isBlank(reqDTO.getAppName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"应用名不能为空");
            }
            if (StringUtils.isBlank(reqDTO.getSeqName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"序列名不能为空");
            }
            if (reqDTO.getLimit() <= 0) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"获取数量必须大于0");
            }

            SequenceInfoDO sequenceInfoDO =
                    getSequenceManager.getSequence(reqDTO.getAppName(),reqDTO.getSeqName(),reqDTO.getLimit());

            GetSeqResDTO response = new GetSeqResDTO();
            response.setSeqEnd(sequenceInfoDO.getNextSeq());
            response.setSeqBegin(sequenceInfoDO.getLastSeq());
            response.setMaxSeq(sequenceInfoDO.getMaxSeq());

            result.setResult(response);

        } catch (SequenceException t) {
            result.setErrorCode(t.getCode());
            result.setErrorMsg(t.getMessage());
            log.error(t.getMessage(),t);
        } catch (Throwable t) {
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getMessage());
            log.error(t.getMessage(),t);
        }

        log.info("Call 获取sequence 耗时: {} 请求参数:{} 响应结果:{}",
                System.currentTimeMillis() - started, reqDTO,result);

        return result;
    }

    /**
     * 创建sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              状态
     */
    @Override
    public Result<Boolean> createBatchSequence(CreSeqReqDTO reqDTO) {

        log.info("Call 创建sequence 请求参数:{}",reqDTO);

        Result<Boolean> result = new Result<>();

        long started = System.currentTimeMillis();

        try {

            if (StringUtils.isBlank(reqDTO.getAppName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"应用名不能为空");
            }

            if (StringUtils.isBlank(reqDTO.getSeqName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"序列名不能为空");
            }

            if (StringUtils.isBlank(reqDTO.getCreatedBy())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"创建人不能为空");
            }

            if (null == reqDTO.getMaxSeq()) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"最大序列不能为null");
            }

            if (Objects.nonNull(reqDTO.getNextSeq()) && reqDTO.getNextSeq() > SequenceManager._default_max_seq) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"初始序列不能大于最大值");
            }

            if (Objects.nonNull(reqDTO.getNextSeq()) && reqDTO.getNextSeq() <= 0) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"初始序列不能小于1");
            }

            if (SequenceManager._default_min_seq > reqDTO.getMaxSeq()) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"最大序列不能小于"+SequenceManager._default_min_seq);
            }

            createSequenceManager.createSequence(reqDTO);

            result.setResult(Boolean.TRUE);

        } catch (SequenceException t) {
            result.setErrorCode(t.getCode());
            result.setErrorMsg(t.getMessage());
            log.error(t.getMessage(),t);
        } catch (Throwable t) {
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getMessage());
            log.error(t.getMessage(),t);
        }

        log.info("Call 创建sequence 耗时: {} 请求参数:{} 响应结果:{}",
                System.currentTimeMillis() - started, reqDTO,result);

        return result;
    }

    /**
     * 删除sequence
     *
     * @param   reqDTO      请求信息封装（应用名称,序列名称）
     * @return              状态
     */
    @Override
    public Result<Boolean> deleteBatchSequence(DeleteSeqReqDTO reqDTO) {

        log.info("Call 删除sequence 请求参数:{}",reqDTO);

        Result<Boolean> result = new Result<>();

        long started = System.currentTimeMillis();

        try {

            if (StringUtils.isBlank(reqDTO.getAppName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"应用名不能为空");
            }

            if (StringUtils.isBlank(reqDTO.getSeqName())) {
                throw new SequenceException(ErrorCodeEnum.PARAMETER_INVALID,"序列名不能为空");
            }

            deleteSequenceManager.deleteSequence(reqDTO);

            result.setResult(Boolean.TRUE);

        } catch (SequenceException t) {
            result.setErrorCode(t.getCode());
            result.setErrorMsg(t.getMessage());
            log.error(t.getMessage(),t);
        } catch (Throwable t) {
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getCode());
            result.setErrorCode(ErrorCodeEnum.SYSTEM_ERROR.getMessage());
            log.error(t.getMessage(),t);
        }

        log.info("Call 创建sequence 耗时: {} 请求参数:{} 响应结果:{}",
                System.currentTimeMillis() - started, reqDTO,result);

        return result;

    }

}
