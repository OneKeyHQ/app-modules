package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.utilities.NullSafety;

public class EventParticipant implements Parcelable {
   private final String id;
   private final String initial;
   private final String label;
   private final String type;
   private final Avatar avatar;
   public static final EventParticipant NULL = new EventParticipant(new EventParticipant$Builder());
   public static final Creator CREATOR = new EventParticipant$1();

   private EventParticipant(EventParticipant$Builder var1) {
      this.id = NullSafety.valueOrEmpty(var1.id);
      this.initial = NullSafety.valueOrEmpty(var1.initial);
      this.label = NullSafety.valueOrEmpty(var1.label);
      this.type = NullSafety.valueOrEmpty(var1.type);
      this.avatar = var1.avatar == null ? new Avatar$Builder().build() : var1.avatar.build();
   }

   public String getId() {
      return this.id;
   }

   public String getInitial() {
      return this.initial;
   }

   public String getLabel() {
      return this.label;
   }

   public String getType() {
      return this.type;
   }

   public Avatar getAvatar() {
      return this.avatar;
   }

   protected EventParticipant(Parcel var1) {
      this.id = var1.readString();
      this.initial = var1.readString();
      this.label = var1.readString();
      this.type = var1.readString();
      this.avatar = (Avatar)var1.readValue(Avatar.class.getClassLoader());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.id);
      var1.writeString(this.initial);
      var1.writeString(this.label);
      var1.writeString(this.type);
      var1.writeValue(this.avatar);
   }

   // $VF: synthetic method
   EventParticipant(EventParticipant$Builder var1, EventParticipant$1 var2) {
      this(var1);
   }
}
