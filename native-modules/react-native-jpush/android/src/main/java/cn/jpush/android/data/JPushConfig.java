package cn.jpush.android.data;

import java.io.Serializable;

public class JPushConfig implements Serializable {
    private static final long serialVersionUID = -3135447319267244288L;
    private String jAppKey;
    private String oppoAppKey;
    private String oppoAppId;
    private String oppoAppSecret;
    private String mzAppId;
    private String mzAppKey;
    private String xmAppId;
    private String xmAppKey;

    public String getjAppKey() {
        return jAppKey;
    }

    public void setjAppKey(String jAppKey) {
        this.jAppKey = jAppKey;
    }

    public String getOppoAppKey() {
        return oppoAppKey;
    }

    public void setOppoAppKey(String oppoAppKey) {
        this.oppoAppKey = oppoAppKey;
    }

    public String getOppoAppId() {
        return oppoAppId;
    }

    public void setOppoAppId(String oppoAppId) {
        this.oppoAppId = oppoAppId;
    }

    public String getOppoAppSecret() {
        return oppoAppSecret;
    }

    public void setOppoAppSecret(String oppoAppSecret) {
        this.oppoAppSecret = oppoAppSecret;
    }

    public String getMzAppId() {
        return mzAppId;
    }

    public void setMzAppId(String mzAppId) {
        this.mzAppId = mzAppId;
    }

    public String getMzAppKey() {
        return mzAppKey;
    }

    public void setMzAppKey(String mzAppKey) {
        this.mzAppKey = mzAppKey;
    }

    public String getXmAppId() {
        return xmAppId;
    }

    public void setXmAppId(String xmAppId) {
        this.xmAppId = xmAppId;
    }

    public String getXmAppKey() {
        return xmAppKey;
    }

    public void setXmAppKey(String xmAppKey) {
        this.xmAppKey = xmAppKey;
    }
}
