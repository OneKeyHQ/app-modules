package cn.jiguang.net;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

public class HttpUtils {
    public static HttpResponse httpGet(Context context, String url) {
        return httpGet(context, new HttpRequest(url));
    }

    public static HttpResponse httpGet(Context context, HttpRequest request) {
        return execute(context, request, false);
    }

    public static HttpResponse httpPost(Context context, String url, String body) {
        HttpRequest req = new HttpRequest(url);
        req.setBody(body);
        return httpPost(context, req);
    }

    public static HttpResponse httpPost(Context context, HttpRequest request) {
        return execute(context, request, true);
    }

    private static HttpResponse execute(Context context, HttpRequest request, boolean isPost) {
        HttpResponse response = new HttpResponse(request != null ? request.getUrl() : "");
        if (request == null || request.getUrl() == null) {
            return response;
        }
        HttpURLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            URL url = new URL(request.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(request.getConnectTimeout());
            conn.setReadTimeout(request.getReadTimeout());
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection https = (HttpsURLConnection) conn;
                if (request.getSslSocketFactory() != null) {
                    https.setSSLSocketFactory(request.getSslSocketFactory());
                }
                https.setHostnameVerifier(
                    request.getHostnameVerifier() != null
                        ? request.getHostnameVerifier()
                        : new DefaultHostVerifier(url.getHost())
                );
            }
            for (Map.Entry<String, String> entry : request.getRequestProperties().entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if (isPost) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                Object body = request.getBody();
                byte[] bytes = null;
                if (body instanceof byte[]) {
                    bytes = (byte[]) body;
                } else if (body instanceof String) {
                    bytes = ((String) body).getBytes(StandardCharsets.UTF_8);
                }
                if (bytes != null) {
                    out = conn.getOutputStream();
                    out.write(bytes);
                    out.flush();
                }
            } else {
                conn.setRequestMethod("GET");
            }
            int code = conn.getResponseCode();
            response.setResponseCode(code);
            in = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
            if (in != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] chunk = new byte[2048];
                int read;
                while ((read = in.read(chunk)) != -1) {
                    buffer.write(chunk, 0, read);
                }
                response.setResponseBody(new String(buffer.toByteArray(), StandardCharsets.UTF_8));
            }
        } catch (Throwable ignored) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Throwable ignored) {
            }
        }
        return response;
    }
}
