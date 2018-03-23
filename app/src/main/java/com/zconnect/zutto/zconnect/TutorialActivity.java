package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TutorialActivity extends BaseActivity {
    Drawable pageData[]=new Drawable[5];	        //Stores the text to swipe.
    LayoutInflater inflater;	//Used to create individual pages
    ViewPager vp;	            //Reference to class to swipe views
    TabLayout tabLayout;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        makeActivityFullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_tutorial);
        //Get the data to be swiped through
        //pageData=getResources().getStringArray(R.array.desserts);
        showProgressDialog();
        pageData[0] = ContextCompat.getDrawable(this, R.drawable.infone_tut);
        pageData[1] = ContextCompat.getDrawable(this, R.drawable.shops_tut);
        pageData[2] = ContextCompat.getDrawable(this, R.drawable.events_tut);
        pageData[3] = ContextCompat.getDrawable(this, R.drawable.storeroom_tut);
        pageData[4] = ContextCompat.getDrawable(this, R.drawable.cabpool_tut);

        tabLayout = (TabLayout) findViewById(R.id.tabDots);


        doneBtn=(Button) findViewById(R.id.dummy_button);
        //get an inflater to be used to create single pages
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Reference ViewPager defined in activity
        vp=(ViewPager)findViewById(R.id.viewPager);
        //set the adapter that will create the individual pages
        vp.setAdapter(new MyPagesAdapter());

        doneButton(vp.getCurrentItem());
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                doneButton(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(vp, true);

        doneBtn.setVisibility(View.GONE);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });
        hideProgressDialog();

    }

    private void doneButton(int position) {
        if(position==3){

            doneBtn.setVisibility(View.VISIBLE);
        } else
            doneBtn.setVisibility(View.INVISIBLE);
    }

    //Implement PagerAdapter Class to handle individual page creation
    private class MyPagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            return pageData.length;
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.tutorial_page, null);
            ImageView image =(ImageView) page.findViewById(R.id.textMessage);
           // RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(container.getWidth(),container.getHeight());

//            image.setLayoutParams(layoutParams);
//            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageDrawable(pageData[position]);
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);

            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }
    }
}
