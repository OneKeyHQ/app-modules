package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.states.BottomSheetState$GifSearch;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.models.GifResponse;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J&\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0086B¢\u0006\u0002\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/LoadGifUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "searchQuery",
         "",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nLoadGifUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadGifUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/LoadGifUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,31:1\n230#2,5:32\n*S KotlinDebug\n*F\n+ 1 LoadGifUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/LoadGifUseCase\n*L\n26#1:32,5\n*E\n"
)
public final class LoadGifUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   public static final int $stable;

   static { $stable = 8; }

   public LoadGifUseCase(@NotNull ConversationRepository var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      this.conversationRepository = var1;
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull String var2, @NotNull Continuation var3) {
      LoadGifUseCase$invoke$1 var11;
      label42: {
         if (var3 instanceof LoadGifUseCase$invoke$1) {
            var11 = (LoadGifUseCase$invoke$1)var3;
            if ((var11.label & -2147483648) != 0) {
               var11.label -= Integer.MIN_VALUE;
               break label42;
            }
         }

         var11 = new LoadGifUseCase$invoke$1(this, var3);
      }

      Object var10 = var11.result;
      Object var12 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var11.label) {
         case 0:
            ResultKt.throwOnFailure(var10);
            ConversationRepository var16 = this.conversationRepository;
            var11.L$0 = var1;
            var11.label = 1;
            var10000 = var16.loadGifs(var2, var11);
            if (var10000 == var12) {
               return var12;
            }
            break;
         case 1:
            var1 = (MutableStateFlow)var11.L$0;
            ResultKt.throwOnFailure(var10);
            var10000 = var10;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var5 = (NetworkResponse)var10000;
      BottomSheetState$GifSearch var17;
      if (!(var5 instanceof NetworkResponse$ClientError) && !(var5 instanceof NetworkResponse$NetworkError) && !(var5 instanceof NetworkResponse$ServerError)) {
         if (!(var5 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         List var6 = ((GifResponse)((NetworkResponse$Success)var5).getBody()).results();
         Intrinsics.checkNotNullExpressionValue(var6, "results(...)");
         var17 = new BottomSheetState$GifSearch(var6);
      } else {
         var17 = new BottomSheetState$GifSearch(CollectionsKt.emptyList());
      }

      BottomSheetState$GifSearch var4 = var17;
      MutableStateFlow var13 = var1;
      int var14 = 0;

      Object var7;
      ConversationClientState var15;
      do {
         var7 = var13.getValue();
         var15 = (ConversationClientState)var7;
         int var9 = 0;
         var15 = ConversationClientState.copy$default(
            (ConversationClientState)var15,
            null,
            null,
            null,
            null,
            null,
            var4,
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
            8388575,
            null
         );
      } while (!var13.compareAndSet(var7, var15));

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static Object invoke$default(LoadGifUseCase var0, MutableStateFlow var1, String var2, Continuation var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = "";
      }

      return var0.invoke(var1, var2, var3);
   }
}
