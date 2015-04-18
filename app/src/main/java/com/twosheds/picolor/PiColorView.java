package com.twosheds.picolor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PiColorView extends View {
    private Bitmap bitmap;
    private float left;
    private float top;

    private int circleX;
    private int circleY;
    private int pickerRadius;
    private Paint circlePaint;

    public interface ColorPickedListener {
        public void onColorPicked(int color);
    }

    private ColorPickedListener listener;

    public PiColorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(10);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        pickerRadius = Math.min(w, h) / 20;

        int bitmapSize = Math.min(w, h) - 2*pickerRadius;
        initBitmap(bitmapSize);

        left = (w - bitmapSize) / 2;
        top = (h - bitmapSize) / 2;
    }

    public void setColorPickerListener(ColorPickedListener listener) {
        this.listener = listener;
    }

    private void initBitmap(int bitmapSize) {
        if (bitmap == null || bitmap.getWidth() != bitmapSize || bitmap.getHeight() != bitmapSize) {
            bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);

            int R = bitmapSize / 2;
            for (int x=-R; x<R; x++) {
                for (int y=-R; y<R; y++) {
                    int radiusSq = x*x + y*y;
                    if (radiusSq <= R*R) {
                        double radius = Math.sqrt(radiusSq);
                        double angle = Math.atan2((double) y, (double) x) + Math.PI;

                        int intensity = (int) (255 * radius / R);
                        int r, g, b;
                        if (angle < 2 * Math.PI / 3) {
                            r = 0;
                            g = (int) (intensity * Math.sin(angle * 0.75));
                            b = (int) (intensity * Math.cos(angle * 0.75));
                        } else if (angle < 4 * Math.PI / 3) {
                            r = (int) (intensity * Math.sin((angle - 2 * Math.PI / 3) * 0.75));
                            g = (int) (intensity * Math.cos((angle - 2 * Math.PI / 3) * 0.75));
                            b = 0;
                        } else {
                            r = (int) (intensity * Math.cos((angle - 4 * Math.PI / 3) * 0.75));
                            g = 0;
                            b = (int) (intensity * Math.sin((angle - 4 * Math.PI / 3) * 0.75));;
                        }
                        int color = (0xFF << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                        bitmap.setPixel(x+R, y+R, color);
                    }
                }
            }
            pickColor(0, R);
        }
    }

    private void pickColor(int x, int y) {
        if (bitmap == null) {
            return;
        }

        int R = bitmap.getWidth() / 2;
        int color = bitmap.getPixel(x + R - 1, y + R - 1);

        if (color != 0) {
            circleX = x;
            circleY = y;

            if (listener != null) {
                listener.onColorPicked(color);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, left, top, null);

        int R = bitmap.getWidth() / 2;

        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(bitmap.getPixel(circleX + R - 1, circleY + R - 1));
        canvas.drawCircle(circleX + R + left,  circleY + R + top, pickerRadius, circlePaint);

        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(bitmap.getPixel(-circleX + R, -circleY + R));
        canvas.drawCircle(circleX + R + left,  circleY + R +top, pickerRadius, circlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int R = bitmap.getWidth() / 2;
        if (x >= left && x < left + bitmap.getWidth()
                && y >= top && y < top + bitmap.getHeight()) {
            // inside the bitmap
            int newX = (int) (x - (left + R));
            int newY = (int) (y - (top + R));

            if (newX * newX + newY * newY < R * R) {
                // inside the circle
                pickColor(newX, newY);
                postInvalidate();
                return true;
            }
        }

        return false;
    }

    private void drawPi(Canvas canvas) {
        String pi = getContext().getString(R.string.pi);

        float x = left + bitmap.getWidth()/5 + 5;
        float y = top + 3*bitmap.getHeight()/4 + 15;

        Paint paintPi = new Paint();
        paintPi.setStyle(Paint.Style.FILL_AND_STROKE);
        paintPi.setAntiAlias(true);
        paintPi.setTextSize(bitmap.getHeight());

        //paintPi.setColor(0xFF628318);

        paintPi.setColor(0xFF314159);
        canvas.drawText(pi, x, y, paintPi);

        paintPi.setStyle(Paint.Style.STROKE);
        paintPi.setColor(Color.WHITE);
        paintPi.setStrokeWidth(4);
        canvas.drawText(pi, x, y, paintPi);
    }

}
