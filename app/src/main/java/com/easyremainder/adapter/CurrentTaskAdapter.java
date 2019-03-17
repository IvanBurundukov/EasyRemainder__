package com.easyremainder.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easyremainder.R;
import com.easyremainder.Utils;
import com.easyremainder.fragment.CurrentTaskFragment;
import com.easyremainder.fragment.TaskFragment;
import com.easyremainder.model.Item;
import com.easyremainder.model.ModelSeparator;
import com.easyremainder.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CurrentTaskAdapter extends TaskAdapter {



    private static  final int TYPE_TASK=0;
    private static  final int TYPE_SEPORATOR=1;

    public CurrentTaskAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }






 @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case TYPE_TASK:
                View v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.model_task,viewGroup,false);
                TextView title= v.findViewById(R.id.tvTaskTitle);
                TextView date= v.findViewById(R.id.tvTaskDate);
                CircleImageView priority = v.findViewById(R.id.cvTaskPriority);
                return new TaskViewHolder(v,title,date,priority);
            case TYPE_SEPORATOR:
                View separator=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.model_separator,viewGroup,false);
                TextView type = (TextView) separator.findViewById(R.id.tvSeparatorName);

                return new SeparatorViewHolder(separator,type);
            default: return null;

        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
     Item item = items.get(position);
        final Resources resources = viewHolder.itemView.getResources();
     if (item.isTask()){
         viewHolder.itemView.setEnabled(true);
         final ModelTask task= (ModelTask) item;
         final TaskViewHolder taskViewHolder = (TaskViewHolder) viewHolder;

         final View itemView= taskViewHolder.itemView;


       //  viewHolder.itemView.setEnabled(true);
         taskViewHolder.title.setText(task.getTitle());
         Log.d("my1","DateTask= "+task.getDate());
         if (task.getDate()!=null && task.getDate()!=0){
             Log.d("my1","NE 0 ");
             taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
         }else {
             taskViewHolder.date.setText(null);
         }

         itemView.setVisibility(View.VISIBLE);
         taskViewHolder.priority.setEnabled(true);


Log.d("live","CURTASKAD - task date - "+task.getDate());
         if ( task.getDate()!=null && task.getDate()< Calendar.getInstance().getTimeInMillis()){ //0
             itemView.setBackgroundColor(resources.getColor(R.color.gray_200));
         }else {
             itemView.setBackgroundColor(resources.getColor(R.color.gray_50));
         }
        if (task.getDate()!=null){
         if (task.getDate()==0){ itemView.setBackgroundColor(resources.getColor(R.color.gray_50));}}



         taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_defult_material_light));
         taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_defult_material_light));
         Log.d("myc","Ctask.getpriority =" +task.getPriorityColor()+" "+task.getPriority());
         Log.d("myc","priority =" +taskViewHolder.priority);

         taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));
         taskViewHolder.priority.setImageResource(R.drawable.baseline_fiber_manual_record_white_48);
        // taskViewHolder.priority.setColorFilter(resources.getColor(R.color.priority_high));

         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
               //  itemView.setEnabled(false);
                 getTaskFragment().showTaskEditDialog(task);
             }
         });

         itemView.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {

                 Handler handler = new Handler();
                 handler.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         getTaskFragment().removeTaskDialog(taskViewHolder.getLayoutPosition());
                     }
                 },500);


                 return true;
             }
         });



         taskViewHolder.priority.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 taskViewHolder.priority.setEnabled(false);
                 task.setStatus(ModelTask.STATUS_DONE);
                 getTaskFragment().activity.dbHelper.update().status(task.getTimeStamp(),ModelTask.STATUS_DONE);

                 itemView.setBackgroundColor(resources.getColor(R.color.gray_200));

                 taskViewHolder.title.setTextColor(resources.getColor(R.color.primary_text_defult_material_light_light));
                 taskViewHolder.date.setTextColor(resources.getColor(R.color.secondary_text_defult_material_light_light));
                 taskViewHolder.priority.setColorFilter(resources.getColor(task.getPriorityColor()));

                 ObjectAnimator flipIn=ObjectAnimator.ofFloat(taskViewHolder.priority, "rotationY", -180f,0f);
                 flipIn.addListener(new Animator.AnimatorListener() {
                     @Override
                     public void onAnimationStart(Animator animation) {

                     }

                     @Override
                     public void onAnimationEnd(Animator animation) {
                         if (task.getStatus() == ModelTask.STATUS_DONE) {
                            // taskViewHolder.priority.setImageResource(R.drawable.baseline_check_circle_black_24);
                           //  taskViewHolder.priority.setImageResource(R.drawable.check_okok_18dp);
                             taskViewHolder.priority.setImageResource(R.drawable.chek_ok);

                             ObjectAnimator translationX = ObjectAnimator.ofFloat
                                     (itemView, "translationX",0f,itemView.getWidth());

                             ObjectAnimator translationXBack = ObjectAnimator.ofFloat
                                     (itemView,"translationX",itemView.getWidth(),0f);


                           translationX.addListener(new Animator.AnimatorListener() {
                               @Override
                               public void onAnimationStart(Animator animation) {

                               }

                               @Override
                               public void onAnimationEnd(Animator animation) {


                                   itemView.setVisibility(View.GONE);
                                   getTaskFragment().moveTask(task);
                                   removeItem(taskViewHolder.getLayoutPosition());

                               }

                               @Override
                               public void onAnimationCancel(Animator animation) {

                               }

                               @Override
                               public void onAnimationRepeat(Animator animation) {

                               }
                           });


                            AnimatorSet translationSet = new AnimatorSet();
                            translationSet.play(translationX).before(translationXBack);
                            translationSet.start();




                         }

                     }

                     @Override
                     public void onAnimationCancel(Animator animation) {

                     }

                     @Override
                     public void onAnimationRepeat(Animator animation) {

                     }
                 });
                 flipIn.start();



             }
         });


         //else {
          //   Log.d("my1","0 ");
          //   taskViewHolder.date.setText(" ");}

     }else{
         ModelSeparator separator = (ModelSeparator) item;
         SeparatorViewHolder separatorViewHolder=(SeparatorViewHolder) viewHolder;

         separatorViewHolder.type.setText(resources.getString(separator.getType()));
     }

    }




    @Override
    public int getItemViewType(int position) {
      if (getItem(position).isTask()){
          return  TYPE_TASK;
      }else{return TYPE_SEPORATOR;}
    }




}
