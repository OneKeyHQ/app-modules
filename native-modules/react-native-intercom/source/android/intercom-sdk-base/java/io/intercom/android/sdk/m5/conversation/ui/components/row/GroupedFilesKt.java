package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.net.Uri;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.foundation.shape.RoundedCornerShape;
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
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\u001aI\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u001a\b\u0002\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000e¨\u0006\u0011",
   d2 = {
         "GroupedFiles",
         "",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "cornerShape",
         "Landroidx/compose/foundation/shape/RoundedCornerShape;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onFileClick",
         "Lkotlin/Function2;",
         "",
         "(Ljava/util/List;Landroidx/compose/foundation/shape/RoundedCornerShape;Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "GroupedFilesWith1FilePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "GroupedFilesWith2FilesPreview",
         "GroupedFilesWith3FilesPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nGroupedFiles.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GroupedFiles.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/GroupedFilesKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Uri.kt\nandroidx/core/net/UriKt\n+ 5 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,103:1\n1047#2,6:104\n1047#2,6:151\n1374#3:110\n1460#3,2:111\n1563#3:113\n1634#3,2:114\n1636#3:117\n1462#3,3:118\n1878#3,2:149\n1880#3:157\n29#4:116\n87#5,6:121\n94#5:161\n81#6,6:127\n88#6,6:142\n96#6:160\n402#7,9:133\n411#7:148\n412#7,2:158\n*S KotlinDebug\n*F\n+ 1 GroupedFiles.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/GroupedFilesKt\n*L\n24#1:104,6\n57#1:151,6\n27#1:110\n27#1:111,2\n28#1:113\n28#1:114,2\n28#1:117\n27#1:118,3\n47#1:149,2\n47#1:157\n34#1:116\n42#1:121,6\n42#1:161\n42#1:127,6\n42#1:142,6\n42#1:160\n42#1:133,9\n42#1:148\n42#1:158,2\n*E\n"
)
public final class GroupedFilesKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedFiles(
      @NotNull List var0, @NotNull RoundedCornerShape var1, @Nullable Modifier var2, @Nullable Function2 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "blocks");
      Intrinsics.checkNotNullParameter(var1, "cornerShape");
      var4 = var4.startRestartGroup(-327530751);
      ComposerKt.sourceInformation(var4, "C(GroupedFiles)23@989L11,41@1548L681:GroupedFiles.kt#hr3bi5");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var6 & 8) != 0) {
            var4.startReplaceGroup(-1874886513);
            ComposerKt.sourceInformation(var4, "CC(remember):GroupedFiles.kt#9igjgp");
            Composer var9 = var4;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var69 = ((kotlin.jvm.functions.Function2) (recoveredArgument_736_0, recoveredArgument_736_1) -> GroupedFilesKt.GroupedFiles$lambda$1$lambda$0((java.lang.String)recoveredArgument_736_0, (java.lang.String)recoveredArgument_736_1));
               var9.updateRememberedValue(var69);
               var10000 = var69;
            } else {
               var10000 = var12;
            }

            Function2 var8 = (Function2)var10000;
            var4.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-327530751, var7, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedFiles (GroupedFiles.kt:24)");
         }

         Iterable var59 = var0;
         int var61 = 0;
         Iterable var63 = var59;
         ArrayList var65 = new ArrayList();
         int var67 = 0;

         for (Object var15 : var63) {
            Block var16 = (Block)var15;
            int var17 = 0;
            List var89 = var16.getAttachments();
            Intrinsics.checkNotNullExpressionValue(var89, "getAttachments(...)");
            Iterable var18 = var89;
            int var19 = 0;
            Iterable var20 = var18;
            var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var18, 10));
            int var22 = 0;

            for (Object var24 : var20) {
               BlockAttachment var25 = (BlockAttachment)var24;
               Collection var26 = destination$iv$ivx;
               int var27 = 0;
               MediaData$Media$Other var90;
               String var10002 = var25.getContentType();
               long var10003 = var25.getSize();
               String var10004 = var25.getName();
               Uri var10005;
               if (var25.getUrl().length() > 0) {
                  String var28 = var25.getUrl();
                  int var29 = 0;
                  var10005 = Uri.parse(var28);
               } else {
                  var10005 = Uri.EMPTY;
               }

               Uri var30 = var10005;
               Intrinsics.checkNotNull(var30);
               var90 = new MediaData$Media$Other(var10002, var10003, var10004, var30);
               var26.add(var90);
            }

            var var73 = (Iterable & List)destination$iv$ivx;
            CollectionsKt.addAll(var65, var73);
         }

         List var58 = (List)var65;
         HorizontalOrVertical var60 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(StreamingMessageListKt.getGroupedMessageGap());
         Horizontal var62 = Alignment.Companion.getEnd();
         int var64 = 432 | 14 & var7 >> 6;
         int var66 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var68 = ColumnKt.columnMeasurePolicy(var60, var62, var4, 14 & var64 >> 3 | 112 & var64 >> 3);
         Modifier var71 = var2;
         MeasurePolicy var72 = var68;
         Composer var74 = var4;
         int var75 = 112 & var64 << 3;
         int var76 = 0;
         ComposerKt.sourceInformationMarkerStart(var74, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var77 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var74, 0));
         CompositionLocalMap var78 = var74.getCurrentCompositionLocalMap();
         Modifier var79 = ComposedModifierKt.materializeModifier(var74, var71);
         Function0 var80 = ComposeUiNode.Companion.getConstructor();
         Composer var81 = var74;
         int var82 = 6 | 896 & var75 << 6;
         int var83 = 0;
         ComposerKt.sourceInformationMarkerStart(var81, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var81.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var81.startReusableNode();
         if (var81.getInserting()) {
            var81.createNode(var80);
         } else {
            var81.useNode();
         }

         Composer var84 = Updater.recovered$676aa17b44fd6280(var81);
         int var85 = 0;
         Updater.recovered$89682b199a142880(var84, var72, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var84, var78, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var84, var77, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var84, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var84, var79, ComposeUiNode.Companion.getSetModifier());
         int var86 = 14 & var82 >> 6;
         Composer var87 = var81;
         int var88 = 0;
         ComposerKt.sourceInformationMarkerStart(var87, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var31 = 6 | 112 & var64 >> 6;
         Composer var32 = var87;
         ColumnScope var33 = ColumnScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 103302694, "C:GroupedFiles.kt#hr3bi5");
         var32.startReplaceGroup(141880142);
         ComposerKt.sourceInformation(var32, "*56@2099L99,53@1982L231");
         Iterable var35 = var58;
         int var36 = 0;
         int var37 = 0;

         for (Object var39 : var35) {
            int var40 = var37++;
            if (var40 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            MediaData$Media$Other var41 = (MediaData$Media$Other)var39;
            int var42 = var40;
            int var43 = 0;
            RoundedCornerShape var44 = BubbleMessageRowKt.recovered$64fcaea88d5ad8c0(var1, var42, var58.size(), 0.0F, 8, null);
            MediaData$Media$Other var92 = var41;
            RoundedCornerShape var10001 = var44;
            Object var94 = null;
            var32.startReplaceGroup(250944805);
            ComposerKt.sourceInformation(var32, "CC(remember):GroupedFiles.kt#9igjgp");
            Composer var45 = var32;
            boolean var46 = (var7 & 7168) == 2048 | var4.changedInstance(var41);
            int var47 = 0;
            Object var48 = var45.rememberedValue();
            int var49 = 0;
            Object var95;
            if (!var46 && var48 != Composer.Companion.getEmpty()) {
               var95 = var48;
            } else {
               Object var50 = null;
               RoundedCornerShape var51 = var44;
               MediaData$Media$Other var52 = var41;
               int var53 = 0;
               final kotlin.jvm.functions.Function2 recoveredCapture_737_0 = var3;
               final io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other recoveredCapture_737_1 = var41;
               Function0 var54 = ((kotlin.jvm.functions.Function0) () -> GroupedFilesKt.GroupedFiles$lambda$7$lambda$6$lambda$5$lambda$4(recoveredCapture_737_0, recoveredCapture_737_1));
               var92 = var52;
               var10001 = var51;
               var94 = var50;
               Object var55 = var54;
               var45.updateRememberedValue(var55);
               var95 = var55;
            }

            Function0 var56 = (Function0)var95;
            var32.endReplaceGroup();
            FileRowKt.FileRow(var92, var10001, (Modifier)var94, var56, null, var32, 0, 20);
         }

         var32.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var87);
         var81.endNode();
         ComposerKt.sourceInformationMarkerEnd(var81);
         ComposerKt.sourceInformationMarkerEnd(var74);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var93 = var4.endRestartGroup();
      if (var93 != null) {
         final java.util.List recoveredCapture_738_0 = var0;
         final androidx.compose.foundation.shape.RoundedCornerShape recoveredCapture_738_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_738_2 = var2;
         final kotlin.jvm.functions.Function2 recoveredCapture_738_3 = var3;
         final int recoveredCapture_738_4 = var5;
         final int recoveredCapture_738_5 = var6;
         var93.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_738_0, recoveredArgument_738_1) -> GroupedFilesKt.GroupedFiles$lambda$8(recoveredCapture_738_0, recoveredCapture_738_1, recoveredCapture_738_2, recoveredCapture_738_3, recoveredCapture_738_4, recoveredCapture_738_5, (androidx.compose.runtime.Composer)recoveredArgument_738_0, ((java.lang.Integer)recoveredArgument_738_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedFilesWith1FilePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-751027684);
      ComposerKt.sourceInformation(var0, "C(GroupedFilesWith1FilePreview)67@2313L229:GroupedFiles.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -751027684, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedFilesWith1FilePreview (GroupedFiles.kt:66)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedFilesKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_739_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_739_0, recoveredArgument_739_1) -> GroupedFilesKt.GroupedFilesWith1FilePreview$lambda$9(recoveredCapture_739_0, (androidx.compose.runtime.Composer)recoveredArgument_739_0, ((java.lang.Integer)recoveredArgument_739_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedFilesWith2FilesPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-969404946);
      ComposerKt.sourceInformation(var0, "C(GroupedFilesWith2FilesPreview)80@2627L247:GroupedFiles.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -969404946, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedFilesWith2FilesPreview (GroupedFiles.kt:79)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedFilesKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_740_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_740_0, recoveredArgument_740_1) -> GroupedFilesKt.GroupedFilesWith2FilesPreview$lambda$10(recoveredCapture_740_0, (androidx.compose.runtime.Composer)recoveredArgument_740_0, ((java.lang.Integer)recoveredArgument_740_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void GroupedFilesWith3FilesPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1902759283);
      ComposerKt.sourceInformation(var0, "C(GroupedFilesWith3FilesPreview)93@2959L275:GroupedFiles.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1902759283, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.GroupedFilesWith3FilesPreview (GroupedFiles.kt:92)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$GroupedFilesKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_741_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_741_0, recoveredArgument_741_1) -> GroupedFilesKt.GroupedFilesWith3FilesPreview$lambda$11(recoveredCapture_741_0, (androidx.compose.runtime.Composer)recoveredArgument_741_0, ((java.lang.Integer)recoveredArgument_741_1).intValue())));
      }
   }

   private static final Unit GroupedFiles$lambda$1$lambda$0(String var0, String var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit GroupedFiles$lambda$7$lambda$6$lambda$5$lambda$4(Function2 var0, MediaData$Media$Other var1) {
      String var10001 = var1.getUri().toString();
      Intrinsics.checkNotNullExpressionValue(var10001, "toString(...)");
      var0.invoke(var10001, var1.getMimeType());
      return Unit.INSTANCE;
   }

   private static final Unit GroupedFiles$lambda$8(
      List var0, RoundedCornerShape var1, Modifier var2, Function2 var3, int var4, int var5, Composer var6, int var7
   ) {
      GroupedFiles(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit GroupedFilesWith1FilePreview$lambda$9(int var0, Composer var1, int var2) {
      GroupedFilesWith1FilePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedFilesWith2FilesPreview$lambda$10(int var0, Composer var1, int var2) {
      GroupedFilesWith2FilesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit GroupedFilesWith3FilesPreview$lambda$11(int var0, Composer var1, int var2) {
      GroupedFilesWith3FilesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
