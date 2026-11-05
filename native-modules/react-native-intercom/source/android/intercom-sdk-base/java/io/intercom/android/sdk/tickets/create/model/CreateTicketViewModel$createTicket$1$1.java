package io.intercom.android.sdk.tickets.create.model;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.survey.QuestionState;
import io.intercom.android.sdk.survey.ValidationError$NoValidationError;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.MutableSharedFlow;

@DebugMetadata(
   f = "CreateTicketViewModel.kt",
   l = {160, 167},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nCreateTicketViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$createTicket$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,499:1\n1869#2,2:500\n1740#2,3:502\n230#2,2:505\n1#3:507\n*S KotlinDebug\n*F\n+ 1 CreateTicketViewModel.kt\nio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$createTicket$1$1\n*L\n143#1:500,2\n147#1:502,3\n149#1:505,2\n*E\n"
)
final class CreateTicketViewModel$createTicket$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final CreateTicketViewModel$CreateTicketFormUiState$Content $content;
   // $VF: synthetic field
   final CreateTicketViewModel this$0;
   // $VF: synthetic field
   final CoroutineScope $compositionAwareScope;

   CreateTicketViewModel$createTicket$1$1(
      CreateTicketViewModel$CreateTicketFormUiState$Content var1, CreateTicketViewModel var2, CoroutineScope var3, Continuation var4
   ) {
      super(2, var4);
      this.$content = var1;
      this.this$0 = var2;
      this.$compositionAwareScope = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Iterable var2 = this.$content.getQuestions();
            int var3 = 0;

            for (Object var5 : var2) {
               QuestionState var6 = (QuestionState)var5;
               int var7 = 0;
               var6.validate();
            }

            var var11 = this.$content.getQuestions();
            int var15 = 0;
            boolean var29;
            if (var11 instanceof Collection && ((Collection)var11).isEmpty()) {
               var29 = true;
            } else {
               Iterator var18 = var11.iterator();

               while (true) {
                  if (!var18.hasNext()) {
                     var29 = true;
                     break;
                  }

                  Object var21 = var18.next();
                  QuestionState var24 = (QuestionState)var21;
                  int var8 = 0;
                  if (!(var24.getValidationError() instanceof ValidationError$NoValidationError)) {
                     var29 = false;
                     break;
                  }
               }
            }

            boolean var10 = var29;
            if (!var10) {
               Iterable var13 = this.$content.getQuestions();
               var15 = 0;

               for (Object var22 : var13) {
                  QuestionState var25 = (QuestionState)var22;
                  int var27 = 0;
                  if (!(var25.getValidationError() instanceof ValidationError$NoValidationError)) {
                     Object var14 = var22;
                     CoroutineScope var17 = this.$compositionAwareScope;
                     QuestionState var20 = (QuestionState)var14;
                     boolean var23 = false;
                     if (var17 != null) {
                        CoroutineScope var26 = var17;
                        var27 = 0;
                        var20.bringIntoView(var26);
                     }

                     return Unit.INSTANCE;
                  }
               }

               throw new NoSuchElementException("Collection contains no element matching the predicate.");
            }

            CreateTicketViewModel.access$withState(this.this$0, io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1.recoveredExpressionFactory_1565(((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1) this).this$0));
            TicketRepository var30 = CreateTicketViewModel.access$getTicketRepository$p(this.this$0);
            String var10001 = CreateTicketViewModel.access$getConversationId$p(this.this$0);
            long var10002 = CreateTicketViewModel.access$getTicketTypeId$p(this.this$0);
            List var10003 = CreateTicketViewModel.access$getAttributeRequest(this.this$0);
            Continuation var10004 = this;
            this.label = 1;
            var10000 = var30.createTicket(var10001, var10002, var10003, var10004);
            if (var10000 == var9) {
               return var9;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var12 = (NetworkResponse)var10000;
      if (!(var12 instanceof NetworkResponse$Success)) {
         CreateTicketViewModel.access$withState(this.this$0, io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1.recoveredExpressionFactory_1566(((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1) this).this$0));
      } else {
         MutableSharedFlow var31 = CreateTicketViewModel.access$get_effect$p(this.this$0);
         CreateTicketViewModel$TicketSideEffect$Finish var32 = CreateTicketViewModel$TicketSideEffect$Finish.INSTANCE;
         Continuation var33 = this;
         this.label = 2;
         if (var31.emit(var32, var33) == var9) {
            return var9;
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new CreateTicketViewModel$createTicket$1$1(this.$content, this.this$0, this.$compositionAwareScope, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((CreateTicketViewModel$createTicket$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   private static final Unit invokeSuspend$lambda$5(CreateTicketViewModel var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      CreateTicketViewModel.access$get_uiState$p(var0)
         .setValue(CreateTicketViewModel$CreateTicketFormUiState$Content.copy$default(var1, null, null, true, false, 11, null));
      return Unit.INSTANCE;
   }

   private static final Unit invokeSuspend$lambda$6(CreateTicketViewModel var0, CreateTicketViewModel$CreateTicketFormUiState$Content var1) {
      CreateTicketViewModel.access$get_uiState$p(var0)
         .setValue(CreateTicketViewModel$CreateTicketFormUiState$Content.copy$default(var1, null, null, false, false, 11, null));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }

   private static kotlin.jvm.functions.Function1 recoveredExpressionFactory_1565(io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1565_0) {
      return ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1565_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1.invokeSuspend$lambda$5(recoveredExpressionCapture_1565_0, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1565_0)));
   }

   private static kotlin.jvm.functions.Function1 recoveredExpressionFactory_1566(io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredExpressionCapture_1566_0) {
      return ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1566_0) -> io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$createTicket$1$1.invokeSuspend$lambda$6(recoveredExpressionCapture_1566_0, (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content) ((io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Content)recoveredExpressionArgument_1566_0)));
   }
}
