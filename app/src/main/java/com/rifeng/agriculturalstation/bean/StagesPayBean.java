package com.rifeng.agriculturalstation.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chw on 2017/3/15.
 */
public class StagesPayBean implements Parcelable {

    public int id;
    public int taskid;
    public int owneruid;
    public int farmeruid;
    public int stages;
    public double money;
    public int status;
    public int dateline;

    protected StagesPayBean(Parcel in) {
        id = in.readInt();
        taskid = in.readInt();
        owneruid = in.readInt();
        farmeruid = in.readInt();
        stages = in.readInt();
        money = in.readDouble();
        status = in.readInt();
        dateline = in.readInt();
    }

    public static final Creator<StagesPayBean> CREATOR = new Creator<StagesPayBean>() {
        @Override
        public StagesPayBean createFromParcel(Parcel source) {
            return new StagesPayBean(source);
        }

        @Override
        public StagesPayBean[] newArray(int size) {
            return new StagesPayBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(taskid);
        dest.writeInt(owneruid);
        dest.writeInt(farmeruid);
        dest.writeInt(stages);
        dest.writeDouble(money);
        dest.writeInt(status);
        dest.writeInt(dateline);
    }


}
