package io.intercom.android.sdk.m5.conversation.utils.audio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Denied;
import io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Granted;
import io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$NotRequired;
import io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$PermanentlyDenied;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.component.PermissionDeniedDialogKt;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u001a/\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0005\u001a\u001a\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0000\u001a\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\tH\u0000\u001a\u0012\u0010\u000e\u001a\u00020\r2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0000¨\u0006\u000f²\u0006\n\u0010\u0010\u001a\u00020\rX\u008a\u008e\u0002",
   d2 = {
         "rememberAudioPermissionHandler",
         "Lkotlin/Function0;",
         "",
         "onPermissionGranted",
         "onPermissionDenied",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)Lkotlin/jvm/functions/Function0;",
         "checkAudioPermissionStatus",
         "Lio/intercom/android/sdk/m5/conversation/utils/audio/PermissionResult;",
         "context",
         "Landroid/content/Context;",
         "activity",
         "Landroid/app/Activity;",
         "hasRecordAudioPermissionInManifest",
         "",
         "shouldShowRequestPermissionRationale",
         "intercom-sdk-base_release",
         "showSettingsDialog"
   }
)
@SourceDebugExtension(
   "SMAP\nAudioPermissionHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AudioPermissionHandler.kt\nio/intercom/android/sdk/m5/conversation/utils/audio/AudioPermissionHandlerKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,152:1\n75#2:153\n75#2:154\n1047#3,6:155\n1047#3,6:161\n1047#3,6:167\n1047#3,6:173\n1047#3,6:179\n85#4:185\n117#4,2:186\n*S KotlinDebug\n*F\n+ 1 AudioPermissionHandler.kt\nio/intercom/android/sdk/m5/conversation/utils/audio/AudioPermissionHandlerKt\n*L\n41#1:153\n46#1:154\n47#1:155,6\n51#1:161,6\n60#1:167,6\n92#1:173,6\n96#1:179,6\n47#1:185\n47#1:186,2\n*E\n"
)
public final class AudioPermissionHandlerKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   @NotNull
   public static final Function0 rememberAudioPermissionHandler(@NotNull Function0 var0, @NotNull Function0 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "onPermissionGranted");
      Intrinsics.checkNotNullParameter(var1, "onPermissionDenied");
      var2.startReplaceGroup(2051621013);
      ComposerKt.sourceInformation(
         var2,
         "C(rememberAudioPermissionHandler)P(1)40@1697L7,45@1960L7,46@2011L34,50@2181L139,48@2076L244,59@2421L621,88@3480L47,89@3555L46,91@3685L95,95@3806L272,86@3399L690:AudioPermissionHandler.kt#lzdt6o"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            2051621013, var3, -1, "io.intercom.android.sdk.m5.conversation.utils.audio.rememberAudioPermissionHandler (AudioPermissionHandler.kt:39)"
         );
      }

      CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalContext();
      int var6 = 0;
      int var7 = 0;
      ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var8 = var2.consume(var5);
      ComposerKt.sourceInformationMarkerEnd(var2);
      Context var4 = (Context)var8;
      CompositionLocal var29 = AndroidCompositionLocals_androidKt.getLocalContext();
      int $changed$ivx = 0;
      int $i$f$getCurrentx = 0;
      ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var10 = var2.consume(var29);
      ComposerKt.sourceInformationMarkerEnd(var2);
      Object var27 = var10;
      Activity var26 = var27 instanceof Activity ? (Activity)var27 : null;
      var2.startReplaceGroup(826581858);
      ComposerKt.sourceInformation(var2, "CC(remember):AudioPermissionHandler.kt#9igjgp");
      Composer var33 = var2;
      boolean var36 = false;
      int var40 = 0;
      Object var11 = var33.rememberedValue();
      int var12 = 0;
      Object var10000;
      if (var11 == Composer.Companion.getEmpty()) {
         int var13 = 0;
         Object var52 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
         var33.updateRememberedValue(var52);
         var10000 = var52;
      } else {
         var10000 = var11;
      }

      MutableState var30 = (MutableState)var10000;
      var2.endReplaceGroup();
      MutableState var28 = var30;
      ActivityResultContract var74 = new RequestPermission();
      var2.startReplaceGroup(826587403);
      ComposerKt.sourceInformation(var2, "CC(remember):AudioPermissionHandler.kt#9igjgp");
      Composer var37 = var2;
      boolean var41 = ((var3 & 14 ^ 6) > 4 && var2.changed(var0) || (var3 & 6) == 4) | ((var3 & 112 ^ 48) > 32 && var2.changed(var1) || (var3 & 48) == 32);
      int $i$f$cachex = 0;
      Object it$ivx = var37.rememberedValue();
      int var53/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var77;
      if (!var41 && it$ivx != Composer.Companion.getEmpty()) {
         var77 = it$ivx;
      } else {
         ActivityResultContract var19 = var74;
         int var14 = 0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_809_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_809_1 = var1;
         Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_809_0) -> io.intercom.android.sdk.m5.conversation.utils.audio.AudioPermissionHandlerKt.rememberAudioPermissionHandler$lambda$4$lambda$3(recoveredExpressionCapture_809_0, recoveredExpressionCapture_809_1, ((java.lang.Boolean)recoveredExpressionArgument_809_0).booleanValue()));
         var74 = var19;
         Object var15 = var10001;
         var37.updateRememberedValue(var15);
         var77 = var15;
      }

      Function1 var34 = (Function1)var77;
      var2.endReplaceGroup();
      ManagedActivityResultLauncher var31 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var74, var34, var2, 0);
      var2.startReplaceGroup(826595565);
      ComposerKt.sourceInformation(var2, "CC(remember):AudioPermissionHandler.kt#9igjgp");
      Composer var42 = var2;
      boolean var45 = false;
      int $i$f$cachexx = 0;
      Object it$ivxx = var42.rememberedValue();
      int var57/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
      Object var75;
      if (it$ivxx == Composer.Companion.getEmpty()) {
         int var60 = 0;
         final android.content.Context recoveredExpressionCapture_810_0 = var4;
         final android.app.Activity recoveredExpressionCapture_810_1 = var26;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_810_2 = var0;
         final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_810_3 = var31;
         final androidx.compose.runtime.MutableState recoveredExpressionCapture_810_4 = var28;
         Object var16 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.utils.audio.AudioPermissionHandlerKt.rememberAudioPermissionHandler$lambda$6$lambda$5(recoveredExpressionCapture_810_0, recoveredExpressionCapture_810_1, recoveredExpressionCapture_810_2, recoveredExpressionCapture_810_3, recoveredExpressionCapture_810_4));
         var42.updateRememberedValue(var16);
         var75 = var16;
      } else {
         var75 = it$ivxx;
      }

      Function0 var38 = (Function0)var75;
      var2.endReplaceGroup();
      Function0 var35 = var38;
      if (rememberAudioPermissionHandler$lambda$1(var28)) {
         String var39 = var4.getApplicationInfo().loadLabel(var4.getPackageManager()).toString();
         String var43 = Phrase.from(var4, string.intercom_need_microphone_access).put("app_name", var39).format().toString();
         Object var76 = null;
         String var78 = var43;
         String var10002 = StringResources_androidKt.stringResource(string.intercom_settings, var2, 0);
         String var10003 = StringResources_androidKt.stringResource(string.intercom_not_now, var2, 0);
         Integer var10004 = drawable.intercom_ic_microphone;
         var2.startReplaceGroup(826635487);
         ComposerKt.sourceInformation(var2, "CC(remember):AudioPermissionHandler.kt#9igjgp");
         Composer var50 = var2;
         boolean var55 = (var3 & 112 ^ 48) > 32 && var2.changed(var1) || (var3 & 48) == 32;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var50.rememberedValue();
         int var63/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10005;
         if (!var55 && it$ivxxx != Composer.Companion.getEmpty()) {
            var10005 = it$ivxxx;
         } else {
            Integer var23 = var10004;
            String var22 = var10003;
            String var21 = var10002;
            String var20 = var43;
            Object var67 = null;
            int var17 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_811_0 = var1;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_811_1 = var28;
            Function0 var24 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.utils.audio.AudioPermissionHandlerKt.rememberAudioPermissionHandler$lambda$8$lambda$7(recoveredExpressionCapture_811_0, recoveredExpressionCapture_811_1));
            var76 = var67;
            var78 = var20;
            var10002 = var21;
            var10003 = var22;
            var10004 = var23;
            Object var18 = var24;
            var50.updateRememberedValue(var18);
            var10005 = var18;
         }

         Function0 var46 = (Function0)var10005;
         var2.endReplaceGroup();
         Function0 var79 = var46;
         var2.startReplaceGroup(826639536);
         ComposerKt.sourceInformation(var2, "CC(remember):AudioPermissionHandler.kt#9igjgp");
         Composer var51 = var2;
         boolean var56 = var2.changedInstance(var4);
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var51.rememberedValue();
         int var64/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var10006;
         if (!var56 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var10006 = it$ivxxxx;
         } else {
            Function0 var73 = var46;
            Integer var72 = var10004;
            String var71 = var10003;
            String var70 = var10002;
            String var69 = var78;
            Object var68 = var76;
            int var65 = 0;
            final android.content.Context recoveredExpressionCapture_812_0 = var4;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_812_1 = var28;
            Function0 var25 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.utils.audio.AudioPermissionHandlerKt.rememberAudioPermissionHandler$lambda$10$lambda$9(recoveredExpressionCapture_812_0, recoveredExpressionCapture_812_1));
            var76 = var68;
            var78 = var69;
            var10002 = var70;
            var10003 = var71;
            var10004 = var72;
            var79 = var73;
            Object var66 = var25;
            var51.updateRememberedValue(var66);
            var10006 = var66;
         }

         Function0 var47 = (Function0)var10006;
         var2.endReplaceGroup();
         PermissionDeniedDialogKt.PermissionDeniedDialog((Modifier)var76, var78, var10002, var10003, var10004, var79, var47, var2, 0, 1);
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var35;
   }

   @NotNull
   public static final PermissionResult checkAudioPermissionStatus(@NotNull Context var0, @Nullable Activity var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      if (!hasRecordAudioPermissionInManifest(var0)) {
         return io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$NotRequired.INSTANCE;
      }

      PermissionResult var10000;
      switch (ContextCompat.checkSelfPermission(var0, "android.permission.RECORD_AUDIO")) {
         case -1:
            var10000 = shouldShowRequestPermissionRationale(var1) ? io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$PermanentlyDenied.INSTANCE : io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Denied.INSTANCE;
            break;
         case 0:
            var10000 = io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Granted.INSTANCE;
            break;
         default:
            var10000 = io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Denied.INSTANCE;
      }

      return var10000;
   }

   public static final boolean hasRecordAudioPermissionInManifest(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");

      boolean var1;
      try {
         PackageInfo var4 = var0.getPackageManager().getPackageInfo(var0.getPackageName(), 4096);
         var1 = var4.requestedPermissions != null ? ArraysKt.contains(var4.requestedPermissions, "android.permission.RECORD_AUDIO") : false;
      } catch (NameNotFoundException var3) {
         var1 = false;
      }

      return var1;
   }

   public static final boolean shouldShowRequestPermissionRationale(@Nullable Activity var0) {
      boolean var10000;
      if (var0 != null) {
         int var2 = 0;
         var10000 = ActivityCompat.shouldShowRequestPermissionRationale(var0, "android.permission.RECORD_AUDIO");
      } else {
         var10000 = false;
      }

      return var10000;
   }

   private static final boolean rememberAudioPermissionHandler$lambda$1(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void rememberAudioPermissionHandler$lambda$2(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit rememberAudioPermissionHandler$lambda$4$lambda$3(Function0 var0, Function0 var1, boolean var2) {
      if (var2) {
         var0.invoke();
      } else {
         var1.invoke();
      }

      return Unit.INSTANCE;
   }

   private static final Unit rememberAudioPermissionHandler$lambda$6$lambda$5(
      Context var0, Activity var1, Function0 var2, ManagedActivityResultLauncher var3, MutableState var4
   ) {
      PermissionResult var5 = checkAudioPermissionStatus(var0, var1);
      if (Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Granted.INSTANCE) || Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$NotRequired.INSTANCE)) {
         var2.invoke();
      } else if (Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$Denied.INSTANCE)) {
         var3.launch("android.permission.RECORD_AUDIO");
      } else {
         if (!Intrinsics.areEqual(var5, io.intercom.android.sdk.m5.conversation.utils.audio.PermissionResult$PermanentlyDenied.INSTANCE)) {
            throw new NoWhenBranchMatchedException();
         }

         rememberAudioPermissionHandler$lambda$2(var4, true);
      }

      return Unit.INSTANCE;
   }

   private static final Unit rememberAudioPermissionHandler$lambda$8$lambda$7(Function0 var0, MutableState var1) {
      rememberAudioPermissionHandler$lambda$2(var1, false);
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit rememberAudioPermissionHandler$lambda$10$lambda$9(Context var0, MutableState var1) {
      rememberAudioPermissionHandler$lambda$2(var1, false);
      Uri var2 = Uri.fromParts("package", var0.getPackageName(), null);
      var0.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var2));
      return Unit.INSTANCE;
   }
}
