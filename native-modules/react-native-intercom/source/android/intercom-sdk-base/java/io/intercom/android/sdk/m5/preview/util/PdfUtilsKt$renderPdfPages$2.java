package io.intercom.android.sdk.m5.preview.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.pdf.PdfRenderer;
import android.graphics.pdf.PdfRenderer.Page;
import android.os.ParcelFileDescriptor;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "PdfUtils.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.util.PdfUtilsKt$renderPdfPages$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "Landroid/graphics/Bitmap;", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nPdfUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PdfUtils.kt\nio/intercom/android/sdk/m5/preview/util/PdfUtilsKt$renderPdfPages$2\n+ 2 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n*L\n1#1,207:1\n83#2,6:208\n*S KotlinDebug\n*F\n+ 1 PdfUtils.kt\nio/intercom/android/sdk/m5/preview/util/PdfUtilsKt$renderPdfPages$2\n*L\n158#1:208,6\n*E\n"
)
final class PdfUtilsKt$renderPdfPages$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ParcelFileDescriptor $fileDescriptor;

   PdfUtilsKt$renderPdfPages$2(ParcelFileDescriptor var1, Continuation var2) {
      super(2, var2);
      this.$fileDescriptor = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            List var2 = new ArrayList();
            PdfRenderer var3 = new PdfRenderer(this.$fileDescriptor);

            try {
               int var4 = 0;

               for (int var5 = var3.getPageCount(); var4 < var5; var4++) {
                  Page var10000 = var3.openPage(var4);
                  Intrinsics.checkNotNullExpressionValue(var10000, "openPage(...)");
                  Page var6 = var10000;
                  int var8 = var6.getWidth();
                  int var9 = var6.getHeight();
                  Config var10 = Config.ARGB_8888;
                  int var11 = 0;
                  Bitmap var7 = Bitmap.createBitmap(var8, var9, var10);
                  Canvas var14 = new Canvas(var7);
                  var14.drawColor(-1);
                  var6.render(var7, null, null, 1);
                  var2.add(var7);
                  var6.close();
               }
            } finally {
               var3.close();
            }

            return var2;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new PdfUtilsKt$renderPdfPages$2(this.$fileDescriptor, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PdfUtilsKt$renderPdfPages$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
