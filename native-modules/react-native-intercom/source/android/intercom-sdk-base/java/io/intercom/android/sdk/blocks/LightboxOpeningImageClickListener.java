package io.intercom.android.sdk.blocks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import androidx.core.app.ActivityOptionsCompat;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.lightbox.LightBoxActivity;
import io.intercom.android.sdk.utilities.LinkOpener;

public class LightboxOpeningImageClickListener implements ImageClickListener {
   private final Api api;

   public LightboxOpeningImageClickListener(Api var1) {
      this.api = var1;
   }

   @Override
   public void onImageClicked(String var1, String var2, View var3, int var4, int var5) {
      Context var6 = var3.getContext();
      if (!TextUtils.isEmpty(var2)) {
         LinkOpener.handleUrl(var2, var6, this.api);
      } else {
         if (var6 instanceof Activity) {
            Activity var7 = (Activity)var6;
            LayoutParams var8 = var7.getWindow().getAttributes();
            boolean var9 = (var8.flags & 1024) != 0;
            Intent var10 = LightBoxActivity.imageIntent(var7, var1, var9);
            ActivityOptionsCompat var11 = ActivityOptionsCompat.makeSceneTransitionAnimation(var7, var3, "lightbox_image");
            var7.startActivity(var10, var11.toBundle());
         } else {
            var6.startActivity(LightBoxActivity.imageIntent(var6, var1, false));
         }
      }
   }
}
