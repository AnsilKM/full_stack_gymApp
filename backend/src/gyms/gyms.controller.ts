import { Controller, Get, Post, Body, Param, UseGuards, Request } from '@nestjs/common';
import { GymsService } from './gyms.service';
import { AuthGuard } from '@nestjs/passport';

@Controller('gyms')
export class GymsController {
  constructor(private readonly gymsService: GymsService) {}

  @UseGuards(AuthGuard('jwt'))
  @Post()
  create(@Body() body: any, @Request() req: any) {
    return this.gymsService.create(body, req.user.id);
  }

  @UseGuards(AuthGuard('jwt'))
  @Get()
  findAll(@Request() req: any) {
    if (req.user.role === 'ADMIN') {
      return this.gymsService.findAll();
    }
    if (req.user.role === 'GYM') {
      return this.gymsService.findAssigned(req.user.id);
    }
    return [];
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.gymsService.findOne(id);
  }

  @Get('owner/:ownerId')
  findByOwner(@Param('ownerId') ownerId: string) {
    return this.gymsService.findByOwner(ownerId);
  }
}
