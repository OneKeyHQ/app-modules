package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.navigation.NamedNavArgument;
import androidx.navigation.NamedNavArgumentKt;
import androidx.navigation.NavArgumentBuilder;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.NavType;
import androidx.navigation.compose.NavGraphBuilderKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u001a\u001c\u0010\u0003\u001a\u00020\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000¨\u0006\n²\u0006\n\u0010\u000b\u001a\u00020\fX\u008a\u0084\u0002",
   d2 = {
         "TICKET_TYPE_ID",
         "",
         "CONVERSATION_ID",
         "createTicketDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "intercom-sdk-base_release",
         "uiState",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;"
   }
)
public final class CreateTicketDestinationKt {
   @NotNull
   public static final String TICKET_TYPE_ID = "ticket_type_id";
   @NotNull
   public static final String CONVERSATION_ID = "conversation_id";

   public static final void createTicketDestination(@NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      Function1 var3 = IntercomTransitionsKt.getSlideUpEnterTransition();
      Function1 var4 = IntercomTransitionsKt.getSlideDownExitTransition();
      NamedNavArgument[] var6 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("ticket_type_id", ((kotlin.jvm.functions.Function1) (recoveredArgument_1013_0) -> CreateTicketDestinationKt.createTicketDestination$lambda$0((androidx.navigation.NavArgumentBuilder)recoveredArgument_1013_0))),
         NamedNavArgumentKt.navArgument("conversation_id", ((kotlin.jvm.functions.Function1) (recoveredArgument_1014_0) -> CreateTicketDestinationKt.createTicketDestination$lambda$1((androidx.navigation.NavArgumentBuilder)recoveredArgument_1014_0))),
         NamedNavArgumentKt.navArgument("from", ((kotlin.jvm.functions.Function1) (recoveredArgument_1015_0) -> CreateTicketDestinationKt.createTicketDestination$lambda$2((androidx.navigation.NavArgumentBuilder)recoveredArgument_1015_0)))
      };
      List var5 = CollectionsKt.listOf(var6);
      NavGraphBuilderKt.composable$default(
         var0,
         "CREATE_TICKET/{ticket_type_id}?conversation_id={conversation_id}?from={from}",
         var5,
         null,
         var3,
         var4,
         null,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(-521503931, true, new CreateTicketDestinationKt$createTicketDestination$4(var2, var1)),
         228,
         null
      );
   }

   private static final void createTicketDestination$navigateUp(NavHostController var0, ComponentActivity var1) {
      if (!var0.navigateUp()) {
         var1.finish();
      }
   }

   private static final Unit createTicketDestination$lambda$0(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.LongType);
      return Unit.INSTANCE;
   }

   private static final Unit createTicketDestination$lambda$1(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      return Unit.INSTANCE;
   }

   private static final Unit createTicketDestination$lambda$2(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$createTicketDestination$navigateUp(NavHostController var0, ComponentActivity var1) {
      createTicketDestination$navigateUp(var0, var1);
   }
}
