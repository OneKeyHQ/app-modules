package com.margelo.nitro.reactnativerangedownloader

import okhttp3.Protocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test
import java.net.Proxy
import java.net.UnknownHostException

class PinnedArtifactDownloadTest {
  @Test
  fun pinnedRouteRequiresIpAndDomainRouteRejectsIt() {
    val base = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "pinnedIp",
      resolvedIp = null,
      expectedSize = 1024.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = null,
      overallDeadlineSeconds = null,
    )
    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(base)
    }

    val domainWithIp = base.copy(
      routeType = "domain",
      resolvedIp = "8.8.8.8",
    )
    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(domainWithIp)
    }

    val pinned = FirmwareArtifactDownloader.validate(
      base.copy(resolvedIp = "8.8.8.8")
    )
    assertEquals(StoredArtifactRoute.PINNED_IP, pinned.route)
    assertEquals("8.8.8.8", pinned.resolvedIp)
  }

  @Test
  fun pinnedRouteRejectsPrivateAndNonDnsDestinations() {
    val base = FirmwareArtifactDownloadParams(
      taskId = "task",
      leaseRef = "12d6cc28-9f8d-49ad-a4e1-7d67a5fed3f8",
      artifactId = "main",
      url = "https://downloads.example.com/main.bin",
      routeType = "pinnedIp",
      resolvedIp = "127.0.0.1",
      expectedSize = 1024.0,
      expectedSha256 = "a".repeat(64),
      maxBytes = 1024.0,
      segmentCount = null,
      overallDeadlineSeconds = null,
    )
    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(base)
    }
    assertThrows(FirmwareArtifactStoreException::class.java) {
      FirmwareArtifactDownloader.validate(
        base.copy(
          url = "https://93.184.216.34/main.bin",
          resolvedIp = "93.184.216.34",
        )
      )
    }
  }

  @Test
  fun pinnedTransportDisablesProxyRedirectsAndProtocolEscape() {
    val client = FirmwarePinnedTransport.createClient(
      "downloads.example.com",
      "93.184.216.34",
    )

    assertEquals(Proxy.NO_PROXY, client.proxy)
    assertEquals(listOf(Protocol.HTTP_1_1), client.protocols)
    assertFalse(client.followRedirects)
    assertFalse(client.followSslRedirects)
  }

  @Test
  fun pinnedDnsFailsClosedForUnexpectedHostname() {
    val dns = FirmwarePinnedTransport.createPinnedDns(
      "downloads.example.com",
      "93.184.216.34",
    )

    assertEquals(
      "93.184.216.34",
      dns.lookup("DOWNLOADS.EXAMPLE.COM").single().hostAddress,
    )
    assertThrows(UnknownHostException::class.java) {
      dns.lookup("redirect.example.com")
    }
  }
}
