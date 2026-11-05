package io.intercom.android.sdk.helpcenter.articles;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u0002H\u0003\"\b\b\u0000\u0010\u0003*\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u0006H\u0016¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "io/intercom/android/sdk/helpcenter/articles/ArticleViewModel$Companion$factory$1",
         "Landroidx/lifecycle/ViewModelProvider$Factory;",
         "create",
         "T",
         "Landroidx/lifecycle/ViewModel;",
         "modelClass",
         "Ljava/lang/Class;",
         "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;",
         "intercom-sdk-base_release"
   }
)
public final class ArticleViewModel$Companion$factory$1 implements Factory {
   // $VF: synthetic field
   final HelpCenterApi $helpCenterApi;
   // $VF: synthetic field
   final String $baseUrl;
   // $VF: synthetic field
   final String $metricPlace;
   // $VF: synthetic field
   final boolean $isFromSearchBrowse;
   // $VF: synthetic field
   final boolean $shouldHideReactions;
   // $VF: synthetic field
   final Function1 $scrollTo;
   // $VF: synthetic field
   final boolean $isSystemInDarkTheme;

   ArticleViewModel$Companion$factory$1(HelpCenterApi var1, String var2, String var3, boolean var4, boolean var5, Function1 var6, boolean var7) {
      this.$helpCenterApi = var1;
      this.$baseUrl = var2;
      this.$metricPlace = var3;
      this.$isFromSearchBrowse = var4;
      this.$shouldHideReactions = var5;
      this.$scrollTo = var6;
      this.$isSystemInDarkTheme = var7;
   }

   @Override
   public ViewModel create(Class var1) {
      Intrinsics.checkNotNullParameter(var1, "modelClass");
      IntercomDataLayer var2 = Injector.get().getDataLayer();
      Object var10000 = Injector.get().getAppConfigProvider().get();
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      AppConfig var3 = (AppConfig)var10000;
      MetricTracker var7 = Injector.get().getMetricTracker();
      Intrinsics.checkNotNullExpressionValue(var7, "getMetricTracker(...)");
      MetricTracker var4 = var7;
      Intrinsics.checkNotNull(var2);
      IntercomDataLayer var5 = var2;
      MessengerApi var10002 = Injector.get().getMessengerApi();
      Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
      CommonRepository var6 = new CommonRepository(var10002, var2);
      return new ArticleViewModel(
         this.$helpCenterApi,
         this.$baseUrl,
         var3,
         var4,
         this.$metricPlace,
         this.$isFromSearchBrowse,
         this.$shouldHideReactions,
         null,
         var5,
         var6,
         this.$scrollTo,
         this.$isSystemInDarkTheme,
         128,
         null
      );
   }
}
