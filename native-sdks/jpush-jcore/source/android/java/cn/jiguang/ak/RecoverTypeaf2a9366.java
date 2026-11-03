package cn.jiguang.ak;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import java.util.ArrayList;

public class RecoverTypeaf2a9366 {
   private static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{122, 103, 123, 63, 68, 69, 119, 107, 115, 127, 68, 14, 104, 121, 123, 100, 67, 73, 122}
   );
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         122,
         103,
         123,
         63,
         68,
         69,
         119,
         107,
         115,
         127,
         68,
         14,
         104,
         121,
         123,
         100,
         67,
         73,
         122,
         38,
         69,
         121,
         81,
         82,
         124,
         92,
         119,
         98,
         91,
         112,
         107,
         103,
         96,
         120,
         84,
         69,
         107
      }
   );
   private static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         117,
         126,
         93,
         14,
         109,
         109,
         120,
         114,
         85,
         78,
         109,
         38,
         103,
         96,
         93,
         85,
         106,
         97,
         117,
         63,
         116,
         73,
         106,
         100,
         127,
         122,
         85,
         105,
         119,
         110,
         121,
         65,
         66,
         79,
         111,
         97,
         114,
         116,
         66
      }
   );
   private static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         114,
         95,
         77,
         55,
         124,
         115,
         127,
         83,
         69,
         119,
         124,
         56,
         96,
         65,
         77,
         108,
         123,
         127,
         114,
         30,
         97,
         106,
         123,
         127,
         98,
         68,
         112,
         107,
         103,
         96,
         120,
         84,
         69,
         107
      }
   );
   private static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         122,
         103,
         123,
         63,
         68,
         69,
         119,
         107,
         115,
         127,
         68,
         14,
         104,
         121,
         123,
         100,
         67,
         73,
         122,
         38,
         80,
         126,
         92,
         68,
         124,
         122,
         87,
         98,
         68,
         112,
         107,
         103,
         96,
         120,
         84,
         69,
         107
      }
   );
   private static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         117,
         126,
         93,
         14,
         109,
         109,
         120,
         114,
         85,
         78,
         109,
         38,
         103,
         96,
         93,
         85,
         106,
         97,
         117,
         63,
         64,
         69,
         107,
         110,
         56,
         65,
         85,
         82,
         127,
         103,
         100,
         124,
         81,
         78,
         122,
         109,
         84,
         80,
         83,
         84,
         112,
         126,
         127,
         101,
         73
      }
   );
   private static final ArrayList recoverField_g = new ArrayList();

   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      return !TextUtils.isEmpty(var1) && TextUtils.equals(var1, recoverField_a) ? recoverMethod_b_f0554df7(var0) : false;
   }

   public static boolean recoverMethod_b_5ffc0101(Context var0, String var1) {
      return !TextUtils.isEmpty(var1) && TextUtils.equals(var1, recoverField_a) ? !recoverMethod_a_f0554df7(var0) : true;
   }

   private static boolean recoverMethod_b_f0554df7(Context var0) {
      for (Object recoveryElement_239 : recoverField_g) {
         String var2 = (String)recoveryElement_239;
         try {
            String var3 = var2;
            if (!var3.startsWith("content://")) {
               var3 = "content://" + var3;
            }

            Uri var4 = Uri.parse(var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeSpecialHelper", " provider: " + var3 + ", uri: " + var4);
            Uri var5 = var0.getApplicationContext().getContentResolver().insert(var4, new ContentValues());
            if (var5 != null) {
               return true;
            }
         } catch (Throwable var6) {
         }
      }

      return false;
   }

   public static boolean recoverMethod_a_f0554df7(Context var0) {
      try {
         Intent var1 = new Intent();
         var1.setClassName(recoverField_a, recoverField_f);
         boolean var2 = RecoverTypeaf2a9362.recoverMethod_a_3441485e(var0, recoverField_a, var1, true);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeSpecialHelper", "q has perform b act: " + var2);
         return var2;
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeSpecialHelper", "check q has perform b act failed, " + var3.getMessage());
         return false;
      }
   }

   static {
      recoverField_g.add(recoverField_b);
      recoverField_g.add(recoverField_c);
      recoverField_g.add(recoverField_d);
      recoverField_g.add(recoverField_e);
   }
}
