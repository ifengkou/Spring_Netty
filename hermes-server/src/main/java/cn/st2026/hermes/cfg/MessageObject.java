package cn.st2026.hermes.cfg;

import java.util.HashMap;
import java.io.Serializable;

/**
 * Created by Sloong on 2015/9/8.
 */
public class MessageObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MessageObject() {
        state = 1;
        data = new HashMap<String, String>();
    }

    public MessageObject(String code,String message,int state,Object data){
        this.code = code;
        this.message = message;
        this.state = state;
        this.data = data;
    }
    public MessageObject(String code,String message,int state){
        this.code = code;
        this.message = message;
        this.state = state;
        this.data = new HashMap<String, String>();
    }
    public MessageObject(String code,String message){
        this.code = code;
        this.message = message;
        this.state = 0;
        this.data = new HashMap<String, String>();
    }
    public MessageObject(String code,int state){
        this.code = code;
        this.message = "";
        this.state = state;
        this.data = new HashMap<String, String>();
    }
    public MessageObject(String code){
        this.code = code;
        this.message = "";
        this.state = 0;
        this.data = new HashMap<String, String>();
    }




    private String code;// 消息代码
    private String message;// 消息内容
    private int state;// 状态
    private Object data;// 消息数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}