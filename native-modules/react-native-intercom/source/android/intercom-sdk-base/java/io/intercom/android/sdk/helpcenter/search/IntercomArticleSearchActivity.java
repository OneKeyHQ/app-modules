package io.intercom.android.sdk.helpcenter.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.databinding.IntercomActivityArticleSearchBinding;
import io.intercom.android.sdk.helpcenter.IntercomHelpCenterBaseActivity;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0001\u0018\u0000 %2\u00020\u0001:\u0002%&B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0014J\b\u0010\u0018\u001a\u00020\u0015H\u0002J\b\u0010\u0019\u001a\u00020\u0015H\u0002JQ\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00150\u001e2\u0018\u0010\u001f\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0!\u0012\u0004\u0012\u00020\u00150 2\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00150 H\u0003¢\u0006\u0002\u0010$R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00058BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u001b\u0010\u000f\u001a\u00020\u00108BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0013\u0010\u000e\u001a\u0004\b\u0011\u0010\u0012¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity;",
         "Lio/intercom/android/sdk/helpcenter/IntercomHelpCenterBaseActivity;",
         "<init>",
         "()V",
         "_binding",
         "Lio/intercom/android/sdk/databinding/IntercomActivityArticleSearchBinding;",
         "binding",
         "getBinding",
         "()Lio/intercom/android/sdk/databinding/IntercomActivityArticleSearchBinding;",
         "viewModel",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel;",
         "getViewModel",
         "()Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel;",
         "viewModel$delegate",
         "Lkotlin/Lazy;",
         "args",
         "Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$ArticleSearchArgs;",
         "getArgs",
         "()Lio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$ArticleSearchArgs;",
         "args$delegate",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "setupInsets",
         "subscribeToStates",
         "SearchScreenContent",
         "articleSearchState",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;",
         "onBackClick",
         "Lkotlin/Function0;",
         "onTextChanged",
         "Lkotlin/Function1;",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "",
         "onArticleClicked",
         "(Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "Companion",
         "ArticleSearchArgs",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomArticleSearchActivity extends IntercomHelpCenterBaseActivity {
   @NotNull
   public static final IntercomArticleSearchActivity$Companion Companion = new IntercomArticleSearchActivity$Companion(null);
   @Nullable
   private IntercomActivityArticleSearchBinding _binding;
   @NotNull
   private final Lazy viewModel$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.viewModel_delegate$lambda$0(this)));
   @NotNull
   private final Lazy args$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.args_delegate$lambda$1(this)));
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String IS_FROM_SEARCH_BROWSE = "IS_SEARCH_BROWSE";

   private final IntercomActivityArticleSearchBinding getBinding() {
      if (this._binding == null) {
         String var1 = "Required value was null.";
         throw new IllegalStateException(var1.toString());
      } else {
         return this._binding;
      }
   }

   private final ArticleSearchViewModel getViewModel() {
      Lazy var1 = this.viewModel$delegate;
      return (ArticleSearchViewModel)var1.getValue();
   }

   private final IntercomArticleSearchActivity$ArticleSearchArgs getArgs() {
      Lazy var1 = this.args$delegate;
      return (IntercomArticleSearchActivity$ArticleSearchArgs)var1.getValue();
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this._binding = IntercomActivityArticleSearchBinding.inflate(this.getLayoutInflater());
      this.setContentView(this.getBinding().getRoot());
      this.setupInsets();
      this.subscribeToStates();
   }

   private final void setupInsets() {
      EdgeToEdge.enable$default(this, null, null, 3, null);
      ConstraintLayout var1 = (ConstraintLayout)this.findViewById(id.intercom_search_screen_root);
      WindowInsetsControllerCompat var2 = WindowCompat.getInsetsController(this.getWindow(), this.getWindow().getDecorView());
      WindowInsetsControllerCompat var3 = var2;
      int var4 = 0;
      boolean var10001;
      switch (ThemeManager.INSTANCE.getCurrentThemeMode()) {
         case DARK:
            var10001 = false;
            break;
         case LIGHT:
            var10001 = true;
            break;
         case SYSTEM:
            var10001 = false;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      var3.setAppearanceLightStatusBars(var10001);
   }

   private final void subscribeToStates() {
      this.getBinding()
         .articleSearchComposeView
         .setContent(ComposableLambdaKt.composableLambdaInstance(-779899693, true, new IntercomArticleSearchActivity$subscribeToStates$1(this)));
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private final void SearchScreenContent(ArticleSearchState var1, Function0 var2, Function1 var3, Function1 var4, Composer var5, int var6) {
      var5 = var5.startRestartGroup(865873108);
      ComposerKt.sourceInformation(
         var5, "C(SearchScreenContent)P(!1,2,3)117@4811L250,124@5106L6,125@5134L545,116@4780L899:IntercomArticleSearchActivity.kt#fpck24"
      );
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var5.changed(var1) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var5.changedInstance(var2) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var5.changedInstance(var3) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var5.changedInstance(var4) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               865873108,
               var7,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.SearchScreenContent (IntercomArticleSearchActivity.kt:115)"
            );
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            null,
            ComposableLambdaKt.rememberComposableLambda(-1018273896, true, new IntercomArticleSearchActivity$SearchScreenContent$1(var2, var3), var5, 54),
            null,
            null,
            null,
            0,
            IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(955713763, true, new IntercomArticleSearchActivity$SearchScreenContent$2(var1, var4), var5, 54),
            var5,
            805306416,
            445
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var5.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity recoveredCapture_108_0 = this;
         final io.intercom.android.sdk.helpcenter.search.ArticleSearchState recoveredCapture_108_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_108_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_108_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_108_4 = var4;
         final int recoveredCapture_108_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_108_0, recoveredArgument_108_1) -> IntercomArticleSearchActivity.SearchScreenContent$lambda$3(recoveredCapture_108_0, recoveredCapture_108_1, recoveredCapture_108_2, recoveredCapture_108_3, recoveredCapture_108_4, recoveredCapture_108_5, (androidx.compose.runtime.Composer)recoveredArgument_108_0, ((java.lang.Integer)recoveredArgument_108_1).intValue())));
      }
   }

   private static final ArticleSearchViewModel viewModel_delegate$lambda$0(IntercomArticleSearchActivity var0) {
      ArticleSearchViewModel$Companion var10000 = ArticleSearchViewModel.Companion;
      ViewModelStoreOwner var10001 = var0;
      HelpCenterApi var10002 = Injector.get().getHelpCenterApi();
      Intrinsics.checkNotNullExpressionValue(var10002, "getHelpCenterApi(...)");
      return var10000.create(var10001, var10002, var0.getArgs().isFromSearchBrowse());
   }

   private static final IntercomArticleSearchActivity$ArticleSearchArgs args_delegate$lambda$1(IntercomArticleSearchActivity var0) {
      IntercomArticleSearchActivity$Companion var10000 = Companion;
      Intent var10001 = var0.getIntent();
      Intrinsics.checkNotNullExpressionValue(var10001, "getIntent(...)");
      return var10000.getArguments(var10001);
   }

   private static final Unit SearchScreenContent$lambda$3(
      IntercomArticleSearchActivity var0, ArticleSearchState var1, Function0 var2, Function1 var3, Function1 var4, int var5, Composer var6, int var7
   ) {
      var0.SearchScreenContent(var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   @JvmStatic
   @NotNull
   public static final Intent buildIntent(@NotNull Context var0, boolean var1) {
      return Companion.buildIntent(var0, var1);
   }

   // $VF: synthetic method
   public static final ArticleSearchViewModel access$getViewModel(IntercomArticleSearchActivity var0) {
      return var0.getViewModel();
   }

   // $VF: synthetic method
   public static final void access$SearchScreenContent(
      IntercomArticleSearchActivity var0, ArticleSearchState var1, Function0 var2, Function1 var3, Function1 var4, Composer var5, int var6
   ) {
      var0.SearchScreenContent(var1, var2, var3, var4, var5, var6);
   }
}
