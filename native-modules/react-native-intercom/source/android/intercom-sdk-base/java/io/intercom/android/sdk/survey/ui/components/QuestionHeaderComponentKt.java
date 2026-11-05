package io.intercom.android.sdk.survey.ui.components;

import androidx.annotation.StringRes;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.survey.ValidationError;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.block.BlockRenderTextStyle;
import io.intercom.android.sdk.survey.block.BlockViewKt;
import io.intercom.android.sdk.survey.block.SuffixText;
import io.intercom.android.sdk.survey.ui.components.validation.ValidationErrorComponentKt;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\u001aj\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0015\b\u0002\u0010\u000f\u001a\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0010¢\u0006\u0002\b\u00112\n\b\u0003\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0001¢\u0006\u0004\b\u0014\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0017\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0017¨\u0006\u0019",
   d2 = {
         "QuestionHeader",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "description",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "isRequired",
         "",
         "validationError",
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "fontWeight",
         "Landroidx/compose/ui/text/font/FontWeight;",
         "fontSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "error",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "titleStringRes",
         "",
         "QuestionHeader-n1tc1qA",
         "(Ljava/util/List;Lio/intercom/android/sdk/ui/common/StringProvider;ZLio/intercom/android/sdk/survey/ValidationError;Landroidx/compose/ui/text/font/FontWeight;JLkotlin/jvm/functions/Function2;Ljava/lang/Integer;Landroidx/compose/runtime/Composer;II)V",
         "HeaderWithError",
         "(Landroidx/compose/runtime/Composer;I)V",
         "HeaderWithoutError",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nQuestionHeaderComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuestionHeaderComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionHeaderComponentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,141:1\n87#2:142\n83#2,10:143\n94#2:188\n87#2:189\n84#2,9:190\n94#2:224\n81#3,6:153\n88#3,6:168\n96#3:187\n81#3,6:199\n88#3,6:214\n96#3:223\n402#4,9:159\n411#4:174\n412#4,2:185\n402#4,9:205\n411#4,3:220\n1563#5:175\n1634#5,3:176\n1878#5,3:179\n118#6:182\n118#6:184\n1#7:183\n*S KotlinDebug\n*F\n+ 1 QuestionHeaderComponent.kt\nio/intercom/android/sdk/survey/ui/components/QuestionHeaderComponentKt\n*L\n42#1:142\n42#1:143,10\n42#1:188\n127#1:189\n127#1:190,9\n127#1:224\n42#1:153,6\n42#1:168,6\n42#1:187\n127#1:199,6\n127#1:214,6\n127#1:223\n42#1:159,9\n42#1:174\n42#1:185,2\n127#1:205,9\n127#1:220,3\n54#1:175\n54#1:176,3\n55#1:179,3\n90#1:182\n98#1:184\n*E\n"
)
public final class QuestionHeaderComponentKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void recovered$5a429c657b5a276d(
      @NotNull List var0,
      @Nullable StringProvider var1,
      boolean var2,
      @NotNull ValidationError var3,
      @NotNull FontWeight var4,
      long var5,
      @Nullable Function2 var7,
      @StringRes @Nullable Integer var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var0, "title");
      Intrinsics.checkNotNullParameter(var3, "validationError");
      Intrinsics.checkNotNullParameter(var4, "fontWeight");
      var9 = var9.startRestartGroup(224116790);
      ComposerKt.sourceInformation(var9, "C(QuestionHeader)P(5!1,4,7,3,2:c#ui.unit.TextUnit)41@1794L2589:QuestionHeaderComponent.kt#bmpoeo");
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var9.changedInstance(var0) ? 4 : 2;
      }

      if ((var10 & 48) == 0) {
         var12 |= (var11 & 2) == 0 && ((var10 & 64) == 0 ? var9.changed(var1) : var9.changedInstance(var1)) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var9.changed(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var9.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var9.changed(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var9.changed(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var9.changedInstance(var7) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var9.changed(var8) ? 8388608 : 4194304;
      }

      if ((var12 & 4793491) == 4793490 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         var9.startDefaults();
         if ((var10 & 1) != 0 && !var9.getDefaultsInvalid()) {
            var9.skipToGroupEnd();
            if ((var11 & 2) != 0) {
               var12 &= -113;
            }
         } else {
            if ((var11 & 2) != 0) {
               var1 = new ActualString("");
               var12 &= -113;
            }

            if ((var11 & 64) != 0) {
               var7 = null;
            }

            if ((var11 & 128) != 0) {
               var8 = null;
            }
         }

         var9.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(224116790, var12, -1, "io.intercom.android.sdk.survey.ui.components.QuestionHeader (QuestionHeaderComponent.kt:40)");
         }

         byte var16 = 0;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var9, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var13 = Modifier.Companion;
         Vertical var14 = Arrangement.INSTANCE.getTop();
         Horizontal var15 = Alignment.Companion.getStart();
         MeasurePolicy var18 = ColumnKt.columnMeasurePolicy(var14, var15, var9, 14 & var16 >> 3 | 112 & var16 >> 3);
         Modifier var19 = var13;
         MeasurePolicy var20 = var18;
         Composer var21 = var9;
         int var22 = 112 & var16 << 3;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var24 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var21, 0));
         CompositionLocalMap var25 = var21.getCurrentCompositionLocalMap();
         Modifier var26 = ComposedModifierKt.materializeModifier(var21, var19);
         Function0 var27 = ComposeUiNode.Companion.getConstructor();
         Composer var28 = var21;
         int var29 = 6 | 896 & var22 << 6;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var28.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var28.startReusableNode();
         if (var28.getInserting()) {
            var28.createNode(var27);
         } else {
            var28.useNode();
         }

         Composer var31 = Updater.recovered$676aa17b44fd6280(var28);
         int var32 = 0;
         Updater.recovered$89682b199a142880(var31, var20, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var31, var25, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var31, var24, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var31, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var31, var26, ComposeUiNode.Companion.getSetModifier());
         int var33 = 14 & var29 >> 6;
         Composer var34 = var28;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var36 = 6 | 112 & var16 >> 6;
         Composer var37 = var34;
         ColumnScope var38 = ColumnScopeInstance.INSTANCE;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, -1315982323, "C42@1842L6:QuestionHeaderComponent.kt#bmpoeo");
         long var40 = IntercomTheme.INSTANCE.getColors(var37, IntercomTheme.$stable).recovered$2bcfe565407cdfb8();
         var37.startReplaceGroup(788845669);
         ComposerKt.sourceInformation(var37, "*48@2071L35");
         List var42 = CollectionsKt.createListBuilder();
         List var43 = var42;
         int var44 = 0;
         var43.addAll(var0);
         if (var8 == null) {
            Object var10000 = null;
         } else {
            int var45 = var8.intValue();
            int var46 = 0;
            var43.add(
               new Block$Builder()
                  .withType(BlockType.PARAGRAPH.getSerializedName())
                  .withText(StringResources_androidKt.stringResource(var8, var37, 14 & var12 >> 21))
            );
         }

         Iterable var47 = CollectionsKt.build(var42);
         int var60 = 0;
         Iterable var66 = var47;
         ArrayList var68 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var47, 10));
         int var70 = 0;

         for (Object var48 : var66) {
            Block$Builder var49 = (Block$Builder)var48;
            Collection var50 = var68;
            int var51 = 0;
            var50.add(var49.withText(var49.build().getText()).build());
         }

         var47 = (Iterable & List)var68;
         int var61 = 0;
         int var67 = 0;

         for (Object var71 : var47) {
            int var74 = var67++;
            if (var74 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            Block var80 = (Block)var71;
            int var81 = var74;
            int var82 = 0;
            if (var81 == 0 && var2) {
               var37.startReplaceGroup(669360702);
               ComposerKt.sourceInformation(var37, "59@2546L64,60@2631L591");
               var37.startReplaceGroup(-671143346);
               ComposerKt.sourceInformation(var37, "57@2457L6");
               long var52 = var3 instanceof ValidationError$ValidationStringError
                  ? var40
                  : IntercomTheme.INSTANCE.getColors(var37, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var37.endReplaceGroup();
               long var54 = var52;
               String var56 = StringResources_androidKt.stringResource(string.intercom_surveys_required_response, var37, 0);
               Intrinsics.checkNotNull(var80);
               BlockViewKt.recovered$5709084683a88fd9(
                  null,
                  new BlockRenderData(var80, null, null, null, new BlockRenderTextStyle(var5, var4, 0L, null, null, null, 60, null), 14, null),
                  false,
                  new SuffixText(" *", var56, var54, null),
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
                  var37,
                  0,
                  0,
                  32757
               );
               var37.endReplaceGroup();
            } else {
               var37.startReplaceGroup(670260694);
               ComposerKt.sourceInformation(var37, "75@3268L373");
               Intrinsics.checkNotNull(var80);
               BlockViewKt.recovered$5709084683a88fd9(
                  null,
                  new BlockRenderData(var80, null, null, null, new BlockRenderTextStyle(var5, var4, 0L, null, null, null, 60, null), 14, null),
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
                  var37,
                  0,
                  0,
                  32765
               );
               var37.endReplaceGroup();
            }
         }

         var37.endReplaceGroup();
         var37.startReplaceGroup(788890982);
         ComposerKt.sourceInformation(var37, "");
         if (var3 instanceof ValidationError$ValidationStringError) {
            var37.startReplaceGroup(-1314134166);
            ComposerKt.sourceInformation(var37, "89@3764L40,90@3847L149");
            Modifier var84 = Modifier.Companion;
            int var76 = 4;
            int var62 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var84, Dp.recovered$95b3d103663bf060(var76)), var37, 6);
            var37.startReplaceGroup(788894943);
            ComposerKt.sourceInformation(var37, "*90@3834L7");
            Unit var85;
            if (var7 == null) {
               var85 = null;
            } else {
               var70 = 0;
               var7.invoke(var37, 14 & var12 >> 18);
               var85 = Unit.INSTANCE;
            }

            Unit var63 = var85;
            var37.endReplaceGroup();
            if (var63 == null) {
               ValidationErrorComponentKt.recovered$f274ad548aeaeedc(null, (ValidationError$ValidationStringError)var3, var40, var37, 0, 1);
            }

            var37.endReplaceGroup();
         } else {
            var37.startReplaceGroup(788901916);
            ComposerKt.sourceInformation(var37, "*96@4036L9");
            boolean var77 = !StringsKt.isBlank(var1.getText(var37, StringProvider.$stable | 14 & var12 >> 3));
            var37.endReplaceGroup();
            if (var77) {
               var37.startReplaceGroup(-1313819423);
               ComposerKt.sourceInformation(var37, "97@4080L40,99@4182L9,100@4235L10,101@4296L6,98@4137L216");
               Modifier var86 = Modifier.Companion;
               int var78 = 4;
               int var64 = 0;
               SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var86, Dp.recovered$95b3d103663bf060(var78)), var37, 6);
               String var79 = var1.getText(var37, StringProvider.$stable | 14 & var12 >> 3);
               TextStyle var65 = IntercomTheme.INSTANCE.getTypography(var37, IntercomTheme.$stable).getType04();
               long var57 = Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var37, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.6F, 0.0F, 0.0F, 0.0F, 14, null
               );
               TextKt.recovered$5b114ab32a4f4190(
                  var79, null, var57, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var65, var37, 0, 0, 131066
               );
               var37.endReplaceGroup();
            }
         }

         var37.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var28.endNode();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var9);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var87 = var9.endRestartGroup();
      if (var87 != null) {
         final java.util.List recoveredCapture_1338_0 = var0;
         final io.intercom.android.sdk.ui.common.StringProvider recoveredCapture_1338_1 = var1;
         final boolean recoveredCapture_1338_2 = var2;
         final io.intercom.android.sdk.survey.ValidationError recoveredCapture_1338_3 = var3;
         final androidx.compose.ui.text.font.FontWeight recoveredCapture_1338_4 = var4;
         final long recoveredCapture_1338_5 = var5;
         final kotlin.jvm.functions.Function2 recoveredCapture_1338_6 = var7;
         final java.lang.Integer recoveredCapture_1338_7 = var8;
         final int recoveredCapture_1338_8 = var10;
         final int recoveredCapture_1338_9 = var11;
         var87.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1338_0, recoveredArgument_1338_1) -> QuestionHeaderComponentKt.QuestionHeader_n1tc1qA$lambda$6(recoveredCapture_1338_0, recoveredCapture_1338_1, recoveredCapture_1338_2, recoveredCapture_1338_3, recoveredCapture_1338_4, recoveredCapture_1338_5, recoveredCapture_1338_6, recoveredCapture_1338_7, recoveredCapture_1338_8, recoveredCapture_1338_9, (androidx.compose.runtime.Composer)recoveredArgument_1338_0, ((java.lang.Integer)recoveredArgument_1338_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HeaderWithError(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(784176451);
      ComposerKt.sourceInformation(var0, "C(HeaderWithError)111@4468L383:QuestionHeaderComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(784176451, var1, -1, "io.intercom.android.sdk.survey.ui.components.HeaderWithError (QuestionHeaderComponent.kt:110)");
         }

         List var2 = CollectionsKt.listOf(
            new Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText("How would your rate your experience?")
         );
         ValidationError$ValidationStringError var3 = new ValidationError$ValidationStringError(string.intercom_surveys_required_response, null, 2, null);
         long var4 = TextUnitKt.getSp(14);
         FontWeight var6 = FontWeight.Companion.getNormal();
         recovered$5a429c657b5a276d(var2, null, true, var3, var6, var4, null, null, var0, 221568, 194);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1336_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1336_0, recoveredArgument_1336_1) -> QuestionHeaderComponentKt.HeaderWithError$lambda$7(recoveredCapture_1336_0, (androidx.compose.runtime.Composer)recoveredArgument_1336_0, ((java.lang.Integer)recoveredArgument_1336_1).intValue())));
      }
   }

   @Preview(showBackground = true)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HeaderWithoutError(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1382338223);
      ComposerKt.sourceInformation(var0, "C(HeaderWithoutError)126@4939L510:QuestionHeaderComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1382338223, var1, -1, "io.intercom.android.sdk.survey.ui.components.HeaderWithoutError (QuestionHeaderComponent.kt:125)");
         }

         Modifier var2 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var5 = 6;
         int var6 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var0, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var3 = Arrangement.INSTANCE.getTop();
         Horizontal var4 = Alignment.Companion.getStart();
         MeasurePolicy var7 = ColumnKt.columnMeasurePolicy(var3, var4, var0, 14 & var5 >> 3 | 112 & var5 >> 3);
         Modifier var8 = var2;
         MeasurePolicy var9 = var7;
         Composer var10 = var0;
         int var11 = 112 & var5 << 3;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var10, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var13 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var10, 0));
         CompositionLocalMap var14 = var10.getCurrentCompositionLocalMap();
         Modifier var15 = ComposedModifierKt.materializeModifier(var10, var8);
         Function0 var16 = ComposeUiNode.Companion.getConstructor();
         Composer var17 = var10;
         int var18 = 6 | 896 & var11 << 6;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var17.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var17.startReusableNode();
         if (var17.getInserting()) {
            var17.createNode(var16);
         } else {
            var17.useNode();
         }

         Composer var20 = Updater.recovered$676aa17b44fd6280(var17);
         int var21 = 0;
         Updater.recovered$89682b199a142880(var20, var9, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var20, var14, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var20, var13, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var20, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var20, var15, ComposeUiNode.Companion.getSetModifier());
         int var22 = 14 & var18 >> 6;
         Composer var23 = var17;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var25 = 6 | 112 & var5 >> 6;
         Composer var26 = var23;
         ColumnScope var27 = ColumnScopeInstance.INSTANCE;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 996731606, "C127@4992L451:QuestionHeaderComponent.kt#bmpoeo");
         List var29 = CollectionsKt.listOf(
            new Block$Builder().withType(BlockType.PARAGRAPH.getSerializedName()).withText("How would your rate your experience?")
         );
         ActualString var30 = new ActualString("Please select a rating");
         ValidationError$NoValidationError var31 = ValidationError$NoValidationError.INSTANCE;
         long var32 = TextUnitKt.getSp(16);
         FontWeight var34 = FontWeight.Companion.getNormal();
         recovered$5a429c657b5a276d(var29, var30, true, var31, var34, var32, null, null, var26, 224640 | ActualString.$stable << 3, 192);
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var23);
         var17.endNode();
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var10);
         ComposerKt.sourceInformationMarkerEnd(var0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1337_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1337_0, recoveredArgument_1337_1) -> QuestionHeaderComponentKt.HeaderWithoutError$lambda$9(recoveredCapture_1337_0, (androidx.compose.runtime.Composer)recoveredArgument_1337_0, ((java.lang.Integer)recoveredArgument_1337_1).intValue())));
      }
   }

   private static final Unit QuestionHeader_n1tc1qA$lambda$6(
      List var0,
      StringProvider var1,
      boolean var2,
      ValidationError var3,
      FontWeight var4,
      long var5,
      Function2 var7,
      Integer var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      recovered$5a429c657b5a276d(var0, var1, var2, var3, var4, var5, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit HeaderWithError$lambda$7(int var0, Composer var1, int var2) {
      HeaderWithError(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HeaderWithoutError$lambda$9(int var0, Composer var1, int var2) {
      HeaderWithoutError(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
