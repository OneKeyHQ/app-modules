package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.net.Uri;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$ServerError;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError$SizeLimitExceeded;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Error;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-4$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,438:1\n118#2:439\n118#2:440\n87#3:441\n85#3,8:442\n94#3:547\n81#4,6:450\n88#4,6:465\n96#4:546\n402#5,9:456\n411#5:471\n412#5,2:544\n1047#6,6:472\n1047#6,6:478\n1047#6,6:484\n1047#6,6:490\n1047#6,6:496\n1047#6,6:502\n1047#6,6:508\n1047#6,6:514\n1047#6,6:520\n1047#6,6:526\n1047#6,6:532\n1047#6,6:538\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-4$1\n*L\n396#1:439\n397#1:440\n395#1:441\n395#1:442,8\n395#1:547\n395#1:450,6\n395#1:465,6\n395#1:546\n395#1:456,9\n395#1:471\n395#1:544,2\n404#1:472,6\n405#1:478,6\n406#1:484,6\n413#1:490,6\n414#1:496,6\n415#1:502,6\n422#1:508,6\n423#1:514,6\n424#1:520,6\n431#1:526,6\n432#1:532,6\n433#1:538,6\n*E\n"
)
final class ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C394@13593L1864:MediaItems.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2147443468,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.lambda-4.<anonymous> (MediaItems.kt:394)"
            );
         }

         int var4 = 16;
         int var5 = 0;
         HorizontalOrVertical var3 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var4));
         Modifier var10000 = Modifier.Companion;
         var5 = 16;
         int $i$f$getDpx = 0;
         Modifier var41 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var5));
         byte var44 = 54;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var43 = Alignment.Companion.getStart();
         MeasurePolicy var8 = ColumnKt.columnMeasurePolicy(var3, var43, var1, 14 & var44 >> 3 | 112 & var44 >> 3);
         Modifier var9 = var41;
         MeasurePolicy var10 = var8;
         Composer var11 = var1;
         int var12 = 112 & var44 << 3;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(var11, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var14 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var11, 0));
         CompositionLocalMap var15 = var11.getCurrentCompositionLocalMap();
         Modifier var16 = ComposedModifierKt.materializeModifier(var11, var9);
         Function0 var17 = ComposeUiNode.Companion.getConstructor();
         Composer var18 = var11;
         int var19 = 6 | 896 & var12 << 6;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var18.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var18.startReusableNode();
         if (var18.getInserting()) {
            var18.createNode(var17);
         } else {
            var18.useNode();
         }

         Composer var21 = Updater.recovered$676aa17b44fd6280(var18);
         int var22 = 0;
         Updater.recovered$89682b199a142880(var21, var10, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var21, var15, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var21, var14, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var21, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var21, var16, ComposeUiNode.Companion.getSetModifier());
         int var23 = 14 & var19 >> 6;
         Composer var24 = var18;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var26 = 6 | 112 & var44 >> 6;
         Composer var27 = var24;
         ColumnScope var28 = ColumnScopeInstance.INSTANCE;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var27,
            1524067671,
            "C403@14046L2,404@14086L2,405@14126L2,398@13736L407,412@14438L2,413@14478L2,414@14518L2,407@14156L379,421@14891L2,422@14931L2,423@14971L2,416@14548L440,430@15350L2,431@15390L2,432@15430L2,425@15001L446:MediaItems.kt#yb6g9"
         );
         Uri var10008 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         MediaUploadItem var162 = new MediaUploadItem(
            null,
            new MediaData$Media$Other("application/pdf", 0L, "pdf_document.pdf", var10008),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         var27.startReplaceGroup(-1197754305);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         Composer var30 = var27;
         boolean var31 = false;
         int var32 = 0;
         Object var33 = var30.rememberedValue();
         int var34 = 0;
         Object var166;
         if (var33 == Composer.Companion.getEmpty()) {
            MediaUploadItem var35 = var162;
            int var36 = 0;
            var166 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$1$lambda$0());
            var162 = var35;
            Object var111 = var166;
            var30.updateRememberedValue(var111);
            var166 = (Function0)var111;
         } else {
            var166 = (Function0)var33;
         }

         Function0 var37 = (kotlin.jvm.functions.Function0) var166;
         var27.endReplaceGroup();
         var166 = var37;
         var27.startReplaceGroup(-1197753025);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         Object var10002;
         if (var33 == Composer.Companion.getEmpty()) {
            Function0 var38 = var37;
            MediaUploadItem var100 = var162;
            int var112 = 0;
            Function1 var39 = ((kotlin.jvm.functions.Function1) (recoveredArgument_540_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_540_0));
            var162 = var100;
            var166 = var38;
            Object var113 = var39;
            var30.updateRememberedValue(var113);
            var10002 = (Function1)var113;
         } else {
            var10002 = (Function1)var33;
         }

         Function1 var134 = (kotlin.jvm.functions.Function1) var10002;
         var27.endReplaceGroup();
         var10002 = var134;
         var27.startReplaceGroup(-1197751745);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         Object var10003;
         if (var33 == Composer.Companion.getEmpty()) {
            Function1 var152 = var134;
            Function0 var145 = (kotlin.jvm.functions.Function0) var166;
            MediaUploadItem var101 = var162;
            int var114 = 0;
            Function1 var40 = ((kotlin.jvm.functions.Function1) (recoveredArgument_541_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$5$lambda$4((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_541_0));
            var162 = var101;
            var166 = var145;
            var10002 = var152;
            Object var115 = var40;
            var30.updateRememberedValue(var115);
            var10003 = var115;
         } else {
            var10003 = var33;
         }

         Function1 var135 = (Function1)var10003;
         var27.endReplaceGroup();
         MediaItemsKt.access$OtherItem(var162, (kotlin.jvm.functions.Function0) var166, (kotlin.jvm.functions.Function1) var10002, var135, var27, 3504);
         var10008 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         MediaUploadItem var163 = new MediaUploadItem(
            null, new MediaData$Media$Other("text/plain", 0L, "txt_document.txt", var10008), MediaUploadItem$UploadState$Uploading.INSTANCE, 1, null
         );
         var27.startReplaceGroup(-1197741761);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            MediaUploadItem var102 = var163;
            int var116 = 0;
            var166 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$7$lambda$6());
            var163 = var102;
            Object var117 = var166;
            var30.updateRememberedValue(var117);
            var166 = (Function0)var117;
         } else {
            var166 = (Function0)var33;
         }

         var37 = (kotlin.jvm.functions.Function0) var166;
         var27.endReplaceGroup();
         var166 = var37;
         var27.startReplaceGroup(-1197740481);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function0 var146 = var37;
            MediaUploadItem var103 = var163;
            int var118 = 0;
            Function1 var153 = ((kotlin.jvm.functions.Function1) (recoveredArgument_543_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$9$lambda$8((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_543_0));
            var163 = var103;
            var166 = var146;
            Object var119 = var153;
            var30.updateRememberedValue(var119);
            var10002 = (Function1)var119;
         } else {
            var10002 = (Function1)var33;
         }

         Function1 var137 = (kotlin.jvm.functions.Function1) var10002;
         var27.endReplaceGroup();
         var10002 = var137;
         var27.startReplaceGroup(-1197739201);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function1 var154 = var137;
            Function0 var147 = (kotlin.jvm.functions.Function0) var166;
            MediaUploadItem var104 = var163;
            int var120 = 0;
            Function1 var159 = ((kotlin.jvm.functions.Function1) (recoveredArgument_544_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$11$lambda$10((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_544_0));
            var163 = var104;
            var166 = var147;
            var10002 = var154;
            Object var121 = var159;
            var30.updateRememberedValue(var121);
            var10003 = var121;
         } else {
            var10003 = var33;
         }

         Function1 var138 = (Function1)var10003;
         var27.endReplaceGroup();
         MediaItemsKt.access$OtherItem(var163, (kotlin.jvm.functions.Function0) var166, (kotlin.jvm.functions.Function1) var10002, var138, var27, 3504);
         var10008 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         MediaUploadItem var164 = new MediaUploadItem(
            null,
            new MediaData$Media$Other("application/zip", 0L, "zip_document.zip", var10008),
            new MediaUploadItem$UploadState$Error(new MediaUploadRepository$UploadError$ServerError(400)),
            1,
            null
         );
         var27.startReplaceGroup(-1197727265);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            MediaUploadItem var105 = var164;
            int var122 = 0;
            var166 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$13$lambda$12());
            var164 = var105;
            Object var123 = var166;
            var30.updateRememberedValue(var123);
            var166 = (Function0)var123;
         } else {
            var166 = (Function0)var33;
         }

         var37 = (kotlin.jvm.functions.Function0) var166;
         var27.endReplaceGroup();
         var166 = var37;
         var27.startReplaceGroup(-1197725985);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function0 var148 = var37;
            MediaUploadItem var106 = var164;
            int var124 = 0;
            Function1 var155 = ((kotlin.jvm.functions.Function1) (recoveredArgument_546_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$15$lambda$14((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_546_0));
            var164 = var106;
            var166 = var148;
            Object var125 = var155;
            var30.updateRememberedValue(var125);
            var10002 = (Function1)var125;
         } else {
            var10002 = (Function1)var33;
         }

         Function1 var140 = (kotlin.jvm.functions.Function1) var10002;
         var27.endReplaceGroup();
         var10002 = var140;
         var27.startReplaceGroup(-1197724705);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function1 var156 = var140;
            Function0 var149 = (kotlin.jvm.functions.Function0) var166;
            MediaUploadItem var107 = var164;
            int var126 = 0;
            Function1 var160 = ((kotlin.jvm.functions.Function1) (recoveredArgument_535_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$17$lambda$16((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_535_0));
            var164 = var107;
            var166 = var149;
            var10002 = var156;
            Object var127 = var160;
            var30.updateRememberedValue(var127);
            var10003 = var127;
         } else {
            var10003 = var33;
         }

         Function1 var141 = (Function1)var10003;
         var27.endReplaceGroup();
         MediaItemsKt.access$OtherItem(var164, (kotlin.jvm.functions.Function0) var166, (kotlin.jvm.functions.Function1) var10002, var141, var27, 3504);
         var10008 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         MediaUploadItem var165 = new MediaUploadItem(
            null,
            new MediaData$Media$Other("text/csv", 0L, "csv_document.zip", var10008),
            new MediaUploadItem$UploadState$Error(new MediaUploadRepository$UploadError$SizeLimitExceeded(0L, 0L, "50")),
            1,
            null
         );
         var27.startReplaceGroup(-1197712577);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            MediaUploadItem var108 = var165;
            int var128 = 0;
            var166 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$19$lambda$18());
            var165 = var108;
            Object var129 = var166;
            var30.updateRememberedValue(var129);
            var166 = (Function0)var129;
         } else {
            var166 = (Function0)var33;
         }

         var37 = (kotlin.jvm.functions.Function0) var166;
         var27.endReplaceGroup();
         var166 = var37;
         var27.startReplaceGroup(-1197711297);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function0 var150 = var37;
            MediaUploadItem var109 = var165;
            int var130 = 0;
            Function1 var157 = ((kotlin.jvm.functions.Function1) (recoveredArgument_537_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$21$lambda$20((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_537_0));
            var165 = var109;
            var166 = var150;
            Object var131 = var157;
            var30.updateRememberedValue(var131);
            var10002 = (Function1)var131;
         } else {
            var10002 = (Function1)var33;
         }

         Function1 var143 = (kotlin.jvm.functions.Function1) var10002;
         var27.endReplaceGroup();
         var10002 = var143;
         var27.startReplaceGroup(-1197710017);
         ComposerKt.sourceInformation(var27, "CC(remember):MediaItems.kt#9igjgp");
         var30 = var27;
         var31 = false;
         var32 = 0;
         var33 = var30.rememberedValue();
         var34 = 0;
         if (var33 == Composer.Companion.getEmpty()) {
            Function1 var158 = var143;
            Function0 var151 = (kotlin.jvm.functions.Function0) var166;
            MediaUploadItem var110 = var165;
            int var132 = 0;
            Function1 var161 = ((kotlin.jvm.functions.Function1) (recoveredArgument_538_0) -> ComposableSingletons$MediaItemsKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$24$lambda$23$lambda$22((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_538_0));
            var165 = var110;
            var166 = var151;
            var10002 = var158;
            Object var133 = var161;
            var30.updateRememberedValue(var133);
            var10003 = var133;
         } else {
            var10003 = var33;
         }

         Function1 var144 = (Function1)var10003;
         var27.endReplaceGroup();
         MediaItemsKt.access$OtherItem(var165, (kotlin.jvm.functions.Function0) var166, (kotlin.jvm.functions.Function1) var10002, var144, var27, 3504);
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var24);
         var18.endNode();
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var11);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$24$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$3$lambda$2(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$5$lambda$4(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$7$lambda$6() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$9$lambda$8(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$11$lambda$10(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$13$lambda$12() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$15$lambda$14(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$17$lambda$16(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$19$lambda$18() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$21$lambda$20(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$24$lambda$23$lambda$22(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
