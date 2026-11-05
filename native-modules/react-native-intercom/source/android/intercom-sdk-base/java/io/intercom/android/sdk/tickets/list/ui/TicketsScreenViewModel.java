package io.intercom.android.sdk.tickets.list.ui;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.CachedPagingDataKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingSource;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import io.intercom.android.sdk.tickets.list.data.TicketsPagingSource;
import io.intercom.android.sdk.tickets.list.reducers.TicketRowReducerKt;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
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

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0001\u0018\u0000 '2\u00020\u0001:\u0001'BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r0\u000b¢\u0006\u0004\b\u000e\u0010\u000fJ\u0006\u0010%\u001a\u00020&R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001c\u0010\u0016\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u00180\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u001f\u0010\u001a\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u00180\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010!\u001a\b\u0012\u0004\u0012\u00020 0\"¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "repository",
         "Lio/intercom/android/sdk/tickets/create/data/TicketRepository;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "pager",
         "Landroidx/paging/Pager;",
         "",
         "Lio/intercom/android/sdk/models/Ticket;",
         "<init>",
         "(Lio/intercom/android/sdk/tickets/create/data/TicketRepository;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/data/CommonRepository;Landroidx/paging/Pager;)V",
         "pagerFlow",
         "Lkotlinx/coroutines/flow/Flow;",
         "Landroidx/paging/PagingData;",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "getPagerFlow",
         "()Lkotlinx/coroutines/flow/Flow;",
         "_openState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "openState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getOpenState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "_effect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketsScreenEffects;",
         "effect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "onRetryOpen",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nTicketsScreenViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel\n+ 2 Transform.kt\nkotlinx/coroutines/flow/FlowKt__TransformKt\n+ 3 Emitters.kt\nkotlinx/coroutines/flow/FlowKt__EmittersKt\n+ 4 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n*L\n1#1,120:1\n49#2:121\n51#2:125\n46#3:122\n51#3:124\n105#4:123\n*S KotlinDebug\n*F\n+ 1 TicketsScreenViewModel.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel\n*L\n53#1:121\n53#1:125\n53#1:122\n53#1:124\n53#1:123\n*E\n"
)
public final class TicketsScreenViewModel extends ViewModel {
   @NotNull
   public static final TicketsScreenViewModel$Companion Companion = new TicketsScreenViewModel$Companion(null);
   @NotNull
   private final TicketRepository repository;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final Flow pagerFlow;
   @NotNull
   private final MutableStateFlow _openState;
   @NotNull
   private final StateFlow openState;
   @NotNull
   private final MutableSharedFlow _effect;
   @NotNull
   private final SharedFlow effect;
   public static final int $stable;

   static { $stable = 8; }

   public TicketsScreenViewModel(
      @NotNull TicketRepository var1, @NotNull CoroutineDispatcher var2, @NotNull IntercomDataLayer var3, @NotNull CommonRepository var4, @NotNull Pager var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "repository");
      Intrinsics.checkNotNullParameter(var2, "dispatcher");
      Intrinsics.checkNotNullParameter(var3, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var4, "commonRepository");
      Intrinsics.checkNotNullParameter(var5, "pager");
      super();
      this.repository = var1;
      this.dispatcher = var2;
      this.intercomDataLayer = var3;
      this.commonRepository = var4;
      Flow var6 = var5.getFlow();
      int var7 = 0;
      Flow var8 = var6;
      int var9 = 0;
      int var10 = 0;
      this.pagerFlow = CachedPagingDataKt.cachedIn(
         new TicketsScreenViewModel$special$RecoveredInnere3b0c44298fc1c14$inlined$map$1(var8), ViewModelKt.getViewModelScope(this)
      );
      this._openState = StateFlowKt.MutableStateFlow(null);
      this.openState = FlowKt.asStateFlow(this._openState);
      this._effect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.effect = FlowKt.shareIn$default(this._effect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new TicketsScreenViewModel$2(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new TicketsScreenViewModel$3(this, null), 3, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new TicketsScreenViewModel$4(this, null), 3, null);
   }

   // $VF: synthetic method
   public TicketsScreenViewModel(
      TicketRepository var1, CoroutineDispatcher var2, IntercomDataLayer var3, CommonRepository var4, Pager var5, int var6, DefaultConstructorMarker var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = new TicketRepository(null, null, null, null, null, 31, null);
      }

      if ((var6 & 2) != 0) {
         var2 = Dispatchers.getIO();
      }

      if ((var6 & 4) != 0) {
         var3 = Injector.get().getDataLayer();
      }

      if ((var6 & 8) != 0) {
         MessengerApi var10002 = Injector.get().getMessengerApi();
         Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
         var4 = new CommonRepository(var10002, var3);
      }

      if ((var6 & 16) != 0) {
         final io.intercom.android.sdk.tickets.create.data.TicketRepository recoveredExpressionCapture_1618_0 = var1;
         var5 = new Pager(new PagingConfig(10, 0, false, 0, 0, 0, 58, null), null, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.list.ui.TicketsScreenViewModel._init_$lambda$0(recoveredExpressionCapture_1618_0)), 2, null);
      }

      this(var1, var2, var3, var4, var5);
   }

   @NotNull
   public final Flow getPagerFlow() {
      return this.pagerFlow;
   }

   @NotNull
   public final StateFlow getOpenState() {
      return this.openState;
   }

   @NotNull
   public final SharedFlow getEffect() {
      return this.effect;
   }

   public final void onRetryOpen() {
      this._openState.setValue(null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new TicketsScreenViewModel$onRetryOpen$1(this, null), 2, null);
   }

   private static final PagingSource _init_$lambda$0(TicketRepository var0) {
      return new TicketsPagingSource(var0);
   }

   // $VF: synthetic method
   private static final Object pagerFlow$lambda$1$reduceTicketRowData(Ticket var0, Continuation var1) {
      return TicketRowReducerKt.reduceTicketRowData(var0);
   }

   public TicketsScreenViewModel() {
      this(null, null, null, null, null, 31, null);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_openState$p(TicketsScreenViewModel var0) {
      return var0._openState;
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(TicketsScreenViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final Object access$pagerFlow$lambda$1$reduceTicketRowData(Ticket var0, Continuation var1) {
      return pagerFlow$lambda$1$reduceTicketRowData(var0, var1);
   }

   // $VF: synthetic method
   public static final TicketRepository access$getRepository$p(TicketsScreenViewModel var0) {
      return var0.repository;
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_effect$p(TicketsScreenViewModel var0) {
      return var0._effect;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(TicketsScreenViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final CoroutineDispatcher access$getDispatcher$p(TicketsScreenViewModel var0) {
      return var0.dispatcher;
   }
}
