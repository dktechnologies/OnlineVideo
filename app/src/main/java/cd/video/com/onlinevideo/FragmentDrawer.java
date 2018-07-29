package cd.video.com.onlinevideo;

/*
  Created by Ravi on 29/07/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentDrawer extends Fragment implements View.OnClickListener {

    private static String TAG = FragmentDrawer.class.getSimpleName();
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    LinearLayout rl_playlist,rl_channel,rl_about, rl_more, rl_share, rl_exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating view layout

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer_new, container, false);

        rl_playlist=layout.findViewById(R.id.rl_playlist);
        rl_channel=layout.findViewById(R.id.rl_channel);
        rl_about = layout.findViewById(R.id.rl_About);
        rl_more = layout.findViewById(R.id.ll_more);
        rl_share = layout.findViewById(R.id.ll_share);
        rl_exit = layout.findViewById(R.id.rl_exit);

        rl_playlist.setOnClickListener(this);
        rl_channel.setOnClickListener(this);
        rl_about.setOnClickListener(this);
        rl_more.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_exit.setOnClickListener(this);



        return layout;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Log.d("slideoff", "" + slideOffset);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_playlist:
                Home.viewPager.setCurrentItem(0);
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.rl_channel:
                Home.viewPager.setCurrentItem(1);
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.rl_About:
                Intent m = new Intent(getContext(), AboutUs.class);
                startActivity(m);
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.ll_more:
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.ll_share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    String sAux = "\n"+getResources().getString(R.string.sharemsg)+"\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id="+getActivity().getPackageName()+"\n\n";
                    Log.e("sAux",""+sAux);
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
                mDrawerLayout.closeDrawer(containerView);
                break;
            case R.id.rl_exit:
                showAlertView();
                mDrawerLayout.closeDrawer(containerView);
                break;
            default:
                break;

        }
    }

    private void showAlertView() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.logout_message))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finishAffinity();
                        ;
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
