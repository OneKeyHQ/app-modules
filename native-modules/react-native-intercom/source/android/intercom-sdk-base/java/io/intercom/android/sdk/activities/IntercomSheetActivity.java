package io.intercom.android.sdk.activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.FrameLayout.LayoutParams;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Type;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.conversation.JavascriptRunner;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.IntercomEvent$CardUpdated;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.sheets.SheetListener;
import io.intercom.android.sdk.sheets.SheetWebViewPresenter;
import io.intercom.android.sdk.utilities.ColorUtils;
import io.intercom.android.sdk.views.IntercomErrorView;
import io.intercom.android.sdk.views.IntercomToolbar;
import io.intercom.android.sdk.views.IntercomToolbar$Listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntercomSheetActivity extends IntercomBaseActivity implements IntercomToolbar$Listener, SheetListener {
   private static final String PARCEL_SHEET_URL = "parcel_sheet_url";
   private static final String PARCEL_PARAMS = "parcel_params";
   private static final String PARCEL_CARD_URI = "parcel_card_uri";
   private static final String PARCEL_CONVERSATION_ID = "parcel_conversation_id";
   private static final String PARCEL_IS_HOME_SCREEN = "parcel_is_home_screen";
   private static final int WEBVIEW_FADE_IN_TIME_MS = 300;
   private static final int ENTRANCE_ANIMATION_TIME_MS = 250;
   private static final int EXIT_ANIMATION_TIME_MS = 200;
   private IntercomErrorView intercomErrorView;
   ProgressBar loadingBar;
   WebView webView;
   private SheetWebViewPresenter presenter;
   private IntercomToolbar intercomToolbar;
   private View containerView;
   private JavascriptRunner jsRunner;
   private MetricTracker metricTracker;
   private String cardUri;
   private String sheetUrl = "";
   private String conversationId = "";
   private HashMap params = new HashMap();
   private boolean isHomeScreen;
   private Provider appConfigProvider;
   private ActivityResultLauncher pickFile;
   public ValueCallback valueCallback;
   @ColorInt
   private int secondaryColor;

   @Override
   protected void onCreate(Bundle var1) {
      EdgeToEdge.enable(this);
      super.onCreate(var1);
      this.getOnBackPressedDispatcher().addCallback(new IntercomSheetActivity$1(this, true));
      Injector var2 = Injector.get();
      this.metricTracker = var2.getMetricTracker();
      this.appConfigProvider = var2.getAppConfigProvider();
      this.secondaryColor = ((AppConfig)this.appConfigProvider.get()).getSecondaryColor();
      Bundle var3 = this.getIntent().getExtras();
      if (var3 != null) {
         this.sheetUrl = var3.getString("parcel_sheet_url", "");
         this.conversationId = var3.getString("parcel_conversation_id", "");
         this.params = (HashMap)var3.getSerializable("parcel_params");
         this.cardUri = var3.getString("parcel_card_uri");
         this.isHomeScreen = var3.getBoolean("parcel_is_home_screen", false);
      }

      this.setContentView(layout.intercom_activity_sheet);
      this.setUpToolbar(this.appConfigProvider);
      this.containerView = this.findViewById(id.sheet_root);
      ViewCompat.setOnApplyWindowInsetsListener(this.containerView, (var0, var1x) -> {
         Insets barsInsets = var1x.getInsets(Type.systemBars() | Type.displayCutout());
         Insets imeInsets = var1x.getInsets(Type.ime());
         var0.setPadding(barsInsets.left, barsInsets.top, barsInsets.right, Math.max(barsInsets.bottom, imeInsets.bottom));
         return WindowInsetsCompat.CONSUMED;
      });
      if (TextUtils.isEmpty(this.sheetUrl) || this.params == null) {
         this.closeSheet();
      }

      this.containerView.setVisibility(8);
      this.containerView.post(new IntercomSheetActivity$2(this));
      this.insertWebView();
      this.jsRunner = new JavascriptRunner(this.webView);
      this.presenter = new SheetWebViewPresenter(this.webView, this.jsRunner, this.sheetUrl, this, var2);
      this.presenter.setUpWebView();
      this.intercomErrorView = (IntercomErrorView)this.containerView.findViewById(id.error_layout_sheet);
      this.intercomErrorView.setActionButtonTextColor(ColorUtils.primaryOrDarkColor(this, (AppConfig)this.appConfigProvider.get()));
      this.intercomErrorView.setActionButtonClickListener(new IntercomSheetActivity$3(this));
      this.pickFile = this.registerForActivityResult(new StartActivityForResult(), (androidx.activity.result.ActivityResultCallback<androidx.activity.result.ActivityResult>) (androidx.activity.result.ActivityResult var1x) -> {
         if (this.valueCallback != null) {
            this.valueCallback.onReceiveValue(extractFileChooserUris(var1x.getResultCode(), var1x.getData()));
            this.valueCallback = null;
         }
      });
      this.loadSheet(var2.getApi());
   }

   private void setUpToolbar(Provider var1) {
      AppConfig var2 = (AppConfig)var1.get();
      this.intercomToolbar = (IntercomToolbar)this.findViewById(id.intercom_toolbar);
      this.intercomToolbar.updateToolbarColors(var2);
      this.intercomToolbar.setListener(this);
      this.intercomToolbar.setSubtitleVisibility(8);
      this.intercomToolbar.setBackgroundColor(this.secondaryColor);
      this.intercomToolbar.setLeftNavigationItemVisibility(0);
      this.intercomToolbar.setCloseButtonVisibility(8);
      this.intercomToolbar.setLeftNavigationIcon(ContextCompat.getDrawable(this, drawable.intercom_close));
      this.loadingBar = (ProgressBar)this.intercomToolbar.findViewById(id.toolbar_progress_bar);
      this.loadingBar
         .setProgressDrawable(
            new LayerDrawable(new Drawable[]{new ColorDrawable(var2.getSecondaryColorDark()), new ScaleDrawable(new ColorDrawable(-1), 8388611, 1.0F, -1.0F)})
         );
   }

   private void loadSheet(Api var1) {
      this.loadingBar.setVisibility(0);
      this.intercomErrorView.setVisibility(8);
      var1.fetchSheet(this.params, new IntercomSheetActivity$4(this));
   }

   private void insertWebView() {
      try {
         this.webView = new WebView(this);
      } catch (NotFoundException var2) {
         this.webView = new WebView(this.getApplicationContext());
      }

      this.webView.setAlpha(0.0F);
      this.webView.setId(id.intercom_sheet_webview);
      this.webView.setLayoutParams(new LayoutParams(-1, -1));
      FrameLayout var1 = (FrameLayout)this.findViewById(id.sheet_view);
      var1.addView(this.webView, 0);
      this.webView.setWebChromeClient(new IntercomSheetActivity$5(this));
   }

   private void showErrorView() {
      this.loadingBar.setVisibility(8);
      this.intercomErrorView.setVisibility(0);
   }

   void closeSheet() {
      this.metricTracker.closedMessengerSheet(this.conversationId, this.sheetUrl, this.isHomeScreen);
      this.hideKeyboard();
      this.animateWindowOut();
   }

   @Nullable
   private static Uri[] extractFileChooserUris(int var0, @Nullable Intent var1) {
      if (var0 == -1 && var1 != null) {
         ClipData var2 = var1.getClipData();
         if (var2 != null) {
            List var6 = new ArrayList(var2.getItemCount());

            for (int var4 = 0; var4 < var2.getItemCount(); var4++) {
               Uri var5 = var2.getItemAt(var4).getUri();
               if (var5 != null) {
                  var6.add(var5);
               }
            }

            return var6.isEmpty() ? null : (Uri[])var6.toArray(new Uri[0]);
         } else {
            Uri var3 = var1.getData();
            return var3 != null ? new Uri[]{var3} : null;
         }
      } else {
         return null;
      }
   }

   public static Intent buildIntent(Context var0, String var1, Map var2, String var3, String var4) {
      Intent var5 = new Intent(var0, IntercomSheetActivity.class);
      var5.putExtra("parcel_sheet_url", var1);
      var5.putExtra("parcel_params", new HashMap(var2));
      var5.putExtra("parcel_card_uri", var3);
      var5.putExtra("parcel_conversation_id", var4);
      return var5;
   }

   private void hideKeyboard() {
      View var1 = this.getCurrentFocus();
      if (var1 != null) {
         InputMethodManager var2 = (InputMethodManager)this.getSystemService("input_method");
         var2.hideSoftInputFromWindow(var1.getWindowToken(), 0);
      }
   }

   private void animateWindowIn() {
      this.containerView.setVisibility(0);
      this.containerView.setY(this.findViewById(16908290).getMeasuredHeight());
      this.containerView.animate().y(0.0F).setInterpolator(new AccelerateInterpolator()).setDuration(250L).start();
   }

   private void animateWindowOut() {
      this.containerView.animate().y(this.getWindow().getDecorView().getHeight()).setDuration(200L).setListener(new IntercomSheetActivity$6(this)).start();
   }

   @Override
   public void onCloseClicked() {
   }

   @Override
   public void onToolbarClicked() {
   }

   @Override
   public void onInboxClicked() {
      this.closeSheet();
   }

   @Override
   public void onWebViewFinishedLoad() {
      this.jsRunner.runPendingScripts();
      this.webView.animate().alpha(1.0F).setDuration(300L).start();
      this.loadingBar.setVisibility(8);
   }

   @Override
   public void onSheetTitleAction(String var1) {
      this.intercomToolbar.setTitle(var1);
   }

   @Override
   public void onCloseSheetAction() {
      this.closeSheet();
   }

   @Override
   public void onSubmitSheetAction(Map var1) {
      Injector.get().getApi().submitSheet(this.cardUri, var1, new IntercomSheetActivity$7(this, var1));
   }

   private void cardUpdated() {
      Injector.get().getDataLayer().emitEvent(IntercomEvent$CardUpdated.INSTANCE);
   }

   private void showSubmitActionError(Map var1) {
      AlertDialog var2 = new Builder(this)
         .setTitle(string.intercom_couldnt_load_content)
         .setMessage(string.intercom_give_it_another_try)
         .setPositiveButton(string.intercom_reload, new IntercomSheetActivity$8(this, var1))
         .create();
      var2.show();
      var2.getButton(-1).setTextColor(ColorUtils.primaryOrDarkColor(this, (AppConfig)this.appConfigProvider.get()));
   }

   // $VF: synthetic method
   static void access$000(IntercomSheetActivity var0) {
      var0.animateWindowIn();
   }

   // $VF: synthetic method
   static void access$100(IntercomSheetActivity var0, Api var1) {
      var0.loadSheet(var1);
   }

   // $VF: synthetic method
   static IntercomErrorView access$200(IntercomSheetActivity var0) {
      return var0.intercomErrorView;
   }

   // $VF: synthetic method
   static SheetWebViewPresenter access$300(IntercomSheetActivity var0) {
      return var0.presenter;
   }

   // $VF: synthetic method
   static String access$400(IntercomSheetActivity var0) {
      return var0.conversationId;
   }

   // $VF: synthetic method
   static String access$500(IntercomSheetActivity var0) {
      return var0.sheetUrl;
   }

   // $VF: synthetic method
   static boolean access$600(IntercomSheetActivity var0) {
      return var0.isHomeScreen;
   }

   // $VF: synthetic method
   static MetricTracker access$700(IntercomSheetActivity var0) {
      return var0.metricTracker;
   }

   // $VF: synthetic method
   static void access$800(IntercomSheetActivity var0) {
      var0.showErrorView();
   }

   // $VF: synthetic method
   static ActivityResultLauncher access$900(IntercomSheetActivity var0) {
      return var0.pickFile;
   }

   // $VF: synthetic method
   static void access$1000(IntercomSheetActivity var0) {
      var0.cardUpdated();
   }

   // $VF: synthetic method
   static void access$1100(IntercomSheetActivity var0, Map var1) {
      var0.showSubmitActionError(var1);
   }
}
