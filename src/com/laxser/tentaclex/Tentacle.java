package com.laxser.tentaclex;

import com.laxser.tentaclex.Octopus.TentacleResponseCallback;

/**
 * XoaClient的接口
 * 
 *@author laxser  Date 2012-6-1 上午8:44:24
@contact [duqifan@gmail.com]
@Tentacle.java

 */
public interface Tentacle {

    /**
     * 提交一个XOA请求，并获取相应的调用信息
     * 
     * @param xoaMethod 请求本身
     * @param callback 回调逻辑
     * @return 调用信息
     */
    public InvocationInfo submit(Method xoaMethod, TentacleResponseCallback callback);
}
