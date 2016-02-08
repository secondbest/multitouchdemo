package nl.hu.zrb;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.FloatMath;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MultiTouch extends Activity implements OnTouchListener {
	// Matrix instances to move and zoom image
	Matrix matrix = new Matrix();
	Matrix eventMatrix = new Matrix();

	// possible touch states
	final static int NONE = 0;
	final static int DRAG = 1;
	final static int ZOOM = 2;
	int touchState = NONE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ImageView view = (ImageView) findViewById(R.id.imageView);
		view.setOnTouchListener(this);
	}

	final static float MIN_DIST = 50;
	float eventDistance = 0;
	float eventX =0, eventY = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			//primary touch event starts: remember touch down location
			touchState = DRAG;
			eventX = event.getX(0);
			eventY = event.getY(0);
			break;

		case MotionEvent.ACTION_POINTER_DOWN:
			//secondary touch event starts: remember distance and center
			eventDistance = calcDistance(event);
			calcMidpoint(event);
			if (eventDistance > MIN_DIST) 
				touchState = ZOOM;
			else
				touchState = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (touchState == DRAG) {
				//single finger drag, translate accordingly
				matrix.set(eventMatrix);
				matrix.postTranslate(event.getX(0) - eventX,
						event.getY(0) - eventY);

			} else if (touchState == ZOOM) {
				//multi-finger zoom, scale accordingly around center
				float dist = calcDistance(event);

				if (dist > MIN_DIST) {
					matrix.set(eventMatrix);
					float scale = dist / eventDistance;
					matrix.postScale(scale, scale, eventX, eventY);										
				}
								
			}

			// Perform the transformation
			view.setImageMatrix(matrix);
			break;

		case MotionEvent.ACTION_UP:
			eventMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			eventMatrix.set(matrix);
			touchState = NONE;
			break;
		}

		return true;
	}

	private float calcDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	private void calcMidpoint(MotionEvent event) {
		eventX = (event.getX(0) + event.getX(1))/2;
		eventY = (event.getY(0) + event.getY(1))/2;
	}
}


