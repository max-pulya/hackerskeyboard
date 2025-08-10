package org.pocketworkstation.pckeyboard;

import java.lang.Math;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class DPadView extends View {
    int deadzoneType;
    float DeadzoneOffset;
    float deadzoneSize;

    final int DEADZONE_SQUARE=0;
    static final int DEADZONE_CIRCLE=1;
    InputConnection ic;
    Paint paint=new Paint();
    boolean previousUpState;
    boolean previousDownState;
    boolean previousLeftState;
    boolean previousRightState;



    boolean checkOutsideDeadzone(MotionEvent motionEvent){
        float x=motionEvent.getX() - (float)getWidth() / 2;
        float y=motionEvent.getY() - (float)getHeight() / 2;
        if (deadzoneType==DEADZONE_SQUARE){
            return (x > DeadzoneOffset ||
                    x < -DeadzoneOffset ||
                    y > DeadzoneOffset ||
                    y < -DeadzoneOffset);
        }
        if (deadzoneType==DEADZONE_CIRCLE){
            return x*x+y*y>DeadzoneOffset;
        }
        return false;
    };
    DPadView(Context context, int deadzoneType, float deadzoneSize, InputConnection ic){
        super(context);
        this.deadzoneType=deadzoneType;
        float width=getWidth();
        this.deadzoneSize=deadzoneSize;
        this.ic = ic;

    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        DeadzoneOffset=deadzoneSize*Math.max(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels)/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        paint.setColor(0x60FFFF60);
        paint.setStyle(Paint.Style.FILL);

        float centerx = (float) getWidth() /2;
        float centery = (float) getHeight() /2;
        canvas.drawRect( (centerx -1.5F*DeadzoneOffset), (centery -0.5F*DeadzoneOffset),
                 (centerx +1.5F*DeadzoneOffset), (centery +0.5F*DeadzoneOffset),paint);
        canvas.drawRect( (centerx -0.5F*DeadzoneOffset), (centery -1.5F*DeadzoneOffset),
                 (centerx +0.5F*DeadzoneOffset), (centery +1.5F*DeadzoneOffset),paint);

    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean up=false;
        boolean down=false;
        boolean left=false;
        boolean right=false;
        if (checkOutsideDeadzone(motionEvent)){
            float x=motionEvent.getX() - (float)getWidth() / 2;
            float y=motionEvent.getY() - (float)getHeight() / 2;
            if (motionEvent.getAction()==MotionEvent.ACTION_DOWN || motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                if (Math.abs(x)<-2*y)up=true;
                if (Math.abs(x)<2*y)down=true;
                if (Math.abs(y)<2*x)right=true;
                if (Math.abs(y)<-2*x)left=true;
            }
            //System.out.println(Boolean.toString(up)+down+left+right);
        }

        if (ic ==null) return true;
        updateDirection(up,previousUpState,KeyEvent.KEYCODE_DPAD_UP);
        updateDirection(down,previousDownState,KeyEvent.KEYCODE_DPAD_DOWN);
        updateDirection(left,previousLeftState,KeyEvent.KEYCODE_DPAD_LEFT);
        updateDirection(right,previousRightState,KeyEvent.KEYCODE_DPAD_RIGHT);
        previousUpState=up;
        previousDownState=down;
        previousLeftState=left;
        previousRightState=right;
        return true;
    }
    public void updateDirection(boolean state, boolean old, int keycode) {
        if (state==old)return;
        if (state)ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,keycode));
        else ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,keycode));
    }
    }
