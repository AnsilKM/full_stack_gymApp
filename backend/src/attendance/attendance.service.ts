import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Attendance } from '@prisma/client';

@Injectable()
export class AttendanceService {
  constructor(private prisma: PrismaService) {}

  async checkIn(data: any): Promise<Attendance> {
    const { memberId, gymId } = data;
    return this.prisma.attendance.create({
      data: {
        member: { connect: { id: memberId } },
        gym: { connect: { id: gymId } },
      },
    });
  }

  async getToday(gymId: string): Promise<Attendance[]> {
    const startOfDay = new Date();
    startOfDay.setHours(0, 0, 0, 0);

    return this.prisma.attendance.findMany({
      where: {
        gymId,
        date: { gte: startOfDay },
      },
      include: {
        member: true,
      },
      orderBy: { checkInTime: 'desc' },
    });
  }

  async getHistory(memberId: string): Promise<Attendance[]> {
    return this.prisma.attendance.findMany({
      where: { memberId },
      orderBy: { date: 'desc' },
    });
  }
}
