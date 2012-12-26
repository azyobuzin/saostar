package net.azyobuzi.saostar;

import android.app.Application;
import android.os.Handler;

public class App extends Application
{
    public App()
    {
        super();
        instance = this;
    }

    public static App instance;

    public Handler h;

    @Override
    public void onCreate()
    {
        super.onCreate();

        h = new Handler();

        System.setProperty("twitter4j.http.useSSL", "true");
        System.setProperty("twitter4j.oauth.consumerKey", "atiCJrEYcDTK06asy9riaA");
        System.setProperty("twitter4j.oauth.consumerSecret", "e1OSvum01Hgh3sg6xKIKJGRWIUOAqIei73rbuDOFbxY");
        System.setProperty("twitter4j.stream.user.repliesAll", "false");
    }
}
