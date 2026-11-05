package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$SizeLimitExceeded;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
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
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0005\u001aE\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\b\u001a\u0015\u0010\t\u001a\u00020\n2\u0006\u0010\u0002\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r¨\u0006\u000f",
   d2 = {
         "UploadErrorDialog",
         "",
         "error",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;",
         "onRetry",
         "Lkotlin/Function0;",
         "onDelete",
         "onDismiss",
         "(Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "getErrorMessage",
         "",
         "(Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadError;Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "UploadErrorDialogSizeLimitPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UploadErrorDialogNetworkErrorPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nUploadErrorDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,107:1\n1047#2,6:108\n1047#2,6:114\n1047#2,6:120\n1047#2,6:126\n75#3:132\n*S KotlinDebug\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt\n*L\n18#1:108,6\n19#1:114,6\n20#1:120,6\n31#1:126,6\n71#1:132\n*E\n"
)
public final class UploadErrorDialogKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadErrorDialog(
      @NotNull MediaUploadRepository$UploadError var0,
      @Nullable Function0 var1,
      @Nullable Function0 var2,
      @Nullable Function0 var3,
      @Nullable Composer var4,
      int var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "error");
      var4 = var4.startRestartGroup(-444387334);
      ComposerKt.sourceInformation(
         var4,
         "C(UploadErrorDialog)P(!1,3)17@708L2,18@739L2,19@771L2,22@835L6,30@1103L35,33@1164L514,49@1704L416,23@868L206,21@783L1344:UploadErrorDialog.kt#o19g89"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 2) != 0) {
            var4.startReplaceGroup(980934010);
            ComposerKt.sourceInformation(var4, "CC(remember):UploadErrorDialog.kt#9igjgp");
            Composer var9 = var4;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var36 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt.UploadErrorDialog$lambda$1$lambda$0());
               var9.updateRememberedValue(var36);
               var10000 = var36;
            } else {
               var10000 = var12;
            }

            Function0 var8 = (Function0)var10000;
            var4.endReplaceGroup();
            var1 = var8;
         }

         if ((var6 & 4) != 0) {
            var4.startReplaceGroup(980935002);
            ComposerKt.sourceInformation(var4, "CC(remember):UploadErrorDialog.kt#9igjgp");
            Composer var22 = var4;
            boolean var24 = false;
            int var27 = 0;
            Object var30 = var22.rememberedValue();
            int var33 = 0;
            Object var42;
            if (var30 == Composer.Companion.getEmpty()) {
               int var37 = 0;
               Object var38 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt.UploadErrorDialog$lambda$3$lambda$2());
               var22.updateRememberedValue(var38);
               var42 = var38;
            } else {
               var42 = var30;
            }

            Function0 var19 = (Function0)var42;
            var4.endReplaceGroup();
            var2 = var19;
         }

         if ((var6 & 8) != 0) {
            var4.startReplaceGroup(980936026);
            ComposerKt.sourceInformation(var4, "CC(remember):UploadErrorDialog.kt#9igjgp");
            Composer var23 = var4;
            boolean var25 = false;
            int var28 = 0;
            Object var31 = var23.rememberedValue();
            int var34 = 0;
            Object var43;
            if (var31 == Composer.Companion.getEmpty()) {
               int var39 = 0;
               Object var40 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt.UploadErrorDialog$lambda$5$lambda$4());
               var23.updateRememberedValue(var40);
               var43 = var40;
            } else {
               var43 = var31;
            }

            Function0 var20 = (Function0)var43;
            var4.endReplaceGroup();
            var3 = var20;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -444387334, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialog (UploadErrorDialog.kt:20)"
            );
         }

         long var21 = IntercomTheme.INSTANCE.getColors(var4, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         var4.startReplaceGroup(980946683);
         ComposerKt.sourceInformation(var4, "CC(remember):UploadErrorDialog.kt#9igjgp");
         Composer var29 = var4;
         boolean var32 = (var7 & 7168) == 2048;
         int var35 = 0;
         Object var41 = var29.rememberedValue();
         int var15 = 0;
         Object var44;
         if (!var32 && var41 != Composer.Companion.getEmpty()) {
            var44 = var41;
         } else {
            int var16 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_491_0 = var3;
            Object var17 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt.UploadErrorDialog$lambda$7$lambda$6(recoveredCapture_491_0));
            var29.updateRememberedValue(var17);
            var44 = var17;
         }

         Function0 var26 = (Function0)var44;
         var4.endReplaceGroup();
         AndroidAlertDialog_androidKt.recovered$feba587216618f03(
            var26,
            ComposableLambdaKt.rememberComposableLambda(1477542978, true, new UploadErrorDialogKt$UploadErrorDialog$5(var0, var1, var3), var4, 54),
            null,
            ComposableLambdaKt.rememberComposableLambda(2126135748, true, new UploadErrorDialogKt$UploadErrorDialog$6(var2, var3), var4, 54),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(951541255, true, new UploadErrorDialogKt$UploadErrorDialog$7(var0), var4, 54),
            null,
            var21,
            0L,
            0L,
            0L,
            0.0F,
            null,
            var4,
            1575984,
            0,
            16052
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var45 = var4.endRestartGroup();
      if (var45 != null) {
         final io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError recoveredCapture_492_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_492_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_492_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_492_3 = var3;
         final int recoveredCapture_492_4 = var5;
         final int recoveredCapture_492_5 = var6;
         var45.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_492_0, recoveredArgument_492_1) -> UploadErrorDialogKt.UploadErrorDialog$lambda$8(recoveredCapture_492_0, recoveredCapture_492_1, recoveredCapture_492_2, recoveredCapture_492_3, recoveredCapture_492_4, recoveredCapture_492_5, (androidx.compose.runtime.Composer)recoveredArgument_492_0, ((java.lang.Integer)recoveredArgument_492_1).intValue())));
      }
   }

   @Composable
   private static final String getErrorMessage(MediaUploadRepository$UploadError var0, Composer var1, int var2) {
      var1.startReplaceGroup(444064229);
      ComposerKt.sourceInformation(var1, "C(getErrorMessage):UploadErrorDialog.kt#o19g89");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(444064229, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.getErrorMessage (UploadErrorDialog.kt:67)");
      }

      String var10000;
      if (var0 instanceof MediaUploadRepository$UploadError$SizeLimitExceeded) {
         var1.startReplaceGroup(-2051085905);
         ComposerKt.sourceInformation(var1, "70@2348L7");
         CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var8 = var1.consume(var5);
         ComposerKt.sourceInformationMarkerEnd(var1);
         String var4 = Phrase.from((Context)var8, string.intercom_file_too_big)
            .put("limit", ((MediaUploadRepository$UploadError$SizeLimitExceeded)var0).getLimitMB())
            .format()
            .toString();
         var1.endReplaceGroup();
         var10000 = var4;
      } else {
         var1.startReplaceGroup(-2050895844);
         ComposerKt.sourceInformation(var1, "77@2505L56");
         String var9 = StringResources_androidKt.stringResource(string.intercom_upload_file_load_error, var1, 0);
         var1.endReplaceGroup();
         var10000 = var9;
      }

      String var3 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadErrorDialogSizeLimitPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1575850437);
      ComposerKt.sourceInformation(var0, "C(UploadErrorDialogSizeLimitPreview)84@2656L251:UploadErrorDialog.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1575850437, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialogSizeLimitPreview (UploadErrorDialog.kt:83)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$UploadErrorDialogKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_494_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_494_0, recoveredArgument_494_1) -> UploadErrorDialogKt.UploadErrorDialogSizeLimitPreview$lambda$9(recoveredCapture_494_0, (androidx.compose.runtime.Composer)recoveredArgument_494_0, ((java.lang.Integer)recoveredArgument_494_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadErrorDialogNetworkErrorPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1574681437);
      ComposerKt.sourceInformation(var0, "C(UploadErrorDialogNetworkErrorPreview)98@2999L207:UploadErrorDialog.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1574681437, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialogNetworkErrorPreview (UploadErrorDialog.kt:97)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$UploadErrorDialogKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_493_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_493_0, recoveredArgument_493_1) -> UploadErrorDialogKt.UploadErrorDialogNetworkErrorPreview$lambda$10(recoveredCapture_493_0, (androidx.compose.runtime.Composer)recoveredArgument_493_0, ((java.lang.Integer)recoveredArgument_493_1).intValue())));
      }
   }

   private static final Unit UploadErrorDialog$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialog$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialog$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialog$lambda$7$lambda$6(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialog$lambda$8(
      MediaUploadRepository$UploadError var0, Function0 var1, Function0 var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      UploadErrorDialog(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialogSizeLimitPreview$lambda$9(int var0, Composer var1, int var2) {
      UploadErrorDialogSizeLimitPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UploadErrorDialogNetworkErrorPreview$lambda$10(int var0, Composer var1, int var2) {
      UploadErrorDialogNetworkErrorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final String access$getErrorMessage(MediaUploadRepository$UploadError var0, Composer var1, int var2) {
      return getErrorMessage(var0, var1, var2);
   }
}
