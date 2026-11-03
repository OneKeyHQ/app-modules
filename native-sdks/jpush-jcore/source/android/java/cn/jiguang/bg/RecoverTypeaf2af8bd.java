package cn.jiguang.bg;

import android.content.Context;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.d.RecoverType5a68d10;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class RecoverTypeaf2af8bd {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{80, 59, 80, 96, 112, 82, 77, 79});
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{104, 13, 76, 65, 82});
   public static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{78, 60, 102, 65, 107, 97, 119, 127, 68});
   public static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{73, 125, 116, 125, 89, 67, 75, 109, 122, 116, 81, 83, 124}
   );
   public static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{65, 37, 85, 126, 94, 84, 124, 102, 98, 60, 125, 100, 44});
   public static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{72, 13, 122, 103, 101, 60, 93, 69, 109, 105, 59, 124, 84, 21, 106, 125, 123}
   );
   public static final String recoverField_g = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{76, 112, 106, 124, 100, 68, 73, 117, 123, 56, 98, 95});

   public static String recoverMethod_a_a4ea6d43(Context var0, String var1) {
      File var2 = new File(var0.getFilesDir(), "zl");
      if (!var2.exists()) {
         var2.mkdirs();
      }

      return new File(var2, var1).getAbsolutePath();
   }

   public static Map recoverMethod_a_604efcea() {
      HashMap var0 = new HashMap();

      try {
         String var1 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
         long var2 = RecoverType5a68d10.recoverMethod_f_f0554de7(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
         var0.put(recoverField_a, var1);
         var0.put(recoverField_b, String.valueOf(var2));
         var0.put(recoverField_c, recoverField_d);
      } catch (Throwable var4) {
      }

      return var0;
   }
}
