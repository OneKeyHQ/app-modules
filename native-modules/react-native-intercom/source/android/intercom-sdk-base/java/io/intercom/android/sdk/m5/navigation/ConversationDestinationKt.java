package io.intercom.android.sdk.m5.navigation;

import android.content.Context;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentTransitionScope;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.compose.LocalLifecycleOwnerKt;
import androidx.navigation.NamedNavArgument;
import androidx.navigation.NamedNavArgumentKt;
import androidx.navigation.NavArgumentBuilder;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.NavType;
import androidx.navigation.compose.NavGraphBuilderKt;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel;
import io.intercom.android.sdk.m5.conversation.ConversationViewModel$Companion;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionStyleKt;
import io.intercom.android.sdk.ui.extension.NavBackStackEntryExtensionKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001c\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0000\u001a=\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000f2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0003¢\u0006\u0002\u0010\u0012¨\u0006\u0013",
   d2 = {
         "conversationDestination",
         "",
         "Landroidx/navigation/NavGraphBuilder;",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "getConversationViewModel",
         "Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "conversationId",
         "",
         "initialMessage",
         "isLaunchedProgrammatically",
         "",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "(Landroidx/lifecycle/ViewModelStoreOwner;Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Landroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationDestination.kt\nio/intercom/android/sdk/m5/navigation/ConversationDestinationKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n*L\n1#1,281:1\n75#2:282\n75#2:283\n1047#3,6:284\n68#4,5:290\n*S KotlinDebug\n*F\n+ 1 ConversationDestination.kt\nio/intercom/android/sdk/m5/navigation/ConversationDestinationKt\n*L\n253#1:282\n254#1:283\n266#1:284,6\n275#1:290,5\n*E\n"
)
public final class ConversationDestinationKt {
   public static final void conversationDestination(@NotNull NavGraphBuilder var0, @NotNull NavHostController var1, @NotNull ComponentActivity var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "rootActivity");
      NamedNavArgument[] var3 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("conversationId", ((kotlin.jvm.functions.Function1) (recoveredArgument_992_0) -> ConversationDestinationKt.conversationDestination$lambda$0((androidx.navigation.NavArgumentBuilder)recoveredArgument_992_0))),
         NamedNavArgumentKt.navArgument("initialMessage", ((kotlin.jvm.functions.Function1) (recoveredArgument_993_0) -> ConversationDestinationKt.conversationDestination$lambda$1((androidx.navigation.NavArgumentBuilder)recoveredArgument_993_0))),
         NamedNavArgumentKt.navArgument("articleId", ((kotlin.jvm.functions.Function1) (recoveredArgument_994_0) -> ConversationDestinationKt.conversationDestination$lambda$2((androidx.navigation.NavArgumentBuilder)recoveredArgument_994_0))),
         NamedNavArgumentKt.navArgument("articleTitle", ((kotlin.jvm.functions.Function1) (recoveredArgument_995_0) -> ConversationDestinationKt.conversationDestination$lambda$3((androidx.navigation.NavArgumentBuilder)recoveredArgument_995_0))),
         NamedNavArgumentKt.navArgument("isLaunchedProgrammatically", ((kotlin.jvm.functions.Function1) (recoveredArgument_996_0) -> ConversationDestinationKt.conversationDestination$lambda$4((androidx.navigation.NavArgumentBuilder)recoveredArgument_996_0))),
         NamedNavArgumentKt.navArgument("transitionArgs", ((kotlin.jvm.functions.Function1) (recoveredArgument_997_0) -> ConversationDestinationKt.conversationDestination$lambda$5((androidx.navigation.NavArgumentBuilder)recoveredArgument_997_0)))
      };
      NavGraphBuilderKt.composable$default(
         var0,
         "CONVERSATION?conversationId={conversationId}&initialMessage={initialMessage}&articleId={articleId}&articleTitle={articleTitle}&isLaunchedProgrammatically={isLaunchedProgrammatically}&transitionArgs={transitionArgs}",
         CollectionsKt.listOf(var3),
         null,
         ((kotlin.jvm.functions.Function1) (recoveredArgument_998_0) -> ConversationDestinationKt.conversationDestination$lambda$6((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_998_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_999_0) -> ConversationDestinationKt.conversationDestination$lambda$7((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_999_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1000_0) -> ConversationDestinationKt.conversationDestination$lambda$8((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1000_0)),
         ((kotlin.jvm.functions.Function1) (recoveredArgument_1001_0) -> ConversationDestinationKt.conversationDestination$lambda$9((androidx.compose.animation.AnimatedContentTransitionScope)recoveredArgument_1001_0)),
         null,
         ComposableLambdaKt.composableLambdaInstance(-1198092933, true, new ConversationDestinationKt$conversationDestination$11(var2, var1)),
         132,
         null
      );
   }

   @Composable
   private static final ConversationViewModel getConversationViewModel(
      ViewModelStoreOwner var0, String var1, String var2, boolean var3, ArticleMetadata var4, Composer var5, int var6, int var7
   ) {
      var5.startReplaceGroup(-1203114984);
      ComposerKt.sourceInformation(
         var5, "C(getConversationViewModel)P(4,1,2,3)252@11108L7,253@11147L7,265@11546L438,265@11513L471:ConversationDestination.kt#m1zs2i"
      );
      if ((var7 & 4) != 0) {
         var2 = "";
      }

      if ((var7 & 16) != 0) {
         var4 = null;
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1203114984, var6, -1, "io.intercom.android.sdk.m5.navigation.getConversationViewModel (ConversationDestination.kt:251)");
      }

      CompositionLocal var9 = LocalLifecycleOwnerKt.getLocalLifecycleOwner();
      int var10 = 0;
      int var11 = 0;
      ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var12 = var5.consume(var9);
      ComposerKt.sourceInformationMarkerEnd(var5);
      LifecycleOwner var8 = (LifecycleOwner)var12;
      CompositionLocal var21 = AndroidCompositionLocals_androidKt.getLocalContext();
      var11 = 0;
      int $i$f$getCurrentx = 0;
      ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var13 = var5.consume(var21);
      ComposerKt.sourceInformationMarkerEnd(var5);
      Context var20 = (Context)var13;
      ConversationViewModel$Companion var24 = ConversationViewModel.Companion;
      LaunchMode var27 = var3 ? LaunchMode.PROGRAMMATIC : LaunchMode.CLASSIC;
      ConversationViewModel var22 = var24.create(var0, var1, var2, var4, var27);
      LifecycleOwner var10000 = var8;
      var5.startReplaceGroup(1938580361);
      ComposerKt.sourceInformation(var5, "CC(remember):ConversationDestination.kt#9igjgp");
      Composer var28 = var5;
      boolean var29 = var5.changedInstance(var22) | var5.changedInstance(var20) | var5.changedInstance(var8);
      int var14 = 0;
      Object var15 = var28.rememberedValue();
      int var16 = 0;
      Object var30;
      if (!var29 && var15 != Composer.Companion.getEmpty()) {
         var30 = (Function1)var15;
      } else {
         LifecycleOwner var19 = var8;
         int var17 = 0;
         final androidx.lifecycle.LifecycleOwner recoveredExpressionCapture_1002_0 = var8;
         final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredExpressionCapture_1002_1 = var22;
         final android.content.Context recoveredExpressionCapture_1002_2 = var20;
         var30 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1002_0) -> io.intercom.android.sdk.m5.navigation.ConversationDestinationKt.getConversationViewModel$lambda$13$lambda$12(recoveredExpressionCapture_1002_0, recoveredExpressionCapture_1002_1, recoveredExpressionCapture_1002_2, (androidx.compose.runtime.DisposableEffectScope) ((androidx.compose.runtime.DisposableEffectScope)recoveredExpressionArgument_1002_0)));
         var10000 = var19;
         Object var18 = var30;
         var28.updateRememberedValue(var18);
         var30 = (Function1)var18;
      }

      Function1 var25 = (kotlin.jvm.functions.Function1) var30;
      var5.endReplaceGroup();
      EffectsKt.DisposableEffect(var10000, var25, var5, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var5.endReplaceGroup();
      return var22;
   }

   private static final Unit conversationDestination$lambda$0(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      return Unit.INSTANCE;
   }

   private static final Unit conversationDestination$lambda$1(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      return Unit.INSTANCE;
   }

   private static final Unit conversationDestination$lambda$2(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      return Unit.INSTANCE;
   }

   private static final Unit conversationDestination$lambda$3(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      var0.setNullable(true);
      return Unit.INSTANCE;
   }

   private static final Unit conversationDestination$lambda$4(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setNullable(false);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final Unit conversationDestination$lambda$5(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(TransitionStyleKt.getTransitionArgNavType());
      var0.setNullable(false);
      var0.setDefaultValue(new TransitionArgs(null, null, null, null, 15, null));
      return Unit.INSTANCE;
   }

   private static final EnterTransition conversationDestination$lambda$6(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getEnter()
         .transition();
   }

   private static final ExitTransition conversationDestination$lambda$7(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getExit()
         .transition();
   }

   private static final EnterTransition conversationDestination$lambda$8(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getTargetState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopEnter()
         .transition();
   }

   private static final ExitTransition conversationDestination$lambda$9(AnimatedContentTransitionScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$composable");
      return ((TransitionArgs)NavBackStackEntryExtensionKt.getParcelableObject(
            (NavBackStackEntry)var0.getInitialState(), "transitionArgs", TransitionArgs.class, new TransitionArgs(null, null, null, null, 15, null)
         ))
         .getPopExit()
         .transition();
   }

   private static final void getConversationViewModel$lambda$13$lambda$12$lambda$10(ConversationViewModel var0, Context var1, LifecycleOwner var2, Event var3) {
      Intrinsics.checkNotNullParameter(var2, "<unused var>");
      Intrinsics.checkNotNullParameter(var3, "event");
      switch (var3) {
         case ON_RESUME:
            var0.onResume(var1);
            break;
         case ON_PAUSE:
            var0.onPause(var1);
      }
   }

   private static final DisposableEffectResult getConversationViewModel$lambda$13$lambda$12(
      LifecycleOwner var0, ConversationViewModel var1, Context var2, DisposableEffectScope var3
   ) {
      Intrinsics.checkNotNullParameter(var3, "$this$DisposableEffect");
      final io.intercom.android.sdk.m5.conversation.ConversationViewModel recoveredCapture_1003_0 = var1;
      final android.content.Context recoveredCapture_1003_1 = var2;
      LifecycleEventObserver var4 = ((androidx.lifecycle.LifecycleEventObserver) (recoveredArgument_1003_0, recoveredArgument_1003_1) -> ConversationDestinationKt.getConversationViewModel$lambda$13$lambda$12$lambda$10(recoveredCapture_1003_0, recoveredCapture_1003_1, (androidx.lifecycle.LifecycleOwner)recoveredArgument_1003_0, (androidx.lifecycle.Lifecycle.Event)recoveredArgument_1003_1));
      var0.getLifecycle().addObserver(var4);
      int var6 = 0;
      return new ConversationDestinationKt$getConversationViewModel$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1(var0, var4);
   }

   // $VF: synthetic method
   public static final ConversationViewModel access$getConversationViewModel(
      ViewModelStoreOwner var0, String var1, String var2, boolean var3, ArticleMetadata var4, Composer var5, int var6, int var7
   ) {
      return getConversationViewModel(var0, var1, var2, var3, var4, var5, var6, var7);
   }
}
