package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J5\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$SurveyActions;",
         "",
         "actionTitle",
         "",
         "id",
         "",
         "webUrl",
         "androidUri",
         "<init>",
         "(Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;)V",
         "getActionTitle",
         "()Ljava/lang/String;",
         "getId",
         "()J",
         "getWebUrl",
         "getAndroidUri",
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
public final class SurveyData$SurveyActions {
   @SerializedName("action_title")
   @NotNull
   private final String actionTitle;
   @SerializedName("id")
   private final long id;
   @SerializedName("web_url")
   @Nullable
   private final String webUrl;
   @SerializedName("android_uri")
   @Nullable
   private final String androidUri;
   public static int $stable;

   static {}

   public SurveyData$SurveyActions(@NotNull String var1, long var2, @Nullable String var4, @Nullable String var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "actionTitle");
      this.actionTitle = var1;
      this.id = var2;
      this.webUrl = var4;
      this.androidUri = var5;
   }

   // $VF: synthetic method
   public SurveyData$SurveyActions(String var1, long var2, String var4, String var5, int var6, DefaultConstructorMarker var7) {
      this(var1, var2, (((var6 & 4) != 0) ? (String) (null) : var4), (((var6 & 8) != 0) ? (String) (null) : var5));
   }

   @NotNull
   public final String getActionTitle() {
      return this.actionTitle;
   }

   public final long getId() {
      return this.id;
   }

   @Nullable
   public final String getWebUrl() {
      return this.webUrl;
   }

   @Nullable
   public final String getAndroidUri() {
      return this.androidUri;
   }

   @NotNull
   public final String component1() {
      return this.actionTitle;
   }

   public final long component2() {
      return this.id;
   }

   @Nullable
   public final String component3() {
      return this.webUrl;
   }

   @Nullable
   public final String component4() {
      return this.androidUri;
   }

   @NotNull
   public final SurveyData$SurveyActions copy(@NotNull String var1, long var2, @Nullable String var4, @Nullable String var5) {
      Intrinsics.checkNotNullParameter(var1, "actionTitle");
      return new SurveyData$SurveyActions(var1, var2, var4, var5);
   }

   // $VF: synthetic method
   public static SurveyData$SurveyActions copy$default(SurveyData$SurveyActions var0, String var1, long var2, String var4, String var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.actionTitle;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.id;
      }

      if ((var6 & 4) != 0) {
         var4 = var0.webUrl;
      }

      if ((var6 & 8) != 0) {
         var5 = var0.androidUri;
      }

      return var0.copy(var1, var2, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "SurveyActions(actionTitle=" + this.actionTitle + ", id=" + this.id + ", webUrl=" + this.webUrl + ", androidUri=" + this.androidUri + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.actionTitle.hashCode();
      var1 = var1 * 31 + Long.hashCode(this.id);
      var1 = var1 * 31 + (this.webUrl == null ? 0 : this.webUrl.hashCode());
      return var1 * 31 + (this.androidUri == null ? 0 : this.androidUri.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveyData$SurveyActions)) {
         return false;
      } else {
         SurveyData$SurveyActions var2 = (SurveyData$SurveyActions)var1;
         if (!Intrinsics.areEqual(this.actionTitle, var2.actionTitle)) {
            return false;
         } else if (this.id != var2.id) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.webUrl, var2.webUrl) ? false : Intrinsics.areEqual(this.androidUri, var2.androidUri);
         }
      }
   }
}
