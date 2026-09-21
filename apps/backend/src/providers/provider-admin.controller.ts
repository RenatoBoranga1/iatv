import { Controller, Get, Header, Headers, UnauthorizedException } from "@nestjs/common";
import { createHash, timingSafeEqual } from "node:crypto";
import { BrasilTvProvider } from "./brasiltv/brasil-tv.provider";

/** Bootstrap operations credential; not a substitute for future account/role authentication. */
export function requireProviderAdmin(authorization?: string) {
  const expected = process.env.PROVIDER_ADMIN_TOKEN;
  if (!expected || expected.length < 32 || !authorization?.startsWith("Bearer ") || authorization.length > 1024) throw new UnauthorizedException();
  const digest = (value: string) => createHash("sha256").update(value).digest();
  if (!timingSafeEqual(digest(authorization.slice(7)), digest(expected))) throw new UnauthorizedException();
}
@Controller("v1/admin/providers/brasiltv")
export class ProviderAdminController {
  @Get("health") @Header("Cache-Control", "no-store") health(@Headers("authorization") authorization?: string) {
    requireProviderAdmin(authorization);
    return new BrasilTvProvider().health();
  }
}
