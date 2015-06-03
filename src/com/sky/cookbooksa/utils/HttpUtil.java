package com.sky.cookbooksa.utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtil {
    private static     AsyncHttpClient client =new AsyncHttpClient();    //ʵ����ￄ1�7
    static
    {
        client.setTimeout(11000);   //�������ӳ�ʱ��������ã�Ĭ��΄1�7s
    }
    public static void get(String urlString,AsyncHttpResponseHandler res)    //��һ������url��ȡһ��string����
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,AsyncHttpResponseHandler res)   //url��������
    {
        client.get(urlString, params,res);
    }
    public static void get(String urlString,JsonHttpResponseHandler res)   //��������ȡjson����������ￄ1�7
    {
        client.get(urlString, res);
    }
    public static void get(String urlString,RequestParams params,JsonHttpResponseHandler res)   //������ȡjson����������ￄ1�7
    {
        client.get(urlString, params,res);
    }
    public static void get(String uString, BinaryHttpResponseHandler bHandler)   //�������ʹ�ã��᷵��byte��ￄ1�7
    {
        client.get(uString, bHandler);
    }
    public static AsyncHttpClient getClient()
    {
        return client;
    }
}