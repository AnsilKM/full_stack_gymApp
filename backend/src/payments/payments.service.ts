import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Payment } from '@prisma/client';

@Injectable()
export class PaymentsService {
  constructor(private prisma: PrismaService) {}

  async create(data: any): Promise<Payment> {
    const { memberId, gymId, planId, ...rest } = data;
    return this.prisma.payment.create({
      data: {
        ...rest,
        member: { connect: { id: memberId } },
        gym: { connect: { id: gymId } },
        plan: { connect: { id: planId } },
      },
    });
  }

  async findAll(gymId: string): Promise<Payment[]> {
    return this.prisma.payment.findMany({
      where: { gymId },
      include: { member: true, plan: true },
      orderBy: { date: 'desc' },
    });
  }
}
