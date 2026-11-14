package io.intercom.android.sdk.utilities;

import kotlin.Metadata;
import kotlin.coroutines.AbstractCoroutineContextElement;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineExceptionHandler;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000!\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000*\u0001\u0000\b\n\u0018\u00002\u00020\u00012\u00020\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\t¸\u0006\u0000",
   d2 = {
         "kotlinx/coroutines/CoroutineExceptionHandlerKt$CoroutineExceptionHandler$1",
         "Lkotlin/coroutines/AbstractCoroutineContextElement;",
         "Lkotlinx/coroutines/CoroutineExceptionHandler;",
         "handleException",
         "",
         "context",
         "Lkotlin/coroutines/CoroutineContext;",
         "exception",
         "",
         "kotlinx-coroutines-core"
   }
)
@SourceDebugExtension(
   "SMAP\nCoroutineExceptionHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt$CoroutineExceptionHandler$1\n+ 2 CoroutineExceptionHandlers.kt\nio/intercom/android/sdk/utilities/CoroutineExceptionHandlersKt\n*L\n1#1,48:1\n17#2,2:49\n*E\n"
)
public final class CoroutineExceptionHandlersKt$loggingCoroutineExceptionHandler$RecoveredInnere3b0c44298fc1c14$inlined$CoroutineExceptionHandler$1
   extends AbstractCoroutineContextElement
   implements CoroutineExceptionHandler {
   // $VF: synthetic field
   final String $scopeName$inlined;

   public CoroutineExceptionHandlersKt$loggingCoroutineExceptionHandler$RecoveredInnere3b0c44298fc1c14$inlined$CoroutineExceptionHandler$1(
      CoroutineExceptionHandler.Key var1, String var2
   ) {
      super(var1);
      this.$scopeName$inlined = var2;
   }

   @Override
   public void handleException(CoroutineContext var1, Throwable var2) {
      Throwable var3 = var2;
      int var4 = 0;
      CoroutineExceptionHandlersKt.access$getTwig$p().w(var3, "Unhandled exception in " + this.$scopeName$inlined);
   }
}
