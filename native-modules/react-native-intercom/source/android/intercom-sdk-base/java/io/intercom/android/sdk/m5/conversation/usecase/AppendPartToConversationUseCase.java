package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001f\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/AppendPartToConversationUseCase;",
         "",
         "trackLastReceivedPartsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;",
         "recordOpenedInteractionUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "partBuilder",
         "Lio/intercom/android/sdk/models/Part$Builder;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nAppendPartToConversationUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AppendPartToConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/AppendPartToConversationUseCase\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,63:1\n1761#2,3:64\n230#3,5:67\n*S KotlinDebug\n*F\n+ 1 AppendPartToConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/AppendPartToConversationUseCase\n*L\n35#1:64,3\n55#1:67,5\n*E\n"
)
public final class AppendPartToConversationUseCase {
   @NotNull
   private final TrackLastReceivedPartsUseCase trackLastReceivedPartsUseCase;
   @NotNull
   private final RecordOpenedInteractionUseCase recordOpenedInteractionUseCase;
   public static final int $stable;

   static { $stable = 8; }

   public AppendPartToConversationUseCase(@NotNull TrackLastReceivedPartsUseCase var1, @NotNull RecordOpenedInteractionUseCase var2) {
      Intrinsics.checkNotNullParameter(var1, "trackLastReceivedPartsUseCase");
      Intrinsics.checkNotNullParameter(var2, "recordOpenedInteractionUseCase");
      super();
      this.trackLastReceivedPartsUseCase = var1;
      this.recordOpenedInteractionUseCase = var2;
   }

   public final void invoke(@NotNull MutableStateFlow var1, @NotNull io.intercom.android.sdk.models.Part$Builder var2) {
      Intrinsics.checkNotNullParameter(var1, "clientStateFlow");
      Intrinsics.checkNotNullParameter(var2, "partBuilder");
      ConversationClientState var3 = (ConversationClientState)var1.getValue();
      Conversation var10000 = var3.getConversation();
      if (var10000 != null) {
         Conversation var4 = var10000;
         Conversation var5 = Conversation.copy$default(
            var4,
            null,
            null,
            false,
            null,
            CollectionsKt.plus(var4.getPartBuilderList(), var2),
            null,
            null,
            null,
            false,
            false,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            2097135,
            null
         );
         List var6 = var5.parts();
         Part var7 = (Part)CollectionsKt.last(var6);
         var var9 = var4.parts();
         int var10 = 0;
         boolean var24;
         if (var9 instanceof Collection && ((Collection)var9).isEmpty()) {
            var24 = false;
         } else {
            Iterator var11 = var9.iterator();

            while (true) {
               if (!var11.hasNext()) {
                  var24 = false;
                  break;
               }

               boolean var23;
               label67: {
                  Object var12 = var11.next();
                  Part var13 = (Part)var12;
                  int var14 = 0;
                  if (!Intrinsics.areEqual(var13.getId(), var7.getId())) {
                     String var22 = var7.getUuid();
                     Intrinsics.checkNotNullExpressionValue(var22, "getUuid(...)");
                     if (var22.length() <= 0 || !Intrinsics.areEqual(var13.getUuid(), var7.getUuid())) {
                        var23 = false;
                        break label67;
                     }
                  }

                  var23 = true;
               }

               if (var23) {
                  var24 = true;
                  break;
               }
            }
         }

         boolean var8 = var24;
         if (!var8) {
            Conversation var15 = var5;
            this.trackLastReceivedPartsUseCase.invoke(var15);
            this.recordOpenedInteractionUseCase.invoke(var15);
            MutableStateFlow var16 = var1;
            int var17 = 0;

            Object var18;
            Object var20;
            do {
               var18 = var16.getValue();
               ConversationClientState var19 = (ConversationClientState)var18;
               int var21 = 0;
               var20 = ConversationClientState.copy$default(
                  var19,
                  null,
                  var15,
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
                  8388597,
                  null
               );
            } while (!var16.compareAndSet(var18, var20));
         }
      }
   }
}
