package org.yafox.muse.validate;

import java.util.List;

public interface Node extends Validator {

    String getName();
    
    List<Node> getChildren();
    
}
