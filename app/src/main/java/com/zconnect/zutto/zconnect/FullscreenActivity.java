package com.zconnect.zutto.zconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends BaseActivity {
    Drawable pageData[]=new Drawable[4];	        //Stores the text to swipe.
    LayoutInflater inflater;	//Used to create individual pages
    ViewPager vp;	            //Reference to class to swipe views
    TabLayout tabLayout;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_tutorial);
        //Get the data to be swiped through
        //pageData=getResources().getStringArray(R.array.desserts);

        pageData[0]=getResources().getDrawable(R.drawable.infone_tut);
        pageData[1]=getResources().getDrawable(R.drawable.shops_tut);
        pageData[2]=getResources().getDrawable(R.drawable.events_tut);
        pageData[3]=getResources().getDrawable(R.drawable.storeroom_tut);

        tabLayout = (TabLayout) findViewById(R.id.tabDots);


        doneBtn=(Button) findViewById(R.id.dummy_button);
        //get an inflater to be used to create single pages
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Reference ViewPager defined in activity
        vp=(ViewPager)findViewById(R.id.viewPager);
        //set the adapter that will create the individual pages
        vp.setAdapter(new MyPagesAdapter());

        tabLayout.setupWithViewPager(vp, true);

        doneBtn.setVisibility(View.GONE);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });

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
            ((ImageView)page.findViewById(R.id.textMessage)).setImageDrawable(pageData[position]);
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);

            if(position==3){

                doneBtn.setVisibility(View.VISIBLE);
            }


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
