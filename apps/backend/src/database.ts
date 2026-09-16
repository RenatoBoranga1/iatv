import { Injectable, OnModuleDestroy, OnModuleInit } from "@nestjs/common";
import { PrismaClient } from "@prisma/client";
@Injectable()
export class Database
  extends PrismaClient
  implements OnModuleInit, OnModuleDestroy
{
  async onModuleInit() {
    if (process.env.DATA_MODE !== "mock") await this.$connect();
  }
  async onModuleDestroy() {
    await this.$disconnect();
  }
}
