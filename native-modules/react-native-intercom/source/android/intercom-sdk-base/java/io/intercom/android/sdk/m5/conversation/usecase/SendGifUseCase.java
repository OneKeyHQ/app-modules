package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType$Text;
import io.intercom.android.sdk.m5.upload.data.MediaData$Gif;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J$\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0086B¢\u0006\u0002\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendGifUseCase;",
         "",
         "sendMessageUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;",
         "changeInputUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/ChangeInputUseCase;)V",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "gifData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/m5/upload/data/MediaData$Gif;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SendGifUseCase {
   @NotNull
   private final SendMessageUseCase sendMessageUseCase;
   @NotNull
   private final ChangeInputUseCase changeInputUseCase;
   public static final int $stable;

   static { $stable = 8; }

   public SendGifUseCase(@NotNull SendMessageUseCase var1, @NotNull ChangeInputUseCase var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "sendMessageUseCase");
      Intrinsics.checkNotNullParameter(var2, "changeInputUseCase");
      this.sendMessageUseCase = var1;
      this.changeInputUseCase = var2;
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull MediaData$Gif var2, @NotNull Continuation var3) {
      this.changeInputUseCase.invoke(var1, ComposerInputType$Text.INSTANCE);
      Block$Builder var4 = new Block$Builder()
         .withType("image")
         .withUrl(var2.getUrl())
         .withAttribution(var2.getAttribution())
         .withHeight(var2.getHeight())
         .withWidth(var2.getWidth());
      Object var10000 = SendMessageUseCase.invoke$default(this.sendMessageUseCase, var1, CollectionsKt.listOf(var4), null, var3, 4, null);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }
}
