package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.home.data.HomeCardType;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData;
import io.intercom.android.sdk.m5.home.data.Link;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ExternalLinkCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ExternalLinkCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ExternalLinkCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C89@3672L437:ExternalLinkCard.kt#utrtal");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               19664413,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$ExternalLinkCardKt.lambda-1.<anonymous> (ExternalLinkCard.kt:89)"
            );
         }

         Link[] var4 = new Link[]{new Link("Ask the community", "https://stackoverflow.com/"), new Link("Knowledge base", "https://stackoverflow.com/")};
         List var3 = CollectionsKt.listOf(var4);
         HomeCardType var5 = HomeCardType.EXTERNAL_LINKS;
         ExternalLinkCardKt.ExternalLinkCard(new HomeCards$HomeExternalLinkData("External Links", var5, var3), var1, 0);
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
