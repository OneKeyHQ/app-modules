package io.intercom.android.sdk.helpcenter.articles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.CookieManager;
import androidx.activity.EdgeToEdge;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.IntercomHelpCenterBaseActivity;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u0000 \u00172\u00020\u0001:\u0002\u0016\u0017B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0015J\b\u0010\u0015\u001a\u00020\u0012H\u0002R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\f\u001a\u00020\r8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\t\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity;",
         "Lio/intercom/android/sdk/helpcenter/IntercomHelpCenterBaseActivity;",
         "<init>",
         "()V",
         "arguments",
         "Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$ArticleActivityArguments;",
         "getArguments",
         "()Lio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$ArticleActivityArguments;",
         "arguments$delegate",
         "Lkotlin/Lazy;",
         "scrollBy",
         "Landroidx/compose/runtime/MutableIntState;",
         "viewModel",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel;",
         "getViewModel",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel;",
         "viewModel$delegate",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "setCookies",
         "ArticleActivityArguments",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomArticleActivity extends IntercomHelpCenterBaseActivity {
   @NotNull
   public static final IntercomArticleActivity$Companion Companion = new IntercomArticleActivity$Companion(null);
   @NotNull
   private final Lazy arguments$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.arguments_delegate$lambda$0(this)));
   @NotNull
   private final MutableIntState scrollBy = SnapshotIntStateKt.mutableIntStateOf(0);
   @NotNull
   private final Lazy viewModel$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.viewModel_delegate$lambda$2(this)));
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String ARTICLE_ID = "ARTICLE_ID";
   @NotNull
   private static final String METRIC_PLACE = "METRIC_PLACE";
   @NotNull
   private static final String IS_SEARCH_BROWSE = "IS_FROM_SEARCH_BROWSE";
   @NotNull
   private static final String SHOULD_HIDE_REACTIONS = "SHOULD_HIDE_REACTIONS";

   private final IntercomArticleActivity$ArticleActivityArguments getArguments() {
      Lazy var1 = this.arguments$delegate;
      return (IntercomArticleActivity$ArticleActivityArguments)var1.getValue();
   }

   private final ArticleViewModel getViewModel() {
      Lazy var1 = this.viewModel$delegate;
      return (ArticleViewModel)var1.getValue();
   }

   @SuppressLint("SetJavaScriptEnabled")
   @Override
   protected void onCreate(@Nullable Bundle var1) {
      EdgeToEdge.enable$default(this, null, null, 3, null);
      super.onCreate(var1);
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(1674700077, true, new IntercomArticleActivity$onCreate$1(this)), 1, null
      );
   }

   private final void setCookies() {
      Injector var1 = Injector.get();
      int var2 = 0;
      String var3 = "intercom-session-" + var1.getAppIdentity().appId();
      String var10000 = var1.getUserIdentity().getEncryptedUserId();
      Intrinsics.checkNotNullExpressionValue(var10000, "getEncryptedUserId(...)");
      String var4 = var10000;
      CookieManager.getInstance().setCookie(((AppConfig)var1.getAppConfigProvider().get()).getHelpCenterUrl(), var3 + '=' + var4);
   }

   private static final IntercomArticleActivity$ArticleActivityArguments arguments_delegate$lambda$0(IntercomArticleActivity var0) {
      IntercomArticleActivity$Companion var10000 = Companion;
      Intent var10001 = var0.getIntent();
      Intrinsics.checkNotNullExpressionValue(var10001, "getIntent(...)");
      return var10000.getArguments(var10001);
   }

   private static final Unit viewModel_delegate$lambda$2$lambda$1(IntercomArticleActivity var0, int var1) {
      var0.scrollBy.setIntValue(var1);
      return Unit.INSTANCE;
   }

   private static final ArticleViewModel viewModel_delegate$lambda$2(IntercomArticleActivity var0) {
      Resources var1 = var0.getResources();
      boolean var2 = (var1.getConfiguration().uiMode & 48) == 32;
      ArticleViewModel$Companion var10000 = ArticleViewModel.Companion;
      ViewModelStoreOwner var10001 = var0;
      HelpCenterApi var10002 = Injector.get().getHelpCenterApi();
      Intrinsics.checkNotNullExpressionValue(var10002, "getHelpCenterApi(...)");
      final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_85_0 = var0;
      return var10000.create(
         var10001,
         var10002,
         ((AppConfig)Injector.get().getAppConfigProvider().get()).getHelpCenterUrl(),
         var0.getArguments().getMetricPlace(),
         var0.getArguments().isFromSearchBrowse(),
         var0.getArguments().getShouldHideReactions(),
         ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_85_0) -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.viewModel_delegate$lambda$2$lambda$1(recoveredExpressionCapture_85_0, ((java.lang.Integer)recoveredExpressionArgument_85_0).intValue())),
         var2
      );
   }

   @JvmStatic
   @NotNull
   public static final Intent buildIntent(@NotNull Context var0, @NotNull IntercomArticleActivity$ArticleActivityArguments var1) {
      return Companion.buildIntent(var0, var1);
   }

   // $VF: synthetic method
   public static final ArticleViewModel access$getViewModel(IntercomArticleActivity var0) {
      return var0.getViewModel();
   }

   // $VF: synthetic method
   public static final void access$setCookies(IntercomArticleActivity var0) {
      var0.setCookies();
   }

   // $VF: synthetic method
   public static final IntercomArticleActivity$ArticleActivityArguments access$getArguments(IntercomArticleActivity var0) {
      return var0.getArguments();
   }
}
