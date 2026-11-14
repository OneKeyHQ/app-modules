package io.intercom.android.sdk.m5.home.ui.header;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
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
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Brush.Companion;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.SubcomposeAsyncImageKt;
import coil.compose.AsyncImagePainter.State.Success;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ThemeUtils;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u001a9\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0001¢\u0006\u0004\b\t\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\u0012\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\u0013",
   d2 = {
         "HomeHeaderBackdrop",
         "",
         "headerHeight",
         "Landroidx/compose/ui/unit/Dp;",
         "backdropStyle",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "backdropStyleDark",
         "onImageLoaded",
         "Lkotlin/Function0;",
         "HomeHeaderBackdrop-AjpBEmI",
         "(FLio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "SolidHeaderBackdropPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "GradientHeaderBackdropPreview",
         "SolidHeaderBackdropWithFadePreview",
         "GradientHeaderBackdropWithFadePreview",
         "CrossTypeSolidToGradientPreview",
         "CrossTypeGradientToSolidPreview",
         "ConsistentFadeBehaviorPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeHeaderBackdrop.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/HomeHeaderBackdropKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,242:1\n70#2:243\n66#2,10:244\n77#2:293\n81#3,6:254\n88#3,6:269\n96#3:292\n402#4,9:260\n411#4:275\n412#4,2:290\n118#5:276\n118#5:279\n118#5:287\n118#5:289\n49#6:277\n49#6:280\n49#6:288\n75#7:278\n1047#8,6:281\n*S KotlinDebug\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/HomeHeaderBackdropKt\n*L\n40#1:243\n40#1:244,10\n40#1:293\n40#1:254,6\n40#1:269,6\n40#1:292\n40#1:260,9\n40#1:275\n40#1:290,2\n48#1:276\n65#1:279\n76#1:287\n89#1:289\n48#1:277\n65#1:280\n76#1:288\n53#1:278\n67#1:281,6\n*E\n"
)
public final class HomeHeaderBackdropKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$b457fc8554473e41(
      float var0,
      @NotNull HomeUiState$Content$ContentHeader$HeaderBackdropStyle var1,
      @Nullable HomeUiState$Content$ContentHeader$HeaderBackdropStyle var2,
      @NotNull Function0 var3,
      @Nullable Composer var4,
      int var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "backdropStyle");
      Intrinsics.checkNotNullParameter(var3, "onImageLoaded");
      var4 = var4.startRestartGroup(1754935872);
      ComposerKt.sourceInformation(var4, "C(HomeHeaderBackdrop)P(2:c#ui.unit.Dp)32@1314L19,39@1482L2281:HomeHeaderBackdrop.kt#rkbaxi");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1754935872, var7, -1, "io.intercom.android.sdk.m5.home.ui.header.HomeHeaderBackdrop (HomeHeaderBackdrop.kt:31)");
         }

         boolean var8 = ThemeUtils.INSTANCE.isDarkModeCompose$intercom_sdk_base_release(var4, 6);
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle var9 = var8 && var2 != null ? var2 : var1;
         byte var13 = 0;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var10 = Modifier.Companion;
         Alignment var11 = Alignment.Companion.getTopStart();
         boolean var12 = false;
         MeasurePolicy var15 = BoxKt.maybeCachedBoxMeasurePolicy(var11, var12);
         Modifier var16 = var10;
         MeasurePolicy var17 = var15;
         Composer var18 = var4;
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
         ComposerKt.sourceInformationMarkerStart(var34, 1047400073, "C:HomeHeaderBackdrop.kt#rkbaxi");
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle var37 = var9;
         if (var37 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient) {
            var34.startReplaceGroup(1047420935);
            ComposerKt.sourceInformation(var34, "42@1593L374");
            Modifier var89 = BackgroundKt.background$default(
               Modifier.Companion,
               Companion.recovered$363f709e34bf7346(
                  Brush.Companion, ((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient)var9).getColors(), 0L, 0L, 0, 14, null
               ),
               null,
               0.0F,
               6,
               null
            );
            float var92;
            if (((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient)var9).getFade()) {
               int var68 = 160;
               int var77 = 0;
               var92 = Dp.recovered$95b3d103663bf060(var68);
            } else {
               int var69 = 80;
               int var78 = 0;
               var92 = Dp.recovered$95b3d103663bf060(var69);
            }

            float var70 = var92;
            int var79 = 0;
            BoxKt.Box(
               SizeKt.fillMaxWidth$default(SizeKt.recovered$3f6c6a76aa292717(var89, Dp.recovered$95b3d103663bf060(var0 + var70)), 0.0F, 1, null), var34, 0
            );
            var34.endReplaceGroup();
         } else if (var37 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image) {
            var34.startReplaceGroup(1047880603);
            ComposerKt.sourceInformation(var34, "52@2071L7,66@2732L63,54@2096L718");
            CompositionLocal var75 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var40 = 0;
            int var41 = 0;
            ComposerKt.sourceInformationMarkerStart(var34, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var42 = var34.consume(var75);
            ComposerKt.sourceInformationMarkerEnd(var34);
            Context var67 = (Context)var42;
            ImageRequest var76 = new Builder(var67)
               .data(((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image)var9).getImageUrl())
               .crossfade(true)
               .build();
            ImageLoader var80 = IntercomImageLoaderKt.getImageLoader(var67);
            ContentScale var81 = ContentScale.Companion.getCrop();
            Modifier var87 = BackgroundKt.recovered$42a3174180b3fb7a(
               Modifier.Companion, ((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image)var9).recovered$b7af0fa3dc882892(), null, 2, null
            );
            int var43 = 80;
            int var44 = 0;
            float var83 = Dp.recovered$95b3d103663bf060(var43);
            var44 = 0;
            Modifier var82 = SizeKt.fillMaxWidth$default(SizeKt.recovered$3f6c6a76aa292717(var87, Dp.recovered$95b3d103663bf060(var0 + var83)), 0.0F, 1, null);
            ImageRequest var88 = var76;
            Object var91 = null;
            ImageLoader var10002 = var80;
            Modifier var10003 = var82;
            Object var10004 = null;
            Object var10005 = null;
            Object var10006 = null;
            Object var10007 = null;
            Object var10008 = null;
            var34.startReplaceGroup(-1074554204);
            ComposerKt.sourceInformation(var34, "CC(remember):HomeHeaderBackdrop.kt#9igjgp");
            Composer var86 = var34;
            boolean var45 = (var7 & 7168) == 2048;
            int var46 = 0;
            Object var47 = var86.rememberedValue();
            int var48 = 0;
            Object var10009;
            if (!var45 && var47 != Composer.Companion.getEmpty()) {
               var10009 = var47;
            } else {
               Object var49 = null;
               Object var50 = null;
               Object var51 = null;
               Object var52 = null;
               Object var53 = null;
               Modifier var54 = var82;
               ImageLoader var55 = var80;
               Object var56 = null;
               ImageRequest var57 = var76;
               int var58 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_952_0 = var3;
               Function1 var59 = ((kotlin.jvm.functions.Function1) (recoveredArgument_952_0) -> HomeHeaderBackdropKt.HomeHeaderBackdrop_AjpBEmI$lambda$2$lambda$1$lambda$0(recoveredCapture_952_0, (coil.compose.AsyncImagePainter.State.Success)recoveredArgument_952_0));
               var88 = var57;
               var91 = var56;
               var10002 = var55;
               var10003 = var54;
               var10004 = var53;
               var10005 = var52;
               var10006 = var51;
               var10007 = var50;
               var10008 = var49;
               Object var60 = var59;
               var86.updateRememberedValue(var60);
               var10009 = var60;
            }

            Function1 var84 = (Function1)var10009;
            var34.endReplaceGroup();
            SubcomposeAsyncImageKt.recovered$9e62f4c19284676e(
               var88,
               (String)var91,
               var10002,
               var10003,
               (Function1)var10004,
               (Function4)var10005,
               (Function4)var10006,
               (Function4)var10007,
               (Function1)var10008,
               var84,
               null,
               null,
               var81,
               0.0F,
               null,
               0,
               false,
               null,
               var34,
               48,
               384,
               257520
            );
            var34.endReplaceGroup();
         } else {
            if (!(var37 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid)) {
               var34.startReplaceGroup(-1074592113);
               var34.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var34.startReplaceGroup(1048705854);
            ComposerKt.sourceInformation(var34, "72@2891L287");
            Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
               Modifier.Companion, ((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid)var9).recovered$6f15fcf6c761ad06(), null, 2, null
            );
            float var10001;
            if (((HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid)var9).getFade()) {
               int var38 = 160;
               int var39 = 0;
               var10001 = Dp.recovered$95b3d103663bf060(var38);
            } else {
               int var65 = 80;
               int var73 = 0;
               var10001 = Dp.recovered$95b3d103663bf060(var65);
            }

            float var66 = var10001;
            int var74 = 0;
            BoxKt.Box(
               SizeKt.fillMaxWidth$default(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var0 + var66)), 0.0F, 1, null), var34, 0
            );
            var34.endReplaceGroup();
         }

         var34.startReplaceGroup(-1074538393);
         ComposerKt.sourceInformation(var34, "85@3465L6,81@3258L489");
         if (var9.getFade()) {
            Modifier var93 = Modifier.Companion;
            Companion var95 = Brush.Companion;
            Color[] var62 = new Color[]{
               Color.recovered$f46ef195ad4a2363(Color.Companion.recovered$562f21ab17d9b4c7()),
               Color.recovered$f46ef195ad4a2363(IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05())
            };
            var93 = BackgroundKt.background$default(
               var93, Companion.recovered$ea3ca130c5d0d624(var95, CollectionsKt.listOf(var62), 0.0F, 0.0F, 0, 14, null), null, 0.0F, 6, null
            );
            float var96;
            if (var9 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image) {
               int var63 = 80;
               int var71 = 0;
               var96 = Dp.recovered$95b3d103663bf060(var63);
            } else {
               int var64 = 160;
               int var72 = 0;
               var96 = Dp.recovered$95b3d103663bf060(var64);
            }

            BoxKt.Box(
               var35.align(SizeKt.fillMaxWidth$default(SizeKt.recovered$3f6c6a76aa292717(var93, var96), 0.0F, 1, null), Alignment.Companion.getBottomCenter()),
               var34,
               0
            );
         }

         var34.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var90 = var4.endRestartGroup();
      if (var90 != null) {
         final float recoveredCapture_951_0 = var0;
         final io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle recoveredCapture_951_1 = var1;
         final io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle recoveredCapture_951_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_951_3 = var3;
         final int recoveredCapture_951_4 = var5;
         final int recoveredCapture_951_5 = var6;
         var90.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_951_0, recoveredArgument_951_1) -> HomeHeaderBackdropKt.HomeHeaderBackdrop_AjpBEmI$lambda$3(recoveredCapture_951_0, recoveredCapture_951_1, recoveredCapture_951_2, recoveredCapture_951_3, recoveredCapture_951_4, recoveredCapture_951_5, (androidx.compose.runtime.Composer)recoveredArgument_951_0, ((java.lang.Integer)recoveredArgument_951_1).intValue())));
      }
   }

   @Container({@Preview(name = "Solid Light Mode", uiMode = 16), @Preview(name = "Solid Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SolidHeaderBackdropPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(784552236);
      ComposerKt.sourceInformation(var0, "C(SolidHeaderBackdropPreview)100@3981L460:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(784552236, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.SolidHeaderBackdropPreview (HomeHeaderBackdrop.kt:99)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_949_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_949_0, recoveredArgument_949_1) -> HomeHeaderBackdropKt.SolidHeaderBackdropPreview$lambda$4(recoveredCapture_949_0, (androidx.compose.runtime.Composer)recoveredArgument_949_0, ((java.lang.Integer)recoveredArgument_949_1).intValue())));
      }
   }

   @Container({@Preview(name = "Gradient Light Mode", uiMode = 16), @Preview(name = "Gradient Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GradientHeaderBackdropPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1564631091);
      ComposerKt.sourceInformation(var0, "C(GradientHeaderBackdropPreview)120@4668L592:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1564631091, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.GradientHeaderBackdropPreview (HomeHeaderBackdrop.kt:119)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_947_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_947_0, recoveredArgument_947_1) -> HomeHeaderBackdropKt.GradientHeaderBackdropPreview$lambda$5(recoveredCapture_947_0, (androidx.compose.runtime.Composer)recoveredArgument_947_0, ((java.lang.Integer)recoveredArgument_947_1).intValue())));
      }
   }

   @Container({@Preview(name = "Solid with fade Light Mode", uiMode = 16), @Preview(name = "Solid with fade Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SolidHeaderBackdropWithFadePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(14975022);
      ComposerKt.sourceInformation(var0, "C(SolidHeaderBackdropWithFadePreview)142@5506L458:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               14975022, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.SolidHeaderBackdropWithFadePreview (HomeHeaderBackdrop.kt:141)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_950_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_950_0, recoveredArgument_950_1) -> HomeHeaderBackdropKt.SolidHeaderBackdropWithFadePreview$lambda$6(recoveredCapture_950_0, (androidx.compose.runtime.Composer)recoveredArgument_950_0, ((java.lang.Integer)recoveredArgument_950_1).intValue())));
      }
   }

   @Container({@Preview(name = "Gradient with fade Light Mode", uiMode = 16), @Preview(name = "Gradient with fade Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GradientHeaderBackdropWithFadePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-205873713);
      ComposerKt.sourceInformation(var0, "C(GradientHeaderBackdropWithFadePreview)162@6219L590:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -205873713, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.GradientHeaderBackdropWithFadePreview (HomeHeaderBackdrop.kt:161)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_948_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_948_0, recoveredArgument_948_1) -> HomeHeaderBackdropKt.GradientHeaderBackdropWithFadePreview$lambda$7(recoveredCapture_948_0, (androidx.compose.runtime.Composer)recoveredArgument_948_0, ((java.lang.Integer)recoveredArgument_948_1).intValue())));
      }
   }

   @Container(
      {@Preview(name = "Cross-type - Solid to Gradient Light Mode", uiMode = 16), @Preview(name = "Cross-type - Solid to Gradient Dark Mode", uiMode = 32)}
   )
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CrossTypeSolidToGradientPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1289954070);
      ComposerKt.sourceInformation(var0, "C(CrossTypeSolidToGradientPreview)184@7082L513:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1289954070, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.CrossTypeSolidToGradientPreview (HomeHeaderBackdrop.kt:183)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_946_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_946_0, recoveredArgument_946_1) -> HomeHeaderBackdropKt.CrossTypeSolidToGradientPreview$lambda$8(recoveredCapture_946_0, (androidx.compose.runtime.Composer)recoveredArgument_946_0, ((java.lang.Integer)recoveredArgument_946_1).intValue())));
      }
   }

   @Container(
      {@Preview(name = "Cross-type - Gradient to Solid Light Mode", uiMode = 16), @Preview(name = "Cross-type - Gradient to Solid Dark Mode", uiMode = 32)}
   )
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CrossTypeGradientToSolidPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(60882784);
      ComposerKt.sourceInformation(var0, "C(CrossTypeGradientToSolidPreview)205@7868L524:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               60882784, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.CrossTypeGradientToSolidPreview (HomeHeaderBackdrop.kt:204)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_945_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_945_0, recoveredArgument_945_1) -> HomeHeaderBackdropKt.CrossTypeGradientToSolidPreview$lambda$9(recoveredCapture_945_0, (androidx.compose.runtime.Composer)recoveredArgument_945_0, ((java.lang.Integer)recoveredArgument_945_1).intValue())));
      }
   }

   @Container({@Preview(name = "Consistent Fade Behavior Light Mode", uiMode = 16), @Preview(name = "Consistent Fade Behavior Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConsistentFadeBehaviorPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-551060646);
      ComposerKt.sourceInformation(var0, "C(ConsistentFadeBehaviorPreview)226@8651L458:HomeHeaderBackdrop.kt#rkbaxi");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -551060646, var1, -1, "io.intercom.android.sdk.m5.home.ui.header.ConsistentFadeBehaviorPreview (HomeHeaderBackdrop.kt:225)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeHeaderBackdropKt.INSTANCE.recovered$3faf0e22fa74c6a7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_944_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_944_0, recoveredArgument_944_1) -> HomeHeaderBackdropKt.ConsistentFadeBehaviorPreview$lambda$10(recoveredCapture_944_0, (androidx.compose.runtime.Composer)recoveredArgument_944_0, ((java.lang.Integer)recoveredArgument_944_1).intValue())));
      }
   }

   private static final Unit HomeHeaderBackdrop_AjpBEmI$lambda$2$lambda$1$lambda$0(Function0 var0, Success var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit HomeHeaderBackdrop_AjpBEmI$lambda$3(
      float var0,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var1,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var2,
      Function0 var3,
      int var4,
      int var5,
      Composer var6,
      int var7
   ) {
      recovered$b457fc8554473e41(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit SolidHeaderBackdropPreview$lambda$4(int var0, Composer var1, int var2) {
      SolidHeaderBackdropPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GradientHeaderBackdropPreview$lambda$5(int var0, Composer var1, int var2) {
      GradientHeaderBackdropPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SolidHeaderBackdropWithFadePreview$lambda$6(int var0, Composer var1, int var2) {
      SolidHeaderBackdropWithFadePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GradientHeaderBackdropWithFadePreview$lambda$7(int var0, Composer var1, int var2) {
      GradientHeaderBackdropWithFadePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CrossTypeSolidToGradientPreview$lambda$8(int var0, Composer var1, int var2) {
      CrossTypeSolidToGradientPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CrossTypeGradientToSolidPreview$lambda$9(int var0, Composer var1, int var2) {
      CrossTypeGradientToSolidPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ConsistentFadeBehaviorPreview$lambda$10(int var0, Composer var1, int var2) {
      ConsistentFadeBehaviorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
