package io.intercom.android.sdk.survey.block;

import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import io.intercom.android.sdk.ui.theme.IntercomSyntaxColors;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\u001a#\u0010\u0005\u001a\u00020\u0003*\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0004\b\n\u0010\u000b\u001a'\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002¢\u0006\u0004\b\u000e\u0010\u000f\u001a\u001a\u0010\u0010\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\u001a\u0013\u0010\u0012\u001a\u00020\u0003*\u00020\tH\u0002¢\u0006\u0004\b\u0013\u0010\u0014\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0003X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0015",
   d2 = {
         "OPENING_SPAN",
         "Lkotlin/text/Regex;",
         "CLOSING_SPAN",
         "",
         "CLOSING_FONT",
         "toSyntaxHighlightedHtml",
         "syntax",
         "Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;",
         "defaultTextColor",
         "Landroidx/compose/ui/graphics/Color;",
         "toSyntaxHighlightedHtml-mxwnekA",
         "(Ljava/lang/String;Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;J)Ljava/lang/String;",
         "resolveTokenColor",
         "classAttribute",
         "resolveTokenColor-mxwnekA",
         "(Ljava/lang/String;Lio/intercom/android/sdk/ui/theme/IntercomSyntaxColors;J)J",
         "roleColorFor",
         "token",
         "toHtmlHex",
         "toHtmlHex-8_81llA",
         "(J)Ljava/lang/String;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCodeBlockSyntaxHighlighting.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CodeBlockSyntaxHighlighting.kt\nio/intercom/android/sdk/survey/block/CodeBlockSyntaxHighlightingKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,47:1\n1869#2:48\n1870#2:50\n1#3:49\n*S KotlinDebug\n*F\n+ 1 CodeBlockSyntaxHighlighting.kt\nio/intercom/android/sdk/survey/block/CodeBlockSyntaxHighlightingKt\n*L\n27#1:48\n27#1:50\n*E\n"
)
public final class CodeBlockSyntaxHighlightingKt {
   @NotNull
   private static final Regex OPENING_SPAN = new Regex("<span class=\"([^\"]*)\">");
   @NotNull
   private static final String CLOSING_SPAN = "</span>";
   @NotNull
   private static final String CLOSING_FONT = "</font>";

   @NotNull
   public static final String recovered$7de1aaa8180c861e(@NotNull String var0, @NotNull IntercomSyntaxColors var1, long var2) {
      Intrinsics.checkNotNullParameter(var0, "$this$toSyntaxHighlightedHtml");
      Intrinsics.checkNotNullParameter(var1, "syntax");
      final io.intercom.android.sdk.ui.theme.IntercomSyntaxColors recoveredExpressionCapture_1237_0 = var1;
      final long recoveredExpressionCapture_1237_1 = var2;
      String var4 = OPENING_SPAN.replace(var0, ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1237_0) -> io.intercom.android.sdk.survey.block.CodeBlockSyntaxHighlightingKt.toSyntaxHighlightedHtml_mxwnekA$lambda$0(recoveredExpressionCapture_1237_0, recoveredExpressionCapture_1237_1, (kotlin.text.MatchResult) ((kotlin.text.MatchResult)recoveredExpressionArgument_1237_0))));
      return StringsKt.replace$default(var4, "</span>", "</font>", false, 4, null);
   }

   private static final long recovered$12c8e4017c15387b(String var0, IntercomSyntaxColors var1, long var2) {
      CharSequence var10000 = var0;
      char[] var4 = new char[]{' '};
      Iterable var13 = StringsKt.split$default(var10000, var4, false, 0, 6, null);
      int var5 = 0;

      for (Object var7 : var13) {
         String var8 = (String)var7;
         int var9 = 0;
         Color var14 = roleColorFor(var8, var1);
         if (var14 != null) {
            long var10 = var14.recovered$45f9de7b85e0e0b5();
            int var12 = 0;
            return var10;
         }
      }

      return var2;
   }

   private static final Color roleColorFor(String var0, IntercomSyntaxColors var1) {
      String var2 = var0;
      switch (var2.hashCode()) {
         case 99:
            if (var2.equals("c")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 107:
            if (var2.equals("k")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 109:
            if (var2.equals("m")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 111:
            if (var2.equals("o")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$fbc8863c865b2b5a());
            }
            break;
         case 112:
            if (var2.equals("p")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$fbc8863c865b2b5a());
            }
            break;
         case 115:
            if (var2.equals("s")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3118:
            if (var2.equals("c1")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 3150:
            if (var2.equals("bp")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$e57f022d6951418e());
            }
            break;
         case 3173:
            if (var2.equals("ch")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 3178:
            if (var2.equals("cm")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 3181:
            if (var2.equals("cp")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 3184:
            if (var2.equals("cs")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
            break;
         case 3208:
            if (var2.equals("dl")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3271:
            if (var2.equals("fm")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$e57f022d6951418e());
            }
            break;
         case 3363:
            if (var2.equals("il")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3416:
            if (var2.equals("kc")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3417:
            if (var2.equals("kd")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3427:
            if (var2.equals("kn")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3429:
            if (var2.equals("kp")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3431:
            if (var2.equals("kr")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3433:
            if (var2.equals("kt")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3477:
            if (var2.equals("mb")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3481:
            if (var2.equals("mf")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3483:
            if (var2.equals("mh")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3484:
            if (var2.equals("mi")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3490:
            if (var2.equals("mo")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$681789bc19bfdf01());
            }
            break;
         case 3507:
            if (var2.equals("na")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3508:
            if (var2.equals("nb")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$e57f022d6951418e());
            }
            break;
         case 3509:
            if (var2.equals("nc")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$e57f022d6951418e());
            }
            break;
         case 3512:
            if (var2.equals("nf")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$e57f022d6951418e());
            }
            break;
         case 3526:
            if (var2.equals("nt")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$ca05afa51c4a9fa2());
            }
            break;
         case 3560:
            if (var2.equals("ow")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$0db701c7994a3f3b());
            }
            break;
         case 3614:
            if (var2.equals("s1")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3615:
            if (var2.equals("s2")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3662:
            if (var2.equals("sa")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3663:
            if (var2.equals("sb")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3664:
            if (var2.equals("sc")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3665:
            if (var2.equals("sd")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3666:
            if (var2.equals("se")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3669:
            if (var2.equals("sh")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3670:
            if (var2.equals("si")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3679:
            if (var2.equals("sr")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3680:
            if (var2.equals("ss")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 3685:
            if (var2.equals("sx")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$38d671a23d5a0e22());
            }
            break;
         case 98713:
            if (var2.equals("cpf")) {
               return Color.recovered$f46ef195ad4a2363(var1.recovered$394f95843b9a4fc9());
            }
      }

      return null;
   }

   private static final String recovered$dba46a12f8234fde(long var0) {
      String var2 = "#%06X";
      Object[] var3 = new Object[]{16777215 & ColorKt.recovered$81a7e7de546cbf77(var0)};
      var3 = var3;
      String var10000 = String.format(var2, Arrays.copyOf(var3, var3.length));
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   private static final CharSequence toSyntaxHighlightedHtml_mxwnekA$lambda$0(IntercomSyntaxColors var0, long var1, MatchResult var3) {
      Intrinsics.checkNotNullParameter(var3, "match");
      long var4 = recovered$12c8e4017c15387b((String)var3.getGroupValues().get(1), var0, var1);
      return "<font color=\"" + recovered$dba46a12f8234fde(var4) + "\">";
   }
}
