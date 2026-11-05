package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$FooterNoticeRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "footerNoticeState",
         "Lio/intercom/android/sdk/m5/conversation/states/FooterNoticeState;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/FooterNoticeState;)V",
         "getFooterNoticeState",
         "()Lio/intercom/android/sdk/m5/conversation/states/FooterNoticeState;",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ContentRow$FooterNoticeRow implements ContentRow {
   @NotNull
   private final FooterNoticeState footerNoticeState;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$FooterNoticeRow(@NotNull FooterNoticeState var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "footerNoticeState");
      this.footerNoticeState = var1;
   }

   @NotNull
   public final FooterNoticeState getFooterNoticeState() {
      return this.footerNoticeState;
   }

   @NotNull
   @Override
   public String getKey() {
      return "footer_notice_row_" + this.footerNoticeState.hashCode();
   }

   @NotNull
   public final FooterNoticeState component1() {
      return this.footerNoticeState;
   }

   @NotNull
   public final ContentRow$FooterNoticeRow copy(@NotNull FooterNoticeState var1) {
      Intrinsics.checkNotNullParameter(var1, "footerNoticeState");
      return new ContentRow$FooterNoticeRow(var1);
   }

   // $VF: synthetic method
   public static ContentRow$FooterNoticeRow copy$default(ContentRow$FooterNoticeRow var0, FooterNoticeState var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.footerNoticeState;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "FooterNoticeRow(footerNoticeState=" + this.footerNoticeState + ')';
   }

   @Override
   public int hashCode() {
      return this.footerNoticeState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$FooterNoticeRow)) {
         return false;
      }

      ContentRow$FooterNoticeRow var2 = (ContentRow$FooterNoticeRow)var1;
      return Intrinsics.areEqual(this.footerNoticeState, var2.footerNoticeState);
   }
}
