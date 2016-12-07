package org.hpdroid.base.net.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import org.hpdroid.base.net.ErrorCode;
import org.hpdroid.base.net.RespResult;
import org.hpdroid.base.net.exception.RespException;
import org.hpdroid.base.net.exception.TokenErrorException;
import org.hpdroid.base.net.exception.UserNotLoginErrorException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());

        try {
            T result = adapter.read(jsonReader);
            if (result instanceof RespResult) {
                RespResult resp = (RespResult) result;
                if (resp.getStatus() != 0) {

                    if (resp.getStatus() == ErrorCode.kTokenInvalid.getCode()) {
                        throw new TokenErrorException(resp.getMsg());
                    } else if (resp.getStatus() == ErrorCode.kUnLogin.getCode()){
                        throw new UserNotLoginErrorException(resp.getMsg());
                    }else {
                        throw new RespException(resp.getStatus(), resp.getMsg());
                    }
                }
            }
            return result;
        } finally {
            value.close();
        }
    }
}
