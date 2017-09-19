package com.rifeng.agriculturalstation.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18.
 */

public class FormBean {
    private String offermoney;

    public String getOffermoney() {
        return offermoney;
    }

    public String getOfferday() {
        return offerday;
    }

    public List<ListBean> getList() {
        return list;
    }

    private String offerday;
    private List<ListBean> list;

    public class ListBean {
        private int id;
        private int uid;
        private int taskid;
        private int offerid;
        private String money;
        private String day;
        private String dateline;
        private String updatetime;
        private String name;
        private String unit;

        public int getId() {
            return id;
        }

        public int getUid() {
            return uid;
        }

        public int getTaskid() {
            return taskid;
        }

        public int getOfferid() {
            return offerid;
        }

        public String getMoney() {
            return money;
        }

        public String getDay() {
            return day;
        }

        public String getDateline() {
            return dateline;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }
    }
}
