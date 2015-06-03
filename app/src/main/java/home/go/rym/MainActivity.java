package home.go.rym;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import home.go.rym.async.GetPageCountTask;
import home.go.rym.async.LoginTask;
import home.go.rym.async.ScanCollectionTask;
import home.go.rym.utils.Constants;
import home.go.rym.utils.RymUtils;


public class MainActivity extends ActionBarActivity {
    private static final String FRAGMENT_MAIN_TAG = "fragment_collection";
    private static final String FRAGMENT_SETTINGS_TAG = "fragment_collection";
    protected MainActivity instance;
    Drawer mDrawer;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;
    private SharedPreferences prefs;
    private FrameLayout croutonPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.croutonPlace = (FrameLayout) findViewById(R.id.crouton_handle);
        instance = this;
        this.prefs = this.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
        initUI();
        init();
        String username = prefs.getString(Constants.PREF_USERNAME.toString(), "");
        String password = prefs.getString(Constants.PREF_PASSWORD.toString(), "");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            LayoutInflater inflater = getLayoutInflater();
            final View v = inflater.inflate(R.layout.login_dialog, null);
            final EditText passwordEditText = (EditText) v.findViewById(R.id.password_request);
            final EditText usernameEditText = (EditText) v.findViewById(R.id.username_request);
            final CheckBox savePassCheckBox = (CheckBox) v.findViewById(R.id.save_pass_checkBox);
            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .autoDismiss(false)
                    .title(R.string.login)
                    .customView(v, false)
                    .positiveText(android.R.string.ok)
                    .negativeText(android.R.string.cancel)
                    .callback(new MaterialDialog.ButtonCallback() {

                        @Override public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            dialog.dismiss();
                        }

                        @Override public void onPositive(MaterialDialog dialog) {
                            String username = usernameEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                                prefs.edit().putString(Constants.PREF_USERNAME, username).commit();
                                if (savePassCheckBox.isChecked()) {
                                    prefs.edit().putString(Constants.PREF_PASSWORD, password).commit();
                                }
                                super.onPositive(dialog);
                                dialog.dismiss();
                                new LoginTask(instance, username, password).execute();
                            }
                        }
                    })
                    .cancelable(false)
                    .build();
            usernameEditText.setText(username);
            if (!TextUtils.isEmpty(username))
                passwordEditText.requestFocus();
            dialog.show();
        } else {
            new LoginTask(this, username, password).execute();
        }
    }

    private void init() {
        mFragmentManager = getSupportFragmentManager();
        if (mFragmentManager.findFragmentByTag(FRAGMENT_MAIN_TAG) == null) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new CollectionFragment(), FRAGMENT_MAIN_TAG).commit();
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
                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id,
                                               IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.getIdentifier() == 1) {
                            if (mFragmentManager.findFragmentByTag(FRAGMENT_MAIN_TAG) == null) {
                                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                                fragmentTransaction
                                        .add(R.id.fragment_container, new CollectionFragment(), FRAGMENT_MAIN_TAG)
                                        .commit();
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
        if (id == R.id.rescan) {
            new MaterialDialog.Builder(this)
                    .content("This deletes all entries of your collection. Do you want to proceed?")
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override public void onPositive(MaterialDialog dialog) {
                            new GetPageCountTask(
                                    instance,
                                    RymUtils.buildRecentUrl(prefs.getString(Constants.PREF_USERNAME, "")),
                                    new GetPageCountTask.OnGetPageCountTask() {
                                        @Override
                                        public void OnGetPageCountTask(int pages) {
                                            Crouton.makeText(instance, String.valueOf(pages), Style.INFO, croutonPlace)
                                                   .show();
                                            new ScanCollectionTask(instance,null,pages,true).execute();
                                        }
                                    }
                            ).execute();
                            super.onPositive(dialog);
                        }
                        @Override public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    })
                    .show();

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
