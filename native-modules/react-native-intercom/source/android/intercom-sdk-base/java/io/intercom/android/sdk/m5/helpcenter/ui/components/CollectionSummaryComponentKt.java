package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
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
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.sections.Author;
import io.intercom.android.sdk.m5.components.AvatarGroupKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\u001e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0002\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000f¨\u0006\u0010",
   d2 = {
         "CollectionSummaryComponent",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "constructByAuthorsText",
         "",
         "context",
         "Landroid/content/Context;",
         "authors",
         "",
         "Lio/intercom/android/sdk/helpcenter/sections/Author;",
         "CollectionSummaryComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCollectionSummaryComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CollectionSummaryComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/CollectionSummaryComponentKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,147:1\n75#2:148\n87#3:149\n84#3,9:150\n87#3:182\n84#3,9:183\n87#3:244\n83#3,10:245\n94#3:280\n94#3:293\n94#3:297\n81#4,6:159\n88#4,6:174\n81#4,6:192\n88#4,6:207\n81#4,6:222\n88#4,6:237\n81#4,6:255\n88#4,6:270\n96#4:279\n96#4:288\n96#4:292\n96#4:296\n402#5,9:165\n411#5:180\n402#5,9:198\n411#5:213\n402#5,9:228\n411#5:243\n402#5,9:261\n411#5,3:276\n412#5,2:286\n412#5,2:290\n412#5,2:294\n118#6:181\n118#6:214\n118#6:215\n118#6:285\n99#7,6:216\n106#7:289\n1563#8:281\n1634#8,3:282\n*S KotlinDebug\n*F\n+ 1 CollectionSummaryComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/CollectionSummaryComponentKt\n*L\n36#1:148\n37#1:149\n37#1:150,9\n42#1:182\n42#1:183,9\n64#1:244\n64#1:245,10\n64#1:280\n42#1:293\n37#1:297\n37#1:159,6\n37#1:174,6\n42#1:192,6\n42#1:207,6\n59#1:222,6\n59#1:237,6\n64#1:255,6\n64#1:270,6\n64#1:279\n59#1:288\n42#1:292\n37#1:296\n37#1:165,9\n37#1:180\n42#1:198,9\n42#1:213\n59#1:228,9\n59#1:243\n64#1:261,9\n64#1:276,3\n59#1:286,2\n42#1:290,2\n37#1:294,2\n43#1:181\n51#1:214\n58#1:215\n88#1:285\n59#1:216,6\n59#1:289\n77#1:281\n77#1:282,3\n*E\n"
)
public final class CollectionSummaryComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CollectionSummaryComponent(
      @NotNull CollectionDetailsUiState$Content var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(-2122142392);
      ComposerKt.sourceInformation(var2, "C(CollectionSummaryComponent)P(1)35@1540L7,39@1654L6,36@1552L2095:CollectionSummaryComponent.kt#ngglre");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changedInstance(var0) ? 4 : 2;
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
            ComposerKt.traceEventStart(
               -2122142392, var5, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionSummaryComponent (CollectionSummaryComponent.kt:34)"
            );
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var6 = (Context)var10;
         Modifier var124 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         byte var127 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var125 = Arrangement.INSTANCE.getTop();
         Horizontal var126 = Alignment.Companion.getStart();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var125, var126, var2, 14 & var127 >> 3 | 112 & var127 >> 3);
         Modifier var13 = var124;
         MeasurePolicy var14 = var12;
         Composer var15 = var2;
         int var16 = 112 & var127 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var127 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -108574485, "C41@1689L1926,91@3624L17:CollectionSummaryComponent.kt#ngglre");
         Modifier var10000 = Modifier.Companion;
         int var34 = 16;
         int var35 = 0;
         Modifier var128 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var34));
         byte var36 = 6;
         int $i$f$Columnx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, horizontalAlignment$ivx, var31, 14 & var36 >> 3 | 112 & var36 >> 3
         );
         Modifier var40 = var128;
         MeasurePolicy var41 = measurePolicy$ivx;
         Composer var42 = var31;
         int var43 = 112 & var36 << 3;
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
         int var56/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var57 = 6 | 112 & var36 >> 6;
         Composer var58 = $composer$ivx;
         ColumnScope var59 = ColumnScopeInstance.INSTANCE;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var58, -85254149, "C45@1808L10,47@1909L6,44@1764L179,57@2285L41,58@2339L1266:CollectionSummaryComponent.kt#ngglre"
         );
         TextStyle var61 = IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04SemiBold();
         String var62 = var0.getTitle();
         long var63 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(var62, null, var63, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var61, var58, 0, 0, 131066);
         var58.startReplaceGroup(2075464467);
         ComposerKt.sourceInformation(var58, "50@2006L40,52@2111L10,54@2220L6,51@2063L195");
         if (!StringsKt.isBlank(var0.getSummary())) {
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var58, 6);
            var61 = IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04Point5();
            var62 = var0.getSummary();
            var63 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            TextKt.recovered$5b114ab32a4f4190(
               var62, null, var63, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var61, var58, 0, 0, 131066
            );
         }

         var58.endReplaceGroup();
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 20;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var58, 6);
         Modifier var133 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.foundation.layout.Arrangement.Horizontal var137 = Arrangement.INSTANCE.getSpaceBetween();
         androidx.compose.ui.Alignment.Vertical var65 = Alignment.Companion.getCenterVertically();
         short var66 = 438;
         int var67 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var58, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var137, var65, var58, 14 & var66 >> 3 | 112 & var66 >> 3);
         Modifier var69 = var133;
         MeasurePolicy var70 = measurePolicy$ivxx;
         Composer var71 = var58;
         int var72 = 112 & var66 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var71, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var71, 0));
         CompositionLocalMap localMap$iv$ivxx = var71.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var71, var69);
         Function0 var77 = ComposeUiNode.Companion.getConstructor();
         Composer var78 = var71;
         int var79 = 6 | 896 & var72 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var78, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var78.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var78.startReusableNode();
         if (var78.getInserting()) {
            var78.createNode(var77);
         } else {
            var78.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var78);
         int var82/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var70, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var83 = 14 & var79 >> 6;
         Composer $composer$ivxx = var78;
         int var85 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var86 = 6 | 112 & var66 >> 6;
         Composer var87 = $composer$ivxx;
         RowScope var88 = RowScopeInstance.INSTANCE;
         int var89 = 0;
         ComposerKt.sourceInformationMarkerStart(var87, 1651030852, "C63@2558L451,85@3482L109:CollectionSummaryComponent.kt#ngglre");
         int $changed$ivx = 0;
         int $i$f$Columnxx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var87, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var92 = Modifier.Companion;
         Vertical verticalArrangement$ivxx = Arrangement.INSTANCE.getTop();
         Horizontal horizontalAlignment$ivxx = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxxx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivxx, horizontalAlignment$ivxx, var87, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3
         );
         Modifier var96 = var92;
         MeasurePolicy var97 = measurePolicy$ivxxx;
         Composer var98 = var87;
         int var99 = 112 & $changed$ivx << 3;
         int $i$f$Layoutxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var98, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var98, 0));
         CompositionLocalMap localMap$iv$ivxxx = var98.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var98, var96);
         Function0 var104 = ComposeUiNode.Companion.getConstructor();
         Composer var105 = var98;
         int var106 = 6 | 896 & var99 << 6;
         int $i$f$ReusableComposeNodexxx = 0;
         ComposerKt.sourceInformationMarkerStart(var105, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var105.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var105.startReusableNode();
         if (var105.getInserting()) {
            var105.createNode(var104);
         } else {
            var105.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var105);
         int var109/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var97, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
         int var110 = 14 & var106 >> 6;
         Composer $composer$ivxxx = var105;
         int var112/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivxx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var113 = 6 | 112 & $changed$ivx >> 6;
         Composer var114 = $composer$ivxxx;
         ColumnScope var115 = ColumnScopeInstance.INSTANCE;
         int var116 = 0;
         ComposerKt.sourceInformationMarkerStart(var114, 1984712282, "C64@2587L97,70@2894L10,67@2705L286:CollectionSummaryComponent.kt#ngglre");
         ArticleCountComponentKt.ArticleCountComponent(null, var0.getArticlesCount(), var114, 0, 1);
         String var117 = constructByAuthorsText(var6, var0.getAuthors());
         int var118 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextStyle var119 = IntercomTheme.INSTANCE.getTypography(var114, IntercomTheme.$stable).getType04Point5();
         long var120 = ColorKt.Color(4285887861L);
         TextKt.recovered$5b114ab32a4f4190(
            var117, null, var120, null, 0L, null, null, null, 0L, null, null, 0L, var118, false, 0, 0, null, var119, var114, 384, 384, 126970
         );
         ComposerKt.sourceInformationMarkerEnd(var114);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
         var105.endNode();
         ComposerKt.sourceInformationMarkerEnd(var105);
         ComposerKt.sourceInformationMarkerEnd(var98);
         ComposerKt.sourceInformationMarkerEnd(var87);
         Iterable var142 = CollectionsKt.take(var0.getAuthors(), 3);
         int var144 = 0;
         Iterable var139 = var142;
         ArrayList var140 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var142, 10));
         int var146 = 0;

         for (Object var148 : var139) {
            Author var149 = (Author)var148;
            Collection var122 = var140;
            var99 = 0;
            Avatar var10002 = Avatar.create(var149.getAvatar().getImageUrl(), var149.getAvatar().getInitials());
            Intrinsics.checkNotNullExpressionValue(var10002, "create(...)");
            var122.add(new AvatarWrapper(var10002, false));
         }

         List var141 = (List)var140;
         int $this$dp$ivxx = 32;
         int $i$f$getDpxx = 0;
         AvatarGroupKt.recovered$813c81fac421be56(var141, null, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0L, var87, 384, 10);
         ComposerKt.sourceInformationMarkerEnd(var87);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var78.endNode();
         ComposerKt.sourceInformationMarkerEnd(var78);
         ComposerKt.sourceInformationMarkerEnd(var71);
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var49.endNode();
         ComposerKt.sourceInformationMarkerEnd(var49);
         ComposerKt.sourceInformationMarkerEnd(var42);
         ComposerKt.sourceInformationMarkerEnd(var31);
         IntercomDividerKt.IntercomDivider(null, var31, 0, 1);
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var153 = var2.endRestartGroup();
      if (var153 != null) {
         final io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content recoveredCapture_870_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_870_1 = var1;
         final int recoveredCapture_870_2 = var3;
         final int recoveredCapture_870_3 = var4;
         var153.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_870_0, recoveredArgument_870_1) -> CollectionSummaryComponentKt.CollectionSummaryComponent$lambda$5(recoveredCapture_870_0, recoveredCapture_870_1, recoveredCapture_870_2, recoveredCapture_870_3, (androidx.compose.runtime.Composer)recoveredArgument_870_0, ((java.lang.Integer)recoveredArgument_870_1).intValue())));
      }
   }

   private static final String constructByAuthorsText(Context var0, List var1) {
      if (var1.isEmpty()) {
         return "";
      }

      CharSequence var10000;
      switch (var1.size()) {
         case 1:
            var10000 = Phrase.from(var0, string.intercom_article_single_author)
               .put("author_first_name", ((Author)CollectionsKt.first(var1)).getName())
               .format();
            break;
         case 2:
            var10000 = Phrase.from(var0, string.intercom_article_double_author)
               .put("author_first_name1", ((Author)CollectionsKt.first(var1)).getName())
               .put("author_first_name2", ((Author)CollectionsKt.last(var1)).getName())
               .format();
            break;
         default:
            var10000 = Phrase.from(var0, string.intercom_article_multiple_authors)
               .put("author_first_name1", ((Author)CollectionsKt.first(var1)).getName())
               .put("number_of_other_authors", var1.size() - 1)
               .format();
      }

      return var10000.toString();
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void CollectionSummaryComponentPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1054855652);
      ComposerKt.sourceInformation(var0, "C(CollectionSummaryComponentPreview)117@4525L1115:CollectionSummaryComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1054855652,
               var1,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionSummaryComponentPreview (CollectionSummaryComponent.kt:116)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$CollectionSummaryComponentKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_871_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_871_0, recoveredArgument_871_1) -> CollectionSummaryComponentKt.CollectionSummaryComponentPreview$lambda$6(recoveredCapture_871_0, (androidx.compose.runtime.Composer)recoveredArgument_871_0, ((java.lang.Integer)recoveredArgument_871_1).intValue())));
      }
   }

   private static final Unit CollectionSummaryComponent$lambda$5(
      CollectionDetailsUiState$Content var0, Modifier var1, int var2, int var3, Composer var4, int var5
   ) {
      CollectionSummaryComponent(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit CollectionSummaryComponentPreview$lambda$6(int var0, Composer var1, int var2) {
      CollectionSummaryComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
