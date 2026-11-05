package io.intercom.android.sdk.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;

public class OrderedListSpan implements LeadingMarginSpan {
   private final int gapWidth;
   private final String number;

   public OrderedListSpan(int var1, String var2) {
      this.gapWidth = var1;
      this.number = var2;
   }

   @Override
   public int getLeadingMargin(boolean var1) {
      return (int)(new Paint().measureText(this.number) + this.gapWidth);
   }

   @Override
   public void drawLeadingMargin(
      Canvas var1, Paint var2, int var3, int var4, int var5, int var6, int var7, CharSequence var8, int var9, int var10, boolean var11, Layout var12
   ) {
      if (((Spanned)var8).getSpanStart(this) == var9) {
         Style var13 = var2.getStyle();
         var2.setStyle(Style.FILL);
         float var14 = var3;
         if (var4 == -1) {
            var14 = var3 - var2.measureText(this.number);
         }

         var1.drawText(this.number + " ", var14, var6, var2);
         var2.setStyle(var13);
      }
   }
}
