package org.yafox.muse.service.impl;

import org.yafox.muse.service.DemoService;

public class DemoServiceImpl implements DemoService {

    public String hello(String name) throws Exception {
        if (name == null) {
            throw new Exception("name is null");
        }
        return "hello " + name;
    }

    public int add(int a, int b) {
        // TODO Auto-generated method stub
        return 0;
    }

}
