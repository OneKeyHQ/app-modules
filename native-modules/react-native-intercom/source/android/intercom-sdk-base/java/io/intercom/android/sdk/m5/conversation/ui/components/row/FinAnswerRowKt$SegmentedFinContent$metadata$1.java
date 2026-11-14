package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.AiAnswerInfo;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nFinAnswerRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinAnswerRowKt$SegmentedFinContent$metadata$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,538:1\n1563#2:539\n1634#2,3:540\n*S KotlinDebug\n*F\n+ 1 FinAnswerRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/FinAnswerRowKt$SegmentedFinContent$metadata$1\n*L\n133#1:539\n133#1:540,3\n*E\n"
)
final class FinAnswerRowKt$SegmentedFinContent$metadata$1 implements Function3 {
   // $VF: synthetic field
   final boolean $suppressAvatarHeader;
   // $VF: synthetic field
   final Part $part;
   // $VF: synthetic field
   final boolean $showReportAiAnswerButton;
   // $VF: synthetic field
   final Function0 $onReportAiAnswer;

   FinAnswerRowKt$SegmentedFinContent$metadata$1(boolean var1, Part var2, boolean var3, Function0 var4) {
      this.$suppressAvatarHeader = var1;
      this.$part = var2;
      this.$showReportAiAnswerButton = var3;
      this.$onReportAiAnswer = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(boolean var1, Composer var2, int var3) {
      ComposerKt.sourceInformation(var2, "C*131@5932L475:FinAnswerRow.kt#hr3bi5");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2012690107, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.SegmentedFinContent.<anonymous> (FinAnswerRow.kt:129)"
            );
         }

         if (!this.$suppressAvatarHeader) {
            io.intercom.android.sdk.models.Metadata var10000 = this.$part.getMetadata();
            if (var10000 != null) {
               io.intercom.android.sdk.models.Metadata var5 = var10000;
               Part var6 = this.$part;
               boolean var7 = this.$showReportAiAnswerButton;
               Function0 var8 = this.$onReportAiAnswer;
               io.intercom.android.sdk.models.Metadata var9 = var5;
               int var10 = 0;
               Iterable var11 = var9.getAvatars();
               int var12 = 0;
               Iterable var13 = var11;
               ArrayList var14 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10));
               int var15 = 0;

               for (Object var17 : var13) {
                  Avatar var18 = (Avatar)var17;
                  Collection var19 = var14;
                  int var20 = 0;
                  var19.add(new AvatarWrapper(var18, false, 2, null));
               }

               List var21 = (List)var14;
               String var22 = var9.getTitle();
               String var23 = var9.getSuffix();
               Long var24 = var9.getTimestamp();
               AiAnswerInfo var25 = var6.getAiAnswerInfo();
               FinAnswerRowKt.FinAnswerMetadata(var21, var22, null, var23, var24, var25, var7, var8, var1, var2, 234881024 & var4 << 24, 4);
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
