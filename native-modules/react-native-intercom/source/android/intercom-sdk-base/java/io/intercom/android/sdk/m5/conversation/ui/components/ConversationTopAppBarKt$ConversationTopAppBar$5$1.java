package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationTopAppBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationTopAppBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationTopAppBarKt$ConversationTopAppBar$5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,95:1\n1047#2,6:96\n*S KotlinDebug\n*F\n+ 1 ConversationTopAppBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationTopAppBarKt$ConversationTopAppBar$5$1\n*L\n75#1:96,6\n*E\n"
)
final class ConversationTopAppBarKt$ConversationTopAppBar$5$1 implements Function3 {
   // $VF: synthetic field
   final TopAppBarUiState $topAppBarUiState;
   // $VF: synthetic field
   final Function1 $onMenuClicked;
   // $VF: synthetic field
   final State $contentColorState;
   // $VF: synthetic field
   final Function1 $trackMetric;

   ConversationTopAppBarKt$ConversationTopAppBar$5$1(TopAppBarUiState var1, Function1 var2, State var3, Function1 var4) {
      this.$topAppBarUiState = var1;
      this.$onMenuClicked = var2;
      this.$contentColorState = var3;
      this.$trackMetric = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TopActionBar");
      ComposerKt.sourceInformation(var2, "C74@3264L137,72@3137L390:ConversationTopAppBar.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -69139937,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBar.<anonymous>.<anonymous> (ConversationTopAppBar.kt:72)"
            );
         }

         List var10000 = this.$topAppBarUiState.getHeaderMenuItems();
         var2.startReplaceGroup(-1166851020);
         ComposerKt.sourceInformation(var2, "CC(remember):ConversationTopAppBar.kt#9igjgp");
         Composer var5 = var2;
         boolean var6 = var2.changed(this.$onMenuClicked);
         Function1 var7 = this.$onMenuClicked;
         int var8 = 0;
         Object var9 = var5.rememberedValue();
         int var10 = 0;
         kotlin.jvm.functions.Function1 var14;
         if (!var6 && var9 != Composer.Companion.getEmpty()) {
            var14 = (Function1)var9;
         } else {
            List var13 = var10000;
            int var11 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_424_0 = var7;
            var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_424_0) -> ConversationTopAppBarKt$ConversationTopAppBar$5$1.invoke$lambda$1$lambda$0(recoveredCapture_424_0, (io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredArgument_424_0));
            var10000 = var13;
            Object var12 = var14;
            var5.updateRememberedValue(var12);
            var14 = (Function1)var12;
         }

         Function1 var4 = var14;
         var2.endReplaceGroup();
         ConversationKebabKt.recovered$25e666bc9a1af21e(
            var10000, var4, ((Color)this.$contentColorState.getValue()).recovered$45f9de7b85e0e0b5(), this.$trackMetric, var2, 0, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function1 var0, HeaderMenuItem var1) {
      Intrinsics.checkNotNullParameter(var1, "item");
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
