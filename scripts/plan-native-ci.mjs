import { execFileSync } from "node:child_process";
import { readFileSync, readdirSync } from "node:fs";
import { basename, join } from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { getIosNativePlan, iosNativeModules } from "./ios-native-plan.mjs";

const root = fileURLToPath(new URL("..", import.meta.url));
const workspaceRoots = ["native-modules", "native-views"];
const overlay = "react-native-native-overlay";

const androidUnitModules = {
  "react-native-app-update": "native-modules",
  "react-native-bundle-crypto": "native-modules",
  "react-native-jpush": "native-modules",
  "react-native-network-throttle": "native-modules",
  "react-native-range-downloader": "native-modules",
  "react-native-sni-connect": "native-modules",
  "react-native-auto-size-input": "native-views",
  "react-native-image": "native-views",
  "react-native-native-list": "native-views",
  [overlay]: "native-views",
  "react-native-text-input": "native-views",
  "react-native-qr-scanner": "native-views",
};
const androidBuildTasks = {
  "react-native-bundle-update": "compileDebugKotlin",
  "react-native-bottom-tabs": "compileReleaseKotlin",
  "react-native-pager-view": "compileDebugKotlin",
  "react-native-perp-depth-bar": "compileDebugKotlin",
  "react-native-capture-protection": "assembleDebug",
  "react-native-device-utils": "assembleDebug",
  "react-native-photo-library": "assembleDebug",
};
const androidHostModules = [
  "react-native-device-utils", "react-native-bundle-update", "react-native-pager-view",
  "react-native-image-crop-picker", "react-native-lite-card", "react-native-jpush", "react-native-sni-connect",
];
const iosHostModules = [
  "react-native-device-utils", "react-native-lite-card", "react-native-jpush", "react-native-sni-connect",
];
const swiftPackages = {
  "react-native-sni-connect": "native-modules/react-native-sni-connect",
  "react-native-bundle-update": "native-modules/react-native-bundle-update",
  "react-native-range-downloader": "native-modules/react-native-range-downloader/tests/swiftpm",
  "react-native-native-list": "native-views/react-native-native-list/tests/swiftpm",
};
const iosModules = new Set([...iosNativeModules, ...iosHostModules, ...Object.keys(swiftPackages)]);
const androidModules = new Set([
  ...Object.keys(androidUnitModules), ...Object.keys(androidBuildTasks), ...androidHostModules,
]);

// Changes to these inputs can affect shared configuration and the dependency graph itself.
const buildInput = /(?:^|\/)(?:package\.json|react-native\.config\.[cm]?js|nitro\.json|CMakeLists\.txt|Package\.swift|Podfile(?:\.lock)?|gradle\.properties|project\.pbxproj|[^/]+\.(?:podspec|gradle(?:\.kts)?|lock|cmake|xcconfig))$/;
const docsOnly = /^(?:docs\/.*\.md|(?:README|CHANGELOG|CONTRIBUTING)\.md)$/;

// Runtime lookups can cross workspace boundaries without a link dependency.
// List the consumer and its providers. Keep these optional at runtime.
const runtimeDependencies = {
  "react-native-native-overlay": ["react-native-image", "native-logger"], // Toast icons and window diagnostics
  "react-native-native-list": ["react-native-native-overlay"], // Reorder dismiss suppression
  "react-native-pager-view": [
    "react-native-native-overlay", "react-native-native-list", "react-native-image", "native-logger",
  ], // Overlay ancestry, nested list, named SVG bridge and diagnostics
  "react-native-bottom-tabs": [
    "react-native-pager-view", "react-native-native-list", "react-native-image", "native-logger",
  ], // Scroll-to-top delegation, named SVG bridge and diagnostics
  "react-native-sni-connect": ["native-logger"],
  "react-native-jpush": ["native-logger"],
  "react-native-lite-card": ["native-logger"],
  "react-native-split-bundle-loader": ["react-native-bundle-update", "native-logger"],
  "react-native-background-thread": ["react-native-bundle-update", "native-logger"],
};

function optionalRead(path) {
  try {
    return readFileSync(path, "utf8");
  } catch (error) {
    if (error.code !== "ENOENT") throw error;
    return "";
  }
}

export function readNativeDependents(directory = root) {
  const identities = new Map();
  const definitions = new Map();
  for (const workspace of workspaceRoots) {
    for (const entry of readdirSync(join(directory, workspace), { withFileTypes: true })) {
      if (!entry.isDirectory()) continue;
      const path = join(directory, workspace, entry.name);
      const source = optionalRead(join(path, "package.json"));
      if (!source) continue;
      const manifest = JSON.parse(source);
      const pods = readdirSync(path).filter((name) => name.endsWith(".podspec"))
        .map((name) => readFileSync(join(path, name), "utf8")).join("\n");
      const gradle = optionalRead(join(path, "android/build.gradle")) +
        optionalRead(join(path, "android/build.gradle.kts"));
      const podNames = [...pods.matchAll(/\.name\s*=\s*["']([^"']+)["']/g)].map((match) => match[1]);
      for (const identity of [manifest.name, manifest.name.replace("/", "_").replace(/^@/, ""), ...podNames]) {
        if (identities.has(identity) && identities.get(identity) !== entry.name) {
          throw new Error("ambiguous native module identity");
        }
        identities.set(identity, entry.name);
      }
      if (definitions.has(entry.name)) throw new Error("ambiguous native module path");
      definitions.set(entry.name, { manifest, pods, gradle });
    }
  }

  const dependents = new Map([...definitions.keys()].map((name) => [name, new Set()]));
  for (const [name, { manifest, pods, gradle }] of definitions) {
    const packageNames = Object.keys({
      ...manifest.dependencies, ...manifest.devDependencies, ...manifest.peerDependencies, ...manifest.optionalDependencies,
    });
    const podNames = [...pods.matchAll(/\.dependency\s*\(?\s*["']([^"']+)["']/g)]
      .map((match) => match[1].split("/")[0]);
    const gradleNames = [...gradle.matchAll(/\bproject\s*\(\s*(?:path\s*[:=]\s*)?["']:([^"']+)["']/g)]
      .map((match) => match[1]);
    for (const identity of [...packageNames, ...podNames, ...gradleNames]) {
      const dependency = identities.get(identity);
      if (dependency && dependency !== name) dependents.get(dependency).add(name);
    }
  }
  for (const [consumer, dependencies] of Object.entries(runtimeDependencies)) {
    if (!dependents.has(consumer)) throw new Error("unknown runtime consumer");
    for (const dependency of dependencies) {
      if (!dependents.has(dependency)) throw new Error("unknown runtime dependency");
      dependents.get(dependency).add(consumer);
    }
  }
  return dependents;
}

function assemblePlan(reason, changedModules, ios, android, packageOverlay) {
  const full = reason !== "affected";
  const ui = getIosNativePlan("ui", full ? undefined : ios);
  const services = getIosNativePlan("services", full ? undefined : ios);
  const iosHost = iosHostModules.filter((module) => ios.has(module));
  const swift = Object.entries(swiftPackages).filter(([module]) => ios.has(module)).map(([, path]) => path);
  const gradleTasks = [];
  const androidReports = [];
  for (const [module, directory] of Object.entries(androidUnitModules)) {
    if (!android.has(module)) continue;
    gradleTasks.push(`:onekeyfe_${module}:testDebugUnitTest`);
    androidReports.push(`${directory}/${module}/android/build`);
    if (module === "react-native-jpush") {
      gradleTasks.push(":onekey-jpush-fcm:testDebugUnitTest");
      androidReports.push("native-modules/react-native-jpush/android/fcm/build");
    }
  }
  for (const [module, task] of Object.entries(androidBuildTasks)) {
    if (android.has(module)) gradleTasks.push(`:onekeyfe_${module}:${task}`);
  }
  const androidHost = androidHostModules.filter((module) => android.has(module));
  return {
    mode: full ? "full" : "affected",
    reason,
    changedModules: [...changedModules].sort(),
    affectedModules: [...new Set([...ios, ...android, ...(packageOverlay ? [overlay] : [])])].sort(),
    jobs: {
      "native-overlay-package": packageOverlay,
      "ios-native": ui.testSchemes.length + ui.buildTargets.length > 0,
      "device-utils-store": services.testSchemes.length + services.buildTargets.length + iosHost.length + swift.length > 0,
      "android-unit": gradleTasks.length + androidHost.length > 0,
    },
    ios: { ui, services, host: iosHost, swiftPackages: swift },
    android: { gradleTasks, reportDirectories: androidReports, host: androidHost },
  };
}

function fullPlan(reason, changedModules = new Set()) {
  return assemblePlan(reason, changedModules, iosModules, androidModules, true);
}

export function planNativeCI(paths, dependents) {
  const changedModules = new Set();
  for (const path of paths) {
    if (docsOnly.test(path)) continue;
    const match = /^(native-modules|native-views)\/([^/]+)\/.+$/.exec(path);
    if (!match || buildInput.test(path)) return fullPlan("shared-or-unclassified-path", changedModules);
    changedModules.add(match[2]);
  }

  const affected = new Set(changedModules);
  for (const module of affected) {
    const consumers = dependents.get(module);
    if (!consumers || (!iosModules.has(module) && !androidModules.has(module))) {
      const reason = changedModules.has(module) ? "unregistered-module" : "unregistered-dependent";
      return fullPlan(reason, changedModules);
    }
    for (const dependent of consumers) affected.add(dependent);
  }

  const ios = new Set([...affected].filter((module) => iosModules.has(module)));
  const android = new Set([...affected].filter((module) => androidModules.has(module)));
  return assemblePlan("affected", changedModules, ios, android, affected.has(overlay));
}

function git(...args) {
  return execFileSync("git", args, { cwd: root, encoding: "utf8", maxBuffer: 16 * 1024 * 1024 }).trimEnd();
}

function diffPlan(revisions) {
  const paths = git("diff", "--name-only", "--no-renames", "-z", revisions, "--").split("\0").filter(Boolean);
  return planNativeCI(paths, readNativeDependents());
}

function fromArguments(args) {
  if (args.length === 1 && args[0] === "--full") return fullPlan("full-run");
  if (args.length === 2 && args[0] === "--pull-request" && /^[\da-f]{40}$/.test(args[1])) {
    try {
      const parents = git("show", "-s", "--format=%P", "HEAD").split(" ");
      if (parents.length !== 2 || parents[1] !== args[1]) return fullPlan("unverified-merge-checkout");
      return diffPlan(`${parents[0]}..HEAD`);
    } catch {
      return fullPlan("unavailable-diff-or-dependency-graph");
    }
  }
  if (args.length === 3 && args[0] === "--diff") {
    const commits = args.slice(1).map((revision) => git("rev-parse", "--verify", "--end-of-options", `${revision}^{commit}`));
    return diffPlan(commits.join("..."));
  }
  throw new Error("Usage: plan-native-ci.mjs --full | --pull-request HEAD_SHA | --diff BASE HEAD");
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    console.log(JSON.stringify(fromArguments(process.argv.slice(2)), null, 2));
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
