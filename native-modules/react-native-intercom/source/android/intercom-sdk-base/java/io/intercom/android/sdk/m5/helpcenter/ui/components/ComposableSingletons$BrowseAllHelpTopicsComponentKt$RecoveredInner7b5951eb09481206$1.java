package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nBrowseAllHelpTopicsComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BrowseAllHelpTopicsComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$BrowseAllHelpTopicsComponentKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,114:1\n118#2:115\n*S KotlinDebug\n*F\n+ 1 BrowseAllHelpTopicsComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$BrowseAllHelpTopicsComponentKt$lambda-1$1\n*L\n56#1:115\n*E\n"
)
final class ComposableSingletons$BrowseAllHelpTopicsComponentKt$RecoveredInner7b5951eb09481206$1 implements Function3 {
   public static final ComposableSingletons$BrowseAllHelpTopicsComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$BrowseAllHelpTopicsComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$OutlinedButton");
      ComposerKt.sourceInformation(
         var2, "C52@2155L59,51@2127L137,55@2273L47,57@2354L61,58@2451L10,59@2519L6,56@2329L221:BrowseAllHelpTopicsComponent.kt#ngglre"
      );
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1224833391,
               var3,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$BrowseAllHelpTopicsComponentKt.lambda-1.<anonymous> (BrowseAllHelpTopicsComponent.kt:51)"
            );
         }

         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_article_book_icon, var2, 0), null, null, 0L, var2, 48 | Painter.$stable, 12
         );
         Modifier var10000 = Modifier.Companion;
         int var4 = 6;
         int var5 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var4)), var2, 6);
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(string.intercom_browse_all_help_topics, var2, 0),
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04Point5(),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
               0L,
               null,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777214,
               null
            ),
            var2,
            0,
            0,
            131070
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
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
