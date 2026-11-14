package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u0000 !2\u00020\u0001:\u0001!B=\u0012\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\n\u0010\u000bJ\u0011\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\bHÆ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\bHÆ\u0003J?\u0010\u001a\u001a\u00020\u00002\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020\bH×\u0001R\u001e\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0018\u0010\t\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00038F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\r¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/models/TeamPresence;",
         "",
         "activeAdmins",
         "",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "activeBot",
         "Lio/intercom/android/sdk/models/ActiveBot;",
         "expectedResponseDelayShortText",
         "",
         "expectedResponseDelayLongText",
         "<init>",
         "(Ljava/util/List;Lio/intercom/android/sdk/models/ActiveBot;Ljava/lang/String;Ljava/lang/String;)V",
         "getActiveAdmins",
         "()Ljava/util/List;",
         "getActiveBot",
         "()Lio/intercom/android/sdk/models/ActiveBot;",
         "getExpectedResponseDelayShortText",
         "()Ljava/lang/String;",
         "getExpectedResponseDelayLongText",
         "builtActiveAdmins",
         "Lio/intercom/android/sdk/models/Participant;",
         "getBuiltActiveAdmins",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nTeamPresence.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamPresence.kt\nio/intercom/android/sdk/models/TeamPresence\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,23:1\n1563#2:24\n1634#2,3:25\n*S KotlinDebug\n*F\n+ 1 TeamPresence.kt\nio/intercom/android/sdk/models/TeamPresence\n*L\n21#1:24\n21#1:25,3\n*E\n"
)
public final class TeamPresence {
   @NotNull
   public static final TeamPresence$Companion Companion = new TeamPresence$Companion(null);
   @SerializedName("active_admins")
   @Nullable
   private final List activeAdmins;
   @SerializedName("active_bot")
   @Nullable
   private final ActiveBot activeBot;
   @SerializedName("expected_response_delay_short_text")
   @Nullable
   private final String expectedResponseDelayShortText;
   @SerializedName("expected_response_delay_long_text")
   @Nullable
   private final String expectedResponseDelayLongText;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   public static final TeamPresence NULL = new TeamPresence(null, null, null, null, 15, null);

   public TeamPresence(@Nullable List var1, @Nullable ActiveBot var2, @Nullable String var3, @Nullable String var4) {
      this.activeAdmins = var1;
      this.activeBot = var2;
      this.expectedResponseDelayShortText = var3;
      this.expectedResponseDelayLongText = var4;
   }

   // $VF: synthetic method
   public TeamPresence(List var1, ActiveBot var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (List) (null) : var1), (((var5 & 2) != 0) ? (ActiveBot) (null) : var2), (((var5 & 4) != 0) ? (String) (null) : var3), (((var5 & 8) != 0) ? (String) (null) : var4));
   }

   @Nullable
   public final List getActiveAdmins() {
      return this.activeAdmins;
   }

   @Nullable
   public final ActiveBot getActiveBot() {
      return this.activeBot;
   }

   @Nullable
   public final String getExpectedResponseDelayShortText() {
      return this.expectedResponseDelayShortText;
   }

   @Nullable
   public final String getExpectedResponseDelayLongText() {
      return this.expectedResponseDelayLongText;
   }

   @NotNull
   public final List getBuiltActiveAdmins() {
      List var10000 = this.activeAdmins;
      if (this.activeAdmins == null) {
         var10000 = CollectionsKt.emptyList();
      }

      Iterable var1 = var10000;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         Participant$Builder var8 = (Participant$Builder)var7;
         Collection var10 = var4;
         int var9 = 0;
         var10.add(var8.build());
      }

      return (List)var4;
   }

   @Nullable
   public final List component1() {
      return this.activeAdmins;
   }

   @Nullable
   public final ActiveBot component2() {
      return this.activeBot;
   }

   @Nullable
   public final String component3() {
      return this.expectedResponseDelayShortText;
   }

   @Nullable
   public final String component4() {
      return this.expectedResponseDelayLongText;
   }

   @NotNull
   public final TeamPresence copy(@Nullable List var1, @Nullable ActiveBot var2, @Nullable String var3, @Nullable String var4) {
      return new TeamPresence(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static TeamPresence copy$default(TeamPresence var0, List var1, ActiveBot var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.activeAdmins;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.activeBot;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.expectedResponseDelayShortText;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.expectedResponseDelayLongText;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "TeamPresence(activeAdmins="
         + this.activeAdmins
         + ", activeBot="
         + this.activeBot
         + ", expectedResponseDelayShortText="
         + this.expectedResponseDelayShortText
         + ", expectedResponseDelayLongText="
         + this.expectedResponseDelayLongText
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.activeAdmins == null ? 0 : this.activeAdmins.hashCode();
      var1 = var1 * 31 + (this.activeBot == null ? 0 : this.activeBot.hashCode());
      var1 = var1 * 31 + (this.expectedResponseDelayShortText == null ? 0 : this.expectedResponseDelayShortText.hashCode());
      return var1 * 31 + (this.expectedResponseDelayLongText == null ? 0 : this.expectedResponseDelayLongText.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TeamPresence)) {
         return false;
      } else {
         TeamPresence var2 = (TeamPresence)var1;
         if (!Intrinsics.areEqual(this.activeAdmins, var2.activeAdmins)) {
            return false;
         } else if (!Intrinsics.areEqual(this.activeBot, var2.activeBot)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.expectedResponseDelayShortText, var2.expectedResponseDelayShortText)
               ? false
               : Intrinsics.areEqual(this.expectedResponseDelayLongText, var2.expectedResponseDelayLongText);
         }
      }
   }

   public TeamPresence() {
      this(null, null, null, null, 15, null);
   }
}
