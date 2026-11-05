package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.Set;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u001a\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u0000 )2\u00020\u0001:\u0001)BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\n0\tHÆ\u0003J\t\u0010\"\u001a\u00020\fHÆ\u0003JU\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u000b\u001a\u00020\fHÇ\u0001J\u0013\u0010$\u001a\u00020\u00032\b\u0010%\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010&\u001a\u00020'H×\u0001J\t\u0010(\u001a\u00020\nH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0010R\u001c\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\u000b\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0019\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001b¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/models/AttachmentSettings;",
         "",
         "cameraEnabled",
         "",
         "mediaEnabled",
         "filesEnabled",
         "gifsEnabled",
         "voiceNotesEnabled",
         "trustedFileExtensions",
         "",
         "",
         "uploadSizeLimit",
         "",
         "<init>",
         "(ZZZZZLjava/util/Set;J)V",
         "getCameraEnabled",
         "()Z",
         "getMediaEnabled",
         "getFilesEnabled",
         "getGifsEnabled",
         "getVoiceNotesEnabled",
         "getTrustedFileExtensions",
         "()Ljava/util/Set;",
         "getUploadSizeLimit",
         "()J",
         "uploadSizeLimitMB",
         "getUploadSizeLimitMB",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AttachmentSettings {
   @NotNull
   public static final AttachmentSettings$Companion Companion = new AttachmentSettings$Companion(null);
   @SerializedName("conversation_camera_enabled")
   private final boolean cameraEnabled;
   @SerializedName("conversation_media_enabled")
   private final boolean mediaEnabled;
   @SerializedName("conversation_files_enabled")
   private final boolean filesEnabled;
   @SerializedName("conversation_gifs_enabled")
   private final boolean gifsEnabled;
   @SerializedName("conversation_voice_notes_enabled")
   private final boolean voiceNotesEnabled;
   @SerializedName("file_upload_extension_trusted_list")
   @NotNull
   private final Set trustedFileExtensions;
   @SerializedName("upload_size_limit")
   private final long uploadSizeLimit;
   public static final int $stable;

   static { $stable = 8; }
   private static final long DEFAULT_UPLOAD_SIZE_LIMIT = 104857600L;
   @NotNull
   private static final AttachmentSettings DEFAULT = new AttachmentSettings(true, true, true, true, true, SetsKt.emptySet(), 104857600L);

   public AttachmentSettings(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, @NotNull Set var6, long var7) {
      super();
      Intrinsics.checkNotNullParameter(var6, "trustedFileExtensions");
      this.cameraEnabled = var1;
      this.mediaEnabled = var2;
      this.filesEnabled = var3;
      this.gifsEnabled = var4;
      this.voiceNotesEnabled = var5;
      this.trustedFileExtensions = var6;
      this.uploadSizeLimit = var7;
   }

   public final boolean getCameraEnabled() {
      return this.cameraEnabled;
   }

   public final boolean getMediaEnabled() {
      return this.mediaEnabled;
   }

   public final boolean getFilesEnabled() {
      return this.filesEnabled;
   }

   public final boolean getGifsEnabled() {
      return this.gifsEnabled;
   }

   public final boolean getVoiceNotesEnabled() {
      return this.voiceNotesEnabled;
   }

   @NotNull
   public final Set getTrustedFileExtensions() {
      return this.trustedFileExtensions;
   }

   public final long getUploadSizeLimit() {
      return this.uploadSizeLimit;
   }

   @NotNull
   public final String getUploadSizeLimitMB() {
      return String.valueOf(this.uploadSizeLimit / 1048576);
   }

   public final boolean component1() {
      return this.cameraEnabled;
   }

   public final boolean component2() {
      return this.mediaEnabled;
   }

   public final boolean component3() {
      return this.filesEnabled;
   }

   public final boolean component4() {
      return this.gifsEnabled;
   }

   public final boolean component5() {
      return this.voiceNotesEnabled;
   }

   @NotNull
   public final Set component6() {
      return this.trustedFileExtensions;
   }

   public final long component7() {
      return this.uploadSizeLimit;
   }

   @NotNull
   public final AttachmentSettings copy(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, @NotNull Set var6, long var7) {
      Intrinsics.checkNotNullParameter(var6, "trustedFileExtensions");
      return new AttachmentSettings(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static AttachmentSettings copy$default(
      AttachmentSettings var0, boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, Set var6, long var7, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.cameraEnabled;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.mediaEnabled;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.filesEnabled;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.gifsEnabled;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.voiceNotesEnabled;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.trustedFileExtensions;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.uploadSizeLimit;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "AttachmentSettings(cameraEnabled="
         + this.cameraEnabled
         + ", mediaEnabled="
         + this.mediaEnabled
         + ", filesEnabled="
         + this.filesEnabled
         + ", gifsEnabled="
         + this.gifsEnabled
         + ", voiceNotesEnabled="
         + this.voiceNotesEnabled
         + ", trustedFileExtensions="
         + this.trustedFileExtensions
         + ", uploadSizeLimit="
         + this.uploadSizeLimit
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.cameraEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.mediaEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.filesEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.gifsEnabled);
      var1 = var1 * 31 + Boolean.hashCode(this.voiceNotesEnabled);
      var1 = var1 * 31 + this.trustedFileExtensions.hashCode();
      return var1 * 31 + Long.hashCode(this.uploadSizeLimit);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AttachmentSettings)) {
         return false;
      } else {
         AttachmentSettings var2 = (AttachmentSettings)var1;
         if (this.cameraEnabled != var2.cameraEnabled) {
            return false;
         } else if (this.mediaEnabled != var2.mediaEnabled) {
            return false;
         } else if (this.filesEnabled != var2.filesEnabled) {
            return false;
         } else if (this.gifsEnabled != var2.gifsEnabled) {
            return false;
         } else if (this.voiceNotesEnabled != var2.voiceNotesEnabled) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.trustedFileExtensions, var2.trustedFileExtensions) ? false : this.uploadSizeLimit == var2.uploadSizeLimit;
         }
      }
   }

   // $VF: synthetic method
   public static final AttachmentSettings access$getDEFAULT$cp() {
      return DEFAULT;
   }
}
