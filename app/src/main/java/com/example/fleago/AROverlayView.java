package com.example.fleago;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.opengl.Matrix;
import android.view.View;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;

import java.util.ArrayList;
import java.util.List;

import com.example.fleago.helper.LocationHelper;
import com.example.fleago.model.ARPoint;

/**
 * Created by ntdat on 1/13/17.
 */

public class AROverlayView extends View {

    Context context;
    private float[] rotatedProjectionMatrix = new float[16];
    private Location currentLocation;
    private List<ARPoint> arPoints;


    public AROverlayView(Context context,final String name, final double latitude, final double longitude) {
        super(context);

        this.context = context;

        //Demo points
        arPoints = new ArrayList<ARPoint>() {{
            add(new ARPoint(name, latitude, longitude, 0));
        }};
    }

    public void updateRotatedProjectionMatrix(float[] rotatedProjectionMatrix) {
        this.rotatedProjectionMatrix = rotatedProjectionMatrix;
        this.invalidate();
    }

    public void updateCurrentLocation(Location currentLocation){
        this.currentLocation = currentLocation;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentLocation == null) {
            return;
        }

        final int radius = 30;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(170);

        for (int i = 0; i < arPoints.size(); i ++) {
            float[] currentLocationInECEF = LocationHelper.WSG84toECEF(currentLocation);
            float[] pointInECEF = LocationHelper.WSG84toECEF(arPoints.get(i).getLocation());
            float[] pointInENU = LocationHelper.ECEFtoENU(currentLocation, currentLocationInECEF, pointInECEF);

            float[] cameraCoordinateVector = new float[4];
            Matrix.multiplyMV(cameraCoordinateVector, 0, rotatedProjectionMatrix, 0, pointInENU, 0);

            // cameraCoordinateVector[2] is z, that always less than 0 to display on right position
            // if z > 0, the point will display on the opposite
            if (cameraCoordinateVector[2] < 0) {
                float x  = (0.5f + cameraCoordinateVector[0]/cameraCoordinateVector[3]) * canvas.getWidth();
                float y = (0.5f - cameraCoordinateVector[1]/cameraCoordinateVector[3]) * canvas.getHeight();

                updateCurrentLocation(currentLocation);


                //현재와 타겟의 gps 거리 측정
                double distance;
                //System.out.println("AROverlayView activity"+ currentLocation.getLatitude()+currentLocation.getLongitude()+currentLocation.getAltitude());
                Location locationA = new Location("current GPS");

                locationA.setLatitude(currentLocation.getLatitude());
                locationA.setLongitude(currentLocation.getLongitude());

                Location locationB = new Location("target GPS");

                locationB.setLatitude(arPoints.get(0).getLocation().getLatitude());
                locationB.setLongitude(arPoints.get(0).getLocation().getLongitude());

                distance = locationA.distanceTo(locationB);


                Bitmap image;
                Resources r = context.getResources();
                image = BitmapFactory.decodeResource(r, R.drawable.fleago);

                int viewHeight = 100;
                float width = image.getWidth();
                float height = image.getHeight();

                if(height > viewHeight)
                {
                    float percente = (float)(height / 400);
                    float scale = (float)(viewHeight / percente);
                    width *= (scale / 100);
                    height *= (scale / 100);
                }

                boolean too_long = false;

                if(distance > 1000){
                    distance /= 1000;
                    too_long = true;

                }

                Bitmap sizingBmp = Bitmap.createScaledBitmap(image, (int) width, (int) height, true);
                //canvas.drawCircle(x, y, radius, paint);
                canvas.drawText(arPoints.get(i).getName(), x + ( 90 * arPoints.get(i).getName().length() / 2), y + 180, paint);
                if(too_long)
                    canvas.drawText(String.format("%.2f",distance) + "KM", x + ( 90 *arPoints.get(i).getName().length() / 2), y + 380 , paint);
                else
                    canvas.drawText(((int)distance)+ "M", x + ( 90 *arPoints.get(i).getName().length() / 2), y + 380 , paint);
                //canvas.drawText(arPoints.get(i).getName(), x + (280 * arPoints.get(i).getName().length() / 2), y + 180, paint);
                //canvas.drawText((int)distance + "M", x + (280 * arPoints.get(i).getName().length() / 2), y + 380 , paint);

                //이미지 추가
                canvas.drawBitmap(sizingBmp,x,y,paint);
            }
        }
    }
}
