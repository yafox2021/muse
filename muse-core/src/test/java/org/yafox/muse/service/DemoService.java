package org.yafox.muse.service;

import org.yafox.muse.annotation.Param;
import org.yafox.muse.annotation.Svc;

@Svc("demo")
public interface DemoService {

    String hello(@Param("name")String name) throws Exception;
    
    int add(int a, int b);
}
