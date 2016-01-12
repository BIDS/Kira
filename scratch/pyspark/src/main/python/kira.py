#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

from astropy import fits
import numpy as np
import sep
import sys
import StringIO

from pyspark import SparkContext

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

if __name__ == "__main__":
	sc = SparkContext(appName="SourceExtractor")
	frdd = sc.binaryFiles("/Users/zhaozhang/projects/SDSS/data")
	#rdd = sc.fitsData("/Users/zhaozhang/projects/Kira/scratch/spark-ec2/data/")
  srdd = frdd.map(lambda x: StringIO.StringIO(x[1]))
  hrdd = srdd.map(lmabda x: fits.getdata(x))

	catalog = hrdd.map(lambda x: (key, extract(np.copy(x))))
	catalog.saveAsTextFile("temp-output")

	sc.stop()
