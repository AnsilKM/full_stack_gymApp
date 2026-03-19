import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class ReportsService {
  constructor(private prisma: PrismaService) {}

  async getDashboardStats(gymId: string) {
    const totalMembers = await this.prisma.member.count({ where: { gymId } });
    const activeMembers = await this.prisma.member.count({
      where: { gymId, status: 'ACTIVE' },
    });
    const expiredMembers = await this.prisma.member.count({
      where: { gymId, status: 'EXPIRED' },
    });

    const revenue = await this.prisma.payment.aggregate({
      where: { gymId },
      _sum: { amount: true },
    });

    const todayAttendance = await this.prisma.attendance.count({
      where: {
        gymId,
        date: { gte: new Date(new Date().setHours(0, 0, 0, 0)) },
      },
    });

    return {
      totalMembers,
      activeMembers,
      expiredMembers,
      totalRevenue: revenue._sum.amount || 0,
      todayAttendance,
    };
  }
}
