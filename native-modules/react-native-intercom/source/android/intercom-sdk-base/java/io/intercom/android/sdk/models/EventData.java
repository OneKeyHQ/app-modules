package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 72\u00020\u0001:\u00017Br\u0012\u000f\b\u0002\u0010\u0002\u001a\t\u0018\u00010\u0003¢\u0006\u0002\b\u0004\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010!\u001a\t\u0018\u00010\u0003¢\u0006\u0002\b\u0004HÂ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0006HÂ\u0003J\u0011\u0010#\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bHÂ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0006HÂ\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u000b\u0010'\u001a\u0004\u0018\u00010\u0006HÆ\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0006HÆ\u0003Jt\u0010)\u001a\u00020\u00002\u000f\b\u0002\u0010\u0002\u001a\t\u0018\u00010\u0003¢\u0006\u0002\b\u00042\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0006HÇ\u0001J\b\u0010*\u001a\u00020+H\u0007J\u0013\u0010,\u001a\u00020-2\b\u0010.\u001a\u0004\u0018\u00010/H×\u0003J\t\u00100\u001a\u00020+H×\u0001J\t\u00101\u001a\u00020\u0006H×\u0001J\u0018\u00102\u001a\u0002032\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u00020+H\u0007R\u0017\u0010\u0002\u001a\t\u0018\u00010\u0003¢\u0006\u0002\b\u00048\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\n\u001a\u0004\u0018\u00010\u00068\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u000b\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0018\u0010\f\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\u0016\u001a\u00020\u00178F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u001a\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u0012R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\t0\b8F¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u001f\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b \u0010\u0012¨\u00068",
   d2 = {
         "Lio/intercom/android/sdk/models/EventData;",
         "Landroid/os/Parcelable;",
         "_participant",
         "Lio/intercom/android/sdk/models/EventParticipant$Builder;",
         "Lkotlinx/parcelize/RawValue;",
         "_eventAsPlainText",
         "",
         "_eventContent",
         "",
         "Lio/intercom/android/sdk/models/EventContent;",
         "_status",
         "customStateLabel",
         "customStatePrefix",
         "mergedConversationId",
         "description",
         "<init>",
         "(Lio/intercom/android/sdk/models/EventParticipant$Builder;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getCustomStateLabel",
         "()Ljava/lang/String;",
         "getCustomStatePrefix",
         "getMergedConversationId",
         "getDescription",
         "participant",
         "Lio/intercom/android/sdk/models/EventParticipant;",
         "getParticipant",
         "()Lio/intercom/android/sdk/models/EventParticipant;",
         "eventAsPlainText",
         "getEventAsPlainText",
         "eventContent",
         "getEventContent",
         "()Ljava/util/List;",
         "status",
         "getStatus",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "describeContents",
         "",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
public final class EventData implements Parcelable {
   @NotNull
   public static final EventData$Companion Companion = new EventData$Companion(null);
   @SerializedName("participant")
   @Nullable
   private final EventParticipant$Builder _participant;
   @SerializedName("eventAsPlainText")
   @Nullable
   private final String _eventAsPlainText;
   @SerializedName("eventContent")
   @Nullable
   private final List _eventContent;
   @SerializedName("status")
   @Nullable
   private final String _status;
   @SerializedName("customStateLabel")
   @Nullable
   private final String customStateLabel;
   @SerializedName("customStatePrefix")
   @Nullable
   private final String customStatePrefix;
   @Nullable
   private final String mergedConversationId;
   @Nullable
   private final String description;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new EventData$Creator();
   @JvmField
   @NotNull
   public static final EventData NULL = new EventData(null, null, null, null, null, null, null, null, 255, null);

   public EventData(
      @Nullable EventParticipant$Builder var1,
      @Nullable String var2,
      @Nullable List var3,
      @Nullable String var4,
      @Nullable String var5,
      @Nullable String var6,
      @Nullable String var7,
      @Nullable String var8
   ) {
      this._participant = var1;
      this._eventAsPlainText = var2;
      this._eventContent = var3;
      this._status = var4;
      this.customStateLabel = var5;
      this.customStatePrefix = var6;
      this.mergedConversationId = var7;
      this.description = var8;
   }

   // $VF: synthetic method
   public EventData(
      EventParticipant$Builder var1,
      String var2,
      List var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      int var9,
      DefaultConstructorMarker var10
   ) {
      this((((var9 & 1) != 0) ? (EventParticipant$Builder) (null) : var1), (((var9 & 2) != 0) ? (String) (null) : var2), (((var9 & 4) != 0) ? (List) (null) : var3), (((var9 & 8) != 0) ? (String) (null) : var4), (((var9 & 16) != 0) ? (String) (null) : var5), (((var9 & 32) != 0) ? (String) (null) : var6), (((var9 & 64) != 0) ? (String) (null) : var7), (((var9 & 128) != 0) ? (String) (null) : var8));
   }

   @Nullable
   public final String getCustomStateLabel() {
      return this.customStateLabel;
   }

   @Nullable
   public final String getCustomStatePrefix() {
      return this.customStatePrefix;
   }

   @Nullable
   public final String getMergedConversationId() {
      return this.mergedConversationId;
   }

   @Nullable
   public final String getDescription() {
      return this.description;
   }

   @NotNull
   public final EventParticipant getParticipant() {
      if (this._participant != null) {
         EventParticipant var10000 = this._participant.build();
         if (var10000 != null) {
            return var10000;
         }
      }

      EventParticipant var1 = EventParticipant.NULL;
      Intrinsics.checkNotNullExpressionValue(EventParticipant.NULL, "NULL");
      return var1;
   }

   @NotNull
   public final String getEventAsPlainText() {
      String var10000 = this._eventAsPlainText;
      if (this._eventAsPlainText == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final List getEventContent() {
      List var10000 = this._eventContent;
      if (this._eventContent == null) {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   @NotNull
   public final String getStatus() {
      String var10000 = this._status;
      if (this._status == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeValue(this._participant);
      var1.writeString(this._eventAsPlainText);
      List var3 = this._eventContent;
      if (var3 == null) {
         var1.writeInt(0);
      } else {
         var1.writeInt(1);
         var1.writeInt(var3.size());
         Iterator var4 = var3.iterator();

         while (var4.hasNext()) {
            ((EventContent)var4.next()).writeToParcel(var1, var2);
         }
      }

      var1.writeString(this._status);
      var1.writeString(this.customStateLabel);
      var1.writeString(this.customStatePrefix);
      var1.writeString(this.mergedConversationId);
      var1.writeString(this.description);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   private final EventParticipant$Builder component1() {
      return this._participant;
   }

   private final String component2() {
      return this._eventAsPlainText;
   }

   private final List component3() {
      return this._eventContent;
   }

   private final String component4() {
      return this._status;
   }

   @Nullable
   public final String component5() {
      return this.customStateLabel;
   }

   @Nullable
   public final String component6() {
      return this.customStatePrefix;
   }

   @Nullable
   public final String component7() {
      return this.mergedConversationId;
   }

   @Nullable
   public final String component8() {
      return this.description;
   }

   @NotNull
   public final EventData copy(
      @Nullable EventParticipant$Builder var1,
      @Nullable String var2,
      @Nullable List var3,
      @Nullable String var4,
      @Nullable String var5,
      @Nullable String var6,
      @Nullable String var7,
      @Nullable String var8
   ) {
      return new EventData(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static EventData copy$default(
      EventData var0,
      EventParticipant$Builder var1,
      String var2,
      List var3,
      String var4,
      String var5,
      String var6,
      String var7,
      String var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0._participant;
      }

      if ((var9 & 2) != 0) {
         var2 = var0._eventAsPlainText;
      }

      if ((var9 & 4) != 0) {
         var3 = var0._eventContent;
      }

      if ((var9 & 8) != 0) {
         var4 = var0._status;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.customStateLabel;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.customStatePrefix;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.mergedConversationId;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.description;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "EventData(_participant="
         + this._participant
         + ", _eventAsPlainText="
         + this._eventAsPlainText
         + ", _eventContent="
         + this._eventContent
         + ", _status="
         + this._status
         + ", customStateLabel="
         + this.customStateLabel
         + ", customStatePrefix="
         + this.customStatePrefix
         + ", mergedConversationId="
         + this.mergedConversationId
         + ", description="
         + this.description
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._participant == null ? 0 : this._participant.hashCode();
      var1 = var1 * 31 + (this._eventAsPlainText == null ? 0 : this._eventAsPlainText.hashCode());
      var1 = var1 * 31 + (this._eventContent == null ? 0 : this._eventContent.hashCode());
      var1 = var1 * 31 + (this._status == null ? 0 : this._status.hashCode());
      var1 = var1 * 31 + (this.customStateLabel == null ? 0 : this.customStateLabel.hashCode());
      var1 = var1 * 31 + (this.customStatePrefix == null ? 0 : this.customStatePrefix.hashCode());
      var1 = var1 * 31 + (this.mergedConversationId == null ? 0 : this.mergedConversationId.hashCode());
      return var1 * 31 + (this.description == null ? 0 : this.description.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof EventData)) {
         return false;
      } else {
         EventData var2 = (EventData)var1;
         if (!Intrinsics.areEqual(this._participant, var2._participant)) {
            return false;
         } else if (!Intrinsics.areEqual(this._eventAsPlainText, var2._eventAsPlainText)) {
            return false;
         } else if (!Intrinsics.areEqual(this._eventContent, var2._eventContent)) {
            return false;
         } else if (!Intrinsics.areEqual(this._status, var2._status)) {
            return false;
         } else if (!Intrinsics.areEqual(this.customStateLabel, var2.customStateLabel)) {
            return false;
         } else if (!Intrinsics.areEqual(this.customStatePrefix, var2.customStatePrefix)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.mergedConversationId, var2.mergedConversationId) ? false : Intrinsics.areEqual(this.description, var2.description);
         }
      }
   }

   public EventData() {
      this(null, null, null, null, null, null, null, null, 255, null);
   }
}
