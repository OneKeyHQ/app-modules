package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.AspectRatioKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.CornerSize;
import androidx.compose.foundation.shape.CornerSizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.Alignment.Horizontal;
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
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000T\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\u001aY\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\r\u001a\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\u0006\u0010\u0010\u001a\u00020\u0011H\u0002\u001a]\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\u0012\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0003¢\u0006\u0004\b\u001d\u0010\u001e\u001a3\u0010\u001f\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u001c\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010 \u001a0\u0010!\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u00162\u0006\u0010#\u001a\u00020\u00162\u0006\u0010$\u001a\u00020\u00162\u0006\u0010%\u001a\u00020\u00162\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\r\u0010&\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010(\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010)\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010*\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010+\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010,\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010-\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010.\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'\u001a\r\u0010/\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010'¨\u00060",
   d2 = {
         "GroupedMedia",
         "",
         "mediaItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/MediaItem;",
         "cornerShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onImageClick",
         "Lkotlin/Function1;",
         "",
         "onVideoClick",
         "(Ljava/util/List;Landroidx/compose/foundation/shape/RoundedCornerShape;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "calculateMediaLayout",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/RowLayout;",
         "count",
         "",
         "SingleMediaItem",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "shouldShowOverlayPlayIcon",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "borderColor",
         "borderWidth",
         "Landroidx/compose/ui/unit/Dp;",
         "onClick",
         "SingleMediaItem-Snr_uVM",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;ZLandroidx/compose/foundation/shape/RoundedCornerShape;JJFLkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "SingleImage",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "getMediaShapeForPosition",
         "isTopStart",
         "isTopEnd",
         "isBottomStart",
         "isBottomEnd",
         "GroupedMediaWith1ImagePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "GroupedMediaWith1VideoPreview",
         "GroupedMediaWith2ImagesPreview",
         "GroupedMediaWith3ImagesPreview",
         "GroupedMediaWith4MixedItemsPreview",
         "GroupedMediaWith5MixedItemsPreview",
         "GroupedMediaWith6MixedItemsPreview",
         "GroupedMediaWith7MixedItemsPreview",
         "GroupedMediaWith10MixedItemsPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nGroupedMedia.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GroupedMedia.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/GroupedMediaKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 9 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 10 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 11 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,448:1\n1047#2,6:449\n1047#2,6:455\n1047#2,6:645\n118#3:461\n118#3:604\n118#3:636\n118#3:653\n70#4:462\n68#4,8:463\n77#4:571\n70#4:573\n68#4,8:574\n70#4:605\n68#4,8:606\n77#4:640\n77#4:644\n81#5,6:471\n88#5,6:486\n81#5,6:502\n88#5,6:517\n81#5,6:537\n88#5,6:552\n96#5:561\n96#5:566\n96#5:570\n81#5,6:582\n88#5,6:597\n81#5,6:614\n88#5,6:629\n96#5:639\n96#5:643\n402#6,9:477\n411#6:492\n402#6,9:508\n411#6:523\n402#6,9:543\n411#6,3:558\n412#6,2:564\n412#6,2:568\n402#6,9:588\n411#6:603\n402#6,9:620\n411#6:635\n412#6,2:637\n412#6,2:641\n87#7:493\n85#7,8:494\n94#7:567\n1878#8,2:524\n1880#8:563\n99#9:526\n95#9,10:527\n106#9:562\n1#10:572\n75#11:651\n75#11:652\n*S KotlinDebug\n*F\n+ 1 GroupedMedia.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/GroupedMediaKt\n*L\n66#1:449,6\n67#1:455,6\n251#1:645,6\n70#1:461\n226#1:604\n234#1:636\n275#1:653\n76#1:462\n76#1:463,8\n76#1:571\n208#1:573\n208#1:574,8\n224#1:605\n224#1:606,8\n224#1:640\n208#1:644\n76#1:471,6\n76#1:486,6\n80#1:502,6\n80#1:517,6\n114#1:537,6\n114#1:552,6\n114#1:561\n80#1:566\n76#1:570\n208#1:582,6\n208#1:597,6\n224#1:614,6\n224#1:629,6\n224#1:639\n208#1:643\n76#1:477,9\n76#1:492\n80#1:508,9\n80#1:523\n114#1:543,9\n114#1:558,3\n80#1:564,2\n76#1:568,2\n208#1:588,9\n208#1:603\n224#1:620,9\n224#1:635\n224#1:637,2\n208#1:641,2\n80#1:493\n80#1:494,8\n80#1:567\n85#1:524,2\n85#1:563\n114#1:526\n114#1:527,10\n114#1:562\n252#1:651\n257#1:652\n*E\n"
)
public final class GroupedMediaKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMedia(
      @NotNull List var0,
      @NotNull RoundedCornerShape var1,
      @Nullable Modifier var2,
      @Nullable Function1 var3,
      @Nullable Function1 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "mediaItems");
      Intrinsics.checkNotNullParameter(var1, "cornerShape");
      var5 = var5.startRestartGroup(2071572915);
      ComposerKt.sourceInformation(var5, "C(GroupedMedia)P(1)65@2825L2,66@2866L2,68@2910L6,70@2998L6,75@3153L3565:GroupedMedia.kt#hr3bi5");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var7 & 8) != 0) {
            var5.startReplaceGroup(-79072205);
            ComposerKt.sourceInformation(var5, "CC(remember):GroupedMedia.kt#9igjgp");
            Composer var10 = var5;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var130 = ((kotlin.jvm.functions.Function1) (recoveredArgument_743_0) -> GroupedMediaKt.GroupedMedia$lambda$1$lambda$0((java.lang.String)recoveredArgument_743_0));
               var10.updateRememberedValue(var130);
               var10000 = var130;
            } else {
               var10000 = var13;
            }

            Function1 var9 = (Function1)var10000;
            var5.endReplaceGroup();
            var3 = var9;
         }

         if ((var7 & 16) != 0) {
            var5.startReplaceGroup(-79070893);
            ComposerKt.sourceInformation(var5, "CC(remember):GroupedMedia.kt#9igjgp");
            Composer var120 = var5;
            boolean var121 = false;
            int var123 = 0;
            Object var126 = var120.rememberedValue();
            int var128 = 0;
            Object var138;
            if (var126 == Composer.Companion.getEmpty()) {
               int var131 = 0;
               Object var132 = ((kotlin.jvm.functions.Function1) (recoveredArgument_744_0) -> GroupedMediaKt.GroupedMedia$lambda$3$lambda$2((java.lang.String)recoveredArgument_744_0));
               var120.updateRememberedValue(var132);
               var138 = var132;
            } else {
               var138 = var126;
            }

            Function1 var118 = (Function1)var138;
            var5.endReplaceGroup();
            var4 = var118;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2071572915, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMedia (GroupedMedia.kt:67)");
         }

         long var119 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).getAlpha().recovered$146d1d8b5c75e8db();
         int var124 = 1;
         int var127 = 0;
         float var122 = Dp.recovered$95b3d103663bf060(var124);
         long var125 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).recovered$baf23fcaed2b2227();
         List var129 = calculateMediaLayout(var0.size());
         Modifier var133 = SizeKt.wrapContentWidth$default(var2, null, false, 3, null);
         Alignment var16 = Alignment.Companion.getTopEnd();
         byte var18 = 48;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var17 = false;
         MeasurePolicy var20 = BoxKt.maybeCachedBoxMeasurePolicy(var16, var17);
         Modifier var21 = var133;
         MeasurePolicy var22 = var20;
         Composer var23 = var5;
         int var24 = 112 & var18 << 3;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var26 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var23, 0));
         CompositionLocalMap var27 = var23.getCurrentCompositionLocalMap();
         Modifier var28 = ComposedModifierKt.materializeModifier(var23, var21);
         Function0 var29 = ComposeUiNode.Companion.getConstructor();
         Composer var30 = var23;
         int var31 = 6 | 896 & var24 << 6;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var30.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var30.startReusableNode();
         if (var30.getInserting()) {
            var30.createNode(var29);
         } else {
            var30.useNode();
         }

         Composer var33 = Updater.recovered$676aa17b44fd6280(var30);
         int var34 = 0;
         Updater.recovered$89682b199a142880(var33, var22, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var33, var27, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var33, var26, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var33, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var33, var28, ComposeUiNode.Companion.getSetModifier());
         int var35 = 14 & var31 >> 6;
         Composer var36 = var30;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var38 = 6 | 112 & var18 >> 6;
         Composer var39 = var36;
         BoxScope var40 = BoxScopeInstance.INSTANCE;
         int var41 = 0;
         ComposerKt.sourceInformationMarkerStart(var39, 1725645630, "C79@3267L3445:GroupedMedia.kt#hr3bi5");
         Modifier var42 = ClipKt.clip(Modifier.Companion, var1);
         Vertical var43 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
         byte var44 = 48;
         int var45 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var39, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var46 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var43, var46, var39, 14 & var44 >> 3 | 112 & var44 >> 3);
         Modifier var48 = var42;
         MeasurePolicy var49 = measurePolicy$ivx;
         Composer var50 = var39;
         int var51 = 112 & var44 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var50, 0));
         CompositionLocalMap localMap$iv$ivx = var50.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var50, var48);
         Function0 var56 = ComposeUiNode.Companion.getConstructor();
         Composer var57 = var50;
         int var58 = 6 | 896 & var51 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var57, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var57.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var57.startReusableNode();
         if (var57.getInserting()) {
            var57.createNode(var56);
         } else {
            var57.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var57);
         int var61/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var49, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var62 = 14 & var58 >> 6;
         Composer $composer$ivx = var57;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var65 = 6 | 112 & var44 >> 6;
         Composer var66 = $composer$ivx;
         ColumnScope var67 = ColumnScopeInstance.INSTANCE;
         int var68 = 0;
         ComposerKt.sourceInformationMarkerStart(var66, -426088185, "C:GroupedMedia.kt#hr3bi5");
         int var69 = 0;
         var66.startReplaceGroup(263351186);
         ComposerKt.sourceInformation(var66, "");
         Iterable var70 = var129;
         int var71 = 0;
         int var72 = 0;

         for (Object var74 : var70) {
            int var75 = var72++;
            if (var75 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            RowLayout var76 = (RowLayout)var74;
            int var77 = var75;
            int var78 = 0;
            boolean var79 = var77 == 0;
            boolean var80 = var77 == CollectionsKt.getLastIndex(var129);
            RowLayout var81 = var76;
            if (var81 instanceof RowLayout$SingleFullWidth) {
               var66.startReplaceGroup(1374402462);
               ComposerKt.sourceInformation(var66, "98@4155L635");
               MediaItem var134 = (MediaItem)var0.get(var69);
               RoundedCornerShape var135 = getMediaShapeForPosition(var79, var79, var80, var80, var1);
               recovered$820d69f83065bffe(
                  var134.getBlock(),
                  var134.isVideo(),
                  var135,
                  var125,
                  var119,
                  var122,
                  var134.isVideo() ? var4 : var3,
                  AspectRatioKt.aspectRatio$default(SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 1.0F, false, 2, null),
                  var66,
                  12779520,
                  0
               );
               int var136 = var69;
               var69 = var136 + 1;
               int var137 = var136;
               var66.endReplaceGroup();
               Integer.valueOf(var137);
            } else {
               if (!(var81 instanceof RowLayout$Grid)) {
                  var66.startReplaceGroup(875619194);
                  var66.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var66.startReplaceGroup(1375596737);
               ComposerKt.sourceInformation(var66, "113@4917L1731");
               androidx.compose.foundation.layout.Arrangement.Horizontal var82 = Arrangement.INSTANCE
                  .recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
               byte var84 = 48;
               int var86 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var66, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
               );
               Modifier var85 = Modifier.Companion;
               androidx.compose.ui.Alignment.Vertical var83 = Alignment.Companion.getTop();
               MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var82, var83, var66, 14 & var84 >> 3 | 112 & var84 >> 3);
               Modifier var88 = var85;
               MeasurePolicy var89 = measurePolicy$ivxx;
               Composer var90 = var66;
               int var91 = 112 & var84 << 3;
               int $i$f$Layoutxx = 0;
               ComposerKt.sourceInformationMarkerStart(var90, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
               int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var90, 0));
               CompositionLocalMap localMap$iv$ivxx = var90.getCurrentCompositionLocalMap();
               Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var90, var88);
               Function0 var96 = ComposeUiNode.Companion.getConstructor();
               Composer var97 = var90;
               int var98 = 6 | 896 & var91 << 6;
               int $i$f$ReusableComposeNodexx = 0;
               ComposerKt.sourceInformationMarkerStart(var97, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
               if (!(var97.getApplier() instanceof Applier)) {
                  ComposablesKt.invalidApplier();
               }

               var97.startReusableNode();
               if (var97.getInserting()) {
                  var97.createNode(var96);
               } else {
                  var97.useNode();
               }

               Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var97);
               int var101/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var89, ComposeUiNode.Companion.getSetMeasurePolicy());
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
               );
               Updater.recovered$89682b199a142880(
                  $this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
               );
               Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
               Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
               int var102 = 14 & var98 >> 6;
               Composer $composer$ivxx = var97;
               int var104 = 0;
               ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
               int var105 = 6 | 112 & var84 >> 6;
               Composer var106 = $composer$ivxx;
               RowScope var107 = RowScopeInstance.INSTANCE;
               int var108 = 0;
               ComposerKt.sourceInformationMarkerStart(var106, -1136520825, "C:GroupedMedia.kt#hr3bi5");
               var106.startReplaceGroup(-1976324612);
               ComposerKt.sourceInformation(var106, "*127@5828L719");
               int var109 = ((RowLayout$Grid)var76).getColumns();

               for (int var110 = 0; var110 < var109; var110++) {
                  int var111 = var110;
                  int var112 = 0;
                  MediaItem var113 = (MediaItem)var0.get(var69);
                  boolean var114 = var111 == 0;
                  boolean var115 = var111 == ((RowLayout$Grid)var76).getColumns() - 1;
                  RoundedCornerShape var116 = getMediaShapeForPosition(var79 && var114, var79 && var115, var80 && var114, var80 && var115, var1);
                  recovered$820d69f83065bffe(
                     var113.getBlock(),
                     var113.isVideo(),
                     var116,
                     var125,
                     var119,
                     var122,
                     var113.isVideo() ? var4 : var3,
                     AspectRatioKt.aspectRatio$default(RowScope.weight$default(var107, Modifier.Companion, 1.0F, false, 2, null), 1.0F, false, 2, null),
                     var106,
                     196608,
                     0
                  );
                  var69++;
               }

               var106.endReplaceGroup();
               ComposerKt.sourceInformationMarkerEnd(var106);
               ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
               var97.endNode();
               ComposerKt.sourceInformationMarkerEnd(var97);
               ComposerKt.sourceInformationMarkerEnd(var90);
               ComposerKt.sourceInformationMarkerEnd(var66);
               var66.endReplaceGroup();
            }
         }

         var66.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var66);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var57.endNode();
         ComposerKt.sourceInformationMarkerEnd(var57);
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var39);
         ComposerKt.sourceInformationMarkerEnd(var36);
         var30.endNode();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var140 = var5.endRestartGroup();
      if (var140 != null) {
         final java.util.List recoveredCapture_742_0 = var0;
         final androidx.compose.foundation.shape.RoundedCornerShape recoveredCapture_742_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_742_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_742_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_742_4 = var4;
         final int recoveredCapture_742_5 = var6;
         final int recoveredCapture_742_6 = var7;
         var140.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_742_0, recoveredArgument_742_1) -> GroupedMediaKt.GroupedMedia$lambda$9(recoveredCapture_742_0, recoveredCapture_742_1, recoveredCapture_742_2, recoveredCapture_742_3, recoveredCapture_742_4, recoveredCapture_742_5, recoveredCapture_742_6, (androidx.compose.runtime.Composer)recoveredArgument_742_0, ((java.lang.Integer)recoveredArgument_742_1).intValue())));
      }
   }

   private static final List calculateMediaLayout(int var0) {
      if (var0 == 0) {
         return CollectionsKt.emptyList();
      }

      if (var0 == 4) {
         RowLayout$Grid[] var9 = new RowLayout$Grid[]{new RowLayout$Grid(2), new RowLayout$Grid(2)};
         return CollectionsKt.listOf(var9);
      }

      int var1 = var0 / 3;
      int var2 = var0 % 3;
      List var3 = CollectionsKt.createListBuilder();
      List var4 = var3;
      int var5 = 0;

      for (int var6 = 0; var6 < var1; var6++) {
         int var8 = 0;
         var4.add(new RowLayout$Grid(3));
      }

      switch (var2) {
         case 1:
            var4.add(RowLayout$SingleFullWidth.INSTANCE);
            break;
         case 2:
            var4.add(new RowLayout$Grid(2));
      }

      return CollectionsKt.build(var3);
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$820d69f83065bffe(
      Block var0, boolean var1, RoundedCornerShape var2, long var3, long var5, float var7, Function1 var8, Modifier var9, Composer var10, int var11, int var12
   ) {
      var10 = var10.startRestartGroup(-2147444412);
      ComposerKt.sourceInformation(
         var10, "C(SingleMediaItem)P(1,7,4,0:c#ui.graphics.Color,2:c#ui.graphics.Color,3:c#ui.unit.Dp,6)207@8560L1210:GroupedMedia.kt#hr3bi5"
      );
      int var13 = var11;
      if ((var12 & 1) != 0) {
         var13 |= 6;
      } else if ((var11 & 6) == 0) {
         var13 |= var10.changedInstance(var0) ? 4 : 2;
      }

      if ((var12 & 2) != 0) {
         var13 |= 48;
      } else if ((var11 & 48) == 0) {
         var13 |= var10.changed(var1) ? 32 : 16;
      }

      if ((var12 & 4) != 0) {
         var13 |= 384;
      } else if ((var11 & 384) == 0) {
         var13 |= var10.changed(var2) ? 256 : 128;
      }

      if ((var12 & 8) != 0) {
         var13 |= 3072;
      } else if ((var11 & 3072) == 0) {
         var13 |= var10.changed(var3) ? 2048 : 1024;
      }

      if ((var12 & 16) != 0) {
         var13 |= 24576;
      } else if ((var11 & 24576) == 0) {
         var13 |= var10.changed(var5) ? 16384 : 8192;
      }

      if ((var12 & 32) != 0) {
         var13 |= 196608;
      } else if ((var11 & 196608) == 0) {
         var13 |= var10.changed(var7) ? 131072 : 65536;
      }

      if ((var12 & 64) != 0) {
         var13 |= 1572864;
      } else if ((var11 & 1572864) == 0) {
         var13 |= var10.changedInstance(var8) ? 1048576 : 524288;
      }

      if ((var12 & 128) != 0) {
         var13 |= 12582912;
      } else if ((var11 & 12582912) == 0) {
         var13 |= var10.changed(var9) ? 8388608 : 4194304;
      }

      if ((var13 & 4793491) == 4793490 && var10.getSkipping()) {
         var10.skipToGroupEnd();
      } else {
         if ((var12 & 128) != 0) {
            var9 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2147444412, var13, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SingleMediaItem (GroupedMedia.kt:206)"
            );
         }

         Modifier var14 = BorderKt.recovered$1227359600bc50fd(ClipKt.clip(BackgroundKt.recovered$60bc47fb440df483(var9, var3, var2), var2), var7, var5, var2);
         Alignment var15 = Alignment.Companion.getCenter();
         byte var17 = 48;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var10, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var16 = false;
         MeasurePolicy var19 = BoxKt.maybeCachedBoxMeasurePolicy(var15, var16);
         Modifier var20 = var14;
         MeasurePolicy var21 = var19;
         Composer var22 = var10;
         int var23 = 112 & var17 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var37 = 6 | 112 & var17 >> 6;
         Composer var38 = var35;
         BoxScope var39 = BoxScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, -1482732813, "C215@8855L131:GroupedMedia.kt#hr3bi5");
         Modifier var41 = var39.matchParentSize(Modifier.Companion);
         SingleImage(var0, var8, var41, var38, 14 & var13 | 112 & var13 >> 15, 0);
         var38.startReplaceGroup(-1294750345);
         ComposerKt.sourceInformation(var38, "227@9238L6,223@9085L669");
         if (var1) {
            Modifier var10000 = Modifier.Companion;
            int var74 = 48;
            int var42 = 0;
            var41 = BackgroundKt.recovered$60bc47fb440df483(
               SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var74)),
               IntercomTheme.INSTANCE.getColors(var38, IntercomTheme.$stable).recovered$03091cedb7ddd0e0(),
               RoundedCornerShapeKt.getCircleShape()
            );
            Alignment var76 = Alignment.Companion.getCenter();
            byte var43 = 48;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var38, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var76, propagateMinConstraints$ivx);
            Modifier var47 = var41;
            MeasurePolicy var48 = measurePolicy$ivx;
            Composer var49 = var38;
            int var50 = 112 & var43 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var49, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var49, 0));
            CompositionLocalMap localMap$iv$ivx = var49.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var49, var47);
            Function0 var55 = ComposeUiNode.Companion.getConstructor();
            Composer var56 = var49;
            int var57 = 6 | 896 & var50 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var56, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var56.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var56.startReusableNode();
            if (var56.getInserting()) {
               var56.createNode(var55);
            } else {
               var56.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var56);
            int var60/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var48, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var61 = 14 & var57 >> 6;
            Composer $composer$ivx = var56;
            int var63/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var64 = 6 | 112 & var43 >> 6;
            Composer var65 = $composer$ivx;
            BoxScope var66 = BoxScopeInstance.INSTANCE;
            int var67 = 0;
            ComposerKt.sourceInformationMarkerStart(var65, 805172648, "C234@9533L51,236@9682L6,232@9427L313:GroupedMedia.kt#hr3bi5");
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 28;
            int $i$f$getDpx = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            $this$dp$ivx = 1;
            $i$f$getDpx = 0;
            Modifier var70 = OffsetKt.recovered$c9c0be2809f7995f(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 2, null);
            Painter var78 = PainterResources_androidKt.painterResource(drawable.intercom_ic_play, var65, 0);
            long var71 = IntercomTheme.INSTANCE.getColors(var65, IntercomTheme.$stable).recovered$2a20e1a7e3ec768f();
            IconKt.recovered$e00adffb7fe45b02(var78, "Play Video", var70, var71, var65, 432 | Painter.$stable, 0);
            ComposerKt.sourceInformationMarkerEnd(var65);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var56.endNode();
            ComposerKt.sourceInformationMarkerEnd(var56);
            ComposerKt.sourceInformationMarkerEnd(var49);
            ComposerKt.sourceInformationMarkerEnd(var38);
         }

         var38.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var10);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var82 = var10.endRestartGroup();
      if (var82 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_756_0 = var0;
         final boolean recoveredCapture_756_1 = var1;
         final androidx.compose.foundation.shape.RoundedCornerShape recoveredCapture_756_2 = var2;
         final long recoveredCapture_756_3 = var3;
         final long recoveredCapture_756_4 = var5;
         final float recoveredCapture_756_5 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_756_6 = var8;
         final androidx.compose.ui.Modifier recoveredCapture_756_7 = var9;
         final int recoveredCapture_756_8 = var11;
         final int recoveredCapture_756_9 = var12;
         var82.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_756_0, recoveredArgument_756_1) -> GroupedMediaKt.SingleMediaItem_Snr_uVM$lambda$14(recoveredCapture_756_0, recoveredCapture_756_1, recoveredCapture_756_2, recoveredCapture_756_3, recoveredCapture_756_4, recoveredCapture_756_5, recoveredCapture_756_6, recoveredCapture_756_7, recoveredCapture_756_8, recoveredCapture_756_9, (androidx.compose.runtime.Composer)recoveredArgument_756_0, ((java.lang.Integer)recoveredArgument_756_1).intValue())));
      }
   }

   @Composable
   public static final void SingleImage(@NotNull Block var0, @NotNull Function1 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var0, "block");
      Intrinsics.checkNotNullParameter(var1, "onClick");
      var3 = var3.startRestartGroup(-1149784997);
      ComposerKt.sourceInformation(var3, "C(SingleImage)P(!1,2)250@9954L22,251@10028L7,256@10225L7,249@9904L485:GroupedMedia.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1149784997, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SingleImage (GroupedMedia.kt:248)");
         }

         Modifier var10000 = var2;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var3.startReplaceGroup(668744382);
         ComposerKt.sourceInformation(var3, "CC(remember):GroupedMedia.kt#9igjgp");
         Composer var9 = var3;
         boolean var10 = (var6 & 112) == 32 | var3.changedInstance(var0);
         int var11 = 0;
         Object var12 = var9.rememberedValue();
         int var13 = 0;
         Object var10005;
         if (!var10 && var12 != Composer.Companion.getEmpty()) {
            var10005 = var12;
         } else {
            Object var20 = null;
            Object var19 = null;
            Object var18 = null;
            boolean var17 = false;
            Modifier var16 = var2;
            int var14 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_754_0 = var1;
            final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_754_1 = var0;
            Function0 var21 = ((kotlin.jvm.functions.Function0) () -> GroupedMediaKt.SingleImage$lambda$16$lambda$15(recoveredCapture_754_0, recoveredCapture_754_1));
            var10000 = var16;
            var10001 = var17;
            var10002 = var18;
            var10003 = var19;
            var10004 = var20;
            Object var15 = var21;
            var9.updateRememberedValue(var15);
            var10005 = var15;
         }

         Function0 var8 = (Function0)var10005;
         var3.endReplaceGroup();
         Modifier var7 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var8, 15, null
         );
         CompositionLocal var24 = AndroidCompositionLocals_androidKt.getLocalContext();
         var10 = false;
         var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         var12 = var3.consume(var24);
         ComposerKt.sourceInformationMarkerEnd(var3);
         ImageRequest var23 = new Builder((Context)var12)
            .data(var0.getUrl())
            .crossfade(true)
            .error(io.intercom.android.sdk.R.drawable.intercom_image_load_failed)
            .build();
         CompositionLocal var27 = AndroidCompositionLocals_androidKt.getLocalContext();
         var11 = 0;
         var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var36 = var3.consume(var27);
         ComposerKt.sourceInformationMarkerEnd(var3);
         ImageLoader var25 = IntercomImageLoaderKt.getImageLoader((Context)var36);
         var3.startReplaceGroup(668754697);
         ComposerKt.sourceInformation(var3, "*257@10285L53");
         var12 = var0.getText();
         CharSequence var38;
         if (StringsKt.isBlank((CharSequence)var12)) {
            int var37 = 0;
            var38 = StringResources_androidKt.stringResource(string.intercom_image_attached, var3, 0);
         } else {
            var38 = (CharSequence)var12;
         }

         String var31 = (String)var38;
         var3.endReplaceGroup();
         String var28 = var31;
         ContentScale var32 = ContentScale.Companion.getCrop();
         AsyncImageKt.recovered$b6a46e909c1fcc39(var23, var28, var25, var7, null, null, null, var32, 0.0F, null, 0, false, null, var3, 12582912, 0, 8048);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var39 = var3.endRestartGroup();
      if (var39 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_755_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_755_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_755_2 = var2;
         final int recoveredCapture_755_3 = var4;
         final int recoveredCapture_755_4 = var5;
         var39.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_755_0, recoveredArgument_755_1) -> GroupedMediaKt.SingleImage$lambda$18(recoveredCapture_755_0, recoveredCapture_755_1, recoveredCapture_755_2, recoveredCapture_755_3, recoveredCapture_755_4, (androidx.compose.runtime.Composer)recoveredArgument_755_0, ((java.lang.Integer)recoveredArgument_755_1).intValue())));
      }
   }

   private static final RoundedCornerShape getMediaShapeForPosition(boolean var0, boolean var1, boolean var2, boolean var3, RoundedCornerShape var4) {
      int var6 = 4;
      int var7 = 0;
      CornerSize var5 = CornerSizeKt.recovered$ea3a4e3d7147c535(Dp.recovered$95b3d103663bf060(var6));
      CornerSize var10 = var0 ? var4.getTopStart() : var5;
      CornerSize var11 = var1 ? var4.getTopEnd() : var5;
      CornerSize var8 = var2 ? var4.getBottomStart() : var5;
      CornerSize var9 = var3 ? var4.getBottomEnd() : var5;
      return new RoundedCornerShape(var10, var11, var9, var8);
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith1ImagePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1318349613);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith1ImagePreview)286@11288L303:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1318349613, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith1ImagePreview (GroupedMedia.kt:285)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_746_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_746_0, recoveredArgument_746_1) -> GroupedMediaKt.GroupedMediaWith1ImagePreview$lambda$19(recoveredCapture_746_0, (androidx.compose.runtime.Composer)recoveredArgument_746_0, ((java.lang.Integer)recoveredArgument_746_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith1VideoPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-3964941);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith1VideoPreview)301@11676L298:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -3964941, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith1VideoPreview (GroupedMedia.kt:300)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_747_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_747_0, recoveredArgument_747_1) -> GroupedMediaKt.GroupedMediaWith1VideoPreview$lambda$20(recoveredCapture_747_0, (androidx.compose.runtime.Composer)recoveredArgument_747_0, ((java.lang.Integer)recoveredArgument_747_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith2ImagesPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1377228095);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith2ImagesPreview)316@12060L372:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1377228095, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith2ImagesPreview (GroupedMedia.kt:315)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_748_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_748_0, recoveredArgument_748_1) -> GroupedMediaKt.GroupedMediaWith2ImagesPreview$lambda$21(recoveredCapture_748_0, (androidx.compose.runtime.Composer)recoveredArgument_748_0, ((java.lang.Integer)recoveredArgument_748_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith3ImagesPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1786952576);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith3ImagesPreview)332@12518L455:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1786952576, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith3ImagesPreview (GroupedMedia.kt:331)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$a427d7fccaa25a17(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_749_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_749_0, recoveredArgument_749_1) -> GroupedMediaKt.GroupedMediaWith3ImagesPreview$lambda$22(recoveredCapture_749_0, (androidx.compose.runtime.Composer)recoveredArgument_749_0, ((java.lang.Integer)recoveredArgument_749_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith4MixedItemsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2015086798);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith4MixedItemsPreview)349@13063L525:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2015086798, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith4MixedItemsPreview (GroupedMedia.kt:348)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$abc66e26750555d7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_750_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_750_0, recoveredArgument_750_1) -> GroupedMediaKt.GroupedMediaWith4MixedItemsPreview$lambda$23(recoveredCapture_750_0, (androidx.compose.runtime.Composer)recoveredArgument_750_0, ((java.lang.Integer)recoveredArgument_750_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith5MixedItemsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1203327089);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith5MixedItemsPreview)367@13678L605:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1203327089, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith5MixedItemsPreview (GroupedMedia.kt:366)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$ce33619ac1c358ef(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_751_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_751_0, recoveredArgument_751_1) -> GroupedMediaKt.GroupedMediaWith5MixedItemsPreview$lambda$24(recoveredCapture_751_0, (androidx.compose.runtime.Composer)recoveredArgument_751_0, ((java.lang.Integer)recoveredArgument_751_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith6MixedItemsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-126773680);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith6MixedItemsPreview)386@14373L673:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -126773680, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith6MixedItemsPreview (GroupedMedia.kt:385)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$46a4deae0c40d2ce(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_752_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_752_0, recoveredArgument_752_1) -> GroupedMediaKt.GroupedMediaWith6MixedItemsPreview$lambda$25(recoveredCapture_752_0, (androidx.compose.runtime.Composer)recoveredArgument_752_0, ((java.lang.Integer)recoveredArgument_752_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith7MixedItemsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(949779729);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith7MixedItemsPreview)406@15136L752:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               949779729, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith7MixedItemsPreview (GroupedMedia.kt:405)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$f9b69ae6c3449124(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_753_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_753_0, recoveredArgument_753_1) -> GroupedMediaKt.GroupedMediaWith7MixedItemsPreview$lambda$26(recoveredCapture_753_0, (androidx.compose.runtime.Composer)recoveredArgument_753_0, ((java.lang.Integer)recoveredArgument_753_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedMediaWith10MixedItemsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(254529965);
      ComposerKt.sourceInformation(var0, "C(GroupedMediaWith10MixedItemsPreview)427@15979L978:GroupedMedia.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               254529965, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedMediaWith10MixedItemsPreview (GroupedMedia.kt:426)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedMediaKt.INSTANCE.recovered$ada631b6fddb2439(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_745_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_745_0, recoveredArgument_745_1) -> GroupedMediaKt.GroupedMediaWith10MixedItemsPreview$lambda$27(recoveredCapture_745_0, (androidx.compose.runtime.Composer)recoveredArgument_745_0, ((java.lang.Integer)recoveredArgument_745_1).intValue())));
      }
   }

   private static final Unit GroupedMedia$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMedia$lambda$3$lambda$2(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMedia$lambda$9(
      List var0, RoundedCornerShape var1, Modifier var2, Function1 var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      GroupedMedia(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit SingleMediaItem_Snr_uVM$lambda$14(
      Block var0,
      boolean var1,
      RoundedCornerShape var2,
      long var3,
      long var5,
      float var7,
      Function1 var8,
      Modifier var9,
      int var10,
      int var11,
      Composer var12,
      int var13
   ) {
      recovered$820d69f83065bffe(var0, var1, var2, var3, var5, var7, var8, var9, var12, RecomposeScopeImplKt.updateChangedFlags(var10 | 1), var11);
      return Unit.INSTANCE;
   }

   private static final Unit SingleImage$lambda$16$lambda$15(Function1 var0, Block var1) {
      String var10001 = var1.getUrl();
      Intrinsics.checkNotNullExpressionValue(var10001, "getUrl(...)");
      var0.invoke(var10001);
      return Unit.INSTANCE;
   }

   private static final Unit SingleImage$lambda$18(Block var0, Function1 var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      SingleImage(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith1ImagePreview$lambda$19(int var0, Composer var1, int var2) {
      GroupedMediaWith1ImagePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith1VideoPreview$lambda$20(int var0, Composer var1, int var2) {
      GroupedMediaWith1VideoPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith2ImagesPreview$lambda$21(int var0, Composer var1, int var2) {
      GroupedMediaWith2ImagesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith3ImagesPreview$lambda$22(int var0, Composer var1, int var2) {
      GroupedMediaWith3ImagesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith4MixedItemsPreview$lambda$23(int var0, Composer var1, int var2) {
      GroupedMediaWith4MixedItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith5MixedItemsPreview$lambda$24(int var0, Composer var1, int var2) {
      GroupedMediaWith5MixedItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith6MixedItemsPreview$lambda$25(int var0, Composer var1, int var2) {
      GroupedMediaWith6MixedItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith7MixedItemsPreview$lambda$26(int var0, Composer var1, int var2) {
      GroupedMediaWith7MixedItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedMediaWith10MixedItemsPreview$lambda$27(int var0, Composer var1, int var2) {
      GroupedMediaWith10MixedItemsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
