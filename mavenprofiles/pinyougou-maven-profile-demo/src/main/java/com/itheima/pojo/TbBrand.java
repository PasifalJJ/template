package com.itheima.pojo;

import java.io.Serializable;

public class TbBrand implements Serializable {
    private Long id;

    private String myname;

    private String first_char;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public String getFirst_char() {
        return first_char;
    }

    public void setFirst_char(String first_char) {
        this.first_char = first_char;
    }

    @Override
    public String toString() {
        return "TbBrand{" +
                "id=" + id +
                ", myname='" + myname + '\'' +
                ", first_char='" + first_char + '\'' +
                '}';
    }
}