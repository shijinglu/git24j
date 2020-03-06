import stringcase
import re
from .utils import get_jtype, get_java_type


class Git2Structure(object):
    """
    Parse and generate getter setter
    """
    def __init__(self, fsig: str, clazz: str):
        """
        Initialize with object name
        """
        splits = clazz.split(".")
        obj_name = splits[0]
        sub_obj_name = splits[-1] if len(splits) > 1 else ""
        self.c_obj = obj_name
        self.c_sub_obj = sub_obj_name
        # InitOptions
        self.obj_pc = stringcase.pascalcase(self.c_obj)
        self.sub_obj_pc = stringcase.pascalcase(self.c_sub_obj)
        # initOptions
        self.obj_cc = stringcase.camelcase(self.c_obj)
        self.sub_obj_cc = stringcase.camelcase(self.c_sub_obj)

        self.group_dict = dict(obj_name=obj_name)
        fsig = fsig.strip().rstrip(";")
        self.fsig = fsig
        sx = [m.end() for m in re.finditer(r'[ *]', fsig)][-1]
        splits = fsig.strip().split(" ")
        self.c_var = fsig[sx:]
        self.c_type = fsig[:sx]  # unsigned int
        self.jtype = get_jtype(self.c_type)  # jint
        self.java_type = get_java_type(self.jtype)  # int
        # CheckoutOpts
        self.var_pc = stringcase.pascalcase(self.c_var)
        # checkoutOpts
        self.var_cc = stringcase.camelcase(self.c_var)

    def header_getter(self):
        """
        JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong clonePtr);
        """
        return (
            f"/** {self.fsig}*/\n"
            f"JNIEXPORT {self.jtype} JNICALL J_MAKE_METHOD("
            f"{self.obj_pc}_jni{self.sub_obj_pc}Get{self.var_pc})(JNIEnv *env, jclass obj, jlong {self.sub_obj_cc}Ptr);"
        )

    def header_setter(self):
        return (
            f"/** {self.fsig}*/\n"
            f"JNIEXPORT void JNICALL J_MAKE_METHOD({self.obj_pc}_jni{self.sub_obj_pc}Set{self.var_pc})"
            f"(JNIEnv *env, jclass obj, jlong {self.sub_obj_cc}Ptr, {self.jtype} {self.var_cc});"
        )

    def body_getter(self):
        """
        JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong clonePtr)
        {
            return ((git_clone_options *)clonePtr)->version;
        }
        """
        getter = self.header_getter().rstrip(';')
        c_type = f"git_{self.c_obj}_{self.c_sub_obj}"
        return (
            f"{getter}\n"
            "{\n"
            f"    return (({c_type} *){self.sub_obj_cc}Ptr)->{self.c_var};\n"
            "}\n"
        )

    def body_setter(self):
        """
        """
        todo = "/**FIXME: callback and payload needs human review*/\n" if "_cb" in self.fsig or "payload" in self.fsig else ""
        setter = self.header_setter().rstrip(';')
        c_obj_type = f"git_{self.c_obj}_{self.c_sub_obj}"
        return (
            f"{setter}\n"
            "{\n"
            f"{todo}"
            f"    (({c_obj_type} *){self.sub_obj_cc}Ptr)->{self.c_var} = ({self.c_type}){self.var_cc};\n"
            "}\n"
        )

    def jni_getter(self):
        """
        static native int jniOptionsGetVersion();
        public int getVersion() {
            return jniOptionsGetVersion();
        }
        """
        sub_class_reminder = f"//TODO: moved to {self.sub_obj_pc}\n" if self.sub_obj_pc else ""
        return (
            f"/**{self.fsig}*/\n"
            f"static native {self.java_type} jni{self.sub_obj_pc}Get{self.var_pc}();\n"
            f"{sub_class_reminder}"
            f"public int getVersion() {{\n"
            f"    return jni{self.sub_obj_pc}Get{self.var_pc}();\n"
            f"}}\n"
        )
        

    def jni_setter(self):
        """
        static native void jniOptionsSetVersion(int version);
        public void setVersion(int version) {
            jniOptionsSetVersion(version);
        }
        """
        sub_class_reminder = f"//TODO: moved to {self.sub_obj_pc}\n" if self.sub_obj_pc else ""
        return (
            f"/**{self.fsig}*/\n"
            f"static native void jni{self.sub_obj_pc}Set{self.var_pc}({self.jtype} {self.var_cc});\n"
            f"{sub_class_reminder}"
            f"public void set{self.var_pc}(int {self.var_cc}) {{\n"
            f"    jni{self.sub_obj_pc}Set{self.var_pc}({self.var_cc});\n"
            f"}}\n"
        )
