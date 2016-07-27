package ivanrudyk.com.open_weather_api.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ivanrudyk.com.open_weather_api.R;
import ivanrudyk.com.open_weather_api.model_user.Users;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDraverFragment extends Fragment {

    Users users = new Users();

    public static final String PREF_FILE_NAME = "preffilename";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearndDrawer;
    private boolean mFromSavedInstanseState;
    private View containerView;
    Bitmap bmEnd;

    public NavigationDraverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearndDrawer = Boolean.valueOf((readFromPreferenses(getActivity(), KEY_USER_LEARNED_DRAWER, "false")));
        if (savedInstanceState != null) {
            mFromSavedInstanseState = true;
        }
        users.setUserName("Mylian");
        users.setPhoto(BitmapFactory.decodeResource(getResources(), R.drawable.qwe));
        if (bmEnd==null) {
         // Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.qwe);
            bmEnd = getCircleMaskedBitmapUsingClip(users.getPhoto(), 60);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_navigation_draver, container, false);
        ImageView i = (ImageView) v.findViewById(R.id.ivPhotoUser);
        TextView t = (TextView) v.findViewById(R.id.textView);
        t.setText(users.getUserName());
        i.setImageBitmap(bmEnd);
        return v;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolBar) {

        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolBar, R.string.drawer_open, R.string.drawer_close) {


            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearndDrawer) {
                    mUserLearndDrawer = true;
                    saveToPreferenses(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearndDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                if (slideOffset < 0.6) {
//                    toolBar.setAlpha(1 - slideOffset);
//                }
//            }
        };

        if (!mUserLearndDrawer && !mFromSavedInstanseState) {
            mDrawerLayout.openDrawer(containerView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferenses(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferenses(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static Bitmap scaleTo(Bitmap source, int size)
    {
        int destWidth = source.getWidth();

        int destHeight = source.getHeight();

        destHeight = destHeight * size / destWidth;
        destWidth = size;

        if (destHeight < size)
        {
            destWidth = destWidth * size / destHeight;
            destHeight = size;
        }

        Bitmap destBitmap = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(destBitmap);
        canvas.drawBitmap(source, new Rect(0, 0, source.getWidth(), source.getHeight()), new Rect(0, 0, destWidth, destHeight), new Paint(Paint.ANTI_ALIAS_FLAG));
        return destBitmap;
    }

    public static Bitmap getCircleMaskedBitmapUsingClip(Bitmap source, int radius)
    {
        if (source == null)
        {
            return null;
        }

        int diam = radius << 1;

        Bitmap scaledBitmap = scaleTo(source, diam);

        final Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);

        Bitmap targetBitmap = Bitmap.createBitmap(diam, diam, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        canvas.clipPath(path);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        canvas.drawBitmap(scaledBitmap, 0, 0, paint);

        return targetBitmap;
    }

}
