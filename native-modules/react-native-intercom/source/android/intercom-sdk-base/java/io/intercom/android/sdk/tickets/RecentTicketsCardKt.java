package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.m5.components.HomeCardScaffoldKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aC\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\r¨\u0006\u000e",
   d2 = {
         "RecentTicketsCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "cardTitle",
         "",
         "tickets",
         "",
         "Lio/intercom/android/sdk/models/Ticket;",
         "onClick",
         "Lkotlin/Function1;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "RecentTicketsCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nRecentTicketsCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/RecentTicketsCardKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,81:1\n1047#2,6:82\n*S KotlinDebug\n*F\n+ 1 RecentTicketsCard.kt\nio/intercom/android/sdk/tickets/RecentTicketsCardKt\n*L\n21#1:82,6\n*E\n"
)
public final class RecentTicketsCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void RecentTicketsCard(
      @Nullable Modifier var0, @NotNull String var1, @NotNull List var2, @Nullable Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "tickets");
      var4 = var4.startRestartGroup(1214351394);
      ComposerKt.sourceInformation(var4, "C(RecentTicketsCard)P(1!1,3)20@815L2,25@911L401,22@827L485:RecentTicketsCard.kt#3ofz5f");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 8) != 0) {
            var4.startReplaceGroup(-424319677);
            ComposerKt.sourceInformation(var4, "CC(remember):RecentTicketsCard.kt#9igjgp");
            Composer var9 = var4;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1526_0) -> RecentTicketsCardKt.RecentTicketsCard$lambda$1$lambda$0((java.lang.String)recoveredArgument_1526_0));
               var9.updateRememberedValue(var16);
               var10000 = var16;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var4.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1214351394, var7, -1, "io.intercom.android.sdk.tickets.RecentTicketsCard (RecentTicketsCard.kt:21)");
         }

         HomeCardScaffoldKt.HomeCardScaffold(
            var0,
            var1,
            ComposableLambdaKt.rememberComposableLambda(1499488214, true, new RecentTicketsCardKt$RecentTicketsCard$2(var2, var3), var4, 54),
            var4,
            384 | 14 & var7 | 112 & var7,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var4.endRestartGroup();
      if (var17 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1527_0 = var0;
         final java.lang.String recoveredCapture_1527_1 = var1;
         final java.util.List recoveredCapture_1527_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1527_3 = var3;
         final int recoveredCapture_1527_4 = var5;
         final int recoveredCapture_1527_5 = var6;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1527_0, recoveredArgument_1527_1) -> RecentTicketsCardKt.RecentTicketsCard$lambda$2(recoveredCapture_1527_0, recoveredCapture_1527_1, recoveredCapture_1527_2, recoveredCapture_1527_3, recoveredCapture_1527_4, recoveredCapture_1527_5, (androidx.compose.runtime.Composer)recoveredArgument_1527_0, ((java.lang.Integer)recoveredArgument_1527_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void RecentTicketsCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1547026625);
      ComposerKt.sourceInformation(var0, "C(RecentTicketsCardPreview)42@1382L1374:RecentTicketsCard.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1547026625, var1, -1, "io.intercom.android.sdk.tickets.RecentTicketsCardPreview (RecentTicketsCard.kt:41)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$RecentTicketsCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1528_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1528_0, recoveredArgument_1528_1) -> RecentTicketsCardKt.RecentTicketsCardPreview$lambda$3(recoveredCapture_1528_0, (androidx.compose.runtime.Composer)recoveredArgument_1528_0, ((java.lang.Integer)recoveredArgument_1528_1).intValue())));
      }
   }

   private static final Unit RecentTicketsCard$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit RecentTicketsCard$lambda$2(Modifier var0, String var1, List var2, Function1 var3, int var4, int var5, Composer var6, int var7) {
      RecentTicketsCard(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit RecentTicketsCardPreview$lambda$3(int var0, Composer var1, int var2) {
      RecentTicketsCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
