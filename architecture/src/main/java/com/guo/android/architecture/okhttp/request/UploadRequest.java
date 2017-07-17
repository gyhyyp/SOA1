package com.guo.android.architecture.okhttp.request;

import com.guo.android.architecture.okhttp.Param;
import com.guo.android.architecture.okhttp.utils.HeaderUtils;

import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;



public class UploadRequest {

    public static Request buildPostRequest(String url, Map<String, String> headers, Object tab, RequestBody requestBody) {

        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.post(requestBody).url(url);
        reqBuilder.tag(tab);

        List<Param> valdatedHeaders = HeaderUtils.validateHeaders(headers);
        if (valdatedHeaders != null && valdatedHeaders.size() > 0) {
            for (int i = 0; i < valdatedHeaders.size(); i++) {
                Param param = valdatedHeaders.get(i);
                String key = param.key;
                String value = param.value;
                reqBuilder.addHeader(key, value);
            }

        }
        return reqBuilder.build();


    }
}
