package io.intercom.android.sdk.m5.preview.util;

import android.content.Context;
import android.os.ParcelFileDescriptor;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import java.io.File;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0080@¢\u0006\u0002\u0010\u0006\u001a\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0080@¢\u0006\u0002\u0010\f\u001a\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0080@¢\u0006\u0002\u0010\u0006¨\u0006\u000f",
   d2 = {
         "openPdfFile",
         "Lio/intercom/android/sdk/m5/preview/util/PdfOpenResult;",
         "context",
         "Landroid/content/Context;",
         "file",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "(Landroid/content/Context;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "renderPdfPages",
         "",
         "Landroid/graphics/Bitmap;",
         "fileDescriptor",
         "Landroid/os/ParcelFileDescriptor;",
         "(Landroid/os/ParcelFileDescriptor;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "shouldOpenPdfInBrowser",
         "",
         "intercom-sdk-base_release"
   }
)
public final class PdfUtilsKt {
   @Nullable
   public static final Object openPdfFile(@NotNull Context var0, @NotNull IntercomPreviewFile var1, @NotNull Continuation var2) {
      return BuildersKt.withContext(Dispatchers.getIO(), new PdfUtilsKt$openPdfFile$2(var1, var0, null), var2);
   }

   @Nullable
   public static final Object renderPdfPages(@NotNull ParcelFileDescriptor var0, @NotNull Continuation var1) {
      return BuildersKt.withContext(Dispatchers.getIO(), new PdfUtilsKt$renderPdfPages$2(var0, null), var1);
   }

   @Nullable
   public static final Object shouldOpenPdfInBrowser(@NotNull Context var0, @NotNull IntercomPreviewFile var1, @NotNull Continuation var2) {
      PdfUtilsKt$shouldOpenPdfInBrowser$1 var5;
      label37: {
         if (var2 instanceof PdfUtilsKt$shouldOpenPdfInBrowser$1) {
            var5 = (PdfUtilsKt$shouldOpenPdfInBrowser$1)var2;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label37;
            }
         }

         var5 = new PdfUtilsKt$shouldOpenPdfInBrowser$1(var2);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            var5.label = 1;
            var10000 = openPdfFile(var0, var1, var5);
            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var4);
            var10000 = var4;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      PdfOpenResult var3 = (PdfOpenResult)var10000;
      boolean var7;
      if (var3 instanceof PdfOpenResult$PasswordProtected) {
         var7 = true;
      } else if (var3 instanceof PdfOpenResult$Success) {
         ((PdfOpenResult$Success)var3).getFileDescriptor().close();
         File var8 = ((PdfOpenResult$Success)var3).getTempFile();
         if (var8 != null) {
            Boxing.boxBoolean(var8.delete());
         }

         var7 = false;
      } else {
         if (!(var3 instanceof PdfOpenResult$Error)) {
            throw new NoWhenBranchMatchedException();
         }

         var7 = true;
      }

      return Boxing.boxBoolean(var7);
   }
}
