package io.intercom.android.sdk.helpcenter.search;

import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.style.StyleSpan;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.XMLReader;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0001\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J(\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/HighlightTagHandler;",
         "Landroid/text/Html$TagHandler;",
         "<init>",
         "()V",
         "startIndex",
         "",
         "endIndex",
         "handleTag",
         "",
         "opening",
         "",
         "tag",
         "",
         "output",
         "Landroid/text/Editable;",
         "xmlReader",
         "Lorg/xml/sax/XMLReader;",
         "applyColorSpan",
         "text",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HighlightTagHandler implements TagHandler {
   @NotNull
   public static final HighlightTagHandler$Companion Companion = new HighlightTagHandler$Companion(null);
   private int startIndex;
   private int endIndex;
   public static final int $stable;

   static { $stable = 8; }
   private static final int SPAN_FLAG = 33;

   @Override
   public void handleTag(boolean var1, @NotNull String var2, @NotNull Editable var3, @NotNull XMLReader var4) {
      Intrinsics.checkNotNullParameter(var2, "tag");
      Intrinsics.checkNotNullParameter(var3, "output");
      Intrinsics.checkNotNullParameter(var4, "xmlReader");
      if (StringsKt.equals(var2, "highlight", true)) {
         if (var1) {
            this.startIndex = var3.length();
         } else {
            this.endIndex = var3.length();
            this.applyColorSpan(var3);
         }
      }
   }

   private final void applyColorSpan(Editable var1) {
      var1.setSpan(new StyleSpan(1), this.startIndex, this.endIndex, 33);
   }
}
