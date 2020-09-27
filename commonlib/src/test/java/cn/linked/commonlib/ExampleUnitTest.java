package cn.linked.commonlib;

import org.junit.Test;

import cn.linked.commonlib.jni.diffpatch.BSDiffPatch;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void main(){
        new BSDiffPatch();
        System.out.println(1);
    }
}