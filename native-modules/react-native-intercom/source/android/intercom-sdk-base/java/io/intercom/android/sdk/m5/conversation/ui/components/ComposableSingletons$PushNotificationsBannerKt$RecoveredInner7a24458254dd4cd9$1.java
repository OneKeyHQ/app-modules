package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPushNotificationsBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$PushNotificationsBannerKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,108:1\n1047#2,6:109\n1047#2,6:115\n*S KotlinDebug\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$PushNotificationsBannerKt$lambda-2$1\n*L\n103#1:109,6\n104#1:115,6\n*E\n"
)
final class ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C102@3755L2,103@3781L2,101@3707L87:PushNotificationsBanner.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -353143684,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$PushNotificationsBannerKt.lambda-2.<anonymous> (PushNotificationsBanner.kt:101)"
            );
         }

         var1.startReplaceGroup(1900618384);
         ComposerKt.sourceInformation(var1, "CC(remember):PushNotificationsBanner.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10000;
         if (var7 == Composer.Companion.getEmpty()) {
            int var9 = 0;
            Object var17 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0());
            var4.updateRememberedValue(var17);
            var10000 = (Function0)var17;
         } else {
            var10000 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10000;
         var1.endReplaceGroup();
         var10000 = var3;
         var1.startReplaceGroup(1900619216);
         ComposerKt.sourceInformation(var1, "CC(remember):PushNotificationsBanner.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         kotlin.jvm.functions.Function0 var21;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var10 = var3;
            int var18 = 0;
            var21 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PushNotificationsBannerKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$3$lambda$2());
            var10000 = var10;
            Object var19 = var21;
            var4.updateRememberedValue(var19);
            var21 = (Function0)var19;
         } else {
            var21 = (Function0)var7;
         }

         var3 = var21;
         var1.endReplaceGroup();
         PushNotificationsBannerKt.PushNotificationsBanner((kotlin.jvm.functions.Function0) var10000, var3, var1, 54);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
