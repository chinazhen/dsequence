package com.github.dsequence.client.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpClient 常用工具类
 *
 * @author ShuZhen(talentshu@163.com)
 * @version 1.0.0 createTime: 2017/3/1
 */
@Slf4j
public class HttpClientUtil {

    private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(30000)
            .setConnectTimeout(30000)
            .setConnectionRequestTimeout(30000)
            .build();

    private static final String _default_charset = "UTF-8";

    private static HttpClientUtil instance = null;

    private HttpClientUtil(){}

    public static HttpClientUtil getInstance(){
        if (instance == null) {
            instance = new HttpClientUtil();
        }
        return instance;
    }

    /**
     * 发送 post请求
     *
     * @param httpUrl       地址
     * @param maps          参数
     */
    public String sendHttpPost(String httpUrl, Map<String, String> maps) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            List<NameValuePair> nameValuePairs = maps.keySet().stream().map(key -> new BasicNameValuePair(key, maps.get(key))).collect(Collectors.toList());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, _default_charset));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return sendHttpPost(httpPost);
    }

    /**
     * 发送Post请求
     */
    private String sendHttpPost(HttpPost httpPost) {
        return send(httpPost);
    }

    private String send(HttpRequestBase http) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpEntity entity;
        String responseContent = null;
        try {
            // 创建默认的httpClient实例.
            httpClient = HttpClients.createDefault();
            http.setConfig(requestConfig);
            // 执行请求
            response = httpClient.execute(http);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, _default_charset);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            close(response,httpClient);
        }
        return responseContent;
    }

    private static void close(CloseableHttpResponse response,CloseableHttpClient httpClient) {
        try {
            if (response != null) {
                response.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

}
