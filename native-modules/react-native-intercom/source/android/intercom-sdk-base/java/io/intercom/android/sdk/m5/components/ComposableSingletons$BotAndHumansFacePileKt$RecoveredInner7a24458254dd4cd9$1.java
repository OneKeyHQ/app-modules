package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBotAndHumansFacePile.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BotAndHumansFacePile.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$BotAndHumansFacePileKt$lambda-2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,123:1\n118#2:124\n*S KotlinDebug\n*F\n+ 1 BotAndHumansFacePile.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$BotAndHumansFacePileKt$lambda-2$1\n*L\n118#1:124\n*E\n"
)
final class ComposableSingletons$BotAndHumansFacePileKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$BotAndHumansFacePileKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$BotAndHumansFacePileKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C111@3850L261:BotAndHumansFacePile.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1080506615,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$BotAndHumansFacePileKt.lambda-2.<anonymous> (BotAndHumansFacePile.kt:111)"
            );
         }

         Avatar var10001 = Avatar.create("", "VR");
         Intrinsics.checkNotNullExpressionValue(var10001, "create(...)");
         Pair var10002 = new Pair(null, Avatar.create("", "SK"));
         int var3 = 32;
         int var4 = 0;
         BotAndHumansFacePileKt.recovered$2d180ffbba3167ff(null, var10001, var10002, Dp.recovered$95b3d103663bf060(var3), "Fin", var1, 27648, 1);
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
