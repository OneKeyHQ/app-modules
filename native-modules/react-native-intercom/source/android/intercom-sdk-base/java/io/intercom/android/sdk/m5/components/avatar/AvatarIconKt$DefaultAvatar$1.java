package io.intercom.android.sdk.m5.components.avatar;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.SubcomposeAsyncImageKt;
import coil.compose.AsyncImagePainter.State.Error;
import coil.compose.AsyncImagePainter.State.Loading;
import coil.compose.AsyncImagePainter.State.Success;
import io.intercom.android.sdk.m5.shapes.CutAvatarWithIndicatorShape;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nAvatarIcon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarIconKt$DefaultAvatar$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,454:1\n118#2:455\n70#3:456\n67#3,9:457\n77#3:510\n81#4,6:466\n88#4,6:481\n96#4:509\n402#5,9:472\n411#5:487\n412#5,2:507\n75#6:488\n1047#7,6:489\n1047#7,6:495\n1047#7,6:501\n*S KotlinDebug\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarIconKt$DefaultAvatar$1\n*L\n165#1:455\n171#1:456\n171#1:457,9\n171#1:510\n171#1:466,6\n171#1:481,6\n171#1:509\n171#1:472,9\n171#1:487\n171#1:507,2\n184#1:488\n195#1:489,6\n205#1:495,6\n200#1:501,6\n*E\n"
)
final class AvatarIconKt$DefaultAvatar$1 implements Function3 {
   // $VF: synthetic field
   final boolean $isActive;
   // $VF: synthetic field
   final Shape $shape;
   // $VF: synthetic field
   final boolean $shouldDrawBorder;
   // $VF: synthetic field
   final MutableState $indicatorSize$delegate;
   // $VF: synthetic field
   final MutableState $cutShape$delegate;
   // $VF: synthetic field
   final MutableState $backgroundColor$delegate;
   // $VF: synthetic field
   final AvatarWrapper $avatarWrapper;
   // $VF: synthetic field
   final long $defaultBackgroundColor;
   // $VF: synthetic field
   final Color $customBackgroundColor;
   // $VF: synthetic field
   final long $textColor;
   // $VF: synthetic field
   final long $placeHolderTextSize;

   AvatarIconKt$DefaultAvatar$1(
      boolean var1,
      Shape var2,
      boolean var3,
      MutableState var4,
      MutableState var5,
      MutableState var6,
      AvatarWrapper var7,
      long var8,
      Color var10,
      long var11,
      long var13
   ) {
      this.$isActive = var1;
      this.$shape = var2;
      this.$shouldDrawBorder = var3;
      this.$indicatorSize$delegate = var4;
      this.$cutShape$delegate = var5;
      this.$backgroundColor$delegate = var6;
      this.$avatarWrapper = var7;
      this.$defaultBackgroundColor = var8;
      this.$customBackgroundColor = var10;
      this.$textColor = var11;
      this.$placeHolderTextSize = var13;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C170@5872L1542,211@7451L162:AvatarIcon.kt#ssa3oh");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1891463123, var4, -1, "io.intercom.android.sdk.m5.components.avatar.DefaultAvatar.<anonymous> (AvatarIcon.kt:163)");
         }

         if (this.$isActive) {
            MutableState var10000 = this.$indicatorSize$delegate;
            float var10001 = var1.recovered$4e84a3b22c31a649();
            int var5 = 36;
            int var6 = 0;
            if (Dp.recovered$a09f069d48ab27f2(var10001, Dp.recovered$95b3d103663bf060(var5)) > 0) {
               var5 = 16;
               var6 = 0;
               var10001 = Dp.recovered$95b3d103663bf060(var5);
            } else {
               var5 = 8;
               var6 = 0;
               var10001 = Dp.recovered$95b3d103663bf060(var5);
            }

            AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$6(var10000, var10001);
            AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$9(
               this.$cutShape$delegate,
               new CutAvatarWithIndicatorShape(this.$shape, AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$5(this.$indicatorSize$delegate), null)
            );
         } else {
            AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$9(this.$cutShape$delegate, this.$shape);
         }

         Modifier var67 = ClipKt.clip(
            AvatarIconKt.avatarBorder(
               BackgroundKt.recovered$60bc47fb440df483(
                  var1.matchParentSize(Modifier.Companion),
                  AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$2(this.$backgroundColor$delegate),
                  AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$8(this.$cutShape$delegate)
               ),
               this.$shouldDrawBorder,
               AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$8(this.$cutShape$delegate)
            ),
            AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$8(this.$cutShape$delegate)
         );
         AvatarWrapper var8 = this.$avatarWrapper;
         long var9 = this.$defaultBackgroundColor;
         Color var11 = this.$customBackgroundColor;
         long var12 = this.$textColor;
         long var14 = this.$placeHolderTextSize;
         MutableState var16 = this.$backgroundColor$delegate;
         int var17 = 0;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var70 = Alignment.Companion.getTopStart();
         boolean var7 = false;
         MeasurePolicy var19 = BoxKt.maybeCachedBoxMeasurePolicy(var70, var7);
         Modifier var20 = var67;
         MeasurePolicy var21 = var19;
         Composer var22 = var2;
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
         ComposerKt.sourceInformationMarkerStart(
            var38, 1297417899, "C178@6201L13,183@6449L7,184@6485L143,189@6654L143,194@6827L170,204@7225L114,199@7025L170,177@6141L1263:AvatarIcon.kt#ssa3oh"
         );
         String var41 = var8.getImageUrl(var38, 0);
         Modifier var42 = SizeKt.fillMaxSize$default(var39.align(Modifier.Companion, Alignment.Companion.getCenter()), 0.0F, 1, null);
         String var43 = var8.getLabel();
         CompositionLocal var44 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivx = 0;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var47 = var38.consume(var44);
         ComposerKt.sourceInformationMarkerEnd(var38);
         ImageLoader var48 = IntercomImageLoaderKt.getImageLoader((Context)var47);
         ContentScale var71 = ContentScale.Companion.getCrop();
         String var110 = var41;
         String var112 = var43;
         ImageLoader var10002 = var48;
         Modifier var10003 = var42;
         Object var10004 = null;
         Function4 var10005 = ComposableLambdaKt.rememberComposableLambda(
            -1513639009, true, new AvatarIconKt$DefaultAvatar$1$1$1(var8, var12, var14), var38, 54
         );
         Object var10006 = null;
         Function4 var10007 = ComposableLambdaKt.rememberComposableLambda(427755177, true, new AvatarIconKt$DefaultAvatar$1$1$2(var8, var12, var14), var38, 54);
         var38.startReplaceGroup(1981535699);
         ComposerKt.sourceInformation(var38, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var75 = var38;
         boolean var78 = var38.changed(var9);
         int var49 = 0;
         Object var50 = var75.rememberedValue();
         int var51 = 0;
         Object var10008;
         if (!var78 && var50 != Composer.Companion.getEmpty()) {
            var10008 = (Function1)var50;
         } else {
            Function4 var52 = var10007;
            Object var53 = null;
            Function4 var54 = var10005;
            Object var55 = null;
            Modifier var56 = var42;
            ImageLoader var57 = var48;
            String var58 = var43;
            String var59 = var41;
            int var60 = 0;
            final long recoveredCapture_279_0 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_279_1 = var16;
            Function1 var61 = ((kotlin.jvm.functions.Function1) (recoveredArgument_279_0) -> AvatarIconKt$DefaultAvatar$1.invoke$lambda$6$lambda$1$lambda$0(recoveredCapture_279_0, recoveredCapture_279_1, (coil.compose.AsyncImagePainter.State.Loading)recoveredArgument_279_0));
            var110 = var59;
            var112 = var58;
            var10002 = var57;
            var10003 = var56;
            var10004 = var55;
            var10005 = var54;
            var10006 = var53;
            var10007 = var52;
            Object var62 = var61;
            var75.updateRememberedValue(var62);
            var10008 = (Function1)var62;
         }

         Function1 var72 = (kotlin.jvm.functions.Function1) var10008;
         var38.endReplaceGroup();
         var10008 = var72;
         var38.startReplaceGroup(1981548379);
         ComposerKt.sourceInformation(var38, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var76 = var38;
         var78 = var38.changed(var11) | var38.changed(var9);
         var49 = 0;
         var50 = var76.rememberedValue();
         var51 = 0;
         Object var10009;
         if (!var78 && var50 != Composer.Companion.getEmpty()) {
            var10009 = (Function1)var50;
         } else {
            Function1 var105 = var72;
            Function4 var87 = var10007;
            Object var89 = var10006;
            Function4 var91 = var10005;
            Object var93 = var10004;
            Modifier var95 = var10003;
            ImageLoader var97 = var10002;
            String var99 = var112;
            String var101 = var110;
            int var103 = 0;
            final androidx.compose.ui.graphics.Color recoveredCapture_280_0 = var11;
            final long recoveredCapture_280_1 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_280_2 = var16;
            Function1 var63 = ((kotlin.jvm.functions.Function1) (recoveredArgument_280_0) -> AvatarIconKt$DefaultAvatar$1.invoke$lambda$6$lambda$3$lambda$2(recoveredCapture_280_0, recoveredCapture_280_1, recoveredCapture_280_2, (coil.compose.AsyncImagePainter.State.Success)recoveredArgument_280_0));
            var110 = var101;
            var112 = var99;
            var10002 = var97;
            var10003 = var95;
            var10004 = var93;
            var10005 = var91;
            var10006 = var89;
            var10007 = var87;
            var10008 = var105;
            Object var107 = var63;
            var76.updateRememberedValue(var107);
            var10009 = (Function1)var107;
         }

         Function1 var73 = (kotlin.jvm.functions.Function1) var10009;
         var38.endReplaceGroup();
         var10009 = var73;
         var38.startReplaceGroup(1981542035);
         ComposerKt.sourceInformation(var38, "CC(remember):AvatarIcon.kt#9igjgp");
         Composer var77 = var38;
         var78 = var38.changed(var9);
         var49 = 0;
         var50 = var77.rememberedValue();
         var51 = 0;
         Object var10010;
         if (!var78 && var50 != Composer.Companion.getEmpty()) {
            var10010 = var50;
         } else {
            Function1 var109 = var73;
            Function1 var106 = (kotlin.jvm.functions.Function1) var10008;
            Function4 var88 = var10007;
            Object var90 = var10006;
            Function4 var92 = var10005;
            Object var94 = var10004;
            Modifier var96 = var10003;
            ImageLoader var98 = var10002;
            String var100 = var112;
            String var102 = var110;
            int var104 = 0;
            final long recoveredCapture_281_0 = var9;
            final androidx.compose.runtime.MutableState recoveredCapture_281_1 = var16;
            Function1 var64 = ((kotlin.jvm.functions.Function1) (recoveredArgument_281_0) -> AvatarIconKt$DefaultAvatar$1.invoke$lambda$6$lambda$5$lambda$4(recoveredCapture_281_0, recoveredCapture_281_1, (coil.compose.AsyncImagePainter.State.Error)recoveredArgument_281_0));
            var110 = var102;
            var112 = var100;
            var10002 = var98;
            var10003 = var96;
            var10004 = var94;
            var10005 = var92;
            var10006 = var90;
            var10007 = var88;
            var10008 = var106;
            var10009 = var109;
            Object var108 = var64;
            var77.updateRememberedValue(var108);
            var10010 = var108;
         }

         Function1 var74 = (Function1)var10010;
         var38.endReplaceGroup();
         SubcomposeAsyncImageKt.recovered$9e62f4c19284676e(
            var110,
            var112,
            var10002,
            var10003,
            (Function1)var10004,
            var10005,
            (Function4)var10006,
            var10007,
            (Function1)var10008,
            (Function1)var10009,
            var74,
            null,
            var71,
            0.0F,
            null,
            0,
            false,
            null,
            var38,
            12779520,
            384,
            256080
         );
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (this.$isActive) {
            AvatarIconKt.access$AvatarActiveIndicator(
               var1.align(
                  SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$5(this.$indicatorSize$delegate)),
                  Alignment.Companion.getBottomEnd()
               ),
               var2,
               0,
               0
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$6$lambda$1$lambda$0(long var0, MutableState var2, Loading var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      if (Color.recovered$9eab66ea8c46e11c(AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$2(var2), Color.Companion.recovered$562f21ab17d9b4c7())) {
         AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$3(var2, var0);
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$3$lambda$2(Color var0, long var1, MutableState var3, Success var4) {
      Intrinsics.checkNotNullParameter(var4, "it");
      AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$3(
         var3, var0 != null ? var0.recovered$45f9de7b85e0e0b5() : ColorExtensionsKt.recovered$da172df9fccf1b21(var1)
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5$lambda$4(long var0, MutableState var2, Error var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      if (Color.recovered$9eab66ea8c46e11c(AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$2(var2), Color.Companion.recovered$562f21ab17d9b4c7())) {
         AvatarIconKt.access$DefaultAvatar_Rd90Nhg$lambda$3(var2, var0);
      }

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
