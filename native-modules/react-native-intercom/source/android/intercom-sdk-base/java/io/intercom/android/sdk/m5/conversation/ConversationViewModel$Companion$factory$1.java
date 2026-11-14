package io.intercom.android.sdk.m5.conversation;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider.Factory;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J%\u0010\u0002\u001a\u0002H\u0003\"\b\b\u0000\u0010\u0003*\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00030\u0006H\u0016¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "io/intercom/android/sdk/m5/conversation/ConversationViewModel$Companion$factory$1",
         "Landroidx/lifecycle/ViewModelProvider$Factory;",
         "create",
         "T",
         "Landroidx/lifecycle/ViewModel;",
         "modelClass",
         "Ljava/lang/Class;",
         "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;",
         "intercom-sdk-base_release"
   }
)
public final class ConversationViewModel$Companion$factory$1 implements Factory {
   // $VF: synthetic field
   final String $conversationId;
   // $VF: synthetic field
   final LaunchMode $launchMode;
   // $VF: synthetic field
   final ArticleMetadata $articleMetadata;
   // $VF: synthetic field
   final String $initialMessage;

   ConversationViewModel$Companion$factory$1(String var1, LaunchMode var2, ArticleMetadata var3, String var4) {
      this.$conversationId = var1;
      this.$launchMode = var2;
      this.$articleMetadata = var3;
      this.$initialMessage = var4;
   }

   @Override
   public ViewModel create(Class var1) {
      Intrinsics.checkNotNullParameter(var1, "modelClass");
      return new ConversationViewModel(
         this.$conversationId,
         this.$launchMode,
         this.$articleMetadata,
         this.$initialMessage,
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
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         -16,
         1023,
         null
      );
   }
}
