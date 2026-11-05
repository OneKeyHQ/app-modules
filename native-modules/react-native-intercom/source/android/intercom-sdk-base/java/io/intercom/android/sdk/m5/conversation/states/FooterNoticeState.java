package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J-\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/FooterNoticeState;",
         "",
         "title",
         "",
         "subtitle",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getSubtitle",
         "getAvatars",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class FooterNoticeState {
   @NotNull
   private final String title;
   @NotNull
   private final String subtitle;
   @NotNull
   private final List avatars;
   public static final int $stable;

   static { $stable = 8; }

   public FooterNoticeState(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "subtitle");
      Intrinsics.checkNotNullParameter(var3, "avatars");
      this.title = var1;
      this.subtitle = var2;
      this.avatars = var3;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getSubtitle() {
      return this.subtitle;
   }

   @NotNull
   public final List getAvatars() {
      return this.avatars;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final String component2() {
      return this.subtitle;
   }

   @NotNull
   public final List component3() {
      return this.avatars;
   }

   @NotNull
   public final FooterNoticeState copy(@NotNull String var1, @NotNull String var2, @NotNull List var3) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "subtitle");
      Intrinsics.checkNotNullParameter(var3, "avatars");
      return new FooterNoticeState(var1, var2, var3);
   }

   // $VF: synthetic method
   public static FooterNoticeState copy$default(FooterNoticeState var0, String var1, String var2, List var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.subtitle;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.avatars;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "FooterNoticeState(title=" + this.title + ", subtitle=" + this.subtitle + ", avatars=" + this.avatars + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + this.subtitle.hashCode();
      return var1 * 31 + this.avatars.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FooterNoticeState)) {
         return false;
      } else {
         FooterNoticeState var2 = (FooterNoticeState)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.subtitle, var2.subtitle) ? false : Intrinsics.areEqual(this.avatars, var2.avatars);
         }
      }
   }
}
