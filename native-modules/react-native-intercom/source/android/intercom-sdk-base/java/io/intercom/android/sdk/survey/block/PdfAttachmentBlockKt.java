package io.intercom.android.sdk.survey.block;

import android.content.Context;
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
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.SubcomposeAsyncImageKt;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.m5.preview.data.DownloadState;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity;
import io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$Companion;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a3\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0004\b\n\u0010\u000b\u001a+\u0010\f\u001a\u00020\u0001*\u00020\r2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0004\b\u000e\u0010\u000f\u001a9\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0003¢\u0006\u0004\b\u0019\u0010\u001a\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u001c¨\u0006\u001d",
   d2 = {
         "PdfAttachmentBlock",
         "",
         "blockAttachment",
         "Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;",
         "isAdmin",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "tintColor",
         "Landroidx/compose/ui/graphics/Color;",
         "PdfAttachmentBlock-ww6aTOc",
         "(Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;ZLandroidx/compose/ui/Modifier;JLandroidx/compose/runtime/Composer;II)V",
         "PdfDetails",
         "Landroidx/compose/foundation/layout/RowScope;",
         "PdfDetails-FNF3uiM",
         "(Landroidx/compose/foundation/layout/RowScope;Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;JZLandroidx/compose/runtime/Composer;I)V",
         "PdfThumbnail",
         "context",
         "Landroid/content/Context;",
         "cacheKey",
         "",
         "density",
         "Landroidx/compose/ui/unit/Density;",
         "pdfSize",
         "Landroidx/compose/ui/unit/Dp;",
         "PdfThumbnail-3xixttE",
         "(Landroid/content/Context;Ljava/lang/String;Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;Landroidx/compose/ui/unit/Density;FLandroidx/compose/runtime/Composer;I)V",
         "PdfAttachmentBlockPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPdfAttachmentBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PdfAttachmentBlock.kt\nio/intercom/android/sdk/survey/block/PdfAttachmentBlockKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 9 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,215:1\n118#2:216\n118#2:225\n118#2:226\n118#2:227\n118#2:228\n118#2:261\n118#2:262\n118#2:300\n75#3:217\n75#3:218\n1047#4,6:219\n99#5:229\n96#5,9:230\n106#5:266\n81#6,6:239\n88#6,6:254\n96#6:265\n81#6,6:273\n88#6,6:288\n96#6:297\n402#7,9:245\n411#7:260\n412#7,2:263\n402#7,9:279\n411#7,3:294\n87#8,6:267\n94#8:298\n1#9:299\n*S KotlinDebug\n*F\n+ 1 PdfAttachmentBlock.kt\nio/intercom/android/sdk/survey/block/PdfAttachmentBlockKt\n*L\n55#1:216\n91#1:225\n92#1:226\n93#1:227\n94#1:228\n99#1:261\n103#1:262\n160#1:300\n56#1:217\n57#1:218\n68#1:219,6\n64#1:229\n64#1:230,9\n64#1:266\n64#1:239,6\n64#1:254,6\n64#1:265\n115#1:273,6\n115#1:288,6\n115#1:297\n64#1:245,9\n64#1:260\n64#1:263,2\n115#1:279,9\n115#1:294,3\n115#1:267,6\n115#1:298\n*E\n"
)
public final class PdfAttachmentBlockKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$913feb9b639e321a(
      @NotNull BlockAttachment var0, boolean var1, @Nullable Modifier var2, long var3, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "blockAttachment");
      Composer var52 = var5.startRestartGroup(369048797);
      ComposerKt.sourceInformation(
         var52,
         "C(PdfAttachmentBlock)P(!,3:c#ui.graphics.Color)52@2328L6,55@2408L7,56@2447L7,57@2486L56,58@2567L49,59@2642L69,60@2737L45,67@3118L1005,63@2975L1793:PdfAttachmentBlock.kt#jxhu3x"
      );
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var52.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var52.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var52.changed(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var8 |= (var7 & 8) == 0 && var52.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var52.getSkipping()) {
         var52.skipToGroupEnd();
      } else {
         var52.startDefaults();
         if ((var6 & 1) != 0 && !var52.getDefaultsInvalid()) {
            var52.skipToGroupEnd();
            if ((var7 & 8) != 0) {
               var8 &= -7169;
            }
         } else {
            if ((var7 & 4) != 0) {
               var2 = Modifier.Companion;
            }

            if ((var7 & 8) != 0) {
               var3 = IntercomTheme.INSTANCE.getColors(var52, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var8 &= -7169;
            }
         }

         var52.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(369048797, var8, -1, "io.intercom.android.sdk.survey.block.PdfAttachmentBlock (PdfAttachmentBlock.kt:53)");
         }

         int var10 = 90;
         int var11 = 0;
         float var9 = Dp.recovered$95b3d103663bf060(var10);
         CompositionLocal var54 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var12 = 0;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var14 = var52.consume(var54);
         ComposerKt.sourceInformationMarkerEnd(var52);
         Context var53 = (Context)var14;
         CompositionLocal var56 = CompositionLocalsKt.getLocalDensity();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var15 = var52.consume(var56);
         ComposerKt.sourceInformationMarkerEnd(var52);
         Density var55 = (Density)var15;
         String var57 = StringResources_androidKt.stringResource(string.intercom_permission_denied, var52, 0);
         String var59 = StringResources_androidKt.stringResource(string.intercom_file_saved, var52, 0);
         String var61 = StringResources_androidKt.stringResource(string.intercom_something_went_wrong_try_again, var52, 0);
         String var62 = StringResources_androidKt.stringResource(string.intercom_saving, var52, 0);
         CharSequence var10000 = var0.getUrl();
         String[] var17 = new String[]{"?"};
         String var16 = (String)CollectionsKt.first(StringsKt.split$default(var10000, var17, false, 0, 6, null));
         Vertical var63 = Alignment.Companion.getCenterVertically();
         Modifier var91 = SizeKt.wrapContentSize$default(var2, null, false, 3, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var52.startReplaceGroup(888414534);
         ComposerKt.sourceInformation(var52, "CC(remember):PdfAttachmentBlock.kt#9igjgp");
         Composer var21 = var52;
         boolean var22 = var52.changedInstance(var53)
            | (var8 & 14) == 4
            | var52.changed(var62)
            | var52.changed(var59)
            | var52.changed(var61)
            | var52.changed(var57);
         int var23 = 0;
         Object var24 = var21.rememberedValue();
         int var25 = 0;
         Object var10005;
         if (!var22 && var24 != Composer.Companion.getEmpty()) {
            var10005 = var24;
         } else {
            Object var50 = null;
            Object var49 = null;
            Object var48 = null;
            boolean var47 = false;
            Modifier var46 = var91;
            int var26 = 0;
            final android.content.Context recoveredExpressionCapture_1277_0 = var53;
            final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1277_1 = var0;
            final java.lang.String recoveredExpressionCapture_1277_2 = var62;
            final java.lang.String recoveredExpressionCapture_1277_3 = var59;
            final java.lang.String recoveredExpressionCapture_1277_4 = var61;
            final java.lang.String recoveredExpressionCapture_1277_5 = var57;
            Function0 var51 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.block.PdfAttachmentBlockKt.PdfAttachmentBlock_ww6aTOc$lambda$1$lambda$0(recoveredExpressionCapture_1277_0, recoveredExpressionCapture_1277_1, recoveredExpressionCapture_1277_2, recoveredExpressionCapture_1277_3, recoveredExpressionCapture_1277_4, recoveredExpressionCapture_1277_5));
            var91 = var46;
            var10001 = var47;
            var10002 = var48;
            var10003 = var49;
            var10004 = var50;
            Object var27 = var51;
            var21.updateRememberedValue(var27);
            var10005 = var27;
         }

         Function0 var20 = (Function0)var10005;
         var52.endReplaceGroup();
         Modifier var19 = ClickableKt.recovered$1c9660c41fd850dc(
            var91, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var20, 15, null
         );
         float var92;
         if (var1) {
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var92 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            var92 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var65 = var92;
         float var93;
         if (var1) {
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            var93 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var93 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var69 = var93;
         int $this$dp$ivx = 4;
         int $i$f$getDpx = 0;
         float var75 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         int $this$dp$ivxx = 4;
         int $i$f$getDpxx = 0;
         float var80 = Dp.recovered$95b3d103663bf060($this$dp$ivxx);
         Modifier var18 = PaddingKt.recovered$f39cb96e94c47cf6(var19, var65, var75, var69, var80);
         short var66 = 384;
         int var70 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var52, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var64 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var76 = RowKt.rowMeasurePolicy(var64, var63, var52, 14 & var66 >> 3 | 112 & var66 >> 3);
         Modifier var81 = var18;
         MeasurePolicy var84 = var76;
         Composer var86 = var52;
         int var87 = 112 & var66 << 3;
         int var88 = 0;
         ComposerKt.sourceInformationMarkerStart(var86, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var28 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var86, 0));
         CompositionLocalMap var29 = var86.getCurrentCompositionLocalMap();
         Modifier var30 = ComposedModifierKt.materializeModifier(var86, var81);
         Function0 var31 = ComposeUiNode.Companion.getConstructor();
         Composer var32 = var86;
         int var33 = 6 | 896 & var87 << 6;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var32.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var32.startReusableNode();
         if (var32.getInserting()) {
            var32.createNode(var31);
         } else {
            var32.useNode();
         }

         Composer var35 = Updater.recovered$676aa17b44fd6280(var32);
         int var36 = 0;
         Updater.recovered$89682b199a142880(var35, var84, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var35, var29, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var35, var28, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var35, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var35, var30, ComposeUiNode.Companion.getSetModifier());
         int var37 = 14 & var33 >> 6;
         Composer var38 = var32;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var40 = 6 | 112 & var66 >> 6;
         Composer var41 = var38;
         RowScope var42 = RowScopeInstance.INSTANCE;
         int var43 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, 189346567, "C:PdfAttachmentBlock.kt#jxhu3x");
         if (var1) {
            var41.startReplaceGroup(189352642);
            ComposerKt.sourceInformation(var41, "97@4369L44,98@4426L40,99@4479L66");
            recovered$f4595526de413860(var42, var0, var3, true, var41, 3072 | 14 & var40 | 112 & var8 << 3 | 896 & var8 >> 3);
            Modifier var94 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var94, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var41, 6);
            recovered$9234c18688ae3590(var53, var16, var0, var55, var9, var41, 24576 | 896 & var8 << 6);
            var41.endReplaceGroup();
         } else {
            var41.startReplaceGroup(189557025);
            ComposerKt.sourceInformation(var41, "101@4575L66,102@4654L40,103@4707L45");
            recovered$9234c18688ae3590(var53, var16, var0, var55, var9, var41, 24576 | 896 & var8 << 6);
            Modifier var95 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var95, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var41, 6);
            recovered$f4595526de413860(var42, var0, var3, false, var41, 3072 | 14 & var40 | 112 & var8 << 3 | 896 & var8 >> 3);
            var41.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var41);
         ComposerKt.sourceInformationMarkerEnd(var38);
         var32.endNode();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var86);
         ComposerKt.sourceInformationMarkerEnd(var52);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var96 = var52.endRestartGroup();
      if (var96 != null) {
         final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1276_0 = var0;
         final boolean recoveredExpressionCapture_1276_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1276_2 = var2;
         final long recoveredExpressionCapture_1276_3 = var3;
         final int recoveredExpressionCapture_1276_4 = var6;
         final int recoveredExpressionCapture_1276_5 = var7;
         var96.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1276_0, recoveredExpressionArgument_1276_1) -> io.intercom.android.sdk.survey.block.PdfAttachmentBlockKt.PdfAttachmentBlock_ww6aTOc$lambda$3(recoveredExpressionCapture_1276_0, recoveredExpressionCapture_1276_1, recoveredExpressionCapture_1276_2, recoveredExpressionCapture_1276_3, recoveredExpressionCapture_1276_4, recoveredExpressionCapture_1276_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1276_0), ((java.lang.Integer)recoveredExpressionArgument_1276_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$f4595526de413860(RowScope var0, BlockAttachment var1, long var2, boolean var4, Composer var5, int var6) {
      Composer var38 = var5.startRestartGroup(-1205911716);
      ComposerKt.sourceInformation(var38, "C(PdfDetails)P(!1,2:c#ui.graphics.Color)114@4907L655:PdfAttachmentBlock.kt#jxhu3x");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var38.changed(var0) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var38.changed(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var38.changed(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var38.changed(var4) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var38.getSkipping()) {
         var38.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1205911716, var7, -1, "io.intercom.android.sdk.survey.block.PdfDetails (PdfAttachmentBlock.kt:113)");
         }

         Modifier var8 = var0.weight(Modifier.Companion, 1.0F, false);
         androidx.compose.ui.Alignment.Horizontal var9 = var4 ? Alignment.Companion.getStart() : Alignment.Companion.getEnd();
         HorizontalOrVertical var10 = Arrangement.INSTANCE.getCenter();
         byte var11 = 48;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var38, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var13 = ColumnKt.columnMeasurePolicy(var10, var9, var38, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var8;
         MeasurePolicy var15 = var13;
         Composer var16 = var38;
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
         ComposerKt.sourceInformationMarkerStart(var32, 182278681, "C122@5226L10,119@5114L212,129@5456L10,126@5335L221:PdfAttachmentBlock.kt#jxhu3x");
         String var35 = var1.getName();
         TextStyle var36 = IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType04();
         int var37 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var35, null, var2, null, 0L, null, null, null, 0L, null, null, 0L, var37, false, 1, 0, null, var36, var32, 896 & var7, 24960, 110586
         );
         String var39 = var1.getHumanFileSize();
         TextStyle var40 = IntercomTheme.INSTANCE.getTypography(var32, IntercomTheme.$stable).getType05();
         int var41 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var39, null, var2, null, 0L, null, null, null, 0L, null, null, 0L, var41, false, 1, 0, null, var40, var32, 896 & var7, 24960, 110586
         );
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var38);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var38.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.foundation.layout.RowScope recoveredExpressionCapture_1279_0 = var0;
         final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1279_1 = var1;
         final long recoveredExpressionCapture_1279_2 = var2;
         final boolean recoveredExpressionCapture_1279_3 = var4;
         final int recoveredExpressionCapture_1279_4 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1279_0, recoveredExpressionArgument_1279_1) -> io.intercom.android.sdk.survey.block.PdfAttachmentBlockKt.PdfDetails_FNF3uiM$lambda$5(recoveredExpressionCapture_1279_0, recoveredExpressionCapture_1279_1, recoveredExpressionCapture_1279_2, recoveredExpressionCapture_1279_3, recoveredExpressionCapture_1279_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1279_0), ((java.lang.Integer)recoveredExpressionArgument_1279_1).intValue())));
      }
   }

   @Composable
   private static final void recovered$9234c18688ae3590(Context var0, String var1, BlockAttachment var2, Density var3, float var4, Composer var5, int var6) {
      Composer var17 = var5.startRestartGroup(1296049859);
      ComposerKt.sourceInformation(var17, "C(PdfThumbnail)P(2,1!,4:c#ui.unit.Dp)144@5734L1543:PdfAttachmentBlock.kt#jxhu3x");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var17.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var17.changed(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var17.changed(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var17.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 24576) == 0) {
         var7 |= var17.changed(var4) ? 16384 : 8192;
      }

      if ((var7 & 9363) == 9362 && var17.getSkipping()) {
         var17.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1296049859, var7, -1, "io.intercom.android.sdk.survey.block.PdfThumbnail (PdfAttachmentBlock.kt:143)");
         }

         Builder var10000 = new Builder(var0).memoryCacheKey(var1).diskCacheKey(var1).data(var2.getUrl());
         Density var9 = var3;
         Builder var14 = var10000;
         int var10 = 0;
         int var15 = (int)var9.recovered$245d7c913f591981(var4);
         Density var18 = var3;
         int var24 = var15;
         Builder var23 = var14;
         int var20 = 0;
         int var16 = (int)var18.recovered$245d7c913f591981(var4);
         ImageRequest var8 = var23.size(var24, var16).crossfade(true).error(drawable.intercom_image_load_failed).build();
         ImageLoader var19 = IntercomImageLoaderKt.getImageLoader(var0);
         String var21 = var2.getName();
         Modifier var25 = Modifier.Companion;
         int var12 = 5;
         int var13 = 0;
         Modifier var11 = SizeKt.recovered$111ef94c93e78c04(
            ClipKt.clip(var25, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var12))), var4
         );
         ContentScale var22 = ContentScale.Companion.getCrop();
         SubcomposeAsyncImageKt.recovered$9e62f4c19284676e(
            var8,
            var21,
            var19,
            var11,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$PdfAttachmentBlockKt.INSTANCE.recovered$241e30c48109aa79(),
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$PdfAttachmentBlockKt.INSTANCE.recovered$a9084f3092f5d576(),
            null,
            null,
            null,
            null,
            var22,
            0.0F,
            null,
            0,
            false,
            null,
            var17,
            12779520,
            384,
            257872
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var26 = var17.endRestartGroup();
      if (var26 != null) {
         final android.content.Context recoveredExpressionCapture_1278_0 = var0;
         final java.lang.String recoveredExpressionCapture_1278_1 = var1;
         final io.intercom.android.sdk.blocks.lib.models.BlockAttachment recoveredExpressionCapture_1278_2 = var2;
         final androidx.compose.ui.unit.Density recoveredExpressionCapture_1278_3 = var3;
         final float recoveredExpressionCapture_1278_4 = var4;
         final int recoveredExpressionCapture_1278_5 = var6;
         var26.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1278_0, recoveredExpressionArgument_1278_1) -> io.intercom.android.sdk.survey.block.PdfAttachmentBlockKt.PdfThumbnail_3xixttE$lambda$8(recoveredExpressionCapture_1278_0, recoveredExpressionCapture_1278_1, recoveredExpressionCapture_1278_2, recoveredExpressionCapture_1278_3, recoveredExpressionCapture_1278_4, recoveredExpressionCapture_1278_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1278_0), ((java.lang.Integer)recoveredExpressionArgument_1278_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PdfAttachmentBlockPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1883421095);
      ComposerKt.sourceInformation(var2, "C(PdfAttachmentBlockPreview)192@7357L732:PdfAttachmentBlock.kt#jxhu3x");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1883421095, var1, -1, "io.intercom.android.sdk.survey.block.PdfAttachmentBlockPreview (PdfAttachmentBlock.kt:191)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.survey.block.ComposableSingletons$PdfAttachmentBlockKt.INSTANCE.recovered$54bdcf11cafa5012(),
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
         final int recoveredExpressionCapture_1275_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1275_0, recoveredExpressionArgument_1275_1) -> io.intercom.android.sdk.survey.block.PdfAttachmentBlockKt.PdfAttachmentBlockPreview$lambda$9(recoveredExpressionCapture_1275_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1275_0), ((java.lang.Integer)recoveredExpressionArgument_1275_1).intValue())));
      }
   }

   private static final Unit PdfAttachmentBlock_ww6aTOc$lambda$1$lambda$0(
      Context var0, BlockAttachment var1, String var2, String var3, String var4, String var5
   ) {
      io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$Companion var10001 = IntercomPreviewActivity.Companion;
      List var10005 = CollectionsKt.listOfNotNull(IntercomPreviewFile.Companion.network(var1.getUrl(), "application/pdf"));
      boolean var6 = var1.getUrl().length() > 0;
      var0.startActivity(
         var10001.createIntent(var0, new IntercomPreviewArgs(var10005, null, null, false, 0, new DownloadState(var6, var2, var3, var4, var5), 30, null))
      );
      return Unit.INSTANCE;
   }

   private static final Unit PdfAttachmentBlock_ww6aTOc$lambda$3(
      BlockAttachment var0, boolean var1, Modifier var2, long var3, int var5, int var6, Composer var7, int var8
   ) {
      recovered$913feb9b639e321a(var0, var1, var2, var3, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit PdfDetails_FNF3uiM$lambda$5(RowScope var0, BlockAttachment var1, long var2, boolean var4, int var5, Composer var6, int var7) {
      recovered$f4595526de413860(var0, var1, var2, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PdfThumbnail_3xixttE$lambda$8(
      Context var0, String var1, BlockAttachment var2, Density var3, float var4, int var5, Composer var6, int var7
   ) {
      recovered$9234c18688ae3590(var0, var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PdfAttachmentBlockPreview$lambda$9(int var0, Composer var1, int var2) {
      PdfAttachmentBlockPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
