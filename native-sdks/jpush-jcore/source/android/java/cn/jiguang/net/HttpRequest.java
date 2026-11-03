package cn.jiguang.net;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.X509TrustManager;

public class HttpRequest {
   private String recoverField_a;
   private String recoverField_b;
   private int recoverField_c;
   private int recoverField_d;
   private Map recoverField_e;
   private Map recoverField_f;
   private Object recoverField_g;
   private boolean recoverField_h;
   private boolean recoverField_i;
   private boolean recoverField_j;
   private boolean recoverField_k = true;
   private boolean recoverField_l = false;
   private boolean recoverField_m = false;
   private X509TrustManager recoverField_n;
   private boolean recoverField_o;
   private HostnameVerifier recoverField_p;

   public HttpRequest(String var1) {
      this.recoverField_a = var1;
      this.recoverField_c = -1;
      this.recoverField_d = -1;
      this.recoverField_f = new HashMap();
   }

   public HttpRequest(String var1, Map var2) {
      this.recoverField_a = var1;
      this.recoverField_e = var2;
      this.recoverField_c = -1;
      this.recoverField_d = -1;
      this.recoverField_f = new HashMap();
   }

   public String getUrl() {
      return this.recoverField_a;
   }

   public int getConnectTimeout() {
      return this.recoverField_c;
   }

   public void setConnectTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeout can not be negative");
      }

      this.recoverField_c = var1;
   }

   public int getReadTimeout() {
      return this.recoverField_d;
   }

   public void setReadTimeout(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("timeout can not be negative");
      }

      this.recoverField_d = var1;
   }

   public void setUrl(String var1) {
      this.recoverField_a = var1;
   }

   public Object getBody() {
      return this.recoverField_g;
   }

   public void setBody(Object var1) {
      this.recoverField_g = var1;
   }

   public boolean isDoOutPut() {
      return this.recoverField_h;
   }

   public void setDoOutPut(boolean var1) {
      this.recoverField_h = var1;
   }

   public boolean isDoInPut() {
      return this.recoverField_i;
   }

   public void setDoInPut(boolean var1) {
      this.recoverField_i = var1;
   }

   public boolean isUseCaches() {
      return this.recoverField_j;
   }

   public void setUseCaches(boolean var1) {
      this.recoverField_j = var1;
   }

   public boolean isNeedErrorInput() {
      return this.recoverField_m;
   }

   public void setNeedErrorInput(boolean var1) {
      this.recoverField_m = var1;
   }

   public X509TrustManager getSslTrustManager() {
      return this.recoverField_n;
   }

   public void setSslTrustManager(X509TrustManager var1) {
      this.recoverField_n = var1;
   }

   public boolean isNeedRetryIfHttpsFailed() {
      return this.recoverField_o;
   }

   public void setNeedRetryIfHttpsFailed(boolean var1) {
      this.recoverField_o = var1;
   }

   public HostnameVerifier getHostnameVerifier() {
      return this.recoverField_p;
   }

   public void setHostnameVerifier(HostnameVerifier var1) {
      this.recoverField_p = var1;
   }

   public Map getParasMap() {
      return this.recoverField_e;
   }

   public void setParasMap(Map var1) {
      this.recoverField_e = var1;
   }

   public byte[] getParas() {
      if (this.recoverField_g != null) {
         if (this.recoverField_g instanceof String) {
            if (!TextUtils.isEmpty((CharSequence)this.recoverField_g)) {
               return ((String)this.recoverField_g).getBytes();
            }
         } else if (this.recoverField_g instanceof byte[]) {
            return (byte[])this.recoverField_g;
         }
      }

      String var1 = HttpUtils.joinParasWithEncodedValue(this.recoverField_e);
      return !TextUtils.isEmpty(var1) ? var1.getBytes() : null;
   }

   public void setRequestProperty(String var1, String var2) {
      this.recoverField_f.put(var1, var2);
   }

   public String getRequestProperty(String var1) {
      return (String)this.recoverField_f.get(var1);
   }

   public void setUserAgent(String var1) {
      this.recoverField_f.put("User-Agent", var1);
   }

   public Map getRequestProperties() {
      return this.recoverField_f;
   }

   public void setRequestProperties(Map var1) {
      this.recoverField_f = var1;
   }

   public boolean isHaveRspData() {
      return this.recoverField_k;
   }

   public void setHaveRspData(boolean var1) {
      this.recoverField_k = var1;
   }

   public boolean isRspDatazip() {
      return this.recoverField_l;
   }

   public void setRspDatazip(boolean var1) {
      this.recoverField_l = var1;
   }

   public String getType() {
      return this.recoverField_b;
   }

   public void setType(String var1) {
      this.recoverField_b = var1;
   }
}
