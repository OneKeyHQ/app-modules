package io.intercom.android.sdk.utilities.extensions;

import io.intercom.android.sdk.identity.AppConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000¨\u0006\u0003",
   d2 = {"canStartNewConversation", "", "Lio/intercom/android/sdk/identity/AppConfig;", "intercom-sdk-base_release"}
)
public final class AppConfigExtensionsKt {
   public static final boolean canStartNewConversation(@NotNull AppConfig var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      boolean var1 = var0.isInboundMessages();
      if (var0.isPreventMultipleInboundConversationsEnabled()) {
         var1 = var1 && !var0.getHasOpenConversations();
      }

      return var1;
   }
}
