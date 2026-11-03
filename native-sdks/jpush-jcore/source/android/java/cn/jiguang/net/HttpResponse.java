package cn.jiguang.net;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
   private String recoverField_a;
   private String recoverField_b;
   private Map recoverField_c;
   private int recoverField_d;
   private long recoverField_e;
   private boolean recoverField_f;
   private boolean recoverField_g;
   private int recoverField_h = -1;
   private int recoverField_i = -1;

   public HttpResponse(String var1) {
      this.recoverField_a = var1;
      this.recoverField_d = 0;
      this.recoverField_f = false;
      this.recoverField_g = false;
      this.recoverField_c = new HashMap();
   }

   public HttpResponse() {
      this.recoverField_c = new HashMap();
   }

   public String getUrl() {
      return this.recoverField_a;
   }

   public void setUrl(String var1) {
      this.recoverField_a = var1;
   }

   public String getResponseBody() {
      return this.recoverField_b;
   }

   public void setResponseBody(String var1) {
      this.recoverField_b = var1;
   }

   public int getResponseCode() {
      return this.recoverField_h;
   }

   public void setResponseCode(int var1) {
      this.recoverField_h = var1;
   }

   public int getStatusCode() {
      return this.recoverField_i;
   }

   public void setStatusCode(int var1) {
      this.recoverField_i = var1;
   }

   public Map getResponseHeaders() {
      return this.recoverField_c;
   }

   public void setResponseHeaders(Map var1) {
      this.recoverField_c = var1;
   }

   public int getType() {
      return this.recoverField_d;
   }

   public void setType(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("The type of HttpResponse cannot be smaller than 0.");
      }

      this.recoverField_d = var1;
   }

   public void setExpiredTime(long var1) {
      this.recoverField_g = true;
      this.recoverField_e = var1;
   }

   public long getExpiredTime() {
      if (this.recoverField_g) {
         return this.recoverField_e;
      }

      this.recoverField_g = true;
      return this.recoverField_e = this.recoverMethod_b_9b69();
   }

   public boolean isExpired() {
      return System.currentTimeMillis() > this.recoverField_e;
   }

   public boolean isInCache() {
      return this.recoverField_f;
   }

   public HttpResponse setInCache(boolean var1) {
      this.recoverField_f = var1;
      return this;
   }

   public String getExpiresHeader() {
      try {
         return this.recoverField_c == null ? null : (String)this.recoverField_c.get("expires");
      } catch (Exception var2) {
         return null;
      }
   }

   private int recoverMethod_a_9b68() {
      try {
         String var1 = (String)this.recoverField_c.get("cache-control");
         if (!TextUtils.isEmpty(var1)) {
            int var2 = var1.indexOf("max-age=");
            if (var2 != -1) {
               int var3 = var1.indexOf(",", var2);
               String var4;
               if (var3 != -1) {
                  var4 = var1.substring(var2 + "max-age=".length(), var3);
               } else {
                  var4 = var1.substring(var2 + "max-age=".length());
               }

               return Integer.parseInt(var4);
            }
         }

         return -1;
      } catch (Exception var5) {
         return -1;
      }
   }

   private long recoverMethod_b_9b69() {
      int var1 = this.recoverMethod_a_9b68();
      if (var1 != -1) {
         return System.currentTimeMillis() + var1 * 1000;
      }

      String var2 = this.getExpiresHeader();
      return !TextUtils.isEmpty(var2) ? HttpUtils.parseGmtTime(this.getExpiresHeader()) : -1L;
   }

   public void setResponseHeader(String var1, String var2) {
      if (this.recoverField_c != null) {
         this.recoverField_c.put(var1, var2);
      }
   }

   @Override
   public String toString() {
      return "HttpResponse{responseBody='" + this.recoverField_b + '\'' + ", responseCode=" + this.recoverField_h + '}';
   }
}
