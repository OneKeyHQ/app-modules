package io.intercom.android.sdk;

import androidx.annotation.VisibleForTesting;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.nexus.NexusConfig;
import io.intercom.android.nexus.NexusEvent;
import io.intercom.android.nexus.NexusEventType;
import io.intercom.android.nexus.NexusListener;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0001\u0018\u0000 *2\u00020\u00012\u00020\u0002:\u0001*B/\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0018\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0007J\b\u0010\u001c\u001a\u00020\u0016H\u0016J\b\u0010\u001d\u001a\u00020\u0016H\u0007J\u000e\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001f\u001a\u00020\nJ\u000f\u0010 \u001a\u0004\u0018\u00010\u001aH\u0002¢\u0006\u0002\u0010!J\u0010\u0010\"\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010&\u001a\u00020\u0016H\u0002J\b\u0010'\u001a\u00020\u0016H\u0016J\b\u0010(\u001a\u00020\u0016H\u0016J\b\u0010)\u001a\u00020\u0016H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u0010X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0012R\u0014\u0010\u0013\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/NexusWrapper;",
         "Lio/intercom/android/nexus/NexusClient;",
         "Lio/intercom/android/nexus/NexusListener;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "okHttpClient",
         "Lokhttp3/OkHttpClient;",
         "api",
         "Lio/intercom/android/sdk/api/Api;",
         "debouncePeriodMs",
         "",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lcom/intercom/twig/Twig;Lokhttp3/OkHttpClient;Lio/intercom/android/sdk/api/Api;JLio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "executor",
         "Ljava/util/concurrent/ScheduledExecutorService;",
         "kotlin.jvm.PlatformType",
         "Ljava/util/concurrent/ScheduledExecutorService;",
         "actionFuture",
         "Ljava/util/concurrent/ScheduledFuture;",
         "connect",
         "",
         "config",
         "Lio/intercom/android/nexus/NexusConfig;",
         "shouldSendPresence",
         "",
         "connectNow",
         "disconnect",
         "disconnectNow",
         "scheduleDisconnect",
         "delayMs",
         "removeCallbacks",
         "()Ljava/lang/Boolean;",
         "notifyEvent",
         "event",
         "Lio/intercom/android/nexus/NexusEvent;",
         "logKnownEvent",
         "fetchUnreadConversations",
         "onConnect",
         "onConnectFailed",
         "onShutdown",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class NexusWrapper extends NexusClient implements NexusListener {
   @NotNull
   public static final NexusWrapper$Companion Companion = new NexusWrapper$Companion(null);
   @NotNull
   private final Twig twig;
   @NotNull
   private final Api api;
   private final long debouncePeriodMs;
   @NotNull
   private final IntercomDataLayer dataLayer;
   private final ScheduledExecutorService executor;
   @Nullable
   private ScheduledFuture actionFuture;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String CONVERSATION_ID = "conversationId";

   public NexusWrapper(@NotNull Twig var1, @NotNull OkHttpClient var2, @NotNull Api var3, long var4, @NotNull IntercomDataLayer var6) {
      Intrinsics.checkNotNullParameter(var1, "twig");
      Intrinsics.checkNotNullParameter(var2, "okHttpClient");
      Intrinsics.checkNotNullParameter(var3, "api");
      Intrinsics.checkNotNullParameter(var6, "dataLayer");
      super(var1, var2);
      this.twig = var1;
      this.api = var3;
      this.debouncePeriodMs = var4;
      this.dataLayer = var6;
      this.executor = Executors.newSingleThreadScheduledExecutor();
   }

   @Override
   public void connect(@NotNull NexusConfig var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "config");
      if (var1.getEndpoints().isEmpty()) {
         this.twig.w("No realtime endpoints present so we can't connect");
      } else {
         this.removeCallbacks();
         final io.intercom.android.sdk.NexusWrapper recoveredExpressionCapture_31_0 = this;
         final io.intercom.android.nexus.NexusConfig recoveredExpressionCapture_31_1 = var1;
         final boolean recoveredExpressionCapture_31_2 = var2;
         this.actionFuture = this.executor.schedule(((java.lang.Runnable) () -> io.intercom.android.sdk.NexusWrapper.connect$lambda$0(recoveredExpressionCapture_31_0, recoveredExpressionCapture_31_1, recoveredExpressionCapture_31_2)), this.debouncePeriodMs, TimeUnit.MILLISECONDS);
      }
   }

   @VisibleForTesting
   public final void connectNow(@NotNull NexusConfig var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "config");
      if (!this.isConnected()) {
         super.connect(var1, var2);
         this.setTopics(CollectionsKt.listOf("*"));
         this.addEventListener(this);
      }
   }

   @Override
   public void disconnect() {
      this.removeCallbacks();
      final io.intercom.android.sdk.NexusWrapper recoveredExpressionCapture_32_0 = this;
      this.actionFuture = this.executor.schedule(((java.lang.Runnable) () -> io.intercom.android.sdk.NexusWrapper.disconnect$lambda$1(recoveredExpressionCapture_32_0)), this.debouncePeriodMs, TimeUnit.MILLISECONDS);
   }

   @VisibleForTesting
   public final void disconnectNow() {
      this.removeEventListener(this);
      super.disconnect();
   }

   public final void scheduleDisconnect(long var1) {
      this.removeCallbacks();
      final io.intercom.android.sdk.NexusWrapper recoveredExpressionCapture_33_0 = this;
      this.actionFuture = this.executor.schedule(((java.lang.Runnable) () -> io.intercom.android.sdk.NexusWrapper.scheduleDisconnect$lambda$2(recoveredExpressionCapture_33_0)), var1, TimeUnit.MILLISECONDS);
   }

   private final Boolean removeCallbacks() {
      return this.actionFuture != null ? this.actionFuture.cancel(false) : null;
   }

   @Override
   public void notifyEvent(@NotNull NexusEvent var1) {
      Intrinsics.checkNotNullParameter(var1, "event");
      String var2 = var1.getEventData().optString("conversationId");
      NexusEventType var10000 = var1.getEventType();
      switch (var10000 == null ? -1 : NexusWrapper$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
            this.logKnownEvent(var1);
            break;
         case 2:
            this.logKnownEvent(var1);
            this.fetchUnreadConversations();
            break;
         case 3:
            this.logKnownEvent(var1);
            break;
         case 4:
            this.logKnownEvent(var1);
            IntercomDataLayer var9 = this.dataLayer;
            Intrinsics.checkNotNull(var2);
            var9.markConversationAsRead(var2);
            break;
         case 5:
            this.logKnownEvent(var1);
            long var8 = var1.getEventData().optLong("entity_type");
            String var5 = var1.getEventData().optString("entity_id");
            long var6 = var8;
            if (var6 == 44L) {
               this.api.fetchCarouselByEntityId(var5);
            } else if (var6 == 85L) {
               this.api.fetchSurveyByEntityId(var5);
            } else {
               this.twig.internal("Nexus NewContent", "Unexpected entity type: " + var8);
            }
            break;
         case 6:
            this.logKnownEvent(var1);
            String conversationIdx = var1.getEventData().optString("conversationId");
            String var4 = var1.getEventData().optString("title");
            Intrinsics.checkNotNull(conversationIdx);
            if (conversationIdx.length() > 0) {
               Intrinsics.checkNotNull(var4);
               if (var4.length() > 0) {
                  this.dataLayer.updateConversationTitle(conversationIdx, var4);
               }
            }
            break;
         default:
            this.twig.internal("Nexus", "Unexpected event: " + var1.getEventType());
      }
   }

   private final void logKnownEvent(NexusEvent var1) {
      this.twig.internal("Nexus", "Received " + var1.getEventType() + " event");
   }

   private final void fetchUnreadConversations() {
      this.api.getUnreadConversations();
   }

   @Override
   public void onConnect() {
      this.fetchUnreadConversations();
   }

   @Override
   public void onConnectFailed() {
   }

   @Override
   public void onShutdown() {
   }

   private static final void connect$lambda$0(NexusWrapper var0, NexusConfig var1, boolean var2) {
      var0.connectNow(var1, var2);
   }

   private static final void disconnect$lambda$1(NexusWrapper var0) {
      var0.disconnectNow();
   }

   private static final void scheduleDisconnect$lambda$2(NexusWrapper var0) {
      var0.disconnectNow();
   }
}
