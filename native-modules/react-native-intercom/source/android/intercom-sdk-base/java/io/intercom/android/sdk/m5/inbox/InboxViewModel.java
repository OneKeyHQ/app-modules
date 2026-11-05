package io.intercom.android.sdk.m5.inbox;

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
import io.intercom.android.sdk.m5.inbox.data.InboxPagingSource;
import io.intercom.android.sdk.m5.inbox.data.InboxRepository;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.EmptyState;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
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

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\b\u0001\u0018\u0000 52\u00020\u0001:\u00015B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010/\u001a\u0002002\u0006\u00101\u001a\u00020\u0016H\u0002J\u0006\u00102\u001a\u000200J\u000e\u00103\u001a\u0002002\u0006\u00104\u001a\u00020%R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u000e0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u001f\u0010\u0010\u001a\u0010\u0012\f\u0012\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u000e0\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R \u0010\u0014\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00170\u00150\rX\u0082\u0004¢\u0006\u0002\n\u0000R#\u0010\u0018\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00170\u00150\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\u001a\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u001c0\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u001d\u001a\u00020\u001e@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u001d\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020%0$0#¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u0014\u0010(\u001a\b\u0012\u0004\u0012\u00020*0)X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010+\u001a\b\u0012\u0004\u0012\u00020*0,¢\u0006\b\n\u0000\u001a\u0004\b-\u0010.¨\u00066",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/InboxViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "inboxRepository",
         "Lio/intercom/android/sdk/m5/inbox/data/InboxRepository;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/inbox/data/InboxRepository;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/data/CommonRepository;)V",
         "_openState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "openState",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getOpenState",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "_typingConversations",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "typingConversations",
         "getTypingConversations",
         "typingJobs",
         "Ljava/util/concurrent/ConcurrentHashMap;",
         "Lkotlinx/coroutines/Job;",
         "value",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "emptyState",
         "getEmptyState",
         "()Lio/intercom/android/sdk/models/EmptyState;",
         "inboxPagingData",
         "Lkotlinx/coroutines/flow/Flow;",
         "Landroidx/paging/PagingData;",
         "Lio/intercom/android/sdk/models/Conversation;",
         "getInboxPagingData",
         "()Lkotlinx/coroutines/flow/Flow;",
         "_effect",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiEffects;",
         "effect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "getEffect",
         "()Lkotlinx/coroutines/flow/SharedFlow;",
         "clearTypingState",
         "",
         "conversationId",
         "onRetryOpen",
         "onConversationClick",
         "it",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nInboxViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxViewModel.kt\nio/intercom/android/sdk/m5/inbox/InboxViewModel\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,177:1\n230#2,5:178\n*S KotlinDebug\n*F\n+ 1 InboxViewModel.kt\nio/intercom/android/sdk/m5/inbox/InboxViewModel\n*L\n141#1:178,5\n*E\n"
)
public final class InboxViewModel extends ViewModel {
   @NotNull
   public static final InboxViewModel$Companion Companion = new InboxViewModel$Companion(null);
   @NotNull
   private final InboxRepository inboxRepository;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final MutableStateFlow _openState;
   @NotNull
   private final StateFlow openState;
   @NotNull
   private final MutableStateFlow _typingConversations;
   @NotNull
   private final StateFlow typingConversations;
   @NotNull
   private final ConcurrentHashMap typingJobs;
   @NotNull
   private EmptyState emptyState;
   @NotNull
   private final Flow inboxPagingData;
   @NotNull
   private final MutableSharedFlow _effect;
   @NotNull
   private final SharedFlow effect;
   public static final int $stable;

   static { $stable = 8; }
   private static final long TYPING_INDICATOR_TIMEOUT = 5000L;

   public InboxViewModel(@NotNull InboxRepository var1, @NotNull CoroutineDispatcher var2, @NotNull IntercomDataLayer var3, @NotNull CommonRepository var4) {
      Intrinsics.checkNotNullParameter(var1, "inboxRepository");
      Intrinsics.checkNotNullParameter(var2, "dispatcher");
      Intrinsics.checkNotNullParameter(var3, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var4, "commonRepository");
      super();
      this.inboxRepository = var1;
      this.dispatcher = var2;
      this.intercomDataLayer = var3;
      this.commonRepository = var4;
      this._openState = StateFlowKt.MutableStateFlow(null);
      this.openState = FlowKt.asStateFlow(this._openState);
      this._typingConversations = StateFlowKt.MutableStateFlow(MapsKt.emptyMap());
      this.typingConversations = FlowKt.asStateFlow(this._typingConversations);
      this.typingJobs = new ConcurrentHashMap();
      this.emptyState = EmptyState.Companion.getNULL();
      final io.intercom.android.sdk.m5.inbox.InboxViewModel recoveredExpressionCapture_960_0 = this;
      this.inboxPagingData = CachedPagingDataKt.cachedIn(
         new Pager(new PagingConfig(20, 0, false, 0, 0, 0, 58, null), null, ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.inbox.InboxViewModel.inboxPagingData$lambda$0(recoveredExpressionCapture_960_0)), 2, null).getFlow(),
         ViewModelKt.getViewModelScope(this)
      );
      this._effect = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.effect = FlowKt.shareIn$default(this._effect, ViewModelKt.getViewModelScope(this), SharingStarted.Companion.getEagerly(), 0, 4, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new InboxViewModel$1(this, null), 2, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new InboxViewModel$2(this, null), 3, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new InboxViewModel$3(this, null), 3, null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new InboxViewModel$4(this, null), 3, null);
   }

   // $VF: synthetic method
   public InboxViewModel(InboxRepository var1, CoroutineDispatcher var2, IntercomDataLayer var3, CommonRepository var4, int var5, DefaultConstructorMarker var6) {
      if ((var5 & 1) != 0) {
         var1 = new InboxRepository(null, null, null, 7, null);
      }

      if ((var5 & 2) != 0) {
         var2 = Dispatchers.getIO();
      }

      if ((var5 & 4) != 0) {
         var3 = Injector.get().getDataLayer();
      }

      if ((var5 & 8) != 0) {
         MessengerApi var10002 = Injector.get().getMessengerApi();
         Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
         var4 = new CommonRepository(var10002, var3);
      }

      this(var1, var2, var3, var4);
   }

   @NotNull
   public final StateFlow getOpenState() {
      return this.openState;
   }

   @NotNull
   public final StateFlow getTypingConversations() {
      return this.typingConversations;
   }

   @NotNull
   public final EmptyState getEmptyState() {
      return this.emptyState;
   }

   @NotNull
   public final Flow getInboxPagingData() {
      return this.inboxPagingData;
   }

   @NotNull
   public final SharedFlow getEffect() {
      return this.effect;
   }

   private final void clearTypingState(String var1) {
      Job var10000 = (Job)this.typingJobs.get(var1);
      if (var10000 != null) {
         DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
      }

      this.typingJobs.remove(var1);
      MutableStateFlow var2 = this._typingConversations;
      int var3 = 0;

      Object var4;
      Map var7;
      do {
         var4 = var2.getValue();
         var7 = (Map)var4;
         int var6 = 0;
         var7 = MapsKt.minus((Map)var7, var1);
      } while (!var2.compareAndSet(var4, var7));
   }

   public final void onRetryOpen() {
      this._openState.setValue(null);
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new InboxViewModel$onRetryOpen$1(this, null), 2, null);
   }

   public final void onConversationClick(@NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new InboxViewModel$onConversationClick$1(this, var1, null), 2, null);
   }

   private static final PagingSource inboxPagingData$lambda$0(InboxViewModel var0) {
      return new InboxPagingSource(var0.inboxRepository, var0.intercomDataLayer, new InboxViewModel$inboxPagingData$1$1(var0, null));
   }

   public InboxViewModel() {
      this(null, null, null, null, 15, null);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_openState$p(InboxViewModel var0) {
      return var0._openState;
   }

   // $VF: synthetic method
   public static final CommonRepository access$getCommonRepository$p(InboxViewModel var0) {
      return var0.commonRepository;
   }

   // $VF: synthetic method
   public static final MutableSharedFlow access$get_effect$p(InboxViewModel var0) {
      return var0._effect;
   }

   // $VF: synthetic method
   public static final void access$setEmptyState$p(InboxViewModel var0, EmptyState var1) {
      var0.emptyState = var1;
   }

   // $VF: synthetic method
   public static final InboxRepository access$getInboxRepository$p(InboxViewModel var0) {
      return var0.inboxRepository;
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(InboxViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final CoroutineDispatcher access$getDispatcher$p(InboxViewModel var0) {
      return var0.dispatcher;
   }

   // $VF: synthetic method
   public static final ConcurrentHashMap access$getTypingJobs$p(InboxViewModel var0) {
      return var0.typingJobs;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_typingConversations$p(InboxViewModel var0) {
      return var0._typingConversations;
   }

   // $VF: synthetic method
   public static final void access$clearTypingState(InboxViewModel var0, String var1) {
      var0.clearTypingState(var1);
   }
}
