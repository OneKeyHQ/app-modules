package io.intercom.android.sdk.blocks.lib.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001c\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "",
         "ticketTypeId",
         "",
         "name",
         "",
         "formFields",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/FormField;",
         "<init>",
         "(JLjava/lang/String;Ljava/util/List;)V",
         "getTicketTypeId",
         "()J",
         "getName",
         "()Ljava/lang/String;",
         "getFormFields",
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
public final class TicketTypeV2 {
   @SerializedName("ticket_type_id")
   private final long ticketTypeId;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("form_fields")
   @NotNull
   private final List formFields;
   public static final int $stable;

   static { $stable = 8; }

   public TicketTypeV2(long var1, @NotNull String var3, @NotNull List var4) {
      super();
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "formFields");
      this.ticketTypeId = var1;
      this.name = var3;
      this.formFields = var4;
   }

   public final long getTicketTypeId() {
      return this.ticketTypeId;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final List getFormFields() {
      return this.formFields;
   }

   public final long component1() {
      return this.ticketTypeId;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @NotNull
   public final List component3() {
      return this.formFields;
   }

   @NotNull
   public final TicketTypeV2 copy(long var1, @NotNull String var3, @NotNull List var4) {
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "formFields");
      return new TicketTypeV2(var1, var3, var4);
   }

   // $VF: synthetic method
   public static TicketTypeV2 copy$default(TicketTypeV2 var0, long var1, String var3, List var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.ticketTypeId;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.name;
      }

      if ((var5 & 4) != 0) {
         var4 = var0.formFields;
      }

      return var0.copy(var1, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketTypeV2(ticketTypeId=" + this.ticketTypeId + ", name=" + this.name + ", formFields=" + this.formFields + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.ticketTypeId);
      var1 = var1 * 31 + this.name.hashCode();
      return var1 * 31 + this.formFields.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketTypeV2)) {
         return false;
      } else {
         TicketTypeV2 var2 = (TicketTypeV2)var1;
         if (this.ticketTypeId != var2.ticketTypeId) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.name, var2.name) ? false : Intrinsics.areEqual(this.formFields, var2.formFields);
         }
      }
   }
}
