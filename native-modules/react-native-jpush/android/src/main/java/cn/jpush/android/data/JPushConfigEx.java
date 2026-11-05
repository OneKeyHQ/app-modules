package cn.jpush.android.data;

public class JPushConfigEx extends JPushConfig {
    private static final long serialVersionUID = 1L;
    private String hwAppId;
    private String honorAppId;
    private String vivoAppId;
    private String vivoAppKey;

    public String getHwAppId() {
        return hwAppId;
    }

    public void setHwAppId(String hwAppId) {
        this.hwAppId = hwAppId;
    }

    public String getHonorAppId() {
        return honorAppId;
    }

    public void setHonorAppId(String honorAppId) {
        this.honorAppId = honorAppId;
    }

    public String getVivoAppId() {
        return vivoAppId;
    }

    public void setVivoAppId(String vivoAppId) {
        this.vivoAppId = vivoAppId;
    }

    public String getVivoAppKey() {
        return vivoAppKey;
    }

    public void setVivoAppKey(String vivoAppKey) {
        this.vivoAppKey = vivoAppKey;
    }
}
