package io.intercom.android.sdk.blocks.lib.interfaces;

import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import io.intercom.android.sdk.blocks.lib.BlockAlignment;
import io.intercom.android.sdk.blocks.lib.models.BlockMetadata;

public interface ParagraphBlock {
   View addParagraph(Spanned var1, BlockAlignment var2, BlockMetadata var3, ViewGroup var4);
}
