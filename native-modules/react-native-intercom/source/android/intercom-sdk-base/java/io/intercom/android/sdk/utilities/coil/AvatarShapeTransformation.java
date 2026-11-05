package io.intercom.android.sdk.utilities.coil;

import android.graphics.Bitmap;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.DensityKt;
import coil.size.Size;
import coil.transform.RoundedCornersTransformation;
import coil.transform.Transformation;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0096@¢\u0006\u0002\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00078VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\t¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/utilities/coil/AvatarShapeTransformation;",
         "Lcoil/transform/Transformation;",
         "avatarShape",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarShape;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/components/avatar/AvatarShape;)V",
         "cacheKey",
         "",
         "getCacheKey",
         "()Ljava/lang/String;",
         "transform",
         "Landroid/graphics/Bitmap;",
         "input",
         "size",
         "Lcoil/size/Size;",
         "(Landroid/graphics/Bitmap;Lcoil/size/Size;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nAvatarShapeTransformation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarShapeTransformation.kt\nio/intercom/android/sdk/utilities/coil/AvatarShapeTransformation\n+ 2 Size.kt\nandroidx/compose/ui/geometry/SizeKt\n+ 3 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n*L\n1#1,27:1\n33#2:28\n53#3,3:29\n*S KotlinDebug\n*F\n+ 1 AvatarShapeTransformation.kt\nio/intercom/android/sdk/utilities/coil/AvatarShapeTransformation\n*L\n17#1:28\n17#1:29,3\n*E\n"
)
public final class AvatarShapeTransformation implements Transformation {
   @NotNull
   private final AvatarShape avatarShape;
   public static int $stable;

   static {}

   public AvatarShapeTransformation(@NotNull AvatarShape var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "avatarShape");
      this.avatarShape = var1;
   }

   @NotNull
   @Override
   public String getCacheKey() {
      return this.avatarShape.name() + this.getClass().getName();
   }

   @Nullable
   @Override
   public Object transform(@NotNull Bitmap var1, @NotNull Size var2, @NotNull Continuation var3) {
      RoundedCornerShape var4 = AvatarIconKt.getComposeShape(this.avatarShape);
      float var7 = var1.getWidth();
      float var8 = var1.getHeight();
      int var9 = 0;
      float var10 = var7;
      float var11 = var8;
      int var12 = 0;
      long var13 = Float.floatToRawIntBits(var10);
      long var15 = Float.floatToRawIntBits(var11);
      long var5 = androidx.compose.ui.geometry.Size.recovered$8c837f3e34208708(var13 << 32 | var15 & 4294967295L);
      Density var17 = DensityKt.Density$default(1.0F, 0.0F, 2, null);
      return new RoundedCornersTransformation(
            var4.getTopStart().recovered$8954be48d26e6609(var5, var17),
            var4.getTopEnd().recovered$8954be48d26e6609(var5, var17),
            var4.getBottomStart().recovered$8954be48d26e6609(var5, var17),
            var4.getBottomEnd().recovered$8954be48d26e6609(var5, var17)
         )
         .transform(var1, var2, var3);
   }
}
