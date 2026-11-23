package cn.jiguang.net;

import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpRequest {
    private String url;
    private int connectTimeout = 10000;
    private int readTimeout = 10000;
    private Map<String, String> requestProperties = new HashMap<String, String>();
    private Object body;
    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;

    public HttpRequest(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Map<String, String> getRequestProperties() {
        return requestProperties;
    }

    public void setRequestProperty(String key, String value) {
        if (key != null && value != null) {
            requestProperties.put(key, value);
        }
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }

    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }
}
