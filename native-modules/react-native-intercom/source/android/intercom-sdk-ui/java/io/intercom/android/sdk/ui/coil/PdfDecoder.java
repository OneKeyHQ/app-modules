package io.intercom.android.sdk.ui.coil;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;
import android.os.ParcelFileDescriptor;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.content.ContextCompat;
import coil.decode.DecodeResult;
import coil.decode.DecodeUtils;
import coil.decode.Decoder;
import coil.decode.ImageSource;
import coil.request.Options;
import coil.size.RecoveredType41ccb0b20d2666ed;
import coil.size.Scale;
import coil.size.Size;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.Ref.BooleanRef;
import kotlin.math.MathKt;
import kotlinx.coroutines.InterruptibleKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001:\u0001\u000bB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\b\u001a\u00020\tH\u0096@¢\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/ui/coil/PdfDecoder;",
         "Lcoil/decode/Decoder;",
         "source",
         "Lcoil/decode/ImageSource;",
         "options",
         "Lcoil/request/Options;",
         "<init>",
         "(Lcoil/decode/ImageSource;Lcoil/request/Options;)V",
         "decode",
         "Lcoil/decode/DecodeResult;",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "Factory",
         "intercom-sdk-ui_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nPdfDecoder.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PdfDecoder.kt\nio/intercom/android/sdk/ui/coil/PdfDecoder\n+ 2 PdfDecoder.kt\nio/intercom/android/sdk/ui/coil/PdfDecoderKt\n+ 3 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n+ 4 BitmapDrawable.kt\nandroidx/core/graphics/drawable/BitmapDrawableKt\n*L\n1#1,140:1\n130#2:141\n132#2:142\n130#2:150\n132#2:151\n83#3,6:143\n83#3,6:152\n27#4:149\n27#4:158\n*S KotlinDebug\n*F\n+ 1 PdfDecoder.kt\nio/intercom/android/sdk/ui/coil/PdfDecoder\n*L\n46#1:141\n47#1:142\n81#1:150\n82#1:151\n70#1:143,6\n85#1:152,6\n75#1:149\n99#1:158\n*E\n"
)
public final class PdfDecoder implements Decoder {
   @NotNull
   private final ImageSource source;
   @NotNull
   private final Options options;
   public static final int $stable;

   static { $stable = 8; }

   public PdfDecoder(@NotNull ImageSource var1, @NotNull Options var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "source");
      Intrinsics.checkNotNullParameter(var2, "options");
      this.source = var1;
      this.options = var2;
   }

   @Nullable
   @Override
   public Object decode(@NotNull Continuation var1) {
      PdfDecoder$decode$1 var5;
      label20: {
         if (var1 instanceof PdfDecoder$decode$1) {
            var5 = (PdfDecoder$decode$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         var5 = new PdfDecoder$decode$1(this, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      BooleanRef var2;
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            var2 = new BooleanRef();
            final io.intercom.android.sdk.ui.coil.PdfDecoder recoveredCapture_0_0 = this;
            final kotlin.jvm.internal.Ref.BooleanRef recoveredCapture_0_1 = var2;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> PdfDecoder.decode$lambda$5(recoveredCapture_0_0, recoveredCapture_0_1));
            var5.L$0 = var2;
            var5.label = 1;
            var10000 = InterruptibleKt.runInterruptible$default(null, var10001, var5, 1, null);
            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            var2 = (BooleanRef)var5.L$0;
            ResultKt.throwOnFailure(var4);
            var10000 = var4;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      BitmapDrawable var3 = (BitmapDrawable)var10000;
      return new DecodeResult(var3, var2.element);
   }

   private static final BitmapDrawable decode$lambda$5(PdfDecoder var0, BooleanRef var1) {
      ParcelFileDescriptor var2 = null;

      BitmapDrawable var3;
      try {
         var2 = ParcelFileDescriptor.open(var0.source.file().toFile(), 268435456);
         PdfRenderer var20 = new PdfRenderer(var2);
         Page var57 = var20.openPage(0);
         Intrinsics.checkNotNullExpressionValue(var57, "openPage(...)");
         Page var4 = var57;
         int var21 = var4.getWidth();
         int var23 = var4.getHeight();
         Size var30 = var0.options.getSize();
         Scale var35 = var0.options.getScale();
         int var39 = 0;
         int var58;
         if (RecoveredType41ccb0b20d2666ed.isOriginal(var30)) {
            int var43 = 0;
            var58 = var21;
         } else {
            var58 = PdfDecoderKt.access$toPx(var30.getWidth(), var35);
         }

         int var26 = var58;
         Size var36 = var0.options.getSize();
         Scale scale$ivx = var0.options.getScale();
         int var44 = 0;
         int var59;
         if (RecoveredType41ccb0b20d2666ed.isOriginal(var36)) {
            int var47 = 0;
            var59 = var23;
         } else {
            var59 = PdfDecoderKt.access$toPx(var36.getHeight(), scale$ivx);
         }

         int var31 = var59;
         int var37 = var21;
         var39 = var23;
         if (var21 > 0 && var23 > 0 && (var21 != var26 || var23 != var31)) {
            double var45 = DecodeUtils.computeSizeMultiplier(var21, var23, var26, var31, var0.options.getScale());
            var1.element = var45 < 1.0;
            if (var1.element || !var0.options.getAllowInexactSize()) {
               var37 = MathKt.roundToInt(var45 * var21);
               var39 = MathKt.roundToInt(var45 * var23);
            }
         }

         Config var48 = Config.ARGB_8888;
         int var50 = 0;
         Bitmap var46 = Bitmap.createBitmap(var37, var39, var48);
         Canvas var49 = new Canvas(var46);
         var49.drawColor(-1);
         var4.render(var46, null, null, 1);
         Bitmap var51 = var46;
         Resources var60 = var0.options.getContext().getResources();
         Intrinsics.checkNotNullExpressionValue(var60, "getResources(...)");
         Resources var52 = var60;
         int var53 = 0;
         var3 = new BitmapDrawable(var52, var51);
      } catch (SecurityException var18) {
         Size var6 = var0.options.getSize();
         Scale var7 = var0.options.getScale();
         int var8 = 0;
         int var10000;
         if (RecoveredType41ccb0b20d2666ed.isOriginal(var6)) {
            int var9 = 0;
            var10000 = 512;
         } else {
            var10000 = PdfDecoderKt.access$toPx(var6.getWidth(), var7);
         }

         int var5 = var10000;
         Size var24 = var0.options.getSize();
         Scale scale$ivx = var0.options.getScale();
         int var32 = 0;
         if (RecoveredType41ccb0b20d2666ed.isOriginal(var24)) {
            int var10 = 0;
            var10000 = 512;
         } else {
            var10000 = PdfDecoderKt.access$toPx(var24.getHeight(), scale$ivx);
         }

         int var22 = var10000;
         Config var28 = Config.ARGB_8888;
         var32 = 0;
         Bitmap var25 = Bitmap.createBitmap(var5, var22, var28);
         Canvas var29 = new Canvas(var25);
         var29.drawColor(-1);
         Drawable var55 = ContextCompat.getDrawable(var0.options.getContext(), drawable.intercom_ic_document);
         if (var55 != null) {
            Drawable var11 = var55;
            int var12 = 0;
            int var13 = Math.min(var5, var22) / 2;
            int var14 = (var5 - var13) / 2;
            int var15 = (var22 - var13) / 2;
            var11.setBounds(var14, var15, var14 + var13, var15 + var13);
            var11.draw(var29);
         }

         Bitmap var34 = var25;
         Resources var56 = var0.options.getContext().getResources();
         Intrinsics.checkNotNullExpressionValue(var56, "getResources(...)");
         Resources var38 = var56;
         int var42 = 0;
         var3 = new BitmapDrawable(var38, var34);
      } finally {
         if (var2 != null) {
            var2.close();
         }
      }

      return var3;
   }
}
