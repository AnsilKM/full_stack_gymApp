import { Controller, Get, Post, Patch, Body, Param, Query } from '@nestjs/common';

import { PlansService } from './plans.service';

@Controller('plans')
export class PlansController {
  constructor(private readonly plansService: PlansService) {}

  @Post()
  create(@Body() body: any) {
    return this.plansService.create(body);
  }

  @Get()
  findAll(@Query('gymId') gymId: string) {
    return this.plansService.findAll(gymId);
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.plansService.findOne(id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() body: any) {
    return this.plansService.update(id, body);
  }
}

