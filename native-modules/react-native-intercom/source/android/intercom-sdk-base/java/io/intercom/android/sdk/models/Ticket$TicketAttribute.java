package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0005\u0015\u0016\u0017\u0018\u0019B9\b\u0004\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010\u0014\u001a\u00020\u0007H&R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u0082\u0001\u0005\u001a\u001b\u001c\u001d\u001e¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute;",
         "",
         "id",
         "",
         "identifier",
         "name",
         "required",
         "",
         "type",
         "Lio/intercom/android/sdk/models/TicketAttributeType;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/TicketAttributeType;)V",
         "getId",
         "()Ljava/lang/String;",
         "getIdentifier",
         "getName",
         "getRequired",
         "()Z",
         "getType",
         "()Lio/intercom/android/sdk/models/TicketAttributeType;",
         "hasValue",
         "PrimitiveAttribute",
         "ListAttribute",
         "DateTimeAttribute",
         "FilesAttribute",
         "UnSupported",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$DateTimeAttribute;",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$FilesAttribute;",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$ListAttribute;",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$PrimitiveAttribute;",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$UnSupported;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class Ticket$TicketAttribute {
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("identifier")
   @NotNull
   private final String identifier;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("required")
   private final boolean required;
   @SerializedName("type")
   @NotNull
   private final TicketAttributeType type;
   public static int $stable;

   static {}

   private Ticket$TicketAttribute(String var1, String var2, String var3, boolean var4, TicketAttributeType var5) {
      this.id = var1;
      this.identifier = var2;
      this.name = var3;
      this.required = var4;
      this.type = var5;
   }

   // $VF: synthetic method
   public Ticket$TicketAttribute(String var1, String var2, String var3, boolean var4, TicketAttributeType var5, int var6, DefaultConstructorMarker var7) {
      if ((var6 & 1) != 0) {
         var1 = "";
      }

      if ((var6 & 2) != 0) {
         var2 = "";
      }

      if ((var6 & 4) != 0) {
         var3 = "";
      }

      if ((var6 & 8) != 0) {
         var4 = false;
      }

      this(var1, var2, var3, var4, var5, null);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getIdentifier() {
      return this.identifier;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final boolean getRequired() {
      return this.required;
   }

   @NotNull
   public final TicketAttributeType getType() {
      return this.type;
   }

   public abstract boolean hasValue();

   // $VF: synthetic method
   public Ticket$TicketAttribute(String var1, String var2, String var3, boolean var4, TicketAttributeType var5, DefaultConstructorMarker var6) {
      this(var1, var2, var3, var4, var5);
   }
}
