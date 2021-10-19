package org.apache.fineract.api.interceptor;

public interface Callback<OUT> {

    public OUT call();
}
