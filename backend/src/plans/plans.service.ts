import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { MembershipPlan, Prisma } from '@prisma/client';

@Injectable()
export class PlansService {
  constructor(private prisma: PrismaService) {}

  async create(data: any): Promise<MembershipPlan> {
    const { gymId, ...rest } = data;
    return this.prisma.membershipPlan.create({
      data: {
        ...rest,
        gym: { connect: { id: gymId } },
      },
    });
  }

  async findAll(gymId: string): Promise<MembershipPlan[]> {
    return this.prisma.membershipPlan.findMany({
      where: { gymId },
    });
  }

  async findOne(id: string): Promise<MembershipPlan | null> {
    return this.prisma.membershipPlan.findUnique({
      where: { id },
    });
  }

  async update(id: string, data: any): Promise<MembershipPlan> {
    const { gymId, ...rest } = data;
    return this.prisma.membershipPlan.update({
      where: { id },
      data: {
        ...rest,
        ...(gymId && { gym: { connect: { id: gymId } } }),
      },
    });
  }
}

