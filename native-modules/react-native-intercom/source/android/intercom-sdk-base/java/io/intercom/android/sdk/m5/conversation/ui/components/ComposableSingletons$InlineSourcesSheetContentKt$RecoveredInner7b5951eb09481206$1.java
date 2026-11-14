package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.InlineSource;
import io.intercom.android.sdk.models.Source;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$InlineSourcesSheetContentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$InlineSourcesSheetContentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$InlineSourcesSheetContentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C183@7999L992:InlineSourcesSheetContent.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -402572306,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$InlineSourcesSheetContentKt.lambda-1.<anonymous> (InlineSourcesSheetContent.kt:183)"
            );
         }

         List var10000 = CollectionsKt.listOf(new InlineSource("", "article", "1", "http://www.developer.intercom.com", "Your pay as you go bill explained"));
         Source[] var3 = new Source[]{
            new Source("1", "article", "Your pay as you go bill explained", "http://www.developer.intercom.com"),
            new Source("2", "external", "External article", "http://www.google.com/lol")
         };
         InlineSourcesSheetContentKt.InlineSourcesSheetContent(var10000, CollectionsKt.listOf(var3), null, var1, 0, 4);
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
