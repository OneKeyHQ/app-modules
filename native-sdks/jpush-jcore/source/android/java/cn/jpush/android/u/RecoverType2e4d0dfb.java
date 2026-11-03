package cn.jpush.android.u;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.ui.RoundedImageView;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4d0dfb extends RecoverType2e4d0dfc {
   private TextView recoverField_g;
   private TextView recoverField_h;
   private TextView recoverField_i;
   private TextView recoverField_j;
   private ImageView recoverField_k;
   private ImageView recoverField_l;
   private View recoverField_m;
   private View recoverField_n;
   private View recoverField_o;
   private View recoverField_p;
   private FrameLayout recoverField_q;
   private RoundedImageView recoverField_r;
   private RoundedImageView recoverField_s;

   public RecoverType2e4d0dfb(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected void recoverMethod_a_9b75() {
      this.recoverField_r = (RoundedImageView)this.recoverMethod_b_edf6881("image");
      this.recoverField_l = (ImageView)this.recoverMethod_b_edf6881("img_bottom_close");
      this.recoverField_k = (ImageView)this.recoverMethod_b_edf6881("img_top_close");
      this.recoverField_s = (RoundedImageView)this.recoverMethod_b_edf6881("image_only");
      this.recoverField_j = (TextView)this.recoverMethod_b_edf6881("btn_two");
      this.recoverField_i = (TextView)this.recoverMethod_b_edf6881("btn_one");
      this.recoverField_h = (TextView)this.recoverMethod_b_edf6881("text_content");
      this.recoverField_g = (TextView)this.recoverMethod_b_edf6881("text_title");
      this.recoverField_m = this.recoverMethod_b_edf6881("content_view");
      this.recoverField_q = (FrameLayout)this.recoverMethod_b_edf6881("frame");
      this.recoverField_n = this.recoverMethod_b_edf6881("margeview");
      this.recoverField_o = this.recoverMethod_b_edf6881("bg_view");
      this.recoverField_p = this.recoverMethod_b_edf6881("btn_parent_view");
      this.recoverField_h.setMovementMethod(ScrollingMovementMethod.getInstance());
      this.recoverField_h.setMaxHeight(this.recoverMethod_b_134621(150));
   }

   @Override
   protected String recoverMethod_b_73cf11cb() {
      return "jpush_interstitial";
   }

   @Override
   public void recoverMethod_c_9b75() {
      if (this.recoverField_f != null) {
         this.recoverField_f = null;
      }
   }

   @Override
   public void recoverMethod_d_9b75() {
      try {
         JSONObject var1 = new JSONObject(this.recoverField_b.recoverField_i);
         int var2 = this.recoverMethod_a_134621(30);
         LayoutParams var3 = (LayoutParams)this.recoverField_q.getLayoutParams();
         var3.leftMargin = var2;
         var3.rightMargin = var2;
         this.recoverField_q.setLayoutParams(var3);
         LayoutParams var4 = (LayoutParams)this.recoverField_k.getLayoutParams();
         var4.rightMargin = var2;
         this.recoverField_k.setLayoutParams(var4);
         if (21 == this.recoverField_e) {
            this.recoverField_s.setVisibility(0);
            android.widget.FrameLayout.LayoutParams var5 = (android.widget.FrameLayout.LayoutParams)this.recoverField_s.getLayoutParams();
            var5.width = this.recoverMethod_a_134621(315);
            var5.height = this.recoverMethod_b_134621(292);
            this.recoverField_s.setLayoutParams(var5);
            byte var6 = 45;
            this.recoverField_r.setRadius(var6, var6, var6, var6);
            this.recoverField_m.setVisibility(8);
            if (var1.has("image")) {
               String var7 = var1.getString("image");
               JSONObject var8 = var1.optJSONObject("click");
               this.recoverMethod_a_94f08c1e(this.recoverField_s, var7, var8, 1, this.recoverMethod_a_134621(315), this.recoverMethod_b_134621(292));
            }
         } else {
            this.recoverField_s.setVisibility(8);
            this.recoverField_m.setVisibility(0);
            android.widget.LinearLayout.LayoutParams var11 = (android.widget.LinearLayout.LayoutParams)this.recoverField_n.getLayoutParams();
            var11.leftMargin = this.recoverMethod_a_134621(22);
            var11.rightMargin = this.recoverMethod_a_134621(21);
            this.recoverField_n.setLayoutParams(var11);
            if (var1.has("image")) {
               String var13 = var1.optString("image");
               if (!TextUtils.isEmpty(var13)) {
                  this.recoverField_r.setVisibility(0);
                  JSONObject var18 = var1.optJSONObject("click");
                  this.recoverMethod_a_94f08c1e(this.recoverField_r, var13, var18, 1, this.recoverMethod_a_134621(22), this.recoverMethod_a_134621(22));
                  android.widget.LinearLayout.LayoutParams var22 = (android.widget.LinearLayout.LayoutParams)this.recoverField_r.getLayoutParams();
                  var22.height = this.recoverMethod_b_134621(142);
                  var22.width = this.recoverMethod_a_134621(315);
                  this.recoverField_r.setLayoutParams(var22);
                  byte var9 = 45;
                  this.recoverField_r.setRadius(var9, var9, 0, 0);
               } else {
                  this.recoverMethod_l_9b75();
               }
            } else {
               this.recoverMethod_l_9b75();
            }
         }

         JSONObject var12 = var1.getJSONObject("interstitial");
         if (var12.has("title")) {
            JSONObject var14 = var12.optJSONObject("title");
            if (var14 == null) {
               return;
            }

            this.recoverField_g.setVisibility(0);
            this.recoverMethod_a_3e1c1434(this.recoverField_g, var14, -1);
         } else {
            this.recoverField_g.setVisibility(8);
         }

         if (var12.has("content")) {
            JSONObject var15 = var12.optJSONObject("content");
            if (var15 == null) {
               return;
            }

            this.recoverField_h.setVisibility(0);
            this.recoverMethod_a_3e1c1434(this.recoverField_h, var15, -1);
         } else {
            this.recoverField_h.setVisibility(8);
         }

         if (var12.has("close_location")) {
            int var16 = var12.optInt("close_location", 1);
            ImageView var19 = null;
            if (var16 == 1) {
               var19 = this.recoverField_l;
            } else if (var16 == 2) {
               var19 = this.recoverField_k;
            }

            if (null == var19) {
               return;
            }

            var19.setVisibility(0);
            var19.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View var1) {
                  RecoverType2e4d0dfb.this.recoverMethod_a_9ea0018b(3, "", 2);
               }
            });
         } else {
            this.recoverField_l.setVisibility(8);
            this.recoverField_k.setVisibility(8);
         }

         if (var12.has("buttons")) {
            JSONArray var17 = var12.optJSONArray("buttons");
            if (var17 == null || var17.length() <= 0) {
               this.recoverField_p.setVisibility(8);
               return;
            }

            this.recoverField_p.setVisibility(0);
            if (var17.length() > 1) {
               this.recoverField_i.setVisibility(0);
               JSONObject var20 = (JSONObject)var17.get(0);
               this.recoverMethod_a_3e1c1434(this.recoverField_i, var20, 4);
               JSONObject var23 = (JSONObject)var17.get(1);
               this.recoverMethod_a_3e1c1434(this.recoverField_j, var23, 5);
            } else {
               this.recoverField_i.setVisibility(8);
               JSONObject var21 = (JSONObject)var17.get(0);
               this.recoverMethod_a_3e1c1434(this.recoverField_j, var21, 5);
            }
         } else {
            this.recoverField_p.setVisibility(8);
         }
      } catch (Throwable var10) {
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "interstitial bindDataToView throwable=" + var10);
      }
   }

   private void recoverMethod_l_9b75() {
      this.recoverField_r.setVisibility(8);
      GradientDrawable var1 = (GradientDrawable)this.recoverField_o.getBackground();
      var1.mutate();
      var1.setCornerRadius(RecoverType9b4c5af2.recoverMethod_a_1a54e363(this.recoverField_d, 15));
   }
}
