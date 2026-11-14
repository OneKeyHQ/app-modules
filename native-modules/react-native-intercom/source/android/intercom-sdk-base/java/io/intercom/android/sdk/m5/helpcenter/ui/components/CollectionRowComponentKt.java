package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
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
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.IntercomChevronKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionRowData;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a3\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b¨\u0006\r",
   d2 = {
         "CollectionRowComponent",
         "",
         "rowData",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionRowData;",
         "onClick",
         "Lkotlin/Function1;",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/helpcenter/states/CollectionRowData;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "RowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "RowWithNoDescriptionPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCollectionRowComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/CollectionRowComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,107:1\n1047#2,6:108\n118#3:114\n118#3:212\n118#3:213\n118#3:214\n118#3:219\n118#3:224\n87#4:115\n84#4,9:116\n87#4:180\n84#4,9:181\n94#4:218\n94#4:228\n81#5,6:125\n88#5,6:140\n81#5,6:158\n88#5,6:173\n81#5,6:190\n88#5,6:205\n96#5:217\n96#5:222\n96#5:227\n402#6,9:131\n411#6:146\n402#6,9:164\n411#6:179\n402#6,9:196\n411#6:211\n412#6,2:215\n412#6,2:220\n412#6,2:225\n99#7:147\n95#7,10:148\n106#7:223\n*S KotlinDebug\n*F\n+ 1 CollectionRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/CollectionRowComponentKt\n*L\n34#1:108,6\n35#1:114\n49#1:212\n56#1:213\n58#1:214\n62#1:219\n64#1:224\n31#1:115\n31#1:116,9\n40#1:180\n40#1:181,9\n40#1:218\n31#1:228\n31#1:125,6\n31#1:140,6\n37#1:158,6\n37#1:173,6\n40#1:190,6\n40#1:205,6\n40#1:217\n37#1:222\n31#1:227\n31#1:131,9\n31#1:146\n37#1:164,9\n37#1:179\n40#1:196,9\n40#1:211\n40#1:215,2\n37#1:220,2\n31#1:225,2\n37#1:147\n37#1:148,10\n37#1:223\n*E\n"
)
public final class CollectionRowComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CollectionRowComponent(
      @NotNull CollectionRowData var0, @NotNull Function1 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "rowData");
      Intrinsics.checkNotNullParameter(var1, "onClick");
      Composer var97 = var3.startRestartGroup(-471072842);
      ComposerKt.sourceInformation(var97, "C(CollectionRowComponent)P(2,1)33@1299L23,30@1212L1405:CollectionRowComponent.kt#ngglre");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var97.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var97.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var97.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var97.getSkipping()) {
         var97.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -471072842, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponent (CollectionRowComponent.kt:29)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var97.startReplaceGroup(1498669980);
         ComposerKt.sourceInformation(var97, "CC(remember):CollectionRowComponent.kt#9igjgp");
         Composer var9 = var97;
         boolean var10 = (var6 & 112) == 32 | (var6 & 14) == 4;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10005;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var10005 = var12;
         } else {
            Object var95 = null;
            Object var94 = null;
            Object var93 = null;
            boolean var92 = false;
            Modifier var91 = var10000;
            int var14 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_867_0 = var1;
            final io.intercom.android.sdk.m5.helpcenter.states.CollectionRowData recoveredExpressionCapture_867_1 = var0;
            Function0 var96 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt.CollectionRowComponent$lambda$1$lambda$0(recoveredExpressionCapture_867_0, recoveredExpressionCapture_867_1));
            var10000 = var91;
            var10001 = var92;
            var10002 = var93;
            var10003 = var94;
            var10004 = var95;
            Object var15 = var96;
            var9.updateRememberedValue(var15);
            var10005 = var15;
         }

         Function0 var8 = (Function0)var10005;
         var97.endReplaceGroup();
         Modifier var7 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var8, 15, null
         );
         int var101 = 16;
         int var104 = 0;
         float var99 = Dp.recovered$95b3d103663bf060(var101);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var102 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         Modifier var98 = PaddingKt.recovered$03b481d31d62885a(var7, var102, var99, 0.0F, 0.0F, 12, null);
         byte var106 = 0;
         int var108 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var97, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var100 = Arrangement.INSTANCE.getTop();
         Horizontal var103 = Alignment.Companion.getStart();
         MeasurePolicy var109 = ColumnKt.columnMeasurePolicy(var100, var103, var97, 14 & var106 >> 3 | 112 & var106 >> 3);
         Modifier var110 = var98;
         MeasurePolicy var111 = var109;
         Composer var112 = var97;
         int var16 = 112 & var106 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var112, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var112, 0));
         CompositionLocalMap var19 = var112.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var112, var110);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var112;
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
         Updater.recovered$89682b199a142880(var25, var111, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var106 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -232711367, "C36@1389L1143,63@2541L70:CollectionRowComponent.kt#ngglre");
         androidx.compose.ui.Alignment.Vertical var34 = Alignment.Companion.getCenterVertically();
         short var35 = 384;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var37 = Modifier.Companion;
         androidx.compose.foundation.layout.Arrangement.Horizontal var38 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var38, var34, var31, 14 & var35 >> 3 | 112 & var35 >> 3);
         Modifier var40 = var37;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & var35 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var42, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var42, 0));
         CompositionLocalMap localMap$iv$ivx = var42.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var42, var40);
         Function0 var48 = ComposeUiNode.Companion.getConstructor();
         Composer var49 = var42;
         int var50 = 6 | 896 & var43 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var49, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var49.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var49.startReusableNode();
         if (var49.getInserting()) {
            var49.createNode(var48);
         } else {
            var49.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var49);
         int var53/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var41, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var54 = 14 & var50 >> 6;
         Composer $composer$ivx = var49;
         int var56 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var57 = 6 | 112 & var35 >> 6;
         Composer var58 = $composer$ivx;
         RowScope var59 = RowScopeInstance.INSTANCE;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, 213578348, "C39@1478L967,61@2458L64:CollectionRowComponent.kt#ngglre");
         Modifier var61 = RowScope.weight$default(var59, Modifier.Companion, 1.0F, false, 2, null);
         byte var62 = 0;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var58, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, horizontalAlignment$ivx, var58, 14 & var62 >> 3 | 112 & var62 >> 3
         );
         Modifier var67 = var61;
         MeasurePolicy var68 = measurePolicy$ivxx;
         Composer var69 = var58;
         int var70 = 112 & var62 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var69, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var69, 0));
         CompositionLocalMap localMap$iv$ivxx = var69.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var69, var67);
         Function0 var75 = ComposeUiNode.Companion.getConstructor();
         Composer var76 = var69;
         int var77 = 6 | 896 & var70 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var76, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var76.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var76.startReusableNode();
         if (var76.getInserting()) {
            var76.createNode(var75);
         } else {
            var76.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var76);
         int var80/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var68, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var81 = 14 & var77 >> 6;
         Composer $composer$ivxx = var76;
         int var83/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var84 = 6 | 112 & var62 >> 6;
         Composer var85 = $composer$ivxx;
         ColumnScope var86 = ColumnScopeInstance.INSTANCE;
         int var87 = 0;
         ComposerKt.sourceInformationMarkerStart(var85, -516359136, "C44@1660L10,42@1566L190,59@2379L52:CollectionRowComponent.kt#ngglre");
         String var88 = var0.getTitleText();
         TextStyle var89 = IntercomTheme.INSTANCE.getTypography(var85, IntercomTheme.$stable).getType04Point5();
         FontWeight var90 = FontWeight.Companion.getSemiBold();
         TextKt.recovered$5b114ab32a4f4190(
            var88, null, 0L, null, 0L, null, var90, null, 0L, null, null, 0L, 0, false, 0, 0, null, var89, var85, 1572864, 0, 131006
         );
         if (var0.getDescriptionVisibility() == 0 && !StringsKt.isBlank(var0.getDescriptionText())) {
            var85.startReplaceGroup(-516077564);
            ComposerKt.sourceInformation(var85, "48@1886L40,51@2055L10,49@1947L250,55@2218L40");
            Modifier var129 = Modifier.Companion;
            int $this$dp$ivxx = 4;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var129, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var85, 6);
            String var121 = var0.getDescriptionText();
            TextStyle var125 = IntercomTheme.INSTANCE.getTypography(var85, IntercomTheme.$stable).getType04Point5();
            int var127 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var121, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var127, false, 3, 0, null, var125, var85, 0, 24960, 110590
            );
            Modifier var130 = Modifier.Companion;
            int $this$dp$ivxxx = 8;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var130, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var85, 6);
            var85.endReplaceGroup();
         } else {
            var85.startReplaceGroup(-515673200);
            ComposerKt.sourceInformation(var85, "57@2304L40");
            Modifier var128 = Modifier.Companion;
            int $this$dp$ivxx = 4;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var128, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var85, 6);
            var85.endReplaceGroup();
         }

         ArticleCountComponentKt.ArticleCountComponent(null, var0.getArticlesCount(), var85, 0, 1);
         ComposerKt.sourceInformationMarkerEnd(var85);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var76.endNode();
         ComposerKt.sourceInformationMarkerEnd(var76);
         ComposerKt.sourceInformationMarkerEnd(var69);
         ComposerKt.sourceInformationMarkerEnd(var58);
         Modifier var131 = Modifier.Companion;
         int $this$dp$ivxx = 22;
         int $i$f$getDpxx = 0;
         IntercomChevronKt.IntercomChevron(
            PaddingKt.recovered$f3b9f2e94c07c806(var131, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null), var58, 6, 0
         );
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var49.endNode();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var31);
         Modifier var132 = Modifier.Companion;
         int $this$dp$ivxxx = 16;
         int $i$f$getDpxxx = 0;
         float var134 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
         int $this$dp$ivxxxx = 16;
         int $i$f$getDpxxxx = 0;
         IntercomDividerKt.IntercomDivider(
            PaddingKt.recovered$03b481d31d62885a(var132, 0.0F, var134, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 9, null), var31, 6, 0
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var112);
         ComposerKt.sourceInformationMarkerEnd(var97);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var133 = var97.endRestartGroup();
      if (var133 != null) {
         final io.intercom.android.sdk.m5.helpcenter.states.CollectionRowData recoveredExpressionCapture_866_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_866_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_866_2 = var2;
         final int recoveredExpressionCapture_866_3 = var4;
         final int recoveredExpressionCapture_866_4 = var5;
         var133.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_866_0, recoveredExpressionArgument_866_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt.CollectionRowComponent$lambda$5(recoveredExpressionCapture_866_0, recoveredExpressionCapture_866_1, recoveredExpressionCapture_866_2, recoveredExpressionCapture_866_3, recoveredExpressionCapture_866_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_866_0), ((java.lang.Integer)recoveredExpressionArgument_866_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void RowPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2042373209);
      ComposerKt.sourceInformation(var2, "C(RowPreview)70@2683L547:CollectionRowComponent.kt#ngglre");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2042373209, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.RowPreview (CollectionRowComponent.kt:69)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$CollectionRowComponentKt.INSTANCE.recovered$1400af5e34386102(),
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
         final int recoveredExpressionCapture_868_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_868_0, recoveredExpressionArgument_868_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt.RowPreview$lambda$6(recoveredExpressionCapture_868_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_868_0), ((java.lang.Integer)recoveredExpressionArgument_868_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void RowWithNoDescriptionPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-613238090);
      ComposerKt.sourceInformation(var2, "C(RowWithNoDescriptionPreview)90@3313L460:CollectionRowComponent.kt#ngglre");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -613238090, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.RowWithNoDescriptionPreview (CollectionRowComponent.kt:89)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$CollectionRowComponentKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_869_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_869_0, recoveredExpressionArgument_869_1) -> io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt.RowWithNoDescriptionPreview$lambda$7(recoveredExpressionCapture_869_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_869_0), ((java.lang.Integer)recoveredExpressionArgument_869_1).intValue())));
      }
   }

   private static final Unit CollectionRowComponent$lambda$1$lambda$0(Function1 var0, CollectionRowData var1) {
      var0.invoke(var1.getId());
      return Unit.INSTANCE;
   }

   private static final Unit CollectionRowComponent$lambda$5(CollectionRowData var0, Function1 var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      CollectionRowComponent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit RowPreview$lambda$6(int var0, Composer var1, int var2) {
      RowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit RowWithNoDescriptionPreview$lambda$7(int var0, Composer var1, int var2) {
      RowWithNoDescriptionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
