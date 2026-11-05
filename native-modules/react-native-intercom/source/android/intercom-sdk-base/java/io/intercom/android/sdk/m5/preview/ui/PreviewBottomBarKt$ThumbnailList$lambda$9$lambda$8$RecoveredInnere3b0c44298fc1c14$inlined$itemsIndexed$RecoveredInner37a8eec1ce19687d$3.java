package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.BorderKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt$itemsIndexed$4\n+ 2 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,523:1\n111#2,5:524\n116#2:530\n117#2:532\n119#2,3:571\n122#2,5:575\n127#2:584\n118#3:529\n118#3:531\n118#3:574\n1047#4,6:533\n70#5:539\n67#5,9:540\n77#5:583\n81#6,6:549\n88#6,6:564\n96#6:582\n402#7,9:555\n411#7:570\n412#7,2:580\n*S KotlinDebug\n*F\n+ 1 PreviewBottomBar.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewBottomBarKt\n*L\n115#1:529\n116#1:531\n121#1:574\n117#1:533,6\n113#1:539\n113#1:540,9\n113#1:583\n113#1:549,6\n113#1:564,6\n113#1:582\n113#1:555,9\n113#1:570\n113#1:580,2\n*E\n"
)
public final class PreviewBottomBarKt$ThumbnailList$lambda$9$lambda$8$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3
   implements Function4 {
   // $VF: synthetic field
   final List $items;
   // $VF: synthetic field
   final int $currentPage$inlined;
   // $VF: synthetic field
   final Function1 $onThumbnailClick$inlined;

   public PreviewBottomBarKt$ThumbnailList$lambda$9$lambda$8$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
      List var1, int var2, Function1 var3
   ) {
      this.$items = var1;
      this.$currentPage$inlined = var2;
      this.$onThumbnailClick$inlined = var3;
   }

   @Composable
   @FunctionKeyMeta(key = 2039820996, startOffset = 10658, endOffset = 10700)
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      ComposerKt.sourceInformation(var3, "CN(it)214@10668L26:LazyDsl.kt#428nma");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if (var3.shouldExecute((var5 & 147) != 146, var5 & 1)) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2039820996, var5, -1, "androidx.compose.foundation.lazy.itemsIndexed.<anonymous> (LazyDsl.kt:214)");
         }

         Object var10002 = this.$items.get(var2);
         int var6 = 14 & var5 | 112 & var5;
         Composer var7 = var3;
         IntercomPreviewFile var8 = (IntercomPreviewFile)var10002;
         int var9 = var2;
         int var11 = 0;
         var7.startReplaceGroup(1711032760);
         ComposerKt.sourceInformation(var7, "CP(1)*116@4198L27,112@4020L507:PreviewBottomBar.kt#hhbc0c");
         long var12 = this.$currentPage$inlined == var9 ? Color.Companion.recovered$95989221dad41c4d() : Color.Companion.recovered$562f21ab17d9b4c7();
         Modifier var10000 = Modifier.Companion;
         int var14 = 2;
         int var15 = 0;
         var10000 = BorderKt.recovered$1227359600bc50fd(var10000, Dp.recovered$95b3d103663bf060(var14), var12, RoundedCornerShapeKt.RoundedCornerShape(10));
         var14 = 4;
         var15 = 0;
         var10000 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var14));
         boolean var10001 = false;
         var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var7.startReplaceGroup(2133413120);
         ComposerKt.sourceInformation(var7, "CC(remember):PreviewBottomBar.kt#9igjgp");
         Composer var53 = var7;
         boolean var16 = var7.changed(this.$onThumbnailClick$inlined) | ((var6 & 112 ^ 48) > 32 && var7.changed(var9) || (var6 & 48) == 32);
         int var17 = 0;
         Object var18 = var53.rememberedValue();
         int var19 = 0;
         Object var10005;
         if (!var16 && var18 != Composer.Companion.getEmpty()) {
            var10005 = var18;
         } else {
            Object var20 = null;
            Object var21 = null;
            Object var22 = null;
            boolean var23 = false;
            Modifier var24 = var10000;
            int var25 = 0;
            Function0 var26 = new PreviewBottomBarKt$ThumbnailList$2$1$1$1$1(this.$onThumbnailClick$inlined, var9);
            var10000 = var24;
            var10001 = var23;
            var10002 = var22;
            var10003 = var21;
            var10004 = var20;
            Object var27 = var26;
            var53.updateRememberedValue(var27);
            var10005 = var27;
         }

         Function0 var50 = (Function0)var10005;
         var7.endReplaceGroup();
         Modifier var51 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var50, 15, null
         );
         byte var56 = 0;
         var18 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var7, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var54 = Alignment.Companion.getTopStart();
         var16 = false;
         MeasurePolicy var58 = BoxKt.maybeCachedBoxMeasurePolicy(var54, var16);
         Modifier var59 = var51;
         MeasurePolicy var60 = var58;
         Composer var28 = var7;
         int var29 = 112 & var56 << 3;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var31 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var28, 0));
         CompositionLocalMap var32 = var28.getCurrentCompositionLocalMap();
         Modifier var33 = ComposedModifierKt.materializeModifier(var28, var59);
         Function0 var34 = ComposeUiNode.Companion.getConstructor();
         Composer var35 = var28;
         int var36 = 6 | 896 & var29 << 6;
         int var37 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var35.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var35.startReusableNode();
         if (var35.getInserting()) {
            var35.createNode(var34);
         } else {
            var35.useNode();
         }

         Composer var38 = Updater.recovered$676aa17b44fd6280(var35);
         int var39 = 0;
         Updater.recovered$89682b199a142880(var38, var60, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var38, var32, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var38, var31, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var38, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var38, var33, ComposeUiNode.Companion.getSetModifier());
         int var40 = 14 & var36 >> 6;
         Composer var41 = var35;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var41, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var43 = 6 | 112 & var56 >> 6;
         Composer var44 = var41;
         BoxScope var45 = BoxScopeInstance.INSTANCE;
         int var46 = 0;
         ComposerKt.sourceInformationMarkerStart(var44, 120485953, "C118@4259L254:PreviewBottomBar.kt#hhbc0c");
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 48;
         int $i$f$getDpx = 0;
         PreviewUriKt.ThumbnailPreview(
            ClipKt.clip(SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), RoundedCornerShapeKt.RoundedCornerShape(10)),
            ContentScale.Companion.getCrop(),
            var8,
            var44,
            48 | 896 & var6,
            0
         );
         ComposerKt.sourceInformationMarkerEnd(var44);
         ComposerKt.sourceInformationMarkerEnd(var41);
         var35.endNode();
         ComposerKt.sourceInformationMarkerEnd(var35);
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var7);
         var7.endReplaceGroup();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      } else {
         var3.skipToGroupEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
