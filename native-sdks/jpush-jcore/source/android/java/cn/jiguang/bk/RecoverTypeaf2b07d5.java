package cn.jiguang.bk;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverTypeaf2b07d5 {
   private static Pattern recoverField_a = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d{1,2})\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d{1,2})");
   private static SimpleDateFormat recoverField_b = new SimpleDateFormat("HHH:mm:ss:SSS", Locale.ENGLISH);
   private static Class recoverField_c;
   private static Method recoverField_d;

   public static boolean recoverMethod_a_aad3b203(String var0) {
      if (null == var0) {
         return true;
      } else {
         return var0.length() == 0 ? true : var0.trim().length() == 0;
      }
   }

   public static String recoverMethod_b_e96ea081(String var0) {
      if (recoverMethod_a_aad3b203(var0)) {
         return "";
      }

      String var1 = "[^\\w#$@\\-一-龥]+";
      return Pattern.compile(var1).matcher(var0).replaceAll("");
   }

   public static byte[] recoverMethod_c_afa28ebe(String var0) {
      try {
         char[] var1 = var0.toCharArray();
         byte[] var2 = new byte[var1.length];

         for (int var3 = 0; var3 < var1.length; var3++) {
            var2[var3] = (byte)var1[var3];
         }

         MessageDigest var5 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
         return var5.digest(var2);
      } catch (Exception var4) {
         return null;
      }
   }

   public static String recoverMethod_d_e96ea081(String var0) {
      MessageDigest var1 = null;

      try {
         var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
         char[] var2 = var0.toCharArray();
         byte[] var3 = new byte[var2.length];

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3[var4] = (byte)var2[var4];
         }

         byte[] var10 = var1.digest(var3);
         StringBuffer var5 = new StringBuffer();

         for (int var6 = 0; var6 < var10.length; var6++) {
            int var7 = var10[var6] & 255;
            if (var7 < 16) {
               var5.append("0");
            }

            var5.append(Integer.toHexString(var7));
         }

         return var5.toString();
      } catch (Exception var8) {
         return "";
      }
   }

   public static String recoverMethod_e_e96ea081(String var0) {
      MessageDigest var1 = null;

      try {
         var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
         byte[] var2 = var0.getBytes("utf-8");
         byte[] var3 = var1.digest(var2);
         StringBuffer var4 = new StringBuffer();

         for (int var5 = 0; var5 < var3.length; var5++) {
            int var6 = var3[var5] & 255;
            if (var6 < 16) {
               var4.append("0");
            }

            var4.append(Integer.toHexString(var6));
         }

         return var4.toString();
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("StringUtils", "Get hash error");
         return "";
      }
   }

   public static boolean recoverMethod_f_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      int var1 = var0.indexOf(":");
      if (var1 >= 0) {
         if (var1 != var0.lastIndexOf(":")) {
            return false;
         }

         var0 = var0.substring(0, var1);
      }

      return recoverField_a.matcher(var0).matches();
   }

   public static boolean recoverMethod_g_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      int var1 = var0.indexOf(":");
      if (var1 == -1) {
         return false;
      }

      int var2 = var0.lastIndexOf(":");
      return var2 != var1;
   }

   public static boolean recoverMethod_h_aad3b203(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return false;
      }

      try {
         Pattern var1 = Pattern.compile("[\\x20-\\x7E]+");
         return var1.matcher(var0).matches();
      } catch (Throwable var2) {
         return true;
      }
   }

   private static void recoverMethod_a_31fc143d(StringBuffer var0, byte var1) {
      var0.append("0123456789ABCDEF".charAt(var1 >> 4 & 15)).append("0123456789ABCDEF".charAt(var1 & 15));
   }

   public static byte[] recoverMethod_i_afa28ebe(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return new byte[0];
      }

      try {
         return var0.getBytes("UTF-8");
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("StringUtils", "stringToUtf8Bytes error:" + var2.getMessage());
         return var0.getBytes();
      }
   }

   public static String recoverMethod_j_e96ea081(String var0) {
      if (null != var0 && !"".equals(var0)) {
         try {
            MessageDigest var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
            var1.update(var0.getBytes());
            return recoverMethod_a_79834524(var1.digest());
         } catch (Throwable var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static String recoverMethod_a_79834524(byte[] var0) {
      if (var0 == null) {
         return "";
      }

      StringBuffer var1 = new StringBuffer(2 * var0.length);

      for (byte var5 : var0) {
         recoverMethod_a_31fc143d(var1, var5);
      }

      return var1.toString();
   }

   public static String recoverMethod_b_79834524(byte[] var0) {
      MessageDigest var1 = null;

      try {
         var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_b);
         byte[] var2 = var1.digest(var0);
         StringBuffer var3 = new StringBuffer();

         for (int var4 = 0; var4 < var2.length; var4++) {
            int var5 = var2[var4] & 255;
            if (var5 < 16) {
               var3.append("0");
            }

            var3.append(Integer.toHexString(var5));
         }

         return var3.toString();
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("StringUtils", "Get SHash error");
         return "";
      }
   }

   public static String recoverMethod_c_79834524(byte[] var0) {
      MessageDigest var1 = null;

      try {
         var1 = MessageDigest.getInstance(RecoverTypeaf2b07d4.recoverField_a);
         byte[] var2 = var1.digest(var0);
         StringBuffer var3 = new StringBuffer();

         for (int var4 = 0; var4 < var2.length; var4++) {
            int var5 = var2[var4] & 255;
            if (var5 < 16) {
               var3.append("0");
            }

            var3.append(Integer.toHexString(var5));
         }

         return var3.toString();
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("StringUtils", "Get SHash error");
         return "";
      }
   }

   public static void recoverMethod_a_fa4794e4(Context var0, List var1) {
      if (RecoverTypeaf2b07c2.recoverMethod_d_f0554df7(var0) && var1 != null && var1.size() >= 2 && !((String)var1.get(0)).contains("ipv6")) {
         Collections.swap(var1, 0, 1);
      }
   }

   public static String recoverMethod_k_e96ea081(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return var0;
      }

      try {
         return recoverMethod_b_79834524(var0.getBytes("utf-8"));
      } catch (Throwable var2) {
         return var0;
      }
   }

   public static String recoverMethod_l_e96ea081(String var0) {
      return recoverMethod_a_8ea82db7(var0, "");
   }

   public static String recoverMethod_a_8ea82db7(String var0, String var1) {
      try {
         if (recoverField_c == null) {
            recoverField_c = Class.forName("android.os.SystemProperties");
         }

         if (recoverField_d == null) {
            recoverField_d = recoverField_c.getMethod("get", String.class, String.class);
         }

         return (String)recoverField_d.invoke(null, var0, var1);
      } catch (Throwable var3) {
         return var1;
      }
   }

   public static JSONObject recoverMethod_a_fcab3ead(Bundle var0) {
      JSONObject var1 = new JSONObject();
      if (var0 != null) {
         for (String var3 : var0.keySet()) {
            Object var4 = var0.get(var3);

            try {
               if (var4 instanceof String) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Integer) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Boolean) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Double) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Float) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Long) {
                  var1.put(var3, var4);
               } else if (var4 instanceof Bundle) {
                  var1.put(var3, recoverMethod_a_fcab3ead((Bundle)var4));
               } else {
                  var1.put(var3, String.valueOf(var4));
               }
            } catch (Exception var6) {
            }
         }
      }

      return var1;
   }

   public static HashMap recoverMethod_b_a9dc66e2(Bundle var0) {
      HashMap var1 = new HashMap();

      try {
         if (var0 != null) {
            for (String var3 : var0.keySet()) {
               Object var4 = var0.get(var3);
               var1.put(var3, var4);
            }
         }
      } catch (Throwable var5) {
      }

      return var1;
   }

   public static Map recoverMethod_a_b00aa7de(JSONObject var0) {
      if (var0 == null) {
         return null;
      }

      HashMap var1 = new HashMap();

      try {
         Iterator var2 = var0.keys();

         while (var2.hasNext()) {
            String var3 = (String)var2.next();
            String var4 = (String)var0.get(var3);
            var1.put(var3, var4);
         }
      } catch (JSONException var5) {
      }

      return var1;
   }
}
