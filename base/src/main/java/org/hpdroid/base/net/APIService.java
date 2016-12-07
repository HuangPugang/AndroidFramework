package org.hpdroid.base.net;

import org.hpdroid.base.common.bean.Banner;

import java.util.List;
import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by paul on 16/10/21.
 */

public interface APIService {

    @GET("http://www.tngou.net/api/lore/classify")
    Observable<List<Banner>> tngou(@QueryMap Map<String,String> para);


}
