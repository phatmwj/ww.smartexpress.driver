package ww.smartexpress.driver.data.remote;

import android.app.Application;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import okhttp3.HttpUrl;
import ww.smartexpress.driver.BuildConfig;
import ww.smartexpress.driver.constant.Constants;
import ww.smartexpress.driver.data.local.prefs.PreferencesService;
import ww.smartexpress.driver.utils.LogService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final PreferencesService appPreferences;
    private final Application application;

    public AuthInterceptor(PreferencesService appPreferences, Application application) {
        this.appPreferences = appPreferences;
        this.application = application;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        String isIgnore = chain.request().header("IgnoreAuth");
        if (isIgnore != null && isIgnore.equals("1")) {
            Request.Builder newRequest = chain.request().newBuilder();
            newRequest.removeHeader("IgnoreAuth");
            return chain.proceed(newRequest.build());
        }


        Request.Builder newRequest = chain.request().newBuilder();
        String isSearchLocation = chain.request().header("isSearchLocation");
        if(isSearchLocation != null && isSearchLocation.equals("1")){
            HttpUrl url = chain.request().url();
            String queryNames = url.encodedQuery();
            StringBuilder builder = new StringBuilder(BuildConfig.GOOGLE_MAP_URL);
            builder.append('/');
            for (int i = 0; i < chain.request().url().pathSegments().size(); i++ ) {
                if(i == chain.request().url().pathSegments().size() -1){
                    builder.append(chain.request().url().pathSegments().get(i)).append("?").append(queryNames);
                }else{
                    builder.append(chain.request().url().pathSegments().get(i)).append('/');
                }
            }
            newRequest.removeHeader("isSearchLocation");
            return chain.proceed(newRequest.url(builder.toString()).build());
        }

        //Add Authentication
        String token = appPreferences.getToken();
        if (token != null && !token.equals("")) {
            newRequest.addHeader("Authorization", "Bearer " + token);
        }

        Response origResponse = chain.proceed(newRequest.build());
        if (origResponse.code() == 403 || origResponse.code() == 401) {
            LogService.i("Error http =====================> code: " + origResponse.code());
            appPreferences.removeKey(PreferencesService.KEY_BEARER_TOKEN);
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_EXPIRED_TOKEN);
            LocalBroadcastManager.getInstance(application.getApplicationContext()).sendBroadcast(intent);
        }

        String isMediaKind = chain.request().header("isMedia");
        if(isMediaKind != null && isMediaKind.equals("1")){
            StringBuilder builder = new StringBuilder(BuildConfig.MEDIA_URL);
            builder.append('/');
            for (String seg: chain.request().url().pathSegments()) {
                builder.append(seg).append('/');
            }
            newRequest.removeHeader("isMedia");
            return chain.proceed(newRequest.url(builder.toString()).build());
        }

        return origResponse;
    }
}
