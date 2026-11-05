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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001J\t\u0010\u0014\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/api/DeDuperStore$DeDuperFields;",
         "",
         "cachedAttributes",
         "",
         "earliestUpdateAt",
         "",
         "<init>",
         "(Ljava/lang/String;J)V",
         "getCachedAttributes",
         "()Ljava/lang/String;",
         "getEarliestUpdateAt",
         "()J",
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
final class DeDuperStore$DeDuperFields {
   @NotNull
   private final String cachedAttributes;
   private final long earliestUpdateAt;

   public DeDuperStore$DeDuperFields(@NotNull String var1, long var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "cachedAttributes");
      this.cachedAttributes = var1;
      this.earliestUpdateAt = var2;
   }

   // $VF: synthetic method
   public DeDuperStore$DeDuperFields(String var1, long var2, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) ("{}") : var1), (((var4 & 2) != 0) ? (long) (0L) : var2));
   }

   @NotNull
   public final String getCachedAttributes() {
      return this.cachedAttributes;
   }

   public final long getEarliestUpdateAt() {
      return this.earliestUpdateAt;
   }

   @NotNull
   public final String component1() {
      return this.cachedAttributes;
   }

   public final long component2() {
      return this.earliestUpdateAt;
   }

   @NotNull
   public final DeDuperStore$DeDuperFields copy(@NotNull String var1, long var2) {
      Intrinsics.checkNotNullParameter(var1, "cachedAttributes");
      return new DeDuperStore$DeDuperFields(var1, var2);
   }

   // $VF: synthetic method
   public static DeDuperStore$DeDuperFields copy$default(DeDuperStore$DeDuperFields var0, String var1, long var2, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.cachedAttributes;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.earliestUpdateAt;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "DeDuperFields(cachedAttributes=" + this.cachedAttributes + ", earliestUpdateAt=" + this.earliestUpdateAt + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.cachedAttributes.hashCode();
      return var1 * 31 + Long.hashCode(this.earliestUpdateAt);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof DeDuperStore$DeDuperFields)) {
         return false;
      }

      DeDuperStore$DeDuperFields var2 = (DeDuperStore$DeDuperFields)var1;
      return !Intrinsics.areEqual(this.cachedAttributes, var2.cachedAttributes) ? false : this.earliestUpdateAt == var2.earliestUpdateAt;
   }

   public DeDuperStore$DeDuperFields() {
      this(null, 0L, 3, null);
   }
}
