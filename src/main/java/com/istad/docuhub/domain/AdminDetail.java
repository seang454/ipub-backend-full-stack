package com.istad.docuhub.domain;

import java.sql.Date;

public class AdminDetail {
    private int id;
    private String department;
    private String position;
    private String cardId;
    private String  adminCode;
    private String status;
    private Date hireDate;
    private String permission;
    //one to one with user and map user uuid
}
