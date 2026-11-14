package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.res.Configuration;
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
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.Source;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a3\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\u0019\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000b*\u00020\u000bH\u0002¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000f¨\u0006\u0010",
   d2 = {
         "InlineSourcesSheetContent",
         "",
         "selectedSources",
         "",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "sources",
         "Lio/intercom/android/sdk/models/Source;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/util/List;Ljava/util/List;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "getHost",
         "",
         "kotlin.jvm.PlatformType",
         "(Ljava/lang/String;)Ljava/lang/String;",
         "InlineSourcesSheetContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nInlineSourcesSheetContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InlineSourcesSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/InlineSourcesSheetContentKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 9 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n*L\n1#1,212:1\n75#2:213\n118#3:214\n118#3:248\n118#3:249\n66#4:215\n87#5:216\n84#5,9:217\n94#5:259\n81#6,6:226\n88#6,6:241\n96#6:258\n402#7,9:232\n411#7:247\n412#7,2:256\n1047#8,6:250\n204#9,13:260\n*S KotlinDebug\n*F\n+ 1 InlineSourcesSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/InlineSourcesSheetContentKt\n*L\n51#1:213\n51#1:214\n56#1:248\n57#1:249\n51#1:215\n48#1:216\n48#1:217,9\n48#1:259\n48#1:226,6\n48#1:241,6\n48#1:258\n48#1:232,9\n48#1:247\n48#1:256,2\n65#1:250,6\n66#1:260,13\n*E\n"
)
public final class InlineSourcesSheetContentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InlineSourcesSheetContent(
      @NotNull List var0, @NotNull List var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "selectedSources");
      Intrinsics.checkNotNullParameter(var1, "sources");
      var3 = var3.startRestartGroup(-2127469664);
      ComposerKt.sourceInformation(var3, "C(InlineSourcesSheetContent)P(1,2)*50@2074L7,47@1963L5755:InlineSourcesSheetContent.kt#o19g89");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 19) == 18 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2127469664, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.InlineSourcesSheetContent (InlineSourcesSheetContent.kt:46)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalConfiguration();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var3.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var3);
         int var53 = ((Configuration)var10).screenHeightDp;
         var8 = 0;
         float var54 = Dp.recovered$95b3d103663bf060(var53);
         float var57 = 0.5F;
         var9 = 0;
         Modifier var55 = SizeKt.recovered$1c935d354e9159f0(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var54 * var57), 1, null);
         byte var61 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var58 = Arrangement.INSTANCE.getTop();
         Horizontal var60 = Alignment.Companion.getStart();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var58, var60, var3, 14 & var61 >> 3 | 112 & var61 >> 3);
         Modifier var13 = var55;
         MeasurePolicy var14 = var12;
         Composer var15 = var3;
         int var16 = 112 & var61 << 3;
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
         int var30 = 6 | 112 & var61 >> 6;
         Composer var31 = var28;
         ColumnScope var32 = ColumnScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 504709295, "C62@2511L10,52@2125L414,64@2559L5153,64@2548L5164:InlineSourcesSheetContent.kt#o19g89");
         var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var34 = 40;
         int var35 = 0;
         var10000 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var34), 0.0F, 2, null);
         var34 = 16;
         var35 = 0;
         Modifier var36 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var34), 7, null);
         String var73;
         if (var1.size() == 1) {
            var31.startReplaceGroup(504735737);
            ComposerKt.sourceInformation(var31, "58@2342L40");
            String var66 = StringResources_androidKt.stringResource(string.intercom_source, var31, 0);
            var31.endReplaceGroup();
            var73 = var66;
         } else {
            var31.startReplaceGroup(504813144);
            ComposerKt.sourceInformation(var31, "60@2420L41");
            String var67 = StringResources_androidKt.stringResource(string.intercom_sources, var31, 0);
            var31.endReplaceGroup();
            var73 = var67;
         }

         String var63 = var73;
         TextStyle var68 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04();
         TextKt.recovered$5b114ab32a4f4190(var63, var36, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var68, var31, 48, 0, 131068);
         var10000 = null;
         Object var10001 = null;
         Object var10002 = null;
         boolean var10003 = false;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var31.startReplaceGroup(-953536926);
         ComposerKt.sourceInformation(var31, "CC(remember):InlineSourcesSheetContent.kt#9igjgp");
         Composer var64 = var31;
         boolean var69 = var3.changedInstance(var1) | var3.changedInstance(var0);
         int var37 = 0;
         Object var38 = var64.rememberedValue();
         int var39 = 0;
         Object var10009;
         if (!var69 && var38 != Composer.Companion.getEmpty()) {
            var10009 = var38;
         } else {
            Object var40 = null;
            boolean var41 = false;
            Object var42 = null;
            Object var43 = null;
            Object var44 = null;
            boolean var45 = false;
            Object var46 = null;
            Object var47 = null;
            Object var48 = null;
            int var49 = 0;
            final java.util.List recoveredExpressionCapture_440_0 = var1;
            final java.util.List recoveredExpressionCapture_440_1 = var0;
            Function1 var50 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_440_0) -> io.intercom.android.sdk.m5.conversation.ui.components.InlineSourcesSheetContentKt.InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12(recoveredExpressionCapture_440_0, recoveredExpressionCapture_440_1, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_440_0)));
            var10000 = (Modifier)var48;
            var10001 = var47;
            var10002 = var46;
            var10003 = var45;
            var10004 = var44;
            var10005 = var43;
            var10006 = var42;
            var10007 = var41;
            var10008 = var40;
            Object var51 = var50;
            var64.updateRememberedValue(var51);
            var10009 = var51;
         }

         Function1 var70 = (Function1)var10009;
         var31.endReplaceGroup();
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
            var70,
            var31,
            0,
            511
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

      ScopeUpdateScope var75 = var3.endRestartGroup();
      if (var75 != null) {
         final java.util.List recoveredCapture_441_0 = var0;
         final java.util.List recoveredCapture_441_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_441_2 = var2;
         final int recoveredCapture_441_3 = var4;
         final int recoveredCapture_441_4 = var5;
         var75.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_441_0, recoveredArgument_441_1) -> InlineSourcesSheetContentKt.InlineSourcesSheetContent$lambda$15(recoveredCapture_441_0, recoveredCapture_441_1, recoveredCapture_441_2, recoveredCapture_441_3, recoveredCapture_441_4, (androidx.compose.runtime.Composer)recoveredArgument_441_0, ((java.lang.Integer)recoveredArgument_441_1).intValue())));
      }
   }

   private static final String getHost(String var0) {
      String var1;
      try {
         URL var4 = new URL(var0);
         var1 = var4.getHost();
      } catch (MalformedURLException var3) {
         var1 = var0;
      }

      return var1;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InlineSourcesSheetContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1197768873);
      ComposerKt.sourceInformation(var0, "C(InlineSourcesSheetContentPreview)181@7953L1054:InlineSourcesSheetContent.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1197768873,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.InlineSourcesSheetContentPreview (InlineSourcesSheetContent.kt:180)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InlineSourcesSheetContentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_443_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_443_0, recoveredArgument_443_1) -> InlineSourcesSheetContentKt.InlineSourcesSheetContentPreview$lambda$16(recoveredCapture_443_0, (androidx.compose.runtime.Composer)recoveredArgument_443_0, ((java.lang.Integer)recoveredArgument_443_1).intValue())));
      }
   }

   private static final Object InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$lambda$1(int var0, Source var1) {
      Intrinsics.checkNotNullParameter(var1, "source");
      CharSequence var2 = var1.getId();
      CharSequence var10000;
      if (var2.length() == 0) {
         int var3 = 0;
         var10000 = "source_" + var0 + '_' + var1.getUrl().hashCode();
      } else {
         var10000 = var2;
      }

      return var10000;
   }

   private static final Unit InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12(List var0, List var1, LazyListScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$LazyColumn");
      LazyListScope var3 = var2;
      Function2 var4 = ((kotlin.jvm.functions.Function2) (recoveredArgument_442_0, recoveredArgument_442_1) -> InlineSourcesSheetContentKt.InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$lambda$1(((java.lang.Integer)recoveredArgument_442_0).intValue(), (io.intercom.android.sdk.models.Source)recoveredArgument_442_1));
      int var5 = 0;
      var3.items(
         var0.size(),
         var4 != null
            ? new InlineSourcesSheetContentKt$InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$1(
               var4, var0
            )
            : null,
         new InlineSourcesSheetContentKt$InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$2(
            var0
         ),
         ComposableLambdaKt.composableLambdaInstance(
            2039820996,
            true,
            new InlineSourcesSheetContentKt$InlineSourcesSheetContent$lambda$14$lambda$13$lambda$12$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
               var0, var1
            )
         )
      );
      return Unit.INSTANCE;
   }

   private static final Unit InlineSourcesSheetContent$lambda$15(List var0, List var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      InlineSourcesSheetContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit InlineSourcesSheetContentPreview$lambda$16(int var0, Composer var1, int var2) {
      InlineSourcesSheetContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final String access$getHost(String var0) {
      return getHost(var0);
   }
}
