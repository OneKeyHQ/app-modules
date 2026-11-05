package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a'\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001aG\u0010\t\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0004\b\u0013\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0016¨\u0006\u0017",
   d2 = {
         "PostCardRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "companyName",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/models/Part;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "PostContent",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "participantName",
         "participantCompanyName",
         "participantAvatarWrapper",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "headerColor",
         "Landroidx/compose/ui/graphics/Color;",
         "PostContent-FHprtrg",
         "(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;JLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "PostCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPostCardRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PostCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/PostCardRowKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,204:1\n75#2:205\n75#2:208\n118#3:206\n118#3:207\n118#3:274\n118#3:275\n118#3:280\n118#3:283\n118#3:284\n118#3:285\n87#4:209\n84#4,9:210\n94#4:290\n81#5,6:219\n88#5,6:234\n81#5,6:252\n88#5,6:267\n96#5:278\n96#5:289\n402#6,9:225\n411#6:240\n402#6,9:258\n411#6:273\n412#6,2:276\n412#6,2:287\n99#7:241\n95#7,10:242\n106#7:279\n1878#8,2:281\n1880#8:286\n*S KotlinDebug\n*F\n+ 1 PostCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/PostCardRowKt\n*L\n53#1:205\n132#1:208\n62#1:206\n63#1:207\n136#1:274\n139#1:275\n149#1:280\n168#1:283\n169#1:284\n170#1:285\n133#1:209\n133#1:210,9\n133#1:290\n133#1:219,6\n133#1:234,6\n134#1:252,6\n134#1:267,6\n134#1:278\n133#1:289\n133#1:225,9\n133#1:240\n134#1:258,9\n134#1:273\n134#1:276,2\n133#1:287,2\n134#1:241\n134#1:242,10\n134#1:279\n150#1:281,2\n150#1:286\n*E\n"
)
public final class PostCardRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PostCardRow(@Nullable Modifier var0, @NotNull Part var1, @NotNull String var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "part");
      Intrinsics.checkNotNullParameter(var2, "companyName");
      var3 = var3.startRestartGroup(-1691901714);
      ComposerKt.sourceInformation(var3, "C(PostCardRow)P(1,2)52@2347L7,53@2391L6,54@2483L6,63@2776L23,64@2807L2352,59@2612L2547:PostCardRow.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
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

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1691901714, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.PostCardRow (PostCardRow.kt:51)");
         }

         CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var3.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var7 = (Context)var11;
         long var16 = ColorExtensionsKt.recovered$93702c38c1546c4a(IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).recovered$202acdf3dbb5c020());
         long var17 = IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         Pair[] var13 = new Pair[]{
            TuplesKt.to(0.0F, Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$562f21ab17d9b4c7())),
            TuplesKt.to(0.9F, Color.recovered$f46ef195ad4a2363(var17))
         };
         Pair[] var12 = var13;
         int var18 = 14;
         int var14 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var18);
         int $this$dp$ivx = 12;
         var14 = 0;
         Modifier var10000 = PaddingKt.recovered$904403cea4c49d95(var0, var10001, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         int $this$dp$ivxx = 200;
         var14 = 0;
         IntercomCardKt.IntercomCard(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)),
            IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var3, IntercomCardStyle.$stable << 15, 31),
            ComposableLambdaKt.rememberComposableLambda(1178622818, true, new PostCardRowKt$PostCardRow$1(var1, var2, var12, var7, var16), var3, 54),
            var3,
            384 | Style.$stable << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var23 = var3.endRestartGroup();
      if (var23 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_766_0 = var0;
         final io.intercom.android.sdk.models.Part recoveredCapture_766_1 = var1;
         final java.lang.String recoveredCapture_766_2 = var2;
         final int recoveredCapture_766_3 = var4;
         final int recoveredCapture_766_4 = var5;
         var23.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_766_0, recoveredArgument_766_1) -> PostCardRowKt.PostCardRow$lambda$0(recoveredCapture_766_0, recoveredCapture_766_1, recoveredCapture_766_2, recoveredCapture_766_3, recoveredCapture_766_4, (androidx.compose.runtime.Composer)recoveredArgument_766_0, ((java.lang.Integer)recoveredArgument_766_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$e9e5bdd615bcb374(
      @NotNull List var0,
      @NotNull String var1,
      @NotNull String var2,
      @NotNull AvatarWrapper var3,
      long var4,
      @Nullable Modifier var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var0, "blocks");
      Intrinsics.checkNotNullParameter(var1, "participantName");
      Intrinsics.checkNotNullParameter(var2, "participantCompanyName");
      Intrinsics.checkNotNullParameter(var3, "participantAvatarWrapper");
      var7 = var7.startRestartGroup(-1350453300);
      ComposerKt.sourceInformation(var7, "C(PostContent)P(!1,5,4,3,1:c#ui.graphics.Color)131@5430L7,132@5442L1718:PostCardRow.kt#hr3bi5");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var7.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var7.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var7.changed(var6) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if ((var9 & 32) != 0) {
            var6 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1350453300, var10, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.PostContent (PostCardRow.kt:130)");
         }

         CompositionLocal var12 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var13 = 0;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var7, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var15 = var7.consume(var12);
         ComposerKt.sourceInformationMarkerEnd(var7);
         Context var11 = (Context)var15;
         var14 = 14 & var10 >> 15;
         int var71 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var7, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var68 = Arrangement.INSTANCE.getTop();
         Horizontal var69 = Alignment.Companion.getStart();
         MeasurePolicy var16 = ColumnKt.columnMeasurePolicy(var68, var69, var7, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var6;
         MeasurePolicy var18 = var16;
         Composer var19 = var7;
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
         ComposerKt.sourceInformationMarkerStart(var35, 1718903724, "C133@5480L608,148@6097L41:PostCardRow.kt#hr3bi5");
         androidx.compose.ui.Alignment.Vertical var38 = Alignment.Companion.getCenterVertically();
         int $changed$ivx = 384;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var41 = Modifier.Companion;
         androidx.compose.foundation.layout.Arrangement.Horizontal var42 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var42, var38, var35, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3);
         Modifier var44 = var41;
         MeasurePolicy var45 = measurePolicy$ivx;
         Composer var46 = var35;
         int var47 = 112 & $changed$ivx << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var44);
         Function0 var52 = ComposeUiNode.Companion.getConstructor();
         Composer var53 = var46;
         int var54 = 6 | 896 & var47 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var53, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var53.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var53.startReusableNode();
         if (var53.getInserting()) {
            var53.createNode(var52);
         } else {
            var53.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var53);
         int var57/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var45, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var58 = 14 & var54 >> 6;
         Composer $composer$ivx = var53;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var61 = 6 | 112 & $changed$ivx >> 6;
         Composer var62 = $composer$ivx;
         RowScope var63 = RowScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var62, 1732477571, "C134@5546L132,138@5691L40,145@6046L10,139@5744L334:PostCardRow.kt#hr3bi5");
         Modifier var10000 = Modifier.Companion;
         int $this$dp$ivx = 24;
         int var66 = 0;
         AvatarIconKt.recovered$f5ec3c81e3146789(
            SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
            var3,
            null,
            false,
            0L,
            null,
            var62,
            6 | 112 & var10 >> 6,
            60
         );
         var10000 = Modifier.Companion;
         $this$dp$ivx = 12;
         var66 = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var62, 6);
         TextKt.recovered$5b114ab32a4f4190(
            Phrase.from(var11, string.intercom_teammate_from_company).put("name", var1).put("company", var2).format().toString(),
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
            IntercomTheme.INSTANCE.getTypography(var62, IntercomTheme.$stable).getType04(),
            var62,
            0,
            0,
            131070
         );
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var35, 6);
         var35.startReplaceGroup(-83077931);
         ComposerKt.sourceInformation(var35, "*150@6199L638");
         Iterable var76 = var0;
         int var78 = 0;
         int var72 = 0;

         for (Object var74 : var76) {
            int var79 = var72++;
            if (var79 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Block var80 = (Block)var74;
            int var81 = var79;
            int var82 = 0;
            BlockViewKt.recovered$5709084683a88fd9(
               SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
               new BlockRenderData(
                  var80,
                  null,
                  new BlockRenderTextStyle(
                     TextUnitKt.getSp(20), FontWeight.Companion.getMedium(), 0L, Color.recovered$f46ef195ad4a2363(var4), null, null, 52, null
                  ),
                  new BlockRenderTextStyle(
                     TextUnitKt.getSp(16), FontWeight.Companion.getNormal(), 0L, Color.recovered$f46ef195ad4a2363(var4), null, null, 52, null
                  ),
                  null,
                  18,
                  null
               ),
               false,
               null,
               false,
               null,
               null,
               0L,
               null,
               null,
               false,
               null,
               null,
               false,
               null,
               var35,
               6,
               0,
               32764
            );
            BlockType var95 = var80.getType();
            float var96;
            switch (var95 == null ? -1 : PostCardRowKt$WhenMappings.$EnumSwitchMapping$0[var95.ordinal()]) {
               case 1:
                  var47 = 32;
                  $i$f$Layoutx = 0;
                  var96 = Dp.recovered$95b3d103663bf060(var47);
                  break;
               case 2:
                  var47 = 16;
                  $i$f$Layoutx = 0;
                  var96 = Dp.recovered$95b3d103663bf060(var47);
                  break;
               default:
                  var47 = 8;
                  $i$f$Layoutx = 0;
                  var96 = Dp.recovered$95b3d103663bf060(var47);
            }

            float var89 = var96;
            var35.startReplaceGroup(-83050692);
            ComposerKt.sourceInformation(var35, "172@7082L48");
            if (var81 != CollectionsKt.getLastIndex(var0)) {
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(Modifier.Companion, var89), var35, 0);
            }

            var35.endReplaceGroup();
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var97 = var7.endRestartGroup();
      if (var97 != null) {
         final java.util.List recoveredCapture_767_0 = var0;
         final java.lang.String recoveredCapture_767_1 = var1;
         final java.lang.String recoveredCapture_767_2 = var2;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredCapture_767_3 = var3;
         final long recoveredCapture_767_4 = var4;
         final androidx.compose.ui.Modifier recoveredCapture_767_5 = var6;
         final int recoveredCapture_767_6 = var8;
         final int recoveredCapture_767_7 = var9;
         var97.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_767_0, recoveredArgument_767_1) -> PostCardRowKt.PostContent_FHprtrg$lambda$4(recoveredCapture_767_0, recoveredCapture_767_1, recoveredCapture_767_2, recoveredCapture_767_3, recoveredCapture_767_4, recoveredCapture_767_5, recoveredCapture_767_6, recoveredCapture_767_7, (androidx.compose.runtime.Composer)recoveredArgument_767_0, ((java.lang.Integer)recoveredArgument_767_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PostCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(620144177);
      ComposerKt.sourceInformation(var0, "C(PostCardPreview)181@7230L725:PostCardRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(620144177, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.PostCardPreview (PostCardRow.kt:180)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PostCardRowKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_765_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_765_0, recoveredArgument_765_1) -> PostCardRowKt.PostCardPreview$lambda$5(recoveredCapture_765_0, (androidx.compose.runtime.Composer)recoveredArgument_765_0, ((java.lang.Integer)recoveredArgument_765_1).intValue())));
      }
   }

   private static final Unit PostCardRow$lambda$0(Modifier var0, Part var1, String var2, int var3, int var4, Composer var5, int var6) {
      PostCardRow(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit PostContent_FHprtrg$lambda$4(
      List var0, String var1, String var2, AvatarWrapper var3, long var4, Modifier var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$e9e5bdd615bcb374(var0, var1, var2, var3, var4, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit PostCardPreview$lambda$5(int var0, Composer var1, int var2) {
      PostCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
