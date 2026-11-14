package io.intercom.android.sdk.m5.preview.ui;

import android.os.Build.VERSION;
import android.os.ext.SdkExtensions;
import androidx.activity.compose.ActivityResultRegistryKt;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.Indication;
import androidx.compose.foundation.IndicationKt;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.SetsKt;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000V\n\u0000\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a\u0097\u0001\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012'\u0010\u0012\u001a#\u0012\u0019\u0012\u0017\u0012\u0004\u0012\u00020\u00150\u0014¢\u0006\f\b\u0016\u0012\b\b\u0017\u0012\u0004\b\b(\u0018\u0012\u0004\u0012\u00020\n0\u00132\u0006\u0010\u0019\u001a\u00020\u001a2\b\b\u0002\u0010\u001b\u001a\u00020\u001c2\u000e\b\u0002\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\n0\u001e2\u0011\u0010\u001f\u001a\r\u0012\u0004\u0012\u00020\n0\u001e¢\u0006\u0002\b H\u0007¢\u0006\u0002\u0010!\u001a\u0006\u0010\"\u001a\u00020\u001c\u001a\r\u0010#\u001a\u00020\nH\u0003¢\u0006\u0002\u0010$\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006%",
   d2 = {
         "defaultTrustedImageExtensions",
         "",
         "",
         "defaultTrustedVideoExtensions",
         "defaultTrustedDocumentExtensions",
         "defaultTrustedAudioExtensions",
         "defaultTrustedFileExtensions",
         "getDefaultTrustedFileExtensions",
         "()Ljava/util/Set;",
         "MediaPickerButton",
         "",
         "maxSelection",
         "",
         "indication",
         "Landroidx/compose/foundation/Indication;",
         "mediaType",
         "Lio/intercom/android/sdk/m5/preview/ui/MediaType;",
         "trustedFileExtensions",
         "onResult",
         "Lkotlin/Function1;",
         "",
         "Landroid/net/Uri;",
         "Lkotlin/ParameterName;",
         "name",
         "uris",
         "mediaPickerButtonCTAStyle",
         "Lio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonCTAStyle;",
         "showPreview",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "content",
         "Landroidx/compose/runtime/Composable;",
         "(ILandroidx/compose/foundation/Indication;Lio/intercom/android/sdk/m5/preview/ui/MediaType;Ljava/util/Set;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonCTAStyle;ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "isPhotoPickerAvailable",
         "MediaPickerButtonPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaPickerButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 5 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 6 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 7 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 8 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,258:1\n75#2:259\n1047#3,6:260\n1047#3,3:273\n1050#3,3:277\n1047#3,6:280\n1047#3,6:286\n1047#3,6:292\n1047#3,6:298\n615#4:266\n612#4,6:267\n613#5:276\n70#6:304\n67#6,9:305\n77#6:339\n81#7,6:314\n88#7,6:329\n96#7:338\n402#8,9:320\n411#8,3:335\n*S KotlinDebug\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonKt\n*L\n58#1:259\n64#1:260,6\n67#1:273,3\n67#1:277,3\n70#1:280,6\n77#1:286,6\n99#1:292,6\n101#1:298,6\n67#1:266\n67#1:267,6\n67#1:276\n96#1:304\n96#1:305,9\n96#1:339\n96#1:314,6\n96#1:329,6\n96#1:338\n96#1:320,9\n96#1:335,3\n*E\n"
)
public final class MediaPickerButtonKt {
   @NotNull
   private static final Set defaultTrustedImageExtensions;
   @NotNull
   private static final Set defaultTrustedVideoExtensions;
   @NotNull
   private static final Set defaultTrustedDocumentExtensions;
   @NotNull
   private static final Set defaultTrustedAudioExtensions;
   @NotNull
   private static final Set defaultTrustedFileExtensions;

   @NotNull
   public static final Set getDefaultTrustedFileExtensions() {
      return defaultTrustedFileExtensions;
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void MediaPickerButton(
      int var0,
      @Nullable Indication var1,
      @Nullable MediaType var2,
      @Nullable Set var3,
      @NotNull Function1 var4,
      @NotNull MediaPickerButtonCTAStyle var5,
      boolean var6,
      @Nullable Function0 var7,
      @NotNull Function2 var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var4, "onResult");
      Intrinsics.checkNotNullParameter(var5, "mediaPickerButtonCTAStyle");
      Intrinsics.checkNotNullParameter(var8, "content");
      Composer var50 = var9.startRestartGroup(-476316130);
      ComposerKt.sourceInformation(
         var50,
         "C(MediaPickerButton)P(2,1,4,8,6,3,7,5)57@2312L7,63@2592L2,66@2653L24,69@2801L36,67@2704L140,76@3013L844,74@2871L993,98@3963L39,100@4059L86,95@3870L308:MediaPickerButton.kt#hhbc0c"
      );
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var50.changed(var0) ? 4 : 2;
      }

      if ((var10 & 48) == 0) {
         var12 |= (var11 & 2) == 0 && var50.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var50.changed(var2) ? 256 : 128;
      }

      if ((var10 & 3072) == 0) {
         var12 |= (var11 & 8) == 0 && var50.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var50.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= ((var10 & 262144) == 0 ? var50.changed(var5) : var50.changedInstance(var5)) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var50.changed(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var50.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 256) != 0) {
         var12 |= 100663296;
      } else if ((var10 & 100663296) == 0) {
         var12 |= var50.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 38347923) == 38347922 && var50.getSkipping()) {
         var50.skipToGroupEnd();
      } else {
         var50.startDefaults();
         if ((var10 & 1) != 0 && !var50.getDefaultsInvalid()) {
            var50.skipToGroupEnd();
            if ((var11 & 2) != 0) {
               var12 &= -113;
            }

            if ((var11 & 8) != 0) {
               var12 &= -7169;
            }
         } else {
            if ((var11 & 2) != 0) {
               CompositionLocal var13 = IndicationKt.getLocalIndication();
               int var14 = 0;
               int var15 = 0;
               ComposerKt.sourceInformationMarkerStart(var50, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
               Object var16 = var50.consume(var13);
               ComposerKt.sourceInformationMarkerEnd(var50);
               var1 = (Indication)var16;
               var12 &= -113;
            }

            if ((var11 & 4) != 0) {
               var2 = MediaType.All;
            }

            if ((var11 & 8) != 0) {
               var3 = defaultTrustedFileExtensions;
               var12 &= -7169;
            }

            if ((var11 & 64) != 0) {
               var6 = true;
            }

            if ((var11 & 128) != 0) {
               var50.startReplaceGroup(-1247397916);
               ComposerKt.sourceInformation(var50, "CC(remember):MediaPickerButton.kt#9igjgp");
               Composer var53 = var50;
               boolean var55 = false;
               int var59 = 0;
               Object var17 = var53.rememberedValue();
               int var18 = 0;
               Object var10000;
               if (var17 == Composer.Companion.getEmpty()) {
                  int var19 = 0;
                  Object var78 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButton$lambda$1$lambda$0());
                  var53.updateRememberedValue(var78);
                  var10000 = var78;
               } else {
                  var10000 = var17;
               }

               Function0 var51 = (Function0)var10000;
               var50.endReplaceGroup();
               var7 = var51;
            }
         }

         var50.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-476316130, var12, -1, "io.intercom.android.sdk.m5.preview.ui.MediaPickerButton (MediaPickerButton.kt:65)");
         }

         boolean var56 = false;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart(var50, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var66 = var50;
         ComposerKt.sourceInformationMarkerStart(var50, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var72 = var50;
         boolean var79 = false;
         int var20 = 0;
         Object var21 = var72.rememberedValue();
         int var22 = 0;
         Object var111;
         if (var21 == Composer.Companion.getEmpty()) {
            int var23 = 0;
            int var24 = 0;
            Object var104 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var66);
            var72.updateRememberedValue(var104);
            var111 = var104;
         } else {
            var111 = var21;
         }

         CoroutineScope var25 = (CoroutineScope)var111;
         ComposerKt.sourceInformationMarkerEnd(var50);
         CoroutineScope var26 = var25;
         ComposerKt.sourceInformationMarkerEnd(var50);
         CoroutineScope var52 = var26;
         ActivityResultContract var112 = new PreviewMediaContract();
         var50.startReplaceGroup(-1247391194);
         ComposerKt.sourceInformation(var50, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var61 = var50;
         boolean var67 = (var12 & 57344) == 16384;
         int $i$f$cachex = 0;
         Object var80 = var61.rememberedValue();
         int var85 = 0;
         Object var116;
         if (!var67 && var80 != Composer.Companion.getEmpty()) {
            var116 = var80;
         } else {
            ActivityResultContract var43 = var112;
            int var90 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1134_0 = var4;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1134_0) -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButton$lambda$3$lambda$2(recoveredExpressionCapture_1134_0, (java.util.List) ((java.util.List)recoveredExpressionArgument_1134_0)));
            var112 = var43;
            Object var95 = var10001;
            var61.updateRememberedValue(var95);
            var116 = var95;
         }

         Function1 var57 = (Function1)var116;
         var50.endReplaceGroup();
         ManagedActivityResultLauncher var54 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var112, var57, var50, 0);
         ActivityResultContract var113 = new IntercomMediaPicker(var2, var3, var0);
         var50.startReplaceGroup(-1247383602);
         ComposerKt.sourceInformation(var50, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var68 = var50;
         boolean var74 = (var12 & 3670016) == 1048576
            | var50.changedInstance(var52)
            | var50.changedInstance(var54)
            | ((var12 & 458752) == 131072 || (var12 & 262144) != 0 && var50.changedInstance(var5))
            | (var12 & 57344) == 16384;
         int $i$f$cachexx = 0;
         Object it$ivx = var68.rememberedValue();
         int var91/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var118;
         if (!var74 && it$ivx != Composer.Companion.getEmpty()) {
            var118 = it$ivx;
         } else {
            ActivityResultContract var108 = var113;
            int var96 = 0;
            final boolean recoveredExpressionCapture_1135_0 = var6;
            final kotlinx.coroutines.CoroutineScope recoveredExpressionCapture_1135_1 = var52;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1135_2 = var4;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_1135_3 = var54;
            final io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle recoveredExpressionCapture_1135_4 = var5;
            Function1 var117 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1135_0) -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButton$lambda$5$lambda$4(recoveredExpressionCapture_1135_0, recoveredExpressionCapture_1135_1, recoveredExpressionCapture_1135_2, recoveredExpressionCapture_1135_3, recoveredExpressionCapture_1135_4, (java.util.List) ((java.util.List)recoveredExpressionArgument_1135_0)));
            var113 = var108;
            Object var101 = var117;
            var68.updateRememberedValue(var101);
            var118 = var101;
         }

         Function1 var62 = (Function1)var118;
         var50.endReplaceGroup();
         ManagedActivityResultLauncher var58 = ActivityResultRegistryKt.rememberLauncherForActivityResult(var113, var62, var50, 0);
         Modifier var114 = Modifier.Companion;
         var50.startReplaceGroup(-1247354007);
         ComposerKt.sourceInformation(var50, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var69 = var50;
         boolean var75 = false;
         int $i$f$cachexxx = 0;
         Object it$ivxx = var69.rememberedValue();
         int var92/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var120;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            Modifier var109 = var114;
            int var97 = 0;
            MutableInteractionSource var119 = InteractionSourceKt.MutableInteractionSource();
            var114 = var109;
            Object var98 = var119;
            var69.updateRememberedValue(var98);
            var120 = var98;
         } else {
            var120 = it$ivxx;
         }

         MutableInteractionSource var63 = (MutableInteractionSource)var120;
         var50.endReplaceGroup();
         MutableInteractionSource var121 = var63;
         Indication var10002 = var1;
         boolean var10003 = false;
         Object var10004 = null;
         Object var10005 = null;
         var50.startReplaceGroup(-1247350888);
         ComposerKt.sourceInformation(var50, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var70 = var50;
         boolean var76 = (var12 & 29360128) == 8388608 | var50.changedInstance(var58);
         int $i$f$cachexxxx = 0;
         Object it$ivxxx = var70.rememberedValue();
         int var93/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10006;
         if (!var76 && it$ivxxx != Composer.Companion.getEmpty()) {
            var10006 = it$ivxxx;
         } else {
            Object var48 = null;
            Object var47 = null;
            boolean var46 = false;
            Indication var45 = var1;
            MutableInteractionSource var44 = var63;
            Modifier var110 = var114;
            int var99 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1131_0 = var7;
            final androidx.activity.compose.ManagedActivityResultLauncher recoveredExpressionCapture_1131_1 = var58;
            Function0 var49 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButton$lambda$8$lambda$7(recoveredExpressionCapture_1131_0, recoveredExpressionCapture_1131_1));
            var114 = var110;
            var121 = var44;
            var10002 = var45;
            var10003 = var46;
            var10004 = var47;
            var10005 = var48;
            Object var102 = var49;
            var70.updateRememberedValue(var102);
            var10006 = var102;
         }

         Function0 var64 = (Function0)var10006;
         var50.endReplaceGroup();
         Modifier var65 = ClickableKt.recovered$1f84abc9b53ba787(var114, var121, var10002, var10003, (String)var10004, (Role)var10005, var64, 28, null);
         byte var84 = 0;
         int var89 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var50, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var71 = Alignment.Companion.getTopStart();
         boolean var77 = false;
         MeasurePolicy var94 = BoxKt.maybeCachedBoxMeasurePolicy(var71, var77);
         Modifier var100 = var65;
         MeasurePolicy var103 = var94;
         Composer var105 = var50;
         int var106 = 112 & var84 << 3;
         int var107 = 0;
         ComposerKt.sourceInformationMarkerStart(var105, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var27 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var105, 0));
         CompositionLocalMap var28 = var105.getCurrentCompositionLocalMap();
         Modifier var29 = ComposedModifierKt.materializeModifier(var105, var100);
         Function0 var30 = ComposeUiNode.Companion.getConstructor();
         Composer var31 = var105;
         int var32 = 6 | 896 & var106 << 6;
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
         Updater.recovered$89682b199a142880(var34, var103, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var34, var28, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var34, var27, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var34, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var34, var29, ComposeUiNode.Companion.getSetModifier());
         int var36 = 14 & var32 >> 6;
         Composer var37 = var31;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var39 = 6 | 112 & var84 >> 6;
         Composer var40 = var37;
         BoxScope var41 = BoxScopeInstance.INSTANCE;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, -662180359, "C105@4163L9:MediaPickerButton.kt#hhbc0c");
         var8.invoke(var40, 14 & var12 >> 24);
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var37);
         var31.endNode();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var105);
         ComposerKt.sourceInformationMarkerEnd(var50);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var115 = var50.endRestartGroup();
      if (var115 != null) {
         final int recoveredExpressionCapture_1132_0 = var0;
         final androidx.compose.foundation.Indication recoveredExpressionCapture_1132_1 = var1;
         final io.intercom.android.sdk.m5.preview.ui.MediaType recoveredExpressionCapture_1132_2 = var2;
         final java.util.Set recoveredExpressionCapture_1132_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1132_4 = var4;
         final io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle recoveredExpressionCapture_1132_5 = var5;
         final boolean recoveredExpressionCapture_1132_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1132_7 = var7;
         final kotlin.jvm.functions.Function2 recoveredExpressionCapture_1132_8 = var8;
         final int recoveredExpressionCapture_1132_9 = var10;
         final int recoveredExpressionCapture_1132_10 = var11;
         var115.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1132_0, recoveredExpressionArgument_1132_1) -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButton$lambda$10(recoveredExpressionCapture_1132_0, recoveredExpressionCapture_1132_1, recoveredExpressionCapture_1132_2, recoveredExpressionCapture_1132_3, recoveredExpressionCapture_1132_4, recoveredExpressionCapture_1132_5, recoveredExpressionCapture_1132_6, recoveredExpressionCapture_1132_7, recoveredExpressionCapture_1132_8, recoveredExpressionCapture_1132_9, recoveredExpressionCapture_1132_10, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1132_0), ((java.lang.Integer)recoveredExpressionArgument_1132_1).intValue())));
      }
   }

   public static final boolean isPhotoPickerAvailable() {
      return VERSION.SDK_INT >= 33 ? true : (VERSION.SDK_INT >= 30 ? SdkExtensions.getExtensionVersion(30) >= 2 : false);
   }

   @Container({@Preview(name = "Light Mode", uiMode = 16), @Preview(name = "Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MediaPickerButtonPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-875600568);
      ComposerKt.sourceInformation(var2, "C(MediaPickerButtonPreview)244@9555L335:MediaPickerButton.kt#hhbc0c");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-875600568, var1, -1, "io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonPreview (MediaPickerButton.kt:243)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.preview.ui.ComposableSingletons$MediaPickerButtonKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_1136_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1136_0, recoveredExpressionArgument_1136_1) -> io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt.MediaPickerButtonPreview$lambda$11(recoveredExpressionCapture_1136_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1136_0), ((java.lang.Integer)recoveredExpressionArgument_1136_1).intValue())));
      }
   }

   private static final Unit MediaPickerButton$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit MediaPickerButton$lambda$3$lambda$2(Function1 var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit MediaPickerButton$lambda$5$lambda$4(
      boolean var0, CoroutineScope var1, Function1 var2, ManagedActivityResultLauncher var3, MediaPickerButtonCTAStyle var4, List var5
   ) {
      Intrinsics.checkNotNullParameter(var5, "it");
      if (!var5.isEmpty() && var0) {
         BuildersKt.launch$default(var1, null, null, new MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1(var3, var5, var4, null), 3, null);
      } else {
         var2.invoke(var5);
      }

      return Unit.INSTANCE;
   }

   private static final Unit MediaPickerButton$lambda$8$lambda$7(Function0 var0, ManagedActivityResultLauncher var1) {
      var0.invoke();
      var1.launch("*/*");
      return Unit.INSTANCE;
   }

   private static final Unit MediaPickerButton$lambda$10(
      int var0,
      Indication var1,
      MediaType var2,
      Set var3,
      Function1 var4,
      MediaPickerButtonCTAStyle var5,
      boolean var6,
      Function0 var7,
      Function2 var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      MediaPickerButton(var0, var1, var2, var3, var4, var5, var6, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit MediaPickerButtonPreview$lambda$11(int var0, Composer var1, int var2) {
      MediaPickerButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   static {
      String[] var0 = new String[]{"gif", "jpeg", "jpg", "png", "heic", "dng"};
      defaultTrustedImageExtensions = SetsKt.setOf(var0);
      String[] var1 = new String[]{"mp4", "mov"};
      defaultTrustedVideoExtensions = SetsKt.setOf(var1);
      String[] var2 = new String[]{"pdf", "txt"};
      defaultTrustedDocumentExtensions = SetsKt.setOf(var2);
      String[] var3 = new String[]{"oga", "ogg"};
      defaultTrustedAudioExtensions = SetsKt.setOf(var3);
      defaultTrustedFileExtensions = SetsKt.plus(
         SetsKt.plus(SetsKt.plus(defaultTrustedImageExtensions, defaultTrustedVideoExtensions), defaultTrustedDocumentExtensions),
         defaultTrustedAudioExtensions
      );
   }
}
