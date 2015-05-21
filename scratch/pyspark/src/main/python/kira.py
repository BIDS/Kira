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
from operator import add

from pyspark import SparkContext


if __name__ == "__main__":
	sc = SparkContext(appName="SourceExtractor")
	rdd = sc.fitsData("/Users/zhaozhang/projects/Montage/m101/rawdir")
	data = rdd.map(lambda x:np.copy(x).byteswap(True).newbyteorder())
	bkg = data.map(lambda x:sep.Background(x, bw=64, bh=64, fw=3, fh=3))
	bkgarr = bkg.map(lambda x:x.back(dtype=np.float32))
	bkgarr.saveAsTextFile("temp-output/")
	sc.stop()
