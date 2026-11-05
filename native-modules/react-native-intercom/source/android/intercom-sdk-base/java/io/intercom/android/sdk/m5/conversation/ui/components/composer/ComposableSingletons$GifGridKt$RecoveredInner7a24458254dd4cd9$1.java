package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nGifGrid.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$GifGridKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,172:1\n1047#2,6:173\n1047#2,6:179\n*S KotlinDebug\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$GifGridKt$lambda-2$1\n*L\n166#1:173,6\n167#1:179,6\n*E\n"
)
final class ComposableSingletons$GifGridKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$GifGridKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$GifGridKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C165@6655L2,166@6700L2,163@6581L136:GifGrid.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2034998788,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$GifGridKt.lambda-2.<anonymous> (GifGrid.kt:163)"
            );
         }

         Object var10000 = null;
         List var10001 = CollectionsKt.emptyList();
         var1.startReplaceGroup(1721950880);
         ComposerKt.sourceInformation(var1, "CC(remember):GifGrid.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            List var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function1 var12 = ((kotlin.jvm.functions.Function1) (recoveredArgument_534_0) -> ComposableSingletons$GifGridKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0((io.intercom.android.sdk.blocks.lib.models.Block)recoveredArgument_534_0));
            var10000 = var10;
            var10001 = var11;
            Object var20 = var12;
            var4.updateRememberedValue(var20);
            var10002 = (Function1)var20;
         } else {
            var10002 = (Function1)var7;
         }

         Function1 var3 = (kotlin.jvm.functions.Function1) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(1721952320);
         ComposerKt.sourceInformation(var1, "CC(remember):GifGrid.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var25 = var3;
            List var24 = var10001;
            Object var23 = var10000;
            int var21 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_533_0) -> ComposableSingletons$GifGridKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$3$lambda$2((java.lang.String)recoveredArgument_533_0));
            var10000 = var23;
            var10001 = var24;
            var10002 = var25;
            Object var22 = var13;
            var4.updateRememberedValue(var22);
            var10003 = var22;
         } else {
            var10003 = var7;
         }

         var3 = (Function1)var10003;
         var1.endReplaceGroup();
         GifGridKt.GifGrid((Modifier)var10000, var10001, (kotlin.jvm.functions.Function1) var10002, var3, var1, 3504, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Block var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
