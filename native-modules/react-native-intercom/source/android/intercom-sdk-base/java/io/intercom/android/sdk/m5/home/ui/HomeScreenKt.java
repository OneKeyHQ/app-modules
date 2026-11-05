package io.intercom.android.sdk.m5.home.ui;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.ExitTransition;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.PrimitiveSnapshotStateKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.PoweredByBadgeKt;
import io.intercom.android.sdk.m5.home.HomeViewModel;
import io.intercom.android.sdk.m5.home.states.HomeUiState;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Error;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Loading;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor;
import io.intercom.android.sdk.models.PoweredBy;
import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.LinkOpener;
import io.intercom.android.sdk.utilities.ThemeUtils;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000J\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\u001aÇ\u0001\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00030\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00030\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00030\u000b2\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00030\u000bH\u0001¢\u0006\u0002\u0010\u0016\u001a\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0018H\u0002\u001a\f\u0010\u001b\u001a\u00020\u001c*\u00020\u001dH\u0002\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001e",
   d2 = {
         "ANIMATION_DURATION",
         "",
         "HomeScreen",
         "",
         "homeViewModel",
         "Lio/intercom/android/sdk/m5/home/HomeViewModel;",
         "onMessagesClicked",
         "Lkotlin/Function0;",
         "onHelpClicked",
         "onTicketsClicked",
         "onTicketItemClicked",
         "Lkotlin/Function1;",
         "",
         "navigateToMessages",
         "navigateToNewConversation",
         "navigateToExistingConversation",
         "onNewConversationClicked",
         "onConversationClicked",
         "Lio/intercom/android/sdk/models/Conversation;",
         "onCloseClick",
         "onTicketLinkClicked",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "(Lio/intercom/android/sdk/m5/home/HomeViewModel;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "getHeaderContentOpacity",
         "",
         "scrollValue",
         "headerHeight",
         "isDarkContentEnabled",
         "",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,275:1\n1047#2,6:276\n1047#2,6:282\n1047#2,6:289\n1047#2,6:366\n1047#2,6:374\n75#3:288\n75#3:365\n70#4:295\n66#4,10:296\n70#4:380\n67#4,9:381\n77#4:415\n77#4:419\n81#5,6:306\n88#5,6:321\n81#5,6:338\n88#5,6:353\n96#5:363\n81#5,6:390\n88#5,6:405\n96#5:414\n96#5:418\n402#6,9:312\n411#6:327\n402#6,9:344\n411#6:359\n412#6,2:361\n402#6,9:396\n411#6,3:411\n412#6,2:416\n87#7:328\n84#7,9:329\n94#7:364\n118#8:360\n118#8:372\n118#8:373\n*S KotlinDebug\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt\n*L\n79#1:276,6\n81#1:282,6\n95#1:289,6\n207#1:366,6\n239#1:374,6\n94#1:288\n200#1:365\n100#1:295\n100#1:296,10\n232#1:380\n232#1:381,9\n232#1:415\n100#1:419\n100#1:306,6\n100#1:321,6\n122#1:338,6\n122#1:353,6\n122#1:363\n232#1:390,6\n232#1:405,6\n232#1:414\n100#1:418\n100#1:312,9\n100#1:327\n122#1:344,9\n122#1:359\n122#1:361,2\n232#1:396,9\n232#1:411,3\n100#1:416,2\n122#1:328\n122#1:329,9\n122#1:364\n198#1:360\n235#1:372\n238#1:373\n*E\n"
)
public final class HomeScreenKt {
   private static final int ANIMATION_DURATION = 600;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeScreen(
      @NotNull HomeViewModel var0,
      @NotNull Function0 var1,
      @NotNull Function0 var2,
      @NotNull Function0 var3,
      @NotNull Function1 var4,
      @NotNull Function0 var5,
      @NotNull Function0 var6,
      @NotNull Function1 var7,
      @NotNull Function0 var8,
      @NotNull Function1 var9,
      @NotNull Function0 var10,
      @NotNull Function1 var11,
      @Nullable Composer var12,
      int var13,
      int var14
   ) {
      Intrinsics.checkNotNullParameter(var0, "homeViewModel");
      Intrinsics.checkNotNullParameter(var1, "onMessagesClicked");
      Intrinsics.checkNotNullParameter(var2, "onHelpClicked");
      Intrinsics.checkNotNullParameter(var3, "onTicketsClicked");
      Intrinsics.checkNotNullParameter(var4, "onTicketItemClicked");
      Intrinsics.checkNotNullParameter(var5, "navigateToMessages");
      Intrinsics.checkNotNullParameter(var6, "navigateToNewConversation");
      Intrinsics.checkNotNullParameter(var7, "navigateToExistingConversation");
      Intrinsics.checkNotNullParameter(var8, "onNewConversationClicked");
      Intrinsics.checkNotNullParameter(var9, "onConversationClicked");
      Intrinsics.checkNotNullParameter(var10, "onCloseClick");
      Intrinsics.checkNotNullParameter(var11, "onTicketLinkClicked");
      Composer var92 = var12.startRestartGroup(1138475448);
      ComposerKt.sourceInformation(
         var92, "C(HomeScreen)P(!1,7,6,11,9,2,3!1,8,5)75@3324L16,77@3364L21,78@3411L36,80@3474L363,80@3453L384,93@4079L7,99@4288L6658:HomeScreen.kt#ac106p"
      );
      int var15 = var13;
      int var16 = var14;
      if ((var13 & 6) == 0) {
         var15 |= var92.changedInstance(var0) ? 4 : 2;
      }

      if ((var13 & 48) == 0) {
         var15 |= var92.changedInstance(var1) ? 32 : 16;
      }

      if ((var13 & 384) == 0) {
         var15 |= var92.changedInstance(var2) ? 256 : 128;
      }

      if ((var13 & 3072) == 0) {
         var15 |= var92.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var13 & 24576) == 0) {
         var15 |= var92.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var13 & 196608) == 0) {
         var15 |= var92.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var13 & 1572864) == 0) {
         var15 |= var92.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var13 & 12582912) == 0) {
         var15 |= var92.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var13 & 100663296) == 0) {
         var15 |= var92.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var13 & 805306368) == 0) {
         var15 |= var92.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var14 & 6) == 0) {
         var16 |= var92.changedInstance(var10) ? 4 : 2;
      }

      if ((var14 & 48) == 0) {
         var16 |= var92.changedInstance(var11) ? 32 : 16;
      }

      if ((var15 & 306783379) == 306783378 && (var16 & 19) == 18 && var92.getSkipping()) {
         var92.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1138475448, var15, var16, "io.intercom.android.sdk.m5.home.ui.HomeScreen (HomeScreen.kt:74)");
         }

         State var17 = SnapshotStateKt.collectAsState(var0.getUiState(), null, var92, 0, 1);
         ScrollState var18 = ScrollKt.rememberScrollState(0, var92, 0, 1);
         var92.startReplaceGroup(853956012);
         ComposerKt.sourceInformation(var92, "CC(remember):HomeScreen.kt#9igjgp");
         Composer var21 = var92;
         boolean var22 = false;
         int var23 = 0;
         Object var24 = var21.rememberedValue();
         int var25 = 0;
         Object var10000;
         if (var24 == Composer.Companion.getEmpty()) {
            int var26 = 0;
            Object var113 = PrimitiveSnapshotStateKt.mutableFloatStateOf(0.0F);
            var21.updateRememberedValue(var113);
            var10000 = var113;
         } else {
            var10000 = var24;
         }

         MutableFloatState var20 = (MutableFloatState)var10000;
         var92.endReplaceGroup();
         MutableFloatState var19 = var20;
         Object var196 = null;
         var92.startReplaceGroup(853958355);
         ComposerKt.sourceInformation(var92, "CC(remember):HomeScreen.kt#9igjgp");
         Composer var96 = var92;
         boolean var98 = var92.changedInstance(var0) | (var15 & 458752) == 131072 | (var15 & 3670016) == 1048576 | (var15 & 29360128) == 8388608;
         int $i$f$cachex = 0;
         Object it$ivx = var96.rememberedValue();
         int var109/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var205;
         if (!var98 && it$ivx != Composer.Companion.getEmpty()) {
            var205 = it$ivx;
         } else {
            Object var91 = null;
            int var114 = 0;
            Function2 var10001 = new HomeScreenKt$HomeScreen$1$1(var0, var5, var6, var7, null);
            var196 = var91;
            Object var27 = var10001;
            var96.updateRememberedValue(var27);
            var205 = var27;
         }

         Function2 var93 = (Function2)var205;
         var92.endReplaceGroup();
         EffectsKt.LaunchedEffect(var196, var93, var92, 6);
         CompositionLocal var102 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var106 = 0;
         int var110 = 0;
         ComposerKt.sourceInformationMarkerStart(var92, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var115 = var92.consume(var102);
         ComposerKt.sourceInformationMarkerEnd(var92);
         Object var99 = var115;
         Window var94 = (var99 instanceof Activity ? (Activity)var99 : null) != null ? (var99 instanceof Activity ? (Activity)var99 : null).getWindow() : null;
         var92.startReplaceGroup(853978516);
         ComposerKt.sourceInformation(var92, "*94@4135L141,94@4124L152");
         if (var94 == null) {
            Object var198 = null;
         } else {
            Window var103 = var94;
            int var107 = 0;
            var92.startReplaceGroup(748713620);
            ComposerKt.sourceInformation(var92, "CC(remember):HomeScreen.kt#9igjgp");
            Composer var111 = var92;
            boolean var116 = var92.changedInstance(var103) | var92.changed(var17);
            int $i$f$cachexx = 0;
            Object it$ivxx = var111.rememberedValue();
            int var29/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var197;
            if (!var116 && it$ivxx != Composer.Companion.getEmpty()) {
               var197 = it$ivxx;
            } else {
               int var30 = 0;
               final android.view.Window recoveredExpressionCapture_906_0 = var103;
               final androidx.compose.runtime.State recoveredExpressionCapture_906_1 = var17;
               Object var31 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.HomeScreenKt.HomeScreen$lambda$4$lambda$3$lambda$2(recoveredExpressionCapture_906_0, recoveredExpressionCapture_906_1));
               var111.updateRememberedValue(var31);
               var197 = var31;
            }

            Function0 var32 = (Function0)var197;
            var92.endReplaceGroup();
            EffectsKt.SideEffect(var32, var92, 0);
         }

         var92.endReplaceGroup();
         byte var104 = 0;
         int var108 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var92, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var95 = Modifier.Companion;
         Alignment var97 = Alignment.Companion.getTopStart();
         boolean var100 = false;
         MeasurePolicy var112 = BoxKt.maybeCachedBoxMeasurePolicy(var97, var100);
         Modifier var117 = var95;
         MeasurePolicy var119 = var112;
         Composer var120 = var92;
         int var121 = 112 & var104 << 3;
         int var122 = 0;
         ComposerKt.sourceInformationMarkerStart(var120, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var123 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var120, 0));
         CompositionLocalMap var124 = var120.getCurrentCompositionLocalMap();
         Modifier var33 = ComposedModifierKt.materializeModifier(var120, var117);
         Function0 var34 = ComposeUiNode.Companion.getConstructor();
         Composer var35 = var120;
         int var36 = 6 | 896 & var121 << 6;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var35.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var35.startReusableNode();
         if (var35.getInserting()) {
            var35.createNode(var34);
         } else {
            var35.useNode();
         }

         Composer var38 = Updater.recovered$676aa17b44fd6280(var35);
         int var39 = 0;
         Updater.recovered$89682b199a142880(var38, var119, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var38, var124, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var38, var123, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var38, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var38, var33, ComposeUiNode.Companion.getSetModifier());
         int var40 = 14 & var36 >> 6;
         Composer var41 = var35;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var43 = 6 | 112 & var104 >> 6;
         Composer var44 = var41;
         BoxScope var45 = BoxScopeInstance.INSTANCE;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(var44, 1735652814, "C104@4568L613,100@4302L879,121@5229L3150,199@8415L7:HomeScreen.kt#ac106p");
         AnimatedVisibilityKt.AnimatedVisibility(
            var17.getValue() instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Content,
            null,
            EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(600, 0, null, 6, null), 0.0F, 2, null),
            EnterExitTransitionKt.fadeOut$default(AnimationSpecKt.tween$default(600, 0, null, 6, null), 0.0F, 2, null),
            null,
            ComposableLambdaKt.rememberComposableLambda(750386582, true, new HomeScreenKt$HomeScreen$3$1(var17, var0, var19), var44, 54),
            var44,
            200064,
            18
         );
         HomeUiState var47 = (HomeUiState)var17.getValue();
         Modifier var48 = ScrollKt.verticalScroll$default(
            WindowInsetsPadding_androidKt.safeDrawingPadding(SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null)),
            var18,
            false,
            null,
            false,
            14,
            null
         );
         byte var49 = 0;
         int var50 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var44, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var51 = Arrangement.INSTANCE.getTop();
         Horizontal var52 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var51, var52, var44, 14 & var49 >> 3 | 112 & var49 >> 3);
         Modifier var54 = var48;
         MeasurePolicy var55 = measurePolicy$ivx;
         Composer var56 = var44;
         int var57 = 112 & var49 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var56, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var56, 0));
         CompositionLocalMap localMap$iv$ivx = var56.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var56, var54);
         Function0 var62 = ComposeUiNode.Companion.getConstructor();
         Composer var63 = var56;
         int var64 = 6 | 896 & var57 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var63, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var63.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var63.startReusableNode();
         if (var63.getInserting()) {
            var63.createNode(var62);
         } else {
            var63.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var63);
         int var67/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var55, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var68 = 14 & var64 >> 6;
         Composer $composer$ivx = var63;
         int var70 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var71 = 6 | 112 & var49 >> 6;
         Composer var72 = $composer$ivx;
         ColumnScope var73 = ColumnScopeInstance.INSTANCE;
         int var74 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var72, 1786606977, "C131@5582L443,128@5429L596,146@6062L394,168@6915L1399,159@6493L1821,197@8327L42:HomeScreen.kt#ac106p"
         );
         Modifier var75 = var73.weight(Modifier.Companion, 1.0F, true);
         boolean var76 = var47 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Error;
         AnimatedVisibilityKt.AnimatedVisibility(
            var73,
            var76,
            var75,
            null,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1537640308, true, new HomeScreenKt$HomeScreen$3$2$1(var47, var10), var72, 54),
            var72,
            1572864 | 14 & var71,
            28
         );
         AnimatedVisibilityKt.AnimatedVisibility(
            var73,
            var47 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Loading,
            null,
            null,
            ExitTransition.Companion.getNone(),
            null,
            io.intercom.android.sdk.m5.home.ui.ComposableSingletons$HomeScreenKt.INSTANCE.recovered$b26556cd5c386b55(),
            var72,
            1572864 | 14 & var71,
            22
         );
         AnimatedVisibilityKt.AnimatedVisibility(
            var73,
            var47 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Content,
            null,
            EnterExitTransitionKt.fadeIn$default(AnimationSpecKt.tween$default(600, 600, null, 4, null), 0.0F, 2, null),
            EnterExitTransitionKt.fadeOut$default(AnimationSpecKt.tween$default(600, 0, null, 6, null), 0.0F, 2, null),
            null,
            ComposableLambdaKt.rememberComposableLambda(
               21007876, true, new HomeScreenKt$HomeScreen$3$2$2(var47, var18, var19, var1, var2, var3, var4, var8, var9, var11), var72, 54
            ),
            var72,
            1600512 | 14 & var71,
            18
         );
         Modifier var199 = Modifier.Companion;
         int var192 = 100;
         int var194 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var199, Dp.recovered$95b3d103663bf060(var192)), var72, 6);
         ComposerKt.sourceInformationMarkerEnd(var72);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var63.endNode();
         ComposerKt.sourceInformationMarkerEnd(var63);
         ComposerKt.sourceInformationMarkerEnd(var56);
         ComposerKt.sourceInformationMarkerEnd(var44);
         CompositionLocal var134 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var44, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var128 = var44.consume(var134);
         ComposerKt.sourceInformationMarkerEnd(var44);
         Context var125 = (Context)var128;
         PoweredBy var135 = ((HomeUiState)var17.getValue()).getPoweredBy();
         var44.startReplaceGroup(748852392);
         ComposerKt.sourceInformation(var44, "*206@8660L303,202@8488L568");
         if (var135 == null) {
            Object var201 = null;
         } else {
            PoweredBy var129 = var135;
            int var138 = 0;
            Modifier var144 = var45.align(WindowInsetsPadding_androidKt.safeDrawingPadding(Modifier.Companion), Alignment.Companion.getBottomCenter());
            String var149 = var129.getText();
            io.intercom.android.sdk.models.PoweredBy$PoweredByIconType var155 = var129.getIcon();
            String var200 = var149;
            io.intercom.android.sdk.models.PoweredBy$PoweredByIconType var206 = var155;
            var44.startReplaceGroup(-1050645419);
            ComposerKt.sourceInformation(var44, "CC(remember):HomeScreen.kt#9igjgp");
            Composer var157 = var44;
            boolean var162 = var44.changed(var129) | var44.changedInstance(var125);
            int $i$f$cachexx = 0;
            Object it$ivxx = var157.rememberedValue();
            int var169/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var10002;
            if (!var162 && it$ivxx != Composer.Companion.getEmpty()) {
               var10002 = it$ivxx;
            } else {
               io.intercom.android.sdk.models.PoweredBy$PoweredByIconType var171 = var155;
               String var173 = var149;
               int var175 = 0;
               final io.intercom.android.sdk.models.PoweredBy recoveredExpressionCapture_903_0 = var129;
               final android.content.Context recoveredExpressionCapture_903_1 = var125;
               Function0 var178 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.HomeScreenKt.HomeScreen$lambda$14$lambda$8$lambda$7$lambda$6(recoveredExpressionCapture_903_0, recoveredExpressionCapture_903_1));
               var200 = var173;
               var206 = var171;
               Object var180 = var178;
               var157.updateRememberedValue(var180);
               var10002 = var180;
            }

            Function0 var183 = (Function0)var10002;
            var44.endReplaceGroup();
            PoweredByBadgeKt.recovered$9b47502c50dedaf9(var200, var206, var183, var144, 0L, 0L, var44, 0, 48);
         }

         var44.endReplaceGroup();
         var44.startReplaceGroup(748873455);
         ComposerKt.sourceInformation(var44, "*238@9929L18,231@9617L1299");
         if (var47 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Content) {
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$CloseButtonColor var137 = ((io.intercom.android.sdk.m5.home.states.HomeUiState$Content)var47).getHeader().getCloseButtonColor();
            int var127 = 0;
            var44.startReplaceGroup(748879182);
            ComposerKt.sourceInformation(var44, "*223@9358L19");
            int var139 = 0;
            boolean var145 = ThemeUtils.INSTANCE.isDarkModeCompose$intercom_sdk_base_release(var44, 6);
            String var150 = var145 && var137.getForegroundColorDark() != null ? var137.getForegroundColorDark() : var137.getForegroundColor();
            var44.endReplaceGroup();
            String var156 = var150;
            Modifier var151 = WindowInsetsPadding_androidKt.safeDrawingPadding(Modifier.Companion);
            int $this$dp$ivx = 14;
            int $i$f$getDpx = 0;
            float var158 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            int $this$dp$ivxx = -16;
            int $i$f$getDpxx = 0;
            float var131 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
            Modifier var202 = ClipKt.clip(
               var45.align(OffsetKt.recovered$ed7db252cbfb0f30(var151, var131, var158), Alignment.Companion.getTopEnd()), RoundedCornerShapeKt.getCircleShape()
            );
            int $this$dp$ivxxx = 30;
            int $i$f$getDpxxx = 0;
            Modifier var203 = SizeKt.recovered$111ef94c93e78c04(var202, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            boolean var207 = false;
            Object var208 = null;
            Object var10003 = null;
            Object var10004 = null;
            var44.startReplaceGroup(-1050605096);
            ComposerKt.sourceInformation(var44, "CC(remember):HomeScreen.kt#9igjgp");
            Composer var160 = var44;
            boolean invalid$ivx = (var16 & 14) == 4;
            int $i$f$cachexx = 0;
            Object it$ivxx = var160.rememberedValue();
            int var163/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var10005;
            if (!invalid$ivx && it$ivxx != Composer.Companion.getEmpty()) {
               var10005 = it$ivxx;
            } else {
               Object var166 = null;
               Object var168 = null;
               Object var170 = null;
               boolean var172 = false;
               Modifier var174 = var203;
               int var176 = 0;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_904_0 = var10;
               Function0 var179 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.HomeScreenKt.HomeScreen$lambda$14$lambda$13$lambda$11$lambda$10(recoveredExpressionCapture_904_0));
               var203 = var174;
               var207 = var172;
               var208 = var170;
               var10003 = var168;
               var10004 = var166;
               Object var181 = var179;
               var160.updateRememberedValue(var181);
               var10005 = var181;
            }

            Function0 var153 = (Function0)var10005;
            var44.endReplaceGroup();
            Modifier var154 = ClickableKt.recovered$1c9660c41fd850dc(
               var203, var207, (String)var208, (Role)var10003, (MutableInteractionSource)var10004, var153, 15, null
            );
            byte var143 = 0;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var44, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment contentAlignment$ivx = Alignment.Companion.getTopStart();
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivxx = BoxKt.maybeCachedBoxMeasurePolicy(contentAlignment$ivx, propagateMinConstraints$ivx);
            Modifier var177 = var154;
            MeasurePolicy var182 = measurePolicy$ivxx;
            Composer var184 = var44;
            int var185 = 112 & var143 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var184, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var184, 0));
            CompositionLocalMap localMap$iv$ivxx = var184.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var184, var177);
            Function0 var190 = ComposeUiNode.Companion.getConstructor();
            Composer var191 = var184;
            int var193 = 6 | 896 & var185 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var191, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var191.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var191.startReusableNode();
            if (var191.getInserting()) {
               var191.createNode(var190);
            } else {
               var191.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var191);
            int var78/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var182, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var79 = 14 & var193 >> 6;
            Composer $composer$ivxx = var191;
            int var81/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var82 = 6 | 112 & var143 >> 6;
            Composer var83 = $composer$ivxx;
            BoxScope var84 = BoxScopeInstance.INSTANCE;
            int var85 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var83, -1214698383, "C244@10202L326,240@9989L539,254@10658L52,255@10757L44,252@10549L349:HomeScreen.kt#ac106p"
            );
            AnimatedVisibilityKt.AnimatedVisibility(
               var18.getValue() > var19.getFloatValue() * 0.6,
               null,
               EnterExitTransitionKt.fadeIn$default(null, 0.0F, 3, null),
               EnterExitTransitionKt.fadeOut$default(null, 0.0F, 3, null),
               null,
               ComposableLambdaKt.rememberComposableLambda(-1722206090, true, new HomeScreenKt$HomeScreen$3$4$2$1(var84, var137), var83, 54),
               var83,
               200064,
               18
            );
            Modifier var86 = var84.align(Modifier.Companion, Alignment.Companion.getCenter());
            Painter var87 = PainterResources_androidKt.painterResource(drawable.intercom_ic_close, var83, 0);
            String var88 = StringResources_androidKt.stringResource(string.intercom_close, var83, 0);
            long var89 = ColorExtensionsKt.toComposeColor$default(var156, 0.0F, 1, null);
            IconKt.recovered$e00adffb7fe45b02(var87, var88, var86, var89, var83, Painter.$stable, 0);
            ComposerKt.sourceInformationMarkerEnd(var83);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var191.endNode();
            ComposerKt.sourceInformationMarkerEnd(var191);
            ComposerKt.sourceInformationMarkerEnd(var184);
            ComposerKt.sourceInformationMarkerEnd(var44);
         }

         var44.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var41);
         var35.endNode();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var120);
         ComposerKt.sourceInformationMarkerEnd(var92);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var204 = var92.endRestartGroup();
      if (var204 != null) {
         final io.intercom.android.sdk.m5.home.HomeViewModel recoveredExpressionCapture_905_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_905_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_905_7 = var7;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_8 = var8;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_905_9 = var9;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_905_10 = var10;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_905_11 = var11;
         final int recoveredExpressionCapture_905_12 = var13;
         final int recoveredExpressionCapture_905_13 = var14;
         var204.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_905_0, recoveredExpressionArgument_905_1) -> io.intercom.android.sdk.m5.home.ui.HomeScreenKt.HomeScreen$lambda$15(recoveredExpressionCapture_905_0, recoveredExpressionCapture_905_1, recoveredExpressionCapture_905_2, recoveredExpressionCapture_905_3, recoveredExpressionCapture_905_4, recoveredExpressionCapture_905_5, recoveredExpressionCapture_905_6, recoveredExpressionCapture_905_7, recoveredExpressionCapture_905_8, recoveredExpressionCapture_905_9, recoveredExpressionCapture_905_10, recoveredExpressionCapture_905_11, recoveredExpressionCapture_905_12, recoveredExpressionCapture_905_13, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_905_0), ((java.lang.Integer)recoveredExpressionArgument_905_1).intValue())));
      }
   }

   private static final float getHeaderContentOpacity(int var0, float var1) {
      float var2 = var1 - var0;
      return RangesKt.coerceIn(var2 / var1, 0.0F, 1.0F);
   }

   private static final boolean isDarkContentEnabled(HomeUiState var0) {
      HomeUiState var1 = var0;
      return var1 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Content
         ? ColorExtensionsKt.recovered$41e561cae61044c5(
            ColorExtensionsKt.toComposeColor$default(((io.intercom.android.sdk.m5.home.states.HomeUiState$Content)var0).getHeader().getIntro().getTextColorLight(), 0.0F, 1, null)
         )
         : (
            var1 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Error
               ? ColorExtensionsKt.recovered$41e561cae61044c5(
                  ColorExtensionsKt.toComposeColor$default(((io.intercom.android.sdk.m5.home.states.HomeUiState$Error)var0).getHeader().getForegroundColor(), 0.0F, 1, null)
               )
               : true
         );
   }

   private static final Unit HomeScreen$lambda$4$lambda$3$lambda$2(Window var0, State var1) {
      WindowCompat.getInsetsController(var0, var0.getDecorView()).setAppearanceLightStatusBars(isDarkContentEnabled((HomeUiState)var1.getValue()));
      return Unit.INSTANCE;
   }

   private static final Unit HomeScreen$lambda$14$lambda$8$lambda$7$lambda$6(PoweredBy var0, Context var1) {
      Injector.get().getMetricTracker().clickedPoweredBy("home");
      LinkOpener.handleUrl(var0.getLinkUrl(), var1, Injector.get().getApi());
      return Unit.INSTANCE;
   }

   private static final Unit HomeScreen$lambda$14$lambda$13$lambda$11$lambda$10(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit HomeScreen$lambda$15(
      HomeViewModel var0,
      Function0 var1,
      Function0 var2,
      Function0 var3,
      Function1 var4,
      Function0 var5,
      Function0 var6,
      Function1 var7,
      Function0 var8,
      Function1 var9,
      Function0 var10,
      Function1 var11,
      int var12,
      int var13,
      Composer var14,
      int var15
   ) {
      HomeScreen(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var14,
         RecomposeScopeImplKt.updateChangedFlags(var12 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var13)
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final float access$getHeaderContentOpacity(int var0, float var1) {
      return getHeaderContentOpacity(var0, var1);
   }
}
