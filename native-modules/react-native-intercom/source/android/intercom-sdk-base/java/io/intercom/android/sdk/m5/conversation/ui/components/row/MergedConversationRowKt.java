package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a=\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b¨\u0006\f",
   d2 = {
         "MergedConversationRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "description",
         "",
         "conversationId",
         "onConversationClick",
         "Lkotlin/Function1;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "MergedConversationRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMergedConversationRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MergedConversationRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/MergedConversationRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,60:1\n1047#2,6:61\n1047#2,6:101\n118#3:67\n118#3:100\n87#4:68\n84#4,9:69\n94#4:110\n81#5,6:78\n88#5,6:93\n96#5:109\n402#6,9:84\n411#6:99\n412#6,2:107\n*S KotlinDebug\n*F\n+ 1 MergedConversationRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/MergedConversationRowKt\n*L\n25#1:61,6\n39#1:101,6\n28#1:67\n37#1:100\n27#1:68\n27#1:69,9\n27#1:110\n27#1:78,6\n27#1:93,6\n27#1:109\n27#1:84,9\n27#1:99\n27#1:107,2\n*E\n"
)
public final class MergedConversationRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MergedConversationRow(
      @Nullable Modifier var0, @NotNull String var1, @NotNull String var2, @Nullable Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "description");
      Intrinsics.checkNotNullParameter(var2, "conversationId");
      Composer var54 = var4.startRestartGroup(-635122177);
      ComposerKt.sourceInformation(var54, "C(MergedConversationRow)P(2,1)24@960L2,26@972L807:MergedConversationRow.kt#hr3bi5");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var54.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var54.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var54.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var54.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var54.getSkipping()) {
         var54.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 8) != 0) {
            var54.startReplaceGroup(-98469438);
            ComposerKt.sourceInformation(var54, "CC(remember):MergedConversationRow.kt#9igjgp");
            Composer var9 = var54;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var63 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_757_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowKt.MergedConversationRow$lambda$1$lambda$0((java.lang.String) ((java.lang.String)recoveredExpressionArgument_757_0)));
               var9.updateRememberedValue(var63);
               var10000 = var63;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var54.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -635122177, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRow (MergedConversationRow.kt:25)"
            );
         }

         int var55 = 16;
         int var57 = 0;
         Modifier var56 = PaddingKt.recovered$f3b9f2e94c07c806(var0, Dp.recovered$95b3d103663bf060(var55), 0.0F, 2, null);
         Horizontal var59 = Alignment.Companion.getCenterHorizontally();
         short var60 = 384;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var54, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var58 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var62 = ColumnKt.columnMeasurePolicy(var58, var59, var54, 14 & var60 >> 3 | 112 & var60 >> 3);
         Modifier var64 = var56;
         MeasurePolicy var15 = var62;
         Composer var16 = var54;
         int var17 = 112 & var60 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var64);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var31 = 6 | 112 & var60 >> 6;
         Composer var32 = var29;
         ColumnScope var33 = ColumnScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32,
            -554697488,
            "C32@1185L10,34@1286L6,30@1113L201,36@1323L40,38@1420L67,41@1508L56,42@1600L10,44@1736L6,37@1372L401:MergedConversationRow.kt#hr3bi5"
         );
         TextStyle var35 = IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType04Point5();
         int var36 = TextAlign.Companion.recovered$d707b9dd64876179();
         long var37 = IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextKt.recovered$5b114ab32a4f4190(
            var1,
            null,
            var37,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var36),
            0L,
            0,
            false,
            0,
            0,
            null,
            var35,
            var32,
            14 & var7 >> 3,
            0,
            130042
         );
         Modifier var72 = Modifier.Companion;
         int $this$dp$ivx = 4;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var72, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var32, 6);
         Modifier var73 = Modifier.Companion;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var32.startReplaceGroup(-1680452222);
         ComposerKt.sourceInformation(var32, "CC(remember):MergedConversationRow.kt#9igjgp");
         Composer var39 = var32;
         boolean var40 = (var7 & 7168) == 2048 | (var7 & 896) == 256;
         int var41 = 0;
         Object var42 = var39.rememberedValue();
         int var43 = 0;
         Object var10005;
         if (!var40 && var42 != Composer.Companion.getEmpty()) {
            var10005 = var42;
         } else {
            Object var44 = null;
            Object var45 = null;
            Object var46 = null;
            boolean var47 = false;
            Modifier var48 = var73;
            int var49 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_758_0 = var3;
            final java.lang.String recoveredExpressionCapture_758_1 = var2;
            Function0 var50 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowKt.MergedConversationRow$lambda$4$lambda$3$lambda$2(recoveredExpressionCapture_758_0, recoveredExpressionCapture_758_1));
            var73 = var48;
            var10001 = var47;
            var10002 = var46;
            var10003 = var45;
            var10004 = var44;
            Object var51 = var50;
            var39.updateRememberedValue(var51);
            var10005 = var51;
         }

         Function0 var68 = (Function0)var10005;
         var32.endReplaceGroup();
         Modifier var66 = ClickableKt.recovered$1c9660c41fd850dc(
            var73, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var68, 15, null
         );
         String var69 = StringResources_androidKt.stringResource(string.intercom_view_conversation, var32, 0);
         TextStyle var70 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType04Point5(),
            0L,
            0L,
            FontWeight.Companion.getBold(),
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
         int var71 = TextAlign.Companion.recovered$d707b9dd64876179();
         long var52 = IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
         TextKt.recovered$5b114ab32a4f4190(
            var69,
            var66,
            var52,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var71),
            0L,
            0,
            false,
            0,
            0,
            null,
            var70,
            var32,
            0,
            0,
            130040
         );
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var54);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var74 = var54.endRestartGroup();
      if (var74 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_759_0 = var0;
         final java.lang.String recoveredExpressionCapture_759_1 = var1;
         final java.lang.String recoveredExpressionCapture_759_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_759_3 = var3;
         final int recoveredExpressionCapture_759_4 = var5;
         final int recoveredExpressionCapture_759_5 = var6;
         var74.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_759_0, recoveredExpressionArgument_759_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowKt.MergedConversationRow$lambda$5(recoveredExpressionCapture_759_0, recoveredExpressionCapture_759_1, recoveredExpressionCapture_759_2, recoveredExpressionCapture_759_3, recoveredExpressionCapture_759_4, recoveredExpressionCapture_759_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_759_0), ((java.lang.Integer)recoveredExpressionArgument_759_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MergedConversationRowPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-243500111);
      ComposerKt.sourceInformation(var2, "C(MergedConversationRowPreview)52@1862L167:MergedConversationRow.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -243500111, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowPreview (MergedConversationRow.kt:51)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$MergedConversationRowKt.INSTANCE.recovered$eac478aa65045e5d(),
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
         final int recoveredExpressionCapture_760_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_760_0, recoveredExpressionArgument_760_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.MergedConversationRowKt.MergedConversationRowPreview$lambda$6(recoveredExpressionCapture_760_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_760_0), ((java.lang.Integer)recoveredExpressionArgument_760_1).intValue())));
      }
   }

   private static final Unit MergedConversationRow$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit MergedConversationRow$lambda$4$lambda$3$lambda$2(Function1 var0, String var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit MergedConversationRow$lambda$5(
      Modifier var0, String var1, String var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      MergedConversationRow(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit MergedConversationRowPreview$lambda$6(int var0, Composer var1, int var2) {
      MergedConversationRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
