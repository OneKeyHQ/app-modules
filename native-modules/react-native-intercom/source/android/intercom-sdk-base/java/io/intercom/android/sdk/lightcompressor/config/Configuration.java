package io.intercom.android.sdk.lightcompressor.config;

import io.intercom.android.sdk.lightcompressor.VideoQuality;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b+\b\u0086\b\u0018\u00002\u00020\u0001Ba\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0005\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b\u0012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e¢\u0006\u0004\b\u0010\u0010\u0011J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\t\u0010-\u001a\u00020\u0005HÆ\u0003J\u0010\u0010.\u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u001aJ\t\u0010/\u001a\u00020\u0005HÆ\u0003J\t\u00100\u001a\u00020\u0005HÆ\u0003J\u0010\u00101\u001a\u0004\u0018\u00010\u000bHÆ\u0003¢\u0006\u0002\u0010\"J\u0010\u00102\u001a\u0004\u0018\u00010\u000bHÆ\u0003¢\u0006\u0002\u0010\"J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eHÆ\u0003Jj\u00104\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\u00052\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eHÆ\u0001¢\u0006\u0002\u00105J\u0013\u00106\u001a\u00020\u00052\b\u00107\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u00108\u001a\u00020\u0007HÖ\u0001J\t\u00109\u001a\u00020\u000fHÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001e\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u001d\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\b\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u0016\"\u0004\b\u001f\u0010\u0018R\u0011\u0010\t\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016R\u001e\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0086\u000e¢\u0006\u0010\n\u0002\u0010%\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$R\u001e\u0010\f\u001a\u0004\u0018\u00010\u000bX\u0086\u000e¢\u0006\u0010\n\u0002\u0010%\u001a\u0004\b&\u0010\"\"\u0004\b'\u0010$R \u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+¨\u0006:",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/config/Configuration;",
         "",
         "quality",
         "Lio/intercom/android/sdk/lightcompressor/VideoQuality;",
         "isMinBitrateCheckEnabled",
         "",
         "videoBitrateInMbps",
         "",
         "disableAudio",
         "keepOriginalResolution",
         "videoHeight",
         "",
         "videoWidth",
         "videoNames",
         "",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/lightcompressor/VideoQuality;ZLjava/lang/Integer;ZZLjava/lang/Double;Ljava/lang/Double;Ljava/util/List;)V",
         "getQuality",
         "()Lio/intercom/android/sdk/lightcompressor/VideoQuality;",
         "setQuality",
         "(Lio/intercom/android/sdk/lightcompressor/VideoQuality;)V",
         "()Z",
         "setMinBitrateCheckEnabled",
         "(Z)V",
         "getVideoBitrateInMbps",
         "()Ljava/lang/Integer;",
         "setVideoBitrateInMbps",
         "(Ljava/lang/Integer;)V",
         "Ljava/lang/Integer;",
         "getDisableAudio",
         "setDisableAudio",
         "getKeepOriginalResolution",
         "getVideoHeight",
         "()Ljava/lang/Double;",
         "setVideoHeight",
         "(Ljava/lang/Double;)V",
         "Ljava/lang/Double;",
         "getVideoWidth",
         "setVideoWidth",
         "getVideoNames",
         "()Ljava/util/List;",
         "setVideoNames",
         "(Ljava/util/List;)V",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "(Lio/intercom/android/sdk/lightcompressor/VideoQuality;ZLjava/lang/Integer;ZZLjava/lang/Double;Ljava/lang/Double;Ljava/util/List;)Lio/intercom/android/sdk/lightcompressor/config/Configuration;",
         "equals",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class Configuration {
   @NotNull
   private VideoQuality quality;
   private boolean isMinBitrateCheckEnabled;
   @Nullable
   private Integer videoBitrateInMbps;
   private boolean disableAudio;
   private final boolean keepOriginalResolution;
   @Nullable
   private Double videoHeight;
   @Nullable
   private Double videoWidth;
   @NotNull
   private List videoNames;

   public Configuration(
      @NotNull VideoQuality var1,
      boolean var2,
      @Nullable Integer var3,
      boolean var4,
      boolean var5,
      @Nullable Double var6,
      @Nullable Double var7,
      @NotNull List var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "quality");
      Intrinsics.checkNotNullParameter(var8, "videoNames");
      this.quality = var1;
      this.isMinBitrateCheckEnabled = var2;
      this.videoBitrateInMbps = var3;
      this.disableAudio = var4;
      this.keepOriginalResolution = var5;
      this.videoHeight = var6;
      this.videoWidth = var7;
      this.videoNames = var8;
   }

   // $VF: synthetic method
   public Configuration(
      VideoQuality var1, boolean var2, Integer var3, boolean var4, boolean var5, Double var6, Double var7, List var8, int var9, DefaultConstructorMarker var10
   ) {
      this((((var9 & 1) != 0) ? (VideoQuality) (VideoQuality.MEDIUM) : var1), (((var9 & 2) != 0) ? (boolean) (true) : var2), (((var9 & 4) != 0) ? (Integer) (null) : var3), (((var9 & 8) != 0) ? (boolean) (false) : var4), (((var9 & 16) != 0) ? (boolean) (false) : var5), (((var9 & 32) != 0) ? (Double) (null) : var6), (((var9 & 64) != 0) ? (Double) (null) : var7), var8);
   }

   @NotNull
   public final VideoQuality getQuality() {
      return this.quality;
   }

   public final void setQuality(@NotNull VideoQuality var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.quality = var1;
   }

   public final boolean isMinBitrateCheckEnabled() {
      return this.isMinBitrateCheckEnabled;
   }

   public final void setMinBitrateCheckEnabled(boolean var1) {
      this.isMinBitrateCheckEnabled = var1;
   }

   @Nullable
   public final Integer getVideoBitrateInMbps() {
      return this.videoBitrateInMbps;
   }

   public final void setVideoBitrateInMbps(@Nullable Integer var1) {
      this.videoBitrateInMbps = var1;
   }

   public final boolean getDisableAudio() {
      return this.disableAudio;
   }

   public final void setDisableAudio(boolean var1) {
      this.disableAudio = var1;
   }

   public final boolean getKeepOriginalResolution() {
      return this.keepOriginalResolution;
   }

   @Nullable
   public final Double getVideoHeight() {
      return this.videoHeight;
   }

   public final void setVideoHeight(@Nullable Double var1) {
      this.videoHeight = var1;
   }

   @Nullable
   public final Double getVideoWidth() {
      return this.videoWidth;
   }

   public final void setVideoWidth(@Nullable Double var1) {
      this.videoWidth = var1;
   }

   @NotNull
   public final List getVideoNames() {
      return this.videoNames;
   }

   public final void setVideoNames(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.videoNames = var1;
   }

   @NotNull
   public final VideoQuality component1() {
      return this.quality;
   }

   public final boolean component2() {
      return this.isMinBitrateCheckEnabled;
   }

   @Nullable
   public final Integer component3() {
      return this.videoBitrateInMbps;
   }

   public final boolean component4() {
      return this.disableAudio;
   }

   public final boolean component5() {
      return this.keepOriginalResolution;
   }

   @Nullable
   public final Double component6() {
      return this.videoHeight;
   }

   @Nullable
   public final Double component7() {
      return this.videoWidth;
   }

   @NotNull
   public final List component8() {
      return this.videoNames;
   }

   @NotNull
   public final Configuration copy(
      @NotNull VideoQuality var1,
      boolean var2,
      @Nullable Integer var3,
      boolean var4,
      boolean var5,
      @Nullable Double var6,
      @Nullable Double var7,
      @NotNull List var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "quality");
      Intrinsics.checkNotNullParameter(var8, "videoNames");
      return new Configuration(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static Configuration copy$default(
      Configuration var0,
      VideoQuality var1,
      boolean var2,
      Integer var3,
      boolean var4,
      boolean var5,
      Double var6,
      Double var7,
      List var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.quality;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.isMinBitrateCheckEnabled;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.videoBitrateInMbps;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.disableAudio;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.keepOriginalResolution;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.videoHeight;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.videoWidth;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.videoNames;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "Configuration(quality="
         + this.quality
         + ", isMinBitrateCheckEnabled="
         + this.isMinBitrateCheckEnabled
         + ", videoBitrateInMbps="
         + this.videoBitrateInMbps
         + ", disableAudio="
         + this.disableAudio
         + ", keepOriginalResolution="
         + this.keepOriginalResolution
         + ", videoHeight="
         + this.videoHeight
         + ", videoWidth="
         + this.videoWidth
         + ", videoNames="
         + this.videoNames
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.quality.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isMinBitrateCheckEnabled);
      var1 = var1 * 31 + (this.videoBitrateInMbps == null ? 0 : this.videoBitrateInMbps.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.disableAudio);
      var1 = var1 * 31 + Boolean.hashCode(this.keepOriginalResolution);
      var1 = var1 * 31 + (this.videoHeight == null ? 0 : this.videoHeight.hashCode());
      var1 = var1 * 31 + (this.videoWidth == null ? 0 : this.videoWidth.hashCode());
      return var1 * 31 + this.videoNames.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Configuration)) {
         return false;
      } else {
         Configuration var2 = (Configuration)var1;
         if (this.quality != var2.quality) {
            return false;
         } else if (this.isMinBitrateCheckEnabled != var2.isMinBitrateCheckEnabled) {
            return false;
         } else if (!Intrinsics.areEqual(this.videoBitrateInMbps, var2.videoBitrateInMbps)) {
            return false;
         } else if (this.disableAudio != var2.disableAudio) {
            return false;
         } else if (this.keepOriginalResolution != var2.keepOriginalResolution) {
            return false;
         } else if (!Intrinsics.areEqual((Object)this.videoHeight, (Object)var2.videoHeight)) {
            return false;
         } else {
            return !Intrinsics.areEqual((Object)this.videoWidth, (Object)var2.videoWidth) ? false : Intrinsics.areEqual(this.videoNames, var2.videoNames);
         }
      }
   }
}
