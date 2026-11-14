package io.intercom.android.sdk.m5.push;

import android.content.Context;
import android.os.Bundle;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\t¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$Companion;",
         "",
         "<init>",
         "()V",
         "createFrom",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData;",
         "context",
         "Landroid/content/Context;",
         "bundle",
         "Landroid/os/Bundle;",
         "isIntercomPush",
         "",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPushData$Companion {
   // $VF: synthetic field
   static final IntercomPushData$Companion $$INSTANCE = new IntercomPushData$Companion();

   private IntercomPushData$Companion() {
   }

   @Nullable
   public final IntercomPushData createFrom(@NotNull Context var1, @NotNull Bundle var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "bundle");
      SimplePushData var3 = IntercomPushDataKt.access$getSimplePushDataFromBundle(var2);
      int var4 = 0;
      IntercomPushData var10000;
      if (var3.isIntercomPush()) {
         if (Intrinsics.areEqual(var3.getIntercomPushType(), "push_only")) {
            var10000 = new IntercomPushData$DeepLinkPushData(
               var3.getContentTitle(var1), var3.getContentText(), var3.getUri(), var3.getContentImageUrl(), var3.getInstanceId()
            );
         } else {
            String var5 = var3.getAuthorName();
            String var6 = var3.getImageUrl();
            String var7 = var3.getConversationId();
            boolean var8 = Intrinsics.areEqual(var3.getConversationPartType(), "message");
            IntercomPushData$ConversationPushData$MessageData var10 = var3.getMessageData();
            if (var10 == null) {
               var10 = new IntercomPushData$ConversationPushData$MessageData$Text(
                  StringsKt.trim(StringsKt.removePrefix(var3.getContentText(), var3.getAuthorName() + ':')).toString()
               );
            }

            IntercomPushData$ConversationPushData$MessageData var9 = var10;
            var10000 = new IntercomPushData$ConversationPushData(var5, var6, var7, var9, false, var8);
         }
      } else {
         var10000 = null;
      }

      return var10000;
   }

   public final boolean isIntercomPush(@NotNull Bundle var1) {
      Intrinsics.checkNotNullParameter(var1, "bundle");
      return IntercomPushDataKt.access$getSimplePushDataFromBundle(var1).isIntercomPush();
   }
}
