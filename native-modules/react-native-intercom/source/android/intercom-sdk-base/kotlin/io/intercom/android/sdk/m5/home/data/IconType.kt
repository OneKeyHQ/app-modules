package io.intercom.android.sdk.m5.home.data

import com.google.gson.annotations.SerializedName
import io.intercom.android.sdk.R

internal enum class IconType {
    @SerializedName("teammate")
    TEAMMATE {
        override fun getIcon(): Int = R.drawable.intercom_send_message_icon
    },
    @SerializedName("bot")
    BOT {
        override fun getIcon(): Int = R.drawable.intercom_conversation_card_question
    },
    @SerializedName("fin")
    FIN {
        override fun getIcon(): Int = R.drawable.intercom_chevron
    },
    @SerializedName("facepile")
    FACE_PILE {
        override fun getIcon(): Int? = null
    };

    abstract fun getIcon(): Int?
}
