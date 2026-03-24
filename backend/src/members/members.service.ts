import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { Member, Prisma, Role } from '@prisma/client';

@Injectable()
export class MembersService {
  constructor(private prisma: PrismaService) {}

  private addMonths(date: Date, months: number): Date {
    const d = new Date(date);
    const day = d.getDate();
    // JavaScript setMonth handles month rollover (e.g., Dec to Jan).
    // The "day" check handles the end-of-month edge case (e.g., Jan 31 + 1 month = Feb 28/29).
    d.setMonth(d.getMonth() + months);
    if (d.getDate() !== day) {
      d.setDate(0);
    }
    return d;
  }

  private enhanceMember(member: any) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const expiryDateStr = (member.expiryDate || member.createdAt).toString();
    const expiryDate = new Date(expiryDateStr);
    const isExpired = expiryDate < today;

    // Standard date format: Mar 24, 2024
    const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'short', day: 'numeric' };

    return {
      ...member,
      isExpired,
      joiningDateDisplay: new Date(member.joinDate || member.createdAt).toLocaleDateString('en-US', options),
      expiryDateDisplay: new Date(member.expiryDate || member.createdAt).toLocaleDateString('en-US', options),
    };
  }

  async create(data: any): Promise<Member> {
    const { gymId, name, email, phone, bloodGroup, photoUrl, planId, ...rest } = data;
    
    let expiryDate: Date | null = null;
    if (planId) {
      const plan = await this.prisma.membershipPlan.findUnique({
        where: { id: planId }
      });
      if (plan) {
        expiryDate = this.addMonths(new Date(), plan.durationMonths);
      }
    }

    const member = await this.prisma.member.create({
      data: {
        ...rest,
        name,
        email,
        phone,
        bloodGroup,
        photoUrl,
        expiryDate,
        joinDate: new Date(),
        gym: { connect: { id: gymId } },
      },
      include: { gym: true }
    });

    return this.enhanceMember(member) as any;
  }

  async findAll(user: any, gymId?: string, page: number = 1, limit: number = 100): Promise<Member[]> {
    const where: any = {};
    const skip = (page - 1) * limit;

    if (user.role === 'GYM') {
      const dbUser = await this.prisma.user.findUnique({
        where: { id: user.id },
        select: { branchGymId: true } as any,
      }) as any;
      
      if (!dbUser?.branchGymId) {
        return [];
      }
      where.gymId = dbUser.branchGymId;
    } else if (user.role === 'ADMIN') {
      if (gymId) {
        where.gymId = gymId;
      }
    } else {
      return [];
    }

    const members = await this.prisma.member.findMany({
      where,
      include: { gym: true },
      orderBy: { createdAt: 'desc' },
      skip,
      take: limit,
    });

    return members.map(m => this.enhanceMember(m)) as any;
  }

  async findOne(id: string): Promise<Member | null> {
    const member = await this.prisma.member.findUnique({
      where: { id },
      include: { gym: true, attendance: true, payments: true },
    });
    return member ? this.enhanceMember(member) as any : null;
  }

  async update(id: string, data: any): Promise<Member> {
    const { planId, ...rest } = data;
    let updateData = { ...rest };

    if (planId) {
      const plan = await this.prisma.membershipPlan.findUnique({
        where: { id: planId }
      });
      if (plan) {
        // Recalculate expiry from today if plan is updated
        updateData.expiryDate = this.addMonths(new Date(), plan.durationMonths);
      }
    }

    const member = await this.prisma.member.update({
      where: { id },
      data: updateData,
      include: { gym: true }
    });

    return this.enhanceMember(member) as any;
  }

  async renew(id: string, months: number): Promise<Member> {
    const member = await this.prisma.member.findUnique({ where: { id } });
    if (!member) throw new Error('Member not found');

    const currentExpiry = member.expiryDate && member.expiryDate > new Date() 
      ? member.expiryDate 
      : new Date();
    
    const newExpiry = this.addMonths(currentExpiry, months);

    const updated = await this.prisma.member.update({
      where: { id },
      data: { 
        expiryDate: newExpiry,
        status: 'ACTIVE'
      },
      include: { gym: true }
    });

    return this.enhanceMember(updated) as any;
  }

  async remove(id: string): Promise<Member> {
    return this.prisma.member.delete({
      where: { id },
    });
  }
}
