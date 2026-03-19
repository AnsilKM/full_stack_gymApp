import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class BroadcastService {
  constructor(private prisma: PrismaService) {}

  async broadcastToAllMembers(gymId: string, message: String): Promise<any> {
    const members = await this.prisma.member.findMany({
      where: { gymId, status: 'ACTIVE' },
      select: { phone: true, name: true },
    });

    if (members.length === 0) {
      return { status: 'NO_MEMBERS', count: 0 };
    }

    const membersWithPhone = members.filter(m => m.phone);

    // Simulation of WhatsApp Broadcast
    console.log(`--- SIMULTATING WHATSAPP BROADCAST FOR GYM ${gymId} ---`);
    console.log(`Message: ${message}`);
    console.log(`Targeting ${membersWithPhone.length} members.`);
    
    // In a real scenario, we would use a WhatsApp Business API provider here
    // Example (pseudo-code):
    // for (const member of membersWithPhone) {
    //   await this.whatsappApi.sendTemplateMessage(member.phone, 'general_broadcast', { message });
    // }

    // Log the broadcast in notifications table for local history
    await this.prisma.notification.create({
      data: {
        title: 'Broadcast Message',
        message: String(message),
        type: 'ANNOUNCEMENT',
        gym: { connect: { id: gymId } },
      },
    });

    return {
      status: 'SUCCESS',
      sentCount: membersWithPhone.length,
      skippedCount: members.length - membersWithPhone.length,
    };
  }

  async getBroadcastLogs(gymId: string): Promise<any[]> {
    return this.prisma.notification.findMany({
      where: { gymId, type: 'ANNOUNCEMENT' },
      orderBy: { createdAt: 'desc' },
    });
  }
}
