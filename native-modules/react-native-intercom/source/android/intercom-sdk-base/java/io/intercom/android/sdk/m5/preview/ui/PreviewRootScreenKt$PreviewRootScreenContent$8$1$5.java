package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.pager.PagerScope;
import androidx.compose.foundation.pager.PagerState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPreviewRootScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt$PreviewRootScreenContent$8$1$5\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,189:1\n1047#2,6:190\n*S KotlinDebug\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt$PreviewRootScreenContent$8$1$5\n*L\n159#1:190,6\n*E\n"
)
final class PreviewRootScreenKt$PreviewRootScreenContent$8$1$5 implements Function4 {
   // $VF: synthetic field
   final PreviewUiState $state;
   // $VF: synthetic field
   final PagerState $pagerState;
   // $VF: synthetic field
   final MutableState $isCurrentPageZoomed$delegate;

   PreviewRootScreenKt$PreviewRootScreenContent$8$1$5(PreviewUiState var1, PagerState var2, MutableState var3) {
      this.$state = var1;
      this.$pagerState = var2;
      this.$isCurrentPageZoomed$delegate = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PagerScope var1, int var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$HorizontalPager");
      ComposerKt.sourceInformation(var3, "C158@6148L180,154@5956L391:PreviewRootScreen.kt#hhbc0c");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            570504714,
            var4,
            -1,
            "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenContent.<anonymous>.<anonymous>.<anonymous> (PreviewRootScreen.kt:153)"
         );
      }

      IntercomPreviewFile var5 = (IntercomPreviewFile)this.$state.getFiles().get(var2);
      Modifier var10000 = Modifier.Companion;
      IntercomPreviewFile var10001 = var5;
      boolean var10002 = var2 == this.$pagerState.getCurrentPage();
      var3.startReplaceGroup(714514166);
      ComposerKt.sourceInformation(var3, "CC(remember):PreviewRootScreen.kt#9igjgp");
      Composer var7 = var3;
      boolean var8 = ((var4 & 112 ^ 48) > 32 && var3.changed(var2) || (var4 & 48) == 32) | var3.changed(this.$pagerState);
      PagerState var9 = this.$pagerState;
      MutableState var10 = this.$isCurrentPageZoomed$delegate;
      int var11 = 0;
      Object var12 = var7.rememberedValue();
      int var13 = 0;
      Object var10003;
      if (!var8 && var12 != Composer.Companion.getEmpty()) {
         var10003 = var12;
      } else {
         boolean var18 = var10002;
         IntercomPreviewFile var17 = var5;
         Modifier var16 = var10000;
         int var14 = 0;
         final int recoveredCapture_1159_0 = var2;
         final androidx.compose.foundation.pager.PagerState recoveredCapture_1159_1 = var9;
         final androidx.compose.runtime.MutableState recoveredCapture_1159_2 = var10;
         Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1159_0) -> PreviewRootScreenKt$PreviewRootScreenContent$8$1$5.invoke$lambda$1$lambda$0(recoveredCapture_1159_0, recoveredCapture_1159_1, recoveredCapture_1159_2, ((java.lang.Boolean)recoveredArgument_1159_0).booleanValue()));
         var10000 = var16;
         var10001 = var17;
         var10002 = var18;
         Object var15 = var19;
         var7.updateRememberedValue(var15);
         var10003 = var15;
      }

      Function1 var6 = (Function1)var10003;
      var3.endReplaceGroup();
      PreviewUriKt.PreviewUri(var10000, var10001, var10002, var6, var3, 6, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(int var0, PagerState var1, MutableState var2, boolean var3) {
      if (var0 == var1.getCurrentPage()) {
         PreviewRootScreenKt.access$PreviewRootScreenContent$lambda$18(var2, var3);
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((PagerScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
