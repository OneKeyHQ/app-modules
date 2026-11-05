package io.intercom.android.sdk.survey.ui.components.validation;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.survey.ValidationError$ValidationStringError;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.Pair;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a)\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0004\b\b\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b¨\u0006\f",
   d2 = {
         "ValidationErrorComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "validationStringError",
         "Lio/intercom/android/sdk/survey/ValidationError$ValidationStringError;",
         "errorColor",
         "Landroidx/compose/ui/graphics/Color;",
         "ValidationErrorComponent-FNF3uiM",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/ValidationError$ValidationStringError;JLandroidx/compose/runtime/Composer;II)V",
         "ErrorPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nValidationErrorComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ValidationErrorComponent.kt\nio/intercom/android/sdk/survey/ui/components/validation/ValidationErrorComponentKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,66:1\n118#2:67\n118#2:100\n118#2:104\n99#3:68\n96#3,9:69\n106#3:108\n81#4,6:78\n88#4,6:93\n96#4:107\n402#5,9:84\n411#5:99\n412#5,2:105\n75#6:101\n1869#7,2:102\n*S KotlinDebug\n*F\n+ 1 ValidationErrorComponent.kt\nio/intercom/android/sdk/survey/ui/components/validation/ValidationErrorComponentKt\n*L\n32#1:67\n36#1:100\n49#1:104\n29#1:68\n29#1:69,9\n29#1:108\n29#1:78,6\n29#1:93,6\n29#1:107\n29#1:84,9\n29#1:99\n29#1:105,2\n41#1:101\n42#1:102,2\n*E\n"
)
public final class ValidationErrorComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$f274ad548aeaeedc(
      @Nullable Modifier var0, @NotNull ValidationError$ValidationStringError var1, long var2, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "validationStringError");
      var4 = var4.startRestartGroup(-1195832801);
      ComposerKt.sourceInformation(var4, "C(ValidationErrorComponent)P(1,2,0:c#ui.graphics.Color)28@1104L865:ValidationErrorComponent.kt#x5xaaf");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1195832801, var7, -1, "io.intercom.android.sdk.survey.ui.components.validation.ValidationErrorComponent (ValidationErrorComponent.kt:27)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         int var8 = 2;
         int var9 = 0;
         float var10002 = Dp.recovered$95b3d103663bf060(var8);
         var8 = 2;
         var9 = 0;
         Modifier var44 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060(var8), 5, null);
         Vertical var10 = Alignment.Companion.getCenterVertically();
         short var11 = 384;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var46 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var13 = RowKt.rowMeasurePolicy(var46, var10, var4, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var44;
         MeasurePolicy var15 = var13;
         Composer var16 = var4;
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
         ComposerKt.sourceInformationMarkerStart(var29, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         RowScope var33 = RowScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 1942949114, "C36@1360L52,34@1286L207,40@1547L7,50@1935L10,44@1705L258:ValidationErrorComponent.kt#x5xaaf"
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var37 = SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(drawable.intercom_ic_error, var32, 0),
            null,
            var37,
            var2,
            var32,
            432 | Painter.$stable | 7168 & var7 << 3,
            0
         );
         CompositionLocal var47 = AndroidCompositionLocals_androidKt.getLocalContext();
         $i$f$getDpx = 0;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var39 = var32.consume(var47);
         ComposerKt.sourceInformationMarkerEnd(var32);
         Phrase var53 = Phrase.from((Context)var39, var1.getStringRes());
         Iterable var48 = var1.getParams();
         $i$f$getDpx = 0;

         for (Object var55 : var48) {
            Pair var40 = (Pair)var55;
            int var41 = 0;
            var53.put((String)var40.getFirst(), (CharSequence)var40.getSecond());
         }

         String var57 = var53.format().toString();
         Modifier var10001 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         $this$dp$ivx = 4;
         $i$f$getDpx = 0;
         TextKt.recovered$5b114ab32a4f4190(
            var57,
            PaddingKt.recovered$03b481d31d62885a(var10001, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 0.0F, 0.0F, 14, null),
            var2,
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
            IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType04(),
            var32,
            48 | 896 & var7,
            0,
            131064
         );
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var58 = var4.endRestartGroup();
      if (var58 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1369_0 = var0;
         final io.intercom.android.sdk.survey.ValidationError$ValidationStringError recoveredCapture_1369_1 = var1;
         final long recoveredCapture_1369_2 = var2;
         final int recoveredCapture_1369_3 = var5;
         final int recoveredCapture_1369_4 = var6;
         var58.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1369_0, recoveredArgument_1369_1) -> ValidationErrorComponentKt.ValidationErrorComponent_FNF3uiM$lambda$2(recoveredCapture_1369_0, recoveredCapture_1369_1, recoveredCapture_1369_2, recoveredCapture_1369_3, recoveredCapture_1369_4, (androidx.compose.runtime.Composer)recoveredArgument_1369_0, ((java.lang.Integer)recoveredArgument_1369_1).intValue())));
      }
   }

   @Composable
   @Preview(showBackground = true)
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ErrorPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1851250451);
      ComposerKt.sourceInformation(var0, "C(ErrorPreview)58@2051L220:ValidationErrorComponent.kt#x5xaaf");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1851250451, var1, -1, "io.intercom.android.sdk.survey.ui.components.validation.ErrorPreview (ValidationErrorComponent.kt:57)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ValidationErrorComponentKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1368_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1368_0, recoveredArgument_1368_1) -> ValidationErrorComponentKt.ErrorPreview$lambda$3(recoveredCapture_1368_0, (androidx.compose.runtime.Composer)recoveredArgument_1368_0, ((java.lang.Integer)recoveredArgument_1368_1).intValue())));
      }
   }

   private static final Unit ValidationErrorComponent_FNF3uiM$lambda$2(
      Modifier var0, ValidationError$ValidationStringError var1, long var2, int var4, int var5, Composer var6, int var7
   ) {
      recovered$f274ad548aeaeedc(var0, var1, var2, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit ErrorPreview$lambda$3(int var0, Composer var1, int var2) {
      ErrorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
