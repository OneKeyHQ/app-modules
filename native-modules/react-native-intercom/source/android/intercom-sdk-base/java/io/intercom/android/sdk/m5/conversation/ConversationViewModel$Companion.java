package io.intercom.android.sdk.m5.conversation;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00009\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000*\u0001\u0010\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J6\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\t2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000eJ1\u0010\u000f\u001a\u00020\u00102\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\t2\b\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000eH\u0002¢\u0006\u0002\u0010\u0011R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ConversationViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/m5/conversation/ConversationViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "conversationId",
         "",
         "initialMessage",
         "articleMetadata",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;",
         "launchMode",
         "Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "factory",
         "io/intercom/android/sdk/m5/conversation/ConversationViewModel$Companion$factory$1",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/articles/ArticleMetadata;Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;)Lio/intercom/android/sdk/m5/conversation/ConversationViewModel$Companion$factory$1;",
         "DEBOUNCE_DELAY_MS",
         "",
         "intercom-sdk-base_release"
   }
)
public final class ConversationViewModel$Companion {
   private ConversationViewModel$Companion() {
   }

   @NotNull
   public final ConversationViewModel create(
      @NotNull ViewModelStoreOwner var1, @Nullable String var2, @NotNull String var3, @Nullable ArticleMetadata var4, @NotNull LaunchMode var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var3, "initialMessage");
      Intrinsics.checkNotNullParameter(var5, "launchMode");
      return (ConversationViewModel)new ViewModelProvider(var1, this.factory(var2, var3, var4, var5)).get(ConversationViewModel.class);
   }

   // $VF: synthetic method
   public static ConversationViewModel create$default(
      ConversationViewModel$Companion var0, ViewModelStoreOwner var1, String var2, String var3, ArticleMetadata var4, LaunchMode var5, int var6, Object var7
   ) {
      if ((var6 & 4) != 0) {
         var3 = "";
      }

      if ((var6 & 8) != 0) {
         var4 = null;
      }

      return var0.create(var1, var2, var3, var4, var5);
   }

   private final ConversationViewModel$Companion$factory$1 factory(String var1, String var2, ArticleMetadata var3, LaunchMode var4) {
      return new ConversationViewModel$Companion$factory$1(var1, var4, var3, var2);
   }

   // $VF: synthetic method
   public ConversationViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
