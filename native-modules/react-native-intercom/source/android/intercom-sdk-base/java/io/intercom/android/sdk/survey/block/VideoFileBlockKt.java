package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.util.Patterns;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorMatrix;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.FixedScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import coil.compose.AsyncImagePainter;
import coil.compose.AsyncImagePainterKt;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.LinkOpener;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\u001a)\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005H\u0001¢\u0006\u0002\u0010\u0007\u001a\f\u0010\b\u001a\u00020\t*\u00020\u0005H\u0002¨\u0006\n",
   d2 = {
         "VideoFileBlock",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "videoUrl",
         "",
         "thumbnailUrl",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "isRemoteUrl",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nVideoFileBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VideoFileBlock.kt\nio/intercom/android/sdk/survey/block/VideoFileBlockKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,91:1\n75#2:92\n1047#3,6:93\n70#4:99\n67#4,9:100\n77#4:137\n81#5,6:109\n88#5,6:124\n96#5:136\n402#6,9:115\n411#6:130\n412#6,2:134\n118#7:131\n118#7:132\n118#7:133\n*S KotlinDebug\n*F\n+ 1 VideoFileBlock.kt\nio/intercom/android/sdk/survey/block/VideoFileBlockKt\n*L\n34#1:92\n47#1:93,6\n46#1:99\n46#1:100,9\n46#1:137\n46#1:109,6\n46#1:124,6\n46#1:136\n46#1:115,9\n46#1:130\n46#1:134,2\n58#1:131\n72#1:132\n83#1:133\n*E\n"
)
public final class VideoFileBlockKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void VideoFileBlock(@Nullable Modifier var0, @NotNull String var1, @Nullable String var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "videoUrl");
      var3 = var3.startRestartGroup(-224511788);
      ComposerKt.sourceInformation(var3, "C(VideoFileBlock)P(!1,2)33@1283L7,35@1310L355,46@1713L143,45@1670L1616:VideoFileBlock.kt#jxhu3x");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-224511788, var6, -1, "io.intercom.android.sdk.survey.block.VideoFileBlock (VideoFileBlock.kt:32)");
         }

         CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var3.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var7 = (Context)var11;
         Builder var10000 = new Builder(var7);
         CharSequence var49 = var2;
         Builder var50 = var10000.data(var49 == null || var49.length() == 0 ? var1 : var2);
         Builder var53 = var50;
         int var56 = 0;
         var53.crossfade(true);
         var53.error(drawable.intercom_image_load_failed);
         AsyncImagePainter var48 = AsyncImagePainterKt.recovered$8ecdef7ed4026761(
            var50.build(), IntercomImageLoaderKt.getImageLoader(var7), null, null, null, 0, null, var3, 0, 124
         );
         Modifier var76 = var0;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var3.startReplaceGroup(-354210906);
         ComposerKt.sourceInformation(var3, "CC(remember):VideoFileBlock.kt#9igjgp");
         Composer var54 = var3;
         boolean var57 = (var6 & 112) == 32 | var3.changedInstance(var7);
         int var12 = 0;
         Object var13 = var54.rememberedValue();
         int var14 = 0;
         Object var10005;
         if (!var57 && var13 != Composer.Companion.getEmpty()) {
            var10005 = var13;
         } else {
            Object var45 = null;
            Object var44 = null;
            Object var43 = null;
            boolean var42 = false;
            Modifier var41 = var0;
            int var15 = 0;
            final java.lang.String recoveredCapture_1316_0 = var1;
            final android.content.Context recoveredCapture_1316_1 = var7;
            Function0 var46 = ((kotlin.jvm.functions.Function0) () -> VideoFileBlockKt.VideoFileBlock$lambda$2$lambda$1(recoveredCapture_1316_0, recoveredCapture_1316_1));
            var76 = var41;
            var10001 = var42;
            var10002 = var43;
            var10003 = var44;
            var10004 = var45;
            Object var16 = var46;
            var54.updateRememberedValue(var16);
            var10005 = var16;
         }

         Function0 var51 = (Function0)var10005;
         var3.endReplaceGroup();
         Modifier var52 = ClickableKt.recovered$1c9660c41fd850dc(
            var76, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var51, 15, null
         );
         byte var59 = 0;
         var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var55 = Alignment.Companion.getTopStart();
         var57 = false;
         MeasurePolicy var61 = BoxKt.maybeCachedBoxMeasurePolicy(var55, var57);
         Modifier var62 = var52;
         MeasurePolicy var63 = var61;
         Composer var17 = var3;
         int var18 = 112 & var59 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var62);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var63, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var32 = 6 | 112 & var59 >> 6;
         Composer var33 = var30;
         BoxScope var34 = BoxScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 1591720527, "C58@2107L6,55@1963L512:VideoFileBlock.kt#jxhu3x");
         float[] var36 = ColorMatrix.recovered$426f31a700720feb(null, 1, null);
         ColorMatrix.recovered$e4d8f44c8df66f0d(var36, 0.0F);
         Modifier var80 = Modifier.Companion;
         int var37 = 640;
         int var38 = 0;
         float var81 = Dp.recovered$95b3d103663bf060(var37);
         var37 = 180;
         var38 = 0;
         Modifier var39 = var34.align(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.recovered$dd5685bd56c7f636(var80, var81, Dp.recovered$95b3d103663bf060(var37)),
               IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).recovered$f6e7a825ec69c94c(),
               null,
               2,
               null
            ),
            Alignment.Companion.getCenter()
         );
         ContentScale var65 = ContentScale.Companion.getCrop();
         Alignment var70 = Alignment.Companion.getCenter();
         ColorFilter var40 = !isRemoteUrl(var1) ? ColorFilter.Companion.recovered$056553a490480fb1(var36) : null;
         ImageKt.Image(var48, "Video Thumbnail", var39, var70, var65, 0.0F, var40, var33, 27696, 32);
         if (isRemoteUrl(var1)) {
            var33.startReplaceGroup(1592327134);
            ComposerKt.sourceInformation(var33, "72@2694L6,73@2771L52,76@2987L6,68@2527L502");
            Modifier var77 = var34.align(Modifier.Companion, Alignment.Companion.getCenter());
            var37 = 48;
            var38 = 0;
            var39 = BackgroundKt.recovered$60bc47fb440df483(
               SizeKt.recovered$111ef94c93e78c04(var77, Dp.recovered$95b3d103663bf060(var37)),
               IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               RoundedCornerShapeKt.RoundedCornerShape(50)
            );
            Painter var67 = PainterResources_androidKt.painterResource(drawable.intercom_play_arrow, var33, 0);
            FixedScale var72 = ContentScale.Companion.getNone();
            var40 = Companion.recovered$14c5668fff7aadde(
               ColorFilter.Companion, IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(), 0, 2, null
            );
            ImageKt.Image(var67, "Play Video", var39, null, var72, 0.0F, var40, var33, 24624 | Painter.$stable, 40);
            var33.endReplaceGroup();
         } else {
            var33.startReplaceGroup(1592845857);
            ComposerKt.sourceInformation(var33, "83@3239L6,79@3059L211");
            Modifier var78 = var34.align(Modifier.Companion, Alignment.Companion.getCenter());
            int $this$dp$ivx = 32;
            var37 = 0;
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(
               SizeKt.recovered$111ef94c93e78c04(var78, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
               IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               0.0F,
               0L,
               0,
               0.0F,
               var33,
               0,
               60
            );
            var33.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var79 = var3.endRestartGroup();
      if (var79 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1317_0 = var0;
         final java.lang.String recoveredCapture_1317_1 = var1;
         final java.lang.String recoveredCapture_1317_2 = var2;
         final int recoveredCapture_1317_3 = var4;
         final int recoveredCapture_1317_4 = var5;
         var79.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1317_0, recoveredArgument_1317_1) -> VideoFileBlockKt.VideoFileBlock$lambda$4(recoveredCapture_1317_0, recoveredCapture_1317_1, recoveredCapture_1317_2, recoveredCapture_1317_3, recoveredCapture_1317_4, (androidx.compose.runtime.Composer)recoveredArgument_1317_0, ((java.lang.Integer)recoveredArgument_1317_1).intValue())));
      }
   }

   private static final boolean isRemoteUrl(String var0) {
      return Patterns.WEB_URL.matcher(var0).matches();
   }

   private static final Unit VideoFileBlock$lambda$2$lambda$1(String var0, Context var1) {
      if (isRemoteUrl(var0)) {
         LinkOpener.handleUrl(var0, var1, Injector.get().getApi());
      }

      return Unit.INSTANCE;
   }

   private static final Unit VideoFileBlock$lambda$4(Modifier var0, String var1, String var2, int var3, int var4, Composer var5, int var6) {
      VideoFileBlock(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }
}
