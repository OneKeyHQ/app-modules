package io.intercom.android.sdk.m5.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.UnreadConversationCountListener;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\n2\b\b\u0002\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001d\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u000b0\t¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/data/UnreadCountTracker;",
         "",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "getDataLayer",
         "()Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "listeners",
         "Ljava/util/concurrent/ConcurrentHashMap;",
         "Lio/intercom/android/sdk/UnreadConversationCountListener;",
         "Lkotlinx/coroutines/Job;",
         "getListeners",
         "()Ljava/util/concurrent/ConcurrentHashMap;",
         "addListener",
         "",
         "listener",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "removeListener",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UnreadCountTracker {
   @NotNull
   private final IntercomDataLayer dataLayer;
   @NotNull
   private final ConcurrentHashMap listeners;
   public static final int $stable;

   static { $stable = 8; }

   public UnreadCountTracker(@NotNull IntercomDataLayer var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "dataLayer");
      this.dataLayer = var1;
      this.listeners = new ConcurrentHashMap();
   }

   @NotNull
   public final IntercomDataLayer getDataLayer() {
      return this.dataLayer;
   }

   @NotNull
   public final ConcurrentHashMap getListeners() {
      return this.listeners;
   }

   public final void addListener(@NotNull UnreadConversationCountListener var1, @NotNull CoroutineDispatcher var2) {
      Intrinsics.checkNotNullParameter(var1, "listener");
      Intrinsics.checkNotNullParameter(var2, "dispatcher");
      CoroutineScope var3 = CoroutineScopeKt.CoroutineScope(var2);
      Job var4 = BuildersKt.launch$default(var3, null, null, new UnreadCountTracker$addListener$job$1(this, var1, null), 3, null);
      this.listeners.put(var1, var4);
   }

   // $VF: synthetic method
   public static void addListener$default(UnreadCountTracker var0, UnreadConversationCountListener var1, CoroutineDispatcher var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = Dispatchers.getMain();
      }

      var0.addListener(var1, var2);
   }

   public final void removeListener(@NotNull UnreadConversationCountListener var1) {
      Intrinsics.checkNotNullParameter(var1, "listener");
      if (this.listeners.containsKey(var1)) {
         Job var10000 = (Job)this.listeners.remove(var1);
         if (var10000 != null) {
            DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
         }
      }
   }
}
