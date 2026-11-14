package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
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
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001:\u0002/0BM\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003\u0012\u0006\u0010\f\u001a\u00020\u0006\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0006HÆ\u0003J\t\u0010 \u001a\u00020\u0006HÆ\u0003J\u0010\u0010!\u001a\u00020\tHÆ\u0003¢\u0006\u0004\b\"\u0010\u0017J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0006HÆ\u0003J\u0010\u0010%\u001a\u0004\u0018\u00010\u000eHÆ\u0003¢\u0006\u0002\u0010\u001cJd\u0010&\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0002\u0010\f\u001a\u00020\u00062\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000eHÇ\u0001¢\u0006\u0004\b'\u0010(J\u0013\u0010)\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010,\u001a\u00020-H×\u0001J\t\u0010.\u001a\u00020\u0006H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0013\u0010\b\u001a\u00020\t¢\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0012R\u0011\u0010\f\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0015\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001b\u0010\u001c¨\u00061",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "",
         "adminAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "statusTitle",
         "",
         "statusSubtitle",
         "progressColor",
         "Landroidx/compose/ui/graphics/Color;",
         "progressSections",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ProgressSection;",
         "statusLabel",
         "timestamp",
         "",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;JLjava/util/List;Ljava/lang/String;Ljava/lang/Long;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getAdminAvatars",
         "()Ljava/util/List;",
         "getStatusTitle",
         "()Ljava/lang/String;",
         "getStatusSubtitle",
         "getProgressColor-0d7_KjU",
         "()J",
         "J",
         "getProgressSections",
         "getStatusLabel",
         "getTimestamp",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component4-0d7_KjU",
         "component5",
         "component6",
         "component7",
         "copy",
         "copy-ww6aTOc",
         "(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;JLjava/util/List;Ljava/lang/String;Ljava/lang/Long;)Lio/intercom/android/sdk/tickets/TicketTimelineCardState;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "ProgressSection",
         "ActualStringOrRes",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketTimelineCardState {
   @NotNull
   private final List adminAvatars;
   @NotNull
   private final String statusTitle;
   @NotNull
   private final String statusSubtitle;
   private final long progressColor;
   @NotNull
   private final List progressSections;
   @NotNull
   private final String statusLabel;
   @Nullable
   private final Long timestamp;
   public static final int $stable;

   static { $stable = 8; }

   private TicketTimelineCardState(List var1, String var2, String var3, long var4, List var6, String var7, Long var8) {
      super();
      Intrinsics.checkNotNullParameter(var1, "adminAvatars");
      Intrinsics.checkNotNullParameter(var2, "statusTitle");
      Intrinsics.checkNotNullParameter(var3, "statusSubtitle");
      Intrinsics.checkNotNullParameter(var6, "progressSections");
      Intrinsics.checkNotNullParameter(var7, "statusLabel");
      this.adminAvatars = var1;
      this.statusTitle = var2;
      this.statusSubtitle = var3;
      this.progressColor = var4;
      this.progressSections = var6;
      this.statusLabel = var7;
      this.timestamp = var8;
   }

   @NotNull
   public final List getAdminAvatars() {
      return this.adminAvatars;
   }

   @NotNull
   public final String getStatusTitle() {
      return this.statusTitle;
   }

   @NotNull
   public final String getStatusSubtitle() {
      return this.statusSubtitle;
   }

   public final long recovered$4e526da7fcc96ce2() {
      return this.progressColor;
   }

   @NotNull
   public final List getProgressSections() {
      return this.progressSections;
   }

   @NotNull
   public final String getStatusLabel() {
      return this.statusLabel;
   }

   @Nullable
   public final Long getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public final List component1() {
      return this.adminAvatars;
   }

   @NotNull
   public final String component2() {
      return this.statusTitle;
   }

   @NotNull
   public final String component3() {
      return this.statusSubtitle;
   }

   public final long recovered$0e8bb1255e180941() {
      return this.progressColor;
   }

   @NotNull
   public final List component5() {
      return this.progressSections;
   }

   @NotNull
   public final String component6() {
      return this.statusLabel;
   }

   @Nullable
   public final Long component7() {
      return this.timestamp;
   }

   @NotNull
   public final TicketTimelineCardState recovered$c859693f53661e67(
      @NotNull List var1, @NotNull String var2, @NotNull String var3, long var4, @NotNull List var6, @NotNull String var7, @Nullable Long var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "adminAvatars");
      Intrinsics.checkNotNullParameter(var2, "statusTitle");
      Intrinsics.checkNotNullParameter(var3, "statusSubtitle");
      Intrinsics.checkNotNullParameter(var6, "progressSections");
      Intrinsics.checkNotNullParameter(var7, "statusLabel");
      return new TicketTimelineCardState(var1, var2, var3, var4, var6, var7, var8, null);
   }

   // $VF: synthetic method
   public static TicketTimelineCardState recovered$a30edbfd18d4c1f7(
      TicketTimelineCardState var0, List var1, String var2, String var3, long var4, List var6, String var7, Long var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.adminAvatars;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.statusTitle;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.statusSubtitle;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.progressColor;
      }

      if ((var9 & 16) != 0) {
         var6 = var0.progressSections;
      }

      if ((var9 & 32) != 0) {
         var7 = var0.statusLabel;
      }

      if ((var9 & 64) != 0) {
         var8 = var0.timestamp;
      }

      return var0.recovered$c859693f53661e67(var1, var2, var3, var4, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketTimelineCardState(adminAvatars="
         + this.adminAvatars
         + ", statusTitle="
         + this.statusTitle
         + ", statusSubtitle="
         + this.statusSubtitle
         + ", progressColor="
         + Color.recovered$cfef63bb1d111f9f(this.progressColor)
         + ", progressSections="
         + this.progressSections
         + ", statusLabel="
         + this.statusLabel
         + ", timestamp="
         + this.timestamp
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.adminAvatars.hashCode();
      var1 = var1 * 31 + this.statusTitle.hashCode();
      var1 = var1 * 31 + this.statusSubtitle.hashCode();
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.progressColor);
      var1 = var1 * 31 + this.progressSections.hashCode();
      var1 = var1 * 31 + this.statusLabel.hashCode();
      return var1 * 31 + (this.timestamp == null ? 0 : this.timestamp.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketTimelineCardState)) {
         return false;
      } else {
         TicketTimelineCardState var2 = (TicketTimelineCardState)var1;
         if (!Intrinsics.areEqual(this.adminAvatars, var2.adminAvatars)) {
            return false;
         } else if (!Intrinsics.areEqual(this.statusTitle, var2.statusTitle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.statusSubtitle, var2.statusSubtitle)) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.progressColor, var2.progressColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.progressSections, var2.progressSections)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.statusLabel, var2.statusLabel) ? false : Intrinsics.areEqual(this.timestamp, var2.timestamp);
         }
      }
   }

   // $VF: synthetic method
   public TicketTimelineCardState(List var1, String var2, String var3, long var4, List var6, String var7, Long var8, DefaultConstructorMarker var9) {
      this(var1, var2, var3, var4, var6, var7, var8);
   }
}
