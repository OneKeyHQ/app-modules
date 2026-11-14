package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0007HÆ\u0003J1\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$FilesAttribute$File;",
         "",
         "id",
         "",
         "name",
         "url",
         "fileType",
         "Lio/intercom/android/sdk/models/FileType;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/FileType;)V",
         "getId",
         "()Ljava/lang/String;",
         "getName",
         "getUrl",
         "getFileType",
         "()Lio/intercom/android/sdk/models/FileType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Ticket$TicketAttribute$FilesAttribute$File {
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("url")
   @NotNull
   private final String url;
   @SerializedName("media_type")
   @NotNull
   private final FileType fileType;
   public static int $stable;

   static {}

   public Ticket$TicketAttribute$FilesAttribute$File(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull FileType var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "url");
      Intrinsics.checkNotNullParameter(var4, "fileType");
      this.id = var1;
      this.name = var2;
      this.url = var3;
      this.fileType = var4;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @NotNull
   public final FileType getFileType() {
      return this.fileType;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @NotNull
   public final String component3() {
      return this.url;
   }

   @NotNull
   public final FileType component4() {
      return this.fileType;
   }

   @NotNull
   public final Ticket$TicketAttribute$FilesAttribute$File copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull FileType var4) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "url");
      Intrinsics.checkNotNullParameter(var4, "fileType");
      return new Ticket$TicketAttribute$FilesAttribute$File(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Ticket$TicketAttribute$FilesAttribute$File copy$default(
      Ticket$TicketAttribute$FilesAttribute$File var0, String var1, String var2, String var3, FileType var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.name;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.url;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.fileType;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "File(id=" + this.id + ", name=" + this.name + ", url=" + this.url + ", fileType=" + this.fileType + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.name.hashCode();
      var1 = var1 * 31 + this.url.hashCode();
      return var1 * 31 + this.fileType.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Ticket$TicketAttribute$FilesAttribute$File)) {
         return false;
      } else {
         Ticket$TicketAttribute$FilesAttribute$File var2 = (Ticket$TicketAttribute$FilesAttribute$File)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.url, var2.url) ? false : this.fileType == var2.fileType;
         }
      }
   }
}
