package com.couponly.server.services;

import com.couponly.server.model.Deal;
import com.couponly.server.model.DealWrapper;
import com.couponly.server.model.RawResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {
    private final SanitationService sanitationService;
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();
    private Logger logger = LoggerFactory.getLogger(RequestService.class);

    @Value("${secret.key}")
    private String KEY;
    private String BASE_URL = "api.discountapi.com";
    private String PATH = "/v2/deals";

    public RequestService(SanitationService sanitationService) {
        this.sanitationService = sanitationService;
    }

    public RawResponse requestRawResponse() {
        return basicRequest("");
    }
    public List<DealWrapper> requestAllDeals(String params) {
       return basicRequest(params).getDeals();
    }
    public List<DealWrapper> requestDealsByLocation(String locationName) {
        return basicRequest("location=" + locationName).getDeals();
    }

    public List<DealWrapper> makeComplexRequest(Map<String, String> params) {
        URI uri = null;
        try {
            uri =  new URIBuilder()
                    .setScheme("https")
                    .setHost(BASE_URL)
                    .setPath(PATH)
                    .setParameters(mapToNameValuePair(params))
                    .setParameter("online", "false")
                    .setParameter("provider_slugs", "groupon")
                    .setParameter("radius", "40")
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        RawResponse response = makeRequest(uri);
        return sanitationService.cleanApiKey(response.getDeals());
    }

    public RawResponse makeRequest(URI uri) {
        Request request = new Request.Builder()
                .url(uri.toString())
                .addHeader("Authorization", "api_key " + KEY)
                .build();
        return getRawResponse(request);
    }

    public RawResponse basicRequest(String params) {
        Request request = new Request.Builder()
                .url("https://"+BASE_URL + PATH)
                .addHeader("Authorization", "api_key " + KEY)
                .build();
        return getRawResponse(request);
    }
    private RawResponse getRawResponse(Request request) {
        Response response = null;
        String json = "";
        try {
            response = client.newCall(request).execute();
            json = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(json, RawResponse.class);
    }
    //Local response
    public List<Deal> mockResponse(){
        String userDirectory = System.getProperty("user.dir");
        StringBuilder sb =  new StringBuilder();
        File file = new File(userDirectory + "\\src\\main\\resources\\assets\\pizzas.json");

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return gson.fromJson(reader,new TypeToken<List<Deal>>(){}.getType());
    }
    //Helper Methods
    private List<NameValuePair> mapToNameValuePair(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<>(params.size());
        params.forEach( (key ,value)  -> list.add(new BasicNameValuePair(key, value)));
        return list;
    }
}
