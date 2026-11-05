package io.intercom.android.sdk.m5.helpcenter;

import android.content.Context;
import android.content.res.Configuration;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.helpcenter.collections.HelpCenterCollection;
import io.intercom.android.sdk.helpcenter.component.TeammateHelpKt;
import io.intercom.android.sdk.helpcenter.sections.HelpCenterArticle;
import io.intercom.android.sdk.helpcenter.sections.HelpCenterCollectionContent;
import io.intercom.android.sdk.helpcenter.utils.HelpCenterEligibilityChecker;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$CollectionRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Initial;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionRowData;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$CollectionRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Error;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Initial;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.models.Space$Type;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import io.intercom.android.sdk.utilities.extensions.AppConfigExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Ü\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0001\u0018\u0000 Z2\u00020\u0001:\u0001ZBK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0010\u001a\u00020\u0011¢\u0006\u0004\b\u0012\u0010\u0013J\u0016\u00101\u001a\u0002022\f\u00103\u001a\b\u0012\u0004\u0012\u00020504H\u0002J\u0006\u0010<\u001a\u000205J\u000e\u0010=\u001a\u00020>2\u0006\u0010?\u001a\u00020>J\u0006\u0010@\u001a\u00020AJ\u0016\u0010B\u001a\u0002052\u000e\b\u0002\u0010C\u001a\b\u0012\u0004\u0012\u00020\t0DJ\u0019\u0010E\u001a\u0002052\n\b\u0002\u0010F\u001a\u0004\u0018\u00010GH\u0002¢\u0006\u0002\u0010HJ\u001c\u0010I\u001a\b\u0012\u0004\u0012\u00020K0J2\f\u0010L\u001a\b\u0012\u0004\u0012\u00020M0JH\u0002J\u000e\u0010N\u001a\u0002052\u0006\u0010O\u001a\u00020\tJ\u0006\u0010P\u001a\u000205J\b\u0010Q\u001a\u00020,H\u0002J\u0019\u0010R\u001a\u0002052\n\b\u0002\u0010F\u001a\u0004\u0018\u00010GH\u0002¢\u0006\u0002\u0010HJ\u0016\u0010I\u001a\b\u0012\u0004\u0012\u00020S0J2\u0006\u0010T\u001a\u00020UH\u0002J\u000e\u0010V\u001a\u000205H\u0082@¢\u0006\u0002\u0010WJ\b\u0010X\u001a\u00020,H\u0002J\u000e\u0010Y\u001a\u000207H\u0082@¢\u0006\u0002\u0010WR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001aR\u001c\u0010\u001f\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020!\u0018\u00010 0\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u001f\u0010\"\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020!\u0018\u00010 0\u0018¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001aR\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020&0%X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010'\u001a\b\u0012\u0004\u0012\u00020&0(¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u000e\u0010+\u001a\u00020,X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020,X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020/X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020/X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u00106\u001a\u0002078BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b:\u0010;\u001a\u0004\b8\u00109¨\u0006[",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "place",
         "",
         "helpCenterEligibilityChecker",
         "Lio/intercom/android/sdk/helpcenter/utils/HelpCenterEligibilityChecker;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Lio/intercom/android/sdk/identity/AppConfig;Lio/intercom/android/sdk/metrics/MetricTracker;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/utils/HelpCenterEligibilityChecker;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/data/CommonRepository;)V",
         "_collectionsState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState;",
         "collectionsState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getCollectionsState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "_collectionDetailsState",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState;",
         "collectionDetailsState",
         "getCollectionDetailsState",
         "_openState",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "openState",
         "getOpenState",
         "_effect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiEffects;",
         "effect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "isPartialHelpCenterLoaded",
         "",
         "hasClickedAtLeastOneArticle",
         "genericError",
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithoutCTA;",
         "notFoundError",
         "errorWithRetry",
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithCTA;",
         "onClick",
         "Lkotlin/Function0;",
         "",
         "searchBrowseTeamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "getSearchBrowseTeamPresenceState",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "searchBrowseTeamPresenceState$delegate",
         "Lkotlin/Lazy;",
         "onRetryOpen",
         "localizedContext",
         "Landroid/content/Context;",
         "context",
         "getScreenTitle",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "fetchCollections",
         "collectionIds",
         "",
         "sendFailedCollectionListMetric",
         "errorCode",
         "",
         "(Ljava/lang/Integer;)V",
         "transformToUiModel",
         "",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsRow$CollectionRow;",
         "body",
         "Lio/intercom/android/sdk/helpcenter/collections/HelpCenterCollection;",
         "fetchCollectionDetails",
         "collectionId",
         "onArticleClicked",
         "shouldAddSendMessageRow",
         "sendFailedSingleCollectionMetric",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow;",
         "networkResponse",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent;",
         "onNewConfig",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "isFromSearchBrowse",
         "getCtaData",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nHelpCenterViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterViewModel.kt\nio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,444:1\n1#2:445\n1563#3:446\n1634#3,3:447\n1563#3:450\n1634#3,3:451\n1563#3:454\n1634#3,3:455\n*S KotlinDebug\n*F\n+ 1 HelpCenterViewModel.kt\nio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel\n*L\n235#1:446\n235#1:447,3\n354#1:450\n354#1:451,3\n363#1:454\n363#1:455,3\n*E\n"
)
public final class HelpCenterViewModel extends ViewModel {
   @NotNull
   public static final HelpCenterViewModel$Companion Companion = new HelpCenterViewModel$Companion(null);
   @NotNull
   private final HelpCenterApi helpCenterApi;
   @NotNull
   private final AppConfig appConfig;
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final String place;
   @NotNull
   private final HelpCenterEligibilityChecker helpCenterEligibilityChecker;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final MutableStateFlow _collectionsState;
   @NotNull
   private final StateFlow collectionsState;
   @NotNull
   private final MutableStateFlow _collectionDetailsState;
   @NotNull
   private final StateFlow collectionDetailsState;
   @NotNull
   private final MutableStateFlow _openState;
   @NotNull
   private final StateFlow openState;
   @NotNull
   private final MutableSharedFlow _effect;
   @NotNull
   private final SharedFlow effect;
   private boolean isPartialHelpCenterLoaded;
   private boolean hasClickedAtLeastOneArticle;
   @NotNull
   private final ErrorState$WithoutCTA genericError;
   @NotNull
   private final ErrorState$WithoutCTA notFoundError;
   @NotNull
   private final Lazy searchBrowseTeamPresenceState$delegate;
   public static final int $stable;

   static { $stable = 8; }

   public HelpCenterViewModel(
      @NotNull HelpCenterApi var1,
      @NotNull AppConfig var2,
      @NotNull MetricTracker var3,
      @NotNull String var4,
      @NotNull HelpCenterEligibilityChecker var5,
      @NotNull CoroutineDispatcher var6,
      @NotNull IntercomDataLayer var7,
      @NotNull CommonRepository var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "helpCenterApi");
      Intrinsics.checkNotNullParameter(var2, "appConfig");
      Intrinsics.checkNotNullParameter(var3, "metricTracker");
      Intrinsics.checkNotNullParameter(var4, "place");
      Intrinsics.checkNotNullParameter(var5, "helpCenterEligibilityChecker");
      Intrinsics.checkNotNullParameter(var6, "dispatcher");
      Intrinsics.checkNotNullParameter(var7, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var8, "commonRepository");
      super();
      this.helpCenterApi = var1;
      this.appConfig = var2;
      this.metricTracker = var3;
      this.place = var4;
      this.helpCenterEligibilityChecker = var5;
      this.dispatcher = var6;
      this.intercomDataLayer = var7;
      this.commonRepository = var8;
      this._collectionsState = StateFlowKt.MutableStateFlow(CollectionsUiState$Initial.INSTANCE);
      this.collectionsState = FlowKt.asStateFlow(this._collectionsState);
      this._collectionDetailsState = StateFlowKt.MutableStateFlow(CollectionDetailsUiState$Initial.INSTANCE);
      this.collectionDetailsState = FlowKt.asStateFlow(this._collectionDetailsState);
      this._openState = StateFlowKt.MutableStateFlow(null);
      this.openState = FlowKt.asStateFlow(this._openState);
      this._effect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.effect = FlowKt.shareIn$default(this._effect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null);
      this.genericError = new ErrorState$WithoutCTA(0, 0, null, 7, null);
      this.notFoundError = new ErrorState$WithoutCTA(0, string.intercom_page_not_found, null, 5, null);
      final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_813_0 = this;
      this.searchBrowseTeamPresenceState$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel.searchBrowseTeamPresenceState_delegate$lambda$0(recoveredExpressionCapture_813_0)));
      if (this.place.length() > 0) {
         this.metricTracker.openedNativeHelpCenter(this.place, null);
      }

      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new HelpCenterViewModel$1(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HelpCenterViewModel$2(this, null), 3, null);
   }

   // $VF: synthetic method
   public HelpCenterViewModel(
      HelpCenterApi var1,
      AppConfig var2,
      MetricTracker var3,
      String var4,
      HelpCenterEligibilityChecker var5,
      CoroutineDispatcher var6,
      IntercomDataLayer var7,
      CommonRepository var8,
      int var9,
      DefaultConstructorMarker var10
   ) {
      this(var1, var2, var3, var4, (((var9 & 16) != 0) ? (HelpCenterEligibilityChecker) (HelpCenterEligibilityChecker.INSTANCE) : var5), (((var9 & 32) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var6), var7, var8);
   }

   @NotNull
   public final StateFlow getCollectionsState() {
      return this.collectionsState;
   }

   @NotNull
   public final StateFlow getCollectionDetailsState() {
      return this.collectionDetailsState;
   }

   @NotNull
   public final StateFlow getOpenState() {
      return this.openState;
   }

   @NotNull
   public final SharedFlow getEffect() {
      return this.effect;
   }

   private final ErrorState$WithCTA errorWithRetry(Function0 var1) {
      return new ErrorState$WithCTA(0, 0, null, 0, var1, 15, null);
   }

   private final ArticleViewState$TeamPresenceState getSearchBrowseTeamPresenceState() {
      Lazy var1 = this.searchBrowseTeamPresenceState$delegate;
      return (ArticleViewState$TeamPresenceState)var1.getValue();
   }

   public final void onRetryOpen() {
      this._openState.setValue(null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new HelpCenterViewModel$onRetryOpen$1(this, null), 2, null);
   }

   @NotNull
   public final Context localizedContext(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Configuration var2 = new Configuration(var1.getResources().getConfiguration());
      Locale var3 = ContextLocaliser.convertToLocale(this.appConfig.getHelpCenterLocale());
      var2.setLocale(var3);
      Context var10000 = var1.createConfigurationContext(var2);
      Intrinsics.checkNotNullExpressionValue(var10000, "createConfigurationContext(...)");
      return var10000;
   }

   @NotNull
   public final StringProvider getScreenTitle() {
      String var1 = this.appConfig.getSpaceLabelIfExists(Space$Type.HELP);
      StringProvider var10000;
      if (var1 != null) {
         String var2 = var1;
         int var3 = 0;
         var10000 = new ActualString(var2);
      } else {
         var10000 = new StringRes(string.intercom_get_help, null, 2, null);
      }

      return var10000;
   }

   public final void fetchCollections(@NotNull Set var1) {
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      if (!this.helpCenterEligibilityChecker.isEligibleUser()) {
         this._collectionsState.setValue(new CollectionsUiState$Error(this.genericError));
      } else {
         BuildersKt.launch$default(
            ViewModelKt.getViewModelScope(this), this.dispatcher, null, new HelpCenterViewModel$fetchCollections$1(this, var1, null), 2, null
         );
      }
   }

   // $VF: synthetic method
   public static void fetchCollections$default(HelpCenterViewModel var0, Set var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = SetsKt.emptySet();
      }

      var0.fetchCollections(var1);
   }

   private final void sendFailedCollectionListMetric(Integer var1) {
      this.metricTracker.failedHelpCenter("help_center", "collection_list", var1 != null ? var1.toString() : null, this.isFromSearchBrowse());
   }

   // $VF: synthetic method
   static void sendFailedCollectionListMetric$default(HelpCenterViewModel var0, Integer var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.sendFailedCollectionListMetric(var1);
   }

   private final List transformToUiModel(List var1) {
      Iterable var2 = var1;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         HelpCenterCollection var9 = (HelpCenterCollection)var8;
         Collection var11 = var5;
         int var10 = 0;
         var11.add(
            new CollectionsRow$CollectionRow(
               new CollectionRowData(
                  var9.getId(),
                  var9.getTitle(),
                  var9.getSummary().length() == 0 ? 8 : 0,
                  var9.getSummary(),
                  var9.getArticlesCount(),
                  var9.getCollectionsCount()
               )
            )
         );
      }

      return (List)var5;
   }

   public final void fetchCollectionDetails(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new HelpCenterViewModel$fetchCollectionDetails$1(this, var1, null), 2, null
      );
   }

   public final void onArticleClicked() {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new HelpCenterViewModel$onArticleClicked$1(this, null), 2, null);
   }

   private final boolean shouldAddSendMessageRow() {
      return AppConfigExtensionsKt.canStartNewConversation(this.appConfig) && this.hasClickedAtLeastOneArticle;
   }

   private final void sendFailedSingleCollectionMetric(Integer var1) {
      this.metricTracker.failedHelpCenter("help_center", "article_list", var1 != null ? var1.toString() : null, this.isFromSearchBrowse());
   }

   // $VF: synthetic method
   static void sendFailedSingleCollectionMetric$default(HelpCenterViewModel var0, Integer var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.sendFailedSingleCollectionMetric(var1);
   }

   private final List transformToUiModel(HelpCenterCollectionContent var1) {
      List var2 = new ArrayList();
      List var3 = var2;
      int var4 = 0;
      Iterable var5 = var1.getHelpCenterArticles();
      List var6 = var3;
      int var7 = 0;
      Iterable var8 = var5;
      ArrayList var9 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var5, 10));
      int var10 = 0;

      for (Object var12 : var8) {
         HelpCenterArticle var13 = (HelpCenterArticle)var12;
         Collection var14 = var9;
         int var15 = 0;
         String var16 = var13.getTitle();
         String var17 = var13.getArticleId();
         var14.add(new CollectionDetailsRow$ArticleRow(var17, var16));
      }

      var6.addAll((Collection & List)var9);
      var5 = var1.getSubCollections();
      var6 = var3;
      var7 = 0;
      var8 = var5;
      var9 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var5, 10));
      var10 = 0;

      for (Object var25 : var8) {
         HelpCenterCollection var26 = (HelpCenterCollection)var25;
         Collection var27 = var9;
         int var28 = 0;
         var27.add(
            new CollectionDetailsRow$CollectionRow(
               new CollectionRowData(
                  var26.getId(),
                  var26.getTitle(),
                  var26.getSummary().length() == 0 ? 8 : 0,
                  var26.getSummary(),
                  var26.getArticlesCount(),
                  var26.getCollectionsCount()
               )
            )
         );
      }

      var6.addAll((Collection & List)var9);
      return var2;
   }

   private final Object onNewConfig(Continuation var1) {
      HelpCenterViewModel recoveredThis = this;
      HelpCenterViewModel$onNewConfig$1 var8;
      label59: {
         if (var1 instanceof HelpCenterViewModel$onNewConfig$1) {
            var8 = (HelpCenterViewModel$onNewConfig$1)var1;
            if ((var8.label & -2147483648) != 0) {
               var8.label -= Integer.MIN_VALUE;
               break label59;
            }
         }

         var8 = new HelpCenterViewModel$onNewConfig$1(recoveredThis, var1);
      }

      MutableStateFlow var15;
      Object var16;
      label67: {
         MutableStateFlow var4;
         CollectionDetailsUiState$Content var5;
         Object var10000;
         label62: {
            Object var9;
            label49: {
               label48: {
                  Object var7 = var8.result;
                  var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                  CollectionsUiState$Content var11;
                  Object var13;
                  switch (var8.label) {
                     case 0:
                        ResultKt.throwOnFailure(var7);
                        CollectionsUiState var2 = (CollectionsUiState)recoveredThis._collectionsState.getValue();
                        if (!(var2 instanceof CollectionsUiState$Content)) {
                           break label49;
                        }

                        var15 = recoveredThis._collectionsState;
                        if (!recoveredThis.shouldAddSendMessageRow()) {
                           var16 = ((CollectionsUiState$Content)var2).copyWithoutSendMessageRow();
                           break label48;
                        }

                        var11 = (CollectionsUiState$Content)var2;
                        var4 = var15;
                        var8.L$0 = recoveredThis;
                        var8.L$1 = var4;
                        var8.L$2 = var11;
                        var8.label = 1;
                        var13 = recoveredThis.getCtaData(var8);
                        if (var13 == var9) {
                           return var9;
                        }
                        break;
                     case 1:
                        var11 = (CollectionsUiState$Content)var8.L$2;
                        var4 = (MutableStateFlow)var8.L$1;
                        recoveredThis = (HelpCenterViewModel)var8.L$0;
                        ResultKt.throwOnFailure(var7);
                        var13 = var7;
                        break;
                     case 2:
                        var5 = (CollectionDetailsUiState$Content)var8.L$1;
                        var4 = (MutableStateFlow)var8.L$0;
                        ResultKt.throwOnFailure(var7);
                        var10000 = var7;
                        break label62;
                     default:
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                  }

                  Object var6 = var13;
                  var15 = var4;
                  var16 = var11.copyWithSingleSendMessageRow((ArticleViewState$TeamPresenceState)var6);
               }

               var15.setValue(var16);
            }

            CollectionDetailsUiState var3 = (CollectionDetailsUiState)recoveredThis._collectionDetailsState.getValue();
            if (!(var3 instanceof CollectionDetailsUiState$Content)) {
               return Unit.INSTANCE;
            }

            var15 = recoveredThis._collectionDetailsState;
            if (!recoveredThis.shouldAddSendMessageRow()) {
               var16 = ((CollectionDetailsUiState$Content)var3).copyWithoutSendMessageRow();
               break label67;
            }

            var5 = (CollectionDetailsUiState$Content)var3;
            var4 = var15;
            var8.L$0 = var4;
            var8.L$1 = var5;
            var8.L$2 = null;
            var8.label = 2;
            var10000 = recoveredThis.getCtaData(var8);
            if (var10000 == var9) {
               return var9;
            }
         }

         Object var12 = var10000;
         var15 = var4;
         var16 = var5.copyWithSingleSendMessageRow((ArticleViewState$TeamPresenceState)var12);
      }

      var15.setValue(var16);
      return Unit.INSTANCE;
   }

   private final boolean isFromSearchBrowse() {
      return Intrinsics.areEqual(this.place, "search_browse");
   }

   private final Object getCtaData(Continuation var1) {
      HelpCenterViewModel recoveredThis = this;
      HelpCenterViewModel$getCtaData$1 var5;
      label28: {
         if (var1 instanceof HelpCenterViewModel$getCtaData$1) {
            var5 = (HelpCenterViewModel$getCtaData$1)var1;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label28;
            }
         }

         var5 = new HelpCenterViewModel$getCtaData$1(recoveredThis, var1);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            CommonRepository var7 = recoveredThis.commonRepository;
            var5.L$0 = recoveredThis;
            var5.label = 1;
            var10000 = var7.openMessenger(var5);
            if (var10000 == var6) {
               return var6;
            }
            break;
         case 1:
            recoveredThis = (HelpCenterViewModel)var5.L$0;
            ResultKt.throwOnFailure(var4);
            var10000 = var4;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      if (!(var2 instanceof NetworkResponse$Success)) {
         return recoveredThis.getSearchBrowseTeamPresenceState();
      }

      OpenMessengerResponse$NewConversationData var8 = ((OpenMessengerResponse)((NetworkResponse$Success)var2).getBody()).getNewConversationData();
      if (var8 == null) {
         return recoveredThis.getSearchBrowseTeamPresenceState();
      }

      OpenMessengerResponse$NewConversationData var3 = var8;
      return ArticleViewState$TeamPresenceState.copy$default(
         recoveredThis.getSearchBrowseTeamPresenceState(), null, null, null, 0, 0, 0, null, null, false, var3.getCta(), 511, null
      );
   }

   private static final ArticleViewState$TeamPresenceState searchBrowseTeamPresenceState_delegate$lambda$0(HelpCenterViewModel var0) {
      return TeammateHelpKt.computeViewState(null, ArticleViewState$TeamPresenceState.Companion.getDefaultTeamPresenceState(), var0.appConfig, var0.place, true);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_openState$p(HelpCenterViewModel var0) {
      return var0._openState;
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(HelpCenterViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_collectionsState$p(HelpCenterViewModel var0) {
      return var0._collectionsState;
   }

   // $VF: synthetic method
   public static final HelpCenterApi access$getHelpCenterApi$p(HelpCenterViewModel var0) {
      return var0.helpCenterApi;
   }

   // $VF: synthetic method
   public static final void access$sendFailedCollectionListMetric(HelpCenterViewModel var0, Integer var1) {
      var0.sendFailedCollectionListMetric(var1);
   }

   // $VF: synthetic method
   public static final ErrorState$WithoutCTA access$getNotFoundError$p(HelpCenterViewModel var0) {
      return var0.notFoundError;
   }

   // $VF: synthetic method
   public static final ErrorState$WithoutCTA access$getGenericError$p(HelpCenterViewModel var0) {
      return var0.genericError;
   }

   // $VF: synthetic method
   public static final ErrorState$WithCTA access$errorWithRetry(HelpCenterViewModel var0, Function0 var1) {
      return var0.errorWithRetry(var1);
   }

   // $VF: synthetic method
   public static final void access$setPartialHelpCenterLoaded$p(HelpCenterViewModel var0, boolean var1) {
      var0.isPartialHelpCenterLoaded = var1;
   }

   // $VF: synthetic method
   public static final MetricTracker access$getMetricTracker$p(HelpCenterViewModel var0) {
      return var0.metricTracker;
   }

   // $VF: synthetic method
   public static final boolean access$isPartialHelpCenterLoaded$p(HelpCenterViewModel var0) {
      return var0.isPartialHelpCenterLoaded;
   }

   // $VF: synthetic method
   public static final boolean access$isFromSearchBrowse(HelpCenterViewModel var0) {
      return var0.isFromSearchBrowse();
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_effect$p(HelpCenterViewModel var0) {
      return var0._effect;
   }

   // $VF: synthetic method
   public static final List access$transformToUiModel(HelpCenterViewModel var0, List var1) {
      return var0.transformToUiModel(var1);
   }

   // $VF: synthetic method
   public static final boolean access$shouldAddSendMessageRow(HelpCenterViewModel var0) {
      return var0.shouldAddSendMessageRow();
   }

   // $VF: synthetic method
   public static final Object access$getCtaData(HelpCenterViewModel var0, Continuation var1) {
      return var0.getCtaData(var1);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_collectionDetailsState$p(HelpCenterViewModel var0) {
      return var0._collectionDetailsState;
   }

   // $VF: synthetic method
   public static final void access$sendFailedSingleCollectionMetric(HelpCenterViewModel var0, Integer var1) {
      var0.sendFailedSingleCollectionMetric(var1);
   }

   // $VF: synthetic method
   public static final List access$transformToUiModel(HelpCenterViewModel var0, HelpCenterCollectionContent var1) {
      return var0.transformToUiModel(var1);
   }

   // $VF: synthetic method
   public static final void access$setHasClickedAtLeastOneArticle$p(HelpCenterViewModel var0, boolean var1) {
      var0.hasClickedAtLeastOneArticle = var1;
   }

   // $VF: synthetic method
   public static final Object access$onNewConfig(HelpCenterViewModel var0, Continuation var1) {
      return var0.onNewConfig(var1);
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(HelpCenterViewModel var0) {
      return var0.intercomDataLayer;
   }
}
