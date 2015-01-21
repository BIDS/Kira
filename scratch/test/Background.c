#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "Background.h"
#include "sep.h"

void flatten(double **matrix, int w, int h, char * ret){
	//char *ret = (char *)malloc(sizeof(char)*w*h*8);
	unsigned char array[6*6*8];
	for(int i=0; i<h; i++){
		for(int j=0; j<w; j++){
			unsigned char* ptr = (unsigned char *)&matrix[i][j];
			memcpy(array+(i*w+j)*8, ptr, 8);
		}
	}
    memcpy(ret, array, 6*6*8);
}

JNIEXPORT jint JNICALL Java_Background_sep_1makeback
(JNIEnv *env, jobject obj, jobjectArray data, jobject mask, jint dtype, jint mdtype, jint w, jint h, jint
 bw, jint bh, jdouble mthresh, jint fw, jint fh, jdouble fthresh, jobject backmap){
  

  int len1 = (*env)->GetArrayLength(env, data);
  jfloatArray dim=  (jfloatArray)(*env)->GetObjectArrayElement(env, data, 0);
  int len2 = (*env)->GetArrayLength(env, dim);
  jdouble **localArray;
  // allocate localArray using len1
  localArray = malloc(sizeof(double *)*len1);
  for(int i=0; i<len1; i++){
     jdoubleArray oneDim= (jfloatArray)(*env)->GetObjectArrayElement(env, data, i);
     jdouble *element=(*env)->GetDoubleArrayElements(env, oneDim, 0);
     //allocate localArray[i] using len2
     localArray[i] = malloc(sizeof(double)*len2);
     for(int j=0; j<len2; j++) {
        localArray[i][j]= element[j];
     }
  }

  for(int i=0; i<len1; i++){
  	for(int j=0; j<len2; j++){
  		printf("%f, ", localArray[i][j]);
  	}
  	printf("\n");
  }

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

  //printf("input, mthresh: %f\t fthresh: %f\n", mthresh, fthresh);

  char array[6*6*8];
  flatten(localArray, w, h, array);

  int status = sep_makeback(&array[0], (void *)NULL, dtype, 0, w, h, bw, bh, mthresh, fw, fh, fthresh, &p);
  //printf("%d\n", status);
  printf("C: %d\t%d\t%f\t%f\n", p->w, p->h, p->globalback, p->globalrms);

  (*env)->SetFloatField(env, obj, id_back, p->globalback);
  (*env)->SetFloatField(env, obj, id_rms, p->globalrms);

  for(int i=0; i<len1; i++){
     free(localArray[i]);
  }
  free(localArray);
  free(p);
  return status;
}