package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView.ScaleType;
import coil.request.ImageRequest.Builder;
import coil.transform.RoundedCornersTransformation;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.dimen;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.LocalImageBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;
import io.intercom.android.sdk.views.ProgressFrameLayout;
import io.intercom.android.sdk.views.ResizableImageView;
import io.intercom.android.sdk.views.UploadProgressBar;

class LocalImage extends Image implements LocalImageBlock {
   private final Twig twig = LumberMill.getLogger();

   LocalImage(StyleType var1) {
      super(var1);
   }

   @Override
   public View addImage(Uri var1, int var2, int var3, BlockAlignment var4, BlockMetadata var5, ViewGroup var6) {
      Context var7 = var6.getContext();
      int var8 = ScreenUtils.dpToPx(var2, var7);
      int var9 = ScreenUtils.dpToPx(var3, var7);
      ProgressFrameLayout var10 = new ProgressFrameLayout(var7);
      BlockUtils.createLayoutParams(var10, -2, -2);
      BlockUtils.setDefaultMarginBottom(var10);
      ResizableImageView var11 = new ResizableImageView(var7);
      BlockUtils.createLayoutParams(var11, -2, -2);
      var11.setAdjustViewBounds(true);
      var11.setScaleType(ScaleType.FIT_START);
      var10.addView(var11);
      Builder var12 = new Builder(var7).target(var11).data(var1);
      this.setImageViewBounds(var8, var9, var11, var12);
      View var13 = var10.getChildAt(0);
      Resources var14 = var7.getResources();
      if (var13 instanceof UploadProgressBar) {
         int var15 = var14.getDimensionPixelSize(dimen.intercom_local_image_upload_size);
         var13.setLayoutParams(new LayoutParams(var15, var15, 17));
         var13.bringToFront();
         var10.uploadStarted();
      }

      this.setBackground(var11);
      ColorMatrix var16 = new ColorMatrix();
      var16.setSaturation(0.0F);
      var11.setColorFilter(new ColorMatrixColorFilter(var16));
      var12.transformations(new RoundedCornersTransformation(var14.getDimensionPixelSize(dimen.intercom_image_rounded_corners)))
         .crossfade(true)
         .listener(new LocalImage$1(this, var11));
      IntercomCoilKt.loadIntercomImage(var7, var12.build());
      BlockUtils.setLayoutMarginsAndGravity(var10, var4.getGravity(), var5.isLastObject());
      return var10;
   }

   // $VF: synthetic method
   static Twig access$000(LocalImage var0) {
      return var0.twig;
   }
}
