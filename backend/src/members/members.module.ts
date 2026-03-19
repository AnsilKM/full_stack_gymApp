import { Module } from '@nestjs/common';
import { MembersService } from './members.service';
import { UsersModule } from '../users/users.module';
import { MembersController } from './members.controller';

@Module({
  imports: [UsersModule],
  controllers: [MembersController],
  providers: [MembersService]
})
export class MembersModule { }
