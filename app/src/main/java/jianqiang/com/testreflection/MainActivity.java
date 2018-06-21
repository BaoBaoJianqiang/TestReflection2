package jianqiang.com.testreflection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Proxy;

import jianqiang.com.testreflection.joor.Reflect;
import jianqiang.com.testreflection.joor.ReflectException;

import static jianqiang.com.testreflection.joor.Reflect.on;

public class MainActivity extends Activity {

    //todo
    //静态方法: invoke(null)

    Button btnNormal;
    Button btnHook;
    TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow = (TextView) findViewById(R.id.txtShow);


        btnNormal = (Button) findViewById(R.id.btnNormal);
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //测试Class
                //test1();

                //测试ctor
                //test2();

                //测试method
                //test3();

                //测试field
                //test4();

                //测试Singleton
                AMN.getDefault().doSomething();
                test5();
                AMN.getDefault().doSomething();

                //测试private static final
                test6();
            }
        });

        btnHook = (Button) findViewById(R.id.btnHook);
        btnHook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //1.获取Class类
    public void test1() {
        //通过getClass，每个Class都有这个函数
        String str = "abc";
        Class c1 = str.getClass();

        //以下3个语法等效
        Reflect r1 = on(Object.class);
        Reflect r2 = on("java.lang.Object");
        Reflect r3 = on("java.lang.Object", ClassLoader.getSystemClassLoader());

        //以下2个语法等效，实例化一个Object变量，得到Object.class
        Object o1 = on(Object.class).<Object>get();
        Object o2 = on("java.lang.Object").get();

        String j2 = on((Object)"abc").get();
        int j3 = on(1).get();

        //等价于Class.forName()
        try {
            Class j4 = on("android.widget.Button").type();
        }
        catch (ReflectException e) {
            e.printStackTrace();
        }
    }

    //2.1.获取类的构造函数，测试类TestClassCtor
    public void test2() {
        TestClassCtor r = new TestClassCtor();
        Class temp = r.getClass();
        String className = temp.getName();        // 获取指定类的类名

        //public构造函数
        Object obj = on(temp).create().get();
        Object obj2 = on(temp).create(1, "abc").get();

        //private构造函数
        TestClassCtor obj3 = on(TestClassCtor.class).create(1, 1.1).get();
        String a = obj3.getName();
    }

    //3.获取类的方法，调用它
    public void test3() {
        try {
            //以下4句话，创建一个对象
            TestClassCtor r = new TestClassCtor();
            Class temp = r.getClass();
            Reflect reflect = on(temp).create();

            //调用一个private实例方法
            String a1 = reflect.call("doSOmething", "param1").get();

            //调用一个public实例方法
            String a2 = reflect.call("getName").get();

            //调用一个private静态方法
            on(TestClassCtor.class).call("work").get();

            //调用一个public静态方法
            on(TestClassCtor.class).call("printAddress").get();

        } catch (ReflectException e) {
            e.printStackTrace();
        }
    }

    //4.获取类的字段，修改它
    public void test4() {
        try {
            //实例字段
            Reflect obj = on("jianqiang.com.testreflection.TestClassCtor")
                    .create(1, 1.1);
            obj.set("name", "jianqiang");
            Object obj1 = obj.get("name");

            //静态字段
            on("jianqiang.com.testreflection.TestClassCtor")
                    .set("address", "avcccc");
            Object obj2 = on("jianqiang.com.testreflection.TestClassCtor")
                    .get("address");

            Log.v("baobao234", obj1.toString());
            Log.v("baobao234", obj2.toString());


        } catch (ReflectException e) {
            e.printStackTrace();
        }
    }

    public void test5() {
        try {
            //获取AMN的gDefault单例gDefault，gDefault是静态的
            Object gDefault = on("jianqiang.com.testreflection.AMN").get("gDefault");

            // gDefault是一个 android.util.Singleton对象; 我们取出这个单例里面的mInstance字段
            // mInstance就是原始的ClassB2Interface对象
            Object mInstance = on(gDefault).get("mInstance");

            // 创建一个这个对象的代理对象ClassB2Mock, 然后替换这个字段, 让我们的代理对象帮忙干活
            Class<?> classB2Interface = on("jianqiang.com.testreflection.ClassB2Interface").type();
            Object proxy = Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class<?>[] { classB2Interface },
                    new ClassB2Mock(mInstance));

            on(gDefault).set("mInstance", proxy);

        } catch (ReflectException e) {
            e.printStackTrace();
        }
    }

    public void test6() {
        //实例字段
        Reflect obj = on("jianqiang.com.testreflection.User").create();
        obj.set("name", "jianqiang");
        Object newObj = obj.get("name");

        //静态字段
        Reflect obj2 = on("jianqiang.com.testreflection.User");
        obj2.set("userId", "123");
        Object newObj2 = obj2.get("userId");
    }
}