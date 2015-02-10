#include <stdio.h>
#include <stdlib.h>
#include "sep.h"

int main(){
  double darray[6][6];
  	for(int i=0; i<6; i++)
      for(int j=0; j<6; j++)
        darray[i][j] = 0.0;

  sepbackmap *p = (sepbackmap *)malloc(sizeof(sepbackmap));
  p->back = malloc(sizeof(float)*4);
  p->dback = malloc(sizeof(float)*4);
  p->sigma = malloc(sizeof(float)*4);  
  p->dsigma = malloc(sizeof(float)*4);

  p->h = 6;
  p->w = 6;
  p->bh = 3;
  p->bw = 3;
  p->nx = 2;
  p->ny = 2;
  p->n = 4;
  p->globalback = 0.08958334;
  p->globalrms = 0.3142697;

  float *pp = p->back;
  for(int i=0; i<p->n; i++){
  	*(pp++) = 0.08958334;
  }

  pp = p->dback;
  for(int i=0; i<p->n; i++){
  	*(pp++) = 0.0;
  }

  pp = p->sigma;
  for(int i=0; i<p->n; i++){
  	*(pp++) = 0.3142697;
  }

  pp = p->dsigma;
  for(int i=0; i<p->n; i++){
  	*(pp++) = 0.0;
  }

  int status = sep_backarray(p, darray, 82);
  printf("%f, \n", darray[0][0]);
}