package org.yafox.muse;

public interface FaultHandler {

    Fault handle(Throwable t);
    
}
