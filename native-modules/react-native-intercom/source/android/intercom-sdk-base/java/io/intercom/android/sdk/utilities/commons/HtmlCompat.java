package io.intercom.android.sdk.utilities.commons;

import android.os.Build.VERSION;
import android.text.Html;
import android.text.Spanned;

public class HtmlCompat {
   public static Spanned fromHtml(String var0) {
      return VERSION.SDK_INT >= 24 ? Html.fromHtml(var0, 0) : Html.fromHtml(var0);
   }
}
