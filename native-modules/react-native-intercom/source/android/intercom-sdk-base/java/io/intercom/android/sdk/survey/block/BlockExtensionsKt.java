package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.LinkAnnotation;
import androidx.compose.ui.text.LinkInteractionListener;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.TextLinkStyles;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.LinkAnnotation.Clickable;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000J\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0002\u001a\u00020\u0003*\u00020\u0003H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0003*\u00020\u0003H\u0000\u001a\u000f\u0010\u0007\u001a\u00020\b*\u00020\t¢\u0006\u0002\u0010\n\u001a/\u0010\u000b\u001a\u00020\f*\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0000¢\u0006\u0004\b\u0014\u0010\u0015\u001a$\u0010\u0016\u001a\u00020\u0017*\u00020\u00182\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0019\u001a\u00020\u001aH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b",
   d2 = {
         "CODE_LINE_BREAK",
         "Lkotlin/text/Regex;",
         "preserveCodeIndentation",
         "",
         "MONOSPACE_FAMILY",
         "INLINE_CODE_TAG",
         "rewriteInlineCodeToMonospace",
         "getTextAlign",
         "Landroidx/compose/ui/text/style/TextAlign;",
         "Lio/intercom/android/sdk/blocks/lib/BlockAlignment;",
         "(Lio/intercom/android/sdk/blocks/lib/BlockAlignment;)I",
         "toAnnotatedString",
         "Landroidx/compose/ui/text/AnnotatedString;",
         "",
         "context",
         "Landroid/content/Context;",
         "urlSpanStyle",
         "Landroidx/compose/ui/text/SpanStyle;",
         "inlineCodeBackground",
         "Landroidx/compose/ui/graphics/Color;",
         "toAnnotatedString-g2O1Hgs",
         "(Ljava/lang/CharSequence;Landroid/content/Context;Landroidx/compose/ui/text/SpanStyle;J)Landroidx/compose/ui/text/AnnotatedString;",
         "appendStringWithLink",
         "",
         "Landroidx/compose/ui/text/AnnotatedString$Builder;",
         "source",
         "Landroid/text/Spanned;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBlockExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlockExtensions.kt\nio/intercom/android/sdk/survey/block/BlockExtensionsKt\n+ 2 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n+ 3 SpannedString.kt\nandroidx/core/text/SpannedStringKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,180:1\n1580#2:181\n1580#2:215\n1512#2,6:221\n31#3,4:182\n31#3,4:186\n31#3,4:190\n31#3,4:194\n31#3,4:198\n31#3,4:216\n3829#4:202\n4344#4,2:203\n13472#4,2:205\n13472#4,2:207\n13472#4,2:209\n13472#4,2:211\n13472#4:220\n13473#4:227\n1869#5,2:213\n648#6,5:228\n*S KotlinDebug\n*F\n+ 1 BlockExtensions.kt\nio/intercom/android/sdk/survey/block/BlockExtensionsKt\n*L\n90#1:181\n138#1:215\n168#1:221,6\n91#1:182,4\n92#1:186,4\n93#1:190,4\n94#1:194,4\n95#1:198,4\n146#1:216,4\n95#1:202\n95#1:203,2\n99#1:205,2\n112#1:207,2\n117#1:209,2\n122#1:211,2\n148#1:220\n148#1:227\n127#1:213,2\n54#1:228,5\n*E\n"
)
public final class BlockExtensionsKt {
   @NotNull
   private static final Regex CODE_LINE_BREAK = new Regex("<br\\s*/?>|\\r\\n|[\\r\\n]", RegexOption.IGNORE_CASE);
   @NotNull
   private static final String MONOSPACE_FAMILY = "monospace";
   @NotNull
   private static final Regex INLINE_CODE_TAG = new Regex("<(/?)code(?:\\s+(?:\"[^\"]*\"|'[^']*'|[^\"'>])*)?>", RegexOption.IGNORE_CASE);

   @NotNull
   public static final String preserveCodeIndentation(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      CharSequence var1 = var0;
      Regex var2 = CODE_LINE_BREAK;
      byte var3 = 0;
      return CollectionsKt.joinToString$default(
         var2.split(var1, var3), "<br>", null, null, 0, null, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1224_0) -> io.intercom.android.sdk.survey.block.BlockExtensionsKt.preserveCodeIndentation$lambda$1((java.lang.String) ((java.lang.String)recoveredExpressionArgument_1224_0))), 30, null
      );
   }

   @NotNull
   public static final String rewriteInlineCodeToMonospace(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return INLINE_CODE_TAG.replace(var0, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1225_0) -> io.intercom.android.sdk.survey.block.BlockExtensionsKt.rewriteInlineCodeToMonospace$lambda$2((kotlin.text.MatchResult) ((kotlin.text.MatchResult)recoveredExpressionArgument_1225_0))));
   }

   public static final int getTextAlign(@NotNull BlockAlignment var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      int var10000;
      switch (var0.getGravity()) {
         case 1:
            var10000 = TextAlign.Companion.recovered$d707b9dd64876179();
            break;
         case 8388611:
            var10000 = TextAlign.Companion.recovered$4fce64874a0d5cf7();
            break;
         case 8388613:
            var10000 = TextAlign.Companion.recovered$ed35a36f4378b5e0();
            break;
         default:
            var10000 = TextAlign.Companion.recovered$4fce64874a0d5cf7();
      }

      return var10000;
   }

   @NotNull
   public static final AnnotatedString recovered$13969ea35978a370(@NotNull CharSequence var0, @NotNull Context var1, @NotNull SpanStyle var2, long var3) {
      Intrinsics.checkNotNullParameter(var0, "$this$toAnnotatedString");
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "urlSpanStyle");
      AnnotatedString var10000;
      if (var0 instanceof Spanned) {
         int var5 = 0;
         Builder var6 = new Builder(0, 1, null);
         Builder var7 = var6;
         int var8 = 0;
         Spanned var9 = (Spanned)var0;
         int var10 = 0;
         int var11 = var9.length();
         int var12 = 0;
         StyleSpan[] var13 = (StyleSpan[])var9.getSpans(var10, var11, StyleSpan.class);
         Spanned var29 = (Spanned)var0;
         int start$ivx = 0;
         int end$ivx = var29.length();
         int $i$f$getSpansx = 0;
         UnderlineSpan[] var28 = (UnderlineSpan[])var29.getSpans(start$ivx, end$ivx, UnderlineSpan.class);
         Spanned var32 = (Spanned)var0;
         int start$ivxx = 0;
         int end$ivxx = var32.length();
         int $i$f$getSpansxx = 0;
         StrikethroughSpan[] var30 = (StrikethroughSpan[])var32.getSpans(start$ivxx, end$ivxx, StrikethroughSpan.class);
         Spanned var36 = (Spanned)var0;
         int start$ivxxx = 0;
         int end$ivxxx = var36.length();
         int $i$f$getSpansxxx = 0;
         ForegroundColorSpan[] var33 = (ForegroundColorSpan[])var36.getSpans(start$ivxxx, end$ivxxx, ForegroundColorSpan.class);
         Spanned var40 = (Spanned)var0;
         int start$ivxxxx = 0;
         int end$ivxxxx = var40.length();
         int $i$f$getSpansxxxx = 0;
         Object[] var41 = var40.getSpans(start$ivxxxx, end$ivxxxx, TypefaceSpan.class);
         int var49 = 0;
         Object[] var56 = var41;
         ArrayList var62 = new ArrayList();
         int var18 = 0;
         int var19 = 0;

         for (int var20 = var56.length; var19 < var20; var19++) {
            Object var21 = var56[var19];
            TypefaceSpan var22 = (TypefaceSpan)var21;
            int var23 = 0;
            if (Intrinsics.areEqual(var22.getFamily(), "monospace")) {
               var62.add(var21);
            }
         }

         List var37 = (List)var62;
         appendStringWithLink(var7, var2, var1, (Spanned)var0);
         Object[] var42 = var13;
         int var50 = 0;
         int var57 = 0;

         for (int var63 = var42.length; var57 < var63; var57++) {
            Object var68 = var42[var57];
            StyleSpan var73 = (StyleSpan)var68;
            int var78 = 0;
            int var83 = ((Spanned)var0).getSpanStart(var73);
            int var88 = ((Spanned)var0).getSpanEnd(var73);
            switch (var73.getStyle()) {
               case 1:
                  var7.addStyle(
                     new SpanStyle(
                        0L, 0L, FontWeight.Companion.getBold(), null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65531, null
                     ),
                     var83,
                     var88
                  );
                  break;
               case 2:
                  var7.addStyle(
                     new SpanStyle(
                        0L,
                        0L,
                        null,
                        FontStyle.recovered$c2bf7a301dcde394(FontStyle.Companion.recovered$3a5e0ce5c0df0c41()),
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
                        null,
                        65527,
                        null
                     ),
                     var83,
                     var88
                  );
                  break;
               case 3:
                  var7.addStyle(
                     new SpanStyle(
                        0L,
                        0L,
                        FontWeight.Companion.getBold(),
                        FontStyle.recovered$c2bf7a301dcde394(FontStyle.Companion.recovered$3a5e0ce5c0df0c41()),
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
                        null,
                        65523,
                        null
                     ),
                     var83,
                     var88
                  );
            }
         }

         Object[] $this$forEach$ivx = var28;
         int $i$f$forEachx = 0;
         int var58 = 0;

         for (int var64 = $this$forEach$ivx.length; var58 < var64; var58++) {
            Object var69 = $this$forEach$ivx[var58];
            UnderlineSpan var74 = (UnderlineSpan)var69;
            int var79 = 0;
            int var84 = ((Spanned)var0).getSpanStart(var74);
            int var89 = ((Spanned)var0).getSpanEnd(var74);
            var7.addStyle(
               new SpanStyle(
                  0L, 0L, null, null, null, null, null, 0L, null, null, null, 0L, TextDecoration.Companion.getUnderline(), null, null, null, 61439, null
               ),
               var84,
               var89
            );
         }

         Object[] $this$forEach$ivxx = var30;
         int $i$f$forEachxx = 0;
         int var59 = 0;

         for (int var65 = $this$forEach$ivxx.length; var59 < var65; var59++) {
            Object var70 = $this$forEach$ivxx[var59];
            StrikethroughSpan var75 = (StrikethroughSpan)var70;
            int var80 = 0;
            int var85 = ((Spanned)var0).getSpanStart(var75);
            int var90 = ((Spanned)var0).getSpanEnd(var75);
            var7.addStyle(
               new SpanStyle(
                  0L, 0L, null, null, null, null, null, 0L, null, null, null, 0L, TextDecoration.Companion.getLineThrough(), null, null, null, 61439, null
               ),
               var85,
               var90
            );
         }

         Object[] $this$forEach$ivxxx = var33;
         int $i$f$forEachxxx = 0;
         int var60 = 0;

         for (int var66 = $this$forEach$ivxxx.length; var60 < var66; var60++) {
            Object var71 = $this$forEach$ivxxx[var60];
            ForegroundColorSpan var76 = (ForegroundColorSpan)var71;
            int var81 = 0;
            int var86 = ((Spanned)var0).getSpanStart(var76);
            int var91 = ((Spanned)var0).getSpanEnd(var76);
            var7.addStyle(
               new SpanStyle(
                  ColorKt.Color(var76.getForegroundColor()), 0L, null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65534, null
               ),
               var86,
               var91
            );
         }

         Iterable $this$forEach$ivxxxx = var37;
         int $i$f$forEachxxxx = 0;

         for (Object var67 : $this$forEach$ivxxxx) {
            TypefaceSpan var72 = (TypefaceSpan)var67;
            int var77 = 0;
            int var82 = ((Spanned)var0).getSpanStart(var72);
            int var87 = ((Spanned)var0).getSpanEnd(var72);
            var7.addStyle(
               new SpanStyle(
                  0L, 0L, null, null, null, FontFamily.Companion.getMonospace(), null, 0L, null, null, null, var3, null, null, null, null, 63455, null
               ),
               var82,
               var87
            );
         }

         var10000 = var6.toAnnotatedString();
      } else {
         int var24 = 0;
         Builder var25 = new Builder(0, 1, null);
         Builder var26 = var25;
         int var27 = 0;
         var26.append(var26.toString());
         var10000 = var25.toAnnotatedString();
      }

      return var10000;
   }

   // $VF: synthetic method
   public static AnnotatedString recovered$c71786fddfec5215(CharSequence var0, Context var1, SpanStyle var2, long var3, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var2 = new SpanStyle(
            0L, 0L, null, null, null, null, null, 0L, null, null, null, 0L, TextDecoration.Companion.getUnderline(), null, null, null, 61439, null
         );
      }

      if ((var5 & 4) != 0) {
         var3 = Color.Companion.recovered$37fe95da52e29a80();
      }

      return recovered$13969ea35978a370(var0, var1, var2, var3);
   }

   private static final void appendStringWithLink(Builder var0, SpanStyle var1, Context var2, Spanned var3) {
      Spanned var5 = var3;
      int var6 = 0;
      int var7 = var5.length();
      int var8 = 0;
      URLSpan[] var4 = (URLSpan[])var5.getSpans(var6, var7, URLSpan.class);
      int var25 = 0;
      Object[] var26 = var4;
      int var27 = 0;
      int var28 = 0;

      for (int var9 = var26.length; var28 < var9; var28++) {
         Object var10 = var26[var28];
         URLSpan var11 = (URLSpan)var10;
         int var12 = 0;
         final android.text.style.URLSpan recoveredExpressionCapture_1223_0 = var11;
         final android.content.Context recoveredExpressionCapture_1223_1 = var2;
         LinkInteractionListener var13 = ((androidx.compose.ui.text.LinkInteractionListener) (recoveredExpressionArgument_1223_0) -> io.intercom.android.sdk.survey.block.BlockExtensionsKt.appendStringWithLink$lambda$13$lambda$11(recoveredExpressionCapture_1223_0, recoveredExpressionCapture_1223_1, (androidx.compose.ui.text.LinkAnnotation) (recoveredExpressionArgument_1223_0)));
         TextLinkStyles var14 = new TextLinkStyles(var1, null, null, null, 14, null);
         Clickable var15 = new Clickable("url", var14, var13);
         int var29 = var3.getSpanStart(var11);
         int var30 = var3.getSpanEnd(var11);
         if (var29 > var25) {
            var0.append(var3.subSequence(var25, var29).toString());
         }

         Builder var16 = var0;
         int var17 = 0;
         int var18 = var16.pushLink(var15);

         try {
            Builder var19 = var16;
            int var20 = 0;
            var19.append(var3.subSequence(var29, var30).toString());
            Unit var21 = Unit.INSTANCE;
         } finally {
            var16.pop(var18);
         }

         var25 = var30;
      }

      if (var25 < var3.length()) {
         var0.append(var3.subSequence(var25, var3.length()).toString());
      }
   }

   private static final CharSequence preserveCodeIndentation$lambda$1(String var0) {
      Intrinsics.checkNotNullParameter(var0, "line");
      String var2 = var0;
      int var3 = 0;
      int var4 = 0;
      int var5 = var2.length();

      String var10000;
      while (true) {
         if (var4 >= var5) {
            var10000 = var2;
            break;
         }

         char var6 = var2.charAt(var4);
         int var7 = 0;
         if (var6 != ' ') {
            String var8 = var2;
            byte var9 = 0;
            var10000 = var8.substring(var9, var4);
            Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
            break;
         }

         var4++;
      }

      int var1 = var10000.length();
      CharSequence var10;
      if (var1 == 0) {
         var10 = var0;
      } else {
         StringBuilder var11 = new StringBuilder().append(StringsKt.repeat("&nbsp;", var1));
         String var10001 = var0.substring(var1);
         Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
         var10 = var11.append(var10001).toString();
      }

      return var10;
   }

   private static final CharSequence rewriteInlineCodeToMonospace$lambda$2(MatchResult var0) {
      Intrinsics.checkNotNullParameter(var0, "match");
      return '<' + (String)var0.getGroupValues().get(1) + "tt>";
   }

   private static final void appendStringWithLink$lambda$13$lambda$11(URLSpan var0, Context var1, LinkAnnotation var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      String var10000 = var0.getURL();
      Intrinsics.checkNotNullExpressionValue(var10000, "getURL(...)");
      if (!StringsKt.isBlank(var10000)) {
         LinkOpener.handleUrl(var0.getURL(), var1, Injector.get().getApi());
      }
   }
}
