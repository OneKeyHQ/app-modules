package cn.jpush.android.u;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.ui.RoundedImageView;
import org.json.JSONObject;

public class RecoverType2e4d0df8 extends RecoverType2e4d0dfc {
   private View recoverField_g;
   private RoundedImageView recoverField_h;
   private RoundedImageView recoverField_i;
   private TextView recoverField_j;
   private TextView recoverField_k;
   private View recoverField_l;
   private View recoverField_m;

   public RecoverType2e4d0df8(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void recoverMethod_a_9b75() {
      this.recoverField_g = this.recoverMethod_b_edf6881("banner_content");
      this.recoverField_h = (RoundedImageView)this.recoverMethod_b_edf6881("image_only");
      this.recoverField_j = (TextView)this.recoverMethod_b_edf6881("text_content");
      this.recoverField_k = (TextView)this.recoverMethod_b_edf6881("text_title");
      this.recoverField_i = (RoundedImageView)this.recoverMethod_b_edf6881("image_small");
      this.recoverField_l = this.recoverMethod_b_edf6881("banner");
      this.recoverField_m = this.recoverMethod_b_edf6881("bg_view");
   }

   @Override
   protected String recoverMethod_b_73cf11cb() {
      return "jpush_banner";
   }

   @Override
   public void recoverMethod_c_9b75() {
   }

   @Override
   public void recoverMethod_d_9b75() {
      try {
         JSONObject var1 = new JSONObject(this.recoverField_b.recoverField_i);
         JSONObject var2 = var1.optJSONObject("click");
         LayoutParams var3 = (LayoutParams)this.recoverField_l.getLayoutParams();
         var3.leftMargin = this.recoverMethod_a_134621(14);
         var3.width = this.recoverMethod_a_134621(345);
         var3.rightMargin = this.recoverMethod_a_134621(14);
         if (var2 != null) {
            final int var4 = var2.optInt("type");
            final String var5 = var2.optString("action");
            this.recoverField_f.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View var1) {
                  RecoverType2e4d0df8.this.recoverMethod_a_9ea0018b(var4, var5, 1);
               }
            });
         }

         if (11 == this.recoverField_e) {
            var3.height = this.recoverMethod_b_134621(92);
            this.recoverField_l.setLayoutParams(var3);
            this.recoverField_m.setVisibility(8);
            this.recoverField_h.setVisibility(0);
            String var11 = var1.getString("image");
            byte var13 = 45;
            this.recoverField_h.setRadius(var13, var13, var13, var13);
            this.recoverMethod_a_94f08c1e(this.recoverField_h, var11, null, -1, -1, -1);
         } else {
            this.recoverField_m.setVisibility(0);
            this.recoverField_h.setVisibility(8);
            this.recoverField_l.setLayoutParams(var3);
            android.widget.RelativeLayout.LayoutParams var12 = (android.widget.RelativeLayout.LayoutParams)this.recoverField_g.getLayoutParams();
            var12.leftMargin = this.recoverMethod_a_134621(17);
            var12.rightMargin = this.recoverMethod_a_134621(19);
            var12.topMargin = this.recoverMethod_b_134621(15);
            var12.bottomMargin = this.recoverMethod_b_134621(12);
            this.recoverField_g.setLayoutParams(var12);
            JSONObject var14 = var1.getJSONObject("banner");

            try {
               if (var14.has("small_image")) {
                  String var6 = var14.optString("small_image");
                  if (!TextUtils.isEmpty(var6)) {
                     this.recoverField_i.setVisibility(0);
                     android.widget.RelativeLayout.LayoutParams var7 = (android.widget.RelativeLayout.LayoutParams)this.recoverField_i.getLayoutParams();
                     var7.width = this.recoverMethod_a_134621(42);
                     var7.height = this.recoverMethod_a_134621(42);
                     this.recoverField_i.setLayoutParams(var7);
                     byte var8 = 0;
                     this.recoverField_i.setRadius(var8, var8, var8, var8);
                     this.recoverMethod_a_94f08c1e(this.recoverField_i, var6, null, -1, -1, -1);
                  } else {
                     this.recoverField_i.setVisibility(8);
                  }
               } else {
                  this.recoverField_i.setVisibility(8);
               }

               JSONObject var15 = var14.getJSONObject("title");
               this.recoverMethod_a_3e1c1434(this.recoverField_k, var15, -1);
               JSONObject var16 = var14.getJSONObject("content");
               this.recoverMethod_a_3e1c1434(this.recoverField_j, var16, -1);
            } catch (Throwable var9) {
               Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "banner bindDataToView throwable=" + var9);
            }
         }
      } catch (Throwable var10) {
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "banner bindDataToView throwable=" + var10);
      }
   }
}
