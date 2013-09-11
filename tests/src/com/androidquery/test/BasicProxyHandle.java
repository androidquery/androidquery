package com.androidquery.test;

import java.net.HttpURLConnection;

import org.apache.http.HttpRequest;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.ProxyHandle;
import com.androidquery.util.AQUtility;

public class BasicProxyHandle extends ProxyHandle{

    private String host;
    private int port;
    private String user;
    private String password;
    
    public BasicProxyHandle(String host, int port, String user, String password){
        
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        
    }
    
    
    @Override
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request) {
        // TODO Auto-generated method stub
        AQUtility.debug("apply token");
        cb.proxy(host, port, user, password);
        
    }

    @Override
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn) {
        // TODO Auto-generated method stub
        
    }

    
    
    
}
