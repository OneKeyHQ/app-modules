package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\t\u001a\u0004\u0018\u00010\u0003HÆ\u0003¢\u0006\u0002\u0010\u0007J\u001a\u0010\n\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003HÇ\u0001¢\u0006\u0002\u0010\u000bJ\u0013\u0010\f\u001a\u00020\u00032\b\u0010\r\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u000e\u001a\u00020\u000fH×\u0001J\t\u0010\u0010\u001a\u00020\u0011H×\u0001R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "",
         "showLastPartMeta",
         "",
         "<init>",
         "(Ljava/lang/Boolean;)V",
         "getShowLastPartMeta",
         "()Ljava/lang/Boolean;",
         "Ljava/lang/Boolean;",
         "component1",
         "copy",
         "(Ljava/lang/Boolean;)Lio/intercom/android/sdk/models/ConversationUiFlags;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationUiFlags {
   @NotNull
   public static final ConversationUiFlags$Companion Companion = new ConversationUiFlags$Companion(null);
   @SerializedName("show_last_part_meta")
   @Nullable
   private final Boolean showLastPartMeta;
   public static int $stable;

   static {}
   @NotNull
   private static final ConversationUiFlags DEFAULT = new ConversationUiFlags(null, 1, null);

   public ConversationUiFlags(@Nullable Boolean var1) {
      this.showLastPartMeta = var1;
   }

   // $VF: synthetic method
   public ConversationUiFlags(Boolean var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Boolean) (true) : var1));
   }

   @Nullable
   public final Boolean getShowLastPartMeta() {
      return this.showLastPartMeta;
   }

   @Nullable
   public final Boolean component1() {
      return this.showLastPartMeta;
   }

   @NotNull
   public final ConversationUiFlags copy(@Nullable Boolean var1) {
      return new ConversationUiFlags(var1);
   }

   // $VF: synthetic method
   public static ConversationUiFlags copy$default(ConversationUiFlags var0, Boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.showLastPartMeta;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationUiFlags(showLastPartMeta=" + this.showLastPartMeta + ')';
   }

   @Override
   public int hashCode() {
      return this.showLastPartMeta == null ? 0 : this.showLastPartMeta.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ConversationUiFlags)) {
         return false;
      }

      ConversationUiFlags var2 = (ConversationUiFlags)var1;
      return Intrinsics.areEqual(this.showLastPartMeta, var2.showLastPartMeta);
   }

   public ConversationUiFlags() {
      this(null, 1, null);
   }

   @NotNull
   public static final ConversationUiFlags getDEFAULT() {
      return Companion.getDEFAULT();
   }

   // $VF: synthetic method
   public static final ConversationUiFlags access$getDEFAULT$cp() {
      return DEFAULT;
   }
}
