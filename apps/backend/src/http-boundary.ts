import { ArgumentsHost, Catch, ExceptionFilter, HttpException } from "@nestjs/common";
import { Request, Response } from "express";
import { randomUUID } from "node:crypto";

const codes: Record<number, string> = {
  400: "INVALID_REQUEST", 401: "UNAUTHORIZED", 403: "FORBIDDEN",
  404: "CONTENT_NOT_FOUND", 429: "RATE_LIMITED", 503: "SERVICE_UNAVAILABLE",
};
const messages: Record<number, string> = {
  400: "Parâmetros inválidos.", 401: "Autenticação necessária.", 403: "Acesso não permitido.",
  404: "Recurso não encontrado.", 429: "Muitas solicitações. Aguarde e tente novamente.",
  503: "Serviço temporariamente indisponível.",
};
@Catch()
export class SafeErrorFilter implements ExceptionFilter {
  catch(error: unknown, host: ArgumentsHost) {
    const response = host.switchToHttp().getResponse<Response>();
    const status = error instanceof HttpException ? error.getStatus() : 500;
    response.status(status).json({
      code: codes[status] || "INTERNAL_ERROR",
      message: messages[status] || "Serviço temporariamente indisponível.",
      requestId: response.getHeader("X-Request-Id") || randomUUID(),
    });
  }
}
export function requestLog(request: Request, response: Response, next: () => void) {
  const supplied = request.header("X-Request-Id");
  const requestId = supplied && /^[a-zA-Z0-9-]{8,64}$/.test(supplied) ? supplied : randomUUID();
  response.setHeader("X-Request-Id", requestId);
  const started = performance.now();
  response.on("finish", () => {
    process.stdout.write(JSON.stringify({
      event: "http_request", requestId, method: request.method,
      path: request.route?.path ?? "unmatched", status: response.statusCode,
      durationMs: Math.round(performance.now() - started),
    }) + "\n");
  });
  next();
}
