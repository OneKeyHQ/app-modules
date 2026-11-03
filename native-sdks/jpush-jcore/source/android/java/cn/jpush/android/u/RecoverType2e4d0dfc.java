package cn.jpush.android.u;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.ad.RecoverType9b4c5ae9;
import cn.jpush.android.ad.RecoverType9b4c5aeb;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.k.RecoverType2e4ce86e;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.x.RecoverType2e4d193c;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RecoverType2e4d0dfc extends RecoverType2e4d0df9 {
   protected View recoverField_f;

   public RecoverType2e4d0dfc(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public View recoverMethod_e_d08d2077() {
      return this.recoverField_f;
   }

   public View recoverMethod_a_edf6881(String var1) {
      int var2 = this.recoverField_d.getResources().getIdentifier(var1, "layout", this.recoverField_d.getPackageName());
      if (var2 <= 0) {
         throw new IllegalArgumentException("\"" + var1 + ".xml\" not found,please check res-layout file!!");
      } else {
         View var3 = LayoutInflater.from(this.recoverField_d).inflate(var2, null);
         if (var3 == null) {
            throw new IllegalArgumentException("\"" + var1 + ".xml\" not found,please check res-layout file!!");
         } else {
            return var3;
         }
      }
   }

   public final View recoverMethod_b_edf6881(String var1) {
      int var2 = this.recoverField_d.getResources().getIdentifier(var1, "id", this.recoverField_d.getPackageName());
      if (var2 <= 0) {
         throw new IllegalArgumentException("view id=\"" + var1 + "\" not found, check layout.xml or not confused keep it");
      } else {
         View var3 = this.recoverField_f.findViewById(var2);
         if (var3 == null) {
            throw new IllegalArgumentException("view id=\"" + var1 + "\" not found, check layout.xml or not confused keep it");
         } else {
            return var3;
         }
      }
   }

   @Override
   public boolean recoverMethod_f_9b79() {
      try {
         this.recoverField_f = this.recoverMethod_a_edf6881(this.recoverMethod_b_73cf11cb());
      } catch (Throwable var3) {
         Logger.recoverMethod_ke_e925fa09("BaseInAppWrapper", "inflateLayout throwable " + var3.getMessage());
         this.recoverMethod_a_1349ef(3013L);
         return false;
      }

      try {
         this.recoverMethod_a_9b75();
         return true;
      } catch (Throwable var2) {
         Logger.recoverMethod_ke_e925fa09("BaseInAppWrapper", "findViewByName throwable =" + var2.getMessage());
         this.recoverMethod_a_1349ef(3014L);
         return false;
      }
   }

   protected abstract void recoverMethod_a_9b75();

   protected abstract String recoverMethod_b_73cf11cb();

   @Override
   public void recoverMethod_g_9b75() {
   }

   protected void recoverMethod_a_fb65286f(TextView var1) {
      String var2 = var1.getText().toString();
      int var3 = RecoverType9b4c5af2.recoverMethod_b_aad3b1f2(var2);
      if (var3 > 1) {
         var1.setText(var2.replace("" + var3, "" + (var3 - 1)));
      } else {
         this.recoverField_a.recoverMethod_a_e2da561b(this.recoverField_d, this.recoverField_f, this.recoverField_b);
      }
   }

   protected void recoverMethod_a_3e1c1434(TextView var1, JSONObject var2, final int var3) {
      try {
         String var4 = var2.optString("text");
         if (var2.has("duration")) {
            int var5 = var2.optInt("duration") / 1000;
            var1.setText(var5 + "s" + var4);
         } else {
            var1.setText(var4);
         }

         if (var2.has("style")) {
            JSONArray var10 = var2.optJSONArray("style");
            if (var10 != null && var10.length() == 4) {
               if (var10.optInt(0) == 1) {
                  var1.setTypeface(Typeface.DEFAULT_BOLD);
               }

               int var6 = var10.optInt(1);
               if (var6 > 0) {
                  var1.setTextSize(var6);
               }

               int var7 = var10.optInt(2);
               if (var7 > 0) {
                  var1.setGravity(this.recoverMethod_e_134621(var7));
               }

               String var8 = var10.optString(3);
               if (!TextUtils.isEmpty(var8) && var8.startsWith("#")) {
                  var1.setTextColor(Color.parseColor(var8));
               }
            }
         }

         if (var2.has("click")) {
            JSONObject var11 = var2.optJSONObject("click");
            if (var11 != null) {
               final int var13 = var11.optInt("type");
               final String var15 = var11.optString("action");
               var1.setOnClickListener(new OnClickListener() {
                  @Override
                  public void onClick(View var1) {
                     RecoverType2e4d0dfc.this.recoverMethod_a_9ea0018b(var13, var15, var3);
                  }
               });
            }
         }

         if (var2.has("bg_color")) {
            String var12 = var2.optString("bg_color");
            if (!TextUtils.isEmpty(var12) && var12.startsWith("#")) {
               GradientDrawable var14 = (GradientDrawable)var1.getBackground();
               var14.setColor(Color.parseColor(var12));
            }
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "native updateTextView throwable=" + var9);
      }
   }

   protected void recoverMethod_a_9ea0018b(int var1, String var2, int var3) {
      switch (var1) {
         case 1:
         case 2:
            RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(this.recoverField_d, this.recoverField_b);
            boolean var4 = RecoverType9b4c5aeb.recoverMethod_a_d8c46bdb().recoverMethod_a_67c6b152(this.recoverField_d, -1, var2, null, null);
            this.recoverMethod_a_2563266(var1, var4);
         case 3:
         default:
            RecoverType2e4d193c.recoverMethod_a_c6e61809(this.recoverField_d, "cn.jpush.android.intent.IN_APP_MSG_CLICK", this.recoverField_b, null, var2);
            if (null != this.recoverField_a) {
               this.recoverField_a.recoverMethod_a_e2da561b(this.recoverField_d, this.recoverMethod_e_d08d2077(), this.recoverField_b);
            }

            this.recoverMethod_d_13462e(var3);
      }
   }

   private void recoverMethod_a_2563266(int var1, boolean var2) {
      long var3 = -1L;
      switch (var1) {
         case 1:
            if (var2) {
               var3 = 4004L;
            } else {
               var3 = 4003L;
            }
            break;
         case 2:
            if (var2) {
               var3 = 4002L;
            } else {
               var3 = 4001L;
            }
      }

      this.recoverMethod_a_1349ef(var3);
   }

   private void recoverMethod_d_13462e(int var1) {
      long var2 = -1L;
      switch (var1) {
         case 1:
            var2 = 4000L;
            break;
         case 2:
            var2 = 4011L;
            break;
         case 3:
            var2 = 4012L;
            break;
         case 4:
            var2 = 4014L;
            break;
         case 5:
            var2 = 4013L;
      }

      this.recoverMethod_a_1349ef(var2);
   }

   private void recoverMethod_a_1349ef(long var1) {
      Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "report code=" + var1);
      if (var1 > 0L) {
         RecoverType8b455831.recoverMethod_a_510e8a7c(this.recoverField_d, this.recoverField_b.recoverField_d, var1, 4);
      }
   }

   private int recoverMethod_e_134621(int var1) {
      switch (var1) {
         case 1:
            return 17;
         case 2:
            return 3;
         case 3:
            return 5;
         default:
            return 17;
      }
   }

   protected void recoverMethod_a_94f08c1e(ImageView var1, String var2, JSONObject var3, final int var4, int var5, int var6) {
      try {
         if (!TextUtils.isEmpty(var2)) {
            var1.setImageBitmap(RecoverType9b4c5ae9.recoverMethod_a_295fb07d(var2, var5, var6));
         }

         if (var3 != null) {
            final int var7 = var3.optInt("type");
            final String var8 = var3.optString("action");
            var1.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View var1) {
                  RecoverType2e4d0dfc.this.recoverMethod_a_9ea0018b(var7, var8, var4);
               }
            });
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "native updateImage throwable=" + var9);
      }
   }
}
