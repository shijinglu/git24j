#include "j_common.h"
#include <git2.h>
#include <jni.h>

#ifndef __GIT24J_CLONE_H__
#define __GIT24J_CLONE_H__
#ifdef __cplusplus
extern "C"
{
#endif

    /** -------- Signature of the header ---------- */
    /** int git_clone_clone(git_repository **out, const char *url, const char *local_path, const git_clone_options *options); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniClone)(JNIEnv *env, jclass obj, jobject out, jstring url, jstring local_path, jlong optionsPtr);

    /** int git_clone_init_options(git_clone_options *opts, unsigned int version); */
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniInitOptions)(JNIEnv *env, jclass obj, jlong optsPtr, jint version);

    // getter
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong clonePtr);
    // setter
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniSetVersion)(JNIEnv *env, jclass obj, jlong clonePtr, jint version);

    /** -------- Signature of the header ---------- */
    /** unsigned int version*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_checkout_options checkout_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_fetch_options fetch_opts*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** int bare*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetBare)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_clone_local_t local*/
    JNIEXPORT jint JNICALL J_MAKE_METHOD(Clone_jniOptionsGetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** const char* checkout_branch*/
    JNIEXPORT jstring JNICALL J_MAKE_METHOD(Clone_jniOptionsGetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_repository_create_cb repository_cb*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** void *repository_cb_payload*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRepositoryCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** git_remote_create_cb remote_cb*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** void *remote_cb_payload*/
    JNIEXPORT jlong JNICALL J_MAKE_METHOD(Clone_jniOptionsGetRemoteCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr);
    /** unsigned int version*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetVersion)(JNIEnv *env, jclass obj, jlong optionsPtr, jint version);
    /** git_checkout_options checkout_opts*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong checkoutOpts);
    /** git_fetch_options fetch_opts*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetFetchOpts)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong fetchOpts);
    /** int bare*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetBare)(JNIEnv *env, jclass obj, jlong optionsPtr, jint bare);
    /** git_clone_local_t local*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetLocal)(JNIEnv *env, jclass obj, jlong optionsPtr, jint local);
    /** const char* checkout_branch*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetCheckoutBranch)(JNIEnv *env, jclass obj, jlong optionsPtr, jstring checkoutBranch);
    /** git_repository_create_cb repository_cb*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong repositoryCb);
    /** void *repository_cb_payload*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRepositoryCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong repositoryCbPayload);
    /** git_remote_create_cb remote_cb*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCb)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong remoteCb);
    /** void *remote_cb_payload*/
    JNIEXPORT void JNICALL J_MAKE_METHOD(Clone_jniOptionsSetRemoteCbPayload)(JNIEnv *env, jclass obj, jlong optionsPtr, jlong remoteCbPayload);

#ifdef __cplusplus
}
#endif
#endif