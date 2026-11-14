package io.intercom.android.sdk.m5.data;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u001aT\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032 \b\u0004\u0010\u0004\u001a\u001a\u0012\u0004\u0012\u0002H\u0002\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0002\b\u0003\u0018\u00010\u00070\u00060\u00052\u0014\b\u0004\u0010\b\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u00020\u0005H\u0080\bø\u0001\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\t",
   d2 = {
         "resetIfUserJwtExpired",
         "",
         "T",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "responses",
         "Lkotlin/Function1;",
         "",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "reset",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nUserJwtExpiredReset.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserJwtExpiredReset.kt\nio/intercom/android/sdk/m5/data/UserJwtExpiredResetKt\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,19:1\n230#2,3:20\n233#2,2:26\n1761#3,3:23\n*S KotlinDebug\n*F\n+ 1 UserJwtExpiredReset.kt\nio/intercom/android/sdk/m5/data/UserJwtExpiredResetKt\n*L\n13#1:20,3\n13#1:26,2\n14#1:23,3\n*E\n"
)
public final class UserJwtExpiredResetKt {
   public static final boolean resetIfUserJwtExpired(@NotNull MutableStateFlow var0, @NotNull Function1 var1, @NotNull Function1 var2) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "responses");
      Intrinsics.checkNotNullParameter(var2, "reset");
      int var3 = 0;
      boolean var4 = false;
      MutableStateFlow var5 = var0;
      int var6 = 0;

      Object var7;
      Object var16;
      do {
         var7 = var5.getValue();
         Object var8 = var7;
         int var9 = 0;
         Iterable var10 = (Iterable)var1.invoke(var8);
         int var11 = 0;
         boolean var10000;
         if (var10 instanceof Collection && ((Collection)var10).isEmpty()) {
            var10000 = false;
         } else {
            label47: {
               for (Object var13 : var10) {
                  NetworkResponse var14 = (NetworkResponse)var13;
                  int var15 = 0;
                  if (var14 != null ? NetworkResponseKt.isUserJwtExpired(var14) : false) {
                     var10000 = true;
                     break label47;
                  }
               }

               var10000 = false;
            }
         }

         var4 = var10000;
         var16 = var4 ? var2.invoke(var8) : var8;
      } while (!var5.compareAndSet(var7, var16));

      return var4;
   }
}
