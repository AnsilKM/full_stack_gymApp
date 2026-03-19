import { Controller, Post, Body, Get, UseGuards } from '@nestjs/common';
import { AdminService } from './admin.service';
import { AuthGuard } from '@nestjs/passport';
import { RolesGuard } from '../common/guards/roles.guard';
import { Roles } from '../common/decorators/roles.decorator';
import { Role } from '@prisma/client';

@Controller('admin')
@UseGuards(AuthGuard('jwt'), RolesGuard)
@Roles(Role.ADMIN)
export class AdminController {
  constructor(private readonly adminService: AdminService) {}

  @Post('gyms')
  async createGym(@Body() data: { name: string; address: string; phone?: string; ownerId: string }) {
    return this.adminService.createGym(data);
  }

  @Post('users')
  async createUser(@Body() data: { email: string; name: string; password: string; role: Role }) {
    return this.adminService.createUser(data);
  }

  @Get('gyms')
  async getAllGyms() {
    return this.adminService.getAllGyms();
  }

  @Get('owners')
  async getAllOwners() {
    return this.adminService.getAllOwners();
  }
}
