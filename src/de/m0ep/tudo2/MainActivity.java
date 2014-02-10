package de.m0ep.tudo2;

import java.util.Arrays;
import java.util.Calendar;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	private static final String TAG = MainActivity.class.getName();

	private java.text.DateFormat dateFormat;
	private Calendar mSelectedDate;

	private Button buttonPrevDate;
	private Button buttonNextDate;
	private TextView textCurDate;
	private ListView listTasks;

	String[] data;

	boolean isItemPressed;
	boolean isSwiping;

	OnTouchListener touchListener = new OnTouchListener() {
		float downX;
		int swipeSlop = -1;

		@Override
		public boolean onTouch( View v, MotionEvent event ) {
			if ( 0 > swipeSlop ) {
				swipeSlop = ViewConfiguration.get( MainActivity.this )
				        .getScaledTouchSlop();
			}

			switch ( event.getAction() ) {
				case MotionEvent.ACTION_DOWN:
					if ( isItemPressed ) {
						return false;
					}

					downX = event.getX();
					isItemPressed = true;
					break;
				case MotionEvent.ACTION_MOVE:
					float x = event.getX() + v.getTranslationX();
					float deltaX = x - downX;

					if ( 0 < deltaX ) {
						if ( !isSwiping ) {
							if ( swipeSlop < deltaX ) {
								isSwiping = true;
								listTasks.requestDisallowInterceptTouchEvent( true );
							}
						} else {
							v.setTranslationX( deltaX );
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					if ( isSwiping ) {
						v.setTranslationX( 0 );
					}

					isItemPressed = false;
					isSwiping = false;
					break;
				case MotionEvent.ACTION_CANCEL:
					v.setTranslationX( 0 );
					isItemPressed = false;
					isSwiping = false;
					break;

				default:
					return false;
			}

			return true;
		}
	};

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		data = new String[20];
		for ( int i = 0; i < data.length; i++ ) {
			data[i] = Integer.toString( i );
		}

		buttonPrevDate = (Button) findViewById( R.id.button_prev_date );
		buttonNextDate = (Button) findViewById( R.id.button_next_date );
		textCurDate = (TextView) findViewById( R.id.text_cur_date );
		listTasks = getListView();

		StableArrayAdapter stableArrayAdapter = new StableArrayAdapter(
		        this,
		        android.R.layout.simple_list_item_1,
		        Arrays.asList( Cheeses.sCheeseStrings ),
		        touchListener );

		setListAdapter( stableArrayAdapter );

		dateFormat = DateFormat.getDateFormat( this );
		mSelectedDate = Calendar.getInstance();
		textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );

		buttonPrevDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mSelectedDate.add( Calendar.DAY_OF_YEAR, -1 );
				textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );
			}
		} );

		buttonNextDate.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick( View v ) {
				mSelectedDate.add( Calendar.DAY_OF_YEAR, 1 );
				textCurDate.setText( dateFormat.format( mSelectedDate.getTime() ) );
			}
		} );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		int id = item.getItemId();

		if ( R.id.action_add == id ) {
			Intent intent = new Intent( this, TaskActivity.class );
			startActivity( intent );
		}

		return super.onOptionsItemSelected( item );
	}
}
