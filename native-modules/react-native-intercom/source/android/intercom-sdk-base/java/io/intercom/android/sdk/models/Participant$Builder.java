package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NameUtils;
import io.intercom.android.sdk.utilities.NullSafety;
import java.util.Objects;

public final class Participant$Builder {
   @Nullable
   String id;
   @Nullable
   String name;
   @Nullable
   String type;
   @Nullable
   String email;
   @Nullable
   Avatar$Builder avatar;
   @Nullable
   Boolean is_bot;

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Participant$Builder var2 = (Participant$Builder)var1;
         return Objects.equals(this.id, var2.id)
            && Objects.equals(this.name, var2.name)
            && Objects.equals(this.type, var2.type)
            && Objects.equals(this.email, var2.email)
            && Objects.equals(this.avatar, var2.avatar)
            && Objects.equals(this.is_bot, var2.is_bot);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id, this.name, this.type, this.email, this.avatar, this.is_bot);
   }

   public Participant build() {
      String var1 = this.type == null ? "user" : this.type;
      String var2 = NullSafety.valueOrEmpty(this.name);
      String var3 = NullSafety.valueOrEmpty(this.email);
      String var4 = NullSafety.valueOrEmpty(this.avatar != null ? this.avatar.initials : null).isEmpty()
         ? NameUtils.getInitial(var2.isEmpty() ? var3 : var2)
         : this.avatar.initials;
      Avatar var5 = this.avatar == null ? Avatar.create("", var4) : this.avatar.withInitials(var4).build();
      Boolean var6 = NullSafety.valueOrDefault(this.is_bot, false);
      return Participant.create(NullSafety.valueOrEmpty(this.id), var2, var1, var3, var5, var6);
   }

   public Participant$Builder withId(String var1) {
      this.id = var1;
      return this;
   }

   public Participant$Builder withName(String var1) {
      this.name = var1;
      return this;
   }

   public Participant$Builder withEmail(String var1) {
      this.email = var1;
      return this;
   }

   public Participant$Builder withType(String var1) {
      this.type = var1;
      return this;
   }

   public Participant$Builder withIsBot(boolean var1) {
      this.is_bot = var1;
      return this;
   }

   public Participant$Builder withAvatar(Avatar$Builder var1) {
      this.avatar = var1;
      return this;
   }
}
