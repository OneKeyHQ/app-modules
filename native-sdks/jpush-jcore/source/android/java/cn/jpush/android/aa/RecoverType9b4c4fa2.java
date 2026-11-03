package cn.jpush.android.aa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Map;

public class RecoverType9b4c4fa2 {
   private SharedPreferences recoverField_a;
   private Editor recoverField_b;

   public RecoverType9b4c4fa2(Context var1) {
      this.recoverField_a = var1.getSharedPreferences("cn.jpush.statistics", 0);
      this.recoverField_b = this.recoverField_a.edit();
   }

   @SuppressLint("CommitPrefEdits")
   public void recoverMethod_a_aad3b1ff(String var1) {
      int var2 = this.recoverField_a.getInt(var1, 0);
      this.recoverField_b.putInt(var1, var2 + 1);
      this.recoverField_b.apply();
   }

   public Map recoverMethod_a_604efcea() {
      return this.recoverField_a.getAll();
   }

   public void recoverMethod_b_9b75() {
      this.recoverField_b.clear();
      this.recoverField_b.apply();
   }
}
