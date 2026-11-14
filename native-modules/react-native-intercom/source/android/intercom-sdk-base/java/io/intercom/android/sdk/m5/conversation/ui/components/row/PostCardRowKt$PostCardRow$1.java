package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
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
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.geometry.Size;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.post.IntercomPostActivity;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.Arrays;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPostCardRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PostCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/PostCardRowKt$PostCardRow$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 8 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 9 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 10 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,204:1\n87#2:205\n83#2,10:206\n87#2:251\n84#2,9:252\n94#2:288\n94#2:292\n81#3,6:216\n88#3,6:231\n81#3,6:261\n88#3,6:276\n96#3:287\n96#3:291\n402#4,9:222\n411#4:237\n402#4,9:267\n411#4:282\n412#4,2:285\n412#4,2:289\n1047#5,6:238\n1047#5,6:245\n118#6:244\n118#6:283\n118#6:284\n118#6:293\n118#6:297\n61#7:294\n70#8:295\n53#8,3:299\n23#9:296\n30#10:298\n*S KotlinDebug\n*F\n+ 1 PostCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/PostCardRowKt$PostCardRow$1\n*L\n66#1:205\n66#1:206,10\n88#1:251\n88#1:252,9\n88#1:288\n66#1:292\n66#1:216,6\n66#1:231,6\n88#1:261,6\n88#1:276,6\n88#1:287\n66#1:291\n66#1:222,9\n66#1:237\n88#1:267,9\n88#1:282\n88#1:285,2\n66#1:289,2\n78#1:238,6\n92#1:245,6\n86#1:244\n111#1:283\n117#1:284\n82#1:293\n83#1:297\n83#1:294\n83#1:295\n83#1:299,3\n83#1:296\n83#1:298\n*E\n"
)
final class PostCardRowKt$PostCardRow$1 implements Function3 {
   // $VF: synthetic field
   final Part $part;
   // $VF: synthetic field
   final String $companyName;
   // $VF: synthetic field
   final Pair[] $gradientColors;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final long $actionColor;

   PostCardRowKt$PostCardRow$1(Part var1, String var2, Pair[] var3, Context var4, long var5) {
      this.$part = var1;
      this.$companyName = var2;
      this.$gradientColors = var3;
      this.$context = var4;
      this.$actionColor = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C65@2817L2336:PostCardRow.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1178622818, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.PostCardRow.<anonymous> (PostCardRow.kt:65)"
            );
         }

         Part var7 = this.$part;
         String var8 = this.$companyName;
         Pair[] var9 = this.$gradientColors;
         Context var10 = this.$context;
         long var11 = this.$actionColor;
         int var13 = 0;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var15 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var13 >> 3 | 112 & var13 >> 3);
         Modifier var16 = var4;
         MeasurePolicy var17 = var15;
         Composer var18 = var2;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         ColumnScope var35 = ColumnScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1182125285, "C74@3253L6,77@3379L357,66@2838L949,90@3926L6,91@3975L536,87@3800L1343:PostCardRow.kt#hr3bi5"
         );
         List var10000 = var7.getBlocks();
         Intrinsics.checkNotNullExpressionValue(var10000, "getBlocks(...)");
         String var10001 = var7.getParticipant().getForename();
         Intrinsics.checkNotNullExpressionValue(var10001, "getForename(...)");
         String var10002 = var8;
         Avatar var10005 = var7.getParticipant().getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10005, "getAvatar(...)");
         AvatarWrapper var10003 = new AvatarWrapper(var10005, false);
         long var10004 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         Modifier var109 = ColumnScope.weight$default(var35, Modifier.Companion, 1.0F, false, 2, null);
         var34.startReplaceGroup(453790434);
         ComposerKt.sourceInformation(var34, "CC(remember):PostCardRow.kt#9igjgp");
         Composer var37 = var34;
         boolean var38 = var34.changedInstance(var9);
         int var39 = 0;
         Object var40 = var37.rememberedValue();
         int var41 = 0;
         Object var10006;
         if (!var38 && var40 != Composer.Companion.getEmpty()) {
            var10006 = var40;
         } else {
            Modifier var42 = var109;
            long var43 = var10004;
            AvatarWrapper var45 = var10003;
            String var46 = var8;
            String var47 = var10001;
            List var48 = var10000;
            int var49 = 0;
            final kotlin.Pair[] recoveredCapture_768_0 = var9;
            Function1 var50 = ((kotlin.jvm.functions.Function1) (recoveredArgument_768_0) -> PostCardRowKt$PostCardRow$1.invoke$lambda$5$lambda$1$lambda$0(recoveredCapture_768_0, (androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredArgument_768_0));
            var10000 = var48;
            var10001 = var47;
            var10002 = var46;
            var10003 = var45;
            var10004 = var43;
            var109 = var42;
            Object var51 = var50;
            var37.updateRememberedValue(var51);
            var10006 = var51;
         }

         Function1 var52 = (Function1)var10006;
         var34.endReplaceGroup();
         Modifier var110 = DrawModifierKt.drawWithContent(var109, var52);
         int var95 = 12;
         int var76 = 0;
         PostCardRowKt.recovered$e9e5bdd615bcb374(
            var10000, var10001, var10002, var10003, var10004, PaddingKt.recovered$eb6a744cd98cdbca(var110, Dp.recovered$95b3d103663bf060(var95)), var34, 0, 0
         );
         Modifier var102 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         boolean var105 = false;
         var10002 = null;
         Object var107 = null;
         Object var108 = null;
         var34.startReplaceGroup(453809685);
         ComposerKt.sourceInformation(var34, "CC(remember):PostCardRow.kt#9igjgp");
         var37 = var34;
         var38 = var34.changedInstance(var10) | var34.changedInstance(var7);
         var39 = 0;
         var40 = var37.rememberedValue();
         var41 = 0;
         Object var111;
         if (!var38 && var40 != Composer.Companion.getEmpty()) {
            var111 = var40;
         } else {
            Object var53 = null;
            Object var87 = null;
            Object var88 = null;
            boolean var89 = false;
            Modifier var90 = var102;
            int var91 = 0;
            final android.content.Context recoveredCapture_769_0 = var10;
            final io.intercom.android.sdk.models.Part recoveredCapture_769_1 = var7;
            Function0 var54 = ((kotlin.jvm.functions.Function0) () -> PostCardRowKt$PostCardRow$1.invoke$lambda$5$lambda$3$lambda$2(recoveredCapture_769_0, recoveredCapture_769_1));
            var102 = var90;
            var105 = var89;
            var10002 = (String)var88;
            var107 = var87;
            var108 = var53;
            Object var93 = var54;
            var37.updateRememberedValue(var93);
            var111 = var93;
         }

         Function0 var96 = (Function0)var111;
         var34.endReplaceGroup();
         Modifier var97 = ClickableKt.recovered$1c9660c41fd850dc(var102, var105, var10002, (Role)var107, (MutableInteractionSource)var108, var96, 15, null);
         Horizontal var80 = Alignment.Companion.getCenterHorizontally();
         short var82 = 384;
         var40 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(verticalArrangement$ivx, var80, var34, 14 & var82 >> 3 | 112 & var82 >> 3);
         Modifier var92 = var97;
         MeasurePolicy var94 = measurePolicy$ivx;
         Composer var55 = var34;
         int var56 = 112 & var82 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var55, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var55, 0));
         CompositionLocalMap localMap$iv$ivx = var55.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var55, var92);
         Function0 var61 = ComposeUiNode.Companion.getConstructor();
         Composer var62 = var55;
         int var63 = 6 | 896 & var56 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var62, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var62.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var62.startReusableNode();
         if (var62.getInserting()) {
            var62.createNode(var61);
         } else {
            var62.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var62);
         int var66/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var94, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var67 = 14 & var63 >> 6;
         Composer $composer$ivx = var62;
         int var69/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var70 = 6 | 112 & var82 >> 6;
         Composer var71 = $composer$ivx;
         ColumnScope var72 = ColumnScopeInstance.INSTANCE;
         int var73 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var71, -902093885, "C105@4613L180,110@4810L41,112@4894L48,113@4986L10,111@4868L203,116@5088L41:PostCardRow.kt#hr3bi5"
         );
         IntercomDividerKt.IntercomDivider(var72.align(SizeKt.fillMaxWidth(Modifier.Companion, 0.9F), Alignment.Companion.getCenterHorizontally()), var71, 0, 0);
         Modifier var103 = Modifier.Companion;
         int $this$dp$ivx = 14;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var103, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var71, 6);
         String var98 = StringResources_androidKt.stringResource(string.intercom_view_post, var71, 0);
         TextStyle var100 = IntercomTheme.INSTANCE.getTypography(var71, IntercomTheme.$stable).getType04SemiBold();
         TextKt.recovered$5b114ab32a4f4190(
            var98, null, var11, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var100, var71, 0, 0, 131066
         );
         Modifier var104 = Modifier.Companion;
         $this$dp$ivx = 14;
         $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var104, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var71, 6);
         ComposerKt.sourceInformationMarkerEnd(var71);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var62.endNode();
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd(var55);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$5$lambda$1$lambda$0(Pair[] var0, ContentDrawScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$drawWithContent");
      var1.drawContent();
      Brush var2 = Companion.recovered$762e0717d61f63ce(Brush.Companion, (Pair[])Arrays.copyOf(var0, var0.length), 0.0F, 0.0F, 0, 14, null);
      long var10000 = var1.recovered$d1ee80c43f6fa69a();
      int var5 = 120;
      int var6 = 0;
      long var3 = Size.recovered$3932c79a6983551f(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var5), 1, null);
      float var7 = 0.0F;
      long var8 = var1.recovered$d1ee80c43f6fa69a();
      int var10 = 0;
      long var11 = var8;
      int var13 = 0;
      int var14 = (int)(var11 & 4294967295L);
      int var15 = 0;
      float var25 = Float.intBitsToFloat(var14);
      int $this$dp$ivx = 120;
      int $i$f$getDpx = 0;
      float var19 = var25 - Dp.recovered$95b3d103663bf060($this$dp$ivx);
      $i$f$getDpx = 0;
      float var21 = var7;
      float var22 = var19;
      int var12 = 0;
      long var23 = Float.floatToRawIntBits(var21);
      long var24 = Float.floatToRawIntBits(var22);
      long var17 = Offset.recovered$8c837f3e34208708(var23 << 32 | var24 & 4294967295L);
      DrawScope.recovered$9f2640ea279134d8(var1, var2, var17, var3, 0.0F, null, null, 0, 120, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$3$lambda$2(Context var0, Part var1) {
      var0.startActivity(
         IntercomPostActivity.Companion
            .buildPostIntent(
               var0,
               var1,
               var1.getParentConversation().getId(),
               var1.getParentConversation().lastParticipatingAdmin(),
               var1.getParentConversation().getComposerState().isVisible(),
               false
            )
      );
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
