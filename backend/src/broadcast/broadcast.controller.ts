import { Controller, Post, Body, Get, Query } from '@nestjs/common';
import { BroadcastService } from './broadcast.service';

@Controller('broadcast')
export class BroadcastController {
  constructor(private readonly broadcastService: BroadcastService) {}

  @Post('send')
  async send(@Body() body: { gymId: string, message: String }) {
    return this.broadcastService.broadcastToAllMembers(body.gymId, body.message);
  }

  @Get('logs')
  async getLogs(@Query('gymId') gymId: string) {
    return this.broadcastService.getBroadcastLogs(gymId);
  }
}
