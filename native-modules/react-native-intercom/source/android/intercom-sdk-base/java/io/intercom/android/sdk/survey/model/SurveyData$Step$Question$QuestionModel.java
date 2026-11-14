package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\b\u0017\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0013¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionModel;",
         "",
         "id",
         "",
         "title",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "description",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "isRequired",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Lio/intercom/android/sdk/ui/common/StringProvider;Z)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
         "()Ljava/util/List;",
         "getDescription",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "()Z",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public class SurveyData$Step$Question$QuestionModel {
   @NotNull
   private final String id;
   @NotNull
   private final List title;
   @NotNull
   private final StringProvider description;
   private final boolean isRequired;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyData$Step$Question$QuestionModel(@NotNull String var1, @NotNull List var2, @NotNull StringProvider var3, boolean var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "description");
      this.id = var1;
      this.title = var2;
      this.description = var3;
      this.isRequired = var4;
   }

   // $VF: synthetic method
   public SurveyData$Step$Question$QuestionModel(String var1, List var2, StringProvider var3, boolean var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (StringProvider) (new ActualString("")) : var3), var4);
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final List getTitle() {
      return this.title;
   }

   @NotNull
   public final StringProvider getDescription() {
      return this.description;
   }

   public final boolean isRequired() {
      return this.isRequired;
   }
}
