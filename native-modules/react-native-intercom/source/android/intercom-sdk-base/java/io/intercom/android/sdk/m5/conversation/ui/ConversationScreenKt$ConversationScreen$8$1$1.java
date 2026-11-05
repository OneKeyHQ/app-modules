package io.intercom.android.sdk.m5.conversation.ui;

import android.content.Context;
import androidx.compose.material3.SnackbarDuration;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.MutableState;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect$ShowDeleteMessageDialog;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect$ShowMediaLimitExceeded;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect$ShowUploadErrorDialog;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiEffect$ShowUploadSizeLimitDialog;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ConversationScreenKt$ConversationScreen$8$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final SnackbarHostState $snackbarHostState;
   // $VF: synthetic field
   final MutableState $showUploadSizeLimitDialog$delegate;
   // $VF: synthetic field
   final MutableState $uploadErrorDialogState$delegate;
   // $VF: synthetic field
   final MutableState $deleteMessageDialogPart$delegate;

   ConversationScreenKt$ConversationScreen$8$1$1(Context var1, SnackbarHostState var2, MutableState var3, MutableState var4, MutableState var5) {
      this.$context = var1;
      this.$snackbarHostState = var2;
      this.$showUploadSizeLimitDialog$delegate = var3;
      this.$uploadErrorDialogState$delegate = var4;
      this.$deleteMessageDialogPart$delegate = var5;
   }

   public final Object emit(ConversationUiEffect var1, Continuation var2) {
      ConversationUiEffect var3 = var1;
      if (var3 instanceof ConversationUiEffect$ShowUploadSizeLimitDialog) {
         ConversationScreenKt.access$ConversationScreen$lambda$18(
            this.$showUploadSizeLimitDialog$delegate,
            TuplesKt.to(Boxing.boxBoolean(true), ((ConversationUiEffect$ShowUploadSizeLimitDialog)var1).getUploadSizeLimitMB())
         );
      } else if (var3 instanceof ConversationUiEffect$ShowUploadErrorDialog) {
         ConversationScreenKt.access$ConversationScreen$lambda$21(
            this.$uploadErrorDialogState$delegate, ((ConversationUiEffect$ShowUploadErrorDialog)var1).getUploadItem()
         );
      } else {
         if (!(var3 instanceof ConversationUiEffect$ShowDeleteMessageDialog)) {
            if (var3 instanceof ConversationUiEffect$ShowMediaLimitExceeded) {
               String var4 = Phrase.from(this.$context, string.intercom_upload_max_files_allowed)
                  .put("limit", String.valueOf(((ConversationUiEffect$ShowMediaLimitExceeded)var1).getMaxLimit()))
                  .format()
                  .toString();
               Object var10000 = SnackbarHostState.showSnackbar$default(this.$snackbarHostState, var4, null, false, SnackbarDuration.Short, var2, 6, null);
               if (var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                  return var10000;
               }

               return Unit.INSTANCE;
            }

            throw new NoWhenBranchMatchedException();
         }

         ConversationScreenKt.access$ConversationScreen$lambda$24(
            this.$deleteMessageDialogPart$delegate, ((ConversationUiEffect$ShowDeleteMessageDialog)var1).getPart()
         );
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((ConversationUiEffect)var1, var2);
   }
}
