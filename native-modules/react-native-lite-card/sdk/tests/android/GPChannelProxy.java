package org.haobtc.onekey.card.gpchannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

// Test-only line protocol. Inputs are synthetic; output is consumed by verify.py.
public class GPChannelProxy {
    public static void main(String[] args) throws Exception {
        System.load(args[0]);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = input.readLine()) != null) {
            JSONObject request = new JSONObject(line);
            JSONObject response = new JSONObject();
            String operation = request.getString("operation");
            String data = request.optString("data", "");
            String result = null;
            int code = 0;
            switch (operation) {
                case "init": code = GPChannelNatives.nativeGPCInitialize(request.getJSONObject("info").toString()); break;
                case "finalize": code = GPChannelNatives.nativeGPCFinalize(); break;
                case "open": code = GPChannelNatives.nativeGPCOpenSecureChannel(data); break;
                case "mutual": result = GPChannelNatives.nativeGPCBuildMutualAuthData(); break;
                case "build": result = GPChannelNatives.nativeGPCBuildAPDU(request.getLong("cla"),request.getLong("ins"),request.getLong("p1"),request.getLong("p2"),data); break;
                case "safe": result = GPChannelNatives.nativeGPCBuildSafeAPDU(request.getLong("cla"),request.getLong("ins"),request.getLong("p1"),request.getLong("p2"),data); break;
                case "parse": result = GPChannelNatives.nativeGPCParseAPDUResponse(data); break;
                case "parseSafe": result = GPChannelNatives.nativeGPCParseSafeAPDUResponse(data); break;
                case "tlv": result = GPChannelNatives.nativeGPCTLVDecode(data); break;
                case "certificate": result = GPChannelNatives.nativeGPCParseCertificate(data); break;
                default: throw new IllegalArgumentException("Unknown operation");
            }
            if (!operation.equals("init") && !operation.equals("finalize") && !operation.equals("open") && result == null)
                code = GPChannelNatives.nativeGetErrorCode();
            response.put("code", code);
            response.put("output", result == null ? JSONObject.NULL : result);
            System.out.println(response.toString());
            System.out.flush();
        }
    }
}

class GPChannelNatives {
    static native int nativeGetErrorCode();
    static native int nativeGPCInitialize(String input);
    static native int nativeGPCFinalize();
    static native String nativeGPCBuildMutualAuthData();
    static native int nativeGPCOpenSecureChannel(String input);
    static native String nativeGPCBuildAPDU(long cla,long ins,long p1,long p2,String input);
    static native String nativeGPCBuildSafeAPDU(long cla,long ins,long p1,long p2,String input);
    static native String nativeGPCParseSafeAPDUResponse(String input);
    static native String nativeGPCParseAPDUResponse(String input);
    static native String nativeGPCTLVDecode(String input);
    static native String nativeGPCParseCertificate(String input);
}
