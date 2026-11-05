package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NullSafety;

public class LastParticipatingAdmin implements Parcelable {
   private static final String TWITTER = "twitter";
   private static final String LINKED_IN = "linkedin";
   private final long lastActiveAt;
   private final String firstName;
   private final String intro;
   private final String jobTitle;
   private final Location location;
   private final Avatar avatar;
   private final SocialAccount twitter;
   private final SocialAccount linkedIn;
   private final boolean isActive;
   private final boolean isBot;
   public static final Creator CREATOR = new LastParticipatingAdmin$1();
   public static final LastParticipatingAdmin NULL = new LastParticipatingAdmin(new LastParticipatingAdmin$Builder());
   public static final LastParticipatingAdmin NONE = new LastParticipatingAdmin(new LastParticipatingAdmin$Builder());

   public LastParticipatingAdmin(LastParticipatingAdmin$Builder var1) {
      this.avatar = var1.avatar == null ? new Avatar$Builder().build() : var1.avatar.build();
      this.firstName = NullSafety.valueOrEmpty(var1.first_name);
      this.intro = NullSafety.valueOrEmpty(var1.intro);
      this.jobTitle = NullSafety.valueOrEmpty(var1.job_title);
      this.location = var1.location == null ? new Location() : var1.location;
      this.lastActiveAt = var1.last_active_at;
      this.isActive = var1.is_active;
      this.isBot = var1.is_bot;
      SocialAccount var2 = SocialAccount.NULL;
      SocialAccount var3 = SocialAccount.NULL;
      if (var1.social_accounts != null) {
         for (Object recoveredSocialAccount : var1.social_accounts) {
            SocialAccount var5 = (SocialAccount)recoveredSocialAccount;
            if (var5 != null) {
               if ("twitter".equals(var5.getProvider())) {
                  var2 = var5;
               } else if ("linkedin".equals(var5.getProvider())) {
                  var3 = var5;
               }
            }
         }
      }

      this.twitter = var2;
      this.linkedIn = var3;
   }

   public long getLastActiveAt() {
      return this.lastActiveAt;
   }

   public Avatar getAvatar() {
      return this.avatar;
   }

   public String getFirstName() {
      return this.firstName;
   }

   public String getIntro() {
      return this.intro;
   }

   public String getJobTitle() {
      return this.jobTitle;
   }

   public Location getLocation() {
      return this.location;
   }

   public SocialAccount getTwitter() {
      return this.twitter;
   }

   public SocialAccount getLinkedIn() {
      return this.linkedIn;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isBot() {
      return this.isBot;
   }

   LastParticipatingAdmin(Parcel var1) {
      this.lastActiveAt = var1.readLong();
      this.isActive = var1.readByte() != 0;
      this.avatar = (Avatar)var1.readValue(Avatar.class.getClassLoader());
      this.firstName = var1.readString();
      this.intro = var1.readString();
      this.jobTitle = var1.readString();
      this.location = (Location)var1.readValue(Location.class.getClassLoader());
      this.twitter = (SocialAccount)var1.readValue(SocialAccount.class.getClassLoader());
      this.linkedIn = (SocialAccount)var1.readValue(SocialAccount.class.getClassLoader());
      this.isBot = var1.readByte() != 0;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeLong(this.lastActiveAt);
      var1.writeByte((byte)(this.isActive ? 1 : 0));
      var1.writeValue(this.avatar);
      var1.writeString(this.firstName);
      var1.writeString(this.intro);
      var1.writeString(this.jobTitle);
      var1.writeValue(this.location);
      var1.writeValue(this.twitter);
      var1.writeValue(this.linkedIn);
      var1.writeByte((byte)(this.isBot ? 1 : 0));
   }

   public static boolean isNull(@Nullable LastParticipatingAdmin var0) {
      return var0 != NONE && (NULL.equals(var0) || var0 == null);
   }
}
