package com.easyremainder.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.easyremainder.R;
import com.easyremainder.Utils;
import com.easyremainder.alarm.AlarmHelper;
import com.easyremainder.model.ModelTask;

import java.util.Calendar;

import static com.easyremainder.R.string.dialog_error_title;

public class AddingTaskDialogFragment extends DialogFragment {

    private AddingTaskListener addingTaskListener;

    public interface AddingTaskListener{
        void onTaskAdded(ModelTask newTask);
        void onTaskAddingCancel();

    }
   @Override
   public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            addingTaskListener = (AddingTaskListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString() +"must implement AddingTaskListener");

        }
   }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d("live","AddingTaskDialogFragment - работает");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.dialog_title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View container = inflater.inflate(R.layout.dialog_task,null);

        final TextInputLayout tilTitle= (TextInputLayout) container.findViewById(R.id.tilDialogTaskTitle);
        final  EditText etTitle = tilTitle.getEditText();

        final TextInputLayout tilDate=(TextInputLayout) container.findViewById(R.id.tilDialogTaskDate);
        final  EditText etDate = tilDate.getEditText();

        final  TextInputLayout tilTime=(TextInputLayout) container.findViewById(R.id.tilDialogTaskTime);
        final EditText etTime = tilTime.getEditText();


        Spinner spPriority = (Spinner) container.findViewById(R.id.spDialogTaskPriority);






        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));
        Log.d("live","AddingTaskDialogFragment  builder");
        builder.setView(container);

        final ModelTask task = new ModelTask();


        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,ModelTask.PRIORITY_LEVELS);
        spPriority.setAdapter(priorityAdapter);

        spPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("live","AddingTaskDialogFragment - выбрали приоритет");
            task.setPriority(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY) +1); // - +1 => сработает через час (если время не указано)

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etDate.length()==0){
                    etDate.setText(" ");


                }

              DialogFragment datePickerFragment= new DatePickerFragment(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //Calendar dateCalendar=Calendar.getInstance();
                       // dateCalendar.set(year,month,dayOfMonth);
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }

                    @Override
                  public void onCancel(DialogInterface dialog){
                        etDate.setText(null);

                    }

                };
                datePickerFragment.show(getFragmentManager(),"DatePickerFragment");

            }
        });

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTime.length()==0){
                    etTime.setText(" ");

                }

                DialogFragment timePickerFragment=new TimePickerFragment(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                   // Calendar timeCalendar =Calendar.getInstance();
                    //timeCalendar.set(0,0,0,hourOfDay,minute);
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.set(Calendar.SECOND,0);
                    etTime.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog){
                        etTime.setText(null);

                    }

                };
                timePickerFragment.show(getFragmentManager(),"TimePickerFragment");


            }
        });


        builder.setPositiveButton(R.string.dialog_OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setTitle(etTitle.getText().toString());
                if(etDate.length()!=0 || etTime.length()!=0){
                    task.setDate(calendar.getTimeInMillis());

                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(task);

                }
                task.setStatus(ModelTask.STATUS_CURRENT);
                addingTaskListener.onTaskAdded(task);
                dialog.dismiss();

            }
        });

        builder.setNegativeButton(R.string.dialog_Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addingTaskListener.onTaskAddingCancel();
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);

                if (etTitle.length()==0){positiveButton.setEnabled(false);
                tilTitle.setError(getResources().getString(R.string.dialog_error_title));
                }
               // else{
               // if (etDate.length()==0){positiveButton.setEnabled(false);
               //     tilDate.setError(getResources().getString(R.string.dialog_error_date));
               // }}

               // if (etDate.length()==0){positiveButton.setEnabled(false);
                //    tilDate.setError(getResources().getString(R.string.dialog_error_date));
               // }
               // else{
                //    if (etTitle.length()==0){positiveButton.setEnabled(false);
                        tilTitle.setError(getResources().getString(R.string.dialog_error_title));
                 //   }}



            etTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0){
                    positiveButton.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_title));
                    }
                    else{
                       // if (etDate.length()!=0) {
                            positiveButton.setEnabled(true);
                            Log.d("my1","etDate");
                            Log.d("my1","etDate= "+etDate.length());
                        //}
                        tilTitle.setErrorEnabled(false);

                    }
                }


                @Override
                public void afterTextChanged(Editable s) {

                }
            });


                etDate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length()==0){
                           // positiveButton.setEnabled(false);
                           // tilDate.setError(getResources().getString(R.string.dialog_error_date));
                        }
                        else{
                           // if (etTitle.length()!=0) {
                              //  positiveButton.setEnabled(true);
                                Log.d("my1","etTitle");
                                Log.d("my1","etTitle= "+etTitle.length());
                           // }
//                            tilDate.setErrorEnabled(false);

                        }
                    }


                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });




            }
        });






        return alertDialog;
    }
}
