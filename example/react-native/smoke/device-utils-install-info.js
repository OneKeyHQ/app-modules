import React, { useEffect, useState } from 'react';
import { AppRegistry, Text } from 'react-native';
import { ReactNativeDeviceUtils } from '@onekeyfe/react-native-device-utils';
import { name as appName } from '../app.json';

function InstallInfoSmokeTest() {
  const [result, setResult] = useState('pending');

  useEffect(() => {
    async function run() {
      try {
        const [installer, time] = await Promise.all([
          ReactNativeDeviceUtils.getInstallerPackageNameAsync(),
          ReactNativeDeviceUtils.getInstallationTime(),
        ]);
        if (
          typeof installer !== 'string' ||
          !Number.isSafeInteger(time) ||
          time <= 0
        ) {
          throw new Error(
            'Expected a string installer and a positive integer timestamp',
          );
        }
        setResult(`${installer} ${time}`);
      } catch (error) {
        setResult(`failed: ${String(error)}`);
      }
    }
    void run();
  }, []);

  const marker = `DeviceUtilsInstallInfo: ${result}`;
  return (
    <Text accessibilityLabel={marker} style={{ margin: 48 }}>
      {marker}
    </Text>
  );
}

AppRegistry.registerComponent(appName, () => InstallInfoSmokeTest);
