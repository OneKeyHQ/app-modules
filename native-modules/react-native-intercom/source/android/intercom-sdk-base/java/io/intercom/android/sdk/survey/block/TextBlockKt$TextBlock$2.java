package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.text.InlineTextContent;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.Placeholder;
import androidx.compose.ui.text.PlaceholderVerticalAlign;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.models.InlineSource;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTextBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/TextBlockKt$TextBlock$2\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,523:1\n1047#2,6:524\n1047#2,6:530\n1563#3:536\n1634#3,3:537\n*S KotlinDebug\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/TextBlockKt$TextBlock$2\n*L\n88#1:524,6\n89#1:530,6\n90#1:536\n90#1:537,3\n*E\n"
)
final class TextBlockKt$TextBlock$2 implements Function2 {
   // $VF: synthetic field
   final BlockRenderTextStyle $blockRenderTextStyle;
   // $VF: synthetic field
   final BlockRenderData $blockRenderData;
   // $VF: synthetic field
   final Block $block;
   // $VF: synthetic field
   final Modifier $modifier;
   // $VF: synthetic field
   final AnnotatedString $textToRender;
   // $VF: synthetic field
   final SuffixText $suffixText;
   // $VF: synthetic field
   final Function1 $onTextLayout;
   // $VF: synthetic field
   final Set $inlineSources;
   // $VF: synthetic field
   final AnnotatedString $finalTextToRender;

   TextBlockKt$TextBlock$2(
      BlockRenderTextStyle var1,
      BlockRenderData var2,
      Block var3,
      Modifier var4,
      AnnotatedString var5,
      SuffixText var6,
      Function1 var7,
      Set var8,
      AnnotatedString var9
   ) {
      this.$blockRenderTextStyle = var1;
      this.$blockRenderData = var2;
      this.$block = var3;
      this.$modifier = var4;
      this.$textToRender = var5;
      this.$suffixText = var6;
      this.$onTextLayout = var7;
      this.$inlineSources = var8;
      this.$finalTextToRender = var9;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C87@3555L69,78@3062L1153:TextBlock.kt#jxhu3x");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-598392820, var2, -1, "io.intercom.android.sdk.survey.block.TextBlock.<anonymous> (TextBlock.kt:78)");
         }

         long var3 = this.$blockRenderTextStyle.recovered$8f8bc2178e9073c3();
         Color var10000 = this.$blockRenderTextStyle.recovered$1b39cf3894296c39();
         if (var10000 == null) {
            var10000 = this.$blockRenderData.recovered$1b39cf3894296c39();
         }

         Color var7 = var10000;
         var1.startReplaceGroup(231561198);
         ComposerKt.sourceInformation(var1, "82@3273L6");
         long var8 = var7 == null
            ? IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a()
            : var7.recovered$45f9de7b85e0e0b5();
         var1.endReplaceGroup();
         long var5 = var8;
         TextAlign var50 = this.$blockRenderTextStyle.recovered$2a7d0969a6d1c1b4();
         int var51;
         if (var50 != null) {
            var51 = var50.recovered$a4a926e155faa205();
         } else {
            BlockAlignment var52 = this.$block.getAlign();
            Intrinsics.checkNotNullExpressionValue(var52, "getAlign(...)");
            var51 = BlockExtensionsKt.getTextAlign(var52);
         }

         int var27 = var51;
         long var28 = this.$blockRenderTextStyle.recovered$17d1bb629b307c0f();
         FontWeight var10 = this.$blockRenderTextStyle.getFontWeight();
         Modifier var53 = this.$modifier;
         boolean var10001 = false;
         var1.startReplaceGroup(231573155);
         ComposerKt.sourceInformation(var1, "CC(remember):TextBlock.kt#9igjgp");
         Composer var13 = var1;
         boolean var14 = var1.changed(this.$textToRender) | var1.changed(this.$suffixText);
         AnnotatedString var15 = this.$textToRender;
         SuffixText var16 = this.$suffixText;
         int var17 = 0;
         Object var18 = var13.rememberedValue();
         int var19 = 0;
         Object var10002;
         if (!var14 && var18 != Composer.Companion.getEmpty()) {
            var10002 = var18;
         } else {
            boolean var25 = false;
            Modifier var24 = var53;
            int var20 = 0;
            final androidx.compose.ui.text.AnnotatedString recoveredExpressionCapture_1314_0 = var15;
            final io.intercom.android.sdk.survey.block.SuffixText recoveredExpressionCapture_1314_1 = var16;
            Function1 var26 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1314_0) -> io.intercom.android.sdk.survey.block.TextBlockKt$TextBlock$2.invoke$lambda$1$lambda$0(recoveredExpressionCapture_1314_0, recoveredExpressionCapture_1314_1, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_1314_0)));
            var53 = var24;
            var10001 = var25;
            Object var21 = var26;
            var13.updateRememberedValue(var21);
            var10002 = var21;
         }

         Function1 var12 = (Function1)var10002;
         var1.endReplaceGroup();
         Modifier var11 = SemanticsModifierKt.semantics$default(var53, var10001, var12, 1, null);
         Function1 var30 = this.$onTextLayout;
         var1.startReplaceGroup(231576240);
         ComposerKt.sourceInformation(var1, "88@3669L2");
         Function1 var54 = var30;
         if (var30 == null) {
            var1.startReplaceGroup(231576736);
            ComposerKt.sourceInformation(var1, "CC(remember):TextBlock.kt#9igjgp");
            Composer var36 = var1;
            boolean var38 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var36.rememberedValue();
            int var44/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var55;
            if (it$ivx == Composer.Companion.getEmpty()) {
               int var46 = 0;
               Object var47 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1315_0) -> io.intercom.android.sdk.survey.block.TextBlockKt$TextBlock$2.invoke$lambda$3$lambda$2((androidx.compose.ui.text.TextLayoutResult) ((androidx.compose.ui.text.TextLayoutResult)recoveredExpressionArgument_1315_0)));
               var36.updateRememberedValue(var47);
               var55 = var47;
            } else {
               var55 = it$ivx;
            }

            Function1 var34 = (Function1)var55;
            var1.endReplaceGroup();
            var54 = var34;
         }

         Function1 var32 = var54;
         var1.endReplaceGroup();
         Function1 var29 = var32;
         var1.startReplaceGroup(231578687);
         ComposerKt.sourceInformation(var1, "*96@4040L142");
         Iterable var35 = this.$inlineSources;
         int var37 = 0;
         Iterable var39 = var35;
         ArrayList var41 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var35, 10));
         int var43 = 0;

         for (Object var48 : var39) {
            InlineSource var22 = (InlineSource)var48;
            Collection var49 = var41;
            int var23 = 0;
            var49.add(
               TuplesKt.to(
                  var22.getDataEntityId(),
                  new InlineTextContent(
                     new Placeholder(TextUnitKt.getEm(1.5), TextUnitKt.getEm(1.1), PlaceholderVerticalAlign.Companion.recovered$a58c56f1b61fbc44(), null),
                     ComposableLambdaKt.rememberComposableLambda(-804032742, true, new TextBlockKt$TextBlock$2$3$1(var22), var1, 54)
                  )
               )
            );
         }

         List var33 = (List)var41;
         var1.endReplaceGroup();
         Map var31 = MapsKt.toMap(var33);
         TextKt.recovered$489343e57da77a35(
            this.$finalTextToRender,
            var11,
            var5,
            null,
            var3,
            null,
            var10,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var27),
            var28,
            0,
            false,
            0,
            0,
            var31,
            var29,
            null,
            var1,
            0,
            0,
            324520
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(AnnotatedString var0, SuffixText var1, SemanticsPropertyReceiver var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var2, var0 + var1.getTtsText());
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(TextLayoutResult var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
