package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
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
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.InlineSource;
import io.intercom.android.sdk.models.Source;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt$itemsIndexed$4\n+ 2 InlineSourcesSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/InlineSourcesSheetContentKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 10 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 11 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n*L\n1#1,523:1\n75#2:524\n76#2,2:528\n78#2:531\n79#2,11:565\n98#2:582\n99#2:584\n101#2:618\n102#2,7:652\n110#2,2:663\n109#2:666\n113#2,2:698\n116#2,8:733\n124#2:742\n121#2:749\n130#2,11:782\n141#2,4:794\n145#2:802\n147#2,7:804\n146#2,10:811\n157#2,3:825\n156#2,7:828\n163#2:839\n164#2,2:844\n166#2:850\n1761#3,3:525\n75#4:530\n87#5:532\n83#5,10:533\n87#5:667\n85#5,8:668\n94#5:838\n94#5:849\n81#6,6:543\n88#6,6:558\n81#6,6:596\n88#6,6:611\n81#6,6:630\n88#6,6:645\n96#6:661\n81#6,6:676\n88#6,6:691\n81#6,6:711\n88#6,6:726\n81#6,6:760\n88#6,6:775\n96#6:800\n96#6:823\n96#6:837\n96#6:842\n96#6:848\n402#7,9:549\n411#7:564\n402#7,9:602\n411#7:617\n402#7,9:636\n411#7:651\n412#7,2:659\n402#7,9:682\n411#7:697\n402#7,9:717\n411#7:732\n402#7,9:766\n411#7:781\n412#7,2:798\n412#7,2:821\n412#7,2:835\n412#7,2:840\n412#7,2:846\n1047#8,6:576\n1047#8,6:743\n118#9:583\n118#9:585\n118#9:619\n118#9:665\n118#9:741\n118#9:793\n118#9:803\n99#10:586\n96#10,9:587\n99#10:700\n95#10,10:701\n106#10:824\n106#10:843\n70#11:620\n67#11,9:621\n77#11:662\n70#11:750\n67#11,9:751\n77#11:801\n*S KotlinDebug\n*F\n+ 1 InlineSourcesSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/InlineSourcesSheetContentKt\n*L\n75#1:525,3\n77#1:530\n78#1:532\n78#1:533,10\n109#1:667\n109#1:668,8\n109#1:838\n78#1:849\n78#1:543,6\n78#1:558,6\n79#1:596,6\n79#1:611,6\n101#1:630,6\n101#1:645,6\n101#1:661\n109#1:676,6\n109#1:691,6\n113#1:711,6\n113#1:726,6\n121#1:760,6\n121#1:775,6\n121#1:800\n113#1:823\n109#1:837\n79#1:842\n78#1:848\n78#1:549,9\n78#1:564\n79#1:602,9\n79#1:617\n101#1:636,9\n101#1:651\n101#1:659,2\n109#1:682,9\n109#1:697\n113#1:717,9\n113#1:732\n121#1:766,9\n121#1:781\n121#1:798,2\n113#1:821,2\n109#1:835,2\n79#1:840,2\n78#1:846,2\n89#1:576,6\n124#1:743,6\n98#1:583\n99#1:585\n101#1:619\n111#1:665\n123#1:741\n140#1:793\n145#1:803\n79#1:586\n79#1:587,9\n113#1:700\n113#1:701,10\n113#1:824\n79#1:843\n101#1:620\n101#1:621,9\n101#1:662\n121#1:750\n121#1:751,9\n121#1:801\n*E\n"
)
public final class InlineSourcesSheetContentKt$InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3
   implements Function4 {
   // $VF: synthetic field
   final List $items;
   // $VF: synthetic field
   final List $selectedSources$inlined;

   public InlineSourcesSheetContentKt$InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
      List var1, List var2
   ) {
      this.$items = var1;
      this.$selectedSources$inlined = var2;
   }

   @Composable
   @FunctionKeyMeta(key = 2039820996, startOffset = 10658, endOffset = 10700)
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      ComposerKt.sourceInformation(var3, "CN(it)214@10668L26:LazyDsl.kt#428nma");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if (var3.shouldExecute((var5 & 147) != 146, var5 & 1)) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2039820996, var5, -1, "androidx.compose.foundation.lazy.itemsIndexed.<anonymous> (LazyDsl.kt:214)");
         }

         Object var10002 = this.$items.get(var2);
         int var6 = 14 & var5 | 112 & var5;
         Composer var7 = var3;
         Source var8 = (Source)var10002;
         int var9 = var2;
         int var11 = 0;
         var7.startReplaceGroup(328361500);
         ComposerKt.sourceInformation(var7, "C*76@3106L7,77@3130L4558:InlineSourcesSheetContent.kt#o19g89");
         var var12 = this.$selectedSources$inlined;
         int var13 = 0;
         boolean var10000;
         if (var12 instanceof Collection && ((Collection)var12).isEmpty()) {
            var10000 = false;
         } else {
            Iterator var14 = var12.iterator();

            while (true) {
               if (!var14.hasNext()) {
                  var10000 = false;
                  break;
               }

               Object var15 = var14.next();
               InlineSource var16 = (InlineSource)var15;
               int var17 = 0;
               if (Intrinsics.areEqual(var16.getHref(), var8.getUrl())) {
                  var10000 = true;
                  break;
               }
            }
         }

         boolean var18 = var10000;
         boolean var167 = Intrinsics.areEqual(var8.getType(), "article");
         CompositionLocal var169 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var171 = 0;
         int var173 = 0;
         ComposerKt.sourceInformationMarkerStart(var7, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var175 = var7.consume(var169);
         ComposerKt.sourceInformationMarkerEnd(var7);
         Context var168 = (Context)var175;
         byte var176 = 0;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var7, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var170 = Modifier.Companion;
         Vertical var172 = Arrangement.INSTANCE.getTop();
         Horizontal var174 = Alignment.Companion.getStart();
         MeasurePolicy var20 = ColumnKt.columnMeasurePolicy(var172, var174, var7, 14 & var176 >> 3 | 112 & var176 >> 3);
         Modifier var21 = var170;
         MeasurePolicy var22 = var20;
         Composer var23 = var7;
         int var24 = 112 & var176 << 3;
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
         int var38 = 6 | 112 & var176 >> 6;
         Composer var39 = var36;
         ColumnScope var40 = ColumnScopeInstance.INSTANCE;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(var39, 292325799, "C88@3656L413,78@3159L4473,163@7653L17:InlineSourcesSheetContent.kt#o19g89");
         androidx.compose.ui.Alignment.Vertical var42 = Alignment.Companion.getCenterVertically();
         Modifier var242 = Modifier.Companion;
         long var10001;
         if (var18) {
            var39.startReplaceGroup(292397625);
            ComposerKt.sourceInformation(var39, "83@3421L6");
            long var43 = IntercomTheme.INSTANCE.getColors(var39, IntercomTheme.$stable).recovered$61a1caf6b6549f74();
            var39.endReplaceGroup();
            var10001 = var43;
         } else {
            var39.startReplaceGroup(292510527);
            ComposerKt.sourceInformation(var39, "85@3535L6");
            long var177 = IntercomTheme.INSTANCE.getColors(var39, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var39.endReplaceGroup();
            var10001 = var177;
         }

         Modifier var243 = BackgroundKt.recovered$42a3174180b3fb7a(var242, var10001, null, 2, null);
         boolean var252 = false;
         var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var39.startReplaceGroup(-406200326);
         ComposerKt.sourceInformation(var39, "CC(remember):InlineSourcesSheetContent.kt#9igjgp");
         Composer var45 = var39;
         boolean var46 = var39.changed(var167) | ((var6 & 896 ^ 384) > 256 && var39.changed(var8) || (var6 & 384) == 256) | var39.changedInstance(var168);
         int var47 = 0;
         Object var48 = var45.rememberedValue();
         int var49 = 0;
         Object var10005;
         if (!var46 && var48 != Composer.Companion.getEmpty()) {
            var10005 = var48;
         } else {
            Object var50 = null;
            Object var51 = null;
            Object var52 = null;
            boolean var53 = false;
            Modifier var54 = var243;
            int var55 = 0;
            Function0 var56 = new InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$1$1(var167, var8, var168);
            var243 = var54;
            var252 = var53;
            var10002 = var52;
            var10003 = var51;
            var10004 = var50;
            Object var57 = var56;
            var45.updateRememberedValue(var57);
            var10005 = var57;
         }

         Function0 var58 = (Function0)var10005;
         var39.endReplaceGroup();
         Modifier var244 = ClickableKt.recovered$1c9660c41fd850dc(
            var243, var252, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var58, 15, null
         );
         int var187 = 24;
         int var178 = 0;
         Modifier var245 = PaddingKt.recovered$f3b9f2e94c07c806(var244, 0.0F, Dp.recovered$95b3d103663bf060(var187), 1, null);
         int $this$dp$ivx = 24;
         int $i$f$getDpx = 0;
         Modifier var59 = PaddingKt.recovered$03b481d31d62885a(var245, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 11, null);
         short var180 = 384;
         var46 = false;
         ComposerKt.sourceInformationMarkerStart(
            var39, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var189 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var189, var42, var39, 14 & var180 >> 3 | 112 & var180 >> 3);
         var48 = var59;
         MeasurePolicy var184 = measurePolicy$ivx;
         Composer var185 = var39;
         int var186 = 112 & var180 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var185, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var185, 0));
         CompositionLocalMap localMap$iv$ivx = var185.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var185, (Modifier)var48);
         Function0 var64 = ComposeUiNode.Companion.getConstructor();
         Composer var65 = var185;
         int var66 = 6 | 896 & var186 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var65, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var65.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var65.startReusableNode();
         if (var65.getInserting()) {
            var65.createNode(var64);
         } else {
            var65.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var65);
         int var69/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var184, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var70 = 14 & var66 >> 6;
         Composer $composer$ivx = var65;
         int var72 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var73 = 6 | 112 & var180 >> 6;
         Composer var74 = $composer$ivx;
         RowScope var75 = RowScopeInstance.INSTANCE;
         int var76 = 0;
         ComposerKt.sourceInformationMarkerStart(var74, -1814769235, "C100@4224L420,108@4669L2941:InlineSourcesSheetContent.kt#o19g89");
         Modifier var246 = Modifier.Companion;
         int $this$dp$ivxx = 40;
         int $i$f$getDpxx = 0;
         Modifier var190 = SizeKt.recovered$4f2dac42b6e775a9(var246, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         int $changed$ivx = 6;
         int var80 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var74, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var192 = Alignment.Companion.getTopStart();
         boolean var81 = false;
         MeasurePolicy measurePolicy$ivxx = BoxKt.maybeCachedBoxMeasurePolicy(var192, var81);
         Modifier var83 = var190;
         MeasurePolicy var84 = measurePolicy$ivxx;
         Composer var85 = var74;
         int var86 = 112 & $changed$ivx << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var85, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var85, 0));
         CompositionLocalMap localMap$iv$ivxx = var85.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var85, var83);
         Function0 var91 = ComposeUiNode.Companion.getConstructor();
         Composer var92 = var85;
         int var93 = 6 | 896 & var86 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var92, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var92.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var92.startReusableNode();
         if (var92.getInserting()) {
            var92.createNode(var91);
         } else {
            var92.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var92);
         int var96/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var84, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var97 = 14 & var93 >> 6;
         Composer $composer$ivxx = var92;
         int var99 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var100 = 6 | 112 & $changed$ivx >> 6;
         Composer var101 = $composer$ivxx;
         BoxScope var102 = BoxScopeInstance.INSTANCE;
         int var103 = 0;
         ComposerKt.sourceInformationMarkerStart(var101, 1230048898, "C103@4429L10,101@4292L326:InlineSourcesSheetContent.kt#o19g89");
         String var104 = String.valueOf(var9 + 1);
         TextStyle var105 = IntercomTheme.INSTANCE.getTypography(var101, IntercomTheme.$stable).getType05();
         FontWeight var106 = FontWeight.Companion.getMedium();
         Modifier modifier$ivx = var102.align(Modifier.Companion, Alignment.Companion.getCenter());
         TextKt.recovered$5b114ab32a4f4190(
            var104, modifier$ivx, 0L, null, 0L, null, var106, null, 0L, null, null, 0L, 0, false, 0, 0, null, var105, var101, 1572864, 0, 131004
         );
         ComposerKt.sourceInformationMarkerEnd(var101);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var92.endNode();
         ComposerKt.sourceInformationMarkerEnd(var92);
         ComposerKt.sourceInformationMarkerEnd(var85);
         ComposerKt.sourceInformationMarkerEnd(var74);
         Modifier var191 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         $i$f$getDpxx = 8;
         var81 = false;
         Vertical var194 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($i$f$getDpxx));
         byte var195 = 54;
         var80 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var74, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal horizontalAlignment$ivx = Alignment.Companion.getStart();
         measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(var194, horizontalAlignment$ivx, var74, 14 & var195 >> 3 | 112 & var195 >> 3);
         var83 = var191;
         var84 = measurePolicy$ivxx;
         var85 = var74;
         var86 = 112 & var195 << 3;
         $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var85, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var85, 0));
         localMap$iv$ivxx = var85.getCurrentCompositionLocalMap();
         materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var85, var83);
         var91 = ComposeUiNode.Companion.getConstructor();
         var92 = var85;
         var93 = 6 | 896 & var86 << 6;
         $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var92, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var92.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var92.startReusableNode();
         if (var92.getInserting()) {
            var92.createNode(var91);
         } else {
            var92.useNode();
         }

         $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var92);
         var96/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var84, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         var97 = 14 & var93 >> 6;
         $composer$ivxx = var92;
         var99 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         var100 = 6 | 112 & var195 >> 6;
         var101 = $composer$ivxx;
         ColumnScope var219 = ColumnScopeInstance.INSTANCE;
         var103 = 0;
         ComposerKt.sourceInformationMarkerStart(var101, 1230701107, "C112@4875L2403,157@7420L10,155@7307L277:InlineSourcesSheetContent.kt#o19g89");
         androidx.compose.ui.Alignment.Vertical var221 = Alignment.Companion.getCenterVertically();
         short var222 = 384;
         int var223 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var101, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         modifier$ivx = Modifier.Companion;
         androidx.compose.foundation.layout.Arrangement.Horizontal horizontalArrangement$ivx = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivxxx = RowKt.rowMeasurePolicy(horizontalArrangement$ivx, var221, var101, 14 & var222 >> 3 | 112 & var222 >> 3);
         Modifier var110 = modifier$ivx;
         MeasurePolicy var111 = measurePolicy$ivxxx;
         Composer var112 = var101;
         int var113 = 112 & var222 << 3;
         int $i$f$Layoutxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var112, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var112, 0));
         CompositionLocalMap localMap$iv$ivxxx = var112.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var112, var110);
         Function0 var118 = ComposeUiNode.Companion.getConstructor();
         Composer var119 = var112;
         int var120 = 6 | 896 & var113 << 6;
         int $i$f$ReusableComposeNodexxx = 0;
         ComposerKt.sourceInformationMarkerStart(var119, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var119.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var119.startReusableNode();
         if (var119.getInserting()) {
            var119.createNode(var118);
         } else {
            var119.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var119);
         int var123/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var111, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
         int var124 = 14 & var120 >> 6;
         Composer $composer$ivxxx = var119;
         int var126/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var127 = 6 | 112 & var222 >> 6;
         Composer var128 = $composer$ivxxx;
         RowScope var129 = RowScopeInstance.INSTANCE;
         int var130 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var128, -1086331079, "C123@5488L222,120@5322L1334,144@6689L28,151@7113L10,152@7196L6,145@6750L498:InlineSourcesSheetContent.kt#o19g89"
         );
         long var247;
         if (var18) {
            var128.startReplaceGroup(-1086354981);
            ComposerKt.sourceInformation(var128, "116@5118L6");
            long var131 = IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).recovered$a9608762563e9caa();
            var128.endReplaceGroup();
            var247 = var131;
         } else {
            var128.startReplaceGroup(-1086240932);
            ComposerKt.sourceInformation(var128, "118@5233L6");
            long var225 = IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).recovered$61a1caf6b6549f74();
            var128.endReplaceGroup();
            var247 = var225;
         }

         long var133 = var247;
         Modifier var248 = Modifier.Companion;
         int $this$dp$ivxxx = 24;
         int $i$f$getDpxxx = 0;
         Modifier var249 = SizeKt.recovered$111ef94c93e78c04(var248, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
         var128.startReplaceGroup(103517240);
         ComposerKt.sourceInformation(var128, "CC(remember):InlineSourcesSheetContent.kt#9igjgp");
         Composer var230 = var128;
         boolean propagateMinConstraints$ivx = var128.changed(var133);
         int $i$f$cachex = 0;
         Object it$ivx = var230.rememberedValue();
         int var140/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var254;
         if (!propagateMinConstraints$ivx && it$ivx != Composer.Companion.getEmpty()) {
            var254 = (Function1)it$ivx;
         } else {
            Modifier var141 = var249;
            int var142 = 0;
            var254 = new InlineSourcesSheetContentKt$InlineSourcesSheetContent$1$1$1$2$1$2$2$1$1$1(var133);
            var249 = var141;
            Object var143 = var254;
            var230.updateRememberedValue(var143);
            var254 = (Function1)var143;
         }

         Function1 var226 = (kotlin.jvm.functions.Function1) var254;
         var128.endReplaceGroup();
         Modifier var227 = DrawModifierKt.drawBehind(var249, var226);
         byte var237 = 0;
         it$ivx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var128, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment contentAlignment$ivx = Alignment.Companion.getTopStart();
         propagateMinConstraints$ivx = false;
         MeasurePolicy measurePolicy$ivxxxx = BoxKt.maybeCachedBoxMeasurePolicy(contentAlignment$ivx, propagateMinConstraints$ivx);
         Modifier var240 = var227;
         MeasurePolicy var241 = measurePolicy$ivxxxx;
         Composer var144 = var128;
         int var145 = 112 & var237 << 3;
         int $i$f$Layoutxxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var144, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var144, 0));
         CompositionLocalMap localMap$iv$ivxxxx = var144.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxxxx = ComposedModifierKt.materializeModifier(var144, var240);
         Function0 var150 = ComposeUiNode.Companion.getConstructor();
         Composer var151 = var144;
         int var152 = 6 | 896 & var145 << 6;
         int $i$f$ReusableComposeNodexxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var151, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var151.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var151.startReusableNode();
         if (var151.getInserting()) {
            var151.createNode(var150);
         } else {
            var151.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxxxx = Updater.recovered$676aa17b44fd6280(var151);
         int var155/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxxx, var241, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(
            $this$Layout_u24lambda_u240$iv$ivxxxx, localMap$iv$ivxxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
         );
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxxx, compositeKeyHash$iv$ivxxxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxxx, materialized$iv$ivxxxx, ComposeUiNode.Companion.getSetModifier());
         int var156 = 14 & var152 >> 6;
         Composer $composer$ivxxxx = var151;
         int var158/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxxxx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var159 = 6 | 112 & var237 >> 6;
         Composer var160 = $composer$ivxxxx;
         BoxScope var161 = BoxScopeInstance.INSTANCE;
         int var162 = 0;
         ComposerKt.sourceInformationMarkerStart(var160, 1903564100, "C130@5840L390,141@6566L6,129@5784L838:InlineSourcesSheetContent.kt#o19g89");
         Painter var250 = PainterResources_androidKt.painterResource(var167 ? drawable.intercom_ic_public_article : drawable.intercom_ic_link, var160, 0);
         Modifier var256 = Modifier.Companion;
         int $this$dp$ivxxxx = 12;
         int $i$f$getDpxxxx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var250,
            null,
            var161.align(SizeKt.recovered$111ef94c93e78c04(var256, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), Alignment.Companion.getCenter()),
            IntercomTheme.INSTANCE.getColors(var160, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
            var160,
            48 | Painter.$stable,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var160);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxxxx);
         var151.endNode();
         ComposerKt.sourceInformationMarkerEnd(var151);
         ComposerKt.sourceInformationMarkerEnd(var144);
         ComposerKt.sourceInformationMarkerEnd(var128);
         Modifier var251 = Modifier.Companion;
         $this$dp$ivxxx = 8;
         $i$f$getDpxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var251, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var128, 6);
         var128.startReplaceGroup(103559224);
         ComposerKt.sourceInformation(var128, "147@6856L53");
         String var236 = var167
            ? StringResources_androidKt.stringResource(string.intercom_help_center_article, var128, 0)
            : InlineSourcesSheetContentKt.access$getHost(var8.getUrl());
         var128.endReplaceGroup();
         String var233 = var236;
         Intrinsics.checkNotNull(var233);
         String var229 = var233;
         TextStyle var234 = IntercomTheme.INSTANCE.getTypography(var128, IntercomTheme.$stable).getType04Point5();
         long var165 = IntercomTheme.INSTANCE.getColors(var128, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextKt.recovered$5b114ab32a4f4190(
            var229, null, var165, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var234, var128, 0, 0, 131066
         );
         ComposerKt.sourceInformationMarkerEnd(var128);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
         var119.endNode();
         ComposerKt.sourceInformationMarkerEnd(var119);
         ComposerKt.sourceInformationMarkerEnd(var112);
         ComposerKt.sourceInformationMarkerEnd(var101);
         TextKt.recovered$5b114ab32a4f4190(
            var8.getTitle(),
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var101, IntercomTheme.$stable).getType04Point5(),
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
            ),
            var101,
            0,
            0,
            131070
         );
         ComposerKt.sourceInformationMarkerEnd(var101);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var92.endNode();
         ComposerKt.sourceInformationMarkerEnd(var92);
         ComposerKt.sourceInformationMarkerEnd(var85);
         ComposerKt.sourceInformationMarkerEnd(var74);
         ComposerKt.sourceInformationMarkerEnd(var74);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var65.endNode();
         ComposerKt.sourceInformationMarkerEnd(var65);
         ComposerKt.sourceInformationMarkerEnd(var185);
         ComposerKt.sourceInformationMarkerEnd(var39);
         IntercomDividerKt.IntercomDivider(null, var39, 0, 1);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var36);
         var30.endNode();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var7);
         var7.endReplaceGroup();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      } else {
         var3.skipToGroupEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
