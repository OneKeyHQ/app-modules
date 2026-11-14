package io.intercom.android.sdk.tickets;

import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000h\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0001\u0018\u0000 '2\u00020\u0001:\u0001'BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0002J\u0015\u0010$\u001a\u00020!2\u0006\u0010\u0010\u001a\u00020\u0011H\u0001¢\u0006\u0002\b%J\u0010\u0010&\u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u001a\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001f¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketDetailViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "launchedFrom",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "user",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "repository",
         "Lio/intercom/android/sdk/tickets/create/data/TicketRepository;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;Lio/intercom/android/sdk/identity/UserIdentity;Lio/intercom/android/sdk/metrics/MetricTracker;Lio/intercom/android/sdk/tickets/create/data/TicketRepository;Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "ticketId",
         "",
         "metricSent",
         "",
         "_stateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/tickets/TicketDetailState;",
         "stateFlow",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getStateFlow",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "activeAdminsAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "getActiveAdminsAvatars",
         "()Ljava/util/List;",
         "markAsReadIfNecessary",
         "",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "fetchTicketDetail",
         "fetchTicketDetail$intercom_sdk_base_release",
         "fireMetricIfNecessary",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nTicketDetailViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailViewModel.kt\nio/intercom/android/sdk/tickets/TicketDetailViewModel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,173:1\n1563#2:174\n1634#2,3:175\n*S KotlinDebug\n*F\n+ 1 TicketDetailViewModel.kt\nio/intercom/android/sdk/tickets/TicketDetailViewModel\n*L\n59#1:174\n59#1:175,3\n*E\n"
)
public final class TicketDetailViewModel extends ViewModel {
   @NotNull
   public static final TicketDetailViewModel$Companion Companion = new TicketDetailViewModel$Companion(null);
   @NotNull
   private final TicketLaunchedFrom launchedFrom;
   @NotNull
   private final UserIdentity user;
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final TicketRepository repository;
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @Nullable
   private String ticketId;
   private boolean metricSent;
   @NotNull
   private final MutableStateFlow _stateFlow;
   @NotNull
   private final StateFlow stateFlow;
   public static final int $stable;

   static { $stable = 8; }

   public TicketDetailViewModel(
      @NotNull TicketLaunchedFrom var1,
      @NotNull UserIdentity var2,
      @NotNull MetricTracker var3,
      @NotNull TicketRepository var4,
      @NotNull CoroutineDispatcher var5,
      @NotNull IntercomDataLayer var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "launchedFrom");
      Intrinsics.checkNotNullParameter(var2, "user");
      Intrinsics.checkNotNullParameter(var3, "metricTracker");
      Intrinsics.checkNotNullParameter(var4, "repository");
      Intrinsics.checkNotNullParameter(var5, "dispatcher");
      Intrinsics.checkNotNullParameter(var6, "intercomDataLayer");
      super();
      this.launchedFrom = var1;
      this.user = var2;
      this.metricTracker = var3;
      this.repository = var4;
      this.dispatcher = var5;
      this.intercomDataLayer = var6;
      this._stateFlow = StateFlowKt.MutableStateFlow(TicketDetailState$Initial.INSTANCE);
      this.stateFlow = this._stateFlow;
      TicketLaunchedFrom var7 = this.launchedFrom;
      if (var7 instanceof TicketLaunchedFrom$Conversation) {
         this.fireMetricIfNecessary(((TicketLaunchedFrom$Conversation)this.launchedFrom).getTicket());
         this.markAsReadIfNecessary(((TicketLaunchedFrom$Conversation)this.launchedFrom).getTicket());
         Ticket var8 = ((TicketLaunchedFrom$Conversation)this.launchedFrom).getTicket();
         int var9 = 0;
         this.ticketId = var8.getId();
         this._stateFlow.setValue(TicketDetailReducerKt.computeTicketViewState(var8, this.user, this.getActiveAdminsAvatars(), this.launchedFrom));
      } else {
         if (!(var7 instanceof TicketLaunchedFrom$Other)) {
            throw new NoWhenBranchMatchedException();
         }

         this.fetchTicketDetail$intercom_sdk_base_release(((TicketLaunchedFrom$Other)this.launchedFrom).getTicketId());
      }

      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new TicketDetailViewModel$2(this, null), 3, null);
   }

   // $VF: synthetic method
   public TicketDetailViewModel(
      TicketLaunchedFrom var1,
      UserIdentity var2,
      MetricTracker var3,
      TicketRepository var4,
      CoroutineDispatcher var5,
      IntercomDataLayer var6,
      int var7,
      DefaultConstructorMarker var8
   ) {
      this(var1, (((var7 & 2) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var2), (((var7 & 4) != 0) ? (MetricTracker) (Injector.get().getMetricTracker()) : var3), (((var7 & 8) != 0) ? (TicketRepository) (new TicketRepository(null, null, null, null, null, 31, null)) : var4), (((var7 & 16) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var5), (((var7 & 32) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var6));
   }

   @NotNull
   public final StateFlow getStateFlow() {
      return this.stateFlow;
   }

   private final List getActiveAdminsAvatars() {
      Iterable var1 = ((TeamPresence)this.intercomDataLayer.getTeamPresence().getValue()).getBuiltActiveAdmins();
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         Participant var8 = (Participant)var7;
         Collection var10 = var4;
         int var9 = 0;
         Avatar var10002 = var8.getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
         Boolean var10003 = var8.isBot();
         Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
         var10.add(new AvatarWrapper(var10002, var10003));
      }

      return (List)var4;
   }

   private final void markAsReadIfNecessary(Ticket var1) {
      if (Intrinsics.areEqual(var1.isRead(), false)) {
         BuildersKt.launch$default(
            ViewModelKt.getViewModelScope(this), this.dispatcher, null, new TicketDetailViewModel$markAsReadIfNecessary$1(this, var1, null), 2, null
         );
      }
   }

   @VisibleForTesting
   public final void fetchTicketDetail$intercom_sdk_base_release(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "ticketId");
      if (StringsKt.isBlank(var1)) {
         this._stateFlow.setValue(new TicketDetailState$Error(new ErrorState$WithoutCTA(0, 0, string.intercom_error_loading_ticket, 3, null)));
      } else {
         BuildersKt.launch$default(
            ViewModelKt.getViewModelScope(this), this.dispatcher, null, new TicketDetailViewModel$fetchTicketDetail$1(this, var1, null), 2, null
         );
      }
   }

   private final void fireMetricIfNecessary(Ticket var1) {
      if (!this.metricSent) {
         MetricTracker var10000 = this.metricTracker;
         Long var10001 = var1.getTicketTypeId();
         String var10002 = var1.getConversationId();
         if (var10002 == null) {
            var10002 = "";
         }

         var10000.viewedTicketDetails(var10001, var10002, var1.getCurrentStatus().getType(), this.launchedFrom.getFrom());
      }
   }

   // $VF: synthetic method
   public static final TicketRepository access$getRepository$p(TicketDetailViewModel var0) {
      return var0.repository;
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_stateFlow$p(TicketDetailViewModel var0) {
      return var0._stateFlow;
   }

   // $VF: synthetic method
   public static final void access$fireMetricIfNecessary(TicketDetailViewModel var0, Ticket var1) {
      var0.fireMetricIfNecessary(var1);
   }

   // $VF: synthetic method
   public static final void access$markAsReadIfNecessary(TicketDetailViewModel var0, Ticket var1) {
      var0.markAsReadIfNecessary(var1);
   }

   // $VF: synthetic method
   public static final void access$setTicketId$p(TicketDetailViewModel var0, String var1) {
      var0.ticketId = var1;
   }

   // $VF: synthetic method
   public static final UserIdentity access$getUser$p(TicketDetailViewModel var0) {
      return var0.user;
   }

   // $VF: synthetic method
   public static final List access$getActiveAdminsAvatars(TicketDetailViewModel var0) {
      return var0.getActiveAdminsAvatars();
   }

   // $VF: synthetic method
   public static final TicketLaunchedFrom access$getLaunchedFrom$p(TicketDetailViewModel var0) {
      return var0.launchedFrom;
   }

   // $VF: synthetic method
   public static final String access$getTicketId$p(TicketDetailViewModel var0) {
      return var0.ticketId;
   }
}
