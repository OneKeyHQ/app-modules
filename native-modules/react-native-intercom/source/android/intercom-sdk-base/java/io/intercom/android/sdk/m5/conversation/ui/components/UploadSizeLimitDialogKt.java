package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.window.DialogProperties;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a-\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0002\u0010\u0007\u001a\r\u0010\b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\t¨\u0006\n",
   d2 = {
         "UploadSizeLimitDialog",
         "",
         "title",
         "",
         "message",
         "onDismiss",
         "Lkotlin/Function0;",
         "(Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "UploadSizeLimitDialogPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nUploadSizeLimitDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadSizeLimitDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadSizeLimitDialogKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,67:1\n1047#2,6:68\n1047#2,6:74\n*S KotlinDebug\n*F\n+ 1 UploadSizeLimitDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadSizeLimitDialogKt\n*L\n17#1:68,6\n35#1:74,6\n*E\n"
)
public final class UploadSizeLimitDialogKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadSizeLimitDialog(
      @NotNull String var0, @NotNull String var1, @Nullable Function0 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "title");
      Intrinsics.checkNotNullParameter(var1, "message");
      var3 = var3.startRestartGroup(-2057533015);
      ComposerKt.sourceInformation(
         var3, "C(UploadSizeLimitDialog)P(2)16@588L2,19@652L6,34@1112L35,37@1173L388,20@686L189,27@892L191,18@600L1061:UploadSizeLimitDialog.kt#o19g89"
      );
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var3.startReplaceGroup(-537331028);
            ComposerKt.sourceInformation(var3, "CC(remember):UploadSizeLimitDialog.kt#9igjgp");
            Composer var8 = var3;
            boolean var9 = false;
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10000;
            if (var11 == Composer.Companion.getEmpty()) {
               int var13 = 0;
               Object var24 = ((kotlin.jvm.functions.Function0) () -> UploadSizeLimitDialogKt.UploadSizeLimitDialog$lambda$1$lambda$0());
               var8.updateRememberedValue(var24);
               var10000 = var24;
            } else {
               var10000 = var11;
            }

            Function0 var7 = (Function0)var10000;
            var3.endReplaceGroup();
            var2 = var7;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2057533015, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadSizeLimitDialog (UploadSizeLimitDialog.kt:17)"
            );
         }

         long var19 = IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         DialogProperties var20 = new DialogProperties(false, false, false, 5, null);
         var3.startReplaceGroup(-537314227);
         ComposerKt.sourceInformation(var3, "CC(remember):UploadSizeLimitDialog.kt#9igjgp");
         Composer var22 = var3;
         boolean var23 = (var6 & 896) == 256;
         int var25 = 0;
         Object var14 = var22.rememberedValue();
         int var15 = 0;
         Object var26;
         if (!var23 && var14 != Composer.Companion.getEmpty()) {
            var26 = var14;
         } else {
            int var16 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_498_0 = var2;
            Object var17 = ((kotlin.jvm.functions.Function0) () -> UploadSizeLimitDialogKt.UploadSizeLimitDialog$lambda$3$lambda$2(recoveredCapture_498_0));
            var22.updateRememberedValue(var17);
            var26 = var17;
         }

         Function0 var21 = (Function0)var26;
         var3.endReplaceGroup();
         AndroidAlertDialog_androidKt.recovered$feba587216618f03(
            var21,
            ComposableLambdaKt.rememberComposableLambda(582096881, true, new UploadSizeLimitDialogKt$UploadSizeLimitDialog$3(var2), var3, 54),
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-905132043, true, new UploadSizeLimitDialogKt$UploadSizeLimitDialog$4(var0), var3, 54),
            ComposableLambdaKt.rememberComposableLambda(-203197450, true, new UploadSizeLimitDialogKt$UploadSizeLimitDialog$5(var1), var3, 54),
            null,
            var19,
            0L,
            0L,
            0L,
            0.0F,
            var20,
            var3,
            1769520,
            3072,
            7836
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var3.endRestartGroup();
      if (var27 != null) {
         final java.lang.String recoveredCapture_499_0 = var0;
         final java.lang.String recoveredCapture_499_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_499_2 = var2;
         final int recoveredCapture_499_3 = var4;
         final int recoveredCapture_499_4 = var5;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_499_0, recoveredArgument_499_1) -> UploadSizeLimitDialogKt.UploadSizeLimitDialog$lambda$4(recoveredCapture_499_0, recoveredCapture_499_1, recoveredCapture_499_2, recoveredCapture_499_3, recoveredCapture_499_4, (androidx.compose.runtime.Composer)recoveredArgument_499_0, ((java.lang.Integer)recoveredArgument_499_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadSizeLimitDialogPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-532105575);
      ComposerKt.sourceInformation(var0, "C(UploadSizeLimitDialogPreview)59@1745L152:UploadSizeLimitDialog.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -532105575, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadSizeLimitDialogPreview (UploadSizeLimitDialog.kt:58)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$UploadSizeLimitDialogKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_500_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_500_0, recoveredArgument_500_1) -> UploadSizeLimitDialogKt.UploadSizeLimitDialogPreview$lambda$5(recoveredCapture_500_0, (androidx.compose.runtime.Composer)recoveredArgument_500_0, ((java.lang.Integer)recoveredArgument_500_1).intValue())));
      }
   }

   private static final Unit UploadSizeLimitDialog$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit UploadSizeLimitDialog$lambda$3$lambda$2(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit UploadSizeLimitDialog$lambda$4(String var0, String var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      UploadSizeLimitDialog(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit UploadSizeLimitDialogPreview$lambda$5(int var0, Composer var1, int var2) {
      UploadSizeLimitDialogPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
