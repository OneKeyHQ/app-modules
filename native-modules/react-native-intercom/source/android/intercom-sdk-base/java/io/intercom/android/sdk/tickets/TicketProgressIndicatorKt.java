package io.intercom.android.sdk.tickets;

import androidx.compose.animation.TransitionKt;
import androidx.compose.animation.core.AnimationSpecKt;
import androidx.compose.animation.core.EasingKt;
import androidx.compose.animation.core.InfiniteRepeatableSpec;
import androidx.compose.animation.core.InfiniteTransition;
import androidx.compose.animation.core.InfiniteTransitionKt;
import androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.ProgressIndicatorKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.StrokeCap;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a/\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0004\b\t\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f¨\u0006\r",
   d2 = {
         "TicketProgressIndicator",
         "",
         "progressSections",
         "",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ProgressSection;",
         "progressColor",
         "Landroidx/compose/ui/graphics/Color;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "TicketProgressIndicator-3IgeMak",
         "(Ljava/util/List;JLandroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketProgressIndicatorPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketProgressIndicator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketProgressIndicator.kt\nio/intercom/android/sdk/tickets/TicketProgressIndicatorKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,99:1\n118#2:100\n118#2:145\n99#3:101\n97#3,8:102\n106#3:162\n81#4,6:110\n88#4,6:125\n96#4:161\n402#5,9:116\n411#5:131\n412#5,2:159\n1869#6:132\n1870#6:158\n1047#7,6:133\n1047#7,6:139\n1047#7,6:146\n1047#7,6:152\n*S KotlinDebug\n*F\n+ 1 TicketProgressIndicator.kt\nio/intercom/android/sdk/tickets/TicketProgressIndicatorKt\n*L\n32#1:100\n81#1:145\n31#1:101\n31#1:102,8\n31#1:162\n31#1:110,6\n31#1:125,6\n31#1:161\n31#1:116,9\n31#1:131\n31#1:159,2\n37#1:132\n37#1:158\n44#1:133,6\n62#1:139,6\n77#1:146,6\n82#1:152,6\n*E\n"
)
public final class TicketProgressIndicatorKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$8eea2a3ce3419e18(@NotNull List var0, long var1, @Nullable Modifier var3, @Nullable Composer var4, int var5, int var6) {
      Intrinsics.checkNotNullParameter(var0, "progressSections");
      Composer var78 = var4.startRestartGroup(484493125);
      ComposerKt.sourceInformation(
         var78, "C(TicketProgressIndicator)P(2,1:c#ui.graphics.Color)28@1196L65,29@1299L6,30@1319L2046:TicketProgressIndicator.kt#3ofz5f"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var78.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var78.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var78.changed(var3) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var78.getSkipping()) {
         var78.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var3 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(484493125, var7, -1, "io.intercom.android.sdk.tickets.TicketProgressIndicator (TicketProgressIndicator.kt:27)");
         }

         InfiniteTransition var8 = InfiniteTransitionKt.rememberInfiniteTransition("Infinite progress animation", var78, 6, 0);
         long var9 = IntercomTheme.INSTANCE.getColors(var78, IntercomTheme.$stable).recovered$090a6e753d82e539();
         int var12 = 4;
         int var13 = 0;
         HorizontalOrVertical var11 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var12));
         Modifier var79 = ClipKt.clip(SizeKt.fillMaxWidth$default(var3, 0.0F, 1, null), RoundedCornerShapeKt.RoundedCornerShape(50));
         byte var14 = 48;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var78, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Vertical var80 = Alignment.Companion.getTop();
         MeasurePolicy var16 = RowKt.rowMeasurePolicy(var11, var80, var78, 14 & var14 >> 3 | 112 & var14 >> 3);
         Modifier var17 = var79;
         MeasurePolicy var18 = var16;
         Composer var19 = var78;
         int var20 = 112 & var14 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var19, 0));
         CompositionLocalMap var23 = var19.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var19, var17);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var19;
         int var27 = 6 | 896 & var20 << 6;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var26.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var26.startReusableNode();
         if (var26.getInserting()) {
            var26.createNode(var25);
         } else {
            var26.useNode();
         }

         Composer var29 = Updater.recovered$676aa17b44fd6280(var26);
         int var30 = 0;
         Updater.recovered$89682b199a142880(var29, var18, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var34 = 6 | 112 & var14 >> 6;
         Composer var35 = var32;
         RowScope var36 = RowScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -1055440123, "C:TicketProgressIndicator.kt#3ofz5f");
         var35.startReplaceGroup(1767069389);
         ComposerKt.sourceInformation(var35, "*76@3069L20,81@3286L2,75@3017L332");
         Iterable var38 = var0;
         int var39 = 0;

         for (Object var41 : var38) {
            io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection var42 = (io.intercom.android.sdk.tickets.TicketTimelineCardState$ProgressSection)var41;
            int var43 = 0;
            var35.startReplaceGroup(1767070266);
            ComposerKt.sourceInformation(var35, "43@1849L177,39@1652L472");
            float var109;
            if (var42.isLoading()) {
               InfiniteTransition var10000 = var8;
               float var10001 = 0.0F;
               float var10002 = 1.0F;
               var35.startReplaceGroup(1463837930);
               ComposerKt.sourceInformation(var35, "CC(remember):TicketProgressIndicator.kt#9igjgp");
               Composer var44 = var35;
               boolean var45 = false;
               int var46 = 0;
               Object var47 = var44.rememberedValue();
               int var48 = 0;
               Object var10003;
               if (var47 == Composer.Companion.getEmpty()) {
                  float var49 = 1.0F;
                  float var50 = 0.0F;
                  InfiniteTransition var51 = var8;
                  int var52 = 0;
                  Function1 var53 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1549_0) -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$1$lambda$0((androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig) ((androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig)recoveredExpressionArgument_1549_0)));
                  var10000 = var51;
                  var10001 = var50;
                  var10002 = var49;
                  Object var96 = var53;
                  var44.updateRememberedValue(var96);
                  var10003 = var96;
               } else {
                  var10003 = var47;
               }

               Function1 var54 = (Function1)var10003;
               var35.endReplaceGroup();
               var109 = ((Number)InfiniteTransitionKt.animateFloat(
                        var10000,
                        var10001,
                        var10002,
                        AnimationSpecKt.recovered$bec71f8ce21a903d(AnimationSpecKt.keyframes(var54), null, 0L, 6, null),
                        "Progress value animation",
                        var35,
                        25008 | InfiniteTransition.$stable | InfiniteRepeatableSpec.$stable << 9,
                        0
                     )
                     .getValue())
                  .floatValue();
            } else {
               var109 = var42.isDone() ? 1.0F : 0.0F;
            }

            float var55 = var109;
            var35.endReplaceGroup();
            float var56 = var55;
            var35.startReplaceGroup(1767091849);
            ComposerKt.sourceInformation(var35, "61@2537L312,57@2323L621");
            long var111;
            if (var42.isLoading()) {
               InfiniteTransition var110 = var8;
               long var115 = var9;
               long var117 = var1;
               var35.startReplaceGroup(1463860081);
               ComposerKt.sourceInformation(var35, "CC(remember):TicketProgressIndicator.kt#9igjgp");
               Composer var86 = var35;
               boolean var90 = (var7 & 112) == 32 | var78.changed(var9);
               int var97 = 0;
               Object var57 = var86.rememberedValue();
               int var58 = 0;
               Object var119;
               if (!var90 && var57 != Composer.Companion.getEmpty()) {
                  var119 = var57;
               } else {
                  long var59 = var1;
                  long var61 = var9;
                  InfiniteTransition var94 = var8;
                  int var63 = 0;
                  final long recoveredExpressionCapture_1550_0 = var1;
                  final long recoveredExpressionCapture_1550_1 = var9;
                  Function1 var64 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1550_0) -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$3$lambda$2(recoveredExpressionCapture_1550_0, recoveredExpressionCapture_1550_1, (androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig) ((androidx.compose.animation.core.KeyframesSpec.KeyframesSpecConfig)recoveredExpressionArgument_1550_0)));
                  var110 = var94;
                  var115 = var61;
                  var117 = var59;
                  Object var65 = var64;
                  var86.updateRememberedValue(var65);
                  var119 = var65;
               }

               Function1 var83 = (Function1)var119;
               var35.endReplaceGroup();
               var111 = ((Color)TransitionKt.recovered$36e5374ecbab97a0(
                        var110,
                        var115,
                        var117,
                        AnimationSpecKt.recovered$bec71f8ce21a903d(AnimationSpecKt.keyframes(var83), null, 0L, 6, null),
                        "Color value animation",
                        var35,
                        24576 | InfiniteTransition.$stable | 896 & var7 << 3 | InfiniteRepeatableSpec.$stable << 9,
                        0
                     )
                     .getValue())
                  .recovered$45f9de7b85e0e0b5();
            } else {
               var111 = var1;
            }

            long var66 = var111;
            var35.endReplaceGroup();
            long var68 = var66;
            Modifier var81 = RowScope.weight$default(var36, Modifier.Companion, 1.0F, false, 2, null);
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            float var82 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            int var85 = StrokeCap.Companion.recovered$d76274c3207f4f6e();
            var35.startReplaceGroup(1463876813);
            ComposerKt.sourceInformation(var35, "CC(remember):TicketProgressIndicator.kt#9igjgp");
            Composer var91 = var35;
            boolean var98 = var35.changed(var56);
            int var100 = 0;
            Object var102 = var91.rememberedValue();
            int var104 = 0;
            Object var112;
            if (!var98 && var102 != Composer.Companion.getEmpty()) {
               var112 = var102;
            } else {
               int var106 = 0;
               final float recoveredExpressionCapture_1551_0 = var56;
               Object var70 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$5$lambda$4(recoveredExpressionCapture_1551_0));
               var91.updateRememberedValue(var70);
               var112 = var70;
            }

            Function0 var88 = (Function0)var112;
            var35.endReplaceGroup();
            Function0 var113 = var88;
            Modifier var116 = var81;
            long var118 = var68;
            long var120 = var9;
            int var10004 = var85;
            float var10005 = var82;
            var35.startReplaceGroup(1463883739);
            ComposerKt.sourceInformation(var35, "CC(remember):TicketProgressIndicator.kt#9igjgp");
            Composer var92 = var35;
            boolean var99 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var92.rememberedValue();
            int var105/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10006;
            if (it$ivx == Composer.Companion.getEmpty()) {
               float var71 = var82;
               int var72 = var85;
               long var73 = var9;
               long var75 = var68;
               Modifier var93 = var81;
               Function0 var95 = var88;
               int var107 = 0;
               Function1 var77 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1552_0) -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$7$lambda$6((androidx.compose.ui.graphics.drawscope.DrawScope) ((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredExpressionArgument_1552_0)));
               var113 = var95;
               var116 = var93;
               var118 = var75;
               var120 = var73;
               var10004 = var72;
               var10005 = var71;
               Object var108 = var77;
               var92.updateRememberedValue(var108);
               var10006 = var108;
            } else {
               var10006 = it$ivx;
            }

            Function1 var89 = (Function1)var10006;
            var35.endReplaceGroup();
            ProgressIndicatorKt.recovered$2e468734cd9041b5(var113, var116, var118, var120, var10004, var10005, var89, var35, 1769472, 0);
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var78);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var114 = var78.endRestartGroup();
      if (var114 != null) {
         final java.util.List recoveredExpressionCapture_1548_0 = var0;
         final long recoveredExpressionCapture_1548_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1548_2 = var3;
         final int recoveredExpressionCapture_1548_3 = var5;
         final int recoveredExpressionCapture_1548_4 = var6;
         var114.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1548_0, recoveredExpressionArgument_1548_1) -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicator_3IgeMak$lambda$10(recoveredExpressionCapture_1548_0, recoveredExpressionCapture_1548_1, recoveredExpressionCapture_1548_2, recoveredExpressionCapture_1548_3, recoveredExpressionCapture_1548_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1548_0), ((java.lang.Integer)recoveredExpressionArgument_1548_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketProgressIndicatorPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1245553611);
      ComposerKt.sourceInformation(var2, "C(TicketProgressIndicatorPreview)91@3451L252:TicketProgressIndicator.kt#3ofz5f");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1245553611, var1, -1, "io.intercom.android.sdk.tickets.TicketProgressIndicatorPreview (TicketProgressIndicator.kt:90)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.tickets.ComposableSingletons$TicketProgressIndicatorKt.INSTANCE.recovered$eac478aa65045e5d(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1547_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1547_0, recoveredExpressionArgument_1547_1) -> io.intercom.android.sdk.tickets.TicketProgressIndicatorKt.TicketProgressIndicatorPreview$lambda$11(recoveredExpressionCapture_1547_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1547_0), ((java.lang.Integer)recoveredExpressionArgument_1547_1).intValue())));
      }
   }

   private static final Unit TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$1$lambda$0(KeyframesSpecConfig var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$keyframes");
      var0.setDurationMillis(3200);
      var0.using(var0.at(1.0F, 850), EasingKt.getLinearEasing());
      var0.at(1.0F, 3200);
      return Unit.INSTANCE;
   }

   private static final Unit TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$3$lambda$2(long var0, long var2, KeyframesSpecConfig var4) {
      Intrinsics.checkNotNullParameter(var4, "$this$keyframes");
      var4.setDurationMillis(3200);
      var4.using(var4.at(Color.recovered$f46ef195ad4a2363(var0), 850), EasingKt.getLinearEasing());
      var4.at(Color.recovered$f46ef195ad4a2363(var0), 1850);
      var4.using(var4.at(Color.recovered$f46ef195ad4a2363(var2), 2200), EasingKt.getLinearEasing());
      var4.at(Color.recovered$f46ef195ad4a2363(var2), 3200);
      return Unit.INSTANCE;
   }

   private static final float TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$5$lambda$4(float var0) {
      return var0;
   }

   private static final Unit TicketProgressIndicator_3IgeMak$lambda$9$lambda$8$lambda$7$lambda$6(DrawScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$LinearProgressIndicator");
      return Unit.INSTANCE;
   }

   private static final Unit TicketProgressIndicator_3IgeMak$lambda$10(List var0, long var1, Modifier var3, int var4, int var5, Composer var6, int var7) {
      recovered$8eea2a3ce3419e18(var0, var1, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit TicketProgressIndicatorPreview$lambda$11(int var0, Composer var1, int var2) {
      TicketProgressIndicatorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
