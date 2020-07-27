//
// Created by yanghui.yangh on 2016-07-11.
//

#ifndef ANDROID_DIFFPATCH_NATIVE_PATCH_H
#define ANDROID_DIFFPATCH_NATIVE_PATCH_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_cn_linked_commonlib_jni_diffpatch_bsdiffpatch_BSDiffPatch_bspatch(JNIEnv *env, jobject thiz, jstring oldFilePath,
                                 jstring diffFilePath, jstring newFilePath);


#ifdef __cplusplus
}
#endif
#endif //ANDROID_DIFFPATCH_NATIVE_PATCH_H