package io.intercom.android.sdk.m5.components;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.core.MutableTransitionState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.ui.common.TransitionsKt;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
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
   d1 = "\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\u001a;\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u000fH\u0001¢\u0006\u0002\u0010\u0010\u001a5\u0010\u0011\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0001¢\u0006\u0002\u0010\u0013\u001a1\u0010\u0014\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u000fH\u0003¢\u0006\u0002\u0010\u0015\u001a#\u0010\u0016\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0003¢\u0006\u0002\u0010\u0017\u001a\r\u0010\u0018\u001a\u00020\u0006H\u0001¢\u0006\u0002\u0010\u0019\u001a\r\u0010\u001a\u001a\u00020\u0006H\u0001¢\u0006\u0002\u0010\u0019\u001a\r\u0010\u001b\u001a\u00020\u0006H\u0001¢\u0006\u0002\u0010\u0019\u001a\r\u0010\u001c\u001a\u00020\u0006H\u0001¢\u0006\u0002\u0010\u0019\u001a\r\u0010\u001d\u001a\u00020\u0006H\u0001¢\u0006\u0002\u0010\u0019\"\u0016\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u001e",
   d2 = {
         "HandoverPillBottomPadding",
         "Landroidx/compose/ui/unit/Dp;",
         "getHandoverPillBottomPadding",
         "()F",
         "F",
         "FooterNoticePill",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "title",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "onClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/util/List;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "ExpandedFooterNotice",
         "subtitle",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Landroidx/compose/runtime/Composer;II)V",
         "FooterNoticePillWithoutAnimation",
         "(Ljava/lang/String;Ljava/util/List;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "FooterTitle",
         "(Ljava/lang/String;Ljava/util/List;Landroidx/compose/runtime/Composer;I)V",
         "ExpandedFooterNoticePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ExpandedFooterNoticePreviewMultipleAvatars",
         "ExpandedTitleOnlyFooterNoticePreview",
         "FooterNoticePillPreview",
         "FooterNoticePillMultipleAvatarsPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFooterNotice.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/FooterNoticeKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 10 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,217:1\n70#2:218\n67#2,9:219\n77#2:261\n81#3,6:228\n88#3,6:243\n96#3:260\n81#3,6:272\n88#3,6:287\n96#3:297\n81#3,6:310\n88#3,6:325\n96#3:336\n402#4,9:234\n411#4:249\n412#4,2:258\n402#4,9:278\n411#4:293\n412#4,2:295\n402#4,9:316\n411#4:331\n412#4,2:334\n1047#5,6:250\n75#6:256\n1#7:257\n87#8:262\n84#8,9:263\n94#8:298\n118#9:294\n118#9:332\n118#9:333\n118#9:338\n99#10:299\n95#10,10:300\n106#10:337\n*S KotlinDebug\n*F\n+ 1 FooterNotice.kt\nio/intercom/android/sdk/m5/components/FooterNoticeKt\n*L\n40#1:218\n40#1:219,9\n40#1:261\n40#1:228,6\n40#1:243,6\n40#1:260\n73#1:272,6\n73#1:287,6\n73#1:297\n121#1:310,6\n121#1:325,6\n121#1:336\n40#1:234,9\n40#1:249\n40#1:258,2\n73#1:278,9\n73#1:293\n73#1:295,2\n121#1:316,9\n121#1:331\n121#1:334,2\n44#1:250,6\n51#1:256\n73#1:262\n73#1:263,9\n73#1:298\n80#1:294\n128#1:332\n132#1:333\n31#1:338\n121#1:299\n121#1:300,10\n121#1:337\n*E\n"
)
public final class FooterNoticeKt {
   private static final float HandoverPillBottomPadding;

   public static final float getHandoverPillBottomPadding() {
      return HandoverPillBottomPadding;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FooterNoticePill(
      @Nullable Modifier var0, @NotNull String var1, @NotNull List var2, @NotNull Function0 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "avatars");
      Intrinsics.checkNotNullParameter(var3, "onClick");
      var4 = var4.startRestartGroup(2116373339);
      ComposerKt.sourceInformation(var4, "C(FooterNoticePill)P(1,3)39@1577L708:FooterNotice.kt#arqjag");
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
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2116373339, var7, -1, "io.intercom.android.sdk.m5.components.FooterNoticePill (FooterNotice.kt:38)");
         }

         int var10 = 14 & var7;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var8 = Alignment.Companion.getTopStart();
         boolean var9 = false;
         MeasurePolicy var12 = BoxKt.maybeCachedBoxMeasurePolicy(var8, var9);
         Modifier var13 = var0;
         MeasurePolicy var14 = var12;
         Composer var15 = var4;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         BoxScope var32 = BoxScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 458276070, "C43@1656L191,*50@1895L7,55@2107L172,52@1958L321:FooterNotice.kt#arqjag");
         var31.startReplaceGroup(-2063426416);
         ComposerKt.sourceInformation(var31, "CC(remember):FooterNotice.kt#9igjgp");
         Composer var34 = var31;
         int var35 = 0;
         int var36 = 0;
         Object var37 = var34.rememberedValue();
         int var38 = 0;
         Object var10000;
         if (var37 == Composer.Companion.getEmpty()) {
            int var39 = 0;
            MutableTransitionState var40 = new MutableTransitionState(false);
            MutableTransitionState var41 = var40;
            int var42 = 0;
            var41.setTargetState(true);
            Object var52 = var40;
            var34.updateRememberedValue(var52);
            var10000 = var52;
         } else {
            var10000 = var37;
         }

         MutableTransitionState var43 = (MutableTransitionState)var10000;
         var31.endReplaceGroup();
         MutableTransitionState var44 = var43;
         CompositionLocal var46 = CompositionLocalsKt.getLocalDensity();
         var35 = 0;
         var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         var37 = var31.consume(var46);
         ComposerKt.sourceInformationMarkerEnd(var31);
         Density var48 = (Density)var37;
         var36 = 0;
         int var53 = (int)var48.recovered$245d7c913f591981(HandoverPillBottomPadding);
         AnimatedVisibilityKt.AnimatedVisibility(
            var44,
            null,
            TransitionsKt.floatingButtonEnterTransition(var53),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1063955783, true, new FooterNoticeKt$FooterNoticePill$1$1(var1, var2, var3), var31, 54),
            var31,
            196608 | MutableTransitionState.$stable,
            26
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var54 = var4.endRestartGroup();
      if (var54 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_203_0 = var0;
         final java.lang.String recoveredCapture_203_1 = var1;
         final java.util.List recoveredCapture_203_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_203_3 = var3;
         final int recoveredCapture_203_4 = var5;
         final int recoveredCapture_203_5 = var6;
         var54.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_203_0, recoveredArgument_203_1) -> FooterNoticeKt.FooterNoticePill$lambda$4(recoveredCapture_203_0, recoveredCapture_203_1, recoveredCapture_203_2, recoveredCapture_203_3, recoveredCapture_203_4, recoveredCapture_203_5, (androidx.compose.runtime.Composer)recoveredArgument_203_0, ((java.lang.Integer)recoveredArgument_203_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedFooterNotice(
      @Nullable Modifier var0, @NotNull String var1, @NotNull String var2, @NotNull List var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "subtitle");
      Intrinsics.checkNotNullParameter(var3, "avatars");
      var4 = var4.startRestartGroup(-1076553086);
      ComposerKt.sourceInformation(var4, "C(ExpandedFooterNotice)P(1,3,2)72@2454L497:FooterNotice.kt#arqjag");
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
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1076553086, var7, -1, "io.intercom.android.sdk.m5.components.ExpandedFooterNotice (FooterNotice.kt:71)");
         }

         Horizontal var9 = Alignment.Companion.getCenterHorizontally();
         int var10 = 384 | 14 & var7;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var8 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var8, var9, var4, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var0;
         MeasurePolicy var14 = var12;
         Composer var15 = var4;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -1077106248, "C76@2567L45:FooterNotice.kt#arqjag");
         FooterTitle(var1, var3, var31, 14 & var7 >> 3 | 112 & var7 >> 6);
         var31.startReplaceGroup(2043466324);
         ComposerKt.sourceInformation(var31, "79@2663L40,83@2794L10,84@2857L6,81@2717L218");
         if (var2.length() > 0) {
            Modifier var10000 = Modifier.Companion;
            int var34 = 8;
            int var35 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var34)), var31, 6);
            TextStyle var40 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04Point5();
            long var36 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
            int var38 = TextAlign.Companion.recovered$d707b9dd64876179();
            TextKt.recovered$5b114ab32a4f4190(
               var2,
               null,
               var36,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var38),
               0L,
               0,
               false,
               0,
               0,
               null,
               var40,
               var31,
               14 & var7 >> 6,
               0,
               130042
            );
         }

         var31.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var41 = var4.endRestartGroup();
      if (var41 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_199_0 = var0;
         final java.lang.String recoveredCapture_199_1 = var1;
         final java.lang.String recoveredCapture_199_2 = var2;
         final java.util.List recoveredCapture_199_3 = var3;
         final int recoveredCapture_199_4 = var5;
         final int recoveredCapture_199_5 = var6;
         var41.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_199_0, recoveredArgument_199_1) -> FooterNoticeKt.ExpandedFooterNotice$lambda$6(recoveredCapture_199_0, recoveredCapture_199_1, recoveredCapture_199_2, recoveredCapture_199_3, recoveredCapture_199_4, recoveredCapture_199_5, (androidx.compose.runtime.Composer)recoveredArgument_199_0, ((java.lang.Integer)recoveredArgument_199_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FooterNoticePillWithoutAnimation(String var0, List var1, Function0 var2, Composer var3, int var4) {
      var3 = var3.startRestartGroup(-2078164816);
      ComposerKt.sourceInformation(var3, "C(FooterNoticePillWithoutAnimation)P(2)100@3259L6,99@3211L76,103@3322L226,97@3099L449:FooterNotice.kt#arqjag");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2078164816, var5, -1, "io.intercom.android.sdk.m5.components.FooterNoticePillWithoutAnimation (FooterNotice.kt:96)");
         }

         Modifier var6 = PaddingKt.recovered$eb6a744cd98cdbca(Modifier.Companion, HandoverPillBottomPadding);
         Style var7 = IntercomCardStyle.INSTANCE
            .recovered$24b4efdd2276d096(
               IntercomTheme.INSTANCE.getShapes(var3, IntercomTheme.$stable).getExtraLarge(), 0L, 0L, 0.0F, null, 0L, var3, IntercomCardStyle.$stable << 18, 62
            );
         IntercomCardKt.IntercomCard(
            var2,
            var6,
            false,
            var7,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1065463783, true, new FooterNoticeKt$FooterNoticePillWithoutAnimation$1(var0, var1), var3, 54),
            var3,
            196656 | 14 & var5 >> 6 | Style.$stable << 9,
            20
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final java.lang.String recoveredCapture_206_0 = var0;
         final java.util.List recoveredCapture_206_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_206_2 = var2;
         final int recoveredCapture_206_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_206_0, recoveredArgument_206_1) -> FooterNoticeKt.FooterNoticePillWithoutAnimation$lambda$7(recoveredCapture_206_0, recoveredCapture_206_1, recoveredCapture_206_2, recoveredCapture_206_3, (androidx.compose.runtime.Composer)recoveredArgument_206_0, ((java.lang.Integer)recoveredArgument_206_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FooterTitle(String var0, List var1, Composer var2, int var3) {
      var2 = var2.startRestartGroup(-973759395);
      ComposerKt.sourceInformation(var2, "C(FooterTitle)P(1)120@3650L585:FooterNotice.kt#arqjag");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-973759395, var4, -1, "io.intercom.android.sdk.m5.components.FooterTitle (FooterNotice.kt:119)");
         }

         androidx.compose.foundation.layout.Arrangement.Horizontal var6 = Arrangement.INSTANCE.getCenter();
         androidx.compose.ui.Alignment.Vertical var7 = Alignment.Companion.getCenterVertically();
         short var8 = 432;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var5 = Modifier.Companion;
         MeasurePolicy var10 = RowKt.rowMeasurePolicy(var6, var7, var2, 14 & var8 >> 3 | 112 & var8 >> 3);
         Modifier var11 = var5;
         MeasurePolicy var12 = var10;
         Composer var13 = var2;
         int var14 = 112 & var8 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         RowScope var30 = RowScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 889091979, "C136@4100L10,137@4159L6,134@4034L195:FooterNotice.kt#arqjag");
         var29.startReplaceGroup(1829795499);
         ComposerKt.sourceInformation(var29, "125@3819L142,131@3975L39");
         if (!var1.isEmpty()) {
            int var32 = 16;
            int var33 = 0;
            AvatarGroupKt.recovered$813c81fac421be56(var1, null, Dp.recovered$95b3d103663bf060(var32), TextUnitKt.getSp(10), var29, 3456 | 14 & var4 >> 3, 2);
            Modifier var10000 = Modifier.Companion;
            var32 = 8;
            var33 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var32)), var29, 6);
         }

         var29.endReplaceGroup();
         TextStyle var39 = IntercomTheme.INSTANCE.getTypography(var29, IntercomTheme.$stable).getType04Point5();
         long var34 = IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         int var36 = TextAlign.Companion.recovered$d707b9dd64876179();
         TextKt.recovered$5b114ab32a4f4190(
            var0,
            null,
            var34,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var36),
            0L,
            0,
            false,
            0,
            0,
            null,
            var39,
            var29,
            14 & var4,
            0,
            130042
         );
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var41 = var2.endRestartGroup();
      if (var41 != null) {
         final java.lang.String recoveredCapture_207_0 = var0;
         final java.util.List recoveredCapture_207_1 = var1;
         final int recoveredCapture_207_2 = var3;
         var41.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_207_0, recoveredArgument_207_1) -> FooterNoticeKt.FooterTitle$lambda$9(recoveredCapture_207_0, recoveredCapture_207_1, recoveredCapture_207_2, (androidx.compose.runtime.Composer)recoveredArgument_207_0, ((java.lang.Integer)recoveredArgument_207_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedFooterNoticePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1644521079);
      ComposerKt.sourceInformation(var0, "C(ExpandedFooterNoticePreview)146@4309L312:FooterNotice.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1644521079, var1, -1, "io.intercom.android.sdk.m5.components.ExpandedFooterNoticePreview (FooterNotice.kt:145)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FooterNoticeKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_200_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_200_0, recoveredArgument_200_1) -> FooterNoticeKt.ExpandedFooterNoticePreview$lambda$10(recoveredCapture_200_0, (androidx.compose.runtime.Composer)recoveredArgument_200_0, ((java.lang.Integer)recoveredArgument_200_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedFooterNoticePreviewMultipleAvatars(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(419901737);
      ComposerKt.sourceInformation(var0, "C(ExpandedFooterNoticePreviewMultipleAvatars)160@4710L442:FooterNotice.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               419901737, var1, -1, "io.intercom.android.sdk.m5.components.ExpandedFooterNoticePreviewMultipleAvatars (FooterNotice.kt:159)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FooterNoticeKt.INSTANCE.recovered$1400af5e34386102(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_201_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_201_0, recoveredArgument_201_1) -> FooterNoticeKt.ExpandedFooterNoticePreviewMultipleAvatars$lambda$11(recoveredCapture_201_0, (androidx.compose.runtime.Composer)recoveredArgument_201_0, ((java.lang.Integer)recoveredArgument_201_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedTitleOnlyFooterNoticePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-385296499);
      ComposerKt.sourceInformation(var0, "C(ExpandedTitleOnlyFooterNoticePreview)176@5235L251:FooterNotice.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-385296499, var1, -1, "io.intercom.android.sdk.m5.components.ExpandedTitleOnlyFooterNoticePreview (FooterNotice.kt:175)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FooterNoticeKt.INSTANCE.recovered$fd838d44303763da(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_202_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_202_0, recoveredArgument_202_1) -> FooterNoticeKt.ExpandedTitleOnlyFooterNoticePreview$lambda$12(recoveredCapture_202_0, (androidx.compose.runtime.Composer)recoveredArgument_202_0, ((java.lang.Integer)recoveredArgument_202_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FooterNoticePillPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(615648759);
      ComposerKt.sourceInformation(var0, "C(FooterNoticePillPreview)190@5556L262:FooterNotice.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(615648759, var1, -1, "io.intercom.android.sdk.m5.components.FooterNoticePillPreview (FooterNotice.kt:189)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FooterNoticeKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_205_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_205_0, recoveredArgument_205_1) -> FooterNoticeKt.FooterNoticePillPreview$lambda$13(recoveredCapture_205_0, (androidx.compose.runtime.Composer)recoveredArgument_205_0, ((java.lang.Integer)recoveredArgument_205_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FooterNoticePillMultipleAvatarsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(961872365);
      ComposerKt.sourceInformation(var0, "C(FooterNoticePillMultipleAvatarsPreview)204@5903L392:FooterNotice.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               961872365, var1, -1, "io.intercom.android.sdk.m5.components.FooterNoticePillMultipleAvatarsPreview (FooterNotice.kt:203)"
            );
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FooterNoticeKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_204_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_204_0, recoveredArgument_204_1) -> FooterNoticeKt.FooterNoticePillMultipleAvatarsPreview$lambda$14(recoveredCapture_204_0, (androidx.compose.runtime.Composer)recoveredArgument_204_0, ((java.lang.Integer)recoveredArgument_204_1).intValue())));
      }
   }

   private static final Unit FooterNoticePill$lambda$4(Modifier var0, String var1, List var2, Function0 var3, int var4, int var5, Composer var6, int var7) {
      FooterNoticePill(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedFooterNotice$lambda$6(Modifier var0, String var1, String var2, List var3, int var4, int var5, Composer var6, int var7) {
      ExpandedFooterNotice(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit FooterNoticePillWithoutAnimation$lambda$7(String var0, List var1, Function0 var2, int var3, Composer var4, int var5) {
      FooterNoticePillWithoutAnimation(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FooterTitle$lambda$9(String var0, List var1, int var2, Composer var3, int var4) {
      FooterTitle(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedFooterNoticePreview$lambda$10(int var0, Composer var1, int var2) {
      ExpandedFooterNoticePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedFooterNoticePreviewMultipleAvatars$lambda$11(int var0, Composer var1, int var2) {
      ExpandedFooterNoticePreviewMultipleAvatars(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTitleOnlyFooterNoticePreview$lambda$12(int var0, Composer var1, int var2) {
      ExpandedTitleOnlyFooterNoticePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FooterNoticePillPreview$lambda$13(int var0, Composer var1, int var2) {
      FooterNoticePillPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FooterNoticePillMultipleAvatarsPreview$lambda$14(int var0, Composer var1, int var2) {
      FooterNoticePillMultipleAvatarsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$FooterNoticePillWithoutAnimation(String var0, List var1, Function0 var2, Composer var3, int var4) {
      FooterNoticePillWithoutAnimation(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static final void access$FooterTitle(String var0, List var1, Composer var2, int var3) {
      FooterTitle(var0, var1, var2, var3);
   }

   static {
      int var0 = 10;
      int var1 = 0;
      HandoverPillBottomPadding = Dp.recovered$95b3d103663bf060(var0);
   }
}
