package com.margelo.nitro.reactnativejpush;

import static org.junit.Assert.*;
import cn.jiguang.core.JCoreWireCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;
import org.junit.Test;

public class JPushHandshakeFailureTest {
    @Test
    public void networkFailureIsNotConvertedToSuccess() {
        InputStream broken = new InputStream() {
            @Override public int read() throws IOException {
                throw new IOException("Synthetic connection loss");
            }
        };
        assertThrows(IOException.class, () -> JPushCloudHandshakeTest.performHandshake(broken, new ByteArrayOutputStream()));
    }

    @Test
    public void missingResponseAssertionIsNotSwallowed() {
        assertThrows(AssertionError.class, () -> JPushCloudHandshakeTest.performHandshake(
            new ByteArrayInputStream(new byte[0]), new ByteArrayOutputStream()
        ));
    }

    @Test
    public void rejectedRegistrationFailsTheHandshake() {
        AssertionError failure = assertThrows(AssertionError.class, () -> JPushCloudHandshakeTest.performHandshake(
            lazyReply(() -> JCoreWireCodec.encodeResponseFrame(
                JCoreWireCodec.CMD_REGISTER, JCoreWireCodec.VER_REGISTER, 101L,
                (int) JCoreWireCodec.getLastRegisterSeed(), 42L, 1009, new byte[0]
            )), new ByteArrayOutputStream()
        ));
        assertTrue(failure.getMessage().contains("Register status"));
    }

    @Test
    public void rejectedLoginFailsTheHandshake() {
        AssertionError failure = assertThrows(AssertionError.class, () -> JPushCloudHandshakeTest.performHandshake(
            lazyReply(() -> {
                byte[] registration = JCoreWireCodec.encodeResponseFrame(
                    JCoreWireCodec.CMD_REGISTER, JCoreWireCodec.VER_REGISTER, 101L,
                    (int) JCoreWireCodec.getLastRegisterSeed(), 42L, 0,
                    JCoreWireCodec.buildRegisterResponsePayload(42L, "synthetic_password", "synthetic_registration", "synthetic_device")
                );
                byte[] login = JCoreWireCodec.encodeResponseFrame(
                    JCoreWireCodec.CMD_LOGIN, JCoreWireCodec.VER_LOGIN, 102L, 0, 42L, 1005, new byte[0]
                );
                byte[] replies = new byte[registration.length + login.length];
                System.arraycopy(registration, 0, replies, 0, registration.length);
                System.arraycopy(login, 0, replies, registration.length, login.length);
                return replies;
            }), new ByteArrayOutputStream()
        ));
        assertTrue(failure.getMessage().contains("Login status"));
    }

    private static InputStream lazyReply(Supplier<byte[]> response) {
        return new InputStream() {
            private ByteArrayInputStream input;
            private ByteArrayInputStream input() {
                if (input == null) input = new ByteArrayInputStream(response.get());
                return input;
            }
            @Override public int read() { return input().read(); }
            @Override public int read(byte[] bytes, int offset, int length) { return input().read(bytes, offset, length); }
        };
    }
}
