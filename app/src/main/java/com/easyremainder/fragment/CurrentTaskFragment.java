package com.easyremainder.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easyremainder.R;
import com.easyremainder.adapter.CurrentTaskAdapter;
import com.easyremainder.database.DBHelper;
import com.easyremainder.model.ModelSeparator;
import com.easyremainder.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {




    public CurrentTaskFragment() {
        // Required empty public constructor
    }
OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener{
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {

        try{
            onTaskDoneListener=(OnTaskDoneListener) activity;

        }catch (ClassCastException e ){
            throw new ClassCastException(activity.toString()+" must implement OnTaskRestoreListener");
        }

        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("live","CurrentTask -старт");
      View rootView=inflater.inflate(R.layout.fragment_current_task,container,false);
      recyclerView = rootView.findViewById(R.id.rvCurrentTask);

      layoutManager = new LinearLayoutManager(getActivity());

      recyclerView.setLayoutManager(layoutManager);


      adapter= new CurrentTaskAdapter(this);
      recyclerView.setAdapter(adapter);
        Log.d("live","CurrentTask - инициализировали");
        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();

        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_LIKE_TITLE+ " AND "+DBHelper.SELECTION_STATUS + " OR "
                + DBHelper.SELECTION_STATUS,
                new String[]{"%"+title+"%",Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)},DBHelper.TASK_DATE_COLUMN));
        for (int i=0;i<tasks.size();i++){
            addTask(tasks.get(i),false);

        }

    }

    @Override
    public void addTaskFromDB() {

        adapter.removeAllItems();
        Log.d("live","CurrentTaskFragment - читаем задачи из БД в список ");
        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(activity.dbHelper.query().getTasks(DBHelper.SELECTION_STATUS + " OR "
                        + DBHelper.SELECTION_STATUS, new String[]{Integer.toString(ModelTask.STATUS_CURRENT),
                Integer.toString(ModelTask.STATUS_OVERDUE)},DBHelper.TASK_DATE_COLUMN));
        for (int i=0;i<tasks.size();i++){
            addTask(tasks.get(i),false);
            Log.d("live","CurrentTaskFragment - добавлена задача -"+i);
        }
        Log.d("live","CurrentTaskFragment - всего "+tasks.size());
    }

    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimeStamp());
        onTaskDoneListener.onTaskDone(task);
    }
    @Override
    public void addTask(ModelTask newTask, boolean saveToDB){
        Log.d("live","TaskFragment - Добавление Задачи:");
        Log.d("live","TaskFragment - newTaskDate = "+ newTask.getDate());
        int position=-1;
        if (newTask.getDate()!=null && newTask.getDate()!=0){
            for (int i=0;i<adapter.getItemCount();i++){
                Log.d("live","TaskFragment - ItemCount: "+adapter.getItemCount());
                if (adapter.getItem(i).isTask()){

                    ModelTask task= (ModelTask) adapter.getItem(i);
                    if (task.getDate()!=null){
                    if (newTask.getDate() < task.getDate()){
                        position=i;
                        break;
                    }
                    }
                  //  else{ position=i-1;
                   //     break;}
                }

            }}
        ModelSeparator separator=null;

        if (newTask.getDate()!=null && newTask.getDate()!=0){ //0
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if(calendar.get(Calendar.DAY_OF_YEAR)<Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newTask.setDatestatus(ModelSeparator.TYPE_OVERDUE);
                if(!adapter.containsSeparatorOverdue){
                    adapter.containsSeparatorOverdue=true;
                    separator=new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            }else if(calendar.get(Calendar.DAY_OF_YEAR)==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newTask.setDatestatus(ModelSeparator.TYPE_TODAY);
                if(!adapter.containsSeparatorToday){
                    adapter.containsSeparatorToday=true;
                    separator=new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            }else if(calendar.get(Calendar.DAY_OF_YEAR)==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+1){
                newTask.setDatestatus(ModelSeparator.TYPE_TOMORROW);
                if(!adapter.containsSeparatorTomorrow){
                    adapter.containsSeparatorTomorrow=true;
                    separator=new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            }else if(calendar.get(Calendar.DAY_OF_YEAR)>Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+1){
                newTask.setDatestatus(ModelSeparator.TYPE_FUTURE);
                if(!adapter.containsSeparatorFuture){
                    adapter.containsSeparatorFuture=true;
                    separator=new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }




        }

        // сравниваем новую задачу с другими по времени - определяем позицию новой( куда поставить)

        if (newTask.getDate()!=null && newTask.getDate()!=0 ){
            if (position !=-1){
                if(!adapter.getItem(position-1).isTask()){
                    if (position-2>=0 && adapter.getItem(position-2).isTask()){
                        ModelTask task = (ModelTask) adapter.getItem(position-2);
                        if (task.getDatestatus()==newTask.getDatestatus()){
                            position -=1;
                        }
                    } else if (position - 2<0 && newTask.getDate()==0){
                        position-=1;
                    }
                }

                if (separator!=null){
                    adapter.addItem(position-1,separator);
                }


                adapter.addItem(position,newTask);
            }else{
                if (separator!=null){
                    adapter.addItem(separator);
                }
                adapter.addItem(newTask);
            }}
        else{adapter.addItem(0,newTask);}

        if (saveToDB){
            activity.dbHelper.saveTask(newTask);
        }

    }


}
