package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a#\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "FileUploadProgressComponent",
         "",
         "title",
         "",
         "onStopUploading",
         "Lkotlin/Function0;",
         "(Ljava/lang/String;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "UploadingActionSheetContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFileUploadProgressComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileUploadProgressComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileUploadProgressComponentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,59:1\n87#2:60\n84#2,9:61\n94#2:97\n81#3,6:70\n88#3,6:85\n96#3:96\n402#4,9:76\n411#4:91\n412#4,2:94\n118#5:92\n118#5:93\n*S KotlinDebug\n*F\n+ 1 FileUploadProgressComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileUploadProgressComponentKt\n*L\n21#1:60\n21#1:61,9\n21#1:97\n21#1:70,6\n21#1:85,6\n21#1:96\n21#1:76,9\n21#1:91\n21#1:94,2\n29#1:92\n36#1:93\n*E\n"
)
public final class FileUploadProgressComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileUploadProgressComponent(@NotNull String var0, @NotNull Function0 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "title");
      Intrinsics.checkNotNullParameter(var1, "onStopUploading");
      var2 = var2.startRestartGroup(-1826067636);
      ComposerKt.sourceInformation(var2, "C(FileUploadProgressComponent)P(1)20@724L623:FileUploadProgressComponent.kt#lphomb");
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
            ComposerKt.traceEventStart(
               -1826067636, var4, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadProgressComponent (FileUploadProgressComponent.kt:19)"
            );
         }

         Modifier var5 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         byte var8 = 6;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var6 = Arrangement.INSTANCE.getTop();
         Horizontal var7 = Alignment.Companion.getStart();
         MeasurePolicy var10 = ColumnKt.columnMeasurePolicy(var6, var7, var2, 14 & var8 >> 3 | 112 & var8 >> 3);
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
         ComposerKt.sourceInformationMarkerStart(var26, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         ColumnScope var30 = ColumnScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -1235754261, "C30@975L10,25@812L191,32@1012L133,38@1186L155:FileUploadProgressComponent.kt#lphomb");
         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var32 = 16;
         int var33 = 0;
         Modifier var34 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var32));
         TextStyle var36 = IntercomTheme.INSTANCE.getTypography(var29, IntercomTheme.$stable).getType04();
         TextKt.recovered$5b114ab32a4f4190(
            var0, var34, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var36, var29, 48 | 14 & var4, 0, 131068
         );
         var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivx = 4;
         var32 = 0;
         IntercomDividerKt.IntercomDivider(
            PaddingKt.recovered$f3b9f2e94c07c806(var10000, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 1, null), var29, 6, 0
         );
         FileUploadErrorComponentKt.recovered$88dfabc3dc9b4383(
            null, drawable.intercom_close, string.intercom_stop_upload, 0, 0L, var1, var29, 458752 & var4 << 12, 25
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

      ScopeUpdateScope var40 = var2.endRestartGroup();
      if (var40 != null) {
         final java.lang.String recoveredCapture_1458_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_1458_1 = var1;
         final int recoveredCapture_1458_2 = var3;
         var40.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1458_0, recoveredArgument_1458_1) -> FileUploadProgressComponentKt.FileUploadProgressComponent$lambda$1(recoveredCapture_1458_0, recoveredCapture_1458_1, recoveredCapture_1458_2, (androidx.compose.runtime.Composer)recoveredArgument_1458_0, ((java.lang.Integer)recoveredArgument_1458_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UploadingActionSheetContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2021767087);
      ComposerKt.sourceInformation(var0, "C(UploadingActionSheetContentPreview)49@1437L178:FileUploadProgressComponent.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2021767087,
               var1,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.UploadingActionSheetContentPreview (FileUploadProgressComponent.kt:48)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null, null, null, ComposableSingletons$FileUploadProgressComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1459_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1459_0, recoveredArgument_1459_1) -> FileUploadProgressComponentKt.UploadingActionSheetContentPreview$lambda$2(recoveredCapture_1459_0, (androidx.compose.runtime.Composer)recoveredArgument_1459_0, ((java.lang.Integer)recoveredArgument_1459_1).intValue())));
      }
   }

   private static final Unit FileUploadProgressComponent$lambda$1(String var0, Function0 var1, int var2, Composer var3, int var4) {
      FileUploadProgressComponent(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UploadingActionSheetContentPreview$lambda$2(int var0, Composer var1, int var2) {
      UploadingActionSheetContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
