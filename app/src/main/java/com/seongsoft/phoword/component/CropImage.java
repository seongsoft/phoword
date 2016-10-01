package com.seongsoft.phoword.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.media.ExifInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.seongsoft.phoword.tesseract.DataPath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by BeINone on 2016-09-22.
 */

public class CropImage extends FrameLayout implements View.OnTouchListener {

    private final static String TAG = "CropImageView";

    private final static int HEIGHT_OF_DRAGGER = 45;
    private final static int INITIAL_MARGIN_OF_DRAGGER = 60;

    private ImageView mImageView;
    private ImageView mTopLeftDragger;
    private ImageView mBottomRightDragger;
    private ImageView mDaBox;

    private Drawable mDraggerDrawable;
    private  Drawable mCropDrawable;

    private boolean mKeepSquare;

    private Bitmap mBitmap;
    int width;
    int height;
    int bitmapWidth = 0;
    int bitmapHeight = 0;
    int bitmapMargin = 0;       //bitmap의 실제 Width or Height 를 제외한 Margin
    int absoluteBitmapSize = 0;     // bitmap의 실제 뷰에서의 가로 or 세로
    double bitmapRatio = 0;
    double WidthRatio = 0;
    double HeightRatio = 0;
    int mTopLeftDraggerLeftMargin;
    int mTopLeftDraggerTopMargin;
    int mBottomRightDraggerRightMargin;
    int mBottomRightDraggerBottomMargin;
    final int baseMargin = 30;

    int imageWidth;
    int imageHeight;
    int viewWidth;
    int viewHeight;

    public CropImage(Context context) {
        super(context);
        init();
    }

    public CropImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public CropImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public void setImageBitmap(Bitmap bm) {
        if (mImageView != null) {
                mBitmap = bm;
                ExifInterface exif = null;
            try {
                  exif = new ExifInterface(DataPath.PHOTO_PATH);
                } catch (IOException e) {

                }
            mImageView.setImageBitmap(bm);

            rotateByOrientation(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
        }
    }

    private void rotateByOrientation(String orientation){
        switch (orientation){
            case "1":
                break;
            case "3" :
                rotateImage(180);
                break;
            case "6" :
                rotateImage(90);
                break;
            case "8" :
                rotateImage(-90);
        }
    }

    private void getImageSize(){
        imageWidth = mBitmap.getWidth();
        imageHeight = mBitmap.getHeight();
    }

    public void setCornerDrawable(int width, int height) {
        mDraggerDrawable = this.getCircleDrawable(width, height);

        if (mTopLeftDragger != null) {
            mTopLeftDragger.setImageDrawable(mDraggerDrawable);
        }

        if (mBottomRightDragger != null) {
            mBottomRightDragger.setImageDrawable(mDraggerDrawable);
        }
    }

    public void setKeepSquare(boolean keepSquare) {
        mKeepSquare = keepSquare;

        if (mTopLeftDragger != null && mBottomRightDragger != null) {
            Log.d(TAG, "Draggers exist");
        } else {
            Log.d(TAG, "Draggers don't exist");
        }
    }

    public void setCropAreaDrawable(int fillColor, int fillAlpha, int strokeColor, int strokeAlpha, int strokeWidth) {
        mCropDrawable = this.getCropDrawable(fillColor, fillAlpha, strokeColor, strokeAlpha, strokeWidth);

        if (mDaBox != null) {
            if (android.os.Build.VERSION.SDK_INT < 16) {
                mDaBox.setBackgroundDrawable(mCropDrawable);
            } else {
                mDaBox.setBackground(mCropDrawable);
            }
        }
    }

    public void setImagePath(String path){
        try {
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 2;
            this.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
   /*public void setImageResource(Resources resources, int resId) {
      Log.d("decodeResorce : ", String.valueOf(BitmapFactory.decodeResource(getResources(), resId)));
      this.setImageBitmap(BitmapFactory.decodeResource(getResources(), resId));
   }*/

    public Bitmap crop(Context context) throws IllegalArgumentException {
        int x, y, width, height;

        LayoutParams params = (LayoutParams) mDaBox.getLayoutParams();

        setBitmapInform();
        if(WidthRatio < HeightRatio) {     //bitmap의 세로 길이가 가로 길이보다 길때
            int left = mDaBox.getLeft() - (bitmapMargin/2);
            x = ((mBitmap.getWidth() * left)/absoluteBitmapSize);
            y = ((mBitmap.getHeight() * mDaBox.getTop()/this.getHeight()));
            width = (mDaBox.getWidth()*mBitmap.getWidth())/absoluteBitmapSize;
            height = (mBitmap.getHeight() * mDaBox.getHeight())/this.getHeight();
            if(x < 0 )
                x = 0;
            if(y < 0)
                y = 0;
        } else{
            int top = mDaBox.getTop() - (bitmapMargin/2);
            x = (int)((mBitmap.getWidth() * mDaBox.getLeft())/this.getWidth());
            y = (int)((mBitmap.getHeight() * top)/absoluteBitmapSize);
            width = (int)((mBitmap.getWidth() * mDaBox.getWidth())/this.getWidth());
            height = (mDaBox.getHeight()*mBitmap.getHeight())/absoluteBitmapSize;
            if(x < 0 )
                x = 0;
            if(y < 0)
                y = 0;

        }
        Bitmap crooopppppppppppppppeed = Bitmap.createBitmap(mBitmap, x, y, width, height);

        return crooopppppppppppppppeed;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {            //이 메소드가 호출되면서 뷰의 사이즈가 정해진다.
        super.onWindowFocusChanged(hasFocus);
        viewWidth = getWidth();
        viewHeight = getHeight();
        System.out.println("ViewSize : " + viewWidth);
        setBitmapInform();
        setCropmargin();
        System.out.println("im" +
                "ageSize : " + imageWidth);
        if (mTopLeftDragger == null) {

            //int leftmargin = INITIAL_MARGIN_OF_DRAGGER;
            //int topmargin = INITIAL_MARGIN_OF_DRAGGER;
            mTopLeftDragger = new ImageView(this.getContext());
            mTopLeftDragger.setImageDrawable(mDraggerDrawable);
            mTopLeftDragger.setOnTouchListener(this);



            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(mTopLeftDraggerLeftMargin, mTopLeftDraggerTopMargin, 0, 0);
            this.addView(mTopLeftDragger, params);
        }

        if (mBottomRightDragger == null) {
            mBottomRightDragger = new ImageView(this.getContext());
            mBottomRightDragger.setImageDrawable(mDraggerDrawable);
            mBottomRightDragger.setOnTouchListener(this);

            width = this.getWidth();
            height = this.getHeight();

            int leftMargin = width - INITIAL_MARGIN_OF_DRAGGER - HEIGHT_OF_DRAGGER;
            int topMargin = height - INITIAL_MARGIN_OF_DRAGGER - HEIGHT_OF_DRAGGER;
            int rightMargin = 0;
            int bottomMargin = 0;

            if (mKeepSquare) {
                int smallestMargin = Math.min(leftMargin, topMargin);
                leftMargin = smallestMargin;
                topMargin = smallestMargin;
            }

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(mBottomRightDraggerRightMargin-30, mBottomRightDraggerBottomMargin-30, -25, -25 );        //Border에 닿았을 때 right에서 0으로 Margin을 주고 있다는 것은 그만큼 밀고 있다는 것이다.
            this.addView(mBottomRightDragger, params);
        }

        if (mDaBox == null) {
            mDaBox = new ImageView(this.getContext());
//       mDaBox.setImageDrawable(this.getCropDrawable());
            if (android.os.Build.VERSION.SDK_INT < 16) {
                mDaBox.setBackgroundDrawable(mCropDrawable);
            } else {
                mDaBox.setBackground(mCropDrawable);
            }
            mDaBox.setScaleType(ImageView.ScaleType.MATRIX);
            mDaBox.setAdjustViewBounds(true);
            mDaBox.setOnTouchListener(this);
//       mDaBox.setBackgroundColor(Color.WHITE);

//       FrameLayout.LayoutParams paramsTopLeft = (LayoutParams) mTopLeftDragger.getLayoutParams();
//       FrameLayout.LayoutParams paramsBottomRight = (LayoutParams) mBottomRightDragger.getLayoutParams();
//
//       int leftMargin = (int) (paramsTopLeft.leftMargin + (HEIGHT_OF_DRAGGER/2.0));
//       int topMargin = (int) (paramsTopLeft.topMargin + (HEIGHT_OF_DRAGGER/2.0));
//       int rightMargin = (int) (paramsBottomRight.rightMargin + HEIGHT_OF_DRAGGER + INITIAL_MARGIN_OF_DRAGGER);
//       int bottomMargin = (int) (paramsBottomRight.bottomMargin + HEIGHT_OF_DRAGGER + INITIAL_MARGIN_OF_DRAGGER);
//
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//       params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            this.addView(mDaBox, 1, params);
            moveCrop();
        }
    }
    public void setCropmargin(){
        if(WidthRatio < HeightRatio){
            mTopLeftDraggerLeftMargin = viewWidth - absoluteBitmapSize + baseMargin;
            mTopLeftDraggerTopMargin = 0 + baseMargin;
            mBottomRightDraggerRightMargin = viewWidth - mTopLeftDraggerLeftMargin;
            mBottomRightDraggerBottomMargin = viewHeight - baseMargin;
        }else{
            mTopLeftDraggerLeftMargin = 0 + baseMargin;
            mTopLeftDraggerTopMargin = (viewHeight- absoluteBitmapSize)/2 + baseMargin;
            mBottomRightDraggerRightMargin = viewWidth - baseMargin;
            mBottomRightDraggerBottomMargin = viewHeight - mTopLeftDraggerTopMargin;
        }
        System.out.println(mTopLeftDraggerLeftMargin + ", " + mTopLeftDraggerTopMargin +
                ", " + mBottomRightDraggerRightMargin + ", " + mBottomRightDraggerBottomMargin);
    }

    private void init() {
        mDraggerDrawable = this.getCircleDrawable(Color.rgb(0, 150, 136), HEIGHT_OF_DRAGGER, HEIGHT_OF_DRAGGER);
        mCropDrawable = this.getCropDrawable(Color.LTGRAY, 150, Color.LTGRAY, 255, 8);

        if (mImageView == null) {
            mImageView = new ImageView(this.getContext());

            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(mImageView, params);
        }
        this.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        System.out.println("View Size" + this.width);
    }

    @Override
    protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {      //이 콜백 메소드는 뷰그룹에서 하위 뷰들에 대해 크기와 위치를 할당
        super.onLayout(changed, left, top, right, bottom);
    }

    LayoutParams parms;
    // LinearLayout.LayoutParams par;
    float dx=0,dy=0,x=0,y=0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN :
            {

//            if (v == mTopLeftDragger || v == mBottomRightDragger) {
                parms = (LayoutParams) v.getLayoutParams();
                System.out.println("getX, getY : " + event.getX() + ", " + event.getY());

                System.out.println("RawX, RawY : " + event.getRawX() + ", " + event.getRawY());
                dx = event.getRawX() - parms.leftMargin;
                dy = event.getRawY() - parms.topMargin;

                Log.d("dx, dy : ", (event.getRawY() - parms.topMargin) + "," + (event.getRawX() - parms.leftMargin));
                Log.d("getRawY, getRawX : ", event.getRawX() + "," + event.getRawY());
//            } else if (v == mDaBox) {
//
//            }
            }
            break;
            case MotionEvent.ACTION_MOVE : {
                x = event.getRawX();
                y = event.getRawY();

                if (v == mTopLeftDragger ||  v == mBottomRightDragger) {
                    int leftMargin = (int) (x - dx);
                    int topMargin = (int) (y - dy);
                    LayoutParams paramsBottomRight;
                    parms.leftMargin = leftMargin;
                    parms.topMargin = topMargin;
                    //크기 조정 시 화면 밖으로 이탈시에 크기 조정
                    if (WidthRatio < HeightRatio) {
                        if (leftMargin < bitmapMargin / 2) {
                            parms.leftMargin = (bitmapMargin / 2) - 25;
                        }
                        if (topMargin < 0) {
                            parms.topMargin = -25;
                        }
                        int Margin =  viewWidth - (bitmapMargin /2);
                        if(leftMargin > Margin + 25){
                            parms.leftMargin = Margin - 25;
                        }
                        if(topMargin > viewHeight + 25){
                            parms.topMargin = viewHeight - 25;
                        }
                    } else {
                        if (leftMargin < 0) {
                            parms.leftMargin = -25;
                        }
                        if (topMargin < bitmapMargin / 2) {
                            parms.topMargin = (bitmapMargin / 2) - 25;
                        }
                        else if(leftMargin > viewWidth + 25) {
                            parms.leftMargin = viewWidth - 25;
                        }

                        int Margin =  viewHeight - (bitmapMargin /2);

                        if(topMargin > Margin + 25){
                            parms.topMargin = Margin- 25;
                        }
                    }
                    v.setLayoutParams(parms);
                    moveCrop();

                } else if (v == mDaBox) {
                    int boxWidth = 0;
                    int leftMargin = (int) (x - dx);
                    int topMargin = (int) (y - dy);
                    int dLeft = leftMargin - parms.leftMargin;
                    int dTop = topMargin - parms.topMargin;
                    Log.d("dLeft, dTop :  ", dLeft + ", " + dTop);

                    LayoutParams topLeftParams = (LayoutParams) mTopLeftDragger.getLayoutParams();              //Dragger의 Parms
                    LayoutParams bottomRightParams = (LayoutParams) mBottomRightDragger.getLayoutParams();

                    parms.leftMargin = leftMargin;
                    parms.topMargin = topMargin;

                    if(WidthRatio > HeightRatio) {
                        if (leftMargin <= 0) {                      //Box가 좌측 Border을 넘었을 때s
                            //parms.rightMargin -= 0 - parms.leftMargin;
                            System.out.println("예외 : " + (mDaBox.getWidth() - boxWidth));
                            parms.leftMargin = 0;               //left Margin을 0으로 해주고 아니면 그대로 적용
                            topLeftParams.leftMargin = -25;      //TopLeftDragger를 왼쪽 Border에 위치
                        } else {
                            parms.rightMargin -= dLeft;         //Border를 넘겼을때 최종 rightMargin로 적용
                            topLeftParams.leftMargin += dLeft;
                            bottomRightParams.leftMargin += dLeft;
                        }
                        if (topMargin <= (viewHeight - absoluteBitmapSize) / 2) {
                            parms.topMargin = (viewHeight - absoluteBitmapSize) / 2;
                            topLeftParams.topMargin = (viewHeight - absoluteBitmapSize) / 2 - 25;
                        } else {
                            parms.bottomMargin -= dTop;
                            topLeftParams.topMargin += dTop;
                            bottomRightParams.topMargin += dTop;
                        }
                        if (leftMargin + mDaBox.getWidth() >= viewWidth) {
                            parms.rightMargin = 0;
                            parms.leftMargin = viewWidth - mDaBox.getWidth();
                            topLeftParams.leftMargin = parms.leftMargin - 25;
                            bottomRightParams.leftMargin = viewWidth - 25;
                        }
                        if (topMargin + mDaBox.getHeight() >= viewHeight - bitmapMargin / 2) {
                            parms.bottomMargin = bitmapMargin / 2;
                            parms.topMargin = viewHeight - bitmapMargin / 2 - mDaBox.getHeight();
                            topLeftParams.topMargin = viewHeight - bitmapMargin / 2 - mDaBox.getHeight() - 25;
                            bottomRightParams.topMargin = viewHeight - bitmapMargin / 2 - 25;
                        }
                    }else{
                        if(leftMargin < viewWidth / 2 - absoluteBitmapSize / 2 ) {
                            parms.leftMargin = viewWidth / 2 - absoluteBitmapSize / 2;
                            topLeftParams.leftMargin = parms.leftMargin -25;
                        }else{
                            parms.rightMargin -= dLeft;         //Border를 넘겼을때 최종 rightMargin로 적용
                            topLeftParams.leftMargin += dLeft;
                            bottomRightParams.leftMargin += dLeft;
                        }
                        if(topMargin < 0){
                            parms.topMargin = 0;
                            topLeftParams.topMargin = -30;
                        }else{
                            parms.bottomMargin -= dTop;
                            topLeftParams.topMargin += dTop;
                            bottomRightParams.topMargin += dTop;
                        }
                        if(leftMargin + mDaBox.getWidth() >= viewWidth - bitmapMargin / 2 ){
                            Log.d(TAG, "넘음");
                            parms.rightMargin = bitmapMargin / 2 ;
                            parms.leftMargin = viewWidth- bitmapMargin / 2 - mDaBox.getWidth();
                            topLeftParams.leftMargin= viewWidth- bitmapMargin / 2 - mDaBox.getWidth() - 25;
                            bottomRightParams.leftMargin= viewWidth- bitmapMargin / 2 - 25;
                        }
                        if (topMargin + mDaBox.getHeight() >= viewHeight) {
                            parms.bottomMargin = 0;
                            parms.topMargin = viewHeight- mDaBox.getHeight();
                            topLeftParams.topMargin= parms.topMargin - 25;
                            bottomRightParams.topMargin = viewHeight - 25;
                        }

                        // parms.rightMargin -= dLeft;
                        // topLeftParams.leftMargin += dLeft;
                        //bottomRightParams.leftMargin += dLeft;

                    }
//                            parms.leftMargin = viewWidth / 2 - mDaBox.getWidth() / 2;
//                            topLeftParams.leftMargin = parms.leftMargin -25;
//                        }else{
//                            parms.rightMargin -= dLeft;         //Border를 넘겼을때 최종 rightMargin로 적용
//                            topLeftParams.leftMargin += dLeft;
//                            bottomRightParams.leftMargin += dLeft;
//                        }
//                    }
//                    }else{
//                        if(leftMargin < viewWidth / 2 - mDaBox.getWidth() / 2 ){
//                            parms.leftMargin = viewWidth / 2 - mDaBox.getWidth() / 2;
//                            topLeftParams.leftMargin = parms.leftMargin -25;
//                        }else{
//
//                        }
//                    }
                    v.setLayoutParams(parms);

                    mTopLeftDragger.setLayoutParams(topLeftParams);
                    mBottomRightDragger.setLayoutParams(bottomRightParams);
                }
                //Log.d(TAG, bitmapMargin + ", " + absoluteBitmapSize + ", " + viewHeight);
            }
            break;
            case MotionEvent.ACTION_UP :
                break;
        }
        return true;
    }

    private void moveCrop() {
        LayoutParams paramsTopLeft = (LayoutParams) mTopLeftDragger.getLayoutParams();
        LayoutParams paramsBottomRight= (LayoutParams)mBottomRightDragger.getLayoutParams();

        int width = this.getWidth();
        int height = this.getHeight();

        int widthOfCorner = mDraggerDrawable.getIntrinsicWidth();
        int heightOfCorner = mDraggerDrawable.getIntrinsicHeight();
        int rightMargin = (int) (width - paramsBottomRight.leftMargin - (widthOfCorner / 2.0f));
        int bottomMargin = (int) (height - paramsBottomRight.topMargin - (heightOfCorner/2.0f));
        int leftMargin = (int) (paramsTopLeft.leftMargin + (widthOfCorner / 2.0f));
        int topMargin = (int) (paramsTopLeft.topMargin + (heightOfCorner / 2.0f));


        LayoutParams params = (LayoutParams) mDaBox.getLayoutParams();
            /*if(rightMargin < 0) {
                System.out.println("오르쪽 넘음!");
                rightMargin = -25;
                params.rightMargin = -25;
                System.out.println("rightMatgin : " + parms.rightMargin);
            }else {
                params.rightMargin = rightMargin;
            }*/
        params.leftMargin = leftMargin;
        params.topMargin = topMargin;
        params.rightMargin = rightMargin;
        params.bottomMargin = bottomMargin;

        mDaBox.setLayoutParams(params);
    }

    private Drawable getCircleDrawable(int color, int width, int height) {
        ShapeDrawable biggerCircle = new ShapeDrawable( new OvalShape());
        biggerCircle.setIntrinsicWidth(width);
        biggerCircle.setIntrinsicHeight(height);
        biggerCircle.setBounds(new Rect(0, 0, width, height));
        biggerCircle.getPaint().setColor(color);

        return biggerCircle;
    }
    private Drawable getCircleDrawable(int width, int height) {
        ShapeDrawable biggerCircle = new ShapeDrawable( new OvalShape());
        biggerCircle.setIntrinsicWidth(width);
        biggerCircle.setIntrinsicHeight(height);
        biggerCircle.setBounds(new Rect(0, 0, width, height));
        biggerCircle.getPaint().setColor(Color.rgb(0, 150, 136));

        return biggerCircle;
    }

    private Drawable getCropDrawable(int fillColor, int fillAlpha, int strokeColor, int strokeAlpha, int strokeWidth) {
        ShapeDrawable sd1 = new ShapeDrawable(new RectShape());
        sd1.getPaint().setColor(strokeColor);
        sd1.getPaint().setStyle(Paint.Style.STROKE);
        sd1.getPaint().setStrokeWidth(strokeWidth);
        sd1.setAlpha(255);

        ShapeDrawable sd2 = new ShapeDrawable(new RectShape());
        sd2.getPaint().setColor(fillColor);
        sd2.getPaint().setStyle(Paint.Style.FILL);
        sd2.setAlpha(fillAlpha);

        Drawable[] layers = new Drawable[2];
        layers[0] = sd1;
        layers[1] = sd2;
        LayerDrawable composite = new LayerDrawable(layers);

        return composite;
    }

    public void setBitmapInform() {
        getImageSize();
        WidthRatio = (double)imageWidth/viewWidth;
        HeightRatio = (double)imageHeight/viewHeight;

        if (WidthRatio < HeightRatio) {
            bitmapWidth = (imageWidth * viewHeight)/imageHeight;
            bitmapMargin= (viewWidth - bitmapWidth);
            absoluteBitmapSize = viewWidth - bitmapMargin;
            Log.d(TAG, "Heith가 상대적으로 더 큼 : " + bitmapWidth + ", " + bitmapMargin);
        } else {
            bitmapHeight = (imageHeight * viewWidth)/imageWidth;
            bitmapMargin= (int)(viewHeight - bitmapHeight);
            absoluteBitmapSize = viewHeight - bitmapMargin;
            Log.d(TAG, "Width가 상대적으로 더 큼 : " + bitmapHeight + ", " + bitmapMargin );

        }
    }

    public void rotateImage(int degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
        setBitmapInform();
        mImageView.setImageBitmap(mBitmap);
    }

}