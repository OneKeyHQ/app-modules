package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
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
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.graphics.ClipOp;
import androidx.compose.ui.graphics.drawscope.ContentDrawScope;
import androidx.compose.ui.graphics.drawscope.DrawContext;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.graphics.drawscope.DrawTransform;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.LayoutDirection;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
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
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0004\u001aK\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0016\u0010\u0006\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0004\b\f\u0010\r\u001a&\u0010\u000e\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0005\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00072\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0010H\u0000\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012¨\u0006\u0014",
   d2 = {
         "BotAndHumansFacePile",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "botAvatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "teammateAvatarPair",
         "Lkotlin/Pair;",
         "botAvatarSize",
         "Landroidx/compose/ui/unit/Dp;",
         "botName",
         "",
         "BotAndHumansFacePile-hGBTI10",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/models/Avatar;Lkotlin/Pair;FLjava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "humanAvatarPairForHome",
         "humanAvatars",
         "",
         "BotWithTwoTeammatesPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "BotsWithOneTeammatePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nBotAndHumansFacePile.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BotAndHumansFacePile.kt\nio/intercom/android/sdk/m5/components/BotAndHumansFacePileKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 8 DrawScope.kt\nandroidx/compose/ui/graphics/drawscope/DrawScopeKt\n+ 9 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 10 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 11 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n*L\n1#1,123:1\n132#2:124\n132#2:125\n132#2:126\n52#3:127\n52#3:172\n52#3:252\n99#4,6:128\n106#4:171\n81#5,6:134\n88#5,6:149\n96#5:170\n402#6,9:140\n411#6:155\n412#6,2:168\n1047#7,6:156\n1047#7,6:162\n202#8,5:173\n207#8,3:181\n249#8,14:184\n202#8,4:198\n206#8:205\n207#8,3:208\n249#8,14:211\n202#8,4:225\n206#8:232\n207#8,3:235\n249#8,14:238\n202#8,5:253\n207#8,3:261\n249#8,14:264\n61#9:178\n57#9:202\n61#9:206\n57#9:229\n61#9:233\n61#9:258\n70#10:179\n60#10:203\n70#10:207\n60#10:230\n70#10:234\n70#10:259\n23#11:180\n23#11:204\n23#11:231\n23#11:260\n*S KotlinDebug\n*F\n+ 1 BotAndHumansFacePile.kt\nio/intercom/android/sdk/m5/components/BotAndHumansFacePileKt\n*L\n29#1:124\n30#1:125\n31#1:126\n35#1:127\n45#1:172\n73#1:252\n33#1:128,6\n33#1:171\n33#1:134,6\n33#1:149,6\n33#1:170\n33#1:140,9\n33#1:155\n33#1:168,2\n43#1:156,6\n67#1:162,6\n45#1:173,5\n45#1:181,3\n45#1:184,14\n49#1:198,4\n49#1:205\n49#1:208,3\n49#1:211,14\n69#1:225,4\n69#1:232\n69#1:235,3\n69#1:238,14\n73#1:253,5\n73#1:261,3\n73#1:264,14\n45#1:178\n49#1:202\n49#1:206\n69#1:229\n69#1:233\n73#1:258\n45#1:179\n49#1:203\n49#1:207\n69#1:230\n69#1:234\n73#1:259\n45#1:180\n49#1:204\n69#1:231\n73#1:260\n*E\n"
)
public final class BotAndHumansFacePileKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$2d180ffbba3167ff(
      @Nullable Modifier var0, @NotNull Avatar var1, @NotNull Pair var2, float var3, @Nullable String var4, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "botAvatar");
      Intrinsics.checkNotNullParameter(var2, "teammateAvatarPair");
      var5 = var5.startRestartGroup(957129373);
      ComposerKt.sourceInformation(var5, "C(BotAndHumansFacePile)P(3!1,4,1:c#ui.unit.Dp)32@1199L1844:BotAndHumansFacePile.kt#arqjag");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 16) != 0) {
            var4 = "";
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(957129373, var8, -1, "io.intercom.android.sdk.m5.components.BotAndHumansFacePile (BotAndHumansFacePile.kt:27)");
         }

         double var10 = 0.75;
         int var12 = 0;
         float var9 = Dp.recovered$95b3d103663bf060((float)var10 * var3);
         double $this$times_u2d3ABfNKs$ivx = 0.25;
         int var13/* $VF was: $i$f$times-3ABfNKsx */ = 0;
         float var52 = Dp.recovered$95b3d103663bf060((float)$this$times_u2d3ABfNKs$ivx * var9);
         double $this$times_u2d3ABfNKs$ivxx = 0.0625;
         int var14/* $VF was: $i$f$times-3ABfNKsxx */ = 0;
         float var53 = Dp.recovered$95b3d103663bf060((float)$this$times_u2d3ABfNKs$ivxx * var3);
         var12 = 0;
         Horizontal var56 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var53 - var52));
         Vertical var57 = Alignment.Companion.getCenterVertically();
         var14/* $VF was: $i$f$times-3ABfNKsxx */ = 384 | 14 & var8;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var16 = RowKt.rowMeasurePolicy(
            var56, var57, var5, 14 & var14/* $VF was: $i$f$times-3ABfNKsxx */ >> 3 | 112 & var14/* $VF was: $i$f$times-3ABfNKsxx */ >> 3
         );
         Modifier var17 = var0;
         MeasurePolicy var18 = var16;
         Composer var19 = var5;
         int var20 = 112 & var14/* $VF was: $i$f$times-3ABfNKsxx */ << 3;
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
         int var34 = 6 | 112 & var14/* $VF was: $i$f$times-3ABfNKsxx */ >> 6;
         Composer var35 = var32;
         RowScope var36 = RowScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -1025646038, "C56@2147L134:BotAndHumansFacePile.kt#arqjag");
         Avatar var38 = (Avatar)var2.getFirst();
         var35.startReplaceGroup(1213840539);
         ComposerKt.sourceInformation(var35, "*42@1616L496,38@1434L693");
         if (var38 != null) {
            Avatar var39 = var38;
            int var40 = 0;
            AvatarWrapper var41 = new AvatarWrapper(var39, false, 2, null);
            Modifier var10000 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var9);
            var35.startReplaceGroup(1012545465);
            ComposerKt.sourceInformation(var35, "CC(remember):BotAndHumansFacePile.kt#9igjgp");
            Composer var42 = var35;
            boolean var43 = var35.changed(var9) | var35.changed(var52);
            int var44 = 0;
            Object var45 = var42.rememberedValue();
            int var46 = 0;
            kotlin.jvm.functions.Function1 var74;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var74 = (Function1)var45;
            } else {
               Modifier var47 = var10000;
               int var48 = 0;
               final float recoveredCapture_149_0 = var9;
               final float recoveredCapture_149_1 = var52;
               var74 = ((kotlin.jvm.functions.Function1) (recoveredArgument_149_0) -> BotAndHumansFacePileKt.BotAndHumansFacePile_hGBTI10$lambda$10$lambda$4$lambda$3$lambda$2(recoveredCapture_149_0, recoveredCapture_149_1, (androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredArgument_149_0));
               var10000 = var47;
               Object var49 = var74;
               var42.updateRememberedValue(var49);
               var74 = (Function1)var49;
            }

            Function1 var50 = var74;
            var35.endReplaceGroup();
            AvatarIconKt.recovered$f5ec3c81e3146789(DrawModifierKt.drawWithContent(var10000, var50), var41, null, false, 0L, null, var35, 0, 60);
         }

         var35.endReplaceGroup();
         AvatarIconKt.recovered$f5ec3c81e3146789(
            SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var3), new AvatarWrapper(var1, true), null, false, 0L, null, var35, 0, 60
         );
         var38 = (Avatar)var2.getSecond();
         var35.startReplaceGroup(1213869401);
         ComposerKt.sourceInformation(var35, "*66@2518L494,62@2336L691");
         if (var38 != null) {
            Avatar var60 = var38;
            int var61 = 0;
            AvatarWrapper var62 = new AvatarWrapper(var60, false, 2, null);
            Modifier var72 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var9);
            var35.startReplaceGroup(1012574327);
            ComposerKt.sourceInformation(var35, "CC(remember):BotAndHumansFacePile.kt#9igjgp");
            Composer var63 = var35;
            boolean var64 = var35.changed(var52) | var35.changed(var9);
            int var65 = 0;
            Object var66 = var63.rememberedValue();
            int var67 = 0;
            kotlin.jvm.functions.Function1 var76;
            if (!var64 && var66 != Composer.Companion.getEmpty()) {
               var76 = (Function1)var66;
            } else {
               Modifier var68 = var72;
               int var69 = 0;
               final float recoveredCapture_150_0 = var52;
               final float recoveredCapture_150_1 = var9;
               var76 = ((kotlin.jvm.functions.Function1) (recoveredArgument_150_0) -> BotAndHumansFacePileKt.BotAndHumansFacePile_hGBTI10$lambda$10$lambda$9$lambda$8$lambda$7(recoveredCapture_150_0, recoveredCapture_150_1, (androidx.compose.ui.graphics.drawscope.ContentDrawScope)recoveredArgument_150_0));
               var72 = var68;
               Object var70 = var76;
               var63.updateRememberedValue(var70);
               var76 = (Function1)var70;
            }

            Function1 var71 = var76;
            var35.endReplaceGroup();
            AvatarIconKt.recovered$f5ec3c81e3146789(DrawModifierKt.drawWithContent(var72, var71), var62, null, false, 0L, null, var35, 0, 60);
         }

         var35.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var73 = var5.endRestartGroup();
      if (var73 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_151_0 = var0;
         final io.intercom.android.sdk.models.Avatar recoveredCapture_151_1 = var1;
         final kotlin.Pair recoveredCapture_151_2 = var2;
         final float recoveredCapture_151_3 = var3;
         final java.lang.String recoveredCapture_151_4 = var4;
         final int recoveredCapture_151_5 = var6;
         final int recoveredCapture_151_6 = var7;
         var73.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_151_0, recoveredArgument_151_1) -> BotAndHumansFacePileKt.BotAndHumansFacePile_hGBTI10$lambda$11(recoveredCapture_151_0, recoveredCapture_151_1, recoveredCapture_151_2, recoveredCapture_151_3, recoveredCapture_151_4, recoveredCapture_151_5, recoveredCapture_151_6, (androidx.compose.runtime.Composer)recoveredArgument_151_0, ((java.lang.Integer)recoveredArgument_151_1).intValue())));
      }
   }

   @NotNull
   public static final Pair humanAvatarPairForHome(@NotNull List var0) {
      Intrinsics.checkNotNullParameter(var0, "humanAvatars");
      Pair var10000;
      switch (var0.size()) {
         case 0:
            var10000 = TuplesKt.to(null, null);
            break;
         case 1:
            var10000 = new Pair(null, var0.get(0));
            break;
         default:
            var10000 = new Pair(var0.get(0), var0.get(1));
      }

      return var10000;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BotWithTwoTeammatesPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-366024049);
      ComposerKt.sourceInformation(var0, "C(BotWithTwoTeammatesPreview)94@3435L310:BotAndHumansFacePile.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-366024049, var1, -1, "io.intercom.android.sdk.m5.components.BotWithTwoTeammatesPreview (BotAndHumansFacePile.kt:93)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BotAndHumansFacePileKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_147_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_147_0, recoveredArgument_147_1) -> BotAndHumansFacePileKt.BotWithTwoTeammatesPreview$lambda$12(recoveredCapture_147_0, (androidx.compose.runtime.Composer)recoveredArgument_147_0, ((java.lang.Integer)recoveredArgument_147_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void BotsWithOneTeammatePreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1130939763);
      ComposerKt.sourceInformation(var0, "C(BotsWithOneTeammatePreview)110@3826L291:BotAndHumansFacePile.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1130939763, var1, -1, "io.intercom.android.sdk.m5.components.BotsWithOneTeammatePreview (BotAndHumansFacePile.kt:109)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$BotAndHumansFacePileKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_148_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_148_0, recoveredArgument_148_1) -> BotAndHumansFacePileKt.BotsWithOneTeammatePreview$lambda$13(recoveredCapture_148_0, (androidx.compose.runtime.Composer)recoveredArgument_148_0, ((java.lang.Integer)recoveredArgument_148_1).intValue())));
      }
   }

   private static final Unit BotAndHumansFacePile_hGBTI10$lambda$10$lambda$4$lambda$3$lambda$2(float var0, float var1, ContentDrawScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$drawWithContent");
      if (var2.getLayoutDirection() == LayoutDirection.Ltr) {
         DrawScope var3 = var2;
         int var6 = 0;
         float var32 = var2.recovered$245d7c913f591981(Dp.recovered$95b3d103663bf060(var0 - var1));
         float var4 = 0.0F;
         float var5 = 0.0F;
         long var9 = var3.recovered$d1ee80c43f6fa69a();
         int var11 = 0;
         long var12 = var9;
         int var14 = 0;
         int var15 = (int)(var12 & 4294967295L);
         int var16 = 0;
         float var7 = Float.intBitsToFloat(var15);
         int var8 = ClipOp.Companion.recovered$5a7e9e4dfa773992();
         int var17 = 0;
         DrawScope var18 = var3;
         int var19 = 0;
         DrawContext var20 = var18.getDrawContext();
         var11 = 0;
         var12 = var20.recovered$d1ee80c43f6fa69a();
         var20.getCanvas().save();

         try {
            DrawTransform var46 = var20.getTransform();
            var15 = 0;
            var46.recovered$46f214774f7004ac(var4, var5, var32, var7, var8);
            int var22 = 0;
            var2.drawContent();
         } finally {
            var20.getCanvas().restore();
            var20.recovered$24a8274fd1fa44b3(var12);
         }
      } else {
         DrawScope var29 = var2;
         float var30 = var2.recovered$245d7c913f591981(var1);
         float var31 = 0.0F;
         long var36 = var29.recovered$d1ee80c43f6fa69a();
         int var39 = 0;
         long var43 = var36;
         int var47 = 0;
         int var51 = (int)(var43 >> 32);
         int var54 = 0;
         float var33 = Float.intBitsToFloat(var51);
         var36 = var29.recovered$d1ee80c43f6fa69a();
         var39 = 0;
         var43 = var36;
         var47 = 0;
         var51 = (int)(var43 & 4294967295L);
         var54 = 0;
         float var34 = Float.intBitsToFloat(var51);
         int var35 = ClipOp.Companion.recovered$5a7e9e4dfa773992();
         int var56 = 0;
         DrawScope var57 = var29;
         int var58 = 0;
         DrawContext var59 = var57.getDrawContext();
         var39 = 0;
         var43 = var59.recovered$d1ee80c43f6fa69a();
         var59.getCanvas().save();

         try {
            DrawTransform var49 = var59.getTransform();
            var51 = 0;
            var49.recovered$46f214774f7004ac(var30, var31, var33, var34, var35);
            int var60 = 0;
            var2.drawContent();
         } finally {
            var59.getCanvas().restore();
            var59.recovered$24a8274fd1fa44b3(var43);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit BotAndHumansFacePile_hGBTI10$lambda$10$lambda$9$lambda$8$lambda$7(float var0, float var1, ContentDrawScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$drawWithContent");
      if (var2.getLayoutDirection() == LayoutDirection.Ltr) {
         DrawScope var3 = var2;
         float var4 = var2.recovered$245d7c913f591981(var0);
         float var5 = 0.0F;
         long var9 = var3.recovered$d1ee80c43f6fa69a();
         int var11 = 0;
         long var12 = var9;
         int var14 = 0;
         int var15 = (int)(var12 >> 32);
         int var16 = 0;
         float var6 = Float.intBitsToFloat(var15);
         var9 = var3.recovered$d1ee80c43f6fa69a();
         var11 = 0;
         var12 = var9;
         var14 = 0;
         var15 = (int)(var12 & 4294967295L);
         var16 = 0;
         float var7 = Float.intBitsToFloat(var15);
         int var8 = ClipOp.Companion.recovered$5a7e9e4dfa773992();
         int var17 = 0;
         DrawScope var18 = var3;
         int var19 = 0;
         DrawContext var20 = var18.getDrawContext();
         var11 = 0;
         var12 = var20.recovered$d1ee80c43f6fa69a();
         var20.getCanvas().save();

         try {
            DrawTransform var47 = var20.getTransform();
            var15 = 0;
            var47.recovered$46f214774f7004ac(var4, var5, var6, var7, var8);
            int var22 = 0;
            var2.drawContent();
         } finally {
            var20.getCanvas().restore();
            var20.recovered$24a8274fd1fa44b3(var12);
         }
      } else {
         DrawScope var29 = var2;
         int var32 = 0;
         float var33 = var2.recovered$245d7c913f591981(Dp.recovered$95b3d103663bf060(var1 - var0));
         float var30 = 0.0F;
         float var31 = 0.0F;
         long var37 = var29.recovered$d1ee80c43f6fa69a();
         int var40 = 0;
         long var44 = var37;
         int var48 = 0;
         int var52 = (int)(var44 & 4294967295L);
         int var55 = 0;
         float var34 = Float.intBitsToFloat(var52);
         int var35 = ClipOp.Companion.recovered$5a7e9e4dfa773992();
         int var56 = 0;
         DrawScope var57 = var29;
         int var58 = 0;
         DrawContext var59 = var57.getDrawContext();
         var40 = 0;
         var44 = var59.recovered$d1ee80c43f6fa69a();
         var59.getCanvas().save();

         try {
            DrawTransform var49 = var59.getTransform();
            var52 = 0;
            var49.recovered$46f214774f7004ac(var30, var31, var33, var34, var35);
            int var60 = 0;
            var2.drawContent();
         } finally {
            var59.getCanvas().restore();
            var59.recovered$24a8274fd1fa44b3(var44);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit BotAndHumansFacePile_hGBTI10$lambda$11(
      Modifier var0, Avatar var1, Pair var2, float var3, String var4, int var5, int var6, Composer var7, int var8
   ) {
      recovered$2d180ffbba3167ff(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit BotWithTwoTeammatesPreview$lambda$12(int var0, Composer var1, int var2) {
      BotWithTwoTeammatesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit BotsWithOneTeammatePreview$lambda$13(int var0, Composer var1, int var2) {
      BotsWithOneTeammatePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
