package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.inbox.InboxViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nInboxScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxScreen$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,295:1\n1047#2,6:296\n*S KotlinDebug\n*F\n+ 1 InboxScreen.kt\nio/intercom/android/sdk/m5/inbox/ui/InboxScreenKt$InboxScreen$1\n*L\n76#1:296,6\n*E\n"
)
final class InboxScreenKt$InboxScreen$1 implements Function2 {
   // $VF: synthetic field
   final NetworkResponse $currentOpenState;
   // $VF: synthetic field
   final InboxViewModel $viewModel;

   InboxScreenKt$InboxScreen$1(NetworkResponse var1, InboxViewModel var2) {
      this.$currentOpenState = var1;
      this.$viewModel = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C72@3176L336:InboxScreen.kt#po70xe");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-157026591, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxScreen.<anonymous> (InboxScreen.kt:72)");
         }

         Modifier var3 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         var1.startReplaceGroup(1816921353);
         ComposerKt.sourceInformation(var1, "75@3371L22");
         ErrorState var26;
         if (!NetworkResponseKt.isRetryable(this.$currentOpenState)) {
            var26 = new ErrorState$WithoutCTA(0, 0, null, 7, null);
         } else {
            byte var10000 = 0;
            byte var10001 = 0;
            Object var10002 = null;
            byte var10003 = 0;
            InboxViewModel var5 = this.$viewModel;
            var1.startReplaceGroup(1816924167);
            ComposerKt.sourceInformation(var1, "CC(remember):InboxScreen.kt#9igjgp");
            Composer var7 = var1;
            boolean var8 = var1.changedInstance(var5);
            int var9 = 0;
            Object var10 = var7.rememberedValue();
            int var11 = 0;
            Object var10004;
            if (!var8 && var10 != Composer.Companion.getEmpty()) {
               var10004 = var10;
            } else {
               byte var17 = 0;
               Object var16 = null;
               byte var15 = 0;
               byte var14 = 0;
               int var12 = 0;
               KFunction var18 = new InboxScreenKt$InboxScreen$1$1$1(var5);
               var10000 = var14;
               var10001 = var15;
               var10002 = var16;
               var10003 = var17;
               Object var13 = var18;
               var7.updateRememberedValue(var13);
               var10004 = var13;
            }

            KFunction var6 = (KFunction)var10004;
            var1.endReplaceGroup();
            Function0 var27 = (Function0)var6;
            Object var19 = null;
            byte var20 = 15;
            Function0 var21 = var27;
            byte var22 = var10003;
            Object var23 = var10002;
            byte var24 = var10001;
            byte var25 = var10000;
            var26 = new ErrorState$WithCTA(var25, var24, (Integer)var23, var22, var21, var20, (DefaultConstructorMarker)var19);
         }

         ErrorState var4 = var26;
         var1.endReplaceGroup();
         InboxErrorScreenKt.InboxErrorScreen(var4, var3, var1, 48, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
