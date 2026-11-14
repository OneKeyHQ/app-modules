package io.intercom.android.sdk.ui.coil;

import coil.size.Dimension;
import coil.size.RecoveredType41ccb0b20d2666ed;
import coil.size.Scale;
import coil.size.Size;
import coil.size.Dimension.Pixels;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a#\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0082\b\u001a#\u0010\u0007\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0082\b\u001a\u0014\u0010\b\u001a\u00020\u0001*\u00020\t2\u0006\u0010\u0003\u001a\u00020\u0004H\u0002¨\u0006\n",
   d2 = {
         "widthPx",
         "",
         "Lcoil/size/Size;",
         "scale",
         "Lcoil/size/Scale;",
         "original",
         "Lkotlin/Function0;",
         "heightPx",
         "toPx",
         "Lcoil/size/Dimension;",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPdfDecoder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PdfDecoder.kt\nio/intercom/android/sdk/ui/coil/PdfDecoderKt\n+ 2 Dimension.kt\ncoil/size/-Dimensions\n*L\n1#1,140:1\n57#2:141\n*S KotlinDebug\n*F\n+ 1 PdfDecoder.kt\nio/intercom/android/sdk/ui/coil/PdfDecoderKt\n*L\n134#1:141\n*E\n"
)
public final class PdfDecoderKt {
   private static final int widthPx(Size var0, Scale var1, Function0 var2) {
      int var3 = 0;
      return RecoveredType41ccb0b20d2666ed.isOriginal(var0) ? ((Number)var2.invoke()).intValue() : access$toPx(var0.getWidth(), var1);
   }

   private static final int heightPx(Size var0, Scale var1, Function0 var2) {
      int var3 = 0;
      return RecoveredType41ccb0b20d2666ed.isOriginal(var0) ? ((Number)var2.invoke()).intValue() : access$toPx(var0.getHeight(), var1);
   }

   private static final int toPx(Dimension var0, Scale var1) {
      Dimension var2 = var0;
      int var3 = 0;
      int var10000;
      if (var2 instanceof Pixels) {
         var10000 = ((Pixels)var2).px;
      } else {
         int var4 = 0;
         switch (PdfDecoderKt$WhenMappings.$EnumSwitchMapping$0[var1.ordinal()]) {
            case 1:
               var10000 = Integer.MIN_VALUE;
               break;
            case 2:
               var10000 = Integer.MAX_VALUE;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }

      return var10000;
   }

   // $VF: synthetic method
   public static final int access$toPx(Dimension var0, Scale var1) {
      return toPx(var0, var1);
   }
}
