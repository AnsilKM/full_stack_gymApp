import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { MembershipPlan, Prisma } from '@prisma/client';

@Injectable()
export class PlansService {
  constructor(private prisma: PrismaService) {}

  async create(data: any): Promise<MembershipPlan> {
    const { gymId, id, ...rest } = data;
    const createData: any = {
      ...rest,
      gym: { connect: { id: gymId } },
    };
    
    // Only include ID if it's not empty, otherwise let Prisma generate it
    if (id && id.trim() !== '') {
      createData.id = id;
    }

    return this.prisma.membershipPlan.create({
      data: createData,
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

  async remove(id: string): Promise<MembershipPlan> {
    return this.prisma.membershipPlan.delete({
      where: { id },
    });
  }
}

