package com.gym.gymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gym.gymapp.getPlatform

@Composable
fun MemberItem(
    name: String, 
    status: String, 
    isActuallyExpired: Boolean,
    joinDate: String, 
    expiryDate: String, 
    phone: String?, 
    imageUrl: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            ProfileImage(
                imageUrl = imageUrl,
                name = name,
                size = 44
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                val displayStatus = if (isActuallyExpired) "EXPIRED" else status

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    
                    Surface(
                        color = if (isActuallyExpired) Color.Red.copy(alpha = 0.1f) 
                               else if (status == "ACTIVE") MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                               else Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            displayStatus,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = if (isActuallyExpired || status != "ACTIVE") Color.Red else MaterialTheme.colorScheme.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Joined Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            " Joined: $joinDate",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    // Expiry Date
                    val expiryColor = when {
                        isActuallyExpired -> Color.Red
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = expiryColor
                        )
                        Text(
                            " Expires: $expiryDate",
                            fontSize = 10.sp,
                            color = expiryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (!phone.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // WhatsApp Button
                        Button(
                            onClick = {
                                val number = phone.replace("+", "").replace(" ", "")
                                getPlatform().openUrl("https://wa.me/$number")
                            },
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF059669).copy(alpha = 0.12f), // Emerald Green
                                contentColor = Color(0xFF059669)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("WhatsApp", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Call Button
                        Button(
                            onClick = {
                                getPlatform().openUrl("tel:$phone")
                            },
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F46E5).copy(alpha = 0.12f), // Indigo
                                contentColor = Color(0xFF4F46E5)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = null
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Call", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
