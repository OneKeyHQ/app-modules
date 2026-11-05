package io.intercom.android.sdk.api;

import androidx.datastore.preferences.core.MutablePreferences;
import io.intercom.android.sdk.utilities.CryptoHelper;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(f = "DeDuperStore.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.api.DeDuperStore$save$1$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\n",
   d2 = {"<anonymous>", "", "prefs", "Landroidx/datastore/preferences/core/MutablePreferences;"}
)
final class DeDuperStore$save$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   Object L$0;
   // $VF: synthetic field
   final String $cachedAttributesJson;
   // $VF: synthetic field
   final long $earliestUpdateAt;

   DeDuperStore$save$1$1(String var1, long var2, Continuation var4) {
      super(2, var4);
      this.$cachedAttributesJson = var1;
      this.$earliestUpdateAt = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            MutablePreferences var2 = (MutablePreferences)this.L$0;
            var2.set(DeDuperStore.access$getKEY_CACHED_ATTRIBUTES$cp(), CryptoHelper.INSTANCE.encrypt$intercom_sdk_base_release(this.$cachedAttributesJson));
            var2.set(
               DeDuperStore.access$getKEY_EARLIEST_UPDATE_AT$cp(),
               CryptoHelper.INSTANCE.encrypt$intercom_sdk_base_release(String.valueOf(this.$earliestUpdateAt))
            );
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      DeDuperStore$save$1$1 var3 = new DeDuperStore$save$1$1(this.$cachedAttributesJson, this.$earliestUpdateAt, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(MutablePreferences var1, Continuation var2) {
      return ((DeDuperStore$save$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((MutablePreferences)var1, (Continuation)var2);
   }
}
