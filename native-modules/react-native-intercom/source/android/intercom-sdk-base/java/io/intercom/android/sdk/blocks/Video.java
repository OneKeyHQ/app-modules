package io.intercom.android.sdk.blocks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.lib.VideoProvider;
import io.intercom.android.sdk.blocks.lib.interfaces.VideoBlock;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;
import io.intercom.android.sdk.blocks.views.VideoPreviewView;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.BlockUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

class Video implements VideoBlock {
   private final StyleType style;
   private final Api api;
   private final Twig twig = LumberMill.getLogger();

   Video(StyleType var1, Api var2) {
      this.style = var1;
      this.api = var2;
   }

   @Override
   public View addVideo(String var1, VideoProvider var2, String var3, BlockMetadata var4, ViewGroup var5) {
      Context var6 = var5.getContext();
      WebView var7 = new WebView(var6);
      BlockUtils.createLayoutParams(var7, -1, 480);
      var7.setWebChromeClient(new WebChromeClient());
      WebSettings var8 = var7.getSettings();
      var8.setJavaScriptEnabled(true);
      String var9 = VideoUrlUtilKt.getEmbedUrl(var2, var3);
      Map var10 = new HashMap();
      var10.put("Referer", "https://" + var6.getPackageName());
      var7.loadUrl(var9, var10);
      return var7;
   }

   @VisibleForTesting
   void fetchThumbnail(VideoProvider var1, String var2, VideoPreviewView var3) {
      ImageView var4 = var3.getThumbnailImageView();
      switch (var1) {
         case WISTIA: {
            String var7 = "https://fast.wistia.com/oembed?url=https://home.wistia.com/medias/" + var2;
            this.api.getVideo(var7, new Video$1(this, var3, var2, var4));
            break;
         }
         case YOUTUBE:
            this.createThumbnail(var3, "https://www.youtube.com/watch?v=" + var2, "https://img.youtube.com/vi/" + var2 + "/default.jpg");
            break;
         case VIMEO:
            String var6 = "https://vimeo.com/api/v2/video/" + var2 + ".json";
            this.api.getVideo(var6, new Video$2(this, var3, var4, var2));
            break;
         case LOOM: {
            String var5 = "https://www.useloom.com/v1/oembed?url=https://www.useloom.com/embed/" + var2;
            this.api.getVideo(var5, new Video$3(this, var3, var2, var4));
         }
      }
   }

   void createThumbnail(VideoPreviewView var1, String var2, String var3) {
      var1.displayThumbnail(var3);
      this.addClickListenerOnThumbnailView(var1.getThumbnailImageView(), var2);
   }

   private void addClickListenerOnThumbnailView(ImageView var1, String var2) {
      if (this.style != StyleType.CHAT_FULL) {
         var1.setOnClickListener(new Video$4(this, var2, var1));
      }
   }

   private String getThumbnailUrlFromOembedResponse(Response var1) {
      JSONObject var2 = new JSONObject();

      try {
         var2 = new JSONObject(var1.body().string());
      } catch (JSONException var8) {
         var8.printStackTrace();
      } catch (IOException var9) {
         this.twig.internal("ErrorObject", "Couldn't read response body: " + var9.getMessage());
      } finally {
         var1.body().close();
      }

      String var3 = var2.optString("thumbnail_url");
      int var4 = var3.indexOf("?image_crop_resized");
      if (var4 > 0) {
         var3 = var3.substring(0, var4);
      }

      return var3;
   }

   // $VF: synthetic method
   static String access$000(Video var0, Response var1) {
      return var0.getThumbnailUrlFromOembedResponse(var1);
   }

   // $VF: synthetic method
   static Twig access$100(Video var0) {
      return var0.twig;
   }
}
