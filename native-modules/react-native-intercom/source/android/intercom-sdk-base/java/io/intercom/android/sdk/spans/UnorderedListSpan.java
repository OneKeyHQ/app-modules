package io.intercom.android.sdk.spans;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

public class UnorderedListSpan implements LeadingMarginSpan {
   private static final int INDENT_IN_DP = 5;
   private static final int BULLET_RADIUS_IN_DP = 2;
   private final int indent;
   private final int bulletRadius;
   private final int gapWidth;

   public UnorderedListSpan(int var1, Context var2) {
      this.gapWidth = var1;
      this.bulletRadius = ScreenUtils.dpToPx(2.0F, var2);
      this.indent = ScreenUtils.dpToPx(5.0F, var2);
   }

   @Override
   public int getLeadingMargin(boolean var1) {
      return 2 * this.bulletRadius + this.gapWidth;
   }

   @Override
   public void drawLeadingMargin(
      Canvas var1, Paint var2, int var3, int var4, int var5, int var6, int var7, CharSequence var8, int var9, int var10, boolean var11, Layout var12
   ) {
      if (((Spanned)var8).getSpanStart(this) == var9) {
         Style var13 = var2.getStyle();
         var2.setStyle(Style.FILL);
         var1.drawCircle(var3 + var4 * (this.bulletRadius + this.indent), (var5 + var7) / 2.0F, this.bulletRadius, var2);
         var2.setStyle(var13);
      }
   }
}
