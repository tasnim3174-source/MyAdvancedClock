package com.example.myadvancedclock
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate

class MyClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weather = fetchWeather()
        val prayerTimes = fetchPrayerTimes()
        val now = LocalTime.now()
        val timeText = now.format(DateTimeFormatter.ofPattern("HH:mm"))
        val isDaytime = now.hour in 6..17
        val bgColor = if (isDaytime) Color(0xFFFFF8E1) else Color(0xFF263238)
        val textColor = if (isDaytime) Color(0xFF263238) else Color(0xFFFFF8E1)
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().background(bgColor).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = timeText, style = TextStyle(fontSize = 48.sp, color = textColor))
                Spacer(GlanceModifier.height(8.dp))
                Text(text = "هوا: $weather", style = TextStyle(fontSize = 16.sp, color = textColor))
                Text(text = "صبح: ${prayerTimes["Fajr"] ?: "--"}", style = TextStyle(fontSize = 14.sp, color = textColor))
                Text(text = "ظهر: ${prayerTimes["Dhuhr"] ?: "--"}", style = TextStyle(fontSize = 14.sp, color = textColor))
                Text(text = "مغرب: ${prayerTimes["Maghrib"] ?: "--"}", style = TextStyle(fontSize = 14.sp, color = textColor))
            }
        }
    }
    private suspend fun fetchWeather(): String = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url("https://api.open-meteo.com/v1/forecast?latitude=35.6892&longitude=51.3890&current_weather=true").build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext "--°C"
            val temp = JSONObject(body).getJSONObject("current_weather").getDouble("temperature")
            String.format("%.0f°C", temp)
        } catch (e: Exception) { "--°C" }
    }
    private suspend fun fetchPrayerTimes(): Map<String, String> = withContext(Dispatchers.IO) {
        try {
            val today = LocalDate.now()
            val url = "https://api.aladhan.com/v1/timingsByCity/${today.dayOfMonth}-${today.monthValue}-${today.year}?city=Tehran&country=Iran&method=7"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext emptyMap()
            val timings = JSONObject(body).getJSONObject("data").getJSONObject("timings")
            mapOf("Fajr" to timings.getString("Fajr"), "Dhuhr" to timings.getString("Dhuhr"), "Maghrib" to timings.getString("Maghrib"))
        } catch (e: Exception) { emptyMap() }
    }
}
fun GlanceModifier.background(color: Color) = this.then(androidx.glance.background(color))
