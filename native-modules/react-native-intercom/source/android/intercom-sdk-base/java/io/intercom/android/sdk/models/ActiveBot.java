package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B1\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\bHÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00052\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0018H×\u0001J\t\u0010\u0019\u001a\u00020\u001aH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\rR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/models/ActiveBot;",
         "",
         "participant",
         "Lio/intercom/android/sdk/models/Participant$Builder;",
         "isAi",
         "",
         "isIdentityCustomized",
         "streamingPart",
         "Lio/intercom/android/sdk/models/StreamingPart;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Participant$Builder;ZZLio/intercom/android/sdk/models/StreamingPart;)V",
         "getParticipant",
         "()Lio/intercom/android/sdk/models/Participant$Builder;",
         "()Z",
         "getStreamingPart",
         "()Lio/intercom/android/sdk/models/StreamingPart;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ActiveBot {
   @SerializedName("participant")
   @NotNull
   private final Participant$Builder participant;
   @SerializedName("has_ai")
   private final boolean isAi;
   @SerializedName("has_customized_identity")
   private final boolean isIdentityCustomized;
   @SerializedName("streaming_part")
   @Nullable
   private final StreamingPart streamingPart;
   public static final int $stable;

   static { $stable = 8; }

   public ActiveBot(@NotNull Participant$Builder var1, boolean var2, boolean var3, @Nullable StreamingPart var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "participant");
      this.participant = var1;
      this.isAi = var2;
      this.isIdentityCustomized = var3;
      this.streamingPart = var4;
   }

   // $VF: synthetic method
   public ActiveBot(Participant$Builder var1, boolean var2, boolean var3, StreamingPart var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (Participant$Builder) (new Participant$Builder()) : var1), (((var5 & 2) != 0) ? (boolean) (false) : var2), (((var5 & 4) != 0) ? (boolean) (false) : var3), (((var5 & 8) != 0) ? (StreamingPart) (null) : var4));
   }

   @NotNull
   public final Participant$Builder getParticipant() {
      return this.participant;
   }

   public final boolean isAi() {
      return this.isAi;
   }

   public final boolean isIdentityCustomized() {
      return this.isIdentityCustomized;
   }

   @Nullable
   public final StreamingPart getStreamingPart() {
      return this.streamingPart;
   }

   @NotNull
   public final Participant$Builder component1() {
      return this.participant;
   }

   public final boolean component2() {
      return this.isAi;
   }

   public final boolean component3() {
      return this.isIdentityCustomized;
   }

   @Nullable
   public final StreamingPart component4() {
      return this.streamingPart;
   }

   @NotNull
   public final ActiveBot copy(@NotNull Participant$Builder var1, boolean var2, boolean var3, @Nullable StreamingPart var4) {
      Intrinsics.checkNotNullParameter(var1, "participant");
      return new ActiveBot(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ActiveBot copy$default(ActiveBot var0, Participant$Builder var1, boolean var2, boolean var3, StreamingPart var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.participant;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.isAi;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.isIdentityCustomized;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.streamingPart;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ActiveBot(participant="
         + this.participant
         + ", isAi="
         + this.isAi
         + ", isIdentityCustomized="
         + this.isIdentityCustomized
         + ", streamingPart="
         + this.streamingPart
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.participant.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isAi);
      var1 = var1 * 31 + Boolean.hashCode(this.isIdentityCustomized);
      return var1 * 31 + (this.streamingPart == null ? 0 : this.streamingPart.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ActiveBot)) {
         return false;
      } else {
         ActiveBot var2 = (ActiveBot)var1;
         if (!Intrinsics.areEqual(this.participant, var2.participant)) {
            return false;
         } else if (this.isAi != var2.isAi) {
            return false;
         } else {
            return this.isIdentityCustomized != var2.isIdentityCustomized ? false : Intrinsics.areEqual(this.streamingPart, var2.streamingPart);
         }
      }
   }

   public ActiveBot() {
      this(null, false, false, null, 15, null);
   }
}
