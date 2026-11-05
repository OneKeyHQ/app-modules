package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "TeamIntro",
         "",
         "text",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TeamIntroPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTeamIntro.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamIntro.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TeamIntroKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,39:1\n75#2:40\n*S KotlinDebug\n*F\n+ 1 TeamIntro.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TeamIntroKt\n*L\n22#1:40\n*E\n"
)
public final class TeamIntroKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamIntro(@NotNull String var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "text");
      var2 = var2.startRestartGroup(673666815);
      ComposerKt.sourceInformation(var2, "C(TeamIntro)P(1)21@790L7,22@830L10,22@873L6,18@626L310:TeamIntro.kt#hr3bi5");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(673666815, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TeamIntro (TeamIntro.kt:17)");
         }

         CharSequence var10000 = HtmlCompat.fromHtml(var0, 0);
         Intrinsics.checkNotNullExpressionValue(var10000, "fromHtml(...)");
         var10000 = var10000;
         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         AnnotatedString var6 = BlockExtensionsKt.recovered$c71786fddfec5215(var10000, (Context)var10, null, 0L, 6, null);
         TextStyle var12 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04Point5(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
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
         );
         var8 = TextAlign.Companion.recovered$d707b9dd64876179();
         TextKt.recovered$489343e57da77a35(
            var6,
            var1,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var8),
            0L,
            0,
            false,
            0,
            0,
            null,
            null,
            var12,
            var2,
            112 & var5,
            0,
            261116
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var15 = var2.endRestartGroup();
      if (var15 != null) {
         final java.lang.String recoveredCapture_794_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_794_1 = var1;
         final int recoveredCapture_794_2 = var3;
         final int recoveredCapture_794_3 = var4;
         var15.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_794_0, recoveredArgument_794_1) -> TeamIntroKt.TeamIntro$lambda$0(recoveredCapture_794_0, recoveredCapture_794_1, recoveredCapture_794_2, recoveredCapture_794_3, (androidx.compose.runtime.Composer)recoveredArgument_794_0, ((java.lang.Integer)recoveredArgument_794_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamIntroPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(825513769);
      ComposerKt.sourceInformation(var0, "C(TeamIntroPreview)30@1008L231:TeamIntro.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(825513769, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TeamIntroPreview (TeamIntro.kt:29)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TeamIntroKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_795_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_795_0, recoveredArgument_795_1) -> TeamIntroKt.TeamIntroPreview$lambda$1(recoveredCapture_795_0, (androidx.compose.runtime.Composer)recoveredArgument_795_0, ((java.lang.Integer)recoveredArgument_795_1).intValue())));
      }
   }

   private static final Unit TeamIntro$lambda$0(String var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      TeamIntro(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TeamIntroPreview$lambda$1(int var0, Composer var1, int var2) {
      TeamIntroPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
