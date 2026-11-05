package io.intercom.android.sdk.blocks;

import android.text.TextUtils;
import android.view.View;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.utilities.LinkOpener;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J0\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/blocks/CarouselImageClickListener;",
         "Lio/intercom/android/sdk/blocks/ImageClickListener;",
         "api",
         "Lio/intercom/android/sdk/api/Api;",
         "<init>",
         "(Lio/intercom/android/sdk/api/Api;)V",
         "onImageClicked",
         "",
         "imageUrl",
         "",
         "linkUrl",
         "imageView",
         "Landroid/view/View;",
         "width",
         "",
         "height",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class CarouselImageClickListener implements ImageClickListener {
   @NotNull
   private final Api api;
   public static final int $stable;

   static { $stable = 8; }

   public CarouselImageClickListener(@NotNull Api var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "api");
      this.api = var1;
   }

   @Override
   public void onImageClicked(@NotNull String var1, @NotNull String var2, @NotNull View var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "imageUrl");
      Intrinsics.checkNotNullParameter(var2, "linkUrl");
      Intrinsics.checkNotNullParameter(var3, "imageView");
      if (!TextUtils.isEmpty(var2)) {
         LinkOpener.handleUrl(var2, var3.getContext(), this.api);
      }
   }
}
