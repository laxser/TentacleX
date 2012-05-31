package com.laxser.tentaclex.registry;


/**
 * Registry中的配置信息发生变更的时候会触发{@link TentacleRegistryListener}
 * 中的相关事件处理方法
 * 
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2011-4-26 上午11:01:43
 */
public interface TentacleRegistryListener {
    
    /**
     * 有节点被置为disabled状�?的时候触�?     * @param node
     */
    public void onNodeDisabled(TentacleServiceDescriptor node);
    
    /**
     * 有节点被删除的时候触�?     * @param node
     */
    public void onNodeDeleted(TentacleServiceDescriptor node);
}
