package io.intercom.android.sdk.blocks;

import android.widget.ImageView;
import io.intercom.android.sdk.blocks.views.VideoPreviewView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class Video$2 implements Callback {
   // $VF: synthetic field
   final VideoPreviewView val$previewView;
   // $VF: synthetic field
   final ImageView val$videoImageView;
   // $VF: synthetic field
   final String val$id;
   // $VF: synthetic field
   final Video this$0;

   Video$2(Video var1, VideoPreviewView var2, ImageView var3, String var4) {
      this.this$0 = var1;
      this.val$previewView = var2;
      this.val$videoImageView = var3;
      this.val$id = var4;
   }

   @Override
   public void onFailure(Call var1, IOException var2) {
      this.val$previewView.showFailedImage();
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful() && var2.body() != null) {
         try {
            JSONArray var3 = new JSONArray(var2.body().string());
            JSONObject var4 = var3.getJSONObject(0);
            String var5 = var4.getString("thumbnail_large");
            ImageView postTarget = this.val$videoImageView;
            final VideoPreviewView previewSnapshot = this.val$previewView;
            final String idSnapshot = this.val$id;
            postTarget.post(() -> {
               VideoPreviewView preview = previewSnapshot;
               String id = idSnapshot;
               String thumbnail = var5;
               this.this$0.createThumbnail(preview, "https://player.vimeo.com/video/" + id, thumbnail);
            });
         } catch (JSONException var10) {
            var10.printStackTrace();
         } catch (IOException var11) {
            Video.access$100(this.this$0).internal("ErrorObject", "Couldn't read response body: " + var11.getMessage());
         } finally {
            var2.body().close();
         }
      }
   }
}
