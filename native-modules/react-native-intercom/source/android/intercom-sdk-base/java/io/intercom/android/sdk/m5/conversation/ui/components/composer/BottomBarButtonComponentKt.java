package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconButtonColors;
import androidx.compose.material3.IconButtonDefaults;
import androidx.compose.material3.IconButtonKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0007\u001a_\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\r\u001a1\u0010\u000e\u001a\u00020\u00012\b\b\u0001\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\nH\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014¨\u0006\u0017",
   d2 = {
         "BottomBarButtonComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "buttons",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBarButton;",
         "enabled",
         "",
         "onGifInputSelected",
         "Lkotlin/Function0;",
         "onMediaInputSelected",
         "onVoiceNotesInputSelected",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "BottomBarIcon",
         "icon",
         "",
         "onClick",
         "(IZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "BottomBarButtonComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "BottomBarButtonComponentPreviewGifOnly",
         "BottomBarButtonComponentPreviewMediaOnly",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBottomBarButtonComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BottomBarButtonComponent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/BottomBarButtonComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,128:1\n1047#2,6:129\n1047#2,6:135\n1047#2,6:141\n1047#2,6:185\n99#3:147\n96#3,9:148\n106#3:184\n81#4,6:157\n88#4,6:172\n96#4:183\n402#5,9:163\n411#5:178\n412#5,2:181\n1869#6,2:179\n118#7:191\n*S KotlinDebug\n*F\n+ 1 BottomBarButtonComponent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/BottomBarButtonComponentKt\n*L\n24#1:129,6\n25#1:135,6\n26#1:141,6\n65#1:185,6\n28#1:147\n28#1:148,9\n28#1:184\n28#1:157,6\n28#1:172,6\n28#1:183\n28#1:163,9\n28#1:178\n28#1:181,2\n31#1:179,2\n68#1:191\n*E\n"
)
public final class BottomBarButtonComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BottomBarButtonComponent(
      @Nullable Modifier var0,
      @NotNull List var1,
      boolean var2,
      @Nullable Function0 var3,
      @Nullable Function0 var4,
      @Nullable Function0 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "buttons");
      var6 = var6.startRestartGroup(665212828);
      ComposerKt.sourceInformation(var6, "C(BottomBarButtonComponent)P(2)23@969L2,24@1012L2,25@1060L2,27@1072L1046:BottomBarButtonComponent.kt#yb6g9");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changedInstance(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         if ((var8 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var8 & 4) != 0) {
            var2 = true;
         }

         if ((var8 & 8) != 0) {
            var6.startReplaceGroup(2094079772);
            ComposerKt.sourceInformation(var6, "CC(remember):BottomBarButtonComponent.kt#9igjgp");
            Composer var11 = var6;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var62 = ((kotlin.jvm.functions.Function0) () -> BottomBarButtonComponentKt.BottomBarButtonComponent$lambda$1$lambda$0());
               var11.updateRememberedValue(var62);
               var10000 = var62;
            } else {
               var10000 = var14;
            }

            Function0 var10 = (Function0)var10000;
            var6.endReplaceGroup();
            var3 = var10;
         }

         if ((var8 & 16) != 0) {
            var6.startReplaceGroup(2094081148);
            ComposerKt.sourceInformation(var6, "CC(remember):BottomBarButtonComponent.kt#9igjgp");
            Composer var47 = var6;
            boolean var50 = false;
            int var53 = 0;
            Object var56 = var47.rememberedValue();
            int var59 = 0;
            Object var68;
            if (var56 == Composer.Companion.getEmpty()) {
               int var63 = 0;
               Object var64 = ((kotlin.jvm.functions.Function0) () -> BottomBarButtonComponentKt.BottomBarButtonComponent$lambda$3$lambda$2());
               var47.updateRememberedValue(var64);
               var68 = var64;
            } else {
               var68 = var56;
            }

            Function0 var44 = (Function0)var68;
            var6.endReplaceGroup();
            var4 = var44;
         }

         if ((var8 & 32) != 0) {
            var6.startReplaceGroup(2094082684);
            ComposerKt.sourceInformation(var6, "CC(remember):BottomBarButtonComponent.kt#9igjgp");
            Composer var48 = var6;
            boolean var51 = false;
            int var54 = 0;
            Object var57 = var48.rememberedValue();
            int var60 = 0;
            Object var69;
            if (var57 == Composer.Companion.getEmpty()) {
               int var65 = 0;
               Object var66 = ((kotlin.jvm.functions.Function0) () -> BottomBarButtonComponentKt.BottomBarButtonComponent$lambda$5$lambda$4());
               var48.updateRememberedValue(var66);
               var69 = var66;
            } else {
               var69 = var57;
            }

            Function0 var45 = (Function0)var69;
            var6.endReplaceGroup();
            var5 = var45;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               665212828, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarButtonComponent (BottomBarButtonComponent.kt:26)"
            );
         }

         int var52 = 14 & var9;
         int var55 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var6, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var46 = Arrangement.INSTANCE.getStart();
         Vertical var49 = Alignment.Companion.getTop();
         MeasurePolicy var58 = RowKt.rowMeasurePolicy(var46, var49, var6, 14 & var52 >> 3 | 112 & var52 >> 3);
         Modifier var61 = var0;
         MeasurePolicy var67 = var58;
         Composer var17 = var6;
         int var18 = 112 & var52 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var61);
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
         Updater.recovered$89682b199a142880(var27, var67, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var32 = 6 | 112 & var52 >> 6;
         Composer var33 = var30;
         RowScope var34 = RowScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -1307913780, "C:BottomBarButtonComponent.kt#yb6g9");
         var33.startReplaceGroup(-1981853169);
         ComposerKt.sourceInformation(var33, "");
         Iterable var36 = var1;
         int var37 = 0;

         for (Object var39 : var36) {
            BottomBarUiState$BottomBarButton var40 = (BottomBarUiState$BottomBarButton)var39;
            int var41 = 0;
            BottomBarUiState$BottomBarButton var42 = var40;
            if (Intrinsics.areEqual(var42, BottomBarUiState$BottomBarButton$MediaInsert.INSTANCE)) {
               var33.startReplaceGroup(1310985591);
               ComposerKt.sourceInformation(var33, "33@1250L203");
               BottomBarIcon(drawable.intercom_ic_attachment, var2, var4, var33, 112 & var9 >> 3 | 896 & var9 >> 6, 0);
               var33.endReplaceGroup();
            } else if (Intrinsics.areEqual(var42, BottomBarUiState$BottomBarButton$GifInsert.INSTANCE)) {
               var33.startReplaceGroup(1311289856);
               ComposerKt.sourceInformation(var33, "41@1557L194");
               BottomBarIcon(drawable.intercom_ic_gif, var2, var3, var33, 112 & var9 >> 3 | 896 & var9 >> 3, 0);
               var33.endReplaceGroup();
            } else {
               if (!Intrinsics.areEqual(var42, BottomBarUiState$BottomBarButton$VoiceNotesInsert.INSTANCE)) {
                  var33.startReplaceGroup(1427761454);
                  var33.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var33.startReplaceGroup(1311592850);
               ComposerKt.sourceInformation(var33, "49@1862L208");
               BottomBarIcon(drawable.intercom_ic_microphone, var2, var5, var33, 112 & var9 >> 3 | 896 & var9 >> 9, 0);
               var33.endReplaceGroup();
            }
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var6);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var70 = var6.endRestartGroup();
      if (var70 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_505_0 = var0;
         final java.util.List recoveredCapture_505_1 = var1;
         final boolean recoveredCapture_505_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_505_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_505_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_505_5 = var5;
         final int recoveredCapture_505_6 = var7;
         final int recoveredCapture_505_7 = var8;
         var70.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_505_0, recoveredArgument_505_1) -> BottomBarButtonComponentKt.BottomBarButtonComponent$lambda$8(recoveredCapture_505_0, recoveredCapture_505_1, recoveredCapture_505_2, recoveredCapture_505_3, recoveredCapture_505_4, recoveredCapture_505_5, recoveredCapture_505_6, recoveredCapture_505_7, (androidx.compose.runtime.Composer)recoveredArgument_505_0, ((java.lang.Integer)recoveredArgument_505_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BottomBarIcon(@DrawableRes int var0, boolean var1, Function0 var2, Composer var3, int var4, int var5) {
      var3 = var3.startRestartGroup(1211476790);
      ComposerKt.sourceInformation(
         var3, "C(BottomBarIcon)P(1)64@2244L2,71@2458L6,72@2531L6,70@2399L182,74@2589L165,66@2256L498:BottomBarButtonComponent.kt#yb6g9"
      );
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
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = true;
         }

         if ((var5 & 4) != 0) {
            var3.startReplaceGroup(1432870732);
            ComposerKt.sourceInformation(var3, "CC(remember):BottomBarButtonComponent.kt#9igjgp");
            Composer var8 = var3;
            boolean var9 = false;
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10000;
            if (var11 == Composer.Companion.getEmpty()) {
               int var13 = 0;
               Object var19 = ((kotlin.jvm.functions.Function0) () -> BottomBarButtonComponentKt.BottomBarIcon$lambda$10$lambda$9());
               var8.updateRememberedValue(var19);
               var10000 = var19;
            } else {
               var10000 = var11;
            }

            Function0 var7 = (Function0)var10000;
            var3.endReplaceGroup();
            var2 = var7;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1211476790, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarIcon (BottomBarButtonComponent.kt:65)"
            );
         }

         Modifier var20 = Modifier.Companion;
         int var16 = 40;
         int var18 = 0;
         Modifier var15 = SizeKt.recovered$111ef94c93e78c04(var20, Dp.recovered$95b3d103663bf060(var16));
         IconButtonColors var17 = IconButtonDefaults.INSTANCE
            .recovered$d0285dfef4036e76(
               0L,
               IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getIcon().recovered$449e06b309cdc9eb(),
               0L,
               Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getIcon().recovered$090a6e753d82e539(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null
               ),
               var3,
               IconButtonDefaults.$stable << 12,
               5
            );
         IconButtonKt.IconButton(
            var2,
            var15,
            var1,
            var17,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(2035069272, true, new BottomBarButtonComponentKt$BottomBarIcon$2(var0), var3, 54),
            var3,
            1572912 | 14 & var6 >> 6 | 896 & var6 << 3,
            48
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var21 = var3.endRestartGroup();
      if (var21 != null) {
         final int recoveredCapture_510_0 = var0;
         final boolean recoveredCapture_510_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_510_2 = var2;
         final int recoveredCapture_510_3 = var4;
         final int recoveredCapture_510_4 = var5;
         var21.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_510_0, recoveredArgument_510_1) -> BottomBarButtonComponentKt.BottomBarIcon$lambda$11(recoveredCapture_510_0, recoveredCapture_510_1, recoveredCapture_510_2, recoveredCapture_510_3, recoveredCapture_510_4, (androidx.compose.runtime.Composer)recoveredArgument_510_0, ((java.lang.Integer)recoveredArgument_510_1).intValue())));
      }
   }

   @Composable
   @Preview
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BottomBarButtonComponentPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-179036889);
      ComposerKt.sourceInformation(var0, "C(BottomBarButtonComponentPreview)87@2863L355:BottomBarButtonComponent.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -179036889,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarButtonComponentPreview (BottomBarButtonComponent.kt:86)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BottomBarButtonComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_506_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_506_0, recoveredArgument_506_1) -> BottomBarButtonComponentKt.BottomBarButtonComponentPreview$lambda$12(recoveredCapture_506_0, (androidx.compose.runtime.Composer)recoveredArgument_506_0, ((java.lang.Integer)recoveredArgument_506_1).intValue())));
      }
   }

   @Composable
   @Preview
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BottomBarButtonComponentPreviewGifOnly(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1619387831);
      ComposerKt.sourceInformation(var0, "C(BottomBarButtonComponentPreviewGifOnly)103@3302L218:BottomBarButtonComponent.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1619387831,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarButtonComponentPreviewGifOnly (BottomBarButtonComponent.kt:102)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BottomBarButtonComponentKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_507_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_507_0, recoveredArgument_507_1) -> BottomBarButtonComponentKt.BottomBarButtonComponentPreviewGifOnly$lambda$13(recoveredCapture_507_0, (androidx.compose.runtime.Composer)recoveredArgument_507_0, ((java.lang.Integer)recoveredArgument_507_1).intValue())));
      }
   }

   @Composable
   @Preview
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BottomBarButtonComponentPreviewMediaOnly(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1269009367);
      ComposerKt.sourceInformation(var0, "C(BottomBarButtonComponentPreviewMediaOnly)117@3606L220:BottomBarButtonComponent.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1269009367,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.BottomBarButtonComponentPreviewMediaOnly (BottomBarButtonComponent.kt:116)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BottomBarButtonComponentKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_508_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_508_0, recoveredArgument_508_1) -> BottomBarButtonComponentKt.BottomBarButtonComponentPreviewMediaOnly$lambda$14(recoveredCapture_508_0, (androidx.compose.runtime.Composer)recoveredArgument_508_0, ((java.lang.Integer)recoveredArgument_508_1).intValue())));
      }
   }

   private static final Unit BottomBarButtonComponent$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponent$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponent$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponent$lambda$8(
      Modifier var0, List var1, boolean var2, Function0 var3, Function0 var4, Function0 var5, int var6, int var7, Composer var8, int var9
   ) {
      BottomBarButtonComponent(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarIcon$lambda$10$lambda$9() {
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarIcon$lambda$11(int var0, boolean var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      BottomBarIcon(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponentPreview$lambda$12(int var0, Composer var1, int var2) {
      BottomBarButtonComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponentPreviewGifOnly$lambda$13(int var0, Composer var1, int var2) {
      BottomBarButtonComponentPreviewGifOnly(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BottomBarButtonComponentPreviewMediaOnly$lambda$14(int var0, Composer var1, int var2) {
      BottomBarButtonComponentPreviewMediaOnly(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
