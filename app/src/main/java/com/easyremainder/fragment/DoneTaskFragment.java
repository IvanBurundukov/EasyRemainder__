package com.easyremainder.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyremainder.R;
import com.easyremainder.adapter.DoneTaskAdapter;
import com.easyremainder.database.DBHelper;
import com.easyremainder.model.ModelTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DoneTaskFragment extends TaskFragment {


    public DoneTaskFragment() {
        // Required empty public constructor
    }

    OnTaskRestoreListener onTaskRestoreListener;

    public interface OnTaskRestoreListener{
        void onTaskRestore(ModelTask task);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            onTaskRestoreListener=(OnTaskRestoreListener) activity;

        }catch (ClassCastException e ){
            throw new ClassCastException(activity.toString()+" must implement OnTaskRestoreListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView=inflater.inflate(R.layout.fragment_done_task,container,false);

        recyclerView =rootView.findViewById(R.id.rvDoneTask);

        layoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DoneTaskAdapter(this);
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();

        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE+" AND "+DBHelper.SELECTION_STATUS ,
                new String[]{"%"+title+"%",Integer.toString(ModelTask.STATUS_DONE)},DBHelper.TASK_DATE_COLUMN));
        for (int i=0;i<tasks.size();i++){

            addTask(tasks.get(i),false);

        }

    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        Log.d("DoneTaskFragment","грузим из БД - в DoneTaskFragment");
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS , new String[]{Integer.toString(ModelTask.STATUS_DONE)},DBHelper.TASK_DATE_COLUMN));
        for (int i=0;i<tasks.size();i++){
            Log.d("DoneTaskFragment"," задача = "+i);
            addTask(tasks.get(i),false);

        }
        Log.d("DoneTaskFragment","всего задач = "+tasks.size());
    }



    @Override
    public void moveTask(ModelTask task) {
        if (  task.getDate()!=null && task.getDate()!=0){
            alarmHelper.setAlarm(task);
        }
onTaskRestoreListener.onTaskRestore(task);
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDB){
        Log.d("live","TaskFragment - Добавление Задачи:");
        Log.d("live","TaskFragment - newTaskDate = "+ newTask.getDate());
        int position=-1;
        // сравниваем новую задачу с другими по времени - определяем позицию новой( куда поставить)
        if (newTask.getDate()!=null && newTask.getDate()!=0){
            for (int i=0;i<adapter.getItemCount();i++){
                Log.d("live","TaskFragment - ItemCount: "+adapter.getItemCount());
                if (adapter.getItem(i).isTask()){

                    ModelTask task= (ModelTask) adapter.getItem(i);
                    if (newTask.getDate() < task.getDate()){
                        position=i;
                        break;
                    }
                }

            }}
        if (newTask.getDate()!=null && newTask.getDate()!=0){
            if (position !=-1){
                adapter.addItem(position,newTask);
            }else{
                adapter.addItem(newTask);
            }}
        else{adapter.addItem(0,newTask);}

        if (saveToDB){
            activity.dbHelper.saveTask(newTask);
        }

    }


}
