package cn.jiguang.api;

public interface ReportCallBack {
   int DELIVERED = 1;
   int recoverField_OK = 0;
   int FAILED = -1;
   int ERROR = -2;

   void onFinish(int var1);
}
