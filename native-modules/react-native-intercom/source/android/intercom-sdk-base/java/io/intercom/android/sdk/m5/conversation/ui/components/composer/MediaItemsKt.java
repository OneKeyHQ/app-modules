package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.AsyncImageKt;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading;
import io.intercom.android.sdk.m5.conversation.ui.components.row.FileRowKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u001aa\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\f\u001aK\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u0006H\u0003¢\u0006\u0002\u0010\u0010\u001aU\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0003¢\u0006\u0002\u0010\u0013\u001aK\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u0006H\u0003¢\u0006\u0002\u0010\u0010\u001a#\u0010\u0015\u001a\u00020\u00012\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0003¢\u0006\u0004\b\u001a\u0010\u001b\u001a#\u0010\u001c\u001a\u00020\u00012\b\b\u0002\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0019H\u0003¢\u0006\u0004\b\u001d\u0010\u001b\u001aE\u0010\u001e\u001a\u00020\u00012\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0003\u0010 \u001a\u00020\u00072\b\b\u0002\u0010!\u001a\u00020\u00192\b\b\u0002\u0010\"\u001a\u00020\u0019H\u0003¢\u0006\u0004\b#\u0010$\u001a\r\u0010%\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010&\u001a\r\u0010'\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010&¨\u0006(",
   d2 = {
         "MediaItems",
         "",
         "mediaUploadItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "onItemClick",
         "Lkotlin/Function1;",
         "",
         "onItemDeleteClick",
         "onFailedItemClick",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "MediaItemWithState",
         "itemWithState",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "ImageItem",
         "uploadItem",
         "(Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "OtherItem",
         "MediaProgressIndicator",
         "size",
         "Landroidx/compose/ui/unit/Dp;",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "MediaProgressIndicator-kbKKJSQ",
         "(FJLandroidx/compose/runtime/Composer;II)V",
         "MediaRetryIcon",
         "MediaRetryIcon-kbKKJSQ",
         "MediaIconButton",
         "onClick",
         "iconRes",
         "backgroundColor",
         "iconColor",
         "MediaIconButton-jA1GFJw",
         "(Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;IJJLandroidx/compose/runtime/Composer;II)V",
         "MediaItemsPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "MediaFilesPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n*L\n1#1,438:1\n118#2:439\n118#2:440\n118#2:441\n118#2:480\n118#2:483\n118#2:484\n118#2:491\n118#2:527\n118#2:563\n118#2:570\n118#2:571\n118#2:615\n118#2:616\n118#2:623\n118#2:630\n118#2:641\n118#2:642\n118#2:643\n118#2:644\n1047#3,6:442\n1047#3,6:485\n1047#3,6:564\n1047#3,6:572\n1047#3,6:617\n1047#3,6:624\n1047#3,6:631\n70#4:448\n67#4,9:449\n70#4:492\n68#4,8:493\n77#4:526\n70#4:528\n68#4,8:529\n77#4:562\n77#4:581\n70#4:582\n66#4,10:583\n77#4:640\n81#5,6:458\n88#5,6:473\n81#5,6:501\n88#5,6:516\n96#5:525\n81#5,6:537\n88#5,6:552\n96#5:561\n96#5:580\n81#5,6:593\n88#5,6:608\n96#5:639\n402#6,9:464\n411#6:479\n402#6,9:507\n411#6,3:522\n402#6,9:543\n411#6,3:558\n412#6,2:578\n402#6,9:599\n411#6:614\n412#6,2:637\n75#7:481\n75#7:482\n204#8,13:645\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt\n*L\n59#1:439\n60#1:440\n62#1:441\n119#1:480\n130#1:483\n136#1:484\n161#1:491\n176#1:527\n190#1:563\n203#1:570\n217#1:571\n236#1:615\n238#1:616\n287#1:623\n305#1:630\n313#1:641\n318#1:642\n323#1:643\n343#1:644\n63#1:442,6\n137#1:485,6\n191#1:564,6\n218#1:572,6\n239#1:617,6\n288#1:624,6\n306#1:631,6\n115#1:448\n115#1:449,9\n158#1:492\n158#1:493,8\n158#1:526\n173#1:528\n173#1:529,8\n173#1:562\n115#1:581\n233#1:582\n233#1:583,10\n233#1:640\n115#1:458,6\n115#1:473,6\n158#1:501,6\n158#1:516,6\n158#1:525\n173#1:537,6\n173#1:552,6\n173#1:561\n115#1:580\n233#1:593,6\n233#1:608,6\n233#1:639\n115#1:464,9\n115#1:479\n158#1:507,9\n158#1:522,3\n173#1:543,9\n173#1:558,3\n115#1:578,2\n233#1:599,9\n233#1:614\n233#1:637,2\n121#1:481\n126#1:482\n64#1:645,13\n*E\n"
)
public final class MediaItemsKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MediaItems(
      @NotNull List var0,
      @NotNull Function1 var1,
      @NotNull Function1 var2,
      @NotNull Function1 var3,
      @Nullable Modifier var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "mediaUploadItems");
      Intrinsics.checkNotNullParameter(var1, "onItemClick");
      Intrinsics.checkNotNullParameter(var2, "onItemDeleteClick");
      Intrinsics.checkNotNullParameter(var3, "onFailedItemClick");
      Composer var31 = var5.startRestartGroup(235588671);
      ComposerKt.sourceInformation(var31, "C(MediaItems)P(!1,3,4,2)62@2700L456,55@2418L738:MediaItems.kt#yb6g9");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var31.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var31.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var31.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var31.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var31.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var31.getSkipping()) {
         var31.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(235588671, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItems (MediaItems.kt:54)");
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var4, 0.0F, 1, null);
         int var10 = 16;
         int var11 = 0;
         Modifier var9 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var10), 0.0F, 0.0F, 13, null);
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         HorizontalOrVertical var32 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Vertical var34 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         PaddingValues var35 = PaddingKt.recovered$0eefed217002276c(Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null);
         Modifier var38 = var9;
         Object var10001 = null;
         PaddingValues var10002 = var35;
         boolean var10003 = false;
         Horizontal var10004 = var32;
         Vertical var10005 = var34;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var31.startReplaceGroup(406277787);
         ComposerKt.sourceInformation(var31, "CC(remember):MediaItems.kt#9igjgp");
         Composer var37 = var31;
         boolean var15 = var31.changedInstance(var0) | (var8 & 112) == 32 | (var8 & 896) == 256 | (var8 & 7168) == 2048;
         int var16 = 0;
         Object var17 = var37.rememberedValue();
         int var18 = 0;
         Object var10009;
         if (!var15 && var17 != Composer.Companion.getEmpty()) {
            var10009 = var17;
         } else {
            Object var29 = null;
            boolean var28 = false;
            Object var27 = null;
            Vertical var26 = var34;
            Horizontal var25 = var10004;
            boolean var24 = false;
            PaddingValues var23 = var35;
            Object var22 = null;
            Modifier var21 = var9;
            int var19 = 0;
            final java.util.List recoveredExpressionCapture_589_0 = var0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_589_1 = var1;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_589_2 = var2;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_589_3 = var3;
            Function1 var30 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_589_0) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaItems$lambda$4$lambda$3(recoveredExpressionCapture_589_0, recoveredExpressionCapture_589_1, recoveredExpressionCapture_589_2, recoveredExpressionCapture_589_3, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_589_0)));
            var38 = var21;
            var10001 = var22;
            var10002 = var23;
            var10003 = var24;
            var10004 = var25;
            var10005 = var26;
            var10006 = var27;
            var10007 = var28;
            var10008 = var29;
            Object var20 = var30;
            var37.updateRememberedValue(var20);
            var10009 = var20;
         }

         Function1 var36 = (Function1)var10009;
         var31.endReplaceGroup();
         LazyDslKt.LazyRow(
            var38,
            (LazyListState)var10001,
            var10002,
            var10003,
            var10004,
            var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var36,
            var31,
            221568,
            458
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var39 = var31.endRestartGroup();
      if (var39 != null) {
         final java.util.List recoveredExpressionCapture_590_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_590_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_590_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_590_3 = var3;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_590_4 = var4;
         final int recoveredExpressionCapture_590_5 = var6;
         final int recoveredExpressionCapture_590_6 = var7;
         var39.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_590_0, recoveredExpressionArgument_590_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaItems$lambda$5(recoveredExpressionCapture_590_0, recoveredExpressionCapture_590_1, recoveredExpressionCapture_590_2, recoveredExpressionCapture_590_3, recoveredExpressionCapture_590_4, recoveredExpressionCapture_590_5, recoveredExpressionCapture_590_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_590_0), ((java.lang.Integer)recoveredExpressionArgument_590_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MediaItemWithState(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Composer var4, int var5) {
      Composer var8 = var4.startRestartGroup(507870855);
      ComposerKt.sourceInformation(var8, "C(MediaItemWithState)P(!1,2,3):MediaItems.kt#yb6g9");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= ((var5 & 8) == 0 ? var8.changed(var0) : var8.changedInstance(var0)) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var8.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var8.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var8.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               507870855, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemWithState (MediaItems.kt:85)"
            );
         }

         io.intercom.android.sdk.m5.upload.data.MediaData$Media var7 = var0.getMediaData();
         if (!(var7 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image) && !(var7 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video)) {
            if (!(var7 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other)) {
               var8.startReplaceGroup(2079947709);
               var8.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var8.startReplaceGroup(2079957472);
            ComposerKt.sourceInformation(var8, "95@3688L181");
            OtherItem(var0, var1, var2, var3, var8, 14 & var6 | 112 & var6 | 896 & var6 | 7168 & var6);
            var8.endReplaceGroup();
         } else {
            var8.startReplaceGroup(2079950592);
            ComposerKt.sourceInformation(var8, "88@3473L181");
            ImageItem(var0, var1, var2, var3, null, var8, 14 & var6 | 112 & var6 | 896 & var6 | 7168 & var6, 16);
            var8.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var8.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_588_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_588_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_588_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_588_3 = var3;
         final int recoveredExpressionCapture_588_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_588_0, recoveredExpressionArgument_588_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaItemWithState$lambda$6(recoveredExpressionCapture_588_0, recoveredExpressionCapture_588_1, recoveredExpressionCapture_588_2, recoveredExpressionCapture_588_3, recoveredExpressionCapture_588_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_588_0), ((java.lang.Integer)recoveredExpressionArgument_588_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ImageItem(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Modifier var4, Composer var5, int var6, int var7) {
      Composer var74 = var5.startRestartGroup(179381522);
      ComposerKt.sourceInformation(var74, "C(ImageItem)P(4,2,3,1)114@4189L4000:MediaItems.kt#yb6g9");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= ((var6 & 8) == 0 ? var74.changed(var0) : var74.changedInstance(var0)) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var74.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var74.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var74.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var74.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var74.getSkipping()) {
         var74.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(179381522, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.ImageItem (MediaItems.kt:110)");
         }

         io.intercom.android.sdk.m5.upload.data.MediaData$Media var9 = var0.getMediaData();
         io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var10 = var0.getUploadState();
         int var13 = 14 & var8 >> 12;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var74, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var11 = Alignment.Companion.getTopStart();
         boolean var12 = false;
         MeasurePolicy var15 = BoxKt.maybeCachedBoxMeasurePolicy(var11, var12);
         Modifier var16 = var4;
         MeasurePolicy var17 = var15;
         Composer var18 = var74;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         BoxScope var35 = BoxScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, -1983114304, "C120@4378L7,125@4594L7,132@4854L6,135@4983L6,136@5039L505,119@4312L1289,217@8139L33,213@7977L206:MediaItems.kt#yb6g9"
         );
         int var37 = 16;
         int var38 = 0;
         RoundedCornerShape var39 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var37));
         CompositionLocal var78 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var40 = 0;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var42 = var34.consume(var78);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ImageRequest var75 = new Builder((Context)var42).data(var9.getUri()).crossfade(true).error(drawable.intercom_image_load_failed).build();
         CompositionLocal var89 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var43 = var34.consume(var89);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ImageLoader var79 = IntercomImageLoaderKt.getImageLoader((Context)var43);
         String var90 = var9.getFileName();
         Modifier var10000 = var35.align(Modifier.Companion, Alignment.Companion.getCenter());
         int $this$dp$ivx = 64;
         int $i$f$getDpx = 0;
         Modifier var165 = BackgroundKt.recovered$60bc47fb440df483(
            ClipKt.clip(SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var39),
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$baf23fcaed2b2227(),
            var39
         );
         int $this$dp$ivxx = 1;
         int $i$f$getDpxx = 0;
         Modifier var166 = BorderKt.recovered$1227359600bc50fd(
            var165,
            Dp.recovered$95b3d103663bf060($this$dp$ivxx),
            IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getAlpha().recovered$146d1d8b5c75e8db(),
            var39
         );
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var34.startReplaceGroup(213144559);
         ComposerKt.sourceInformation(var34, "CC(remember):MediaItems.kt#9igjgp");
         Composer var121 = var34;
         boolean var44 = var74.changedInstance(var10)
            | (var8 & 7168) == 2048
            | ((var8 & 14) == 4 || (var8 & 8) != 0 && var74.changedInstance(var0))
            | (var8 & 112) == 32;
         int var45 = 0;
         Object var46 = var121.rememberedValue();
         int var47 = 0;
         Object var10005;
         if (!var44 && var46 != Composer.Companion.getEmpty()) {
            var10005 = var46;
         } else {
            Object var48 = null;
            Object var49 = null;
            Object var50 = null;
            boolean var51 = false;
            Modifier var52 = var166;
            int var53 = 0;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState recoveredExpressionCapture_586_0 = var10;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_586_1 = var3;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_586_2 = var0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_586_3 = var1;
            Function0 var54 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.ImageItem$lambda$15$lambda$8$lambda$7(recoveredExpressionCapture_586_0, recoveredExpressionCapture_586_1, recoveredExpressionCapture_586_2, recoveredExpressionCapture_586_3));
            var166 = var52;
            var10001 = var51;
            var10002 = var50;
            var10003 = var49;
            var10004 = var48;
            Object var55 = var54;
            var121.updateRememberedValue(var55);
            var10005 = var55;
         }

         Function0 var114 = (Function0)var10005;
         var34.endReplaceGroup();
         Modifier var104 = ClickableKt.recovered$1c9660c41fd850dc(
            var166, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var114, 15, null
         );
         ContentScale var115 = ContentScale.Companion.getCrop();
         AsyncImageKt.recovered$b6a46e909c1fcc39(var75, var90, var79, var104, null, null, null, var115, 0.0F, null, 0, false, null, var34, 12582912, 0, 8048);
         io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var76 = var10;
         if (var76 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading) {
            var34.startReplaceGroup(-1981783165);
            ComposerKt.sourceInformation(var34, "162@5950L6,163@6041L6,157@5732L489");
            Modifier var167 = var35.align(Modifier.Companion, Alignment.Companion.getCenter());
            int $this$dp$ivxxx = 64;
            int $i$f$getDpxxx = 0;
            Modifier var81 = BackgroundKt.recovered$60bc47fb440df483(
               SizeKt.recovered$111ef94c93e78c04(var167, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)),
               IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$1aad99ea8dc7d116(),
               IntercomTheme.INSTANCE.getShapes(var34, IntercomTheme.$stable).getLarge()
            );
            Alignment var92 = Alignment.Companion.getCenter();
            byte var116 = 48;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var34, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var92, propagateMinConstraints$ivx);
            Modifier var130 = var81;
            MeasurePolicy var134 = measurePolicy$ivx;
            Composer var138 = var34;
            int var141 = 112 & var116 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var138, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var138, 0));
            CompositionLocalMap localMap$iv$ivx = var138.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var138, var130);
            Function0 var59 = ComposeUiNode.Companion.getConstructor();
            Composer var60 = var138;
            int var61 = 6 | 896 & var141 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var60, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var60.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var60.startReusableNode();
            if (var60.getInserting()) {
               var60.createNode(var59);
            } else {
               var60.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var60);
            int var64/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var134, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var65 = 14 & var61 >> 6;
            Composer $composer$ivx = var60;
            int var67/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var68 = 6 | 112 & var116 >> 6;
            Composer var69 = $composer$ivx;
            BoxScope var70 = BoxScopeInstance.INSTANCE;
            int var71 = 0;
            ComposerKt.sourceInformationMarkerStart(var69, 887062550, "C167@6179L24:MediaItems.kt#yb6g9");
            recovered$c1fa517051a761c4(0.0F, 0L, var69, 0, 3);
            ComposerKt.sourceInformationMarkerEnd(var69);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var60.endNode();
            ComposerKt.sourceInformationMarkerEnd(var60);
            ComposerKt.sourceInformationMarkerEnd(var138);
            ComposerKt.sourceInformationMarkerEnd(var34);
            var34.endReplaceGroup();
         } else if (var76 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error) {
            var34.startReplaceGroup(-1981195808);
            ComposerKt.sourceInformation(var34, "177@6525L6,178@6616L6,172@6307L555,194@7307L6,190@7075L106,186@6879L464");
            Modifier var168 = var35.align(Modifier.Companion, Alignment.Companion.getCenter());
            int $this$dp$ivxxx = 64;
            int $i$f$getDpxxx = 0;
            Modifier var83 = BackgroundKt.recovered$60bc47fb440df483(
               SizeKt.recovered$111ef94c93e78c04(var168, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)),
               IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$1aad99ea8dc7d116(),
               IntercomTheme.INSTANCE.getShapes(var34, IntercomTheme.$stable).getLarge()
            );
            Alignment var94 = Alignment.Companion.getCenter();
            byte var117 = 48;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var34, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var94, propagateMinConstraints$ivx);
            Modifier var131 = var83;
            MeasurePolicy var135 = measurePolicy$ivx;
            Composer var139 = var34;
            int var142 = 112 & var117 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var139, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var139, 0));
            CompositionLocalMap localMap$iv$ivx = var139.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var139, var131);
            Function0 var151 = ComposeUiNode.Companion.getConstructor();
            Composer var152 = var139;
            int var153 = 6 | 896 & var142 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var152, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var152.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var152.startReusableNode();
            if (var152.getInserting()) {
               var152.createNode(var151);
            } else {
               var152.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var152);
            int var156/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var135, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var157 = 14 & var153 >> 6;
            Composer $composer$ivx = var152;
            int var159/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var160 = 6 | 112 & var117 >> 6;
            Composer var161 = $composer$ivx;
            BoxScope var162 = BoxScopeInstance.INSTANCE;
            int var163 = 0;
            ComposerKt.sourceInformationMarkerStart(var161, 887634996, "C:MediaItems.kt#yb6g9");
            var161.startReplaceGroup(167180719);
            ComposerKt.sourceInformation(var161, "183@6806L16");
            if (((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error)var10).getError().getCanRetry()) {
               recovered$44c67957b5e01983(0.0F, 0L, var161, 0, 3);
            }

            var161.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var161);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var152.endNode();
            ComposerKt.sourceInformationMarkerEnd(var152);
            ComposerKt.sourceInformationMarkerEnd(var139);
            ComposerKt.sourceInformationMarkerEnd(var34);
            Modifier var169 = var35.align(Modifier.Companion, Alignment.Companion.getBottomEnd());
            int $this$dp$ivxxxx = 8;
            int $i$f$getDpxxxx = 0;
            float var174 = Dp.recovered$95b3d103663bf060($this$dp$ivxxxx);
            int $this$dp$ivxxxxx = 8;
            int $i$f$getDpxxxxx = 0;
            Modifier var84 = OffsetKt.recovered$ed7db252cbfb0f30(var169, var174, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx));
            int var97 = io.intercom.android.sdk.ui.R.drawable.intercom_ic_exlamation_mark;
            long var72 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getIcon().recovered$2bcfe565407cdfb8();
            var34.startReplaceGroup(213209312);
            ComposerKt.sourceInformation(var34, "CC(remember):MediaItems.kt#9igjgp");
            Composer var128 = var34;
            boolean var132 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var128.rememberedValue();
            int var143/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var170;
            if (it$ivx == Composer.Companion.getEmpty()) {
               int var146 = 0;
               Object var147 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.ImageItem$lambda$15$lambda$12$lambda$11());
               var128.updateRememberedValue(var147);
               var170 = var147;
            } else {
               var170 = it$ivx;
            }

            Function0 var124 = (Function0)var170;
            var34.endReplaceGroup();
            recovered$418378d6ab0e1c07(var124, var84, var97, var72, 0L, var34, 6, 16);
            var34.endReplaceGroup();
         } else {
            if (!(var76 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success)) {
               var34.startReplaceGroup(213165681);
               var34.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var34.startReplaceGroup(-1980097819);
            ComposerKt.sourceInformation(var34, "204@7667L51,206@7838L6,200@7488L412");
            if (var9 instanceof io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video) {
               Modifier var175 = Modifier.Companion;
               int $this$dp$ivxxx = 24;
               int $i$f$getDpxxx = 0;
               Modifier var85 = var35.align(
                  SizeKt.recovered$111ef94c93e78c04(var175, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), Alignment.Companion.getCenter()
               );
               Painter var99 = PainterResources_androidKt.painterResource(io.intercom.android.sdk.ui.R.drawable.intercom_ic_play, var34, 0);
               long var164 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$2a20e1a7e3ec768f();
               IconKt.recovered$e00adffb7fe45b02(var99, null, var85, var164, var34, 48 | Painter.$stable, 0);
            }

            var34.endReplaceGroup();
         }

         Modifier var171 = var35.align(Modifier.Companion, Alignment.Companion.getTopEnd());
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         float var176 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
         int $this$dp$ivxxxx = -8;
         int $i$f$getDpxxxx = 0;
         Modifier var77 = OffsetKt.recovered$ed7db252cbfb0f30(var171, var176, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx));
         var34.startReplaceGroup(213243287);
         ComposerKt.sourceInformation(var34, "CC(remember):MediaItems.kt#9igjgp");
         Composer var102 = var34;
         boolean var110 = (var8 & 896) == 256 | ((var8 & 14) == 4 || (var8 & 8) != 0 && var74.changedInstance(var0));
         int $i$f$cachex = 0;
         Object it$ivx = var102.rememberedValue();
         int var129/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var172;
         if (!var110 && it$ivx != Composer.Companion.getEmpty()) {
            var172 = it$ivx;
         } else {
            int var133 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_584_0 = var2;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_584_1 = var0;
            Object var137 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.ImageItem$lambda$15$lambda$14$lambda$13(recoveredExpressionCapture_584_0, recoveredExpressionCapture_584_1));
            var102.updateRememberedValue(var137);
            var172 = var137;
         }

         Function0 var88 = (Function0)var172;
         var34.endReplaceGroup();
         recovered$418378d6ab0e1c07(var88, var77, 0, 0L, 0L, var34, 0, 28);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var74);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var173 = var74.endRestartGroup();
      if (var173 != null) {
         final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_585_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_585_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_585_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_585_3 = var3;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_585_4 = var4;
         final int recoveredExpressionCapture_585_5 = var6;
         final int recoveredExpressionCapture_585_6 = var7;
         var173.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_585_0, recoveredExpressionArgument_585_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.ImageItem$lambda$16(recoveredExpressionCapture_585_0, recoveredExpressionCapture_585_1, recoveredExpressionCapture_585_2, recoveredExpressionCapture_585_3, recoveredExpressionCapture_585_4, recoveredExpressionCapture_585_5, recoveredExpressionCapture_585_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_585_0), ((java.lang.Integer)recoveredExpressionArgument_585_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void OtherItem(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Composer var4, int var5) {
      Composer var54 = var4.startRestartGroup(-1588325420);
      ComposerKt.sourceInformation(var54, "C(OtherItem)P(3,1,2)232@8499L2451:MediaItems.kt#yb6g9");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= ((var5 & 8) == 0 ? var54.changed(var0) : var54.changedInstance(var0)) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var54.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var54.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var54.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var54.getSkipping()) {
         var54.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1588325420, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.OtherItem (MediaItems.kt:228)");
         }

         io.intercom.android.sdk.m5.upload.data.MediaData$Media var10000 = var0.getMediaData();
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type io.intercom.android.sdk.m5.upload.data.MediaData.Media.Other");
         io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other var7 = (io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other)var10000;
         io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var8 = var0.getUploadState();
         byte var12 = 0;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var54, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var9 = Modifier.Companion;
         Alignment var10 = Alignment.Companion.getTopStart();
         boolean var11 = false;
         MeasurePolicy var14 = BoxKt.maybeCachedBoxMeasurePolicy(var10, var11);
         Modifier var15 = var9;
         MeasurePolicy var16 = var14;
         Composer var17 = var54;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
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
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         BoxScope var34 = BoxScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -786526163, "C238@8689L457,234@8533L1477,305@10900L33,301@10738L206:MediaItems.kt#yb6g9");
         Modifier var87 = Modifier.Companion;
         int var36 = 200;
         int var37 = 0;
         Modifier var38 = SizeKt.recovered$4f2dac42b6e775a9(var87, Dp.recovered$95b3d103663bf060(var36));
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         RoundedCornerShape var55 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var70 = var8;
         Function3 var61 = var70 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading
            ? io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.INSTANCE.recovered$b26556cd5c386b55()
            : (
               var70 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error
                  ? (
                     ((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error)var8).getError().getCanRetry()
                        ? io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.INSTANCE
                           .recovered$6532409fab43c00f()
                        : null
                  )
                  : null
            );
         io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other var88 = var7;
         RoundedCornerShape var10001 = var55;
         Modifier var10002 = var38;
         var33.startReplaceGroup(1914293876);
         ComposerKt.sourceInformation(var33, "CC(remember):MediaItems.kt#9igjgp");
         Composer var40 = var33;
         boolean var41 = var54.changedInstance(var8)
            | (var6 & 7168) == 2048
            | ((var6 & 14) == 4 || (var6 & 8) != 0 && var54.changedInstance(var0))
            | (var6 & 112) == 32;
         int var42 = 0;
         Object var43 = var40.rememberedValue();
         int var44 = 0;
         Object var10003;
         if (!var41 && var43 != Composer.Companion.getEmpty()) {
            var10003 = var43;
         } else {
            Modifier var45 = var38;
            RoundedCornerShape var46 = var55;
            io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other var47 = var7;
            int var48 = 0;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState recoveredExpressionCapture_593_0 = var8;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_593_1 = var3;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_593_2 = var0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_593_3 = var1;
            Function0 var49 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.OtherItem$lambda$23$lambda$18$lambda$17(recoveredExpressionCapture_593_0, recoveredExpressionCapture_593_1, recoveredExpressionCapture_593_2, recoveredExpressionCapture_593_3));
            var88 = var47;
            var10001 = var46;
            var10002 = var45;
            Object var50 = var49;
            var40.updateRememberedValue(var50);
            var10003 = var50;
         }

         Function0 var71 = (Function0)var10003;
         var33.endReplaceGroup();
         FileRowKt.FileRow(var88, var10001, var10002, var71, var61, var33, 384, 0);
         io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var68 = var8;
         var33.startReplaceGroup(1914337686);
         ComposerKt.sourceInformation(var33, "291@10569L6,287@10333L110,283@10137L468");
         if (var68 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error) {
            Modifier var89 = var34.align(Modifier.Companion, Alignment.Companion.getBottomEnd());
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            float var94 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
            int $this$dp$ivxxx = 8;
            int $i$f$getDpxxx = 0;
            Modifier var56 = OffsetKt.recovered$ed7db252cbfb0f30(var89, var94, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            int var64 = io.intercom.android.sdk.ui.R.drawable.intercom_ic_exlamation_mark;
            long var51 = IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getIcon().recovered$2bcfe565407cdfb8();
            var33.startReplaceGroup(1914346137);
            ComposerKt.sourceInformation(var33, "CC(remember):MediaItems.kt#9igjgp");
            Composer var78 = var33;
            boolean var80 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var78.rememberedValue();
            int var85/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var90;
            if (it$ivx == Composer.Companion.getEmpty()) {
               int var53 = 0;
               Object var86 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.OtherItem$lambda$23$lambda$20$lambda$19());
               var78.updateRememberedValue(var86);
               var90 = var86;
            } else {
               var90 = it$ivx;
            }

            Function0 var76 = (Function0)var90;
            var33.endReplaceGroup();
            recovered$418378d6ab0e1c07(var76, var56, var64, var51, 0L, var33, 6, 16);
         }

         var33.endReplaceGroup();
         Modifier var91 = var34.align(Modifier.Companion, Alignment.Companion.getTopEnd());
         int $this$dp$ivxx = 8;
         int $i$f$getDpxx = 0;
         float var95 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         int $this$dp$ivxxx = -8;
         int $i$f$getDpxxx = 0;
         Modifier var69 = OffsetKt.recovered$ed7db252cbfb0f30(var91, var95, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
         var33.startReplaceGroup(1914364204);
         ComposerKt.sourceInformation(var33, "CC(remember):MediaItems.kt#9igjgp");
         Composer var67 = var33;
         boolean var74 = (var6 & 896) == 256 | ((var6 & 14) == 4 || (var6 & 8) != 0 && var54.changedInstance(var0));
         int $i$f$cachex = 0;
         Object it$ivx = var67.rememberedValue();
         int var79/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var92;
         if (!var74 && it$ivx != Composer.Companion.getEmpty()) {
            var92 = it$ivx;
         } else {
            int var81 = 0;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_595_0 = var2;
            final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_595_1 = var0;
            Object var83 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.OtherItem$lambda$23$lambda$22$lambda$21(recoveredExpressionCapture_595_0, recoveredExpressionCapture_595_1));
            var67.updateRememberedValue(var83);
            var92 = var83;
         }

         Function0 var59 = (Function0)var92;
         var33.endReplaceGroup();
         recovered$418378d6ab0e1c07(var59, var69, 0, 0L, 0L, var33, 0, 28);
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var54);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var93 = var54.endRestartGroup();
      if (var93 != null) {
         final io.intercom.android.sdk.m5.conversation.states.MediaUploadItem recoveredExpressionCapture_596_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_596_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_596_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_596_3 = var3;
         final int recoveredExpressionCapture_596_4 = var5;
         var93.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_596_0, recoveredExpressionArgument_596_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.OtherItem$lambda$24(recoveredExpressionCapture_596_0, recoveredExpressionCapture_596_1, recoveredExpressionCapture_596_2, recoveredExpressionCapture_596_3, recoveredExpressionCapture_596_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_596_0), ((java.lang.Integer)recoveredExpressionArgument_596_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$c1fa517051a761c4(float var0, long var1, Composer var3, int var4, int var5) {
      Composer var9 = var3.startRestartGroup(587300576);
      ComposerKt.sourceInformation(var9, "C(MediaProgressIndicator)P(1:c#ui.unit.Dp,0:c#ui.graphics.Color)313@11057L6,314@11102L107:MediaItems.kt#yb6g9");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var6 |= (var5 & 2) == 0 && var9.changed(var1) ? 32 : 16;
      }

      if ((var6 & 19) == 18 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         var9.startDefaults();
         if ((var4 & 1) != 0 && !var9.getDefaultsInvalid()) {
            var9.skipToGroupEnd();
            if ((var5 & 2) != 0) {
               var6 &= -113;
            }
         } else {
            if ((var5 & 1) != 0) {
               int var7 = 24;
               int var8 = 0;
               var0 = Dp.recovered$95b3d103663bf060(var7);
            }

            if ((var5 & 2) != 0) {
               var1 = IntercomTheme.INSTANCE.getColors(var9, IntercomTheme.$stable).recovered$2a20e1a7e3ec768f();
               var6 &= -113;
            }
         }

         var9.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               587300576, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaProgressIndicator (MediaItems.kt:314)"
            );
         }

         Modifier var10000 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var0);
         int var10 = 2;
         int var11 = 0;
         ProgressIndicatorKt.recovered$276fbc1f701bb35f(var10000, var1, Dp.recovered$95b3d103663bf060(var10), 0L, 0, 0.0F, var9, 384 | 112 & var6, 56);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var9.endRestartGroup();
      if (var12 != null) {
         final float recoveredExpressionCapture_599_0 = var0;
         final long recoveredExpressionCapture_599_1 = var1;
         final int recoveredExpressionCapture_599_2 = var4;
         final int recoveredExpressionCapture_599_3 = var5;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_599_0, recoveredExpressionArgument_599_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaProgressIndicator_kbKKJSQ$lambda$25(recoveredExpressionCapture_599_0, recoveredExpressionCapture_599_1, recoveredExpressionCapture_599_2, recoveredExpressionCapture_599_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_599_0), ((java.lang.Integer)recoveredExpressionArgument_599_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$44c67957b5e01983(float var0, long var1, Composer var3, int var4, int var5) {
      Composer var10 = var3.startRestartGroup(628002367);
      ComposerKt.sourceInformation(
         var10, "C(MediaRetryIcon)P(1:c#ui.unit.Dp,0:c#ui.graphics.Color)323@11306L6,327@11419L51,328@11501L39,325@11355L214:MediaItems.kt#yb6g9"
      );
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var10.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var6 |= (var5 & 2) == 0 && var10.changed(var1) ? 32 : 16;
      }

      if ((var6 & 19) == 18 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         var10.startDefaults();
         if ((var4 & 1) != 0 && !var10.getDefaultsInvalid()) {
            var10.skipToGroupEnd();
            if ((var5 & 2) != 0) {
               var6 &= -113;
            }
         } else {
            if ((var5 & 1) != 0) {
               int var7 = 24;
               int var8 = 0;
               var0 = Dp.recovered$95b3d103663bf060(var7);
            }

            if ((var5 & 2) != 0) {
               var1 = IntercomTheme.INSTANCE.getColors(var10, IntercomTheme.$stable).recovered$2a20e1a7e3ec768f();
               var6 &= -113;
            }
         }

         var10.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(628002367, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaRetryIcon (MediaItems.kt:324)");
         }

         Modifier var11 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var0);
         Painter var12 = PainterResources_androidKt.painterResource(drawable.intercom_ic_reload, var10, 0);
         String var9 = StringResources_androidKt.stringResource(string.intercom_retry, var10, 0);
         IconKt.recovered$e00adffb7fe45b02(var12, var9, var11, var1, var10, Painter.$stable | 7168 & var6 << 6, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var10.endRestartGroup();
      if (var10000 != null) {
         final float recoveredExpressionCapture_598_0 = var0;
         final long recoveredExpressionCapture_598_1 = var1;
         final int recoveredExpressionCapture_598_2 = var4;
         final int recoveredExpressionCapture_598_3 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_598_0, recoveredExpressionArgument_598_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaRetryIcon_kbKKJSQ$lambda$26(recoveredExpressionCapture_598_0, recoveredExpressionCapture_598_1, recoveredExpressionCapture_598_2, recoveredExpressionCapture_598_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_598_0), ((java.lang.Integer)recoveredExpressionArgument_598_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$418378d6ab0e1c07(
      Function0 var0, Modifier var1, @DrawableRes int var2, long var3, long var5, Composer var7, int var8, int var9
   ) {
      Composer var14 = var7.startRestartGroup(1705420252);
      ComposerKt.sourceInformation(
         var14, "C(MediaIconButton)P(4,3,2,0:c#ui.graphics.Color,1:c#ui.graphics.Color)338@11782L6,339@11842L6,344@11959L501,341@11873L587:MediaItems.kt#yb6g9"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var14.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var14.changed(var1) ? 32 : 16;
      }

      if ((var8 & 384) == 0) {
         var10 |= (var9 & 4) == 0 && var14.changed(var2) ? 256 : 128;
      }

      if ((var8 & 3072) == 0) {
         var10 |= (var9 & 8) == 0 && var14.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 24576) == 0) {
         var10 |= (var9 & 16) == 0 && var14.changed(var5) ? 16384 : 8192;
      }

      if ((var10 & 9363) == 9362 && var14.getSkipping()) {
         var14.skipToGroupEnd();
      } else {
         var14.startDefaults();
         if ((var8 & 1) != 0 && !var14.getDefaultsInvalid()) {
            var14.skipToGroupEnd();
            if ((var9 & 4) != 0) {
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var10 &= -7169;
            }

            if ((var9 & 16) != 0) {
               var10 &= -57345;
            }
         } else {
            if ((var9 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var9 & 4) != 0) {
               var2 = io.intercom.android.sdk.ui.R.drawable.intercom_ic_x_mark;
               var10 &= -897;
            }

            if ((var9 & 8) != 0) {
               var3 = IntercomTheme.INSTANCE.getColors(var14, IntercomTheme.$stable).getAlpha().recovered$b71800394e05ff63();
               var10 &= -7169;
            }

            if ((var9 & 16) != 0) {
               var5 = IntercomTheme.INSTANCE.getColors(var14, IntercomTheme.$stable).getAlpha().recovered$12e27dd6c0ca521e();
               var10 &= -57345;
            }
         }

         var14.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1705420252, var10, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaIconButton (MediaItems.kt:340)"
            );
         }

         int var12 = 32;
         int var13 = 0;
         Modifier var11 = SizeKt.recovered$111ef94c93e78c04(var1, Dp.recovered$95b3d103663bf060(var12));
         IconButtonKt.IconButton(
            var0,
            var11,
            false,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1466983418, true, new MediaItemsKt$MediaIconButton$1(var3, var2, var5), var14, 54),
            var14,
            1572864 | 14 & var10,
            60
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var14.endRestartGroup();
      if (var10000 != null) {
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_597_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_597_1 = var1;
         final int recoveredExpressionCapture_597_2 = var2;
         final long recoveredExpressionCapture_597_3 = var3;
         final long recoveredExpressionCapture_597_4 = var5;
         final int recoveredExpressionCapture_597_5 = var8;
         final int recoveredExpressionCapture_597_6 = var9;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_597_0, recoveredExpressionArgument_597_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaIconButton_jA1GFJw$lambda$27(recoveredExpressionCapture_597_0, recoveredExpressionCapture_597_1, recoveredExpressionCapture_597_2, recoveredExpressionCapture_597_3, recoveredExpressionCapture_597_4, recoveredExpressionCapture_597_5, recoveredExpressionCapture_597_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_597_0), ((java.lang.Integer)recoveredExpressionArgument_597_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MediaItemsPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1307364875);
      ComposerKt.sourceInformation(var2, "C(MediaItemsPreview)367@12532L964:MediaItems.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1307364875, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsPreview (MediaItems.kt:366)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.INSTANCE.recovered$fd838d44303763da(),
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
         final int recoveredExpressionCapture_592_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_592_0, recoveredExpressionArgument_592_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaItemsPreview$lambda$28(recoveredExpressionCapture_592_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_592_0), ((java.lang.Integer)recoveredExpressionArgument_592_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MediaFilesPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(44999646);
      ComposerKt.sourceInformation(var2, "C(MediaFilesPreview)393@13569L1894:MediaItems.kt#yb6g9");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               44999646, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaFilesPreview (MediaItems.kt:392)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_587_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_587_0, recoveredExpressionArgument_587_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaFilesPreview$lambda$29(recoveredExpressionCapture_587_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_587_0), ((java.lang.Integer)recoveredExpressionArgument_587_1).intValue())));
      }
   }

   private static final Object MediaItems$lambda$4$lambda$3$lambda$0(int var0, MediaUploadItem var1) {
      Intrinsics.checkNotNullParameter(var1, "item");
      return var1.getInstanceId();
   }

   private static final Unit MediaItems$lambda$4$lambda$3(List var0, Function1 var1, Function1 var2, Function1 var3, LazyListScope var4) {
      Intrinsics.checkNotNullParameter(var4, "$this$LazyRow");
      LazyListScope var5 = var4;
      Function2 var6 = ((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_591_0, recoveredExpressionArgument_591_1) -> io.intercom.android.sdk.m5.conversation.ui.components.composer.MediaItemsKt.MediaItems$lambda$4$lambda$3$lambda$0(((java.lang.Integer)recoveredExpressionArgument_591_0).intValue(), (io.intercom.android.sdk.m5.conversation.states.MediaUploadItem) ((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredExpressionArgument_591_1)));
      int var7 = 0;
      var5.items(
         var0.size(),
         var6 != null
            ? new MediaItemsKt$MediaItems$lambda$4$lambda$3$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$1(var6, var0)
            : null,
         new MediaItemsKt$MediaItems$lambda$4$lambda$3$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$2(var0),
         ComposableLambdaKt.composableLambdaInstance(
            2039820996,
            true,
            new MediaItemsKt$MediaItems$lambda$4$lambda$3$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
               var0, var1, var2, var3
            )
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit MediaItems$lambda$5(
      List var0, Function1 var1, Function1 var2, Function1 var3, Modifier var4, int var5, int var6, Composer var7, int var8
   ) {
      MediaItems(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit MediaItemWithState$lambda$6(
      MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, int var4, Composer var5, int var6
   ) {
      MediaItemWithState(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ImageItem$lambda$15$lambda$8$lambda$7(io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var0, Function1 var1, MediaUploadItem var2, Function0 var3) {
      io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var4 = var0;
      if (var4 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error) {
         var1.invoke(var2);
      } else if (var4 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success) {
         var3.invoke();
      } else if (!Intrinsics.areEqual(var4, io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading.INSTANCE)) {
         throw new NoWhenBranchMatchedException();
      }

      return Unit.INSTANCE;
   }

   private static final Unit ImageItem$lambda$15$lambda$12$lambda$11() {
      return Unit.INSTANCE;
   }

   private static final Unit ImageItem$lambda$15$lambda$14$lambda$13(Function1 var0, MediaUploadItem var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit ImageItem$lambda$16(
      MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Modifier var4, int var5, int var6, Composer var7, int var8
   ) {
      ImageItem(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit OtherItem$lambda$23$lambda$18$lambda$17(io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var0, Function1 var1, MediaUploadItem var2, Function0 var3) {
      io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState var4 = var0;
      if (var4 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error) {
         var1.invoke(var2);
      } else if (var4 instanceof io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success) {
         var3.invoke();
      } else if (!Intrinsics.areEqual(var4, io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading.INSTANCE)) {
         throw new NoWhenBranchMatchedException();
      }

      return Unit.INSTANCE;
   }

   private static final Unit OtherItem$lambda$23$lambda$20$lambda$19() {
      return Unit.INSTANCE;
   }

   private static final Unit OtherItem$lambda$23$lambda$22$lambda$21(Function1 var0, MediaUploadItem var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit OtherItem$lambda$24(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, int var4, Composer var5, int var6) {
      OtherItem(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MediaProgressIndicator_kbKKJSQ$lambda$25(float var0, long var1, int var3, int var4, Composer var5, int var6) {
      recovered$c1fa517051a761c4(var0, var1, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit MediaRetryIcon_kbKKJSQ$lambda$26(float var0, long var1, int var3, int var4, Composer var5, int var6) {
      recovered$44c67957b5e01983(var0, var1, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit MediaIconButton_jA1GFJw$lambda$27(
      Function0 var0, Modifier var1, int var2, long var3, long var5, int var7, int var8, Composer var9, int var10
   ) {
      recovered$418378d6ab0e1c07(var0, var1, var2, var3, var5, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit MediaItemsPreview$lambda$28(int var0, Composer var1, int var2) {
      MediaItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MediaFilesPreview$lambda$29(int var0, Composer var1, int var2) {
      MediaFilesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void recovered$59138ed8819f7636(float var0, long var1, Composer var3, int var4, int var5) {
      recovered$c1fa517051a761c4(var0, var1, var3, var4, var5);
   }

   // $VF: synthetic method
   public static final void recovered$8892cb5149e057df(float var0, long var1, Composer var3, int var4, int var5) {
      recovered$44c67957b5e01983(var0, var1, var3, var4, var5);
   }

   // $VF: synthetic method
   public static final void access$OtherItem(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Composer var4, int var5) {
      OtherItem(var0, var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static final void access$MediaItemWithState(MediaUploadItem var0, Function0 var1, Function1 var2, Function1 var3, Composer var4, int var5) {
      MediaItemWithState(var0, var1, var2, var3, var4, var5);
   }
}
