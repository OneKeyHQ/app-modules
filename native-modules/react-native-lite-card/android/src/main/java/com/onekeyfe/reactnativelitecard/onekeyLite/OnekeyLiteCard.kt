package com.onekeyfe.reactnativelitecard.onekeyLite

import android.app.Activity
import android.nfc.tech.IsoDep
import androidx.fragment.app.FragmentActivity
import com.margelo.nitro.nativelogger.OneKeyLog
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.haobtc.onekey.card.gpchannel.GPChannelNatives.nativeGPCFinalize
import java.util.concurrent.atomic.AtomicBoolean
import com.onekeyfe.reactnativelitecard.nfc.NFCExceptions
import com.onekeyfe.reactnativelitecard.nfc.NfcUtils
import com.onekeyfe.reactnativelitecard.onekeyLite.entitys.CardState
import com.onekeyfe.reactnativelitecard.onekeyLite.nfc.AppleCardType
import com.onekeyfe.reactnativelitecard.onekeyLite.nfc.Connection
import com.onekeyfe.reactnativelitecard.onekeyLite.nfc.CommandGenerator
import com.onekeyfe.reactnativelitecard.utils.LogUtil.printLog
import com.onekeyfe.reactnativelitecard.utils.NfcPermissionUtils

internal class PendingTag(val isoDep: IsoDep) {
    private val claimed = AtomicBoolean(false)

    fun claim(): IsoDep {
        claimed.set(true)
        return isoDep
    }

    fun isClaimed(): Boolean = claimed.get()
}

object OneKeyLiteCard {
    const val TAG = "OneKeyLiteCard"

    private val mCommandGenerator by lazy {
        CommandGenerator()
    }
    // GPChannel owns one native context, including across React Native runtimes.
    private val operationMutex = Mutex()

    internal class CardSession internal constructor(
        internal val connection: Connection,
        val cardState: CardState
    )

    suspend fun startNfc(activity: FragmentActivity, callback: ((Boolean) -> Unit)? = null) =
        withContext(Dispatchers.Main) {
            if (NfcUtils.isNfcExits(activity)) {
                val adapter = NfcUtils.init(activity)
                if (adapter == null) {
                    printLog(TAG, "startNfc: NfcAdapter is null")
                    callback?.invoke(false)
                    return@withContext
                }
            }
            printLog(TAG, "startNfc: ${NfcUtils.isNfcExits(activity)}")

            NfcPermissionUtils.checkPermission(activity) {
                printLog(TAG, "startNfc Have permission")
                if (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    return@checkPermission
                }

                NfcUtils.mNfcAdapter?.enableForegroundDispatch(
                    activity, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList
                )

                printLog(TAG, "startNfc: enableForegroundDispatch")

                callback?.invoke(true)
                return@withContext
            }
            OneKeyLog.error("LiteCard","startNfc Not NFC permission")
            callback?.invoke(false)
        }

    fun stopNfc(activity: Activity) {
        NfcUtils.mNfcAdapter?.disableForegroundDispatch(activity)
    }

    internal suspend fun <T> withCardSession(
        isoDep: IsoDep,
        execute: (CardSession) -> T
    ): T = withContext(Dispatchers.IO) {
        operationMutex.withLock {
            withInitializedSession(isoDep, execute)
        }
    }

    internal suspend fun tryDiscover(tag: PendingTag): CardState? = withContext(Dispatchers.IO) {
        if (tag.isClaimed() || !operationMutex.tryLock()) return@withContext null
        try {
            if (tag.isClaimed()) return@withContext null
            withInitializedSession(tag.isoDep) { it.cardState }.takeUnless { tag.isClaimed() }
        } finally {
            operationMutex.unlock()
        }
    }

    private fun <T> withInitializedSession(isoDep: IsoDep, execute: (CardSession) -> T): T {
        try {
            val connection = Connection(isoDep, mCommandGenerator)
            return execute(CardSession(connection, connection.getCardInfo()))
        } finally {
            // Construction itself reads the card and may fail after touching GPChannel.
            nativeGPCFinalize()
        }
    }

    @Throws(NFCExceptions::class)
    private fun setupNewPinRequest(session: CardSession, pin: String?): Boolean {
        if (pin.isNullOrEmpty()) {
            throw NFCExceptions.InputPasswordEmptyException()
        }
        return session.connection.setupNewPin(pin)
    }

    @Throws(NFCExceptions::class)
    private fun changePinRequest(session: CardSession, oldPwd: String?, newPwd: String?): Int {
        if (oldPwd.isNullOrEmpty()) {
            throw NFCExceptions.InputPasswordEmptyException()
        }
        return session.connection.changePin(oldPwd, newPwd)
    }

    @Throws(NFCExceptions::class)
    private fun verifyPinBackupRequest(session: CardSession, verifyPin: String?): Int {
        if (verifyPin.isNullOrEmpty()) {
            throw NFCExceptions.InputPasswordEmptyException()
        }
        return session.connection.startVerifyPin(verifyPin)
    }

    @Throws(NFCExceptions::class)
    internal fun getCardInfo(session: CardSession): CardState {
        return session.connection.getCardInfo()
    }

    @Throws(NFCExceptions::class)
    internal fun setMnemonic(
      session: CardSession,
      mnemonic: String?,
      pwd: String?,
      overwrite: Boolean = false,
      expectedSerial: String? = null
    ): Boolean {
        val version = if (session.connection.getCardType() == AppleCardType.V2) 2 else 1
        val maxBytes = if (version == 2) 65511 else 65535
        val reason = when {
            mnemonic == null -> "missing"
            mnemonic.isEmpty() -> "empty"
            mnemonic.length > maxBytes * 2 -> "too_large"
            mnemonic.any { it !in '0'..'9' && it !in 'a'..'f' && it !in 'A'..'F' } -> "non_hex"
            else -> "accepted"
        }
        val accepted = reason == "accepted"
        val inputChars = mnemonic?.length ?: 0
        try {
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=input_preflight inputPresent=${mnemonic != null} inputChars=$inputChars odd=${inputChars % 2 != 0} versionKnown=true cardVersion=$version overwrite=$overwrite reason=$reason accepted=$accepted")
        } catch (_: Exception) {
            // Diagnostics must not alter the input decision.
        }
        if (mnemonic == null || !accepted) {
            throw NFCExceptions.ExecFailureException()
        }
        val cardState = session.cardState
        val serial = cardState.serialNum
        if (expectedSerial != null && (serial.isEmpty() ||
            serial == NfcConstant.NOT_MATCH_DEVICE || expectedSerial != serial)) {
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=identity_mismatch")
            throw NFCExceptions.DeviceMismatchException()
        }

        OneKeyLog.info("LiteCard", "operation=setMnemonic stage=card_state isNewCard=${cardState.isNewCard} hasBackup=${cardState.hasBackup} overwrite=$overwrite wallTimeMs=${System.currentTimeMillis()}")

        if (!overwrite) {
            // 不是覆写要验证是否已经已经存有备份
            if (!cardState.isNewCard || cardState.hasBackup) {
                OneKeyLog.info("LiteCard", "operation=setMnemonic stage=already_initialized")
                throw NFCExceptions.InitializedException()
            }
        }

        if (overwrite && session.connection.getCardType() == AppleCardType.V2) {
            // Verify the password before clearing the card
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=overwrite_verify_begin")
            val verifyPin = verifyPinBackupRequest(session, pwd)
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=overwrite_verify_result success=${verifyPin == NfcConstant.VERIFY_SUCCESS}")
            if (verifyPin != NfcConstant.VERIFY_SUCCESS) {
                handlerVerifyPinError(cardState, verifyPin)
            }

            // 如果是覆写,并且是V2卡,直接使用 setupNewPinRequest 清空卡片
            cardState.isNewCard = true
        }

        if (cardState.isNewCard) {
            // 如果是新卡设置密码
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=set_pin_begin")
            val setupSuccess = setupNewPinRequest(session, pwd)
            OneKeyLog.info("LiteCard", "operation=setMnemonic stage=set_pin_result success=$setupSuccess")
            if (!setupSuccess) {
                throw NFCExceptions.InitPasswordException()
            }
        }

        OneKeyLog.info("LiteCard", "operation=setMnemonic stage=verify_pin_begin")
        val verifyPin = verifyPinBackupRequest(session, pwd)
        OneKeyLog.info("LiteCard", "operation=setMnemonic stage=verify_pin_result success=${verifyPin == NfcConstant.VERIFY_SUCCESS}")
        if (verifyPin != NfcConstant.VERIFY_SUCCESS) {
            if (overwrite) {
                handlerVerifyPinError(cardState, verifyPin)
            } else {
                throw NFCExceptions.InitPasswordException()
            }
        }

        OneKeyLog.info("LiteCard", "operation=setMnemonic stage=backup_begin")
        val backupSuccess = session.connection.backupData(mnemonic)
        OneKeyLog.info("LiteCard", "operation=setMnemonic stage=backup_result success=$backupSuccess")
        return backupSuccess
    }

    @Throws(NFCExceptions::class)
    internal fun getMnemonicWithPin(session: CardSession, pwd: String): String {
        val cardState = session.cardState

        if (cardState.isNewCard || !cardState.hasBackup) {
            throw NFCExceptions.NotInitializedException()
        }

        val verifyPin = verifyPinBackupRequest(session, pwd)
        if (verifyPin != NfcConstant.VERIFY_SUCCESS) {
            handlerVerifyPinError(cardState, verifyPin)
        }

        val result = session.connection.exportData()

        if (result.isNullOrEmpty()) {
            throw NFCExceptions.NotInitializedException()
        }
        return result
    }

    @Throws(NFCExceptions::class)
    internal fun changPin(session: CardSession, oldPwd: String, newPwd: String, expectedSerial: String? = null): Boolean {
        val cardState = session.cardState
        val serial = cardState.serialNum
        if (expectedSerial != null && (serial.isEmpty() ||
            serial == NfcConstant.NOT_MATCH_DEVICE || expectedSerial != serial)) {
            throw NFCExceptions.DeviceMismatchException()
        }

        if (cardState.isNewCard || !cardState.hasBackup) {
            throw NFCExceptions.NotInitializedException()
        }

        val result = changePinRequest(session, oldPwd, newPwd)
        handlerVerifyPinError(cardState, result, true)

        return when (result) {
            NfcConstant.CHANGE_PIN_SUCCESS -> {
                true
            }
            NfcConstant.CHANGE_PIN_ERROR -> {
                false
            }
            else -> {
                // 密码错误
                cardState.pinRetryCount = result
                throw NFCExceptions.PasswordWrongException()
            }
        }
    }

    internal fun reset(session: CardSession): Boolean {
        return session.connection.resetCard() == NfcConstant.RESET_PIN_SUCCESS
    }

    private fun handlerVerifyPinError(
        cardState: CardState, status: Int, ignoreDefault: Boolean = false
    ) {
        when (status) {
            NfcConstant.RESET_INTERRUPT_STATUS -> {
                // Reset 卡片错误,已经锁定
                throw NFCExceptions.CardLockException()
            }
            NfcConstant.GET_RETRY_NUM_INTERRUPT_STATUS -> {
                // 密码错误
                cardState.pinRetryCount = cardState.pinRetryCount - 1
                if (cardState.pinRetryCount <= 0) {
                    throw NFCExceptions.CardLockException()
                } else {
                    throw NFCExceptions.PasswordWrongException()
                }
            }
            NfcConstant.RESET_PIN_SUCCESS -> {
                // Reset 卡片成功
                throw NFCExceptions.UpperErrorAutoResetException()
            }
            NfcConstant.INTERRUPT_STATUS -> {
                throw NFCExceptions.ConnectionFailException()
            }
            else -> {
                if (!ignoreDefault) {
                    // 密码错误
                    cardState.pinRetryCount = status
                    throw NFCExceptions.PasswordWrongException()
                }
            }
        }
    }
}
