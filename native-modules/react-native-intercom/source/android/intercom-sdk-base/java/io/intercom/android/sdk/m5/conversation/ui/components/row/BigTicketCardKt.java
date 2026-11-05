package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.EnterTransition;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.FiniteAnimationSpec;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a5\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\u000e",
   d2 = {
         "BigTicketCard",
         "",
         "ticketDetailState",
         "Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;",
         "onClick",
         "Lkotlin/Function0;",
         "visible",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/tickets/TicketDetailState$TicketDetailContentState;Lkotlin/jvm/functions/Function0;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "BigTicketCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "BigTicketCardWaitingPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBigTicketCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,198:1\n75#2:199\n1047#3,6:200\n1047#3,6:206\n*S KotlinDebug\n*F\n+ 1 BigTicketCard.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/BigTicketCardKt\n*L\n50#1:199\n62#1:200,6\n71#1:206,6\n*E\n"
)
public final class BigTicketCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BigTicketCard(
      @NotNull io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var0, @NotNull Function0 var1, boolean var2, @Nullable Modifier var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "ticketDetailState");
      Intrinsics.checkNotNullParameter(var1, "onClick");
      Composer var22 = var4.startRestartGroup(1861461937);
      ComposerKt.sourceInformation(var22, "C(BigTicketCard)P(2,1,3)49@2254L7,61@2700L7,70@3012L7,80@3340L3629,50@2266L4703:BigTicketCard.kt#hr3bi5");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var22.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var22.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var22.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var22.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var22.getSkipping()) {
         var22.skipToGroupEnd();
      } else {
         if ((var6 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1861461937, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCard (BigTicketCard.kt:47)");
         }

         int var8 = 1000;
         CompositionLocal var10 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var13 = var22.consume(var10);
         ComposerKt.sourceInformationMarkerEnd(var22);
         Context var9 = (Context)var13;
         EnterTransition var10000 = EnterExitTransitionKt.expandIn$default(
            AnimationSpecKt.tween$default(var8, 0, null, 6, null), Alignment.Companion.getTopCenter(), false, null, 12, null
         );
         FiniteAnimationSpec var10001 = AnimationSpecKt.tween$default(var8, var8 / 2, null, 4, null);
         var22.startReplaceGroup(-90870824);
         ComposerKt.sourceInformation(var22, "CC(remember):BigTicketCard.kt#9igjgp");
         Composer var26 = var22;
         boolean var29 = false;
         int var14 = 0;
         Object var15 = var26.rememberedValue();
         int var16 = 0;
         Object var10002;
         if (var15 == Composer.Companion.getEmpty()) {
            FiniteAnimationSpec var20 = var10001;
            EnterTransition var19 = var10000;
            int var17 = 0;
            Function1 var21 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_649_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt.BigTicketCard$lambda$1$lambda$0(((java.lang.Integer)recoveredExpressionArgument_649_0).intValue()));
            var10000 = var19;
            var10001 = var20;
            Object var34 = var21;
            var26.updateRememberedValue(var34);
            var10002 = var34;
         } else {
            var10002 = var15;
         }

         Function1 var24 = (Function1)var10002;
         var22.endReplaceGroup();
         EnterTransition var23 = var10000.plus(EnterExitTransitionKt.slideInVertically(var10001, var24))
            .plus(EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(var8, var8 / 2, null, 4, null), 0.0F, 2, null));
         FiniteAnimationSpec var38 = AnimationSpecKt.tween$default(var8, 0, null, 6, null);
         var22.startReplaceGroup(-90860840);
         ComposerKt.sourceInformation(var22, "CC(remember):BigTicketCard.kt#9igjgp");
         Composer var30 = var22;
         boolean var31 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var30.rememberedValue();
         int var35/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var42;
         if (it$ivx == Composer.Companion.getEmpty()) {
            FiniteAnimationSpec var37 = var38;
            int var18 = 0;
            Function1 var41 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_650_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt.BigTicketCard$lambda$3$lambda$2(((java.lang.Integer)recoveredExpressionArgument_650_0).intValue()));
            var38 = var37;
            Object var36 = var41;
            var30.updateRememberedValue(var36);
            var42 = var36;
         } else {
            var42 = it$ivx;
         }

         Function1 var27 = (Function1)var42;
         var22.endReplaceGroup();
         ExitTransition var39 = EnterExitTransitionKt.slideOutVertically(var38, var27)
            .plus(EnterExitTransitionKt.fadeOut$default(AnimationSpecKt.tween$default(var8, 0, null, 6, null), 0.0F, 2, null));
         int var28 = var8 / 2;
         ExitTransition var25 = var39.plus(
            EnterExitTransitionKt.shrinkOut$default(
               AnimationSpecKt.tween$default(var8, var28, null, 4, null), Alignment.Companion.getTopCenter(), false, null, 12, null
            )
         );
         AnimatedVisibilityKt.AnimatedVisibility(
            var2,
            var3,
            var23,
            var25,
            null,
            ComposableLambdaKt.rememberComposableLambda(-915811879, true, new BigTicketCardKt$BigTicketCard$3(var1, var0, var9), var22, 54),
            var22,
            196992 | 14 & var7 >> 6 | 112 & var7 >> 6,
            16
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var40 = var22.endRestartGroup();
      if (var40 != null) {
         final io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState recoveredExpressionCapture_651_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_651_1 = var1;
         final boolean recoveredExpressionCapture_651_2 = var2;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_651_3 = var3;
         final int recoveredExpressionCapture_651_4 = var5;
         final int recoveredExpressionCapture_651_5 = var6;
         var40.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_651_0, recoveredExpressionArgument_651_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt.BigTicketCard$lambda$4(recoveredExpressionCapture_651_0, recoveredExpressionCapture_651_1, recoveredExpressionCapture_651_2, recoveredExpressionCapture_651_3, recoveredExpressionCapture_651_4, recoveredExpressionCapture_651_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_651_0), ((java.lang.Integer)recoveredExpressionArgument_651_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BigTicketCardPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1841168271);
      ComposerKt.sourceInformation(var2, "C(BigTicketCardPreview)157@7045L165:BigTicketCard.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1841168271, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardPreview (BigTicketCard.kt:156)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BigTicketCardKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_652_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_652_0, recoveredExpressionArgument_652_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt.BigTicketCardPreview$lambda$5(recoveredExpressionCapture_652_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_652_0), ((java.lang.Integer)recoveredExpressionArgument_652_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BigTicketCardWaitingPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1532589538);
      ComposerKt.sourceInformation(var2, "C(BigTicketCardWaitingPreview)169@7293L1084:BigTicketCard.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1532589538, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardWaitingPreview (BigTicketCard.kt:168)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$BigTicketCardKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_653_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_653_0, recoveredExpressionArgument_653_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.BigTicketCardKt.BigTicketCardWaitingPreview$lambda$6(recoveredExpressionCapture_653_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_653_0), ((java.lang.Integer)recoveredExpressionArgument_653_1).intValue())));
      }
   }

   private static final int BigTicketCard$lambda$1$lambda$0(int var0) {
      return -var0;
   }

   private static final int BigTicketCard$lambda$3$lambda$2(int var0) {
      return -var0;
   }

   private static final Unit BigTicketCard$lambda$4(
      io.intercom.android.sdk.tickets.TicketDetailState$TicketDetailContentState var0, Function0 var1, boolean var2, Modifier var3, int var4, int var5, Composer var6, int var7
   ) {
      BigTicketCard(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit BigTicketCardPreview$lambda$5(int var0, Composer var1, int var2) {
      BigTicketCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BigTicketCardWaitingPreview$lambda$6(int var0, Composer var1, int var2) {
      BigTicketCardWaitingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
