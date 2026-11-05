package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.annotation.StringRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.SurfaceKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u001a!\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "AddFileButton",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "text",
         "",
         "(Landroidx/compose/ui/Modifier;ILandroidx/compose/runtime/Composer;II)V",
         "AddFileButtonPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAddFileButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AddFileButton.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/AddFileButtonKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,62:1\n118#2:63\n118#2:64\n118#2:93\n99#3,6:65\n106#3:97\n81#4,6:71\n88#4,6:86\n96#4:96\n402#5,9:77\n411#5:92\n412#5,2:94\n*S KotlinDebug\n*F\n+ 1 AddFileButton.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/AddFileButtonKt\n*L\n34#1:63\n36#1:64\n47#1:93\n27#1:65,6\n27#1:97\n27#1:71,6\n27#1:86,6\n27#1:96\n27#1:77,9\n27#1:92\n27#1:94,2\n*E\n"
)
public final class AddFileButtonKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AddFileButton(@Nullable Modifier var0, @StringRes int var1, @Nullable Composer var2, int var3, int var4) {
      var2 = var2.startRestartGroup(533336753);
      ComposerKt.sourceInformation(var2, "C(AddFileButton)29@1184L6,30@1263L6,26@1088L901:AddFileButton.kt#lphomb");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(533336753, var5, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.AddFileButton (AddFileButton.kt:25)");
         }

         Modifier var10000 = ClipKt.clip(
            BackgroundKt.recovered$60bc47fb440df483(
               var0,
               Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.06F, 0.0F, 0.0F, 0.0F, 14, null
               ),
               IntercomTheme.INSTANCE.getShapes(var2, IntercomTheme.$stable).getSmall()
            ),
            RoundedCornerShapeKt.RoundedCornerShape(2)
         );
         int var7 = 8;
         int var8 = 0;
         Modifier var6 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var7));
         Vertical var42 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivx = 3;
         int $i$f$getDpx = 0;
         HorizontalOrVertical var43 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         short var44 = 432;
         $i$f$getDpx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var43, var42, var2, 14 & var44 >> 3 | 112 & var44 >> 3);
         Modifier var12 = var6;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var44 << 3;
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
         int var29 = 6 | 112 & var44 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var30, -134309387, "C39@1550L25,40@1611L10,42@1716L6,37@1492L254,47@1829L49,49@1953L6,45@1756L227:AddFileButton.kt#lphomb"
         );
         Modifier var33 = Modifier.Companion;
         String var34 = StringResources_androidKt.stringResource(var1, var30, 14 & var5 >> 3);
         TextStyle var35 = IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04Point5();
         FontWeight var36 = FontWeight.Companion.getSemiBold();
         long var37 = IntercomTheme.INSTANCE.getColors(var30, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var34, var33, var37, null, 0L, null, var36, null, 0L, null, null, 0L, 0, false, 0, 0, null, var35, var30, 1572912, 0, 131000
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         var33 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         Painter var48 = PainterResources_androidKt.painterResource(drawable.intercom_ic_plus, var30, 0);
         long var39 = IntercomTheme.INSTANCE.getColors(var30, IntercomTheme.$stable).getIcon().recovered$e01a1d3b7b68e21a();
         IconKt.recovered$e00adffb7fe45b02(var48, "Add", var33, var39, var30, 432 | Painter.$stable, 0);
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var51 = var2.endRestartGroup();
      if (var51 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1426_0 = var0;
         final int recoveredCapture_1426_1 = var1;
         final int recoveredCapture_1426_2 = var3;
         final int recoveredCapture_1426_3 = var4;
         var51.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1426_0, recoveredArgument_1426_1) -> AddFileButtonKt.AddFileButton$lambda$1(recoveredCapture_1426_0, recoveredCapture_1426_1, recoveredCapture_1426_2, recoveredCapture_1426_3, (androidx.compose.runtime.Composer)recoveredArgument_1426_0, ((java.lang.Integer)recoveredArgument_1426_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void AddFileButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-126735215);
      ComposerKt.sourceInformation(var0, "C(AddFileButtonPreview)57@2064L80:AddFileButton.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-126735215, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.AddFileButtonPreview (AddFileButton.kt:56)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$AddFileButtonKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1427_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1427_0, recoveredArgument_1427_1) -> AddFileButtonKt.AddFileButtonPreview$lambda$2(recoveredCapture_1427_0, (androidx.compose.runtime.Composer)recoveredArgument_1427_0, ((java.lang.Integer)recoveredArgument_1427_1).intValue())));
      }
   }

   private static final Unit AddFileButton$lambda$1(Modifier var0, int var1, int var2, int var3, Composer var4, int var5) {
      AddFileButton(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit AddFileButtonPreview$lambda$2(int var0, Composer var1, int var2) {
      AddFileButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
