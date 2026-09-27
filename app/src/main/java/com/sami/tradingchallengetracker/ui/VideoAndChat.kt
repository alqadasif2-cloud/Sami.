package com.sami.tradingchallengetracker.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.sami.tradingchallengetracker.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AndroidAuthUser(
    val id: Int,
    val username: String,
    val displayName: String,
    val password: String
)

val AUTHORIZED_ANDROID_USERS = listOf(
    AndroidAuthUser(1, "Sami", "Sami", "12345678"),
    AndroidAuthUser(2, "Hani Alqadasi", "Hani Alqadasi", "Hani33334"),
    AndroidAuthUser(3, "Eslam Alqadasi", "Eslam Alqadasi", "55555555"),
    AndroidAuthUser(4, "Omar Ali", "Omar Ali", "Omar1234"),
    AndroidAuthUser(5, "Ahmed Saleh", "Ahmed Saleh", "Ahmed123"),
    AndroidAuthUser(6, "Mohammed Ali", "Mohammed Ali", "Mo2025"),
    AndroidAuthUser(7, "Khaled Nasser", "Khaled Nasser", "Khaled22"),
    AndroidAuthUser(8, "Yasser Ahmed", "Yasser Ahmed", "Yasser11"),
    AndroidAuthUser(9, "Ali Hassan", "Ali Hassan", "Ali2025"),
    AndroidAuthUser(10, "Abdullah Sami", "Abdullah Sami", "Abd12345"),
    AndroidAuthUser(11, "Faisal Omar", "Faisal Omar", "Faisal88"),
    AndroidAuthUser(12, "Mahmoud Adel", "Mahmoud Adel", "Mahmoud7"),
    AndroidAuthUser(13, "Tareq Salem", "Tareq Salem", "Tareq123"),
    AndroidAuthUser(14, "Zaid Ahmed", "Zaid Ahmed", "Zaid2025"),
    AndroidAuthUser(15, "Noor Ali", "Noor Ali", "Noor1234"),
    AndroidAuthUser(16, "Amjad Sami", "Amjad Sami", "Amjad555"),
    AndroidAuthUser(17, "Saleh Omar", "Saleh Omar", "Saleh2025"),
    AndroidAuthUser(18, "Nasser Ali", "Nasser Ali", "Nasser99"),
    AndroidAuthUser(19, "Tariq90", "Tariq90", "Tariq4141"),
    AndroidAuthUser(20, "Yazan Sami", "Yazan Sami", "Yazan2025")
)

/**
 * Looping Background Video using Media3 ExoPlayer as required by PROJECT_REQUIREMENTS.txt:
 * - Autoplay on screen open
 * - Infinite loop (REPEAT_MODE_ALL)
 * - Mute (volume 0f)
 * - Fullscreen Center Crop (RESIZE_MODE_ZOOM)
 * - Transparent dark overlay (60-70%)
 * - Properly releases ExoPlayer resources in onDispose to prevent memory leaks.
 */
@OptIn(UnstableApi::class)
@Composable
fun LoopingBackgroundVideo(
    modifier: Modifier = Modifier,
    rawResId: Int = R.raw.background_video,
    overlayAlpha: Float = 0.65f
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("android.resource://${context.packageName}/$rawResId")
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        // 60-70% Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )
    }
}

/**
 * Android Offline Login Screen
 */
@Composable
fun AndroidLoginScreen(
    onLoginSuccess: (AndroidAuthUser) -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Video Background with 70% dark overlay
        LoopingBackgroundVideo(rawResId = R.raw.background_video, overlayAlpha = 0.70f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xEA0D131F)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sami",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700)
                    )
                    Text(
                        text = "متتبع رحلة التداول الشخصية • تسجيل الدخول",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    errorMessage?.let { msg ->
                        Surface(
                            color = Color(0x33EF4444),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        label = { Text("اسم المستخدم (Username)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFFD700)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("كلمة المرور (Password)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD700)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD700),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val trimmedUser = username.trim()
                            val trimmedPass = password.trim()
                            val matched = AUTHORIZED_ANDROID_USERS.find {
                                it.username.equals(trimmedUser, ignoreCase = true) && it.password == trimmedPass
                            }
                            if (matched != null) {
                                // Save locally
                                val prefs = context.getSharedPreferences("sami_auth_prefs", Context.MODE_PRIVATE)
                                prefs.edit()
                                    .putInt("user_id", matched.id)
                                    .putString("username", matched.username)
                                    .putString("display_name", matched.displayName)
                                    .apply()

                                onLoginSuccess(matched)
                            } else {
                                errorMessage = "اسم المستخدم أو كلمة المرور غير صحيحة."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "تسجيل الدخول",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

data class AndroidChatMessage(
    val id: String,
    val senderId: Int,
    val senderName: String,
    val text: String?,
    val timestamp: Long
)

/**
 * Group Chat Screen: 💬 دفعة الرحلة إلى 3000$
 */
@Composable
fun AndroidGroupChatScreen(
    currentUser: AndroidAuthUser
) {
    val context = LocalContext.current
    var isOnline by remember { mutableStateOf(checkInternet(context)) }
    var inputText by remember { mutableStateOf("") }

    val initialMessages = remember {
        mutableStateListOf<AndroidChatMessage>()
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // 48h filter
    val validMessages = remember(initialMessages.size) {
        val now = System.currentTimeMillis()
        initialMessages.filter { now - it.timestamp < 48 * 3600 * 1000 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A10))
    ) {
        // Chat Header
        Surface(
            color = Color(0xFF0D131F),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFD700)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💬", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "دفعة الرحلة إلى 3000$",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "20 عضواً • حذف تلقائي بعد 48 ساعة",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Offline Banner
        if (!isOnline) {
            Surface(
                color = Color(0x33EF4444),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("لا يوجد اتصال بالإنترنت", color = Color(0xFFFCA5A5), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(validMessages) { msg ->
                val isMe = msg.senderId == currentUser.id
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.Start else Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isMe) Color(0x33FFD700) else Color(0xFF131D30),
                        border = if (isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0x88FFD700)) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isMe) Color(0xFFFFD700) else Color(0xFF38BDF8)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                Text(
                                    text = timeFormat.format(Date(msg.timestamp)),
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            msg.text?.let {
                                Text(text = it, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Input bar
        Surface(
            color = Color(0xFF0D131F),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("اكتب رسالتك للمجموعة...", fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFD700),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotEmpty()) {
                            initialMessages.add(
                                AndroidChatMessage(
                                    id = UUID.randomUUID().toString(),
                                    senderId = currentUser.id,
                                    senderName = currentUser.displayName,
                                    text = text,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                            inputText = ""
                            coroutineScope.launch {
                                listState.animateScrollToItem(initialMessages.size - 1)
                            }
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color(0xFF0F172A))
                }
            }
        }
    }
}

fun checkInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
    val activeNet = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(activeNet) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
