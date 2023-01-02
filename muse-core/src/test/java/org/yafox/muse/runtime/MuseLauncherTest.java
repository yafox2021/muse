package org.yafox.muse.runtime;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.yafox.muse.Invoker;
import org.yafox.muse.MockPallet;
import org.yafox.muse.service.impl.DemoServiceImpl;

public class MuseLauncherTest {

    @Test
    public void test() throws Exception {
        MockPallet pallet = new MockPallet();
        pallet.addBean("demoService", new DemoServiceImpl());
        
        DynamicImpl dynamic = new DynamicImpl();
        dynamic.setAssistant(new AssistantImpl());  
        InvokerBucketImpl invokerBucket = new InvokerBucketImpl();
        
        MuseLauncher launcher = new MuseLauncher();
        launcher.setDynamic(dynamic);
        launcher.setInvokerBucket(invokerBucket);
        launcher.setPallet(pallet);
        launcher.launch();
        
        Invoker invoker = invokerBucket.findInvoker("/demo/hello@rule1");
        assertNotNull(invoker);
    }

}
