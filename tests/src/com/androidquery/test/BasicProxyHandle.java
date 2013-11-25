package com.androidquery.test;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.Uri;
import android.text.TextUtils;

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
    public void applyProxy(AbstractAjaxCallback<?, ?> cb, HttpRequest request, DefaultHttpClient client) {
        
        
        
        if(!isIntranet(cb.getUrl())){
            
            
            if(!TextUtils.isEmpty(host) && !TextUtils.isEmpty(user)){
                cb.proxy(host, port, user, password);
            }else if(!TextUtils.isEmpty(host)){
                cb.proxy(host, port);
            }
            
            
        }
        
        
        
        
    }
    
    
    
    private boolean isIntranet(String url){
        
        if(url == null) return false;
        
        Uri uri = Uri.parse(url);
        
        String host = uri.getHost();
        
        AQUtility.debug("host", host);
        return Character.isDigit(host.charAt(0));
        
    }



    @Override
    public Proxy makeProxy(AbstractAjaxCallback<?, ?> cb) {
        
        Proxy result = null;
        
        if(!isIntranet(cb.getUrl())){
            
            if(!TextUtils.isEmpty(host)){
                result = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));            
            }
            
            
        }
        
        
        return result;
    }


    

   
    
    
}
