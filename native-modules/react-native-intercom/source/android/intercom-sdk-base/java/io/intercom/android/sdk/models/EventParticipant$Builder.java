package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;

public final class EventParticipant$Builder {
   @Nullable
   String id;
   @Nullable
   String initial;
   @Nullable
   String label;
   @Nullable
   String type;
   @Nullable
   Avatar$Builder avatar;

   public EventParticipant build() {
      return new EventParticipant(this, null);
   }
}
