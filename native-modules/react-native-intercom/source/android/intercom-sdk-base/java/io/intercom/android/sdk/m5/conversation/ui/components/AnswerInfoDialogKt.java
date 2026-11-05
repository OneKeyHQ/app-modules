package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.window.AndroidDialog_androidKt;
import io.intercom.android.sdk.models.AiAnswerInfo;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a=\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\t\u001a-\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0003¢\u0006\u0002\u0010\u0010\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012¨\u0006\u0014",
   d2 = {
         "AnswerInfoDialog",
         "",
         "info",
         "Lio/intercom/android/sdk/models/AiAnswerInfo;",
         "showReportAiAnswerButton",
         "",
         "onDismiss",
         "Lkotlin/Function0;",
         "onReportAiAnswer",
         "(Lio/intercom/android/sdk/models/AiAnswerInfo;ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "AnswerInfoButton",
         "text",
         "",
         "onClick",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "AnswerInfoDialogPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "AnswerInfoDialogWithoutReportButtonPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAnswerInfoDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AnswerInfoDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/AnswerInfoDialogKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,110:1\n1047#2,6:111\n1047#2,6:117\n1047#2,6:123\n118#3:129\n99#4,6:130\n106#4:161\n81#5,6:136\n88#5,6:151\n96#5:160\n402#6,9:142\n411#6,3:157\n*S KotlinDebug\n*F\n+ 1 AnswerInfoDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/AnswerInfoDialogKt\n*L\n29#1:111,6\n30#1:117,6\n71#1:123,6\n73#1:129\n69#1:130,6\n69#1:161\n69#1:136,6\n69#1:151,6\n69#1:160\n69#1:142,9\n69#1:157,3\n*E\n"
)
public final class AnswerInfoDialogKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AnswerInfoDialog(
      @NotNull AiAnswerInfo var0, boolean var1, @Nullable Function0 var2, @Nullable Function0 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "info");
      Composer var15 = var4.startRestartGroup(-1331432837);
      ComposerKt.sourceInformation(var15, "C(AnswerInfoDialog)P(!1,3)28@1177L2,29@1216L2,33@1292L967,31@1228L1038:AnswerInfoDialog.kt#o19g89");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var15.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var15.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var15.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var15.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var15.getSkipping()) {
         var15.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var15.startReplaceGroup(1808890605);
            ComposerKt.sourceInformation(var15, "CC(remember):AnswerInfoDialog.kt#9igjgp");
            Composer var9 = var15;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var22 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoDialog$lambda$1$lambda$0());
               var9.updateRememberedValue(var22);
               var10000 = var22;
            } else {
               var10000 = var12;
            }

            Function0 var8 = (Function0)var10000;
            var15.endReplaceGroup();
            var2 = var8;
         }

         if ((var6 & 8) != 0) {
            var15.startReplaceGroup(1808891853);
            ComposerKt.sourceInformation(var15, "CC(remember):AnswerInfoDialog.kt#9igjgp");
            Composer var17 = var15;
            boolean var18 = false;
            int var19 = 0;
            Object var20 = var17.rememberedValue();
            int var21 = 0;
            Object var25;
            if (var20 == Composer.Companion.getEmpty()) {
               int var23 = 0;
               Object var24 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoDialog$lambda$3$lambda$2());
               var17.updateRememberedValue(var24);
               var25 = var24;
            } else {
               var25 = var20;
            }

            Function0 var16 = (Function0)var25;
            var15.endReplaceGroup();
            var3 = var16;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1331432837, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialog (AnswerInfoDialog.kt:30)");
         }

         AndroidDialog_androidKt.Dialog(
            var2,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1632439278, true, new AnswerInfoDialogKt$AnswerInfoDialog$3(var0, var1, var2, var3), var15, 54),
            var15,
            384 | 14 & var7 >> 6,
            2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var26 = var15.endRestartGroup();
      if (var26 != null) {
         final io.intercom.android.sdk.models.AiAnswerInfo recoveredExpressionCapture_402_0 = var0;
         final boolean recoveredExpressionCapture_402_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_402_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_402_3 = var3;
         final int recoveredExpressionCapture_402_4 = var5;
         final int recoveredExpressionCapture_402_5 = var6;
         var26.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_402_0, recoveredExpressionArgument_402_1) -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoDialog$lambda$4(recoveredExpressionCapture_402_0, recoveredExpressionCapture_402_1, recoveredExpressionCapture_402_2, recoveredExpressionCapture_402_3, recoveredExpressionCapture_402_4, recoveredExpressionCapture_402_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_402_0), ((java.lang.Integer)recoveredExpressionArgument_402_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AnswerInfoButton(String var0, Function0 var1, Modifier var2, Composer var3, int var4, int var5) {
      Composer var43 = var3.startRestartGroup(8337655);
      ComposerKt.sourceInformation(var43, "C(AnswerInfoButton)P(2,1)70@2454L13,68@2398L475:AnswerInfoDialog.kt#o19g89");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var43.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var43.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 19) == 18 && var43.getSkipping()) {
         var43.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(8337655, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoButton (AnswerInfoDialog.kt:67)");
         }

         Modifier var10000 = Modifier.Companion;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var43.startReplaceGroup(934769154);
         ComposerKt.sourceInformation(var43, "CC(remember):AnswerInfoDialog.kt#9igjgp");
         Composer var9 = var43;
         boolean var10 = (var6 & 112) == 32;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10005;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var10005 = var12;
         } else {
            Object var41 = null;
            Object var40 = null;
            Object var39 = null;
            boolean var38 = false;
            Modifier var37 = var10000;
            int var14 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_398_0 = var1;
            Function0 var42 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoButton$lambda$6$lambda$5(recoveredExpressionCapture_398_0));
            var10000 = var37;
            var10001 = var38;
            var10002 = var39;
            var10003 = var40;
            var10004 = var41;
            Object var15 = var42;
            var9.updateRememberedValue(var15);
            var10005 = var15;
         }

         Function0 var8 = (Function0)var10005;
         var43.endReplaceGroup();
         Modifier var56 = SizeKt.fillMaxWidth$default(
            ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var8, 15, null),
            0.0F,
            1,
            null
         );
         int var44 = 24;
         int var47 = 0;
         float var58 = Dp.recovered$95b3d103663bf060(var44);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var7 = PaddingKt.recovered$904403cea4c49d95(var56, var58, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Vertical var46 = Alignment.Companion.getCenterVertically();
         HorizontalOrVertical var49 = Arrangement.INSTANCE.getCenter();
         short var50 = 432;
         int var51 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var43, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var52 = RowKt.rowMeasurePolicy(var49, var46, var43, 14 & var50 >> 3 | 112 & var50 >> 3);
         Modifier var53 = var7;
         MeasurePolicy var54 = var52;
         Composer var55 = var43;
         int var16 = 112 & var50 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var55, 0));
         CompositionLocalMap var19 = var55.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var55, var53);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var55;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var54, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var50 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 788169844, "C78@2745L10,79@2837L6,76@2680L187:AnswerInfoDialog.kt#o19g89");
         TextStyle var34 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04(),
            0L,
            0L,
            FontWeight.Companion.getSemiBold(),
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
            16777211,
            null
         );
         long var35 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var0, null, var35, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var34, var31, 14 & var6, 0, 131066
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd(var43);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var57 = var43.endRestartGroup();
      if (var57 != null) {
         final java.lang.String recoveredExpressionCapture_399_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_399_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_399_2 = var2;
         final int recoveredExpressionCapture_399_3 = var4;
         final int recoveredExpressionCapture_399_4 = var5;
         var57.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_399_0, recoveredExpressionArgument_399_1) -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoButton$lambda$8(recoveredExpressionCapture_399_0, recoveredExpressionCapture_399_1, recoveredExpressionCapture_399_2, recoveredExpressionCapture_399_3, recoveredExpressionCapture_399_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_399_0), ((java.lang.Integer)recoveredExpressionArgument_399_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AnswerInfoDialogPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1630534767);
      ComposerKt.sourceInformation(var2, "C(AnswerInfoDialogPreview)87@2952L263:AnswerInfoDialog.kt#o19g89");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1630534767, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogPreview (AnswerInfoDialog.kt:86)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$AnswerInfoDialogKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_403_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_403_0, recoveredExpressionArgument_403_1) -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoDialogPreview$lambda$9(recoveredExpressionCapture_403_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_403_0), ((java.lang.Integer)recoveredExpressionArgument_403_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AnswerInfoDialogWithoutReportButtonPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-916076999);
      ComposerKt.sourceInformation(var2, "C(AnswerInfoDialogWithoutReportButtonPreview)100@3313L264:AnswerInfoDialog.kt#o19g89");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -916076999,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogWithoutReportButtonPreview (AnswerInfoDialog.kt:99)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$AnswerInfoDialogKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_404_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_404_0, recoveredExpressionArgument_404_1) -> io.intercom.android.sdk.m5.conversation.ui.components.AnswerInfoDialogKt.AnswerInfoDialogWithoutReportButtonPreview$lambda$10(recoveredExpressionCapture_404_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_404_0), ((java.lang.Integer)recoveredExpressionArgument_404_1).intValue())));
      }
   }

   private static final Unit AnswerInfoDialog$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoDialog$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoDialog$lambda$4(
      AiAnswerInfo var0, boolean var1, Function0 var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      AnswerInfoDialog(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoButton$lambda$6$lambda$5(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoButton$lambda$8(String var0, Function0 var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      AnswerInfoButton(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoDialogPreview$lambda$9(int var0, Composer var1, int var2) {
      AnswerInfoDialogPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AnswerInfoDialogWithoutReportButtonPreview$lambda$10(int var0, Composer var1, int var2) {
      AnswerInfoDialogWithoutReportButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$AnswerInfoButton(String var0, Function0 var1, Modifier var2, Composer var3, int var4, int var5) {
      AnswerInfoButton(var0, var1, var2, var3, var4, var5);
   }
}
