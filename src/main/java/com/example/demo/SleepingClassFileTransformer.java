package com.example.demo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class SleepingClassFileTransformer implements ClassFileTransformer {

  private long testTimeout = 2000;

  public SleepingClassFileTransformer(String args) {
    if (args.contains("test.timeout=")) {
      testTimeout = Long.parseLong(args.split("test.timeout=")[1]);
    }
  }

  public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
      ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

    byte[] byteCode = classfileBuffer;

    if (className.equals("com/example/demo/Sleeping")) {

      try {
        ClassPool cp = ClassPool.getDefault();
        CtClass cc = cp.get("com.example.demo.Sleeping");
        CtMethod m = cc.getDeclaredMethod("randomSleep");
        m.addLocalVariable("elapsedTime", CtClass.longType);
        m.insertBefore("elapsedTime = System.currentTimeMillis();"
            + " try {java.lang.Thread.sleep(" + testTimeout + "L); } catch(Exception e) {e.printStackTrace();}");
        m.insertAfter("{"
            + "elapsedTime = System.currentTimeMillis() - elapsedTime;"
            + "System.out.println(\"Method Executed in ms: \" + elapsedTime  + \", of methodName: \" + \"" + m.getName() + "\");"
            + "}");

//        m.instrument(new ExprEditor() {
//          public void edit(MethodCall mc) throws CannotCompileException {
////            mc.replace("{"
////                + "long elapsedTime=System.currentTimeMillis(); "
//////                + "try {Thread.sleep(2000L);} catch(Exception e) {e.printStackTrace();}"
////                + "$_ = $proceed($$); "
////                + "elapsedTime = System.currentTimeMillis() - elapsedTime;"
////                + "System.out.println(\"Method Executed in ms: \" + elapsedTime );"
////                + "}");
//
//              mc.replace(
//                  "{ long stime = System.currentTimeMillis(); "
//                      + "$_ = $proceed($$); "
//                      + "System.out.println(\"" + mc.getClassName() + "." + mc.getMethodName()
//                      + ":\"+(System.currentTimeMillis()-stime));}");
//          }
//        });

        byteCode = cc.toBytecode();
        cc.detach();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return byteCode;
  }
}
