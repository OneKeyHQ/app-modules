package io.intercom.android.sdk.ui.common;

import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.ui.unit.Density;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000)\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J$\u0010\u0002\u001a\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016¨\u0006\f",
   d2 = {
         "io/intercom/android/sdk/ui/common/IntercomArrangement$itemAtBottom$1",
         "Landroidx/compose/foundation/layout/Arrangement$Vertical;",
         "arrange",
         "",
         "Landroidx/compose/ui/unit/Density;",
         "totalSize",
         "",
         "sizes",
         "",
         "outPositions",
         "toString",
         "",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomArrangement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArrangement.kt\nio/intercom/android/sdk/ui/common/IntercomArrangement$itemAtBottom$1\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,35:1\n13567#2,3:36\n*S KotlinDebug\n*F\n+ 1 IntercomArrangement.kt\nio/intercom/android/sdk/ui/common/IntercomArrangement$itemAtBottom$1\n*L\n22#1:36,3\n*E\n"
)
public final class IntercomArrangement$itemAtBottom$1 implements Vertical {
   // $VF: synthetic field
   final int $itemIndex;

   IntercomArrangement$itemAtBottom$1(int var1) {
      this.$itemIndex = var1;
   }

   @Override
   public void arrange(Density var1, int var2, int[] var3, int[] var4) {
      Intrinsics.checkNotNullParameter(var1, "<this>");
      Intrinsics.checkNotNullParameter(var3, "sizes");
      Intrinsics.checkNotNullParameter(var4, "outPositions");
      int var5 = 0;
      int[] var6 = var3;
      int var7 = this.$itemIndex;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;

      for (int var11 = var6.length; var10 < var11; var10++) {
         int var12 = var6[var10];
         int var10000 = var9++;
         int var13 = var12;
         int var14 = var10000;
         int var15 = 0;
         if (var14 == var7) {
            var4[var14] = var2 - var13;
         } else {
            var4[var14] = var5;
            var5 += var13;
         }
      }
   }

   @Override
   public String toString() {
      return "Arrangement#itemAtBottom(" + this.$itemIndex + ')';
   }
}
