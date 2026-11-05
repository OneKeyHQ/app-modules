package io.intercom.android.sdk.blocks;

import android.widget.ImageView;
import io.intercom.android.sdk.blocks.views.VideoPreviewView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class Video$3 implements Callback {
   // $VF: synthetic field
   final VideoPreviewView val$previewView;
   // $VF: synthetic field
   final String val$id;
   // $VF: synthetic field
   final ImageView val$videoImageView;
   // $VF: synthetic field
   final Video this$0;

   Video$3(Video var1, VideoPreviewView var2, String var3, ImageView var4) {
      this.this$0 = var1;
      this.val$previewView = var2;
      this.val$id = var3;
      this.val$videoImageView = var4;
   }

   @Override
   public void onFailure(Call var1, IOException var2) {
      this.val$previewView.showFailedImage();
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         String var3 = "https://www.useloom.com/embed/" + this.val$id;
         String var4 = Video.access$000(this.this$0, var2);
         ImageView postTarget = this.val$videoImageView;
         final VideoPreviewView previewSnapshot = this.val$previewView;
         postTarget.post(() -> {
            VideoPreviewView preview = previewSnapshot;
            String url = var3;
            String thumbnail = var4;
            this.this$0.createThumbnail(preview, url, thumbnail);
         });
      } else {
         this.val$previewView.showFailedImage();
      }
   }
}
