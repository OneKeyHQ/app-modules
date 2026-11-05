package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import coil.request.ImageRequest.Builder;
import coil.transform.RoundedCornersTransformation;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.ImageBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ImageUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.coil.GrayscaleTransformation;
import io.intercom.android.sdk.utilities.coil.RoundedCornersAnimatedTransformation;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.IntercomShimmerLayout;
import io.intercom.android.sdk.views.ResizableImageView;

public class NetworkImage extends Image implements ImageBlock {
   private final UploadingImageCache uploadingImageCache;
   @Nullable
   private final ImageClickListener listener;
   private final Twig twig = LumberMill.getLogger();

   NetworkImage(StyleType var1, UploadingImageCache var2, @Nullable ImageClickListener var3) {
      super(var1);
      this.uploadingImageCache = var2;
      this.listener = var3;
   }

   @Override
   public View addImage(String var1, String var2, String var3, int var4, int var5, BlockAlignment var6, BlockMetadata var7, ViewGroup var8) {
      Context var9 = var8.getContext();
      int var10 = ScreenUtils.dpToPx(var4, var9);
      int var11 = ScreenUtils.dpToPx(var5, var9);
      ShimmerFrameLayout var12 = new IntercomShimmerLayout(var9);
      BlockUtils.createLayoutParams(var12, -2, -2);
      BlockUtils.setDefaultMarginBottom(var12);
      ResizableImageView var13 = new ResizableImageView(var9);
      var13.setTransitionName("lightbox_image");
      var13.setLayoutParams(new LayoutParams(-2, -2));
      var13.setAdjustViewBounds(true);
      var13.setScaleType(ScaleType.FIT_START);
      this.setBackground(var13);
      var12.addView(var13);
      this.loadImageFromUrl(var1, var2, var9, var10, var11, var13, var12);
      BlockUtils.setLayoutMarginsAndGravity(var12, var6.getGravity(), var7.isLastObject());
      if (TextUtils.isEmpty(var2) && TextUtils.isEmpty(var3)) {
         AccessibilityUtils.INSTANCE.removeClickAbilityAnnouncement(var13);
      } else if (!TextUtils.isEmpty(var3)) {
         var13.setFocusable(true);
         var13.setContentDescription(var3);
      }

      return var12;
   }

   private void loadImageFromUrl(String var1, String var2, Context var3, int var4, int var5, ResizableImageView var6, ShimmerFrameLayout var7) {
      if (TextUtils.isEmpty(var1)) {
         this.hideLoadingState(var7, var6);
         var6.setImageResource(drawable.intercom_image_load_failed);
         var6.setScaleType(ScaleType.CENTER);
      } else {
         Builder var8 = new Builder(var3).error(drawable.intercom_image_load_failed).crossfade(true).target(var6).data(var1);
         this.setImageViewBounds(var4, var5, var6, var8);
         Resources var9 = var3.getResources();
         if (this.getStyle() != StyleType.CAROUSEL) {
            if (ImageUtils.isGif(var1)) {
               var8.setParameter(
                  "coil#animated_transformation", new RoundedCornersAnimatedTransformation(var9.getDimensionPixelSize(dimen.intercom_image_rounded_corners))
               );
            } else {
               var8.transformations(new RoundedCornersTransformation(var9.getDimensionPixelSize(dimen.intercom_image_rounded_corners)));
            }
         }

         Uri var10 = this.uploadingImageCache.getLocalImageUriForRemoteUrl(var1);
         if (var10 != null) {
            var8.transformations(new GrayscaleTransformation()).data(var10);
         }

         var8.listener(new NetworkImage$1(this, var6, var7, var3, var1, var2, var4, var5));
         IntercomCoilKt.loadIntercomImage(var3, var8.build());
      }
   }

   @VisibleForTesting
   int getSampleSize(int var1, int var2, DisplayMetrics var3) {
      int var4 = 1;
      if (var1 > var3.widthPixels || var2 > var3.heightPixels) {
         var4 = (int)Math.pow(2.0, (int)Math.ceil(Math.log((double)var3.widthPixels / Math.max(var2, var1)) / Math.log(0.5)));
      }

      return var4;
   }

   void hideLoadingState(ShimmerFrameLayout var1, ImageView var2) {
      if (var1 != null) {
         var1.hideShimmer();
         var2.setBackgroundResource(17170445);
      }
   }

   // $VF: synthetic method
   static Twig access$000(NetworkImage var0) {
      return var0.twig;
   }

   // $VF: synthetic method
   static ImageClickListener access$100(NetworkImage var0) {
      return var0.listener;
   }
}
