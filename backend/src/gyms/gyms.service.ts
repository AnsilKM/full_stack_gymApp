import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Gym, Prisma } from '@prisma/client';

@Injectable()
export class GymsService {
  constructor(private prisma: PrismaService) {}

  async create(data: Prisma.GymCreateWithoutOwnerInput, ownerId: string): Promise<Gym> {
    return this.prisma.gym.create({
      data: {
        ...data,
        owner: { connect: { id: ownerId } },
      },
    });
  }

  async findAll(): Promise<Gym[]> {
    return this.prisma.gym.findMany({
      include: { owner: true, _count: { select: { members: true } } },
    });
  }

  async findOne(id: string): Promise<Gym | null> {
    return this.prisma.gym.findUnique({
      where: { id },
      include: {
        owner: true,
        plans: true,
        _count: { select: { members: true } },
      },
    });
  }

  async findByOwner(ownerId: string): Promise<Gym[]> {
    return this.prisma.gym.findMany({
      where: { ownerId },
      include: { _count: { select: { members: true } } },
    });
  }

  async findAssigned(userId: string): Promise<Gym[]> {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      select: { branchGymId: true } as any,
    }) as any;

    if (!user?.branchGymId) {
      return [];
    }

    return this.prisma.gym.findMany({
      where: { id: user.branchGymId },
      include: { _count: { select: { members: true } } },
    });
  }
}
