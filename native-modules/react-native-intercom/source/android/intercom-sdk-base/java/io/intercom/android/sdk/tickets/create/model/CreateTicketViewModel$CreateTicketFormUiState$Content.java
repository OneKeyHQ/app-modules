package io.intercom.android.sdk.tickets.create.model;

import androidx.compose.runtime.internal.StabilityInferred;
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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J\t\u0010\u0016\u001a\u00020\bHÆ\u0003J7\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0018\u001a\u00020\b2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState$Content;",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$CreateTicketFormUiState;",
         "title",
         "",
         "questions",
         "",
         "Lio/intercom/android/sdk/survey/QuestionState;",
         "showCreatingTicketProgress",
         "",
         "enableCta",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;ZZ)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getQuestions",
         "()Ljava/util/List;",
         "getShowCreatingTicketProgress",
         "()Z",
         "getEnableCta",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class CreateTicketViewModel$CreateTicketFormUiState$Content extends CreateTicketViewModel$CreateTicketFormUiState {
   @NotNull
   private final String title;
   @NotNull
   private final List questions;
   private final boolean showCreatingTicketProgress;
   private final boolean enableCta;
   public static final int $stable;

   static { $stable = 8; }

   public CreateTicketViewModel$CreateTicketFormUiState$Content(@NotNull String var1, @NotNull List var2, boolean var3, boolean var4) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "questions");
      super(null);
      this.title = var1;
      this.questions = var2;
      this.showCreatingTicketProgress = var3;
      this.enableCta = var4;
   }

   // $VF: synthetic method
   public CreateTicketViewModel$CreateTicketFormUiState$Content(String var1, List var2, boolean var3, boolean var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (boolean) (false) : var3), (((var5 & 8) != 0) ? (boolean) (true) : var4));
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final List getQuestions() {
      return this.questions;
   }

   public final boolean getShowCreatingTicketProgress() {
      return this.showCreatingTicketProgress;
   }

   public final boolean getEnableCta() {
      return this.enableCta;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final List component2() {
      return this.questions;
   }

   public final boolean component3() {
      return this.showCreatingTicketProgress;
   }

   public final boolean component4() {
      return this.enableCta;
   }

   @NotNull
   public final CreateTicketViewModel$CreateTicketFormUiState$Content copy(@NotNull String var1, @NotNull List var2, boolean var3, boolean var4) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "questions");
      return new CreateTicketViewModel$CreateTicketFormUiState$Content(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static CreateTicketViewModel$CreateTicketFormUiState$Content copy$default(
      CreateTicketViewModel$CreateTicketFormUiState$Content var0, String var1, List var2, boolean var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.questions;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.showCreatingTicketProgress;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.enableCta;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(title="
         + this.title
         + ", questions="
         + this.questions
         + ", showCreatingTicketProgress="
         + this.showCreatingTicketProgress
         + ", enableCta="
         + this.enableCta
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + this.questions.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.showCreatingTicketProgress);
      return var1 * 31 + Boolean.hashCode(this.enableCta);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CreateTicketViewModel$CreateTicketFormUiState$Content)) {
         return false;
      } else {
         CreateTicketViewModel$CreateTicketFormUiState$Content var2 = (CreateTicketViewModel$CreateTicketFormUiState$Content)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.questions, var2.questions)) {
            return false;
         } else {
            return this.showCreatingTicketProgress != var2.showCreatingTicketProgress ? false : this.enableCta == var2.enableCta;
         }
      }
   }
}
