package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.shapes.OverlappedAvatarShape;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
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
   d1 = "\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a;\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0004\b\u000b\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0010",
   d2 = {
         "AvatarGroup",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "avatarSize",
         "Landroidx/compose/ui/unit/Dp;",
         "placeHolderTextSize",
         "Landroidx/compose/ui/unit/TextUnit;",
         "AvatarGroup--J8mCjc",
         "(Ljava/util/List;Landroidx/compose/ui/Modifier;FJLandroidx/compose/runtime/Composer;II)V",
         "AvatarGroupPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "AvatarGroupWithMixedShapesPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAvatarGroup.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarGroup.kt\nio/intercom/android/sdk/m5/components/AvatarGroupKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,79:1\n118#2:80\n118#2:81\n55#3:82\n68#3:116\n99#4:83\n97#4,8:84\n106#4:121\n81#5,6:92\n88#5,6:107\n96#5:120\n402#6,9:98\n411#6:113\n412#6,2:118\n1878#7,2:114\n1880#7:117\n*S KotlinDebug\n*F\n+ 1 AvatarGroup.kt\nio/intercom/android/sdk/m5/components/AvatarGroupKt\n*L\n25#1:80\n28#1:81\n30#1:82\n38#1:116\n29#1:83\n29#1:84,8\n29#1:121\n29#1:92,6\n29#1:107,6\n29#1:120\n29#1:98,9\n29#1:113\n29#1:118,2\n33#1:114,2\n33#1:117\n*E\n"
)
public final class AvatarGroupKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$813c81fac421be56(
      @NotNull List var0, @Nullable Modifier var1, float var2, long var3, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "avatars");
      var5 = var5.startRestartGroup(-258460642);
      ComposerKt.sourceInformation(var5, "C(AvatarGroup)P(1,2,0:c#ui.unit.Dp,3:c#ui.unit.TextUnit)25@1099L10,28@1172L791:AvatarGroup.kt#arqjag");
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

      if ((var6 & 3072) == 0) {
         var8 |= (var7 & 8) == 0 && var5.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         var5.startDefaults();
         if ((var6 & 1) != 0 && !var5.getDefaultsInvalid()) {
            var5.skipToGroupEnd();
            if ((var7 & 8) != 0) {
               var8 &= -7169;
            }
         } else {
            if ((var7 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var7 & 4) != 0) {
               int var9 = 38;
               int var10 = 0;
               var2 = Dp.recovered$95b3d103663bf060(var9);
            }

            if ((var7 & 8) != 0) {
               var3 = IntercomTheme.INSTANCE.getTypography(var5, IntercomTheme.$stable).getType04Point5().recovered$8f8bc2178e9073c3();
               var8 &= -7169;
            }
         }

         var5.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-258460642, var8, -1, "io.intercom.android.sdk.m5.components.AvatarGroup (AvatarGroup.kt:26)");
         }

         int var53 = 2;
         int var11 = 0;
         float var52 = Dp.recovered$95b3d103663bf060(var53);
         var11 = 0;
         HorizontalOrVertical var54 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(-var52));
         Modifier var56 = SizeKt.wrapContentSize$default(var1, null, false, 3, null);
         byte var13 = 48;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Vertical var12 = Alignment.Companion.getTop();
         MeasurePolicy var15 = RowKt.rowMeasurePolicy(var54, var12, var5, 14 & var13 >> 3 | 112 & var13 >> 3);
         Modifier var16 = var56;
         MeasurePolicy var17 = var15;
         Composer var18 = var5;
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
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 250187591, "C:AvatarGroup.kt#arqjag");
         var34.startReplaceGroup(700807476);
         ComposerKt.sourceInformation(var34, "*39@1738L209");
         Iterable var37 = var0;
         int var38 = 0;
         int var39 = 0;

         for (Object var41 : var37) {
            int var42 = var39++;
            if (var42 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            AvatarWrapper var43 = (AvatarWrapper)var41;
            int var44 = var42;
            int var45 = 0;
            Shape var59;
            if (var44 == 0) {
               AvatarShape var58 = var43.getAvatar().getShape();
               Intrinsics.checkNotNullExpressionValue(var58, "getShape(...)");
               var59 = AvatarIconKt.getComposeShape(var58);
            } else {
               AvatarShape var60 = ((AvatarWrapper)var0.get(var44 - 1)).getAvatar().getShape();
               Intrinsics.checkNotNullExpressionValue(var60, "getShape(...)");
               RoundedCornerShape var46 = AvatarIconKt.getComposeShape(var60);
               int var47 = 2;
               int var48 = 0;
               float var49 = Dp.recovered$95b3d103663bf060(var52 * var47);
               AvatarShape var61 = var43.getAvatar().getShape();
               Intrinsics.checkNotNullExpressionValue(var61, "getShape(...)");
               RoundedCornerShape var57 = AvatarIconKt.getComposeShape(var61);
               var59 = new OverlappedAvatarShape(var57, var46, var49, null);
            }

            Shape var50 = var59;
            AvatarIconKt.recovered$f5ec3c81e3146789(
               SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var2), var43, var50, false, var3, null, var34, 57344 & var8 << 3, 40
            );
         }

         var34.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var62 = var5.endRestartGroup();
      if (var62 != null) {
         final java.util.List recoveredCapture_142_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_142_1 = var1;
         final float recoveredCapture_142_2 = var2;
         final long recoveredCapture_142_3 = var3;
         final int recoveredCapture_142_4 = var6;
         final int recoveredCapture_142_5 = var7;
         var62.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_142_0, recoveredArgument_142_1) -> AvatarGroupKt.AvatarGroup__J8mCjc$lambda$2(recoveredCapture_142_0, recoveredCapture_142_1, recoveredCapture_142_2, recoveredCapture_142_3, recoveredCapture_142_4, recoveredCapture_142_5, (androidx.compose.runtime.Composer)recoveredArgument_142_0, ((java.lang.Integer)recoveredArgument_142_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarGroupPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2091006176);
      ComposerKt.sourceInformation(var0, "C(AvatarGroupPreview)52@2036L304:AvatarGroup.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2091006176, var1, -1, "io.intercom.android.sdk.m5.components.AvatarGroupPreview (AvatarGroup.kt:51)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AvatarGroupKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_140_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_140_0, recoveredArgument_140_1) -> AvatarGroupKt.AvatarGroupPreview$lambda$3(recoveredCapture_140_0, (androidx.compose.runtime.Composer)recoveredArgument_140_0, ((java.lang.Integer)recoveredArgument_140_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AvatarGroupWithMixedShapesPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1253949399);
      ComposerKt.sourceInformation(var0, "C(AvatarGroupWithMixedShapesPreview)67@2428L348:AvatarGroup.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1253949399, var1, -1, "io.intercom.android.sdk.m5.components.AvatarGroupWithMixedShapesPreview (AvatarGroup.kt:66)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AvatarGroupKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_141_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_141_0, recoveredArgument_141_1) -> AvatarGroupKt.AvatarGroupWithMixedShapesPreview$lambda$4(recoveredCapture_141_0, (androidx.compose.runtime.Composer)recoveredArgument_141_0, ((java.lang.Integer)recoveredArgument_141_1).intValue())));
      }
   }

   private static final Unit AvatarGroup__J8mCjc$lambda$2(List var0, Modifier var1, float var2, long var3, int var5, int var6, Composer var7, int var8) {
      recovered$813c81fac421be56(var0, var1, var2, var3, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit AvatarGroupPreview$lambda$3(int var0, Composer var1, int var2) {
      AvatarGroupPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit AvatarGroupWithMixedShapesPreview$lambda$4(int var0, Composer var1, int var2) {
      AvatarGroupWithMixedShapesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
