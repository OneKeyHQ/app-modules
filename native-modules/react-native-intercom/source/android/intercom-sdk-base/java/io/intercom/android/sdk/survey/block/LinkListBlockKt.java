package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a1\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0004\b\n\u0010\u000b¨\u0006\f",
   d2 = {
         "LinkListBlock",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "textColor",
         "Landroidx/compose/ui/graphics/Color;",
         "conversationId",
         "",
         "LinkListBlock-cf5BqRc",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/blocks/lib/models/Block;JLjava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
public final class LinkListBlockKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$82efcc70bb4f4526(
      @Nullable Modifier var0, @NotNull Block var1, long var2, @NotNull String var4, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "block");
      Intrinsics.checkNotNullParameter(var4, "conversationId");
      var5 = var5.startRestartGroup(-1519911583);
      ComposerKt.sourceInformation(var5, "C(LinkListBlock)P(2!1,3:c#ui.graphics.Color)18@570L23,19@601L152,16@493L260:LinkListBlock.kt#jxhu3x");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var4) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1519911583, var8, -1, "io.intercom.android.sdk.survey.block.LinkListBlock (LinkListBlock.kt:15)");
         }

         IntercomCardKt.IntercomCard(
            var0,
            IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var5, IntercomCardStyle.$stable << 15, 31),
            ComposableLambdaKt.rememberComposableLambda(-46303019, true, new LinkListBlockKt$LinkListBlock$1(var1, var2, var4), var5, 54),
            var5,
            384 | 14 & var8 | Style.$stable << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var5.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1274_0 = var0;
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_1274_1 = var1;
         final long recoveredCapture_1274_2 = var2;
         final java.lang.String recoveredCapture_1274_3 = var4;
         final int recoveredCapture_1274_4 = var6;
         final int recoveredCapture_1274_5 = var7;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1274_0, recoveredArgument_1274_1) -> LinkListBlockKt.LinkListBlock_cf5BqRc$lambda$0(recoveredCapture_1274_0, recoveredCapture_1274_1, recoveredCapture_1274_2, recoveredCapture_1274_3, recoveredCapture_1274_4, recoveredCapture_1274_5, (androidx.compose.runtime.Composer)recoveredArgument_1274_0, ((java.lang.Integer)recoveredArgument_1274_1).intValue())));
      }
   }

   private static final Unit LinkListBlock_cf5BqRc$lambda$0(Modifier var0, Block var1, long var2, String var4, int var5, int var6, Composer var7, int var8) {
      recovered$82efcc70bb4f4526(var0, var1, var2, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }
}
