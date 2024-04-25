package org.incava.diffj.app;

import java.util.Set; 
import java.util.HashSet; 

public class MethodDiff {

    private Set<String> methods; 

    private static MethodDiff instance;

    private MethodDiff() {
        methods = new HashSet<String>();
    }

    public static MethodDiff instance() {
        if(instance == null) {
            instance = new MethodDiff();
        }
        return instance;
    }

    public void addChangedMethod(String method) {
        methods.add(method);
    }

    public void dump() {
        for(String m: methods) {
            System.out.println(m);
        }
    }

    public int numberOfChangedMethods() {
        return methods.size();
    }

    public Set<String> getChangedMethods() {
        return methods;
    }

}