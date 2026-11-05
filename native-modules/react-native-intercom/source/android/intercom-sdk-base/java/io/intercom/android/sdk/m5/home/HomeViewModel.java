package io.intercom.android.sdk.m5.home;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.home.data.HomeRepository;
import io.intercom.android.sdk.m5.home.reducers.HomeReducer;
import io.intercom.android.sdk.m5.home.states.HomeClientState;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Loading;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.HomeConfig;
import io.intercom.android.sdk.models.OpenConfig;
import io.intercom.android.sdk.models.OpenToSpace;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 -2\u00020\u0001:\u0001-B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0002J\b\u0010'\u001a\u00020$H\u0002J\u0006\u0010(\u001a\u00020$J\u0006\u0010)\u001a\u00020$J\u0010\u0010*\u001a\u00020$2\u0006\u0010+\u001a\u00020,H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0014¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001d¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u000e\u0010!\u001a\u00020\"X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006.",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/HomeViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "lifecycle",
         "Landroidx/lifecycle/Lifecycle;",
         "homeRepository",
         "Lio/intercom/android/sdk/m5/home/data/HomeRepository;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "homeReducer",
         "Lio/intercom/android/sdk/m5/home/reducers/HomeReducer;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "<init>",
         "(Landroidx/lifecycle/Lifecycle;Lio/intercom/android/sdk/m5/home/data/HomeRepository;Lio/intercom/android/sdk/m5/data/CommonRepository;Lio/intercom/android/sdk/m5/home/reducers/HomeReducer;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lkotlinx/coroutines/CoroutineDispatcher;)V",
         "_effect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiEffects;",
         "effect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/home/states/HomeClientState;",
         "homeRefreshJob",
         "Lkotlinx/coroutines/Job;",
         "uiState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "getUiState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "hasConversationScreenOpenedDirectlyFromHome",
         "",
         "handleOpening",
         "",
         "openToSpace",
         "Lio/intercom/android/sdk/models/OpenToSpace;",
         "fetchHomeData",
         "onHeaderImageLoaded",
         "onRetryClicked",
         "onConfig",
         "config",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nHomeViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 5 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,214:1\n49#2:215\n51#2:219\n46#3:216\n51#3:218\n105#4:217\n230#5,5:220\n230#5,5:225\n*S KotlinDebug\n*F\n+ 1 HomeViewModel.kt\nio/intercom/android/sdk/m5/home/HomeViewModel\n*L\n58#1:215\n58#1:219\n58#1:216\n58#1:218\n58#1:217\n165#1:220,5\n173#1:225,5\n*E\n"
)
public final class HomeViewModel extends ViewModel {
   @NotNull
   public static final HomeViewModel$Companion Companion = new HomeViewModel$Companion(null);
   @NotNull
   private final HomeRepository homeRepository;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final HomeReducer homeReducer;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final MutableSharedFlow _effect;
   @NotNull
   private final SharedFlow effect;
   @NotNull
   private final MutableStateFlow clientState;
   @Nullable
   private Job homeRefreshJob;
   @NotNull
   private final StateFlow uiState;
   private boolean hasConversationScreenOpenedDirectlyFromHome;
   public static final int $stable;

   static { $stable = 8; }

   public HomeViewModel(
      @NotNull Lifecycle var1,
      @NotNull HomeRepository var2,
      @NotNull CommonRepository var3,
      @NotNull HomeReducer var4,
      @NotNull IntercomDataLayer var5,
      @NotNull CoroutineDispatcher var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "lifecycle");
      Intrinsics.checkNotNullParameter(var2, "homeRepository");
      Intrinsics.checkNotNullParameter(var3, "commonRepository");
      Intrinsics.checkNotNullParameter(var4, "homeReducer");
      Intrinsics.checkNotNullParameter(var5, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var6, "dispatcher");
      super();
      this.homeRepository = var2;
      this.commonRepository = var3;
      this.homeReducer = var4;
      this.intercomDataLayer = var5;
      this.dispatcher = var6;
      this._effect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.effect = FlowKt.shareIn$default(this._effect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null);
      this.clientState = StateFlowKt.MutableStateFlow(new HomeClientState(null, null, false, 7, null));
      Flow var7 = this.clientState;
      int var8 = 0;
      Flow var9 = var7;
      int var10 = 0;
      int var11 = 0;
      this.uiState = FlowKt.stateIn(
         new HomeViewModel$special$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var9, this),
         ViewModelKt.getViewModelScope(this),
         SharingStarted.Companion.WhileSubscribed$default(SharingStarted.Companion, 5000L, 0L, 2, null),
         new HomeUiState$Loading(null)
      );
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$1(this, var1, null), 3, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$2(this, var1, null), 3, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$3(this, null), 3, null);
   }

   // $VF: synthetic method
   public HomeViewModel(
      Lifecycle var1,
      HomeRepository var2,
      CommonRepository var3,
      HomeReducer var4,
      IntercomDataLayer var5,
      CoroutineDispatcher var6,
      int var7,
      DefaultConstructorMarker var8
   ) {
      this(var1, var2, var3, (((var7 & 8) != 0) ? (HomeReducer) (new HomeReducer(null, null, 3, null)) : var4), var5, (((var7 & 32) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var6));
   }

   @NotNull
   public final SharedFlow getEffect() {
      return this.effect;
   }

   @NotNull
   public final StateFlow getUiState() {
      return this.uiState;
   }

   private final void handleOpening(OpenToSpace var1) {
      switch (var1) {
         case HOME:
            this.fetchHomeData();
            break;
         case MESSAGES:
            BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$handleOpening$1(this, null), 3, null);
            break;
         case CONVERSATION:
            if (!this.hasConversationScreenOpenedDirectlyFromHome) {
               this.hasConversationScreenOpenedDirectlyFromHome = true;
               BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$handleOpening$2(this, null), 3, null);
            } else {
               this.fetchHomeData();
            }
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   private final void fetchHomeData() {
      if (this.homeRefreshJob != null) {
         DefaultImpls.cancel$default(this.homeRefreshJob, (CancellationException)null, 1, null);
      }

      this.homeRefreshJob = BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new HomeViewModel$fetchHomeData$1(this, null), 3, null);
   }

   public final void onHeaderImageLoaded() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      HomeClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (HomeClientState)var3;
         int var5 = 0;
         var6 = HomeClientState.copy$default((HomeClientState)var6, null, null, true, 3, null);
      } while (!var1.compareAndSet(var3, var6));
   }

   public final void onRetryClicked() {
      MutableStateFlow var1 = this.clientState;
      int var2 = 0;

      Object var3;
      HomeClientState var6;
      do {
         var3 = var1.getValue();
         var6 = (HomeClientState)var3;
         int var5 = 0;
         var6 = HomeClientState.copy$default((HomeClientState)var6, null, null, false, 4, null);
      } while (!var1.compareAndSet(var3, var6));

      this.fetchHomeData();
   }

   private final void onConfig(AppConfig var1) {
      OpenToSpace var4;
      label15: {
         ConfigModules var10001 = var1.getConfigModules();
         if (var10001 != null) {
            HomeConfig var2 = var10001.getHome();
            if (var2 != null) {
               OpenConfig var3 = var2.getOpenConfig();
               if (var3 != null) {
                  var4 = var3.getOpenTo();
                  if (var4 != null) {
                     break label15;
                  }
               }
            }
         }

         var4 = OpenToSpace.HOME;
      }

      this.handleOpening(var4);
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_effect$p(HomeViewModel var0) {
      return var0._effect;
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(HomeViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$getClientState$p(HomeViewModel var0) {
      return var0.clientState;
   }

   // $VF: synthetic method
   public static final void access$fetchHomeData(HomeViewModel var0) {
      var0.fetchHomeData();
   }

   // $VF: synthetic method
   public static final CoroutineDispatcher access$getDispatcher$p(HomeViewModel var0) {
      return var0.dispatcher;
   }

   // $VF: synthetic method
   public static final HomeRepository access$getHomeRepository$p(HomeViewModel var0) {
      return var0.homeRepository;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(HomeViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final HomeReducer access$getHomeReducer$p(HomeViewModel var0) {
      return var0.homeReducer;
   }

   // $VF: synthetic method
   public static final void access$onConfig(HomeViewModel var0, AppConfig var1) {
      var0.onConfig(var1);
   }
}
