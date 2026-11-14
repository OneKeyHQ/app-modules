package io.intercom.android.sdk.m5.home.ui.header;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ImageKt;
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
import androidx.compose.material3.IconKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import coil.compose.AsyncImagePainter;
import coil.compose.AsyncImagePainterKt;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.AvatarGroupKt;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader;
import io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.ThemeUtils;
import kotlin.Metadata;
import kotlin.Pair;
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
   d1 = "\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a-\u0010\u0007\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\r¨\u0006\u000f",
   d2 = {
         "HomeContentHeader",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "header",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;Landroidx/compose/runtime/Composer;II)V",
         "HomeErrorHeader",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;",
         "onCloseClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "HomeContentHeaderPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "HomeErrorHeaderPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeHeader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeader.kt\nio/intercom/android/sdk/m5/home/ui/header/HomeHeaderKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 8 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 9 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n*L\n1#1,239:1\n1047#2,6:240\n1047#2,6:246\n1047#2,6:327\n1047#2,6:333\n1047#2,6:373\n118#3:252\n118#3:253\n118#3:319\n118#3:320\n118#3:321\n118#3:326\n118#3:343\n118#3:344\n87#4:254\n84#4,9:255\n94#4:342\n81#5,6:264\n88#5,6:279\n81#5,6:296\n88#5,6:311\n96#5:324\n96#5:341\n81#5,6:351\n88#5,6:366\n81#5,6:389\n88#5,6:404\n96#5:413\n96#5:417\n402#6,9:270\n411#6:285\n402#6,9:302\n411#6:317\n412#6,2:322\n412#6,2:339\n402#6,9:357\n411#6:372\n402#6,9:395\n411#6,3:410\n412#6,2:415\n99#7:286\n96#7,9:287\n106#7:325\n99#7,6:345\n106#7:418\n75#8:318\n70#9:379\n67#9,9:380\n77#9:414\n*S KotlinDebug\n*F\n+ 1 HomeHeader.kt\nio/intercom/android/sdk/m5/home/ui/header/HomeHeaderKt\n*L\n51#1:240,6\n52#1:246,6\n115#1:327,6\n139#1:333,6\n165#1:373,6\n56#1:252\n57#1:253\n83#1:319\n84#1:320\n94#1:321\n96#1:326\n158#1:343\n159#1:344\n54#1:254\n54#1:255,9\n54#1:342\n54#1:264,6\n54#1:279,6\n59#1:296,6\n59#1:311,6\n59#1:324\n54#1:341\n154#1:351,6\n154#1:366,6\n164#1:389,6\n164#1:404,6\n164#1:413\n154#1:417\n54#1:270,9\n54#1:285\n59#1:302,9\n59#1:317\n59#1:322,2\n54#1:339,2\n154#1:357,9\n154#1:372\n164#1:395,9\n164#1:410,3\n154#1:415,2\n59#1:286\n59#1:287,9\n59#1:325\n154#1:345,6\n154#1:418\n64#1:318\n164#1:379\n164#1:380,9\n164#1:414\n*E\n"
)
public final class HomeHeaderKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeContentHeader(@Nullable Modifier var0, @NotNull io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "header");
      Composer var75 = var2.startRestartGroup(-1992208830);
      ComposerKt.sourceInformation(var75, "C(HomeContentHeader)P(1)49@2157L10,50@2195L40,51@2267L34,53@2307L3531:HomeHeader.kt#rkbaxi");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var75.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var75.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var75.getSkipping()) {
         var75.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1992208830, var5, -1, "io.intercom.android.sdk.m5.home.ui.header.HomeContentHeader (HomeHeader.kt:48)");
         }

         TextStyle var6 = IntercomTheme.INSTANCE.getTypography(var75, IntercomTheme.$stable).getType02();
         var75.startReplaceGroup(-1302174101);
         ComposerKt.sourceInformation(var75, "CC(remember):HomeHeader.kt#9igjgp");
         Composer var9 = var75;
         boolean var10 = false;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10000;
         if (var12 == Composer.Companion.getEmpty()) {
            int var14 = 0;
            Object var15 = SnapshotStateKt.mutableStateOf$default(var6, null, 2, null);
            var9.updateRememberedValue(var15);
            var10000 = var15;
         } else {
            var10000 = var12;
         }

         MutableState var8 = (MutableState)var10000;
         var75.endReplaceGroup();
         MutableState var7 = var8;
         var75.startReplaceGroup(-1302171803);
         ComposerKt.sourceInformation(var75, "CC(remember):HomeHeader.kt#9igjgp");
         Composer var83 = var75;
         boolean var89 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var83.rememberedValue();
         int var95/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var158;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var97 = 0;
            Object var98 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var83.updateRememberedValue(var98);
            var158 = var98;
         } else {
            var158 = it$ivx;
         }

         MutableState var77 = (MutableState)var158;
         var75.endReplaceGroup();
         MutableState var76 = var77;
         int var78 = 10;
         int var84 = 0;
         float var10002 = Dp.recovered$95b3d103663bf060(var78);
         int $this$dp$ivx = 32;
         int $i$f$getDpx = 0;
         Modifier var159 = PaddingKt.recovered$03b481d31d62885a(var0, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 5, null);
         int $this$dp$ivxx = 32;
         int $i$f$getDpxx = 0;
         float var10001 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         int $this$dp$ivxxx = 24;
         int $i$f$getDpxxx = 0;
         Modifier var82 = PaddingKt.recovered$03b481d31d62885a(var159, var10001, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxx), 0.0F, 10, null);
         byte var92 = 0;
         int var94 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var75, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var88 = Arrangement.INSTANCE.getTop();
         Horizontal var90 = Alignment.Companion.getStart();
         MeasurePolicy var96 = ColumnKt.columnMeasurePolicy(var88, var90, var75, 14 & var92 >> 3 | 112 & var92 >> 3);
         Modifier var99 = var82;
         MeasurePolicy var16 = var96;
         Composer var17 = var75;
         int var18 = 112 & var92 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var99);
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
         int var32 = 6 | 112 & var92 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 2058218217, "C58@2512L1387,95@3908L41:HomeHeader.kt#rkbaxi");
         Modifier var36 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         androidx.compose.ui.Alignment.Vertical var37 = Alignment.Companion.getCenterVertically();
         short var38 = 390;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var33, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var40 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var40, var37, var33, 14 & var38 >> 3 | 112 & var38 >> 3);
         Modifier var42 = var36;
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
         ComposerKt.sourceInformationMarkerStart(var60, 111321802, "C93@3850L39:HomeHeader.kt#rkbaxi");
         var60.startReplaceGroup(-827693262);
         ComposerKt.sourceInformation(var60, "63@2715L7,64@2764L119,70@2938L299,69@2901L679");
         if (var1.getShowLogo()) {
            CompositionLocal var63 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var64 = 0;
            int var65 = 0;
            ComposerKt.sourceInformationMarkerStart(var60, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var66 = var60.consume(var63);
            ComposerKt.sourceInformationMarkerEnd(var60);
            Context var67 = (Context)var66;
            String var148 = ThemeUtils.INSTANCE.selectUrlForThemeCompose$intercom_sdk_base_release(var1.getLogoUrl(), var1.getLogoDarkUrl(), var60, 384);
            AsyncImagePainter var150 = AsyncImagePainterKt.recovered$8ecdef7ed4026761(
               new Builder(var67).data(var148).crossfade(true).build(), IntercomImageLoaderKt.getImageLoader(var67), null, null, null, 0, null, var60, 0, 124
            );
            ContentScale var151 = ContentScale.Companion.getFillHeight();
            Alignment var152 = Alignment.Companion.getCenterStart();
            Modifier var160 = RowScope.weight$default(var61, Modifier.Companion, 1.0F, false, 2, null);
            int $this$dp$ivxxxx = 16;
            int $i$f$getDpxxxx = 0;
            Modifier var161 = PaddingKt.recovered$03b481d31d62885a(var160, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 11, null);
            int $this$dp$ivxxxxx = 32;
            int $i$f$getDpxxxxx = 0;
            Modifier var70 = SizeKt.recovered$3f6c6a76aa292717(var161, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx));
            ImageKt.Image(var150, null, var70, var152, var151, 0.0F, null, var60, 27696, 96);
         }

         var60.endReplaceGroup();
         var60.startReplaceGroup(-827663462);
         ComposerKt.sourceInformation(var60, "87@3649L33");
         if (var1.getShowAvatars()) {
            AvatarGroupKt.recovered$813c81fac421be56(var1.getAdminsAvatars(), null, 0.0F, 0L, var60, 0, 14);
         }

         var60.endReplaceGroup();
         var60.startReplaceGroup(-827660195);
         ComposerKt.sourceInformation(var60, "90@3749L38");
         if (!var1.getShowLogo()) {
            SpacerKt.Spacer(RowScope.weight$default(var61, Modifier.Companion, 1.0F, false, 2, null), var60, 0);
         }

         var60.endReplaceGroup();
         Modifier var162 = Modifier.Companion;
         int $this$dp$ivxxxx = 24;
         int $i$f$getDpxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$111ef94c93e78c04(var162, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), var60, 6);
         ComposerKt.sourceInformationMarkerEnd(var60);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var51.endNode();
         ComposerKt.sourceInformationMarkerEnd(var51);
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var33);
         Modifier var163 = Modifier.Companion;
         int $this$dp$ivxxxxx = 48;
         int $i$f$getDpxxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var163, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx)), var33, 6);
         var33.startReplaceGroup(-2011771929);
         ComposerKt.sourceInformation(var33, "");
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var110 = var1.getGreeting();
         int var101 = 0;
         var33.startReplaceGroup(-2011770823);
         ComposerKt.sourceInformation(var33, "114@4740L109,110@4533L335");
         if (!StringsKt.isBlank(var110.getText())) {
            var33.startReplaceGroup(-827645376);
            ComposerKt.sourceInformation(var33, "*101@4218L19");
            int var105 = 0;
            boolean var112 = ThemeUtils.INSTANCE.isDarkModeCompose$intercom_sdk_base_release(var33, 6);
            String var114 = var112 && var110.getTextColorDark() != null ? var110.getTextColorDark() : var110.getTextColorLight();
            Pair var116 = new Pair(var114, var110.getOpacity());
            var33.endReplaceGroup();
            Pair var120 = var116;
            String var117 = (String)var120.component1();
            float var122 = ((Number)var120.component2()).floatValue();
            String var103 = var110.getText();
            TextStyle var106 = (TextStyle)var7.getValue();
            long var71 = ColorExtensionsKt.toComposeColor(var117, var122);
            Object var164 = null;
            String var167 = var103;
            long var169 = var71;
            TextStyle var10003 = var106;
            var33.startReplaceGroup(-827627186);
            ComposerKt.sourceInformation(var33, "CC(remember):HomeHeader.kt#9igjgp");
            Composer var124 = var33;
            boolean var126 = false;
            int $i$f$cachexx = 0;
            Object it$ivxx = var124.rememberedValue();
            int var132/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var10004;
            if (it$ivxx == Composer.Companion.getEmpty()) {
               TextStyle var134 = var106;
               long var73 = var71;
               String var136 = var103;
               Object var138 = null;
               int var140 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_953_0 = var76;
               Function1 var142 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_953_0) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeContentHeader$lambda$11$lambda$6$lambda$5$lambda$4(recoveredExpressionCapture_953_0, ((java.lang.Boolean)recoveredExpressionArgument_953_0).booleanValue()));
               var164 = var138;
               var167 = var136;
               var169 = var73;
               var10003 = var134;
               Object var144 = var142;
               var124.updateRememberedValue(var144);
               var10004 = var144;
            } else {
               var10004 = it$ivxx;
            }

            Function1 var146 = (Function1)var10004;
            var33.endReplaceGroup();
            WrapReportingTextKt.recovered$e51f4221e6440197((Modifier)var164, var167, var169, var10003, var146, var33, 24576, 1);
         }

         var33.endReplaceGroup();
         var33.endReplaceGroup();
         var33.startReplaceGroup(-2011741756);
         ComposerKt.sourceInformation(var33, "");
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$ColoredText var111 = var1.getIntro();
         int var102 = 0;
         var33.startReplaceGroup(-2011740743);
         ComposerKt.sourceInformation(var33, "138@5680L109,134@5473L335");
         if (!StringsKt.isBlank(var111.getText())) {
            var33.startReplaceGroup(-827615296);
            ComposerKt.sourceInformation(var33, "*125@5158L19");
            int var107 = 0;
            boolean var113 = ThemeUtils.INSTANCE.isDarkModeCompose$intercom_sdk_base_release(var33, 6);
            String var115 = var113 && var111.getTextColorDark() != null ? var111.getTextColorDark() : var111.getTextColorLight();
            Pair var118 = new Pair(var115, var111.getOpacity());
            var33.endReplaceGroup();
            Pair var121 = var118;
            String var119 = (String)var121.component1();
            float var123 = ((Number)var121.component2()).floatValue();
            String var104 = var111.getText();
            TextStyle var108 = (TextStyle)var7.getValue();
            long var156 = ColorExtensionsKt.toComposeColor(var119, var123);
            Object var165 = null;
            String var168 = var104;
            long var170 = var156;
            TextStyle var171 = var108;
            var33.startReplaceGroup(-827597106);
            ComposerKt.sourceInformation(var33, "CC(remember):HomeHeader.kt#9igjgp");
            Composer var125 = var33;
            boolean var127 = false;
            int $i$f$cachexx = 0;
            Object it$ivxx = var125.rememberedValue();
            int var133/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var172;
            if (it$ivxx == Composer.Companion.getEmpty()) {
               TextStyle var135 = var108;
               long var157 = var156;
               String var137 = var104;
               Object var139 = null;
               int var141 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_954_0 = var76;
               Function1 var143 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_954_0) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeContentHeader$lambda$11$lambda$10$lambda$9$lambda$8(recoveredExpressionCapture_954_0, ((java.lang.Boolean)recoveredExpressionArgument_954_0).booleanValue()));
               var165 = var139;
               var168 = var137;
               var170 = var157;
               var171 = var135;
               Object var145 = var143;
               var125.updateRememberedValue(var145);
               var172 = var145;
            } else {
               var172 = it$ivxx;
            }

            Function1 var147 = (Function1)var172;
            var33.endReplaceGroup();
            WrapReportingTextKt.recovered$e51f4221e6440197((Modifier)var165, var168, var170, var171, var147, var33, 24576, 1);
         }

         var33.endReplaceGroup();
         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var75);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var166 = var75.endRestartGroup();
      if (var166 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_955_0 = var0;
         final io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader recoveredExpressionCapture_955_1 = var1;
         final int recoveredExpressionCapture_955_2 = var3;
         final int recoveredExpressionCapture_955_3 = var4;
         var166.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_955_0, recoveredExpressionArgument_955_1) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeContentHeader$lambda$12(recoveredExpressionCapture_955_0, recoveredExpressionCapture_955_1, recoveredExpressionCapture_955_2, recoveredExpressionCapture_955_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_955_0), ((java.lang.Integer)recoveredExpressionArgument_955_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeErrorHeader(
      @Nullable Modifier var0, @NotNull io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader var1, @NotNull Function0 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "header");
      Intrinsics.checkNotNullParameter(var2, "onCloseClick");
      Composer var74 = var3.startRestartGroup(964565742);
      ComposerKt.sourceInformation(var74, "C(HomeErrorHeader)P(1)153@6000L815:HomeHeader.kt#rkbaxi");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var74.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var74.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var74.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var74.getSkipping()) {
         var74.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(964565742, var6, -1, "io.intercom.android.sdk.m5.home.ui.header.HomeErrorHeader (HomeHeader.kt:152)");
         }

         Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), ColorExtensionsKt.toComposeColor$default(var1.getBackgroundColor(), 0.0F, 1, null), null, 2, null
         );
         int var8 = 16;
         int var9 = 0;
         Modifier var87 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var8), 0.0F, 2, null);
         int $this$dp$ivx = 56;
         int $i$f$getDpx = 0;
         Modifier var7 = SizeKt.recovered$3f6c6a76aa292717(var87, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         androidx.compose.ui.Alignment.Vertical var76 = Alignment.Companion.getCenterVertically();
         androidx.compose.foundation.layout.Arrangement.Horizontal var78 = Arrangement.INSTANCE.getStart();
         short var10 = 432;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var74, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var12 = RowKt.rowMeasurePolicy(var78, var76, var74, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var7;
         MeasurePolicy var14 = var12;
         Composer var15 = var74;
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
         ComposerKt.sourceInformationMarkerStart(var31, -871604488, "C:HomeHeader.kt#rkbaxi");
         var31.startReplaceGroup(941715051);
         ComposerKt.sourceInformation(var31, "*164@6429L18,163@6378L421");
         String var34 = var1.getForegroundColor();
         int var35 = 0;
         Modifier var88 = Modifier.Companion;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var31.startReplaceGroup(1204041954);
         ComposerKt.sourceInformation(var31, "CC(remember):HomeHeader.kt#9igjgp");
         Composer var36 = var31;
         boolean var37 = (var6 & 896) == 256;
         int var38 = 0;
         Object var39 = var36.rememberedValue();
         int var40 = 0;
         Object var10005;
         if (!var37 && var39 != Composer.Companion.getEmpty()) {
            var10005 = var39;
         } else {
            Object var41 = null;
            Object var42 = null;
            Object var43 = null;
            boolean var44 = false;
            Modifier var45 = var88;
            int var46 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_957_0 = var2;
            Function0 var47 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeErrorHeader$lambda$17$lambda$16$lambda$14$lambda$13(recoveredExpressionCapture_957_0));
            var88 = var45;
            var10001 = var44;
            var10002 = var43;
            var10003 = var42;
            var10004 = var41;
            Object var48 = var47;
            var36.updateRememberedValue(var48);
            var10005 = var48;
         }

         Function0 var49 = (Function0)var10005;
         var31.endReplaceGroup();
         Modifier var86 = ClickableKt.recovered$1c9660c41fd850dc(
            var88, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var49, 15, null
         );
         byte var81 = 0;
         int var82 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var31, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var79 = Alignment.Companion.getTopStart();
         boolean var80 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var79, var80);
         Modifier var84 = var86;
         MeasurePolicy var85 = measurePolicy$ivx;
         Composer var50 = var31;
         int var51 = 112 & var81 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var50, 0));
         CompositionLocalMap localMap$iv$ivx = var50.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var50, var84);
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
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var85, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var62 = 14 & var58 >> 6;
         Composer $composer$ivx = var57;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var65 = 6 | 112 & var81 >> 6;
         Composer var66 = $composer$ivx;
         BoxScope var67 = BoxScopeInstance.INSTANCE;
         int var68 = 0;
         ComposerKt.sourceInformationMarkerStart(var66, -56617628, "C168@6582L52,169@6677L44,166@6481L304:HomeHeader.kt#rkbaxi");
         Modifier var69 = var67.align(Modifier.Companion, Alignment.Companion.getCenter());
         Painter var70 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var66, 0);
         String var71 = StringResources_androidKt.stringResource(string.intercom_close, var66, 0);
         long var72 = ColorExtensionsKt.toComposeColor$default(var34, 0.0F, 1, null);
         IconKt.recovered$e00adffb7fe45b02(var70, var71, var69, var72, var66, Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var66);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var57.endNode();
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var31.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var74);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var89 = var74.endRestartGroup();
      if (var89 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_958_0 = var0;
         final io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader recoveredExpressionCapture_958_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_958_2 = var2;
         final int recoveredExpressionCapture_958_3 = var4;
         final int recoveredExpressionCapture_958_4 = var5;
         var89.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_958_0, recoveredExpressionArgument_958_1) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeErrorHeader$lambda$18(recoveredExpressionCapture_958_0, recoveredExpressionCapture_958_1, recoveredExpressionCapture_958_2, recoveredExpressionCapture_958_3, recoveredExpressionCapture_958_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_958_0), ((java.lang.Integer)recoveredExpressionArgument_958_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeContentHeaderPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1555491493);
      ComposerKt.sourceInformation(var2, "C(HomeContentHeaderPreview)180@6895L1746:HomeHeader.kt#rkbaxi");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1555491493, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.HomeContentHeaderPreview (HomeHeader.kt:179)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderKt.INSTANCE.recovered$1400af5e34386102(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_956_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_956_0, recoveredExpressionArgument_956_1) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeContentHeaderPreview$lambda$19(recoveredExpressionCapture_956_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_956_0), ((java.lang.Integer)recoveredExpressionArgument_956_1).intValue())));
      }
   }

   @Preview(name = "Error header - Light Mode", uiMode = 16)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeErrorHeaderPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-484536790);
      ComposerKt.sourceInformation(var2, "C(HomeErrorHeaderPreview)226@8786L300:HomeHeader.kt#rkbaxi");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-484536790, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.HomeErrorHeaderPreview (HomeHeader.kt:225)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderKt.INSTANCE.recovered$54bdcf11cafa5012(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_959_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_959_0, recoveredExpressionArgument_959_1) -> io.intercom.android.sdk.m5.home.ui.header.HomeHeaderKt.HomeErrorHeaderPreview$lambda$20(recoveredExpressionCapture_959_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_959_0), ((java.lang.Integer)recoveredExpressionArgument_959_1).intValue())));
      }
   }

   private static final Unit HomeContentHeader$lambda$11$lambda$6$lambda$5$lambda$4(MutableState var0, boolean var1) {
      var0.setValue((Boolean)var0.getValue() | var1);
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentHeader$lambda$11$lambda$10$lambda$9$lambda$8(MutableState var0, boolean var1) {
      var0.setValue((Boolean)var0.getValue() | var1);
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentHeader$lambda$12(Modifier var0, io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader var1, int var2, int var3, Composer var4, int var5) {
      HomeContentHeader(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit HomeErrorHeader$lambda$17$lambda$16$lambda$14$lambda$13(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit HomeErrorHeader$lambda$18(Modifier var0, io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      HomeErrorHeader(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentHeaderPreview$lambda$19(int var0, Composer var1, int var2) {
      HomeContentHeaderPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HomeErrorHeaderPreview$lambda$20(int var0, Composer var1, int var2) {
      HomeErrorHeaderPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
