package com.androidquery.test;

import java.net.HttpURLConnection;
import java.net.Proxy;

import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.Uri;
import android.text.TextUtils;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.ProxyHandle;
import com.androidquery.util.AQUtility;

public class NTLMProxyHandle extends ProxyHandle{

    private String host;
    private int port;
    private String user;
    private String password;
    private String domain;
    
    public NTLMProxyHandle(String host, int port, String domain, String user, String password){
        
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.domain = domain;
    }
    
    
    @Override
    public void applyProxy(AbstractAjaxCallback<?, ?> cb, HttpRequest request, DefaultHttpClient client) {
       
        
        
        if(!isIntranet(cb.getUrl())){
            
          
            if(!TextUtils.isEmpty(host) && !TextUtils.isEmpty(user)){
                
                AQUtility.debug("ntlm token");
                
                client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());        
                client.getCredentialsProvider().setCredentials(new AuthScope(host, port), new NTCredentials(user, password, host, domain));
              
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
        // TODO Auto-generated method stub
        return null;
    }
    
    
}
