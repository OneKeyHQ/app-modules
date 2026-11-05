package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.material3.IconButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterSearchTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,163:1\n1047#2,6:164\n*S KotlinDebug\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3\n*L\n131#1:164,6\n*E\n"
)
final class HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3 implements Function2 {
   // $VF: synthetic field
   final MutableStateFlow $textFlow;
   // $VF: synthetic field
   final MutableState $searchText$delegate;

   HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3(MutableStateFlow var1, MutableState var2) {
      this.$textFlow = var1;
      this.$searchText$delegate = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C130@5608L131,129@5558L569:HelpCenterSearchTopBar.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1908343233, var2, -1, "io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBar.<anonymous>.<anonymous> (HelpCenterSearchTopBar.kt:128)"
            );
         }

         if (HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$2(this.$searchText$delegate).length() > 0) {
            var1.startReplaceGroup(215647391);
            ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
            Composer var4 = var1;
            boolean var5 = var1.changedInstance(this.$textFlow);
            MutableStateFlow var6 = this.$textFlow;
            MutableState var7 = this.$searchText$delegate;
            int var8 = 0;
            Object var9 = var4.rememberedValue();
            int var10 = 0;
            Object var10000;
            if (!var5 && var9 != Composer.Companion.getEmpty()) {
               var10000 = var9;
            } else {
               int var11 = 0;
               final kotlinx.coroutines.flow.MutableStateFlow recoveredCapture_105_0 = var6;
               final androidx.compose.runtime.MutableState recoveredCapture_105_1 = var7;
               Object var12 = ((kotlin.jvm.functions.Function0) () -> HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3.invoke$lambda$1$lambda$0(recoveredCapture_105_0, recoveredCapture_105_1));
               var4.updateRememberedValue(var12);
               var10000 = var12;
            }

            Function0 var3 = (Function0)var10000;
            var1.endReplaceGroup();
            IconButtonKt.IconButton(
               var3, null, false, null, null, null, ComposableSingletons$HelpCenterSearchTopBarKt.INSTANCE.recovered$1400af5e34386102(), var1, 1572864, 62
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(MutableStateFlow var0, MutableState var1) {
      HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$3(var1, "");
      var0.setValue("");
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
