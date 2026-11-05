package io.intercom.android.sdk.ui.component;

import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.window.DialogProperties;
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
   d1 = "\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a[\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0007¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000f¨\u0006\u0010",
   d2 = {
         "PermissionDeniedDialog",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "message",
         "",
         "confirmText",
         "dismissText",
         "iconId",
         "",
         "onDismiss",
         "Lkotlin/Function0;",
         "onConfirm",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "PermissionDeniedDialogPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPermissionDeniedDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PermissionDeniedDialog.kt\nio/intercom/android/sdk/ui/component/PermissionDeniedDialogKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,88:1\n1047#2,6:89\n1047#2,6:95\n*S KotlinDebug\n*F\n+ 1 PermissionDeniedDialog.kt\nio/intercom/android/sdk/ui/component/PermissionDeniedDialogKt\n*L\n24#1:89,6\n25#1:95,6\n*E\n"
)
public final class PermissionDeniedDialogKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PermissionDeniedDialog(
      @Nullable Modifier var0,
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @Nullable Integer var4,
      @Nullable Function0 var5,
      @Nullable Function0 var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "confirmText");
      Intrinsics.checkNotNullParameter(var3, "dismissText");
      var7 = var7.startRestartGroup(-537034576);
      ComposerKt.sourceInformation(
         var7,
         "C(PermissionDeniedDialog)P(4,3!3,6)23@822L2,24@854L2,28@947L6,47@1571L321,58@1918L321,29@980L319,39@1316L191,26@866L1473:PermissionDeniedDialog.kt#yr602f"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var7.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var7.changed(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var7.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 64) != 0) {
         var10 |= 1572864;
      } else if ((var8 & 1572864) == 0) {
         var10 |= var7.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 599187) == 599186 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if ((var9 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var9 & 16) != 0) {
            var4 = null;
         }

         if ((var9 & 32) != 0) {
            var7.startReplaceGroup(-511130677);
            ComposerKt.sourceInformation(var7, "CC(remember):PermissionDeniedDialog.kt#9igjgp");
            Composer var12 = var7;
            boolean var13 = false;
            int var14 = 0;
            Object var15 = var12.rememberedValue();
            int var16 = 0;
            Object var10000;
            if (var15 == Composer.Companion.getEmpty()) {
               int var17 = 0;
               Object var27 = ((kotlin.jvm.functions.Function0) () -> PermissionDeniedDialogKt.PermissionDeniedDialog$lambda$1$lambda$0());
               var12.updateRememberedValue(var27);
               var10000 = var27;
            } else {
               var10000 = var15;
            }

            Function0 var11 = (Function0)var10000;
            var7.endReplaceGroup();
            var5 = var11;
         }

         if ((var9 & 64) != 0) {
            var7.startReplaceGroup(-511129653);
            ComposerKt.sourceInformation(var7, "CC(remember):PermissionDeniedDialog.kt#9igjgp");
            Composer var21 = var7;
            boolean var22 = false;
            int var24 = 0;
            Object var25 = var21.rememberedValue();
            int var26 = 0;
            Object var30;
            if (var25 == Composer.Companion.getEmpty()) {
               int var28 = 0;
               Object var29 = ((kotlin.jvm.functions.Function0) () -> PermissionDeniedDialogKt.PermissionDeniedDialog$lambda$3$lambda$2());
               var21.updateRememberedValue(var29);
               var30 = var29;
            } else {
               var30 = var25;
            }

            Function0 var19 = (Function0)var30;
            var7.endReplaceGroup();
            var6 = var19;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-537034576, var10, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialog (PermissionDeniedDialog.kt:25)");
         }

         long var20 = IntercomTheme.INSTANCE.getColors(var7, 6).getBase().recovered$00296b21a1edce05();
         DialogProperties var23 = new DialogProperties(false, false, false, 5, null);
         AndroidAlertDialog_androidKt.recovered$feba587216618f03(
            var5,
            ComposableLambdaKt.rememberComposableLambda(-1474258584, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$3(var6, var2), var7, 54),
            var0,
            ComposableLambdaKt.rememberComposableLambda(-868483802, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$4(var5, var3), var7, 54),
            ComposableLambdaKt.rememberComposableLambda(-565596411, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$5(var4), var7, 54),
            null,
            ComposableLambdaKt.rememberComposableLambda(40178371, true, new PermissionDeniedDialogKt$PermissionDeniedDialog$6(var1), var7, 54),
            null,
            var20,
            0L,
            0L,
            0L,
            0.0F,
            var23,
            var7,
            1600560 | 14 & var10 >> 15 | 896 & var10 << 6,
            3072,
            7840
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var31 = var7.endRestartGroup();
      if (var31 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_28_0 = var0;
         final java.lang.String recoveredCapture_28_1 = var1;
         final java.lang.String recoveredCapture_28_2 = var2;
         final java.lang.String recoveredCapture_28_3 = var3;
         final java.lang.Integer recoveredCapture_28_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_28_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_28_6 = var6;
         final int recoveredCapture_28_7 = var8;
         final int recoveredCapture_28_8 = var9;
         var31.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_28_0, recoveredArgument_28_1) -> PermissionDeniedDialogKt.PermissionDeniedDialog$lambda$4(recoveredCapture_28_0, recoveredCapture_28_1, recoveredCapture_28_2, recoveredCapture_28_3, recoveredCapture_28_4, recoveredCapture_28_5, recoveredCapture_28_6, recoveredCapture_28_7, recoveredCapture_28_8, (androidx.compose.runtime.Composer)recoveredArgument_28_0, ((java.lang.Integer)recoveredArgument_28_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PermissionDeniedDialogPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-501567607);
      ComposerKt.sourceInformation(var0, "C(PermissionDeniedDialogPreview)78@2414L306:PermissionDeniedDialog.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -501567607, var1, -1, "io.intercom.android.sdk.ui.component.PermissionDeniedDialogPreview (PermissionDeniedDialog.kt:77)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PermissionDeniedDialogKt.INSTANCE.recovered$a54a3cf5c9caf09f(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_29_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_29_0, recoveredArgument_29_1) -> PermissionDeniedDialogKt.PermissionDeniedDialogPreview$lambda$5(recoveredCapture_29_0, (androidx.compose.runtime.Composer)recoveredArgument_29_0, ((java.lang.Integer)recoveredArgument_29_1).intValue())));
      }
   }

   private static final Unit PermissionDeniedDialog$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit PermissionDeniedDialog$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit PermissionDeniedDialog$lambda$4(
      Modifier var0, String var1, String var2, String var3, Integer var4, Function0 var5, Function0 var6, int var7, int var8, Composer var9, int var10
   ) {
      PermissionDeniedDialog(var0, var1, var2, var3, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit PermissionDeniedDialogPreview$lambda$5(int var0, Composer var1, int var2) {
      PermissionDeniedDialogPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
