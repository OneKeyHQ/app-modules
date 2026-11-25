import { pathToFileURL } from "node:url";

const plans = {
  ui: {
    buildTargets: {},
    testSchemes: {
      "react-native-pager-view": "react-native-pager-view-Unit-Tests",
      "react-native-native-list": "react-native-native-list-Unit-Tests",
      "react-native-image": "OneKeyImage-Unit-Tests",
      "react-native-bottom-tabs": "react-native-bottom-tabs-Unit-Tests",
    },
  },
  services: {
    buildTargets: {
      "react-native-lite-card": "ReactNativeLiteCard",
      "react-native-native-overlay": "NativeOverlay",
      "react-native-auto-size-input": "AutoSizeInput",
      "react-native-network-throttle": "ReactNativeNetworkThrottle",
    },
    testSchemes: {
      "react-native-text-input": "OneKeyTextInput-Unit-Tests",
      "react-native-perp-depth-bar": "PerpDepthBar-Unit-Tests",
      "react-native-bundle-crypto": "ReactNativeBundleCrypto-Unit-Tests",
      "react-native-image-crop-picker": "ReactNativeImageCropPicker-Unit-Tests",
      "react-native-capture-protection": "ReactNativeCaptureProtection-Unit-Tests",
      "react-native-device-utils": "ReactNativeDeviceUtils-Unit-Tests",
      "react-native-photo-library": "ReactNativePhotoLibrary-Unit-Tests",
      "react-native-jpush": "ReactNativeJpush-Unit-Tests",
      "react-native-qr-scanner": "QrScanner-Unit-Tests",
    },
  },
};

export const iosNativeModules = Object.values(plans).flatMap(({ buildTargets, testSchemes }) =>
  [...Object.keys(buildTargets), ...Object.keys(testSchemes)],
);

export function getIosNativePlan(group, modules) {
  if (!Object.hasOwn(plans, group)) throw new Error("invalid_ios_native_group");
  const select = (targets) => Object.entries(targets)
    .filter(([module]) => modules === undefined || modules.has(module))
    .map(([, target]) => target);
  return {
    buildTargets: select(plans[group].buildTargets),
    testSchemes: select(plans[group].testSchemes),
  };
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
