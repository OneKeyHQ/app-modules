package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0012BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b¢\u0006\u0004\b\r\u0010\u000eJ\b\u0010\u0011\u001a\u00020\u0007H\u0016R\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$FilesAttribute;",
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
         "",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$FilesAttribute$File;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/TicketAttributeType;Ljava/util/List;)V",
         "getValue",
         "()Ljava/util/List;",
         "hasValue",
         "File",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Ticket$TicketAttribute$FilesAttribute extends Ticket$TicketAttribute {
   @SerializedName("value")
   @NotNull
   private final List value;
   public static final int $stable;

   static { $stable = 8; }

   public Ticket$TicketAttribute$FilesAttribute(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, boolean var4, @NotNull TicketAttributeType var5, @NotNull List var6
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
   public Ticket$TicketAttribute$FilesAttribute(
      String var1, String var2, String var3, boolean var4, TicketAttributeType var5, List var6, int var7, DefaultConstructorMarker var8
   ) {
      this((((var7 & 1) != 0) ? (String) ("") : var1), (((var7 & 2) != 0) ? (String) ("") : var2), (((var7 & 4) != 0) ? (String) ("") : var3), (((var7 & 8) != 0) ? (boolean) (false) : var4), var5, var6);
   }

   @NotNull
   public final List getValue() {
      return this.value;
   }

   @Override
   public boolean hasValue() {
      return !this.value.isEmpty();
   }
}
