package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption;",
         "value",
         "",
         "emojiUrl",
         "",
         "unicode",
         "<init>",
         "(ILjava/lang/String;Ljava/lang/String;)V",
         "getValue",
         "()I",
         "getEmojiUrl",
         "()Ljava/lang/String;",
         "getUnicode",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption
   extends SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption {
   @SerializedName("value")
   private final int value;
   @SerializedName("image_url")
   @NotNull
   private final String emojiUrl;
   @SerializedName("unicodeEmoticon")
   @NotNull
   private final String unicode;
   public static int $stable;

   static {}

   public SurveyData$Step$Question$NumericRatingQuestionModel$RatingOption$EmojiRatingOption(int var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var2, "emojiUrl");
      Intrinsics.checkNotNullParameter(var3, "unicode");
      super(null);
      this.value = var1;
      this.emojiUrl = var2;
      this.unicode = var3;
   }

   public final int getValue() {
      return this.value;
   }

   @NotNull
   public final String getEmojiUrl() {
      return this.emojiUrl;
   }

   @NotNull
   public final String getUnicode() {
      return this.unicode;
   }
}
