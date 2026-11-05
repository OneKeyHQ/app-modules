package io.intercom.android.sdk.api;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.PreferencesKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "WrapperPrefsStore.kt",
   l = 107,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.api.WrapperPrefsStore$setReactNativeVersion$2"
)
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48, d1 = "\u0000\u0006\n\u0000\n\u0002\u0010\u0002\u0010\u0000\u001a\u00020\u0001H\n", d2 = {"<anonymous>", ""})
final class WrapperPrefsStore$setReactNativeVersion$2 extends SuspendLambda implements Function1 {
   int label;
   // $VF: synthetic field
   final WrapperPrefsStore this$0;
   // $VF: synthetic field
   final String $version;

   WrapperPrefsStore$setReactNativeVersion$2(WrapperPrefsStore var1, String var2, Continuation var3) {
      super(1, var3);
      this.this$0 = var1;
      this.$version = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            DataStore var10000 = WrapperPrefsStore.access$getDataStore$p(this.this$0);
            Function2 var10001 = new WrapperPrefsStore$setReactNativeVersion$2$1(this.$version, null);
            Continuation var10002 = this;
            this.label = 1;
            if (PreferencesKt.edit(var10000, var10001, var10002) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Continuation var1) {
      return new WrapperPrefsStore$setReactNativeVersion$2(this.this$0, this.$version, var1);
   }

   public final Object invoke(Continuation var1) {
      return ((WrapperPrefsStore$setReactNativeVersion$2)this.create(var1)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke((Continuation)var1);
   }
}
