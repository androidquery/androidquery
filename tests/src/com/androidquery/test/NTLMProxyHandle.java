package com.androidquery.test;

import java.net.HttpURLConnection;

import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.impl.client.DefaultHttpClient;

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
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request, DefaultHttpClient client) {
       
        AQUtility.debug("ntlm token");
        
        client.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());        
        client.getCredentialsProvider().setCredentials(new AuthScope(host, port), new NTCredentials(user, password, host, domain));
        
        cb.proxy(host, port);
        
    }

    @Override
    public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn) {
        // TODO Auto-generated method stub
        
    }

    
    
    
}
