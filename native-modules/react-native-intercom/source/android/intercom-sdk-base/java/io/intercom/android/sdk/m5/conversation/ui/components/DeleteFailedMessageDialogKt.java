package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a-\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0001¢\u0006\u0002\u0010\u0005\u001a\r\u0010\u0006\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "DeleteFailedMessageDialog",
         "",
         "onDelete",
         "Lkotlin/Function0;",
         "onDismiss",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "DeleteFailedMessageDialogPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nDeleteFailedMessageDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeleteFailedMessageDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/DeleteFailedMessageDialogKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,66:1\n1047#2,6:67\n1047#2,6:73\n1047#2,6:79\n*S KotlinDebug\n*F\n+ 1 DeleteFailedMessageDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/DeleteFailedMessageDialogKt\n*L\n14#1:67,6\n15#1:73,6\n26#1:79,6\n*E\n"
)
public final class DeleteFailedMessageDialogKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DeleteFailedMessageDialog(@Nullable Function0 var0, @Nullable Function0 var1, @Nullable Composer var2, int var3, int var4) {
      var2 = var2.startRestartGroup(-1904644559);
      ComposerKt.sourceInformation(
         var2, "C(DeleteFailedMessageDialog)13@500L2,14@532L2,17@596L6,25@900L35,28@961L416,42@1403L385,16@544L1251:DeleteFailedMessageDialog.kt#o19g89"
      );
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var2.startReplaceGroup(-518335726);
            ComposerKt.sourceInformation(var2, "CC(remember):DeleteFailedMessageDialog.kt#9igjgp");
            Composer var7 = var2;
            boolean var8 = false;
            int var9 = 0;
            Object var10 = var7.rememberedValue();
            int var11 = 0;
            Object var10000;
            if (var10 == Composer.Companion.getEmpty()) {
               int var12 = 0;
               Object var28 = ((kotlin.jvm.functions.Function0) () -> DeleteFailedMessageDialogKt.DeleteFailedMessageDialog$lambda$1$lambda$0());
               var7.updateRememberedValue(var28);
               var10000 = var28;
            } else {
               var10000 = var10;
            }

            Function0 var6 = (Function0)var10000;
            var2.endReplaceGroup();
            var0 = var6;
         }

         if ((var4 & 2) != 0) {
            var2.startReplaceGroup(-518334702);
            ComposerKt.sourceInformation(var2, "CC(remember):DeleteFailedMessageDialog.kt#9igjgp");
            Composer var19 = var2;
            boolean var20 = false;
            int var22 = 0;
            Object var24 = var19.rememberedValue();
            int var26 = 0;
            Object var32;
            if (var24 == Composer.Companion.getEmpty()) {
               int var29 = 0;
               Object var30 = ((kotlin.jvm.functions.Function0) () -> DeleteFailedMessageDialogKt.DeleteFailedMessageDialog$lambda$3$lambda$2());
               var19.updateRememberedValue(var30);
               var32 = var30;
            } else {
               var32 = var24;
            }

            Function0 var17 = (Function0)var32;
            var2.endReplaceGroup();
            var1 = var17;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1904644559, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.DeleteFailedMessageDialog (DeleteFailedMessageDialog.kt:15)"
            );
         }

         long var18 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
         var2.startReplaceGroup(-518322893);
         ComposerKt.sourceInformation(var2, "CC(remember):DeleteFailedMessageDialog.kt#9igjgp");
         Composer var23 = var2;
         boolean var25 = (var5 & 112) == 32;
         int var27 = 0;
         Object var31 = var23.rememberedValue();
         int var13 = 0;
         Object var33;
         if (!var25 && var31 != Composer.Companion.getEmpty()) {
            var33 = var31;
         } else {
            int var14 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_427_0 = var1;
            Object var15 = ((kotlin.jvm.functions.Function0) () -> DeleteFailedMessageDialogKt.DeleteFailedMessageDialog$lambda$5$lambda$4(recoveredCapture_427_0));
            var23.updateRememberedValue(var15);
            var33 = var15;
         }

         Function0 var21 = (Function0)var33;
         var2.endReplaceGroup();
         AndroidAlertDialog_androidKt.recovered$feba587216618f03(
            var21,
            ComposableLambdaKt.rememberComposableLambda(1313773689, true, new DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4(var0, var1), var2, 54),
            null,
            ComposableLambdaKt.rememberComposableLambda(-609476741, true, new DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$5(var1), var2, 54),
            null,
            null,
            ComposableSingletons$DeleteFailedMessageDialogKt.INSTANCE.recovered$fd838d44303763da(),
            null,
            var18,
            0L,
            0L,
            0L,
            0.0F,
            null,
            var2,
            1575984,
            0,
            16052
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var34 = var2.endRestartGroup();
      if (var34 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_428_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_428_1 = var1;
         final int recoveredCapture_428_2 = var3;
         final int recoveredCapture_428_3 = var4;
         var34.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_428_0, recoveredArgument_428_1) -> DeleteFailedMessageDialogKt.DeleteFailedMessageDialog$lambda$6(recoveredCapture_428_0, recoveredCapture_428_1, recoveredCapture_428_2, recoveredCapture_428_3, (androidx.compose.runtime.Composer)recoveredArgument_428_0, ((java.lang.Integer)recoveredArgument_428_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DeleteFailedMessageDialogPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2110213197);
      ComposerKt.sourceInformation(var0, "C(DeleteFailedMessageDialogPreview)61@1883L57:DeleteFailedMessageDialog.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2110213197, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.DeleteFailedMessageDialogPreview (DeleteFailedMessageDialog.kt:60)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$DeleteFailedMessageDialogKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_429_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_429_0, recoveredArgument_429_1) -> DeleteFailedMessageDialogKt.DeleteFailedMessageDialogPreview$lambda$7(recoveredCapture_429_0, (androidx.compose.runtime.Composer)recoveredArgument_429_0, ((java.lang.Integer)recoveredArgument_429_1).intValue())));
      }
   }

   private static final Unit DeleteFailedMessageDialog$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit DeleteFailedMessageDialog$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit DeleteFailedMessageDialog$lambda$5$lambda$4(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit DeleteFailedMessageDialog$lambda$6(Function0 var0, Function0 var1, int var2, int var3, Composer var4, int var5) {
      DeleteFailedMessageDialog(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit DeleteFailedMessageDialogPreview$lambda$7(int var0, Composer var1, int var2) {
      DeleteFailedMessageDialogPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
