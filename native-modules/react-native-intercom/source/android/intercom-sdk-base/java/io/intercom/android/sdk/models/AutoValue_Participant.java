package io.intercom.android.sdk.models;

import org.jspecify.annotations.Nullable;

final class AutoValue_Participant extends Participant {
   private final String getId;
   private final String getName;
   private final String getType;
   private final String getEmail;
   private final Avatar getAvatar;
   private final Boolean isBot;

   AutoValue_Participant(String var1, String var2, String var3, String var4, Avatar var5, Boolean var6) {
      if (var1 == null) {
         throw new NullPointerException("Null getId");
      }

      this.getId = var1;
      if (var2 == null) {
         throw new NullPointerException("Null getName");
      }

      this.getName = var2;
      if (var3 == null) {
         throw new NullPointerException("Null getType");
      }

      this.getType = var3;
      if (var4 == null) {
         throw new NullPointerException("Null getEmail");
      }

      this.getEmail = var4;
      if (var5 == null) {
         throw new NullPointerException("Null getAvatar");
      }

      this.getAvatar = var5;
      if (var6 == null) {
         throw new NullPointerException("Null isBot");
      }

      this.isBot = var6;
   }

   @Override
   public String getId() {
      return this.getId;
   }

   @Override
   public String getName() {
      return this.getName;
   }

   @Override
   public String getType() {
      return this.getType;
   }

   @Override
   public String getEmail() {
      return this.getEmail;
   }

   @Override
   public Avatar getAvatar() {
      return this.getAvatar;
   }

   @Override
   public Boolean isBot() {
      return this.isBot;
   }

   @Override
   public String toString() {
      return "Participant{getId="
         + this.getId
         + ", getName="
         + this.getName
         + ", getType="
         + this.getType
         + ", getEmail="
         + this.getEmail
         + ", getAvatar="
         + this.getAvatar
         + ", isBot="
         + this.isBot
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Participant)) {
         return false;
      }

      Participant var2 = (Participant)var1;
      return this.getId.equals(var2.getId())
         && this.getName.equals(var2.getName())
         && this.getType.equals(var2.getType())
         && this.getEmail.equals(var2.getEmail())
         && this.getAvatar.equals(var2.getAvatar())
         && this.isBot.equals(var2.isBot());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.getId.hashCode();
      var1 *= 1000003;
      var1 ^= this.getName.hashCode();
      var1 *= 1000003;
      var1 ^= this.getType.hashCode();
      var1 *= 1000003;
      var1 ^= this.getEmail.hashCode();
      var1 *= 1000003;
      var1 ^= this.getAvatar.hashCode();
      var1 *= 1000003;
      return var1 ^ this.isBot.hashCode();
   }
}
