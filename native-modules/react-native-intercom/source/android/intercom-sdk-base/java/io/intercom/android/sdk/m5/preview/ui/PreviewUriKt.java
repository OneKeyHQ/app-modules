package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup.LayoutParams;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.DisposableEffectResult;
import androidx.compose.runtime.DisposableEffectScope;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Lifecycle.Event;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import coil.ImageLoader;
import coil.compose.AsyncImageKt;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgsKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Error;
import io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Loading;
import io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Success;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aN\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072#\b\u0002\u0010\b\u001a\u001d\u0012\u0013\u0012\u00110\u0007¢\u0006\f\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\r\u001a)\u0010\u000e\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0011\u001a\u001f\u0010\u0012\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0014H\u0003¢\u0006\u0002\u0010\u0015\u001a1\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0017\u001a\u00020\u00072\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0003¢\u0006\u0002\u0010\u0018\u001a\u001f\u0010\u0019\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0002\u0010\u001a\u001a\u001b\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c2\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0002\u0010\u001e¨\u0006\u001f",
   d2 = {
         "PreviewUri",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "file",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "isCurrentPage",
         "",
         "onZoomChanged",
         "Lkotlin/Function1;",
         "Lkotlin/ParameterName;",
         "name",
         "isZoomed",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;ZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "ThumbnailPreview",
         "contentScale",
         "Landroidx/compose/ui/layout/ContentScale;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/ui/layout/ContentScale;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;Landroidx/compose/runtime/Composer;II)V",
         "VideoPlayer",
         "uri",
         "Landroid/net/Uri;",
         "(Landroidx/compose/ui/Modifier;Landroid/net/Uri;Landroidx/compose/runtime/Composer;II)V",
         "DocumentPreview",
         "showTitle",
         "(Landroidx/compose/ui/Modifier;Landroid/net/Uri;ZLandroidx/compose/ui/layout/ContentScale;Landroidx/compose/runtime/Composer;II)V",
         "PdfPreview",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;Landroidx/compose/runtime/Composer;II)V",
         "loadPdfState",
         "Landroidx/compose/runtime/State;",
         "Lio/intercom/android/sdk/m5/preview/ui/PdfLoadState;",
         "(Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;Landroidx/compose/runtime/Composer;I)Landroidx/compose/runtime/State;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPreviewUri.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Effects.kt\nandroidx/compose/runtime/DisposableEffectScope\n+ 9 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n*L\n1#1,320:1\n1047#2,6:321\n1047#2,6:333\n1047#2,6:339\n1047#2,6:381\n1047#2,6:388\n75#3:327\n75#3:328\n75#3:329\n75#3:331\n75#3:332\n75#3:345\n75#3:387\n1#4:330\n70#5:346\n68#5,8:347\n77#5:380\n81#6,6:355\n88#6,6:370\n96#6:379\n402#7,9:361\n411#7,3:376\n68#8,5:394\n168#9,13:399\n*S KotlinDebug\n*F\n+ 1 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt\n*L\n71#1:321,6\n162#1:333,6\n176#1:339,6\n266#1:381,6\n292#1:388,6\n73#1:327\n113#1:328\n128#1:329\n149#1:331\n150#1:332\n205#1:345\n290#1:387\n255#1:346\n255#1:347,8\n255#1:380\n255#1:355,6\n255#1:370,6\n255#1:379\n255#1:361,9\n255#1:376,3\n190#1:394,5\n267#1:399,13\n*E\n"
)
public final class PreviewUriKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewUri(
      @Nullable Modifier var0, @NotNull IntercomPreviewFile var1, boolean var2, @Nullable Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "file");
      Composer var15 = var4.startRestartGroup(-1870915791);
      ComposerKt.sourceInformation(var15, "C(PreviewUri)P(2)70@2948L2,72@2987L7:PreviewUri.kt#hhbc0c");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var15.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var15.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var15.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var15.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var15.getSkipping()) {
         var15.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 4) != 0) {
            var2 = true;
         }

         if ((var6 & 8) != 0) {
            var15.startReplaceGroup(177778760);
            ComposerKt.sourceInformation(var15, "CC(remember):PreviewUri.kt#9igjgp");
            Composer var9 = var15;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var23 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1167_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.PreviewUri$lambda$1$lambda$0(((java.lang.Boolean)recoveredExpressionArgument_1167_0).booleanValue()));
               var9.updateRememberedValue(var23);
               var10000 = var23;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var15.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1870915791, var7, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewUri (PreviewUri.kt:71)");
         }

         CompositionLocal var17 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var19 = 0;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var22 = var15.consume(var17);
         ComposerKt.sourceInformationMarkerEnd(var15);
         Context var16 = (Context)var22;
         var15.startReplaceGroup(177788277);
         ComposerKt.sourceInformation(var15, "77@3279L53");
         if (!IntercomPreviewArgsKt.hasSafeScheme(var1)) {
            Uri var10001 = Uri.EMPTY;
            Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
            DocumentPreview(var0, var10001, false, null, var15, 14 & var7, 12);
            var15.endReplaceGroup();
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var24 = var15.endRestartGroup();
            if (var24 != null) {
               final androidx.compose.ui.Modifier recoveredExpressionCapture_1168_0 = var0;
               final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1168_1 = var1;
               final boolean recoveredExpressionCapture_1168_2 = var2;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1168_3 = var3;
               final int recoveredExpressionCapture_1168_4 = var5;
               final int recoveredExpressionCapture_1168_5 = var6;
               var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1168_0, recoveredExpressionArgument_1168_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.PreviewUri$lambda$2(recoveredExpressionCapture_1168_0, recoveredExpressionCapture_1168_1, recoveredExpressionCapture_1168_2, recoveredExpressionCapture_1168_3, recoveredExpressionCapture_1168_4, recoveredExpressionCapture_1168_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1168_0), ((java.lang.Integer)recoveredExpressionArgument_1168_1).intValue())));
            }

            return;
         }

         var15.endReplaceGroup();
         Uri var18 = var1.getUri();
         String var20 = var1.getMimeType(var16);
         if (ContentTypeExtensionKt.isImage(var20)) {
            var15.startReplaceGroup(1216691656);
            ComposerKt.sourceInformation(var15, "84@3477L188");
            ZoomableImageKt.ZoomableImage(var0, var1, var2, var3, var15, 14 & var7 | 112 & var7 | 896 & var7 | 7168 & var7, 0);
            var15.endReplaceGroup();
         } else if (ContentTypeExtensionKt.isVideo(var20) || ContentTypeExtensionKt.isAudio(var20)) {
            var15.startReplaceGroup(1216951033);
            ComposerKt.sourceInformation(var15, "93@3743L43");
            VideoPlayer(var0, var18, var15, 14 & var7, 0);
            var15.endReplaceGroup();
         } else if (ContentTypeExtensionKt.isPdf(var20)) {
            var15.startReplaceGroup(1217047288);
            ComposerKt.sourceInformation(var15, "97@3840L44");
            PdfPreview(var0, var1, var15, 14 & var7 | 112 & var7, 0);
            var15.endReplaceGroup();
         } else {
            var15.startReplaceGroup(1217132693);
            ComposerKt.sourceInformation(var15, "101@3926L47");
            DocumentPreview(var0, var18, false, null, var15, 14 & var7, 12);
            var15.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var25 = var15.endRestartGroup();
      if (var25 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1169_0 = var0;
         final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1169_1 = var1;
         final boolean recoveredExpressionCapture_1169_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1169_3 = var3;
         final int recoveredExpressionCapture_1169_4 = var5;
         final int recoveredExpressionCapture_1169_5 = var6;
         var25.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1169_0, recoveredExpressionArgument_1169_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.PreviewUri$lambda$3(recoveredExpressionCapture_1169_0, recoveredExpressionCapture_1169_1, recoveredExpressionCapture_1169_2, recoveredExpressionCapture_1169_3, recoveredExpressionCapture_1169_4, recoveredExpressionCapture_1169_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1169_0), ((java.lang.Integer)recoveredExpressionArgument_1169_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ThumbnailPreview(
      @Nullable Modifier var0, @Nullable ContentScale var1, @NotNull IntercomPreviewFile var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var2, "file");
      Composer var16 = var3.startRestartGroup(-939708445);
      ComposerKt.sourceInformation(var16, "C(ThumbnailPreview)P(2)112@4188L7:PreviewUri.kt#hhbc0c");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var16.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var16.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var16.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var16.getSkipping()) {
         var16.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 2) != 0) {
            var1 = ContentScale.Companion.getFit();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-939708445, var6, -1, "io.intercom.android.sdk.m5.preview.ui.ThumbnailPreview (PreviewUri.kt:111)");
         }

         CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var16.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var16);
         Context var7 = (Context)var11;
         var16.startReplaceGroup(-701010211);
         ComposerKt.sourceInformation(var16, "116@4399L101");
         if (!IntercomPreviewArgsKt.hasSafeScheme(var2)) {
            Uri var10001 = Uri.EMPTY;
            Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
            DocumentPreview(var0, var10001, false, var1, var16, 384 | 14 & var6 | 7168 & var6 << 6, 0);
            var16.endReplaceGroup();
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var10000 = var16.endRestartGroup();
            if (var10000 != null) {
               final androidx.compose.ui.Modifier recoveredExpressionCapture_1170_0 = var0;
               final androidx.compose.ui.layout.ContentScale recoveredExpressionCapture_1170_1 = var1;
               final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1170_2 = var2;
               final int recoveredExpressionCapture_1170_3 = var4;
               final int recoveredExpressionCapture_1170_4 = var5;
               var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1170_0, recoveredExpressionArgument_1170_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.ThumbnailPreview$lambda$4(recoveredExpressionCapture_1170_0, recoveredExpressionCapture_1170_1, recoveredExpressionCapture_1170_2, recoveredExpressionCapture_1170_3, recoveredExpressionCapture_1170_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1170_0), ((java.lang.Integer)recoveredExpressionArgument_1170_1).intValue())));
            }

            return;
         }

         var16.endReplaceGroup();
         String var17 = var2.getMimeType(var7);
         if (!ContentTypeExtensionKt.isImage(var17) && !ContentTypeExtensionKt.isVideo(var17) && !ContentTypeExtensionKt.isPdf(var17)) {
            var16.startReplaceGroup(-255794215);
            ComposerKt.sourceInformation(var16, "133@5066L179");
            DocumentPreview(var0, var2.getUri(), false, var1, var16, 384 | 14 & var6 | 7168 & var6 << 6, 0);
            var16.endReplaceGroup();
         } else {
            var16.startReplaceGroup(-256187388);
            ComposerKt.sourceInformation(var16, "*127@4931L7,122@4664L360");
            Modifier var18 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
            ImageLoader var19 = IntercomImageLoaderKt.getImageLoader(var7);
            CompositionLocal var12 = AndroidCompositionLocals_androidKt.getLocalContext();
            int $changed$ivx = 0;
            int $i$f$getCurrentx = 0;
            ComposerKt.sourceInformationMarkerStart(var16, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var15 = var16.consume(var12);
            ComposerKt.sourceInformationMarkerEnd(var16);
            Builder var20 = new Builder((Context)var15).data(var2.getUri());
            Builder var21 = var20;
            int var22 = 0;
            var21.crossfade(true);
            AsyncImageKt.recovered$b6a46e909c1fcc39(
               var20.build(), "Image", var19, var18, null, null, null, var1, 0.0F, null, 0, false, null, var16, 48 | 29360128 & var6 << 18, 0, 8048
            );
            var16.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var23 = var16.endRestartGroup();
      if (var23 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1171_0 = var0;
         final androidx.compose.ui.layout.ContentScale recoveredExpressionCapture_1171_1 = var1;
         final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1171_2 = var2;
         final int recoveredExpressionCapture_1171_3 = var4;
         final int recoveredExpressionCapture_1171_4 = var5;
         var23.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1171_0, recoveredExpressionArgument_1171_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.ThumbnailPreview$lambda$6(recoveredExpressionCapture_1171_0, recoveredExpressionCapture_1171_1, recoveredExpressionCapture_1171_2, recoveredExpressionCapture_1171_3, recoveredExpressionCapture_1171_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1171_0), ((java.lang.Integer)recoveredExpressionArgument_1171_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void VideoPlayer(Modifier var0, Uri var1, Composer var2, int var3, int var4) {
      Composer var19 = var2.startRestartGroup(554501913);
      ComposerKt.sourceInformation(
         var19, "C(VideoPlayer)148@5386L7,149@5460L7,149@5419L49,161@5796L287,159@5736L354,175@6214L532,173@6160L593:PreviewUri.kt#hhbc0c"
      );
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var19.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var19.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var19.getSkipping()) {
         var19.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(554501913, var5, -1, "io.intercom.android.sdk.m5.preview.ui.VideoPlayer (PreviewUri.kt:147)");
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var19.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var19);
         Context var6 = (Context)var10;
         CompositionLocal var21 = AndroidCompositionLocals_androidKt.getLocalLifecycleOwner();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var19, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var19.consume(var21);
         ComposerKt.sourceInformationMarkerEnd(var19);
         State var20 = SnapshotStateKt.rememberUpdatedState(var11, var19, 0);
         androidx.media3.common.MediaItem.Builder var26 = MediaItem.fromUri(var1).buildUpon();
         androidx.media3.common.MediaItem.Builder var29 = var26;
         int var12 = 0;
         var29.setMediaId(String.valueOf(var1.hashCode()));
         var29.setTag(var1);
         MediaItem var10000 = var26.build();
         Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
         MediaItem var22 = var10000;
         ExoPlayer var30 = new androidx.media3.exoplayer.ExoPlayer.Builder(var6).build();
         ExoPlayer var33 = var30;
         int var13 = 0;
         var33.setMediaItem(var22);
         var33.prepare();
         Intrinsics.checkNotNullExpressionValue(var30, "apply(...)");
         ExoPlayer var24 = var30;
         var19.startReplaceGroup(-1887717927);
         ComposerKt.sourceInformation(var19, "CC(remember):PreviewUri.kt#9igjgp");
         Composer var31 = var19;
         boolean var34 = var19.changedInstance(var24);
         int var36 = 0;
         Object var14 = var31.rememberedValue();
         int var15 = 0;
         Object var42;
         if (!var34 && var14 != Composer.Companion.getEmpty()) {
            var42 = var14;
         } else {
            int var16 = 0;
            final androidx.media3.exoplayer.ExoPlayer recoveredExpressionCapture_1172_0 = var24;
            Object var17 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1172_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.VideoPlayer$lambda$11$lambda$10(recoveredExpressionCapture_1172_0, (android.content.Context) ((android.content.Context)recoveredExpressionArgument_1172_0)));
            var31.updateRememberedValue(var17);
            var42 = var17;
         }

         Function1 var27 = (Function1)var42;
         var19.endReplaceGroup();
         AndroidView_androidKt.AndroidView(var27, var0, null, var19, 112 & var5 << 3, 4);
         String var43 = "";
         var19.startReplaceGroup(-1887704306);
         ComposerKt.sourceInformation(var19, "CC(remember):PreviewUri.kt#9igjgp");
         Composer var32 = var19;
         boolean var35 = var19.changedInstance(var24) | var19.changed(var20);
         int $i$f$cachex = 0;
         Object it$ivx = var32.rememberedValue();
         int var39/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var45;
         if (!var35 && it$ivx != Composer.Companion.getEmpty()) {
            var45 = it$ivx;
         } else {
            String var18 = "";
            int var40 = 0;
            final androidx.media3.exoplayer.ExoPlayer recoveredExpressionCapture_1173_0 = var24;
            final androidx.compose.runtime.State recoveredExpressionCapture_1173_1 = var20;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1173_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.VideoPlayer$lambda$15$lambda$14(recoveredExpressionCapture_1173_0, recoveredExpressionCapture_1173_1, (androidx.compose.runtime.DisposableEffectScope) ((androidx.compose.runtime.DisposableEffectScope)recoveredExpressionArgument_1173_0)));
            var43 = var18;
            Object var41 = var10001;
            var32.updateRememberedValue(var41);
            var45 = var41;
         }

         Function1 var28 = (Function1)var45;
         var19.endReplaceGroup();
         EffectsKt.DisposableEffect(var43, var28, var19, 6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var44 = var19.endRestartGroup();
      if (var44 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1174_0 = var0;
         final android.net.Uri recoveredExpressionCapture_1174_1 = var1;
         final int recoveredExpressionCapture_1174_2 = var3;
         final int recoveredExpressionCapture_1174_3 = var4;
         var44.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1174_0, recoveredExpressionArgument_1174_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.VideoPlayer$lambda$16(recoveredExpressionCapture_1174_0, recoveredExpressionCapture_1174_1, recoveredExpressionCapture_1174_2, recoveredExpressionCapture_1174_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1174_0), ((java.lang.Integer)recoveredExpressionArgument_1174_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DocumentPreview(Modifier var0, Uri var1, boolean var2, ContentScale var3, Composer var4, int var5, int var6) {
      Composer var13 = var4.startRestartGroup(-290699575);
      ComposerKt.sourceInformation(var13, "C(DocumentPreview)P(1,3,2)204@6953L7,207@7034L1662,205@6965L1731:PreviewUri.kt#hhbc0c");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var13.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var13.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var13.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var13.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var13.getSkipping()) {
         var13.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = true;
         }

         if ((var6 & 8) != 0) {
            var3 = ContentScale.Companion.getFit();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-290699575, var7, -1, "io.intercom.android.sdk.m5.preview.ui.DocumentPreview (PreviewUri.kt:203)");
         }

         CompositionLocal var9 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var10 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var12 = var13.consume(var9);
         ComposerKt.sourceInformationMarkerEnd(var13);
         Context var8 = (Context)var12;
         BoxWithConstraintsKt.BoxWithConstraints(
            SizeKt.fillMaxSize$default(var0, 0.0F, 1, null),
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(-561669217, true, new PreviewUriKt$DocumentPreview$1(var8, var1, var3, var2), var13, 54),
            var13,
            3072,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var13.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1164_0 = var0;
         final android.net.Uri recoveredExpressionCapture_1164_1 = var1;
         final boolean recoveredExpressionCapture_1164_2 = var2;
         final androidx.compose.ui.layout.ContentScale recoveredExpressionCapture_1164_3 = var3;
         final int recoveredExpressionCapture_1164_4 = var5;
         final int recoveredExpressionCapture_1164_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1164_0, recoveredExpressionArgument_1164_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.DocumentPreview$lambda$17(recoveredExpressionCapture_1164_0, recoveredExpressionCapture_1164_1, recoveredExpressionCapture_1164_2, recoveredExpressionCapture_1164_3, recoveredExpressionCapture_1164_4, recoveredExpressionCapture_1164_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1164_0), ((java.lang.Integer)recoveredExpressionArgument_1164_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PdfPreview(Modifier var0, IntercomPreviewFile var1, Composer var2, int var3, int var4) {
      Composer var44 = var2.startRestartGroup(-2135159466);
      ComposerKt.sourceInformation(var44, "C(PdfPreview)P(1)252@8821L25:PreviewUri.kt#hhbc0c");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var44.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var44.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var44.getSkipping()) {
         var44.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2135159466, var5, -1, "io.intercom.android.sdk.m5.preview.ui.PdfPreview (PreviewUri.kt:251)");
         }

         PdfLoadState var6 = (PdfLoadState)loadPdfState(var1, var44, 14 & var5 >> 3).getValue();
         if (var6 instanceof io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Loading) {
            var44.startReplaceGroup(1376783257);
            ComposerKt.sourceInformation(var44, "254@8905L217");
            Modifier var45 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
            Alignment var46 = Alignment.Companion.getCenter();
            byte var48 = 48;
            int var49 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var44, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            boolean var47 = false;
            MeasurePolicy var50 = BoxKt.maybeCachedBoxMeasurePolicy(var46, var47);
            Modifier var51 = var45;
            MeasurePolicy var52 = var50;
            Composer var15 = var44;
            int var16 = 112 & var48 << 3;
            int var17 = 0;
            ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
            CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
            Modifier var20 = ComposedModifierKt.materializeModifier(var15, var51);
            Function0 var21 = ComposeUiNode.Companion.getConstructor();
            Composer var22 = var15;
            int var23 = 6 | 896 & var16 << 6;
            int var24 = 0;
            ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var22.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var22.startReusableNode();
            if (var22.getInserting()) {
               var22.createNode(var21);
            } else {
               var22.useNode();
            }

            Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
            int var26 = 0;
            Updater.recovered$89682b199a142880(var25, var52, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
            int var27 = 14 & var23 >> 6;
            Composer var28 = var22;
            int var29 = 0;
            ComposerKt.sourceInformationMarkerStart(var28, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var30 = 6 | 112 & var48 >> 6;
            Composer var31 = var28;
            BoxScope var32 = BoxScopeInstance.INSTANCE;
            int var33 = 0;
            ComposerKt.sourceInformationMarkerStart(var31, -2022645650, "C258@9094L6,258@9046L62:PreviewUri.kt#hhbc0c");
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(
               null, IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).recovered$202acdf3dbb5c020(), 0.0F, 0L, 0, 0.0F, var31, 0, 61
            );
            ComposerKt.sourceInformationMarkerEnd(var31);
            ComposerKt.sourceInformationMarkerEnd(var28);
            var22.endNode();
            ComposerKt.sourceInformationMarkerEnd(var22);
            ComposerKt.sourceInformationMarkerEnd(var15);
            ComposerKt.sourceInformationMarkerEnd(var44);
            var44.endReplaceGroup();
         } else if (!(var6 instanceof io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Success)) {
            if (!(var6 instanceof io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Error)) {
               var44.startReplaceGroup(2122620189);
               var44.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var44.startReplaceGroup(1377626302);
            ComposerKt.sourceInformation(var44, "280@9760L52");
            DocumentPreview(var0, var1.getUri(), false, null, var44, 14 & var5, 12);
            var44.endReplaceGroup();
         } else {
            var44.startReplaceGroup(1377068364);
            ComposerKt.sourceInformation(var44, "265@9260L441,263@9183L518");
            Modifier var10000 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
            Object var10001 = null;
            Object var10002 = null;
            boolean var10003 = false;
            Object var10004 = null;
            Object var10005 = null;
            Object var10006 = null;
            boolean var10007 = false;
            Object var10008 = null;
            var44.startReplaceGroup(2122634353);
            ComposerKt.sourceInformation(var44, "CC(remember):PreviewUri.kt#9igjgp");
            Composer var8 = var44;
            boolean var9 = var44.changed(var6);
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10009;
            if (!var9 && var11 != Composer.Companion.getEmpty()) {
               var10009 = var11;
            } else {
               Object var42 = null;
               boolean var41 = false;
               Object var40 = null;
               Object var39 = null;
               Object var38 = null;
               boolean var37 = false;
               Object var36 = null;
               Object var35 = null;
               Modifier var34 = var10000;
               int var13 = 0;
               final io.intercom.android.sdk.m5.preview.ui.PdfLoadState recoveredExpressionCapture_1165_0 = var6;
               Function1 var43 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1165_0) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.PdfPreview$lambda$21$lambda$20(recoveredExpressionCapture_1165_0, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_1165_0)));
               var10000 = var34;
               var10001 = var35;
               var10002 = var36;
               var10003 = var37;
               var10004 = var38;
               var10005 = var39;
               var10006 = var40;
               var10007 = var41;
               var10008 = var42;
               Object var14 = var43;
               var8.updateRememberedValue(var14);
               var10009 = var14;
            }

            Function1 var7 = (Function1)var10009;
            var44.endReplaceGroup();
            LazyDslKt.LazyColumn(
               var10000,
               (LazyListState)var10001,
               (PaddingValues)var10002,
               var10003,
               (Vertical)var10004,
               (Horizontal)var10005,
               (FlingBehavior)var10006,
               var10007,
               (OverscrollEffect)var10008,
               var7,
               var44,
               0,
               510
            );
            var44.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var53 = var44.endRestartGroup();
      if (var53 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1166_0 = var0;
         final io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile recoveredExpressionCapture_1166_1 = var1;
         final int recoveredExpressionCapture_1166_2 = var3;
         final int recoveredExpressionCapture_1166_3 = var4;
         var53.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1166_0, recoveredExpressionArgument_1166_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.PdfPreview$lambda$22(recoveredExpressionCapture_1166_0, recoveredExpressionCapture_1166_1, recoveredExpressionCapture_1166_2, recoveredExpressionCapture_1166_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1166_0), ((java.lang.Integer)recoveredExpressionArgument_1166_1).intValue())));
      }
   }

   @Composable
   private static final State loadPdfState(IntercomPreviewFile var0, Composer var1, int var2) {
      var1.startReplaceGroup(-782364509);
      ComposerKt.sourceInformation(var1, "C(loadPdfState)289@9957L7,291@10047L1092,291@9977L1162:PreviewUri.kt#hhbc0c");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-782364509, var2, -1, "io.intercom.android.sdk.m5.preview.ui.loadPdfState (PreviewUri.kt:288)");
      }

      CompositionLocal var4 = AndroidCompositionLocals_androidKt.getLocalContext();
      int var5 = 0;
      int var6 = 0;
      ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var7 = var1.consume(var4);
      ComposerKt.sourceInformationMarkerEnd(var1);
      Context var3 = (Context)var7;
      io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Loading var10000 = io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Loading.INSTANCE;
      IntercomPreviewFile var10001 = var0;
      var1.startReplaceGroup(1470587979);
      ComposerKt.sourceInformation(var1, "CC(remember):PreviewUri.kt#9igjgp");
      Composer var18 = var1;
      boolean var19 = var1.changedInstance(var3) | var1.changedInstance(var0);
      int var8 = 0;
      Object var9 = var18.rememberedValue();
      int var10 = 0;
      Object var10002;
      if (!var19 && var9 != Composer.Companion.getEmpty()) {
         var10002 = var9;
      } else {
         IntercomPreviewFile var14 = var0;
         io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Loading var13 = var10000;
         int var11 = 0;
         Function2 var15 = new PreviewUriKt$loadPdfState$1$1(var3, var0, null);
         var10000 = var13;
         var10001 = var14;
         Object var12 = var15;
         var18.updateRememberedValue(var12);
         var10002 = var12;
      }

      Function2 var17 = (Function2)var10002;
      var1.endReplaceGroup();
      State var16 = SnapshotStateKt.produceState(var10000, var10001, var17, var1, 6 | 112 & var2 << 3);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var16;
   }

   private static final Unit PreviewUri$lambda$1$lambda$0(boolean var0) {
      return Unit.INSTANCE;
   }

   private static final Unit PreviewUri$lambda$2(
      Modifier var0, IntercomPreviewFile var1, boolean var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      PreviewUri(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewUri$lambda$3(
      Modifier var0, IntercomPreviewFile var1, boolean var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      PreviewUri(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit ThumbnailPreview$lambda$4(Modifier var0, ContentScale var1, IntercomPreviewFile var2, int var3, int var4, Composer var5, int var6) {
      ThumbnailPreview(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ThumbnailPreview$lambda$6(Modifier var0, ContentScale var1, IntercomPreviewFile var2, int var3, int var4, Composer var5, int var6) {
      ThumbnailPreview(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final PlayerView VideoPlayer$lambda$11$lambda$10(ExoPlayer var0, Context var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      PlayerView var2 = new PlayerView(var1);
      PlayerView var3 = var2;
      int var4 = 0;
      var3.setPlayer(var0);
      var3.setLayoutParams(new LayoutParams(-1, -1));
      return var2;
   }

   private static final void VideoPlayer$lambda$15$lambda$14$lambda$12(ExoPlayer var0, LifecycleOwner var1, Event var2) {
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      Intrinsics.checkNotNullParameter(var2, "event");
      if (PreviewUriKt$WhenMappings.$EnumSwitchMapping$0[var2.ordinal()] == 1) {
         var0.pause();
      }
   }

   private static final DisposableEffectResult VideoPlayer$lambda$15$lambda$14(ExoPlayer var0, State var1, DisposableEffectScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$DisposableEffect");
      var0.play();
      final androidx.media3.exoplayer.ExoPlayer recoveredExpressionCapture_1175_0 = var0;
      LifecycleEventObserver var3 = ((androidx.lifecycle.LifecycleEventObserver) (recoveredExpressionArgument_1175_0, recoveredExpressionArgument_1175_1) -> io.intercom.android.sdk.m5.preview.ui.PreviewUriKt.VideoPlayer$lambda$15$lambda$14$lambda$12(recoveredExpressionCapture_1175_0, (androidx.lifecycle.LifecycleOwner) (recoveredExpressionArgument_1175_0), (androidx.lifecycle.Lifecycle.Event) (recoveredExpressionArgument_1175_1)));
      Lifecycle var4 = ((LifecycleOwner)var1.getValue()).getLifecycle();
      var4.addObserver(var3);
      int var6 = 0;
      return new PreviewUriKt$VideoPlayer$lambda$15$lambda$14$RecoveredInnere3b0c44298fc1c14$inlined$onDispose$1(var4, var3, var0);
   }

   private static final Unit VideoPlayer$lambda$16(Modifier var0, Uri var1, int var2, int var3, Composer var4, int var5) {
      VideoPlayer(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit DocumentPreview$lambda$17(Modifier var0, Uri var1, boolean var2, ContentScale var3, int var4, int var5, Composer var6, int var7) {
      DocumentPreview(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit PdfPreview$lambda$21$lambda$20(PdfLoadState var0, LazyListScope var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$LazyColumn");
      LazyListScope var2 = var1;
      List var3 = ((io.intercom.android.sdk.m5.preview.ui.PdfLoadState$Success)var0).getBitmaps();
      Function1 var4 = null;
      Function1 var5 = PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$1.INSTANCE;
      int var6 = 0;
      var2.items(
         var3.size(),
         null,
         new PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$3(var5, var3),
         ComposableLambdaKt.composableLambdaInstance(
            802480018,
            true,
            new PreviewUriKt$PdfPreview$lambda$21$lambda$20$RecoveredInnere3b0c44298fc1c14$inlined$items$RecoveredInner37a8eec1ce19687d$4(var3)
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit PdfPreview$lambda$22(Modifier var0, IntercomPreviewFile var1, int var2, int var3, Composer var4, int var5) {
      PdfPreview(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }
}
