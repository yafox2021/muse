package org.yafox.muse.assign;

import org.yafox.muse.util.BeanUtil;

public class SingletonNode extends AbstractNode {
    
    public void assign(Object target) throws Exception {
        Object oldValue = BeanUtil.get(target, name);
        Object newValue = null;
        if (suggestion != null) {
            newValue = suggestion.suggest(oldValue);
            BeanUtil.set(target, name, newValue);
        } else {
            newValue = oldValue;
        }
        
        if (children != null && newValue != null) {
            for (Node child : children) {
                child.assign(newValue);
            }
        }
    }
    
}
