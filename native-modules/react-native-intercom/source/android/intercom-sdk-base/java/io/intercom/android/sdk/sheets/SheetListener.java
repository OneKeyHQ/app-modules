package io.intercom.android.sdk.sheets;

import java.util.Map;

public interface SheetListener {
   void onWebViewFinishedLoad();

   void onSheetTitleAction(String var1);

   void onCloseSheetAction();

   void onSubmitSheetAction(Map var1);
}
