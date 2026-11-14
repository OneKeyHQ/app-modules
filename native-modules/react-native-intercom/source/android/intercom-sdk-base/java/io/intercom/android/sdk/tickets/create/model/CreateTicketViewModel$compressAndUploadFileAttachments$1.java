package io.intercom.android.sdk.tickets.create.model;

import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Queued;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Uploading;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$MediaItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "CreateTicketViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$compressAndUploadFileAttachments$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$compressAndUploadFileAttachments$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,499:1\n774#2:500\n865#2,2:501\n1563#2:503\n1634#2,3:504\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$compressAndUploadFileAttachments$1\n*L\n350#1:500\n350#1:501,2\n351#1:503\n351#1:504,3\n*E\n"
)
final class CreateTicketViewModel$compressAndUploadFileAttachments$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final List $mediaItems;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;

   CreateTicketViewModel$compressAndUploadFileAttachments$1(List var1, CreateTicketViewModel var2, Continuation var3) {
      super(2, var3);
      this.$mediaItems = var1;
      this.this$0 = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Iterable var3 = this.$mediaItems;
            int var4 = 0;
            Iterable var5 = var3;
            ArrayList var6 = new ArrayList();
            int var7 = 0;

            for (Object var9 : var5) {
               Answer$MediaAnswer$MediaItem var10 = (Answer$MediaAnswer$MediaItem)var9;
               int var11 = 0;
               if (Intrinsics.areEqual(var10.getUploadStatus(), Answer$MediaAnswer$FileUploadStatus$Queued.INSTANCE)) {
                  var6.add(var9);
               }
            }

            var3 = (Iterable & List)var6;
            var4 = 0;
            var5 = var3;
            var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var3, 10));
            var7 = 0;

            for (Object var19 : var5) {
               Answer$MediaAnswer$MediaItem var20 = (Answer$MediaAnswer$MediaItem)var19;
               Collection var12 = var6;
               int var21 = 0;
               var20.setUploadStatus(Answer$MediaAnswer$FileUploadStatus$Uploading.INSTANCE);
               var12.add(var20);
            }

            List var2 = (List)var6;
            BuildersKt.launch$default(
               ViewModelKt.getViewModelScope(this.this$0),
               CreateTicketViewModel.access$getDispatcher$p(this.this$0),
               null,
               new CreateTicketViewModel$compressAndUploadFileAttachments$1$1(var2, this.this$0, null),
               2,
               null
            );
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketViewModel$compressAndUploadFileAttachments$1(this.$mediaItems, this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$compressAndUploadFileAttachments$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
