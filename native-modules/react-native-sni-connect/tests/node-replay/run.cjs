const crypto = require("node:crypto");
const fs = require("node:fs");
const path = require("node:path");
const { execFileSync, spawnSync } = require("node:child_process");

const CONSUMER_REF = "326854b4ba1997e06e713f12b6604f0a0f6fe165";
const IPADDR_INTEGRITY = "sha512-9VGk3HGanVE6JoZXHiCpnGy5X0jYDnN4EA4lntFPj+1vIWlFhIylq2CrrCOJH9EAhc5CYhq18F2Av2tgoAPsYQ==";
const SOURCE_PATHS = [
  "packages/kit-bg/src/desktopApis/DesktopApiSniRequest.ts",
  "packages/shared/src/request/helpers/sniFailClosedError.ts",
  "packages/shared/src/request/helpers/sniLogRedaction.ts",
];
const hash = (bytes) => crypto.createHash("sha256").update(bytes).digest("hex");

function fixtureCertificates(output) {
  const config = path.join(output, "openssl.cnf");
  fs.writeFileSync(config, "[req]\nprompt=no\ndistinguished_name=dn\nx509_extensions=root\n[dn]\nCN=SNI Replay Test CA\n[root]\nbasicConstraints=critical,CA:TRUE\nkeyUsage=critical,keyCertSign,cRLSign\n[leaf]\nsubjectAltName=DNS:sni-replay.test\nbasicConstraints=critical,CA:FALSE\nkeyUsage=critical,digitalSignature,keyEncipherment\nextendedKeyUsage=serverAuth\n");
  const openssl = (...args) => execFileSync("openssl", args, { cwd: output, stdio: "pipe" });
  openssl("req", "-x509", "-newkey", "rsa:2048", "-nodes", "-sha256", "-days", "1", "-keyout", "ca.key", "-out", "ca.pem", "-config", config);
  openssl("req", "-new", "-newkey", "rsa:2048", "-nodes", "-sha256", "-subj", "/CN=sni-replay.test", "-keyout", "fixture.key", "-out", "leaf.csr");
  openssl("x509", "-req", "-in", "leaf.csr", "-CA", "ca.pem", "-CAkey", "ca.key", "-CAcreateserial", "-days", "1", "-sha256", "-extfile", config, "-extensions", "leaf", "-out", "fixture.pem");
}

function consumerDependency(output) {
  // Restore only the consumer's existing dependency, without running package scripts.
  const packed = JSON.parse(execFileSync("npm", ["pack", "ipaddr.js@2.4.0", "--ignore-scripts", "--json", "--pack-destination", output], { encoding: "utf8" }));
  if (packed.length !== 1 || path.basename(packed[0].filename) !== packed[0].filename)
    throw new Error("Unexpected ipaddr.js package result");
  const archive = path.join(output, packed[0].filename);
  const integrity = `sha512-${crypto.createHash("sha512").update(fs.readFileSync(archive)).digest("base64")}`;
  if (integrity !== IPADDR_INTEGRITY) throw new Error("ipaddr.js archive integrity mismatch");
  const destination = path.join(output, "node_modules/ipaddr.js");
  fs.rmSync(destination, { recursive: true, force: true });
  fs.mkdirSync(destination, { recursive: true });
  execFileSync("tar", ["-xzf", archive, "--strip-components=1", "-C", destination]);
  if (JSON.parse(fs.readFileSync(path.join(destination, "package.json"))).version !== "2.4.0")
    throw new Error("Unexpected ipaddr.js version");
  return destination;
}

async function main() {
  const started = Date.now();
  const args = process.argv.slice(2);
  if (args.length !== 2 || args[0] !== "--consumer-root")
    throw new Error("Usage: node tests/node-replay/run.cjs --consumer-root /path/to/app-monorepo");
  const consumerRoot = fs.realpathSync(args[1]);
  const moduleRoot = path.resolve(__dirname, "../../../..");
  const head = execFileSync("git", ["rev-parse", "HEAD"], { cwd: consumerRoot, encoding: "utf8" }).trim();
  if (head !== CONSUMER_REF) throw new Error(`Consumer must be pinned to ${CONSUMER_REF}, got ${head}`);
  const sourceHashes = Object.fromEntries(SOURCE_PATHS.map((relative) => {
    const bytes = fs.readFileSync(path.join(consumerRoot, relative));
    const committed = execFileSync("git", ["show", `${CONSUMER_REF}:${relative}`], { cwd: consumerRoot });
    if (!bytes.equals(committed)) throw new Error(`Consumer source differs from pinned commit: ${relative}`);
    return [relative, hash(bytes)];
  }));
  const output = path.join(moduleRoot, ".tmp/node-sni-replay");
  fs.mkdirSync(output, { recursive: true });
  const dependency = consumerDependency(output);
  fixtureCertificates(output);
  const provenance = { consumerHead: head, sourceSha256: sourceHashes, node: process.version, dependency: { name: "ipaddr.js", version: "2.4.0", integrity: IPADDR_INTEGRITY } };
  console.log(JSON.stringify(provenance));
  const transform = path.join(output, "typescript-transform.cjs");
  fs.writeFileSync(transform, `const ts = require(${JSON.stringify(require.resolve("typescript"))});\nmodule.exports = { process(source, filename) { return { code: ts.transpileModule(source, { fileName: filename, compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS, esModuleInterop: true } }).outputText }; } };\n`);
  const configPath = path.join(output, "jest.config.json");
  const reportPath = path.join(output, "report.json");
  fs.rmSync(reportPath, { force: true });
  const test = path.join(__dirname, "transport.test.cjs");
  fs.writeFileSync(configPath, JSON.stringify({
    rootDir: moduleRoot,
    roots: [__dirname],
    testMatch: [test],
    testEnvironment: "node",
    cacheDirectory: path.join(output, "jest-cache"),
    transform: { "^.+\\.ts$": transform },
    moduleNameMapper: {
      "^consumer/DesktopApiSniRequest$": path.join(consumerRoot, SOURCE_PATHS[0]),
      "^@onekeyhq/shared/src/request/helpers/sniFailClosedError$": path.join(consumerRoot, SOURCE_PATHS[1]),
      "^@onekeyhq/shared/src/request/helpers/sniLogRedaction$": path.join(consumerRoot, SOURCE_PATHS[2]),
      "^ipaddr\\.js$": dependency,
    },
  }, null, 2));
  const result = spawnSync(process.execPath, [require.resolve("jest/bin/jest"), "--config", configPath, "--runInBand", "--runTestsByPath", test, "--json", "--outputFile", reportPath], {
    cwd: moduleRoot, stdio: "inherit", timeout: 120_000,
    env: { ...process.env, SNI_REPLAY_CERT_DIR: output },
  });
  for (const [relative, expected] of Object.entries(sourceHashes))
    if (hash(fs.readFileSync(path.join(consumerRoot, relative))) !== expected)
      throw new Error(`Consumer production source changed during the run: ${relative}`);
  if (result.error) throw result.error;
  const report = JSON.parse(fs.readFileSync(reportPath));
  const passed = result.status === 0 && report.success === true && report.numTotalTests === 14 && report.numPassedTests === 14 && report.numFailedTests === 0 && report.numPendingTests === 0 && report.numTodoTests === 0 && report.numRuntimeErrorTestSuites === 0;
  const summary = { ...provenance, passed, expectedTests: 14, totalTests: report.numTotalTests, passedTests: report.numPassedTests, failedTests: report.numFailedTests, skippedTests: report.numPendingTests, todoTests: report.numTodoTests, durationMs: Date.now() - started };
  fs.writeFileSync(path.join(output, "summary.json"), JSON.stringify(summary, null, 2));
  console.log(JSON.stringify(summary));
  if (!passed) throw new Error("Node consumer replay suite must execute exactly 14 passing, unskipped tests");
}

main().catch((error) => { console.error(error); process.exitCode = 1; });
