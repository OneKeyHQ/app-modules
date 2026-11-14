package io.intercom.android.sdk.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.utilities.coil.AvatarShapeTransformation;
import io.intercom.android.sdk.views.AvatarDefaultDrawable;
import io.intercom.android.sdk.views.AvatarInitialsDrawable;

public class AvatarUtils {
   public static void preloadAvatar(Avatar var0, Runnable var1, Activity var2) {
      String var3 = ThemeUtils.selectUrlForTheme(var2, var0.getImageUrl(), var0.getImageDarkUrl());
      if (var3 != null && !var3.isEmpty()) {
         ImageRequest var4 = new Builder(var2).data(var3).listener(new AvatarUtils$1(var1)).build();
         IntercomCoilKt.loadIntercomImage(var2, var4);
      } else {
         var1.run();
      }
   }

   public static void loadAvatarIntoView(Avatar var0, ImageView var1, AppConfig var2) {
      createAvatar(var0, var1, 0, var2);
   }

   public static void createAvatar(Avatar var0, ImageView var1, int var2, AppConfig var3) {
      Context var4 = var1.getContext();
      ImageRequest var5 = getAvatarImageRequestBuilder(var4, var0, var3).target(var1).build();
      IntercomCoilKt.loadIntercomImage(var4, var5);
   }

   public static Builder getAvatarImageRequestBuilder(Context var0, Avatar var1, AppConfig var2) {
      Drawable var3 = getPlaceHolderDrawable(var0, var1, var2);
      String var4 = ThemeUtils.selectUrlForTheme(var0, var1.getImageUrl(), var1.getImageDarkUrl());
      return new Builder(var0).data(var4).placeholder(var3).error(var3).crossfade(true).transformations(new AvatarShapeTransformation(var1.getShape()));
   }

   public static Drawable getPlaceHolderDrawable(Context var0, Avatar var1, AppConfig var2) {
      return var1.getInitials().isEmpty() ? getDefaultDrawable(var0, var2) : getInitialsDrawable(var1.getInitials(), var2);
   }

   public static AvatarInitialsDrawable getInitialsDrawable(String var0, AppConfig var1) {
      return new AvatarInitialsDrawable(var0.toUpperCase(), var1.getSecondaryColorDark());
   }

   public static AvatarDefaultDrawable getDefaultDrawable(Context var0, AppConfig var1) {
      return new AvatarDefaultDrawable(var0, var1.getSecondaryColorDark());
   }
}
