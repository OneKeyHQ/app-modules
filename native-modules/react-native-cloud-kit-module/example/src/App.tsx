import { Button, ScrollView , Text, StyleSheet } from 'react-native';
import { CloudKit } from 'react-native-cloud-kit-module';
import { useEffect, useState } from 'react';

export default function App() {
  const [status, setStatus] = useState<string>('');
  const [accountInfo, setAccountInfo] = useState<string>('');
  const [recordResult, setRecordResult] = useState<string>('');

  useEffect(() => {
    checkAvailability();
  }, []);

  const checkAvailability = async () => {
    try {
      const available = await CloudKit.isAvailable();
      setStatus(`CloudKit Available: ${available}`);
    } catch (error) {
      setStatus(`Error: ${error}`);
    }
  };

  const testGetAccountInfo = async () => {
    try {
      const info = await CloudKit.getAccountInfo();
      setAccountInfo(JSON.stringify(info, null, 2));
    } catch (error) {
      setAccountInfo(`Error: ${error}`);
    }
  };

  const testSaveRecord = async () => {
    try {
      const result = await CloudKit.saveRecord({
        recordType: 'TestRecord',
        recordID: 'test-record-1',
        data: JSON.stringify({ message: 'Hello CloudKit' }),
        meta: JSON.stringify({ version: 1 }),
      });
      setRecordResult(`Saved: ${JSON.stringify(result, null, 2)}`);
    } catch (error) {
      setRecordResult(`Save Error: ${error}`);
    }
  };

  const testFetchRecord = async () => {
    try {
      const result = await CloudKit.fetchRecord({
        recordType: 'TestRecord',
        recordID: 'test-record-1',
      });
      setRecordResult(`Fetched: ${JSON.stringify(result, null, 2)}`);
    } catch (error) {
      setRecordResult(`Fetch Error: ${error}`);
    }
  };

  const testRecordExists = async () => {
    try {
      const exists = await CloudKit.recordExists({
        recordType: 'TestRecord',
        recordID: 'test-record-1',
      });
      setRecordResult(`Record Exists: ${exists}`);
    } catch (error) {
      setRecordResult(`Exists Error: ${error}`);
    }
  };

  const testQueryRecords = async () => {
    try {
      const result = await CloudKit.queryRecords({
        recordType: 'TestRecord',
      });
      setRecordResult(`Query: ${JSON.stringify(result, null, 2)}`);
    } catch (error) {
      setRecordResult(`Query Error: ${error}`);
    }
  };

  const testDeleteRecord = async () => {
    try {
      const result = await CloudKit.deleteRecord({
        recordType: 'TestRecord',
        recordID: 'test-record-1',
      });
      setRecordResult(`Deleted: ${result}`);
    } catch (error) {
      setRecordResult(`Delete Error: ${error}`);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>CloudKit Test App</Text>
      
      <Text style={styles.section}>Status:</Text>
      <Text style={styles.result}>{status}</Text>
      
      <Button title="Get Account Info" onPress={testGetAccountInfo} />
      <Text style={styles.result}>{accountInfo}</Text>
      
      <Button title="Save Record" onPress={testSaveRecord} />
      <Button title="Fetch Record" onPress={testFetchRecord} />
      <Button title="Record Exists" onPress={testRecordExists} />
      <Button title="Query Records" onPress={testQueryRecords} />
      <Button title="Delete Record" onPress={testDeleteRecord} />
      
      <Text style={styles.section}>Record Result:</Text>
      <Text style={styles.result}>{recordResult}</Text>
    </ScrollView>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  content: {
    padding: 20,
    gap: 10,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  section: {
    fontSize: 18,
    fontWeight: '600',
    marginTop: 10,
  },
  result: {
    fontSize: 14,
    fontFamily: 'monospace',
    backgroundColor: '#f0f0f0',
    padding: 10,
    marginVertical: 5,
  },
});