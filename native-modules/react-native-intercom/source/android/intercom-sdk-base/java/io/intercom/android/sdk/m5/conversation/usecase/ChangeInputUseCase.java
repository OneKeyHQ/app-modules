package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001f\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\nH\u0086\u0002¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase;",
         "",
         "<init>",
         "()V",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "inputType",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nChangeInputUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ChangeInputUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,27:1\n230#2,5:28\n*S KotlinDebug\n*F\n+ 1 ChangeInputUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase\n*L\n16#1:28,5\n*E\n"
)
public final class ChangeInputUseCase {
   public static int $stable;

   static {}

   public final void invoke(@NotNull MutableStateFlow var1, @NotNull ComposerInputType var2) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      Intrinsics.checkNotNullParameter(var2, "inputType");
      MutableStateFlow var3 = var1;
      int var4 = 0;

      Object var5;
      ConversationClientState var8;
      do {
         var5 = var3.getValue();
         var8 = (ConversationClientState)var5;
         int var7 = 0;
         var8 = ConversationClientState.copy$default(
            (ConversationClientState)var8,
            null,
            null,
            null,
            null,
            new ComposerState$TextInput("", new StringRes(string.intercom_reply_to_conversation, null, 2, null), false, null, null, var2, 28, null),
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
            8388591,
            null
         );
      } while (!var3.compareAndSet(var5, var8));
   }
}
