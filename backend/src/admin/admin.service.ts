import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import * as bcrypt from 'bcrypt';
import { Role } from '@prisma/client';

@Injectable()
export class AdminService {
  constructor(private prisma: PrismaService) {}

  async createGym(data: { name: string; address: string; phone?: string; ownerId: string }) {
    return this.prisma.gym.create({
      data,
    });
  }

  async createUser(data: { email: string; name: string; password: string; role: Role }) {
    const hashedPassword = await bcrypt.hash(data.password, 10);
    return this.prisma.user.create({
      data: {
        ...data,
        password: hashedPassword,
      },
    });
  }

  async getAllGyms() {
    return this.prisma.gym.findMany({
      include: {
        owner: {
          select: {
            name: true,
            email: true,
          },
        },
      },
    });
  }

  async getAllOwners() {
    return this.prisma.user.findMany({
      where: {
        role: Role.ADMIN,
      },
      select: {
        id: true,
        name: true,
        email: true,
      },
    });
  }
}
