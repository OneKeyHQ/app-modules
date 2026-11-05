package io.intercom.android.sdk.utilities.coil;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import androidx.compose.runtime.internal.StabilityInferred;
import coil.size.Size;
import coil.transform.Transformation;
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
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0001\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fH\u0096@¢\u0006\u0002\u0010\rJ\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0096\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0005H\u0016R\u0014\u0010\u0004\u001a\u00020\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/utilities/coil/GrayscaleTransformation;",
         "Lcoil/transform/Transformation;",
         "<init>",
         "()V",
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
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nGrayscaleTransformation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GrayscaleTransformation.kt\nio/intercom/android/sdk/utilities/coil/GrayscaleTransformation\n+ 2 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,41:1\n41#2,3:42\n1#3:45\n*S KotlinDebug\n*F\n+ 1 GrayscaleTransformation.kt\nio/intercom/android/sdk/utilities/coil/GrayscaleTransformation\n*L\n24#1:42,3\n*E\n"
)
public final class GrayscaleTransformation implements Transformation {
   @NotNull
   private static final GrayscaleTransformation$Companion Companion = new GrayscaleTransformation$Companion(null);
   public static int $stable;

   static {}
   @NotNull
   private static final ColorMatrixColorFilter COLOR_FILTER;

   @NotNull
   @Override
   public String getCacheKey() {
      String var10000 = GrayscaleTransformation.class.getName();
      Intrinsics.checkNotNullExpressionValue(var10000, "getName(...)");
      return var10000;
   }

   @Nullable
   @Override
   public Object transform(@NotNull Bitmap var1, @NotNull Size var2, @NotNull Continuation var3) {
      Paint var4 = new Paint(3);
      var4.setColorFilter(COLOR_FILTER);
      int var10000 = var1.getWidth();
      int var10001 = var1.getHeight();
      Config var10002 = var1.getConfig();
      if (var10002 == null) {
         var10002 = Config.ARGB_8888;
      }

      Bitmap var11 = Bitmap.createBitmap(var10000, var10001, var10002);
      Intrinsics.checkNotNullExpressionValue(var11, "createBitmap(...)");
      Bitmap var5 = var11;
      Bitmap var6 = var5;
      int var7 = 0;
      Canvas var8 = new Canvas(var6);
      Canvas var9 = var8;
      int var10 = 0;
      var9.drawBitmap(var1, 0.0F, 0.0F, var4);
      return var5;
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      return var1 instanceof GrayscaleTransformation;
   }

   @Override
   public int hashCode() {
      return this.getClass().hashCode();
   }

   @NotNull
   @Override
   public String toString() {
      return "GrayscaleTransformation()";
   }

   // $VF: synthetic method
   public static final ColorMatrixColorFilter access$getCOLOR_FILTER$cp() {
      return COLOR_FILTER;
   }

   static {
      ColorMatrix var0 = new ColorMatrix();
      ColorMatrix var1 = var0;
      int var2 = 0;
      var1.setSaturation(0.0F);
      ColorMatrix var3 = var0;
      COLOR_FILTER = new ColorMatrixColorFilter(var3);
   }
}
