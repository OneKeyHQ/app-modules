package io.intercom.android.sdk.survey.ui.questiontype.numericscale;

import android.content.Context;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorMatrix;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import coil.compose.AsyncImagePainter;
import coil.compose.AsyncImagePainterKt;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a/\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b¨\u0006\r",
   d2 = {
         "EmojiRating",
         "",
         "emojiUrl",
         "",
         "contentDescription",
         "showInColor",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Ljava/lang/String;ZLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "SelectedEmoji",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UnSelectedEmoji",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nEmojiRating.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EmojiRating.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/EmojiRatingKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,62:1\n75#2:63\n1#3:64\n118#4:65\n118#4:66\n*S KotlinDebug\n*F\n+ 1 EmojiRating.kt\nio/intercom/android/sdk/survey/ui/questiontype/numericscale/EmojiRatingKt\n*L\n23#1:63\n48#1:65\n59#1:66\n*E\n"
)
public final class EmojiRatingKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EmojiRating(
      @NotNull String var0, @NotNull String var1, boolean var2, @NotNull Modifier var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "emojiUrl");
      Intrinsics.checkNotNullParameter(var1, "contentDescription");
      Intrinsics.checkNotNullParameter(var3, "modifier");
      var4 = var4.startRestartGroup(-538688650);
      ComposerKt.sourceInformation(var4, "C(EmojiRating)P(1!1,3)22@785L7,23@816L204,30@1025L262:EmojiRating.kt#hdztyp");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-538688650, var7, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.EmojiRating (EmojiRating.kt:21)");
         }

         CompositionLocal var9 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var10 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var12 = var4.consume(var9);
         ComposerKt.sourceInformationMarkerEnd(var4);
         Context var8 = (Context)var12;
         AsyncImagePainter var22 = AsyncImagePainterKt.recovered$8ecdef7ed4026761(
            new Builder(var8).data(var0).crossfade(true).build(), IntercomImageLoaderKt.getImageLoader(var8), null, null, null, 0, null, var4, 0, 124
         );
         Painter var10000 = var22;
         Companion var10006 = ColorFilter.Companion;
         float[] var23 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         float[] var24 = var23;
         Companion var19 = var10006;
         float var18 = 0.0F;
         Object var17 = null;
         Object var16 = null;
         Modifier var15 = var3;
         String var14 = var1;
         Painter var13 = var10000;
         int var25 = 0;
         ColorMatrix.recovered$e4d8f44c8df66f0d(var24, var2 ? 1.0F : 0.0F);
         Unit var20 = Unit.INSTANCE;
         ImageKt.Image(
            var13, var14, var15, (Alignment)var16, (ContentScale)var17, var18, var19.recovered$056553a490480fb1(var23), var4, 112 & var7 | 896 & var7 >> 3, 56
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var26 = var4.endRestartGroup();
      if (var26 != null) {
         final java.lang.String recoveredCapture_1468_0 = var0;
         final java.lang.String recoveredCapture_1468_1 = var1;
         final boolean recoveredCapture_1468_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_1468_3 = var3;
         final int recoveredCapture_1468_4 = var5;
         final int recoveredCapture_1468_5 = var6;
         var26.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1468_0, recoveredArgument_1468_1) -> EmojiRatingKt.EmojiRating$lambda$1(recoveredCapture_1468_0, recoveredCapture_1468_1, recoveredCapture_1468_2, recoveredCapture_1468_3, recoveredCapture_1468_4, recoveredCapture_1468_5, (androidx.compose.runtime.Composer)recoveredArgument_1468_0, ((java.lang.Integer)recoveredArgument_1468_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SelectedEmoji(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1230381561);
      ComposerKt.sourceInformation(var0, "C(SelectedEmoji)43@1347L204:EmojiRating.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1230381561, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.SelectedEmoji (EmojiRating.kt:42)");
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 44;
         int var4 = 0;
         Modifier var2 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var3));
         EmojiRating("https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83e\udd29", true, var2, var0, 3510, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var6 = var0.endRestartGroup();
      if (var6 != null) {
         final int recoveredCapture_1469_0 = var1;
         var6.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1469_0, recoveredArgument_1469_1) -> EmojiRatingKt.SelectedEmoji$lambda$2(recoveredCapture_1469_0, (androidx.compose.runtime.Composer)recoveredArgument_1469_0, ((java.lang.Integer)recoveredArgument_1469_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnSelectedEmoji(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2043776160);
      ComposerKt.sourceInformation(var0, "C(UnSelectedEmoji)54@1613L205:EmojiRating.kt#hdztyp");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2043776160, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.numericscale.UnSelectedEmoji (EmojiRating.kt:53)");
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 44;
         int var4 = 0;
         Modifier var2 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var3));
         EmojiRating("https://js.intercomcdn.com/images/emojis/v2/72x72/1f929.png", "\ud83e\udd29", false, var2, var0, 3510, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var6 = var0.endRestartGroup();
      if (var6 != null) {
         final int recoveredCapture_1470_0 = var1;
         var6.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1470_0, recoveredArgument_1470_1) -> EmojiRatingKt.UnSelectedEmoji$lambda$3(recoveredCapture_1470_0, (androidx.compose.runtime.Composer)recoveredArgument_1470_0, ((java.lang.Integer)recoveredArgument_1470_1).intValue())));
      }
   }

   private static final Unit EmojiRating$lambda$1(String var0, String var1, boolean var2, Modifier var3, int var4, int var5, Composer var6, int var7) {
      EmojiRating(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit SelectedEmoji$lambda$2(int var0, Composer var1, int var2) {
      SelectedEmoji(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UnSelectedEmoji$lambda$3(int var0, Composer var1, int var2) {
      UnSelectedEmoji(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
