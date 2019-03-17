package com.easyremainder.fragment;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.easyremainder.MainActivity;
import com.easyremainder.R;
import com.easyremainder.adapter.CurrentTaskAdapter;
import com.easyremainder.adapter.TaskAdapter;
import com.easyremainder.alarm.AlarmHelper;
import com.easyremainder.dialog.EditTaskDialogFragment;
import com.easyremainder.model.Item;
import com.easyremainder.model.ModelTask;

import java.util.Date;

public abstract class TaskFragment extends Fragment {
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;


    protected TaskAdapter adapter;
    public AlarmHelper alarmHelper;
    public MainActivity activity;

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity()!=null){
            activity= (MainActivity) getActivity();
            Log.d("live","TaskFragment - Создание Активити");
        }
        alarmHelper = AlarmHelper.getInstance();

        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);

    public void updateTask(ModelTask task){
        adapter.updateTask(task);
    }

    public void removeTaskDialog(final int location){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setMessage(R.string.dialog_remove_message);

        Item item = adapter.getItem(location);
        if (item.isTask()) {
            ModelTask removingTask = (ModelTask) item;

            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    adapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator),
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_Cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        addTask(activity.dbHelper.query().getTask(timeStamp),false); // востановление
                        isRemoved[0]=false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if (isRemoved[0]){
                                alarmHelper.removeAlarm(timeStamp);
                                activity.dbHelper.removeTask(timeStamp);

                            }

                        }
                    });

                    snackbar.show();

                    dialog.dismiss();


                }
            });

            dialogBuilder.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        dialogBuilder.show();

    }

    public void showTaskEditDialog(ModelTask task){
        DialogFragment editingTaskDialog= EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivity().getFragmentManager(),"EditTaskDialogFragment");
    }


    public abstract void findTasks(String title);

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);


}
