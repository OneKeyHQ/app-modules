package cn.jiguang.api;

import java.util.Map;

public class HttpResponse {
   public int statusCode;
   public Map headers;
   public byte[] body;

   public HttpResponse(int var1, Map var2, byte[] var3) {
      this.statusCode = var1;
      this.headers = var2;
      this.body = var3;
   }
}
