package com.android.fyf.sdk.common.toolbox;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

public class URLParser {

    private String url;

    private String path;

    private Map<String, String> paramMap = new HashMap<String, String>();

    public URLParser(String url) {
        this.url = url;

        initUrl();
    }

    private void initUrl() {
        if (StringUtils.isBlank(url)) {
            return;
        }

        this.paramMap.clear();

        int index = this.url.indexOf("?");
        if (index != -1) {
            // 包含参数的url
            this.path = this.url.substring(0, index);
            String paramString = this.url.substring(index + 1);
            if (StringUtils.isNotBlank(paramString)) {
                String[] paramArray = paramString.split("&");
                for (int i = 0; i < paramArray.length; i++) {
                    if (StringUtils.isNotBlank(paramArray[i])) {
                        String[] params = paramArray[i].split("=");
                        if (params.length > 1) {
                            paramMap.put(params[0], params[1]);
                        }
                    }
                }
            }
        } else {
            // 不包含参数的url
            this.path = url;
        }
    }

    private String getUrlWithQueryString() {
        String tmpUrl = this.path;
        // 此处必须用isEmpty
        if (StringUtils.isEmpty(tmpUrl)) {
            return "";
        }
        if (!MapUtils.isEmpty(this.paramMap)) {
            for (Map.Entry<String, String> entry : this.paramMap.entrySet()) {
                String paramString = entry.getKey() + "=" + entry.getValue();
                if (tmpUrl.indexOf("?") == -1) {
                    tmpUrl += "?" + paramString;
                } else {
                    if (tmpUrl.endsWith("&")) {
                        tmpUrl += paramString;
                    } else {
                        tmpUrl += "&" + paramString;
                    }
                }
            }
        }

        return tmpUrl;
    }

    public void addParamMap(Map<String, String> map) {
        for (Map.Entry<String, String> entry : this.paramMap.entrySet()) {
            addParam(entry.getKey(), entry.getValue());
        }
    }

    public void addParam(String key, long value) {
        String stringValue = String.valueOf(value);
        addParam(key, stringValue);
    }

    public void addParam(String key, float value) {
        String stringValue = String.valueOf(value);
        addParam(key, stringValue);
    }

    public void addParam(String key, double value) {
        String stringValue = String.valueOf(value);
        addParam(key, stringValue);
    }

    public void addParam(String key, int value) {
        String stringValue = String.valueOf(value);
        addParam(key, stringValue);
    }

    public void addParam(String key, boolean value) {
        String stringValue = String.valueOf(value);
        addParam(key, stringValue);
    }

    public void addParam(String key, String value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        this.paramMap.put(key, TextUtils.isEmpty(value) ? "" : value);
    }

    public String getParamValue(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }

        if (MapUtils.isEmpty(this.paramMap)) {
            return null;
        }

        return this.paramMap.get(key);
    }

    public String getOriginalUrl() {
        return url;
    }

    /**
     * 获取path和当前参数拼接的URL
     * 
     * @return
     */
    public String getUrl() {
        return getUrlWithQueryString();
    }

    /**
     * 获取path
     * 
     * <pre>
     * url:www.sohu.com?a=1&b=2
     * path:www.sohu.com
     * </pre>
     * 
     * @return
     */
    public String getPath() {
        return path;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

}
