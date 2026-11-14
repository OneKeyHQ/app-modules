package io.intercom.android.sdk.helpcenter.articles;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0003\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0005H×\u0001J\t\u0010\u0013\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$ConversationState;",
         "",
         "conversationId",
         "",
         "messageButtonText",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getMessageButtonText",
         "()I",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleViewState$ConversationState {
   @NotNull
   private final String conversationId;
   private final int messageButtonText;
   public static int $stable;

   static {}

   public ArticleViewState$ConversationState(@NotNull String var1, @StringRes int var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      this.conversationId = var1;
      this.messageButtonText = var2;
   }

   // $VF: synthetic method
   public ArticleViewState$ConversationState(String var1, int var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (int) (string.intercom_continue_the_conversation) : var2));
   }

   @NotNull
   public final String getConversationId() {
      return this.conversationId;
   }

   public final int getMessageButtonText() {
      return this.messageButtonText;
   }

   @NotNull
   public final String component1() {
      return this.conversationId;
   }

   public final int component2() {
      return this.messageButtonText;
   }

   @NotNull
   public final ArticleViewState$ConversationState copy(@NotNull String var1, @StringRes int var2) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      return new ArticleViewState$ConversationState(var1, var2);
   }

   // $VF: synthetic method
   public static ArticleViewState$ConversationState copy$default(ArticleViewState$ConversationState var0, String var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.messageButtonText;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationState(conversationId=" + this.conversationId + ", messageButtonText=" + this.messageButtonText + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId.hashCode();
      return var1 * 31 + Integer.hashCode(this.messageButtonText);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ArticleViewState$ConversationState)) {
         return false;
      }

      ArticleViewState$ConversationState var2 = (ArticleViewState$ConversationState)var1;
      return !Intrinsics.areEqual(this.conversationId, var2.conversationId) ? false : this.messageButtonText == var2.messageButtonText;
   }
}
