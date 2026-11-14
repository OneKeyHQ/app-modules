package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aw\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072 \b\u0002\u0010\t\u001a\u001a\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u0001\u0018\u00010\n¢\u0006\u0002\b\f¢\u0006\u0002\b\r2 \b\u0002\u0010\u000e\u001a\u001a\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u0001\u0018\u00010\n¢\u0006\u0002\b\f¢\u0006\u0002\b\rH\u0007¢\u0006\u0004\b\u000f\u0010\u0010¨\u0006\u0011",
   d2 = {
         "BoxedTextLayout",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "text",
         "",
         "borderColor",
         "Landroidx/compose/ui/graphics/Color;",
         "textColor",
         "trialingIcon",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "leadingIcon",
         "BoxedTextLayout-Y0xEhic",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;JJLkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBoxedTextLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BoxedTextLayout.kt\nio/intercom/android/sdk/ui/component/BoxedTextLayoutKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,61:1\n118#2:62\n118#2:63\n118#2:64\n99#3,6:65\n106#3:96\n81#4,6:71\n88#4,6:86\n96#4:95\n402#5,9:77\n411#5,3:92\n*S KotlinDebug\n*F\n+ 1 BoxedTextLayout.kt\nio/intercom/android/sdk/ui/component/BoxedTextLayoutKt\n*L\n31#1:62\n36#1:63\n38#1:64\n28#1:65,6\n28#1:96\n28#1:71,6\n28#1:86,6\n28#1:95\n28#1:77,9\n28#1:92,3\n*E\n"
)
public final class BoxedTextLayoutKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable]]")
   public static final void recovered$73ea9cf1e7267681(
      @Nullable Modifier var0,
      @NotNull String var1,
      long var2,
      long var4,
      @Nullable Function3 var6,
      @Nullable Function3 var7,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var1, "text");
      var8 = var8.startRestartGroup(-1728859823);
      ComposerKt.sourceInformation(
         var8, "C(BoxedTextLayout)P(2,3,0:c#ui.graphics.Color,4:c#ui.graphics.Color,5)23@878L6,32@1187L6,27@1028L882:BoxedTextLayout.kt#yr602f"
      );
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var8.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var8.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var8.changed(var2) ? 256 : 128;
      }

      if ((var9 & 3072) == 0) {
         var11 |= (var10 & 8) == 0 && var8.changed(var4) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var8.changedInstance(var6) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var8.changedInstance(var7) ? 131072 : 65536;
      }

      if ((var11 & 74899) == 74898 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         var8.startDefaults();
         if ((var9 & 1) != 0 && !var8.getDefaultsInvalid()) {
            var8.skipToGroupEnd();
            if ((var10 & 8) != 0) {
               var11 &= -7169;
            }
         } else {
            if ((var10 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var10 & 8) != 0) {
               var4 = IntercomTheme.INSTANCE.getColors(var8, 6).getText().recovered$e01a1d3b7b68e21a();
               var11 &= -7169;
            }

            if ((var10 & 16) != 0) {
               var6 = null;
            }

            if ((var10 & 32) != 0) {
               var7 = null;
            }
         }

         var8.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1728859823, var11, -1, "io.intercom.android.sdk.ui.component.BoxedTextLayout (BoxedTextLayout.kt:26)");
         }

         int var13 = 1;
         int var14 = 0;
         Modifier var10000 = ClipKt.clip(
            BorderKt.recovered$1227359600bc50fd(var0, Dp.recovered$95b3d103663bf060(var13), var2, IntercomTheme.INSTANCE.getShapes(var8, 6).getSmall()),
            RoundedCornerShapeKt.RoundedCornerShape(2)
         );
         var13 = 8;
         var14 = 0;
         Modifier var12 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var13));
         Vertical var44 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         HorizontalOrVertical var46 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         short var47 = 432;
         $i$f$getDpx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var8, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var17 = RowKt.rowMeasurePolicy(var46, var44, var8, 14 & var47 >> 3 | 112 & var47 >> 3);
         Modifier var18 = var12;
         MeasurePolicy var19 = var17;
         Composer var20 = var8;
         int var21 = 112 & var47 << 3;
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
         ComposerKt.sourceInformationMarkerStart(var33, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var35 = 6 | 112 & var47 >> 6;
         Composer var36 = var33;
         RowScope var37 = RowScopeInstance.INSTANCE;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 851313493, "C48@1667L10,45@1551L247:BoxedTextLayout.kt#yr602f");
         var36.startReplaceGroup(581650665);
         ComposerKt.sourceInformation(var36, "41@1491L14");
         if (var6 != null) {
            var6.invoke(var37, var36, 14 & var35 | 112 & var11 >> 9);
         }

         var36.endReplaceGroup();
         Modifier var39 = var37.weight(Modifier.Companion, 1.0F, false);
         TextStyle var40 = IntercomTheme.INSTANCE.getTypography(var36, 6).getType04();
         int var41 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var1,
            var39,
            var4,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            var41,
            false,
            1,
            0,
            null,
            var40,
            var36,
            14 & var11 >> 3 | 896 & var11 >> 3,
            24960,
            110584
         );
         var36.startReplaceGroup(581663175);
         ComposerKt.sourceInformation(var36, "56@1881L13");
         if (var7 != null) {
            var7.invoke(var37, var36, 14 & var35 | 112 & var11 >> 12);
         }

         var36.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var33);
         var27.endNode();
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var49 = var8.endRestartGroup();
      if (var49 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_2_0 = var0;
         final java.lang.String recoveredCapture_2_1 = var1;
         final long recoveredCapture_2_2 = var2;
         final long recoveredCapture_2_3 = var4;
         final kotlin.jvm.functions.Function3 recoveredCapture_2_4 = var6;
         final kotlin.jvm.functions.Function3 recoveredCapture_2_5 = var7;
         final int recoveredCapture_2_6 = var9;
         final int recoveredCapture_2_7 = var10;
         var49.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_2_0, recoveredArgument_2_1) -> BoxedTextLayoutKt.BoxedTextLayout_Y0xEhic$lambda$1(recoveredCapture_2_0, recoveredCapture_2_1, recoveredCapture_2_2, recoveredCapture_2_3, recoveredCapture_2_4, recoveredCapture_2_5, recoveredCapture_2_6, recoveredCapture_2_7, (androidx.compose.runtime.Composer)recoveredArgument_2_0, ((java.lang.Integer)recoveredArgument_2_1).intValue())));
      }
   }

   private static final Unit BoxedTextLayout_Y0xEhic$lambda$1(
      Modifier var0, String var1, long var2, long var4, Function3 var6, Function3 var7, int var8, int var9, Composer var10, int var11
   ) {
      recovered$73ea9cf1e7267681(var0, var1, var2, var4, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }
}
