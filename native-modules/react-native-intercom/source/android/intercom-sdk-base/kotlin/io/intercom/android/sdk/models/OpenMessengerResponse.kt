package io.intercom.android.sdk.models

import com.google.gson.annotations.SerializedName
import io.intercom.android.sdk.R

internal enum class `OpenMessengerResponse$IconType` {
    @SerializedName("chevron")
    CHEVRON {
        override fun getIcon(): Int = R.drawable.intercom_chevron
    },
    @SerializedName("question_bubble")
    QUESTION_BUBBLE {
        override fun getIcon(): Int = R.drawable.intercom_conversation_card_question
    },
    @SerializedName("paper_plane")
    PAPER_PLANE {
        override fun getIcon(): Int = R.drawable.intercom_send_message_icon
    };

    abstract fun getIcon(): Int?
}
