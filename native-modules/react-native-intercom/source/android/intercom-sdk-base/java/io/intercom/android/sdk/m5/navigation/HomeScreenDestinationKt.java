package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.EnterTransition;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.compose.NavGraphBuilderKt;
import io.intercom.android.sdk.m5.navigation.transitions.EnterTransitionStyle;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a$\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0000¨\u0006\t",
   d2 = {
         "homeScreen",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "intercom-sdk-base_release"
   }
)
public final class HomeScreenDestinationKt {
   public static final void homeScreen(
      @NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2, @NotNull CoroutineScope var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      Intrinsics.checkNotNullParameter(var3, "scope");
      NavGraphBuilderKt.composable$default(
         var0,
         "HOME",
         null,
         null,
         null,
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1031_0) -> HomeScreenDestinationKt.homeScreen$lambda$0((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1031_0)),
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(1180315695, true, new HomeScreenDestinationKt$homeScreen$2(var2, var1, var3)),
         222,
         null
      );
   }

   private static final EnterTransition homeScreen$lambda$0(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return EnterTransitionStyle.PEEK_BEHIND.transition();
   }
}
