package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.text.InlineTextContentKt;
import androidx.compose.foundation.text.selection.SelectionContainerKt;
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
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.LinkAnnotation;
import androidx.compose.ui.text.ParagraphStyle;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.TextLinkStyles;
import androidx.compose.ui.text.TextMeasurer;
import androidx.compose.ui.text.TextMeasurerHelperKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.LinkAnnotation.Clickable;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.text.style.TextIndent;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.IntSize;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.models.InlineSource;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0019\u001ag\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u001a\b\u0002\u0010\n\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0004\u0012\u00020\u00010\u000b2\u0016\b\u0002\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000bH\u0001¢\u0006\u0002\u0010\u0010\u001a+\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0003¢\u0006\u0004\b\u0016\u0010\u0017\u001aM\u0010\u0018\u001a\u0014\u0012\u0004\u0012\u00020\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u001b0\u0019*\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\b\u001a\u00020\t2\u0018\u0010\n\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0004\u0012\u00020\u00010\u000bH\u0003¢\u0006\u0002\u0010\u001f\u001a<\u0010 \u001a\u00020\u0001*\u00020!2\u0006\u0010\"\u001a\u00020\u001a2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\r0\u001b2\u0018\u0010\n\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0004\u0012\u00020\u00010\u000bH\u0002\u001aY\u0010$\u001a\u0014\u0012\u0004\u0012\u00020\u001a\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u001b0\u00192\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00130\f2\u0006\u0010&\u001a\u00020\t2\u0006\u0010\u001d\u001a\u00020\u001e2\u001a\u0010\n\u001a\u0016\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000bH\u0001¢\u0006\u0002\u0010'\u001a\u0018\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00130\f*\b\u0012\u0004\u0012\u00020\u00130\fH\u0000\u001a\u001e\u0010)\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u001b\u0012\u0004\u0012\u00020\u00130\u0019*\u00020\u0013H\u0002\u001a\u0016\u0010*\u001a\u0004\u0018\u00010\u0013*\u00020\u00132\u0006\u0010+\u001a\u00020\u0013H\u0002\u001a\r\u0010-\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u0010/\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00100\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00101\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00102\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00103\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00104\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00105\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00107\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\u001a\r\u00109\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010.\"\u000e\u0010,\u001a\u00020\u0013X\u0082T¢\u0006\u0002\n\u0000\"\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00130\fX\u0082\u0004¢\u0006\u0002\n\u0000\"\u0014\u00106\u001a\b\u0012\u0004\u0012\u00020\u00130\fX\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u00108\u001a\u00020\u0013X\u0082T¢\u0006\u0002\n\u0000¨\u0006:",
   d2 = {
         "TextBlock",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "blockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "suffixText",
         "Lio/intercom/android/sdk/survey/block/SuffixText;",
         "inlineSourcesVisible",
         "",
         "onInlineSourcesClick",
         "Lkotlin/Function1;",
         "",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "onTextLayout",
         "Landroidx/compose/ui/text/TextLayoutResult;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/BlockRenderData;Lio/intercom/android/sdk/survey/block/SuffixText;ZLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "InlineSourceBadge",
         "index",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "InlineSourceBadge-FNF3uiM",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;JLandroidx/compose/runtime/Composer;II)V",
         "textToRender",
         "Lkotlin/Pair;",
         "Landroidx/compose/ui/text/AnnotatedString;",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "blockRenderTextStyle",
         "Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lio/intercom/android/sdk/survey/block/BlockRenderTextStyle;ZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)Lkotlin/Pair;",
         "appendInlineSources",
         "Landroidx/compose/ui/text/AnnotatedString$Builder;",
         "text",
         "sources",
         "textListToRender",
         "items",
         "isOrderedList",
         "(Ljava/util/List;ZLio/intercom/android/sdk/survey/block/BlockRenderTextStyle;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)Lkotlin/Pair;",
         "renderableListItems",
         "extractInlineSources",
         "findAttribute",
         "name",
         "formattedText",
         "TextBlockPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "InlineCodeTextBlockPreview",
         "TextBlockAlignmentPreview",
         "HeadingTextBlockPreview",
         "SubheadingTextBlockPreview",
         "TextBlockWithSuffixPreview",
         "OrderedListTextBlockPreview",
         "UnorderedListTextBlockPreview",
         "itemsWithEmpty",
         "OrderedListWithEmptyItemTextBlockPreview",
         "textWithSources",
         "InlineSourcesTextBlockPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTextBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/TextBlockKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 9 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 10 IntSize.kt\nandroidx/compose/ui/unit/IntSize\n+ 11 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n*L\n1#1,523:1\n1047#2,6:524\n1047#2,6:537\n1047#2,6:588\n1580#3:530\n1398#3,6:531\n1580#3:579\n1512#3,3:581\n1516#3,2:586\n1580#3:601\n1420#3,3:609\n1424#3,2:614\n1420#3,6:616\n70#4:543\n67#4,9:544\n77#4:578\n81#5,6:553\n88#5,6:568\n96#5:577\n402#6,9:559\n411#6,3:574\n75#7:580\n75#7:594\n75#7:598\n75#7:605\n1869#8,2:584\n1563#8:602\n1634#8,2:603\n1636#8:606\n1878#8,2:607\n1869#8,2:612\n1880#8:622\n774#8:623\n865#8,2:624\n3307#8,10:626\n1617#8,9:636\n1869#8:645\n1870#8:647\n1626#8:648\n1#9:595\n1#9:646\n54#10:596\n59#10:599\n85#11:597\n90#11:600\n*S KotlinDebug\n*F\n+ 1 TextBlock.kt\nio/intercom/android/sdk/survey/block/TextBlockKt\n*L\n53#1:524,6\n116#1:537,6\n226#1:588,6\n67#1:530\n69#1:531,6\n169#1:579\n200#1:581,3\n200#1:586,2\n240#1:601\n260#1:609,3\n260#1:614,2\n276#1:616,6\n113#1:543\n113#1:544,9\n113#1:578\n113#1:553,6\n113#1:568,6\n113#1:577\n113#1:559,9\n113#1:574,3\n172#1:580\n232#1:594\n233#1:598\n251#1:605\n204#1:584,2\n242#1:602\n242#1:603,2\n242#1:606\n259#1:607,2\n265#1:612,2\n259#1:622\n290#1:623\n290#1:624,2\n299#1:626,10\n303#1:636,9\n303#1:645\n303#1:647\n303#1:648\n303#1:646\n232#1:596\n233#1:599\n232#1:597\n233#1:600\n*E\n"
)
public final class TextBlockKt {
   @NotNull
   private static final String formattedText = "Hello <b>World</b>. This <i><strike>text</strike>sentence</i> is form<b>att<u>ed</u></b> in simple html. <a href=\"https://github.com/ch4rl3x/HtmlText\">HtmlText</a>";
   @NotNull
   private static final List items;
   @NotNull
   private static final List itemsWithEmpty;
   @NotNull
   private static final String textWithSources = "Hello World! This is <a href=\"https://github.com/ch4rl3x/HtmlText\">simple link</a>. And this is source link <a class=\"inline-citation\" data-entity-id=\"111\" data-entity-type=\"article\" data-source-index=\"1\" href=\"https://github.com/ch4rl3x/HtmlText\" title=\"Leaving Elephant Energy\">link here</a> <a class=\"inline-citation\" data-entity-id=\"222\" data-entity-type=\"article\" data-source-index=\"2\" href=\"https://github.com/ch4rl3x/HtmlText\" title=\"Article 2\">link here</a>";

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextBlock(
      @Nullable Modifier var0,
      @NotNull BlockRenderData var1,
      @Nullable SuffixText var2,
      boolean var3,
      @Nullable Function1 var4,
      @Nullable Function1 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "blockRenderData");
      Composer var28 = var6.startRestartGroup(474787515);
      ComposerKt.sourceInformation(var28, "C(TextBlock)P(2!1,5)52@2333L2,58@2544L109,77@3052L1169,77@3035L1186:TextBlock.kt#jxhu3x");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var28.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var28.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var28.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var28.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var28.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var28.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var28.getSkipping()) {
         var28.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = SuffixText.Companion.getNO_SUFFIX();
         }

         if ((var8 & 8) != 0) {
            var3 = false;
         }

         if ((var8 & 16) != 0) {
            var28.startReplaceGroup(1265136161);
            ComposerKt.sourceInformation(var28, "CC(remember):TextBlock.kt#9igjgp");
            Composer var11 = var28;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var35 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1305_0) -> io.intercom.android.sdk.survey.block.TextBlockKt.TextBlock$lambda$1$lambda$0((java.util.List) ((java.util.List)recoveredExpressionArgument_1305_0)));
               var11.updateRememberedValue(var35);
               var10000 = var35;
            } else {
               var10000 = var14;
            }

            Function1 var10 = (Function1)var10000;
            var28.endReplaceGroup();
            var4 = var10;
         }

         if ((var8 & 32) != 0) {
            var5 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(474787515, var9, -1, "io.intercom.android.sdk.survey.block.TextBlock (TextBlock.kt:54)");
         }

         Block var29 = var1.getBlock();
         BlockRenderTextStyle var30 = var1.getTextStyle();
         Pair var31 = textToRender(var29, var30, var3, var4, var28, 896 & var9 >> 3 | 7168 & var9 >> 3);
         AnnotatedString var32 = (AnnotatedString)var31.component1();
         Set var33 = (Set)var31.component2();
         AnnotatedString var38;
         if (!Intrinsics.areEqual(var2, SuffixText.Companion.getNO_SUFFIX())) {
            int var36 = 0;
            Builder var17 = new Builder(0, 1, null);
            Builder var18 = var17;
            int var19 = 0;
            var18.append(var32);
            Builder var20 = var18;
            SpanStyle var21 = new SpanStyle(
               var2.recovered$6f15fcf6c761ad06(), 0L, null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65534, null
            );
            int var22 = 0;
            int var23 = var20.pushStyle(var21);

            try {
               Builder var24 = var20;
               int var25 = 0;
               var24.append(var2.getText());
               Unit var37 = Unit.INSTANCE;
            } finally {
               var20.pop(var23);
            }

            var38 = var17.toAnnotatedString();
         } else {
            var38 = var32;
         }

         AnnotatedString var34 = var38;
         SelectionContainerKt.DisableSelection(
            ComposableLambdaKt.rememberComposableLambda(
               -598392820, true, new TextBlockKt$TextBlock$2(var30, var1, var29, var0, var32, var2, var5, var33, var34), var28, 54
            ),
            var28,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var39 = var28.endRestartGroup();
      if (var39 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1306_0 = var0;
         final io.intercom.android.sdk.survey.block.BlockRenderData recoveredExpressionCapture_1306_1 = var1;
         final io.intercom.android.sdk.survey.block.SuffixText recoveredExpressionCapture_1306_2 = var2;
         final boolean recoveredExpressionCapture_1306_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1306_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1306_5 = var5;
         final int recoveredExpressionCapture_1306_6 = var7;
         final int recoveredExpressionCapture_1306_7 = var8;
         var39.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1306_0, recoveredExpressionArgument_1306_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.TextBlock$lambda$4(recoveredExpressionCapture_1306_0, recoveredExpressionCapture_1306_1, recoveredExpressionCapture_1306_2, recoveredExpressionCapture_1306_3, recoveredExpressionCapture_1306_4, recoveredExpressionCapture_1306_5, recoveredExpressionCapture_1306_6, recoveredExpressionCapture_1306_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1306_0), ((java.lang.Integer)recoveredExpressionArgument_1306_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$578f218e7033db7a(String var0, Modifier var1, long var2, Composer var4, int var5, int var6) {
      Composer var41 = var4.startRestartGroup(234500363);
      ComposerKt.sourceInformation(var41, "C(InlineSourceBadge)P(1,2,0:c#ui.graphics.Color)110@4365L6,115@4484L106,112@4400L490:TextBlock.kt#jxhu3x");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var41.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var41.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var41.changed(var2) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var41.getSkipping()) {
         var41.skipToGroupEnd();
      } else {
         var41.startDefaults();
         if ((var5 & 1) != 0 && !var41.getDefaultsInvalid()) {
            var41.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getColors(var41, IntercomTheme.$stable).getContainer().recovered$556707ad0606947e();
               var7 &= -897;
            }
         }

         var41.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(234500363, var7, -1, "io.intercom.android.sdk.survey.block.InlineSourceBadge (TextBlock.kt:111)");
         }

         Modifier var10000 = SizeKt.fillMaxSize$default(var1, 0.0F, 1, null);
         var41.startReplaceGroup(-1159004872);
         ComposerKt.sourceInformation(var41, "CC(remember):TextBlock.kt#9igjgp");
         Composer var9 = var41;
         boolean var10 = (var7 & 896 ^ 384) > 256 && var41.changed(var2) || (var7 & 384) == 256;
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var51;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var51 = var12;
         } else {
            Modifier var40 = var10000;
            int var14 = 0;
            final long recoveredExpressionCapture_1312_0 = var2;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1312_0) -> io.intercom.android.sdk.survey.block.TextBlockKt.InlineSourceBadge_FNF3uiM$lambda$6$lambda$5(recoveredExpressionCapture_1312_0, (androidx.compose.ui.graphics.drawscope.DrawScope) ((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredExpressionArgument_1312_0)));
            var10000 = var40;
            Object var15 = var10001;
            var9.updateRememberedValue(var15);
            var51 = var15;
         }

         Function1 var8 = (Function1)var51;
         var41.endReplaceGroup();
         Modifier var42 = DrawModifierKt.drawBehind(var10000, var8);
         byte var45 = 0;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var41, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var43 = Alignment.Companion.getTopStart();
         boolean var44 = false;
         MeasurePolicy var47 = BoxKt.maybeCachedBoxMeasurePolicy(var43, var44);
         Modifier var48 = var42;
         MeasurePolicy var49 = var47;
         Composer var16 = var41;
         int var17 = 112 & var45 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var48);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var49, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var31 = 6 | 112 & var45 >> 6;
         Composer var32 = var29;
         BoxScope var33 = BoxScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 601555394, "C124@4731L10,125@4784L6,121@4608L276:TextBlock.kt#jxhu3x");
         Modifier var35 = var33.align(Modifier.Companion, Alignment.Companion.getCenter());
         TextStyle var36 = IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType05();
         long var37 = IntercomTheme.INSTANCE.getColors(var32, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         FontWeight var39 = FontWeight.Companion.getMedium();
         TextKt.recovered$5b114ab32a4f4190(
            var0, var35, var37, null, 0L, null, var39, null, 0L, null, null, 0L, 0, false, 1, 0, null, var36, var32, 1572864 | 14 & var7, 24576, 114616
         );
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var41);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var50 = var41.endRestartGroup();
      if (var50 != null) {
         final java.lang.String recoveredExpressionCapture_1313_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1313_1 = var1;
         final long recoveredExpressionCapture_1313_2 = var2;
         final int recoveredExpressionCapture_1313_3 = var5;
         final int recoveredExpressionCapture_1313_4 = var6;
         var50.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1313_0, recoveredExpressionArgument_1313_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.InlineSourceBadge_FNF3uiM$lambda$8(recoveredExpressionCapture_1313_0, recoveredExpressionCapture_1313_1, recoveredExpressionCapture_1313_2, recoveredExpressionCapture_1313_3, recoveredExpressionCapture_1313_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1313_0), ((java.lang.Integer)recoveredExpressionArgument_1313_1).intValue())));
      }
   }

   @Composable
   private static final Pair textToRender(Block var0, BlockRenderTextStyle var1, boolean var2, Function1 var3, Composer var4, int var5) {
      var4.startReplaceGroup(1340770584);
      ComposerKt.sourceInformation(var4, "C(textToRender):TextBlock.kt#jxhu3x");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1340770584, var5, -1, "io.intercom.android.sdk.survey.block.textToRender (TextBlock.kt:147)");
      }

      BlockType var10000 = var0.getType();
      Pair var29;
      switch (var10000 == null ? -1 : TextBlockKt$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
         case 1:
            var4.startReplaceGroup(-1598787434);
            ComposerKt.sourceInformation(var4, "148@5366L183");
            List var31 = var0.getItems();
            Intrinsics.checkNotNullExpressionValue(var31, "getItems(...)");
            Pair var23 = textListToRender(var31, false, var1, var3, var4, 48 | 896 & var5 << 3 | 7168 & var5);
            var4.endReplaceGroup();
            var29 = var23;
            break;
         case 2:
            var4.startReplaceGroup(-1598780587);
            ComposerKt.sourceInformation(var4, "155@5580L182");
            List var30 = var0.getItems();
            Intrinsics.checkNotNullExpressionValue(var30, "getItems(...)");
            Pair var22 = textListToRender(var30, true, var1, var3, var4, 48 | 896 & var5 << 3 | 7168 & var5);
            var4.endReplaceGroup();
            var29 = var22;
            break;
         default:
            var4.startReplaceGroup(1977628558);
            ComposerKt.sourceInformation(var4, "*171@6194L7,176@6485L6");
            Pair var26;
            if (var2) {
               String var25 = var0.getText();
               Intrinsics.checkNotNullExpressionValue(var25, "getText(...)");
               var26 = extractInlineSources(var25);
            } else {
               var26 = TuplesKt.to(SetsKt.emptySet(), var0.getText());
            }

            Pair var8 = var26;
            Set var9 = (Set)var8.component1();
            String var10 = (String)var8.component2();
            int var11 = 0;
            Builder var12 = new Builder(0, 1, null);
            Builder var13 = var12;
            int var14 = 0;
            Spanned var27 = HtmlCompat.fromHtml(BlockExtensionsKt.rewriteInlineCodeToMonospace(var10), 0);
            Intrinsics.checkNotNullExpressionValue(var27, "fromHtml(...)");
            CharSequence var28 = var27;
            CompositionLocal var15 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var16 = 0;
            int var17 = 0;
            ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var18 = var4.consume(var15);
            ComposerKt.sourceInformationMarkerEnd(var4);
            Context var10001 = (Context)var18;
            TextDecoration var24 = TextDecoration.Companion.getUnderline();
            Color var10002 = var1.recovered$25e2f784684f6a30();
            long var19 = var10002 != null ? var10002.recovered$45f9de7b85e0e0b5() : Color.Companion.recovered$37fe95da52e29a80();
            AnnotatedString var21 = BlockExtensionsKt.recovered$13969ea35978a370(
               var28,
               var10001,
               new SpanStyle(var19, 0L, null, null, null, null, null, 0L, null, null, null, 0L, var24, null, null, null, 61438, null),
               IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getContainer().recovered$556707ad0606947e()
            );
            if (var2 && !var9.isEmpty()) {
               appendInlineSources(var13, var21, var9, var3);
            } else {
               var13.append(var21);
            }

            Pair var7 = TuplesKt.to(var12.toAnnotatedString(), var9);
            var4.endReplaceGroup();
            var29 = var7;
      }

      Pair var6 = var29;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var4.endReplaceGroup();
      return var6;
   }

   private static final void appendInlineSources(Builder var0, AnnotatedString var1, Set var2, Function1 var3) {
      final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1311_0 = var3;
      final java.util.Set recoveredExpressionCapture_1311_1 = var2;
      Clickable var4 = new Clickable(
         "inline_source",
         new TextLinkStyles(
            new SpanStyle(0L, 0L, null, null, null, null, null, 0L, null, null, null, 0L, TextDecoration.Companion.getNone(), null, null, null, 61439, null),
            null,
            null,
            null,
            14,
            null
         ),
         ((androidx.compose.ui.text.LinkInteractionListener) (recoveredExpressionArgument_1311_0) -> io.intercom.android.sdk.survey.block.TextBlockKt.appendInlineSources$lambda$10(recoveredExpressionCapture_1311_0, recoveredExpressionCapture_1311_1, (androidx.compose.ui.text.LinkAnnotation) (recoveredExpressionArgument_1311_0)))
      );
      Builder var5 = var0;
      int var6 = 0;
      int var7 = var5.pushLink(var4);

      try {
         Builder var8 = var5;
         int var9 = 0;
         var8.append(var1);
         Iterable var10 = var2;
         int var11 = 0;

         for (Object var13 : var10) {
            InlineSource var14 = (InlineSource)var13;
            int var15 = 0;
            InlineTextContentKt.appendInlineContent$default(var8, var14.getDataEntityId(), null, 2, null);
         }

         Unit var16 = Unit.INSTANCE;
      } finally {
         var5.pop(var7);
      }
   }

   @Composable
   @NotNull
   public static final <E extends Exception> Pair textListToRender(
      @NotNull List var0, boolean var1, @NotNull BlockRenderTextStyle var2, @Nullable Function1 var3, @Nullable Composer var4, int var5
   ) throws E {
      Intrinsics.checkNotNullParameter(var0, "items");
      Intrinsics.checkNotNullParameter(var2, "blockRenderTextStyle");
      var4.startReplaceGroup(239669781);
      ComposerKt.sourceInformation(var4, "C(textListToRender)P(2,1)224@7891L22,225@7944L203,*231@8185L7,232@8270L7,250@9082L7,255@9393L6:TextBlock.kt#jxhu3x");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(239669781, var5, -1, "io.intercom.android.sdk.survey.block.textListToRender (TextBlock.kt:215)");
      }

      List var6 = renderableListItems(var0);
      TextStyle var7 = var2.toTextStyle$intercom_sdk_base_release();
      TextMeasurer var8 = TextMeasurerHelperKt.rememberTextMeasurer(0, var4, 0, 1);
      int var11 = var6.size();
      var4.startReplaceGroup(1608526632);
      ComposerKt.sourceInformation(var4, "CC(remember):TextBlock.kt#9igjgp");
      Composer var14 = var4;
      boolean var15 = var4.changed(var7) | var4.changed(var8) | var4.changed(var11) | ((var5 & 112 ^ 48) > 32 && var4.changed(var1) || (var5 & 48) == 32);
      int var16 = 0;
      Object var17 = var14.rememberedValue();
      int var18 = 0;
      Object var10000;
      if (!var15 && var17 != Composer.Companion.getEmpty()) {
         var10000 = var17;
      } else {
         int var19 = 0;
         Object var20 = IntSize.recovered$970bda8bc70de0d8(
            TextMeasurer.recovered$5c3651eeca97200c(
                  var8, textListToRender$bullet(var1, var6.size()), var7, 0, false, 0, 0L, null, null, null, false, 1020, null
               )
               .recovered$244b35ad2fcbefb2()
         );
         var14.updateRememberedValue(var20);
         var10000 = var20;
      }

      long var12 = ((IntSize)var10000).recovered$45f9de7b85e0e0b5();
      var4.endReplaceGroup();
      long var9 = var12;
      CompositionLocal var13 = CompositionLocalsKt.getLocalDensity();
      int var60 = 0;
      int var62 = 0;
      ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var66 = var4.consume(var13);
      ComposerKt.sourceInformationMarkerEnd(var4);
      Density var61 = (Density)var66;
      int var63 = 0;
      int var67 = 0;
      long var72 = var9;
      int var78 = 0;
      long var58 = var61.recovered$2570910d855284db((int)(var72 >> 32));
      CompositionLocal var64 = CompositionLocalsKt.getLocalDensity();
      int $changed$ivx = 0;
      int $i$f$getCurrentx = 0;
      ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var76 = var4.consume(var64);
      ComposerKt.sourceInformationMarkerEnd(var4);
      Density var69 = (Density)var76;
      int var74 = 0;
      int var77 = 0;
      long value$iv$ivx = var9;
      int var21 = 0;
      long var59 = var69.recovered$2570910d855284db((int)(value$iv$ivx & 4294967295L));
      TextIndent var70 = new TextIndent(0L, var58, 1, null);
      ParagraphStyle var65 = new ParagraphStyle(0, 0, var59, var70, null, null, 0, 0, null, 499, null);
      Set var71 = new LinkedHashSet();
      int var80 = 0;
      Builder var81 = new Builder(0, 1, null);
      Builder var82 = var81;
      int var22 = 0;
      Iterable var23 = var6;
      int var24 = 0;
      Iterable var25 = var23;
      ArrayList var26 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var23, 10));
      int var27 = 0;

      for (Object var29 : var25) {
         String var30 = (String)var29;
         Collection var31 = var26;
         int var32 = 0;
         Pair var33 = var3 != null ? extractInlineSources(var30) : TuplesKt.to(SetsKt.emptySet(), var30);
         Set var34 = (Set)var33.component1();
         String var35 = (String)var33.component2();
         var71.addAll(var34);
         Spanned var107 = HtmlCompat.fromHtml(BlockExtensionsKt.rewriteInlineCodeToMonospace(var35), 0);
         Intrinsics.checkNotNullExpressionValue(var107, "fromHtml(...)");
         CharSequence var108 = var107;
         CompositionLocal var36 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivxx = 0;
         int $i$f$getCurrentxx = 0;
         ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var39 = var4.consume(var36);
         ComposerKt.sourceInformationMarkerEnd(var4);
         Context var10001 = (Context)var39;
         TextDecoration var97 = TextDecoration.Companion.getUnderline();
         Color var10002 = var2.recovered$25e2f784684f6a30();
         long var40 = var10002 != null ? var10002.recovered$45f9de7b85e0e0b5() : Color.Companion.recovered$37fe95da52e29a80();
         var31.add(
            TuplesKt.to(
               BlockExtensionsKt.recovered$13969ea35978a370(
                  var108,
                  var10001,
                  new SpanStyle(var40, 0L, null, null, null, null, null, 0L, null, null, null, 0L, var97, null, null, null, 61438, null),
                  IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getContainer().recovered$556707ad0606947e()
               ),
               var34
            )
         );
      }

      var var42 = (Iterable & List)var26;
      int var83 = 0;
      int var84 = 0;

      for (Object var86 : var42) {
         int var87 = var84++;
         if (var87 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Pair var88 = (Pair)var86;
         int var89 = var87;
         int var90 = 0;
         AnnotatedString var91 = (AnnotatedString)var88.component1();
         Set var92 = (Set)var88.component2();
         Builder var93 = var82;
         int var95 = 0;
         int index$ivx = var93.pushStyle(var65);

         try {
            Builder var100 = var93;
            int var102 = 0;
            var100.append(access$textListToRender$bullet(var1, var89 + 1));
            int var105 = String.valueOf(var6.size()).length();
            if (var105 > 1) {
               int var43 = var105 - String.valueOf(var89 + 1).length();
               Iterable var44 = RangesKt.until((int)0, (int)var43);
               int var45 = 0;
               Iterator var46 = var44.iterator();

               while (var46.hasNext()) {
                  int var47 = ((IntIterator)var46).nextInt();
                  int var49 = 0;
                  var100.append("\t");
               }
            }

            if (var3 != null && !var92.isEmpty()) {
               access$appendInlineSources(var100, var91, var92, var3);
            } else {
               var100.append(var91);
            }

            Unit var50 = Unit.INSTANCE;
         } finally {
            var93.pop(index$ivx);
         }

         if (var89 < CollectionsKt.getLastIndex(var6)) {
            Builder var94 = var82;
            ParagraphStyle var96 = new ParagraphStyle(0, 0, TextUnitKt.getSp(0), null, null, null, 0, 0, null, 507, null);
            int $i$f$withStylex = 0;
            int index$ivxx = var94.pushStyle(var96);

            try {
               Builder var103 = var94;
               int var106 = 0;
               try {
                  ((java.lang.Appendable)var103).append('\n');
               } catch (java.io.IOException sourceFailure) {
                  throw (E)sourceFailure;
               }
               Unit var104 = Unit.INSTANCE;
            } finally {
               var94.pop(index$ivxx);
            }
         }
      }

      Pair var75 = TuplesKt.to(var81.toAnnotatedString(), var71);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var4.endReplaceGroup();
      return var75;
   }

   @NotNull
   public static final List renderableListItems(@NotNull List var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Iterable var1 = var0;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList();
      int var5 = 0;

      for (Object var7 : var3) {
         String var8 = (String)var7;
         int var9 = 0;
         if (var8.length() > 0) {
            var4.add(var7);
         }
      }

      return (List)var4;
   }

   private static final Pair extractInlineSources(String var0) {
      String var1 = "<a";
      String var2 = "</a>";
      String var3 = "inline-citation";
      CharSequence var5 = var0;
      Regex var6 = new Regex("((?=" + var1 + ")|(?<=" + var2 + "))");
      byte var7 = 0;
      List var4 = var6.split(var5, var7);
      Iterable var29 = var4;
      int var31 = 0;
      ArrayList var8 = new ArrayList();
      ArrayList var9 = new ArrayList();

      for (Object var11 : var29) {
         String var12 = (String)var11;
         int var13 = 0;
         if (StringsKt.contains$default(var12, var1, false, 2, null)
            && StringsKt.contains$default(var12, var2, false, 2, null)
            && StringsKt.contains$default(var12, var3, false, 2, null)) {
            var8.add(var11);
         } else {
            var9.add(var11);
         }
      }

      Pair var28 = new Pair(var8, var9);
      List var30 = (List)var28.component1();
      List var32 = (List)var28.component2();
      Iterable var34 = var30;
      int var35 = 0;
      Iterable var36 = var34;
      ArrayList var37 = new ArrayList();
      int var38 = 0;
      Iterable var14 = var36;
      int var15 = 0;

      for (Object var17 : var14) {
         InlineSource var10000;
         label59: {
            Object var18 = var17;
            int var19 = 0;
            String var20 = (String)var18;
            int var21 = 0;
            String var22 = findAttribute(var20, "href");
            String var23 = findAttribute(var20, "data-entity-id");
            String var24 = findAttribute(var20, "data-source-index");
            CharSequence var25 = var22;
            if (var25 != null && var25.length() != 0) {
               CharSequence var39 = var23;
               if (var39 != null && var39.length() != 0) {
                  CharSequence var40 = var24;
                  if (var40 != null && var40.length() != 0) {
                     var10000 = new InlineSource(var23, findAttribute(var20, "data-entity-type"), var24, var22, findAttribute(var20, "title"));
                     break label59;
                  }
               }
            }

            var10000 = null;
         }

         if (var10000 != null) {
            Object var26 = var10000;
            int var27 = 0;
            var37.add(var26);
         }
      }

      List var33 = (List)var37;
      return TuplesKt.to(CollectionsKt.toSet(var33), CollectionsKt.joinToString$default(var32, "", null, null, 0, null, null, 62, null));
   }

   private static final String findAttribute(String var0, String var1) {
      String var3 = var1 + "=\".*?\"";
      byte var4 = 0;
      Pattern var10000 = Pattern.compile(var3, var4);
      Intrinsics.checkNotNullExpressionValue(var10000, "compile(...)");
      Pattern var2 = var10000;
      Matcher var5 = var2.matcher(var0);
      String var7;
      if (var5.find()) {
         String var6 = var5.group();
         Intrinsics.checkNotNullExpressionValue(var6, "group(...)");
         var7 = StringsKt.substringBefore$default(StringsKt.substringAfter$default(var6, "\"", null, 2, null), "\"", null, 2, null);
      } else {
         var7 = null;
      }

      return var7;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(443046075);
      ComposerKt.sourceInformation(var3, "C(TextBlockPreview)347@12908L282,347@12894L296:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(443046075, var1, -1, "io.intercom.android.sdk.survey.block.TextBlockPreview (TextBlock.kt:341)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.PARAGRAPH.getSerializedName())
            .withText(
               "Hello <b>World</b>. This <i><strike>text</strike>sentence</i> is form<b>att<u>ed</u></b> in simple html. <a href=\"https://github.com/ch4rl3x/HtmlText\">HtmlText</a>"
            )
            .build();
         IntercomThemeKt.IntercomTheme(
            null, null, null, ComposableLambdaKt.rememberComposableLambda(-140923183, true, new TextBlockKt$TextBlockPreview$1(var2), var3, 54), var3, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1308_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1308_0, recoveredExpressionArgument_1308_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.TextBlockPreview$lambda$25(recoveredExpressionCapture_1308_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1308_0), ((java.lang.Integer)recoveredExpressionArgument_1308_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InlineCodeTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-1573168459);
      ComposerKt.sourceInformation(var3, "C(InlineCodeTextBlockPreview)367@13494L99,367@13480L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1573168459, var1, -1, "io.intercom.android.sdk.survey.block.InlineCodeTextBlockPreview (TextBlock.kt:361)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.PARAGRAPH.getSerializedName())
            .withText("Run <code>yarn install</code> then <code>yarn start</code> to boot the dev server.")
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(367037771, true, new TextBlockKt$InlineCodeTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1300_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1300_0, recoveredExpressionArgument_1300_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.InlineCodeTextBlockPreview$lambda$26(recoveredExpressionCapture_1300_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1300_0), ((java.lang.Integer)recoveredExpressionArgument_1300_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextBlockAlignmentPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1235422502);
      ComposerKt.sourceInformation(var2, "C(TextBlockAlignmentPreview)377@13674L925:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1235422502, var1, -1, "io.intercom.android.sdk.survey.block.TextBlockAlignmentPreview (TextBlock.kt:376)");
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, io.intercom.android.sdk.survey.block.ComposableSingletons$TextBlockKt.INSTANCE.recovered$1400af5e34386102(), var2, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1307_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1307_0, recoveredExpressionArgument_1307_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.TextBlockAlignmentPreview$lambda$27(recoveredExpressionCapture_1307_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1307_0), ((java.lang.Integer)recoveredExpressionArgument_1307_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HeadingTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-1066073995);
      ComposerKt.sourceInformation(var3, "C(HeadingTextBlockPreview)410@14823L99,410@14809L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1066073995, var1, -1, "io.intercom.android.sdk.survey.block.HeadingTextBlockPreview (TextBlock.kt:404)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder().withType(BlockType.HEADING.getSerializedName()).withText("Heading").build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1821427103, true, new TextBlockKt$HeadingTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1299_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1299_0, recoveredExpressionArgument_1299_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.HeadingTextBlockPreview$lambda$28(recoveredExpressionCapture_1299_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1299_0), ((java.lang.Integer)recoveredExpressionArgument_1299_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubheadingTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(1598324377);
      ComposerKt.sourceInformation(var3, "C(SubheadingTextBlockPreview)425@15155L99,425@15141L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1598324377, var1, -1, "io.intercom.android.sdk.survey.block.SubheadingTextBlockPreview (TextBlock.kt:419)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.SUBHEADING.getSerializedName())
            .withText("Subheading")
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-756436689, true, new TextBlockKt$SubheadingTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1304_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1304_0, recoveredExpressionArgument_1304_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.SubheadingTextBlockPreview$lambda$29(recoveredExpressionCapture_1304_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1304_0), ((java.lang.Integer)recoveredExpressionArgument_1304_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextBlockWithSuffixPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-979323118);
      ComposerKt.sourceInformation(var3, "C(TextBlockWithSuffixPreview)440@15497L191,440@15483L205:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-979323118, var1, -1, "io.intercom.android.sdk.survey.block.TextBlockWithSuffixPreview (TextBlock.kt:434)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.PARAGRAPH.getSerializedName())
            .withText("Paragraph with suffix")
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(960883112, true, new TextBlockKt$TextBlockWithSuffixPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1309_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1309_0, recoveredExpressionArgument_1309_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.TextBlockWithSuffixPreview$lambda$30(recoveredExpressionCapture_1309_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1309_0), ((java.lang.Integer)recoveredExpressionArgument_1309_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void OrderedListTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(627599340);
      ComposerKt.sourceInformation(var3, "C(OrderedListTextBlockPreview)464@16149L99,464@16135L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(627599340, var1, -1, "io.intercom.android.sdk.survey.block.OrderedListTextBlockPreview (TextBlock.kt:458)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.ORDEREDLIST.getSerializedName())
            .withItems(items)
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(644450326, true, new TextBlockKt$OrderedListTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1302_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1302_0, recoveredExpressionArgument_1302_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.OrderedListTextBlockPreview$lambda$31(recoveredExpressionCapture_1302_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1302_0), ((java.lang.Integer)recoveredExpressionArgument_1302_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnorderedListTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-321451131);
      ComposerKt.sourceInformation(var3, "C(UnorderedListTextBlockPreview)479@16481L99,479@16467L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-321451131, var1, -1, "io.intercom.android.sdk.survey.block.UnorderedListTextBlockPreview (TextBlock.kt:473)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.UNORDEREDLIST.getSerializedName())
            .withItems(items)
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1307522769, true, new TextBlockKt$UnorderedListTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1310_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1310_0, recoveredExpressionArgument_1310_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.UnorderedListTextBlockPreview$lambda$32(recoveredExpressionCapture_1310_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1310_0), ((java.lang.Integer)recoveredExpressionArgument_1310_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void OrderedListWithEmptyItemTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-881859700);
      ComposerKt.sourceInformation(var3, "C(OrderedListWithEmptyItemTextBlockPreview)496@16900L99,496@16886L113:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-881859700, var1, -1, "io.intercom.android.sdk.survey.block.OrderedListWithEmptyItemTextBlockPreview (TextBlock.kt:490)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.ORDEREDLIST.getSerializedName())
            .withItems(itemsWithEmpty)
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-73017438, true, new TextBlockKt$OrderedListWithEmptyItemTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1303_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1303_0, recoveredExpressionArgument_1303_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.OrderedListWithEmptyItemTextBlockPreview$lambda$33(recoveredExpressionCapture_1303_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1303_0), ((java.lang.Integer)recoveredExpressionArgument_1303_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InlineSourcesTextBlockPreview(@Nullable Composer var0, int var1) {
      Composer var3 = var0.startRestartGroup(-1209811272);
      ComposerKt.sourceInformation(var3, "C(InlineSourcesTextBlockPreview)516@17798L128,516@17784L142:TextBlock.kt#jxhu3x");
      if (var1 == 0 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1209811272, var1, -1, "io.intercom.android.sdk.survey.block.InlineSourcesTextBlockPreview (TextBlock.kt:510)");
         }

         Block var2 = new io.intercom.android.sdk.blocks.lib.models.Block$Builder()
            .withType(BlockType.PARAGRAPH.getSerializedName())
            .withText(
               "Hello World! This is <a href=\"https://github.com/ch4rl3x/HtmlText\">simple link</a>. And this is source link <a class=\"inline-citation\" data-entity-id=\"111\" data-entity-type=\"article\" data-source-index=\"1\" href=\"https://github.com/ch4rl3x/HtmlText\" title=\"Leaving Elephant Energy\">link here</a> <a class=\"inline-citation\" data-entity-id=\"222\" data-entity-type=\"article\" data-source-index=\"2\" href=\"https://github.com/ch4rl3x/HtmlText\" title=\"Article 2\">link here</a>"
            )
            .build();
         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(2099084386, true, new TextBlockKt$InlineSourcesTextBlockPreview$1(var2), var3, 54),
            var3,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1301_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1301_0, recoveredExpressionArgument_1301_1) -> io.intercom.android.sdk.survey.block.TextBlockKt.InlineSourcesTextBlockPreview$lambda$34(recoveredExpressionCapture_1301_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1301_0), ((java.lang.Integer)recoveredExpressionArgument_1301_1).intValue())));
      }
   }

   private static final Unit TextBlock$lambda$1$lambda$0(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit TextBlock$lambda$4(
      Modifier var0, BlockRenderData var1, SuffixText var2, boolean var3, Function1 var4, Function1 var5, int var6, int var7, Composer var8, int var9
   ) {
      TextBlock(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit InlineSourceBadge_FNF3uiM$lambda$6$lambda$5(long var0, DrawScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$drawBehind");
      DrawScope.recovered$34dce0a89fdcb963(var2, var0, 0.0F, 0L, 0.0F, null, null, 0, 126, null);
      return Unit.INSTANCE;
   }

   private static final Unit InlineSourceBadge_FNF3uiM$lambda$8(String var0, Modifier var1, long var2, int var4, int var5, Composer var6, int var7) {
      recovered$578f218e7033db7a(var0, var1, var2, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final void appendInlineSources$lambda$10(Function1 var0, Set var1, LinkAnnotation var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      var0.invoke(CollectionsKt.toList(var1));
   }

   private static final String textListToRender$bullet(boolean var0, int var1) {
      return var0 ? var1 + ".\t\t" : "•\t\t";
   }

   private static final Unit TextBlockPreview$lambda$25(int var0, Composer var1, int var2) {
      TextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InlineCodeTextBlockPreview$lambda$26(int var0, Composer var1, int var2) {
      InlineCodeTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextBlockAlignmentPreview$lambda$27(int var0, Composer var1, int var2) {
      TextBlockAlignmentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HeadingTextBlockPreview$lambda$28(int var0, Composer var1, int var2) {
      HeadingTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubheadingTextBlockPreview$lambda$29(int var0, Composer var1, int var2) {
      SubheadingTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextBlockWithSuffixPreview$lambda$30(int var0, Composer var1, int var2) {
      TextBlockWithSuffixPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit OrderedListTextBlockPreview$lambda$31(int var0, Composer var1, int var2) {
      OrderedListTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UnorderedListTextBlockPreview$lambda$32(int var0, Composer var1, int var2) {
      UnorderedListTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit OrderedListWithEmptyItemTextBlockPreview$lambda$33(int var0, Composer var1, int var2) {
      OrderedListWithEmptyItemTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InlineSourcesTextBlockPreview$lambda$34(int var0, Composer var1, int var2) {
      InlineSourcesTextBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void recovered$bc276efe291e8160(String var0, Modifier var1, long var2, Composer var4, int var5, int var6) {
      recovered$578f218e7033db7a(var0, var1, var2, var4, var5, var6);
   }

   // $VF: synthetic method
   public static final String access$textListToRender$bullet(boolean var0, int var1) {
      return textListToRender$bullet(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$appendInlineSources(Builder var0, AnnotatedString var1, Set var2, Function1 var3) {
      appendInlineSources(var0, var1, var2, var3);
   }

   static {
      String[] var0 = new String[]{
         "First item",
         "Second item with very long text. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
         "Hello <b>World</b>. This <i><strike>text</strike>sentence</i> is form<b>att<u>ed</u></b> in simple html. <a href=\"https://github.com/ch4rl3x/HtmlText\">HtmlText</a>"
      };
      items = CollectionsKt.listOf(var0);
      String[] var1 = new String[]{"First item", "", "Third item"};
      itemsWithEmpty = CollectionsKt.listOf(var1);
   }
}
