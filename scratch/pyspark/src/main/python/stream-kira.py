import numpy as np
import sep
import sys
import StringIO

from astropy.io import fits
from pyspark import SparkContext
from pyspark.streaming import StreamingContext

def extract(data):
  bkg = sep.Background(data, bw=64, bh=64, fw=3, fh=3)
  bkg.subfrom(data)
  objs = sep.extract(data, 1.5*bkg.globalrms)
  flux, fluxerr, flag = sep.sum_circle(data, objs['x'], objs['y'], 5.,
                                         err=bkg.globalrms)
  kr, flag = sep.kron_radius(data, objs['x'], objs['y'], objs['a'],
                                    objs['b'], objs['theta'], 6.0)
  eflux, efluxerr, eflag = sep.sum_ellipse(data, objs['x'], objs['y'],
                                          objs['a'], objs['b'],
                                          objs['theta'], r=2.5 * kr,
                                          err=bkg.globalrms, subpix=1)
  retstr = ""
  for i in range(len(objs['x'])):
    retstr = retstr+(str(objs['x'][i])+"\t"+str(objs['y'][i])+"\t"+str(flux[i])+"\t"+str(fluxerr[i])+"\t"+str(kr[i])+"\t"+str(eflux[i])+"\t"+str(efluxerr[i])+"\t"+str(flag[i])+"\n")
  return retstr  

def get_output(srdd, outPath):
  hrdd = srdd.map(lambda x: fits.getdata(x))
  catalog = hrdd.map(lambda x: extract(x.astype(float)))
  catalog.saveAsTextFile(outPath)

if __name__ == "__main__":
  sc = SparkContext(appName="StreamingSourceExtractor")
  inPath = sys.argv[1]
  outPath = sys.argv[2]
  ssc = StreamingContext(sc, 300)

  #frdd = ssc.binaryFileStream(inPath)
  #srdd = frdd.map(lambda x: StringIO.StringIO(x))
  #hrdd = srdd.map(lambda x: fits.getdata(x))

  #catalog = hrdd.map(lambda x: extract(x.astype(float)))
  #catalog.saveAsTextFiles(outPath)

  frdd = sc.binaryFiles(inPath+"/1")
  srdd = frdd.map(lambda x: StringIO.StringIO(x[1]))

  srddQueue = [srdd]
  in_stream = ssc.queueStream(srddQueue)

  in_stream.foreachRDD(lambda x: get_output(x, outPath))

  ssc.start()
  ssc.awaitTermination()
