package io.intercom.android.sdk.models;

import com.google.gson.annotations.SerializedName;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/models/TicketAttributeType;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "STRING",
         "INTEGER",
         "FLOAT",
         "BOOLEAN",
         "DATETIME",
         "LIST",
         "FILES",
         "intercom-sdk-base_release"
   }
)
public enum TicketAttributeType {
   @SerializedName("string")
   STRING,
   @SerializedName("integer")
   INTEGER,
   @SerializedName("float")
   FLOAT,
   @SerializedName("boolean")
   BOOLEAN,
   @SerializedName("datetime")
   DATETIME,
   @SerializedName("list")
   LIST,
   @SerializedName("files")
   FILES;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(TicketAttributeType.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
