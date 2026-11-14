package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
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
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
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
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\u001aT\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u001b\b\u0002\u0010\n\u001a\u0015\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u000b¢\u0006\u0002\b\rH\u0001¢\u0006\u0002\u0010\u000e\u001a\u001d\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015¨\u0006\u0016",
   d2 = {
         "FileRow",
         "",
         "item",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Other;",
         "cornerShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onClick",
         "Lkotlin/Function0;",
         "customIconContent",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FileStyle;",
         "Landroidx/compose/runtime/Composable;",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media$Other;Landroidx/compose/foundation/shape/RoundedCornerShape;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "getFileStyle",
         "mimeType",
         "",
         "name",
         "(Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;I)Lio/intercom/android/sdk/m5/conversation/ui/components/row/FileStyle;",
         "FileRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFileRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FileRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,211:1\n1047#2,6:212\n118#3:218\n118#3:219\n118#3:220\n118#3:221\n118#3:250\n118#3:251\n118#3:283\n118#3:288\n118#3:289\n99#4,6:222\n106#4:330\n81#5,6:228\n88#5,6:243\n81#5,6:261\n88#5,6:276\n96#5:286\n81#5,6:301\n88#5,6:316\n96#5:325\n96#5:329\n402#6,9:234\n411#6:249\n402#6,9:267\n411#6:282\n412#6,2:284\n402#6,9:307\n411#6,3:322\n412#6,2:327\n70#7:252\n68#7,8:253\n77#7:287\n87#8:290\n83#8,10:291\n94#8:326\n*S KotlinDebug\n*F\n+ 1 FileRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FileRowKt\n*L\n44#1:212,6\n55#1:218\n56#1:219\n57#1:220\n58#1:221\n65#1:250\n68#1:251\n77#1:283\n83#1:288\n85#1:289\n48#1:222,6\n48#1:330\n48#1:228,6\n48#1:243,6\n63#1:261,6\n63#1:276,6\n63#1:286\n84#1:301,6\n84#1:316,6\n84#1:325\n48#1:329\n48#1:234,9\n48#1:249\n63#1:267,9\n63#1:282\n63#1:284,2\n84#1:307,9\n84#1:322,3\n48#1:327,2\n63#1:252\n63#1:253,8\n63#1:287\n84#1:290\n84#1:291,10\n84#1:326\n*E\n"
)
public final class FileRowKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void FileRow(
      @NotNull MediaData$Media$Other var0,
      @NotNull RoundedCornerShape var1,
      @Nullable Modifier var2,
      @Nullable Function0 var3,
      @Nullable Function3 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "item");
      Intrinsics.checkNotNullParameter(var1, "cornerShape");
      var5 = var5.startRestartGroup(-787364961);
      ComposerKt.sourceInformation(var5, "C(FileRow)P(2!1,3,4)43@1850L2,46@1944L42,51@2129L6,47@1991L1855:FileRow.kt#hr3bi5");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var7 & 8) != 0) {
            var5.startReplaceGroup(1879378485);
            ComposerKt.sourceInformation(var5, "CC(remember):FileRow.kt#9igjgp");
            Composer var10 = var5;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var88 = ((kotlin.jvm.functions.Function0) () -> FileRowKt.FileRow$lambda$1$lambda$0());
               var10.updateRememberedValue(var88);
               var10000 = var88;
            } else {
               var10000 = var13;
            }

            Function0 var9 = (Function0)var10000;
            var5.endReplaceGroup();
            var3 = var9;
         }

         if ((var7 & 16) != 0) {
            var4 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-787364961, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FileRow (FileRow.kt:45)");
         }

         FileStyle var75 = getFileStyle(var0.getMimeType(), var0.getFileName(), var5, 0);
         Modifier var77 = ClickableKt.recovered$1c9660c41fd850dc(
            BackgroundKt.recovered$42a3174180b3fb7a(
               ClipKt.clip(SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null), var1),
               IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).getContainer().recovered$e2b74d614aa09788(),
               null,
               2,
               null
            ),
            false,
            null,
            null,
            null,
            var3,
            15,
            null
         );
         int var81 = 12;
         int var84 = 0;
         float var79 = Dp.recovered$95b3d103663bf060(var81);
         var84 = 16;
         int $i$f$getDpx = 0;
         float var82 = Dp.recovered$95b3d103663bf060(var84);
         $i$f$getDpx = 12;
         int $i$f$getDpxx = 0;
         float var86 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
         $i$f$getDpxx = 12;
         int $i$f$getDpxxx = 0;
         float var91 = Dp.recovered$95b3d103663bf060($i$f$getDpxx);
         Modifier var76 = PaddingKt.recovered$f39cb96e94c47cf6(var77, var79, var86, var82, var91);
         Vertical var78 = Alignment.Companion.getCenterVertically();
         Horizontal var80 = Arrangement.INSTANCE.getStart();
         short var83 = 432;
         var84 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var92 = RowKt.rowMeasurePolicy(var80, var78, var5, 14 & var83 >> 3 | 112 & var83 >> 3);
         Modifier var94 = var76;
         MeasurePolicy var95 = var92;
         Composer var18 = var5;
         int var19 = 112 & var83 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var94);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var95, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var83 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, -399596289, "C62@2478L675,82@3162L39,83@3210L630:FileRow.kt#hr3bi5");
         Modifier var136 = Modifier.Companion;
         int $this$dp$ivx = 40;
         int $i$f$getDpxxxx = 0;
         var136 = SizeKt.recovered$111ef94c93e78c04(var136, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         long var10001 = var75.recovered$934f6183e8132565();
         $this$dp$ivx = 10;
         $i$f$getDpxxxx = 0;
         Modifier var97 = BackgroundKt.recovered$60bc47fb440df483(
            var136, var10001, RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060($this$dp$ivx))
         );
         Alignment var101 = Alignment.Companion.getCenter();
         byte var39 = 48;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var41 = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var101, var41);
         Modifier var43 = var97;
         MeasurePolicy var44 = measurePolicy$ivx;
         Composer var45 = var34;
         int var46 = 112 & var39 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var45, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var45, 0));
         CompositionLocalMap localMap$iv$ivx = var45.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var45, var43);
         Function0 var51 = ComposeUiNode.Companion.getConstructor();
         Composer var52 = var45;
         int var53 = 6 | 896 & var46 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var52, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var52.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var52.startReusableNode();
         if (var52.getInserting()) {
            var52.createNode(var51);
         } else {
            var52.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var52);
         int var56/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var44, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var57 = 14 & var53 >> 6;
         Composer $composer$ivx = var52;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var60 = 6 | 112 & var39 >> 6;
         Composer var61 = $composer$ivx;
         BoxScope var62 = BoxScopeInstance.INSTANCE;
         int var63 = 0;
         ComposerKt.sourceInformationMarkerStart(var61, 1527604322, "C:FileRow.kt#hr3bi5");
         if (var4 == null) {
            var61.startReplaceGroup(1527632066);
            ComposerKt.sourceInformation(var61, "73@2860L36,72@2824L239");
            Painter var64 = PainterResources_androidKt.painterResource(var75.getIcon(), var61, 0);
            long var65 = var75.recovered$01833f34228693e4();
            var136 = Modifier.Companion;
            int $this$dp$ivxx = 20;
            int $i$f$getDpxxxxx = 0;
            Modifier var69 = SizeKt.recovered$111ef94c93e78c04(var136, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
            IconKt.recovered$e00adffb7fe45b02(var64, null, var69, var65, var61, 432 | Painter.$stable, 0);
            var61.endReplaceGroup();
         } else {
            var61.startReplaceGroup(1527900309);
            ComposerKt.sourceInformation(var61, "79@3101L28");
            var4.invoke(var75, var61, 112 & var8 >> 9);
            var61.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var61);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var52.endNode();
         ComposerKt.sourceInformationMarkerEnd(var52);
         ComposerKt.sourceInformationMarkerEnd(var45);
         ComposerKt.sourceInformationMarkerEnd(var34);
         var136 = Modifier.Companion;
         $this$dp$ivx = 8;
         $i$f$getDpxxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var136, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var34, 6);
         $i$f$getDpxxxx = 2;
         var41 = false;
         androidx.compose.foundation.layout.Arrangement.Vertical var104 = Arrangement.INSTANCE
            .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($i$f$getDpxxxx));
         var39 = 48;
         var40 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var34, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var99 = Modifier.Companion;
         androidx.compose.ui.Alignment.Horizontal var108 = Alignment.Companion.getStart();
         measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var104, var108, var34, 14 & var39 >> 3 | 112 & var39 >> 3);
         var43 = var99;
         var44 = measurePolicy$ivx;
         var45 = var34;
         var46 = 112 & var39 << 3;
         $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var45, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var45, 0));
         localMap$iv$ivx = var45.getCurrentCompositionLocalMap();
         materialized$iv$ivx = ComposedModifierKt.materializeModifier(var45, var43);
         var51 = ComposeUiNode.Companion.getConstructor();
         var52 = var45;
         var53 = 6 | 896 & var46 << 6;
         $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var52, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var52.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var52.startReusableNode();
         if (var52.getInserting()) {
            var52.createNode(var51);
         } else {
            var52.useNode();
         }

         $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var52);
         var56/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var44, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         var57 = 14 & var53 >> 6;
         $composer$ivx = var52;
         var59 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         var60 = 6 | 112 & var39 >> 6;
         var61 = $composer$ivx;
         ColumnScope var129 = ColumnScopeInstance.INSTANCE;
         var63 = 0;
         ComposerKt.sourceInformationMarkerStart(var61, 1528133988, "C88@3386L10,91@3531L6,86@3304L261,95@3661L10,98@3798L6,93@3578L252:FileRow.kt#hr3bi5");
         String var131 = var0.getFileName();
         TextStyle var70 = IntercomTheme.INSTANCE.getTypography(var61, IntercomTheme.$stable).getType04SemiBold();
         int var71 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         long var72 = IntercomTheme.INSTANCE.getColors(var61, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextKt.recovered$5b114ab32a4f4190(
            var131, null, var72, null, 0L, null, null, null, 0L, null, null, 0L, var71, false, 1, 0, null, var70, var61, 0, 24960, 110586
         );
         var131 = var75.getType();
         var70 = IntercomTheme.INSTANCE.getTypography(var61, IntercomTheme.$stable).getType04();
         var71 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         var72 = IntercomTheme.INSTANCE.getColors(var61, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextKt.recovered$5b114ab32a4f4190(
            var131, null, var72, null, 0L, null, null, null, 0L, null, null, 0L, var71, false, 1, 0, null, var70, var61, 0, 24960, 110586
         );
         ComposerKt.sourceInformationMarkerEnd(var61);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var52.endNode();
         ComposerKt.sourceInformationMarkerEnd(var52);
         ComposerKt.sourceInformationMarkerEnd(var45);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var140 = var5.endRestartGroup();
      if (var140 != null) {
         final io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other recoveredCapture_710_0 = var0;
         final androidx.compose.foundation.shape.RoundedCornerShape recoveredCapture_710_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_710_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_710_3 = var3;
         final kotlin.jvm.functions.Function3 recoveredCapture_710_4 = var4;
         final int recoveredCapture_710_5 = var6;
         final int recoveredCapture_710_6 = var7;
         var140.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_710_0, recoveredArgument_710_1) -> FileRowKt.FileRow$lambda$5(recoveredCapture_710_0, recoveredCapture_710_1, recoveredCapture_710_2, recoveredCapture_710_3, recoveredCapture_710_4, recoveredCapture_710_5, recoveredCapture_710_6, (androidx.compose.runtime.Composer)recoveredArgument_710_0, ((java.lang.Integer)recoveredArgument_710_1).intValue())));
      }
   }

   @Composable
   private static final FileStyle getFileStyle(String var0, String var1, Composer var2, int var3) {
      var2.startReplaceGroup(2000929547);
      ComposerKt.sourceInformation(var2, "C(getFileStyle):FileRow.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(2000929547, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.getFileStyle (FileRow.kt:117)");
      }

      FileStyle var10000;
      if (ContentTypeExtensionKt.isPdf(var0) || StringsKt.endsWith(var1, ".pdf", true)) {
         var2.startReplaceGroup(180161046);
         ComposerKt.sourceInformation(var2, "120@4456L6,121@4512L6");
         FileStyle var9 = new FileStyle(
            drawable.intercom_ic_file,
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$176187de5f63aca7(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getIcon().recovered$0ec5fde925363ce1(),
            "PDF",
            null
         );
         var2.endReplaceGroup();
         var10000 = var9;
      } else if (ContentTypeExtensionKt.isCsv(var0) || StringsKt.endsWith(var1, ".csv", true)) {
         var2.startReplaceGroup(180169815);
         ComposerKt.sourceInformation(var2, "127@4729L6,128@4787L6");
         FileStyle var8 = new FileStyle(
            drawable.intercom_ic_csv,
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$06f5b36abee3ec99(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getIcon().recovered$0ec5fde925363ce1(),
            "CSV",
            null
         );
         var2.endReplaceGroup();
         var10000 = var8;
      } else if (ContentTypeExtensionKt.isZip(var0) || StringsKt.endsWith(var1, ".zip", true)) {
         var2.startReplaceGroup(180178614);
         ComposerKt.sourceInformation(var2, "134@5004L6,135@5061L6");
         FileStyle var7 = new FileStyle(
            drawable.intercom_ic_zip,
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$ff90a8751967f2f7(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getIcon().recovered$0ec5fde925363ce1(),
            "ZIP",
            null
         );
         var2.endReplaceGroup();
         var10000 = var7;
      } else if (!ContentTypeExtensionKt.isTxt(var0) && !StringsKt.endsWith(var1, ".txt", true)) {
         var2.startReplaceGroup(180194423);
         ComposerKt.sourceInformation(var2, "148@5498L6,149@5554L6,150@5593L38");
         FileStyle var6 = new FileStyle(
            drawable.intercom_ic_file,
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$176187de5f63aca7(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getIcon().recovered$0ec5fde925363ce1(),
            StringResources_androidKt.stringResource(string.intercom_file, var2, 0),
            null
         );
         var2.endReplaceGroup();
         var10000 = var6;
      } else {
         var2.startReplaceGroup(180187383);
         ComposerKt.sourceInformation(var2, "141@5278L6,142@5336L6");
         FileStyle var5 = new FileStyle(
            drawable.intercom_ic_txt,
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getContainer().recovered$235b30c090a399c5(),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getIcon().recovered$0ec5fde925363ce1(),
            "TXT",
            null
         );
         var2.endReplaceGroup();
         var10000 = var5;
      }

      FileStyle var4 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var4;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(486972875);
      ComposerKt.sourceInformation(var0, "C(FileRowPreview)157@5708L1916:FileRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(486972875, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FileRowPreview (FileRow.kt:156)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FileRowKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_712_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_712_0, recoveredArgument_712_1) -> FileRowKt.FileRowPreview$lambda$6(recoveredCapture_712_0, (androidx.compose.runtime.Composer)recoveredArgument_712_0, ((java.lang.Integer)recoveredArgument_712_1).intValue())));
      }
   }

   private static final Unit FileRow$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit FileRow$lambda$5(
      MediaData$Media$Other var0, RoundedCornerShape var1, Modifier var2, Function0 var3, Function3 var4, int var5, int var6, Composer var7, int var8
   ) {
      FileRow(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit FileRowPreview$lambda$6(int var0, Composer var1, int var2) {
      FileRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
