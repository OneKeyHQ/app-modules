package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridDslKt;
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope;
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState;
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells;
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells.Fixed;
import androidx.compose.foundation.text.BasicTextFieldKt;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00004\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aM\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\bH\u0001¢\u0006\u0002\u0010\u000b\u001a)\u0010\f\u001a\u00020\u00012\b\b\u0001\u0010\r\u001a\u00020\u000e2\u0010\b\u0002\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0010H\u0003¢\u0006\u0002\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0014",
   d2 = {
         "GifGrid",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "gifs",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "onGifClick",
         "Lkotlin/Function1;",
         "onGifSearchQueryChange",
         "",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "GifGridIcon",
         "iconId",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "(ILkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "PreviewGifGrid",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nGifGrid.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/GifGridKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 9 LazyStaggeredGridDsl.kt\nandroidx/compose/foundation/lazy/staggeredgrid/LazyStaggeredGridDslKt\n+ 10 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,172:1\n1047#2,6:173\n1047#2,6:179\n1047#2,6:253\n1047#2,6:259\n1047#2,6:266\n1047#2,6:280\n1047#2,6:291\n75#3:185\n87#4:186\n83#4,10:187\n94#4:289\n81#5,6:197\n88#5,6:212\n81#5,6:231\n88#5,6:246\n96#5:274\n96#5:288\n402#6,9:203\n411#6:218\n402#6,9:237\n411#6:252\n412#6,2:272\n412#6,2:286\n118#7:219\n118#7:220\n118#7:265\n118#7:276\n118#7:277\n118#7:278\n118#7:279\n118#7:290\n99#8:221\n96#8,9:222\n106#8:275\n425#9,10:297\n436#9,4:308\n1#10:307\n*S KotlinDebug\n*F\n+ 1 GifGrid.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/GifGridKt\n*L\n54#1:173,6\n55#1:179,6\n84#1:253,6\n75#1:259,6\n115#1:266,6\n131#1:280,6\n150#1:291,6\n56#1:185\n57#1:186\n57#1:187,10\n57#1:289\n57#1:197,6\n57#1:212,6\n61#1:231,6\n61#1:246,6\n61#1:274\n57#1:288\n57#1:203,9\n57#1:218\n61#1:237,9\n61#1:252\n61#1:272,2\n57#1:286,2\n64#1:219\n69#1:220\n113#1:265\n123#1:276\n128#1:277\n129#1:278\n130#1:279\n146#1:290\n61#1:221\n61#1:222,9\n61#1:275\n132#1:297,10\n132#1:308,4\n132#1:307\n*E\n"
)
public final class GifGridKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GifGrid(
      @Nullable Modifier var0, @NotNull List var1, @NotNull Function1 var2, @NotNull Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "gifs");
      Intrinsics.checkNotNullParameter(var2, "onGifClick");
      Intrinsics.checkNotNullParameter(var3, "onGifSearchQueryChange");
      Composer var104 = var4.startRestartGroup(2027814826);
      ComposerKt.sourceInformation(var104, "C(GifGrid)P(1)53@2417L31,54@2477L39,55@2578L7,56@2590L3364:GifGrid.kt#yb6g9");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var104.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var104.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var104.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var104.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var104.getSkipping()) {
         var104.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2027814826, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGrid (GifGrid.kt:52)");
         }

         var104.startReplaceGroup(-1549196238);
         ComposerKt.sourceInformation(var104, "CC(remember):GifGrid.kt#9igjgp");
         Composer var10 = var104;
         boolean var11 = false;
         int var12 = 0;
         Object var13 = var10.rememberedValue();
         int var14 = 0;
         Object var10000;
         if (var13 == Composer.Companion.getEmpty()) {
            int var15 = 0;
            Object var120 = SnapshotStateKt.mutableStateOf$default("", null, 2, null);
            var10.updateRememberedValue(var120);
            var10000 = var120;
         } else {
            var10000 = var13;
         }

         MutableState var9 = (MutableState)var10000;
         var104.endReplaceGroup();
         MutableState var8 = var9;
         var104.startReplaceGroup(-1549194310);
         ComposerKt.sourceInformation(var104, "CC(remember):GifGrid.kt#9igjgp");
         Composer var108 = var104;
         boolean var111 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var108.rememberedValue();
         int var121/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var171;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var16 = 0;
            Object var123 = InteractionSourceKt.MutableInteractionSource();
            var108.updateRememberedValue(var123);
            var171 = var123;
         } else {
            var171 = it$ivx;
         }

         MutableInteractionSource var106 = (MutableInteractionSource)var171;
         var104.endReplaceGroup();
         MutableInteractionSource var105 = var106;
         CompositionLocal var109 = CompositionLocalsKt.getLocalSoftwareKeyboardController();
         int var112 = 0;
         int var115 = 0;
         ComposerKt.sourceInformationMarkerStart(var104, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var118 = var104.consume(var109);
         ComposerKt.sourceInformationMarkerEnd(var104);
         SoftwareKeyboardController var107 = (SoftwareKeyboardController)var118;
         byte var119 = 0;
         int var122 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var104, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var110 = Modifier.Companion;
         Vertical var113 = Arrangement.INSTANCE.getTop();
         Horizontal var116 = Alignment.Companion.getStart();
         MeasurePolicy var124 = ColumnKt.columnMeasurePolicy(var113, var116, var104, 14 & var119 >> 3 | 112 & var119 >> 3);
         Modifier var17 = var110;
         MeasurePolicy var18 = var124;
         Composer var19 = var104;
         int var20 = 112 & var119 << 3;
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
         int var34 = 6 | 112 & var119 >> 6;
         Composer var35 = var32;
         ColumnScope var36 = ColumnScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -1029925500, "C65@2897L6,66@2972L6,60@2721L2699,122@5429L40,130@5785L163,123@5478L470:GifGrid.kt#yb6g9");
         var35.startReplaceGroup(-1834341957);
         ComposerKt.sourceInformation(var35, "58@2665L37");
         if (Intrinsics.areEqual(var8.getValue(), "intercom version")) {
            TextKt.recovered$5b114ab32a4f4190(
               "18.10.0", null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var35, 6, 0, 262142
            );
         }

         var35.endReplaceGroup();
         Modifier var172 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var38 = 8;
         int var39 = 0;
         Modifier var173 = BackgroundKt.recovered$60bc47fb440df483(
            PaddingKt.recovered$f3b9f2e94c07c806(var172, Dp.recovered$95b3d103663bf060(var38), 0.0F, 2, null),
            Color.recovered$41aa0ee03c2282fb(
               IntercomTheme.INSTANCE.getColors(var35, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.05F, 0.0F, 0.0F, 0.0F, 14, null
            ),
            IntercomTheme.INSTANCE.getShapes(var35, IntercomTheme.$stable).getSmall()
         );
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         float var10001 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         int $this$dp$ivxx = 12;
         int $i$f$getDpxx = 0;
         Modifier var127 = PaddingKt.recovered$904403cea4c49d95(var173, var10001, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         androidx.compose.ui.Alignment.Vertical var40 = Alignment.Companion.getCenterVertically();
         short var41 = 384;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var132 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var132, var40, var35, 14 & var41 >> 3 | 112 & var41 >> 3);
         Modifier var44 = var127;
         MeasurePolicy var45 = measurePolicy$ivx;
         Composer var46 = var35;
         int var47 = 112 & var41 << 3;
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
         int var61 = 6 | 112 & var41 >> 6;
         Composer var62 = $composer$ivx;
         RowScope var63 = RowScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var62, -1806465185, "C73@3251L10,73@3296L6,83@3740L74,74@3350L108,87@3849L1196,71@3151L1894,112@5058L39:GifGrid.kt#yb6g9"
         );
         String var65 = (String)var8.getValue();
         TextStyle var66 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var62, IntercomTheme.$stable).getType04(),
            IntercomTheme.INSTANCE.getColors(var62, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            0L,
            null,
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
            16777214,
            null
         );
         Modifier var67 = RowScope.weight$default(var63, Modifier.Companion, 1.0F, false, 2, null);
         KeyboardOptions var68 = KeyboardOptions.recovered$fde8a67d1873b6f2(
            KeyboardOptions.Companion.getDefault(), 0, null, 0, ImeAction.Companion.recovered$99e7e34e4aa177ff(), null, null, null, 119, null
         );
         Object var174 = null;
         Object var181 = null;
         Object var10002 = null;
         Object var10003 = null;
         var62.startReplaceGroup(-1582277061);
         ComposerKt.sourceInformation(var62, "CC(remember):GifGrid.kt#9igjgp");
         Composer var69 = var62;
         boolean var70 = var62.changed(var107);
         int $i$f$cachexx = 0;
         Object it$ivxx = var69.rememberedValue();
         int var73/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10004;
         if (!var70 && it$ivxx != Composer.Companion.getEmpty()) {
            var10004 = it$ivxx;
         } else {
            Object var74 = null;
            Object var75 = null;
            Object var76 = null;
            Object var77 = null;
            int var80 = 0;
            final androidx.compose.ui.platform.SoftwareKeyboardController recoveredExpressionCapture_575_0 = var107;
            Function1 var81 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_575_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGrid$lambda$12$lambda$8$lambda$3$lambda$2(recoveredExpressionCapture_575_0, (androidx.compose.foundation.text.KeyboardActionScope) ((androidx.compose.foundation.text.KeyboardActionScope)recoveredExpressionArgument_575_0)));
            var174 = var77;
            var181 = var76;
            var10002 = var75;
            var10003 = var74;
            Object var82 = var81;
            var69.updateRememberedValue(var82);
            var10004 = var82;
         }

         Function1 var83 = (Function1)var10004;
         var62.endReplaceGroup();
         Object var96 = null;
         byte var97 = 47;
         Object var98 = null;
         Function1 var99 = var83;
         Object var100 = var10003;
         Object var101 = var10002;
         Object var102 = var181;
         Object var103 = var174;
         KeyboardActions var84 = new KeyboardActions(
            (Function1)var103, (Function1)var102, (Function1)var101, (Function1)var100, var99, (Function1)var98, var97, (DefaultConstructorMarker)var96
         );
         String var175 = var65;
         var62.startReplaceGroup(-1582289507);
         ComposerKt.sourceInformation(var62, "CC(remember):GifGrid.kt#9igjgp");
         Composer var158 = var62;
         boolean var160 = (var7 & 7168) == 2048;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var158.rememberedValue();
         int var164/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var183;
         if (!var160 && it$ivxxx != Composer.Companion.getEmpty()) {
            var183 = it$ivxxx;
         } else {
            String var79 = var65;
            int var166 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_576_0 = var8;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_576_1 = var3;
            Function1 var182 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_576_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGrid$lambda$12$lambda$8$lambda$5$lambda$4(recoveredExpressionCapture_576_0, recoveredExpressionCapture_576_1, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_576_0)));
            var175 = var79;
            Object var167 = var182;
            var158.updateRememberedValue(var167);
            var183 = var167;
         }

         Function1 var168 = (Function1)var183;
         var62.endReplaceGroup();
         BasicTextFieldKt.BasicTextField(
            var175,
            var168,
            var67,
            false,
            false,
            var66,
            var68,
            var84,
            true,
            0,
            0,
            null,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1827261249, true, new GifGridKt$GifGrid$1$1$3(var8, var105), var62, 54),
            var62,
            100663296,
            196608,
            32280
         );
         Modifier var176 = Modifier.Companion;
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var176, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var62, 6);
         if (((CharSequence)var8.getValue()).length() > 0) {
            var62.startReplaceGroup(-1804551618);
            ComposerKt.sourceInformation(var62, "114@5202L108,114@5163L147");
            int var177 = drawable.intercom_close;
            var62.startReplaceGroup(-1582230243);
            ComposerKt.sourceInformation(var62, "CC(remember):GifGrid.kt#9igjgp");
            Composer var155 = var62;
            boolean var156 = (var7 & 7168) == 2048;
            int $i$f$cachexxxx = 0;
            Object it$ivxxxx = var155.rememberedValue();
            int var169/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
            Object var184;
            if (!var156 && it$ivxxxx != Composer.Companion.getEmpty()) {
               var184 = it$ivxxxx;
            } else {
               int var165 = var177;
               int var159 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_577_0 = var8;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_577_1 = var3;
               Function0 var78 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGrid$lambda$12$lambda$8$lambda$7$lambda$6(recoveredExpressionCapture_577_0, recoveredExpressionCapture_577_1));
               var177 = var165;
               Object var161 = var78;
               var155.updateRememberedValue(var161);
               var184 = var161;
            }

            Function0 var153 = (Function0)var184;
            var62.endReplaceGroup();
            GifGridIcon(var177, var153, var62, 0, 0);
            var62.endReplaceGroup();
         } else {
            var62.startReplaceGroup(-1804371167);
            ComposerKt.sourceInformation(var62, "119@5348L48");
            GifGridIcon(drawable.intercom_gif_search_icon, null, var62, 0, 2);
            var62.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var35);
         Modifier var178 = Modifier.Companion;
         int $this$dp$ivxxxx = 4;
         int $i$f$getDpxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var178, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), var35, 6);
         Modifier var129 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         Fixed var134 = new Fixed(3);
         int $this$dp$ivxxxxx = 8;
         int $i$f$getDpxxxxx = 0;
         PaddingValues var135 = PaddingKt.recovered$9fd290d401ad9a87(Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx));
         int $this$dp$ivxxxxxx = 8;
         int $i$f$getDpxxxxxx = 0;
         float var137 = Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx);
         int $this$dp$ivxxxxxxx = 8;
         int $i$f$getDpxxxxxxx = 0;
         HorizontalOrVertical var140 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxxx));
         StaggeredGridCells var179 = var134;
         Modifier var185 = var129;
         Object var186 = null;
         PaddingValues var187 = var135;
         boolean var188 = false;
         float var10005 = var137;
         androidx.compose.foundation.layout.Arrangement.Horizontal var10006 = var140;
         Object var10007 = null;
         boolean var10008 = false;
         Object var10009 = null;
         var35.startReplaceGroup(-1834240203);
         ComposerKt.sourceInformation(var35, "CC(remember):GifGrid.kt#9igjgp");
         Composer var145 = var35;
         boolean var146 = var104.changedInstance(var1) | (var7 & 896) == 256;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var145.rememberedValue();
         int var149/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var10010;
         if (!var146 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var10010 = it$ivxxxx;
         } else {
            Object var85 = null;
            boolean var86 = false;
            Object var87 = null;
            androidx.compose.foundation.layout.Arrangement.Horizontal var88 = var10006;
            float var89 = var137;
            boolean var90 = false;
            PaddingValues var91 = var135;
            Object var92 = null;
            Modifier var93 = var129;
            StaggeredGridCells var94 = var179;
            int var150 = 0;
            final java.util.List recoveredExpressionCapture_578_0 = var1;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_578_1 = var2;
            Function1 var95 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_578_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGrid$lambda$12$lambda$11$lambda$10(recoveredExpressionCapture_578_0, recoveredExpressionCapture_578_1, (androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope) ((androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope)recoveredExpressionArgument_578_0)));
            var179 = var94;
            var185 = var93;
            var186 = var92;
            var187 = var91;
            var188 = var90;
            var10005 = var89;
            var10006 = var88;
            var10007 = var87;
            var10008 = var86;
            var10009 = var85;
            Object var151 = var95;
            var145.updateRememberedValue(var151);
            var10010 = var151;
         }

         Function1 var143 = (Function1)var10010;
         var35.endReplaceGroup();
         LazyStaggeredGridDslKt.recovered$3ed1810454af3ada(
            var179,
            var185,
            (LazyStaggeredGridState)var186,
            var187,
            var188,
            var10005,
            var10006,
            (FlingBehavior)var10007,
            var10008,
            (OverscrollEffect)var10009,
            var143,
            var35,
            1772544,
            0,
            916
         );
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var104);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var180 = var104.endRestartGroup();
      if (var180 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_579_0 = var0;
         final java.util.List recoveredExpressionCapture_579_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_579_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_579_3 = var3;
         final int recoveredExpressionCapture_579_4 = var5;
         final int recoveredExpressionCapture_579_5 = var6;
         var180.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_579_0, recoveredExpressionArgument_579_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGrid$lambda$13(recoveredExpressionCapture_579_0, recoveredExpressionCapture_579_1, recoveredExpressionCapture_579_2, recoveredExpressionCapture_579_3, recoveredExpressionCapture_579_4, recoveredExpressionCapture_579_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_579_0), ((java.lang.Integer)recoveredExpressionArgument_579_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void GifGridIcon(@DrawableRes int var0, Function0 var1, Composer var2, int var3, int var4) {
      Composer var21 = var2.startRestartGroup(-1949834895);
      ComposerKt.sourceInformation(var21, "C(GifGridIcon)149@6248L49,152@6317L28,153@6376L6,143@6068L398:GifGrid.kt#yb6g9");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var21.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var21.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var21.getSkipping()) {
         var21.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1949834895, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridIcon (GifGrid.kt:142)");
         }

         Modifier var10000 = Modifier.Companion;
         int var7 = 16;
         int var8 = 0;
         Modifier var26 = TestTagKt.testTag(SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var7)), String.valueOf(var0));
         boolean var10001 = var1 != null;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var21.startReplaceGroup(-2076065795);
         ComposerKt.sourceInformation(var21, "CC(remember):GifGrid.kt#9igjgp");
         Composer var24 = var21;
         boolean var9 = (var5 & 112) == 32;
         int var10 = 0;
         Object var11 = var24.rememberedValue();
         int var12 = 0;
         Object var10005;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var10005 = var11;
         } else {
            Object var19 = null;
            Object var18 = null;
            Object var17 = null;
            boolean var16 = var10001;
            Modifier var15 = var26;
            int var13 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_580_0 = var1;
            Function0 var20 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGridIcon$lambda$15$lambda$14(recoveredExpressionCapture_580_0));
            var26 = var15;
            var10001 = var16;
            var10002 = var17;
            var10003 = var18;
            var10004 = var19;
            Object var14 = var20;
            var24.updateRememberedValue(var14);
            var10005 = var14;
         }

         Function0 var22 = (Function0)var10005;
         var21.endReplaceGroup();
         Modifier var6 = ClickableKt.recovered$1c9660c41fd850dc(
            var26, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var22, 14, null
         );
         Painter var23 = PainterResources_androidKt.painterResource(var0, var21, 14 & var5);
         long var25 = IntercomTheme.INSTANCE.getColors(var21, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
         IconKt.recovered$e00adffb7fe45b02(var23, null, var6, var25, var21, 48 | Painter.$stable, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var21.endRestartGroup();
      if (var27 != null) {
         final int recoveredExpressionCapture_581_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_581_1 = var1;
         final int recoveredExpressionCapture_581_2 = var3;
         final int recoveredExpressionCapture_581_3 = var4;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_581_0, recoveredExpressionArgument_581_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.GifGridIcon$lambda$16(recoveredExpressionCapture_581_0, recoveredExpressionCapture_581_1, recoveredExpressionCapture_581_2, recoveredExpressionCapture_581_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_581_0), ((java.lang.Integer)recoveredExpressionArgument_581_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewGifGrid(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1512591839);
      ComposerKt.sourceInformation(var2, "C(PreviewGifGrid)161@6535L198:GifGrid.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1512591839, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.PreviewGifGrid (GifGrid.kt:160)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$GifGridKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_582_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_582_0, recoveredExpressionArgument_582_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.GifGridKt.PreviewGifGrid$lambda$17(recoveredExpressionCapture_582_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_582_0), ((java.lang.Integer)recoveredExpressionArgument_582_1).intValue())));
      }
   }

   private static final Unit GifGrid$lambda$12$lambda$8$lambda$3$lambda$2(SoftwareKeyboardController var0, KeyboardActionScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$KeyboardActions");
      if (var0 != null) {
         var0.hide();
      }

      return Unit.INSTANCE;
   }

   private static final Unit GifGrid$lambda$12$lambda$8$lambda$5$lambda$4(MutableState var0, Function1 var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      var0.setValue(var2);
      var1.invoke(var2);
      return Unit.INSTANCE;
   }

   private static final Unit GifGrid$lambda$12$lambda$8$lambda$7$lambda$6(MutableState var0, Function1 var1) {
      var0.setValue("");
      var1.invoke("");
      return Unit.INSTANCE;
   }

   private static final Unit GifGrid$lambda$12$lambda$11$lambda$10(List var0, Function1 var1, LazyStaggeredGridScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$LazyVerticalStaggeredGrid");
      LazyStaggeredGridScope var3 = var2;
      Function2 var4 = null;
      Function2 var5 = null;
      int var6 = 0;
      var3.items(
         var0.size(),
         null,
         new GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$2(var0),
         null,
         ComposableLambdaKt.composableLambdaInstance(
            -1466459515,
            true,
            new GifGridKt$GifGrid$lambda$12$lambda$11$lambda$10$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$4(var0, var1)
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit GifGrid$lambda$13(Modifier var0, List var1, Function1 var2, Function1 var3, int var4, int var5, Composer var6, int var7) {
      GifGrid(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit GifGridIcon$lambda$15$lambda$14(Function0 var0) {
      if (var0 != null) {
         var0.invoke();
      }

      return Unit.INSTANCE;
   }

   private static final Unit GifGridIcon$lambda$16(int var0, Function0 var1, int var2, int var3, Composer var4, int var5) {
      GifGridIcon(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewGifGrid$lambda$17(int var0, Composer var1, int var2) {
      PreviewGifGrid(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
