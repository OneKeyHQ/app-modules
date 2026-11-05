package io.intercom.android.sdk.survey.ui.models;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.utilities.TimeFormatter;
import java.text.DecimalFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\b\u0010\u000e\u001a\u00020\u0005H\u0016J\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0010J\u0006\u0010\u0012\u001a\u00020\u0010J\u0010\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0005H\u0002J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J'\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH×\u0003J\t\u0010\u001d\u001a\u00020\u0005H×\u0001J\t\u0010\u001e\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/models/Answer$DateTimeAnswer;",
         "Lio/intercom/android/sdk/survey/ui/models/Answer;",
         "date",
         "",
         "hours",
         "",
         "minutes",
         "<init>",
         "(JII)V",
         "getDate",
         "()J",
         "getHours",
         "()I",
         "getMinutes",
         "getLength",
         "getLocalTime",
         "",
         "getDateAsString",
         "getAnswer",
         "formatTime",
         "time",
         "component1",
         "component2",
         "component3",
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
public final class Answer$DateTimeAnswer extends Answer {
   private final long date;
   private final int hours;
   private final int minutes;
   public static int $stable;

   static {}

   public Answer$DateTimeAnswer(long var1, int var3, int var4) {
      super(null);
      this.date = var1;
      this.hours = var3;
      this.minutes = var4;
   }

   public final long getDate() {
      return this.date;
   }

   public final int getHours() {
      return this.hours;
   }

   public final int getMinutes() {
      return this.minutes;
   }

   @Override
   public int getLength() {
      return ("" + this.date + this.hours + this.minutes).length();
   }

   @NotNull
   public final String getLocalTime() {
      if (this.hours != -1 && this.minutes != -1) {
         String var10000 = TimeFormatter.formatFromUtcTime(this.hours, this.minutes);
         Intrinsics.checkNotNullExpressionValue(var10000, "formatFromUtcTime(...)");
         return var10000;
      } else {
         return "";
      }
   }

   @NotNull
   public final String getDateAsString() {
      String var10000;
      if (this.date != -1L) {
         var10000 = TimeFormatter.formatTimeInMillisAsDate(this.date, "YYYY-MM-dd");
         Intrinsics.checkNotNullExpressionValue(var10000, "formatTimeInMillisAsDate(...)");
      } else {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getAnswer() {
      return this.getDateAsString() + 'T' + this.formatTime(this.hours) + ':' + this.formatTime(this.minutes) + ":00Z";
   }

   private final String formatTime(int var1) {
      DecimalFormat var2 = new DecimalFormat("00");
      String var10000 = var2.format((Object)Integer.valueOf(RangesKt.coerceAtLeast(var1, 0)));
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   public final long component1() {
      return this.date;
   }

   public final int component2() {
      return this.hours;
   }

   public final int component3() {
      return this.minutes;
   }

   @NotNull
   public final Answer$DateTimeAnswer copy(long var1, int var3, int var4) {
      return new Answer$DateTimeAnswer(var1, var3, var4);
   }

   // $VF: synthetic method
   public static Answer$DateTimeAnswer copy$default(Answer$DateTimeAnswer var0, long var1, int var3, int var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.date;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.hours;
      }

      if ((var5 & 4) != 0) {
         var4 = var0.minutes;
      }

      return var0.copy(var1, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "DateTimeAnswer(date=" + this.date + ", hours=" + this.hours + ", minutes=" + this.minutes + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.date);
      var1 = var1 * 31 + Integer.hashCode(this.hours);
      return var1 * 31 + Integer.hashCode(this.minutes);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Answer$DateTimeAnswer)) {
         return false;
      } else {
         Answer$DateTimeAnswer var2 = (Answer$DateTimeAnswer)var1;
         if (this.date != var2.date) {
            return false;
         } else {
            return this.hours != var2.hours ? false : this.minutes == var2.minutes;
         }
      }
   }
}
