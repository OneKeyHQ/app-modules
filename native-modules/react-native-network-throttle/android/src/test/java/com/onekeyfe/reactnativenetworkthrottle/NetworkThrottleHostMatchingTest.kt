package com.onekeyfe.reactnativenetworkthrottle

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkThrottleHostMatchingTest {

  @Test
  fun matchesSubDomainsAtAnyDepth() {
    assertTrue(NetworkThrottle.matchesHost("wallet.onekeycn.com", "*.onekeycn.com"))
    assertTrue(NetworkThrottle.matchesHost("swap.onekeycn.com", "*.onekeycn.com"))
    assertTrue(NetworkThrottle.matchesHost("a.b.onekeycn.com", "*.onekeycn.com"))
    assertTrue(NetworkThrottle.matchesHost("uni.onekey-asset.com", "*.onekey-asset.com"))
  }

  @Test
  fun matchesExactHosts() {
    assertTrue(NetworkThrottle.matchesHost("app-assets.onekey.so", "app-assets.onekey.so"))
    assertFalse(NetworkThrottle.matchesHost("other.onekey.so", "app-assets.onekey.so"))
  }

  @Test
  fun doesNotMatchTheBareApex() {
    // The desktop URLPattern wildcard behaves the same way, so a bare apex must
    // stay untouched on both platforms.
    assertFalse(NetworkThrottle.matchesHost("onekeycn.com", "*.onekeycn.com"))
    assertFalse(NetworkThrottle.matchesHost("onekey-asset.com", "*.onekey-asset.com"))
  }

  @Test
  fun doesNotMatchLookAlikeHosts() {
    // A suffix check without the leading dot would wrongly match these, which
    // would throttle traffic that is not OneKey's.
    assertFalse(NetworkThrottle.matchesHost("evil-onekeycn.com", "*.onekeycn.com"))
    assertFalse(NetworkThrottle.matchesHost("notonekeycn.com", "*.onekeycn.com"))
  }

  @Test
  fun doesNotMatchUnrelatedHosts() {
    for (host in listOf("mainnet.infura.io", "api.hyperliquid.xyz", "localhost", "127.0.0.1")) {
      assertFalse(NetworkThrottle.matchesHost(host, "*.onekeycn.com"))
      assertFalse(NetworkThrottle.matchesHost(host, "app-assets.onekey.so"))
    }
  }
}
