package io.intercom.android.sdk.m5.helpcenter.ui;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterDestination;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "COLLECTIONS",
         "COLLECTION_DETAILS",
         "intercom-sdk-base_release"
   }
)
public enum HelpCenterDestination {
   COLLECTIONS,
   COLLECTION_DETAILS;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(HelpCenterDestination.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
