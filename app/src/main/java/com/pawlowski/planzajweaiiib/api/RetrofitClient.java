package com.pawlowski.planzajweaiiib.api;

import android.content.Context;
import android.os.Build;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.pawlowski.planzajweaiiib.consts.Const;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;


import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.tls.Certificates;
import okhttp3.tls.HandshakeCertificates;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;


    public static Retrofit getClient(Context context) {

        if (okHttpClient == null)
            initOkHttp(context);

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Const.BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static void initOkHttp(final Context context) {



        OkHttpClient.Builder httpClient = getOkHttpClientDependingOnAndroidVersion().newBuilder()//getUnsafeOkHttpClient()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);

        httpClient.addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();


                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        okHttpClient = httpClient.build();
    }

    public static OkHttpClient getOkHttpClientDependingOnAndroidVersion()
    {
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            return getOkHttpClientBelow24(); //Installing certificates
        }
        else
        {
            return new OkHttpClient(); //Certificates installed in res/xml/network_security_config.xml
        }
    }



    public static OkHttpClient getOkHttpClientBelow24() {


        HandshakeCertificates certificates = new HandshakeCertificates.Builder()
                .addTrustedCertificate(cert)
                // Uncomment if standard certificates are also required.
                //.addPlatformTrustedCertificates()
                .build();

        return new OkHttpClient.Builder()
                .sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager())
                .build();
    }



        static final X509Certificate cert = Certificates.decodeCertificatePem("-----BEGIN CERTIFICATE-----\n" +
            "MIIHwjCCBaqgAwIBAgIRAPRKXeYhyjIj0mLxZ8XI5LMwDQYJKoZIhvcNAQEMBQAw\n" +
            "RDELMAkGA1UEBhMCTkwxGTAXBgNVBAoTEEdFQU5UIFZlcmVuaWdpbmcxGjAYBgNV\n" +
            "BAMTEUdFQU5UIE9WIFJTQSBDQSA0MB4XDTIxMDgxNzAwMDAwMFoXDTIyMDgxNzIz\n" +
            "NTk1OVowgaMxCzAJBgNVBAYTAlBMMRUwEwYDVQQIDAxNYcWCb3BvbHNraWUxEDAO\n" +
            "BgNVBAcMB0tyYWvDs3cxRjBEBgNVBAoMPUFrYWRlbWlhIEdvcm5pY3pvIEh1dG5p\n" +
            "Y3phIGltIFN0YW5pc8WCYXdhIFN0YXN6aWNhIHcgS3Jha293aWUxIzAhBgNVBAMT\n" +
            "GnBsYW56YWplYy5lYWlpYi5hZ2guZWR1LnBsMIIBIjANBgkqhkiG9w0BAQEFAAOC\n" +
            "AQ8AMIIBCgKCAQEA55CptIqZ4qJ5nNEca8f2PvLZG0A84c4PYTwIfp16cUGYzT8d\n" +
            "yN/IJmQ8HB6Sbet4KnLX4Cnk6UimE27rT/s3vORE9Qzo6UWcoOLjYXpsscbVDol7\n" +
            "0IaxSLbDpXvg1O/wo65gDp/K4/pBtwa+PKJODH0TPrKwV6YEoRWMbkNdOMjly85E\n" +
            "n/3witT8y1oiDlk8hpNfveTOAoMsGf0ytTlh8kbEYBMNPDau/RxDRf6FbDYszMyv\n" +
            "/SUowjMOg/m2uDbTEF5KUe8D+1wAOqb8KkWBwtD19e8jNZJR5mHJmPIMTpW2PYWb\n" +
            "1uX/G0R094msp89Kh4z8O8lQ/pVIgj2R1+HniQIDAQABo4IDTTCCA0kwHwYDVR0j\n" +
            "BBgwFoAUbx01SRBsMvpZoJ68iugflb5xegwwHQYDVR0OBBYEFKxFB+x7QU8xnz10\n" +
            "CTRpEb46IwnfMA4GA1UdDwEB/wQEAwIFoDAMBgNVHRMBAf8EAjAAMB0GA1UdJQQW\n" +
            "MBQGCCsGAQUFBwMBBggrBgEFBQcDAjBJBgNVHSAEQjBAMDQGCysGAQQBsjEBAgJP\n" +
            "MCUwIwYIKwYBBQUHAgEWF2h0dHBzOi8vc2VjdGlnby5jb20vQ1BTMAgGBmeBDAEC\n" +
            "AjA/BgNVHR8EODA2MDSgMqAwhi5odHRwOi8vR0VBTlQuY3JsLnNlY3RpZ28uY29t\n" +
            "L0dFQU5UT1ZSU0FDQTQuY3JsMHUGCCsGAQUFBwEBBGkwZzA6BggrBgEFBQcwAoYu\n" +
            "aHR0cDovL0dFQU5ULmNydC5zZWN0aWdvLmNvbS9HRUFOVE9WUlNBQ0E0LmNydDAp\n" +
            "BggrBgEFBQcwAYYdaHR0cDovL0dFQU5ULm9jc3Auc2VjdGlnby5jb20wRQYDVR0R\n" +
            "BD4wPIIacGxhbnphamVjLmVhaWliLmFnaC5lZHUucGyCHnd3dy5wbGFuemFqZWMu\n" +
            "ZWFpaWIuYWdoLmVkdS5wbDCCAX4GCisGAQQB1nkCBAIEggFuBIIBagFoAHYARqVV\n" +
            "63X6kSAwtaKJafTzfREsQXS+/Um4havy/HD+bUcAAAF7VDNhpwAABAMARzBFAiEA\n" +
            "7u7zgIbhYN+nsiwulfVVuaQyuQ1uK44vGsxGySBlJNQCIHYYBHUJiF/R6tW3AQOl\n" +
            "C/s1Z+kk1sQDwxKJjqLjwTZSAHYAQcjKsd8iRkoQxqE6CUKHXk4xixsD6+tLx2jw\n" +
            "kGKWBvYAAAF7VDNhtgAABAMARzBFAiEA2eS0VTaSvyA/n1TocIDL44D3slep0aUw\n" +
            "WVDf7bo5Z48CIBo8+UI2cyjdR/MGdeUvJ9pBtwKL7TcuY7zKezyboXnKAHYAKXm+\n" +
            "8J45OSHwVnOfY6V35b5XfZxgCvj5TV0mXCVdx4QAAAF7VDNhiQAABAMARzBFAiEA\n" +
            "xd9GniOJ3pfjWhogNP/BKspPs9nWa2RZj39StxwALg8CICi2a1rCE27cxTwNSDtS\n" +
            "4csFDu4eIkYQ9x1yuafkUNlwMA0GCSqGSIb3DQEBDAUAA4ICAQBvYm99O5U7V4N4\n" +
            "r0ma0C3dtqktwR6ho39UjFf6+Fbba6xeOvCSlwK/oyFc/JnSU/C1C9LtEWrll0cU\n" +
            "OAK4MEk6L9krcXgZ2lPj85LJ+H1JcWc8DUUdEd5PDJgE0fFMaOV5IIb3w4pQymQD\n" +
            "vCCW2gFmQux5NxBcIJlhEHRoQoAYXpVBufYGNtdlZXp41N4/N2F0V3AGvf4KsO58\n" +
            "onuR+PaavPmGLQnbIUFXS5JkLqJboVSDm5HKhOcpWvwmDneJ4M4lKR4h1eTPCuQz\n" +
            "NS7NHNWpPdf1sXlh5s/GMdVW54PFECkK8PeHtoEybXtpSOZ3bDH8PwcXSZ7rrgCu\n" +
            "plZ693lvtv4O2nxb17eniUmFl5wOZPrcML5/L7N7BEHvJTIH5RVs1ylxTEjbEliJ\n" +
            "99YZA2aaJiSduCOH3u/Fa629TCJ/zydxfYQyHFovB33gBs8zbfsnnKQZmJ/S0ypi\n" +
            "2eytB0uLUFk34kym9RzV8tMlDJj4NmmFFXJ8VpNHnPPOuWg6SHO2zlkfYnkndrz6\n" +
            "CyBiLZ6zPXtzEkDwRvUkvV+T/p8mliodX8QMKFVwztUsOMjJ+wDrBnuEjboMdRyz\n" +
            "FX1q79nia6RAdd4lNvkBsc0XaGlO1jFnTPxLPboFmoygfOm5VRCmLzmRTum3dR5R\n" +
            "+MrR6Ype7q11FapTDd59QCcB09OK7g==\n" +
            "-----END CERTIFICATE-----");
}