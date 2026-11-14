package io.intercom.android.sdk.identity;

import android.content.Context;
import androidx.datastore.preferences.core.Preferences;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "AppIdentityStore.kt",
   l = 108,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.identity.AppIdentityStore$Companion$loadCachedCredentials$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/identity/AppIdentityStore$AppCredentials;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class AppIdentityStore$Companion$loadCachedCredentials$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Context $context;

   AppIdentityStore$Companion$loadCachedCredentials$1(Context var1, Continuation var2) {
      super(2, var2);
      this.$context = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Flow var4 = AppIdentityStore$Companion.access$getAppDataStore(AppIdentityStore.Companion, this.$context).getData();
            Continuation var10001 = this;
            this.label = 1;
            var10000 = FlowKt.first(var4, var10001);
            if (var10000 == var3) {
               return var3;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Preferences var2 = (Preferences)var10000;
      return new AppIdentityStore$AppCredentials(
         AppIdentityStore$Companion.access$decryptField(AppIdentityStore.Companion, var2, AppIdentityStore.access$getKEY_API_KEY$cp()),
         AppIdentityStore$Companion.access$decryptField(AppIdentityStore.Companion, var2, AppIdentityStore.access$getKEY_APP_ID$cp())
      );
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new AppIdentityStore$Companion$loadCachedCredentials$1(this.$context, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AppIdentityStore$Companion$loadCachedCredentials$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
