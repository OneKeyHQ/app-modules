package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.input.pointer.PointerInputEventHandler;
import androidx.compose.ui.input.pointer.SuspendingPointerInputFilterKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.ClipboardManager;
import androidx.compose.ui.platform.CompositionLocalsKt;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000R\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\u001a\u007f\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\r2\u0010\b\u0002\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f2.\u0010\u0010\u001a*\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u000f\u0012\u0004\u0012\u00020\u00030\u0011¢\u0006\u0002\b\u0013¢\u0006\u0002\b\u0014H\u0001¢\u0006\u0002\u0010\u0015\u001a\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00172\u0006\u0010\u0019\u001a\u00020\u0017H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001a²\u0006\n\u0010\u001b\u001a\u00020\u001cX\u008a\u008e\u0002",
   d2 = {
         "TWIG",
         "Lcom/intercom/twig/Twig;",
         "ClickableMessageRow",
         "",
         "conversationPart",
         "Lio/intercom/android/sdk/models/Part;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "bottomMetadata",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata;",
         "horizontalAlignment",
         "Landroidx/compose/ui/Alignment$Horizontal;",
         "paddingValues",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "onRetryMessageClicked",
         "Lkotlin/Function0;",
         "content",
         "Lkotlin/Function3;",
         "Landroidx/compose/foundation/layout/ColumnScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "(Lio/intercom/android/sdk/models/Part;Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/ui/components/row/BottomMetadata;Landroidx/compose/ui/Alignment$Horizontal;Landroidx/compose/foundation/layout/PaddingValues;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function5;Landroidx/compose/runtime/Composer;II)V",
         "joinSenderLabelAndTimestamp",
         "",
         "label",
         "timestamp",
         "intercom-sdk-base_release",
         "showTimestamp",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nClickableMessageRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClickableMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ClickableMessageRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,127:1\n1047#2,6:128\n1047#2,6:135\n1047#2,6:141\n1047#2,6:147\n75#3:134\n75#3:185\n87#4:153\n84#4,9:154\n94#4:189\n81#5,6:163\n88#5,6:178\n96#5:188\n402#6,9:169\n411#6:184\n412#6,2:186\n85#7:190\n117#7,2:191\n*S KotlinDebug\n*F\n+ 1 ClickableMessageRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ClickableMessageRowKt\n*L\n47#1:128,6\n51#1:135,6\n66#1:141,6\n71#1:147,6\n49#1:134\n92#1:185\n68#1:153\n68#1:154,9\n68#1:189\n68#1:163,6\n68#1:178,6\n68#1:188\n68#1:169,9\n68#1:184\n68#1:186,2\n47#1:190\n47#1:191,2\n*E\n"
)
public final class ClickableMessageRowKt {
   @NotNull
   private static final Twig TWIG;

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void ClickableMessageRow(
      @NotNull Part var0,
      @Nullable Modifier var1,
      @Nullable BottomMetadata var2,
      @Nullable Horizontal var3,
      @Nullable PaddingValues var4,
      @Nullable Function0 var5,
      @NotNull Function5 var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversationPart");
      Intrinsics.checkNotNullParameter(var6, "content");
      Composer var52 = var7.startRestartGroup(-932954058);
      ComposerKt.sourceInformation(
         var52, "C(ClickableMessageRow)P(2,4!1,3,6,5)46@1851L34,48@1954L7,50@1985L842,70@3007L178,67@2911L1372:ClickableMessageRow.kt#hr3bi5"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var52.changedInstance(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var52.changed(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var52.changed(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var52.changed(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var52.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var52.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 64) != 0) {
         var10 |= 1572864;
      } else if ((var8 & 1572864) == 0) {
         var10 |= var52.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 599187) == 599186 && var52.getSkipping()) {
         var52.skipToGroupEnd();
      } else {
         if ((var9 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if ((var9 & 8) != 0) {
            var3 = Alignment.Companion.getStart();
         }

         if ((var9 & 16) != 0) {
            var4 = PaddingKt.recovered$0eefed217002276c(0.0F, 0.0F, 3, null);
         }

         if ((var9 & 32) != 0) {
            var5 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -932954058, var10, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRow (ClickableMessageRow.kt:45)"
            );
         }

         var52.startReplaceGroup(1099063116);
         ComposerKt.sourceInformation(var52, "CC(remember):ClickableMessageRow.kt#9igjgp");
         Composer var13 = var52;
         boolean var14 = false;
         int var15 = 0;
         Object var16 = var13.rememberedValue();
         int var17 = 0;
         Object var10000;
         if (var16 == Composer.Companion.getEmpty()) {
            int var18 = 0;
            Object var73 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var13.updateRememberedValue(var73);
            var10000 = var73;
         } else {
            var10000 = var16;
         }

         MutableState var12 = (MutableState)var10000;
         var52.endReplaceGroup();
         MutableState var11 = var12;
         CompositionLocal var54 = CompositionLocalsKt.getLocalClipboardManager();
         int var56 = 0;
         int var59 = 0;
         ComposerKt.sourceInformationMarkerStart(var52, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var64 = var52.consume(var54);
         ComposerKt.sourceInformationMarkerEnd(var52);
         ClipboardManager var53 = (ClipboardManager)var64;
         var52.startReplaceGroup(1099068212);
         ComposerKt.sourceInformation(var52, "CC(remember):ClickableMessageRow.kt#9igjgp");
         Composer var60 = var52;
         boolean var65 = var52.changedInstance(var53) | var52.changedInstance(var0);
         int $i$f$cachex = 0;
         Object it$ivx = var60.rememberedValue();
         int var19/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var91;
         if (!var65 && it$ivx != Composer.Companion.getEmpty()) {
            var91 = it$ivx;
         } else {
            int var20 = 0;
            final androidx.compose.ui.platform.ClipboardManager recoveredCapture_683_0 = var53;
            final io.intercom.android.sdk.models.Part recoveredCapture_683_1 = var0;
            Object var21 = ((kotlin.jvm.functions.Function0) () -> ClickableMessageRowKt.ClickableMessageRow$lambda$4$lambda$3(recoveredCapture_683_0, recoveredCapture_683_1));
            var60.updateRememberedValue(var21);
            var91 = var21;
         }

         Function0 var57 = (Function0)var91;
         var52.endReplaceGroup();
         Function0 var55 = var57;
         Function0 var61 = var5;
         var52.startReplaceGroup(1099094981);
         ComposerKt.sourceInformation(var52, "65@2871L34");
         Function0 var92 = var61;
         if (var61 == null) {
            var52.startReplaceGroup(1099095756);
            ComposerKt.sourceInformation(var52, "CC(remember):ClickableMessageRow.kt#9igjgp");
            Composer var75 = var52;
            boolean var78 = false;
            int $i$f$cachexx = 0;
            Object it$ivxx = var75.rememberedValue();
            int var22/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var93;
            if (it$ivxx == Composer.Companion.getEmpty()) {
               int var23 = 0;
               final androidx.compose.runtime.MutableState recoveredCapture_684_0 = var11;
               Object var24 = ((kotlin.jvm.functions.Function0) () -> ClickableMessageRowKt.ClickableMessageRow$lambda$6$lambda$5(recoveredCapture_684_0));
               var75.updateRememberedValue(var24);
               var93 = var24;
            } else {
               var93 = it$ivxx;
            }

            Function0 var70 = (Function0)var93;
            var52.endReplaceGroup();
            var92 = var70;
         }

         Function0 var66 = var92;
         var52.endReplaceGroup();
         Function0 var58 = var66;
         Modifier var94 = SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null);
         Unit var10001 = Unit.INSTANCE;
         var52.startReplaceGroup(1099100252);
         ComposerKt.sourceInformation(var52, "CC(remember):ClickableMessageRow.kt#9igjgp");
         Composer var67 = var52;
         boolean var71 = var52.changed(var55) | var52.changed(var58);
         int $i$f$cachexx = 0;
         Object it$ivxx = var67.rememberedValue();
         int var82/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10002;
         if (!var71 && it$ivxx != Composer.Companion.getEmpty()) {
            var10002 = it$ivxx;
         } else {
            Unit var50 = var10001;
            Modifier var49 = var94;
            int var85 = 0;
            PointerInputEventHandler var51 = new ClickableMessageRowKt$ClickableMessageRow$1$1(var55, var58);
            var94 = var49;
            var10001 = var50;
            Object var87 = var51;
            var67.updateRememberedValue(var87);
            var10002 = var87;
         }

         PointerInputEventHandler var62 = (PointerInputEventHandler)var10002;
         var52.endReplaceGroup();
         Modifier var63 = PaddingKt.padding(SuspendingPointerInputFilterKt.pointerInput(var94, var10001, var62), var4);
         int var72 = 896 & var10 >> 3;
         int var77 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var52, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var68 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var80 = ColumnKt.columnMeasurePolicy(var68, var3, var52, 14 & var72 >> 3 | 112 & var72 >> 3);
         Modifier var83 = var63;
         MeasurePolicy var86 = var80;
         Composer var88 = var52;
         int var89 = 112 & var72 << 3;
         int var90 = 0;
         ComposerKt.sourceInformationMarkerStart(var88, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var88, 0));
         CompositionLocalMap var26 = var88.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var88, var83);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var88;
         int var30 = 6 | 896 & var89 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var86, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var72 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, -1588942854, "C80@3317L34:ClickableMessageRow.kt#hr3bi5");
         var6.invoke(var39, var0, var58, var38, 14 & var37 | 112 & var10 << 3 | 7168 & var10 >> 9);
         var38.startReplaceGroup(-1990916298);
         ComposerKt.sourceInformation(var38, "84@3497L58,85@3568L699");
         if (var2 != null && ClickableMessageRow$lambda$1(var11) || (var2 != null ? var2.getAlwaysShow() : false)) {
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(Modifier.Companion, var2.recovered$9cbfa2f73aae0193()), var38, 0);
            Modifier var41 = PaddingKt.recovered$03b481d31d62885a(Modifier.Companion, var2.recovered$2c49dee5a543a836(), 0.0F, 0.0F, 0.0F, 14, null);
            String var42 = var2.getText();
            var38.startReplaceGroup(-1990904109);
            ComposerKt.sourceInformation(var38, "91@3912L7");
            String var96;
            if (BubbleMessageRowKt.shouldShowAttribution(var0)) {
               Phrase var95;
               String var100;
               label160: {
                  CompositionLocal var43 = AndroidCompositionLocals_androidKt.getLocalContext();
                  int $changed$ivx = 0;
                  int $i$f$getCurrentx = 0;
                  ComposerKt.sourceInformationMarkerStart(var38, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
                  Object var46 = var38.consume(var43);
                  ComposerKt.sourceInformationMarkerEnd(var38);
                  var95 = Phrase.from((Context)var46, string.intercom_gif_attribution);
                  List var98 = var0.getBlocks();
                  Intrinsics.checkNotNullExpressionValue(var98, "getBlocks(...)");
                  Block var99 = (Block)CollectionsKt.firstOrNull(var98);
                  if (var99 != null) {
                     var100 = var99.getAttribution();
                     if (var100 != null) {
                        break label160;
                     }
                  }

                  var100 = "";
               }

               var96 = var95.put("providername", var100).format().toString();
            } else {
               var96 = "";
            }

            String var47 = var96;
            var38.endReplaceGroup();
            String var48 = var47;
            BubbleMessageRowKt.MessageMeta(var41, var42, var48, true, var38, 3072, 0);
         }

         var38.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var88);
         ComposerKt.sourceInformationMarkerEnd(var52);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var97 = var52.endRestartGroup();
      if (var97 != null) {
         final io.intercom.android.sdk.models.Part recoveredCapture_682_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_682_1 = var1;
         final io.intercom.android.sdk.m5.conversation.ui.components.row.BottomMetadata recoveredCapture_682_2 = var2;
         final androidx.compose.ui.Alignment.Horizontal recoveredCapture_682_3 = var3;
         final androidx.compose.foundation.layout.PaddingValues recoveredCapture_682_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_682_5 = var5;
         final kotlin.jvm.functions.Function5 recoveredCapture_682_6 = var6;
         final int recoveredCapture_682_7 = var8;
         final int recoveredCapture_682_8 = var9;
         var97.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_682_0, recoveredArgument_682_1) -> ClickableMessageRowKt.ClickableMessageRow$lambda$9(recoveredCapture_682_0, recoveredCapture_682_1, recoveredCapture_682_2, recoveredCapture_682_3, recoveredCapture_682_4, recoveredCapture_682_5, recoveredCapture_682_6, recoveredCapture_682_7, recoveredCapture_682_8, (androidx.compose.runtime.Composer)recoveredArgument_682_0, ((java.lang.Integer)recoveredArgument_682_1).intValue())));
      }
   }

   @NotNull
   public static final String joinSenderLabelAndTimestamp(@NotNull String var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "label");
      Intrinsics.checkNotNullParameter(var1, "timestamp");
      return !StringsKt.isBlank(var0) && !StringsKt.isBlank(var1) ? var0 + " • " + var1 : (!StringsKt.isBlank(var0) ? var0 : var1);
   }

   private static final boolean ClickableMessageRow$lambda$1(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void ClickableMessageRow$lambda$2(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit ClickableMessageRow$lambda$4$lambda$3(ClipboardManager var0, Part var1) {
      try {
         var0.setText(BubbleMessageRowKt.getCopyText(var1));
      } catch (SecurityException var3) {
         TWIG.e("Clipboard access denied due to user restrictions: " + var3.getMessage());
      } catch (Exception var4) {
         TWIG.e("Failed to copy message to clipboard: " + var4.getMessage());
      }

      return Unit.INSTANCE;
   }

   private static final Unit ClickableMessageRow$lambda$6$lambda$5(MutableState var0) {
      ClickableMessageRow$lambda$2(var0, !ClickableMessageRow$lambda$1(var0));
      return Unit.INSTANCE;
   }

   private static final Unit ClickableMessageRow$lambda$9(
      Part var0,
      Modifier var1,
      BottomMetadata var2,
      Horizontal var3,
      PaddingValues var4,
      Function0 var5,
      Function5 var6,
      int var7,
      int var8,
      Composer var9,
      int var10
   ) {
      ClickableMessageRow(var0, var1, var2, var3, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      TWIG = var10000;
   }
}
