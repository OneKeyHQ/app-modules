package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.gestures.TapGestureDetectorKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.PointerInputEventHandler;
import androidx.compose.ui.input.pointer.PointerInputScope;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ClickableMessageRowKt$ClickableMessageRow$1$1 implements PointerInputEventHandler {
   // $VF: synthetic field
   final Function0 $onLongClick;
   // $VF: synthetic field
   final Function0 $onClick;

   ClickableMessageRowKt$ClickableMessageRow$1$1(Function0 var1, Function0 var2) {
      this.$onLongClick = var1;
      this.$onClick = var2;
   }

   @Override
   public final Object invoke(PointerInputScope var1, Continuation var2) {
      final kotlin.jvm.functions.Function0 recoveredExpressionCapture_686_0 = ((io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRowKt$ClickableMessageRow$1$1) this).$onLongClick;
      Object var10000 = TapGestureDetectorKt.detectTapGestures$default(
         var1,
         null,
         ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_686_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRowKt$ClickableMessageRow$1$1.invoke$lambda$0(recoveredExpressionCapture_686_0, (androidx.compose.ui.geometry.Offset) ((androidx.compose.ui.geometry.Offset)recoveredExpressionArgument_686_0))),
         null,
         io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRowKt$ClickableMessageRow$1$1.recoveredExpressionFactory_685(((io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRowKt$ClickableMessageRow$1$1) this).$onClick),
         var2,
         5,
         null
      );
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$0(Function0 var0, Offset var1) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$1(Function0 var0, Offset var1) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static kotlin.jvm.functions.Function1 recoveredExpressionFactory_685(kotlin.jvm.functions.Function0 recoveredExpressionCapture_685_0) {
      return ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_685_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ClickableMessageRowKt$ClickableMessageRow$1$1.invoke$lambda$1(recoveredExpressionCapture_685_0, (androidx.compose.ui.geometry.Offset) ((androidx.compose.ui.geometry.Offset)recoveredExpressionArgument_685_0)));
   }
}
