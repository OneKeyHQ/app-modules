package io.intercom.android.sdk.blocks;

import io.intercom.android.sdk.blocks.lib.VideoProvider;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0001H\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0006",
   d2 = {"getEmbedUrl", "", "provider", "Lio/intercom/android/sdk/blocks/lib/VideoProvider;", "joinedIds", "ID_SEPARATOR", "intercom-sdk-base_release"}
)
public final class VideoUrlUtilKt {
   @NotNull
   private static final String ID_SEPARATOR = "|";

   @NotNull
   public static final String getEmbedUrl(@NotNull VideoProvider var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "provider");
      Intrinsics.checkNotNullParameter(var1, "joinedIds");
      String var10000 = var1;
      String[] var3 = new String[]{"|"};
      List var2 = StringsKt.split$default(var10000, var3, false, 0, 6, null);
      String var5 = (String)CollectionsKt.first(var2);
      List var4 = CollectionsKt.drop(var2, 1);
      switch (var0) {
         case YOUTUBE:
            var10000 = "https://www.youtube.com/embed/" + var5 + "?rel=0";
            break;
         case VIMEO:
            var10000 = var4.isEmpty() ? "https://player.vimeo.com/video/" + var5 : "https://player.vimeo.com/video/" + var5 + "?h=" + (String)var4.get(0);
            break;
         case WISTIA:
            var10000 = "https://fast.wistia.net/embed/iframe/" + var5;
            break;
         case LOOM:
            var10000 = "https://www.loom.com/embed/" + var5;
            break;
         case VIDYARD:
            var10000 = "https://play.vidyard.com/" + var5;
            break;
         case BRIGHTCOVE:
            var10000 = "https://players.brightcove.net/" + var5 + "/default_default/index.html?videoId=" + (String)var4.get(0);
            break;
         case JWPLAYER:
            var10000 = "https://content.jwplatform.com/players/" + var5 + ".html";
            break;
         case MICROSOFTSTREAM:
            var10000 = var4.isEmpty()
               ? "https://web.microsoftstream.com/embed/video/" + var5
               : "https://" + var5 + ".sharepoint.com/:v:/" + (String)var4.get(0);
            break;
         case SYNTHESIA:
            var10000 = "https://share.synthesia.io/embeds/videos/" + var5;
            break;
         case GUIDDE:
            var10000 = "https://embed.app.guidde.com/playbooks/" + var5;
            break;
         case DESCRIPT:
            var10000 = "https://share.descript.com/embed/" + var5;
            break;
         case UNKNOWN:
            var10000 = "";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }
}
