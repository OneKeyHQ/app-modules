package io.intercom.android.sdk.survey;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u001a\b\u0002\u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00060\u0005¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u001b\u0010\u000f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00060\u0005HÆ\u0003J/\u0010\u0010\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\u001a\b\u0002\u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00060\u0005HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0003H×\u0001J\t\u0010\u0016\u001a\u00020\u0007H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR#\u0010\u0004\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/survey/ValidationError$ValidationStringError;",
         "Lio/intercom/android/sdk/survey/ValidationError;",
         "stringRes",
         "",
         "params",
         "",
         "Lkotlin/Pair;",
         "",
         "<init>",
         "(ILjava/util/List;)V",
         "getStringRes",
         "()I",
         "getParams",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ValidationError$ValidationStringError extends ValidationError {
   private final int stringRes;
   @NotNull
   private final List params;
   public static final int $stable;

   static { $stable = 8; }

   public ValidationError$ValidationStringError(@StringRes int var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var2, "params");
      super(null);
      this.stringRes = var1;
      this.params = var2;
   }

   // $VF: synthetic method
   public ValidationError$ValidationStringError(int var1, List var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (List) (CollectionsKt.emptyList()) : var2));
   }

   public final int getStringRes() {
      return this.stringRes;
   }

   @NotNull
   public final List getParams() {
      return this.params;
   }

   public final int component1() {
      return this.stringRes;
   }

   @NotNull
   public final List component2() {
      return this.params;
   }

   @NotNull
   public final ValidationError$ValidationStringError copy(@StringRes int var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var2, "params");
      return new ValidationError$ValidationStringError(var1, var2);
   }

   // $VF: synthetic method
   public static ValidationError$ValidationStringError copy$default(ValidationError$ValidationStringError var0, int var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.stringRes;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.params;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ValidationStringError(stringRes=" + this.stringRes + ", params=" + this.params + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.stringRes);
      return var1 * 31 + this.params.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ValidationError$ValidationStringError)) {
         return false;
      }

      ValidationError$ValidationStringError var2 = (ValidationError$ValidationStringError)var1;
      return this.stringRes != var2.stringRes ? false : Intrinsics.areEqual(this.params, var2.params);
   }
}
