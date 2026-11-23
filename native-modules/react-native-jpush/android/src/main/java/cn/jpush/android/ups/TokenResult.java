package cn.jpush.android.ups;

public class TokenResult {
    private String token;
    private int returnCode;
    private String actionType;

    public TokenResult(String token, int returnCode) {
        this.token = token;
        this.returnCode = returnCode;
    }

    public TokenResult(String token, int returnCode, String actionType) {
        this.token = token;
        this.returnCode = returnCode;
        this.actionType = actionType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public String toString() {
        return "TokenResult{token='" + token + "', returnCode=" + returnCode + ", actionType='" + actionType + "'}";
    }
}
