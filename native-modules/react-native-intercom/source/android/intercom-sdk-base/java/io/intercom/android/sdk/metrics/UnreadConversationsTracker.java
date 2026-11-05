package io.intercom.android.sdk.metrics;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.DeliveryOption;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.UsersResponse;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010#\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u0018\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\tH\u0002J\u0006\u0010\u0015\u001a\u00020\u000eR\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u00048BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/metrics/UnreadConversationsTracker;",
         "",
         "trackerProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "<init>",
         "(Lio/intercom/android/sdk/Provider;)V",
         "trackedPartIds",
         "",
         "",
         "tracker",
         "getTracker",
         "()Lio/intercom/android/sdk/metrics/MetricTracker;",
         "track",
         "",
         "response",
         "Lio/intercom/android/sdk/models/UsersResponse;",
         "trackReceivedPart",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "conversationId",
         "clear",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nUnreadConversationsTracker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UnreadConversationsTracker.kt\nio/intercom/android/sdk/metrics/UnreadConversationsTracker\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,48:1\n1869#2,2:49\n*S KotlinDebug\n*F\n+ 1 UnreadConversationsTracker.kt\nio/intercom/android/sdk/metrics/UnreadConversationsTracker\n*L\n20#1:49,2\n*E\n"
)
public final class UnreadConversationsTracker {
   @NotNull
   private final Provider trackerProvider;
   @NotNull
   private final Set trackedPartIds;
   public static final int $stable;

   static { $stable = 8; }

   public UnreadConversationsTracker(@NotNull Provider var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "trackerProvider");
      this.trackerProvider = var1;
      this.trackedPartIds = new LinkedHashSet();
   }

   private final MetricTracker getTracker() {
      Object var10000 = this.trackerProvider.get();
      Intrinsics.checkNotNullExpressionValue(var10000, "get(...)");
      return (MetricTracker)var10000;
   }

   public final void track(@NotNull UsersResponse var1) {
      Intrinsics.checkNotNullParameter(var1, "response");
      List var2 = var1.getUnreadConversations().getConversations();
      Intrinsics.checkNotNull(var2);
      Iterable var3 = var2;
      int var4 = 0;

      for (Object var6 : var3) {
         Conversation var7 = (Conversation)var6;
         int var8 = 0;
         Part var9 = var7.lastPart();
         if (!this.trackedPartIds.contains(var9.getId())) {
            Set var10000 = this.trackedPartIds;
            String var10001 = var9.getId();
            Intrinsics.checkNotNullExpressionValue(var10001, "getId(...)");
            var10000.add(var10001);
            this.trackReceivedPart(var9, var7.getId());
         }
      }
   }

   private final void trackReceivedPart(Part var1, String var2) {
      boolean var3 = var1.hasAttachments();
      boolean var4 = PartExtensionsKt.isLinkCard(var1);
      String var5 = var1.getId();
      MessageStyle var6 = var1.getMessageStyle();
      if (DeliveryOption.SUMMARY != var1.getDeliveryOption()) {
         this.getTracker().receivedMessageFromFullWhenClosed(var3, var4, var2, var5, var6);
      } else if (var1.isInitialMessage()) {
         this.getTracker().receivedMessageFromSnippetWhenClosed(var3, var4, var2, var5, var6);
      } else {
         this.getTracker().receivedReplyFromSnippetWhenClosed(var3, var4, var2, var5);
      }
   }

   public final void clear() {
      this.trackedPartIds.clear();
   }
}
