package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Avatar;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J8\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\fH\u0086B¢\u0006\u0002\u0010\u000eJ4\u0010\u000f\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\fH\u0082@¢\u0006\u0002\u0010\u000e¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/ShowAdminIsTypingUseCase;",
         "",
         "<init>",
         "()V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "isBot",
         "",
         "showAvatar",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/models/Avatar;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "sendAdminIndicator",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nShowAdminIsTypingUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShowAdminIsTypingUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/ShowAdminIsTypingUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,57:1\n230#2,5:58\n230#2,5:63\n*S KotlinDebug\n*F\n+ 1 ShowAdminIsTypingUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/ShowAdminIsTypingUseCase\n*L\n38#1:58,5\n50#1:63,5\n*E\n"
)
public final class ShowAdminIsTypingUseCase {
   public static int $stable;

   static {}

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull Avatar var2, boolean var3, boolean var4, @NotNull Continuation var5) {
      Object var10000 = this.sendAdminIndicator(var1, var2, var3, var4 && !var3, var5);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static Object invoke$default(
      ShowAdminIsTypingUseCase var0, MutableStateFlow var1, Avatar var2, boolean var3, boolean var4, Continuation var5, int var6, Object var7
   ) {
      if ((var6 & 4) != 0) {
         var3 = false;
      }

      if ((var6 & 8) != 0) {
         var4 = true;
      }

      return var0.invoke(var1, var2, var3, var4, var5);
   }

   private final Object sendAdminIndicator(MutableStateFlow var1, Avatar var2, boolean var3, boolean var4, Continuation var5) {
      ShowAdminIsTypingUseCase$sendAdminIndicator$1 var15;
      label48: {
         if (var5 instanceof ShowAdminIsTypingUseCase$sendAdminIndicator$1) {
            var15 = (ShowAdminIsTypingUseCase$sendAdminIndicator$1)var5;
            if ((var15.label & -2147483648) != 0) {
               var15.label -= Integer.MIN_VALUE;
               break label48;
            }
         }

         var15 = new ShowAdminIsTypingUseCase$sendAdminIndicator$1(this, var5);
      }

      Object var14 = var15.result;
      Object var16 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var15.label) {
         case 0:
            ResultKt.throwOnFailure(var14);
            if (((ConversationClientState)var1.getValue()).getCurrentlyTypingState().getUserType() != TypingIndicatorType.NONE) {
               return Unit.INSTANCE;
            }

            MutableStateFlow var6 = var1;
            int var7 = 0;

            Object var8;
            Object var13;
            do {
               var8 = var6.getValue();
               ConversationClientState var9 = (ConversationClientState)var8;
               int var10 = 0;
               AvatarWrapper var11 = new AvatarWrapper(var2, var3);
               TypingIndicatorType var12 = TypingIndicatorType.ADMIN;
               var13 = ConversationClientState.copy$default(
                  var9,
                  null,
                  null,
                  null,
                  new CurrentlyTypingState(var11, var4, null, var12, 4, null),
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  0,
                  0,
                  null,
                  null,
                  false,
                  false,
                  null,
                  null,
                  null,
                  8388599,
                  null
               );
            } while (!var6.compareAndSet(var8, var13));

            var15.L$0 = var1;
            var15.label = 1;
            if (DelayKt.delay(5000L, var15) == var16) {
               return var16;
            }
            break;
         case 1:
            var1 = (MutableStateFlow)var15.L$0;
            ResultKt.throwOnFailure(var14);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      MutableStateFlow var17 = var1;
      int var18 = 0;

      Object var19;
      ConversationClientState var21;
      do {
         var19 = var17.getValue();
         var21 = (ConversationClientState)var19;
         int var22 = 0;
         var21 = ConversationClientState.copy$default(
            (ConversationClientState)var21,
            null,
            null,
            null,
            new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8388599,
            null
         );
      } while (!var17.compareAndSet(var19, var21));

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final Object access$sendAdminIndicator(
      ShowAdminIsTypingUseCase var0, MutableStateFlow var1, Avatar var2, boolean var3, boolean var4, Continuation var5
   ) {
      return var0.sendAdminIndicator(var1, var2, var3, var4, var5);
   }
}
