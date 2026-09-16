import {
  ArgumentsHost,
  Catch,
  ExceptionFilter,
  HttpException,
} from "@nestjs/common";
import { Request, Response } from "express";
@Catch()
export class SafeErrorFilter implements ExceptionFilter {
  catch(error: unknown, host: ArgumentsHost) {
    const response = host.switchToHttp().getResponse<Response>();
    const status = error instanceof HttpException ? error.getStatus() : 500;
    const detail =
      error instanceof HttpException
        ? error.getResponse()
        : "Serviço temporariamente indisponível.";
    response.status(status).json({ statusCode: status, error: detail });
  }
}
export function requestLog(
  request: Request,
  response: Response,
  next: () => void,
) {
  const started = Date.now();
  response.on("finish", () => {
    // Route template only: never log queries, bodies, authorization or user IDs.
    process.stdout.write(
      JSON.stringify({
        event: "http_request",
        method: request.method,
        route: request.route?.path ?? "unmatched",
        status: response.statusCode,
        durationMs: Date.now() - started,
      }) + "\n",
    );
  });
  next();
}
