import NativeTcpSocket from './NativeTcpSocket';

// Compatibility shim that mimics the react-native-tcp-socket createConnection API
// but uses a simple Promise underneath (works in background Hermes runtime)
export interface TcpSocketShim {
  on(event: 'error', handler: (err: Error) => void): this;
  on(event: 'timeout', handler: () => void): this;
  destroy(): void;
}

function createConnection(
  options: { host: string; port: number; timeout?: number },
  connectCallback: () => void,
): TcpSocketShim {
  const timeout = options.timeout ?? 5000;
  const handlers: { error?: (err: Error) => void; timeout?: () => void } = {};

  let destroyed = false;

  NativeTcpSocket.connectWithTimeout(options.host, options.port, timeout)
    .then(() => {
      if (!destroyed) connectCallback();
    })
    .catch((err: unknown) => {
      if (!destroyed) {
        const errMsg =
          err instanceof Error ? err.message : String(err as string);
        if (
          errMsg.includes('timeout') ||
          errMsg.includes('ETIMEDOUT') ||
          errMsg.includes('Connection timeout')
        ) {
          handlers.timeout?.();
        } else if (handlers.error) {
          handlers.error(err instanceof Error ? err : new Error(errMsg));
        }
      }
    });

  const shim = {
    on(event: string, handler: (err?: Error) => void) {
      if (event === 'error') {
        handlers.error = handler as (err: Error) => void;
      } else if (event === 'timeout') {
        handlers.timeout = handler as () => void;
      }
      return shim as unknown as TcpSocketShim;
    },
    destroy() {
      destroyed = true;
    },
  } as unknown as TcpSocketShim;
  return shim;
}

export default { createConnection };
export { NativeTcpSocket };
