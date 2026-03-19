import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { PrismaModule } from './prisma/prisma.module';
import { AuthModule } from './auth/auth.module';
import { UsersModule } from './users/users.module';
import { GymsModule } from './gyms/gyms.module';
import { MembersModule } from './members/members.module';
import { PlansModule } from './plans/plans.module';
import { AttendanceModule } from './attendance/attendance.module';
import { PaymentsModule } from './payments/payments.module';
import { ReportsModule } from './reports/reports.module';
import { AdminModule } from './admin/admin.module';
import { BroadcastModule } from './broadcast/broadcast.module';

@Module({
  imports: [
    PrismaModule,
    AuthModule,
    UsersModule,
    GymsModule,
    MembersModule,
    PlansModule,
    AttendanceModule,
    PaymentsModule,
    ReportsModule,
    AdminModule,
    BroadcastModule,
  ],

  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
