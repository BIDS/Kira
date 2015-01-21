#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "Background.h"
#include "sep.h"


JNIEXPORT jint JNICALL Java_Background_sep_1makeback
(JNIEnv *env, jobject obj, jbyteArray data, jobject mask, jint dtype, jint mdtype, jint w, jint h, jint
 bw, jint bh, jdouble mthresh, jint fw, jint fh, jdouble fthresh, jobject backmap){
  
  int len = (*env)->GetArrayLength(env, data);
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  /*printf("%d\n", len);
  for(int i=0; i<len; i++)
    printf("%d, ", (unsigned char)array[i]);
  printf("\n");*/

  jclass cls = (*env)->GetObjectClass(env, obj);
  assert(cls != NULL);
  jfieldID id_back = (*env)->GetFieldID(env, cls, "globalback", "F"); 
  assert(id_back != NULL);
  jfieldID id_rms = (*env)->GetFieldID(env, cls, "globalrms", "F"); 
  assert(id_rms != NULL);

  sepbackmap *p = (sepbackmap *)malloc(sizeof(sepbackmap));
  p->w = w;
  p->h = h;
  p->bw = bw;
  p->bh = bh;

  int status = sep_makeback(array, (void *)NULL, dtype, 0, w, h, bw, bh, mthresh, fw, fh, fthresh, &p);
  //printf("%d\n", status);
  printf("C: %d\t%d\t%f\t%f\n", p->w, p->h, p->globalback, p->globalrms);

  (*env)->SetFloatField(env, obj, id_back, p->globalback);
  (*env)->SetFloatField(env, obj, id_rms, p->globalrms);

  free(array);
  free(p);
  return status;
}