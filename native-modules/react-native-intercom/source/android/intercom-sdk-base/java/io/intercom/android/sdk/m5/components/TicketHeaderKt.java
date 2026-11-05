package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
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
   d1 = "\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a)\u0010\u0007\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0010",
   d2 = {
         "TicketHeader",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "args",
         "Lio/intercom/android/sdk/m5/components/TicketStatusHeaderArgs;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/components/TicketStatusHeaderArgs;Landroidx/compose/runtime/Composer;II)V",
         "SimpleTicketHeader",
         "text",
         "",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Landroidx/compose/ui/text/font/FontWeight;Landroidx/compose/runtime/Composer;II)V",
         "TicketHeaderPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TicketHeaderLongTextPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketHeader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketHeader.kt\nio/intercom/android/sdk/m5/components/TicketHeaderKt\n+ 2 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,103:1\n99#2:104\n96#2,9:105\n106#2:141\n81#3,6:114\n88#3,6:129\n96#3:140\n402#4,9:120\n411#4:135\n412#4,2:138\n118#5:136\n118#5:137\n*S KotlinDebug\n*F\n+ 1 TicketHeader.kt\nio/intercom/android/sdk/m5/components/TicketHeaderKt\n*L\n41#1:104\n41#1:105,9\n41#1:141\n41#1:114,6\n41#1:129,6\n41#1:140\n41#1:120,9\n41#1:135\n41#1:138,2\n46#1:136\n51#1:137\n*E\n"
)
public final class TicketHeaderKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketHeader(@Nullable Modifier var0, @NotNull TicketStatusHeaderArgs var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "args");
      var2 = var2.startRestartGroup(-254158349);
      ComposerKt.sourceInformation(var2, "C(TicketHeader)P(1)27@1008L119:TicketHeader.kt#arqjag");
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
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-254158349, var5, -1, "io.intercom.android.sdk.m5.components.TicketHeader (TicketHeader.kt:26)");
         }

         SimpleTicketHeader(var0, var1.getTitle(), var1.getFontWeight(), var2, 14 & var5, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_250_0 = var0;
         final io.intercom.android.sdk.m5.components.TicketStatusHeaderArgs recoveredCapture_250_1 = var1;
         final int recoveredCapture_250_2 = var3;
         final int recoveredCapture_250_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_250_0, recoveredArgument_250_1) -> TicketHeaderKt.TicketHeader$lambda$0(recoveredCapture_250_0, recoveredCapture_250_1, recoveredCapture_250_2, recoveredCapture_250_3, (androidx.compose.runtime.Composer)recoveredArgument_250_0, ((java.lang.Integer)recoveredArgument_250_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SimpleTicketHeader(
      @Nullable Modifier var0, @NotNull String var1, @Nullable FontWeight var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "text");
      var3 = var3.startRestartGroup(-921635597);
      ComposerKt.sourceInformation(var3, "C(SimpleTicketHeader)P(1,2)40@1285L705:TicketHeader.kt#arqjag");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
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

         if ((var5 & 4) != 0) {
            var2 = FontWeight.Companion.getNormal();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-921635597, var6, -1, "io.intercom.android.sdk.m5.components.SimpleTicketHeader (TicketHeader.kt:39)");
         }

         Vertical var7 = Alignment.Companion.getCenterVertically();
         Modifier var8 = SizeKt.wrapContentSize$default(var0, null, false, 3, null);
         short var10 = 384;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var9 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var12 = RowKt.rowMeasurePolicy(var9, var7, var3, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var8;
         MeasurePolicy var14 = var12;
         Composer var15 = var3;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
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
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 685154068, "C46@1482L60,48@1616L6,44@1409L235,50@1653L39,53@1766L10,54@1828L6,51@1701L283:TicketHeader.kt#arqjag"
         );
         Modifier var10000 = Modifier.Companion;
         int var34 = 16;
         int var35 = 0;
         Modifier var36 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var34));
         Painter var40 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_detail_icon, var31, 0);
         long var37 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122();
         IconKt.recovered$e00adffb7fe45b02(var40, null, var36, var37, var31, 432 | Painter.$stable, 0);
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 4;
         var34 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var31, 6);
         TextStyle var44 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04(),
            IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$0aef7413383f6122(),
            0L,
            var2,
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
            16777210,
            null
         );
         var34 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var1, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var34, false, 1, 0, null, var44, var31, 14 & var6 >> 3, 24960, 110590
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var46 = var3.endRestartGroup();
      if (var46 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_249_0 = var0;
         final java.lang.String recoveredCapture_249_1 = var1;
         final androidx.compose.ui.text.font.FontWeight recoveredCapture_249_2 = var2;
         final int recoveredCapture_249_3 = var4;
         final int recoveredCapture_249_4 = var5;
         var46.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_249_0, recoveredArgument_249_1) -> TicketHeaderKt.SimpleTicketHeader$lambda$2(recoveredCapture_249_0, recoveredCapture_249_1, recoveredCapture_249_2, recoveredCapture_249_3, recoveredCapture_249_4, (androidx.compose.runtime.Composer)recoveredArgument_249_0, ((java.lang.Integer)recoveredArgument_249_1).intValue())));
      }
   }

   @Container({@Preview(uiMode = 16), @Preview(uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TicketHeaderPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1418431454);
      ComposerKt.sourceInformation(var0, "C(TicketHeaderPreview)75@2351L143,75@2337L157:TicketHeader.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1418431454, var1, -1, "io.intercom.android.sdk.m5.components.TicketHeaderPreview (TicketHeader.kt:71)");
         }

         TicketStatusHeaderArgs var2 = new TicketStatusHeaderArgs("Bug", null, 2, null);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(10965256, true, new TicketHeaderKt$TicketHeaderPreview$1(var2), var0, 54),
            var0,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_252_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_252_0, recoveredArgument_252_1) -> TicketHeaderKt.TicketHeaderPreview$lambda$3(recoveredCapture_252_0, (androidx.compose.runtime.Composer)recoveredArgument_252_0, ((java.lang.Integer)recoveredArgument_252_1).intValue())));
      }
   }

   @Container({@Preview(uiMode = 16), @Preview(uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TicketHeaderLongTextPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1234985657);
      ComposerKt.sourceInformation(var0, "C(TicketHeaderLongTextPreview)91@2768L183,91@2754L197:TicketHeader.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1234985657, var1, -1, "io.intercom.android.sdk.m5.components.TicketHeaderLongTextPreview (TicketHeader.kt:87)");
         }

         TicketStatusHeaderArgs var2 = new TicketStatusHeaderArgs("Very long long long long title", null, 2, null);
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(337942641, true, new TicketHeaderKt$TicketHeaderLongTextPreview$1(var2), var0, 54),
            var0,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_251_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_251_0, recoveredArgument_251_1) -> TicketHeaderKt.TicketHeaderLongTextPreview$lambda$4(recoveredCapture_251_0, (androidx.compose.runtime.Composer)recoveredArgument_251_0, ((java.lang.Integer)recoveredArgument_251_1).intValue())));
      }
   }

   private static final Unit TicketHeader$lambda$0(Modifier var0, TicketStatusHeaderArgs var1, int var2, int var3, Composer var4, int var5) {
      TicketHeader(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit SimpleTicketHeader$lambda$2(Modifier var0, String var1, FontWeight var2, int var3, int var4, Composer var5, int var6) {
      SimpleTicketHeader(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit TicketHeaderPreview$lambda$3(int var0, Composer var1, int var2) {
      TicketHeaderPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TicketHeaderLongTextPreview$lambda$4(int var0, Composer var1, int var2) {
      TicketHeaderLongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
