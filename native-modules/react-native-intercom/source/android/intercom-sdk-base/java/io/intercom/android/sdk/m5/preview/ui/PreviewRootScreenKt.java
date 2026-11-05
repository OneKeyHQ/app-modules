package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.widget.Toast;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.compose.foundation.pager.PagerState;
import androidx.compose.foundation.pager.PagerStateKt;
import androidx.compose.material3.ScaffoldKt;
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
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.lifecycle.viewmodel.CreationExtras.Empty;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.ViewModelKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel;
import io.intercom.android.sdk.ui.IntercomPreviews;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000T\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\u001a\u009e\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072'\u0010\b\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00010\t2!\u0010\u000f\u001a\u001d\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\u00010\t2'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00010\tH\u0007¢\u0006\u0002\u0010\u0013\u001aÊ\u0001\u0010\u0014\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u00182)\b\u0002\u0010\b\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00010\t2#\b\u0002\u0010\u000f\u001a\u001d\u0012\u0013\u0012\u00110\u0010¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u0011\u0012\u0004\u0012\u00020\u00010\t2)\b\u0002\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00010\t2\u000e\b\u0002\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u001a2\u0014\b\u0002\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\u001d\u001a\r\u0010\u001e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u001f¨\u0006 ²\u0006\n\u0010!\u001a\u00020\"X\u008a\u008e\u0002",
   d2 = {
         "PreviewRootScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "previewArgs",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "viewModel",
         "Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;",
         "onBackCLick",
         "Lkotlin/Function1;",
         "",
         "Landroid/net/Uri;",
         "Lkotlin/ParameterName;",
         "name",
         "uris",
         "onDeleteClick",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "file",
         "onSendClick",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "PreviewRootScreenContent",
         "state",
         "Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;",
         "permissionDeniedText",
         "",
         "onDownloadClick",
         "Lkotlin/Function0;",
         "onPageNavigated",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "PreviewRootScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "isCurrentPageZoomed",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nPreviewRootScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt\n+ 2 ViewModel.kt\nandroidx/lifecycle/viewmodel/compose/ViewModelKt__ViewModelKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 6 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,189:1\n68#2:190\n57#2,10:191\n75#3:201\n75#3:244\n1047#4,6:202\n1047#4,6:208\n1047#4,6:214\n1047#4,6:220\n1047#4,6:226\n1047#4,6:232\n1047#4,6:238\n1047#4,3:252\n1050#4,3:256\n1047#4,6:259\n1047#4,6:265\n1047#4,6:271\n1047#4,6:277\n1047#4,6:283\n615#5:245\n612#5,6:246\n613#6:255\n85#7:289\n117#7,2:290\n*S KotlinDebug\n*F\n+ 1 PreviewRootScreen.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewRootScreenKt\n*L\n45#1:190\n45#1:191,10\n53#1:201\n79#1:244\n63#1:202,6\n64#1:208,6\n73#1:214,6\n74#1:220,6\n75#1:226,6\n76#1:232,6\n77#1:238,6\n80#1:252,3\n80#1:256,3\n84#1:259,6\n88#1:265,6\n92#1:271,6\n102#1:277,6\n109#1:283,6\n80#1:245\n80#1:246,6\n80#1:255\n88#1:289\n88#1:290,2\n*E\n"
)
public final class PreviewRootScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewRootScreen(
      @Nullable Modifier var0,
      @NotNull IntercomPreviewArgs var1,
      @Nullable PreviewViewModel var2,
      @NotNull Function1 var3,
      @NotNull Function1 var4,
      @NotNull Function1 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "previewArgs");
      Intrinsics.checkNotNullParameter(var3, "onBackCLick");
      Intrinsics.checkNotNullParameter(var4, "onDeleteClick");
      Intrinsics.checkNotNullParameter(var5, "onSendClick");
      Composer var29 = var6.startRestartGroup(919809320);
      ComposerKt.sourceInformation(
         var29, "C(PreviewRootScreen)P(!1,4,5)44@2002L117,52@2300L7,53@2340L16,62@2660L63,63@2751L26,55@2368L416:PreviewRootScreen.kt#hhbc0c"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var29.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var29.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var9 |= (var8 & 4) == 0 && var29.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var29.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var29.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var29.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var29.getSkipping()) {
         var29.skipToGroupEnd();
      } else {
         var29.startDefaults();
         if ((var7 & 1) != 0 && !var29.getDefaultsInvalid()) {
            var29.skipToGroupEnd();
            if ((var8 & 4) != 0) {
               var9 &= -897;
            }
         } else {
            if ((var8 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var8 & 4) != 0) {
               String var11 = UUID.randomUUID().toString();
               Factory var12 = PreviewViewModel.Companion.factory$intercom_sdk_base_release(var1);
               byte var14 = 0;
               int var15 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var29, 1729797275, "CC(viewModel)N(viewModelStoreOwner,key,factory,extras)56@2573L7,67@2981L63:ViewModel.kt#3tja67"
               );
               ViewModelStoreOwner var10000 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var29, 6);
               if (var10000 == null) {
                  int var16 = 0;
                  String var44 = "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner";
                  throw new IllegalStateException(var44.toString());
               }

               ViewModelStoreOwner var10 = var10000;
               CreationExtras var13 = var10 instanceof HasDefaultViewModelProviderFactory
                  ? ((HasDefaultViewModelProviderFactory)var10).getDefaultViewModelCreationExtras()
                  : Empty.INSTANCE;
               ViewModel var17 = ViewModelKt.viewModel(
                  Reflection.getOrCreateKotlinClass(PreviewViewModel.class),
                  var10,
                  var11,
                  var12,
                  var13,
                  var29,
                  112 & var14 << 3 | 896 & var14 << 3 | 7168 & var14 << 3 | 57344 & var14 << 3,
                  0
               );
               ComposerKt.sourceInformationMarkerEnd(var29);
               var2 = (PreviewViewModel)var17;
               var9 &= -897;
            }
         }

         var29.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(919809320, var9, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreen (PreviewRootScreen.kt:51)");
         }

         CompositionLocal var31 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var33 = 0;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var39 = var29.consume(var31);
         ComposerKt.sourceInformationMarkerEnd(var29);
         Context var30 = (Context)var39;
         PreviewUiState var32 = (PreviewUiState)SnapshotStateKt.collectAsState(var2.getState$intercom_sdk_base_release(), null, var29, 0, 1).getValue();
         Modifier var58 = var0;
         PreviewUiState var10001 = var32;
         String var10002 = var1.getDownloadState().getPermissionDeniedText();
         Function1 var10003 = var3;
         Function1 var10004 = var4;
         Function1 var10005 = var5;
         var29.startReplaceGroup(-301496685);
         ComposerKt.sourceInformation(var29, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var37 = var29;
         boolean var40 = var29.changedInstance(var2) | var29.changedInstance(var32) | var29.changedInstance(var30);
         int var42 = 0;
         Object var45 = var37.rememberedValue();
         int var47 = 0;
         Object var10006;
         if (!var40 && var45 != Composer.Companion.getEmpty()) {
            var10006 = var45;
         } else {
            Function1 var26 = var5;
            Function1 var25 = var4;
            Function1 var24 = var3;
            String var23 = var10002;
            PreviewUiState var22 = var32;
            Modifier var21 = var0;
            int var18 = 0;
            final io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel recoveredExpressionCapture_1141_0 = var2;
            final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1141_1 = var32;
            final android.content.Context recoveredExpressionCapture_1141_2 = var30;
            Function0 var27 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreen$lambda$1$lambda$0(recoveredExpressionCapture_1141_0, recoveredExpressionCapture_1141_1, recoveredExpressionCapture_1141_2));
            var58 = var21;
            var10001 = var22;
            var10002 = var23;
            var10003 = var24;
            var10004 = var25;
            var10005 = var26;
            Object var19 = var27;
            var37.updateRememberedValue(var19);
            var10006 = var19;
         }

         Function0 var34 = (Function0)var10006;
         var29.endReplaceGroup();
         Function0 var60 = var34;
         PreviewViewModel var35 = var2;
         var29.startReplaceGroup(-301493810);
         ComposerKt.sourceInformation(var29, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var41 = var29;
         boolean var43 = var29.changedInstance(var35);
         int $i$f$cachex = 0;
         Object it$ivx = var41.rememberedValue();
         int var49/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10007;
         if (!var43 && it$ivx != Composer.Companion.getEmpty()) {
            var10007 = it$ivx;
         } else {
            Function0 var57 = var34;
            Function1 var56 = var10005;
            Function1 var55 = var10004;
            Function1 var54 = var10003;
            String var53 = var10002;
            PreviewUiState var52 = var10001;
            Modifier var51 = var58;
            int var50 = 0;
            KFunction var28 = new PreviewRootScreenKt$PreviewRootScreen$2$1(var35);
            var58 = var51;
            var10001 = var52;
            var10002 = var53;
            var10003 = var54;
            var10004 = var55;
            var10005 = var56;
            var60 = var57;
            Object var20 = var28;
            var41.updateRememberedValue(var20);
            var10007 = var20;
         }

         KFunction var38 = (KFunction)var10007;
         var29.endReplaceGroup();
         PreviewRootScreenContent(
            var58, var10001, var10002, var10003, var10004, var10005, var60, (Function1)var38, var29, 14 & var9 | 7168 & var9 | 57344 & var9 | 458752 & var9, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var59 = var29.endRestartGroup();
      if (var59 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1142_0 = var0;
         final io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs recoveredExpressionCapture_1142_1 = var1;
         final io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel recoveredExpressionCapture_1142_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1142_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1142_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1142_5 = var5;
         final int recoveredExpressionCapture_1142_6 = var7;
         final int recoveredExpressionCapture_1142_7 = var8;
         var59.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1142_0, recoveredExpressionArgument_1142_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreen$lambda$3(recoveredExpressionCapture_1142_0, recoveredExpressionCapture_1142_1, recoveredExpressionCapture_1142_2, recoveredExpressionCapture_1142_3, recoveredExpressionCapture_1142_4, recoveredExpressionCapture_1142_5, recoveredExpressionCapture_1142_6, recoveredExpressionCapture_1142_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1142_0), ((java.lang.Integer)recoveredExpressionArgument_1142_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewRootScreenContent(
      Modifier var0,
      PreviewUiState var1,
      String var2,
      Function1 var3,
      Function1 var4,
      Function1 var5,
      Function0 var6,
      Function1 var7,
      Composer var8,
      int var9,
      int var10
   ) {
      Composer var30 = var8.startRestartGroup(-1316795838);
      ComposerKt.sourceInformation(
         var30,
         "C(PreviewRootScreenContent)P(!1,7,6!2,5)72@2984L2,73@3045L2,74@3094L2,75@3132L2,76@3173L2,78@3212L7,79@3236L24,83@3389L32,80@3282L139,87@3454L34,91@3613L182,89@3519L276,101@3867L123,99@3801L196,108@4060L43,108@4003L100,116@4228L2550,112@4109L2669:PreviewRootScreen.kt#hhbc0c"
      );
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var30.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var30.changedInstance(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var30.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var30.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var30.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var30.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var30.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var30.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var30.getSkipping()) {
         var30.skipToGroupEnd();
      } else {
         if ((var10 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var10 & 4) != 0) {
            var2 = "";
         }

         if ((var10 & 8) != 0) {
            var30.startReplaceGroup(-404726073);
            ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var13 = var30;
            boolean var14 = false;
            int var15 = 0;
            Object var16 = var13.rememberedValue();
            int var17 = 0;
            Object var10000;
            if (var16 == Composer.Companion.getEmpty()) {
               int var18 = 0;
               Object var75 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1145_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$5$lambda$4((java.util.List) ((java.util.List)recoveredExpressionArgument_1145_0)));
               var13.updateRememberedValue(var75);
               var10000 = var75;
            } else {
               var10000 = var16;
            }

            Function1 var12 = (Function1)var10000;
            var30.endReplaceGroup();
            var3 = var12;
         }

         if ((var10 & 16) != 0) {
            var30.startReplaceGroup(-404724121);
            ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var36 = var30;
            boolean var42 = false;
            int var48 = 0;
            Object var56 = var36.rememberedValue();
            int var65 = 0;
            Object var123;
            if (var56 == Composer.Companion.getEmpty()) {
               int var76 = 0;
               Object var77 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1146_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$7$lambda$6((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile) ((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile)recoveredExpressionArgument_1146_0)));
               var36.updateRememberedValue(var77);
               var123 = var77;
            } else {
               var123 = var56;
            }

            Function1 var31 = (Function1)var123;
            var30.endReplaceGroup();
            var4 = var31;
         }

         if ((var10 & 32) != 0) {
            var30.startReplaceGroup(-404722553);
            ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var37 = var30;
            boolean var43 = false;
            int var49 = 0;
            Object var57 = var37.rememberedValue();
            int var66 = 0;
            Object var124;
            if (var57 == Composer.Companion.getEmpty()) {
               int var78 = 0;
               Object var79 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1147_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$9$lambda$8((java.util.List) ((java.util.List)recoveredExpressionArgument_1147_0)));
               var37.updateRememberedValue(var79);
               var124 = var79;
            } else {
               var124 = var57;
            }

            Function1 var32 = (Function1)var124;
            var30.endReplaceGroup();
            var5 = var32;
         }

         if ((var10 & 64) != 0) {
            var30.startReplaceGroup(-404721337);
            ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var38 = var30;
            boolean var44 = false;
            int var50 = 0;
            Object var58 = var38.rememberedValue();
            int var67 = 0;
            Object var125;
            if (var58 == Composer.Companion.getEmpty()) {
               int var80 = 0;
               Object var81 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$11$lambda$10());
               var38.updateRememberedValue(var81);
               var125 = var81;
            } else {
               var125 = var58;
            }

            Function0 var33 = (Function0)var125;
            var30.endReplaceGroup();
            var6 = var33;
         }

         if ((var10 & 128) != 0) {
            var30.startReplaceGroup(-404720025);
            ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
            Composer var39 = var30;
            boolean var45 = false;
            int var51 = 0;
            Object var59 = var39.rememberedValue();
            int var68 = 0;
            Object var126;
            if (var59 == Composer.Companion.getEmpty()) {
               int var82 = 0;
               Object var83 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1149_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$13$lambda$12(((java.lang.Integer)recoveredExpressionArgument_1149_0).intValue()));
               var39.updateRememberedValue(var83);
               var126 = var83;
            } else {
               var126 = var59;
            }

            Function1 var34 = (Function1)var126;
            var30.endReplaceGroup();
            var7 = var34;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1316795838, var11, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenContent (PreviewRootScreen.kt:77)");
         }

         CompositionLocal var40 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var46 = 0;
         int var52 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var60 = var30.consume(var40);
         ComposerKt.sourceInformationMarkerEnd(var30);
         Context var35 = (Context)var60;
         boolean var53 = false;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var69 = var30;
         ComposerKt.sourceInformationMarkerStart(var30, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var84 = var30;
         boolean var19 = false;
         int var20 = 0;
         Object var21 = var84.rememberedValue();
         int var22 = 0;
         Object var127;
         if (var21 == Composer.Companion.getEmpty()) {
            int var23 = 0;
            int var24 = 0;
            Object var114 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var69);
            var84.updateRememberedValue(var114);
            var127 = var114;
         } else {
            var127 = var21;
         }

         CoroutineScope var25 = (CoroutineScope)var127;
         ComposerKt.sourceInformationMarkerEnd(var30);
         CoroutineScope var26 = var25;
         ComposerKt.sourceInformationMarkerEnd(var30);
         CoroutineScope var41 = var26;
         int var128 = var1.getCurrentPage();
         float var10001 = 0.0F;
         var30.startReplaceGroup(-404713083);
         ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var62 = var30;
         boolean var70 = var30.changedInstance(var1);
         int $i$f$cachex = 0;
         Object var90 = var62.rememberedValue();
         int var95 = 0;
         Object var10002;
         if (!var70 && var90 != Composer.Companion.getEmpty()) {
            var10002 = var90;
         } else {
            float var28 = 0.0F;
            int var27 = var128;
            int var100 = 0;
            final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1150_0 = var1;
            Function0 var29 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$15$lambda$14(recoveredExpressionCapture_1150_0));
            var128 = var27;
            var10001 = var28;
            Object var105 = var29;
            var62.updateRememberedValue(var105);
            var10002 = var105;
         }

         Function0 var54 = (Function0)var10002;
         var30.endReplaceGroup();
         PagerState var47 = PagerStateKt.rememberPagerState(var128, var10001, var54, var30, 48, 0);
         var30.startReplaceGroup(-404711001);
         ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var71 = var30;
         boolean var86 = false;
         int $i$f$cachexx = 0;
         Object it$ivx = var71.rememberedValue();
         int var101/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var129;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var106 = 0;
            Object var107 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var71.updateRememberedValue(var107);
            var129 = var107;
         } else {
            var129 = it$ivx;
         }

         MutableState var63 = (MutableState)var129;
         var30.endReplaceGroup();
         MutableState var55 = var63;
         ActivityResultContract var130 = new RequestPermission();
         var30.startReplaceGroup(-404705765);
         ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var87 = var30;
         boolean var92 = (var11 & 3670016) == 1048576 | var30.changedInstance(var35) | (var11 & 896) == 256;
         int $i$f$cachexxx = 0;
         Object it$ivxx = var87.rememberedValue();
         int var108/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var135;
         if (!var92 && it$ivxx != Composer.Companion.getEmpty()) {
            var135 = it$ivxx;
         } else {
            ActivityResultContract var118 = var130;
            int var111 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1143_0 = var6;
            final android.content.Context recoveredExpressionCapture_1143_1 = var35;
            final java.lang.String recoveredExpressionCapture_1143_2 = var2;
            Function1 var134 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1143_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$20$lambda$19(recoveredExpressionCapture_1143_0, recoveredExpressionCapture_1143_1, recoveredExpressionCapture_1143_2, ((java.lang.Boolean)recoveredExpressionArgument_1143_0).booleanValue()));
            var130 = var118;
            Object var115 = var134;
            var87.updateRememberedValue(var115);
            var135 = var115;
         }

         Function1 var72 = (Function1)var135;
         var30.endReplaceGroup();
         ManagedActivityResultLauncher var64 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var130, var72, var30, 0);
         String var131 = "Page Navigation";
         var30.startReplaceGroup(-404697696);
         ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var88 = var30;
         boolean var93 = var30.changed(var47) | (var11 & 29360128) == 8388608;
         int $i$f$cachexxxx = 0;
         Object it$ivxxx = var88.rememberedValue();
         int var109/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var137;
         if (!var93 && it$ivxxx != Composer.Companion.getEmpty()) {
            var137 = it$ivxxx;
         } else {
            String var119 = "Page Navigation";
            int var112 = 0;
            Function2 var136 = new PreviewRootScreenKt$PreviewRootScreenContent$6$1(var47, var7, null);
            var131 = var119;
            Object var116 = var136;
            var88.updateRememberedValue(var116);
            var137 = var116;
         }

         Function2 var73 = (Function2)var137;
         var30.endReplaceGroup();
         EffectsKt.LaunchedEffect(var131, var73, var30, 6);
         Integer var132 = var47.getCurrentPage();
         Integer var138 = var1.getFiles().size();
         var30.startReplaceGroup(-404691600);
         ComposerKt.sourceInformation(var30, "CC(remember):PreviewRootScreen.kt#9igjgp");
         Composer var89 = var30;
         boolean var94 = false;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxx = var89.rememberedValue();
         int var110/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var139;
         if (it$ivxxxx == Composer.Companion.getEmpty()) {
            Integer var121 = var138;
            Integer var120 = var132;
            int var113 = 0;
            Function2 var122 = new PreviewRootScreenKt$PreviewRootScreenContent$7$1(var55, null);
            var132 = var120;
            var138 = var121;
            Object var117 = var122;
            var89.updateRememberedValue(var117);
            var139 = var117;
         } else {
            var139 = it$ivxxxx;
         }

         Function2 var74 = (Function2)var139;
         var30.endReplaceGroup();
         EffectsKt.LaunchedEffect(var132, var138, var74, var30, 0);
         ScaffoldKt.recovered$857cf99fc8161460(
            var0,
            null,
            null,
            null,
            null,
            0,
            Color.Companion.recovered$235b30c090a399c5(),
            Color.Companion.recovered$95989221dad41c4d(),
            null,
            ComposableLambdaKt.rememberComposableLambda(
               612610579, true, new PreviewRootScreenKt$PreviewRootScreenContent$8(var1, var3, var4, var5, var6, var35, var64, var47, var41, var55), var30, 54
            ),
            var30,
            819462144 | 14 & var11,
            318
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var133 = var30.endRestartGroup();
      if (var133 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1144_0 = var0;
         final io.intercom.android.sdk.m5.preview.model.PreviewUiState recoveredExpressionCapture_1144_1 = var1;
         final java.lang.String recoveredExpressionCapture_1144_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1144_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1144_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1144_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1144_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1144_7 = var7;
         final int recoveredExpressionCapture_1144_8 = var9;
         final int recoveredExpressionCapture_1144_9 = var10;
         var133.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1144_0, recoveredExpressionArgument_1144_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenContent$lambda$23(recoveredExpressionCapture_1144_0, recoveredExpressionCapture_1144_1, recoveredExpressionCapture_1144_2, recoveredExpressionCapture_1144_3, recoveredExpressionCapture_1144_4, recoveredExpressionCapture_1144_5, recoveredExpressionCapture_1144_6, recoveredExpressionCapture_1144_7, recoveredExpressionCapture_1144_8, recoveredExpressionCapture_1144_9, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1144_0), ((java.lang.Integer)recoveredExpressionArgument_1144_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewRootScreenPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1805731172);
      ComposerKt.sourceInformation(var2, "C(PreviewRootScreenPreview)180@6857L189:PreviewRootScreen.kt#hhbc0c");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1805731172, var1, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenPreview (PreviewRootScreen.kt:179)");
         }

         PreviewRootScreenContent(
            null, new PreviewUiState(null, 0, true, false, "Send", true, null, null, null, 459, null), null, null, null, null, null, null, var2, 0, 253
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1151_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1151_0, recoveredExpressionArgument_1151_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewRootScreenKt.PreviewRootScreenPreview$lambda$24(recoveredExpressionCapture_1151_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1151_0), ((java.lang.Integer)recoveredExpressionArgument_1151_1).intValue())));
      }
   }

   private static final Unit PreviewRootScreen$lambda$1$lambda$0(PreviewViewModel var0, PreviewUiState var1, Context var2) {
      var0.saveFile$intercom_sdk_base_release((IntercomPreviewFile)var1.getFiles().get(var1.getCurrentPage()), var2);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreen$lambda$3(
      Modifier var0,
      IntercomPreviewArgs var1,
      PreviewViewModel var2,
      Function1 var3,
      Function1 var4,
      Function1 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      PreviewRootScreen(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$5$lambda$4(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$7$lambda$6(IntercomPreviewFile var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$9$lambda$8(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$11$lambda$10() {
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$13$lambda$12(int var0) {
      return Unit.INSTANCE;
   }

   private static final int PreviewRootScreenContent$lambda$15$lambda$14(PreviewUiState var0) {
      return var0.getFiles().size();
   }

   private static final boolean PreviewRootScreenContent$lambda$17(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void PreviewRootScreenContent$lambda$18(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit PreviewRootScreenContent$lambda$20$lambda$19(Function0 var0, Context var1, String var2, boolean var3) {
      if (var3) {
         var0.invoke();
      } else {
         Toast.makeText(var1, var2, 1).show();
      }

      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenContent$lambda$23(
      Modifier var0,
      PreviewUiState var1,
      String var2,
      Function1 var3,
      Function1 var4,
      Function1 var5,
      Function0 var6,
      Function1 var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      PreviewRootScreenContent(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewRootScreenPreview$lambda$24(int var0, Composer var1, int var2) {
      PreviewRootScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final boolean access$PreviewRootScreenContent$lambda$17(MutableState var0) {
      return PreviewRootScreenContent$lambda$17(var0);
   }

   // $VF: synthetic method
   public static final void access$PreviewRootScreenContent$lambda$18(MutableState var0, boolean var1) {
      PreviewRootScreenContent$lambda$18(var0, var1);
   }
}
