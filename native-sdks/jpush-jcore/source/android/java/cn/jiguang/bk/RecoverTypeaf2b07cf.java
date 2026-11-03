package cn.jiguang.bk;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Process;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.g.RecoverType5a69853;
import java.lang.reflect.Method;
import java.util.HashMap;

public class RecoverTypeaf2b07cf {
   private static PackageInfo recoverField_a;
   private static HashMap recoverField_b = new HashMap();
   private static String recoverField_c = "";
   private static int recoverField_d = Integer.MIN_VALUE;
   private static Object recoverField_e;
   private static final HashMap recoverField_f = new HashMap();
   private static int recoverField_g = 0;

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (!TextUtils.isEmpty(recoverField_c)) {
         return recoverField_c;
      }

      recoverField_c = var0.getPackageName();
      return recoverField_c;
   }

   public static PackageInfo recoverMethod_a_9537863d(Context var0, int var1) {
      return recoverMethod_a_afbf9147(var0, recoverMethod_a_e501eb0d(var0), var1);
   }

   public static PackageInfo recoverMethod_a_afbf9147(Context var0, String var1, int var2) {
      return recoverMethod_a_4b8f43ff(var0, var1, var2, false);
   }

   public static PackageInfo recoverMethod_a_4b8f43ff(Context var0, String var1, int var2, boolean var3) {
      boolean var4 = recoverMethod_a_30483d68(var0, 1109, true);
      PackageInfo var5 = null;
      if (var3 || var4 && VERSION.SDK_INT >= 33) {
         var5 = recoverMethod_a_342ba49(var1, var2);
      }

      if (var5 == null) {
         var5 = recoverMethod_b_afbf9147(var0, var1, var2);
      }

      return var5;
   }

   public static PackageInfo recoverMethod_b_afbf9147(Context var0, String var1, int var2) {
      if (null != var0 && !TextUtils.isEmpty(var1) && var2 >= 0) {
         try {
            int var3 = var1.indexOf(":");
            if (var3 > 0) {
               String var4 = var1.substring(0, var3);
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "get p info has p name, origin: " + var1 + ", new: " + var4);
               var1 = var4;
            }

            String var10 = recoverMethod_a_e501eb0d(var0);
            if (TextUtils.equals(var10, var1)) {
               return recoverMethod_b_9537863d(var0, var2);
            }

            if (recoverMethod_a_30483d68(var0, 1105, true) && recoverMethod_a_30483d68(var0, 1100, true)) {
               PackageInfo var5 = null;
               HashMap var6 = (HashMap)recoverField_b.get(var1);
               if (null != var6) {
                  var5 = (PackageInfo)var6.get(var2);
                  if (null != var5) {
                     var5 = TextUtils.isEmpty(var5.packageName) ? null : var5;
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                        "PackageUtils", "get third p info by mem-cache, p name: " + var1 + ", flag: " + var2 + ", pinfo: " + var5
                     );
                     return var5;
                  }
               }

               try {
                  var5 = var0.getPackageManager().getPackageInfo(var1, var2);
               } catch (Throwable var8) {
               }

               if (recoverField_b.containsKey(var1)) {
                  var6 = new HashMap();
                  var6.put(var2, var5 == null ? new PackageInfo() : var5);
                  recoverField_b.put(var1, var6);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "cache third p info, p name: " + var1 + ", flag: " + var2);
               }

               return var5;
            } else {
               return null;
            }
         } catch (Throwable var9) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "get p info failed, error: " + var9.getMessage());
            return null;
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "get third p info failed, because param is illegal");
         return null;
      }
   }

   private static int recoverMethod_a_9b68() {
      if (recoverField_d != Integer.MIN_VALUE) {
         return recoverField_d;
      }

      if (VERSION.SDK_INT >= 17) {
         try {
            Class var0 = Class.forName(new String(Base64.decode("YW5kcm9pZC5vcy5Vc2VySGFuZGxl", 0)));
            String var1 = new String(Base64.decode("Z2V0VXNlcklk", 0));
            Method var2 = var0.getDeclaredMethod(var1, int.class);
            var2.setAccessible(true);
            int var3 = (Integer)var2.invoke(var0, Process.myUid());
            recoverField_d = var3;
            return var3;
         } catch (Throwable var4) {
         }
      }

      return 0;
   }

   public static PackageInfo recoverMethod_a_342ba49(String var0, int var1) {
      PackageInfo var3;
      try {
         if (recoverField_e == null) {
            String var4 = new String(Base64.decode("YW5kcm9pZC5hcHAuQWN0aXZpdHlUaHJlYWQ=", 0));
            String var5 = new String(Base64.decode("Z2V0UGFja2FnZU1hbmFnZXI=", 0));
            recoverField_e = Class.forName(var4).getMethod(var5).invoke(null);
         }

         IBinder var10 = ((IInterface)recoverField_e).asBinder();
         Parcel var11 = Parcel.obtain();
         Parcel var6 = Parcel.obtain();
         var11.writeInterfaceToken(var10.getInterfaceDescriptor());
         var11.writeString(var0);
         var11.writeLong(var1);
         var11.writeInt(recoverMethod_a_9b68());
         var10.transact(3, var11, var6, 0);
         var6.readException();
         var3 = var6.readInt() != 0 ? (PackageInfo)PackageInfo.CREATOR.createFromParcel(var6) : null;

         try {
            var6.recycle();
            var11.recycle();
         } catch (Throwable var8) {
            return var3;
         }
      } catch (Throwable var9) {
         var3 = null;
      }

      return var3;
   }

   public static boolean recoverMethod_a_30483d68(Context var0, int var1, boolean var2) {
      return RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_b_256326a(var1, var2);
   }

   private static PackageInfo recoverMethod_a_3fc4bbf(int var0) {
      if (null == recoverField_a || var0 != 0 && (recoverField_g & var0) == 0) {
         if (recoverField_f.containsKey(var0) && recoverField_f.get(var0) != null) {
            return (PackageInfo)recoverField_f.get(var0);
         }

         if (var0 == 0 && recoverField_f.size() > 0) {
            for (Object recoveryElement_177 : recoverField_f.keySet()) {
               int var2 = (int)recoveryElement_177;
               PackageInfo var3 = (PackageInfo)recoverField_f.get(var2);
               if (null != var3) {
                  return var3;
               }
            }
         }

         return null;
      } else {
         return recoverField_a;
      }
   }

   private static synchronized PackageInfo recoverMethod_b_9537863d(Context var0, int var1) {
      PackageInfo var2 = null;
      String var3 = recoverMethod_a_e501eb0d(var0);

      try {
         var2 = recoverMethod_a_3fc4bbf(var1);
         if (null != var2) {
            return var2;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "no l p info cache, call sys api");
         int var4 = var1;
         if (recoverField_g == 0) {
            var4 |= 15;
            var4 |= 4288;
            if (VERSION.SDK_INT >= 28) {
               var4 |= 134217728;
            }
         }

         recoverField_g |= var4;
         var2 = var0.getPackageManager().getPackageInfo(var3, recoverField_g);
         if (var2 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "g l p info failed, p info is null");
            var2 = var0.getPackageManager().getPackageInfo(var3, var1);
            if (var2 != null) {
               recoverField_f.put(var1, var2);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "get l p info again success for l p is null, flag: " + var1);
            }
         } else {
            recoverField_a = var2;
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "get l p info success, flag: " + var1 + ", flags: " + recoverField_g);
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "get l p info failed, error: " + var7.getMessage());

         try {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "get l p info again");
            var2 = var0.getPackageManager().getPackageInfo(var3, var1);
            if (var2 == null) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "g l p info again failed, p info is null");
            } else {
               recoverField_f.put(var1, var2);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("PackageUtils", "get l p info again success, flag: " + var1);
            }
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("PackageUtils", "get l p again failed, error: " + var6.getMessage());
         }
      }

      return var2;
   }

   static {
      recoverField_b.put("com.huawei.hwid", null);
   }
}
