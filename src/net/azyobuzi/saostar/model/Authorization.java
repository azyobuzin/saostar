package net.azyobuzi.saostar.model;

import android.net.Uri;
import net.azyobuzi.saostar.model.configuration.Account;
import net.azyobuzi.saostar.model.configuration.Accounts;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Authorization
{
    public RequestToken requestToken;
    
    public Uri beginAuthorization() throws TwitterException {
        requestToken = Twitter4JFactories.twitterFactory.getInstance().getOAuthRequestToken();
        return Uri.parse(requestToken.getAuthorizationURL());
    }
        
    public Account endAuthorization(String verifier) throws TwitterException {
        AccessToken token = Twitter4JFactories.twitterFactory.getInstance().getOAuthAccessToken(requestToken, verifier);
        
        long id = token.getUserId();
        boolean newAccount = false;
        Account a = Accounts.fromId(id);
        if (a == null) {
            a = new Account(id);
            newAccount = true;
        }
        a.setScreenName(token.getScreenName());
        a.setOAuthToken(token.getToken());
        a.setOAuthTokenSecret(token.getTokenSecret());
        if (newAccount)
            Accounts.getList().add(a);
        
        return a;
    }
}
