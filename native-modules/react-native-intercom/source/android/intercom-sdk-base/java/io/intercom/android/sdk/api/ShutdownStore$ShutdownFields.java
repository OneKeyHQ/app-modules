package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B+\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0007\u0010\bJ\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\fJ\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J2\u0010\u0012\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003HÆ\u0001¢\u0006\u0002\u0010\u0013J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/api/ShutdownStore$ShutdownFields;",
         "",
         "fingerprint",
         "",
         "expiry",
         "",
         "reason",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;)V",
         "getFingerprint",
         "()Ljava/lang/String;",
         "getExpiry",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "getReason",
         "component1",
         "component2",
         "component3",
         "copy",
         "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;)Lio/intercom/android/sdk/api/ShutdownStore$ShutdownFields;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
final class ShutdownStore$ShutdownFields {
   @Nullable
   private final String fingerprint;
   @Nullable
   private final Long expiry;
   @Nullable
   private final String reason;

   public ShutdownStore$ShutdownFields(@Nullable String var1, @Nullable Long var2, @Nullable String var3) {
      this.fingerprint = var1;
      this.expiry = var2;
      this.reason = var3;
   }

   // $VF: synthetic method
   public ShutdownStore$ShutdownFields(String var1, Long var2, String var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) (null) : var1), (((var4 & 2) != 0) ? (Long) (null) : var2), (((var4 & 4) != 0) ? (String) (null) : var3));
   }

   @Nullable
   public final String getFingerprint() {
      return this.fingerprint;
   }

   @Nullable
   public final Long getExpiry() {
      return this.expiry;
   }

   @Nullable
   public final String getReason() {
      return this.reason;
   }

   @Nullable
   public final String component1() {
      return this.fingerprint;
   }

   @Nullable
   public final Long component2() {
      return this.expiry;
   }

   @Nullable
   public final String component3() {
      return this.reason;
   }

   @NotNull
   public final ShutdownStore$ShutdownFields copy(@Nullable String var1, @Nullable Long var2, @Nullable String var3) {
      return new ShutdownStore$ShutdownFields(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ShutdownStore$ShutdownFields copy$default(ShutdownStore$ShutdownFields var0, String var1, Long var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.fingerprint;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.expiry;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.reason;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ShutdownFields(fingerprint=" + this.fingerprint + ", expiry=" + this.expiry + ", reason=" + this.reason + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.fingerprint == null ? 0 : this.fingerprint.hashCode();
      var1 = var1 * 31 + (this.expiry == null ? 0 : this.expiry.hashCode());
      return var1 * 31 + (this.reason == null ? 0 : this.reason.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ShutdownStore$ShutdownFields)) {
         return false;
      } else {
         ShutdownStore$ShutdownFields var2 = (ShutdownStore$ShutdownFields)var1;
         if (!Intrinsics.areEqual(this.fingerprint, var2.fingerprint)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.expiry, var2.expiry) ? false : Intrinsics.areEqual(this.reason, var2.reason);
         }
      }
   }

   public ShutdownStore$ShutdownFields() {
      this(null, null, null, 7, null);
   }
}
