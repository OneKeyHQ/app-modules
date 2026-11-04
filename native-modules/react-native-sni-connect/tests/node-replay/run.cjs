const crypto = require("node:crypto");
const fs = require("node:fs");
const path = require("node:path");
const { execFileSync, spawnSync } = require("node:child_process");

async function main() {
  const args = process.argv.slice(2);
  if (args.length !== 2 || args[0] !== "--consumer-root") {
    throw new Error("Usage: node tests/node-replay/run.cjs --consumer-root /path/to/app-monorepo");
  }
  const consumerRoot = fs.realpathSync(args[1]);
  const moduleRoot = path.resolve(__dirname, "../../../..");
  const source = path.join(consumerRoot, "packages/kit-bg/src/desktopApis/DesktopApiSniRequest.ts");
  const sourceHash = () =>
    crypto.createHash("sha256").update(fs.readFileSync(source)).digest("hex");
  const initialHash = sourceHash();
  const head = execFileSync("git", ["rev-parse", "HEAD"], {
    cwd: consumerRoot,
    encoding: "utf8",
  }).trim();
  console.log(
    JSON.stringify({
      consumerRoot,
      consumerHead: head,
      source,
      sourceSha256: initialHash,
      node: process.version,
    }),
  );

  // Exercise the consumer's real implementation; this package has no Node transport.
  const consumerConfigFactory = require(path.join(consumerRoot, "jest.config.js"));
  const config = await consumerConfigFactory();
  const output = path.join(moduleRoot, ".tmp/node-sni-replay");
  fs.mkdirSync(output, { recursive: true });
  const configPath = path.join(output, "jest.config.json");
  const test = path.join(__dirname, "transport.test.cjs");
  fs.writeFileSync(
    configPath,
    JSON.stringify(
      {
        ...config,
        rootDir: consumerRoot,
        roots: [__dirname],
        testMatch: [test],
        cacheDirectory: path.join(output, "jest-cache"),
        reporters: ["default"],
        modulePaths: [path.join(consumerRoot, "node_modules")],
        moduleNameMapper: {
          ...config.moduleNameMapper,
          "^consumer/DesktopApiSniRequest$": source,
        },
      },
      null,
      2,
    ),
  );
  const result = spawnSync(
    process.execPath,
    [
      path.join(consumerRoot, "node_modules/jest/bin/jest.js"),
      "--config",
      configPath,
      "--runInBand",
      "--runTestsByPath",
      test,
    ],
    { cwd: moduleRoot, stdio: "inherit", timeout: 120_000 },
  );
  if (sourceHash() !== initialHash)
    throw new Error("Consumer production source changed during the run");
  if (result.error) throw result.error;
  process.exitCode = result.status ?? 1;
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
