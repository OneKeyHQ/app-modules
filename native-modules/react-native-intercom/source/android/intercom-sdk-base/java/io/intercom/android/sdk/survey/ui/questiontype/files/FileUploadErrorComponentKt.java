package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileTooLarge;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UnsupportedFileType;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UploadFailed;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.ErrorMessageLayoutKt;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a9\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\t\u001aO\u0010\n\u001a\u00020\u00012\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\u000e2\b\b\u0001\u0010\u000f\u001a\u00020\u000e2\b\b\u0003\u0010\u0010\u001a\u00020\u000e2\b\b\u0002\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0004\b\u0014\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0017\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0017¨\u0006\u0019",
   d2 = {
         "FileUploadErrorComponent",
         "",
         "title",
         "",
         "error",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError;",
         "onRetryClick",
         "Lkotlin/Function0;",
         "onDeleteClick",
         "(Ljava/lang/String;Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$FileUploadError;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "ActionRow",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "icon",
         "",
         "text",
         "contentDescription",
         "tint",
         "Landroidx/compose/ui/graphics/Color;",
         "onClick",
         "ActionRow-FHprtrg",
         "(Landroidx/compose/ui/Modifier;IIIJLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "ErrorActionSheetContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UploadFailedErrorActionSheetPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFileUploadErrorComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileUploadErrorComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileUploadErrorComponentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,163:1\n87#2:164\n84#2,9:165\n94#2:203\n81#3,6:174\n88#3,6:189\n96#3:202\n81#3,6:218\n88#3,6:233\n96#3:242\n402#4,9:180\n411#4:195\n412#4,2:200\n402#4,9:224\n411#4,3:239\n118#5:196\n118#5:197\n118#5:198\n118#5:199\n118#5:210\n118#5:211\n1047#6,6:204\n99#7,6:212\n106#7:243\n*S KotlinDebug\n*F\n+ 1 FileUploadErrorComponent.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileUploadErrorComponentKt\n*L\n40#1:164\n40#1:165,9\n40#1:203\n40#1:174,6\n40#1:189,6\n40#1:202\n111#1:218,6\n111#1:233,6\n111#1:242\n40#1:180,9\n40#1:195\n40#1:200,2\n111#1:224,9\n111#1:239,3\n48#1:196\n55#1:197\n61#1:198\n88#1:199\n117#1:210\n119#1:211\n114#1:204,6\n111#1:212,6\n111#1:243\n*E\n"
)
public final class FileUploadErrorComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileUploadErrorComponent(
      @NotNull String var0, @NotNull io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError var1, @NotNull Function0 var2, @NotNull Function0 var3, @Nullable Composer var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "title");
      Intrinsics.checkNotNullParameter(var1, "error");
      Intrinsics.checkNotNullParameter(var2, "onRetryClick");
      Intrinsics.checkNotNullParameter(var3, "onDeleteClick");
      Composer var41 = var4.startRestartGroup(725182893);
      ComposerKt.sourceInformation(var41, "C(FileUploadErrorComponent)P(3!1,2)39@1813L1884:FileUploadErrorComponent.kt#lphomb");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var41.changed(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var41.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var41.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var41.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var41.getSkipping()) {
         var41.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               725182893, var6, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponent (FileUploadErrorComponent.kt:38)"
            );
         }

         Modifier var7 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         byte var10 = 6;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var41, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var8 = Arrangement.INSTANCE.getTop();
         Horizontal var9 = Alignment.Companion.getStart();
         MeasurePolicy var12 = ColumnKt.columnMeasurePolicy(var8, var9, var41, 14 & var10 >> 3 | 112 & var10 >> 3);
         Modifier var13 = var7;
         MeasurePolicy var14 = var12;
         Composer var15 = var41;
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
         ComposerKt.sourceInformationMarkerStart(var31, 156891081, "C49@2098L10,44@1901L225,51@2135L205,57@2349L133:FileUploadErrorComponent.kt#lphomb");
         Modifier var34 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var35 = 16;
         int var36 = 0;
         float var37 = Dp.recovered$95b3d103663bf060(var35);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         float var47 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         float var49 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         Modifier var40 = PaddingKt.recovered$03b481d31d62885a(var34, var37, var49, var47, 0.0F, 8, null);
         TextStyle var42 = IntercomTheme.INSTANCE.getTypography(var31, IntercomTheme.$stable).getType04();
         TextKt.recovered$5b114ab32a4f4190(
            var0, var40, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var42, var31, 14 & var6, 0, 131068
         );
         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivxxx = 16;
         int $i$f$getDpxxx = 0;
         float var10001 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
         int $this$dp$ivxxxx = 8;
         int $i$f$getDpxxxx = 0;
         ErrorMessageLayoutKt.ErrorMessageLayout(
            PaddingKt.recovered$904403cea4c49d95(var10000, var10001, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx)), var1.getErrorMessages(), var31, 6, 0
         );
         Modifier var56 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivxxxxx = 4;
         int $i$f$getDpxxxxx = 0;
         IntercomDividerKt.IntercomDivider(
            PaddingKt.recovered$f3b9f2e94c07c806(var56, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxx), 1, null), var31, 6, 0
         );
         io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError var55 = var1;
         if (!(var55 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileLimitExceeded) && !(var55 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UnsupportedFileType) && !(var55 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$FileTooLarge)) {
            if (!(var55 instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError$UploadFailed)) {
               var31.startReplaceGroup(-549109013);
               var31.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var31.startReplaceGroup(157902703);
            ComposerKt.sourceInformation(var31, "78@3011L186,84@3248L165,90@3470L197");
            recovered$88dfabc3dc9b4383(null, drawable.intercom_ic_reload, string.intercom_try_again, 0, 0L, var2, var31, 458752 & var6 << 9, 25);
            Modifier var57 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            int $this$dp$ivxxxxxx = 4;
            int $i$f$getDpxxxxxx = 0;
            IntercomDividerKt.IntercomDivider(
               PaddingKt.recovered$f3b9f2e94c07c806(var57, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivxxxxxx), 1, null), var31, 6, 0
            );
            recovered$88dfabc3dc9b4383(
               null, io.intercom.android.sdk.ui.R.drawable.intercom_ic_delete, string.intercom_delete_attachment, 0, 0L, var3, var31, 458752 & var6 << 6, 25
            );
            var31.endReplaceGroup();
         } else {
            var31.startReplaceGroup(157589913);
            ComposerKt.sourceInformation(var31, "70@2711L197");
            recovered$88dfabc3dc9b4383(
               null, io.intercom.android.sdk.ui.R.drawable.intercom_ic_delete, string.intercom_delete_attachment, 0, 0L, var3, var31, 458752 & var6 << 6, 25
            );
            var31.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var41);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var58 = var41.endRestartGroup();
      if (var58 != null) {
         final java.lang.String recoveredExpressionCapture_1454_0 = var0;
         final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError recoveredExpressionCapture_1454_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1454_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1454_3 = var3;
         final int recoveredExpressionCapture_1454_4 = var5;
         var58.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1454_0, recoveredExpressionArgument_1454_1) -> io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponentKt.FileUploadErrorComponent$lambda$1(recoveredExpressionCapture_1454_0, recoveredExpressionCapture_1454_1, recoveredExpressionCapture_1454_2, recoveredExpressionCapture_1454_3, recoveredExpressionCapture_1454_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1454_0), ((java.lang.Integer)recoveredExpressionArgument_1454_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$88dfabc3dc9b4383(
      @Nullable Modifier var0,
      @DrawableRes int var1,
      @StringRes int var2,
      @StringRes int var3,
      long var4,
      @NotNull Function0 var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var6, "onClick");
      Composer var44 = var7.startRestartGroup(-1845655864);
      ComposerKt.sourceInformation(var44, "C(ActionRow)P(2,1,4!1,5:c#ui.graphics.Color)106@3945L6,113@4086L41,110@4013L627:FileUploadErrorComponent.kt#lphomb");
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var44.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var44.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var44.changed(var2) ? 256 : 128;
      }

      if ((var8 & 3072) == 0) {
         var10 |= (var9 & 8) == 0 && var44.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 24576) == 0) {
         var10 |= (var9 & 16) == 0 && var44.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var44.changedInstance(var6) ? 131072 : 65536;
      }

      if ((var10 & 74899) == 74898 && var44.getSkipping()) {
         var44.skipToGroupEnd();
      } else {
         var44.startDefaults();
         if ((var8 & 1) != 0 && !var44.getDefaultsInvalid()) {
            var44.skipToGroupEnd();
            if ((var9 & 8) != 0) {
               var10 &= -7169;
            }

            if ((var9 & 16) != 0) {
               var10 &= -57345;
            }
         } else {
            if ((var9 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var9 & 8) != 0) {
               var3 = var2;
               var10 &= -7169;
            }

            if ((var9 & 16) != 0) {
               var4 = IntercomTheme.INSTANCE.getColors(var44, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var10 &= -57345;
            }
         }

         var44.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1845655864, var10, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.ActionRow (FileUploadErrorComponent.kt:108)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var44.startReplaceGroup(1130566658);
         ComposerKt.sourceInformation(var44, "CC(remember):FileUploadErrorComponent.kt#9igjgp");
         Composer var13 = var44;
         boolean var14 = (var10 & 458752) == 131072;
         int var15 = 0;
         Object var16 = var13.rememberedValue();
         int var17 = 0;
         Object var10005;
         if (!var14 && var16 != Composer.Companion.getEmpty()) {
            var10005 = var16;
         } else {
            Object var42 = null;
            Object var41 = null;
            Object var40 = null;
            boolean var39 = false;
            Modifier var38 = var10000;
            int var18 = 0;
            final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1456_0 = var6;
            Function0 var43 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponentKt.ActionRow_FHprtrg$lambda$3$lambda$2(recoveredExpressionCapture_1456_0));
            var10000 = var38;
            var10001 = var39;
            var10002 = var40;
            var10003 = var41;
            var10004 = var42;
            Object var19 = var43;
            var13.updateRememberedValue(var19);
            var10005 = var19;
         }

         Function0 var12 = (Function0)var10005;
         var44.endReplaceGroup();
         Modifier var59 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var12, 15, null
         );
         int var45 = 16;
         int var48 = 0;
         float var61 = Dp.recovered$95b3d103663bf060(var45);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var11 = PaddingKt.recovered$904403cea4c49d95(var59, var61, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         androidx.compose.ui.Alignment.Vertical var47 = Alignment.Companion.getCenterVertically();
         int $this$dp$ivxx = 16;
         int $i$f$getDpxx = 0;
         HorizontalOrVertical var50 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         short var52 = 432;
         int var54 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var44, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var55 = RowKt.rowMeasurePolicy(var50, var47, var44, 14 & var52 >> 3 | 112 & var52 >> 3);
         Modifier var56 = var11;
         MeasurePolicy var57 = var55;
         Composer var58 = var44;
         int var20 = 112 & var52 << 3;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var58, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var22 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var58, 0));
         CompositionLocalMap var23 = var58.getCurrentCompositionLocalMap();
         Modifier var24 = ComposedModifierKt.materializeModifier(var58, var56);
         Function0 var25 = ComposeUiNode.Companion.getConstructor();
         Composer var26 = var58;
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
         Updater.recovered$89682b199a142880(var29, var57, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var29, var23, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var29, var22, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var29, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var29, var24, ComposeUiNode.Companion.getSetModifier());
         int var31 = 14 & var27 >> 6;
         Composer var32 = var26;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var34 = 6 | 112 & var52 >> 6;
         Composer var35 = var32;
         RowScope var36 = RowScopeInstance.INSTANCE;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, -1712175310, "C121@4349L26,122@4410L39,120@4321L164,126@4519L25,128@4606L10,125@4494L140:FileUploadErrorComponent.kt#lphomb"
         );
         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(var1, var35, 14 & var10 >> 3),
            StringResources_androidKt.stringResource(var3, var35, 14 & var10 >> 9),
            null,
            var4,
            var35,
            Painter.$stable | 7168 & var10 >> 3,
            4
         );
         TextKt.recovered$5b114ab32a4f4190(
            StringResources_androidKt.stringResource(var2, var35, 14 & var10 >> 6),
            null,
            var4,
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
            IntercomTheme.INSTANCE.getTypography(var35, IntercomTheme.$stable).getType04(),
            var35,
            896 & var10 >> 6,
            0,
            131066
         );
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var32);
         var26.endNode();
         ComposerKt.sourceInformationMarkerEnd(var26);
         ComposerKt.sourceInformationMarkerEnd(var58);
         ComposerKt.sourceInformationMarkerEnd(var44);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var60 = var44.endRestartGroup();
      if (var60 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1457_0 = var0;
         final int recoveredExpressionCapture_1457_1 = var1;
         final int recoveredExpressionCapture_1457_2 = var2;
         final int recoveredExpressionCapture_1457_3 = var3;
         final long recoveredExpressionCapture_1457_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_1457_5 = var6;
         final int recoveredExpressionCapture_1457_6 = var8;
         final int recoveredExpressionCapture_1457_7 = var9;
         var60.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1457_0, recoveredExpressionArgument_1457_1) -> io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponentKt.ActionRow_FHprtrg$lambda$5(recoveredExpressionCapture_1457_0, recoveredExpressionCapture_1457_1, recoveredExpressionCapture_1457_2, recoveredExpressionCapture_1457_3, recoveredExpressionCapture_1457_4, recoveredExpressionCapture_1457_5, recoveredExpressionCapture_1457_6, recoveredExpressionCapture_1457_7, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1457_0), ((java.lang.Integer)recoveredExpressionArgument_1457_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ErrorActionSheetContentPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2121321299);
      ComposerKt.sourceInformation(var2, "C(ErrorActionSheetContentPreview)136@4725L327:FileUploadErrorComponent.kt#lphomb");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2121321299, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.ErrorActionSheetContentPreview (FileUploadErrorComponent.kt:135)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$FileUploadErrorComponentKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1453_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1453_0, recoveredExpressionArgument_1453_1) -> io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponentKt.ErrorActionSheetContentPreview$lambda$6(recoveredExpressionCapture_1453_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1453_0), ((java.lang.Integer)recoveredExpressionArgument_1453_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void UploadFailedErrorActionSheetPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(2130831888);
      ComposerKt.sourceInformation(var2, "C(UploadFailedErrorActionSheetPreview)151@5142L310:FileUploadErrorComponent.kt#lphomb");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2130831888,
               var1,
               -1,
               "io.intercom.android.sdk.survey.ui.questiontype.files.UploadFailedErrorActionSheetPreview (FileUploadErrorComponent.kt:150)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.ui.questiontype.files.ComposableSingletons$FileUploadErrorComponentKt.INSTANCE.recovered$54bdcf11cafa5012(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1455_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1455_0, recoveredExpressionArgument_1455_1) -> io.intercom.android.sdk.survey.ui.questiontype.files.FileUploadErrorComponentKt.UploadFailedErrorActionSheetPreview$lambda$7(recoveredExpressionCapture_1455_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1455_0), ((java.lang.Integer)recoveredExpressionArgument_1455_1).intValue())));
      }
   }

   private static final Unit FileUploadErrorComponent$lambda$1(
      String var0, io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadError var1, Function0 var2, Function0 var3, int var4, Composer var5, int var6
   ) {
      FileUploadErrorComponent(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ActionRow_FHprtrg$lambda$3$lambda$2(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ActionRow_FHprtrg$lambda$5(
      Modifier var0, int var1, int var2, int var3, long var4, Function0 var6, int var7, int var8, Composer var9, int var10
   ) {
      recovered$88dfabc3dc9b4383(var0, var1, var2, var3, var4, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit ErrorActionSheetContentPreview$lambda$6(int var0, Composer var1, int var2) {
      ErrorActionSheetContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UploadFailedErrorActionSheetPreview$lambda$7(int var0, Composer var1, int var2) {
      UploadFailedErrorActionSheetPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
