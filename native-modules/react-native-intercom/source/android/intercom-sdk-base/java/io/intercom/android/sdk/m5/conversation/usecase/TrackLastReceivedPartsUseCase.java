package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B!\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u0011\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\nH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;",
         "",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "userIdentity",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lio/intercom/android/sdk/metrics/MetricTracker;Lkotlin/jvm/functions/Function0;)V",
         "currentConversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "invoke",
         "",
         "newConversation",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nTrackLastReceivedPartsUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TrackLastReceivedPartsUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,60:1\n774#2:61\n865#2,2:62\n2756#2:64\n1869#2,2:66\n1#3:65\n*S KotlinDebug\n*F\n+ 1 TrackLastReceivedPartsUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase\n*L\n35#1:61\n35#1:62,2\n38#1:64\n47#1:66,2\n38#1:65\n*E\n"
)
public final class TrackLastReceivedPartsUseCase {
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final Function0 userIdentity;
   @NotNull
   private Conversation currentConversation;
   public static final int $stable;

   static { $stable = 8; }

   public TrackLastReceivedPartsUseCase(@NotNull MetricTracker var1, @NotNull Function0 var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "metricTracker");
      Intrinsics.checkNotNullParameter(var2, "userIdentity");
      this.metricTracker = var1;
      this.userIdentity = var2;
      this.currentConversation = new Conversation(
         null, null, false, null, null, null, null, null, false, false, null, null, false, null, null, null, null, null, null, null, null, 2097151, null
      );
   }

   // $VF: synthetic method
   public TrackLastReceivedPartsUseCase(MetricTracker var1, Function0 var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (MetricTracker) (Injector.get().getMetricTracker()) : var1), (((var3 & 2) != 0) ? (Function0) (((kotlin.jvm.functions.Function0)()->TrackLastReceivedPartsUseCase._init_$lambda$0())) : var2));
   }

   public final synchronized void invoke(@NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "newConversation");
      int var2 = this.currentConversation.parts().size();
      int var3 = var1.parts().size();
      boolean var4 = var2 == 0 && this.currentConversation.getTicket() == null;
      List var5 = var4
         ? (!var1.isRead() && var1.lastPart().isReply() ? CollectionsKt.listOf(var1.lastPart()) : CollectionsKt.emptyList())
         : (var3 > var2 ? var1.parts().subList(var2, var3) : CollectionsKt.emptyList());
      Iterable var6 = var5;
      int var7 = 0;
      Iterable var8 = var6;
      ArrayList var9 = new ArrayList();
      int var10 = 0;

      for (Object var12 : var8) {
         Part var13 = (Part)var12;
         int var14 = 0;
         if (var13.isAdmin()
            || !var13.getParticipant().isUserWithId(((UserIdentity)this.userIdentity.invoke()).getIntercomId()) && !Intrinsics.areEqual(var13, Part.NULL)) {
            var9.add(var12);
         }
      }

      var6 = (Iterable & List)var9;
      var7 = 0;
      var8 = var6;
      Iterable var24 = var8;
      var10 = 0;

      for (Object var27 : var24) {
         Part var28 = (Part)var27;
         int var29 = 0;
         this.metricTracker.receivedReply(var28.hasAttachments(), PartExtensionsKt.isLinkCard(var28), var28.getId(), var1.getId());
         if (Intrinsics.areEqual(var28.getId(), ((Part)CollectionsKt.last(var5)).getId())) {
            List var10000 = var28.getReplyOptions();
            Intrinsics.checkNotNullExpressionValue(var10000, "getReplyOptions(...)");
            Iterable var15 = var10000;
            int var16 = 0;

            for (Object element$ivx : var15) {
               ReplyOption var19 = (ReplyOption)element$ivx;
               int var20 = 0;
               this.metricTracker.receivedQuickReply(var19.getUuid(), var1.getId(), var28.getId());
            }
         }
      }

      this.currentConversation = var1;
   }

   private static final UserIdentity _init_$lambda$0() {
      return Injector.get().getUserIdentity();
   }

   public TrackLastReceivedPartsUseCase() {
      this(null, null, 3, null);
   }
}
