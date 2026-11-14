package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.helpcenter.component.TeammateHelpKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.FlowPreview;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 32\u00020\u0001:\u00013BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0010\u0010\u0011J\u0016\u0010\u001e\u001a\u00020\u001f2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001a0!H\u0007J\u000e\u0010\"\u001a\u00020\u001fH\u0082@¢\u0006\u0002\u0010#J\u0006\u0010$\u001a\u00020\u001fJ\u0006\u0010%\u001a\u00020\u001fJ\u0019\u0010&\u001a\u00020\u001f2\n\b\u0002\u0010'\u001a\u0004\u0018\u00010(H\u0002¢\u0006\u0002\u0010)J\"\u0010*\u001a\b\u0012\u0004\u0012\u00020,0+2\f\u0010-\u001a\b\u0012\u0004\u0012\u00020.0+H\u0082@¢\u0006\u0002\u0010/J\u000e\u00100\u001a\u000201H\u0082@¢\u0006\u0002\u0010#J\b\u00102\u001a\u00020\tH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00140\u0016¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u00064",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "isFromSearchBrowse",
         "",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Lio/intercom/android/sdk/identity/AppConfig;Lio/intercom/android/sdk/metrics/MetricTracker;ZLkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/data/CommonRepository;)V",
         "_state",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;",
         "state",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "lastSearchedInput",
         "",
         "searchInput",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "hasClickedAtLeastOneArticle",
         "searchForArticles",
         "",
         "textChanged",
         "Lkotlinx/coroutines/flow/Flow;",
         "updateTeammateHelpRow",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "addTeammateHelpRow",
         "sendClickOnSearchResultMetric",
         "sendFailedSearchMetric",
         "errorCode",
         "",
         "(Ljava/lang/Integer;)V",
         "transformToUiModel",
         "",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow;",
         "searchResponses",
         "Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse;",
         "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "teammateHelpRow",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchResultRow$TeammateHelpRow;",
         "shouldAddSendMessageRow",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nArticleSearchViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,286:1\n2746#2,3:287\n827#2:290\n855#2,2:291\n1563#2:293\n1634#2,2:294\n1636#2:297\n1#3:296\n*S KotlinDebug\n*F\n+ 1 ArticleSearchViewModel.kt\nio/intercom/android/sdk/helpcenter/search/ArticleSearchViewModel\n*L\n152#1:287,3\n160#1:290\n160#1:291,2\n215#1:293\n215#1:294,2\n215#1:297\n*E\n"
)
public final class ArticleSearchViewModel extends ViewModel {
   @NotNull
   public static final ArticleSearchViewModel$Companion Companion = new ArticleSearchViewModel$Companion(null);
   @NotNull
   private final HelpCenterApi helpCenterApi;
   @NotNull
   private final AppConfig appConfig;
   @NotNull
   private final MetricTracker metricTracker;
   private final boolean isFromSearchBrowse;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final MutableStateFlow _state;
   @NotNull
   private final StateFlow state;
   @NotNull
   private String lastSearchedInput;
   @NotNull
   private final MutableSharedFlow searchInput;
   private boolean hasClickedAtLeastOneArticle;
   public static final int $stable;

   static { $stable = 8; }

   public ArticleSearchViewModel(
      @NotNull HelpCenterApi var1,
      @NotNull AppConfig var2,
      @NotNull MetricTracker var3,
      boolean var4,
      @NotNull CoroutineDispatcher var5,
      @NotNull IntercomDataLayer var6,
      @NotNull CommonRepository var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "helpCenterApi");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      Intrinsics.checkNotNullParameter(var3, "metricTracker");
      Intrinsics.checkNotNullParameter(var5, "dispatcher");
      Intrinsics.checkNotNullParameter(var6, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var7, "commonRepository");
      super();
      this.helpCenterApi = var1;
      this.appConfig = var2;
      this.metricTracker = var3;
      this.isFromSearchBrowse = var4;
      this.dispatcher = var5;
      this.intercomDataLayer = var6;
      this.commonRepository = var7;
      this._state = StateFlowKt.MutableStateFlow(ArticleSearchState$Initial.INSTANCE);
      this.state = FlowKt.asStateFlow(this._state);
      this.lastSearchedInput = "";
      this.searchInput = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleSearchViewModel$1(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleSearchViewModel$2(this, null), 2, null);
   }

   // $VF: synthetic method
   public ArticleSearchViewModel(
      HelpCenterApi var1,
      AppConfig var2,
      MetricTracker var3,
      boolean var4,
      CoroutineDispatcher var5,
      IntercomDataLayer var6,
      CommonRepository var7,
      int var8,
      DefaultConstructorMarker var9
   ) {
      this(var1, var2, var3, (((var8 & 8) != 0) ? (boolean) (false) : var4), (((var8 & 16) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var5), var6, var7);
   }

   @NotNull
   public final StateFlow getState() {
      return this.state;
   }

   @FlowPreview
   public final void searchForArticles(@NotNull Flow var1) {
      Intrinsics.checkNotNullParameter(var1, "textChanged");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleSearchViewModel$searchForArticles$1(var1, this, null), 2, null
      );
   }

   private final Object updateTeammateHelpRow(Continuation var1) {
      ArticleSearchViewModel$updateTeammateHelpRow$1 var19;
      label76: {
         if (var1 instanceof ArticleSearchViewModel$updateTeammateHelpRow$1) {
            var19 = (ArticleSearchViewModel$updateTeammateHelpRow$1)var1;
            if ((var19.label & -2147483648) != 0) {
               var19.label -= Integer.MIN_VALUE;
               break label76;
            }
         }

         var19 = new ArticleSearchViewModel$updateTeammateHelpRow$1(this, var1);
      }

      Object var18 = var19.result;
      Object var20 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      MutableStateFlow var14;
      ArticleSearchState$Content var15;
      Collection var16;
      Object var10000;
      switch (var19.label) {
         case 0:
            ResultKt.throwOnFailure(var18);
            ArticleSearchState var2 = (ArticleSearchState)this._state.getValue();
            if (!this.shouldAddSendMessageRow()) {
               MutableStateFlow recoveredNoTeamState = this._state;
               ArticleSearchState var21 = var2;
               ArticleSearchState var31;
               if (!(var21 instanceof ArticleSearchState$Content)) {
                  var31 = var21 instanceof ArticleSearchState$NoResults
                     ? new ArticleSearchState$NoResultsNoTeamHelp(((ArticleSearchState$NoResults)var2).getSearchTerm())
                     : var2;
               } else {
                  Iterable var23 = ((ArticleSearchState$Content)var2).getSearchResults();
                  var14 = recoveredNoTeamState;
                  int var24 = 0;
                  Iterable var25 = var23;
                  ArrayList var26 = new ArrayList();
                  int var9 = 0;

                  for (Object var11 : var25) {
                     ArticleSearchResultRow var12 = (ArticleSearchResultRow)var11;
                     int var13 = 0;
                     if (!(var12 instanceof ArticleSearchResultRow$TeammateHelpRow)) {
                        var26.add(var11);
                     }
                  }

                  List var10001 = (List)var26;
                  recoveredNoTeamState = var14;
                  List var22 = var10001;
                  var31 = ((ArticleSearchState$Content)var2).copy(var22);
               }

               recoveredNoTeamState.setValue(var31);
               return Unit.INSTANCE;
            }

            if (!(var2 instanceof ArticleSearchState$Content)) {
               return Unit.INSTANCE;
            }

            var var3 = ((ArticleSearchState$Content)var2).getSearchResults();
            int var4 = 0;
            boolean var28;
            if (var3 instanceof Collection && ((Collection)var3).isEmpty()) {
               var28 = true;
            } else {
               Iterator var5 = var3.iterator();

               while (true) {
                  if (!var5.hasNext()) {
                     var28 = true;
                     break;
                  }

                  Object var6 = var5.next();
                  ArticleSearchResultRow var7 = (ArticleSearchResultRow)var6;
                  int var8 = 0;
                  if (var7 instanceof ArticleSearchResultRow$TeammateHelpRow) {
                     var28 = false;
                     break;
                  }
               }
            }

            if (!var28) {
               return Unit.INSTANCE;
            }

            MutableStateFlow recoveredArticleState = this._state;
            var16 = ((ArticleSearchState$Content)var2).getSearchResults();
            var15 = (ArticleSearchState$Content)var2;
            var14 = recoveredArticleState;
            var19.L$0 = var14;
            var19.L$1 = var15;
            var19.L$2 = var16;
            var19.label = 1;
            var10000 = this.teammateHelpRow(var19);
            if (var10000 == var20) {
               return var20;
            }
            break;
         case 1:
            var16 = (Collection)var19.L$2;
            var15 = (ArticleSearchState$Content)var19.L$1;
            var14 = (MutableStateFlow)var19.L$0;
            ResultKt.throwOnFailure(var18);
            var10000 = var18;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Object var17 = var10000;
      var14.setValue(var15.copy(CollectionsKt.plus(var16, (Object)var17)));
      return Unit.INSTANCE;
   }

   public final void addTeammateHelpRow() {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleSearchViewModel$addTeammateHelpRow$1(this, null), 2, null
      );
   }

   public final void sendClickOnSearchResultMetric() {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new ArticleSearchViewModel$sendClickOnSearchResultMetric$1(this, null), 2, null
      );
   }

   private final void sendFailedSearchMetric(Integer var1) {
      this.metricTracker.failedHelpCenter("help_center", "search_results", var1 != null ? var1.toString() : null, this.isFromSearchBrowse);
   }

   // $VF: synthetic method
   static void sendFailedSearchMetric$default(ArticleSearchViewModel var0, Integer var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.sendFailedSearchMetric(var1);
   }

   private final Object transformToUiModel(List var1, Continuation var2) {
      ArticleSearchViewModel$transformToUiModel$1 var19;
      label62: {
         if (var2 instanceof ArticleSearchViewModel$transformToUiModel$1) {
            var19 = (ArticleSearchViewModel$transformToUiModel$1)var2;
            if ((var19.label & -2147483648) != 0) {
               var19.label -= Integer.MIN_VALUE;
               break label62;
            }
         }

         var19 = new ArticleSearchViewModel$transformToUiModel$1(this, var2);
      }

      Object var18 = var19.result;
      Object var24 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      List var3;
      Collection var17;
      Object var10000;
      switch (var19.label) {
         case 0:
            ResultKt.throwOnFailure(var18);
            Iterable var4 = var1;
            int var5 = 0;
            Iterable var6 = var4;
            ArrayList var7 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var4, 10));
            int var8 = 0;

            for (Object var10 : var6) {
               HelpCenterArticleSearchResponse var11 = (HelpCenterArticleSearchResponse)var10;
               var17 = var7;
               int var12 = 0;
               HelpCenterArticleSearchResponse$Highlight var13 = var11.getHighlight();
               String recoveredArticleId = var11.getArticleId();
               CharSequence var14 = var13.getTitle();
               CharSequence var10001;
               if (var14.length() == 0) {
                  String var15 = recoveredArticleId;
                  int var16 = 0;
                  var10001 = var11.getTitle();
                  recoveredArticleId = var15;
               } else {
                  var10001 = var14;
               }

               String recoveredTitle = (String)var10001;
               String var21 = var13.getSummary();
               int var20 = var13.getSummary().length() == 0 ? 8 : 0;
               String var22 = recoveredTitle;
               String var23 = recoveredArticleId;
               var17.add(new ArticleSearchResultRow$ArticleResultRow(var23, var22, var21, var20));
            }

            var3 = CollectionsKt.toMutableList((Collection & List)var7);
            if (!this.shouldAddSendMessageRow() || !this.hasClickedAtLeastOneArticle) {
               return var3;
            }

            var17 = var3;
            var19.L$0 = var3;
            var19.L$1 = var17;
            var19.label = 1;
            var10000 = this.teammateHelpRow(var19);
            if (var10000 == var24) {
               return var24;
            }
            break;
         case 1:
            var17 = (Collection)var19.L$1;
            var3 = (List)var19.L$0;
            ResultKt.throwOnFailure(var18);
            var10000 = var18;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      var17.add(var10000);
      return var3;
   }

   private final Object teammateHelpRow(Continuation var1) {
      ArticleSearchViewModel$teammateHelpRow$1 var6;
      label26: {
         if (var1 instanceof ArticleSearchViewModel$teammateHelpRow$1) {
            var6 = (ArticleSearchViewModel$teammateHelpRow$1)var1;
            if ((var6.label & -2147483648) != 0) {
               var6.label -= Integer.MIN_VALUE;
               break label26;
            }
         }

         var6 = new ArticleSearchViewModel$teammateHelpRow$1(this, var1);
      }

      Object var5 = var6.result;
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      ArticleViewState$TeamPresenceState var2;
      Object var10000;
      switch (var6.label) {
         case 0:
            ResultKt.throwOnFailure(var5);
            var2 = TeammateHelpKt.computeViewState(
               null, ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState(), this.appConfig, "search_results", this.isFromSearchBrowse
            );
            CommonRepository var8 = this.commonRepository;
            var6.L$0 = var2;
            var6.label = 1;
            var10000 = var8.openMessenger(var6);
            if (var10000 == var7) {
               return var7;
            }
            break;
         case 1:
            var2 = (ArticleViewState$TeamPresenceState)var6.L$0;
            ResultKt.throwOnFailure(var5);
            var10000 = var5;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var3 = (NetworkResponse)var10000;
      if (var3 instanceof NetworkResponse$Success) {
         OpenMessengerResponse$NewConversationData var4 = ((OpenMessengerResponse)((NetworkResponse$Success)var3).getBody()).getNewConversationData();
         if (var4 != null) {
            var2 = ArticleViewState$TeamPresenceState.copy$default(var2, null, null, null, 0, 0, 0, null, null, false, var4.getCta(), 511, null);
         }
      }

      return new ArticleSearchResultRow$TeammateHelpRow(var2);
   }

   private final boolean shouldAddSendMessageRow() {
      return AppConfigExtensionsKt.canStartNewConversation(this.appConfig);
   }

   // $VF: synthetic method
   public static final MetricTracker access$getMetricTracker$p(ArticleSearchViewModel var0) {
      return var0.metricTracker;
   }

   // $VF: synthetic method
   public static final boolean access$isFromSearchBrowse$p(ArticleSearchViewModel var0) {
      return var0.isFromSearchBrowse;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_state$p(ArticleSearchViewModel var0) {
      return var0._state;
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$getSearchInput$p(ArticleSearchViewModel var0) {
      return var0.searchInput;
   }

   // $VF: synthetic method
   public static final Object access$updateTeammateHelpRow(ArticleSearchViewModel var0, Continuation var1) {
      return var0.updateTeammateHelpRow(var1);
   }

   // $VF: synthetic method
   public static final void access$setHasClickedAtLeastOneArticle$p(ArticleSearchViewModel var0, boolean var1) {
      var0.hasClickedAtLeastOneArticle = var1;
   }

   // $VF: synthetic method
   public static final boolean access$shouldAddSendMessageRow(ArticleSearchViewModel var0) {
      return var0.shouldAddSendMessageRow();
   }

   // $VF: synthetic method
   public static final Object access$teammateHelpRow(ArticleSearchViewModel var0, Continuation var1) {
      return var0.teammateHelpRow(var1);
   }

   // $VF: synthetic method
   public static final String access$getLastSearchedInput$p(ArticleSearchViewModel var0) {
      return var0.lastSearchedInput;
   }

   // $VF: synthetic method
   public static final Object access$transformToUiModel(ArticleSearchViewModel var0, List var1, Continuation var2) {
      return var0.transformToUiModel(var1, var2);
   }

   // $VF: synthetic method
   public static final HelpCenterApi access$getHelpCenterApi$p(ArticleSearchViewModel var0) {
      return var0.helpCenterApi;
   }

   // $VF: synthetic method
   public static final void access$setLastSearchedInput$p(ArticleSearchViewModel var0, String var1) {
      var0.lastSearchedInput = var1;
   }

   // $VF: synthetic method
   public static final void access$sendFailedSearchMetric(ArticleSearchViewModel var0, Integer var1) {
      var0.sendFailedSearchMetric(var1);
   }

   // $VF: synthetic method
   public static final AppConfig access$getAppConfig$p(ArticleSearchViewModel var0) {
      return var0.appConfig;
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(ArticleSearchViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(ArticleSearchViewModel var0) {
      return var0.intercomDataLayer;
   }
}
