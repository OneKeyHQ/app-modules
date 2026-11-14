package io.intercom.android.sdk.blocks.messengercard;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.blocks.StyleType;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.interfaces.MessengerCardBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.ParagraphView;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.utilities.ThemeUtils;
import io.intercom.android.sdk.utilities.commons.HtmlCompat;

public class MessengerCard implements MessengerCardBlock {
   private final Provider appConfigProvider;
   private final Gson gson;
   private final MetricTracker metricTracker;
   private final String conversationId;

   public MessengerCard(Provider var1, Gson var2, MetricTracker var3, String var4) {
      this.appConfigProvider = var1;
      this.gson = var2;
      this.metricTracker = var3;
      this.conversationId = var4;
   }

   @SuppressLint("SetJavaScriptEnabled")
   @Override
   public View addCard(String var1, String var2, BlockMetadata var3, ViewGroup var4) {
      if (TextUtils.isEmpty(var2)) {
         return this.textFallback(var1, var4);
      }

      Context var5 = var4.getContext();
      LayoutInflater var6 = LayoutInflater.from(var5);
      FrameLayout var7 = (FrameLayout)var6.inflate(layout.intercom_messenger_card_block, var4, false);
      LayoutTransition var8 = var7.getLayoutTransition();
      var8.enableTransitionType(4);
      ProgressBar var9 = (ProgressBar)var7.findViewById(id.loading_view);
      CardWebView var10 = MessengerCardWebViewPresenter.createCardWebView(var5);
      var7.addView(var10);
      int var11 = ColorUtils.primaryOrDarkColor(var10.getContext(), (AppConfig)this.appConfigProvider.get());
      MessengerCardWebViewPresenter var12 = new MessengerCardWebViewPresenter(
         var10, var9, var2, var11, this.gson, this.metricTracker, var5.getCacheDir(), this.conversationId, false, var5
      );
      var12.setUpWebView();
      var10.loadUrl(ThemeUtils.appendInterfaceStyleToUrl(var5, var2));
      return var7;
   }

   private View textFallback(String var1, ViewGroup var2) {
      TextView var3 = new ParagraphView(var2.getContext(), StyleType.ADMIN, BlockAlignment.LEFT, (AppConfig)this.appConfigProvider.get());
      var3.setText(HtmlCompat.fromHtml(var1));
      return var3;
   }
}
