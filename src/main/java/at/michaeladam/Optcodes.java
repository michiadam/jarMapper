package at.michaeladam;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Optcodes {

    private static final int ACC_SUPER = 0x0020; // class
    private static final int ACC_SYNCHRONIZED = 0x0020; // method
    private static final int ACC_OPEN = 0x0020; // module
    private static final int ACC_TRANSITIVE = 0x0020; // module requires
    private static final int ACC_VOLATILE = 0x0040; // field
    private static final int ACC_BRIDGE = 0x0040; // method
    private static final int ACC_STATIC_PHASE = 0x0040; // module requires
    private static final int ACC_VARARGS = 0x0080; // method
    private static final int ACC_TRANSIENT = 0x0080; // field
    private static final int ACC_NATIVE = 0x0100; // method
    private static final int ACC_INTERFACE = 0x0200; // class
    private static final int ACC_ABSTRACT = 0x0400; // class, method
    private static final int ACC_STRICT = 0x0800; // method
    private static final int ACC_SYNTHETIC = 0x1000; // class, field, method, parameter, module *
    private static final int ACC_ANNOTATION = 0x2000; // class
    private static final int ACC_ENUM = 0x4000; // class(?) field inner
    private static final int ACC_MANDATED = 0x8000; // field, method, parameter, module, module *
    private static final int ACC_MODULE = 0x8000; // class

    public static enum OptCode {
        PUBLIC("public", Opcodes.ACC_PUBLIC, true,true,true),
        PRIVATE("private", Opcodes.ACC_PRIVATE, true,true,true),
        PROTECTED("protected", Opcodes.ACC_PROTECTED, true,true,true),
        STATIC("static", Opcodes.ACC_STATIC, false,true,true),
        FINAL("final", Opcodes.ACC_FINAL, true,true,true),
        SYNCHRONIZED("synchronized", Opcodes.ACC_SYNCHRONIZED, false,true,false);



        private String name;
        private boolean isClass;
        private boolean isMethod;
        private boolean isField;
        private int optCode;



        OptCode(String name, int optCode, boolean isClass, boolean isMethod, boolean isField) {
            this.name = name;
            this.isClass = isClass;
            this.isMethod = isMethod;
            this.optCode = optCode;
            this.isField = isField;
        }

        public static  List<OptCode> getAllMethodOptcodes(){
            return Arrays.stream(OptCode.values()).filter(optCode -> optCode.isMethod).collect(Collectors.toList());
        }


        public String getName() {
            return name;
        }

        public int getOptCode() {
            return optCode;
        }
    }



}
