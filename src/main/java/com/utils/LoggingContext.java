package com.utils;

import org.springframework.web.context.request.RequestScope;

public class LoggingContext {

    //adds scope - in hindsight that makes a lot of sense
    RequestScope scope;
    public String var1;
    public String var2;

    public void setVar1(String var1) {
        this.var1 = var1;
    }

    public void setVar2(String var2) {
        this.var2 = var2;
    }

    public String getVar2() {
        return var2;
    }

    public String getVar1() {
        return var1;
    }

    public void setScope(RequestScope scope) {
        this.scope = scope;
    }
    public RequestScope getScope() {
        return this.scope;
    }

}
