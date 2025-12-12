import { Text, View, StyleSheet } from 'react-native';
import { checkBiometricAuthChanged } from '@onekeyfe/react-native-check-biometric-auth-changed';
import { useEffect, useState } from 'react';


export default function App() {
  const [result, setResult] = useState<boolean>(false);

  // useEffect(() => {
  //   checkBiometricAuthChanged().then((result) => {
  //     setResult(result);
  //   });
  // }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.text}>Result: {result ? 'true' : 'false'}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'green',
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 20,
    fontWeight: 'bold',
    color: 'red',
  },
});
