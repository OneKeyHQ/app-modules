package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

public class BlockUtils {
   private static final int DEFAULT_MARGIN_BOTTOM_DP = 10;
   private static final int SMALL_LINE_SPACING_DP = 2;
   private static final int LARGE_LINE_SPACING_DP = 4;

   public static void createLayoutParams(View var0, int var1, int var2) {
      var0.setLayoutParams(new LayoutParams(var1, var2));
   }

   public static void setDefaultMarginBottom(View var0) {
      setMarginBottom(var0, 10);
   }

   public static void setMarginBottom(View var0, int var1) {
      MarginLayoutParams var2 = (MarginLayoutParams)var0.getLayoutParams();
      var2.bottomMargin = ScreenUtils.dpToPx(var1, var0.getContext());
   }

   public static void setMarginLeft(View var0, int var1) {
      MarginLayoutParams var2 = (MarginLayoutParams)var0.getLayoutParams();
      var2.setMarginStart(ScreenUtils.dpToPx(var1, var0.getContext()));
   }

   public static void setLayoutMarginsAndGravity(View var0, int var1, boolean var2) {
      LayoutParams var3 = (LayoutParams)var0.getLayoutParams();
      if (var2) {
         var3.setMargins(var3.leftMargin, var3.topMargin, var3.rightMargin, 0);
      }

      var3.gravity = var1;
   }

   public static void setSmallLineSpacing(TextView var0) {
      setLineSpacing(var0, 2);
   }

   public static void setLargeLineSpacing(TextView var0) {
      setLineSpacing(var0, 4);
   }

   private static void setLineSpacing(TextView var0, int var1) {
      var0.setLineSpacing(ScreenUtils.dpToPx(var1, var0.getContext()), 1.0F);
   }

   public static View getBlockView(ViewGroup var0, LinearLayout var1, Context var2) {
      LayoutInflater var3 = LayoutInflater.from(var2);
      View var4 = var3.inflate(layout.intercom_container_layout, var0, false);
      LinearLayout var5 = (LinearLayout)var4.findViewById(id.cellLayout);
      if (var1.getParent() != null) {
         ((LinearLayout)var1.getParent()).removeView(var1);
      }

      if (var5.getChildCount() > 0) {
         var5.removeAllViews();
      }

      var5.addView(var1, 0);
      var4.setFocusable(false);
      var5.setVisibility(0);
      return var4;
   }
}
