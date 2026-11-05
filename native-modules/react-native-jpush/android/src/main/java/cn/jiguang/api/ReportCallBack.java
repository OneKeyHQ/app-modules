package cn.jiguang.api;

public interface ReportCallBack {
    int OK = 0;
    int FAILED = -1;
    int ERROR_NO_INIT = -2;

    void onFinish(int statusCode);
}
