package io.intercom.android.sdk.m5.conversation.utils;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import androidx.annotation.RawRes;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.raw;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0001\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0011J\u001c\u0010\u0015\u001a\u00020\u00072\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\b\b\u0001\u0010\u0016\u001a\u00020\u0007H\u0002J\u0006\u0010\u0017\u001a\u00020\u0011J\u0006\u0010\u0018\u001a\u00020\u0011J\u0006\u0010\u0019\u001a\u00020\u0011J\u0006\u0010\u001a\u001a\u00020\u0011J\u0018\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/utils/SoundPlayer;",
         "",
         "<init>",
         "()V",
         "soundPool",
         "Landroid/media/SoundPool;",
         "messageReceivedId",
         "",
         "replyFailedId",
         "replySentId",
         "operatorReceivedId",
         "messageReceivedLoaded",
         "",
         "replyFailedLoaded",
         "replySentLoaded",
         "operatorReceivedLoaded",
         "loadSounds",
         "",
         "context",
         "Landroid/content/Context;",
         "release",
         "loadSound",
         "soundId",
         "playMessageFailedSound",
         "playMessageSentSound",
         "playAdminMessageReceivedSound",
         "playOperatorMessageReceivedSound",
         "playIfLoaded",
         "loaded",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SoundPlayer {
   @NotNull
   public static final SoundPlayer$Companion Companion = new SoundPlayer$Companion(null);
   @NotNull
   private final SoundPool soundPool;
   private int messageReceivedId;
   private int replyFailedId;
   private int replySentId;
   private int operatorReceivedId;
   private boolean messageReceivedLoaded;
   private boolean replyFailedLoaded;
   private boolean replySentLoaded;
   private boolean operatorReceivedLoaded;
   public static final int $stable;

   static { $stable = 8; }
   private static final Twig TWIG = LumberMill.getLogger();
   public static final int NOT_FOUND_ID = -1;

   public SoundPlayer() {
      SoundPool var10001 = new Builder().setAudioAttributes(new android.media.AudioAttributes.Builder().setContentType(4).setUsage(5).build()).build();
      Intrinsics.checkNotNullExpressionValue(var10001, "build(...)");
      this.soundPool = var10001;
      this.messageReceivedId = -1;
      this.replyFailedId = -1;
      this.replySentId = -1;
      this.operatorReceivedId = -1;
   }

   public final void loadSounds(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      final io.intercom.android.sdk.m5.conversation.utils.SoundPlayer recoveredExpressionCapture_808_0 = this;
      this.soundPool.setOnLoadCompleteListener(((android.media.SoundPool.OnLoadCompleteListener) (recoveredExpressionArgument_808_0, recoveredExpressionArgument_808_1, recoveredExpressionArgument_808_2) -> io.intercom.android.sdk.m5.conversation.utils.SoundPlayer.loadSounds$lambda$0(recoveredExpressionCapture_808_0, (android.media.SoundPool) (recoveredExpressionArgument_808_0), recoveredExpressionArgument_808_1, recoveredExpressionArgument_808_2)));
      this.messageReceivedId = this.loadSound(var1, raw.intercom_received);
      this.replyFailedId = this.loadSound(var1, raw.intercom_failed);
      this.replySentId = this.loadSound(var1, raw.intercom_sent);
      this.operatorReceivedId = this.loadSound(var1, raw.intercom_operator);
   }

   public final void release() {
      this.soundPool.release();
   }

   private final int loadSound(Context var1, @RawRes int var2) {
      int var3;
      try {
         var3 = this.soundPool.load(var1, var2, 1);
      } catch (NotFoundException var5) {
         TWIG.e(var5, "Could not play sound");
         var3 = -1;
      }

      return var3;
   }

   public final void playMessageFailedSound() {
      this.playIfLoaded(this.replyFailedLoaded, this.replyFailedId);
   }

   public final void playMessageSentSound() {
      this.playIfLoaded(this.replySentLoaded, this.replySentId);
   }

   public final void playAdminMessageReceivedSound() {
      this.playIfLoaded(this.messageReceivedLoaded, this.messageReceivedId);
   }

   public final void playOperatorMessageReceivedSound() {
      this.playIfLoaded(this.operatorReceivedLoaded, this.operatorReceivedId);
   }

   private final void playIfLoaded(boolean var1, int var2) {
      if (var1 && var2 != -1) {
         this.soundPool.play(var2, 1.0F, 1.0F, 1, 0, 1.0F);
      }
   }

   private static final void loadSounds$lambda$0(SoundPlayer var0, SoundPool var1, int var2, int var3) {
      if (var3 == 0) {
         int var4 = var2;
         if (var4 == var0.messageReceivedId) {
            var0.messageReceivedLoaded = true;
         } else if (var4 == var0.replyFailedId) {
            var0.replyFailedLoaded = true;
         } else if (var4 == var0.replySentId) {
            var0.replySentLoaded = true;
         } else if (var4 == var0.operatorReceivedId) {
            var0.operatorReceivedLoaded = true;
         }
      }
   }
}
