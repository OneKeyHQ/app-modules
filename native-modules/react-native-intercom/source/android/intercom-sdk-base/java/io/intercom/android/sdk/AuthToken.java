package io.intercom.android.sdk;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\n¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/AuthToken;",
         "",
         "name",
         "",
         "token",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getName",
         "()Ljava/lang/String;",
         "setName",
         "(Ljava/lang/String;)V",
         "getToken",
         "setToken",
         "component1",
         "component2",
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
public final class AuthToken {
   @NotNull
   private String name;
   @NotNull
   private String token;
   public static final int $stable;

   static { $stable = 8; }

   public AuthToken(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "token");
      this.name = var1;
      this.token = var2;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final void setName(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.name = var1;
   }

   @NotNull
   public final String getToken() {
      return this.token;
   }

   public final void setToken(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.token = var1;
   }

   @NotNull
   public final String component1() {
      return this.name;
   }

   @NotNull
   public final String component2() {
      return this.token;
   }

   @NotNull
   public final AuthToken copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "name");
      Intrinsics.checkNotNullParameter(var2, "token");
      return new AuthToken(var1, var2);
   }

   // $VF: synthetic method
   public static AuthToken copy$default(AuthToken var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.name;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.token;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AuthToken(name=" + this.name + ", token=" + this.token + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.name.hashCode();
      return var1 * 31 + this.token.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AuthToken)) {
         return false;
      }

      AuthToken var2 = (AuthToken)var1;
      return !Intrinsics.areEqual(this.name, var2.name) ? false : Intrinsics.areEqual(this.token, var2.token);
   }
}
