package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.TextKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.IntercomChevronKt;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a5\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b¨\u0006\r",
   d2 = {
         "ArticleRowComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "articleRow",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow$ArticleRow;",
         "onClick",
         "Lkotlin/Function1;",
         "",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsRow$ArticleRow;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "ArticleRowComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ArticleRowComponentLongTextPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nArticleRowComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleRowComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,68:1\n1047#2,6:69\n1047#2,6:75\n118#3:81\n118#3:82\n118#3:111\n99#4,6:83\n106#4:115\n81#5,6:89\n88#5,6:104\n96#5:114\n402#6,9:95\n411#6:110\n412#6,2:112\n*S KotlinDebug\n*F\n+ 1 ArticleRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleRowComponentKt\n*L\n23#1:69,6\n28#1:75,6\n29#1:81\n30#1:82\n40#1:111\n25#1:83,6\n25#1:115\n25#1:89,6\n25#1:104,6\n25#1:114\n25#1:95,9\n25#1:110\n25#1:112,2\n*E\n"
)
public final class ArticleRowComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ArticleRowComponent(
      @Nullable Modifier var0, @NotNull CollectionDetailsRow$ArticleRow var1, @Nullable Function1 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "articleRow");
      var3 = var3.startRestartGroup(1326302174);
      ComposerKt.sourceInformation(var3, "C(ArticleRowComponent)P(1)22@939L2,27@1035L26,24@951L606:ArticleRowComponent.kt#ngglre");
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
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var3.startReplaceGroup(1587295895);
            ComposerKt.sourceInformation(var3, "CC(remember):ArticleRowComponent.kt#9igjgp");
            Composer var8 = var3;
            boolean var9 = false;
            int var10 = 0;
            Object var11 = var8.rememberedValue();
            int var12 = 0;
            Object var10000;
            if (var11 == Composer.Companion.getEmpty()) {
               int var13 = 0;
               Object var62 = ((kotlin.jvm.functions.Function1) (recoveredArgument_855_0) -> ArticleRowComponentKt.ArticleRowComponent$lambda$1$lambda$0((java.lang.String)recoveredArgument_855_0));
               var8.updateRememberedValue(var62);
               var10000 = var62;
            } else {
               var10000 = var11;
            }

            Function1 var7 = (Function1)var10000;
            var3.endReplaceGroup();
            var2 = var7;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1326302174, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleRowComponent (ArticleRowComponent.kt:23)"
            );
         }

         Modifier var68 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var3.startReplaceGroup(1587298991);
         ComposerKt.sourceInformation(var3, "CC(remember):ArticleRowComponent.kt#9igjgp");
         Composer var50 = var3;
         boolean var54 = (var6 & 896) == 256 | (var6 & 112) == 32;
         int var56 = 0;
         Object var58 = var50.rememberedValue();
         int var60 = 0;
         Object var10005;
         if (!var54 && var58 != Composer.Companion.getEmpty()) {
            var10005 = var58;
         } else {
            Object var43 = null;
            Object var42 = null;
            Object var41 = null;
            boolean var40 = false;
            Modifier var39 = var68;
            int var63 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_856_0 = var2;
            final io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow recoveredCapture_856_1 = var1;
            Function0 var44 = ((kotlin.jvm.functions.Function0) () -> ArticleRowComponentKt.ArticleRowComponent$lambda$3$lambda$2(recoveredCapture_856_0, recoveredCapture_856_1));
            var68 = var39;
            var10001 = var40;
            var10002 = var41;
            var10003 = var42;
            var10004 = var43;
            Object var14 = var44;
            var50.updateRememberedValue(var14);
            var10005 = var14;
         }

         Function0 var46 = (Function0)var10005;
         var3.endReplaceGroup();
         var68 = ClickableKt.recovered$1c9660c41fd850dc(var68, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var46, 15, null);
         int var47 = 16;
         int var51 = 0;
         var68 = PaddingKt.recovered$03b481d31d62885a(var68, Dp.recovered$95b3d103663bf060(var47), 0.0F, 0.0F, 0.0F, 14, null);
         int $this$dp$ivx = 12;
         int $i$f$getDpx = 0;
         Modifier var49 = PaddingKt.recovered$f3b9f2e94c07c806(var68, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 1, null);
         Horizontal var53 = Arrangement.INSTANCE.getSpaceBetween();
         Vertical var55 = Alignment.Companion.getCenterVertically();
         short var57 = 432;
         var58 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var61 = RowKt.rowMeasurePolicy(var53, var55, var3, 14 & var57 >> 3 | 112 & var57 >> 3);
         Modifier var64 = var49;
         MeasurePolicy var65 = var61;
         Composer var15 = var3;
         int var16 = 112 & var57 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var64);
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
         Updater.recovered$89682b199a142880(var25, var65, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var30 = 6 | 112 & var57 >> 6;
         Composer var31 = var28;
         RowScope var32 = RowScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -998052204, "C35@1345L10,36@1404L6,33@1268L210,39@1487L64:ArticleRowComponent.kt#ngglre");
         String var34 = var1.getTitle();
         TextStyle var35 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04Point5();
         long var36 = IntercomTheme.INSTANCE.getColors(var31, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         Modifier var38 = RowScope.weight$default(var32, Modifier.Companion, 1.0F, false, 2, null);
         TextKt.recovered$5b114ab32a4f4190(
            var34, var38, var36, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var35, var31, 0, 0, 131064
         );
         var68 = Modifier.Companion;
         int $this$dp$ivxx = 22;
         int $i$f$getDpxx = 0;
         IntercomChevronKt.IntercomChevron(
            PaddingKt.recovered$f3b9f2e94c07c806(var68, Dp.recovered$95b3d103663bf060($this$dp$ivxx), 0.0F, 2, null), var31, 6, 0
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var72 = var3.endRestartGroup();
      if (var72 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_857_0 = var0;
         final io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow recoveredCapture_857_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_857_2 = var2;
         final int recoveredCapture_857_3 = var4;
         final int recoveredCapture_857_4 = var5;
         var72.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_857_0, recoveredArgument_857_1) -> ArticleRowComponentKt.ArticleRowComponent$lambda$5(recoveredCapture_857_0, recoveredCapture_857_1, recoveredCapture_857_2, recoveredCapture_857_3, recoveredCapture_857_4, (androidx.compose.runtime.Composer)recoveredArgument_857_0, ((java.lang.Integer)recoveredArgument_857_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ArticleRowComponentPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(761606012);
      ComposerKt.sourceInformation(var0, "C(ArticleRowComponentPreview)46@1638L205:ArticleRowComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               761606012, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleRowComponentPreview (ArticleRowComponent.kt:45)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ArticleRowComponentKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_859_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_859_0, recoveredArgument_859_1) -> ArticleRowComponentKt.ArticleRowComponentPreview$lambda$6(recoveredCapture_859_0, (androidx.compose.runtime.Composer)recoveredArgument_859_0, ((java.lang.Integer)recoveredArgument_859_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ArticleRowComponentLongTextPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(2014154163);
      ComposerKt.sourceInformation(var0, "C(ArticleRowComponentLongTextPreview)57@1932L312:ArticleRowComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2014154163, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleRowComponentLongTextPreview (ArticleRowComponent.kt:56)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ArticleRowComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_858_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_858_0, recoveredArgument_858_1) -> ArticleRowComponentKt.ArticleRowComponentLongTextPreview$lambda$7(recoveredCapture_858_0, (androidx.compose.runtime.Composer)recoveredArgument_858_0, ((java.lang.Integer)recoveredArgument_858_1).intValue())));
      }
   }

   private static final Unit ArticleRowComponent$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ArticleRowComponent$lambda$3$lambda$2(Function1 var0, CollectionDetailsRow$ArticleRow var1) {
      var0.invoke(var1.getId());
      return Unit.INSTANCE;
   }

   private static final Unit ArticleRowComponent$lambda$5(
      Modifier var0, CollectionDetailsRow$ArticleRow var1, Function1 var2, int var3, int var4, Composer var5, int var6
   ) {
      ArticleRowComponent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ArticleRowComponentPreview$lambda$6(int var0, Composer var1, int var2) {
      ArticleRowComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ArticleRowComponentLongTextPreview$lambda$7(int var0, Composer var1, int var2) {
      ArticleRowComponentLongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
