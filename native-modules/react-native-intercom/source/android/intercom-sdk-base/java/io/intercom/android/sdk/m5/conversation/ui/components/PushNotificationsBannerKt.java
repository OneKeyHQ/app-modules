package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.EasingFunctionsKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\u001a)\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0001¢\u0006\u0002\u0010\u0005\u001a\r\u0010\u0006\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0007¨\u0006\b²\u0006\n\u0010\t\u001a\u00020\nX\u008a\u008e\u0002",
   d2 = {
         "PushNotificationsBanner",
         "",
         "onEnable",
         "Lkotlin/Function0;",
         "onClose",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "ContextualPushNotificationsReminderPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "isVisible",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nPushNotificationsBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/PushNotificationsBannerKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,108:1\n1047#2,6:109\n1047#2,6:115\n85#3:121\n117#3,2:122\n*S KotlinDebug\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/PushNotificationsBannerKt\n*L\n39#1:109,6\n42#1:115,6\n39#1:121\n39#1:122,2\n*E\n"
)
public final class PushNotificationsBannerKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PushNotificationsBanner(@NotNull Function0 var0, @NotNull Function0 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "onEnable");
      Intrinsics.checkNotNullParameter(var1, "onClose");
      var2 = var2.startRestartGroup(-2004591797);
      ComposerKt.sourceInformation(
         var2, "C(PushNotificationsBanner)P(1)38@1529L34,41@1656L32,41@1635L53,53@1906L1679,45@1694L1891:PushNotificationsBanner.kt#o19g89"
      );
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2004591797, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.PushNotificationsBanner (PushNotificationsBanner.kt:37)"
            );
         }

         var2.startReplaceGroup(-633266491);
         ComposerKt.sourceInformation(var2, "CC(remember):PushNotificationsBanner.kt#9igjgp");
         Composer var7 = var2;
         boolean var8 = false;
         int var9 = 0;
         Object var10 = var7.rememberedValue();
         int var11 = 0;
         Object var10000;
         if (var10 == Composer.Companion.getEmpty()) {
            int var12 = 0;
            Object var22 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var7.updateRememberedValue(var22);
            var10000 = var22;
         } else {
            var10000 = var10;
         }

         MutableState var6 = (MutableState)var10000;
         var2.endReplaceGroup();
         MutableState var5 = var6;
         Unit var24 = Unit.INSTANCE;
         var2.startReplaceGroup(-633262429);
         ComposerKt.sourceInformation(var2, "CC(remember):PushNotificationsBanner.kt#9igjgp");
         var7 = var2;
         var8 = false;
         var9 = 0;
         var10 = var7.rememberedValue();
         var11 = 0;
         Object var26;
         if (var10 == Composer.Companion.getEmpty()) {
            Unit var14 = var24;
            int var23 = 0;
            var26 = new PushNotificationsBannerKt$PushNotificationsBanner$1$1(var5, null);
            var24 = var14;
            Object var13 = var26;
            var7.updateRememberedValue(var13);
            var26 = (Function2)var13;
         } else {
            var26 = (Function2)var10;
         }

         Function2 var16 = (kotlin.jvm.functions.Function2) var26;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var24, var16, var2, 6);
         AnimatedVisibilityKt.AnimatedVisibility(
            PushNotificationsBanner$lambda$1(var5),
            null,
            EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(300, 0, EasingFunctionsKt.getEaseOut(), 2, null), 0.0F, 2, null),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1923339635, true, new PushNotificationsBannerKt$PushNotificationsBanner$2(var0, var1), var2, 54),
            var2,
            196608,
            26
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var25 = var2.endRestartGroup();
      if (var25 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_463_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_463_1 = var1;
         final int recoveredCapture_463_2 = var3;
         var25.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_463_0, recoveredArgument_463_1) -> PushNotificationsBannerKt.PushNotificationsBanner$lambda$4(recoveredCapture_463_0, recoveredCapture_463_1, recoveredCapture_463_2, (androidx.compose.runtime.Composer)recoveredArgument_463_0, ((java.lang.Integer)recoveredArgument_463_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ContextualPushNotificationsReminderPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-119534126);
      ComposerKt.sourceInformation(var0, "C(ContextualPushNotificationsReminderPreview)100@3683L117:PushNotificationsBanner.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -119534126,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ContextualPushNotificationsReminderPreview (PushNotificationsBanner.kt:99)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PushNotificationsBannerKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_462_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_462_0, recoveredArgument_462_1) -> PushNotificationsBannerKt.ContextualPushNotificationsReminderPreview$lambda$5(recoveredCapture_462_0, (androidx.compose.runtime.Composer)recoveredArgument_462_0, ((java.lang.Integer)recoveredArgument_462_1).intValue())));
      }
   }

   private static final boolean PushNotificationsBanner$lambda$1(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void PushNotificationsBanner$lambda$2(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit PushNotificationsBanner$lambda$4(Function0 var0, Function0 var1, int var2, Composer var3, int var4) {
      PushNotificationsBanner(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ContextualPushNotificationsReminderPreview$lambda$5(int var0, Composer var1, int var2) {
      ContextualPushNotificationsReminderPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$PushNotificationsBanner$lambda$2(MutableState var0, boolean var1) {
      PushNotificationsBanner$lambda$2(var0, var1);
   }
}
