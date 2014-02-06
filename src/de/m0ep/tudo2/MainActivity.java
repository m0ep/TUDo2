package de.m0ep.tudo2;

import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import de.m0ep.tudo2.fragment.ListDayTaskFragment;
import de.m0ep.tudo2.provider.TaskProvider;

public class MainActivity extends FragmentActivity {
	private static final int PAGE_RIGHT = 2;
	private static final int PAGE_LEFT = 0;
	private static final int PAGE_CENTER = 1;
	private static final String TAG = MainActivity.class.getName();
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ListDayTaskFragment[] fragments = new ListDayTaskFragment[3];

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById( R.id.pager );
		mViewPager.setAdapter( mSectionsPagerAdapter );
		mViewPager.setCurrentItem( 1 );

		Calendar curCal = Calendar.getInstance( Locale.getDefault() );
		curCal.setTimeInMillis( System.currentTimeMillis() );
		fragments[PAGE_CENTER] = new ListDayTaskFragment();
		fragments[PAGE_CENTER].setCalendar( curCal );

		Calendar prvCal = (Calendar) curCal.clone();
		prvCal.add( Calendar.DAY_OF_YEAR, -1 );
		fragments[PAGE_LEFT] = new ListDayTaskFragment();
		fragments[PAGE_LEFT].setCalendar( prvCal );

		Calendar nxtCal = (Calendar) curCal.clone();
		nxtCal.add( Calendar.DAY_OF_YEAR, 1 );
		fragments[PAGE_RIGHT] = new ListDayTaskFragment();
		fragments[PAGE_RIGHT].setCalendar( nxtCal );

		mViewPager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
			private int focusedPage;

			@Override
			public void onPageSelected( int position ) {
				focusedPage = position;
			}

			@Override
			public void onPageScrolled( int arg0, float arg1, int arg2 ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged( int state ) {
				Log.v( TAG, "onPageScrollStateChanged(" + state + ")" );
				if ( ViewPager.SCROLL_STATE_IDLE == state ) {
					if ( 0 == focusedPage ) {
						Calendar cal = (Calendar) fragments[PAGE_LEFT].getCalendar().clone();
						cal.add( Calendar.DAY_OF_YEAR, -1 );

						fragments[PAGE_RIGHT].setCalendar( fragments[PAGE_CENTER].getCalendar() );
						fragments[PAGE_CENTER].setCalendar( fragments[PAGE_LEFT].getCalendar() );
						fragments[PAGE_LEFT].setCalendar( cal );
					} else if ( 2 == focusedPage ) {
						Calendar cal = (Calendar) fragments[PAGE_RIGHT].getCalendar().clone();
						cal.add( Calendar.DAY_OF_YEAR, 1 );

						fragments[PAGE_LEFT].setCalendar( fragments[PAGE_CENTER].getCalendar() );
						fragments[PAGE_CENTER].setCalendar( fragments[PAGE_RIGHT].getCalendar() );
						fragments[PAGE_RIGHT].setCalendar( cal );
					}

					mViewPager.setCurrentItem( 1, false );
				}
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

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter( FragmentManager fm ) {
			super( fm );
		}

		@Override
		public Fragment getItem( int position ) {
			return fragments[position];
		}

		@Override
		public Object instantiateItem( ViewGroup container, int position ) {
			return super.instantiateItem( container, position );
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle( int position ) {
			return TaskProvider.formatDate( fragments[position].getCalendar().getTime() );
		}
	}
}
