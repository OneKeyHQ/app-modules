package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\"\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e¢\u0006\u0004\b\u000f\u0010\u0010R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$UploadFileQuestionModel;",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "description",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "isRequired",
         "",
         "maxSelection",
         "",
         "supportedFileType",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Lio/intercom/android/sdk/ui/common/StringProvider;ZILjava/util/Set;)V",
         "getMaxSelection",
         "()I",
         "getSupportedFileType",
         "()Ljava/util/Set;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyData$Step$Question$UploadFileQuestionModel extends SurveyData$Step$Question$QuestionModel {
   private final int maxSelection;
   @NotNull
   private final Set supportedFileType;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$UploadFileQuestionModel(
      @NotNull String var1, @NotNull List var2, @NotNull StringProvider var3, boolean var4, int var5, @NotNull Set var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "description");
      Intrinsics.checkNotNullParameter(var6, "supportedFileType");
      super(var1, var2, var3, var4);
      this.maxSelection = var5;
      this.supportedFileType = var6;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$UploadFileQuestionModel(
      String var1, List var2, StringProvider var3, boolean var4, int var5, Set var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, (((var7 & 4) != 0) ? (StringProvider) (new ActualString("")) : var3), var4, var5, var6);
   }

   public final int getMaxSelection() {
      return this.maxSelection;
   }

   @NotNull
   public final Set getSupportedFileType() {
      return this.supportedFileType;
   }
}
