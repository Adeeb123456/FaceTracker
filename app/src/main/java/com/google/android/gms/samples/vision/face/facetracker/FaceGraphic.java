/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int COLOR_CHOICES[] = {
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.MAGENTA,
        Color.RED,
        Color.WHITE,
        Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;

    private volatile Face mFace;
    private int mFaceId;
    private float mFaceHappiness;
    Context mContext;

    FaceGraphic(GraphicOverlay overlay, Context context) {
        super(overlay);
mContext=context;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
        mBoxPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

    }

    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face) {
        mFace = face;
        postInvalidate();
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
       Bitmap  bitmapMask= BitmapFactory.decodeResource(mContext.getResources(),R.drawable.masfit,options);
       Canvas canvasface=new Canvas(bitmapMask);

        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        if (face == null) {
            return;
        }
        canvas.rotate(face.getEulerZ()-face.getEulerY(),face.getWidth()/2,face.getHeight()/2);
        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);
        canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);


        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getWidth() / 2.0f);
        float yOffset = scaleY(face.getHeight() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, mBoxPaint);



        float righteyeX = 0;
        float righteyeY=0;
        float leftEyeX = 0;
        float leftEyeY = 0;


        float rightmouthX = 0;
        float rightmouthY=0;
        float leftmouthX = 0;
        float leftmouthY = 0;

        int noseBaseX=0;
        int noseBaseY=0;

      /*  for (Landmark landmark :  face.getLandmarks()) {
            int cx = (int) (landmark.getPosition().x );
            int cy = (int) (landmark.getPosition().y );
            Paint bluePaint=new Paint();
            bluePaint.setColor(Color.BLUE);

            Paint GreenPaint=new Paint();
            GreenPaint.setColor(Color.GREEN);

            if(landmark.getType()==Landmark.LEFT_EYE){


                leftEyeX=landmark.getPosition().x;
                leftEyeY=landmark.getPosition().y;
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, 70, paint);
                canvas.drawCircle(cx, cy, 60, bluePaint);
                canvas.drawCircle(cx, cy, 50, GreenPaint);
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, 40, paint);

                canvas.drawCircle(cx, cy, 30, paint);

            }

            if(landmark.getType()==Landmark.RIGHT_EYE){
                righteyeX=landmark.getPosition().x;
                righteyeY=landmark.getPosition().y;
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, 70, paint);
                canvas.drawCircle(cx, cy, 60, bluePaint);
                canvas.drawCircle(cx, cy, 50, GreenPaint);
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(cx, cy, 40, paint);

                canvas.drawCircle(cx, cy, 30, paint);
            }

            if(landmark.getType()==Landmark.NOSE_BASE){
                noseBaseX=(int)landmark.getPosition().x;
                noseBaseY=(int)landmark.getPosition().y;
                canvas.drawCircle(cx, cy, 40, paint);
            }


            if(landmark.getType()==Landmark.LEFT_MOUTH){
                leftmouthX=(int)landmark.getPosition().x;
                leftmouthY=(int)landmark.getPosition().y;
                canvas.drawCircle(cx, cy, 40, paint);
            }

            if(landmark.getType()==Landmark.RIGHT_MOUTH){
                rightmouthX=(int)landmark.getPosition().x;
                rightmouthY=(int)landmark.getPosition().y;
                canvas.drawCircle(cx, cy, 40, paint);
            }


            //  canvasFace.drawBitmap(bitmapmask,leftEyeX+righteyeX,leftEyeY+righteyeY,paint);

        }*/







        int xleft= (int) face.getPosition().x;
        int ytop=(int)(face.getPosition().y+(face.getHeight()/yOffset));
        int xRight= (int) (xleft+face.getWidth());
        int yBottom=(int) (ytop+face.getHeight());

        Rect rect=new Rect((int)left,(int)(top+100),(int)right,(int)bottom);

        canvas.drawBitmap(bitmapMask,null,rect,mBoxPaint);
    }
}
