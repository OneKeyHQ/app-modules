package io.intercom.android.sdk.utilities;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.utilities.commons.TimeProvider;

public class TrackingLinkMovementMethod extends LinkMovementMethod {
   private boolean longClickActive = false;
   private boolean longClickPerformed = false;
   private long startClickTime;

   @Override
   public boolean onTouchEvent(TextView var1, Spannable var2, MotionEvent var3) {
      boolean var4 = false;
      int var5 = this.getPosition(var3, var1);
      URLSpan[] var6 = (URLSpan[])var2.getSpans(var5, var5, URLSpan.class);
      switch (var3.getAction()) {
         case 0:
            this.startClickTime = TimeProvider.SYSTEM.currentTimeMillis();
            if (!this.longClickActive) {
               this.longClickActive = true;
            }

            if (var6.length != 0) {
               Selection.setSelection(var2, var2.getSpanStart(var6[0]), var2.getSpanEnd(var6[0]));
            }
            break;
         case 1:
            this.longClickActive = false;
            if (!this.longClickPerformed) {
               if (var6.length != 0) {
                  String var9 = var6[0].getURL();
                  LinkOpener.handleUrl(var9, var1.getContext(), Injector.get().getApi());
               } else {
                  Selection.removeSelection(var2);
                  this.performClick((ViewGroup)var1.getParent());
               }

               var4 = true;
            }

            this.longClickPerformed = false;
            break;
         case 2:
            if (this.longClickActive) {
               long var7 = TimeProvider.SYSTEM.currentTimeMillis() - this.startClickTime;
               if (var7 >= ViewConfiguration.getLongPressTimeout()) {
                  this.longClickActive = false;
                  this.performLongClick((ViewGroup)var1.getParent());
                  var4 = true;
                  this.longClickPerformed = true;
               }
            }
      }

      return var4;
   }

   private void performClick(ViewGroup var1) {
      while (!var1.performClick()) {
         ViewParent var2 = var1.getParent();
         if (!(var2 instanceof ViewGroup)) {
            return;
         }

         var1 = (ViewGroup)var1.getParent();
      }
   }

   private void performLongClick(ViewGroup var1) {
      while (!var1.performLongClick()) {
         ViewParent var2 = var1.getParent();
         if (!(var2 instanceof ViewGroup)) {
            return;
         }

         var1 = (ViewGroup)var1.getParent();
      }
   }

   private int getPosition(MotionEvent var1, TextView var2) {
      int var3 = (int)var1.getX();
      int var4 = (int)var1.getY();
      var3 -= var2.getTotalPaddingLeft();
      var4 -= var2.getTotalPaddingTop();
      var3 += var2.getScrollX();
      var4 += var2.getScrollY();
      Layout var5 = var2.getLayout();
      int var6 = var5.getLineForVertical(var4);
      return var5.getOffsetForHorizontal(var6, var3);
   }
}
