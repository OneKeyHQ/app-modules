package io.intercom.android.sdk.m5.push.ui;

import android.net.Uri;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.core.app.Person;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\tHÆ\u0003J5\u0010\u001b\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tHÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u00152\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020\u0007H×\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0016¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation$Message;",
         "",
         "person",
         "Landroidx/core/app/Person;",
         "timestamp",
         "",
         "message",
         "",
         "contentImageUri",
         "Landroid/net/Uri;",
         "<init>",
         "(Landroidx/core/app/Person;JLjava/lang/String;Landroid/net/Uri;)V",
         "getPerson",
         "()Landroidx/core/app/Person;",
         "getTimestamp",
         "()J",
         "getMessage",
         "()Ljava/lang/String;",
         "getContentImageUri",
         "()Landroid/net/Uri;",
         "isCurrentUser",
         "",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPushConversation$Message {
   @Nullable
   private final Person person;
   private final long timestamp;
   @NotNull
   private final String message;
   @Nullable
   private final Uri contentImageUri;
   private final boolean isCurrentUser;
   public static final int $stable;

   static { $stable = 8; }

   public IntercomPushConversation$Message(@Nullable Person var1, long var2, @NotNull String var4, @Nullable Uri var5) {
      super();
      Intrinsics.checkNotNullParameter(var4, "message");
      this.person = var1;
      this.timestamp = var2;
      this.message = var4;
      this.contentImageUri = var5;
      this.isCurrentUser = this.person == null;
   }

   @Nullable
   public final Person getPerson() {
      return this.person;
   }

   public final long getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public final String getMessage() {
      return this.message;
   }

   @Nullable
   public final Uri getContentImageUri() {
      return this.contentImageUri;
   }

   public final boolean isCurrentUser() {
      return this.isCurrentUser;
   }

   @Nullable
   public final Person component1() {
      return this.person;
   }

   public final long component2() {
      return this.timestamp;
   }

   @NotNull
   public final String component3() {
      return this.message;
   }

   @Nullable
   public final Uri component4() {
      return this.contentImageUri;
   }

   @NotNull
   public final IntercomPushConversation$Message copy(@Nullable Person var1, long var2, @NotNull String var4, @Nullable Uri var5) {
      Intrinsics.checkNotNullParameter(var4, "message");
      return new IntercomPushConversation$Message(var1, var2, var4, var5);
   }

   // $VF: synthetic method
   public static IntercomPushConversation$Message copy$default(
      IntercomPushConversation$Message var0, Person var1, long var2, String var4, Uri var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.person;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.timestamp;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.message;
      }

      if ((var6 & 8) != 0) {
         var5 = var0.contentImageUri;
      }

      return var0.copy(var1, var2, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Message(person="
         + this.person
         + ", timestamp="
         + this.timestamp
         + ", message="
         + this.message
         + ", contentImageUri="
         + this.contentImageUri
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.person == null ? 0 : this.person.hashCode();
      var1 = var1 * 31 + Long.hashCode(this.timestamp);
      var1 = var1 * 31 + this.message.hashCode();
      return var1 * 31 + (this.contentImageUri == null ? 0 : this.contentImageUri.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomPushConversation$Message)) {
         return false;
      } else {
         IntercomPushConversation$Message var2 = (IntercomPushConversation$Message)var1;
         if (!Intrinsics.areEqual(this.person, var2.person)) {
            return false;
         } else if (this.timestamp != var2.timestamp) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.message, var2.message) ? false : Intrinsics.areEqual(this.contentImageUri, var2.contentImageUri);
         }
      }
   }
}
