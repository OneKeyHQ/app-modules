package io.intercom.android.sdk.m5.home.ui.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.ComposeView;
import com.google.gson.Gson;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.messengercard.CardWebView;
import io.intercom.android.sdk.blocks.messengercard.MessengerCardWebViewClient;
import io.intercom.android.sdk.blocks.messengercard.MessengerCardWebViewPresenter;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.home.ui.helpers.InMemoryWebViewCacheKt;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.utilities.ThemeUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a%\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\"\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\"\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "LegacyMessengerAppCard",
         "",
         "url",
         "",
         "style",
         "Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;",
         "isHomeScreen",
         "",
         "(Ljava/lang/String;Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;ZLandroidx/compose/runtime/Composer;I)V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "getWebView",
         "Lio/intercom/android/sdk/blocks/messengercard/CardWebView;",
         "context",
         "Landroid/content/Context;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nLegacyMessengerAppCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LegacyMessengerAppCard.kt\nio/intercom/android/sdk/m5/home/ui/components/LegacyMessengerAppCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,148:1\n75#2:149\n*S KotlinDebug\n*F\n+ 1 LegacyMessengerAppCard.kt\nio/intercom/android/sdk/m5/home/ui/components/LegacyMessengerAppCardKt\n*L\n33#1:149\n*E\n"
)
public final class LegacyMessengerAppCardKt {
   @NotNull
   private static final Twig twig;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void LegacyMessengerAppCard(@NotNull String var0, @NotNull Style var1, boolean var2, @Nullable Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "url");
      Intrinsics.checkNotNullParameter(var1, "style");
      var3 = var3.startRestartGroup(-1087658045);
      ComposerKt.sourceInformation(var3, "C(LegacyMessengerAppCard)P(2,1)32@1369L7,37@1499L1852,35@1456L1895:LegacyMessengerAppCard.kt#utrtal");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= ((var4 & 64) == 0 ? var3.changed(var1) : var3.changedInstance(var1)) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1087658045, var5, -1, "io.intercom.android.sdk.m5.home.ui.components.LegacyMessengerAppCard (LegacyMessengerAppCard.kt:31)"
            );
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var3.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var6 = (Context)var10;
         String var12 = ThemeUtils.appendInterfaceStyleToUrl(var6, var0);
         IntercomCardKt.IntercomCard(
            null,
            var1,
            ComposableLambdaKt.rememberComposableLambda(231006519, true, new LegacyMessengerAppCardKt$LegacyMessengerAppCard$1(var12, var2), var3, 54),
            var3,
            384 | Style.$stable << 3 | 112 & var5,
            1
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final java.lang.String recoveredCapture_916_0 = var0;
         final io.intercom.android.sdk.ui.component.IntercomCardStyle.Style recoveredCapture_916_1 = var1;
         final boolean recoveredCapture_916_2 = var2;
         final int recoveredCapture_916_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_916_0, recoveredArgument_916_1) -> LegacyMessengerAppCardKt.LegacyMessengerAppCard$lambda$0(recoveredCapture_916_0, recoveredCapture_916_1, recoveredCapture_916_2, recoveredCapture_916_3, (androidx.compose.runtime.Composer)recoveredArgument_916_0, ((java.lang.Integer)recoveredArgument_916_1).intValue())));
      }
   }

   private static final CardWebView getWebView(String var0, Context var1, boolean var2) {
      CardWebView var3 = InMemoryWebViewCacheKt.getCachedWebView(var0);
      CardWebView var10000;
      if (var3 != null) {
         var10000 = var3;
      } else {
         CardWebView var5;
         try {
            var5 = new CardWebView(var1);
         } catch (RuntimeException var24) {
            twig.w("CardWebView init failed with activity context, retrying with application context: " + var24.getMessage());

            CardWebView var7;
            try {
               var7 = new CardWebView(Injector.get().getApplication());
            } catch (RuntimeException var23) {
               twig.w("CardWebView init failed with application context fallback: " + var23.getMessage());
               return null;
            }

            var5 = var7;
         }

         CardWebView var4 = var5;
         var5 = var4;
         CardWebView var6 = var5;
         int var26 = 0;
         var6.setLayoutParams(new LayoutParams(-1, 160));
         var6.getSettings().setJavaScriptEnabled(true);
         var6.getSettings().setUseWideViewPort(true);
         var6.getSettings().setCacheMode(-1);
         var6.getSettings().setMixedContentMode(0);
         var6.setVerticalScrollBarEnabled(false);
         var6.setHorizontalScrollBarEnabled(false);
         var6.setWebViewClient(new MessengerCardWebViewClient(var0));
         ComposeView var8 = new ComposeView(var1, null, 0, 6, null);
         ComposeView var9 = var8;
         CardWebView var10 = var6;
         int var11 = 0;
         var9.setContent(ComposableSingletons$LegacyMessengerAppCardKt.INSTANCE.recovered$1400af5e34386102());
         View var10001 = var8;
         int var10003 = ((AppConfig)Injector.get().getAppConfigProvider().get()).getPrimaryColor();
         Gson var10004 = Injector.get().getGson();
         MetricTracker var10005 = Injector.get().getMetricTracker();
         File var10006 = var6.getContext().getCacheDir();
         Context var13 = var1;
         boolean var14 = var2;
         String var15 = "";
         File var16 = var10006;
         MetricTracker var17 = var10005;
         Gson var18 = var10004;
         int var19 = var10003;
         String var20 = var0;
         View var21 = var10001;
         CardWebView var22 = var10;
         MessengerCardWebViewPresenter var12 = new MessengerCardWebViewPresenter(var22, var21, var20, var19, var18, var17, var16, var15, var14, var13);
         var12.setUpWebView();
         var6.loadUrl(var0);
         InMemoryWebViewCacheKt.cacheWebView(var0, var4);
         var10000 = var4;
      }

      return var10000;
   }

   private static final Unit LegacyMessengerAppCard$lambda$0(String var0, Style var1, boolean var2, int var3, Composer var4, int var5) {
      LegacyMessengerAppCard(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final CardWebView access$getWebView(String var0, Context var1, boolean var2) {
      return getWebView(var0, var1, var2);
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}
