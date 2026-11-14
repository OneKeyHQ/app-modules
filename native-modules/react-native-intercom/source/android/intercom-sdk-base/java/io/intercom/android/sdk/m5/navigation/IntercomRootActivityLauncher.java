package io.intercom.android.sdk.m5.navigation;

import android.content.Context;
import android.content.Intent;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0005\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u001e\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fJ&\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\f0\u00102\u0006\u0010\u0011\u001a\u00020\fJ\u001e\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\f¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityLauncher;",
         "",
         "<init>",
         "()V",
         "startHome",
         "",
         "context",
         "Landroid/content/Context;",
         "startMessages",
         "startTickets",
         "startTicketDetails",
         "ticketId",
         "",
         "from",
         "startHelpCenterCollections",
         "collectionIds",
         "",
         "metricPlace",
         "startHelpCenterCollection",
         "collectionId",
         "place",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomRootActivityLauncher {
   @NotNull
   public static final IntercomRootActivityLauncher INSTANCE = new IntercomRootActivityLauncher();
   public static int $stable;

   static {}

   private IntercomRootActivityLauncher() {
   }

   public final void startHome(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intent var2 = IntercomRootActivityArgsKt.getIntentForArgs$default(var1, IntercomRootActivityArgs$HomeScreenArgs.INSTANCE, null, null, 12, null);
      var1.startActivity(var2);
   }

   public final void startMessages(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intent var2 = IntercomRootActivityArgsKt.getIntentForArgs$default(var1, IntercomRootActivityArgs$MessagesScreenArgs.INSTANCE, null, null, 12, null);
      var1.startActivity(var2);
   }

   public final void startTickets(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intent var2 = IntercomRootActivityArgsKt.getIntentForArgs$default(var1, IntercomRootActivityArgs$TicketsScreenArgs.INSTANCE, null, null, 12, null);
      var1.startActivity(var2);
   }

   public final void startTicketDetails(@NotNull Context var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "ticketId");
      Intrinsics.checkNotNullParameter(var3, "from");
      Intent var4 = IntercomRootActivityArgsKt.getIntentForArgs$default(
         var1, new IntercomRootActivityArgs$TicketDetailsScreenArgs(var2, var3), null, null, 12, null
      );
      var1.startActivity(var4);
   }

   public final void startHelpCenterCollections(@NotNull Context var1, @NotNull List var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "collectionIds");
      Intrinsics.checkNotNullParameter(var3, "metricPlace");
      IntercomRootActivityArgs$HelpCenterCollectionsArgs var4 = new IntercomRootActivityArgs$HelpCenterCollectionsArgs(var2, var3);
      Intent var5 = IntercomRootActivityArgsKt.getIntentForArgs$default(var1, var4, null, null, 12, null);
      var1.startActivity(var5);
   }

   // $VF: synthetic method
   public static void startHelpCenterCollections$default(IntercomRootActivityLauncher var0, Context var1, List var2, String var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = CollectionsKt.emptyList();
      }

      var0.startHelpCenterCollections(var1, var2, var3);
   }

   public final void startHelpCenterCollection(@NotNull Context var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "collectionId");
      Intrinsics.checkNotNullParameter(var3, "place");
      IntercomRootActivityArgs$HelpCenterCollectionArgs var4 = new IntercomRootActivityArgs$HelpCenterCollectionArgs(var2, var3);
      Intent var5 = IntercomRootActivityArgsKt.getIntentForArgs$default(var1, var4, null, null, 12, null);
      var1.startActivity(var5);
   }
}
