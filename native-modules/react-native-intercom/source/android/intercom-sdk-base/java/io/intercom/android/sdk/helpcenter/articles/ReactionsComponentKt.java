package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.animation.core.AnimateAsStateKt;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ImageKt;
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
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorMatrix;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\u000f²\u0006\n\u0010\u0010\u001a\u00020\u0011X\u008a\u0084\u0002²\u0006\n\u0010\u0012\u001a\u00020\u0011X\u008a\u0084\u0002²\u0006\n\u0010\u0013\u001a\u00020\u0011X\u008a\u0084\u0002",
   d2 = {
         "ReactionsComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "currentReactionState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;",
         "sadReactionTapped",
         "Lkotlin/Function0;",
         "neutralReactionTapped",
         "happyReactionTapped",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ReactionState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "ReactionsComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ReactionComponentSadTappedPreview",
         "ReactionComponentNeutralTappedPreview",
         "intercom-sdk-base_release",
         "sadSize",
         "Landroidx/compose/ui/unit/Dp;",
         "happySize",
         "neutralSize"
   }
)
@SourceDebugExtension(
   "SMAP\nReactionsComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReactionsComponent.kt\nio/intercom/android/sdk/helpcenter/articles/ReactionsComponentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,189:1\n87#2:190\n84#2,9:191\n94#2:288\n81#3,6:200\n88#3,6:215\n81#3,6:233\n88#3,6:248\n96#3:283\n96#3:287\n402#4,9:206\n411#4:221\n402#4,9:239\n411#4:254\n412#4,2:281\n412#4,2:285\n118#5:222\n118#5:223\n118#5:255\n118#5:256\n118#5:257\n118#5:258\n118#5:259\n118#5:260\n118#5:267\n118#5:274\n99#6:224\n97#6,8:225\n106#6:284\n1047#7,6:261\n1047#7,6:268\n1047#7,6:275\n85#8:289\n85#8:290\n85#8:291\n*S KotlinDebug\n*F\n+ 1 ReactionsComponent.kt\nio/intercom/android/sdk/helpcenter/articles/ReactionsComponentKt\n*L\n41#1:190\n41#1:191,9\n41#1:288\n41#1:200,6\n41#1:215,6\n48#1:233,6\n48#1:248,6\n48#1:283\n41#1:287\n41#1:206,9\n41#1:221\n48#1:239,9\n48#1:254\n48#1:281,2\n41#1:285,2\n46#1:222\n50#1:223\n56#1:255\n58#1:256\n63#1:257\n65#1:258\n70#1:259\n72#1:260\n95#1:267\n116#1:274\n48#1:224\n48#1:225,8\n48#1:284\n77#1:261,6\n98#1:268,6\n119#1:275,6\n54#1:289\n61#1:290\n68#1:291\n*E\n"
)
public final class ReactionsComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReactionsComponent(
      @Nullable Modifier var0,
      @NotNull ArticleViewState$ReactionState var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function0 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "currentReactionState");
      Intrinsics.checkNotNullParameter(var2, "sadReactionTapped");
      Intrinsics.checkNotNullParameter(var3, "neutralReactionTapped");
      Intrinsics.checkNotNullParameter(var4, "happyReactionTapped");
      var5 = var5.startRestartGroup(-1539407934);
      ComposerKt.sourceInformation(var5, "C(ReactionsComponent)P(2!1,4,3)41@1729L6,40@1668L4197:ReactionsComponent.kt#1wdz33");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1539407934, var8, -1, "io.intercom.android.sdk.helpcenter.articles.ReactionsComponent (ReactionsComponent.kt:38)");
         }

         Modifier var9 = BackgroundKt.recovered$42a3174180b3fb7a(
            var0, IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
         );
         Horizontal var11 = Alignment.Companion.getCenterHorizontally();
         short var12 = 384;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var10 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var10, var11, var5, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var9;
         MeasurePolicy var16 = var14;
         Composer var17 = var5;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 908794817, "C44@1824L17,45@1850L41,46@1912L55,46@1900L68,47@1977L3882:ReactionsComponent.kt#1wdz33");
         IntercomDividerKt.IntercomDivider(null, var33, 0, 1);
         Modifier var10000 = Modifier.Companion;
         int var36 = 16;
         int var37 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var36)), var33, 6);
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(string.intercom_article_question, var33, 0),
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
            null,
            var33,
            0,
            0,
            262142
         );
         var10000 = Modifier.Companion;
         var36 = 16;
         var37 = 0;
         Modifier var83 = SizeKt.fillMaxWidth$default(PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var36)), 0.0F, 1, null);
         androidx.compose.foundation.layout.Arrangement.Horizontal var85 = Arrangement.INSTANCE.getCenter();
         byte var38 = 54;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var33, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.ui.Alignment.Vertical var40 = Alignment.Companion.getTop();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var85, var40, var33, 14 & var38 >> 3 | 112 & var38 >> 3);
         Modifier var42 = var83;
         MeasurePolicy var43 = measurePolicy$ivx;
         Composer var44 = var33;
         int var45 = 112 & var38 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var44, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var44, 0));
         CompositionLocalMap localMap$iv$ivx = var44.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var44, var42);
         Function0 var50 = ComposeUiNode.Companion.getConstructor();
         Composer var51 = var44;
         int var52 = 6 | 896 & var45 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var51, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var51.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var51.startReusableNode();
         if (var51.getInserting()) {
            var51.createNode(var50);
         } else {
            var51.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var51);
         int var55/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var43, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var56 = 14 & var52 >> 6;
         Composer $composer$ivx = var51;
         int var58 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var59 = 6 | 112 & var38 >> 6;
         Composer var60 = $composer$ivx;
         RowScope var61 = RowScopeInstance.INSTANCE;
         int var62 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var60,
            -374843269,
            "C53@2174L296,60@2500L298,67@2830L300,76@3217L67,80@3347L54,74@3143L850,94@4006L40,97@4133L71,101@4271L58,95@4059L866,115@4938L40,118@5065L69,122@5199L56,116@4991L858:ReactionsComponent.kt#1wdz33"
         );
         float var151;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Sad && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            int $this$dp$ivx = 24;
            int $i$f$getDpx = 0;
            var151 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 38;
            int $i$f$getDpx = 0;
            var151 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         State var65 = AnimateAsStateKt.recovered$b63f920e95265746(var151, null, null, null, var60, 0, 14);
         float var152;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Happy && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            int $this$dp$ivx = 24;
            int $i$f$getDpx = 0;
            var152 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 38;
            int $i$f$getDpx = 0;
            var152 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         State var87 = AnimateAsStateKt.recovered$b63f920e95265746(var152, null, null, null, var60, 0, 14);
         float var153;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Neutral && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            int $this$dp$ivx = 24;
            int $i$f$getDpx = 0;
            var153 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 38;
            int $i$f$getDpx = 0;
            var153 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         State var91 = AnimateAsStateKt.recovered$b63f920e95265746(var153, null, null, null, var60, 0, 14);
         var10000 = Modifier.Companion;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var60.startReplaceGroup(-1674629471);
         ComposerKt.sourceInformation(var60, "CC(remember):ReactionsComponent.kt#9igjgp");
         Composer var68 = var60;
         boolean var69 = (var8 & 896) == 256;
         int var70 = 0;
         Object var71 = var68.rememberedValue();
         int var72 = 0;
         Object var10005;
         if (!var69 && var71 != Composer.Companion.getEmpty()) {
            var10005 = var71;
         } else {
            Object var73 = null;
            Object var74 = null;
            Object var75 = null;
            boolean var76 = false;
            Modifier var77 = var10000;
            int var78 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_96_0 = var2;
            Function0 var79 = ((kotlin.jvm.functions.Function0) () -> ReactionsComponentKt.ReactionsComponent$lambda$13$lambda$12$lambda$4$lambda$3(recoveredCapture_96_0));
            var10000 = var77;
            var10001 = var76;
            var10002 = var75;
            var10003 = var74;
            var10004 = var73;
            Object var80 = var79;
            var68.updateRememberedValue(var80);
            var10005 = var80;
         }

         Function0 var101 = (Function0)var10005;
         var60.endReplaceGroup();
         Modifier var95 = SizeKt.recovered$111ef94c93e78c04(
            ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var101, 15, null),
            ReactionsComponent$lambda$13$lambda$12$lambda$0(var65)
         );
         Painter var102 = PainterResources_androidKt.painterResource(drawable.intercom_reaction_sad, var60, 0);
         Companion var155 = ColorFilter.Companion;
         float[] var114 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         float[] var119 = var114;
         Companion var139 = var155;
         var71 = 0;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Sad && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var119, 0.0F);
         } else {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var119, 1.0F);
         }

         ColorFilter var109 = var139.recovered$056553a490480fb1(var114);
         ImageKt.Image(var102, null, var95, null, null, 0.0F, var109, var60, 48 | Painter.$stable, 56);
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var60, 6);
         var10000 = Modifier.Companion;
         var10001 = false;
         var10002 = null;
         var10003 = null;
         var10004 = null;
         var60.startReplaceGroup(-1674600155);
         ComposerKt.sourceInformation(var60, "CC(remember):ReactionsComponent.kt#9igjgp");
         var68 = var60;
         var69 = (var8 & 7168) == 2048;
         var70 = 0;
         var71 = var68.rememberedValue();
         var72 = 0;
         if (!var69 && var71 != Composer.Companion.getEmpty()) {
            var10005 = var71;
         } else {
            Object var131 = null;
            Object var133 = null;
            Object var135 = null;
            boolean var137 = false;
            Modifier var140 = var10000;
            int var144 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_97_0 = var3;
            Function0 var146 = ((kotlin.jvm.functions.Function0) () -> ReactionsComponentKt.ReactionsComponent$lambda$13$lambda$12$lambda$7$lambda$6(recoveredCapture_97_0));
            var10000 = var140;
            var10001 = var137;
            var10002 = var135;
            var10003 = var133;
            var10004 = var131;
            Object var148 = var146;
            var68.updateRememberedValue(var148);
            var10005 = var148;
         }

         var101 = (Function0)var10005;
         var60.endReplaceGroup();
         var95 = SizeKt.recovered$111ef94c93e78c04(
            ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var101, 15, null),
            ReactionsComponent$lambda$13$lambda$12$lambda$2(var91)
         );
         Painter var105 = PainterResources_androidKt.painterResource(drawable.intercom_reaction_neutral, var60, 0);
         Companion var158 = ColorFilter.Companion;
         float[] var116 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         float[] var121 = var116;
         var139 = var158;
         var71 = 0;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Neutral && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var121, 0.0F);
         } else {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var121, 1.0F);
         }

         ColorFilter var111 = var139.recovered$056553a490480fb1(var116);
         ImageKt.Image(var105, null, var95, null, null, 0.0F, var111, var60, 48 | Painter.$stable, 56);
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var60, 6);
         var10000 = Modifier.Companion;
         var10001 = false;
         var10002 = null;
         var10003 = null;
         var10004 = null;
         var60.startReplaceGroup(-1674570333);
         ComposerKt.sourceInformation(var60, "CC(remember):ReactionsComponent.kt#9igjgp");
         var68 = var60;
         var69 = (var8 & 57344) == 16384;
         var70 = 0;
         var71 = var68.rememberedValue();
         var72 = 0;
         if (!var69 && var71 != Composer.Companion.getEmpty()) {
            var10005 = var71;
         } else {
            Object var132 = null;
            Object var134 = null;
            Object var136 = null;
            boolean var138 = false;
            Modifier var142 = var10000;
            int var145 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_98_0 = var4;
            Function0 var147 = ((kotlin.jvm.functions.Function0) () -> ReactionsComponentKt.ReactionsComponent$lambda$13$lambda$12$lambda$10$lambda$9(recoveredCapture_98_0));
            var10000 = var142;
            var10001 = var138;
            var10002 = var136;
            var10003 = var134;
            var10004 = var132;
            Object var149 = var147;
            var68.updateRememberedValue(var149);
            var10005 = var149;
         }

         var101 = (Function0)var10005;
         var60.endReplaceGroup();
         var95 = SizeKt.recovered$111ef94c93e78c04(
            ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var101, 15, null),
            ReactionsComponent$lambda$13$lambda$12$lambda$1(var87)
         );
         Painter var108 = PainterResources_androidKt.painterResource(drawable.intercom_reaction_happy, var60, 0);
         Companion var161 = ColorFilter.Companion;
         float[] var118 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         float[] var123 = var118;
         var139 = var161;
         var71 = 0;
         if (var1.getSelectedReaction() != ArticleViewState$Reaction.Happy && var1.getSelectedReaction() != ArticleViewState$Reaction.None) {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var123, 0.0F);
         } else {
            ColorMatrix.recovered$e4d8f44c8df66f0d(var123, 1.0F);
         }

         ColorFilter var113 = var139.recovered$056553a490480fb1(var118);
         ImageKt.Image(var108, null, var95, null, null, 0.0F, var113, var60, 48 | Painter.$stable, 56);
         ComposerKt.sourceInformationMarkerEnd(var60);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var51.endNode();
         ComposerKt.sourceInformationMarkerEnd(var51);
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var162 = var5.endRestartGroup();
      if (var162 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_99_0 = var0;
         final io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState recoveredCapture_99_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_99_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_99_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_99_4 = var4;
         final int recoveredCapture_99_5 = var6;
         final int recoveredCapture_99_6 = var7;
         var162.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_99_0, recoveredArgument_99_1) -> ReactionsComponentKt.ReactionsComponent$lambda$14(recoveredCapture_99_0, recoveredCapture_99_1, recoveredCapture_99_2, recoveredCapture_99_3, recoveredCapture_99_4, recoveredCapture_99_5, recoveredCapture_99_6, (androidx.compose.runtime.Composer)recoveredArgument_99_0, ((java.lang.Integer)recoveredArgument_99_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReactionsComponentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(913251333);
      ComposerKt.sourceInformation(var0, "C(ReactionsComponentPreview)143@5937L313:ReactionsComponent.kt#1wdz33");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(913251333, var1, -1, "io.intercom.android.sdk.helpcenter.articles.ReactionsComponentPreview (ReactionsComponent.kt:142)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ReactionsComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_100_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_100_0, recoveredArgument_100_1) -> ReactionsComponentKt.ReactionsComponentPreview$lambda$15(recoveredCapture_100_0, (androidx.compose.runtime.Composer)recoveredArgument_100_0, ((java.lang.Integer)recoveredArgument_100_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReactionComponentSadTappedPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2092315616);
      ComposerKt.sourceInformation(var0, "C(ReactionComponentSadTappedPreview)158@6330L407:ReactionsComponent.kt#1wdz33");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2092315616, var1, -1, "io.intercom.android.sdk.helpcenter.articles.ReactionComponentSadTappedPreview (ReactionsComponent.kt:157)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ReactionsComponentKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_95_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_95_0, recoveredArgument_95_1) -> ReactionsComponentKt.ReactionComponentSadTappedPreview$lambda$16(recoveredCapture_95_0, (androidx.compose.runtime.Composer)recoveredArgument_95_0, ((java.lang.Integer)recoveredArgument_95_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReactionComponentNeutralTappedPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(507405585);
      ComposerKt.sourceInformation(var0, "C(ReactionComponentNeutralTappedPreview)175@6821L411:ReactionsComponent.kt#1wdz33");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               507405585, var1, -1, "io.intercom.android.sdk.helpcenter.articles.ReactionComponentNeutralTappedPreview (ReactionsComponent.kt:174)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ReactionsComponentKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_94_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_94_0, recoveredArgument_94_1) -> ReactionsComponentKt.ReactionComponentNeutralTappedPreview$lambda$17(recoveredCapture_94_0, (androidx.compose.runtime.Composer)recoveredArgument_94_0, ((java.lang.Integer)recoveredArgument_94_1).intValue())));
      }
   }

   private static final float ReactionsComponent$lambda$13$lambda$12$lambda$0(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Dp)var1.getValue()).recovered$3537cef520440dc0();
   }

   private static final float ReactionsComponent$lambda$13$lambda$12$lambda$1(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Dp)var1.getValue()).recovered$3537cef520440dc0();
   }

   private static final float ReactionsComponent$lambda$13$lambda$12$lambda$2(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Dp)var1.getValue()).recovered$3537cef520440dc0();
   }

   private static final Unit ReactionsComponent$lambda$13$lambda$12$lambda$4$lambda$3(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ReactionsComponent$lambda$13$lambda$12$lambda$7$lambda$6(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ReactionsComponent$lambda$13$lambda$12$lambda$10$lambda$9(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ReactionsComponent$lambda$14(
      Modifier var0, ArticleViewState$ReactionState var1, Function0 var2, Function0 var3, Function0 var4, int var5, int var6, Composer var7, int var8
   ) {
      ReactionsComponent(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit ReactionsComponentPreview$lambda$15(int var0, Composer var1, int var2) {
      ReactionsComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ReactionComponentSadTappedPreview$lambda$16(int var0, Composer var1, int var2) {
      ReactionComponentSadTappedPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ReactionComponentNeutralTappedPreview$lambda$17(int var0, Composer var1, int var2) {
      ReactionComponentNeutralTappedPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
