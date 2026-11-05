package io.intercom.android.sdk.views.compose;

import android.content.res.Resources;
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
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.ColorResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.Attribute;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nAttributeCollectorCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/AttributeCollectorCardKt$AttributeCollectorCard$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,238:1\n118#2:239\n118#2:319\n118#2:352\n118#2:357\n118#2:364\n87#3:240\n84#3,9:241\n87#3:280\n83#3,10:281\n94#3:368\n94#3:372\n81#4,6:250\n88#4,6:265\n81#4,6:291\n88#4,6:306\n81#4,6:330\n88#4,6:345\n96#4:355\n96#4:367\n96#4:371\n402#5,9:256\n411#5:271\n402#5,9:297\n411#5:312\n402#5,9:336\n411#5:351\n412#5,2:353\n412#5,2:365\n412#5,2:369\n360#6,7:272\n1#7:279\n1047#8,6:313\n1047#8,6:358\n99#9:320\n96#9,9:321\n106#9:356\n85#10:373\n117#10,2:374\n*S KotlinDebug\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/AttributeCollectorCardKt$AttributeCollectorCard$2\n*L\n47#1:239\n64#1:319\n80#1:352\n94#1:357\n126#1:364\n46#1:240\n46#1:241,9\n55#1:280\n55#1:281,10\n55#1:368\n46#1:372\n46#1:250,6\n46#1:265,6\n55#1:291,6\n55#1:306,6\n66#1:330,6\n66#1:345,6\n66#1:355\n55#1:367\n46#1:371\n46#1:256,9\n46#1:271\n55#1:297,9\n55#1:312\n66#1:336,9\n66#1:351\n66#1:353,2\n55#1:365,2\n46#1:369,2\n49#1:272,7\n56#1:313,6\n119#1:358,6\n66#1:320\n66#1:321,9\n66#1:356\n56#1:373\n56#1:374,2\n*E\n"
)
final class AttributeCollectorCardKt$AttributeCollectorCard$2 implements Function3 {
   // $VF: synthetic field
   final List $attributes;
   // $VF: synthetic field
   final List $failedAttributeIdentifiers;
   // $VF: synthetic field
   final Resources $resources;
   // $VF: synthetic field
   final List $loadingAttributeIdentifiers;
   // $VF: synthetic field
   final String $partId;
   // $VF: synthetic field
   final boolean $isFormDisabled;
   // $VF: synthetic field
   final Function1 $onSubmitAttribute;

   AttributeCollectorCardKt$AttributeCollectorCard$2(List var1, List var2, Resources var3, List var4, String var5, boolean var6, Function1 var7) {
      this.$attributes = var1;
      this.$failedAttributeIdentifiers = var2;
      this.$resources = var3;
      this.$loadingAttributeIdentifiers = var4;
      this.$partId = var5;
      this.$isFormDisabled = var6;
      this.$onSubmitAttribute = var7;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C45@1824L4696:AttributeCollectorCard.kt#h0e4f2");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1007698855, var3, -1, "io.intercom.android.sdk.views.compose.AttributeCollectorCard.<anonymous> (AttributeCollectorCard.kt:45)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var4 = 24;
         int var5 = 0;
         Modifier var111 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var4));
         List var7 = this.$attributes;
         List var8 = this.$failedAttributeIdentifiers;
         Resources var9 = this.$resources;
         List var10 = this.$loadingAttributeIdentifiers;
         String var11 = this.$partId;
         boolean var12 = this.$isFormDisabled;
         Function1 var13 = this.$onSubmitAttribute;
         int var14 = 6;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var112 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var16 = ColumnKt.columnMeasurePolicy(var112, var6, var2, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var111;
         MeasurePolicy var18 = var16;
         Composer var19 = var2;
         int var20 = 112 & var14 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var17);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var34 = 6 | 112 & var14 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2039152950, "C:AttributeCollectorCard.kt#h0e4f2");
         List var38 = var7;
         int var39 = 0;
         int var40 = 0;

         int var161;
         label165: {
            for (Object var42 : var38) {
               Attribute var43 = (Attribute)var42;
               int var44 = 0;
               if (!var43.hasValue()) {
                  var161 = var40;
                  break label165;
               }

               var40++;
            }

            var161 = -1;
         }

         Integer var113 = var161;
         int var115 = var113.intValue();
         int var117 = 0;
         int var45 = (var115 != -1 ? var113 : null) != null ? var115 != -1 ? var113 : null : var7.size() - 1;
         var35.startReplaceGroup(1866899653);
         ComposerKt.sourceInformation(var35, "*54@2195L4301");
         int var46 = 0;

         for (int var114 = var45 + 1; var46 < var114; var46++) {
            Attribute var116 = (Attribute)var7.get(var46);
            byte var121 = 0;
            int $i$f$Columnx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var35, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Modifier var118 = Modifier.Companion;
            Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
            Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(
               verticalArrangement$ivx, horizontalAlignment$ivx, var35, 14 & var121 >> 3 | 112 & var121 >> 3
            );
            Modifier var48 = var118;
            MeasurePolicy var49 = measurePolicy$ivx;
            Composer var50 = var35;
            int var51 = 112 & var121 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var50, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var50, 0));
            CompositionLocalMap localMap$iv$ivx = var50.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var50, var48);
            Function0 var56 = ComposeUiNode.Companion.getConstructor();
            Composer var57 = var50;
            int var58 = 6 | 896 & var51 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var57, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var57.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var57.startReusableNode();
            if (var57.getInserting()) {
               var57.createNode(var56);
            } else {
               var57.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var57);
            int var61/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var49, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var62 = 14 & var58 >> 6;
            Composer $composer$ivx = var57;
            int var64/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var65 = 6 | 112 & var121 >> 6;
            Composer var66 = $composer$ivx;
            ColumnScope var67 = ColumnScopeInstance.INSTANCE;
            int var68 = 0;
            ComposerKt.sourceInformationMarkerStart(var66, -643101961, "C55@2264L22,55@2247L39,65@2801L1184:AttributeCollectorCard.kt#h0e4f2");
            Object[] var162 = new Object[0];
            var66.startReplaceGroup(671988484);
            ComposerKt.sourceInformation(var66, "CC(remember):AttributeCollectorCard.kt#9igjgp");
            Composer var69 = var66;
            boolean var70 = false;
            int var71 = 0;
            Object var72 = var69.rememberedValue();
            int var73 = 0;
            Object var169;
            if (var72 == Composer.Companion.getEmpty()) {
               Object[] var74 = var162;
               int var75 = 0;
               Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.views.compose.AttributeCollectorCardKt$AttributeCollectorCard$2.invoke$lambda$11$lambda$10$lambda$3$lambda$2());
               var162 = var74;
               Object var141 = var10001;
               var69.updateRememberedValue(var141);
               var169 = var141;
            } else {
               var169 = var72;
            }

            Function0 var76 = (Function0)var169;
            var66.endReplaceGroup();
            MutableState var77 = (MutableState)RememberSaveableKt.rememberSaveable(var162, var76, var66, 48);
            String var123 = var8.contains(var116.getIdentifier()) ? var9.getString(string.intercom_something_went_wrong_try_again) : "";
            Intrinsics.checkNotNull(var123);
            String var147 = var123;
            boolean var124 = var10.contains(var116.getIdentifier());
            var66.startReplaceGroup(672001750);
            ComposerKt.sourceInformation(var66, "63@2717L41");
            if (var46 > 0) {
               Modifier var163 = Modifier.Companion;
               int $this$dp$ivx = 24;
               int $i$f$getDpx = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var163, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var66, 6);
            }

            var66.endReplaceGroup();
            Modifier var126 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            byte var135 = 6;
            int var142 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var66, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Horizontal var129 = Arrangement.INSTANCE.getStart();
            androidx.compose.ui.Alignment.Vertical var131 = Alignment.Companion.getTop();
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var129, var131, var66, 14 & var135 >> 3 | 112 & var135 >> 3);
            Modifier var79 = var126;
            MeasurePolicy var80 = measurePolicy$ivxx;
            Composer var81 = var66;
            int var82 = 112 & var135 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var81, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var81, 0));
            CompositionLocalMap localMap$iv$ivxx = var81.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var81, var79);
            Function0 var87 = ComposeUiNode.Companion.getConstructor();
            Composer var88 = var81;
            int var89 = 6 | 896 & var82 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var88, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var88.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var88.startReusableNode();
            if (var88.getInserting()) {
               var88.createNode(var87);
            } else {
               var88.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var88);
            int var92/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var80, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var93 = 14 & var89 >> 6;
            Composer $composer$ivxx = var88;
            int var95 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var96 = 6 | 112 & var135 >> 6;
            Composer var97 = $composer$ivxx;
            RowScope var98 = RowScopeInstance.INSTANCE;
            int var99 = 0;
            ComposerKt.sourceInformationMarkerStart(var97, 1598737277, "C71@3145L10,68@2914L379:AttributeCollectorCard.kt#h0e4f2");
            Modifier var100 = RowScope.weight$default(var98, Modifier.Companion, 1.0F, false, 2, null);
            String var101 = var7.size() > 1 ? var46 + 1 + ". " + var116.getName() : var116.getName();
            TextStyle var102 = IntercomTheme.INSTANCE.getTypography(var97, IntercomTheme.$stable).getType04();
            int var103 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var101, var100, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var103, false, 2, 0, null, var102, var97, 0, 24960, 110588
            );
            var97.startReplaceGroup(-86962638);
            ComposerKt.sourceInformation(var97, "81@3790L10,82@3849L57,78@3555L382");
            if (var46 == 0 && var7.size() > 1) {
               Modifier var164 = Modifier.Companion;
               int $this$dp$ivx = 8;
               int $i$f$getDpx = 0;
               Modifier var156 = PaddingKt.recovered$03b481d31d62885a(var164, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 0.0F, 0.0F, 14, null);
               String var158 = "" + '(' + (var45 + 1) + " / " + var7.size() + ')';
               TextStyle var160 = IntercomTheme.INSTANCE.getTypography(var97, IntercomTheme.$stable).getType04();
               long var104 = ColorResources_androidKt.colorResource(color.intercom_attribute_input_hint, var97, 0);
               TextKt.recovered$5b114ab32a4f4190(
                  var158, var156, var104, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var160, var97, 48, 0, 131064
               );
            }

            var97.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var97);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var88.endNode();
            ComposerKt.sourceInformationMarkerEnd(var88);
            ComposerKt.sourceInformationMarkerEnd(var81);
            ComposerKt.sourceInformationMarkerEnd(var66);
            AttributeData var127 = new AttributeData(var116, var11, var12);
            Modifier var165 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            Modifier var130 = PaddingKt.recovered$03b481d31d62885a(var165, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 0.0F, 13, null);
            String var133 = var116.getRenderType();
            if (Intrinsics.areEqual(var133, "boolean")) {
               var66.startReplaceGroup(672058796);
               ComposerKt.sourceInformation(var66, "95@4451L350");
               BooleanAttributeCollectorKt.BooleanAttributeCollector(var130, var127, var147.length() > 0, var124, var13, var66, 6, 0);
               var66.endReplaceGroup();
            } else if (Intrinsics.areEqual(var133, "list")) {
               var66.startReplaceGroup(672071529);
               ComposerKt.sourceInformation(var66, "103@4849L347");
               ListAttributeCollectorKt.ListAttributeCollector(var130, var127, var147.length() > 0, var124, var13, var66, 6, 0);
               var66.endReplaceGroup();
            } else {
               var66.startReplaceGroup(672090384);
               ComposerKt.sourceInformation(var66, "118@5741L24,113@5438L354");
               Modifier var166 = var130;
               AttributeData var170 = var127;
               boolean var10002 = var124;
               var66.startReplaceGroup(672099750);
               ComposerKt.sourceInformation(var66, "CC(remember):AttributeCollectorCard.kt#9igjgp");
               Composer var143 = var66;
               boolean var148 = var66.changed(var77);
               int $i$f$cachex = 0;
               Object it$ivx = var143.rememberedValue();
               int var153/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               Object var10003;
               if (!var148 && it$ivx != Composer.Companion.getEmpty()) {
                  var10003 = it$ivx;
               } else {
                  boolean var106 = var124;
                  AttributeData var107 = var127;
                  Modifier var140 = var130;
                  int var154 = 0;
                  final androidx.compose.runtime.MutableState recoveredExpressionCapture_1630_0 = var77;
                  Function1 var108 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1630_0) -> io.intercom.android.sdk.views.compose.AttributeCollectorCardKt$AttributeCollectorCard$2.invoke$lambda$11$lambda$10$lambda$8$lambda$7(recoveredExpressionCapture_1630_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1630_0)));
                  var166 = var140;
                  var170 = var107;
                  var10002 = var106;
                  Object var155 = var108;
                  var143.updateRememberedValue(var155);
                  var10003 = var155;
               }

               Function1 var137 = (Function1)var10003;
               var66.endReplaceGroup();
               TextAttributeCollectorKt.TextAttributeCollector(var166, var170, var10002, var137, var13, var66, 6, 0);
               var66.endReplaceGroup();
            }

            var66.startReplaceGroup(672103377);
            ComposerKt.sourceInformation(var66, "127@6208L10,128@6263L62,122@5924L532");
            if (invoke$lambda$11$lambda$10$lambda$4(var77).length() > 0 || var147.length() > 0) {
               Modifier var167 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int $this$dp$ivxx = 12;
               int $i$f$getDpxx = 0;
               Modifier var134 = PaddingKt.recovered$03b481d31d62885a(var167, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 0.0F, 13, null);
               CharSequence var145 = invoke$lambda$11$lambda$10$lambda$4(var77);
               CharSequence var168;
               if (var145.length() == 0) {
                  int var149 = 0;
                  var168 = var147;
               } else {
                  var168 = var145;
               }

               String var139 = (String)var168;
               TextStyle var146 = IntercomTheme.INSTANCE.getTypography(var66, IntercomTheme.$stable).getType04();
               long var109 = ColorResources_androidKt.colorResource(color.intercom_attribute_collector_error, var66, 0);
               int var152 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
               TextKt.recovered$5b114ab32a4f4190(
                  var139, var134, var109, null, 0L, null, null, null, 0L, null, null, 0L, var152, false, 3, 0, null, var146, var66, 48, 24960, 110584
               );
            }

            var66.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var66);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var57.endNode();
            ComposerKt.sourceInformationMarkerEnd(var57);
            ComposerKt.sourceInformationMarkerEnd(var50);
            ComposerKt.sourceInformationMarkerEnd(var35);
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final MutableState invoke$lambda$11$lambda$10$lambda$3$lambda$2() {
      return SnapshotStateKt.mutableStateOf$default("", null, 2, null);
   }

   private static final String invoke$lambda$11$lambda$10$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void invoke$lambda$11$lambda$10$lambda$5(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit invoke$lambda$11$lambda$10$lambda$8$lambda$7(MutableState var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      invoke$lambda$11$lambda$10$lambda$5(var0, var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
