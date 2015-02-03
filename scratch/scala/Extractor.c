#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "Extractor.h"
#include "sep.h"

JNIEXPORT jint JNICALL Java_Extractor_sep_1sum_1circle
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdouble r, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);


  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  int len = (*env)->GetArrayLength(env, x);

  int status = 0;
  for(int i=0; i<len; i++){
    double dsum = 0.0;
    double dsumerr = 0.0;
    double darea = 0.0;
    short dflag = 0;
    
    status = sep_sum_circle(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], r, subpix, &dsum, &dsumerr, &darea, &dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}

JNIEXPORT jint JNICALL Java_Extractor_sep_1sum_1circann
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdouble rin, jdouble rout, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);


  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  int len = (*env)->GetArrayLength(env, x);

  int status = 0;
  for(int i=0; i<len; i++){
    double dsum = 0.0;
    double dsumerr = 0.0;
    double darea = 0.0;
    short dflag = 0;
    
    status = sep_sum_circann(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], rin, rout, subpix, &dsum, &dsumerr, &darea, &dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}

JNIEXPORT jint JNICALL Java_Extractor_sep_1sum_1ellipse
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdoubleArray a, jdoubleArray b, jdoubleArray theta, jdouble r, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  jdouble *a_array = (jdouble *)(*env)->GetDoubleArrayElements(env, a, 0);
  jdouble *b_array = (jdouble *)(*env)->GetDoubleArrayElements(env, b, 0);
  jdouble *theta_array = (jdouble *)(*env)->GetDoubleArrayElements(env, theta, 0);
  int len = (*env)->GetArrayLength(env, x);
  int status = 0;

  for(int i=0; i<len; i++){
    double dsum = 1.0;
    double dsumerr = 1.0;
    double darea = 1.0;
    short dflag = 1;

    status = sep_sum_ellipse(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], a_array[i], b_array[i], theta_array[i], r, subpix, &dsum, &dsumerr, &darea, &dflag);
    //printf("C sep_sum_ellipse: dsum: %f\t dsumerr: %f\t darea: %f\t dflag: %d\n", dsum, dsumerr, darea, dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}

JNIEXPORT jint JNICALL Java_Extractor_sep_1sum_1ellipann
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdoubleArray a, jdoubleArray b, jdoubleArray theta, jdouble rin, jdouble rout, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  jdouble *a_array = (jdouble *)(*env)->GetDoubleArrayElements(env, a, 0);
  jdouble *barray = (jdouble *)(*env)->GetDoubleArrayElements(env, b, 0);
  jdouble *thetaarray = (jdouble *)(*env)->GetDoubleArrayElements(env, theta, 0);
  int len = (*env)->GetArrayLength(env, x);
  int status = 0;

  for(int i=0; i<len; i++){
    double dsum = 1.0;
    double dsumerr = 1.0;
    double darea = 1.0;
    short dflag = 1;

    status = sep_sum_ellipann(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], a_array[i], barray[i], thetaarray[i], rin, rout, subpix, &dsum, &dsumerr, &darea, &dflag);
    //printf("C sep_sum_ellipse: dsum: %f\t dsumerr: %f\t darea: %f\t dflag: %d\n", dsum, dsumerr, darea, dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}

JNIEXPORT jint JNICALL Java_Extractor_sep_1extract
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray nstream, jint dtype, jint ndtype, jshort noise_flag, jint w, jint h, jfloat thresh, jint minarea, jbyteArray cstream, jint convw, jint convh, jint deblend_nthresh, jdouble deblend_cont, jboolean clean_flag, jdouble clean_param, jobjectArray objects, jint nobj)
{
  jbyte *marray = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);  
 
  
  jfloat *conv = NULL;
  printf("C sep_extract: convw: %d, convh: %d\n", convw, convh);
  if(convw !=0 && convh != 0)
  { 
    jbyte *carray = (jbyte *)(*env)->GetByteArrayElements(env, cstream, NULL);  
    conv = (jfloat *)malloc(sizeof(float)*convw*convh);
    double *dp = (double *)carray;
    for(int i=0; i<convw*convh; i++){
      conv[i] = *(dp++);
      //printf("conv[%d]: %f\n", i, conv[i]);
    }
  }

  jbyte *narray = NULL;
  if(nstream != NULL){
    printf("C sep_extract: nstream is null\n");
    jbyte *narray = (jbyte *)(*env)->GetByteArrayElements(env, nstream, NULL);
  }

  sepobj *objs;

  int status = sep_extract(marray, narray, dtype, ndtype, noise_flag, w, h, thresh, minarea, conv, convw, convh, deblend_nthresh, deblend_cont, clean_flag, clean_param, &objs, &nobj);
  printf("C sep_extract: status: %d\n", status);

  /*for(int i=0; i<nobj; i++)
    printf("Number: %d\t X_IMAGE: %f\t Y_IMAGE: %f\n", i, objs[i].x, objs[i].y);*/

  jclass cls = (*env)->GetObjectClass(env, obj);
  assert(cls != NULL);
  jmethodID consID = (*env)->GetMethodID(env, cls, "init_obj", "(DIIIIIIDDDDDFFFFFFFFFFIIIISI)LSepobj;");
  assert(consID != NULL);

  for(int i=0; i<nobj; i++){
    jobject sepobj = (*env)->CallObjectMethod(env, obj, consID, objs[i].thresh, objs[i].npix, objs[i].tnpix, objs[i].xmin, objs[i].xmax, objs[i].ymin, objs[i].ymax, objs[i].x, objs[i].y, objs[i].x2, objs[i].y2, objs[i].xy, objs[i].a, objs[i].b, objs[i].theta, objs[i].cxx, objs[i].cyy, objs[i].cxy, objs[i].cflux, objs[i].flux, objs[i].cpeak, objs[i].peak, objs[i].xcpeak, objs[i].ycpeak, objs[i].xpeak, objs[i].ypeak, objs[i].flag);
    (*env)->SetObjectArrayElement(env, objects, i, sepobj);
  }
  return nobj;
}