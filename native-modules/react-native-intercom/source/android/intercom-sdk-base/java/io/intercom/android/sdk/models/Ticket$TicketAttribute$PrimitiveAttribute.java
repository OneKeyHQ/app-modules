package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001BC\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003¢\u0006\u0004\b\u000b\u0010\fJ\b\u0010\u000f\u001a\u00020\u0007H\u0016R\u0016\u0010\n\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$PrimitiveAttribute;",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute;",
         "id",
         "",
         "identifier",
         "name",
         "required",
         "",
         "type",
         "Lio/intercom/android/sdk/models/TicketAttributeType;",
         "value",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/TicketAttributeType;Ljava/lang/String;)V",
         "getValue",
         "()Ljava/lang/String;",
         "hasValue",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Ticket$TicketAttribute$PrimitiveAttribute extends Ticket$TicketAttribute {
   @SerializedName("value")
   @NotNull
   private final String value;
   public static int $stable;

   static {}

   public Ticket$TicketAttribute$PrimitiveAttribute(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, boolean var4, @NotNull TicketAttributeType var5, @NotNull String var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "identifier");
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var5, "type");
      Intrinsics.checkNotNullParameter(var6, "value");
      super(var1, var2, var3, var4, var5, null);
      this.value = var6;
   }

   // $VF: synthetic method
   public Ticket$TicketAttribute$PrimitiveAttribute(
      String var1, String var2, String var3, boolean var4, TicketAttributeType var5, String var6, int var7, DefaultConstructorMarker var8
   ) {
      this((((var7 & 1) != 0) ? (String) ("") : var1), (((var7 & 2) != 0) ? (String) ("") : var2), (((var7 & 4) != 0) ? (String) ("") : var3), (((var7 & 8) != 0) ? (boolean) (false) : var4), (((var7 & 16) != 0) ? (TicketAttributeType) (TicketAttributeType.STRING) : var5), (((var7 & 32) != 0) ? (String) ("") : var6));
   }

   @NotNull
   public final String getValue() {
      return this.value;
   }

   @Override
   public boolean hasValue() {
      return !StringsKt.isBlank(this.value);
   }

   public Ticket$TicketAttribute$PrimitiveAttribute() {
      this(null, null, null, false, null, null, 63, null);
   }
}
