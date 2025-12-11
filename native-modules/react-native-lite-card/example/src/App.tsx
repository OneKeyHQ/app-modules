import {View, StyleSheet, Button, ScrollView, Alert } from 'react-native';
import onekeyLite from '@onekeyfe/react-native-lite-card';

export default function App() {
  const handleGetLiteInfo = () => {
    onekeyLite.getLiteInfo().then((result) => {
      Alert.alert('getLiteInfo', JSON.stringify(result, null, 2));
    });
  };

  const handleCheckNFCPermission = () => {
    onekeyLite.checkNFCPermission().then((result) => {
      Alert.alert('checkNFCPermission', JSON.stringify(result, null, 2));
    });
  };

  const handleSetMnemonic = () => {
    const mnemonic = 'abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about';
    const pwd = '123456';
    const overwrite = false;
    onekeyLite.setMnemonic(mnemonic, pwd, overwrite).then((result) => {
      Alert.alert('setMnemonic', JSON.stringify(result, null, 2));
    });
  };

  const handleGetMnemonicWithPin = () => {
    const pwd = '123456';
    onekeyLite.getMnemonicWithPin(pwd).then((result) => {
      Alert.alert('getMnemonicWithPin', JSON.stringify(result, null, 2));
    });
  };

  const handleChangePin = () => {
    const oldPin = '123456';
    const newPin = '654321';
    onekeyLite.changePin(oldPin, newPin).then((result) => {
      Alert.alert('changePin', JSON.stringify(result, null, 2));
    });
  };

  const handleReset = () => {
    onekeyLite.reset().then((result) => {
      Alert.alert('reset', JSON.stringify(result, null, 2));
    });
  };

  const handleCancel = () => {
    onekeyLite.cancel();
    Alert.alert('cancel', 'Cancel called');
  };

  const handleIntoSetting = () => {
    onekeyLite.intoSetting();
    Alert.alert('intoSetting', 'Opening settings');
  };

  return (
    <ScrollView style={styles.container}>
      <View style={styles.buttonContainer}>
        <Button title="Get Lite Info" onPress={handleGetLiteInfo} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Check NFC Permission" onPress={handleCheckNFCPermission} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Set Mnemonic" onPress={handleSetMnemonic} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Get Mnemonic With Pin" onPress={handleGetMnemonicWithPin} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Change Pin" onPress={handleChangePin} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Reset" onPress={handleReset} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Cancel" onPress={handleCancel} />
      </View>
      <View style={styles.buttonContainer}>
        <Button title="Into Setting" onPress={handleIntoSetting} />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  buttonContainer: {
    marginVertical: 10,
  },
});
