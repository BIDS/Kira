#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "Background.h"
#include "sep.h"

void sep_back_c_java(JNIEnv *env, jclass cls, jobject obj, jclass bkmapcls, jobject bkmap, sepbackmap *p){
  /*set the globalback and globalrms field as return value*/
  jfieldID id_gback = (*env)->GetFieldID(env, bkmapcls, "globalback", "F"); 
  assert(id_gback != NULL);
  (*env)->SetFloatField(env, bkmap, id_gback, p->globalback);

  jfieldID id_rms = (*env)->GetFieldID(env, bkmapcls, "globalrms", "F"); 
  assert(id_rms != NULL);
  (*env)->SetFloatField(env, bkmap, id_rms, p->globalrms);

  /*set the w and h field as return value*/
  jfieldID id_w = (*env)->GetFieldID(env, bkmapcls, "w", "I"); 
  (*env)->SetIntField(env, bkmap, id_w, p->w);
  jfieldID id_h = (*env)->GetFieldID(env, bkmapcls, "h", "I"); 
  (*env)->SetIntField(env, bkmap, id_h, p->h);

  /*set the bw and bh field as return value*/
  jfieldID id_bw = (*env)->GetFieldID(env, bkmapcls, "bw", "I"); 
  (*env)->SetIntField(env, bkmap, id_bw, p->bw);
  jfieldID id_bh = (*env)->GetFieldID(env, bkmapcls, "bh", "I"); 
  (*env)->SetIntField(env, bkmap, id_bh, p->bh);

  /*set the nx, ny, and n field as return value*/
  jfieldID id_nx = (*env)->GetFieldID(env, bkmapcls, "nx", "I"); 
  (*env)->SetIntField(env, bkmap, id_nx, p->nx);
  jfieldID id_ny = (*env)->GetFieldID(env, bkmapcls, "ny", "I"); 
  (*env)->SetIntField(env, bkmap, id_ny, p->ny);
  jfieldID id_n = (*env)->GetFieldID(env, bkmapcls, "n", "I"); 
  (*env)->SetIntField(env, bkmap, id_n, p->n);

  jmethodID setBackID = (*env)->GetMethodID(env, cls, "setBack", "([F)V");
  jfloatArray backArray = (*env)->NewFloatArray(env, p->n);
  (*env)->SetFloatArrayRegion(env, backArray, 0, p->n, p->back);
  (*env)->CallVoidMethod(env, obj, setBackID, backArray);

  jmethodID setDbackID = (*env)->GetMethodID(env, cls, "setDback", "([F)V");
  jfloatArray dbackArray = (*env)->NewFloatArray(env, p->n);
  (*env)->SetFloatArrayRegion(env, dbackArray, 0, p->n, p->dback);
  (*env)->CallVoidMethod(env, obj, setDbackID, dbackArray);

  jmethodID setSigmaID = (*env)->GetMethodID(env, cls, "setSigma", "([F)V");
  jfloatArray sigmaArray = (*env)->NewFloatArray(env, p->n);
  (*env)->SetFloatArrayRegion(env, sigmaArray, 0, p->n, p->sigma);
  (*env)->CallVoidMethod(env, obj, setSigmaID, sigmaArray);

  jmethodID setDsigmaID = (*env)->GetMethodID(env, cls, "setDsigma", "([F)V");
  jfloatArray dsigmaArray = (*env)->NewFloatArray(env, p->n);
  (*env)->SetFloatArrayRegion(env, dsigmaArray, 0, p->n, p->dsigma);
  (*env)->CallVoidMethod(env, obj, setDsigmaID, dsigmaArray);
}

void sep_back_java_c(JNIEnv *env, jclass bkmapcls, jobject bkmap, sepbackmap *p, jfloatArray back, jfloatArray dback, jfloatArray sigma, jfloatArray dsigma){
  jfieldID id_h = (*env)->GetFieldID(env, bkmapcls, "h", "I"); 
  jint h = (*env)->GetIntField(env, bkmap, id_h);
  jfieldID id_w = (*env)->GetFieldID(env, bkmapcls, "w", "I"); 
  jint w = (*env)->GetIntField(env, bkmap, id_w);

  jfieldID id_bh = (*env)->GetFieldID(env, bkmapcls, "bh", "I"); 
  jint bh = (*env)->GetIntField(env, bkmap, id_bh);
  jfieldID id_bw = (*env)->GetFieldID(env, bkmapcls, "bw", "I"); 
  jint bw = (*env)->GetIntField(env, bkmap, id_bw);

  jfieldID id_nx = (*env)->GetFieldID(env, bkmapcls, "nx", "I"); 
  jint nx = (*env)->GetIntField(env, bkmap, id_nx);
  jfieldID id_ny = (*env)->GetFieldID(env, bkmapcls, "ny", "I"); 
  jint ny = (*env)->GetIntField(env, bkmap, id_ny);
  jfieldID id_n = (*env)->GetFieldID(env, bkmapcls, "n", "I"); 
  jint n = (*env)->GetIntField(env, bkmap, id_n);
  
  jfieldID id_gback = (*env)->GetFieldID(env, bkmapcls, "globalback", "F"); 
  jfloat gback = (*env)->GetFloatField(env, bkmap, id_gback);
  jfieldID id_rms = (*env)->GetFieldID(env, bkmapcls, "globalrms", "F"); 
  jfloat rms = (*env)->GetFloatField(env, bkmap, id_rms);

  /*need to implement new array access functions here*/
  jfloat *backarray = (jfloat *)(*env)->GetFloatArrayElements(env, back, NULL);
  jfloat *dbackarray = (jfloat *)(*env)->GetFloatArrayElements(env, dback, NULL);
  jfloat *sigmaarray = (jfloat *)(*env)->GetFloatArrayElements(env, sigma, NULL);
  jfloat *dsigmaarray = (jfloat *)(*env)->GetFloatArrayElements(env, dsigma, NULL);

  p->h = h;
  p->w = w;
  p->bh = bh;
  p->bw = bw;
  p->nx = nx;
  p->ny = ny;
  p->n = n;
  p->globalback = gback;
  p->globalrms = rms;

  jfloat *bp = p->back;
  jfloat *dbp = p->dback;
  jfloat *sp = p->sigma;
  jfloat *dsp = p->dsigma;

  memcpy(bp, backarray, sizeof(float)*(p->n));
  memcpy(dbp, dbackarray, sizeof(float)*(p->n));
  memcpy(sp, sigmaarray, sizeof(float)*(p->n));
  memcpy(dsp, dsigmaarray, sizeof(float)*(p->n));
}

JNIEXPORT jint JNICALL Java_Background_sep_1makeback
(JNIEnv *env, jobject obj, jbyteArray data, jobjectArray maskdata, jint dtype, jint mdtype, jint w, jint h, jint
 bw, jint bh, jdouble mthresh, jint fw, jint fh, jdouble fthresh, jobject backmap){
  
  int len = (*env)->GetArrayLength(env, data);
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  int mlen1 = (*env)->GetArrayLength(env, maskdata);
  printf("mlen1: %d\n", mlen1);
  jboolean *mask;
  if(mlen1 == 0){
    mask = (void *)NULL;
  }
  else{
    /*parse the matrix*/
    jbooleanArray dim = (jbooleanArray)(*env)->GetObjectArrayElement(env, maskdata, 0);
    int mlen2 = (*env)->GetArrayLength(env, dim);
    printf("mlen2: %d\n", mlen2);
    mask = malloc(sizeof(jboolean)* mlen1 * mlen2);
    for(int i=0; i<mlen1; i++){
      jbooleanArray oneDim= (jbooleanArray)(*env)->GetObjectArrayElement(env, maskdata, i);
      jboolean *element=(*env)->GetBooleanArrayElements(env, oneDim, 0);
      //mask[i] = malloc(sizeof(jboolean)*mlen2);
      memcpy(mask+i*mlen2, element, sizeof(jboolean)*mlen2);
   }
  }


  /*printf("%d\n", len);
  for(int i=0; i<len; i++)
    printf("%d, ", (unsigned char)mask[i]);
  printf("\n");*/

  sepbackmap *p = (sepbackmap *)malloc(sizeof(sepbackmap));
  p->w = w;
  p->h = h;
  p->bw = bw;
  p->bh = bh;

  int status = sep_makeback(array, mask, dtype, mdtype, w, h, bw, bh, mthresh, fw, fh, fthresh, &p);

  //printf("%d\n", status);
  printf("C sep_makeback: %d\t%d\t%f\t%f\t%d\t%d\t%d\t%d\n", p->w, p->h, p->globalback, p->globalrms, p->bw, p->bh, p->nx, p->ny);
  printf("C sep_makeback back: %f\t dback: %f\n", *(p->back), *(p->dback));

  /*Finding the sepbackmap field in the Background class*/
  jclass cls = (*env)->GetObjectClass(env, obj);
  assert(cls != NULL);
  jfieldID id_bkmap = (*env)->GetFieldID(env, cls, "backmap", "LBackground$Sepbackmap;"); 
  assert(id_bkmap != NULL);
  jobject bkmap = (*env)->GetObjectField(env, obj, id_bkmap);
  assert(bkmap != NULL);
  jclass bkmapcls = (*env)->GetObjectClass(env, bkmap);
  assert(bkmapcls != NULL);

  sep_back_c_java(env, cls, obj, bkmapcls, bkmap, p);

  float *fp = p->back;
  for(int i=0; i<p->n; i++){
    printf("back: %f\t", *(fp++));
  }
  printf("\n");

  fp = p->dback;
  for(int i=0; i<p->n; i++){
    printf("dback: %f\t", *(fp++));
  }
  printf("\n");

  fp = p->sigma;
  for(int i=0; i<p->n; i++){
    printf("sigma: %f\t", *(fp++));
  }
  printf("\n");

  fp = p->dsigma;
  for(int i=0; i<p->n; i++){
    printf("dsigma: %f\t", *(fp++));
  }
  printf("\n");
  free(array);
  free(p);

  return status;
}

JNIEXPORT jint JNICALL Java_Background_sep_1backarray
  (JNIEnv *env, jobject obj, jobject bkmap, jbyteArray data, jint dtype, jfloatArray back, jfloatArray dback, jfloatArray sigma, jfloatArray dsigma){

  int len = (*env)->GetArrayLength(env, data);
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  jclass cls = (*env)->GetObjectClass(env, bkmap);
  assert(cls != NULL);

  int backLen = (*env)->GetArrayLength(env, back);
  sepbackmap *p = (sepbackmap *)malloc(sizeof(sepbackmap));
  p->back = malloc(sizeof(float)*backLen);
  p->dback = malloc(sizeof(float)*backLen);
  p->sigma = malloc(sizeof(float)*backLen);  
  p->dsigma = malloc(sizeof(float)*backLen);

  sep_back_java_c(env, cls, bkmap, p, back, dback, sigma, dsigma);

  printf("C sep_backarray: %d\t%d\t%f\t%f\t%d\t%d\n", p->w, p->h, p->globalback, p->globalrms, p->bw, p->bh);
  printf("C sep_backarray: nx: %d \t ny: %d \t n: %d\n", p->nx, p->ny, p->n);
  printf("C sep_backarray: back: %f \t dback: %f\n", *(p->back), *(p->dback));
  printf("C sep_backarray: sigma: %f \t dsigma: %f\n", *(p->sigma), *(p->dsigma));

  
  int status = sep_backarray(p, array, dtype);
  printf("status: %d\n", status);
  (*env)->SetByteArrayRegion(env, data, 0, len, array);

  printf("%d, \n", array[0]);

  /*for(int i=0; i<len/8; i++){
    double d;
    memcpy(&d, (jbyte *)(array+i*8), sizeof(double));
    printf("%f, ", d);
  }*/

  free(array);
  free(p);
  return status;
}

JNIEXPORT jint JNICALL Java_Background_sep_1sum_1circle
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

JNIEXPORT jint JNICALL Java_Background_sep_1sum_1ellipse
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdouble a, jdouble b, jdouble theta, jdouble r, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  int len = (*env)->GetArrayLength(env, x);
  int status = 0;

  for(int i=0; i<len; i++){
    double dsum = 1.0;
    double dsumerr = 1.0;
    double darea = 1.0;
    short dflag = 1;

    status = sep_sum_ellipse(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], a, b, theta, r, subpix, &dsum, &dsumerr, &darea, &dflag);
    //printf("C sep_sum_ellipse: dsum: %f\t dsumerr: %f\t darea: %f\t dflag: %d\n", dsum, dsumerr, darea, dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}

JNIEXPORT jint JNICALL Java_Background_sep_1sum_1circann
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

JNIEXPORT jint JNICALL Java_Background_sep_1sum_1ellipann
  (JNIEnv *env, jobject obj, jbyteArray data, jbyteArray error, jbyteArray mask, jint dtype, jint edtype, jint mdtype, jint w, jint h, jdouble maskthresh, jdouble gain, jshort inflag, jdoubleArray x, jdoubleArray y, jdouble a, jdoubleArray b, jdoubleArray theta, jdouble rin, jdouble rout, jint subpix, jdoubleArray sum, jdoubleArray sumerr, jdoubleArray area, jshortArray flag)
{
  jbyte *array = (jbyte *)(*env)->GetByteArrayElements(env, data, NULL);

  jdouble *xarray = (jdouble *)(*env)->GetDoubleArrayElements(env, x, 0);
  jdouble *yarray = (jdouble *)(*env)->GetDoubleArrayElements(env, y, 0);
  jdouble *barray = (jdouble *)(*env)->GetDoubleArrayElements(env, b, 0);
  jdouble *thetaarray = (jdouble *)(*env)->GetDoubleArrayElements(env, theta, 0);
  int len = (*env)->GetArrayLength(env, x);
  int status = 0;

  for(int i=0; i<len; i++){
    double dsum = 1.0;
    double dsumerr = 1.0;
    double darea = 1.0;
    short dflag = 1;

    status = sep_sum_ellipann(array, NULL, NULL, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, xarray[i], yarray[i], a, barray[i], thetaarray[i], rin, rout, subpix, &dsum, &dsumerr, &darea, &dflag);
    //printf("C sep_sum_ellipse: dsum: %f\t dsumerr: %f\t darea: %f\t dflag: %d\n", dsum, dsumerr, darea, dflag);

    (*env)->SetDoubleArrayRegion(env, sum, i, 1, &dsum);
    (*env)->SetDoubleArrayRegion(env, sumerr, i, 1, &dsumerr);
    (*env)->SetDoubleArrayRegion(env, area, i, 1, &darea);
    (*env)->SetShortArrayRegion(env, flag, i, 1, &dflag);
  }
  return status;
}