import {
  NodeRangeDownloadCode,
  NodeRangeDownloadError,
  classifyNodeError as classifyRangeError,
  downloadNodeFile as downloadRangeFile,
} from '@onekeyfe/react-native-range-downloader/node';
import type {
  INodeDownloadOptions,
  IUpdateResponse,
  TNodeRangeDownloadCode,
} from '@onekeyfe/react-native-range-downloader/node';

import {
  EUpdateErrorCode,
  createUpdateError,
  getUpdateError,
  normalizeUpdateErrorDTO,
} from './errors.js';

export { stripSensitiveHeaders } from '@onekeyfe/react-native-range-downloader/node';
export type {
  INodeDownloadOptions,
  INodeDownloadProgress,
  IUpdateResponse,
} from '@onekeyfe/react-native-range-downloader/node';

const rangeCodes = new Set<string>(Object.values(NodeRangeDownloadCode));

function isRangeCode(code: string): code is TNodeRangeDownloadCode {
  return rangeCodes.has(code);
}

function throwRangeTransportError(error: unknown): never {
  const dto = getUpdateError(error, 'download');
  if (dto.code === EUpdateErrorCode.unknown) throw error;
  const code = isRangeCode(dto.code) ? dto.code : NodeRangeDownloadCode.unknown;
  throw new NodeRangeDownloadError(code, dto.httpStatus, { cause: error });
}

function wrapTransportResponse(result: IUpdateResponse): IUpdateResponse {
  const { response, url } = result;
  return {
    url,
    response: {
      statusCode: response.statusCode,
      headers: response.headers,
      resume: () => response.resume(),
      destroy: (error) => response.destroy(error),
      async *[Symbol.asyncIterator]() {
        try {
          yield* response;
        } catch (error) {
          throwRangeTransportError(error);
        }
      },
    },
  };
}

export function classifyNodeError(error: unknown) {
  const known = getUpdateError(error, 'download');
  if (known.code !== EUpdateErrorCode.unknown) return createUpdateError(known);

  const { code, httpStatus, cause } = classifyRangeError(error);
  const transportError = getUpdateError(cause, 'download');
  if (transportError.code !== EUpdateErrorCode.unknown) {
    return createUpdateError(transportError);
  }
  return createUpdateError(
    normalizeUpdateErrorDTO({ code, step: 'download', httpStatus }, 'download')
  );
}

export async function downloadNodeFile(options: INodeDownloadOptions) {
  const { transport } = options;
  try {
    return await downloadRangeFile({
      ...options,
      transport: transport
        ? async (url, headers, signal) => {
            try {
              return wrapTransportResponse(await transport(url, headers, signal));
            } catch (error) {
              throwRangeTransportError(error);
            }
          }
        : undefined,
    });
  } catch (error) {
    throw classifyNodeError(error);
  }
}
