import React, { useState } from 'react';
import { View, Text, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { CloudKit } from '@onekeyfe/react-native-cloud-kit-module';
import type { SaveRecordParams, FetchRecordParams, DeleteRecordParams, RecordExistsParams, QueryRecordsParams } from '@onekeyfe/react-native-cloud-kit-module';

interface CloudKitTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function CloudKitTestPage({ onGoHome, safeAreaInsets }: CloudKitTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  
  // Form inputs
  const [recordType, setRecordType] = useState('TestRecord');
  const [recordID, setRecordID] = useState('test-record-1');
  const [recordData, setRecordData] = useState('{"name": "Test", "value": 123}');
  const [recordMeta, setRecordMeta] = useState('{"created": "2024-01-01"}');

  const executeCloudKitOperation = async (operation: () => Promise<any>, operationName: string) => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await operation();
      setResult(res);
      Alert.alert('Success', `${operationName} completed successfully`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `${operationName} failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const checkAvailability = () => {
    executeCloudKitOperation(() => CloudKit.isAvailable(), 'Availability Check');
  };

  const getAccountInfo = () => {
    executeCloudKitOperation(() => CloudKit.getAccountInfo(), 'Get Account Info');
  };

  const saveRecord = () => {
    if (!recordType.trim() || !recordID.trim()) {
      Alert.alert('Error', 'Record type and ID are required');
      return;
    }

    const params: SaveRecordParams = {
      recordType: recordType.trim(),
      recordID: recordID.trim(),
      data: recordData.trim(),
      meta: recordMeta.trim(),
    };

    executeCloudKitOperation(() => CloudKit.saveRecord(params), 'Save Record');
  };

  const fetchRecord = () => {
    if (!recordType.trim() || !recordID.trim()) {
      Alert.alert('Error', 'Record type and ID are required');
      return;
    }

    const params: FetchRecordParams = {
      recordType: recordType.trim(),
      recordID: recordID.trim(),
    };

    executeCloudKitOperation(() => CloudKit.fetchRecord(params), 'Fetch Record');
  };

  const deleteRecord = () => {
    if (!recordType.trim() || !recordID.trim()) {
      Alert.alert('Error', 'Record type and ID are required');
      return;
    }

    const params: DeleteRecordParams = {
      recordType: recordType.trim(),
      recordID: recordID.trim(),
    };

    executeCloudKitOperation(() => CloudKit.deleteRecord(params), 'Delete Record');
  };

  const checkRecordExists = () => {
    if (!recordType.trim() || !recordID.trim()) {
      Alert.alert('Error', 'Record type and ID are required');
      return;
    }

    const params: RecordExistsParams = {
      recordType: recordType.trim(),
      recordID: recordID.trim(),
    };

    executeCloudKitOperation(() => CloudKit.recordExists(params), 'Check Record Exists');
  };

  const queryRecords = () => {
    if (!recordType.trim()) {
      Alert.alert('Error', 'Record type is required');
      return;
    }

    const params: QueryRecordsParams = {
      recordType: recordType.trim(),
    };

    executeCloudKitOperation(() => CloudKit.queryRecords(params), 'Query Records');
  };

  const resetForm = () => {
    setResult(null);
    setError(null);
    setRecordType('TestRecord');
    setRecordID('test-record-1');
    setRecordData('{"name": "Test", "value": 123}');
    setRecordMeta('{"created": "2024-01-01"}');
  };

  return (
    <TestPageBase title="CloudKit Module Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      {/* Availability and Account */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          CloudKit Status
        </Text>
        
        <TestButton
          title={isLoading ? 'Checking...' : 'Check CloudKit Availability'}
          onPress={checkAvailability}
          disabled={isLoading}
        />
        
        <TestButton
          title={isLoading ? 'Getting...' : 'Get Account Info'}
          onPress={getAccountInfo}
          disabled={isLoading}
          style={{ marginTop: 10 }}
        />
      </View>

      {/* Record Operations */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Record Operations
        </Text>
        
        <TestInput
          placeholder="Record Type (e.g., TestRecord)"
          value={recordType}
          onChangeText={setRecordType}
        />
        
        <TestInput
          placeholder="Record ID (e.g., test-record-1)"
          value={recordID}
          onChangeText={setRecordID}
        />
        
        <TestInput
          placeholder="Record Data (JSON string)"
          value={recordData}
          onChangeText={setRecordData}
          multiline
        />
        
        <TestInput
          placeholder="Record Meta (JSON string)"
          value={recordMeta}
          onChangeText={setRecordMeta}
          multiline
        />
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Save Record"
            onPress={saveRecord}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
          <TestButton
            title="Fetch Record"
            onPress={fetchRecord}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
        </View>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Delete Record"
            onPress={deleteRecord}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120, backgroundColor: '#ff3b30' }}
          />
          <TestButton
            title="Record Exists?"
            onPress={checkRecordExists}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120 }}
          />
        </View>
        
        <TestButton
          title="Query All Records"
          onPress={queryRecords}
          disabled={isLoading}
          style={{ marginTop: 10 }}
        />
        
        <TestButton
          title="Reset Form"
          onPress={resetForm}
          disabled={isLoading}
          style={{ marginTop: 10, backgroundColor: '#ff9500' }}
        />
      </View>

      <TestResult result={result} error={error} />

      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>CloudKit Operations:</Text>{'\n'}
          • <Text style={{ fontWeight: '500' }}>Check Availability:</Text> Test if CloudKit is available{'\n'}
          • <Text style={{ fontWeight: '500' }}>Account Info:</Text> Get current iCloud account status{'\n'}
          • <Text style={{ fontWeight: '500' }}>Save Record:</Text> Create or update a record{'\n'}
          • <Text style={{ fontWeight: '500' }}>Fetch Record:</Text> Retrieve a specific record{'\n'}
          • <Text style={{ fontWeight: '500' }}>Delete Record:</Text> Remove a record{'\n'}
          • <Text style={{ fontWeight: '500' }}>Record Exists:</Text> Check if a record exists{'\n'}
          • <Text style={{ fontWeight: '500' }}>Query Records:</Text> Get all records of a type{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Note:</Text> Requires signed-in iCloud account and CloudKit entitlements.
        </Text>
      </View>
    </TestPageBase>
  );
}
