package io.intercom.android.sdk.identity;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(
   f = "DeviceIdentityStore.kt",
   l = {53, 69, 80},
   i = {0, 0, 1, 1, 2},
   s = {"L$0", "L$1", "L$0", "L$1", "L$0"},
   n = {"this", "context", "this", "newId", "fallback"},
   m = "loadDeviceIdentifier",
   c = "io.intercom.android.sdk.identity.DeviceIdentityStore"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class DeviceIdentityStore$loadDeviceIdentifier$1 extends ContinuationImpl {
   Object L$0;
   Object L$1;
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final DeviceIdentityStore this$0;
   int label;

   DeviceIdentityStore$loadDeviceIdentifier$1(DeviceIdentityStore var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return DeviceIdentityStore.access$loadDeviceIdentifier(this.this$0, null, this);
   }
}
