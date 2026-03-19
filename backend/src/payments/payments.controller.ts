import { Controller, Get, Post, Body, Query } from '@nestjs/common';
import { PaymentsService } from './payments.service';

@Controller('payments')
export class PaymentsController {
  constructor(private readonly paymentsService: PaymentsService) {}

  @Post()
  create(@Body() body: any) {
    return this.paymentsService.create(body);
  }

  @Get()
  findAll(@Query('gymId') gymId: string) {
    return this.paymentsService.findAll(gymId);
  }
}
