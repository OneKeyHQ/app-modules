package cn.jiguang.net;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String url;
    private String responseBody;
    private int responseCode = -1;
    private Map<String, Object> responseHeaders = new HashMap<String, Object>();

    public HttpResponse() {
    }

    public HttpResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setResponseHeader(String key, String value) {
        if (key != null) {
            responseHeaders.put(key, value);
        }
    }
}
