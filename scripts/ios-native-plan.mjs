import { pathToFileURL } from "node:url";

// Keep Pager with NativeList and Image: its test bundle links both pods.
const plans = {
  ui: {
    buildTargets: [],
    testSchemes: [
      "react-native-pager-view-Unit-Tests",
      "react-native-native-list-Unit-Tests",
      "OneKeyImage-Unit-Tests",
    ],
  },
  services: {
    buildTargets: ["ReactNativeLiteCard", "NativeOverlay"],
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
    if (process.argv.length !== 3) throw new Error("invalid_ios_native_arguments");
    console.log(JSON.stringify(getIosNativePlan(process.argv[2])));
  } catch (error) {
    console.error(error.message);
    process.exitCode = 1;
  }
}
