package org.yafox.muse.validate;

import java.util.List;

public interface Node extends Validation {

    String getName();
    
    List<Node> getChildren();
    
}
