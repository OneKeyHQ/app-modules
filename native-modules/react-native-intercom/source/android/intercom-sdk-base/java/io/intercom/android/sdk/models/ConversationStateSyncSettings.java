package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0010\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00032\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0014\u001a\u00020\u0005H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationStateSyncSettings;",
         "",
         "enabled",
         "",
         "startTimeout",
         "",
         "syncInterval",
         "<init>",
         "(ZII)V",
         "getEnabled",
         "()Z",
         "getStartTimeout",
         "()I",
         "getSyncInterval",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ConversationStateSyncSettings {
   @NotNull
   public static final ConversationStateSyncSettings$Companion Companion = new ConversationStateSyncSettings$Companion(null);
   @SerializedName("enabled")
   private final boolean enabled;
   @SerializedName("start_timeout")
   private final int startTimeout;
   @SerializedName("sync_interval")
   private final int syncInterval;
   public static int $stable;

   static {}
   @NotNull
   private static final ConversationStateSyncSettings DEFAULT = new ConversationStateSyncSettings(false, 0, 0);

   public ConversationStateSyncSettings(boolean var1, int var2, int var3) {
      this.enabled = var1;
      this.startTimeout = var2;
      this.syncInterval = var3;
   }

   public final boolean getEnabled() {
      return this.enabled;
   }

   public final int getStartTimeout() {
      return this.startTimeout;
   }

   public final int getSyncInterval() {
      return this.syncInterval;
   }

   public final boolean component1() {
      return this.enabled;
   }

   public final int component2() {
      return this.startTimeout;
   }

   public final int component3() {
      return this.syncInterval;
   }

   @NotNull
   public final ConversationStateSyncSettings copy(boolean var1, int var2, int var3) {
      return new ConversationStateSyncSettings(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ConversationStateSyncSettings copy$default(ConversationStateSyncSettings var0, boolean var1, int var2, int var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.enabled;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.startTimeout;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.syncInterval;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationStateSyncSettings(enabled=" + this.enabled + ", startTimeout=" + this.startTimeout + ", syncInterval=" + this.syncInterval + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.enabled);
      var1 = var1 * 31 + Integer.hashCode(this.startTimeout);
      return var1 * 31 + Integer.hashCode(this.syncInterval);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationStateSyncSettings)) {
         return false;
      } else {
         ConversationStateSyncSettings var2 = (ConversationStateSyncSettings)var1;
         if (this.enabled != var2.enabled) {
            return false;
         } else {
            return this.startTimeout != var2.startTimeout ? false : this.syncInterval == var2.syncInterval;
         }
      }
   }

   // $VF: synthetic method
   public static final ConversationStateSyncSettings access$getDEFAULT$cp() {
      return DEFAULT;
   }
}
