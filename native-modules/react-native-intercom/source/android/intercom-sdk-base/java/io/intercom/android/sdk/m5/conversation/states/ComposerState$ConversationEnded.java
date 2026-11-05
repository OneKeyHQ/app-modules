package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001:\u0001\u0017B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "message",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "cta",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded$ConversationEndedCta;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/StringProvider;Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded$ConversationEndedCta;)V",
         "getMessage",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getCta",
         "()Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded$ConversationEndedCta;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "ConversationEndedCta",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ComposerState$ConversationEnded implements ComposerState {
   @NotNull
   private final StringProvider message;
   @Nullable
   private final ComposerState$ConversationEnded$ConversationEndedCta cta;
   public static final int $stable = StringProvider.$stable | StringProvider.$stable;

   public ComposerState$ConversationEnded(@NotNull StringProvider var1, @Nullable ComposerState$ConversationEnded$ConversationEndedCta var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "message");
      this.message = var1;
      this.cta = var2;
   }

   // $VF: synthetic method
   public ComposerState$ConversationEnded(
      StringProvider var1, ComposerState$ConversationEnded$ConversationEndedCta var2, int var3, DefaultConstructorMarker var4
   ) {
      this(var1, (((var3 & 2) != 0) ? (ComposerState$ConversationEnded$ConversationEndedCta) (null) : var2));
   }

   @NotNull
   public final StringProvider getMessage() {
      return this.message;
   }

   @Nullable
   public final ComposerState$ConversationEnded$ConversationEndedCta getCta() {
      return this.cta;
   }

   @NotNull
   public final StringProvider component1() {
      return this.message;
   }

   @Nullable
   public final ComposerState$ConversationEnded$ConversationEndedCta component2() {
      return this.cta;
   }

   @NotNull
   public final ComposerState$ConversationEnded copy(@NotNull StringProvider var1, @Nullable ComposerState$ConversationEnded$ConversationEndedCta var2) {
      Intrinsics.checkNotNullParameter(var1, "message");
      return new ComposerState$ConversationEnded(var1, var2);
   }

   // $VF: synthetic method
   public static ComposerState$ConversationEnded copy$default(
      ComposerState$ConversationEnded var0, StringProvider var1, ComposerState$ConversationEnded$ConversationEndedCta var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.message;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.cta;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationEnded(message=" + this.message + ", cta=" + this.cta + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.message.hashCode();
      return var1 * 31 + (this.cta == null ? 0 : this.cta.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ComposerState$ConversationEnded)) {
         return false;
      }

      ComposerState$ConversationEnded var2 = (ComposerState$ConversationEnded)var1;
      return !Intrinsics.areEqual(this.message, var2.message) ? false : Intrinsics.areEqual(this.cta, var2.cta);
   }
}
