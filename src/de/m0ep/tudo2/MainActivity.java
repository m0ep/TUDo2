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
import de.m0ep.tudo2.provider.TaskProvider;

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
	PageModel[] pageModels = new PageModel[3];
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

		Calendar curCal = Calendar.getInstance( Locale.getDefault() );
		curCal.setTimeInMillis( System.currentTimeMillis() );
		pageModels[1] = new PageModel( curCal );

		Calendar prvCal = (Calendar) curCal.clone();
		prvCal.add( Calendar.DAY_OF_YEAR, -1 );
		pageModels[0] = new PageModel( prvCal );

		Calendar nxtCal = (Calendar) curCal.clone();
		nxtCal.add( Calendar.DAY_OF_YEAR, 1 );
		pageModels[2] = new PageModel( nxtCal );

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
					if ( 0 == focusedPage ) {
						PageModel oldLeft = pageModels[0];

						Calendar cal = (Calendar) oldLeft.date.clone();
						cal.add( Calendar.DAY_OF_YEAR, -1 );
						PageModel newLeft = new PageModel( cal );

						pageModels[2] = pageModels[1];
						pageModels[1] = oldLeft;
						pageModels[0] = newLeft;
					} else if ( 2 == focusedPage ) {
						PageModel oldRight = pageModels[2];

						Calendar cal = (Calendar) oldRight.date.clone();
						cal.add( Calendar.DAY_OF_YEAR, 1 );
						PageModel newRight = new PageModel( cal );

						pageModels[0] = pageModels[1];
						pageModels[1] = oldRight;
						pageModels[2] = newRight;
					}

					System.out.println( TaskProvider.formatDate( pageModels[0].date.getTime() ) );
					System.out.println( TaskProvider.formatDate( pageModels[1].date.getTime() ) );
					System.out.println( TaskProvider.formatDate( pageModels[2].date.getTime() ) );

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

	private static class PageModel {
		public Calendar date;
		public ListDayTaskFragment fragment;

		public PageModel( Calendar date, ListDayTaskFragment fragment ) {
			this.date = date;
			this.fragment = fragment;
		}

		public PageModel( Calendar date ) {
			this.date = date;
			this.fragment = new ListDayTaskFragment();
			this.fragment.setDate( date.getTime() );
		}
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
			return pageModels[position].fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle( int position ) {
			return dateFormat.format(
			        pageModels[position]
			        .date
			                .getTime() );
		}
	}
}
