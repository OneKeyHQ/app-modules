package io.intercom.android.sdk.survey.ui.components;

import android.content.Context;
import androidx.compose.animation.core.AnimateAsStateKt;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.foundation.ClickableKt;
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
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.ProgressIndicatorKt;
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
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.StrokeCap;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.survey.ProgressBarState;
import io.intercom.android.sdk.survey.SurveyUiColors;
import io.intercom.android.sdk.survey.SurveyViewModelKt;
import io.intercom.android.sdk.survey.TopBarState;
import io.intercom.android.sdk.survey.TopBarState$NoTopBarState;
import io.intercom.android.sdk.survey.TopBarState$SenderTopBarState;
import io.intercom.android.sdk.survey.model.SurveyCustomization;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a-\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\f",
   d2 = {
         "SurveyTopBar",
         "",
         "topBarState",
         "Lio/intercom/android/sdk/survey/TopBarState;",
         "onClose",
         "Lkotlin/Function0;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/survey/TopBarState;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "SurveyAvatarBar",
         "(Landroidx/compose/runtime/Composer;I)V",
         "NoTopBar",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSurveyTopBarComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyTopBarComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyTopBarComponentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,165:1\n87#2:166\n84#2,9:167\n94#2:289\n81#3,6:176\n88#3,6:191\n81#3,6:206\n88#3,6:221\n81#3,6:240\n88#3,6:255\n96#3:265\n96#3:270\n96#3:288\n402#4,9:182\n411#4:197\n402#4,9:212\n411#4:227\n402#4,9:246\n411#4:261\n412#4,2:263\n412#4,2:268\n412#4,2:286\n118#5:198\n118#5:199\n118#5:262\n118#5:267\n118#5:272\n118#5:273\n99#6,6:200\n99#6:229\n95#6,10:230\n106#6:266\n106#6:271\n75#7:228\n1047#8,6:274\n1047#8,6:280\n1047#8,6:290\n1047#8,6:296\n*S KotlinDebug\n*F\n+ 1 SurveyTopBarComponent.kt\nio/intercom/android/sdk/survey/ui/components/SurveyTopBarComponentKt\n*L\n50#1:166\n50#1:167,9\n50#1:289\n50#1:176,6\n50#1:191,6\n54#1:206,6\n54#1:221,6\n70#1:240,6\n70#1:255,6\n70#1:265\n54#1:270\n50#1:288\n50#1:182,9\n50#1:197\n54#1:212,9\n54#1:227\n70#1:246,9\n70#1:261\n70#1:263,2\n54#1:268,2\n50#1:286,2\n53#1:198\n57#1:199\n72#1:262\n84#1:267\n98#1:272\n127#1:273\n54#1:200,6\n70#1:229\n70#1:230,10\n70#1:266\n54#1:271\n64#1:228\n123#1:274,6\n128#1:280,6\n149#1:290,6\n162#1:296,6\n*E\n"
)
public final class SurveyTopBarComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyTopBar(
      @NotNull TopBarState var0, @NotNull Function0 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "topBarState");
      Intrinsics.checkNotNullParameter(var1, "onClose");
      Composer var107 = var3.startRestartGroup(-295801747);
      ComposerKt.sourceInformation(var107, "C(SurveyTopBar)P(2,1)49@2119L3492:SurveyTopBarComponent.kt#bmpoeo");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var107.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var107.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var107.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var107.getSkipping()) {
         var107.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-295801747, var6, -1, "io.intercom.android.sdk.survey.ui.components.SurveyTopBar (SurveyTopBarComponent.kt:48)");
         }

         int var9 = 14 & var6 >> 6;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var107, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var7 = Arrangement.INSTANCE.getTop();
         Horizontal var8 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var7, var8, var107, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var2;
         MeasurePolicy var13 = var11;
         Composer var14 = var107;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -263201435, "C52@2172L41,53@2222L1923:SurveyTopBarComponent.kt#bmpoeo");
         Modifier var10000 = Modifier.Companion;
         int var33 = 16;
         int var34 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var33)), var30, 6);
         androidx.compose.ui.Alignment.Vertical var108 = Alignment.Companion.getCenterVertically();
         Modifier var155 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var109 = SizeKt.fillMaxWidth$default(
            PaddingKt.recovered$f3b9f2e94c07c806(var155, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 2, null), 0.0F, 1, null
         );
         HorizontalOrVertical var111 = Arrangement.INSTANCE.getSpaceBetween();
         short var113 = 438;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var111, var108, var30, 14 & var113 >> 3 | 112 & var113 >> 3);
         Modifier var39 = var109;
         MeasurePolicy var40 = measurePolicy$ivx;
         Composer var41 = var30;
         int var42 = 112 & var113 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var41, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var41, 0));
         CompositionLocalMap localMap$iv$ivx = var41.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var41, var39);
         Function0 var47 = ComposeUiNode.Companion.getConstructor();
         Composer var48 = var41;
         int var49 = 6 | 896 & var42 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var48, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var48.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var48.startReusableNode();
         if (var48.getInserting()) {
            var48.createNode(var47);
         } else {
            var48.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var48);
         int var52/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var40, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var53 = 14 & var49 >> 6;
         Composer $composer$ivx = var48;
         int var55 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var56 = 6 | 112 & var113 >> 6;
         Composer var57 = $composer$ivx;
         RowScope var58 = RowScopeInstance.INSTANCE;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart(var57, 359747643, "C:SurveyTopBarComponent.kt#bmpoeo");
         TopBarState var60 = var0;
         if (var60 instanceof io.intercom.android.sdk.survey.TopBarState$SenderTopBarState) {
            var57.startReplaceGroup(359789399);
            ComposerKt.sourceInformation(var57, "63@2628L7,69@2907L666");
            CompositionLocal var61 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var62 = 0;
            int var63 = 0;
            ComposerKt.sourceInformationMarkerStart(var57, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var64 = var57.consume(var61);
            ComposerKt.sourceInformationMarkerEnd(var57);
            CharSequence var65 = Phrase.from((Context)var64, string.intercom_teammate_from_company)
               .put("name", ((io.intercom.android.sdk.survey.TopBarState$SenderTopBarState)var0).getSenderName())
               .put("company", ((io.intercom.android.sdk.survey.TopBarState$SenderTopBarState)var0).getAppConfig().getName())
               .format();
            androidx.compose.ui.Alignment.Vertical var149 = Alignment.Companion.getCenterVertically();
            int $changed$ivx = 384;
            int $i$f$Rowx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var57, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var145 = Modifier.Companion;
            androidx.compose.foundation.layout.Arrangement.Horizontal var148 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var148, var149, var57, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3);
            Modifier var68 = var145;
            MeasurePolicy var69 = measurePolicy$ivxx;
            Composer var70 = var57;
            int var71 = 112 & $changed$ivx << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var70, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var70, 0));
            CompositionLocalMap localMap$iv$ivxx = var70.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var70, var68);
            Function0 var76 = ComposeUiNode.Companion.getConstructor();
            Composer var77 = var70;
            int var78 = 6 | 896 & var71 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var77, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var77.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var77.startReusableNode();
            if (var77.getInserting()) {
               var77.createNode(var76);
            } else {
               var77.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var77);
            int var81/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var69, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var82 = 14 & var78 >> 6;
            Composer $composer$ivxx = var77;
            int var84/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var85 = 6 | 112 & $changed$ivx >> 6;
            Composer var86 = $composer$ivxx;
            RowScope var87 = RowScopeInstance.INSTANCE;
            int var88 = 0;
            ComposerKt.sourceInformationMarkerStart(var86, 428077530, "C70@2985L106,71@3116L39,72@3180L371:SurveyTopBarComponent.kt#bmpoeo");
            CircularAvatarComponentKt.recovered$7baf454f09f9b402(
               ((io.intercom.android.sdk.survey.TopBarState$SenderTopBarState)var0).getAvatar(), ColorKt.Color(((io.intercom.android.sdk.survey.TopBarState$SenderTopBarState)var0).getAppConfig().getSecondaryColor()), 0.0F, var86, 0, 4
            );
            Modifier var156 = Modifier.Companion;
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var156, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var86, 6);
            String var154 = var65.toString();
            long var91 = TextUnitKt.getSp(14);
            FontWeight var93 = FontWeight.Companion.getNormal();
            int var94 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            long var95 = ((io.intercom.android.sdk.survey.TopBarState$SenderTopBarState)var0).getSurveyUiColors().recovered$f126774a6443d8d2();
            TextKt.recovered$5b114ab32a4f4190(
               var154, null, var95, null, var91, null, var93, null, 0L, null, null, 0L, var94, false, 1, 0, null, null, var86, 1597440, 24960, 241578
            );
            ComposerKt.sourceInformationMarkerEnd(var86);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var77.endNode();
            ComposerKt.sourceInformationMarkerEnd(var77);
            ComposerKt.sourceInformationMarkerEnd(var70);
            ComposerKt.sourceInformationMarkerEnd(var57);
            var57.endReplaceGroup();
         } else {
            if (!(var60 instanceof io.intercom.android.sdk.survey.TopBarState$NoTopBarState)) {
               var57.startReplaceGroup(1535625002);
               var57.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var57.startReplaceGroup(360834719);
            ComposerKt.sourceInformation(var57, "83@3650L39");
            Modifier var157 = Modifier.Companion;
            int $this$dp$ivxx = 1;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var157, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var57, 6);
            var57.endReplaceGroup();
         }

         var57.startReplaceGroup(1535664197);
         ComposerKt.sourceInformation(var57, "88@3823L52,89@3918L46,87@3787L334");
         if (var0.getShowDismissButton()) {
            Painter var143 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var57, 0);
            String var153 = StringResources_androidKt.stringResource(string.intercom_dismiss, var57, 0);
            long var97 = var0.getSurveyUiColors().recovered$f126774a6443d8d2();
            Modifier var150 = ClickableKt.recovered$1c9660c41fd850dc(Modifier.Companion, false, null, null, null, var1, 15, null);
            IconKt.recovered$e00adffb7fe45b02(var143, var153, var150, var97, var57, Painter.$stable, 0);
         }

         var57.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var48.endNode();
         ComposerKt.sourceInformationMarkerEnd(var48);
         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var30.startReplaceGroup(-1116807584);
         ComposerKt.sourceInformation(var30, "");
         ProgressBarState var110 = var0.getProgressBarState();
         int var112 = 0;
         var30.startReplaceGroup(-1116806075);
         ComposerKt.sourceInformation(var30, "97@4236L41,98@4320L194,122@5258L29,127@5510L2,121@5202L379");
         if (var110.isVisible()) {
            Modifier var158 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var158, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var30, 6);
            State var115 = AnimateAsStateKt.animateFloatAsState(
               var110.getProgress(), AnimationSpecKt.tween$default(200, 0, null, 6, null), 0.0F, null, null, var30, 48, 28
            );
            long var99 = ColorExtensionsKt.recovered$41e561cae61044c5(var0.getSurveyUiColors().recovered$a7e7bdd99c6c8d3d())
               ? ColorKt.Color(1728053247)
               : ColorKt.Color(1291845632);
            SurveyUiColors var117 = var0.getSurveyUiColors();
            int var119 = 0;
            long var101 = Color.recovered$9eab66ea8c46e11c(var117.recovered$a7e7bdd99c6c8d3d(), var117.recovered$923dd289d74a1df8())
                  && ColorExtensionsKt.recovered$d9b2a0cefdbf2194(var117.recovered$a7e7bdd99c6c8d3d())
               ? ColorKt.Color(3439329279L)
               : (
                  Color.recovered$9eab66ea8c46e11c(var117.recovered$a7e7bdd99c6c8d3d(), var117.recovered$923dd289d74a1df8())
                        && ColorExtensionsKt.recovered$4c5bbe819723778c(var117.recovered$a7e7bdd99c6c8d3d())
                     ? ColorKt.Color(2147483648L)
                     : var117.recovered$923dd289d74a1df8()
               );
            Modifier var122 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int $this$dp$ivxxx = 0;
            int $i$f$getDpxxx = 0;
            float var118 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
            int var121 = StrokeCap.Companion.recovered$d76274c3207f4f6e();
            var30.startReplaceGroup(1535712593);
            ComposerKt.sourceInformation(var30, "CC(remember):SurveyTopBarComponent.kt#9igjgp");
            Composer var126 = var30;
            boolean var128 = var30.changed(var115);
            int var130 = 0;
            Object var132 = var126.rememberedValue();
            int var134 = 0;
            Object var159;
            if (!var128 && var132 != Composer.Companion.getEmpty()) {
               var159 = var132;
            } else {
               int var136 = 0;
               final androidx.compose.runtime.State recoveredExpressionCapture_1365_0 = var115;
               Object var139 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.SurveyTopBar$lambda$8$lambda$7$lambda$4$lambda$3(recoveredExpressionCapture_1365_0));
               var126.updateRememberedValue(var139);
               var159 = var139;
            }

            Function0 var124 = (Function0)var159;
            var30.endReplaceGroup();
            Function0 var160 = var124;
            Modifier var10001 = var122;
            long var10002 = var101;
            long var10003 = var99;
            int var10004 = var121;
            float var10005 = var118;
            var30.startReplaceGroup(1535720630);
            ComposerKt.sourceInformation(var30, "CC(remember):SurveyTopBarComponent.kt#9igjgp");
            Composer var127 = var30;
            boolean var129 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var127.rememberedValue();
            int var135/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10006;
            if (it$ivx == Composer.Companion.getEmpty()) {
               float var140 = var118;
               int var141 = var121;
               long var103 = var99;
               long var105 = var101;
               Modifier var142 = var122;
               Function0 var144 = var124;
               int var137 = 0;
               Function1 var147 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1366_0) -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.SurveyTopBar$lambda$8$lambda$7$lambda$6$lambda$5((androidx.compose.ui.graphics.drawscope.DrawScope) ((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredExpressionArgument_1366_0)));
               var160 = var144;
               var10001 = var142;
               var10002 = var105;
               var10003 = var103;
               var10004 = var141;
               var10005 = var140;
               Object var138 = var147;
               var127.updateRememberedValue(var138);
               var10006 = var138;
            } else {
               var10006 = it$ivx;
            }

            Function1 var125 = (Function1)var10006;
            var30.endReplaceGroup();
            ProgressIndicatorKt.recovered$2e468734cd9041b5(var160, var10001, var10002, var10003, var10004, var10005, var125, var30, 1769520, 0);
         }

         var30.endReplaceGroup();
         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var107);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var161 = var107.endRestartGroup();
      if (var161 != null) {
         final io.intercom.android.sdk.survey.TopBarState recoveredExpressionCapture_1367_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1367_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1367_2 = var2;
         final int recoveredExpressionCapture_1367_3 = var4;
         final int recoveredExpressionCapture_1367_4 = var5;
         var161.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1367_0, recoveredExpressionArgument_1367_1) -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.SurveyTopBar$lambda$9(recoveredExpressionCapture_1367_0, recoveredExpressionCapture_1367_1, recoveredExpressionCapture_1367_2, recoveredExpressionCapture_1367_3, recoveredExpressionCapture_1367_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1367_0), ((java.lang.Integer)recoveredExpressionArgument_1367_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyAvatarBar(@Nullable Composer var0, int var1) {
      Composer var10 = var0.startRestartGroup(1511683997);
      ComposerKt.sourceInformation(var10, "C(SurveyAvatarBar)148@6042L2,138@5673L378:SurveyTopBarComponent.kt#bmpoeo");
      if (var1 == 0 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1511683997, var1, -1, "io.intercom.android.sdk.survey.ui.components.SurveyAvatarBar (SurveyTopBarComponent.kt:137)");
         }

         Avatar var10000 = new io.intercom.android.sdk.models.Avatar$Builder().withInitials("VR").build();
         Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
         Avatar var2 = var10000;
         AppConfig var3 = SurveyComponentKt.getEmptyAppConfig();
         SurveyUiColors var4 = SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null));
         TopBarState var15 = new io.intercom.android.sdk.survey.TopBarState$SenderTopBarState(var2, "Vinesh", var3, false, var4, null, 32, null);
         var10.startReplaceGroup(-263655356);
         ComposerKt.sourceInformation(var10, "CC(remember):SurveyTopBarComponent.kt#9igjgp");
         Composer var12 = var10;
         boolean var13 = false;
         int var5 = 0;
         Object var6 = var12.rememberedValue();
         int var7 = 0;
         Object var17;
         if (var6 == Composer.Companion.getEmpty()) {
            TopBarState var9 = var15;
            int var8 = 0;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.SurveyAvatarBar$lambda$11$lambda$10());
            var15 = var9;
            Object var14 = var10001;
            var12.updateRememberedValue(var14);
            var17 = var14;
         } else {
            var17 = var6;
         }

         Function0 var11 = (Function0)var17;
         var10.endReplaceGroup();
         SurveyTopBar(var15, var11, null, var10, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var16 = var10.endRestartGroup();
      if (var16 != null) {
         final int recoveredExpressionCapture_1364_0 = var1;
         var16.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1364_0, recoveredExpressionArgument_1364_1) -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.SurveyAvatarBar$lambda$12(recoveredExpressionCapture_1364_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1364_0), ((java.lang.Integer)recoveredExpressionArgument_1364_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NoTopBar(@Nullable Composer var0, int var1) {
      Composer var10 = var0.startRestartGroup(1502798722);
      ComposerKt.sourceInformation(var10, "C(NoTopBar)161@6346L2,155@6106L249:SurveyTopBarComponent.kt#bmpoeo");
      if (var1 == 0 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1502798722, var1, -1, "io.intercom.android.sdk.survey.ui.components.NoTopBar (SurveyTopBarComponent.kt:154)");
         }

         TopBarState var10000 = new io.intercom.android.sdk.survey.TopBarState$NoTopBarState(
            true, SurveyViewModelKt.toSurveyUiColors(new SurveyCustomization(null, null, 3, null)), new ProgressBarState(false, 0.0F, 3, null)
         );
         var10.startReplaceGroup(-86743009);
         ComposerKt.sourceInformation(var10, "CC(remember):SurveyTopBarComponent.kt#9igjgp");
         Composer var3 = var10;
         boolean var4 = false;
         int var5 = 0;
         Object var6 = var3.rememberedValue();
         int var7 = 0;
         Object var13;
         if (var6 == Composer.Companion.getEmpty()) {
            TopBarState var9 = var10000;
            int var8 = 0;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.NoTopBar$lambda$14$lambda$13());
            var10000 = var9;
            Object var11 = var10001;
            var3.updateRememberedValue(var11);
            var13 = var11;
         } else {
            var13 = var6;
         }

         Function0 var2 = (Function0)var13;
         var10.endReplaceGroup();
         SurveyTopBar(var10000, var2, null, var10, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var10.endRestartGroup();
      if (var12 != null) {
         final int recoveredExpressionCapture_1361_0 = var1;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1361_0, recoveredExpressionArgument_1361_1) -> io.intercom.android.sdk.survey.ui.components.SurveyTopBarComponentKt.NoTopBar$lambda$15(recoveredExpressionCapture_1361_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1361_0), ((java.lang.Integer)recoveredExpressionArgument_1361_1).intValue())));
      }
   }

   private static final float SurveyTopBar$lambda$8$lambda$7$lambda$4$lambda$3(State var0) {
      return ((Number)var0.getValue()).floatValue();
   }

   private static final Unit SurveyTopBar$lambda$8$lambda$7$lambda$6$lambda$5(DrawScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$LinearProgressIndicator");
      return Unit.INSTANCE;
   }

   private static final Unit SurveyTopBar$lambda$9(TopBarState var0, Function0 var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      SurveyTopBar(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit SurveyAvatarBar$lambda$11$lambda$10() {
      return Unit.INSTANCE;
   }

   private static final Unit SurveyAvatarBar$lambda$12(int var0, Composer var1, int var2) {
      SurveyAvatarBar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NoTopBar$lambda$14$lambda$13() {
      return Unit.INSTANCE;
   }

   private static final Unit NoTopBar$lambda$15(int var0, Composer var1, int var2) {
      NoTopBar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
