package de.m0ep.tudo2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class TaskListOnTouchListener implements OnTouchListener {
	private float downX;
	private float downY;
	private boolean isSwiping;

	private final int touchSlop;
	private final ViewConfiguration vc;
	private final int animationTime;
	private View downView;

	private boolean enable = true;

	private final ListView listView;

	private boolean swipeEnabled = true;

	private final TaskTouchActionCallback callback;

	private CheckForLongClick pendingCheckForLongClick;

	public static interface TaskTouchActionCallback {
		public void onMoveTask( int postition );

		public void onSelectTask( int postition );
	}

	public TaskListOnTouchListener( ListView listView, TaskTouchActionCallback callback ) {
		this.listView = listView;
		this.callback = callback;
		this.vc = ViewConfiguration.get( listView.getContext() );
		this.touchSlop = vc.getScaledTouchSlop();
		this.animationTime = listView.getContext().getResources().getInteger(
		        android.R.integer.config_mediumAnimTime );
	}

	public void setEnable( boolean enable ) {
		this.enable = enable;
	}

	public void setSwipeEnable( boolean enable ) {
		this.swipeEnabled = enable;
	}

	public OnScrollListener getScrollListener() {
		return new OnScrollListener() {

			@Override
			public void onScrollStateChanged( AbsListView view, int scrollState ) {
				setEnable( scrollState != AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL );
			}

			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount,
			        int totalItemCount ) {
			}
		};
	}

	@Override
	public boolean onTouch( final View v, final MotionEvent event ) {
		listView.onTouchEvent( event );

		if ( !enable ) {
			return false;
		}

		switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN: {

				// Find the child view that was touched (perform a hit test)
				Rect rect = new Rect();
				int childCount = listView.getChildCount();
				int[] listViewCoords = new int[2];
				listView.getLocationOnScreen( listViewCoords );
				int x = (int) event.getRawX() - listViewCoords[0];
				int y = (int) event.getRawY() - listViewCoords[1];
				View child;
				for ( int i = 0; i < childCount; i++ ) {
					child = listView.getChildAt( i );
					child.getHitRect( rect );
					if ( rect.contains( x, y ) ) {
						downView = child;
						break;
					}
				}

				if ( null != downView ) {
					downX = event.getRawX();
					downY = event.getRawY();
				}

				if ( null != pendingCheckForLongClick ) {
					listView.removeCallbacks( pendingCheckForLongClick );
				}

				pendingCheckForLongClick = new CheckForLongClick();
				listView.postDelayed(
				        pendingCheckForLongClick,
				        ViewConfiguration.getLongPressTimeout() );

				return false;
			}
			case MotionEvent.ACTION_CANCEL: {
				if ( null == downView ) {
					break;
				}

				if ( null != downView && isSwiping ) {
					downView.animate()
					        .alpha( 1 )
					        .translationX( 0 )
					        .setDuration( animationTime )
					        .setListener( null );
				}

				downX = 0;
				downY = 0;
				downView = null;
				isSwiping = false;
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				if ( null == downView ) {
					break;
				}

				float deltaX = event.getRawX() - downX;
				float deltaY = event.getRawY() - downY;

				if ( swipeEnabled
				        && 0 < deltaX
				        && deltaX > touchSlop
				        && deltaX > deltaY ) {
					isSwiping = true;
					listView.requestDisallowInterceptTouchEvent( true );
				}

				if ( isSwiping && 0 < deltaX ) {
					downView.setTranslationX( deltaX - touchSlop );
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				if ( null == downView ) {
					break;
				}

				float x = event.getRawX() + downView.getTranslationX();
				float deltaX = x - downX;

				if ( isSwiping ) {
					if ( deltaX > downView.getWidth() / 2 ) {
						final View movedView = downView;
						downView.animate()
						        .translationX( downView.getWidth() )
						        .alpha( 0 )
						        .setDuration( animationTime )
						        .setListener( new AnimatorListenerAdapter() {
							        @Override
							        public void onAnimationEnd( Animator animation ) {
								        performMoveTask(
								                movedView,
								                listView.getPositionForView( movedView ) );
							        }
						        } );
					} else {
						downView.animate()
						        .translationX( 0 )
						        .alpha( 1 )
						        .setListener( null );
					}
				}

				downX = 0;
				downY = 0;
				downView = null;
				isSwiping = false;
				break;
			}

		}

		return false;
	}

	protected void performMoveTask( final View view, final int position ) {
		final ViewGroup.LayoutParams lp = view.getLayoutParams();
		final int viewOriginalHight = view.getHeight();

		final ValueAnimator animator = ValueAnimator
		        .ofInt( viewOriginalHight, 1 )
		        .setDuration( animationTime );

		animator.addListener( new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd( Animator animation ) {
				view.setAlpha( 1 );
				view.setTranslationX( 0 );
				lp.height = viewOriginalHight;
				view.setLayoutParams( lp );

				if ( null != callback ) {
					callback.onMoveTask( position );
				}
			}
		} );

		animator.addUpdateListener( new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate( ValueAnimator animation ) {
				lp.height = (Integer) animator.getAnimatedValue();
				view.setLayoutParams( lp );
			}
		} );

		animator.start();
	}

	class CheckForLongClick implements Runnable {
		@Override
		public void run() {
			if ( !isSwiping && null != downView ) {
				if ( null != callback ) {
					callback.onSelectTask( listView.getPositionForView( downView ) );
				}

				downX = 0;
				downY = 0;
				downView = null;
				isSwiping = false;
				listView.performHapticFeedback( HapticFeedbackConstants.LONG_PRESS );
				listView.playSoundEffect( SoundEffectConstants.CLICK );
				pendingCheckForLongClick = null;
			}
		}
	}
}