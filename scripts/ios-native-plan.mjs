import { pathToFileURL } from "node:url";

export const iosNativeGroups = ["lite", "ui", "modules"];

const plans = {
  lite: {
    action: "build",
    buildTargets: ["ReactNativeLiteCard"],
    testSchemes: [],
  },
  ui: {
    action: "test",
    buildTargets: ["NativeOverlay"],
    testSchemes: [
      "react-native-pager-view-Unit-Tests",
      "react-native-native-list-Unit-Tests",
      "OneKeyImage-Unit-Tests",
    ],
  },
  modules: {
    action: "test",
    buildTargets: [],
    testSchemes: [
      "OneKeyTextInput-Unit-Tests",
      "PerpDepthBar-Unit-Tests",
      "ReactNativeBundleCrypto-Unit-Tests",
      "ReactNativeImageCropPicker-Unit-Tests",
      "ReactNativeCaptureProtection-Unit-Tests",
      "ReactNativePhotoLibrary-Unit-Tests",
      "ReactNativeJpush-Unit-Tests",
    ],
  },
};

export function getIosNativePlan(group) {
  if (!Object.hasOwn(plans, group)) throw new Error("invalid_ios_native_group");
  return structuredClone(plans[group]);
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    console.log(JSON.stringify(getIosNativePlan(process.argv[2])));
  } catch {
    console.error("invalid_ios_native_group");
    process.exitCode = 1;
  }
}
