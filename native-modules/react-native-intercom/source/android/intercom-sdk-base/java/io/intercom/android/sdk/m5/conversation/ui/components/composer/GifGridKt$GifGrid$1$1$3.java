package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.material3.TextFieldDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.text.input.VisualTransformation;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nGifGrid.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/GifGridKt$GifGrid$1$1$3\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,172:1\n118#2:173\n*S KotlinDebug\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/GifGridKt$GifGrid$1$1$3\n*L\n96#1:173\n*E\n"
)
final class GifGridKt$GifGrid$1$1$3 implements Function3 {
   // $VF: synthetic field
   final MutableState $searchText;
   // $VF: synthetic field
   final MutableInteractionSource $interactionSource;

   GifGridKt$GifGrid$1$1$3(MutableState var1, MutableInteractionSource var2) {
      this.$searchText = var1;
      this.$interactionSource = var2;
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public final void invoke(Function2 var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "it");
      ComposerKt.sourceInformation(var2, "C96@4296L301,88@3885L1146:GifGrid.kt#yb6g9");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1827261249,
               var4,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGrid.<anonymous>.<anonymous>.<anonymous> (GifGrid.kt:88)"
            );
         }

         TextFieldDefaults var5 = TextFieldDefaults.INSTANCE;
         String var6 = (String)this.$searchText.getValue();
         VisualTransformation var7 = VisualTransformation.Companion.getNone();
         int var9 = 0;
         int var10 = 0;
         PaddingValues var8 = PaddingKt.recovered$9fd290d401ad9a87(Dp.recovered$95b3d103663bf060(var9));
         TextFieldDefaults var20 = TextFieldDefaults.INSTANCE;
         long var11 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var13 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var15 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var17 = Color.Companion.recovered$562f21ab17d9b4c7();
         TextFieldColors var19 = var20.recovered$8136159443990665(
            0L,
            0L,
            0L,
            0L,
            var13,
            var11,
            0L,
            0L,
            0L,
            0L,
            null,
            var15,
            var17,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            var2,
            221184,
            432,
            0,
            0,
            3072,
            2147477455,
            4095
         );
         var5.DecorationBox(
            var6,
            var1,
            true,
            true,
            var7,
            this.$interactionSource,
            false,
            null,
            ComposableSingletons$GifGridKt.INSTANCE.recovered$eac478aa65045e5d(),
            null,
            null,
            null,
            null,
            null,
            null,
            var19,
            var8,
            null,
            var2,
            100887936 | 112 & var4 << 3,
            102236160,
            163520
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
      this.invoke((Function2)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
