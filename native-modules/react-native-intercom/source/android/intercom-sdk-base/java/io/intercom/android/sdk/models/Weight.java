package io.intercom.android.sdk.models;

import com.google.gson.annotations.SerializedName;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/models/Weight;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "REGULAR",
         "LIGHT",
         "MEDIUM",
         "SEMI_BOLD",
         "BOLD",
         "intercom-sdk-base_release"
   }
)
public enum Weight {
   @SerializedName("regular")
   REGULAR,
   @SerializedName("light")
   LIGHT,
   @SerializedName("medium")
   MEDIUM,
   @SerializedName("semibold")
   SEMI_BOLD,
   @SerializedName("bold")
   BOLD;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(Weight.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
