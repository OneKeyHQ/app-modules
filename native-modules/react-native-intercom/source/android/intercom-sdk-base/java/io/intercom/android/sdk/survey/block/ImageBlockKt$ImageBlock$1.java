package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.FocusableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.ColorMatrix;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.AsyncImageKt;
import coil.compose.AsyncImagePainter.State;
import coil.compose.AsyncImagePainter.State.Empty;
import coil.compose.AsyncImagePainter.State.Loading;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import com.google.accompanist.placeholder.PlaceholderHighlight;
import com.google.accompanist.placeholder.PlaceholderHighlightKt;
import com.google.accompanist.placeholder.PlaceholderKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.preview.data.DownloadState;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$Companion;
import io.intercom.android.sdk.m5.preview.util.MimeTypeResolverKt;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.extension.ModifierExtensionsKt;
import io.intercom.android.sdk.utilities.ImageUtils;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nImageBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ImageBlock.kt\nio/intercom/android/sdk/survey/block/ImageBlockKt$ImageBlock$1\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,198:1\n75#2:199\n75#2:205\n75#2:206\n75#2:207\n1#3:200\n118#4:201\n118#4:202\n166#4:204\n66#5:203\n1047#6,6:208\n1047#6,6:214\n1047#6,6:220\n1047#6,6:226\n*S KotlinDebug\n*F\n+ 1 ImageBlock.kt\nio/intercom/android/sdk/survey/block/ImageBlockKt$ImageBlock$1\n*L\n76#1:199\n116#1:205\n126#1:206\n137#1:207\n81#1:201\n95#1:202\n111#1:204\n101#1:203\n141#1:208,6\n144#1:214,6\n154#1:220,6\n139#1:226,6\n*E\n"
)
final class ImageBlockKt$ImageBlock$1 implements Function3 {
   // $VF: synthetic field
   final Block $block;
   // $VF: synthetic field
   final ImageRenderType $renderType;
   // $VF: synthetic field
   final String $cacheKey;
   // $VF: synthetic field
   final Uri $dataUri;
   // $VF: synthetic field
   final long $backgroundColor;
   // $VF: synthetic field
   final Function1 $onClick;
   // $VF: synthetic field
   final MutableState $state$delegate;

   ImageBlockKt$ImageBlock$1(Block var1, ImageRenderType var2, String var3, Uri var4, long var5, Function1 var7, MutableState var8) {
      this.$block = var1;
      this.$renderType = var2;
      this.$cacheKey = var3;
      this.$dataUri = var4;
      this.$backgroundColor = var5;
      this.$onClick = var7;
      this.$state$delegate = var8;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(
         var2,
         "C75@3239L7,115@5118L7,120@5251L56,121@5336L49,122@5415L45,123@5490L69,125@5634L7,136@6120L7,140@6407L94,143@6597L86,153@7100L1920,138@6263L14,124@5568L3684:ImageBlock.kt#jxhu3x"
      );
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(491541600, var4, -1, "io.intercom.android.sdk.survey.block.ImageBlock.<anonymous> (ImageBlock.kt:75)");
         }

         CompositionLocal var6 = CompositionLocalsKt.getLocalDensity();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var2.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Density var5 = (Density)var9;
         boolean var37 = this.$block.getHeight() > 0 && this.$block.getWidth() > 0;
         Pair var132;
         if (this.$renderType == ImageRenderType.WITH_MAX_SIZE && var37) {
            Density var47 = var5;
            int var52 = 0;
            int var58 = 268;
            int var64 = 0;
            float var42 = var47.recovered$245d7c913f591981(Dp.recovered$95b3d103663bf060(var58));
            if (this.$block.getWidth() > this.$block.getHeight()) {
               float var48 = 1.0F * this.$block.getHeight() / this.$block.getWidth();
               Density var53 = var5;
               int var59 = 0;
               var132 = TuplesKt.to(
                  Dp.recovered$89bc2ea8129b5eb8(var53.recovered$7835939063ae77d1(var42)),
                  Dp.recovered$89bc2ea8129b5eb8(var53.recovered$7835939063ae77d1(var42 * var48))
               );
            } else {
               float var49 = 1.0F * this.$block.getWidth() / this.$block.getHeight();
               Density var54 = var5;
               int var60 = 0;
               var132 = TuplesKt.to(
                  Dp.recovered$89bc2ea8129b5eb8(var54.recovered$7835939063ae77d1(var42 * var49)),
                  Dp.recovered$89bc2ea8129b5eb8(var54.recovered$7835939063ae77d1(var42))
               );
            }
         } else if (this.$renderType == ImageRenderType.WITH_MAX_SIZE) {
            int var39 = 268;
            int var44 = 0;
            Dp var10000 = Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var39));
            int $this$dp$ivx = 268;
            int $i$f$getDpx = 0;
            var132 = TuplesKt.to(var10000, Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060($this$dp$ivx)));
         } else {
            float var41 = var1.recovered$4e84a3b22c31a649();
            float var133;
            if (this.$renderType == ImageRenderType.FULL) {
               var133 = var1.recovered$1760a9074021b1b8();
            } else {
               float var10 = 1.5F;
               int var11 = 0;
               var133 = Dp.recovered$95b3d103663bf060(var41 * var10);
            }

            float var46 = var133;
            float var134;
            if (this.$block.getHeight() <= 0) {
               var134 = var46;
            } else {
               Block var56 = this.$block;
               Density var12 = var5;
               int var13 = 0;
               float var57 = var12.recovered$555eceb1d39659cf(
                  ImageUtils.getAspectHeight((int)var12.recovered$245d7c913f591981(var41), ImageUtils.getAspectRatio(var56.getWidth(), var56.getHeight()))
               );
               int var63 = 0;
               var134 = Dp.recovered$95b3d103663bf060(RangesKt.coerceAtMost(var57, var46));
            }

            float var51 = var134;
            var132 = TuplesKt.to(Dp.recovered$89bc2ea8129b5eb8(var41), Dp.recovered$89bc2ea8129b5eb8(var51));
         }

         Pair var38 = var132;
         float var43 = ((Dp)var38.component1()).recovered$3537cef520440dc0();
         float var50 = ((Dp)var38.component2()).recovered$3537cef520440dc0();
         CompositionLocal var61 = AndroidCompositionLocals_androidKt.getLocalView();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var14 = var2.consume(var61);
         ComposerKt.sourceInformationMarkerEnd(var2);
         View var55 = (View)var14;
         float[] var62 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         ColorMatrix.recovered$e4d8f44c8df66f0d(var62, 0.0F);
         String var66 = StringResources_androidKt.stringResource(string.intercom_permission_denied, var2, 0);
         String var68 = StringResources_androidKt.stringResource(string.intercom_file_saved, var2, 0);
         String var69 = StringResources_androidKt.stringResource(string.intercom_saving, var2, 0);
         String var15 = StringResources_androidKt.stringResource(string.intercom_something_went_wrong_try_again, var2, 0);
         CompositionLocal var17 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivxx = 0;
         int $i$f$getCurrentxx = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var20 = var2.consume(var17);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Builder var135 = new Builder((Context)var20).memoryCacheKey(this.$cacheKey).diskCacheKey(this.$cacheKey).data(this.$dataUri);
         Density var70 = var5;
         Builder var31 = var135;
         int var73 = 0;
         int var32 = (int)var70.recovered$245d7c913f591981(var43);
         Density var71 = var5;
         int var120 = var32;
         Builder var115 = var31;
         int var74 = 0;
         int var33 = (int)var71.recovered$245d7c913f591981(var50);
         ImageRequest var16 = var115.size(var120, var33).crossfade(true).error(drawable.intercom_image_load_failed).build();
         CompositionLocal var75 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivxxx = 0;
         int $i$f$getCurrentxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var21 = var2.consume(var75);
         ComposerKt.sourceInformationMarkerEnd(var2);
         ImageLoader var72 = IntercomImageLoaderKt.getImageLoader((Context)var21);
         var2.startReplaceGroup(1738647987);
         ComposerKt.sourceInformation(var2, "*137@6184L53");
         CharSequence var81 = this.$block.getText();
         CharSequence var136;
         if (StringsKt.isBlank(var81)) {
            int var85 = 0;
            var136 = StringResources_androidKt.stringResource(string.intercom_image_attached, var2, 0);
         } else {
            var136 = var81;
         }

         String var78 = (String)var136;
         var2.endReplaceGroup();
         String var76 = var78;
         Modifier var137 = Modifier.Companion;
         boolean var10001 = this.$renderType == ImageRenderType.WITH_MAX_SIZE && !var37;
         var2.startReplaceGroup(1738655472);
         ComposerKt.sourceInformation(var2, "CC(remember):ImageBlock.kt#9igjgp");
         Composer var22 = var2;
         boolean var23 = var2.changed(var43) | var2.changed(var50);
         int var24 = 0;
         Object var25 = var22.rememberedValue();
         int var26 = 0;
         Object var10002;
         if (!var23 && var25 != Composer.Companion.getEmpty()) {
            var10002 = var25;
         } else {
            boolean var121 = var10001;
            Modifier var116 = var137;
            int var27 = 0;
            final float recoveredExpressionCapture_1271_0 = var43;
            final float recoveredExpressionCapture_1271_1 = var50;
            Function1 var125 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1271_0) -> io.intercom.android.sdk.survey.block.ImageBlockKt$ImageBlock$1.invoke$lambda$8$lambda$7(recoveredExpressionCapture_1271_0, recoveredExpressionCapture_1271_1, (androidx.compose.ui.Modifier) ((androidx.compose.ui.Modifier)recoveredExpressionArgument_1271_0)));
            var137 = var116;
            var10001 = var121;
            Object var28 = var125;
            var22.updateRememberedValue(var28);
            var10002 = var28;
         }

         Function1 var86 = (Function1)var10002;
         var2.endReplaceGroup();
         Modifier var138 = ModifierExtensionsKt.ifTrue(var137, var10001, var86);
         boolean var141 = this.$renderType != ImageRenderType.WITH_MAX_SIZE || var37;
         var2.startReplaceGroup(1738661544);
         ComposerKt.sourceInformation(var2, "CC(remember):ImageBlock.kt#9igjgp");
         Composer var91 = var2;
         boolean invalid$ivx = var2.changed(var43) | var2.changed(var50);
         int $i$f$cachex = 0;
         Object it$ivx = var91.rememberedValue();
         int var105/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var144;
         if (!invalid$ivx && it$ivx != Composer.Companion.getEmpty()) {
            var144 = it$ivx;
         } else {
            boolean var122 = var141;
            Modifier var117 = var138;
            int var108 = 0;
            final float recoveredExpressionCapture_1272_0 = var43;
            final float recoveredExpressionCapture_1272_1 = var50;
            Function1 var126 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1272_0) -> io.intercom.android.sdk.survey.block.ImageBlockKt$ImageBlock$1.invoke$lambda$10$lambda$9(recoveredExpressionCapture_1272_0, recoveredExpressionCapture_1272_1, (androidx.compose.ui.Modifier) ((androidx.compose.ui.Modifier)recoveredExpressionArgument_1272_0)));
            var138 = var117;
            var141 = var122;
            Object var111 = var126;
            var91.updateRememberedValue(var111);
            var144 = var111;
         }

         Function1 var87 = (Function1)var144;
         var2.endReplaceGroup();
         Modifier var82 = BackgroundKt.recovered$42a3174180b3fb7a(ModifierExtensionsKt.ifTrue(var138, var141, var87), this.$backgroundColor, null, 2, null);
         boolean var88 = ImageBlockKt.access$ImageBlock_V_9fs2A$lambda$1(this.$state$delegate) instanceof Empty
            || ImageBlockKt.access$ImageBlock_V_9fs2A$lambda$1(this.$state$delegate) instanceof Loading;
         PlaceholderHighlight var92 = PlaceholderHighlightKt.recovered$1645558cf4b2ff83(
            PlaceholderHighlight.Companion, ColorKt.Color(2499805183L), null, 0.0F, 6, null
         );
         long var96 = ColorKt.Color(869059788);
         Modifier var139 = FocusableKt.focusable$default(
            PlaceholderKt.recovered$e8fd26c47c3df1ec(var82, var88, var96, null, var92, null, null, 52, null), false, null, 3, null
         );
         boolean var142 = false;
         Object var145 = null;
         Object var10003 = null;
         Object var10004 = null;
         var2.startReplaceGroup(1738679474);
         ComposerKt.sourceInformation(var2, "CC(remember):ImageBlock.kt#9igjgp");
         Composer var89 = var2;
         boolean var93 = var2.changed(this.$onClick)
            | var2.changedInstance(this.$block)
            | var2.changedInstance(var55)
            | var2.changed(var69)
            | var2.changed(var68)
            | var2.changed(var15)
            | var2.changed(var66);
         Function1 var97 = this.$onClick;
         Block var100 = this.$block;
         int $i$f$cachexx = 0;
         Object it$ivxx = var89.rememberedValue();
         int var109/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10005;
         if (!var93 && it$ivxx != Composer.Companion.getEmpty()) {
            var10005 = it$ivxx;
         } else {
            Object var35 = null;
            Object var34 = null;
            Object var127 = null;
            boolean var123 = false;
            Modifier var118 = var139;
            int var112 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1273_0 = var97;
            final io.intercom.android.sdk.blocks.lib.models.Block recoveredExpressionCapture_1273_1 = var100;
            final android.view.View recoveredExpressionCapture_1273_2 = var55;
            final java.lang.String recoveredExpressionCapture_1273_3 = var69;
            final java.lang.String recoveredExpressionCapture_1273_4 = var68;
            final java.lang.String recoveredExpressionCapture_1273_5 = var15;
            final java.lang.String recoveredExpressionCapture_1273_6 = var66;
            Function0 var36 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.block.ImageBlockKt$ImageBlock$1.invoke$lambda$13$lambda$12(recoveredExpressionCapture_1273_0, recoveredExpressionCapture_1273_1, recoveredExpressionCapture_1273_2, recoveredExpressionCapture_1273_3, recoveredExpressionCapture_1273_4, recoveredExpressionCapture_1273_5, recoveredExpressionCapture_1273_6));
            var139 = var118;
            var142 = var123;
            var145 = var127;
            var10003 = var34;
            var10004 = var35;
            Object var29 = var36;
            var89.updateRememberedValue(var29);
            var10005 = var29;
         }

         Function0 var83 = (Function0)var10005;
         var2.endReplaceGroup();
         Modifier var79 = ClickableKt.recovered$1c9660c41fd850dc(
            var139, var142, (String)var145, (Role)var10003, (MutableInteractionSource)var10004, var83, 15, null
         );
         ContentScale var84 = this.$renderType == ImageRenderType.CROPPED ? ContentScale.Companion.getCrop() : ContentScale.Companion.getFit();
         ColorFilter var90 = ImageBlockKt.access$getHasUri(this.$block) ? ColorFilter.Companion.recovered$056553a490480fb1(var62) : null;
         ImageRequest var140 = var16;
         String var143 = var76;
         ImageLoader var146 = var72;
         Modifier var147 = var79;
         Object var148 = null;
         var2.startReplaceGroup(1738650784);
         ComposerKt.sourceInformation(var2, "CC(remember):ImageBlock.kt#9igjgp");
         Composer var98 = var2;
         boolean var101 = false;
         MutableState var104 = this.$state$delegate;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var98.rememberedValue();
         int var113/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var149;
         if (it$ivxxx == Composer.Companion.getEmpty()) {
            Object var130 = null;
            Modifier var129 = var79;
            ImageLoader var128 = var72;
            String var124 = var76;
            ImageRequest var119 = var16;
            int var114 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1270_0 = var104;
            Function1 var131 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1270_0) -> io.intercom.android.sdk.survey.block.ImageBlockKt$ImageBlock$1.invoke$lambda$15$lambda$14(recoveredExpressionCapture_1270_0, (coil.compose.AsyncImagePainter.State) ((coil.compose.AsyncImagePainter.State)recoveredExpressionArgument_1270_0)));
            var140 = var119;
            var143 = var124;
            var146 = var128;
            var147 = var129;
            var148 = var130;
            Object var30 = var131;
            var98.updateRememberedValue(var30);
            var149 = var30;
         } else {
            var149 = it$ivxxx;
         }

         Function1 var94 = (Function1)var149;
         var2.endReplaceGroup();
         AsyncImageKt.recovered$b6a46e909c1fcc39(
            var140, var143, var146, var147, (Function1)var148, var94, null, var84, 0.0F, var90, 0, false, null, var2, 196608, 0, 7504
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Modifier invoke$lambda$8$lambda$7(float var0, float var1, Modifier var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$ifTrue");
      return SizeKt.recovered$40da1df10351f0ad(var2, 0.0F, 0.0F, var0, var1, 3, null);
   }

   private static final Modifier invoke$lambda$10$lambda$9(float var0, float var1, Modifier var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$ifTrue");
      return SizeKt.recovered$dd5685bd56c7f636(var2, var0, var1);
   }

   private static final Unit invoke$lambda$13$lambda$12(Function1 var0, Block var1, View var2, String var3, String var4, String var5, String var6) {
      if (var0 != null) {
         var0.invoke(var1);
      } else {
         String var10000 = var1.getLinkUrl();
         Intrinsics.checkNotNullExpressionValue(var10000, "getLinkUrl(...)");
         if (var10000.length() > 0) {
            LinkOpener.handleUrl(var1.getLinkUrl(), var2.getContext(), Injector.get().getApi());
         } else {
            Context var7 = var2.getContext();
            Context var8 = var7;
            int var9 = 0;
            io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$Companion var10001 = IntercomPreviewActivity.Companion;
            Intrinsics.checkNotNull(var8);
            io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$Companion var10005 = IntercomPreviewFile.Companion;
            String var10006 = var1.getUrl();
            Intrinsics.checkNotNullExpressionValue(var10006, "getUrl(...)");
            String var10007 = var1.getUrl();
            Intrinsics.checkNotNullExpressionValue(var10007, "getUrl(...)");
            List var11 = CollectionsKt.listOfNotNull(var10005.network(var10006, MimeTypeResolverKt.resolveImageMimeType$default(var10007, null, 2, null)));
            String var10010 = var1.getUrl();
            Intrinsics.checkNotNullExpressionValue(var10010, "getUrl(...)");
            boolean var10 = var10010.length() > 0;
            var8.startActivity(
               var10001.createIntent(var8, new IntercomPreviewArgs(var11, null, null, false, 0, new DownloadState(var10, var3, var4, var5, var6), 30, null))
                  .addFlags(268435456)
            );
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$14(MutableState var0, State var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      ImageBlockKt.access$ImageBlock_V_9fs2A$lambda$2(var0, var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
