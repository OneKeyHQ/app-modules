package io.intercom.android.sdk.post;

import android.content.Context;
import android.content.Intent;
import io.intercom.android.sdk.models.LastParticipatingAdmin;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J8\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/post/IntercomPostActivity$Companion;",
         "",
         "<init>",
         "()V",
         "PARCEL_PART",
         "",
         "PARCEL_CONVERSATION_ID",
         "POST_PREVIEW",
         "LAST_PARTICIPANT",
         "COMPOSER_IS_VISIBLE",
         "buildPostIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "conversationId",
         "lastParticipatingAdmin",
         "Lio/intercom/android/sdk/models/LastParticipatingAdmin;",
         "composerIsVisible",
         "",
         "isPreview",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPostActivity$Companion {
   private IntercomPostActivity$Companion() {
   }

   @JvmStatic
   @NotNull
   public final Intent buildPostIntent(
      @NotNull Context var1, @NotNull Part var2, @NotNull String var3, @NotNull LastParticipatingAdmin var4, boolean var5, boolean var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intrinsics.checkNotNullParameter(var2, "part");
      Intrinsics.checkNotNullParameter(var3, "conversationId");
      Intrinsics.checkNotNullParameter(var4, "lastParticipatingAdmin");
      Intent var10000 = new Intent(var1, IntercomPostActivity.class)
         .putExtra("parcel_part", var2)
         .putExtra("last_participant", var4)
         .putExtra("composer_is_visible", var5)
         .putExtra("is_post_preview", var6);
      Intrinsics.checkNotNullExpressionValue(var10000, "putExtra(...)");
      Intent var7 = var10000;
      Intent var8 = var7;
      int var9 = 0;
      if (var3.length() > 0) {
         var8.putExtra("parcel_conversation_id", var3);
      }

      var8.setExtrasClassLoader(Part.class.getClassLoader());
      return var7;
   }

   // $VF: synthetic method
   public IntercomPostActivity$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
