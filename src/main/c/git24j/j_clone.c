#include "j_clone.h"
#include "j_common.h"
#include "j_mappers.h"
#include "j_util.h"
#include <assert.h>
#include <git2.h>
#include <stdio.h>

extern j_constants_t *jniConstants;

/** -------- Wrapper Body ---------- */
/** int git_clone_clone(git_repository **out, const char *url, const char *local_path, const git_clone_options *options); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniClone)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring local_path, jlong optionsPtr)
{
    git_repository *c_out;
    char *c_url = j_copy_of_jstring(env, url, true);
    char *c_local_path = j_copy_of_jstring(env, local_path, true);
    int r = git_clone(&c_out, c_url, c_local_path, (git_clone_options *)optionsPtr);
    (*env)->CallVoidMethod(env, out, jniConstants->midAtomicLongSet, (long)c_out);
    free(c_url);
    free(c_local_path);
    return r;
}

/** int git_clone_init_options(git_clone_options *opts, unsigned int version); */
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version)
{
    int r = git_clone_init_options((git_clone_options *)optsPtr, version);
    return r;
}

// getter
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong clonePtr)
{
    return ((git_clone_options *)clonePtr)->version;
}

// setter
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniSetVersion)(JNIEnv *env, jclass obj, jlong clonePtr, jint version)
{
    ((git_clone_options *)clonePtr)->version = version;
}

/** -------- Wrapper Body ---------- */
/** unsigned int version*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->version;
}

/** git_checkout_options checkout_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->checkout_opts;
}

/** git_fetch_options fetch_opts*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->fetch_opts;
}

/** int bare*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetBare)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->bare;
}

/** git_clone_local_t local*/
JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->local;
}

/** const char* checkout_branch*/
JNIEXPORT jstring JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->checkout_branch;
}

/** git_repository_create_cb repository_cb*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->repository_cb;
}

/** void *repository_cb_payload*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRepositoryCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->repository_cb_payload;
}

/** git_remote_create_cb remote_cb*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->remote_cb;
}

/** void *remote_cb_payload*/
JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRemoteCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr)
{
    return ((git_clone_options *)optionsPtr)->remote_cb_payload;
}

/** unsigned int version*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version)
{
    ((git_clone_options *)optionsPtr)->version = (unsigned int)version;
}

/** git_checkout_options checkout_opts*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong checkoutOpts)
{
    ((git_clone_options *)optionsPtr)->checkout_opts = (git_checkout_options)checkoutOpts;
}

/** git_fetch_options fetch_opts*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong fetchOpts)
{
    ((git_clone_options *)optionsPtr)->fetch_opts = (git_fetch_options)fetchOpts;
}

/** int bare*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetBare)(JNIEnv *env, jclass obj, jlong optionsPtr, jint bare)
{
    ((git_clone_options *)optionsPtr)->bare = (int)bare;
}

/** git_clone_local_t local*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr, jint local)
{
    ((git_clone_options *)optionsPtr)->local = (git_clone_local_t)local;
}

/** const char* checkout_branch*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring checkoutBranch)
{
    ((git_clone_options *)optionsPtr)->checkout_branch = (const char *)checkoutBranch;
}

/** git_repository_create_cb repository_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong repositoryCb)
{
    /**FIXME: callback and payload needs human review*/
    ((git_clone_options *)optionsPtr)->repository_cb = (git_repository_create_cb)repositoryCb;
}

/** void *repository_cb_payload*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong repositoryCbPayload)
{
    /**FIXME: callback and payload needs human review*/
    ((git_clone_options *)optionsPtr)->repository_cb_payload = (void *)repositoryCbPayload;
}

/** git_remote_create_cb remote_cb*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong remoteCb)
{
    /**FIXME: callback and payload needs human review*/
    ((git_clone_options *)optionsPtr)->remote_cb = (git_remote_create_cb)remoteCb;
}

/** void *remote_cb_payload*/
JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong remoteCbPayload)
{
    /**FIXME: callback and payload needs human review*/
    ((git_clone_options *)optionsPtr)->remote_cb_payload = (void *)remoteCbPayload;
}
