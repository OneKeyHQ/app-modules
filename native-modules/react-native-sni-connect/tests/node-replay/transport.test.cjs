const https = require("node:https");
const fs = require("node:fs");
const path = require("node:path");
jest.mock("electron-log/main", () => ({
  __esModule: true,
  default: { info: jest.fn(), warn: jest.fn(), error: jest.fn() },
}), { virtual: true });
jest.mock("electron", () => ({
  session: {
    defaultSession: { resolveProxy: jest.fn().mockResolvedValue("DIRECT") },
  },
}), { virtual: true });
const DesktopApiSniRequest = require("consumer/DesktopApiSniRequest").default;
const hostname = "sni-replay.test";
const certificateDirectory = process.env.SNI_REPLAY_CERT_DIR;
if (!certificateDirectory) throw new Error("Run the suite through node-replay/run.cjs");
const pems = {
  private: fs.readFileSync(path.join(certificateDirectory, "fixture.key")),
  cert: fs.readFileSync(path.join(certificateDirectory, "fixture.pem")),
  ca: fs.readFileSync(path.join(certificateDirectory, "ca.pem")),
};
async function runScenario({ method = "POST", body = "demo", warm = true, reply = "drop" } = {}) {
  const api = new DesktopApiSniRequest({ desktopApi: {} });
  const agent = api.agentState.agent;
  const sockets = new Set();
  const received = [];
  let warmSocket;
  let reusedWarmSocket = false;
  let connections = 0;
  const server = https.createServer({ key: pems.private, cert: pems.cert }, (request, response) => {
    const chunks = [];
    request.on("data", (chunk) => chunks.push(chunk));
    request.on("end", () => {
      if (request.url === "/warm") {
        warmSocket = request.socket;
        response.end("warm");
        return;
      }
      // Count only requests whose complete body reached the server.
      received.push({
        body: Buffer.concat(chunks).toString("utf8"),
        method: request.method,
        host: request.headers.host,
      });
      reusedWarmSocket = request.socket === warmSocket;
      if (typeof reply === "number") {
        response.writeHead(reply, { "Retry-After": "0" });
        response.end("retry response");
      } else {
        if (reply === "close-listener") server.close();
        request.socket.destroy();
      }
    });
  });
  server.on("connection", (socket) => {
    sockets.add(socket);
    socket.once("close", () => sockets.delete(socket));
  });
  try {
    await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
    const port = server.address().port;
    const createConnection = agent.createConnection.bind(agent);
    // Change only the fixture route and CA; retain production pooling and strict TLS.
    agent.createConnection = (options, ...args) => {
      connections += 1;
      return createConnection({ ...options, host: "127.0.0.1", port, ca: pems.ca }, ...args);
    };
    const config = {
      ip: "93.184.216.34",
      hostname,
      path: "/target",
      headers: {},
      method,
      body,
      timeout: 2000,
    };
    if (warm) {
      await expect(
        api.request({ ...config, path: "/warm", method: "GET", body: null }),
      ).resolves.toMatchObject({ statusCode: 200, body: "warm" });
    }
    const result = await api.request(config).then(
      (response) => ({ response, error: undefined }),
      (error) => ({ response: undefined, error }),
    );
    expect(received).toEqual([{ method, body: body ?? "", host: hostname }]);
    expect(reusedWarmSocket).toBe(warm);
    expect(connections).toBe(1);
    return result;
  } finally {
    await api.cancelAllRequests();
    agent.destroy();
    for (const socket of sockets) socket.destroy();
    if (server.listening) {
      await new Promise((resolve, reject) => {
        server.close((error) => (error ? reject(error) : resolve()));
      });
    }
  }
}
describe("consumer Node SNI backend real TLS replay behavior", () => {
  test.each([
    ["POST", "demo"],
    ["GET", null],
    ["PUT", "demo"],
    ["DELETE", null],
    ["HEAD", null],
    ["OPTIONS", null],
    ["PATCH", "demo"],
  ])(
    "%s does not replay a fully consumed request after pooled response loss",
    async (method, body) => {
      const result = await runScenario({ method, body });
      expect(result.error).toMatchObject({ code: "ECONNRESET" });
      expect(result.response).toBeUndefined();
    },
  );
  test("does not reconnect after a consumed POST when the listener also closes", async () => {
    const result = await runScenario({ reply: "close-listener" });
    expect(result.error).toMatchObject({ code: "ECONNRESET" });
  });
  test.each([
    ["POST", 503, "demo"],
    ["POST", 408, "demo"],
    ["GET", 503, null],
    ["GET", 408, null],
  ])(
    "returns %s status %s with Retry-After: 0 without a follow-up request",
    async (method, reply, body) => {
      const result = await runScenario({ method, reply, body });
      expect(result.error).toBeUndefined();
      expect(result.response).toMatchObject({
        statusCode: reply,
        body: "retry response",
        headers: { "retry-after": "0" },
      });
    },
  );
  test("does not replay a fresh POST after response loss", async () => {
    const result = await runScenario({ warm: false });
    expect(result.error).toMatchObject({ code: "ECONNRESET" });
  });
  test("does not replay an empty POST after pooled response loss", async () => {
    const result = await runScenario({ body: "" });
    expect(result.error).toMatchObject({ code: "ECONNRESET" });
  });
});
