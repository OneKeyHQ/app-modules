package io.intercom.android.sdk.survey.ui.questiontype.files;

import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.models.FileType;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Error;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import io.intercom.android.sdk.tickets.FIleAttachmentListKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
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
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a>\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032!\u0010\u0005\u001a\u001d\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\t\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f¨\u0006\u000e",
   d2 = {
         "FileAttachmentList",
         "",
         "items",
         "",
         "Lio/intercom/android/sdk/survey/ui/models/Answer$MediaAnswer$MediaItem;",
         "onItemClick",
         "Lkotlin/Function1;",
         "Lkotlin/ParameterName;",
         "name",
         "mediaItem",
         "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "FileAttachmentListPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "FileAttachmentListErrorPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFileAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileAttachmentListKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,124:1\n118#2:125\n87#3:126\n83#3,10:127\n94#3:176\n81#4,6:137\n88#4,6:152\n96#4:175\n402#5,9:143\n411#5:158\n412#5,2:173\n1869#6:159\n1870#6:172\n1047#7,6:160\n1047#7,6:166\n*S KotlinDebug\n*F\n+ 1 FileAttachmentList.kt\nio/intercom/android/sdk/survey/ui/questiontype/files/FileAttachmentListKt\n*L\n35#1:125\n34#1:126\n34#1:127,10\n34#1:176\n34#1:137,6\n34#1:152,6\n34#1:175\n34#1:143,9\n34#1:158\n34#1:173,2\n37#1:159\n37#1:172\n42#1:160,6\n50#1:166,6\n*E\n"
)
public final class FileAttachmentListKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileAttachmentList(@NotNull List var0, @NotNull Function1 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "items");
      Intrinsics.checkNotNullParameter(var1, "onItemClick");
      var2 = var2.startRestartGroup(-2107060022);
      ComposerKt.sourceInformation(var2, "C(FileAttachmentList)33@1628L1127:FileAttachmentList.kt#lphomb");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2107060022, var4, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileAttachmentList (FileAttachmentList.kt:32)"
            );
         }

         int var6 = 8;
         int var7 = 0;
         Vertical var55 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var6));
         byte var8 = 48;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var5 = Modifier.Companion;
         Horizontal var56 = Alignment.Companion.getStart();
         MeasurePolicy var10 = ColumnKt.columnMeasurePolicy(var55, var56, var2, 14 & var8 >> 3 | 112 & var8 >> 3);
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
         ComposerKt.sourceInformationMarkerStart(var29, -1429323795, "C:FileAttachmentList.kt#lphomb");
         var29.startReplaceGroup(1339366288);
         ComposerKt.sourceInformation(var29, "");
         Iterable var32 = var0;
         int var33 = 0;

         for (Object var35 : var32) {
            Answer$MediaAnswer$MediaItem var36 = (Answer$MediaAnswer$MediaItem)var35;
            int var37 = 0;
            if (var36.getUploadStatus() instanceof Answer$MediaAnswer$FileUploadStatus$Error) {
               var29.startReplaceGroup(-582498571);
               ComposerKt.sourceInformation(var29, "41@1967L63,38@1786L263");
               String var38 = var36.getData().getFileName();
               FileType var39 = FIleAttachmentListKt.getFileType(var36.getData().getMimeType());
               Modifier var10000 = Modifier.Companion;
               boolean var10001 = false;
               Object var10002 = null;
               Object var10003 = null;
               Object var10004 = null;
               var29.startReplaceGroup(1643783845);
               ComposerKt.sourceInformation(var29, "CC(remember):FileAttachmentList.kt#9igjgp");
               Composer var40 = var29;
               boolean var41 = (var4 & 112) == 32 | var2.changedInstance(var36);
               int var42 = 0;
               Object var43 = var40.rememberedValue();
               int var44 = 0;
               Object var10005;
               if (!var41 && var43 != Composer.Companion.getEmpty()) {
                  var10005 = var43;
               } else {
                  Object var45 = null;
                  Object var46 = null;
                  Object var47 = null;
                  boolean var48 = false;
                  Modifier var49 = var10000;
                  int var50 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_1448_0 = var1;
                  final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem recoveredCapture_1448_1 = var36;
                  Function0 var51 = ((kotlin.jvm.functions.Function0) () -> FileAttachmentListKt.FileAttachmentList$lambda$5$lambda$4$lambda$1$lambda$0(recoveredCapture_1448_0, recoveredCapture_1448_1));
                  var10000 = var49;
                  var10001 = var48;
                  var10002 = var47;
                  var10003 = var46;
                  var10004 = var45;
                  Object var52 = var51;
                  var40.updateRememberedValue(var52);
                  var10005 = var52;
               }

               Function0 var53 = (Function0)var10005;
               var29.endReplaceGroup();
               FIleAttachmentListKt.FailedFileAttached(
                  ClickableKt.recovered$1c9660c41fd850dc(
                     var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var53, 15, null
                  ),
                  var38,
                  var39,
                  var29,
                  0,
                  0
               );
               var29.endReplaceGroup();
            } else {
               var29.startReplaceGroup(-582188354);
               ComposerKt.sourceInformation(var29, "49@2264L63,52@2363L343,46@2087L638");
               String var57 = var36.getData().getFileName();
               FileType var58 = FIleAttachmentListKt.getFileType(var36.getData().getMimeType());
               Modifier var73 = Modifier.Companion;
               boolean var75 = false;
               Object var76 = null;
               Object var77 = null;
               Object var78 = null;
               var29.startReplaceGroup(1643793349);
               ComposerKt.sourceInformation(var29, "CC(remember):FileAttachmentList.kt#9igjgp");
               Composer var59 = var29;
               boolean var60 = (var4 & 112) == 32 | var2.changedInstance(var36);
               int var61 = 0;
               Object var62 = var59.rememberedValue();
               int var63 = 0;
               Object var79;
               if (!var60 && var62 != Composer.Companion.getEmpty()) {
                  var79 = var62;
               } else {
                  Object var64 = null;
                  Object var65 = null;
                  Object var66 = null;
                  boolean var67 = false;
                  Modifier var68 = var73;
                  int var69 = 0;
                  final kotlin.jvm.functions.Function1 recoveredCapture_1449_0 = var1;
                  final io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem recoveredCapture_1449_1 = var36;
                  Function0 var70 = ((kotlin.jvm.functions.Function0) () -> FileAttachmentListKt.FileAttachmentList$lambda$5$lambda$4$lambda$3$lambda$2(recoveredCapture_1449_0, recoveredCapture_1449_1));
                  var73 = var68;
                  var75 = var67;
                  var76 = var66;
                  var77 = var65;
                  var78 = var64;
                  Object var71 = var70;
                  var59.updateRememberedValue(var71);
                  var79 = var71;
               }

               Function0 var72 = (Function0)var79;
               var29.endReplaceGroup();
               FIleAttachmentListKt.recovered$b6c145e31a447e64(
                  ClickableKt.recovered$1c9660c41fd850dc(var73, var75, (String)var76, (Role)var77, (MutableInteractionSource)var78, var72, 15, null),
                  var57,
                  var58,
                  0L,
                  0L,
                  null,
                  ComposableLambdaKt.rememberComposableLambda(2007803062, true, new FileAttachmentListKt$FileAttachmentList$1$1$3(var36), var29, 54),
                  var29,
                  1572864,
                  56
               );
               var29.endReplaceGroup();
            }
         }

         var29.endReplaceGroup();
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

      ScopeUpdateScope var74 = var2.endRestartGroup();
      if (var74 != null) {
         final java.util.List recoveredCapture_1450_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_1450_1 = var1;
         final int recoveredCapture_1450_2 = var3;
         var74.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1450_0, recoveredArgument_1450_1) -> FileAttachmentListKt.FileAttachmentList$lambda$6(recoveredCapture_1450_0, recoveredCapture_1450_1, recoveredCapture_1450_2, (androidx.compose.runtime.Composer)recoveredArgument_1450_0, ((java.lang.Integer)recoveredArgument_1450_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileAttachmentListPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1973696025);
      ComposerKt.sourceInformation(var0, "C(FileAttachmentListPreview)69@2835L582:FileAttachmentList.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1973696025, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileAttachmentListPreview (FileAttachmentList.kt:68)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FileAttachmentListKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1452_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1452_0, recoveredArgument_1452_1) -> FileAttachmentListKt.FileAttachmentListPreview$lambda$7(recoveredCapture_1452_0, (androidx.compose.runtime.Composer)recoveredArgument_1452_0, ((java.lang.Integer)recoveredArgument_1452_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileAttachmentListErrorPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(232584117);
      ComposerKt.sourceInformation(var0, "C(FileAttachmentListErrorPreview)93@3502L1067:FileAttachmentList.kt#lphomb");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               232584117, var1, -1, "io.intercom.android.sdk.survey.ui.questiontype.files.FileAttachmentListErrorPreview (FileAttachmentList.kt:92)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$FileAttachmentListKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1451_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1451_0, recoveredArgument_1451_1) -> FileAttachmentListKt.FileAttachmentListErrorPreview$lambda$8(recoveredCapture_1451_0, (androidx.compose.runtime.Composer)recoveredArgument_1451_0, ((java.lang.Integer)recoveredArgument_1451_1).intValue())));
      }
   }

   private static final Unit FileAttachmentList$lambda$5$lambda$4$lambda$1$lambda$0(Function1 var0, Answer$MediaAnswer$MediaItem var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentList$lambda$5$lambda$4$lambda$3$lambda$2(Function1 var0, Answer$MediaAnswer$MediaItem var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentList$lambda$6(List var0, Function1 var1, int var2, Composer var3, int var4) {
      FileAttachmentList(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentListPreview$lambda$7(int var0, Composer var1, int var2) {
      FileAttachmentListPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentListErrorPreview$lambda$8(int var0, Composer var1, int var2) {
      FileAttachmentListErrorPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
