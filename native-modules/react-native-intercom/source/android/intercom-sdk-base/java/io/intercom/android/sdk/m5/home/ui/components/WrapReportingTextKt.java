package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.foundation.text.TextAutoSize;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.text.TextLayoutResult;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextDecoration;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\u001aV\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2#\b\u0002\u0010\n\u001a\u001d\u0012\u0013\u0012\u00110\f¢\u0006\f\b\r\u0012\b\b\u000e\u0012\u0004\b\b(\u000f\u0012\u0004\u0012\u00020\u00010\u000bH\u0001¢\u0006\u0004\b\u0010\u0010\u0011\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0013¨\u0006\u0014",
   d2 = {
         "WrapReportingText",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "text",
         "",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "style",
         "Landroidx/compose/ui/text/TextStyle;",
         "onTextWrap",
         "Lkotlin/Function1;",
         "",
         "Lkotlin/ParameterName;",
         "name",
         "textWrapped",
         "WrapReportingText-T042LqI",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;JLandroidx/compose/ui/text/TextStyle;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "PreviewShortText",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nWrapReportingText.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WrapReportingText.kt\nio/intercom/android/sdk/m5/home/ui/components/WrapReportingTextKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,47:1\n1047#2,6:48\n1047#2,6:54\n1047#2,6:60\n1047#2,6:66\n*S KotlinDebug\n*F\n+ 1 WrapReportingText.kt\nio/intercom/android/sdk/m5/home/ui/components/WrapReportingTextKt\n*L\n21#1:48,6\n23#1:54,6\n28#1:60,6\n29#1:66,6\n*E\n"
)
public final class WrapReportingTextKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$e51f4221e6440197(
      @Nullable Modifier var0, @NotNull String var1, long var2, @NotNull TextStyle var4, @Nullable Function1 var5, @Nullable Composer var6, int var7, int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var4, "style");
      Composer var41 = var6.startRestartGroup(834036955);
      ComposerKt.sourceInformation(
         var41, "C(WrapReportingText)P(1,4,0:c#ui.graphics.Color,3)20@734L2,22@764L34,27@920L40,28@985L141,23@803L330:WrapReportingText.kt#utrtal"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var41.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var41.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var41.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var41.changed(var4) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var41.changedInstance(var5) ? 16384 : 8192;
      }

      if ((var9 & 9363) == 9362 && var41.getSkipping()) {
         var41.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 16) != 0) {
            var41.startReplaceGroup(1070300984);
            ComposerKt.sourceInformation(var41, "CC(remember):WrapReportingText.kt#9igjgp");
            Composer var11 = var41;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var57 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_932_0) -> io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt.WrapReportingText_T042LqI$lambda$1$lambda$0(((java.lang.Boolean)recoveredExpressionArgument_932_0).booleanValue()));
               var11.updateRememberedValue(var57);
               var10000 = var57;
            } else {
               var10000 = var14;
            }

            Function1 var10 = (Function1)var10000;
            var41.endReplaceGroup();
            var5 = var10;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(834036955, var9, -1, "io.intercom.android.sdk.m5.home.ui.components.WrapReportingText (WrapReportingText.kt:21)");
         }

         var41.startReplaceGroup(1070301976);
         ComposerKt.sourceInformation(var41, "CC(remember):WrapReportingText.kt#9igjgp");
         Composer var45 = var41;
         boolean var48 = false;
         int var51 = 0;
         Object var54 = var45.rememberedValue();
         int var58 = 0;
         Object var67;
         if (var54 == Composer.Companion.getEmpty()) {
            int var17 = 0;
            Object var61 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var45.updateRememberedValue(var61);
            var67 = var61;
         } else {
            var67 = var54;
         }

         MutableState var43 = (MutableState)var67;
         var41.endReplaceGroup();
         MutableState var42 = var43;
         Modifier var68 = var0;
         var41.startReplaceGroup(1070306974);
         ComposerKt.sourceInformation(var41, "CC(remember):WrapReportingText.kt#9igjgp");
         Composer var49 = var41;
         boolean var52 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var49.rememberedValue();
         int var62/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var71;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Modifier var20 = var0;
            int var18 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_933_0 = var42;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_933_0) -> io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt.WrapReportingText_T042LqI$lambda$4$lambda$3(recoveredExpressionCapture_933_0, (androidx.compose.ui.graphics.drawscope.ContentDrawScope) ((androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredExpressionArgument_933_0)));
            var68 = var20;
            Object var19 = var10001;
            var49.updateRememberedValue(var19);
            var71 = var19;
         } else {
            var71 = it$ivx;
         }

         Function1 var46 = (Function1)var71;
         var41.endReplaceGroup();
         Modifier var44 = DrawModifierKt.drawWithContent(var68, var46);
         String var69 = var1;
         Modifier var72 = var44;
         long var10002 = var2;
         Object var10003 = null;
         long var10004 = 0L;
         Object var10005 = null;
         Object var10006 = null;
         Object var10007 = null;
         long var10008 = 0L;
         Object var10009 = null;
         Object var10010 = null;
         long var10011 = 0L;
         byte var10012 = 0;
         boolean var10013 = false;
         byte var10014 = 0;
         byte var10015 = 0;
         var41.startReplaceGroup(1070309155);
         ComposerKt.sourceInformation(var41, "CC(remember):WrapReportingText.kt#9igjgp");
         Composer var50 = var41;
         boolean var53 = (var9 & 57344) == 16384;
         int $i$f$cachexx = 0;
         Object it$ivxx = var50.rememberedValue();
         int var63/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10016;
         if (!var53 && it$ivxx != Composer.Companion.getEmpty()) {
            var10016 = it$ivxx;
         } else {
            byte var39 = 0;
            byte var38 = 0;
            boolean var37 = false;
            byte var36 = 0;
            long var34 = 0L;
            Object var33 = null;
            Object var32 = null;
            long var30 = 0L;
            Object var29 = null;
            Object var28 = null;
            Object var27 = null;
            long var25 = 0L;
            Object var24 = null;
            long var22 = var2;
            Modifier var21 = var44;
            String var66 = var1;
            int var64 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_934_0 = var5;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_934_1 = var42;
            Function1 var40 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_934_0) -> io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt.WrapReportingText_T042LqI$lambda$6$lambda$5(recoveredExpressionCapture_934_0, recoveredExpressionCapture_934_1, (androidx.compose.ui.text.TextLayoutResult) ((androidx.compose.ui.text.TextLayoutResult)recoveredExpressionArgument_934_0)));
            var69 = var66;
            var72 = var21;
            var10002 = var22;
            var10003 = var24;
            var10004 = var25;
            var10005 = var27;
            var10006 = var28;
            var10007 = var29;
            var10008 = var30;
            var10009 = var32;
            var10010 = var33;
            var10011 = var34;
            var10012 = var36;
            var10013 = var37;
            var10014 = var38;
            var10015 = var39;
            Object var65 = var40;
            var50.updateRememberedValue(var65);
            var10016 = var65;
         }

         Function1 var47 = (Function1)var10016;
         var41.endReplaceGroup();
         TextKt.recovered$5b114ab32a4f4190(
            var69,
            var72,
            var10002,
            (TextAutoSize)var10003,
            var10004,
            (FontStyle)var10005,
            (FontWeight)var10006,
            (FontFamily)var10007,
            var10008,
            (TextDecoration)var10009,
            (TextAlign)var10010,
            var10011,
            var10012,
            var10013,
            var10014,
            var10015,
            var47,
            var4,
            var41,
            14 & var9 >> 3 | 896 & var9,
            29360128 & var9 << 12,
            65528
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var70 = var41.endRestartGroup();
      if (var70 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_935_0 = var0;
         final java.lang.String recoveredExpressionCapture_935_1 = var1;
         final long recoveredExpressionCapture_935_2 = var2;
         final androidx.compose.ui.text.TextStyle recoveredExpressionCapture_935_3 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_935_4 = var5;
         final int recoveredExpressionCapture_935_5 = var7;
         final int recoveredExpressionCapture_935_6 = var8;
         var70.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_935_0, recoveredExpressionArgument_935_1) -> io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt.WrapReportingText_T042LqI$lambda$7(recoveredExpressionCapture_935_0, recoveredExpressionCapture_935_1, recoveredExpressionCapture_935_2, recoveredExpressionCapture_935_3, recoveredExpressionCapture_935_4, recoveredExpressionCapture_935_5, recoveredExpressionCapture_935_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_935_0), ((java.lang.Integer)recoveredExpressionArgument_935_1).intValue())));
      }
   }

   @Composable
   @Preview
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewShortText(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(381018303);
      ComposerKt.sourceInformation(var2, "C(PreviewShortText)38@1195L179:WrapReportingText.kt#utrtal");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(381018303, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.PreviewShortText (WrapReportingText.kt:37)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.home.ui.components.ComposableSingletons$WrapReportingTextKt.INSTANCE.recovered$eac478aa65045e5d(),
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
         final int recoveredExpressionCapture_931_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_931_0, recoveredExpressionArgument_931_1) -> io.intercom.android.sdk.m5.home.ui.components.WrapReportingTextKt.PreviewShortText$lambda$8(recoveredExpressionCapture_931_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_931_0), ((java.lang.Integer)recoveredExpressionArgument_931_1).intValue())));
      }
   }

   private static final Unit WrapReportingText_T042LqI$lambda$1$lambda$0(boolean var0) {
      return Unit.INSTANCE;
   }

   private static final Unit WrapReportingText_T042LqI$lambda$4$lambda$3(MutableState var0, ContentDrawScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$drawWithContent");
      if ((Boolean)var0.getValue()) {
         var1.drawContent();
      }

      return Unit.INSTANCE;
   }

   private static final Unit WrapReportingText_T042LqI$lambda$6$lambda$5(Function1 var0, MutableState var1, TextLayoutResult var2) {
      Intrinsics.checkNotNullParameter(var2, "textLayoutResult");
      var0.invoke(var2.getLineCount() > 1);
      var1.setValue(true);
      return Unit.INSTANCE;
   }

   private static final Unit WrapReportingText_T042LqI$lambda$7(
      Modifier var0, String var1, long var2, TextStyle var4, Function1 var5, int var6, int var7, Composer var8, int var9
   ) {
      recovered$e51f4221e6440197(var0, var1, var2, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewShortText$lambda$8(int var0, Composer var1, int var2) {
      PreviewShortText(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
