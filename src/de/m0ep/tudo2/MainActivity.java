package de.m0ep.tudo2;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import de.m0ep.tudo2.fragment.ListDayTaskFragment;

public class MainActivity extends FragmentActivity {

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
	Calendar[] time = new Calendar[3];
	DateFormat dateFormat;

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

		dateFormat = android.text.format.DateFormat.getDateFormat( getApplicationContext() );

		time[1] = Calendar.getInstance( Locale.getDefault() );
		time[1].setTimeInMillis( System.currentTimeMillis() );

		time[0] = (Calendar) time[1].clone();
		time[0].add( Calendar.DAY_OF_YEAR, -1 );

		time[2] = (Calendar) time[1].clone();
		time[2].add( Calendar.DAY_OF_YEAR, 1 );

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
				if ( ViewPager.SCROLL_STATE_IDLE == state ) {
					final Calendar oldLeftTime = time[0];
					final Calendar oldCenterTime = time[1];
					final Calendar oldRightTime = time[2];

					if ( 0 == focusedPage ) {
						final Calendar tmp = (Calendar) oldLeftTime.clone();
						tmp.add( Calendar.DAY_OF_YEAR, -1 );

						time[0] = tmp;
						time[1] = oldLeftTime;
						time[2] = oldCenterTime;
					} else if ( 2 == focusedPage ) {
						final Calendar tmp = (Calendar) oldRightTime.clone();
						tmp.add( Calendar.DAY_OF_YEAR, 1 );

						time[0] = oldCenterTime;
						time[1] = oldRightTime;
						time[2] = tmp;
					}

					mViewPager.setCurrentItem( 1, false );
				}
			}
		} );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( R.id.action_add == item.getItemId() ) {
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
			ListDayTaskFragment fragment = new ListDayTaskFragment();
			fragment.setDate( time[position].getTime() );
			fragment.setArguments( null );
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle( int position ) {
			return dateFormat.format( time[position].getTime() );
		}
	}
}
