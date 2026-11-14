package io.intercom.android.sdk.survey.ui.questiontype.files;

import android.view.View;
import android.view.Window;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.window.DialogWindowProvider;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.preview.data.DeleteType$Delete;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt;
import io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$None;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Queued;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Uploading;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aM\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b\u001a\u0015\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000fH\u0003¢\u0006\u0002\u0010\u0010¨\u0006\u0011",
   d2 = {
         "FileActionSheet",
         "",
         "item",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "onRetryClick",
         "Lkotlin/Function0;",
         "onDeleteClick",
         "onStopUploading",
         "dismiss",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "FileActionSheetUploadingPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "FileActionSheetQueuedPreview",
         "FileActionSheetPreviewForStatus",
         "uploadStatus",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;",
         "(Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadStatus;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFileActionSheet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileActionSheet.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileActionSheetKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,133:1\n1047#2,6:134\n1047#2,6:141\n1047#2,6:147\n1047#2,6:153\n1047#2,6:159\n75#3:140\n*S KotlinDebug\n*F\n+ 1 FileActionSheet.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileActionSheetKt\n*L\n39#1:134,6\n61#1:141,6\n73#1:147,6\n74#1:153,6\n75#1:159,6\n59#1:140\n*E\n"
)
public final class FileActionSheetKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileActionSheet(
      @NotNull Answer$MediaAnswer$MediaItem var0,
      @NotNull Function0 var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function0 var4,
      @Nullable Composer var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "item");
      Intrinsics.checkNotNullParameter(var1, "onRetryClick");
      Intrinsics.checkNotNullParameter(var2, "onDeleteClick");
      Intrinsics.checkNotNullParameter(var3, "onStopUploading");
      Intrinsics.checkNotNullParameter(var4, "dismiss");
      var5 = var5.startRestartGroup(592767504);
      ComposerKt.sourceInformation(var5, "C(FileActionSheet)P(1,3,2,4)38@1820L46:FileActionSheet.kt#lphomb");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= ((var6 & 8) == 0 ? var5.changed(var0) : var5.changedInstance(var0)) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 24576) == 0) {
         var7 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(592767504, var7, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheet (FileActionSheet.kt:37)");
         }

         var5.startReplaceGroup(-984101712);
         ComposerKt.sourceInformation(var5, "CC(remember):FileActionSheet.kt#9igjgp");
         Composer var10 = var5;
         boolean var11 = false;
         int var12 = 0;
         Object var13 = var10.rememberedValue();
         int var14 = 0;
         Object var10000;
         if (var13 == Composer.Companion.getEmpty()) {
            int var15 = 0;
            Object var16 = SnapshotStateKt.mutableStateOf$default(var0.getUploadStatus(), null, 2, null);
            var10.updateRememberedValue(var16);
            var10000 = var16;
         } else {
            var10000 = var13;
         }

         MutableState var9 = (MutableState)var10000;
         var5.endReplaceGroup();
         MutableState var8 = var9;
         if (!Intrinsics.areEqual(var8.getValue(), var0.getUploadStatus())) {
            var4.invoke();
         }

         var8.setValue(var0.getUploadStatus());
         Answer$MediaAnswer$FileUploadStatus var31 = var0.getUploadStatus();
         if (var31 instanceof Answer$MediaAnswer$FileUploadStatus$Error) {
            var5.startReplaceGroup(-442090195);
            ComposerKt.sourceInformation(var5, "47@2122L219");
            FileUploadErrorComponentKt.FileUploadErrorComponent(
               var0.getData().getFileName(),
               ((Answer$MediaAnswer$FileUploadStatus$Error)var31).getError(),
               var1,
               var2,
               var5,
               896 & var7 << 3 | 7168 & var7 << 3
            );
            var5.endReplaceGroup();
         } else if (var31 instanceof Answer$MediaAnswer$FileUploadStatus$Success) {
            var5.startReplaceGroup(-441803011);
            ComposerKt.sourceInformation(var5, "58@2580L7,72@3212L13,73@3259L19,74@3310L3,64@2840L488");
            CompositionLocal var33 = AndroidCompositionLocals_androidKt.getLocalView();
            var12 = 0;
            var13 = 0;
            ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var47 = var5.consume(var33);
            ComposerKt.sourceInformationMarkerEnd(var5);
            View var32 = (View)var47;
            var13 = var32.getParent();
            Window var34 = (var13 instanceof DialogWindowProvider ? (DialogWindowProvider)var13 : null) != null
               ? (var13 instanceof DialogWindowProvider ? (DialogWindowProvider)var13 : null).getWindow()
               : null;
            var5.startReplaceGroup(-984075083);
            ComposerKt.sourceInformation(var5, "*60@2691L122,60@2680L133");
            if (var34 == null) {
               var10000 = null;
            } else {
               Window var48 = var34;
               int var52 = 0;
               var5.startReplaceGroup(-130938661);
               ComposerKt.sourceInformation(var5, "CC(remember):FileActionSheet.kt#9igjgp");
               Composer var56 = var5;
               boolean var17 = var5.changedInstance(var48) | var5.changedInstance(var32);
               int $i$f$cachex = 0;
               Object it$ivx = var56.rememberedValue();
               int var20/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
               if (!var17 && it$ivx != Composer.Companion.getEmpty()) {
                  var10000 = it$ivx;
               } else {
                  int var21 = 0;
                  final android.view.Window recoveredCapture_1436_0 = var48;
                  final android.view.View recoveredCapture_1436_1 = var32;
                  Object var22 = ((kotlin.jvm.functions.Function0) () -> FileActionSheetKt.FileActionSheet$lambda$3$lambda$2$lambda$1(recoveredCapture_1436_0, recoveredCapture_1436_1));
                  var56.updateRememberedValue(var22);
                  var10000 = var22;
               }

               Function0 var23 = (Function0)var10000;
               var5.endReplaceGroup();
               EffectsKt.SideEffect(var23, var5, 0);
            }

            var5.endReplaceGroup();
            Modifier var77 = BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), Color.Companion.recovered$235b30c090a399c5(), null, 2, null
            );
            IntercomPreviewArgs var10001 = new IntercomPreviewArgs(
               CollectionsKt.listOfNotNull(IntercomPreviewFile.Companion.local(var0.getData().getUri())),
               DeleteType$Delete.INSTANCE,
               null,
               false,
               0,
               null,
               60,
               null
            );
            Object var10002 = null;
            var5.startReplaceGroup(-984057201);
            ComposerKt.sourceInformation(var5, "CC(remember):FileActionSheet.kt#9igjgp");
            Composer var39 = var5;
            boolean var44 = (var7 & 57344) == 16384;
            int $i$f$cachex = 0;
            Object it$ivx = var39.rememberedValue();
            int var57/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10003;
            if (!var44 && it$ivx != Composer.Companion.getEmpty()) {
               var10003 = (Function1)it$ivx;
            } else {
               Object var26 = null;
               IntercomPreviewArgs var25 = var10001;
               Modifier var24 = var77;
               int var60 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_1437_0 = var4;
               Function1 var27 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1437_0) -> FileActionSheetKt.FileActionSheet$lambda$5$lambda$4(recoveredCapture_1437_0, (java.util.List)recoveredArgument_1437_0));
               var77 = var24;
               var10001 = var25;
               var10002 = var26;
               Object var64 = var27;
               var39.updateRememberedValue(var64);
               var10003 = (Function1)var64;
            }

            Function1 var35 = (kotlin.jvm.functions.Function1) var10003;
            var5.endReplaceGroup();
            var10003 = var35;
            var5.startReplaceGroup(-984055691);
            ComposerKt.sourceInformation(var5, "CC(remember):FileActionSheet.kt#9igjgp");
            Composer var40 = var5;
            boolean var45 = (var7 & 896) == 256;
            $i$f$cachex = 0;
            it$ivx = var40.rememberedValue();
            var57/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10004;
            if (!var45 && it$ivx != Composer.Companion.getEmpty()) {
               var10004 = (Function1)it$ivx;
            } else {
               Function1 var72 = var35;
               Object var70 = var10002;
               IntercomPreviewArgs var68 = var10001;
               Modifier var66 = var77;
               int var61 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_1438_0 = var2;
               Function1 var28 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1438_0) -> FileActionSheetKt.FileActionSheet$lambda$7$lambda$6(recoveredCapture_1438_0, (io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile)recoveredArgument_1438_0));
               var77 = var66;
               var10001 = var68;
               var10002 = var70;
               var10003 = var72;
               Object var65 = var28;
               var40.updateRememberedValue(var65);
               var10004 = (Function1)var65;
            }

            Function1 var36 = (kotlin.jvm.functions.Function1) var10004;
            var5.endReplaceGroup();
            var10004 = var36;
            var5.startReplaceGroup(-984054075);
            ComposerKt.sourceInformation(var5, "CC(remember):FileActionSheet.kt#9igjgp");
            Composer var41 = var5;
            boolean var46 = false;
            $i$f$cachex = 0;
            it$ivx = var41.rememberedValue();
            var57/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10005;
            if (it$ivx == Composer.Companion.getEmpty()) {
               Function1 var74 = var36;
               Function1 var73 = (kotlin.jvm.functions.Function1) var10003;
               Object var71 = var10002;
               IntercomPreviewArgs var69 = var10001;
               Modifier var67 = var77;
               int var62 = 0;
               Function1 var29 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1439_0) -> FileActionSheetKt.FileActionSheet$lambda$9$lambda$8((java.util.List)recoveredArgument_1439_0));
               var77 = var67;
               var10001 = var69;
               var10002 = var71;
               var10003 = var73;
               var10004 = var74;
               Object var63 = var29;
               var41.updateRememberedValue(var63);
               var10005 = var63;
            } else {
               var10005 = it$ivx;
            }

            Function1 var37 = (Function1)var10005;
            var5.endReplaceGroup();
            PreviewRootScreenKt.PreviewRootScreen(var77, var10001, (PreviewViewModel)var10002, (kotlin.jvm.functions.Function1) var10003, (kotlin.jvm.functions.Function1) var10004, var37, var5, 196614, 4);
            var5.endReplaceGroup();
         } else if (Intrinsics.areEqual(var31, Answer$MediaAnswer$FileUploadStatus$Uploading.INSTANCE)) {
            var5.startReplaceGroup(-440849761);
            ComposerKt.sourceInformation(var5, "79@3375L137");
            FileUploadProgressComponentKt.FileUploadProgressComponent(var0.getData().getFileName(), var3, var5, 112 & var7 >> 6);
            var5.endReplaceGroup();
         } else {
            if (!Intrinsics.areEqual(var31, Answer$MediaAnswer$FileUploadStatus$None.INSTANCE)
               && !Intrinsics.areEqual(var31, Answer$MediaAnswer$FileUploadStatus$Queued.INSTANCE)) {
               var5.startReplaceGroup(-984093140);
               var5.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var5.startReplaceGroup(-440669186);
            var5.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var78 = var5.endRestartGroup();
      if (var78 != null) {
         final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem recoveredCapture_1440_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1440_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_1440_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_1440_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_1440_4 = var4;
         final int recoveredCapture_1440_5 = var6;
         var78.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1440_0, recoveredArgument_1440_1) -> FileActionSheetKt.FileActionSheet$lambda$10(recoveredCapture_1440_0, recoveredCapture_1440_1, recoveredCapture_1440_2, recoveredCapture_1440_3, recoveredCapture_1440_4, recoveredCapture_1440_5, (androidx.compose.runtime.Composer)recoveredArgument_1440_0, ((java.lang.Integer)recoveredArgument_1440_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileActionSheetUploadingPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(31049684);
      ComposerKt.sourceInformation(var0, "C(FileActionSheetUploadingPreview)92@3642L42:FileActionSheet.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               31049684, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheetUploadingPreview (FileActionSheet.kt:91)"
            );
         }

         FileActionSheetPreviewForStatus(Answer$MediaAnswer$FileUploadStatus$Uploading.INSTANCE, var0, 6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1443_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1443_0, recoveredArgument_1443_1) -> FileActionSheetKt.FileActionSheetUploadingPreview$lambda$11(recoveredCapture_1443_0, (androidx.compose.runtime.Composer)recoveredArgument_1443_0, ((java.lang.Integer)recoveredArgument_1443_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileActionSheetQueuedPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-61695068);
      ComposerKt.sourceInformation(var0, "C(FileActionSheetQueuedPreview)98@3767L262:FileActionSheet.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -61695068, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheetQueuedPreview (FileActionSheet.kt:97)"
            );
         }

         FileActionSheetPreviewForStatus(
            new Answer$MediaAnswer$FileUploadStatus$Error(
               new Answer$MediaAnswer$FileUploadError$FileLimitExceeded(
                  CollectionsKt.listOf(new StringRes(string.intercom_inbox_error_state_title, null, 2, null))
               )
            ),
            var0,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1442_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1442_0, recoveredArgument_1442_1) -> FileActionSheetKt.FileActionSheetQueuedPreview$lambda$12(recoveredCapture_1442_0, (androidx.compose.runtime.Composer)recoveredArgument_1442_0, ((java.lang.Integer)recoveredArgument_1442_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileActionSheetPreviewForStatus(Answer$MediaAnswer$FileUploadStatus var0, Composer var1, int var2) {
      var1 = var1.startRestartGroup(-915176137);
      ComposerKt.sourceInformation(var1, "C(FileActionSheetPreviewForStatus)111@4154L581,111@4146L589:FileActionSheet.kt#lphomb");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -915176137, var3, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileActionSheetPreviewForStatus (FileActionSheet.kt:110)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(193945980, true, new FileActionSheetKt$FileActionSheetPreviewForStatus$1(var0), var1, 54),
            var1,
            12582912,
            127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus recoveredCapture_1441_0 = var0;
         final int recoveredCapture_1441_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1441_0, recoveredArgument_1441_1) -> FileActionSheetKt.FileActionSheetPreviewForStatus$lambda$13(recoveredCapture_1441_0, recoveredCapture_1441_1, (androidx.compose.runtime.Composer)recoveredArgument_1441_0, ((java.lang.Integer)recoveredArgument_1441_1).intValue())));
      }
   }

   private static final Unit FileActionSheet$lambda$3$lambda$2$lambda$1(Window var0, View var1) {
      WindowCompat.getInsetsController(var0, var1).setAppearanceLightStatusBars(false);
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheet$lambda$5$lambda$4(Function0 var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheet$lambda$7$lambda$6(Function0 var0, IntercomPreviewFile var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheet$lambda$9$lambda$8(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheet$lambda$10(
      Answer$MediaAnswer$MediaItem var0, Function0 var1, Function0 var2, Function0 var3, Function0 var4, int var5, Composer var6, int var7
   ) {
      FileActionSheet(var0, var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheetUploadingPreview$lambda$11(int var0, Composer var1, int var2) {
      FileActionSheetUploadingPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheetQueuedPreview$lambda$12(int var0, Composer var1, int var2) {
      FileActionSheetQueuedPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FileActionSheetPreviewForStatus$lambda$13(Answer$MediaAnswer$FileUploadStatus var0, int var1, Composer var2, int var3) {
      FileActionSheetPreviewForStatus(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
