package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import java.util.List;

public final class LastParticipatingAdmin$Builder {
   long last_active_at;
   @Nullable
   Avatar$Builder avatar;
   @Nullable
   String first_name;
   @Nullable
   String intro;
   @Nullable
   String job_title;
   @Nullable
   Location location;
   @Nullable
   List social_accounts;
   boolean is_active;
   boolean is_bot;

   public LastParticipatingAdmin build() {
      return new LastParticipatingAdmin(this);
   }

   @VisibleForTesting
   public LastParticipatingAdmin$Builder withJobTitle(String var1) {
      this.job_title = var1;
      return this;
   }

   @VisibleForTesting
   public LastParticipatingAdmin$Builder withIntro(String var1) {
      this.intro = var1;
      return this;
   }

   @VisibleForTesting
   public LastParticipatingAdmin$Builder withFirstName(String var1) {
      this.first_name = var1;
      return this;
   }

   @VisibleForTesting
   public LastParticipatingAdmin$Builder withAvatar(Avatar$Builder var1) {
      this.avatar = var1;
      return this;
   }

   @VisibleForTesting
   public LastParticipatingAdmin$Builder withIsBot(Boolean var1) {
      this.is_bot = var1;
      return this;
   }
}
