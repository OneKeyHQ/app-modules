package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.VideoFileBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.ParagraphView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.utilities.AccessibilityUtils;
import io.intercom.android.sdk.utilities.BlockUtils;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;
import io.intercom.android.sdk.utilities.commons.ScreenUtils;

public class VideoFile implements VideoFileBlock {
   private final StyleType style;
   private final Provider appConfigProvider;
   private final MetricTracker metricTracker;
   private static final int PLAY_BUTTON_DIAMETER_DP = 36;
   private static final int PLAY_ARROW_OFFSET_DP = 3;

   VideoFile(StyleType var1, Provider var2, MetricTracker var3) {
      this.style = var1;
      this.appConfigProvider = var2;
      this.metricTracker = var3;
   }

   @Override
   public View addVideoFile(String var1, String var2, String var3, BlockMetadata var4, ViewGroup var5) {
      if (TextUtils.isEmpty(var2)) {
         return this.textFallback(var1, var5);
      }

      RelativeLayout var6 = new RelativeLayout(var5.getContext());
      WebView var7 = new WebView(var5.getContext());
      BlockUtils.createLayoutParams(var7, -1, 480);
      BlockUtils.setMarginBottom(var7, 16);
      var7.setWebChromeClient(new WebChromeClient());
      WebSettings var8 = var7.getSettings();
      var8.setJavaScriptEnabled(true);
      var8.setMediaPlaybackRequiresUserGesture(true);
      var7.loadUrl(var3);
      ImageView var9 = this.getPlayButtonView(var5.getContext());
      var9.setOnClickListener(var3x -> {
         var9.setVisibility(8);
         var7.loadUrl(var2);
      });
      var6.addView(var7);
      var6.addView(var9);
      return var6;
   }

   private View textFallback(String var1, ViewGroup var2) {
      TextView var3 = new ParagraphView(var2.getContext(), StyleType.ADMIN, BlockAlignment.LEFT, (AppConfig)this.appConfigProvider.get());
      var3.setText(HtmlCompat.fromHtml(var1));
      return var3;
   }

   private ImageView getPlayButtonView(Context var1) {
      int var2 = ScreenUtils.dpToPx(36.0F, var1);
      LayoutParams var3 = new LayoutParams(var2, var2);
      var3.addRule(13);
      ImageView var4 = new ImageView(var1);
      var4.setLayoutParams(var3);
      var4.setScaleType(ScaleType.CENTER);
      var4.setImageResource(drawable.intercom_play_arrow);
      var4.setPadding(ScreenUtils.dpToPx(3.0F, var1), 0, 0, 0);
      var4.setBackgroundResource(drawable.intercom_solid_circle);
      var4.setVisibility(0);
      var4.setId(id.intercom_video_thumbnail_play_button);
      AccessibilityUtils.INSTANCE.addClickAbilityAnnouncement(var4);
      return var4;
   }
}
