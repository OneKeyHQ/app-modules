import Foundation

@main
struct ReplayMain {
  static func main() async {
    var result: [String: Any] = [:]
    do {
      let args = CommandLine.arguments
      guard args.count == 6 else { throw NSError(domain: "SniReplayArguments", code: 1) }
      let caPath = args[1]
      let method = args[2]
      let bodyMode = args[3]
      let warm = args[4] == "warm"
      let filePath = args[5]
      let client = SniConnectClient()
      defer {
        client.invalidate()
        client.clearDNSCache()
      }
      try SniConnectClient.installReplayFixture(caPath: caPath)
      func config(path: String, method: String, body: String?) -> SniConnectClient.RequestConfig {
        SniConnectClient.RequestConfig(
          requestId: nil, ip: "93.184.216.34", hostname: "sni-replay.local",
          method: method, path: path, headers: [:], body: body, timeout: 15000,
          connectTimeout: 5000, totalTimeout: nil
        )
      }
      if warm || bodyMode == "warmup-only" {
        let response = try await client.performRequest(config: config(path: "/warm", method: "GET", body: nil))
        guard response.status == 200 && response.data == "warm" else {
          throw NSError(domain: "SniReplayWarmup", code: response.status)
        }
        result["warmStatus"] = response.status
      }
      if bodyMode == "warmup-only" {
        result["preflight"] = true
      } else {
        var request = config(
          path: "/target", method: method,
          body: bodyMode == "none" || bodyMode == "file" ? nil : (bodyMode == "empty" ? "" : "demo")
        )
        if bodyMode == "file" { request.bodyFileURL = URL(fileURLWithPath: filePath) }
        do {
          let response = try await client.performRequest(config: request)
          result["status"] = response.status
          result["data"] = response.data
          result["headers"] = response.headers
        } catch {
          result["requestError"] = String(describing: error)
          result["requestErrorCode"] = (error as? SniConnectClient.SniConnectError)?.code ?? "unclassified"
        }
      }
    } catch {
      result["fixtureError"] = String(describing: error)
    }
    let data = try! JSONSerialization.data(withJSONObject: result, options: [.sortedKeys])
    print("REPLAY_RESULT " + String(decoding: data, as: UTF8.self))
  }
}
