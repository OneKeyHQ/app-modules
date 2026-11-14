package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0001\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/TeamPresenceState;",
         "",
         "expandedTeamPresenceState",
         "Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;",
         "teamIntro",
         "",
         "specialNotice",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;Ljava/lang/String;Ljava/lang/String;)V",
         "getExpandedTeamPresenceState",
         "()Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;",
         "getTeamIntro",
         "()Ljava/lang/String;",
         "getSpecialNotice",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TeamPresenceState {
   @NotNull
   private final ExpandedTeamPresenceState expandedTeamPresenceState;
   @NotNull
   private final String teamIntro;
   @NotNull
   private final String specialNotice;
   public static final int $stable;

   static { $stable = 8; }

   public TeamPresenceState(@NotNull ExpandedTeamPresenceState var1, @NotNull String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "expandedTeamPresenceState");
      Intrinsics.checkNotNullParameter(var2, "teamIntro");
      Intrinsics.checkNotNullParameter(var3, "specialNotice");
      this.expandedTeamPresenceState = var1;
      this.teamIntro = var2;
      this.specialNotice = var3;
   }

   @NotNull
   public final ExpandedTeamPresenceState getExpandedTeamPresenceState() {
      return this.expandedTeamPresenceState;
   }

   @NotNull
   public final String getTeamIntro() {
      return this.teamIntro;
   }

   @NotNull
   public final String getSpecialNotice() {
      return this.specialNotice;
   }
}
