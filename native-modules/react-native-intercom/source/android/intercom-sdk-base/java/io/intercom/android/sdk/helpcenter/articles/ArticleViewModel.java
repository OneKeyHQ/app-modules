package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.articles.ArticleWebViewListener;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 :2\u00020\u00012\u00020\u0002:\u0001:B{\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0012\u0012\u0006\u0010\u0013\u001a\u00020\u0014\u0012\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00180\u0016\u0012\b\b\u0002\u0010\u0019\u001a\u00020\r¢\u0006\u0004\b\u001a\u0010\u001bJ\u000e\u0010%\u001a\u00020\u00182\u0006\u0010$\u001a\u00020\u0006J\b\u0010&\u001a\u00020\u0018H\u0016J\b\u0010'\u001a\u00020\u0018H\u0016J\b\u0010(\u001a\u00020\u0018H\u0016J\b\u0010)\u001a\u00020\u0018H\u0016J\u0010\u0010*\u001a\u00020\u00182\u0006\u0010+\u001a\u00020\u0017H\u0016J\u0006\u0010,\u001a\u00020\u0018J\u0006\u0010-\u001a\u00020\u0018J\u0006\u0010.\u001a\u00020\u0018J \u0010/\u001a\u00020\u00182\u0006\u0010$\u001a\u00020\u00062\u0006\u0010#\u001a\u00020\u00062\u0006\u00100\u001a\u00020\u0017H\u0002J\u0010\u00101\u001a\u00020\u00182\b\u00102\u001a\u0004\u0018\u00010\u0006J\u0019\u00103\u001a\u00020\u00182\n\b\u0002\u00104\u001a\u0004\u0018\u00010\u0017H\u0002¢\u0006\u0002\u00105J\b\u00106\u001a\u00020\rH\u0002J\u0010\u00107\u001a\u00020\u00182\u0006\u00108\u001a\u000209H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00180\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001e0 ¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u000e\u0010#\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006;",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "Lio/intercom/android/sdk/articles/ArticleWebViewListener;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "baseUrl",
         "",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "metricPlace",
         "isFromSearchBrowse",
         "",
         "shouldHideReactions",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "scrollTo",
         "Lkotlin/Function1;",
         "",
         "",
         "isSystemInDarkTheme",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Ljava/lang/String;Lio/intercom/android/sdk/identity/AppConfig;Lio/intercom/android/sdk/metrics/MetricTracker;Ljava/lang/String;ZZLkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/data/CommonRepository;Lkotlin/jvm/functions/Function1;Z)V",
         "_state",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState;",
         "state",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "articleContentId",
         "articleId",
         "fragmentLoaded",
         "onArticleFinishedLoading",
         "onArticleLoadingError",
         "articleNotFound",
         "onArticleStartedLoading",
         "scrollArticleViewTo",
         "y",
         "sadReactionTapped",
         "happyReactionTapped",
         "neutralReactionTapped",
         "sendReactionToServer",
         "reactionIndex",
         "articleContentIdFetched",
         "value",
         "sendFailedMetric",
         "errorCode",
         "(Ljava/lang/Integer;)V",
         "shouldAddSendMessageRow",
         "updateTeamPresence",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ArticleViewModel extends ViewModel implements ArticleWebViewListener {
   @NotNull
   public static final ArticleViewModel$Companion Companion = new ArticleViewModel$Companion(null);
   @NotNull
   private final HelpCenterApi helpCenterApi;
   @NotNull
   private final String baseUrl;
   @NotNull
   private final AppConfig appConfig;
   @NotNull
   private final MetricTracker metricTracker;
   private final boolean isFromSearchBrowse;
   private final boolean shouldHideReactions;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final Function1 scrollTo;
   private final boolean isSystemInDarkTheme;
   @NotNull
   private final MutableStateFlow _state;
   @NotNull
   private final StateFlow state;
   @NotNull
   private String articleContentId;
   @NotNull
   private String articleId;
   public static final int $stable;

   static { $stable = 8; }
   public static final int HAPPY_SERVER_INDEX = 0;
   public static final int NEUTRAL_SERVER_INDEX = 1;
   public static final int SAD_SERVER_INDEX = 2;

   public ArticleViewModel(
      @NotNull HelpCenterApi var1,
      @NotNull String var2,
      @NotNull AppConfig var3,
      @NotNull MetricTracker var4,
      @NotNull String var5,
      boolean var6,
      boolean var7,
      @NotNull CoroutineDispatcher var8,
      @NotNull IntercomDataLayer var9,
      @NotNull CommonRepository var10,
      @NotNull Function1 var11,
      boolean var12
   ) {
      Intrinsics.checkNotNullParameter(var1, "helpCenterApi");
      Intrinsics.checkNotNullParameter(var2, "baseUrl");
      Intrinsics.checkNotNullParameter(var3, "appConfig");
      Intrinsics.checkNotNullParameter(var4, "metricTracker");
      Intrinsics.checkNotNullParameter(var5, "metricPlace");
      Intrinsics.checkNotNullParameter(var8, "dispatcher");
      Intrinsics.checkNotNullParameter(var9, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var10, "commonRepository");
      Intrinsics.checkNotNullParameter(var11, "scrollTo");
      super();
      this.helpCenterApi = var1;
      this.baseUrl = var2;
      this.appConfig = var3;
      this.metricTracker = var4;
      this.isFromSearchBrowse = var6;
      this.shouldHideReactions = var7;
      this.dispatcher = var8;
      this.intercomDataLayer = var9;
      this.commonRepository = var10;
      this.scrollTo = var11;
      this.isSystemInDarkTheme = var12;
      this._state = StateFlowKt.MutableStateFlow(ArticleViewState$Initial.INSTANCE);
      this.state = FlowKt.asStateFlow(this._state);
      this.articleContentId = "";
      this.articleId = "";
      if (!Intrinsics.areEqual(var5, "collection_list") && !Intrinsics.areEqual(var5, "article")) {
         String var13 = "no_context";
         if (Intrinsics.areEqual(var5, "api")) {
            var13 = "article";
         }

         this.metricTracker.openedNativeHelpCenter(var5, var13);
      }

      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleViewModel$1(this, null), 2, null);
   }

   // $VF: synthetic method
   public ArticleViewModel(
      HelpCenterApi var1,
      String var2,
      AppConfig var3,
      MetricTracker var4,
      String var5,
      boolean var6,
      boolean var7,
      CoroutineDispatcher var8,
      IntercomDataLayer var9,
      CommonRepository var10,
      Function1 var11,
      boolean var12,
      int var13,
      DefaultConstructorMarker var14
   ) {
      this(var1, var2, var3, var4, var5, (((var13 & 32) != 0) ? (boolean) (false) : var6), (((var13 & 64) != 0) ? (boolean) (false) : var7), (((var13 & 128) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var8), var9, var10, var11, (((var13 & 2048) != 0) ? (boolean) (false) : var12));
   }

   @NotNull
   public final StateFlow getState() {
      return this.state;
   }

   public final void fragmentLoaded(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      this.articleId = var1;
      String var10000;
      switch (ThemeManager.INSTANCE.getCurrentThemeMode()) {
         case DARK:
            var10000 = "dark";
            break;
         case LIGHT:
            var10000 = "light";
            break;
         case SYSTEM:
            var10000 = this.isSystemInDarkTheme ? "dark" : "light";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      String var2 = var10000;
      String var3 = this.baseUrl + "/articles/" + var1 + "?theme=" + var2;
      ArticleViewState$Content var4 = new ArticleViewState$Content(
         var3,
         new ArticleMetadata(var1, null, 2, null),
         ArticleViewState$WebViewStatus.Loading,
         ArticleViewState$ReactionState.Companion.getDefaultReactionState(),
         ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState()
      );
      this._state.setValue(var4);
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleViewModel$fragmentLoaded$1(this, var1, var4, null), 2, null
      );
   }

   @Override
   public void onArticleFinishedLoading() {
      this.metricTracker.viewedNativeHelpCenter("article", null, this.isFromSearchBrowse);
      ArticleViewState var1 = (ArticleViewState)this._state.getValue();
      if (var1 instanceof ArticleViewState$Content) {
         this._state
            .setValue(
               ArticleViewState$Content.copy$default((ArticleViewState$Content)var1, null, null, ArticleViewState$WebViewStatus.Ready, null, null, 27, null)
            );
      } else if (!Intrinsics.areEqual(var1, ArticleViewState$Initial.INSTANCE) && !(var1 instanceof ArticleViewState$Error)) {
         throw new NoWhenBranchMatchedException();
      }
   }

   @Override
   public void onArticleLoadingError() {
      sendFailedMetric$default(this, null, 1, null);
      this._state.setValue(new ArticleViewState$Error(string.intercom_something_went_wrong_try_again, 0, this.appConfig.getPrimaryColor()));
   }

   @Override
   public void articleNotFound() {
      this.sendFailedMetric(404);
      this._state.setValue(new ArticleViewState$Error(string.intercom_page_not_found, 8, this.appConfig.getPrimaryColor()));
   }

   @Override
   public void onArticleStartedLoading() {
      ArticleViewState var1 = (ArticleViewState)this._state.getValue();
      if (var1 instanceof ArticleViewState$Content) {
         this._state
            .setValue(
               ArticleViewState$Content.copy$default((ArticleViewState$Content)var1, null, null, ArticleViewState$WebViewStatus.Loading, null, null, 27, null)
            );
      } else if (!Intrinsics.areEqual(var1, ArticleViewState$Initial.INSTANCE) && !(var1 instanceof ArticleViewState$Error)) {
         throw new NoWhenBranchMatchedException();
      }
   }

   @Override
   public void scrollArticleViewTo(int var1) {
      this.scrollTo.invoke(var1);
   }

   public final void sadReactionTapped() {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleViewModel$sadReactionTapped$1(this, null), 2, null);
   }

   public final void happyReactionTapped() {
      ArticleViewState var1 = (ArticleViewState)this._state.getValue();
      if (var1 instanceof ArticleViewState$Content) {
         ArticleViewState$Content var2 = (ArticleViewState$Content)var1;
         MutableStateFlow var6 = this._state;
         int var3 = 0;
         this.metricTracker.sentArticleReaction("reaction_happy", null, this.isFromSearchBrowse);
         this.sendReactionToServer(this.articleId, this.articleContentId, 0);
         ArticleViewState$ReactionState var4 = var2.getReactionState();
         ArticleViewState$Reaction var5 = ArticleViewState$Reaction.Happy;
         var6.setValue(
            ArticleViewState$Content.copy$default(
               var2, null, null, null, ArticleViewState$ReactionState.copy$default(var4, 0, var5, 8, false, 1, null), null, 23, null
            )
         );
      } else if (!Intrinsics.areEqual(var1, ArticleViewState$Initial.INSTANCE) && !(var1 instanceof ArticleViewState$Error)) {
         throw new NoWhenBranchMatchedException();
      }
   }

   public final void neutralReactionTapped() {
      ArticleViewState var1 = (ArticleViewState)this._state.getValue();
      if (var1 instanceof ArticleViewState$Content) {
         ArticleViewState$Content var2 = (ArticleViewState$Content)var1;
         MutableStateFlow var6 = this._state;
         int var3 = 0;
         this.metricTracker.sentArticleReaction("reaction_neutral", null, this.isFromSearchBrowse);
         this.sendReactionToServer(this.articleId, this.articleContentId, 1);
         ArticleViewState$ReactionState var4 = var2.getReactionState();
         ArticleViewState$Reaction var5 = ArticleViewState$Reaction.Neutral;
         var6.setValue(
            ArticleViewState$Content.copy$default(
               var2, null, null, null, ArticleViewState$ReactionState.copy$default(var4, 0, var5, 8, false, 1, null), null, 23, null
            )
         );
      } else if (!Intrinsics.areEqual(var1, ArticleViewState$Initial.INSTANCE) && !(var1 instanceof ArticleViewState$Error)) {
         throw new NoWhenBranchMatchedException();
      }
   }

   private final void sendReactionToServer(String var1, String var2, int var3) {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleViewModel$sendReactionToServer$1(this, var1, var3, var2, null), 2, null
      );
   }

   public final void articleContentIdFetched(@Nullable String var1) {
      if (var1 != null) {
         this.articleContentId = var1;
         ArticleViewState var2 = (ArticleViewState)this._state.getValue();
         if (var2 instanceof ArticleViewState$Content) {
            this._state
               .setValue(
                  ArticleViewState$Content.copy$default(
                     (ArticleViewState$Content)var2,
                     null,
                     null,
                     null,
                     ArticleViewState$ReactionState.copy$default(
                        ((ArticleViewState$Content)var2).getReactionState(), this.shouldHideReactions ? 8 : 0, null, 0, false, 14, null
                     ),
                     null,
                     23,
                     null
                  )
               );
         } else if (!Intrinsics.areEqual(var2, ArticleViewState$Initial.INSTANCE) && !(var2 instanceof ArticleViewState$Error)) {
            throw new NoWhenBranchMatchedException();
         }
      }
   }

   private final void sendFailedMetric(Integer var1) {
      this.metricTracker.failedHelpCenter("help_center", "article", var1 != null ? var1.toString() : null, this.isFromSearchBrowse);
   }

   // $VF: synthetic method
   static void sendFailedMetric$default(ArticleViewModel var0, Integer var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.sendFailedMetric(var1);
   }

   private final boolean shouldAddSendMessageRow() {
      return AppConfigExtensionsKt.canStartNewConversation(this.appConfig) && this.appConfig.getArticleAutoReactionEnabled();
   }

   private final void updateTeamPresence(Conversation var1) {
      ArticleViewState var2 = (ArticleViewState)this._state.getValue();
      if (var2 instanceof ArticleViewState$Content
         && ((ArticleViewState$Content)var2).getReactionState().getSelectedReaction() == ArticleViewState$Reaction.Sad) {
         this._state
            .setValue(
               ArticleViewState$Content.copy$default(
                  (ArticleViewState$Content)var2,
                  null,
                  null,
                  null,
                  null,
                  ArticleViewState$TeamPresenceState.copy$default(
                     ((ArticleViewState$Content)var2).getTeamPresenceState(),
                     null,
                     new ArticleViewState$ConversationState(var1.getId(), 0, 2, null),
                     null,
                     0,
                     0,
                     0,
                     null,
                     null,
                     false,
                     null,
                     1021,
                     null
                  ),
                  15,
                  null
               )
            );
      }
   }

   // $VF: synthetic method
   public static final HelpCenterApi access$getHelpCenterApi$p(ArticleViewModel var0) {
      return var0.helpCenterApi;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_state$p(ArticleViewModel var0) {
      return var0._state;
   }

   // $VF: synthetic method
   public static final MetricTracker access$getMetricTracker$p(ArticleViewModel var0) {
      return var0.metricTracker;
   }

   // $VF: synthetic method
   public static final AppConfig access$getAppConfig$p(ArticleViewModel var0) {
      return var0.appConfig;
   }

   // $VF: synthetic method
   public static final boolean access$isFromSearchBrowse$p(ArticleViewModel var0) {
      return var0.isFromSearchBrowse;
   }

   // $VF: synthetic method
   public static final void access$sendReactionToServer(ArticleViewModel var0, String var1, String var2, int var3) {
      var0.sendReactionToServer(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final String access$getArticleId$p(ArticleViewModel var0) {
      return var0.articleId;
   }

   // $VF: synthetic method
   public static final String access$getArticleContentId$p(ArticleViewModel var0) {
      return var0.articleContentId;
   }

   // $VF: synthetic method
   public static final boolean access$shouldAddSendMessageRow(ArticleViewModel var0) {
      return var0.shouldAddSendMessageRow();
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(ArticleViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(ArticleViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final void access$updateTeamPresence(ArticleViewModel var0, Conversation var1) {
      var0.updateTeamPresence(var1);
   }
}
