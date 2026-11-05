package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleSearchActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$SearchScreenContent$2\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,143:1\n87#2:144\n84#2,9:145\n94#2:185\n81#3,6:154\n88#3,6:169\n96#3:184\n402#4,9:160\n411#4:175\n412#4,2:182\n1047#5,6:176\n*S KotlinDebug\n*F\n+ 1 IntercomArticleSearchActivity.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchActivity$SearchScreenContent$2\n*L\n128#1:144\n128#1:145,9\n128#1:185\n128#1:154,6\n128#1:169,6\n128#1:184\n128#1:160,9\n128#1:175\n128#1:182,2\n136#1:176,6\n*E\n"
)
final class IntercomArticleSearchActivity$SearchScreenContent$2 implements Function3 {
   // $VF: synthetic field
   final ArticleSearchState $articleSearchState;
   // $VF: synthetic field
   final Function1 $onArticleClicked;

   IntercomArticleSearchActivity$SearchScreenContent$2(ArticleSearchState var1, Function1 var2) {
      this.$articleSearchState = var1;
      this.$onArticleClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C130@5328L6,127@5203L466:IntercomArticleSearchActivity.kt#fpck24");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               955713763,
               var4,
               -1,
               "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchActivity.SearchScreenContent.<anonymous> (IntercomArticleSearchActivity.kt:127)"
            );
         }

         Modifier var5 = PaddingKt.padding(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               null,
               2,
               null
            ),
            var1
         );
         ArticleSearchState var8 = this.$articleSearchState;
         Function1 var9 = this.$onArticleClicked;
         int var10 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var6 = Arrangement.INSTANCE.getTop();
         Horizontal var7 = Alignment.Companion.getStart();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var6, var7, var2, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var5;
         MeasurePolicy var14 = var12;
         Composer var15 = var2;
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
         ComposerKt.sourceInformationMarkerStart(var31, -674455936, "C135@5541L38,133@5423L232:IntercomArticleSearchActivity.kt#fpck24");
         ArticleSearchState var10000 = var8;
         var31.startReplaceGroup(-1961415711);
         ComposerKt.sourceInformation(var31, "CC(remember):IntercomArticleSearchActivity.kt#9igjgp");
         Composer var34 = var31;
         boolean var35 = false;
         int var36 = 0;
         Object var37 = var34.rememberedValue();
         int var38 = 0;
         kotlin.jvm.functions.Function0 var43;
         if (var37 == Composer.Companion.getEmpty()) {
            ArticleSearchState var39 = var8;
            int var40 = 0;
            var43 = ((kotlin.jvm.functions.Function0) () -> IntercomArticleSearchActivity$SearchScreenContent$2.invoke$lambda$2$lambda$1$lambda$0());
            var10000 = var39;
            Object var42 = var43;
            var34.updateRememberedValue(var42);
            var43 = (Function0)var42;
         } else {
            var43 = (Function0)var37;
         }

         Function0 var41 = var43;
         var31.endReplaceGroup();
         IntercomArticleSearchScreenKt.IntercomArticleSearchScreen(var10000, var41, var9, var31, 48);
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
