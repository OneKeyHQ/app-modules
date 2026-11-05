package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.shapes.CutAvatarBoxShape;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a;\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0004\b\u000b\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0011",
   d2 = {
         "AvatarTriangleGroup",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "avatarShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "size",
         "Landroidx/compose/ui/unit/Dp;",
         "AvatarTriangleGroup--jt2gSs",
         "(Ljava/util/List;Landroidx/compose/ui/Modifier;Landroidx/compose/ui/graphics/Shape;FLandroidx/compose/runtime/Composer;II)V",
         "SingleAvatarPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "DoubleAvatarsPreview",
         "TripleAvatarsPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAvatarTriangleGroup.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarTriangleGroup.kt\nio/intercom/android/sdk/m5/components/AvatarTriangleGroupKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,128:1\n118#2:129\n118#2:130\n118#2:174\n118#2:193\n118#2:195\n60#3,9:131\n49#3:140\n52#3,9:175\n52#3,9:184\n52#3:194\n70#4:141\n67#4,9:142\n77#4:199\n81#5,6:151\n88#5,6:166\n96#5:198\n402#6,9:157\n411#6:172\n412#6,2:196\n1#7:173\n*S KotlinDebug\n*F\n+ 1 AvatarTriangleGroup.kt\nio/intercom/android/sdk/m5/components/AvatarTriangleGroupKt\n*L\n27#1:129\n33#1:130\n45#1:174\n60#1:193\n62#1:195\n34#1:131,9\n34#1:140\n47#1:175,9\n48#1:184,9\n62#1:194\n35#1:141\n35#1:142,9\n35#1:199\n35#1:151,6\n35#1:166,6\n35#1:198\n35#1:157,9\n35#1:172\n35#1:196,2\n*E\n"
)
public final class AvatarTriangleGroupKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$617ccf65fce42f20(
      @NotNull List var0, @Nullable Modifier var1, @Nullable Shape var2, float var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "avatars");
      var4 = var4.startRestartGroup(-534156342);
      ComposerKt.sourceInformation(var4, "C(AvatarTriangleGroup)P(1,2!,3:c#ui.unit.Dp):AvatarTriangleGroup.kt#arqjag");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var7 |= (var6 & 4) == 0 && var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         var4.startDefaults();
         if ((var5 & 1) != 0 && !var4.getDefaultsInvalid()) {
            var4.skipToGroupEnd();
            if ((var6 & 4) != 0) {
               var7 &= -897;
            }
         } else {
            if ((var6 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var6 & 4) != 0) {
               var2 = AvatarIconKt.getComposeShape(AvatarShape.CIRCLE);
               var7 &= -897;
            }

            if ((var6 & 8) != 0) {
               int var8 = 32;
               int var9 = 0;
               var3 = Dp.recovered$95b3d103663bf060(var8);
            }
         }

         var4.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-534156342, var7, -1, "io.intercom.android.sdk.m5.components.AvatarTriangleGroup (AvatarTriangleGroup.kt:27)");
         }

         long var47 = TextUnitKt.getSp(12);
         if (var0.size() > 1) {
            var4.startReplaceGroup(643591148);
            ComposerKt.sourceInformation(var4, "34@1354L1744");
            int var11 = 1;
            int var12 = 0;
            float var10 = Dp.recovered$95b3d103663bf060(var11);
            var12 = 2;
            int var13 = 0;
            float var53 = Dp.recovered$95b3d103663bf060(var3 / var12);
            var13 = 2;
            int var14 = 0;
            float var58 = Dp.recovered$95b3d103663bf060(var10 * var13);
            var14 = 0;
            float var49 = Dp.recovered$95b3d103663bf060(var53 + var58);
            Modifier var54 = SizeKt.recovered$111ef94c93e78c04(var1, var3);
            byte var15 = 0;
            int var16 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var4, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment var59 = Alignment.Companion.getTopStart();
            boolean var61 = false;
            MeasurePolicy var17 = BoxKt.maybeCachedBoxMeasurePolicy(var59, var61);
            Modifier var18 = var54;
            MeasurePolicy var19 = var17;
            Composer var20 = var4;
            int var21 = 112 & var15 << 3;
            int var22 = 0;
            ComposerKt.sourceInformationMarkerStart(var20, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var23 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var20, 0));
            CompositionLocalMap var24 = var20.getCurrentCompositionLocalMap();
            Modifier var25 = ComposedModifierKt.materializeModifier(var20, var18);
            Function0 var26 = ComposeUiNode.Companion.getConstructor();
            Composer var27 = var20;
            int var28 = 6 | 896 & var21 << 6;
            int var29 = 0;
            ComposerKt.sourceInformationMarkerStart(var27, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var27.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var27.startReusableNode();
            if (var27.getInserting()) {
               var27.createNode(var26);
            } else {
               var27.useNode();
            }

            Composer var30 = Updater.recovered$676aa17b44fd6280(var27);
            int var31 = 0;
            Updater.recovered$89682b199a142880(var30, var19, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var30, var24, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var30, var23, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var30, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var30, var25, ComposeUiNode.Companion.getSetModifier());
            int var32 = 14 & var28 >> 6;
            Composer var33 = var27;
            int var34 = 0;
            ComposerKt.sourceInformationMarkerStart(var33, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var35 = 6 | 112 & var15 >> 6;
            Composer var36 = var33;
            BoxScope var37 = BoxScopeInstance.INSTANCE;
            int var38 = 0;
            ComposerKt.sourceInformationMarkerStart(var36, -1063669345, "C37@1439L683,52@2139L585,66@2741L343:AvatarTriangleGroup.kt#arqjag");
            List var39 = var0;
            int var40 = 0;
            Object var10000;
            if (var40 < var39.size()) {
               var10000 = var39.get(var40);
            } else {
               int var42 = 0;
               var10000 = AvatarWrapper.Companion.getNULL();
            }

            AvatarWrapper var43 = (AvatarWrapper)var10000;
            Modifier var63 = var37.align(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var49), Alignment.Companion.getTopCenter());
            int $this$dp$ivx = 2;
            int $i$f$getDpx = 0;
            float var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            Pair[] var71 = new Pair[2];
            $i$f$getDpx = 0;
            float var77 = Dp.recovered$95b3d103663bf060(var3 - var49);
            int var44 = 2;
            int var45/* $VF was: $i$f$div-u2uoSUMx */ = 0;
            Dp var10006 = Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var77 / var44));
            $i$f$getDpx = 0;
            var71[0] = TuplesKt.to(var10006, Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var3 - var49)));
            $i$f$getDpx = 0;
            float var80 = Dp.recovered$95b3d103663bf060(var3 - var49);
            var44 = 2;
            var45/* $VF was: $i$f$div-u2uoSUMx */ = 0;
            float var81 = Dp.recovered$95b3d103663bf060(var80 / var44);
            var44 = 0;
            var10006 = Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(-var81));
            $i$f$getDpx = 0;
            var71[1] = TuplesKt.to(var10006, Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var3 - var49)));
            CutAvatarBoxShape var67 = new CutAvatarBoxShape(var2, var10003, CollectionsKt.listOf(var71), null);
            AvatarIconKt.recovered$f5ec3c81e3146789(var63, var43, var67, false, var47, null, var36, 24576, 40);
            var39 = var0;
            var40 = 1;
            if (var40 < var39.size()) {
               var10000 = var39.get(var40);
            } else {
               $i$f$getDpx = 0;
               var10000 = AvatarWrapper.Companion.getNULL();
            }

            var43 = (AvatarWrapper)var10000;
            Modifier var65 = var37.align(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var49), Alignment.Companion.getBottomStart());
            $this$dp$ivx = 2;
            $i$f$getDpx = 0;
            var10003 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            $this$dp$ivx = 0;
            Dp var10004 = Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060(var3 - var49));
            $this$dp$ivx = 0;
            $i$f$getDpx = 0;
            CutAvatarBoxShape var69 = new CutAvatarBoxShape(
               var2, var10003, CollectionsKt.listOf(TuplesKt.to(var10004, Dp.recovered$89bc2ea8129b5eb8(Dp.recovered$95b3d103663bf060($this$dp$ivx)))), null
            );
            AvatarIconKt.recovered$f5ec3c81e3146789(var65, var43, var69, false, var47, null, var36, 24576, 40);
            var39 = var0;
            var40 = 2;
            if (var40 < var39.size()) {
               var10000 = var39.get(var40);
            } else {
               $i$f$getDpx = 0;
               var10000 = AvatarWrapper.Companion.getNULL();
            }

            var43 = (AvatarWrapper)var10000;
            AvatarIconKt.recovered$f5ec3c81e3146789(
               var37.align(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var49), Alignment.Companion.getBottomEnd()),
               var43,
               var2,
               false,
               var47,
               null,
               var36,
               24576 | 896 & var7,
               40
            );
            ComposerKt.sourceInformationMarkerEnd(var36);
            ComposerKt.sourceInformationMarkerEnd(var33);
            var27.endNode();
            ComposerKt.sourceInformationMarkerEnd(var27);
            ComposerKt.sourceInformationMarkerEnd(var20);
            ComposerKt.sourceInformationMarkerEnd(var4);
            var4.endReplaceGroup();
         } else {
            var4.startReplaceGroup(645459394);
            ComposerKt.sourceInformation(var4, "78@3208L171");
            List var50 = var0;
            int var55 = 0;
            Object var94;
            if (var55 < var50.size()) {
               var94 = var50.get(var55);
            } else {
               int var62 = 0;
               var94 = AvatarWrapper.Companion.getNULL();
            }

            AvatarWrapper var48 = (AvatarWrapper)var94;
            Modifier var51 = SizeKt.recovered$111ef94c93e78c04(var1, var3);
            AvatarShape var95 = var48.getAvatar().getShape();
            Intrinsics.checkNotNullExpressionValue(var95, "getShape(...)");
            RoundedCornerShape var56 = AvatarIconKt.getComposeShape(var95);
            AvatarIconKt.recovered$f5ec3c81e3146789(var51, var48, var56, false, 0L, null, var4, 0, 56);
            var4.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var96 = var4.endRestartGroup();
      if (var96 != null) {
         final java.util.List recoveredCapture_146_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_146_1 = var1;
         final androidx.compose.ui.graphics.Shape recoveredCapture_146_2 = var2;
         final float recoveredCapture_146_3 = var3;
         final int recoveredCapture_146_4 = var5;
         final int recoveredCapture_146_5 = var6;
         var96.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_146_0, recoveredArgument_146_1) -> AvatarTriangleGroupKt.AvatarTriangleGroup__jt2gSs$lambda$5(recoveredCapture_146_0, recoveredCapture_146_1, recoveredCapture_146_2, recoveredCapture_146_3, recoveredCapture_146_4, recoveredCapture_146_5, (androidx.compose.runtime.Composer)recoveredArgument_146_0, ((java.lang.Integer)recoveredArgument_146_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void SingleAvatarPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-932654159);
      ComposerKt.sourceInformation(var0, "C(SingleAvatarPreview)90@3469L170:AvatarTriangleGroup.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-932654159, var1, -1, "io.intercom.android.sdk.m5.components.SingleAvatarPreview (AvatarTriangleGroup.kt:89)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AvatarTriangleGroupKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_144_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_144_0, recoveredArgument_144_1) -> AvatarTriangleGroupKt.SingleAvatarPreview$lambda$6(recoveredCapture_144_0, (androidx.compose.runtime.Composer)recoveredArgument_144_0, ((java.lang.Integer)recoveredArgument_144_1).intValue())));
      }
   }

   @IntercomPreviews
   @Preview(name = "RTL", locale = "ar")
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DoubleAvatarsPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2121947035);
      ComposerKt.sourceInformation(var0, "C(DoubleAvatarsPreview)103@3752L235:AvatarTriangleGroup.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2121947035, var1, -1, "io.intercom.android.sdk.m5.components.DoubleAvatarsPreview (AvatarTriangleGroup.kt:102)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AvatarTriangleGroupKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_143_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_143_0, recoveredArgument_143_1) -> AvatarTriangleGroupKt.DoubleAvatarsPreview$lambda$7(recoveredCapture_143_0, (androidx.compose.runtime.Composer)recoveredArgument_143_0, ((java.lang.Integer)recoveredArgument_143_1).intValue())));
      }
   }

   @IntercomPreviews
   @Preview(name = "RTL", locale = "ar")
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TripleAvatarsPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-724464974);
      ComposerKt.sourceInformation(var0, "C(TripleAvatarsPreview)117@4100L300:AvatarTriangleGroup.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-724464974, var1, -1, "io.intercom.android.sdk.m5.components.TripleAvatarsPreview (AvatarTriangleGroup.kt:116)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AvatarTriangleGroupKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_145_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_145_0, recoveredArgument_145_1) -> AvatarTriangleGroupKt.TripleAvatarsPreview$lambda$8(recoveredCapture_145_0, (androidx.compose.runtime.Composer)recoveredArgument_145_0, ((java.lang.Integer)recoveredArgument_145_1).intValue())));
      }
   }

   private static final Unit AvatarTriangleGroup__jt2gSs$lambda$5(List var0, Modifier var1, Shape var2, float var3, int var4, int var5, Composer var6, int var7) {
      recovered$617ccf65fce42f20(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit SingleAvatarPreview$lambda$6(int var0, Composer var1, int var2) {
      SingleAvatarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DoubleAvatarsPreview$lambda$7(int var0, Composer var1, int var2) {
      DoubleAvatarsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TripleAvatarsPreview$lambda$8(int var0, Composer var1, int var2) {
      TripleAvatarsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
