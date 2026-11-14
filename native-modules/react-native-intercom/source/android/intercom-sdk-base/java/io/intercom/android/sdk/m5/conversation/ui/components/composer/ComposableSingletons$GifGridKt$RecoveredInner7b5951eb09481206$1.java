package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nGifGrid.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$GifGridKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,172:1\n118#2:173\n*S KotlinDebug\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$GifGridKt$lambda-1$1\n*L\n105#1:173\n*E\n"
)
final class ComposableSingletons$GifGridKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$GifGridKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$GifGridKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C105@4791L6,106@4845L49,107@4946L10,103@4659L331:GifGrid.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               885449481,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$GifGridKt.lambda-1.<anonymous> (GifGrid.kt:103)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 8;
         int var5 = 0;
         Modifier var3 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var4), 0.0F, 2, null);
         long var8 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         String var6 = StringResources_androidKt.stringResource(string.intercom_search_gif, var1, 0);
         TextStyle var7 = IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType04();
         TextKt.recovered$5b114ab32a4f4190(var6, var3, var8, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var7, var1, 48, 0, 131064);
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
