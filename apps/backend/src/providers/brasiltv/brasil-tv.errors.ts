import { HttpException } from "@nestjs/common";
export const providerErrorCodes = [
  "PROVIDER_AUTH_FAILED", "PROVIDER_UNAVAILABLE", "PROVIDER_RESPONSE_INVALID",
  "DEVICE_REJECTED", "DEVICE_LIMIT_REACHED", "ENTITLEMENT_REQUIRED",
  "CONTENT_NOT_AVAILABLE", "GEO_RESTRICTED", "BROKER_UNAVAILABLE",
  "STREAM_RESOLUTION_FAILED", "PLAYBACK_SOURCE_UNAVAILABLE", "PLAYBACK_TOKEN_EXPIRED",
] as const;
export type ProviderErrorCode = typeof providerErrorCodes[number];
/** No upstream response, URL, credentials or exception message crosses this boundary. */
export class ProviderError extends HttpException {
  constructor(readonly code: ProviderErrorCode) {
    super("Provedor temporariamente indisponível.", 503);
  }
}
