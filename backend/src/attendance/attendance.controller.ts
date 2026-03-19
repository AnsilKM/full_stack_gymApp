import { Controller, Get, Post, Body, Param, Query } from '@nestjs/common';
import { AttendanceService } from './attendance.service';

@Controller('attendance')
export class AttendanceController {
  constructor(private readonly attendanceService: AttendanceService) {}

  @Post('checkin')
  checkIn(@Body() body: any) {
    return this.attendanceService.checkIn(body);
  }

  @Get('today')
  getToday(@Query('gymId') gymId: string) {
    return this.attendanceService.getToday(gymId);
  }

  @Get('history/:memberId')
  getHistory(@Param('memberId') memberId: string) {
    return this.attendanceService.getHistory(memberId);
  }
}
