package io.intercom.android.sdk.tickets.list.ui;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.platform.TestTagKt;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aC\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0001\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0002\u0010\u000b\u001a3\u0010\f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\u0011¨\u0006\u0012",
   d2 = {
         "TicketsScreen",
         "",
         "uiState",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "onBackButtonClick",
         "Lkotlin/Function0;",
         "onTicketClick",
         "Lkotlin/Function1;",
         "",
         "navIcon",
         "",
         "(Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ILandroidx/compose/runtime/Composer;II)V",
         "TicketsScreenContent",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Content;",
         "paddingValues",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "onClick",
         "(Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Content;Landroidx/compose/foundation/layout/PaddingValues;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,147:1\n1047#2,6:148\n1047#2,6:154\n1047#2,6:160\n*S KotlinDebug\n*F\n+ 1 TicketsScreen.kt\nio/intercom/android/sdk/tickets/list/ui/TicketsScreenKt\n*L\n41#1:148,6\n88#1:154,6\n93#1:160,6\n*E\n"
)
public final class TicketsScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsScreen(
      @NotNull TicketsScreenUiState var0, @NotNull Function0 var1, @Nullable Function1 var2, @DrawableRes int var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "uiState");
      Intrinsics.checkNotNullParameter(var1, "onBackButtonClick");
      var4 = var4.startRestartGroup(-1552213693);
      ComposerKt.sourceInformation(var4, "C(TicketsScreen)P(3,1,2)40@1974L2,45@2093L6,46@2157L13,48@2229L305,57@2554L786,43@2017L1330:TicketsScreen.kt#yl74uv");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var4.startReplaceGroup(1435034320);
            ComposerKt.sourceInformation(var4, "CC(remember):TicketsScreen.kt#9igjgp");
            Composer var9 = var4;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1612_0) -> TicketsScreenKt.TicketsScreen$lambda$1$lambda$0((java.lang.String)recoveredArgument_1612_0));
               var9.updateRememberedValue(var16);
               var10000 = var16;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var4.endReplaceGroup();
            var2 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1552213693, var7, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreen (TicketsScreen.kt:42)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            TestTagKt.testTag(
               WindowInsetsPaddingKt.windowInsetsPadding(
                  BackgroundKt.recovered$42a3174180b3fb7a(
                     Modifier.Companion, IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
                  ),
                  WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var4, 6)
               ),
               "tickets_screen"
            ),
            ComposableLambdaKt.rememberComposableLambda(-1473942521, true, new TicketsScreenKt$TicketsScreen$2(var0, var1, var3), var4, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1690440366, true, new TicketsScreenKt$TicketsScreen$3(var0, var2), var4, 54),
            var4,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var4.endRestartGroup();
      if (var17 != null) {
         final io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState recoveredCapture_1613_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1613_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_1613_2 = var2;
         final int recoveredCapture_1613_3 = var3;
         final int recoveredCapture_1613_4 = var5;
         final int recoveredCapture_1613_5 = var6;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1613_0, recoveredArgument_1613_1) -> TicketsScreenKt.TicketsScreen$lambda$2(recoveredCapture_1613_0, recoveredCapture_1613_1, recoveredCapture_1613_2, recoveredCapture_1613_3, recoveredCapture_1613_4, recoveredCapture_1613_5, (androidx.compose.runtime.Composer)recoveredArgument_1613_0, ((java.lang.Integer)recoveredArgument_1613_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketsScreenContent(
      @NotNull TicketsScreenUiState$Content var0, @NotNull PaddingValues var1, @Nullable Function1 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "uiState");
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      var3 = var3.startRestartGroup(-1912868973);
      ComposerKt.sourceInformation(var3, "C(TicketsScreenContent)P(2,1)87@3486L2,92@3599L1969,89@3498L2070:TicketsScreen.kt#yl74uv");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= ((var4 & 8) == 0 ? var3.changed(var0) : var3.changedInstance(var0)) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var3.startReplaceGroup(1467405837);
            ComposerKt.sourceInformation(var3, "CC(remember):TicketsScreen.kt#9igjgp");
            Composer var8 = var3;
            boolean var9 = false;
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10000;
            if (var11 == Composer.Companion.getEmpty()) {
               int var13 = 0;
               Object var32 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1614_0) -> TicketsScreenKt.TicketsScreenContent$lambda$4$lambda$3((java.lang.String)recoveredArgument_1614_0));
               var8.updateRememberedValue(var32);
               var10000 = var32;
            } else {
               var10000 = var11;
            }

            Function1 var7 = (Function1)var10000;
            var3.endReplaceGroup();
            var2 = var7;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1912868973, var6, -1, "io.intercom.android.sdk.tickets.list.ui.TicketsScreenContent (TicketsScreen.kt:88)");
         }

         Modifier var34 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         Object var10001 = null;
         PaddingValues var10002 = var1;
         boolean var10003 = false;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var3.startReplaceGroup(1467411420);
         ComposerKt.sourceInformation(var3, "CC(remember):TicketsScreen.kt#9igjgp");
         Composer var27 = var3;
         boolean var28 = ((var6 & 14) == 4 || (var6 & 8) != 0 && var3.changedInstance(var0)) | (var6 & 896) == 256;
         int var29 = 0;
         Object var30 = var27.rememberedValue();
         int var31 = 0;
         Object var10009;
         if (!var28 && var30 != Composer.Companion.getEmpty()) {
            var10009 = var30;
         } else {
            Object var23 = null;
            boolean var22 = false;
            Object var21 = null;
            Object var20 = null;
            Object var19 = null;
            boolean var18 = false;
            PaddingValues var17 = var1;
            Object var16 = null;
            Modifier var15 = var34;
            int var33 = 0;
            final io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content recoveredCapture_1615_0 = var0;
            final kotlin.jvm.functions.Function1 recoveredCapture_1615_1 = var2;
            Function1 var24 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1615_0) -> TicketsScreenKt.TicketsScreenContent$lambda$6$lambda$5(recoveredCapture_1615_0, recoveredCapture_1615_1, (androidx.compose.foundation.lazy.LazyListScope)recoveredArgument_1615_0));
            var34 = var15;
            var10001 = var16;
            var10002 = var17;
            var10003 = var18;
            var10004 = var19;
            var10005 = var20;
            var10006 = var21;
            var10007 = var22;
            var10008 = var23;
            Object var14 = var24;
            var27.updateRememberedValue(var14);
            var10009 = var14;
         }

         Function1 var26 = (Function1)var10009;
         var3.endReplaceGroup();
         LazyDslKt.LazyColumn(
            var34,
            (LazyListState)var10001,
            var10002,
            var10003,
            (Vertical)var10004,
            (Horizontal)var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var26,
            var3,
            6 | 896 & var6 << 3,
            506
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var35 = var3.endRestartGroup();
      if (var35 != null) {
         final io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content recoveredCapture_1616_0 = var0;
         final androidx.compose.foundation.layout.PaddingValues recoveredCapture_1616_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_1616_2 = var2;
         final int recoveredCapture_1616_3 = var4;
         final int recoveredCapture_1616_4 = var5;
         var35.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1616_0, recoveredArgument_1616_1) -> TicketsScreenKt.TicketsScreenContent$lambda$7(recoveredCapture_1616_0, recoveredCapture_1616_1, recoveredCapture_1616_2, recoveredCapture_1616_3, recoveredCapture_1616_4, (androidx.compose.runtime.Composer)recoveredArgument_1616_0, ((java.lang.Integer)recoveredArgument_1616_1).intValue())));
      }
   }

   private static final Unit TicketsScreen$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit TicketsScreen$lambda$2(
      TicketsScreenUiState var0, Function0 var1, Function1 var2, int var3, int var4, int var5, Composer var6, int var7
   ) {
      TicketsScreen(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TicketsScreenContent$lambda$4$lambda$3(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit TicketsScreenContent$lambda$6$lambda$5(TicketsScreenUiState$Content var0, Function1 var1, LazyListScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$LazyColumn");
      LazyListScope.items$default(
         var2,
         var0.getLazyPagingTickets().getItemCount(),
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(264752689, true, new TicketsScreenKt$TicketsScreenContent$2$1$1(var0, var1)),
         6,
         null
      );
      ErrorState var3 = var0.getErrorState();
      if (var3 != null) {
         LazyListScope.item$default(
            var2, null, null, ComposableLambdaKt.composableLambdaInstance(-1511938327, true, new TicketsScreenKt$TicketsScreenContent$2$1$2(var3)), 3, null
         );
      }

      if (var0.isLoadingMore()) {
         LazyListScope.item$default(var2, null, null, ComposableSingletons$TicketsScreenKt.INSTANCE.recovered$b26556cd5c386b55(), 3, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit TicketsScreenContent$lambda$7(
      TicketsScreenUiState$Content var0, PaddingValues var1, Function1 var2, int var3, int var4, Composer var5, int var6
   ) {
      TicketsScreenContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }
}
