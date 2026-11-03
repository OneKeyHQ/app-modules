package cn.jiguang.am;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.an.RecoverTypeaf2a9ea5;
import cn.jiguang.an.RecoverTypeaf2a9ea6;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.e.RecoverType5a690d3;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import org.json.JSONObject;

public class RecoverTypeaf2a9ae5 {
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         121, 68, 84, 105, 123, 44, 62, 31, 67, 124, 59, 115, 38, 5, 68, 44, 38, 124, 97, 69, 83, 113, 38, 117, 127, 31, 87, 112, 39, 122, 124, 95, 80, 106
      }
   );
   private static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         126,
         101,
         68,
         80,
         106,
         50,
         57,
         62,
         83,
         69,
         42,
         109,
         33,
         36,
         84,
         21,
         52,
         97,
         102,
         103,
         6,
         14,
         115,
         120,
         99,
         98,
         88,
         14,
         122,
         102,
         57,
         102,
         89,
         15,
         117,
         101,
         121,
         97,
         67
      }
   );
   public static String recoverField_a = "";
   private static LinkedList recoverField_d = new LinkedList();

   public static void recoverMethod_a_f0554df3(final Context var0) {
      if (var0 != null) {
         int var1 = (Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aD_48794abf());
         var1 = var1 > 10 ? var1 : 10;
         RecoverTypeaf2b0403.recoverMethod_a_63b8aba6("DELAY_TASK", new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  long var1x = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
                  if (var1x == 0L) {
                     return;
                  }

                  String var3 = var0.getFilesDir() + File.separator + "nes";
                  String var4 = RecoverTypeaf2b07c1.recoverMethod_j_e501eb0d(var0);
                  String var5 = "android-" + var4 + "-" + var1x + ".zip";
                  String var6 = var0.getFilesDir() + File.separator + var5;
                  RecoverTypeaf2b07d7.recoverMethod_a_e925fa09(var3, var6);
                  File var7 = new File(var6);
                  if (var7 == null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "logFile is null!");
                     return;
                  }

                  File var8 = var7.getParentFile();
                  if (!var8.exists()) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "there is no log file dir!");
                     return;
                  }

                  if (!var7.exists() || var7.length() == 0L) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "there is no log file to upload!");
                     return;
                  }

                  String var9 = RecoverTypeaf2a9ae5.recoverMethod_a_a4ea6d43(var0, var5);
                  if (TextUtils.isEmpty(var9)) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "getUploadToken url is empty");
                     var7.delete();
                     return;
                  }

                  boolean var10 = RecoverTypeaf2a9ea6.recoverMethod_a_e5090bb8(var0, var9, var7);
                  if (var10) {
                     File var11 = new File(var3);
                     RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var11);
                  }

                  var7.delete();
               } catch (Throwable var12) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "startUpload error is " + var12.getMessage());
               }
            }
         }, var1 * 1000);
      }
   }

   public static void recoverMethod_b_f0554df3(final Context var0) {
      if (var0 != null) {
         int var1 = (Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aD_48794abf());
         int var2 = RecoverType5a690d3.recoverMethod_e_f0554de6(var0);
         boolean var3 = RecoverType5a690d3.recoverMethod_d_f0554df7(var0);
         var1 = Math.max(var1, 10);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "LogFileHelper", "startUpload delay is " + var1 + ", uploadLog is " + var3 + ", serverUploadState is " + var2
         );
         if (var2 == 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "serverUploadState is 0, not uploadLog");
         } else if (var2 >= 2 && !var3) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "serverUploadState is 2 and local_upload is false, not uploadLog");
         } else {
            RecoverTypeaf2b0403.recoverMethod_a_63b8aba6("DELAY_TASK", new RecoverTypeaf2b0401() {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     long var1x = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
                     if (var1x == 0L) {
                        return;
                     }

                     String var3x = var0.getFilesDir() + File.separator + RecoverTypeaf2a9ae8.recoverField_a + File.separator;
                     File var4 = new File(var3x + RecoverTypeaf2a9ae8.recoverField_b);
                     if (var4 == null) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "logFile is null!");
                        return;
                     }

                     File var5 = var4.getParentFile();
                     if (!var5.exists()) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "there is no log file dir!");
                        return;
                     }

                     if (!var4.exists() || var4.length() == 0L) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "there is no log file to upload!");
                        return;
                     }

                     String var6 = RecoverTypeaf2b07c1.recoverMethod_j_e501eb0d(var0);
                     String var7 = "android-" + var6 + "-" + var1x + ".aes";
                     File var8 = new File(var3x + var7);
                     if (!var8.exists()) {
                        var8.createNewFile();
                        RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("LogFileHelper", "Create aesFile : " + var8.getName());
                     } else {
                        var8.delete();
                        var8.createNewFile();
                        RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("LogFileHelper", " repeat Create aesFile : " + var8.getName());
                     }

                     String var9 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
                     RecoverTypeaf2a9ea5.recoverMethod_a_9f49842d(var9, var4, var8);
                     if (var8.length() == 0L) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "aesFile encrypt fail");
                        var8.delete();
                        return;
                     }

                     String var10 = RecoverTypeaf2a9ae5.recoverMethod_a_a4ea6d43(var0, var7);
                     if (TextUtils.isEmpty(var10)) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "getUploadToken url is empty");
                        var8.delete();
                        return;
                     }

                     boolean var11 = RecoverTypeaf2a9ea6.recoverMethod_a_e5090bb8(var0, var10, var8);
                     if (var11) {
                        if (RecoverTypeaf2a9ae7.recoverMethod_c_f0554df7(var0)) {
                           var4.delete();
                        }

                        RecoverTypeaf2a9ae5.recoverMethod_a_93bf8bd5(var0, var11, var6, "");
                     }

                     var8.delete();
                  } catch (Throwable var12) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "startUpload error is " + var12.getMessage());
                  }
               }
            }, var1 * 1000);
         }
      }
   }

   public static void recoverMethod_a_93bf8bd5(Context var0, boolean var1, String var2, String var3) {
      if ((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_an_48794abf())) {
         PackageManager var4 = var0.getPackageManager();
         Intent var5 = var4.getLaunchIntentForPackage("com.jiguang.openlog");
         var5.putExtra("uid", RecoverType5a68d10.recoverMethod_f_f0554de7(var0));
         var5.putExtra("appKey", RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0));
         var5.putExtra("appName", var2);
         var5.putExtra("content", var3);
         var5.putExtra("result", var1);
         var0.startActivity(var5);
      }
   }

   public static String recoverMethod_a_a4ea6d43(Context var0, String var1) {
      String var2 = "";

      try {
         String var3 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         if (TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "request upload token failed because can't get appKey");
            return var2;
         }

         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var0, recoverField_d);

         for (Object recoveryElement_259 : recoverField_d) {
            String var5 = (String)recoveryElement_259;
            long var6 = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
            if (var6 == 0L) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "request l config failed because can't get uid");
               return null;
            }

            String var8 = RecoverType5a68d10.recoverMethod_j_e501eb0d(var0);
            if (TextUtils.isEmpty(var8)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "request l config failed because need register first");
               return null;
            }

            JSONObject var9 = new JSONObject();
            var9.put("appkey", var3);
            var9.put("uid", var6);
            var9.put("platform", "a");
            var9.put("key", var1);
            String var10 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var8).toUpperCase();
            String var11 = RecoverTypeaf2b07d5.recoverMethod_e_e96ea081(var6 + var10).toUpperCase();
            if (TextUtils.isEmpty(var11)) {
               return null;
            }

            String var12 = var6 + ":" + var11;
            var12 = Base64.encodeToString(var12.getBytes(), 10);
            if (TextUtils.isEmpty(var12)) {
               return null;
            }

            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "url:" + var5 + ", param json:" + var9.toString());
            HttpRequest var13 = new HttpRequest(var5);
            var13.setRequestProperty("Content-Type", "application/json");
            var13.setRequestProperty("Accept", "application/json");
            var13.setRequestProperty("X-Http-Platform", "android");
            var13.setRequestProperty("X-Http-Appkey", var3);
            var13.setRequestProperty("Authorization", "Basic " + var12);
            var13.setRequestProperty("Charset", "UTF-8");
            var13.setBody(var9.toString());
            HttpResponse var14 = HttpUtils.httpPost(var0, var13);
            int var15 = var14.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "responseCode:" + var15);
            if (200 == var15) {
               String var16 = var14.getResponseBody();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "responseBody:" + var16);
               if (!TextUtils.isEmpty(var16)) {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LogFileHelper", "request upload token success,response body:" + var16);
                  JSONObject var17 = new JSONObject(var16);
                  if (var17.optInt("code") == 2000) {
                     return var17.optJSONObject("data") != null ? var17.optJSONObject("data").optString("uploadPreSignedURL") : "";
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "start to open log ");
                  recoverMethod_a_93bf8bd5(var0, false, "", var17.optString("content"));
               }
            }
         }
      } catch (Throwable var18) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "getUploadToken error is " + var18.getMessage());
      }

      return var2;
   }

   public static String recoverMethod_a_e45c358f(Context var0, long var1) {
      String var3 = var0.getFilesDir() + File.separator + RecoverTypeaf2a9ae8.recoverField_a + File.separator;
      File var4 = new File(var3);
      if (var4.exists() && var4.isDirectory()) {
         long var5 = var1;
         StringBuilder var7 = new StringBuilder();

         try {
            File[] var8 = var4.listFiles(new FilenameFilter() {
               @Override
               public boolean accept(File var1, String var2) {
                  return var2.endsWith(".txt");
               }
            });
            if (var8 != null && var8.length > 0) {
               Arrays.sort(var8, new Comparator() {
                  public int recoverMethod_a_7c424096(File var1, File var2) {
                     return Long.compare(var1.lastModified(), var2.lastModified());
                  }

                  // $VF: synthetic method
                  @Override
                  public int compare(Object var1, Object var2) {
                     return this.recoverMethod_a_7c424096((File)var1, (File)var2);
                  }
               });

               for (File var12 : var8) {
                  try {
                     BufferedReader var13 = new BufferedReader(new FileReader(var12));

                     String var14;
                     try {
                        while ((var14 = var13.readLine()) != null) {
                           long var15 = recoverMethod_a_aad3b1f3(var14);
                           if (var15 > var1) {
                              var7.append(var14).append("\n");
                           }

                           var5 = var15;
                        }
                     } catch (Throwable var18) {
                        try {
                           var13.close();
                        } catch (Throwable var17) {
                           var18.addSuppressed(var17);
                        }

                        throw var18;
                     }

                     var13.close();
                  } catch (IOException var19) {
                     RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "Error reading log file: " + var19.getMessage());
                  }
               }
            }
         } catch (Throwable var20) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "Error merging logs: " + var20.getMessage());
         }

         if (!TextUtils.isEmpty(var7)) {
            RecoverType5a690d3.recoverMethod_a_1a54e731(var0, var5);
         }

         return var7.toString();
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "Log directory does not exist or is not a directory: " + var3);
         return "";
      }
   }

   private static long recoverMethod_a_aad3b1f3(String var0) {
      try {
         if (var0 != null && var0.length() > 23) {
            String var1 = var0.substring(0, 23);
            SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
            Date var3 = var2.parse(var1);
            return var3 != null ? var3.getTime() : 0L;
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LogFileHelper", "Error parsing timestamp from log: " + var4.getMessage());
      }

      return 0L;
   }

   static {
      recoverField_d.clear();
      recoverField_d.add(recoverField_b);
      recoverField_d.add(recoverField_c);
   }
}
