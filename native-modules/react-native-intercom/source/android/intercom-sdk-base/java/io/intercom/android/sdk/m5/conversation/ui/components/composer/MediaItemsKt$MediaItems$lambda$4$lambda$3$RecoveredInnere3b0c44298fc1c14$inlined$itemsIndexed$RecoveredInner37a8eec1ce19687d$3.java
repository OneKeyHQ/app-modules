package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt$itemsIndexed$4\n+ 2 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,523:1\n68#2,3:524\n73#2,4:533\n1047#3,6:527\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/MediaItemsKt\n*L\n70#1:527,6\n*E\n"
)
public final class MediaItemsKt$MediaItems$lambda$4$lambda$3$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3
   implements Function4 {
   // $VF: synthetic field
   final List $items;
   // $VF: synthetic field
   final Function1 $onItemClick$inlined;
   // $VF: synthetic field
   final Function1 $onItemDeleteClick$inlined;
   // $VF: synthetic field
   final Function1 $onFailedItemClick$inlined;

   public MediaItemsKt$MediaItems$lambda$4$lambda$3$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
      List var1, Function1 var2, Function1 var3, Function1 var4
   ) {
      this.$items = var1;
      this.$onItemClick$inlined = var2;
      this.$onItemDeleteClick$inlined = var3;
      this.$onFailedItemClick$inlined = var4;
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
         MediaUploadItem var8 = (MediaUploadItem)var10002;
         int var9 = var2;
         int var11 = 0;
         var7.startReplaceGroup(-1437731301);
         ComposerKt.sourceInformation(var7, "C*69@2957L58,67@2860L280:MediaItems.kt#yb6g9");
         MediaUploadItem var10000 = var8;
         var7.startReplaceGroup(1200550442);
         ComposerKt.sourceInformation(var7, "CC(remember):MediaItems.kt#9igjgp");
         Composer var12 = var7;
         boolean var13 = var7.changed(this.$onItemClick$inlined) | ((var6 & 112 ^ 48) > 32 && var7.changed(var9) || (var6 & 48) == 32);
         int var14 = 0;
         Object var15 = var12.rememberedValue();
         int var16 = 0;
         Object var21;
         if (!var13 && var15 != Composer.Companion.getEmpty()) {
            var21 = (Function0)var15;
         } else {
            MediaUploadItem var17 = var8;
            int var18 = 0;
            var21 = new MediaItemsKt$MediaItems$1$1$2$1$1(this.$onItemClick$inlined, var9);
            var10000 = var17;
            Object var19 = var21;
            var12.updateRememberedValue(var19);
            var21 = (Function0)var19;
         }

         Function0 var20 = (kotlin.jvm.functions.Function0) var21;
         var7.endReplaceGroup();
         MediaItemsKt.access$MediaItemWithState(var10000, var20, this.$onItemDeleteClick$inlined, this.$onFailedItemClick$inlined, var7, 14 & var6 >> 6);
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
