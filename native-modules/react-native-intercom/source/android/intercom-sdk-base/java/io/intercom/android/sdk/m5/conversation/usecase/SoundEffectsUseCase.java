package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.conversation.utils.SoundPlayer;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u0019\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0000¢\u0006\u0002\b\u0010R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;",
         "",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "soundPlayer",
         "Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;",
         "<init>",
         "(Lio/intercom/android/sdk/identity/AppConfig;Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;)V",
         "getAppConfig",
         "()Lio/intercom/android/sdk/identity/AppConfig;",
         "getSoundPlayer",
         "()Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;",
         "invoke",
         "",
         "action",
         "Lio/intercom/android/sdk/m5/conversation/usecase/Action;",
         "invoke$intercom_sdk_base_release",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SoundEffectsUseCase {
   @NotNull
   private final AppConfig appConfig;
   @NotNull
   private final SoundPlayer soundPlayer;
   public static final int $stable;

   static { $stable = 8; }

   public SoundEffectsUseCase(@NotNull AppConfig var1, @NotNull SoundPlayer var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "appConfig");
      Intrinsics.checkNotNullParameter(var2, "soundPlayer");
      this.appConfig = var1;
      this.soundPlayer = var2;
   }

   // $VF: synthetic method
   public SoundEffectsUseCase(AppConfig var1, SoundPlayer var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (AppConfig) ((AppConfig)Injector.get().getAppConfigProvider().get()) : var1), var2);
   }

   @NotNull
   public final AppConfig getAppConfig() {
      return this.appConfig;
   }

   @NotNull
   public final SoundPlayer getSoundPlayer() {
      return this.soundPlayer;
   }

   public final void invoke$intercom_sdk_base_release(@NotNull Action var1) {
      Intrinsics.checkNotNullParameter(var1, "action");
      if (this.appConfig.isAudioEnabled()) {
         switch (var1) {
            case MESSAGE_SENT:
               this.soundPlayer.playMessageSentSound();
               break;
            case ADMIN_MESSAGE_RECEIVED:
               this.soundPlayer.playAdminMessageReceivedSound();
               break;
            case OPERATOR_MESSAGE_RECEIVED:
               this.soundPlayer.playOperatorMessageReceivedSound();
               break;
            case MESSAGE_FAILED:
               this.soundPlayer.playMessageFailedSound();
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }
}
