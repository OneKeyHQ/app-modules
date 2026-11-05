package io.intercom.android.sdk.helpcenter.search;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterErrorScreenKt;
import io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterLoadingScreenKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleSearchScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleSearchScreen.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,129:1\n1047#2,6:130\n87#3:136\n83#3,10:137\n94#3:175\n81#4,6:147\n88#4,6:162\n96#4:174\n402#5,9:153\n411#5:168\n412#5,2:172\n118#6:169\n118#6:170\n118#6:171\n*S KotlinDebug\n*F\n+ 1 IntercomArticleSearchScreen.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1\n*L\n52#1:130,6\n88#1:136\n88#1:137,10\n88#1:175\n88#1:147,6\n88#1:162,6\n88#1:174\n88#1:153,9\n88#1:168\n88#1:172,2\n98#1:169\n99#1:170\n100#1:171\n*E\n"
)
final class IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1 implements Function2 {
   // $VF: synthetic field
   final ArticleSearchState $state;
   // $VF: synthetic field
   final Function1 $onArticleClicked;
   // $VF: synthetic field
   final Function0 $onClearSearchClick;
   // $VF: synthetic field
   final Context $context;

   IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1(ArticleSearchState var1, Function1 var2, Function0 var3, Context var4) {
      this.$state = var1;
      this.$onArticleClicked = var2;
      this.$onClearSearchClick = var3;
      this.$context = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C:IntercomArticleSearchScreen.kt#fpck24");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1420291739, var2, -1, "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchScreen.<anonymous> (IntercomArticleSearchScreen.kt:47)"
            );
         }

         ArticleSearchState var3 = this.$state;
         if (!(var3 instanceof ArticleSearchState$Content)) {
            if (Intrinsics.areEqual(var3, ArticleSearchState$Error.INSTANCE)) {
               var1.startReplaceGroup(485141132);
               ComposerKt.sourceInformation(var1, "65@2989L148");
               HelpCenterErrorScreenKt.HelpCenterErrorScreen(
                  new ErrorState$WithoutCTA(0, 0, null, 7, null), SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), var1, 48, 0
               );
               var1.endReplaceGroup();
            } else if (Intrinsics.areEqual(var3, ArticleSearchState$Initial.INSTANCE)) {
               var1.startReplaceGroup(485340524);
               var1.endReplaceGroup();
               this.$onClearSearchClick.invoke();
            } else if (Intrinsics.areEqual(var3, ArticleSearchState$Loading.INSTANCE)) {
               var1.startReplaceGroup(485419295);
               ComposerKt.sourceInformation(var1, "76@3271L97");
               HelpCenterLoadingScreenKt.HelpCenterLoadingScreen(SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null), var1, 6, 0);
               var1.endReplaceGroup();
            } else {
               if (!(var3 instanceof ArticleSearchState$NoResults) && !(var3 instanceof ArticleSearchState$NoResultsNoTeamHelp)) {
                  var1.startReplaceGroup(154175038);
                  var1.endReplaceGroup();
                  throw new NoWhenBranchMatchedException();
               }

               var1.startReplaceGroup(485631955);
               ComposerKt.sourceInformation(var1, "87@3667L888");
               ArticleSearchState var57 = this.$state;
               String var56 = var57 instanceof ArticleSearchState$NoResults
                  ? ((ArticleSearchState$NoResults)this.$state).getSearchTerm()
                  : (var57 instanceof ArticleSearchState$NoResultsNoTeamHelp ? ((ArticleSearchState$NoResultsNoTeamHelp)this.$state).getSearchTerm() : "");
               Context var61 = this.$context;
               ArticleSearchState var62 = this.$state;
               int var63 = 0;
               int var64 = 0;
               ComposerKt.sourceInformationMarkerStart(
                  var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
               );
               Modifier var58 = Modifier.Companion;
               Vertical var59 = Arrangement.INSTANCE.getTop();
               Horizontal var60 = Alignment.Companion.getStart();
               MeasurePolicy var65 = ColumnKt.columnMeasurePolicy(var59, var60, var1, 14 & var63 >> 3 | 112 & var63 >> 3);
               Modifier var66 = var58;
               MeasurePolicy var14 = var65;
               Composer var15 = var1;
               int var16 = 112 & var63 << 3;
               int var17 = 0;
               ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
               int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
               CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
               Modifier var20 = ComposedModifierKt.materializeModifier(var15, var66);
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
               int var30 = 6 | 112 & var63 >> 6;
               Composer var31 = var28;
               ColumnScope var32 = ColumnScopeInstance.INSTANCE;
               int var33 = 0;
               ComposerKt.sourceInformationMarkerStart(var31, 201700569, "C102@4294L6,92@3848L530:IntercomArticleSearchScreen.kt#fpck24");
               AnnotatedString var34 = IntercomArticleSearchScreenKt.access$getNoResultsMessage(var61, var56);
               Modifier var35 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int var36 = 32;
               int var37 = 0;
               float var38 = Dp.recovered$95b3d103663bf060(var36);
               var37 = 20;
               int $i$f$getDpx = 0;
               float var68 = Dp.recovered$95b3d103663bf060(var37);
               $i$f$getDpx = 20;
               int $i$f$getDpxx = 0;
               float var70 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
               Modifier var41 = PaddingKt.recovered$03b481d31d62885a(var35, var68, var38, var70, 0.0F, 8, null);
               int var67 = TextAlign.Companion.recovered$d707b9dd64876179();
               long var42 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               long var44 = TextUnitKt.getSp(16);
               TextKt.recovered$489343e57da77a35(
                  var34,
                  var41,
                  var42,
                  null,
                  var44,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  TextAlign.recovered$18ab2fc4c81b19cd(var67),
                  0L,
                  0,
                  false,
                  0,
                  0,
                  null,
                  null,
                  null,
                  var31,
                  24576,
                  0,
                  523240
               );
               var31.startReplaceGroup(145075595);
               ComposerKt.sourceInformation(var31, "106@4449L66");
               if (var62 instanceof ArticleSearchState$NoResults) {
                  TeamPresenceComponentKt.TeamPresenceComponent(((ArticleSearchState$NoResults)var62).getTeamPresenceState(), false, null, var31, 0, 6);
               }

               var31.endReplaceGroup();
               ComposerKt.sourceInformationMarkerEnd(var31);
               ComposerKt.sourceInformationMarkerEnd(var28);
               var22.endNode();
               ComposerKt.sourceInformationMarkerEnd(var22);
               ComposerKt.sourceInformationMarkerEnd(var15);
               ComposerKt.sourceInformationMarkerEnd(var1);
               var1.endReplaceGroup();
            }
         } else {
            var1.startReplaceGroup(484447724);
            ComposerKt.sourceInformation(var1, "50@2351L6,51@2388L546,49@2274L660");
            Modifier var10000 = BackgroundKt.recovered$42a3174180b3fb7a(
               Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
            );
            Object var10001 = null;
            Object var10002 = null;
            boolean var10003 = false;
            Object var10004 = null;
            Object var10005 = null;
            Object var10006 = null;
            boolean var10007 = false;
            Object var10008 = null;
            var1.startReplaceGroup(154178756);
            ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleSearchScreen.kt#9igjgp");
            Composer var5 = var1;
            boolean var6 = var1.changed(this.$state) | var1.changed(this.$onArticleClicked);
            ArticleSearchState var7 = this.$state;
            Function1 var8 = this.$onArticleClicked;
            int var9 = 0;
            Object var10 = var5.rememberedValue();
            int var11 = 0;
            Object var10009;
            if (!var6 && var10 != Composer.Companion.getEmpty()) {
               var10009 = var10;
            } else {
               Object var54 = null;
               boolean var53 = false;
               Object var52 = null;
               Object var51 = null;
               Object var50 = null;
               boolean var49 = false;
               Object var48 = null;
               Object var47 = null;
               Modifier var46 = var10000;
               int var12 = 0;
               final io.intercom.android.sdk.helpcenter.search.ArticleSearchState recoveredCapture_115_0 = var7;
               final kotlin.jvm.functions.Function1 recoveredCapture_115_1 = var8;
               Function1 var55 = ((kotlin.jvm.functions.Function1) (recoveredArgument_115_0) -> IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1.invoke$lambda$1$lambda$0(recoveredCapture_115_0, recoveredCapture_115_1, (androidx.compose.foundation.lazy.LazyListScope)recoveredArgument_115_0));
               var10000 = var46;
               var10001 = var47;
               var10002 = var48;
               var10003 = var49;
               var10004 = var50;
               var10005 = var51;
               var10006 = var52;
               var10007 = var53;
               var10008 = var54;
               Object var13 = var55;
               var5.updateRememberedValue(var13);
               var10009 = var13;
            }

            Function1 var4 = (Function1)var10009;
            var1.endReplaceGroup();
            LazyDslKt.LazyColumn(
               var10000,
               (LazyListState)var10001,
               (PaddingValues)var10002,
               var10003,
               (Vertical)var10004,
               (Horizontal)var10005,
               (FlingBehavior)var10006,
               var10007,
               (OverscrollEffect)var10008,
               var4,
               var1,
               0,
               510
            );
            var1.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(ArticleSearchState var0, Function1 var1, LazyListScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$LazyColumn");
      LazyListScope.items$default(
         var2,
         ((ArticleSearchState$Content)var0).getSearchResults().size(),
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(1308683973, true, new IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1$1$1$1(var0, var1)),
         6,
         null
      );
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
