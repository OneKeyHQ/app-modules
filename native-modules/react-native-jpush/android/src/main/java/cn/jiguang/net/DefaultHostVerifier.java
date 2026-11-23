package cn.jiguang.net;

import java.security.cert.Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

public class DefaultHostVerifier implements HostnameVerifier {
    public String expectedHost;

    public DefaultHostVerifier(String expectedHost) {
        this.expectedHost = expectedHost;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (hostname == null || hostname.isEmpty() || session == null) {
            return false;
        }
        String targetHost = (this.expectedHost != null && !this.expectedHost.isEmpty())
            ? this.expectedHost
            : hostname;
        if (!targetHost.equalsIgnoreCase(hostname)) {
            return false;
        }
        try {
            Certificate[] peerCertificates = session.getPeerCertificates();
            if (peerCertificates == null || peerCertificates.length == 0) {
                return false;
            }
        } catch (SSLPeerUnverifiedException e) {
            return false;
        } catch (Throwable ignored) {
            return false;
        }
        HostnameVerifier defaultVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
        return defaultVerifier != null && defaultVerifier != this && defaultVerifier.verify(targetHost, session);
    }
}
