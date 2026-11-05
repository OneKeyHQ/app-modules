package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.draw.CacheDrawScope;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.draw.DrawModifierKt;
import androidx.compose.ui.draw.DrawResult;
import androidx.compose.ui.graphics.AndroidPath_androidKt;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.Path;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.components.IntercomPrimaryButtonKt;
import io.intercom.android.sdk.models.OpenMessengerResponse$IconType;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$Cta;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomButton;
import io.intercom.android.sdk.ui.component.IntercomButtonKt;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.component.IntercomButton.Style;
import io.intercom.android.sdk.ui.extension.ModifierExtensionsKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.extensions.ContextExtensionsKt;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u001a)\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\u0015\u0010\n\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\"\u000e\u0010\t\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "TeamPresenceComponent",
         "",
         "teamPresenceState",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;",
         "needsDivider",
         "",
         "buttonStyle",
         "Lio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceButtonStyle;",
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;ZLio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceButtonStyle;Landroidx/compose/runtime/Composer;II)V",
         "mockTeamPresenceState",
         "TeamPresenceComponentWithBubble",
         "(Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$TeamPresenceState;Landroidx/compose/runtime/Composer;I)V",
         "TeamPresenceWithBubblePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TeamPresencePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTeamPresenceComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamPresenceComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceComponentKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 10 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 11 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 12 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 13 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n*L\n1#1,245:1\n75#2:246\n75#2:247\n75#2:301\n118#3:248\n118#3:281\n118#3:282\n118#3:296\n118#3:302\n118#3:337\n118#3:345\n118#3:346\n118#3:347\n87#4:249\n84#4,9:250\n94#4:300\n87#4:303\n83#4,10:304\n94#4:393\n81#5,6:259\n88#5,6:274\n96#5:299\n81#5,6:314\n88#5,6:329\n81#5,6:364\n88#5,6:379\n96#5:388\n96#5:392\n402#6,9:265\n411#6:280\n412#6,2:297\n402#6,9:320\n411#6:335\n402#6,9:370\n411#6,3:385\n412#6,2:390\n1#7:283\n1047#8,6:284\n1047#8,6:290\n1047#8,6:339\n1047#8,6:348\n58#9:336\n52#9:338\n70#10:354\n67#10,9:355\n77#10:389\n61#11:394\n57#11:397\n61#11:400\n57#11:402\n61#11:405\n70#12:395\n60#12:398\n70#12:401\n60#12:403\n70#12:406\n23#13:396\n23#13:399\n23#13:404\n*S KotlinDebug\n*F\n+ 1 TeamPresenceComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/TeamPresenceComponentKt\n*L\n61#1:246\n62#1:247\n184#1:301\n94#1:248\n100#1:281\n101#1:282\n133#1:296\n185#1:302\n191#1:337\n202#1:345\n207#1:346\n208#1:347\n91#1:249\n91#1:250,9\n91#1:300\n187#1:303\n187#1:304,10\n187#1:393\n91#1:259,6\n91#1:274,6\n91#1:299\n187#1:314,6\n187#1:329,6\n205#1:364,6\n205#1:379,6\n205#1:388\n187#1:392\n91#1:265,9\n91#1:280\n91#1:297,2\n187#1:320,9\n187#1:335\n205#1:370,9\n205#1:385,3\n187#1:390,2\n123#1:284,6\n130#1:290,6\n192#1:339,6\n209#1:348,6\n191#1:336\n191#1:338\n205#1:354\n205#1:355,9\n205#1:389\n194#1:394\n195#1:397\n195#1:400\n196#1:402\n196#1:405\n194#1:395\n195#1:398\n195#1:401\n196#1:403\n196#1:406\n194#1:396\n195#1:399\n196#1:404\n*E\n"
)
public final class TeamPresenceComponentKt {
   @NotNull
   private static final ArticleViewState$TeamPresenceState mockTeamPresenceState;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamPresenceComponent(
      @NotNull ArticleViewState$TeamPresenceState var0, boolean var1, @Nullable TeamPresenceButtonStyle var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "teamPresenceState");
      var3 = var3.startRestartGroup(-696298916);
      ComposerKt.sourceInformation(var3, "C(TeamPresenceComponent)P(2,1)60@2610L7,61@2643L7,90@3896L1883:TeamPresenceComponent.kt#ngglre");
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
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 2) != 0) {
            var1 = true;
         }

         if ((var5 & 4) != 0) {
            var2 = TeamPresenceButtonStyle.SECONDARY;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -696298916, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponent (TeamPresenceComponent.kt:59)"
            );
         }

         CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var3.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var7 = (Context)var11;
         CompositionLocal var54 = AndroidCompositionLocals_androidKt.getLocalView();
         var10 = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var12 = var3.consume(var54);
         ComposerKt.sourceInformationMarkerEnd(var3);
         View var53 = (View)var12;
         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         var9 = 24;
         var10 = 0;
         Modifier var56 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var9), 1, null);
         Horizontal var61 = Alignment.Companion.getCenterHorizontally();
         short var62 = 390;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var59 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var59, var61, var3, 14 & var62 >> 3 | 112 & var62 >> 3);
         Modifier var15 = var56;
         MeasurePolicy var16 = var14;
         Composer var17 = var3;
         int var18 = 112 & var62 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
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
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var62 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 1001177922, "C104@4286L56,132@5522L41:TeamPresenceComponent.kt#ngglre");
         var33.startReplaceGroup(-937536771);
         ComposerKt.sourceInformation(var33, "97@4108L147");
         if (var1) {
            var10000 = Modifier.Companion;
            int var36 = 100;
            int var37 = 0;
            var10000 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060(var36));
            var36 = 16;
            var37 = 0;
            IntercomDividerKt.IntercomDivider(
               PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var36), 7, null), var33, 6, 0
            );
         }

         var33.endReplaceGroup();
         String var64 = StringResources_androidKt.stringResource(var0.getMessageButtonText(), var33, 0);
         Integer var66 = var0.getMessageButtonIcon();
         Integer var38 = var0.getSubtitleText();
         var33.startReplaceGroup(-937524289);
         ComposerKt.sourceInformation(var33, "*106@4477L23");
         String var87;
         if (var38 == null) {
            var87 = null;
         } else {
            int var39 = var38.intValue();
            int var40 = 0;
            var87 = StringResources_androidKt.stringResource(var39, var33, 0);
         }

         String var41 = var87;
         var33.endReplaceGroup();
         String var42 = var41;
         if (var0.getCtaData() != null) {
            var64 = var0.getCtaData().getText();
            Integer var88 = var0.getCtaData().getIcon().getIcon();
            if (var88 == null) {
               var88 = var66;
            }

            var66 = var88;
            var42 = var0.getCtaData().getSubtitle();
         }

         var33.startReplaceGroup(-937511910);
         ComposerKt.sourceInformation(var33, "115@4924L74");
         if (var0.getConversationState() != null) {
            var64 = StringResources_androidKt.stringResource(var0.getConversationState().getMessageButtonText(), var33, 0);
         }

         var33.endReplaceGroup();
         if (var2 == TeamPresenceButtonStyle.PRIMARY) {
            var33.startReplaceGroup(1002117376);
            ComposerKt.sourceInformation(var33, "122@5209L30,119@5084L170");
            String var90 = var64;
            Object var93 = null;
            Integer var94 = var66;
            var33.startReplaceGroup(-937500674);
            ComposerKt.sourceInformation(var33, "CC(remember):TeamPresenceComponent.kt#9igjgp");
            Composer var75 = var33;
            boolean var77 = var3.changedInstance(var53) | var3.changedInstance(var7) | (var6 & 14) == 4;
            int var78 = 0;
            Object var71 = var75.rememberedValue();
            int var73 = 0;
            Object var95;
            if (!var77 && var71 != Composer.Companion.getEmpty()) {
               var95 = var71;
            } else {
               Integer var79 = var66;
               Object var80 = null;
               String var81 = var64;
               int var82 = 0;
               final io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState recoveredCapture_881_0 = var0;
               final android.view.View recoveredCapture_881_1 = var53;
               final android.content.Context recoveredCapture_881_2 = var7;
               Function0 var83 = ((kotlin.jvm.functions.Function0) () -> TeamPresenceComponentKt.TeamPresenceComponent$lambda$5$lambda$2$lambda$1(recoveredCapture_881_0, recoveredCapture_881_1, recoveredCapture_881_2));
               var90 = var81;
               var93 = var80;
               var94 = var79;
               Object var84 = var83;
               var75.updateRememberedValue(var84);
               var95 = var84;
            }

            Function0 var68 = (Function0)var95;
            var33.endReplaceGroup();
            IntercomPrimaryButtonKt.IntercomPrimaryButton(var90, (Modifier)var93, var94, var68, var33, 0, 2);
            var33.endReplaceGroup();
         } else {
            var33.startReplaceGroup(1002317295);
            ComposerKt.sourceInformation(var33, "126@5339L15,129@5458L30,125@5284L219");
            var10000 = null;
            Style var10001 = IntercomButton.INSTANCE.recovered$6b6eaea65c87a6df(0L, 0L, null, var33, IntercomButton.$stable << 9, 7);
            String var10002 = var64;
            Integer var10003 = var66;
            var33.startReplaceGroup(-937492706);
            ComposerKt.sourceInformation(var33, "CC(remember):TeamPresenceComponent.kt#9igjgp");
            Composer var74 = var33;
            boolean var43 = var3.changedInstance(var53) | var3.changedInstance(var7) | (var6 & 14) == 4;
            int var44 = 0;
            Object var70 = var74.rememberedValue();
            int var72 = 0;
            Object var10004;
            if (!var43 && var70 != Composer.Companion.getEmpty()) {
               var10004 = var70;
            } else {
               Integer var49 = var66;
               String var45 = var64;
               Style var46 = var10001;
               Object var47 = null;
               int var48 = 0;
               final io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState recoveredCapture_882_0 = var0;
               final android.view.View recoveredCapture_882_1 = var53;
               final android.content.Context recoveredCapture_882_2 = var7;
               Function0 var51 = ((kotlin.jvm.functions.Function0) () -> TeamPresenceComponentKt.TeamPresenceComponent$lambda$5$lambda$4$lambda$3(recoveredCapture_882_0, recoveredCapture_882_1, recoveredCapture_882_2));
               var10000 = (Modifier)var47;
               var10001 = var46;
               var10002 = var45;
               var10003 = var49;
               Object var50 = var51;
               var74.updateRememberedValue(var50);
               var10004 = var50;
            }

            Function0 var67 = (Function0)var10004;
            var33.endReplaceGroup();
            IntercomButtonKt.IntercomButton(var10000, var10001, var10002, var10003, var67, var33, Style.$stable << 3, 1);
            var33.endReplaceGroup();
         }

         var10000 = Modifier.Companion;
         int var69 = 16;
         int var76 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var69)), var33, 6);
         var33.startReplaceGroup(-937488887);
         ComposerKt.sourceInformation(var33, "136@5693L10,134@5612L151");
         if (var42 != null) {
            TextKt.recovered$5b114ab32a4f4190(
               var42,
               null,
               0L,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               0L,
               0,
               false,
               0,
               0,
               null,
               TextStyle.recovered$cf01d8b71690b5c5(
                  IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType04Point5(),
                  ColorKt.Color(4285887861L),
                  0L,
                  null,
                  null,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  null,
                  0,
                  0,
                  0L,
                  null,
                  null,
                  null,
                  0,
                  0,
                  null,
                  16777214,
                  null
               ),
               var33,
               0,
               0,
               131070
            );
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var92 = var3.endRestartGroup();
      if (var92 != null) {
         final io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState recoveredCapture_880_0 = var0;
         final boolean recoveredCapture_880_1 = var1;
         final io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceButtonStyle recoveredCapture_880_2 = var2;
         final int recoveredCapture_880_3 = var4;
         final int recoveredCapture_880_4 = var5;
         var92.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_880_0, recoveredArgument_880_1) -> TeamPresenceComponentKt.TeamPresenceComponent$lambda$6(recoveredCapture_880_0, recoveredCapture_880_1, recoveredCapture_880_2, recoveredCapture_880_3, recoveredCapture_880_4, (androidx.compose.runtime.Composer)recoveredArgument_880_0, ((java.lang.Integer)recoveredArgument_880_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamPresenceComponentWithBubble(@NotNull ArticleViewState$TeamPresenceState var0, @Nullable Composer var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "teamPresenceState");
      var1 = var1.startRestartGroup(1539837505);
      ComposerKt.sourceInformation(var1, "C(TeamPresenceComponentWithBubble)183@7343L7,185@7440L6,186@7468L1204:TeamPresenceComponent.kt#ngglre");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1539837505, var3, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentWithBubble (TeamPresenceComponent.kt:182)"
            );
         }

         CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalConfiguration();
         int var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var8 = var1.consume(var5);
         ComposerKt.sourceInformationMarkerEnd(var1);
         Configuration var4 = (Configuration)var8;
         var6 = var4.screenWidthDp;
         var7 = 0;
         float var66 = Dp.recovered$95b3d103663bf060(var6);
         long var68 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).recovered$f6e7a825ec69c94c();
         byte var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var70 = Modifier.Companion;
         Vertical var9 = Arrangement.INSTANCE.getTop();
         Horizontal var10 = Alignment.Companion.getStart();
         MeasurePolicy var13 = ColumnKt.columnMeasurePolicy(var9, var10, var1, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var70;
         MeasurePolicy var15 = var13;
         Composer var16 = var1;
         int var17 = 112 & var11 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         ColumnScope var33 = ColumnScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -1649038188, "C208@8375L63,204@8157L509:TeamPresenceComponent.kt#ngglre");
         var32.startReplaceGroup(-745931959);
         ComposerKt.sourceInformation(var32, "191@7682L408,188@7543L595");
         if (var0.getSubtitleText() != null) {
            Modifier var10000 = Modifier.Companion;
            float var35 = 2.0F;
            int var36 = 0;
            var35 = Dp.recovered$95b3d103663bf060(var66 / var35);
            var36 = 60;
            int var37 = 0;
            float var82 = Dp.recovered$95b3d103663bf060(var36);
            var37 = 0;
            float var10001 = Dp.recovered$95b3d103663bf060(var35 - var82);
            int var72 = 0;
            var36 = 0;
            var10000 = OffsetKt.recovered$ed7db252cbfb0f30(var10000, var10001, Dp.recovered$95b3d103663bf060(var72));
            var32.startReplaceGroup(-745925910);
            ComposerKt.sourceInformation(var32, "CC(remember):TeamPresenceComponent.kt#9igjgp");
            Composer var84 = var32;
            boolean var93 = var32.changed(var68);
            int var38 = 0;
            Object var39 = var84.rememberedValue();
            int var40 = 0;
            Object var114;
            if (!var93 && var39 != Composer.Companion.getEmpty()) {
               var114 = (Function1)var39;
            } else {
               Modifier var41 = var10000;
               int var42 = 0;
               final long recoveredExpressionCapture_884_0 = var68;
               var114 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_884_0) -> io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt.TeamPresenceComponentWithBubble$lambda$13$lambda$9$lambda$8(recoveredExpressionCapture_884_0, (androidx.compose.ui.draw.CacheDrawScope) ((androidx.compose.ui.draw.CacheDrawScope)recoveredExpressionArgument_884_0)));
               var10000 = var41;
               Object var43 = var114;
               var84.updateRememberedValue(var43);
               var114 = (Function1)var43;
            }

            Function1 var73 = (kotlin.jvm.functions.Function1) var114;
            var32.endReplaceGroup();
            var10000 = DrawModifierKt.drawWithCache(var10000, var73);
            int $this$dp$ivx = 16;
            var36 = 0;
            SpacerKt.Spacer(SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var32, 0);
         }

         var32.endReplaceGroup();
         Modifier var109 = Modifier.Companion;
         int var75 = 24;
         int var86 = 0;
         float var115 = Dp.recovered$95b3d103663bf060(var75);
         var75 = 24;
         var86 = 0;
         float var10003 = Dp.recovered$95b3d103663bf060(var75);
         var75 = 24;
         var86 = 0;
         var109 = PaddingKt.recovered$03b481d31d62885a(var109, var115, 0.0F, var10003, Dp.recovered$95b3d103663bf060(var75), 2, null);
         var75 = 8;
         var86 = 0;
         var109 = ClipKt.clip(var109, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var75)));
         boolean var116 = var0.getSubtitleText() != null;
         var32.startReplaceGroup(-745904079);
         ComposerKt.sourceInformation(var32, "CC(remember):TeamPresenceComponent.kt#9igjgp");
         Composer var90 = var32;
         boolean var94 = var32.changed(var68);
         int var96 = 0;
         Object var98 = var90.rememberedValue();
         int var100 = 0;
         Object var10002;
         if (!var94 && var98 != Composer.Companion.getEmpty()) {
            var10002 = var98;
         } else {
            boolean var44 = var116;
            Modifier var102 = var109;
            int var103 = 0;
            final long recoveredCapture_885_0 = var68;
            Function1 var45 = ((kotlin.jvm.functions.Function1) (recoveredArgument_885_0) -> TeamPresenceComponentKt.TeamPresenceComponentWithBubble$lambda$13$lambda$11$lambda$10(recoveredCapture_885_0, (androidx.compose.ui.Modifier)recoveredArgument_885_0));
            var109 = var102;
            var116 = var44;
            Object var105 = var45;
            var90.updateRememberedValue(var105);
            var10002 = var105;
         }

         Function1 var79 = (Function1)var10002;
         var32.endReplaceGroup();
         Modifier var80 = ModifierExtensionsKt.ifTrue(var109, var116, var79);
         byte var97 = 0;
         var98 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var91 = Alignment.Companion.getTopStart();
         var94 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var91, var94);
         Modifier var104 = var80;
         MeasurePolicy var106 = measurePolicy$ivx;
         Composer var46 = var32;
         int var47 = 112 & var97 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var46, 0));
         CompositionLocalMap localMap$iv$ivx = var46.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var46, var104);
         Function0 var52 = ComposeUiNode.Companion.getConstructor();
         Composer var53 = var46;
         int var54 = 6 | 896 & var47 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var53, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var53.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var53.startReusableNode();
         if (var53.getInserting()) {
            var53.createNode(var52);
         } else {
            var53.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var53);
         int var57/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var106, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var58 = 14 & var54 >> 6;
         Composer $composer$ivx = var53;
         int var60 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var61 = 6 | 112 & var97 >> 6;
         Composer var62 = $composer$ivx;
         BoxScope var63 = BoxScopeInstance.INSTANCE;
         int var64 = 0;
         ComposerKt.sourceInformationMarkerStart(var62, -61034670, "C212@8464L192:TeamPresenceComponent.kt#ngglre");
         TeamPresenceComponent(var0, false, TeamPresenceButtonStyle.PRIMARY, var62, 432 | 14 & var3, 0);
         ComposerKt.sourceInformationMarkerEnd(var62);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var53.endNode();
         ComposerKt.sourceInformationMarkerEnd(var53);
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var112 = var1.endRestartGroup();
      if (var112 != null) {
         final io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState recoveredCapture_883_0 = var0;
         final int recoveredCapture_883_1 = var2;
         var112.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_883_0, recoveredArgument_883_1) -> TeamPresenceComponentKt.TeamPresenceComponentWithBubble$lambda$14(recoveredCapture_883_0, recoveredCapture_883_1, (androidx.compose.runtime.Composer)recoveredArgument_883_0, ((java.lang.Integer)recoveredArgument_883_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamPresenceWithBubblePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1128132221);
      ComposerKt.sourceInformation(var0, "C(TeamPresenceWithBubblePreview)224@8757L167:TeamPresenceComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1128132221, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceWithBubblePreview (TeamPresenceComponent.kt:223)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TeamPresenceComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_888_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_888_0, recoveredArgument_888_1) -> TeamPresenceComponentKt.TeamPresenceWithBubblePreview$lambda$15(recoveredCapture_888_0, (androidx.compose.runtime.Composer)recoveredArgument_888_0, ((java.lang.Integer)recoveredArgument_888_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamPresencePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-161512363);
      ComposerKt.sourceInformation(var0, "C(TeamPresencePreview)236@8999L157:TeamPresenceComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -161512363, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresencePreview (TeamPresenceComponent.kt:235)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TeamPresenceComponentKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_887_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_887_0, recoveredArgument_887_1) -> TeamPresenceComponentKt.TeamPresencePreview$lambda$16(recoveredCapture_887_0, (androidx.compose.runtime.Composer)recoveredArgument_887_0, ((java.lang.Integer)recoveredArgument_887_1).intValue())));
      }
   }

   private static final void TeamPresenceComponent$onClick(View var0, Context var1, ArticleViewState$TeamPresenceState var2) {
      Injector.get().getMetricTracker().clickedNewConversation(var2.getMetricPlace(), var2.getMetricContext(), var2.isFromSearchBrowse());
      Context var10000 = var0.getContext();
      Intrinsics.checkNotNullExpressionValue(var10000, "getContext(...)");
      var10000 = ContextExtensionsKt.findActivity(var10000);
      Context var3 = var10000 != null ? var10000 : var1;
      Integer var4 = ConversationScreenOpenerKt.composerIntentFlagsFor(var3);
      if (var2.getConversationState() != null) {
         ConversationScreenOpenerKt.openComposer$default(var3, null, false, null, var2.getConversationState().getConversationId(), var4, 14, null);
      } else if (var2.getArticleMetadata() != null) {
         ConversationScreenOpenerKt.openComposer$default(var3, null, false, var2.getArticleMetadata(), null, var4, 22, null);
      } else {
         ConversationScreenOpenerKt.openComposer$default(var3, null, false, null, null, var4, 30, null);
      }
   }

   private static final Unit TeamPresenceComponent$lambda$5$lambda$2$lambda$1(ArticleViewState$TeamPresenceState var0, View var1, Context var2) {
      TeamPresenceComponent$onClick(var1, var2, var0);
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresenceComponent$lambda$5$lambda$4$lambda$3(ArticleViewState$TeamPresenceState var0, View var1, Context var2) {
      TeamPresenceComponent$onClick(var1, var2, var0);
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresenceComponent$lambda$6(
      ArticleViewState$TeamPresenceState var0, boolean var1, TeamPresenceButtonStyle var2, int var3, int var4, Composer var5, int var6
   ) {
      TeamPresenceComponent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresenceComponentWithBubble$lambda$13$lambda$9$lambda$8$lambda$7(Path var0, long var1, DrawScope var3) {
      Intrinsics.checkNotNullParameter(var3, "$this$onDrawBehind");
      DrawScope.recovered$a35e5c0f927371d1(var3, var0, var1, 0.0F, null, null, 0, 60, null);
      return Unit.INSTANCE;
   }

   private static final DrawResult TeamPresenceComponentWithBubble$lambda$13$lambda$9$lambda$8(long var0, CacheDrawScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$drawWithCache");
      Path var3 = AndroidPath_androidKt.Path();
      long var4 = var2.recovered$d1ee80c43f6fa69a();
      int var6 = 0;
      long var7 = var4;
      int var9 = 0;
      int var10 = (int)(var7 & 4294967295L);
      int var11 = 0;
      var3.moveTo(0.0F, Float.intBitsToFloat(var10));
      var4 = var2.recovered$d1ee80c43f6fa69a();
      var6 = 0;
      var7 = var4;
      var9 = 0;
      var10 = (int)(var7 >> 32);
      var11 = 0;
      float var10001 = Float.intBitsToFloat(var10) / 2.0F;
      var4 = var2.recovered$d1ee80c43f6fa69a();
      var6 = 0;
      var7 = var4;
      var9 = 0;
      var10 = (int)(var7 & 4294967295L);
      var11 = 0;
      var3.lineTo(var10001, Float.intBitsToFloat(var10) / 2.0F);
      var4 = var2.recovered$d1ee80c43f6fa69a();
      var6 = 0;
      var7 = var4;
      var9 = 0;
      var10 = (int)(var7 >> 32);
      var11 = 0;
      var10001 = Float.intBitsToFloat(var10);
      var4 = var2.recovered$d1ee80c43f6fa69a();
      var6 = 0;
      var7 = var4;
      var9 = 0;
      var10 = (int)(var7 & 4294967295L);
      var11 = 0;
      var3.lineTo(var10001, Float.intBitsToFloat(var10));
      var3.close();
      final androidx.compose.ui.graphics.Path recoveredExpressionCapture_886_0 = var3;
      final long recoveredExpressionCapture_886_1 = var0;
      return var2.onDrawBehind(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_886_0) -> io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt.TeamPresenceComponentWithBubble$lambda$13$lambda$9$lambda$8$lambda$7(recoveredExpressionCapture_886_0, recoveredExpressionCapture_886_1, (androidx.compose.ui.graphics.drawscope.DrawScope) ((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredExpressionArgument_886_0))));
   }

   private static final Modifier TeamPresenceComponentWithBubble$lambda$13$lambda$11$lambda$10(long var0, Modifier var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$ifTrue");
      return BackgroundKt.recovered$42a3174180b3fb7a(var2, var0, null, 2, null);
   }

   private static final Unit TeamPresenceComponentWithBubble$lambda$14(ArticleViewState$TeamPresenceState var0, int var1, Composer var2, int var3) {
      TeamPresenceComponentWithBubble(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresenceWithBubblePreview$lambda$15(int var0, Composer var1, int var2) {
      TeamPresenceWithBubblePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresencePreview$lambda$16(int var0, Composer var1, int var2) {
      TeamPresencePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final ArticleViewState$TeamPresenceState access$getMockTeamPresenceState$p() {
      return mockTeamPresenceState;
   }

   static {
      int var0 = string.intercom_the_team_can_help_if_needed;
      int var1 = string.intercom_send_us_a_message;
      int var2 = drawable.intercom_new_conversation_send_button;
      OpenMessengerResponse$NewConversationData$Cta var3 = new OpenMessengerResponse$NewConversationData$Cta(
         "Send us a message", "The team can help if needed", OpenMessengerResponse$IconType.PAPER_PLANE
      );
      mockTeamPresenceState = new ArticleViewState$TeamPresenceState(null, null, var0, var1, var2, -16777216, "article", "style_human", false, var3);
   }
}
