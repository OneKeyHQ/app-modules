package io.intercom.android.sdk.blocks.views;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.blocks.StyleType;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BackgroundUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.IntercomCoilKt;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

public class VideoPreviewView extends RelativeLayout {
   private static final int PLAY_BUTTON_DIAMETER_DP = 48;
   private static final int PLAY_ARROW_OFFSET_DP = 3;
   private final ImageView thumbnailImageView;
   private final ImageView playButton;
   private final ProgressBar loadingSpinner;

   public VideoPreviewView(@NonNull Context var1, Provider var2, StyleType var3) {
      super(var1);
      BlockUtils.createLayoutParams(this, -2, -2);
      BlockUtils.setDefaultMarginBottom(this);
      this.thumbnailImageView = this.getVideoImageView(var1);
      this.playButton = this.getPlayButtonView(var1);
      this.loadingSpinner = this.getLoadingSpinner();
      this.addView(this.thumbnailImageView);
      this.addView(this.playButton);
      this.addView(this.loadingSpinner);
      int var4 = ((AppConfig)var2.get()).getPrimaryColor();
      if (var3 == StyleType.POST) {
         var4 = ColorUtils.lightenColor(var4);
      }

      if (ColorUtils.isColorLight(var4)) {
         int var5 = ContextCompat.getColor(var1, color.intercom_accessibility_black);
         this.playButton.setColorFilter(var5, Mode.SRC_ATOP);
      } else {
         this.playButton.setColorFilter(var4, Mode.SRC_ATOP);
      }

      AccessibilityUtils.INSTANCE.removeClickAbilityAnnouncement(this);
      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(this.playButton);
   }

   public ImageView getThumbnailImageView() {
      return this.thumbnailImageView;
   }

   public void displayThumbnail(String var1) {
      this.loadingSpinner.setVisibility(0);
      this.thumbnailImageView.setVisibility(4);
      ImageRequest var2 = new Builder(this.getContext())
         .data(var1)
         .placeholder(drawable.intercom_video_thumbnail_fallback)
         .error(drawable.intercom_video_thumbnail_fallback)
         .crossfade(true)
         .target(this.thumbnailImageView)
         .listener(new VideoPreviewView$1(this))
         .build();
      IntercomCoilKt.loadIntercomImage(this.getContext(), var2);
   }

   private void updateThumbnailAspectRatio() {
      this.thumbnailImageView.getLayoutParams().height = this.thumbnailImageView.getWidth() * 3 / 4;
   }

   public void showFailedImage() {
      this.thumbnailImageView
         .post(
            () -> BackgroundUtils.setBackground(
               this.thumbnailImageView, ContextCompat.getDrawable(this.getContext(), drawable.intercom_video_thumbnail_fallback)
            )
         );
   }

   private ImageView getPlayButtonView(Context var1) {
      int var2 = ScreenUtils.dpToPx(48.0F, var1);
      RelativeLayout.LayoutParams var3 = new RelativeLayout.LayoutParams(var2, var2);
      var3.addRule(13);
      ImageView var4 = new ImageView(var1);
      var4.setLayoutParams(var3);
      var4.setScaleType(ScaleType.CENTER);
      var4.setImageResource(drawable.intercom_play_arrow);
      var4.setPadding(ScreenUtils.dpToPx(3.0F, var1), 0, 0, 0);
      var4.setBackgroundResource(drawable.intercom_solid_circle);
      var4.setVisibility(8);
      var4.setId(id.intercom_video_thumbnail_play_button);
      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(var4);
      return var4;
   }

   private ImageView getVideoImageView(Context var1) {
      RelativeLayout.LayoutParams var2 = new RelativeLayout.LayoutParams(-2, -2);
      ImageView var3 = new ImageView(var1);
      var3.setLayoutParams(var2);
      var3.setAdjustViewBounds(true);
      var3.setScaleType(ScaleType.CENTER_CROP);
      var3.setId(id.intercom_video_thumbnail);
      return var3;
   }

   private ProgressBar getLoadingSpinner() {
      View var1 = inflate(this.getContext(), layout.intercom_progress_bar, null);
      return (ProgressBar)var1.findViewById(id.progressBar);
   }

   // $VF: synthetic method
   static ProgressBar access$000(VideoPreviewView var0) {
      return var0.loadingSpinner;
   }

   // $VF: synthetic method
   static ImageView access$100(VideoPreviewView var0) {
      return var0.thumbnailImageView;
   }

   // $VF: synthetic method
   static void access$200(VideoPreviewView var0) {
      var0.updateThumbnailAspectRatio();
   }

   // $VF: synthetic method
   static ImageView access$300(VideoPreviewView var0) {
      return var0.playButton;
   }
}
