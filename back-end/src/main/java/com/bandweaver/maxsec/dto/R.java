package com.bandweaver.maxsec.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class R<T> implements Serializable {
    private static final int SUCCESS = 200;
    private static final int FAIL = 500;
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String msg = "success";

    @Getter
    @Setter
    private int code = SUCCESS;

    @Getter
    @Setter
    private T data;

    public R() {
        super();
    }

    public R(T data) {
        super();
        this.data = data;
    }

    public R(T data, String msg) {
        super();
        this.data = data;
        this.msg = msg;
    }

    public R(Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = FAIL;
    }

    public R(int code, Throwable e) {
        super();
        this.msg = e.getMessage();
        this.code = code;
    }

    public R(int code, T data, String msg){
        super();
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
}
