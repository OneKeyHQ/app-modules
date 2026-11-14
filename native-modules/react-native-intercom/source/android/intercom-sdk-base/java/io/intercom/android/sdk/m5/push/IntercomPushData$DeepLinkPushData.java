package io.intercom.android.sdk.m5.push;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J;\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001dH×\u0003J\t\u0010\u001e\u001a\u00020\u0011H×\u0001J\t\u0010\u001f\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0014\u0010\u0010\u001a\u00020\u00118VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0012\u0010\u0013¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$DeepLinkPushData;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData;",
         "contentTitle",
         "",
         "contentText",
         "uri",
         "contentImageUrl",
         "instanceId",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getContentTitle",
         "()Ljava/lang/String;",
         "getContentText",
         "getUri",
         "getContentImageUrl",
         "getInstanceId",
         "notificationId",
         "",
         "getNotificationId",
         "()I",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomPushData$DeepLinkPushData implements IntercomPushData {
   @NotNull
   private final String contentTitle;
   @NotNull
   private final String contentText;
   @NotNull
   private final String uri;
   @NotNull
   private final String contentImageUrl;
   @NotNull
   private final String instanceId;
   public static int $stable;

   static {}

   public IntercomPushData$DeepLinkPushData(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "contentTitle");
      Intrinsics.checkNotNullParameter(var2, "contentText");
      Intrinsics.checkNotNullParameter(var3, "uri");
      Intrinsics.checkNotNullParameter(var4, "contentImageUrl");
      Intrinsics.checkNotNullParameter(var5, "instanceId");
      this.contentTitle = var1;
      this.contentText = var2;
      this.uri = var3;
      this.contentImageUrl = var4;
      this.instanceId = var5;
   }

   @NotNull
   public final String getContentTitle() {
      return this.contentTitle;
   }

   @NotNull
   public final String getContentText() {
      return this.contentText;
   }

   @NotNull
   public final String getUri() {
      return this.uri;
   }

   @NotNull
   public final String getContentImageUrl() {
      return this.contentImageUrl;
   }

   @NotNull
   public final String getInstanceId() {
      return this.instanceId;
   }

   @Override
   public int getNotificationId() {
      return this.instanceId.hashCode();
   }

   @NotNull
   public final String component1() {
      return this.contentTitle;
   }

   @NotNull
   public final String component2() {
      return this.contentText;
   }

   @NotNull
   public final String component3() {
      return this.uri;
   }

   @NotNull
   public final String component4() {
      return this.contentImageUrl;
   }

   @NotNull
   public final String component5() {
      return this.instanceId;
   }

   @NotNull
   public final IntercomPushData$DeepLinkPushData copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "contentTitle");
      Intrinsics.checkNotNullParameter(var2, "contentText");
      Intrinsics.checkNotNullParameter(var3, "uri");
      Intrinsics.checkNotNullParameter(var4, "contentImageUrl");
      Intrinsics.checkNotNullParameter(var5, "instanceId");
      return new IntercomPushData$DeepLinkPushData(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static IntercomPushData$DeepLinkPushData copy$default(
      IntercomPushData$DeepLinkPushData var0, String var1, String var2, String var3, String var4, String var5, int var6, Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.contentTitle;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.contentText;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.uri;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.contentImageUrl;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.instanceId;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "DeepLinkPushData(contentTitle="
         + this.contentTitle
         + ", contentText="
         + this.contentText
         + ", uri="
         + this.uri
         + ", contentImageUrl="
         + this.contentImageUrl
         + ", instanceId="
         + this.instanceId
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.contentTitle.hashCode();
      var1 = var1 * 31 + this.contentText.hashCode();
      var1 = var1 * 31 + this.uri.hashCode();
      var1 = var1 * 31 + this.contentImageUrl.hashCode();
      return var1 * 31 + this.instanceId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomPushData$DeepLinkPushData)) {
         return false;
      } else {
         IntercomPushData$DeepLinkPushData var2 = (IntercomPushData$DeepLinkPushData)var1;
         if (!Intrinsics.areEqual(this.contentTitle, var2.contentTitle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.contentText, var2.contentText)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uri, var2.uri)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.contentImageUrl, var2.contentImageUrl) ? false : Intrinsics.areEqual(this.instanceId, var2.instanceId);
         }
      }
   }
}
