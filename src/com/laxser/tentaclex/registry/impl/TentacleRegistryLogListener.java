package com.laxser.tentaclex.registry.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.laxser.tentaclex.registry.TentacleRegistryListener;
import com.laxser.tentaclex.registry.TentacleServiceDescriptor;

/**
 * 
 * @author laxser
 * @ contact qifan.du@renren-inc.com
 * date: 2012-5-31
 */
public class TentacleRegistryLogListener implements TentacleRegistryListener {

    protected static Log logger = LogFactory.getLog(TentacleRegistryLogListener.class);
    
    @Override
    public void onNodeDeleted(TentacleServiceDescriptor node) {
        if (logger.isInfoEnabled()) {
            logger.info("Node deleted: " + node);
        }
    }

    @Override
    public void onNodeDisabled(TentacleServiceDescriptor node) {
        if (logger.isInfoEnabled()) {
            logger.info("Node disabled: " + node);
        }
    }

}
