import { PrismaClient, Role } from '@prisma/client';
import { PrismaPg } from '@prisma/adapter-pg';
import { Pool } from 'pg';
import * as bcrypt from 'bcrypt';
import 'dotenv/config';

const pool = new Pool({ connectionString: process.env.DATABASE_URL });
const adapter = new PrismaPg(pool as any);
const prisma = new PrismaClient({ adapter });

async function main() {
  console.log('🌱 Starting database seeding...');

  // 1. Create Admin (Super Admin / Owner)
  const adminEmail = process.env.SUPER_ADMIN_EMAIL || 'super@gym.com';
  const adminPassword = process.env.SUPER_ADMIN_PASSWORD || 'admin123';
  const hashedPassword = await bcrypt.hash(adminPassword, 10);

  const adminAccount = await prisma.user.upsert({
    where: { email: adminEmail },
    update: {},
    create: {
      email: adminEmail,
      name: 'System Admin',
      password: hashedPassword,
      role: 'ADMIN' as Role, // Using 'ADMIN' instead of 'SUPER_ADMIN'
    },
  });

  console.log(`✅ Admin created/verified: ${adminAccount.email}`);

  // 2. Create a default Gym owned by the Admin
  const gym = await prisma.gym.upsert({
    where: { id: 'default-gym-id' },
    update: {},
    create: {
      id: 'default-gym-id',
      name: 'PowerHouse Fitness Central',
      address: '456 Elite Plaza, Downtown',
      phone: '555-0199',
      ownerId: adminAccount.id,
    },
  });

  console.log(`✅ Default Gym created/verified: ${gym.name}`);

  // 3. Create a Gym-level account (Branch Login)
  const branchEmail = 'powerhouse@gym.com';
  const branchPassword = await bcrypt.hash('gym123', 10);

  const gymAccount = await prisma.user.upsert({
    where: { email: branchEmail },
    update: {},
    create: {
      email: branchEmail,
      name: 'Central Branch Admin',
      password: branchPassword,
      role: 'GYM' as Role,
      branchGymId: gym.id,
    } as any,
  });

  console.log(`✅ Gym Account created: ${gymAccount.email} (Assigned to ${gym.name})`);

  // 4. Create default Membership Plans for this gym
  const plans = [
    { name: 'Basic Monthly', price: 29.99, durationMonths: 1, description: 'Access to gym area' },
    { name: 'Standard Quarterly', price: 79.99, durationMonths: 3, description: 'Gym + Locker access' },
    { name: 'Premium Yearly', price: 249.99, durationMonths: 12, description: 'Full access + Personal Trainer sessions' },
  ];

  for (const plan of plans) {
    await prisma.membershipPlan.upsert({
      where: { 
        id: `plan-${plan.name.toLowerCase().replace(/\s+/g, '-')}` 
      },
      update: {},
      create: {
        id: `plan-${plan.name.toLowerCase().replace(/\s+/g, '-')}`,
        name: plan.name,
        price: plan.price,
        durationMonths: plan.durationMonths,
        description: plan.description,
        gymId: gym.id,
      },
    });
  }

  console.log('✅ Default Membership Plans created!');
  console.log('🚀 Seeding complete!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
    await pool.end();
  });
