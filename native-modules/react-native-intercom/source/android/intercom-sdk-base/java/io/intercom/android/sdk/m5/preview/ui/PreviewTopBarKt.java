package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.material3.IconButtonKt;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import io.intercom.android.sdk.ui.IntercomPreviews;
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
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u001ao\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000f¨\u0006\u0010",
   d2 = {
         "PreviewTopBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "showDeleteAction",
         "",
         "showSendAction",
         "showDownloadAction",
         "onBackCLick",
         "Lkotlin/Function0;",
         "onDeleteClick",
         "onSendClick",
         "onDownloadClick",
         "(Landroidx/compose/ui/Modifier;ZZZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "PreviewTopBarPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPreviewTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewTopBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,108:1\n1047#2,6:109\n1047#2,6:115\n99#3,6:121\n99#3:149\n95#3,10:150\n106#3:185\n106#3:189\n81#4,6:127\n88#4,6:142\n81#4,6:160\n88#4,6:175\n96#4:184\n96#4:188\n402#5,9:133\n411#5:148\n402#5,9:166\n411#5,3:181\n412#5,2:186\n*S KotlinDebug\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewTopBarKt\n*L\n30#1:109,6\n31#1:115,6\n33#1:121,6\n51#1:149\n51#1:150,10\n51#1:185\n33#1:189\n33#1:127,6\n33#1:142,6\n51#1:160,6\n51#1:175,6\n51#1:184\n33#1:188\n33#1:133,9\n33#1:148\n51#1:166,9\n51#1:181,3\n33#1:186,2\n*E\n"
)
public final class PreviewTopBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewTopBar(
      @Nullable Modifier var0,
      boolean var1,
      boolean var2,
      boolean var3,
      @NotNull Function0 var4,
      @NotNull Function0 var5,
      @Nullable Function0 var6,
      @Nullable Function0 var7,
      @Nullable Composer var8,
      int var9,
      int var10
   ) {
      Intrinsics.checkNotNullParameter(var4, "onBackCLick");
      Intrinsics.checkNotNullParameter(var5, "onDeleteClick");
      var8 = var8.startRestartGroup(1902685617);
      ComposerKt.sourceInformation(var8, "C(PreviewTopBar)P(!1,5,7,6!2,4)29@1137L2,30@1175L2,32@1187L1870:PreviewTopBar.kt#hhbc0c");
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var8.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var8.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var8.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var8.changed(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var8.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var8.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var8.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var8.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         if ((var10 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var10 & 4) != 0) {
            var2 = false;
         }

         if ((var10 & 8) != 0) {
            var3 = false;
         }

         if ((var10 & 64) != 0) {
            var8.startReplaceGroup(-2144022138);
            ComposerKt.sourceInformation(var8, "CC(remember):PreviewTopBar.kt#9igjgp");
            Composer var13 = var8;
            boolean var14 = false;
            int var15 = 0;
            Object var16 = var13.rememberedValue();
            int var17 = 0;
            Object var10000;
            if (var16 == Composer.Companion.getEmpty()) {
               int var18 = 0;
               Object var79 = ((kotlin.jvm.functions.Function0) () -> PreviewTopBarKt.PreviewTopBar$lambda$1$lambda$0());
               var13.updateRememberedValue(var79);
               var10000 = var79;
            } else {
               var10000 = var16;
            }

            Function0 var12 = (Function0)var10000;
            var8.endReplaceGroup();
            var6 = var12;
         }

         if ((var10 & 128) != 0) {
            var8.startReplaceGroup(-2144020922);
            ComposerKt.sourceInformation(var8, "CC(remember):PreviewTopBar.kt#9igjgp");
            Composer var69 = var8;
            boolean var71 = false;
            int var73 = 0;
            Object var75 = var69.rememberedValue();
            int var77 = 0;
            Object var83;
            if (var75 == Composer.Companion.getEmpty()) {
               int var80 = 0;
               Object var81 = ((kotlin.jvm.functions.Function0) () -> PreviewTopBarKt.PreviewTopBar$lambda$3$lambda$2());
               var69.updateRememberedValue(var81);
               var83 = var81;
            } else {
               var83 = var75;
            }

            Function0 var67 = (Function0)var83;
            var8.endReplaceGroup();
            var7 = var67;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1902685617, var11, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewTopBar (PreviewTopBar.kt:31)");
         }

         Modifier var68 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.wrapContentHeight$default(SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), null, false, 3, null),
            Color.recovered$41aa0ee03c2282fb(Color.Companion.recovered$235b30c090a399c5(), 0.5F, 0.0F, 0.0F, 0.0F, 14, null),
            null,
            2,
            null
         );
         Vertical var70 = Alignment.Companion.getCenterVertically();
         HorizontalOrVertical var72 = Arrangement.INSTANCE.getSpaceBetween();
         short var74 = 432;
         int var76 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var8, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var78 = RowKt.rowMeasurePolicy(var72, var70, var8, 14 & var74 >> 3 | 112 & var74 >> 3);
         Modifier var82 = var68;
         MeasurePolicy var19 = var78;
         Composer var20 = var8;
         int var21 = 112 & var74 << 3;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var23 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var20, 0));
         CompositionLocalMap var24 = var20.getCurrentCompositionLocalMap();
         Modifier var25 = ComposedModifierKt.materializeModifier(var20, var82);
         Function0 var26 = ComposeUiNode.Companion.getConstructor();
         Composer var27 = var20;
         int var28 = 6 | 896 & var21 << 6;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var27.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var27.startReusableNode();
         if (var27.getInserting()) {
            var27.createNode(var26);
         } else {
            var27.useNode();
         }

         Composer var30 = Updater.recovered$676aa17b44fd6280(var27);
         int var31 = 0;
         Updater.recovered$89682b199a142880(var30, var19, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var30, var24, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var30, var23, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var30, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var30, var25, ComposeUiNode.Companion.getSetModifier());
         int var32 = 14 & var28 >> 6;
         Composer var33 = var27;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var35 = 6 | 112 & var74 >> 6;
         Composer var36 = var33;
         RowScope var37 = RowScopeInstance.INSTANCE;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, 302601521, "C40@1468L269,50@1747L1304:PreviewTopBar.kt#hhbc0c");
         IconButtonKt.IconButton(
            var4,
            null,
            false,
            null,
            null,
            null,
            ComposableSingletons$PreviewTopBarKt.INSTANCE.recovered$eac478aa65045e5d(),
            var36,
            1572864 | 14 & var11 >> 12,
            62
         );
         Vertical var39 = Alignment.Companion.getCenterVertically();
         short var40 = 384;
         int $i$f$Rowx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var36, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var42 = Modifier.Companion;
         Horizontal var43 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var43, var39, var36, 14 & var40 >> 3 | 112 & var40 >> 3);
         Modifier var45 = var42;
         MeasurePolicy var46 = measurePolicy$ivx;
         Composer var47 = var36;
         int var48 = 112 & var40 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var47, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var47, 0));
         CompositionLocalMap localMap$iv$ivx = var47.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var47, var45);
         Function0 var53 = ComposeUiNode.Companion.getConstructor();
         Composer var54 = var47;
         int var55 = 6 | 896 & var48 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var54, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var54.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var54.startReusableNode();
         if (var54.getInserting()) {
            var54.createNode(var53);
         } else {
            var54.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var54);
         int var58/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var46, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var59 = 14 & var55 >> 6;
         Composer $composer$ivx = var54;
         int var61/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var62 = 6 | 112 & var40 >> 6;
         Composer var63 = $composer$ivx;
         RowScope var64 = RowScopeInstance.INSTANCE;
         int var65 = 0;
         ComposerKt.sourceInformationMarkerStart(var63, -601807924, "C:PreviewTopBar.kt#hhbc0c");
         var63.startReplaceGroup(2058796008);
         ComposerKt.sourceInformation(var63, "54@1876L336");
         if (var1) {
            IconButtonKt.IconButton(
               var5,
               null,
               false,
               null,
               null,
               null,
               ComposableSingletons$PreviewTopBarKt.INSTANCE.recovered$1400af5e34386102(),
               var63,
               1572864 | 14 & var11 >> 15,
               62
            );
         }

         var63.endReplaceGroup();
         var63.startReplaceGroup(2058808930);
         ComposerKt.sourceInformation(var63, "66@2278L332");
         if (var2) {
            IconButtonKt.IconButton(
               var6,
               null,
               false,
               null,
               null,
               null,
               ComposableSingletons$PreviewTopBarKt.INSTANCE.recovered$fd838d44303763da(),
               var63,
               1572864 | 14 & var11 >> 18,
               62
            );
         }

         var63.endReplaceGroup();
         var63.startReplaceGroup(2058821685);
         ComposerKt.sourceInformation(var63, "78@2680L347");
         if (var3) {
            IconButtonKt.IconButton(
               var7,
               null,
               false,
               null,
               null,
               null,
               ComposableSingletons$PreviewTopBarKt.INSTANCE.recovered$54bdcf11cafa5012(),
               var63,
               1572864 | 14 & var11 >> 21,
               62
            );
         }

         var63.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var63);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var54.endNode();
         ComposerKt.sourceInformationMarkerEnd(var54);
         ComposerKt.sourceInformationMarkerEnd(var47);
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var33);
         var27.endNode();
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var84 = var8.endRestartGroup();
      if (var84 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1160_0 = var0;
         final boolean recoveredCapture_1160_1 = var1;
         final boolean recoveredCapture_1160_2 = var2;
         final boolean recoveredCapture_1160_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_1160_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_1160_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_1160_6 = var6;
         final kotlin.jvm.functions.Function0 recoveredCapture_1160_7 = var7;
         final int recoveredCapture_1160_8 = var9;
         final int recoveredCapture_1160_9 = var10;
         var84.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1160_0, recoveredArgument_1160_1) -> PreviewTopBarKt.PreviewTopBar$lambda$6(recoveredCapture_1160_0, recoveredCapture_1160_1, recoveredCapture_1160_2, recoveredCapture_1160_3, recoveredCapture_1160_4, recoveredCapture_1160_5, recoveredCapture_1160_6, recoveredCapture_1160_7, recoveredCapture_1160_8, recoveredCapture_1160_9, (androidx.compose.runtime.Composer)recoveredArgument_1160_0, ((java.lang.Integer)recoveredArgument_1160_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewTopBarPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-309143356);
      ComposerKt.sourceInformation(var0, "C(PreviewTopBarPreview)95@3132L283:PreviewTopBar.kt#hhbc0c");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-309143356, var1, -1, "io.intercom.android.sdk.m5.preview.ui.PreviewTopBarPreview (PreviewTopBar.kt:94)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$PreviewTopBarKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1163_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1163_0, recoveredArgument_1163_1) -> PreviewTopBarKt.PreviewTopBarPreview$lambda$7(recoveredCapture_1163_0, (androidx.compose.runtime.Composer)recoveredArgument_1163_0, ((java.lang.Integer)recoveredArgument_1163_1).intValue())));
      }
   }

   private static final Unit PreviewTopBar$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit PreviewTopBar$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit PreviewTopBar$lambda$6(
      Modifier var0,
      boolean var1,
      boolean var2,
      boolean var3,
      Function0 var4,
      Function0 var5,
      Function0 var6,
      Function0 var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      PreviewTopBar(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewTopBarPreview$lambda$7(int var0, Composer var1, int var2) {
      PreviewTopBarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
