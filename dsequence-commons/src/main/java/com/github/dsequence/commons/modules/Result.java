package com.github.dsequence.commons.modules;

import java.io.Serializable;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 简介
 *
 * 接口统一返回结果
 * 使用isSuccess判断调用是否成功 ,如果为true,则可以调用getResult,如果为false,则调用errorCode来获取出错信息
 * <p>
 * 1、isSuccess         判断调用是否成功
 * 2、getResult         获取调用结果集
 * 3、setResult         设置调用结果集
 * 4、getErrorCode      获取错误码
 * 5、setErrorCode      设置错误码
 * 6、getErrorMsg       获取错误描述
 * 7、setErrorMsg       设置错误描述
 * </p>
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@NoArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 515035262795920236L;

    /**
     * 接口调用状态-如果为true,则可以调用getResult,如果为false,则调用errorCode来获取出错信息
     */
    @Getter
    @Setter
    private boolean isSuccess;

    /**
     * 调用返回值-泛型
     */
    private T result;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误描述
     */
    private String errorMsg;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        this.isSuccess = success;
    }

    /**
     * 默认构造方法<p/>
     * 设置返回结果，则默认接口调用成功
     *
     * @param result 调用返回值
     */
    public Result(T result) {
        this.isSuccess = true;
        this.result = result;
    }

    /**
     * 构造方法，根据flag返回不同结果
     *
     * @param flag   接口调用状态
     * @param result 调用返回值
     */
    public Result(boolean flag, T result) {
        if (flag) {
            this.isSuccess = true;
            this.result = result;
        } else {
            this.isSuccess = false;
            this.errorCode = (String) result;
        }
    }

    /**
     * 构造方法，接口调用失败，设置错误描述
     *
     * @param errorCode 错误描述
     */
    public Result(String errorCode) {
        this.isSuccess = false;
        this.errorCode = errorCode;
    }

    /**
     * 构造方法，设置错误信息
     *
     * @param errorCode 错误码
     * @param errorMsg  错误描述
     */
    public Result(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public void setResult(T result) {
        this.result = result;
        this.isSuccess = true;
    }

    public T getResult() {
        return result;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
        this.isSuccess = false;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        this.isSuccess = false;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result response = (Result) o;

        return isSuccess == response.isSuccess
                && result.equals(response.result)
                && errorCode.equals(response.errorCode);

    }

    @Override
    public int hashCode() {
        int result1 = (isSuccess ? 1 : 0);
        result1 = 31 * result1 + result.hashCode();
        result1 = 31 * result1 + errorCode.hashCode();
        return result1;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("isSuccess", isSuccess)
                .add("result", result)
                .add("errorCode", errorCode)
                .add("errorMsg", errorMsg)
                .omitNullValues()
                .toString();
    }

}
