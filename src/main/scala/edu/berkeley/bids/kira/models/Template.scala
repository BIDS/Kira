/*
 * Copyright (c) 2014. Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.berkeley.bids.kira.models

import scala.io.Source

/**
 * Class describing the templates for merging images.
 *
 * @param path The location to populate this from.
 */
class Template(private val path: String) {
  var kmap = Source.fromFile(path)
    .getLines()
    .flatMap(line => {
      var words = line.split('=').map(_.trim)

      if (words.length > 1) {
        Some((words(0), words(1)))
      } else {
        None
      }
    }).toMap

  def tcol = kmap("NAXIS1").toInt
  def trow = kmap("NAXIS2").toInt
  def crpix: Array[Double] = Array(kmap("CRPIX1").toFloat, kmap("CRPIX2").toFloat)
  def crval: Array[Double] = Array(kmap("CRVAL1").toFloat, kmap("CRVAL2").toFloat)
  def cdelt: Array[Double] = Array(kmap("CDELT1").toFloat, kmap("CDELT2").toFloat)
  def ctype: Array[String] = Array(kmap("CTYPE1"), kmap("CTYPE2"))
}
