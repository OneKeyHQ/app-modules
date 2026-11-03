package cn.jpush.android.u;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.t.RecoverType2e4d0a37;
import org.json.JSONObject;

public class RecoverType2e4d0dfa extends RecoverType2e4d0dfc {
   private ImageView recoverField_g;
   private ImageView recoverField_h;
   private TextView recoverField_i;
   private View recoverField_j;

   public RecoverType2e4d0dfa(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void recoverMethod_a_9b75() {
      this.recoverField_h = (ImageView)this.recoverMethod_b_edf6881("image");
      this.recoverField_i = (TextView)this.recoverMethod_b_edf6881("btn_countdown");
      this.recoverField_g = (ImageView)this.recoverMethod_b_edf6881("image_close");
      this.recoverField_j = this.recoverMethod_b_edf6881("countdown_container");
   }

   @Override
   protected String recoverMethod_b_73cf11cb() {
      return "jpush_full";
   }

   @Override
   public void recoverMethod_c_9b75() {
   }

   @Override
   public void recoverMethod_d_9b75() {
      try {
         JSONObject var1 = new JSONObject(this.recoverField_b.recoverField_i);
         if (31 == this.recoverField_e) {
            this.recoverField_g.setVisibility(0);
            this.recoverField_i.setVisibility(8);
            this.recoverField_g.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View var1) {
                  RecoverType2e4d0dfa.this.recoverMethod_a_9ea0018b(3, null, 2);
               }
            });
         } else {
            this.recoverField_g.setVisibility(8);
            this.recoverField_i.setVisibility(0);
            LayoutParams var2 = (LayoutParams)this.recoverField_j.getLayoutParams();
            var2.width = this.recoverMethod_a_134621(140);
            this.recoverField_j.setLayoutParams(var2);
            JSONObject var3 = var1.getJSONObject("full");
            this.recoverMethod_a_3e1c1434(this.recoverField_i, var3, 3);
            this.recoverField_i.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View var1) {
                  RecoverType2e4d0dfa.this.recoverMethod_a_9ea0018b(3, null, 3);
               }
            });
         }

         String var6 = var1.getString("image");
         JSONObject var7 = var1.optJSONObject("click");
         DisplayMetrics var4 = this.recoverField_d.getResources().getDisplayMetrics();
         this.recoverMethod_a_94f08c1e(this.recoverField_h, var6, var7, 1, var4.widthPixels, var4.heightPixels);
      } catch (Throwable var5) {
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "full bindDataToView throwable=" + var5);
      }
   }

   @Override
   public void recoverMethod_g_9b75() {
      this.recoverMethod_a_fb65286f(this.recoverField_i);
   }
}
