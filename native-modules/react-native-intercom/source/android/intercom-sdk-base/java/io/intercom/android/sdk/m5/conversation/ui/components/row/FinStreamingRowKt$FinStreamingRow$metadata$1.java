package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.StreamingPart;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFinStreamingRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinStreamingRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinStreamingRowKt$FinStreamingRow$metadata$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,307:1\n1563#2:308\n1634#2,3:309\n*S KotlinDebug\n*F\n+ 1 FinStreamingRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinStreamingRowKt$FinStreamingRow$metadata$1\n*L\n48#1:308\n48#1:309,3\n*E\n"
)
final class FinStreamingRowKt$FinStreamingRow$metadata$1 implements Function3 {
   // $VF: synthetic field
   final boolean $suppressAvatarHeader;
   // $VF: synthetic field
   final StreamingPart $streamingPart;

   FinStreamingRowKt$FinStreamingRow$metadata$1(boolean var1, StreamingPart var2) {
      this.$suppressAvatarHeader = var1;
      this.$streamingPart = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(boolean var1, Composer var2, int var3) {
      ComposerKt.sourceInformation(var2, "C*46@2094L332:FinStreamingRow.kt#hr3bi5");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1358447106, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.FinStreamingRow.<anonymous> (FinStreamingRow.kt:44)"
            );
         }

         if (!this.$suppressAvatarHeader) {
            io.intercom.android.sdk.models.Metadata var10000 = this.$streamingPart.getMetadata();
            if (var10000 != null) {
               io.intercom.android.sdk.models.Metadata var5 = var10000;
               int var6 = 0;
               Iterable var7 = var5.getAvatars();
               int var8 = 0;
               Iterable var9 = var7;
               ArrayList var10 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
               int var11 = 0;

               for (Object var13 : var9) {
                  Avatar var14 = (Avatar)var13;
                  Collection var15 = var10;
                  int var16 = 0;
                  var15.add(new AvatarWrapper(var14, false, 2, null));
               }

               FinAnswerRowKt.FinAnswerMetadata(
                  (List)var10,
                  var5.getTitle(),
                  null,
                  var5.getSuffix(),
                  var5.getTimestamp(),
                  null,
                  false,
                  null,
                  var1,
                  var2,
                  196608 | 234881024 & var4 << 24,
                  196
               );
            }
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke(((Boolean)var1).booleanValue(), (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
