package com.easyremainder;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.easyremainder.adapter.TabAdapter;
import com.easyremainder.alarm.AlarmHelper;
import com.easyremainder.database.DBHelper;
import com.easyremainder.dialog.AddingTaskDialogFragment;
import com.easyremainder.dialog.EditTaskDialogFragment;
import com.easyremainder.fragment.CurrentTaskFragment;
import com.easyremainder.fragment.DoneTaskFragment;
import com.easyremainder.fragment.SplashFragment;
import com.easyremainder.fragment.TaskFragment;
import com.easyremainder.model.ModelTask;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements AddingTaskDialogFragment.AddingTaskListener,
        CurrentTaskFragment.OnTaskDoneListener, DoneTaskFragment.OnTaskRestoreListener, EditTaskDialogFragment.EditingTaskListner

{

FragmentManager fragmentManager;

PreferenceHalper preferenceHalper;
    TabAdapter tabAdapter;
    TaskFragment currentTaskFragment;
    TaskFragment doneTaskFragment;
    SearchView searchView;
    public DBHelper dbHelper;
public static final String tag="mylog";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // int theme  = R.style.ThNoAB;
       // setTheme(theme);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceHalper.getInstance().init(getApplicationContext());
        preferenceHalper = PreferenceHalper.getInstance();

        AlarmHelper.getInstance().init(getApplicationContext());
        dbHelper = new DBHelper(getApplicationContext());

        Log.d("live","start");
        fragmentManager=getFragmentManager();
        runSplash();
        Log.d("live","заставка");

        setUI();



    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPoused();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        MenuItem splashItem=menu.findItem(R.id.action_splash);
        splashItem.setChecked(preferenceHalper.getBoolean(PreferenceHalper.SPLAH_IS_INVISIBLE));

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        //return true;
        if (id==R.id.action_splash){
            item.setChecked(!item.isChecked());
            preferenceHalper.putBoolean(PreferenceHalper.SPLAH_IS_INVISIBLE,item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runSplash(){
     if(!preferenceHalper.getBoolean(PreferenceHalper.SPLAH_IS_INVISIBLE)){
        SplashFragment splashFragment = new SplashFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, splashFragment)
                .addToBackStack(null)
                .commit();}

    }





    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar!=null){
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorwhite));
            setSupportActionBar(toolbar);
        }



        Log.d("live","интерфейс");

        TabLayout tabLayout=findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task));

        final ViewPager viewPager = findViewById(R.id.pager);

        tabAdapter = new TabAdapter(fragmentManager,2);


        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.d("live","таб выбран ="+tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }



        });


        currentTaskFragment = (CurrentTaskFragment) tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION);
        Log.d("live","MainActiv, CurrentTaskFragment ="+tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION));
        doneTaskFragment = (DoneTaskFragment) tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION);
        Log.d("live","MainActiv, DoneTaskFragment ="+tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION));
        searchView=findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentTaskFragment.findTasks(newText);
                doneTaskFragment.findTasks(newText);
                return false;
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("live","нажали кнопку +");

                DialogFragment addingTaskDialogFragment=new AddingTaskDialogFragment();
                Log.d("live","AddingTaskDialogFragment - объявлен");


                addingTaskDialogFragment.show(fragmentManager,"AddingTaskDialogFragment");

                Log.d("live","AddingTaskDialogFragment - запущен");

            }
        });

    }


    @Override
    public void onTaskAdded(ModelTask newTask) {
        currentTaskFragment.addTask(newTask, true);
        Toast.makeText(this,"Задача добавлена",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskAddingCancel() {
        Toast.makeText(this,"Добавление задачи отменено",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskDone(ModelTask task){
        doneTaskFragment.addTask(task,false);
        Log.d("live","MainActivity - добавляем задачу в DoneTaskFragment"); }



     @Override
     public void onTaskRestore(ModelTask task){
       currentTaskFragment.addTask(task,false);
    }


    @Override
    public void onTaskEdited(ModelTask updatedTask) {
    currentTaskFragment.updateTask(updatedTask);
    dbHelper.update().task(updatedTask);
    }
}
