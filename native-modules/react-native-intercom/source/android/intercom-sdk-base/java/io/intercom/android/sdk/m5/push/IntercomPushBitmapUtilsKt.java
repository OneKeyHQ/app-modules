package io.intercom.android.sdk.m5.push;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.webkit.URLUtil;
import androidx.core.content.FileProvider;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import coil.transform.CircleCropTransformation;
import coil.transform.Transformation;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.IntercomFileProviderKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.AvatarUtils;
import io.intercom.android.sdk.utilities.BitmapUtilsKt;
import io.intercom.android.sdk.utilities.NameUtils;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.AvatarDefaultDrawable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.SupervisorKt;
import kotlinx.coroutines.TimeoutKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000T\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0090\u0001\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u000f2\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\t0\u00132:\u0010\u0016\u001a6\u0012\u0015\u0012\u0013\u0018\u00010\u0018¢\u0006\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001b\u0012\u0015\u0012\u0013\u0018\u00010\u0018¢\u0006\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001c\u0012\u0004\u0012\u00020\t0\u0017H\u0000\u001a\u008a\u0001\u0010\u001d\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u001e\u001a\u00020\u00142\u0006\u0010\u001f\u001a\u00020\u00142\u0006\u0010 \u001a\u00020\u00142:\u0010\u0016\u001a6\u0012\u0015\u0012\u0013\u0018\u00010\u0018¢\u0006\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001b\u0012\u0015\u0012\u0013\u0018\u00010\u0018¢\u0006\f\b\u0019\u0012\b\b\u001a\u0012\u0004\b\b(\u001c\u0012\u0004\u0012\u00020\t0\u0017H\u0082@¢\u0006\u0002\u0010!\u001a \u0010\"\u001a\u0004\u0018\u00010\u00182\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010#\u001a\u00020\u000fH\u0082@¢\u0006\u0002\u0010$\u001a&\u0010%\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010&\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rH\u0082@¢\u0006\u0002\u0010'\u001a \u0010(\u001a\u00020\u00182\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rH\u0002\u001a\u0010\u0010)\u001a\u00020\u00142\u0006\u0010#\u001a\u00020\u000fH\u0002\u001a\u001a\u0010*\u001a\u0004\u0018\u00010+2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010,\u001a\u00020\u0018H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006-",
   d2 = {
         "LARGE_ICON_SIZE_DP",
         "",
         "BIG_PICTURE_WIDTH_DP",
         "BIG_PICTURE_HEIGHT_DP",
         "IMAGE_LOAD_TIMEOUT_MS",
         "",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "loadBitmaps",
         "",
         "context",
         "Landroid/content/Context;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "contentImageUrl",
         "",
         "avatarImageUrl",
         "authorName",
         "isMainThread",
         "Lkotlin/Function0;",
         "",
         "onInitial",
         "onUpdate",
         "Lkotlin/Function2;",
         "Landroid/graphics/Bitmap;",
         "Lkotlin/ParameterName;",
         "name",
         "contentBitmap",
         "avatarBitmap",
         "loadAndApplyBitmaps",
         "hasContentImage",
         "hasAvatarUrl",
         "hasAuthorName",
         "(Landroid/content/Context;Lio/intercom/android/sdk/identity/AppConfig;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZLkotlin/jvm/functions/Function2;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "loadContentBitmap",
         "url",
         "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "loadAvatarBitmap",
         "imageUrl",
         "(Landroid/content/Context;Ljava/lang/String;Lio/intercom/android/sdk/identity/AppConfig;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "generateInitialsAvatar",
         "isValidImageUrl",
         "getBitmapUri",
         "Landroid/net/Uri;",
         "bitmap",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPushBitmapUtilsKt {
   private static final float LARGE_ICON_SIZE_DP = 48.0F;
   private static final float BIG_PICTURE_WIDTH_DP = 512.0F;
   private static final float BIG_PICTURE_HEIGHT_DP = 256.0F;
   private static final long IMAGE_LOAD_TIMEOUT_MS = 10000L;
   @NotNull
   private static final Twig twig;

   public static final void loadBitmaps(
      @NotNull Context var0,
      @NotNull AppConfig var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull Function0 var5,
      @NotNull Function0 var6,
      @NotNull Function2 var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "appConfig");
      Intrinsics.checkNotNullParameter(var2, "contentImageUrl");
      Intrinsics.checkNotNullParameter(var3, "avatarImageUrl");
      Intrinsics.checkNotNullParameter(var4, "authorName");
      Intrinsics.checkNotNullParameter(var5, "isMainThread");
      Intrinsics.checkNotNullParameter(var6, "onInitial");
      Intrinsics.checkNotNullParameter(var7, "onUpdate");
      var6.invoke();
      boolean var8 = isValidImageUrl(var2);
      boolean var9 = isValidImageUrl(var3);
      boolean var10 = !StringsKt.isBlank(var4);
      if (var8 || var9 || var10) {
         twig.d("loadBitmaps: hasContentImage=" + var8 + ", hasAvatarUrl=" + var9 + ", hasAuthorName=" + var10);
         if ((Boolean)var5.invoke()) {
            BuildersKt.launch$default(
               CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob$default(null, 1, null).plus(Dispatchers.getIO())),
               null,
               null,
               new IntercomPushBitmapUtilsKt$loadBitmaps$2(var0, var1, var2, var3, var4, var8, var9, var10, var7, null),
               3,
               null
            );
         } else {
            BuildersKt.runBlocking(
               Dispatchers.getIO(), new IntercomPushBitmapUtilsKt$loadBitmaps$3(var0, var1, var2, var3, var4, var8, var9, var10, var7, null)
            );
         }
      }
   }

   // $VF: synthetic method
   public static void loadBitmaps$default(
      Context var0, AppConfig var1, String var2, String var3, String var4, Function0 var5, Function0 var6, Function2 var7, int var8, Object var9
   ) {
      if ((var8 & 4) != 0) {
         var2 = "";
      }

      if ((var8 & 8) != 0) {
         var3 = "";
      }

      if ((var8 & 16) != 0) {
         var4 = "";
      }

      if ((var8 & 32) != 0) {
         var5 = ((kotlin.jvm.functions.Function0) () -> IntercomPushBitmapUtilsKt.loadBitmaps$lambda$0());
      }

      loadBitmaps(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private static final Object loadAndApplyBitmaps(
      Context var0, AppConfig var1, String var2, String var3, String var4, boolean var5, boolean var6, boolean var7, Function2 var8, Continuation var9
   ) {
      Object var10000 = CoroutineScopeKt.coroutineScope(
         new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2(var5, var6, var7, var8, var0, var2, var3, var1, var4, null), var9
      );
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   private static final Object loadContentBitmap(Context var0, String var1, Continuation var2) {
      int var3 = ScreenUtils.dpToPx(512.0F, var0);
      int var4 = ScreenUtils.dpToPx(256.0F, var0);
      ImageRequest var5 = new Builder(var0).data(var1).size(var3, var4).build();
      return TimeoutKt.withTimeoutOrNull(10000L, new IntercomPushBitmapUtilsKt$loadContentBitmap$2(var0, var5, var3, var4, null), var2);
   }

   private static final Object loadAvatarBitmap(Context var0, String var1, AppConfig var2, Continuation var3) {
      IntercomPushBitmapUtilsKt$loadAvatarBitmap$1 var9;
      label24: {
         if (var3 instanceof IntercomPushBitmapUtilsKt$loadAvatarBitmap$1) {
            var9 = (IntercomPushBitmapUtilsKt$loadAvatarBitmap$1)var3;
            if ((var9.label & -2147483648) != 0) {
               var9.label -= Integer.MIN_VALUE;
               break label24;
            }
         }

         var9 = new IntercomPushBitmapUtilsKt$loadAvatarBitmap$1(var3);
      }

      Object var8 = var9.result;
      Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      int var4;
      AvatarDefaultDrawable var5;
      Object var10000;
      switch (var9.label) {
         case 0:
            ResultKt.throwOnFailure(var8);
            var4 = ScreenUtils.dpToPx(48.0F, var0);
            var5 = AvatarUtils.getDefaultDrawable(var0, var2);
            Builder var11 = new Builder(var0).error(var5);
            Transformation[] var7 = new Transformation[]{new CircleCropTransformation()};
            ImageRequest var6 = var11.transformations(var7).data(var1).size(var4, var4).build();
            Function2 var10001 = new IntercomPushBitmapUtilsKt$loadAvatarBitmap$2(var0, var6, var5, var4, null);
            var9.L$0 = var5;
            var9.I$0 = var4;
            var9.label = 1;
            var10000 = TimeoutKt.withTimeoutOrNull(10000L, var10001, var9);
            if (var10000 == var10) {
               return var10;
            }
            break;
         case 1:
            var4 = var9.I$0;
            var5 = (AvatarDefaultDrawable)var9.L$0;
            ResultKt.throwOnFailure(var8);
            var10000 = var8;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Bitmap var12 = (Bitmap)var10000;
      if ((Bitmap)var10000 == null) {
         Intrinsics.checkNotNull(var5);
         var12 = BitmapUtilsKt.drawableToBitmap(var5, var4, var4);
      }

      return var12;
   }

   private static final Bitmap generateInitialsAvatar(Context var0, String var1, AppConfig var2) {
      int var3 = ScreenUtils.dpToPx(48.0F, var0);
      String var4 = NameUtils.getInitial(var1);

      Drawable var6;
      try {
         var6 = AvatarUtils.getInitialsDrawable(var4, var2);
      } catch (Exception var8) {
         twig.w("Failed to generate initials avatar: " + var8.getMessage());
         var6 = AvatarUtils.getDefaultDrawable(var0, var2);
      }

      Drawable var5 = var6;
      Intrinsics.checkNotNull(var5);
      return BitmapUtilsKt.drawableToBitmap(var5, var3, var3);
   }

   private static final boolean isValidImageUrl(String var0) {
      return !StringsKt.isBlank(var0) && URLUtil.isValidUrl(var0) && (URLUtil.isHttpUrl(var0) || URLUtil.isHttpsUrl(var0));
   }

   @Nullable
   public static final Uri getBitmapUri(@NotNull Context var0, @NotNull Bitmap var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "bitmap");

      Uri var2;
      try {
         File var7 = new File(var0.getCacheDir(), "images");
         var7.mkdirs();
         String var10000 = UUID.randomUUID().toString();
         Intrinsics.checkNotNullExpressionValue(var10000, "toString(...)");
         String var3 = var10000;
         File var4 = new File(var7, var3 + ".png");
         FileOutputStream var5 = new FileOutputStream(var4);
         var1.compress(CompressFormat.PNG, 90, var5);
         var5.flush();
         var5.close();
         var2 = FileProvider.getUriForFile(var0, IntercomFileProviderKt.fileProviderAuthority(var0), var4);
      } catch (Exception var6) {
         twig.w("Failed to create bitmap URI: " + var6.getMessage());
         var2 = null;
      }

      return var2;
   }

   private static final boolean loadBitmaps$lambda$0() {
      return Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper());
   }

   // $VF: synthetic method
   public static final Object access$loadAndApplyBitmaps(
      Context var0, AppConfig var1, String var2, String var3, String var4, boolean var5, boolean var6, boolean var7, Function2 var8, Continuation var9
   ) {
      return loadAndApplyBitmaps(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   // $VF: synthetic method
   public static final Object access$loadContentBitmap(Context var0, String var1, Continuation var2) {
      return loadContentBitmap(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final Object access$loadAvatarBitmap(Context var0, String var1, AppConfig var2, Continuation var3) {
      return loadAvatarBitmap(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final Bitmap access$generateInitialsAvatar(Context var0, String var1, AppConfig var2) {
      return generateInitialsAvatar(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$p() {
      return twig;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}
