package com.froggengo.practise.HttpClientTest;


import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

public class HttpClientTestone {

    @Test
    public void test (){
        String url= "https://www.163.com/";
        try {
            URL url1 = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection)url1.openConnection();
            System.out.println("getContent: "+ connection.getContent());
            System.out.println("getContentEncoding: "+connection.getContentEncoding());
            System.out.println("getExpiration :"+connection.getExpiration());
            connection.getHeaderFields().forEach((k,v)->{
                System.out.println("  "+k);
                v.stream().forEach(sss->System.out.println("    "+sss));
            });
            System.out.println("response"+connection.getResponseCode());
            System.out.println("message"+connection.getResponseMessage());
            Certificate[] certs = connection.getServerCertificates();
            for (Certificate s:certs){
                System.out.println("  getPublicKey:" +  s.getPublicKey().getAlgorithm());
                System.out.println("  getPublicKey.getEncoded:" +  new String(s.getPublicKey().getEncoded()));
                System.out.println("  getType:" + s.getType());
                System.out.println("  getPublicKey.getFormat:" +  s.getPublicKey().getFormat());
                System.out.println("  getEncoded:" +  s.getEncoded());
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s ;
            System.out.println("content:");
            while ((s= reader.readLine())!=null){
                System.out.println("  "+s);
            }
            //HttpsURLConnectionImpl connection = new HttpsURLConnectionImpl(url1);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }


    }
}
