package cn.jpush.android.w;

import android.content.Context;
import cn.jpush.android.ad.RecoverType9b4c5ae8;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.apache.http.conn.ConnectTimeoutException;

public class RecoverType2e4d157b {
   public static final String recoverField_a = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{32, 0, 14, 41, 38, 38, 63, 1, 23, 43});
   private static final String recoverField_b = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{62, 82, 38, 113, 19, 93, 73, 85, 84, 2, 23, 92, 58, 34, 40, 115, 19, 47, 78, 84, 32, 3, 19, 40, 57, 83, 32, 114, 23, 93, 49, 82}
   );
   private static HashMap recoverField_c = new HashMap();

   public static String recoverMethod_a_3c5a5db8(Context var0, String var1, String var2, int var3, long var4) {
      Logger.recoverMethod_d_e925fa09("HttpManager", "action:httpSimpleGet - " + var1);
      boolean var6 = JCoreHelper.isContinueBusiness(var0);
      Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk continueBusiness=" + var6);
      if (!var6) {
         Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk limit");
         return null;
      }

      if (var3 < 1 || var3 > 10) {
         var3 = 1;
      }

      if (var4 < 200L || var4 > 60000L) {
         var4 = 2000L;
      }

      int var7 = 0;
      int var8 = -1;
      String var9 = null;

      while (true) {
         try {
            label152: {
               HttpURLConnection var10 = null;
               InputStream var11 = null;

               try {
                  var10 = (HttpURLConnection)new URL(var1).openConnection();
                  var10.setRequestProperty("Connection", "Close");
                  var10.setRequestProperty("Accept-Encoding", "identity");
                  var8 = var10.getResponseCode();
                  Logger.recoverMethod_v_e925fa09("HttpManager", "statusCode:" + var8);
                  if (var8 != 200) {
                     break label152;
                  }

                  var11 = var10.getInputStream();
                  byte[] var12 = recoverMethod_a_b328ee5f(var11);
                  JCoreHelper.netMonitor(var0, var1, var2, 0L, var12.length);
                  var9 = new String(var12, "UTF-8");
               } catch (Throwable var27) {
                  Logger.recoverMethod_d_e925fa09("HttpManager", "httpSimpleGet failed,retriedCount:" + var7 + ",statusCode:" + var8);
                  break label152;
               } finally {
                  try {
                     if (var10 != null) {
                        var10.disconnect();
                     }
                  } catch (Throwable var25) {
                  }

                  RecoverType9b4c5af2.recoverMethod_a_d17885d6(var11);
               }

               return var9;
            }
         } catch (Exception var29) {
            Logger.recoverMethod_d_e925fa09("HttpManager", "http client execute error:" + var29.getMessage());
         } catch (AssertionError var30) {
            Logger.recoverMethod_e_e925fa09("HttpManager", "Catch AssertionError to avoid http close crash - " + var30.toString());
         }

         if (var7 >= var3) {
            return var9;
         }

         var7++;

         try {
            Thread.sleep(var4);
         } catch (InterruptedException var26) {
         }
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      return recoverField_c != null && recoverField_c.size() > 0 && recoverField_c.containsKey(var0) ? (String)recoverField_c.get(var0) : null;
   }

   public static void recoverMethod_b_aad3b1ff(String var0) {
      try {
         if (recoverField_c != null) {
            recoverField_c.remove(var0);
         }
      } catch (Throwable var2) {
      }
   }

   public static byte[] recoverMethod_a_87852596(Context var0, String var1, String var2, int var3, int var4) {
      return recoverMethod_a_69351af0(var0, var1, var2, var3, var4, false);
   }

   public static byte[] recoverMethod_a_69351af0(Context var0, String var1, String var2, int var3, int var4, boolean var5) {
      byte[] var6 = null;

      for (int var7 = 0; var7 < var3; var7++) {
         var6 = recoverMethod_a_878cdfe5(var0, var1, var2, var4, var5);
         if (var6 != null) {
            break;
         }
      }

      return var6;
   }

   private static byte[] recoverMethod_a_878cdfe5(Context var0, String var1, String var2, int var3, boolean var4) {
      byte[] var5 = null;
      Logger.recoverMethod_d_e925fa09("HttpManager", "action:httpGetIconBytes-" + var1);
      boolean var6 = JCoreHelper.isContinueBusiness(var0);
      Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk continueBusiness=" + var6);
      if (!var6) {
         Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk limit");
         return null;
      }

      HttpURLConnection var7 = null;
      InputStream var8 = null;
      URL var9 = null;
      int var10 = -1;
      int var11 = 0;
      int var12 = 30720;
      if (var3 > var12) {
         var12 = var3;
      }

      short var13 = 5000;

      try {
         var9 = new URL(var1);
         var7 = (HttpURLConnection)var9.openConnection();
         var7.setRequestProperty("Accept-Encoding", "identity");
         var7.addRequestProperty("Connection", "Close");
         var7.setConnectTimeout(30000);
         var7.setReadTimeout(var13);
         var10 = var7.getResponseCode();
         if (var10 == 200) {
            var11 = var7.getContentLength();
            var8 = var7.getInputStream();
            if (var8 != null) {
               var5 = recoverMethod_a_b328ee5f(var8);
            }

            JCoreHelper.netMonitor(var0, var1, var2, 0L, var5.length);
            if (var5 == null || var11 == 0) {
               Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - Unexpected: downloaded bytes content length is 0");
               return null;
            }

            if (var11 > var12) {
               Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - Unexpected:icon  size too big,max size is" + var12);
               return null;
            }

            if (var5.length < var11) {
               Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - Download bytes failed. Got bytes len < header content length.");
               return null;
            }
         } else if (400 == var10) {
            Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - server response failure - " + var1);
         } else if (404 == var10) {
            Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - Request path does not exist: 404 - " + var1);
         } else {
            Logger.recoverMethod_d_e925fa09("HttpManager", "httpGetIconBytes - Other wrong response status - " + var10 + ", url:" + var1);
         }
      } catch (ConnectTimeoutException | SocketTimeoutException var29) {
         if (var4) {
            recoverField_c.put(var1, "1000");
         }
      } catch (Throwable var30) {
         Logger.recoverMethod_w_e925fa09("HttpManager", "httpGetIconBytes failed:" + var30.getMessage());
      } finally {
         try {
            if (var8 != null) {
               var8.close();
            }

            if (var7 != null) {
               var7.disconnect();
            }
         } catch (Throwable var28) {
         }
      }

      return var5;
   }

   public static byte[] recoverMethod_a_693b7822(Context var0, String var1, String var2, int var3, long var4, int var6) {
      byte[] var7 = null;

      for (int var8 = 0; var8 < var6; var8++) {
         var7 = recoverMethod_b_878599f5(var0, var1, var2, var3, var4);
         if (var7 != null) {
            break;
         }
      }

      return var7;
   }

   public static byte[] recoverMethod_b_878599f5(Context var0, String var1, String var2, int var3, long var4) {
      boolean var6 = JCoreHelper.isContinueBusiness(var0);
      Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk continueBusiness=" + var6);
      if (!var6) {
         Logger.recoverMethod_w_e925fa09("HttpManager", " https sdk limit");
         return null;
      }

      if (var3 < 1 || var3 > 10) {
         var3 = 1;
      }

      if (var4 < 200L || var4 > 60000L) {
         var4 = 2000L;
      }

      Logger.recoverMethod_d_e925fa09("HttpManager", "action:httpGet - " + var1);
      HttpURLConnection var7 = null;
      InputStream var8 = null;
      URL var9 = null;
      byte[] var10 = null;
      int var11 = 0;
      int var12 = -1;
      int var13 = 0;

      while (true) {
         try {
            var9 = new URL(var1);
            var7 = (HttpURLConnection)var9.openConnection();
            var7.setRequestProperty("Accept-Encoding", "identity");
            var7.addRequestProperty("Connection", "Close");
            var12 = var7.getResponseCode();
            Logger.recoverMethod_v_e925fa09("HttpManager", "statusCode:" + var12);
            if (var12 == 200) {
               var13 = var7.getContentLength();
               var8 = var7.getInputStream();
               if (var8 != null) {
                  var10 = recoverMethod_a_b328ee5f(var8);
               }
               break;
            }
         } catch (SSLPeerUnverifiedException var33) {
            Logger.recoverMethod_ee_e925fa09("HttpManager", "Catch SSLPeerUnverifiedException, http client execute error!");
         } catch (SSLHandshakeException var34) {
            Logger.recoverMethod_dd_e925fa09("HttpManager", "Catch SSLHandshakeException, http client execute error!" + var34.getMessage());
            break;
         } catch (Exception var35) {
            Logger.recoverMethod_d_e925fa09("HttpManager", "http client execute error:" + var35.getMessage());
         } finally {
            if (var8 != null) {
               try {
                  var8.close();
               } catch (IOException var30) {
               }
            }

            if (var7 != null) {
               var7.disconnect();
            }
         }

         if (var11 >= var3) {
            return null;
         }

         var11++;

         try {
            Thread.sleep(var4 * var11);
         } catch (InterruptedException var32) {
         }
      }

      JCoreHelper.netMonitor(var0, var1, var2, 0L, var10 != null ? var10.length : 0);
      if (200 == var12) {
         try {
            if (var13 == 0) {
               Logger.recoverMethod_d_e925fa09("HttpManager", "Unexpected: downloaded bytes content length is 0");
               return null;
            } else if (var10.length < var13) {
               Logger.recoverMethod_d_e925fa09("HttpManager", "Download bytes failed. Got bytes len < header content length.");
               return null;
            } else {
               return var10;
            }
         } catch (Exception var31) {
            Logger.recoverMethod_d_e925fa09("HttpManager", "parse response error:" + var31.getMessage());
            return null;
         }
      } else if (400 == var12) {
         Logger.recoverMethod_d_e925fa09("HttpManager", "server response failure - " + var1);
         return null;
      } else if (404 == var12) {
         Logger.recoverMethod_d_e925fa09("HttpManager", "Request path does not exist: 404 - " + var1);
         return null;
      } else {
         Logger.recoverMethod_d_e925fa09("HttpManager", "Other wrong response status - " + var12 + ", url:" + var1);
         return null;
      }
   }

   public static byte[] recoverMethod_a_b328ee5f(InputStream var0) throws java.io.IOException {
      return RecoverType9b4c5af2.recoverMethod_b_b328ee5f(var0);
   }
}
