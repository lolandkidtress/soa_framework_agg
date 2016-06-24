package com.James.demo.javasist;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Loader;
import javassist.NotFoundException;

/**
 * Created by James on 16/5/27.
 * 通过javasist方式执行,
 * 只能通过main入口
 */
public class JavasistLaunch {

    final static String class_name = "com.James.demo.sample.Launch";
    final static String method_name = "buildString";

    public void javasistMainLaunch(){
        try {

            // start by getting the class file and method
            ClassPool pool =ClassPool.getDefault();
            CtClass clas = pool.get(class_name);

            Loader loader = new Loader(pool);
            if (clas == null) {
                System.err.println("Class " + class_name + " not found");
            } else {

                // add timing interceptor to the class
                addTiming(clas, method_name);
//                    clas.writeFile();
                System.out.println("Added timing to method " +
                        class_name + "." + method_name);
            }

            String[] pargs = new String[]{"19","b","c"};
            //执行main方法
            loader.run(class_name,pargs);
        } catch (CannotCompileException ex) {
            ex.printStackTrace();
        } catch (NotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JavasistLaunch javasistLaunch = new JavasistLaunch();
        javasistLaunch.javasistMainLaunch();
    }


    private static void addTiming(CtClass clas, String mname)
            throws Exception {

        //  get the method information (throws exception if method with
        //  given name is not declared directly by this class, returns
        //  arbitrary choice if more than one with the given name)
        CtMethod mold = clas.getDeclaredMethod(mname);

        //  rename old method to synthetic name, then duplicate the
        //  method with original name for use as interceptor
        String nname = mname+"$impl";

        mold.setName(nname);
        CtMethod mnew = CtNewMethod.copy(mold, mname, clas, null);

        //  start the body text generation by saving the start time
        //  to a local variable, then call the timed method; the
        //  actual code generated needs to depend on whether the
        //  timed method returns a value
        String type = mold.getReturnType().getName();
        StringBuffer body = new StringBuffer();
        body.append("{\nlong start = System.currentTimeMillis();\n");
        if (!"void".equals(type)) {
            body.append(type + " result = ");
        }
        body.append(nname + "($$);\n");

        //  finish body text generation with call to print the timing
        //  information, and return saved value (if not void)
        body.append("System.out.println(\"Call to method " + mname +
                " took \" +\n (System.currentTimeMillis()-start) + " +
                "\" ms.\");\n");
        if (!"void".equals(type)) {
            body.append("return result;\n");
        }
        body.append("}");

        //  replace the body of the interceptor method with generated
        //  code block and add it to class
        mnew.setBody(body.toString());
        clas.addMethod(mnew);
        clas.writeFile();

        //  print the generated code block just to show what was done
        System.out.println("Interceptor method body:");
        System.out.println(body.toString());
    }

}
