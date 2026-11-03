### GPC SCP11c Secure Channel
> Our customers:
> - Bixin

---

## The following device operation related interfaces are exported:
> - JUB_FreeMemory
> - JUB_GPC_BuildAPDU & JUB_GPC_ParseAPDUResponse
> - JUB_GPC_Initialize & JUB_GPC_Finalize
> - JUB_GPC_BuildMutualAuthData
> - JUB_GPC_OpenSecureChannel
> - JUB_GPC_BuildSafeAPDU & JUB_GPC_ParseSafeAPDUResponse

---

## Demo in C
See "test/GPChannelSDK_test_scp11.cpp", function scp11_process_sample().
#### 0. prepare:
**JUB_GPC_ParseCertificate** is called for parse a card's certificate, return it's 'Certificate Serial Number' and 'Subject Identifier' (in hex).
You must check that the device serial number is the same as it's certificate serial number, if yes, go ahead, using 'Subject Identifier' as GPC_SCP11_SHAREDINFO.cardGroupID.
#### 1. Initialize: 
**JUB_GPC_Initialize()** is called when it is ready to start the secure channel.
#### 2. PerformSecurityOperation:  '80 2A 18 10'
Using **JUB_GPC_BuildAPDU()** to build 'PerformSecurityOperation' APDU and sends the APDU to the device.
#### 3. MutualAuthenticate: 80 82 18 15
Using **JUB_GPC_BuildMutualAuthData()** to build 'MutualAuthenticate' APDU data, then using **JUB_GPC_BuildAPDU()** to build 'MutualAuthenticate' APDU, finally, sends the APDU to the device.
#### 4. OpenSecureChannel:
Call **JUB_GPC_OpenSecureChannel()**, the parameter is the 'MutualAuthenticate' APDU's response.
#### 5. Secure channel APDU:
Now, the secure channel is open, and the safe APDU can be sent. Non-ciphertext APDU can be interspersed between ciphertext APDUs in the channel.
Call **JUB_GPC_BuildSafeAPDU()** to build Safe APDU, and call **JUB_GPC_ParseSafeAPDUResponse()** to parse the Safe APDU response.
 >**Pay special attention:
**JUB_GPC_BuildSafeAPDU()** and **JUB_GPC_ParseSafeAPDUResponse()** **MUST** be called in pairs.**

#### 6. Finalize:
**JUB_GPC_Finalize()** must be called when
- the channel is ready to be closed,
- or when the NFC device is disconnected,
- or after '00 A4 04 00',
and then **MUST** reopen the secure channel if you want to use it again.
