package io.intercom.android.sdk.models;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0016\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0001H\u0000¨\u0006\u0003",
   d2 = {"withOrderedComposerState", "Lio/intercom/android/sdk/models/Conversation;", "cached", "intercom-sdk-base_release"}
)
public final class ConversationKt {
   @NotNull
   public static final Conversation withOrderedComposerState(@NotNull Conversation var0, @Nullable Conversation var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      if (var1 != null && Intrinsics.areEqual(var1.getId(), var0.getId())) {
         return var0.getComposerState().getVersion() < var1.getComposerState().getVersion()
            ? Conversation.copy$default(
               var0,
               null,
               null,
               false,
               null,
               null,
               null,
               null,
               var1.getComposerState(),
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
               2097023,
               null
            )
            : var0;
      } else {
         return var0;
      }
   }
}
