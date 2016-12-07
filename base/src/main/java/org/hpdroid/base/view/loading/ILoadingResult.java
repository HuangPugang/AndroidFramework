package org.hpdroid.base.view.loading;

/**
 * Created by paul on 16/8/8.
 */
public interface ILoadingResult {

    /**
     * 开始加载
     */
    void start();

    /**
     * 停止加载
     */
    void stop();

    /**
     * 加载失败
     */
    void failed();

    /**
     * 加载成功
     */
    void success();


}
