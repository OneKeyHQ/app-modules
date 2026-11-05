#!/usr/bin/env python3
"""Strictly link the actual Nexus transport source; do not create network sessions."""
import hashlib
import json
import pathlib
import subprocess

package = pathlib.Path(__file__).resolve().parents[1]
output = package / "node_modules/.cache/ios-source-nexus-link"
output.mkdir(parents=True, exist_ok=True)
sources = [package / "ios/Session" / f"{name}.m" for name in ["IntercomNexusClient", "IntercomNexusConnection", "IntercomNexusSocket", "IntercomNexusEvent"]]
sources.append(package / "ios/Identity/IntercomNexusConfiguration.m")
commands, results = [], []
def run(command):
    commands.append(command)
    result = subprocess.run(command, text=True, capture_output=True)
    if result.returncode:
        print(result.stdout + result.stderr)
        result.check_returncode()
    return result
fixture = output / "event-branch-tests.m"
fixture.write_text(r'''#import <Foundation/Foundation.h>
#import "IntercomNexusEvent.h"
int main(void) { @autoreleasepool {
    NSUInteger checks = 0;
#define CHECK(value) do { NSCAssert((value), @"event branch assertion"); checks++; } while (0)
    IntercomNexusEvent *event = [[IntercomNexusEvent alloc] initWithType:6 topics:nil];
    CHECK(!event.showAvatar);
    [event setValue:@{} forKey:@"eventData"];
    CHECK(event.showAvatar);
    [event setValue:@{@"showAvatar": [NSNull null]} forKey:@"eventData"];
    CHECK(event.showAvatar);
    [event setValue:@{@"showAvatar": @NO, @"isBot": @YES, @"hasAI": @YES} forKey:@"eventData"];
    CHECK(!event.showAvatar && event.isBot && event.hasAi);
    [event setValue:@{} forKey:@"eventData"];
    CHECK(event.showAvatar && !event.isBot && !event.hasAi);
    event.assigneeId = @"synthetic";
    CHECK([event.dictionaryRepresentation[@"eventData"][@"assigeeId"] isEqual:@"synthetic"]);
    CHECK(event.dictionaryRepresentation[@"eventData"][@"assigneeId"] == nil);
    CHECK([IntercomNexusEvent eventStringToTypeDictionary].count == 22);
    CHECK([IntercomNexusEvent eventTypeForEventName:@"ConversationTitleGenerated"] == 24);
    CHECK([[IntercomNexusEvent eventNameForEventType:23] isEqual:@"nx.Ping"]);
    CHECK([[IntercomNexusEvent eventNameForEventType:NSUIntegerMax] isEqual:@""]);
    CHECK([[[IntercomNexusEvent alloc] initWithType:0 topics:nil] dictionaryRepresentation] == nil);
    NSData *json = [NSJSONSerialization dataWithJSONObject:@{@"status": @"PASS", @"testCount": @(checks)} options:0 error:nil];
    puts([[NSString alloc] initWithData:json encoding:NSUTF8StringEncoding].UTF8String);
    return 0;
} }
''')
host = output / "event-branch-tests"
run(["xcrun", "clang", "-fobjc-arc", "-fmodules", "-Wall", "-Wextra", "-Werror", "-I", str(package / "ios/Session"), str(package / "ios/Session/IntercomNexusEvent.m"), str(fixture), "-framework", "Foundation", "-o", str(host)])
host_result = run([str(host)])
(output / "host-event-tests.json").write_text(host_result.stdout)
for sdk, arch, target in [("iphoneos", "arm64", "arm64-apple-ios15.0"), ("iphonesimulator", "arm64", "arm64-apple-ios15.0-simulator"), ("iphonesimulator", "x86_64", "x86_64-apple-ios15.0-simulator")]:
    sdk_path = run(["xcrun", "--sdk", sdk, "--show-sdk-path"]).stdout.strip()
    library = output / f"{sdk}-{arch}-Nexus.dylib"
    result = run(["xcrun", "--sdk", sdk, "clang", "-target", target, "-isysroot", sdk_path, "-fobjc-arc", "-fmodules", f"-fmodules-cache-path={output / 'modules'}", "-Wall", "-Wextra", "-Werror", "-dynamiclib", *map(str, sources), "-framework", "Foundation", "-framework", "UIKit", "-framework", "Network", "-o", str(library)])
    (output / f"{sdk}-{arch}-link.log").write_text(result.stdout + result.stderr)
    (output / f"{sdk}-{arch}-dependencies.txt").write_text(run(["xcrun", "otool", "-L", str(library)]).stdout)
    results.append({"target": target, "linked": True, "library": str(library)})
inputs = [*sources, *(p.with_suffix('.h') for p in sources)]
receipt = {"status": "NEXUS_SOURCE_LINKED_RUNTIME_UNVERIFIED", "targets": results, "fixtureImplementationsUsed": False, "originalSDKCompileOrLinkInput": False, "sessionsCreatedByValidation": 0, "runtimeNetworkBehaviorVerified": False, "hostEventBranches": json.loads(host_result.stdout), "hostEventFixtureSHA256": hashlib.sha256(fixture.read_bytes()).hexdigest(), "fullSDKLinked": False, "inputs": {str(p.relative_to(package)): hashlib.sha256(p.read_bytes()).hexdigest() for p in inputs}, "commands": commands}
(output / "receipt.json").write_text(json.dumps(receipt, indent=2) + "\n")
print("PASS: genuine Nexus Client/Connection/Socket/Event/Configuration strict source link for three iOS15 targets; no sessions created")
print(output / "receipt.json")
