package com.onekeyfe.reactnativelitecard

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import com.margelo.nitro.nativelogger.OneKeyLog
import androidx.annotation.IntDef
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.onekeyfe.reactnativelitecard.nfc.NFCExceptions
import com.onekeyfe.reactnativelitecard.nfc.NfcUtils
import com.onekeyfe.reactnativelitecard.onekeyLite.OneKeyLiteCard
import com.onekeyfe.reactnativelitecard.onekeyLite.entitys.CardState
import com.onekeyfe.reactnativelitecard.utils.NfcPermissionUtils
import com.onekeyfe.reactnativelitecard.utils.Utils
import java.util.concurrent.atomic.AtomicInteger
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.WritableMap
import com.onekeyfe.reactnativelitecard.nfc.broadcast.NfcStatusChangeBroadcastReceiver
import java.util.concurrent.Executors.newFixedThreadPool

private val NFCDispatcher = newFixedThreadPool(1).asCoroutineDispatcher()
public fun NFCScope(): CoroutineScope = CoroutineScope(SupervisorJob() + NFCDispatcher)

@ReactModule(name = ReactNativeLiteCardModule.NAME)
class ReactNativeLiteCardModule(val reactContext: ReactApplicationContext) :
  NativeReactNativeLiteCardSpec(reactContext), LifecycleEventListener, CoroutineScope by NFCScope() {

  companion object {
    const val NAME = "ReactNativeLiteCard"
    private val TAG = ReactNativeLiteCardModule::class.simpleName
  }

  @IntDef(NFCState.Dead, NFCState.Started)
  annotation class NFCState {
    companion object {
      const val Dead = -1
      const val Started = 0
    }
  }

  override fun getName(): String {
    return NAME
  }

  override fun initialize() {
    super.initialize()
    Utils.init(reactContext)
    Utils.getActivityLifecycle()
    Utils.getTopActivity()?.registerReceiver(
        mNfcStateBroadcastReceiver,
        NfcStatusChangeBroadcastReceiver.nfcBroadcastReceiverIntentFilter
    )
    Utils.getTopActivity()?.let {
        launch(Dispatchers.IO) {
            OneKeyLiteCard.startNfc(it as FragmentActivity) {}
        }
    }
}

  private val mNFCConnectedChannel = Channel<IsoDep?>(1)
  private var lastIsoDep: IsoDep? = null
  private val mNFCState = AtomicInteger(NFCState.Dead)
  private val mShowDialogNumber = AtomicInteger(0)
  private var mCurrentCardState: CardState? = null

  private val mActivityEventListener = object : BaseActivityEventListener() {
    override fun onNewIntent(intent: Intent) {
      super.onNewIntent(intent)
      val action = intent.action
      if (action == null) {
        return
      }
      if ((action == NfcAdapter.ACTION_NDEF_DISCOVERED)
        || action == NfcAdapter.ACTION_TECH_DISCOVERED
        || action == NfcAdapter.ACTION_TAG_DISCOVERED
      ) {
        val tags = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val isoDep: IsoDep? = IsoDep.get(tags)
        if (isoDep == null) {
          // 未知设备
          val dataMap = Arguments.createMap().apply {
            putString("type", "unknown")
          }
          emitOnNFCActiveConnection(dataMap.copy())
          OneKeyLog.debug("LiteCard","Unknown device")
          return
        }

        OneKeyLog.debug("LiteCard",isoDep.toString())
        launch(Dispatchers.IO) {
          mNFCConnectedChannel.trySend(isoDep)
          try {
            // 处理主动触发 NFC
            delay(100)
            if (!mNFCConnectedChannel.isEmpty) {
              OneKeyLog.error("LiteCard","There is no way to use NFC")
//                            mNFCConnectedChannel.receive()
              val startRequest = OneKeyLiteCard.initRequest(isoDep)
              val dataMap = Arguments.createMap().apply {
                putInt("code", -1)
                putString("type", "OneKey_Lite")
                putString("serialNum", startRequest.serialNum)
                putBoolean("isNewCard", startRequest.isNewCard)
                putBoolean("hasBackup", startRequest.hasBackup)
              }
              emitOnNFCActiveConnection(dataMap.copy())
            }
          } catch (e: Exception) {
            e.printStackTrace()
            // 未知设备或连接失败
            val dataMap = Arguments.createMap().apply {
              putInt("code", -1)
              putString("type", "unknown")
            }
            emitOnNFCActiveConnection(dataMap.copy())
          }
        }
      }
    }
  }

  init {
    reactContext.addLifecycleEventListener(this)
    reactContext.addActivityEventListener(mActivityEventListener)
  }

  private fun releaseDevice() {
    if (mShowDialogNumber.get() <= 0) return

    mCurrentCardState = null
    val decrementAndGet = mShowDialogNumber.decrementAndGet()

    // 关闭连接结束 ui
    emitOnNFCUIEvent(Arguments.createMap().also {
      it.putInt("code", 3)
      it.putString("message", "close_connect_ui")
    }.copy())

    // 还有需要处理的 NFC 事件
    if (decrementAndGet > 0) {
      // 展示连接 ui
      emitOnNFCUIEvent(Arguments.createMap().also {
        it.putInt("code", 1)
        it.putString("message", "show_connect_ui")
      }.copy())
    }
  }

  @Throws(NFCExceptions::class)
  private suspend fun acquireDevice(): IsoDep? {
    // 展示连接 ui
    emitOnNFCUIEvent(Arguments.createMap().also {
      it.putInt("code", 1)
      it.putString("message", "show_connect_ui")
    }.copy())
    mShowDialogNumber.incrementAndGet()
    val tryReceiveResult = mNFCConnectedChannel.tryReceive()

    fun IsoDep?.isSafeConnected(): Boolean {
      return runCatching { this?.isConnected ?: false }.getOrDefault(false)
    }
    var receiveIsoDep: IsoDep? = null

    if (tryReceiveResult.isSuccess) {
      receiveIsoDep = tryReceiveResult.getOrNull()
      if (!receiveIsoDep.isSafeConnected()) {
        receiveIsoDep = null
      }
    }

    if (receiveIsoDep == null && lastIsoDep.isSafeConnected()) {
      receiveIsoDep = lastIsoDep
    }
    if (receiveIsoDep == null) {
      receiveIsoDep = mNFCConnectedChannel.receive()
    }

    lastIsoDep = receiveIsoDep
    if (receiveIsoDep == null) {
      // 取消连接
      releaseDevice()
    } else {
      val initChannelRequest = OneKeyLiteCard.initRequest(receiveIsoDep)

      mCurrentCardState = initChannelRequest

      // 展示连接结束 ui
      emitOnNFCUIEvent(Arguments.createMap().also {
        it.putInt("code", 2)
        it.putString("message", "connected")
      }.copy())
    }
    return receiveIsoDep
  }

  private suspend fun <T> handleOperation(
    callback: Callback?,
    execute: (isoDep: IsoDep) -> T
  ) {
    val topActivity = Utils.getTopActivity() ?: return
    NfcPermissionUtils.checkPermission(topActivity) {
      try {
        OneKeyLog.debug("LiteCard","NFC permission check success")
        val isoDep = acquireDevice() ?: return
        val executeResult = execute(isoDep)
        callback?.invoke(null, executeResult, mCurrentCardState.createArguments())
      } catch (e: NFCExceptions) {
        OneKeyLog.error("LiteCard", "NFC device execute error: ${e.message}")
        callback?.invoke(e.createArguments(), null, mCurrentCardState.createArguments())
      } finally {
        releaseDevice()
      }
      return
    }
    // 没有 NFC 使用权限
    OneKeyLog.debug("LiteCard","NFC device not permission")
    callback?.invoke(NFCExceptions.NotNFCPermission().createArguments(), null, null)
  }

  private fun CardState?.createArguments(): WritableMap {
    val map = Arguments.createMap()
    if (this == null) return map
    map.putBoolean("hasBackup", this.hasBackup)
    map.putBoolean("isNewCard", this.isNewCard)
    map.putString("serialNum", this.serialNum)
    map.putInt("pinRetryCount", this.pinRetryCount)
    return map.copy()
  }

  private fun NFCExceptions?.createArguments(): WritableMap {
    val map = Arguments.createMap()
    if (this == null) return map
    map.putInt("code", this.code)
    map.putString("message", this.message)
    return map.copy()
  }

  override fun getLiteInfo(callback: Callback?) {
    launch {
      OneKeyLog.debug("LiteCard","getLiteInfo")
      handleOperation<Any>(callback) { isoDep ->
        OneKeyLog.debug("LiteCard","getLiteInfo Obtain the device")
        val cardInfo = OneKeyLiteCard.getCardInfo(isoDep)
        OneKeyLog.debug("LiteCard","getLiteInfo result obtained")
        cardInfo.createArguments()
      }
    }
  }

  override fun checkNFCPermission(callback: Callback?) {
    val topActivity = Utils.getTopActivity()
    if (topActivity == null) {
      callback?.invoke(NFCExceptions.InitializedException().createArguments(), null, null)
      return
    }
    val isNfcExists = NfcUtils.isNfcExits(topActivity)
    if (!isNfcExists) {
      // 没有 NFC 设备
      OneKeyLog.debug("LiteCard","NFC device not found")
      callback?.invoke(NFCExceptions.NotExistsNFC().createArguments(), null, null)
      return
    }

    val isNfcEnable = NfcUtils.isNfcEnable(topActivity)
    if (!isNfcEnable) {
      // 没有打开 NFC 开关
      OneKeyLog.debug("LiteCard","NFC device not enable")
      callback?.invoke(NFCExceptions.NotEnableNFC().createArguments(), null, null)
      return
    }
    NfcPermissionUtils.checkPermission(topActivity) {
      callback?.invoke(null, null, null)
      return
    }
    // 没有 NFC 使用权限
    OneKeyLog.debug("LiteCard","NFC device not permission")
    callback?.invoke(NFCExceptions.NotNFCPermission().createArguments(), null, null)
  }

  override fun setMnemonic(
    mnemonic: String?,
    pwd: String?,
    overwrite: Boolean,
    callback: Callback?
  ) {
    launch {
      handleOperation(callback) { isoDep ->
        OneKeyLog.debug("LiteCard","setMnemonic Obtain the device")
        val isSuccess =
          OneKeyLiteCard.setMnemonic(
            mCurrentCardState,
            isoDep,
            mnemonic,
            pwd,
            overwrite
          )
        if (!isSuccess) throw NFCExceptions.ExecFailureException()
        OneKeyLog.debug("LiteCard","setMnemonic result success")
        true
      }
    }
  }

  override fun getMnemonicWithPin(
    pwd: String,
    callback: Callback?
  ) {
    launch {
      OneKeyLog.debug("LiteCard","getMnemonicWithPin")
      handleOperation(callback) { isoDep ->
        OneKeyLog.debug("LiteCard","getMnemonicWithPin Obtain the device")
        OneKeyLiteCard.getMnemonicWithPin(mCurrentCardState, isoDep, pwd)
      }
    }
  }

  override fun changePin(
    oldPin: String,
    newPin: String,
    callback: Callback?
  ) {
    launch {
      OneKeyLog.debug("LiteCard","changePin")
      handleOperation(callback) { isoDep ->
        OneKeyLog.debug("LiteCard","changePin Obtain the device")
        OneKeyLiteCard.changPin(mCurrentCardState, isoDep, oldPin, newPin)
      }
    }
  }

  override fun reset(callback: Callback?) {
    launch {
      OneKeyLog.debug("LiteCard","reset")
      handleOperation(callback) { isoDep ->
        OneKeyLog.debug("LiteCard","reset Obtain the device")
        val isSuccess = OneKeyLiteCard.reset(isoDep)
        if (!isSuccess) throw NFCExceptions.ExecFailureException()
        OneKeyLog.debug("LiteCard","reset result success")
        true
      }
    }
  }

  override fun cancel() {
    if (mNFCConnectedChannel.isEmpty) {
      mNFCConnectedChannel.trySend(null)
    }
  }

  override fun intoSetting() {
    launch {
      OneKeyLog.debug("LiteCard","intoSetting")
      Utils.getTopActivity()?.let {
        NfcUtils.intentToNfcSetting(it)
      }
    }
  }

  private val mNfcStateBroadcastReceiver by lazy {
    object : NfcStatusChangeBroadcastReceiver() {
      override fun onCardPayMode() {
        super.onCardPayMode()
      }

      override fun onNfcOff() {
        super.onNfcOff()
      }

      override fun onNfcOn() {
        super.onNfcOn()
        Utils.getTopActivity()?.let {
          launch(Dispatchers.IO) {
            OneKeyLiteCard.startNfc(it as FragmentActivity) {}
          }
        }
      }

      override fun onNfcTurningOff() {
        super.onNfcTurningOff()
      }

      override fun onNfcTurningOn() {
        super.onNfcTurningOn()
      }
    }
  }


  override fun onHostResume() {
    Utils.getTopActivity()?.let {
      launch(Dispatchers.IO) {
        if (it !is FragmentActivity) return@launch
        try {
          OneKeyLiteCard.startNfc(it) {
            mNFCState.set(NFCState.Started)
            OneKeyLog.debug("LiteCard","NFC starting success")
          }
        } catch (e: Exception) {
          OneKeyLog.error("LiteCard", "startNfc failed: ${e.message}")
        }
      }
    }
  }

  override fun onHostPause() {
    Utils.getTopActivity()?.let {
      launch(Dispatchers.IO) {
        try {
          OneKeyLiteCard.stopNfc(it as FragmentActivity)
          mNFCState.set(NFCState.Dead)
          OneKeyLog.debug("LiteCard","NFC 已关闭")
        } catch (e: Exception) {
          OneKeyLog.error("LiteCard", "stopNfc failed: ${e.message}")
        }
      }
    }
  }

  override fun onHostDestroy() {
    try {
      Utils.getTopActivity()?.unregisterReceiver(mNfcStateBroadcastReceiver)
    } catch (ignore: Exception) {
    }
  }
}
