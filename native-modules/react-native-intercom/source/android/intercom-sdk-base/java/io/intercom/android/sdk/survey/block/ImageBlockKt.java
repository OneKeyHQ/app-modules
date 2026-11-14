package io.intercom.android.sdk.survey.block;

import android.net.Uri;
import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import coil.compose.AsyncImagePainter.State;
import coil.compose.AsyncImagePainter.State.Empty;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
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
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\u001aW\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0016\b\u0002\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0001¢\u0006\u0004\b\u000e\u0010\u000f\"\u0018\u0010\u0010\u001a\u00020\t*\u00020\u00038BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012¨\u0006\u0013²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u008e\u0002",
   d2 = {
         "ImageBlock",
         "",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onClick",
         "Lkotlin/Function1;",
         "shouldLoadPreviewUrl",
         "",
         "renderType",
         "Lio/intercom/android/sdk/survey/block/ImageRenderType;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "ImageBlock-V-9fs2A",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;ZLio/intercom/android/sdk/survey/block/ImageRenderType;JLandroidx/compose/runtime/Composer;II)V",
         "hasUri",
         "getHasUri",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;)Z",
         "intercom-sdk-base_release",
         "state",
         "Lcoil/compose/AsyncImagePainter$State;"
   }
)
@SourceDebugExtension(
   "SMAP\nImageBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ImageBlock.kt\nio/intercom/android/sdk/survey/block/ImageBlockKt\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,198:1\n29#2:199\n29#2:200\n1047#3,6:201\n85#4:207\n117#4,2:208\n*S KotlinDebug\n*F\n+ 1 ImageBlock.kt\nio/intercom/android/sdk/survey/block/ImageBlockKt\n*L\n68#1:199\n69#1:200\n73#1:201,6\n73#1:207\n73#1:208,2\n*E\n"
)
public final class ImageBlockKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$5b15bdef32cfadf4(
      @NotNull Block var0,
      @Nullable Modifier var1,
      @Nullable Function1 var2,
      boolean var3,
      @Nullable ImageRenderType var4,
      long var5,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var0, "block");
      var7 = var7.startRestartGroup(1837674486);
      ComposerKt.sourceInformation(var7, "C(ImageBlock)P(1,2,3,5,4,0:c#ui.graphics.Color)60@2648L6,72@3073L83,74@3202L6056,74@3162L6096:ImageBlock.kt#jxhu3x");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var7.changedInstance(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var7.changed(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 196608) == 0) {
         var10 |= (var9 & 32) == 0 && var7.changed(var5) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         var7.startDefaults();
         if ((var8 & 1) != 0 && !var7.getDefaultsInvalid()) {
            var7.skipToGroupEnd();
            if ((var9 & 32) != 0) {
               var10 &= -458753;
            }
         } else {
            if ((var9 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var9 & 4) != 0) {
               var2 = null;
            }

            if ((var9 & 8) != 0) {
               var3 = false;
            }

            if ((var9 & 16) != 0) {
               var4 = ImageRenderType.CROPPED;
            }

            if ((var9 & 32) != 0) {
               var5 = IntercomTheme.INSTANCE.getColors(var7, IntercomTheme.$stable).recovered$f6e7a825ec69c94c();
               var10 &= -458753;
            }
         }

         var7.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1837674486, var10, -1, "io.intercom.android.sdk.survey.block.ImageBlock (ImageBlock.kt:61)");
         }

         boolean var10000;
         label143: {
            if (!getHasUri(var0) && var3) {
               CharSequence var12 = var0.getPreviewUrl();
               if (var12 != null && var12.length() != 0) {
                  var10000 = true;
                  break label143;
               }
            }

            var10000 = false;
         }

         boolean var11 = var10000;
         Uri var29;
         if (getHasUri(var0)) {
            var29 = var0.getLocalUri();
         } else if (var11) {
            String var30 = var0.getPreviewUrl();
            Intrinsics.checkNotNullExpressionValue(var30, "getPreviewUrl(...)");
            String var13 = var30;
            int var14 = 0;
            var29 = Uri.parse(var13);
         } else {
            String var31 = var0.getUrl();
            if (var31 == null) {
               var31 = "";
            }

            String var24 = var31;
            int var26 = 0;
            var29 = Uri.parse(var24);
         }

         Uri var23 = var29;
         String var25 = var23.getPath();
         var7.startReplaceGroup(2072025254);
         ComposerKt.sourceInformation(var7, "CC(remember):ImageBlock.kt#9igjgp");
         Composer var16 = var7;
         boolean var17 = false;
         int var18 = 0;
         Object var19 = var16.rememberedValue();
         int var20 = 0;
         Object var32;
         if (var19 == Composer.Companion.getEmpty()) {
            int var21 = 0;
            Object var28 = SnapshotStateKt.mutableStateOf$default(Empty.INSTANCE, null, 2, null);
            var16.updateRememberedValue(var28);
            var32 = var28;
         } else {
            var32 = var19;
         }

         MutableState var15 = (MutableState)var32;
         var7.endReplaceGroup();
         MutableState var27 = var15;
         BoxWithConstraintsKt.BoxWithConstraints(
            var1,
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(491541600, true, new ImageBlockKt$ImageBlock$1(var0, var4, var25, var23, var5, var2, var27), var7, 54),
            var7,
            3072 | 14 & var10 >> 3,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var33 = var7.endRestartGroup();
      if (var33 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_1269_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1269_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_1269_2 = var2;
         final boolean recoveredCapture_1269_3 = var3;
         final io.intercom.android.sdk.survey.block.ImageRenderType recoveredCapture_1269_4 = var4;
         final long recoveredCapture_1269_5 = var5;
         final int recoveredCapture_1269_6 = var8;
         final int recoveredCapture_1269_7 = var9;
         var33.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1269_0, recoveredArgument_1269_1) -> ImageBlockKt.ImageBlock_V_9fs2A$lambda$3(recoveredCapture_1269_0, recoveredCapture_1269_1, recoveredCapture_1269_2, recoveredCapture_1269_3, recoveredCapture_1269_4, recoveredCapture_1269_5, recoveredCapture_1269_6, recoveredCapture_1269_7, (androidx.compose.runtime.Composer)recoveredArgument_1269_0, ((java.lang.Integer)recoveredArgument_1269_1).intValue())));
      }
   }

   private static final boolean getHasUri(Block var0) {
      return var0.getLocalUri() != null && !Intrinsics.areEqual(var0.getLocalUri(), Uri.EMPTY);
   }

   private static final State ImageBlock_V_9fs2A$lambda$1(MutableState var0) {
      androidx.compose.runtime.State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (State)var1.getValue();
   }

   private static final void ImageBlock_V_9fs2A$lambda$2(MutableState var0, State var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit ImageBlock_V_9fs2A$lambda$3(
      Block var0, Modifier var1, Function1 var2, boolean var3, ImageRenderType var4, long var5, int var7, int var8, Composer var9, int var10
   ) {
      recovered$5b15bdef32cfadf4(var0, var1, var2, var3, var4, var5, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final State access$ImageBlock_V_9fs2A$lambda$1(MutableState var0) {
      return ImageBlock_V_9fs2A$lambda$1(var0);
   }

   // $VF: synthetic method
   public static final boolean access$getHasUri(Block var0) {
      return getHasUri(var0);
   }

   // $VF: synthetic method
   public static final void access$ImageBlock_V_9fs2A$lambda$2(MutableState var0, State var1) {
      ImageBlock_V_9fs2A$lambda$2(var0, var1);
   }
}
