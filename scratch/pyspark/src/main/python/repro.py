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

import numpy as np
import sep
import sys
from astropy.io import fits
from astropy.wcs import WCS
import reproject
from reproject import reproject_interp

from pyspark import SparkContext

if __name__ == "__main__":
	sc = SparkContext(appName="Reproject")
	rdd = sc.fitsFiles("/Users/zhaozhang/projects/SDSS/data")
	header = rdd.take(1).pop().pop().header
	rprordd = rdd.map(lambda x: reproject_interp(x.pop(), header))
	nrdd = rprordd.zipWithIndex()
	nrdd.foreach(lambda ((array, footprint), index): fits.writeto(str(index)+".fits", array, header))	

	sc.stop()
