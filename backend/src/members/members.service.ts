import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Member, Prisma, Role } from '@prisma/client';

@Injectable()
export class MembersService {
  constructor(private prisma: PrismaService) {}

  async create(data: any): Promise<Member> {
    const { gymId, name, email, phone, bloodGroup, photoUrl, planId, ...rest } = data;
    
    // Create member directly (no more User link for members)
    return this.prisma.member.create({
      data: {
        ...rest,
        name,
        email,
        phone,
        bloodGroup,
        photoUrl,
        gym: { connect: { id: gymId } },
      },
    });
  }

  async findAll(user: any, gymId?: string): Promise<Member[]> {
    const where: any = {};

    // Logic for Role-based filtering
    if (user.role === 'GYM') {
      // Gym accounts can only see their own branch
      const dbUser = await this.prisma.user.findUnique({
        where: { id: user.id },
        select: { branchGymId: true } as any,
      }) as any;
      
      if (!dbUser?.branchGymId) {
        return [];
      }
      where.gymId = dbUser.branchGymId;
    } else if (user.role === 'ADMIN') {
      // Admin sees everything, or a specific gym if requested
      if (gymId) {
        where.gymId = gymId;
      }
    } else {
      return [];
    }

    return this.prisma.member.findMany({
      where,
      include: { gym: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async findOne(id: string): Promise<Member | null> {
    return this.prisma.member.findUnique({
      where: { id },
      include: { gym: true, attendance: true, payments: true },
    });
  }

  async update(id: string, data: any): Promise<Member> {
    return this.prisma.member.update({
      where: { id },
      data,
    });
  }

  async renew(id: string, months: number): Promise<Member> {
    const member = await this.prisma.member.findUnique({ where: { id } });
    if (!member) throw new Error('Member not found');

    const currentExpiry = member.expiryDate || new Date();
    const newExpiry = new Date(currentExpiry);
    newExpiry.setMonth(newExpiry.getMonth() + months);

    return this.prisma.member.update({
      where: { id },
      data: { 
        expiryDate: newExpiry,
        status: 'ACTIVE'
      },
    });
  }

  async remove(id: string): Promise<Member> {
    return this.prisma.member.delete({
      where: { id },
    });
  }
}
