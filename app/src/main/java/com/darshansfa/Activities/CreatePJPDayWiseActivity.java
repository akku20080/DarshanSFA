package com.darshansfa.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.darshansfa.Adapters.LocalityRecycleAdapter;
import com.darshansfa.Models.Locality;
import com.darshansfa.R;
import com.darshansfa.Utility.Constants;
import com.darshansfa.dbModel.PJPSchedule;
import com.darshansfa.dbModel.Retailer;

public class CreatePJPDayWiseActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String day = Constants.MONDAY;
    private FloatingActionButton fab;
    private ArrayList<Locality> localityArrayList;
    private List<PJPSchedule> pjpSchedules;
    private RecyclerView recyclerView;
    private LocalityRecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pjp);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int dele = PJPSchedule.deleteAll(PJPSchedule.class);
                Log.e("Delete", "Deo --------- + " + dele);
                Log.e("Delete", "pjpSchedules --------- + " + pjpSchedules.size());
                PJPSchedule.saveInTx(pjpSchedules);
                Snackbar.make(view, "PJP Update successfully..", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                fab.setVisibility(View.GONE);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerLocality);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        localityArrayList = new ArrayList<>();
        adapter = new LocalityRecycleAdapter(this, localityArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        localityArrayList = new ArrayList<>();
        pjpSchedules = PJPSchedule.listAll(PJPSchedule.class);

        List<Retailer> retailers = Retailer.findWithQuery(Retailer.class, "Select * from Retailer Group by locality_id ;");
        Log.e("retailers", "------- " + retailers);

        for (int i = 0; i < retailers.size(); i++) {
            Locality locality = new Locality();
            locality.setLocalityId(retailers.get(i).getLocalityId());
            locality.setLocalityName(retailers.get(i).getLocality());
            localityArrayList.add(locality);
        }
        mViewPager.setOffscreenPageLimit(5);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getCurrentDay(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateLocalityForDay();

    }


    @Override
    protected void onResume() {
        super.onResume();
//        ((PlaceholderFragment) mSectionsPagerAdapter.getCurrentFragment()).updateLocalityForDay(Constants.MONDAY, localityArrayList, pjpSchedules);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_pj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_pj, container, false);
            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private Fragment mCurrentFragment;

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    day = Constants.MONDAY;
                    break;
                case 1:
                    day = Constants.TUESDAY;
                    break;
                case 2:
                    day = Constants.WEDNESDAY;
                    break;
                case 3:
                    day = Constants.THURSDAY;
                    break;
                case 4:
                    day = Constants.FRIDAY;
                    break;
                case 5:
                    day = Constants.SATURDAY;
                    break;
                case 6:
                    day = Constants.SUNDAY;
                    break;
            }

            return day;
        }
    }


    public void getCurrentDay(int position) {
        switch (position) {
            case 0:
                day = Constants.MONDAY;
                break;
            case 1:
                day = Constants.TUESDAY;
                break;
            case 2:
                day = Constants.WEDNESDAY;
                break;
            case 3:
                day = Constants.THURSDAY;
                break;
            case 4:
                day = Constants.FRIDAY;
                break;
            case 5:
                day = Constants.SATURDAY;
                break;
            case 6:
                day = Constants.SUNDAY;
                break;

        }
        updateLocalityForDay();

    }


    public void updateLocalityForDay() {
        try {

            Log.e("Day", "Day -==-=-=-===-=-=-=-" + day);
            Log.e("Day", "Day -==-=-=-===-=-=-=-" + pjpSchedules);
            Log.e("Day", "localityArrayListFrag -==-=-=-===-=-=-=-" + localityArrayList);

            List<PJPSchedule> list = new ArrayList<>();
            for (int i = 0; i < pjpSchedules.size(); i++) {
                if (pjpSchedules.get(i).getPjpDay().equalsIgnoreCase(day))
                    list.add(pjpSchedules.get(i));
            }

            Log.e("DayPJp", "****************" + list);
            for (int i = 0; i < localityArrayList.size(); i++) {
                Locality locality = localityArrayList.get(i);
                locality.setSelected(checkForDe(list, localityArrayList.get(i).getLocalityId()));
                localityArrayList.set(i, locality);
            }
            adapter = new LocalityRecycleAdapter(this, localityArrayList);
            recyclerView.setAdapter(adapter);
            adapter.SetOnItemClickListener(new LocalityRecycleAdapter.OnItemClickListener() {
                @Override
                public void updatePJP(Locality localityId, int position) {
                    updateSchedule(localityId);
                }
            });
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkForDe(List<PJPSchedule> pjpSchedules, String id) {
        for (int i = 0; i < pjpSchedules.size(); i++) {
            if (id.equalsIgnoreCase(pjpSchedules.get(i).getLocalityId()))
                return true;
        }
        return false;
    }


    private int checkForPJPExist(List<PJPSchedule> pjpSchedules, Locality locality) {
        Log.e("Day", "d--------------" + day);
        for (int i = 0; i < pjpSchedules.size(); i++) {
            if (locality.getLocalityId().equalsIgnoreCase(pjpSchedules.get(i).getLocalityId())
                    && day.equalsIgnoreCase(pjpSchedules.get(i).getPjpDay()))
                return i;
        }
        return -1;
    }

    private void updateSchedule(Locality locality) {
        int pos = checkForPJPExist(pjpSchedules, locality);
        fab.setVisibility(View.VISIBLE);
        Log.e("Pos", " -- pos : " + pos + "    : " + locality);
        if (pos != -1) {
            if (!locality.isSelected()) {
                Log.e("remove", " -- pos : " + pos + "    : " + pjpSchedules.size());
                pjpSchedules.remove(pos);
                Log.e("remove", " -- pos : " + pos + "    : " + pjpSchedules.size());
            }
        } else {
            if (locality.isSelected()) {
                PJPSchedule schedule = new PJPSchedule();
//                schedule.setLocalityName(locality.getLocalityName());

                schedule.setLocalityId(locality.getLocalityId());
                schedule.setPjpDay(day);
                pjpSchedules.add(schedule);
            }
        }
    }
}