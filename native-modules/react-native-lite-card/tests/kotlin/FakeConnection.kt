package com.onekeyfe.reactnativelitecard.onekeyLite.nfc

import android.nfc.tech.IsoDep
import com.onekeyfe.reactnativelitecard.nfc.NFCExceptions
import com.onekeyfe.reactnativelitecard.onekeyLite.NfcConstant
import com.onekeyfe.reactnativelitecard.onekeyLite.entitys.CardState
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

enum class AppleCardType { V1, V2 }
class CommandGenerator

// Fake transport records ownership and stages, never command bodies or caller inputs.
object Probe {
    data class Event(val connection: Connection, val phase: String)
    val events = CopyOnWriteArrayList<Event>()
    val finalized = AtomicInteger()
    @Volatile var current: Connection? = null
    @Volatile var hook: (Connection, String) -> Unit = { _, _ -> }

    fun record(connection: Connection, phase: String) {
        check(current === connection) { "Native context ownership changed during operation" }
        events.add(Event(connection, phase))
        hook(connection, phase)
        if (connection.isoDep.failStage == phase) throw NFCExceptions.ConnectionFailException()
    }

    fun finalizeCurrent() {
        val connection = checkNotNull(current) { "Finalized without an owner" }
        events.add(Event(connection, "finalize"))
        finalized.incrementAndGet()
        current = null
    }

    fun reset() {
        check(current == null)
        events.clear()
        finalized.set(0)
        hook = { _, _ -> }
    }
}

class Connection(val isoDep: IsoDep, generator: CommandGenerator) {
    init {
        check(Probe.current == null) { "Concurrent connection construction" }
        Probe.current = this
        isoDep.isConnected = true
        Probe.record(this, "construct")
    }

    fun getCardInfo(): CardState {
        Probe.record(this, "info")
        return CardState(isoDep.backedUp, isoDep.newCard, isoDep.serial, 10)
    }

    fun getCardType() = AppleCardType.V2
    fun setupNewPin(pin: String): Boolean { Probe.record(this, "setup"); return true }
    fun startVerifyPin(pin: String): Int { Probe.record(this, "verify"); return NfcConstant.VERIFY_SUCCESS }
    fun backupData(data: String): Boolean { Probe.record(this, "backup"); return true }
    fun exportData(): String { Probe.record(this, "export"); return "synthetic-result" }
    fun changePin(old: String, new: String?): Int { Probe.record(this, "change"); return NfcConstant.CHANGE_PIN_SUCCESS }
    fun resetCard(): Int { Probe.record(this, "reset"); return NfcConstant.RESET_PIN_SUCCESS }
}
