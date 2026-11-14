package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u001a'\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "NoteCardRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "companyName",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/models/Part;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "NoteCardRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNoteCardRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NoteCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NoteCardRowKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,67:1\n118#2:68\n*S KotlinDebug\n*F\n+ 1 NoteCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NoteCardRowKt\n*L\n24#1:68\n*E\n"
)
public final class NoteCardRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NoteCardRow(@Nullable Modifier var0, @NotNull Part var1, @NotNull String var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "companyName");
      var3 = var3.startRestartGroup(-746207954);
      ComposerKt.sourceInformation(var3, "C(NoteCardRow)P(1,2)24@960L23,25@991L504,22@837L658:NoteCardRow.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-746207954, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.NoteCardRow (NoteCardRow.kt:21)");
         }

         int var7 = 14;
         int var8 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var7);
         var7 = 12;
         var8 = 0;
         IntercomCardKt.IntercomCard(
            PaddingKt.recovered$904403cea4c49d95(var0, var10001, Dp.recovered$95b3d103663bf060(var7)),
            IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var3, IntercomCardStyle.$stable << 15, 31),
            ComposableLambdaKt.rememberComposableLambda(2124316578, true, new NoteCardRowKt$NoteCardRow$1(var1, var2), var3, 54),
            var3,
            384 | Style.$stable << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_763_0 = var0;
         final io.intercom.android.sdk.models.Part recoveredCapture_763_1 = var1;
         final java.lang.String recoveredCapture_763_2 = var2;
         final int recoveredCapture_763_3 = var4;
         final int recoveredCapture_763_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_763_0, recoveredArgument_763_1) -> NoteCardRowKt.NoteCardRow$lambda$0(recoveredCapture_763_0, recoveredCapture_763_1, recoveredCapture_763_2, recoveredCapture_763_3, recoveredCapture_763_4, (androidx.compose.runtime.Composer)recoveredArgument_763_0, ((java.lang.Integer)recoveredArgument_763_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NoteCardRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1220886807);
      ComposerKt.sourceInformation(var0, "C(NoteCardRowPreview)45@1569L679:NoteCardRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1220886807, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.NoteCardRowPreview (NoteCardRow.kt:44)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$NoteCardRowKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_764_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_764_0, recoveredArgument_764_1) -> NoteCardRowKt.NoteCardRowPreview$lambda$1(recoveredCapture_764_0, (androidx.compose.runtime.Composer)recoveredArgument_764_0, ((java.lang.Integer)recoveredArgument_764_1).intValue())));
      }
   }

   private static final Unit NoteCardRow$lambda$0(Modifier var0, Part var1, String var2, int var3, int var4, Composer var5, int var6) {
      NoteCardRow(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit NoteCardRowPreview$lambda$1(int var0, Composer var1, int var2) {
      NoteCardRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
