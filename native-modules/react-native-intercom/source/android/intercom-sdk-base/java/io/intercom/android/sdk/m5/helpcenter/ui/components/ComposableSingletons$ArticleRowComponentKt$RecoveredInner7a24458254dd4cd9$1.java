package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ArticleRowComponentKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$ArticleRowComponentKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$ArticleRowComponentKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C59@2034L6,58@1956L282:ArticleRowComponent.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -772569635,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$ArticleRowComponentKt.lambda-2.<anonymous> (ArticleRowComponent.kt:58)"
            );
         }

         ArticleRowComponentKt.ArticleRowComponent(
            BackgroundKt.recovered$42a3174180b3fb7a(
               Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
            ),
            new CollectionDetailsRow$ArticleRow("", "Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum Lorem Ipsum"),
            null,
            var1,
            0,
            4
         );
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
