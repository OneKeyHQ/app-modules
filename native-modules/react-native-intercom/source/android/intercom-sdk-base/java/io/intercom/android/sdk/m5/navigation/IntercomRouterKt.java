package io.intercom.android.sdk.m5.navigation;

import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aP\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00042\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0000\u001a,\u0010\r\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0000\u001a*\u0010\u000e\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0000\u001a0\u0010\u000e\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0011\u001a\u00020\u00042\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0000\u001a&\u0010\u0012\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0011\u001a\u00020\u0004H\u0000\u001a \u0010\u0015\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0000\u001a \u0010\u0016\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0000\u001a \u0010\u0017\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0000¨\u0006\u0018",
   d2 = {
         "openConversation",
         "",
         "Landroidx/navigation/NavHostController;",
         "conversationId",
         "",
         "initialMessage",
         "isLaunchedProgrammatically",
         "",
         "articleId",
         "navOptions",
         "Landroidx/navigation/NavOptions;",
         "transitionArgs",
         "Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "openNewConversation",
         "openTicketDetailScreen",
         "showSubmissionCard",
         "ticketId",
         "from",
         "openCreateTicketsScreen",
         "ticketTypeData",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "openMessages",
         "openHelpCenter",
         "openTicketList",
         "intercom-sdk-base_release"
   }
)
public final class IntercomRouterKt {
   public static final void openConversation(
      @NotNull NavHostController var0,
      @Nullable String var1,
      @Nullable String var2,
      boolean var3,
      @Nullable String var4,
      @Nullable NavOptions var5,
      @NotNull TransitionArgs var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var6, "transitionArgs");
      NavController var10000 = var0;
      IntercomRootActivityArgs$ConversationScreenArgs var10001;
      String var10004 = var2;
      if (var2 == null) {
         var10004 = "";
      }

      var10001 = new IntercomRootActivityArgs$ConversationScreenArgs(var1, var10004, var3, var4, null, var6, 16, null);
      NavController.navigate$default(var10000, var10001.getRoute(), var5, null, 4, null);
   }

   // $VF: synthetic method
   public static void openConversation$default(
      NavHostController var0, String var1, String var2, boolean var3, String var4, NavOptions var5, TransitionArgs var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = null;
      }

      if ((var7 & 2) != 0) {
         var2 = "";
      }

      if ((var7 & 4) != 0) {
         var3 = false;
      }

      if ((var7 & 8) != 0) {
         var4 = null;
      }

      if ((var7 & 16) != 0) {
         var5 = null;
      }

      if ((var7 & 32) != 0) {
         var6 = new TransitionArgs(null, null, null, null, 15, null);
      }

      openConversation(var0, var1, var2, var3, var4, var5, var6);
   }

   public static final void openNewConversation(@NotNull NavHostController var0, boolean var1, @Nullable NavOptions var2, @NotNull TransitionArgs var3) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var3, "transitionArgs");
      openConversation$default(var0, null, null, var1, null, var2, var3, 11, null);
   }

   // $VF: synthetic method
   public static void openNewConversation$default(NavHostController var0, boolean var1, NavOptions var2, TransitionArgs var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = false;
      }

      if ((var4 & 2) != 0) {
         var2 = null;
      }

      if ((var4 & 4) != 0) {
         var3 = new TransitionArgs(null, null, null, null, 15, null);
      }

      openNewConversation(var0, var1, var2, var3);
   }

   public static final void openTicketDetailScreen(@NotNull NavHostController var0, boolean var1, @NotNull TransitionArgs var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var2, "transitionArgs");
      NavController.navigate$default(
         var0, "TICKET_DETAIL?show_submission_card=" + var1 + "&transitionArgs=" + var2 + "&isLaunchedProgrammatically=" + var3, null, null, 6, null
      );
   }

   // $VF: synthetic method
   public static void openTicketDetailScreen$default(NavHostController var0, boolean var1, TransitionArgs var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = false;
      }

      if ((var4 & 2) != 0) {
         var2 = new TransitionArgs(null, null, null, null, 15, null);
      }

      if ((var4 & 4) != 0) {
         var3 = false;
      }

      openTicketDetailScreen(var0, var1, var2, var3);
   }

   public static final void openTicketDetailScreen(
      @NotNull NavHostController var0, @NotNull String var1, @NotNull String var2, @NotNull TransitionArgs var3, boolean var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "ticketId");
      Intrinsics.checkNotNullParameter(var2, "from");
      Intrinsics.checkNotNullParameter(var3, "transitionArgs");
      NavController.navigate$default(
         var0, "TICKET_DETAIL/" + var1 + "?from=" + var2 + "&transitionArgs=" + var3 + "&isLaunchedProgrammatically=" + var4, null, null, 6, null
      );
   }

   // $VF: synthetic method
   public static void openTicketDetailScreen$default(NavHostController var0, String var1, String var2, TransitionArgs var3, boolean var4, int var5, Object var6) {
      if ((var5 & 4) != 0) {
         var3 = new TransitionArgs(null, null, null, null, 15, null);
      }

      if ((var5 & 8) != 0) {
         var4 = false;
      }

      openTicketDetailScreen(var0, var1, var2, var3, var4);
   }

   public static final void openCreateTicketsScreen(@NotNull NavHostController var0, @NotNull TicketType var1, @Nullable String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "ticketTypeData");
      Intrinsics.checkNotNullParameter(var3, "from");
      NavController.navigate$default(var0, "CREATE_TICKET/" + var1.getId() + "?conversation_id=" + var2 + "?from=" + var3, null, null, 6, null);
   }

   public static final void openMessages(@NotNull NavHostController var0, @NotNull TransitionArgs var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "transitionArgs");
      NavController.navigate$default(var0, "MESSAGES?transitionArgs=" + var1 + "&isLaunchedProgrammatically=" + var2, null, null, 6, null);
   }

   // $VF: synthetic method
   public static void openMessages$default(NavHostController var0, TransitionArgs var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = new TransitionArgs(null, null, null, null, 15, null);
      }

      if ((var3 & 2) != 0) {
         var2 = false;
      }

      openMessages(var0, var1, var2);
   }

   public static final void openHelpCenter(@NotNull NavHostController var0, @NotNull TransitionArgs var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "transitionArgs");
      NavController.navigate$default(var0, "HELP_CENTER?transitionArgs=" + var1 + "&isLaunchedProgrammatically=" + var2, null, null, 6, null);
   }

   // $VF: synthetic method
   public static void openHelpCenter$default(NavHostController var0, TransitionArgs var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = new TransitionArgs(null, null, null, null, 15, null);
      }

      if ((var3 & 2) != 0) {
         var2 = false;
      }

      openHelpCenter(var0, var1, var2);
   }

   public static final void openTicketList(@NotNull NavHostController var0, @NotNull TransitionArgs var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "transitionArgs");
      NavController.navigate$default(var0, "TICKETS?transitionArgs=" + var1 + "&isLaunchedProgrammatically=" + var2, null, null, 6, null);
   }

   // $VF: synthetic method
   public static void openTicketList$default(NavHostController var0, TransitionArgs var1, boolean var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = new TransitionArgs(null, null, null, null, 15, null);
      }

      if ((var3 & 2) != 0) {
         var2 = false;
      }

      openTicketList(var0, var1, var2);
   }
}
