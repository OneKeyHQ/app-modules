package io.intercom.android.sdk.ui.coil;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "PdfDecoder.kt", l = 34, i = 0, s = "L$0", n = "isSampled", m = "decode", c = "io.intercom.android.sdk.ui.coil.PdfDecoder")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PdfDecoder$decode$1 extends ContinuationImpl {
   Object L$0;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final PdfDecoder this$0;
   int label;

   PdfDecoder$decode$1(PdfDecoder var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return this.this$0.decode(this);
   }
}
