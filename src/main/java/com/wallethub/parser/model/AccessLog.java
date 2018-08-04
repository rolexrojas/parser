package com.wallethub.parser.model;

import java.io.Serializable;

public class AccessLog implements Serializable {
    private static final long serialVersionUID = 1L;
    private String date_event;
    private String ip_address;
    private String request_method;
    private String status_code;
    private String user_agent;
    private String commentary;

    public AccessLog() {
    }

    public AccessLog(String date_event, String ip_address, String request_method, String status_code, String user_agent) {
        this.date_event = date_event;
        this.ip_address = ip_address;
        this.request_method = request_method;
        this.status_code = status_code;
        this.user_agent = user_agent;
    }

    public AccessLog(String date_event, String ip_address, String request_method, String status_code, String user_agent, String commentary) {
        this.date_event = date_event;
        this.ip_address = ip_address;
        this.request_method = request_method;
        this.status_code = status_code;
        this.user_agent = user_agent;
        this.commentary = commentary;
    }


    public String getDate_event() {
        return date_event;
    }

    public void setDate_event(String date_event) {
        this.date_event = date_event;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getRequest_method() {
        return request_method;
    }

    public void setRequest_method(String request_method) {
        this.request_method = request_method;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    @Override
    public String toString() {
        return "AccessLog [date_event=" + date_event + ", ip_address=" + ip_address + ", request_method=" + request_method + ", status_code=" + status_code + ", user_agent=" + user_agent + "]";
    }
}
