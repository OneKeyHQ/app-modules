package io.intercom.android.sdk.m5.conversation.states;

import androidx.annotation.DrawableRes;
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
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0007HÆ\u0003J)\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0005H×\u0001J\t\u0010\u0018\u001a\u00020\u0007H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded$ConversationEndedCta;",
         "",
         "title",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "trailingIcon",
         "",
         "linkedConversationId",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/StringProvider;ILjava/lang/String;)V",
         "getTitle",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getTrailingIcon",
         "()I",
         "getLinkedConversationId",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ComposerState$ConversationEnded$ConversationEndedCta {
   @NotNull
   private final StringProvider title;
   private final int trailingIcon;
   @Nullable
   private final String linkedConversationId;
   public static final int $stable = StringProvider.$stable;

   public ComposerState$ConversationEnded$ConversationEndedCta(@NotNull StringProvider var1, @DrawableRes int var2, @Nullable String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      this.title = var1;
      this.trailingIcon = var2;
      this.linkedConversationId = var3;
   }

   // $VF: synthetic method
   public ComposerState$ConversationEnded$ConversationEndedCta(StringProvider var1, int var2, String var3, int var4, DefaultConstructorMarker var5) {
      this(var1, var2, (((var4 & 4) != 0) ? (String) (null) : var3));
   }

   @NotNull
   public final StringProvider getTitle() {
      return this.title;
   }

   public final int getTrailingIcon() {
      return this.trailingIcon;
   }

   @Nullable
   public final String getLinkedConversationId() {
      return this.linkedConversationId;
   }

   @NotNull
   public final StringProvider component1() {
      return this.title;
   }

   public final int component2() {
      return this.trailingIcon;
   }

   @Nullable
   public final String component3() {
      return this.linkedConversationId;
   }

   @NotNull
   public final ComposerState$ConversationEnded$ConversationEndedCta copy(@NotNull StringProvider var1, @DrawableRes int var2, @Nullable String var3) {
      Intrinsics.checkNotNullParameter(var1, "title");
      return new ComposerState$ConversationEnded$ConversationEndedCta(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ComposerState$ConversationEnded$ConversationEndedCta copy$default(
      ComposerState$ConversationEnded$ConversationEndedCta var0, StringProvider var1, int var2, String var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.trailingIcon;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.linkedConversationId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationEndedCta(title=" + this.title + ", trailingIcon=" + this.trailingIcon + ", linkedConversationId=" + this.linkedConversationId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.trailingIcon);
      return var1 * 31 + (this.linkedConversationId == null ? 0 : this.linkedConversationId.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ComposerState$ConversationEnded$ConversationEndedCta)) {
         return false;
      } else {
         ComposerState$ConversationEnded$ConversationEndedCta var2 = (ComposerState$ConversationEnded$ConversationEndedCta)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return this.trailingIcon != var2.trailingIcon ? false : Intrinsics.areEqual(this.linkedConversationId, var2.linkedConversationId);
         }
      }
   }
}
