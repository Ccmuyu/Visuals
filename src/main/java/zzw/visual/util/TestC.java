package zzw.visual.util;

import zzw.visual.zk.TestInter;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhenwei.wang
 * @description TODO
 * @date 2020/7/29
 */
public class TestC {

   static String s = "package zzw.visual.util;\n" +
           "\n" +
           "import java.util.Date;\n" +
           "import zzw.visual.zk.TestInter;\n" +
           "/**\n" +
           " * @author zhenwei.wang\n" +
           " * @description TODO\n" +
           " * @date 2020/7/29\n" +
           " */\n" +
           "public class Test implements TestInter {\n" +
           "\n" +
           " static {\n" +
           "        System.out.println(\"init A..\");\n" +
           "    }\n" +
           "    public static void main(String[] args) {\n" +
           "        System.out.println(\"---------------------------\");\n" +
           "        System.out.println(new Date());\n" +
           "    }\n" +
           "\t\n" +
           "\tpublic void say(){\n" +
           "        System.out.println(\"---------------------------\");\n" +
           "\tSystem.out.println(new Date());\n" +
           "\t}\n" +
           "}";



    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        ClassLoader classLoader = ToolProvider.getSystemToolClassLoader();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);

        JavaFileObject javaFileObject = new JavaSourceFromString("Test",s);

        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, null, null, null, Arrays.asList(javaFileObject));
        if (task.call()) {
            System.out.println("编译成功。");
            Class.forName("zzw.visual.zk.TestInter");
            Class<?> aClass = Class.forName("zzw.visual.util.Test");
            Object o = aClass.newInstance();
            Class<?>[] interfaces = o.getClass().getInterfaces();
            System.out.println(Stream.of(interfaces).collect(Collectors.toList()));
//            inter.say();
        }
    }

}


class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;
    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/')+ Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}

class StringJavaFileObj implements JavaFileObject {
    String className;

    String sourceCode;

    public StringJavaFileObj(String className, String sourceCode) {
        this.className = className;
        this.sourceCode = sourceCode;
    }

    @Override
    public Kind getKind() {
        return Kind.SOURCE;
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {

        return true;
    }

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }

    @Override
    public URI toUri() {
        return null;
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return null;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(sourceCode.getBytes());
        return stream;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return null;
    }

    @Override
    public Writer openWriter() throws IOException {
        return null;
    }

    @Override
    public long getLastModified() {
        return 0;
    }

    @Override
    public boolean delete() {
        return false;
    }
}