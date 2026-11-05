package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import coil.ImageLoader;
import coil.ImageLoaders;
import coil.request.ImageRequest;
import coil.request.ImageResult;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a\u001a\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a \u0010\b\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0080@¢\u0006\u0002\u0010\t¨\u0006\n",
   d2 = {
         "loadIntercomImage",
         "",
         "context",
         "Landroid/content/Context;",
         "imageRequest",
         "Lcoil/request/ImageRequest;",
         "loadIntercomImageBlocking",
         "Landroid/graphics/drawable/Drawable;",
         "loadIntercomImageSuspend",
         "(Landroid/content/Context;Lcoil/request/ImageRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
public final class IntercomCoilKt {
   public static final void loadIntercomImage(@NotNull Context var0, @NotNull ImageRequest var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "imageRequest");
      IntercomImageLoaderKt.getImageLoader(var0).enqueue(var1);
   }

   @Nullable
   public static final Drawable loadIntercomImageBlocking(@NotNull Context var0, @NotNull ImageRequest var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "imageRequest");
      return ImageLoaders.executeBlocking(IntercomImageLoaderKt.getImageLoader(var0), var1).getDrawable();
   }

   @Nullable
   public static final Object loadIntercomImageSuspend(@NotNull Context var0, @NotNull ImageRequest var1, @NotNull Continuation var2) {
      IntercomCoilKt$loadIntercomImageSuspend$1 var4;
      label20: {
         if (var2 instanceof IntercomCoilKt$loadIntercomImageSuspend$1) {
            var4 = (IntercomCoilKt$loadIntercomImageSuspend$1)var2;
            if ((var4.label & -2147483648) != 0) {
               var4.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         var4 = new IntercomCoilKt$loadIntercomImageSuspend$1(var2);
      }

      Object var3 = var4.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var4.label) {
         case 0:
            ResultKt.throwOnFailure(var3);
            ImageLoader var6 = IntercomImageLoaderKt.getImageLoader(var0);
            var4.label = 1;
            var10000 = var6.execute(var1, var4);
            if (var10000 == var5) {
               return var5;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var3);
            var10000 = var3;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return ((ImageResult)var10000).getDrawable();
   }
}
