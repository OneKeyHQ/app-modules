package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.animation.SingleValueAnimationKt;
import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.m5.components.TopActionBarKt;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.m5.conversation.states.TicketProgressRowState;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.tickets.TicketProgressBannerKt;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.ThemeManager;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001as\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\u0014\b\u0002\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\r¨\u0006\u000e",
   d2 = {
         "ConversationTopAppBar",
         "",
         "topAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "onBackClick",
         "Lkotlin/Function0;",
         "onTitleClicked",
         "navigateToTicketDetail",
         "onMenuClicked",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "trackMetric",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "(Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationTopAppBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationTopAppBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationTopAppBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,95:1\n1047#2,6:96\n1047#2,6:102\n1047#2,6:108\n1047#2,6:114\n87#3:120\n83#3,10:121\n94#3:156\n81#4,6:131\n88#4,6:146\n96#4:155\n402#5,9:137\n411#5,3:152\n*S KotlinDebug\n*F\n+ 1 ConversationTopAppBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationTopAppBarKt\n*L\n20#1:96,6\n21#1:102,6\n22#1:108,6\n23#1:114,6\n59#1:120\n59#1:121,10\n59#1:156\n59#1:131,6\n59#1:146,6\n59#1:155\n59#1:137,9\n59#1:152,3\n*E\n"
)
public final class ConversationTopAppBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationTopAppBar(
      @NotNull TopAppBarUiState var0,
      @Nullable Function0 var1,
      @Nullable Function0 var2,
      @Nullable Function0 var3,
      @Nullable Function1 var4,
      @Nullable Function1 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var0, "topAppBarUiState");
      Composer var54 = var6.startRestartGroup(1613129219);
      ComposerKt.sourceInformation(
         var54,
         "C(ConversationTopAppBar)P(4,1,3)19@906L2,20@951L2,21@1001L2,22@1045L2,25@1147L21,34@1450L317,42@1804L315,50@2157L321,58@2484L1329:ConversationTopAppBar.kt#o19g89"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var54.changedInstance(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var54.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var54.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var54.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var54.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var54.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var54.getSkipping()) {
         var54.skipToGroupEnd();
      } else {
         if ((var8 & 2) != 0) {
            var1 = null;
         }

         if ((var8 & 4) != 0) {
            var54.startReplaceGroup(-287893905);
            ComposerKt.sourceInformation(var54, "CC(remember):ConversationTopAppBar.kt#9igjgp");
            Composer var11 = var54;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var80 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt.ConversationTopAppBar$lambda$1$lambda$0());
               var11.updateRememberedValue(var80);
               var10000 = var80;
            } else {
               var10000 = var14;
            }

            Function0 var10 = (Function0)var10000;
            var54.endReplaceGroup();
            var2 = var10;
         }

         if ((var8 & 8) != 0) {
            var54.startReplaceGroup(-287892465);
            ComposerKt.sourceInformation(var54, "CC(remember):ConversationTopAppBar.kt#9igjgp");
            Composer var59 = var54;
            boolean var63 = false;
            int var67 = 0;
            Object var71 = var59.rememberedValue();
            int var75 = 0;
            Object var94;
            if (var71 == Composer.Companion.getEmpty()) {
               int var81 = 0;
               Object var82 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt.ConversationTopAppBar$lambda$3$lambda$2());
               var59.updateRememberedValue(var82);
               var94 = var82;
            } else {
               var94 = var71;
            }

            Function0 var55 = (Function0)var94;
            var54.endReplaceGroup();
            var3 = var55;
         }

         if ((var8 & 16) != 0) {
            var54.startReplaceGroup(-287890865);
            ComposerKt.sourceInformation(var54, "CC(remember):ConversationTopAppBar.kt#9igjgp");
            Composer var60 = var54;
            boolean var64 = false;
            int var68 = 0;
            Object var72 = var60.rememberedValue();
            int var76 = 0;
            Object var95;
            if (var72 == Composer.Companion.getEmpty()) {
               int var83 = 0;
               Object var84 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_422_0) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt.ConversationTopAppBar$lambda$5$lambda$4((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem) ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredExpressionArgument_422_0)));
               var60.updateRememberedValue(var84);
               var95 = var84;
            } else {
               var95 = var72;
            }

            Function1 var56 = (Function1)var95;
            var54.endReplaceGroup();
            var4 = var56;
         }

         if ((var8 & 32) != 0) {
            var54.startReplaceGroup(-287889457);
            ComposerKt.sourceInformation(var54, "CC(remember):ConversationTopAppBar.kt#9igjgp");
            Composer var61 = var54;
            boolean var65 = false;
            int var69 = 0;
            Object var73 = var61.rememberedValue();
            int var77 = 0;
            Object var96;
            if (var73 == Composer.Companion.getEmpty()) {
               int var85 = 0;
               Object var86 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_423_0) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt.ConversationTopAppBar$lambda$7$lambda$6((io.intercom.android.sdk.m5.conversation.metrics.MetricData) ((io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredExpressionArgument_423_0)));
               var61.updateRememberedValue(var86);
               var96 = var86;
            } else {
               var96 = var73;
            }

            Function1 var57 = (Function1)var96;
            var54.endReplaceGroup();
            var5 = var57;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1613129219, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBar (ConversationTopAppBar.kt:23)"
            );
         }

         boolean var58 = DarkThemeKt.isSystemInDarkTheme(var54, 0);
         boolean var97;
         switch (ThemeManager.INSTANCE.getCurrentThemeMode()) {
            case DARK:
               var97 = true;
               break;
            case LIGHT:
               var97 = false;
               break;
            case SYSTEM:
               var97 = var58;
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         boolean var62 = var97;
         var54.startReplaceGroup(-287873620);
         ComposerKt.sourceInformation(var54, "38@1729L6");
         long var99;
         if (var62 && var0.recovered$cef678816fbbfac0() != null) {
            var99 = var0.recovered$cef678816fbbfac0().recovered$45f9de7b85e0e0b5();
         } else {
            Color var98 = var0.recovered$4d97b5da718de5d3();
            var99 = var98 == null
               ? IntercomTheme.INSTANCE.getColors(var54, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4()
               : var98.recovered$45f9de7b85e0e0b5();
         }

         long var78 = var99;
         var54.endReplaceGroup();
         State var66 = SingleValueAnimationKt.recovered$672a9cefbd871cc0(var78, null, "bgColorState", null, var54, 384, 10);
         var54.startReplaceGroup(-287862139);
         ComposerKt.sourceInformation(var54, "46@2079L6");
         long var101;
         if (var62 && var0.recovered$9043379a6aac6e48() != null) {
            var101 = var0.recovered$9043379a6aac6e48().recovered$45f9de7b85e0e0b5();
         } else {
            Color var100 = var0.recovered$7cce2490cba0e529();
            var101 = var100 == null
               ? IntercomTheme.INSTANCE.getColors(var54, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5()
               : var100.recovered$45f9de7b85e0e0b5();
         }

         long var87 = var101;
         var54.endReplaceGroup();
         State var70 = SingleValueAnimationKt.recovered$672a9cefbd871cc0(var87, null, "contentColorState", null, var54, 384, 10);
         var54.startReplaceGroup(-287850806);
         ComposerKt.sourceInformation(var54, "54@2436L6");
         long var103;
         if (var62 && var0.recovered$2fa8d65c338be95f() != null) {
            var103 = var0.recovered$2fa8d65c338be95f().recovered$45f9de7b85e0e0b5();
         } else {
            Color var102 = var0.recovered$cef6cdac25aafd3e();
            var103 = var102 == null
               ? IntercomTheme.INSTANCE.getColors(var54, IntercomTheme.$stable).getText().recovered$0aef7413383f6122()
               : var102.recovered$45f9de7b85e0e0b5();
         }

         long var17 = var103;
         var54.endReplaceGroup();
         State var74 = SingleValueAnimationKt.recovered$672a9cefbd871cc0(var17, null, "subTitleColorState", null, var54, 384, 10);
         byte var18 = 0;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var54, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var79 = Modifier.Companion;
         Vertical var88 = Arrangement.INSTANCE.getTop();
         Horizontal var89 = Alignment.Companion.getStart();
         MeasurePolicy var20 = ColumnKt.columnMeasurePolicy(var88, var89, var54, 14 & var18 >> 3 | 112 & var18 >> 3);
         Modifier var21 = var79;
         MeasurePolicy var22 = var20;
         Composer var23 = var54;
         int var24 = 112 & var18 << 3;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var26 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var23, 0));
         CompositionLocalMap var27 = var23.getCurrentCompositionLocalMap();
         Modifier var28 = ComposedModifierKt.materializeModifier(var23, var21);
         Function0 var29 = ComposeUiNode.Companion.getConstructor();
         Composer var30 = var23;
         int var31 = 6 | 896 & var24 << 6;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var30.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var30.startReusableNode();
         if (var30.getInserting()) {
            var30.createNode(var29);
         } else {
            var30.useNode();
         }

         Composer var33 = Updater.recovered$676aa17b44fd6280(var30);
         int var34 = 0;
         Updater.recovered$89682b199a142880(var33, var22, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var33, var27, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var33, var26, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var33, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var33, var28, ComposeUiNode.Companion.getSetModifier());
         int var35 = 14 & var31 >> 6;
         Composer var36 = var30;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var38 = 6 | 112 & var18 >> 6;
         Composer var39 = var36;
         ColumnScope var40 = ColumnScopeInstance.INSTANCE;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(var39, 1530880861, "C60@2558L9,71@3119L422,59@2501L1051:ConversationTopAppBar.kt#o19g89");
         String var42 = var0.getTitle().getText(var39, StringProvider.$stable);
         StringProvider var43 = var0.getSubTitle();
         var39.startReplaceGroup(603575061);
         ComposerKt.sourceInformation(var39, "61@2619L9");
         String var44 = var43 == null ? null : var43.getText(var39, StringProvider.$stable);
         var39.endReplaceGroup();
         String var45 = var44;
         Integer var91 = var0.getSubTitleLeadingIcon();
         List var92 = var0.getAvatars();
         Integer var46 = var0.getNavIcon();
         long var47 = ((Color)var66.getValue()).recovered$45f9de7b85e0e0b5();
         long var49 = ((Color)var70.getValue()).recovered$45f9de7b85e0e0b5();
         long var51 = ((Color)var74.getValue()).recovered$45f9de7b85e0e0b5();
         boolean var53 = var0.getDisplayActiveIndicator();
         TopActionBarKt.recovered$e7704aa1c1b2200b(
            null,
            var42,
            var45,
            var91,
            var92,
            var1,
            var46,
            var53,
            var47,
            var49,
            var51,
            var2,
            ComposableLambdaKt.rememberComposableLambda(
               -69139937, true, new ConversationTopAppBarKt$ConversationTopAppBar$5$1(var0, var4, var70, var5), var39, 54
            ),
            var39,
            458752 & var9 << 12,
            384 | 112 & var9 >> 3,
            1
         );
         TicketProgressRowState var90 = var0.getTicketStatusState();
         var39.startReplaceGroup(603606589);
         ComposerKt.sourceInformation(var39, "*86@3616L181");
         if (var90 == null) {
            Object var104 = null;
         } else {
            int var93 = 0;
            TicketProgressBannerKt.TicketProgressBanner(var0.getTicketStatusState().getName(), var3, true, null, var39, 384 | 112 & var9 >> 6, 8);
         }

         var39.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var36);
         var30.endNode();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var54);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var105 = var54.endRestartGroup();
      if (var105 != null) {
         final io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState recoveredExpressionCapture_419_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_419_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_419_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_419_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_419_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_419_5 = var5;
         final int recoveredExpressionCapture_419_6 = var7;
         final int recoveredExpressionCapture_419_7 = var8;
         var105.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_419_0, recoveredExpressionArgument_419_1) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt.ConversationTopAppBar$lambda$10(recoveredExpressionCapture_419_0, recoveredExpressionCapture_419_1, recoveredExpressionCapture_419_2, recoveredExpressionCapture_419_3, recoveredExpressionCapture_419_4, recoveredExpressionCapture_419_5, recoveredExpressionCapture_419_6, recoveredExpressionCapture_419_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_419_0), ((java.lang.Integer)recoveredExpressionArgument_419_1).intValue())));
      }
   }

   private static final Unit ConversationTopAppBar$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationTopAppBar$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationTopAppBar$lambda$5$lambda$4(HeaderMenuItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationTopAppBar$lambda$7$lambda$6(MetricData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationTopAppBar$lambda$10(
      TopAppBarUiState var0, Function0 var1, Function0 var2, Function0 var3, Function1 var4, Function1 var5, int var6, int var7, Composer var8, int var9
   ) {
      ConversationTopAppBar(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }
}
