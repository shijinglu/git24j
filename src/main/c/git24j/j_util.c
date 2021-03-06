#include "j_util.h"
#include "j_common.h"
#include "j_exception.h"
#include <assert.h>
#include <stdio.h>
#include <string.h>

void j_save_c_pointer(JNIEnv *env, void *ptr, jobject object, const char *setterName)
{
    jclass clz = (*env)->GetObjectClass(env, object);
    if (clz == NULL)
    {
        j_throw_jni_error(env, "cannot save native pointer because the receiver is not identifiable");
    }

    jmethodID method = NULL;
    if ((method = (*env)->GetMethodID(env, clz, setterName, "(J)V")) == NULL)
    {
        j_throw_jni_error(env, "cannot find setter to save native pointer.");
    }

    (*env)->CallVoidMethod(env, object, method, (long)ptr);
}

void git_strarray_of_jobject_array(JNIEnv *env, jobjectArray jstrarr, git_strarray *out)
{
    assert(out);

    jsize len = (*env)->GetArrayLength(env, jstrarr);
    out->count = len;
    out->strings = (char **)calloc(out->count, sizeof(char *));

    assert(out->strings != NULL);
    for (jsize i = 0; i < len; i++)
    {
        jstring jstr = (jstring)(*env)->GetObjectArrayElement(env, jstrarr, i);
        const char *cstr = (*env)->GetStringUTFChars(env, jstr, NULL);
        out->strings[i] = strdup(cstr);
        (*env)->ReleaseStringUTFChars(env, jstr, cstr);
    }
}
