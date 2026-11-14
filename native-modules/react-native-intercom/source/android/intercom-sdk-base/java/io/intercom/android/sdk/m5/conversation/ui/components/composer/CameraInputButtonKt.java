package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import io.intercom.android.sdk.IntercomFileProviderKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.component.PermissionDeniedDialogKt;
import io.intercom.android.sdk.utilities.Phrase;
import java.io.File;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000J\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u001aw\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052#\u0010\u0006\u001a\u001f\u0012\u0015\u0012\u0013\u0018\u00010\b¢\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\u0011\u0010\u000f\u001a\r\u0012\u0004\u0012\u00020\u00010\r¢\u0006\u0002\b\u0010H\u0001¢\u0006\u0002\u0010\u0011\u001a \u0010\u0012\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0002\u001a\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0013\u001a\u00020\u0014H\u0002¨\u0006\u001a²\u0006\n\u0010\u001b\u001a\u00020\u0016X\u008a\u008e\u0002²\u0006\n\u0010\u001c\u001a\u00020\u0019X\u008a\u008e\u0002",
   d2 = {
         "CameraInputButton",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "cameraInputType",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/CameraInputType;",
         "onResult",
         "Lkotlin/Function1;",
         "Landroid/net/Uri;",
         "Lkotlin/ParameterName;",
         "name",
         "uri",
         "performWithOnClick",
         "Lkotlin/Function0;",
         "onPermissionDenied",
         "content",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/ui/components/composer/CameraInputType;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "getUriByFileName",
         "context",
         "Landroid/content/Context;",
         "fileName",
         "",
         "folderName",
         "hasCameraPermissionInManifest",
         "",
         "intercom-sdk-base_release",
         "latestFileName",
         "showDialog"
   }
)
@SourceDebugExtension(
   "SMAP\nCameraInputButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CameraInputButton.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/CameraInputButtonKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,157:1\n1047#2,6:158\n1047#2,6:164\n1047#2,6:171\n1047#2,6:177\n1047#2,6:183\n1047#2,6:189\n1047#2,6:195\n1047#2,6:201\n1047#2,6:207\n75#3:170\n70#4:213\n67#4,9:214\n77#4:248\n81#5,6:223\n88#5,6:238\n96#5:247\n402#6,9:229\n411#6,3:244\n85#7:249\n117#7,2:250\n85#7:252\n117#7,2:253\n*S KotlinDebug\n*F\n+ 1 CameraInputButton.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/CameraInputButtonKt\n*L\n42#1:158,6\n43#1:164,6\n47#1:171,6\n54#1:177,6\n78#1:183,6\n90#1:189,6\n94#1:195,6\n105#1:201,6\n116#1:207,6\n46#1:170\n114#1:213\n114#1:214,9\n114#1:248\n114#1:223,6\n114#1:238,6\n114#1:247\n114#1:229,9\n114#1:244,3\n47#1:249\n47#1:250,2\n78#1:252\n78#1:253,2\n*E\n"
)
public final class CameraInputButtonKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void CameraInputButton(
      @Nullable Modifier var0,
      @NotNull CameraInputType var1,
      @NotNull Function1 var2,
      @Nullable Function0 var3,
      @Nullable Function0 var4,
      @NotNull Function2 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "cameraInputType");
      Intrinsics.checkNotNullParameter(var2, "onResult");
      Intrinsics.checkNotNullParameter(var5, "content");
      Composer var50 = var6.startRestartGroup(-2022676727);
      ComposerKt.sourceInformation(
         var50,
         "C(CameraInputButton)P(2!1,4,5,3)41@1513L2,42@1554L2,45@1630L7,46@1664L31,53@1980L228,51@1857L358,77@2943L34,104@4062L220,102@3968L314,115@4344L677,113@4288L766:CameraInputButton.kt#yb6g9"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var50.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var50.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var50.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var50.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var50.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var50.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var50.getSkipping()) {
         var50.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 8) != 0) {
            var50.startReplaceGroup(-845519198);
            ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
            Composer var11 = var50;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var75 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$1$lambda$0());
               var11.updateRememberedValue(var75);
               var10000 = var75;
            } else {
               var10000 = var14;
            }

            Function0 var10 = (Function0)var10000;
            var50.endReplaceGroup();
            var3 = var10;
         }

         if ((var8 & 16) != 0) {
            var50.startReplaceGroup(-845517886);
            ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
            Composer var53 = var50;
            boolean var56 = false;
            int var60 = 0;
            Object var64 = var53.rememberedValue();
            int var69 = 0;
            Object var143;
            if (var64 == Composer.Companion.getEmpty()) {
               int var76 = 0;
               Object var77 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$3$lambda$2());
               var53.updateRememberedValue(var77);
               var143 = var77;
            } else {
               var143 = var64;
            }

            Function0 var51 = (Function0)var143;
            var50.endReplaceGroup();
            var4 = var51;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2022676727, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButton (CameraInputButton.kt:44)"
            );
         }

         CompositionLocal var54 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var57 = 0;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var50, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var65 = var50.consume(var54);
         ComposerKt.sourceInformationMarkerEnd(var50);
         Context var52 = (Context)var65;
         var50.startReplaceGroup(-845514337);
         ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
         Composer var62 = var50;
         boolean var66 = false;
         int var70 = 0;
         Object var78 = var62.rememberedValue();
         int var17 = 0;
         Object var144;
         if (var78 == Composer.Companion.getEmpty()) {
            int var18 = 0;
            Object var92 = SnapshotStateKt.mutableStateOf$default("", null, 2, null);
            var62.updateRememberedValue(var92);
            var144 = var92;
         } else {
            var144 = var78;
         }

         MutableState var58 = (MutableState)var144;
         var50.endReplaceGroup();
         MutableState var55 = var58;
         String var145;
         switch (var1) {
            case VIDEO:
               var145 = "videos";
               break;
            case PHOTO:
               var145 = "images";
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         String var59 = var145;
         ActivityResultContract var146 = new StartActivityForResult();
         var50.startReplaceGroup(-845504028);
         ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
         Composer var71 = var50;
         boolean var79 = (var9 & 896) == 256 | var50.changedInstance(var52) | var50.changed(var59);
         int $i$f$cachex = 0;
         Object it$ivx = var71.rememberedValue();
         int var19/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var152;
         if (!var79 && it$ivx != Composer.Companion.getEmpty()) {
            var152 = it$ivx;
         } else {
            ActivityResultContract var43 = var146;
            int var20 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_516_0 = var2;
            final android.content.Context recoveredExpressionCapture_516_1 = var52;
            final java.lang.String recoveredExpressionCapture_516_2 = var59;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_516_3 = var55;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_516_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$8$lambda$7(recoveredExpressionCapture_516_0, recoveredExpressionCapture_516_1, recoveredExpressionCapture_516_2, recoveredExpressionCapture_516_3, (androidx.activity.result.ActivityResult) ((androidx.activity.result.ActivityResult)recoveredExpressionArgument_516_0)));
            var146 = var43;
            Object var21 = var10001;
            var71.updateRememberedValue(var21);
            var152 = var21;
         }

         Function1 var67 = (Function1)var152;
         var50.endReplaceGroup();
         ManagedActivityResultLauncher var63 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var146, var67, var50, 0);
         var50.startReplaceGroup(-845473406);
         ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
         Composer var80 = var50;
         boolean var86 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var80.rememberedValue();
         int var106/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var147;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var112 = 0;
            Object var113 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var80.updateRememberedValue(var113);
            var147 = var113;
         } else {
            var147 = it$ivxx;
         }

         MutableState var72 = (MutableState)var147;
         var50.endReplaceGroup();
         MutableState var68 = var72;
         var50.startReplaceGroup(-845471236);
         ComposerKt.sourceInformation(var50, "86@3345L47,87@3420L46,89@3544L87,93@3657L264,84@3264L668");
         if (CameraInputButton$lambda$10(var68)) {
            String var73 = var52.getApplicationInfo().loadLabel(var52.getPackageManager()).toString();
            String var81 = Phrase.from(var52, string.intercom_need_camera_access).put("app_name", var73).format().toString();
            Object var148 = null;
            String var153 = var81;
            String var10002 = StringResources_androidKt.stringResource(string.intercom_settings, var50, 0);
            String var10003 = StringResources_androidKt.stringResource(string.intercom_not_now, var50, 0);
            Integer var10004 = drawable.intercom_ic_camera;
            var50.startReplaceGroup(-845454121);
            ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
            Composer var95 = var50;
            boolean var101 = (var9 & 57344) == 16384;
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var95.rememberedValue();
            int var22/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var10005;
            if (!var101 && it$ivxxx != Composer.Companion.getEmpty()) {
               var10005 = it$ivxxx;
            } else {
               Integer var47 = var10004;
               String var46 = var10003;
               String var45 = var10002;
               String var44 = var81;
               Object var129 = null;
               int var23 = 0;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_517_0 = var4;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_517_1 = var68;
               Function0 var48 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$13$lambda$12(recoveredExpressionCapture_517_0, recoveredExpressionCapture_517_1));
               var148 = var129;
               var153 = var44;
               var10002 = var45;
               var10003 = var46;
               var10004 = var47;
               Object var24 = var48;
               var95.updateRememberedValue(var24);
               var10005 = var24;
            }

            Function0 var87 = (Function0)var10005;
            var50.endReplaceGroup();
            Function0 var160 = var87;
            var50.startReplaceGroup(-845450328);
            ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
            Composer var96 = var50;
            boolean var102 = var50.changedInstance(var52);
            int $i$f$cachexxxx = 0;
            Object it$ivxxxx = var96.rememberedValue();
            int var119/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
            Object var10006;
            if (!var102 && it$ivxxxx != Composer.Companion.getEmpty()) {
               var10006 = it$ivxxxx;
            } else {
               Function0 var141 = var87;
               Integer var139 = var10004;
               String var137 = var10003;
               String var135 = var10002;
               String var133 = var153;
               Object var130 = var148;
               int var123 = 0;
               final android.content.Context recoveredExpressionCapture_518_0 = var52;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_518_1 = var68;
               Function0 var49 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$15$lambda$14(recoveredExpressionCapture_518_0, recoveredExpressionCapture_518_1));
               var148 = var130;
               var153 = var133;
               var10002 = var135;
               var10003 = var137;
               var10004 = var139;
               var160 = var141;
               Object var127 = var49;
               var96.updateRememberedValue(var127);
               var10006 = var127;
            }

            Function0 var88 = (Function0)var10006;
            var50.endReplaceGroup();
            PermissionDeniedDialogKt.PermissionDeniedDialog((Modifier)var148, var153, var10002, var10003, var10004, var160, var88, var50, 0, 1);
         }

         var50.endReplaceGroup();
         ActivityResultContract var149 = new RequestPermission();
         var50.startReplaceGroup(-845437412);
         ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
         Composer var89 = var50;
         boolean var97 = (var9 & 112) == 32 | var50.changedInstance(var52) | var50.changed(var59) | var50.changedInstance(var63);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var89.rememberedValue();
         int var116/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var155;
         if (!var97 && it$ivxxx != Composer.Companion.getEmpty()) {
            var155 = it$ivxxx;
         } else {
            ActivityResultContract var131 = var149;
            int var120 = 0;
            final io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputType recoveredExpressionCapture_511_0 = var1;
            final android.content.Context recoveredExpressionCapture_511_1 = var52;
            final java.lang.String recoveredExpressionCapture_511_2 = var59;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_511_3 = var63;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_511_4 = var55;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_511_5 = var68;
            Function1 var154 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_511_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$17$lambda$16(recoveredExpressionCapture_511_0, recoveredExpressionCapture_511_1, recoveredExpressionCapture_511_2, recoveredExpressionCapture_511_3, recoveredExpressionCapture_511_4, recoveredExpressionCapture_511_5, ((java.lang.Boolean)recoveredExpressionArgument_511_0).booleanValue()));
            var149 = var131;
            Object var124 = var154;
            var89.updateRememberedValue(var124);
            var155 = var124;
         }

         Function1 var82 = (Function1)var155;
         var50.endReplaceGroup();
         ManagedActivityResultLauncher var74 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var149, var82, var50, 0);
         Modifier var150 = var0;
         boolean var156 = false;
         Object var157 = null;
         Object var158 = null;
         Object var159 = null;
         var50.startReplaceGroup(-845427931);
         ComposerKt.sourceInformation(var50, "CC(remember):CameraInputButton.kt#9igjgp");
         Composer var90 = var50;
         boolean var98 = (var9 & 7168) == 2048
            | var50.changedInstance(var52)
            | (var9 & 112) == 32
            | var50.changed(var59)
            | var50.changedInstance(var63)
            | var50.changedInstance(var74);
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var90.rememberedValue();
         int var117/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var161;
         if (!var98 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var161 = it$ivxxxx;
         } else {
            Object var140 = null;
            Object var138 = null;
            Object var136 = null;
            boolean var134 = false;
            Modifier var132 = var0;
            int var121 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_512_0 = var3;
            final android.content.Context recoveredExpressionCapture_512_1 = var52;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_512_2 = var74;
            final io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputType recoveredExpressionCapture_512_3 = var1;
            final java.lang.String recoveredExpressionCapture_512_4 = var59;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_512_5 = var63;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_512_6 = var55;
            Function0 var142 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$19$lambda$18(recoveredExpressionCapture_512_0, recoveredExpressionCapture_512_1, recoveredExpressionCapture_512_2, recoveredExpressionCapture_512_3, recoveredExpressionCapture_512_4, recoveredExpressionCapture_512_5, recoveredExpressionCapture_512_6));
            var150 = var132;
            var156 = var134;
            var157 = var136;
            var158 = var138;
            var159 = var140;
            Object var125 = var142;
            var90.updateRememberedValue(var125);
            var161 = var125;
         }

         Function0 var83 = (Function0)var161;
         var50.endReplaceGroup();
         Modifier var84 = ClickableKt.recovered$1c9660c41fd850dc(
            var150, var156, (String)var157, (Role)var158, (MutableInteractionSource)var159, var83, 15, null
         );
         byte var105 = 0;
         int var111 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var50, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var91 = Alignment.Companion.getTopStart();
         boolean var99 = false;
         MeasurePolicy var118 = BoxKt.maybeCachedBoxMeasurePolicy(var91, var99);
         Modifier var122 = var84;
         MeasurePolicy var126 = var118;
         Composer var128 = var50;
         int var25 = 112 & var105 << 3;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var128, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var27 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var128, 0));
         CompositionLocalMap var28 = var128.getCurrentCompositionLocalMap();
         Modifier var29 = ComposedModifierKt.materializeModifier(var128, var122);
         Function0 var30 = ComposeUiNode.Companion.getConstructor();
         Composer var31 = var128;
         int var32 = 6 | 896 & var25 << 6;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var31.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var31.startReusableNode();
         if (var31.getInserting()) {
            var31.createNode(var30);
         } else {
            var31.useNode();
         }

         Composer var34 = Updater.recovered$676aa17b44fd6280(var31);
         int var35 = 0;
         Updater.recovered$89682b199a142880(var34, var126, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var34, var28, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var34, var27, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var34, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var34, var29, ComposeUiNode.Companion.getSetModifier());
         int var36 = 14 & var32 >> 6;
         Composer var37 = var31;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var39 = 6 | 112 & var105 >> 6;
         Composer var40 = var37;
         BoxScope var41 = BoxScopeInstance.INSTANCE;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, -2087646309, "C131@5039L9:CameraInputButton.kt#yb6g9");
         var5.invoke(var40, 14 & var9 >> 15);
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var37);
         var31.endNode();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var128);
         ComposerKt.sourceInformationMarkerEnd(var50);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var151 = var50.endRestartGroup();
      if (var151 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_513_0 = var0;
         final io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputType recoveredExpressionCapture_513_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_513_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_513_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_513_4 = var4;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_513_5 = var5;
         final int recoveredExpressionCapture_513_6 = var7;
         final int recoveredExpressionCapture_513_7 = var8;
         var151.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_513_0, recoveredExpressionArgument_513_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt.CameraInputButton$lambda$21(recoveredExpressionCapture_513_0, recoveredExpressionCapture_513_1, recoveredExpressionCapture_513_2, recoveredExpressionCapture_513_3, recoveredExpressionCapture_513_4, recoveredExpressionCapture_513_5, recoveredExpressionCapture_513_6, recoveredExpressionCapture_513_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_513_0), ((java.lang.Integer)recoveredExpressionArgument_513_1).intValue())));
      }
   }

   private static final Uri getUriByFileName(Context var0, String var1, String var2) {
      File var10000 = var0.getExternalCacheDir();
      if (var10000 == null) {
         var10000 = var0.getCacheDir();
      }

      File var3 = var10000;
      File var4 = new File(var3, var2);
      var4.mkdir();
      File var5 = new File(var4, var1);
      Uri var6 = FileProvider.getUriForFile(var0, IntercomFileProviderKt.fileProviderAuthority(var0), var5);
      Intrinsics.checkNotNullExpressionValue(var6, "getUriForFile(...)");
      return var6;
   }

   private static final boolean hasCameraPermissionInManifest(Context var0) {
      try {
         PackageInfo var1 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 4096);
         return var1.requestedPermissions != null ? ArraysKt.contains(var1.requestedPermissions, "android.permission.CAMERA") : false;
      } catch (NameNotFoundException var2) {
         var2.printStackTrace();
         return false;
      }
   }

   private static final Unit CameraInputButton$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit CameraInputButton$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final String CameraInputButton$lambda$5(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void CameraInputButton$lambda$6(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit CameraInputButton$lambda$8$lambda$7(Function1 var0, Context var1, String var2, MutableState var3, ActivityResult var4) {
      Intrinsics.checkNotNullParameter(var4, "result");
      if (var4.getResultCode() == -1 && CameraInputButton$lambda$5(var3).length() > 0) {
         var0.invoke(getUriByFileName(var1, CameraInputButton$lambda$5(var3), var2));
      }

      CameraInputButton$lambda$6(var3, "");
      return Unit.INSTANCE;
   }

   private static final void CameraInputButton$launchCameraIntent(
      CameraInputType var0, Context var1, String var2, ManagedActivityResultLauncher var3, MutableState var4
   ) {
      String var10000;
      switch (var0) {
         case VIDEO:
            var10000 = ".mp4";
            break;
         case PHOTO:
            var10000 = ".jpg";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      String var5 = var10000;
      String var6 = UUID.randomUUID() + var5;
      Uri var7 = getUriByFileName(var1, var6, var2);
      CameraInputButton$lambda$6(var4, var6);
      Intent var9;
      switch (var0) {
         case VIDEO:
            var9 = new Intent("android.media.action.VIDEO_CAPTURE");
            break;
         case PHOTO:
            var9 = new Intent("android.media.action.IMAGE_CAPTURE");
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      Intent var10 = var9.putExtra("output", var7);
      Intrinsics.checkNotNullExpressionValue(var10, "putExtra(...)");
      Intent var8 = var10;
      var3.launch(var8);
   }

   private static final boolean CameraInputButton$lambda$10(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void CameraInputButton$lambda$11(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit CameraInputButton$lambda$13$lambda$12(Function0 var0, MutableState var1) {
      CameraInputButton$lambda$11(var1, false);
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit CameraInputButton$lambda$15$lambda$14(Context var0, MutableState var1) {
      CameraInputButton$lambda$11(var1, false);
      Uri var2 = Uri.fromParts("package", var0.getPackageName(), null);
      var0.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var2));
      return Unit.INSTANCE;
   }

   private static final Unit CameraInputButton$lambda$17$lambda$16(
      CameraInputType var0, Context var1, String var2, ManagedActivityResultLauncher var3, MutableState var4, MutableState var5, boolean var6
   ) {
      if (var6) {
         CameraInputButton$launchCameraIntent(var0, var1, var2, var3, var4);
      } else {
         CameraInputButton$lambda$11(var5, true);
      }

      return Unit.INSTANCE;
   }

   private static final Unit CameraInputButton$lambda$19$lambda$18(
      Function0 var0,
      Context var1,
      ManagedActivityResultLauncher var2,
      CameraInputType var3,
      String var4,
      ManagedActivityResultLauncher var5,
      MutableState var6
   ) {
      var0.invoke();
      if (hasCameraPermissionInManifest(var1)) {
         int var7 = ContextCompat.checkSelfPermission(var1, "android.permission.CAMERA");
         if (var7 == 0) {
            CameraInputButton$launchCameraIntent(var3, var1, var4, var5, var6);
         } else {
            var2.launch("android.permission.CAMERA");
         }
      } else {
         CameraInputButton$launchCameraIntent(var3, var1, var4, var5, var6);
      }

      return Unit.INSTANCE;
   }

   private static final Unit CameraInputButton$lambda$21(
      Modifier var0, CameraInputType var1, Function1 var2, Function0 var3, Function0 var4, Function2 var5, int var6, int var7, Composer var8, int var9
   ) {
      CameraInputButton(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }
}
