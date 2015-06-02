package home.go.rym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity {
    private static final String FRAGMENT_MAIN_TAG = "fragment_collection";
    private static final String FRAGMENT_SETTINGS_TAG = "fragment_collection";
    protected MainActivity instance;
    Drawer mDrawer;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        initUI();
        init();
    }

    private void init() {
        mFragmentManager = getSupportFragmentManager();
        if (mFragmentManager.findFragmentByTag(FRAGMENT_MAIN_TAG) == null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new MainActivityFragment(), FRAGMENT_MAIN_TAG).commit();
        }
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowToolbar(true)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .withCloseOnClick(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_collection).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.friends).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.recommendations).withIdentifier(3),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(11)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.getIdentifier() == 1) {
                            if (mFragmentManager.findFragmentByTag(FRAGMENT_MAIN_TAG) == null) {
                                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                                fragmentTransaction.add(R.id.fragment_container, new MainActivityFragment(), FRAGMENT_MAIN_TAG).commit();
                            }
                        }
                        if (drawerItem.getIdentifier() == 11) {
                            Intent intentSettings = new Intent(instance, SettingsActivity.class);
                            startActivityForResult(intentSettings, 11);
                        }
                        Crouton.makeText(instance, String.valueOf(position), Style.CONFIRM).show();
                        return true;
                    }
                })
                .build();
    }


    private void initUI() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Fragment checkFragmentInstance(int id, Object instanceClass) {
        Fragment result = null;
        if (mFragmentManager != null) {
            Fragment fragment = mFragmentManager.findFragmentById(id);
            if (instanceClass.equals(fragment.getClass())) {
                result = fragment;
            }
        }
        return result;
    }

}
