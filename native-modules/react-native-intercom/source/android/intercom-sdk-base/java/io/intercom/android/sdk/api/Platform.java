package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/api/Platform;",
         "",
         "identifier",
         "",
         "<init>",
         "(Ljava/lang/String;ILjava/lang/String;)V",
         "getIdentifier",
         "()Ljava/lang/String;",
         "Cordova",
         "ReactNative",
         "Unity",
         "Flutter",
         "Native",
         "intercom-sdk-base_release"
   }
)
public enum Platform {
   Cordova("intercom-sdk-cordova"),
   ReactNative("intercom-sdk-react-native"),
   Unity("intercom-sdk-unity"),
   Flutter("intercom-sdk-flutter"),
   Native("intercom-sdk-native");

   @NotNull
   private final String identifier;
   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(Platform.values());

   Platform(String var3) {
      this.identifier = var3;
   }

   @NotNull
   public final String getIdentifier() {
      return this.identifier;
   }

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
