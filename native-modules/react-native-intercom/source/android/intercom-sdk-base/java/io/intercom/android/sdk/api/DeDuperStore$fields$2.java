package io.intercom.android.sdk.api;

import androidx.datastore.preferences.core.Preferences;
import io.intercom.android.sdk.utilities.CryptoHelper;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;

@DebugMetadata(f = "DeDuperStore.kt", l = 50, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.api.DeDuperStore$fields$2")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u0006\n\u0000\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/api/DeDuperStore$DeDuperFields;"}
)
@SourceDebugExtension(
   "SMAP\nDeDuperStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeDuperStore.kt\nio/intercom/android/sdk/api/DeDuperStore$fields$2\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,177:1\n1#2:178\n*E\n"
)
final class DeDuperStore$fields$2 extends SuspendLambda implements Function1 {
   int label;
   // $VF: synthetic field
   final DeDuperStore this$0;

   DeDuperStore$fields$2(DeDuperStore var1, Continuation var2) {
      super(1, var2);
      this.this$0 = var1;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var13 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            try {
               Flow var25 = DeDuperStore.access$getDataStore$p(this.this$0).getData();
               Continuation var10001 = this;
               this.label = 1;
               var10000 = FlowKt.first(var25, var10001);
            } catch (CancellationException var16) {
               throw var16;
            } catch (Exception var17) {
               DeDuperStore.access$getTwig$cp().w("Failed to read deduper fields from DataStore: " + var17.getMessage());
               return new DeDuperStore$DeDuperFields(null, 0L, 3, null);
            }

            if (var10000 == var13) {
               return var13;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            } catch (CancellationException var18) {
               throw var18;
            } catch (Exception var19) {
               DeDuperStore.access$getTwig$cp().w("Failed to read deduper fields from DataStore: " + var19.getMessage());
               return new DeDuperStore$DeDuperFields(null, 0L, 3, null);
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Preferences var2;
      DeDuperStore$DeDuperFields recoveredFields;
      try {
         String var27;
         label52: {
            var2 = (Preferences)var10000;
            String var3 = (String)var2.get(DeDuperStore.access$getKEY_CACHED_ATTRIBUTES$cp());
            if (var3 != null) {
               String var5 = var3;
               int var6 = 0;
               CharSequence var7 = CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var5);
               CharSequence var26;
               if (var7.length() == 0) {
                  int var8 = 0;
                  var26 = "{}";
               } else {
                  var26 = var7;
               }

               String var4 = (String)var26;
               if (var4 != null) {
                  var27 = var4;
                  break label52;
               }
            }

            var27 = "{}";
         }

         long var29;
         label46: {
            String var21 = (String)var2.get(DeDuperStore.access$getKEY_EARLIEST_UPDATE_AT$cp());
            if (var21 != null) {
               String var23 = var21;
               String var9 = var27;
               int var24 = 0;
               Long var28 = StringsKt.toLongOrNull(CryptoHelper.INSTANCE.decrypt$intercom_sdk_base_release(var23));
               var27 = var9;
               Long var22 = var28;
               if (var22 != null) {
                  var29 = var22;
                  break label46;
               }
            }

            var29 = 0L;
         }

         long var10 = var29;
         String var12 = var27;
         recoveredFields = new DeDuperStore$DeDuperFields(var12, var10);
      } catch (CancellationException var14) {
         throw var14;
      } catch (Exception var15) {
         DeDuperStore.access$getTwig$cp().w("Failed to read deduper fields from DataStore: " + var15.getMessage());
         recoveredFields = new DeDuperStore$DeDuperFields(null, 0L, 3, null);
      }

      return recoveredFields;
   }

   @Override
   public final Continuation create(Continuation var1) {
      return new DeDuperStore$fields$2(this.this$0, var1);
   }

   public final Object invoke(Continuation var1) {
      return ((DeDuperStore$fields$2)this.create(var1)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      return this.invoke((Continuation)var1);
   }
}
