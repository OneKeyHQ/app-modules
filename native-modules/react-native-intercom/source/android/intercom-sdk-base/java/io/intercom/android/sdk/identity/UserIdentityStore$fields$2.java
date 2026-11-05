package io.intercom.android.sdk.identity;

import androidx.datastore.preferences.core.Preferences;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(
   f = "UserIdentityStore.kt",
   l = 54,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.identity.UserIdentityStore$fields$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u0006\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/identity/UserIdentityStore$UserFields;"}
)
final class UserIdentityStore$fields$2 extends SuspendLambda implements Function1 {
   int label;
   // $VF: synthetic field
   final UserIdentityStore this$0;

   UserIdentityStore$fields$2(UserIdentityStore var1, Continuation var2) {
      super(1, var2);
      this.this$0 = var1;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            try {
               Flow var12 = UserIdentityStore.access$getDataStore$p(this.this$0).getData();
               Continuation var10001 = this;
               this.label = 1;
               var10000 = FlowKt.first(var12, var10001);
            } catch (CancellationException var7) {
               throw var7;
            } catch (Exception var8) {
               UserIdentityStore.access$getTwig$p(this.this$0).w("Failed to load user fields from DataStore, returning defaults: " + var8.getMessage());
               return new UserIdentityStore$UserFields(null, null, null, null, null, null, null, 127, null);
            }

            if (var10000 == var4) {
               return var4;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            } catch (CancellationException var9) {
               throw var9;
            } catch (Exception var10) {
               UserIdentityStore.access$getTwig$p(this.this$0).w("Failed to load user fields from DataStore, returning defaults: " + var10.getMessage());
               return new UserIdentityStore$UserFields(null, null, null, null, null, null, null, 127, null);
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      UserIdentityStore$UserFields var2;
      try {
         Preferences var11 = (Preferences)var10000;
         var2 = new UserIdentityStore$UserFields(
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_ANONYMOUS_ID$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_INTERCOM_ID$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_USER_ID$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_EMAIL$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_HMAC$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_JWT$cp()),
            UserIdentityStore.access$decryptField(this.this$0, var11, UserIdentityStore.access$getKEY_MESSENGER_ENVIRONMENT$cp())
         );
      } catch (CancellationException var5) {
         throw var5;
      } catch (Exception var6) {
         UserIdentityStore.access$getTwig$p(this.this$0).w("Failed to load user fields from DataStore, returning defaults: " + var6.getMessage());
         var2 = new UserIdentityStore$UserFields(null, null, null, null, null, null, null, 127, null);
      }

      return var2;
   }

   @Override
   public final Continuation create(Continuation var1) {
      return new UserIdentityStore$fields$2(this.this$0, var1);
   }

   public final Object invoke(Continuation var1) {
      return ((UserIdentityStore$fields$2)this.create(var1)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke((Continuation)var1);
   }
}
