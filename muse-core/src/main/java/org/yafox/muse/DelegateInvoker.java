package org.yafox.muse;

public class DelegateInvoker implements Invoker {

    private Invoker delegate;

    public String getId() {
        return delegate.getId();
    }
    
    public Class<?> getRequestType() {
        return delegate.getRequestType();
    }

    public Class<?> getResponseType() {
        return delegate.getResponseType();
    }

    public void setTarget(Object target) {
        delegate.setTarget(target);
    }

    public Object invoke(Object input) throws Exception {
        return delegate.invoke(input);
    }

    public Invoker getDelegate() {
        return delegate;
    }

    public void setDelegate(Invoker delegate) {
        this.delegate = delegate;
    }

}
