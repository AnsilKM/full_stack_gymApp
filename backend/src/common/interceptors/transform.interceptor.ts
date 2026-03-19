import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface Response<T> {
  status: boolean;
  message: string;
  data: T;
}

@Injectable()
export class TransformInterceptor<T>
  implements NestInterceptor<T, Response<T>> {
  intercept(
    context: ExecutionContext,
    next: CallHandler,
  ): Observable<Response<T>> {
    const request = context.switchToHttp().getRequest();
    
    // Determine a default message based on the route if possible
    let message = 'Success';
    const path = request.url;
    
    if (path.includes('login')) message = 'Login successful';
    if (path.includes('register')) message = 'Registration successful';
    if (path.includes('members')) {
        if (request.method === 'POST') message = 'Member registered successfully';
        if (request.method === 'GET') message = 'Members loaded successfully';
    }
    if (path.includes('attendance')) {
        if (request.method === 'POST') message = 'Attendance marked successfully';
    }

    return next.handle().pipe(
      map((data) => ({
        status: true,
        message: data?.message || message,
        data: data?.data || data,
      })),
    );
  }
}
