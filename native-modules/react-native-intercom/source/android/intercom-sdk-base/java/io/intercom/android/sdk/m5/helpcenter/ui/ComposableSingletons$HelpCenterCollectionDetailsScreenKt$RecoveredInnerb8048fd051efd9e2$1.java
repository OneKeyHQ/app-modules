package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterCollectionDetailsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/ComposableSingletons$HelpCenterCollectionDetailsScreenKt$lambda-3$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,96:1\n118#2:97\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/ComposableSingletons$HelpCenterCollectionDetailsScreenKt$lambda-3$1\n*L\n85#1:97\n*E\n"
)
final class ComposableSingletons$HelpCenterCollectionDetailsScreenKt$RecoveredInnerb8048fd051efd9e2$1 implements Function3 {
   public static final ComposableSingletons$HelpCenterCollectionDetailsScreenKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$HelpCenterCollectionDetailsScreenKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C84@3656L70:HelpCenterCollectionDetailsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               999489967,
               var3,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionDetailsScreenKt.lambda-3.<anonymous> (HelpCenterCollectionDetailsScreen.kt:84)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 24;
         int var5 = 0;
         BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsComponent(
            PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var4), 0.0F, 0.0F, 13, null), var2, 6, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
