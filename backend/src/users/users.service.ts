import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Prisma, User } from '@prisma/client';
import * as bcrypt from 'bcrypt';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async create(data: Prisma.UserCreateInput): Promise<User> {
    const hashedPassword = await bcrypt.hash(data.password, 10);
    return this.prisma.user.create({
      data: {
        ...data,
        password: hashedPassword,
      },
    });
  }

  async findByEmail(email: string): Promise<User | null> {
    return this.prisma.user.findUnique({
      where: { email },
    });
  }

  async findById(id: string): Promise<User | null> {
    return this.prisma.user.findUnique({
      where: { id },
    });
  }

  async registerWithOwnerGym(data: any, hashedPassword: string) {
    return this.prisma.user.create({
      data: {
        email: data.email,
        password: hashedPassword,
        name: data.ownerName,
        role: 'ADMIN',
        gyms: {
          create: {
            name: data.gymName,
            address: data.address || '',
            phone: data.phone || '',
          }
        }
      },
      include: {
        gyms: true
      }
    });
  }
}


