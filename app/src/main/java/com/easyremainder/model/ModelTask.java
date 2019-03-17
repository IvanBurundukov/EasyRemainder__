package com.easyremainder.model;

import android.util.Log;

import com.easyremainder.R;

import java.util.Date;

public class ModelTask implements Item{

    public static final int PRIORITY_LOW=0;
    public static final int PRIORITY_NORMAL=1;
    public static final int PRIORITY_HIGH=2;

    //public static final String[] PRIORITY_LEVELS={"Low priority","Normal priority","High priority"};
    public static final String[] PRIORITY_LEVELS={"Низкий приоритет","Средний приоритет","Высокий приоритет"};

     public static  final int STATUS_OVERDUE=0;
     public static  final int STATUS_CURRENT=1;
     public static  final int STATUS_DONE=2;


    private String title;
   private Long date;

   private int priority;
   private int status;
   private  long timeStamp;
   private int datestatus;


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priotity) {
        this.priority = priotity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ModelTask(){
       this.status=-1;
       this.timeStamp=new Date().getTime();

   }
   public ModelTask(String title, long date, int priority,int status, long timeStamp){
       this.title=title;
       this.date=date;
       this.priority=priority;
       this.status=status;
       this.timeStamp=timeStamp;

   }

   public int getPriorityColor(){
       Log.d("live","ModelTask - ищем приаритет");
        switch (getPriority()){
            case PRIORITY_HIGH:
                if (getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_high;

                }else{
                    return R.color.priority_high_selected;
                }

            case PRIORITY_NORMAL:
                if (getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_normal;

                }else{
                    return R.color.priority_normal_selected;
                }
            case PRIORITY_LOW:
                if (getStatus()==STATUS_CURRENT||getStatus()==STATUS_OVERDUE){
                    return R.color.priority_low;

                }else{
                    return R.color.priority_low_selected;
                }
            default:return 0;
        }

   }



    @Override
    public boolean isTask(){
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public int getDatestatus() {
        return datestatus;
    }

    public void setDatestatus(int datestatus) {
        this.datestatus = datestatus;
    }
}
