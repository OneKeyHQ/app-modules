package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.InfiniteRepeatableSpec;
import androidx.compose.animation.core.InfiniteTransition;
import androidx.compose.animation.core.InfiniteTransitionKt;
import androidx.compose.animation.core.RepeatMode;
import androidx.compose.animation.core.StartOffset;
import androidx.compose.animation.core.StartOffsetType;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.CanvasKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.extension.ModifierExtensionsKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
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
   d1 = "\u0000.\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\u001a+\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0004\b\n\u0010\u000b\u001a\u0017\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\u000f\u001a\u001b\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\u000f\u001a\r\u0010\u0016\u001a\u00020\u0003H\u0007¢\u0006\u0002\u0010\u000f\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0017²\u0006\n\u0010\u0018\u001a\u00020\u0012X\u008a\u0084\u0002",
   d2 = {
         "AnimateDuration",
         "",
         "TypingIndicator",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "typingIndicatorData",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "avatarSize",
         "Landroidx/compose/ui/unit/Dp;",
         "TypingIndicator-6a0pyJM",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;FLandroidx/compose/runtime/Composer;II)V",
         "AnimatedTypingDots",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TeammateTypingIndicator",
         "(Landroidx/compose/runtime/Composer;I)V",
         "animateDotAlpha",
         "Landroidx/compose/runtime/State;",
         "",
         "delayMillis",
         "(ILandroidx/compose/runtime/Composer;I)Landroidx/compose/runtime/State;",
         "TypingIndicatorPreview",
         "TypingIndicatorWithoutAvatarPreview",
         "intercom-sdk-base_release",
         "alpha"
   }
)
@SourceDebugExtension(
   "SMAP\nTypingIndicator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypingIndicator.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TypingIndicatorKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,174:1\n118#2:175\n118#2:176\n118#2:209\n118#2:239\n118#2:251\n118#2:252\n118#2:259\n99#3,6:177\n106#3:208\n99#3,6:210\n106#3:250\n81#4,6:183\n88#4,6:198\n96#4:207\n81#4,6:216\n88#4,6:231\n96#4:249\n402#5,9:189\n411#5,3:204\n402#5,9:222\n411#5:237\n412#5,2:247\n1869#6:238\n1870#6:246\n1047#7,6:240\n1047#7,6:253\n85#8:260\n*S KotlinDebug\n*F\n+ 1 TypingIndicator.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/TypingIndicatorKt\n*L\n45#1:175\n50#1:176\n67#1:209\n73#1:239\n83#1:251\n84#1:252\n98#1:259\n47#1:177,6\n47#1:208\n64#1:210,6\n64#1:250\n47#1:183,6\n47#1:198,6\n47#1:207\n64#1:216,6\n64#1:231,6\n64#1:249\n47#1:189,9\n47#1:204,3\n64#1:222,9\n64#1:237\n64#1:247,2\n70#1:238\n70#1:246\n73#1:240,6\n93#1:253,6\n71#1:260\n*E\n"
)
public final class TypingIndicatorKt {
   private static final int AnimateDuration = 600;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$891ffcbb9c232a50(
      @Nullable Modifier var0, @NotNull CurrentlyTypingState var1, float var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "typingIndicatorData");
      var3 = var3.startRestartGroup(1574154580);
      ComposerKt.sourceInformation(var3, "C(TypingIndicator)P(1,2,0:c#ui.unit.Dp)46@2000L412:TypingIndicator.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
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
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            int var7 = 36;
            int var8 = 0;
            var2 = Dp.recovered$95b3d103663bf060(var7);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1574154580, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicator (TypingIndicator.kt:45)"
            );
         }

         Vertical var35 = Alignment.Companion.getCenterVertically();
         int var9 = 8;
         int var10 = 0;
         HorizontalOrVertical var36 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var9));
         var9 = 432 | 14 & var6;
         var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var36, var35, var3, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var0;
         MeasurePolicy var13 = var11;
         Composer var14 = var3;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -290644409, "C57@2381L25:TypingIndicator.kt#hr3bi5");
         var30.startReplaceGroup(-1949038310);
         ComposerKt.sourceInformation(var30, "52@2216L146");
         if (var1.getShowAvatar()) {
            AvatarWrapper var33 = var1.getAvatarWrapper();
            AvatarIconKt.recovered$f5ec3c81e3146789(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var2), var33, null, false, 0L, null, var30, 0, 60);
         }

         var30.endReplaceGroup();
         TeammateTypingIndicator(var30, 0);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_804_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState recoveredCapture_804_1 = var1;
         final float recoveredCapture_804_2 = var2;
         final int recoveredCapture_804_3 = var4;
         final int recoveredCapture_804_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_804_0, recoveredArgument_804_1) -> TypingIndicatorKt.TypingIndicator_6a0pyJM$lambda$1(recoveredCapture_804_0, recoveredCapture_804_1, recoveredCapture_804_2, recoveredCapture_804_3, recoveredCapture_804_4, (androidx.compose.runtime.Composer)recoveredArgument_804_0, ((java.lang.Integer)recoveredArgument_804_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AnimatedTypingDots(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(-950116908);
      ComposerKt.sourceInformation(var1, "C(AnimatedTypingDots)63@2497L505:TypingIndicator.kt#hr3bi5");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -950116908, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AnimatedTypingDots (TypingIndicator.kt:62)"
            );
         }

         Vertical var5 = Alignment.Companion.getCenterVertically();
         int var7 = 4;
         int var8 = 0;
         HorizontalOrVertical var6 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var7));
         var7 = 432 | 14 & var4;
         var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var9 = RowKt.rowMeasurePolicy(var6, var5, var1, 14 & var7 >> 3 | 112 & var7 >> 3);
         Modifier var10 = var0;
         MeasurePolicy var11 = var9;
         Composer var12 = var1;
         int var13 = 112 & var7 << 3;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
         CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
         Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
         Function0 var18 = ComposeUiNode.Companion.getConstructor();
         Composer var19 = var12;
         int var20 = 6 | 896 & var13 << 6;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var19.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var19.startReusableNode();
         if (var19.getInserting()) {
            var19.createNode(var18);
         } else {
            var19.useNode();
         }

         Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
         int var23 = 0;
         Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
         int var24 = 14 & var20 >> 6;
         Composer var25 = var19;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         RowScope var29 = RowScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 601129256, "C:TypingIndicator.kt#hr3bi5");
         Integer[] var31 = new Integer[]{0, 200, 400};
         List var32 = CollectionsKt.listOf(var31);
         var28.startReplaceGroup(850676747);
         ComposerKt.sourceInformation(var28, "*70@2761L42,71@2842L6,72@2909L77,72@2870L116");
         Iterable var53 = var32;
         int var33 = 0;

         for (Object var35 : var53) {
            int var36 = ((Number)var35).intValue();
            int var37 = 0;
            State var38 = animateDotAlpha(var36, var28, 0);
            long var39 = IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).recovered$4983b1a7f0ae0645();
            Modifier var10000 = Modifier.Companion;
            int $this$dp$ivx = 8;
            int $i$f$getDpx = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            var28.startReplaceGroup(387186938);
            ComposerKt.sourceInformation(var28, "CC(remember):TypingIndicator.kt#9igjgp");
            Composer var55 = var28;
            boolean var43 = var28.changed(var39) | var28.changed(var38);
            int var44 = 0;
            Object var45 = var55.rememberedValue();
            int var46 = 0;
            kotlin.jvm.functions.Function1 var58;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var58 = (Function1)var45;
            } else {
               Modifier var47 = var10000;
               int var48 = 0;
               final long recoveredCapture_798_0 = var39;
               final androidx.compose.runtime.State recoveredCapture_798_1 = var38;
               var58 = ((kotlin.jvm.functions.Function1) (recoveredArgument_798_0) -> TypingIndicatorKt.AnimatedTypingDots$lambda$6$lambda$5$lambda$4$lambda$3(recoveredCapture_798_0, recoveredCapture_798_1, (androidx.compose.ui.graphics.drawscope.DrawScope)recoveredArgument_798_0));
               var10000 = var47;
               Object var49 = var58;
               var55.updateRememberedValue(var49);
               var58 = (Function1)var49;
            }

            Function1 var54 = var58;
            var28.endReplaceGroup();
            CanvasKt.Canvas(var10000, var54, var28, 6);
         }

         var28.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var57 = var1.endRestartGroup();
      if (var57 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_799_0 = var0;
         final int recoveredCapture_799_1 = var2;
         final int recoveredCapture_799_2 = var3;
         var57.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_799_0, recoveredArgument_799_1) -> TypingIndicatorKt.AnimatedTypingDots$lambda$7(recoveredCapture_799_0, recoveredCapture_799_1, recoveredCapture_799_2, (androidx.compose.runtime.Composer)recoveredArgument_799_0, ((java.lang.Integer)recoveredArgument_799_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TeammateTypingIndicator(Composer var0, int var1) {
      var0 = var0.startRestartGroup(349650241);
      ComposerKt.sourceInformation(var0, "C(TeammateTypingIndicator)83@3195L6,84@3246L6,92@3489L158,86@3280L433:TypingIndicator.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               349650241, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TeammateTypingIndicator (TypingIndicator.kt:80)"
            );
         }

         int var3 = 20;
         int var4 = 0;
         Shape var10002 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var3));
         var3 = 1;
         var4 = 0;
         TypingIndicatorStyle var2 = new TypingIndicatorStyle(
            var10002,
            BorderStrokeKt.recovered$91879a1bef2fb536(
               Dp.recovered$95b3d103663bf060(var3), IntercomTheme.INSTANCE.getColors(var0, IntercomTheme.$stable).recovered$8c831c6299defa27()
            ),
            IntercomTheme.INSTANCE.getColors(var0, IntercomTheme.$stable).recovered$61a1caf6b6549f74(),
            null
         );
         Modifier var10000 = BackgroundKt.recovered$60bc47fb440df483(Modifier.Companion, var2.recovered$6f15fcf6c761ad06(), var2.getShape());
         boolean var10001 = var2.getBorderStroke() != null;
         var0.startReplaceGroup(-676438007);
         ComposerKt.sourceInformation(var0, "CC(remember):TypingIndicator.kt#9igjgp");
         Composer var20 = var0;
         boolean var5 = var0.changed(var2);
         int var6 = 0;
         Object var7 = var20.rememberedValue();
         int var8 = 0;
         Object var26;
         if (!var5 && var7 != Composer.Companion.getEmpty()) {
            var26 = var7;
         } else {
            boolean var12 = var10001;
            Modifier var11 = var10000;
            int var9 = 0;
            final io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicatorStyle recoveredCapture_800_0 = var2;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_800_0) -> TypingIndicatorKt.TeammateTypingIndicator$lambda$10$lambda$9(recoveredCapture_800_0, (androidx.compose.ui.Modifier)recoveredArgument_800_0));
            var10000 = var11;
            var10001 = var12;
            Object var10 = var13;
            var20.updateRememberedValue(var10);
            var26 = var10;
         }

         Function1 var16 = (Function1)var26;
         var0.endReplaceGroup();
         var10000 = ModifierExtensionsKt.ifTrue(var10000, var10001, var16);
         var3 = 16;
         var4 = 0;
         float var25 = Dp.recovered$95b3d103663bf060(var3);
         var3 = 18;
         var4 = 0;
         AnimatedTypingDots(PaddingKt.recovered$904403cea4c49d95(var10000, var25, Dp.recovered$95b3d103663bf060(var3)), var0, 0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var24 = var0.endRestartGroup();
      if (var24 != null) {
         final int recoveredCapture_801_0 = var1;
         var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_801_0, recoveredArgument_801_1) -> TypingIndicatorKt.TeammateTypingIndicator$lambda$11(recoveredCapture_801_0, (androidx.compose.runtime.Composer)recoveredArgument_801_0, ((java.lang.Integer)recoveredArgument_801_1).intValue())));
      }
   }

   @Composable
   private static final State animateDotAlpha(int var0, Composer var1, int var2) {
      var1.startReplaceGroup(-1913274997);
      ComposerKt.sourceInformation(var1, "C(animateDotAlpha)109@3945L64,110@4040L375:TypingIndicator.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1913274997, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.animateDotAlpha (TypingIndicator.kt:108)");
      }

      InfiniteTransition var3 = InfiniteTransitionKt.rememberInfiniteTransition("IsTypingInfiniteTransition", var1, 6, 0);
      State var4 = InfiniteTransitionKt.animateFloat(
         var3,
         1.0F,
         0.1F,
         AnimationSpecKt.recovered$bc7eb28e068e7932(
            AnimationSpecKt.tween$default(600, 0, null, 6, null),
            RepeatMode.Reverse,
            StartOffset.recovered$324f0ad34d5c99a9(var0, StartOffsetType.Companion.recovered$e4ace4baca7365a2())
         ),
         "IsTypingAnimation",
         var1,
         25008 | InfiniteTransition.$stable | InfiniteRepeatableSpec.$stable << 9,
         0
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var4;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TypingIndicatorPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-955207145);
      ComposerKt.sourceInformation(var0, "C(TypingIndicatorPreview)125@4491L1023:TypingIndicator.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -955207145, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicatorPreview (TypingIndicator.kt:124)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TypingIndicatorKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_802_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_802_0, recoveredArgument_802_1) -> TypingIndicatorKt.TypingIndicatorPreview$lambda$12(recoveredCapture_802_0, (androidx.compose.runtime.Composer)recoveredArgument_802_0, ((java.lang.Integer)recoveredArgument_802_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TypingIndicatorWithoutAvatarPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-544244118);
      ComposerKt.sourceInformation(var0, "C(TypingIndicatorWithoutAvatarPreview)158@5603L439:TypingIndicator.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -544244118, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicatorWithoutAvatarPreview (TypingIndicator.kt:157)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TypingIndicatorKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_803_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_803_0, recoveredArgument_803_1) -> TypingIndicatorKt.TypingIndicatorWithoutAvatarPreview$lambda$13(recoveredCapture_803_0, (androidx.compose.runtime.Composer)recoveredArgument_803_0, ((java.lang.Integer)recoveredArgument_803_1).intValue())));
      }
   }

   private static final Unit TypingIndicator_6a0pyJM$lambda$1(Modifier var0, CurrentlyTypingState var1, float var2, int var3, int var4, Composer var5, int var6) {
      recovered$891ffcbb9c232a50(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final float AnimatedTypingDots$lambda$6$lambda$5$lambda$2(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return ((Number)var1.getValue()).floatValue();
   }

   private static final Unit AnimatedTypingDots$lambda$6$lambda$5$lambda$4$lambda$3(long var0, State var2, DrawScope var3) {
      Intrinsics.checkNotNullParameter(var3, "$this$Canvas");
      DrawScope.recovered$34dce0a89fdcb963(
         var3,
         Color.recovered$41aa0ee03c2282fb(var0, AnimatedTypingDots$lambda$6$lambda$5$lambda$2(var2), 0.0F, 0.0F, 0.0F, 14, null),
         0.0F,
         0L,
         0.0F,
         null,
         null,
         0,
         126,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit AnimatedTypingDots$lambda$7(Modifier var0, int var1, int var2, Composer var3, int var4) {
      AnimatedTypingDots(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Modifier TeammateTypingIndicator$lambda$10$lambda$9(TypingIndicatorStyle var0, Modifier var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$ifTrue");
      BorderStroke var10000 = var0.getBorderStroke();
      if (var10000 != null) {
         BorderStroke var2 = var10000;
         int var3 = 0;
         Modifier var4 = BorderKt.border(var1, var2, var0.getShape());
         if (var4 != null) {
            return var4;
         }
      }

      return var1;
   }

   private static final Unit TeammateTypingIndicator$lambda$11(int var0, Composer var1, int var2) {
      TeammateTypingIndicator(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TypingIndicatorPreview$lambda$12(int var0, Composer var1, int var2) {
      TypingIndicatorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TypingIndicatorWithoutAvatarPreview$lambda$13(int var0, Composer var1, int var2) {
      TypingIndicatorWithoutAvatarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
