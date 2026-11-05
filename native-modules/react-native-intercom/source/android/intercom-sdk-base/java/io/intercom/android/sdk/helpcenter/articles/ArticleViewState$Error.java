package io.intercom.android.sdk.helpcenter.articles;

import androidx.annotation.StringRes;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B!\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J'\u0010\u000f\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H×\u0003J\t\u0010\u0014\u001a\u00020\u0003H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState$Error;",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleViewState;",
         "message",
         "",
         "retryButtonVisibility",
         "retryButtonPrimaryColor",
         "<init>",
         "(III)V",
         "getMessage",
         "()I",
         "getRetryButtonVisibility",
         "getRetryButtonPrimaryColor",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleViewState$Error extends ArticleViewState {
   private final int message;
   private final int retryButtonVisibility;
   private final int retryButtonPrimaryColor;
   public static int $stable;

   static {}

   public ArticleViewState$Error(@StringRes int var1, int var2, int var3) {
      super(null);
      this.message = var1;
      this.retryButtonVisibility = var2;
      this.retryButtonPrimaryColor = var3;
   }

   public final int getMessage() {
      return this.message;
   }

   public final int getRetryButtonVisibility() {
      return this.retryButtonVisibility;
   }

   public final int getRetryButtonPrimaryColor() {
      return this.retryButtonPrimaryColor;
   }

   public final int component1() {
      return this.message;
   }

   public final int component2() {
      return this.retryButtonVisibility;
   }

   public final int component3() {
      return this.retryButtonPrimaryColor;
   }

   @NotNull
   public final ArticleViewState$Error copy(@StringRes int var1, int var2, int var3) {
      return new ArticleViewState$Error(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ArticleViewState$Error copy$default(ArticleViewState$Error var0, int var1, int var2, int var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.message;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.retryButtonVisibility;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.retryButtonPrimaryColor;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Error(message="
         + this.message
         + ", retryButtonVisibility="
         + this.retryButtonVisibility
         + ", retryButtonPrimaryColor="
         + this.retryButtonPrimaryColor
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.message);
      var1 = var1 * 31 + Integer.hashCode(this.retryButtonVisibility);
      return var1 * 31 + Integer.hashCode(this.retryButtonPrimaryColor);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ArticleViewState$Error)) {
         return false;
      } else {
         ArticleViewState$Error var2 = (ArticleViewState$Error)var1;
         if (this.message != var2.message) {
            return false;
         } else {
            return this.retryButtonVisibility != var2.retryButtonVisibility ? false : this.retryButtonPrimaryColor == var2.retryButtonPrimaryColor;
         }
      }
   }
}
