#include "j_index.h"
#include "j_common.h"
#include "j_ensure.h"
#include "j_mappers.h"
#include "j_repository.h"
#include "j_util.h"
#include <git2.h>
#include <jni.h>
#include <stdio.h>

/**Pack jni objects to pass to update callback. */
typedef struct
{
    JNIEnv *env;
    jobject biConsumer;
} j_mached_cb_paylocads;

int standard_matched_cb(const char *path, const char *matched_pathspec, void *payload)
{
    if (payload == NULL)
    {
        return 0;
    }
    j_mached_cb_paylocads *j_payload = (j_mached_cb_paylocads *)payload;
    JNIEnv *env = j_payload->env;
    jobject biConsumer = j_payload->biConsumer;
    jclass jclz = (*env)->GetObjectClass(env, biConsumer);
    if (jclz == NULL)
    {
        return 0;
    }
    jmethodID accept = (*env)->GetMethodID(env, jclz, "accept", "(Ljava/lang/String;Ljava/lang/String;)V");
    if (accept == NULL)
    {
        return 0;
    }
    jstring j_path = (*env)->NewStringUTF(env, path);
    jstring j_pathspec = (*env)->NewStringUTF(env, matched_pathspec);
    (*env)->CallVoidMethod(env, biConsumer, accept, j_path, j_pathspec);
    (*env)->DeleteLocalRef(env, j_path);
    (*env)->DeleteLocalRef(env, j_pathspec);
    (*env)->DeleteLocalRef(env, jclz);
    return 0;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniUpdateAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;

    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_update_all(c_index, &c_pathspec, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddAll)(JNIEnv *env, jclass obj, jlong index, jobjectArray pathspec, jint flags, jobject biConsumer)
{
    j_mached_cb_paylocads j_payloads = {env, biConsumer};
    git_strarray c_pathspec = {0};
    git_index *c_index = (git_index *)index;
    git_strarray_of_jobject_array(env, pathspec, &c_pathspec);
    int error = git_index_add_all(c_index, &c_pathspec, (unsigned int)flags, standard_matched_cb, (void *)(&j_payloads));
    git_strarray_free(&c_pathspec);
    return error;
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAdd)(JNIEnv *env, jclass obj, jlong index, jobject srcEntry)
{
    git_index *c_index = (git_index *)index;
    git_index_entry c_source_entry;
    index_entry_from_java(env, &c_source_entry, srcEntry);
    return git_index_add(c_index, &c_source_entry);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniWrite)(JNIEnv *env, jclass obj, jlong index)
{
    return git_index_write((git_index *)index);
}

JNIEXPORT void JNICALL J_MAKE_METHOD(Index_jniFree)(JNIEnv *env, jclass obj, jlong index)
{
    git_index_free((git_index *)index);
}

JNIEXPORT jint JNICALL J_MAKE_METHOD(Index_jniAddByPath)(JNIEnv *env, jclass obj, jlong index, jstring path)
{
    git_index *c_index = (git_index *)index;
    char *c_path = j_copy_of_jstring(env, path, false);
    int error = git_index_add_bypath(c_index, c_path);
    free(c_path);
    return error;
}
