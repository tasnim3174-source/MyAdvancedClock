package com.example.myadvancedclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.RemoteViews;
import java.util.Calendar;

public class ClockWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            int width = 400; int height = 400;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            
            boolean isDay = (hour >= 6 && hour < 18);
            int bgColor = isDay ? Color.parseColor("#FFF8E1") : Color.parseColor("#263238");
            int handColor = isDay ? Color.parseColor("#263238") : Color.parseColor("#FFF8E1");
            int tickColor = isDay ? Color.parseColor("#B0BEC5") : Color.parseColor("#546E7A");

            Paint bgPaint = new Paint();
            bgPaint.setColor(bgColor);
            bgPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width/2f, height/2f, width/2f, bgPaint);

            Paint tickPaint = new Paint();
            tickPaint.setColor(tickColor);
            tickPaint.setStrokeWidth(6);
            tickPaint.setAntiAlias(true);
            for (int i = 0; i < 12; i++) {
                float angle = (float) Math.toRadians(i * 30);
                float startX = width/2f + (float) Math.cos(angle) * (width/2f - 30);
                float startY = height/2f + (float) Math.sin(angle) * (height/2f - 30);
                float endX = width/2f + (float) Math.cos(angle) * (width/2f - 10);
                float endY = height/2f + (float) Math.sin(angle) * (height/2f - 10);
                canvas.drawLine(startX, startY, endX, endY, tickPaint);
            }

            Paint handPaint = new Paint();
            handPaint.setColor(handColor);
            handPaint.setAntiAlias(true);
            handPaint.setStrokeCap(Paint.Cap.ROUND);
            handPaint.setStrokeWidth(16);
            float hourAngle = (float) Math.toRadians((hour % 12) * 30 + minute * 0.5f - 90);
            canvas.drawLine(width/2f, height/2f, width/2f + (float) Math.cos(hourAngle) * (width/4f), height/2f + (float) Math.sin(hourAngle) * (height/4f), handPaint);

            Paint minPaint = new Paint();
            minPaint.setColor(Color.parseColor("#9C27B0"));
            minPaint.setAntiAlias(true);
            minPaint.setStrokeCap(Paint.Cap.ROUND);
            minPaint.setStrokeWidth(10);
            float minAngle = (float) Math.toRadians(minute * 6 - 90);
            canvas.drawLine(width/2f, height/2f, width/2f + (float) Math.cos(minAngle) * (width/3f), height/2f + (float) Math.sin(minAngle) * (height/3f), minPaint);

            Paint centerPaint = new Paint();
            centerPaint.setColor(handColor);
            canvas.drawCircle(width/2f, height/2f, 10, centerPaint);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setImageViewBitmap(R.id.clock_image, bitmap);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
