package org.yafox.muse.assign;

import java.util.List;

public interface Node {

    String getName();
    
    List<Node> getChildren();
    
    void assign(Object target) throws Exception;
}
