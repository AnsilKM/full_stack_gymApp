package com.gym.gymapp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gym.gymapp.ui.components.AppLoader
import com.gym.gymapp.ui.components.MemberItem
import com.gym.gymapp.ui.viewmodels.DashboardViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onAddMember: () -> Unit = {},
    onViewMembers: () -> Unit = {},
    onViewAttendance: () -> Unit = {},
    onViewProfile: () -> Unit = {},
    onMemberClick: (String) -> Unit = {},
    viewModel: DashboardViewModel = koinViewModel()
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isTablet = maxWidth > 600.dp

        if (isTablet) {
            // Tablet Layout: 2 Columns
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Column: Overview & Stats
                Column(modifier = Modifier.weight(1.2f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                viewModel.uiState.gymName.ifEmpty { "Gym Dashboard" },
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Main Branch Overview",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Overview", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Total Members", viewModel.uiState.totalMembers, Icons.Default.Groups, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                        StatCard("Revenue", viewModel.uiState.totalRevenue, Icons.Default.Payments, MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(16.dp))
                    StatCard("Growth", viewModel.uiState.monthlyGrowth, Icons.AutoMirrored.Filled.TrendingUp, MaterialTheme.colorScheme.onSurface, Modifier.fillMaxWidth())
                    
                    Spacer(Modifier.height(32.dp))
                    Text("Quick Actions", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onAddMember, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp)) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Add Member")
                        }
                        OutlinedButton(onClick = onViewAttendance, modifier = Modifier.weight(1f).height(56.dp), shape = RoundedCornerShape(16.dp)) {
                            Icon(Icons.Default.Checklist, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Attendance")
                        }
                    }
                }

                // Right Column: Recent Activity/Members
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Recent Members", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("See More", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onViewMembers() })
                    }
                    Spacer(Modifier.height(16.dp))

                    PullToRefreshBox(
                        isRefreshing = viewModel.uiState.isLoading,
                        onRefresh = { viewModel.loadDashboardData() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (viewModel.uiState.recentMembers.isEmpty()) {
                                item { EmptyCard("No members joined yet") }
                            } else {
                                items(viewModel.uiState.recentMembers) { member ->
                                    MemberItem(
                                        name = member.name,
                                        status = member.status,
                                        isActuallyExpired = member.isExpired ?: false,
                                        joinDate = member.joiningDateDisplay ?: "N/A",
                                        expiryDate = member.expiryDateDisplay ?: "N/A",
                                        phone = member.phone,
                                        imageUrl = member.photoUrl,
                                        onClick = { onMemberClick(member.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Mobile Layout: Single Column Scroll
            PullToRefreshBox(
                isRefreshing = viewModel.uiState.isLoading,
                onRefresh = { viewModel.loadDashboardData() }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    viewModel.uiState.gymName.ifEmpty { "Gym Dashboard" },
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "Main Branch Overview",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (viewModel.uiState.isLoading) {
                        item { AppLoader(isFullPage = true) }
                    } else {
                        item {
                            Text("Overview", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    StatCard("Total Members", viewModel.uiState.totalMembers, Icons.Default.Groups, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                                    StatCard("Revenue", viewModel.uiState.totalRevenue, Icons.Default.Payments, MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                                }
                                StatCard("Growth", viewModel.uiState.monthlyGrowth, Icons.AutoMirrored.Filled.TrendingUp, MaterialTheme.colorScheme.onSurface, Modifier.fillMaxWidth())
                            }
                        }


                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Recent Members", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("See More", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onViewMembers() })
                            }
                        }

                        if (viewModel.uiState.recentMembers.isEmpty()) {
                            item { EmptyCard("No members joined yet") }
                        } else {
                            items(viewModel.uiState.recentMembers.take(5)) { member ->
                                MemberItem(
                                    name = member.name,
                                    status = member.status,
                                    isActuallyExpired = member.isExpired ?: false,
                                    joinDate = member.joiningDateDisplay ?: "N/A",
                                    expiryDate = member.expiryDateDisplay ?: "N/A",
                                    phone = member.phone,
                                    imageUrl = member.photoUrl,
                                    onClick = { onMemberClick(member.id) }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}


@Composable
fun EmptyCard(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), fontSize = 13.sp)
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (accentColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (accentColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.1f
                        ) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (accentColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = if (accentColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                title,
                fontSize = 10.sp,
                color = if (accentColor == MaterialTheme.colorScheme.primary) MaterialTheme.colorScheme.onPrimary.copy(
                    alpha = 0.6f
                ) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ActivityRow(memberId: String, time: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Member ${memberId.take(6).uppercase()}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    "Check-in successful",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                time.split("T").getOrNull(1)?.take(5) ?: "Just now",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



