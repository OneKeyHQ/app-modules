package io.intercom.android.sdk.m5.preview.util;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "PdfUtils.kt", l = 191, i = {}, s = {}, n = {}, m = "shouldOpenPdfInBrowser", c = "io.intercom.android.sdk.m5.preview.util.PdfUtilsKt")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class PdfUtilsKt$shouldOpenPdfInBrowser$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   int label;

   PdfUtilsKt$shouldOpenPdfInBrowser$1(Continuation var1) {
      super(var1);
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return PdfUtilsKt.shouldOpenPdfInBrowser(null, null, this);
   }
}
