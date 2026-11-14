package io.intercom.android.sdk.utilities;

import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineExceptionHandler;
import kotlinx.coroutines.CoroutineExceptionHandler.Key;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0006",
   d2 = {
         "twig",
         "Lcom/intercom/twig/Twig;",
         "loggingCoroutineExceptionHandler",
         "Lkotlinx/coroutines/CoroutineExceptionHandler;",
         "scopeName",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCoroutineExceptionHandlers.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CoroutineExceptionHandlers.kt\nio/intercom/android/sdk/utilities/CoroutineExceptionHandlersKt\n+ 2 CoroutineExceptionHandler.kt\nkotlinx/coroutines/CoroutineExceptionHandlerKt\n*L\n1#1,19:1\n46#2,4:20\n*S KotlinDebug\n*F\n+ 1 CoroutineExceptionHandlers.kt\nio/intercom/android/sdk/utilities/CoroutineExceptionHandlersKt\n*L\n16#1:20,4\n*E\n"
)
public final class CoroutineExceptionHandlersKt {
   @NotNull
   private static final Twig twig;

   @NotNull
   public static final CoroutineExceptionHandler loggingCoroutineExceptionHandler(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "scopeName");
      int var1 = 0;
      Key var2 = CoroutineExceptionHandler.Key;
      return new CoroutineExceptionHandlersKt$loggingCoroutineExceptionHandler$RecoveredInnere3b0c44298fc1c14$inlined$CoroutineExceptionHandler$1(var2, var0);
   }

   // $VF: synthetic method
   public static final Twig access$getTwig$p() {
      return twig;
   }

   static {
      Twig var10000 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      twig = var10000;
   }
}
