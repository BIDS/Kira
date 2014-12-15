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
package edu.berkeley.bids.kira.util

import edu.berkeley.bids.kira.models._
import org.eso.fits.{
  Fits => ESOFits,
  FitsFile,
  FitsHDUnit,
  FitsHeader,
  FitsKeyword,
  FitsMatrix,
  FitsWCS
}

object FitsUtils {

  /**
   * Extracts FITS data from a file.
   *
   * @param path Path to the file.
   * @return Returns a FITS dataset.
   */
  def readFits(path: String): Fits = {
    // open file and get header
    val file = new FitsFile(path)
    val hdu: FitsHDUnit = file.getHDUnit(0)
    val hdr = hdu.getHeader()

    // extract matrix data
    val dm: FitsMatrix = hdu.getData().asInstanceOf[FitsMatrix]
    val naxis = dm.getNaxis()
    val crpix = dm.getCrpix()
    val ncol = naxis(0)
    val nval = dm.getNoValues()
    val nrow = nval / ncol

    // build and populate an array
    var matrix = Array.ofDim[Float](nrow, ncol)
    (0 until nrow).map(i => dm.getFloatValues(i * ncol, ncol, matrix(i)))

    Fits(path, naxis, crpix, ncol, matrix)
  }

  /**
   * Processes the metadata for all the FITS files.
   *
   * @param fitsList An array of FITS images.
   * @return
   */
  def processMeta(fitsList: Array[Fits]): Map[Int, FitsMetadata] = {
    // get the max of the second element in the crpix arrays
    val crpix0max = fitsList.map(f => f.crpix(0)).max

    // get the max of the second element in the crpix arrays
    val crpix1max = fitsList.map(f => f.crpix(1)).max

    fitsList.zipWithIndex.map(vk => {
      val (fit, idx) = vk

      // calculate start, end, and offset
      val start = crpix1max - fit.crpix(1) + 1
      val end = start + fit.naxis(1) - 1
      val offset = crpix0max - fit.crpix(0)

      (idx, FitsMetadata(start.toInt, end.toInt, offset.toInt))
    }).toMap
  }

  /**
   * Saves the output as a Fits file.
   *
   * @param template Template for saving.
   * @param matrix 2D matrix of floats.
   * @param path Path to save file at.
   */
  def createFits(template: Template, matrix: Array[Array[Float]], path: String) {
    // extract the total number of columns and rows
    val tcol = template.tcol
    val trow = template.trow

    // populate the axis array
    val naxis = Array(tcol, trow)

    // flat map the matrix down into a single long array
    val data = matrix.flatMap(a => a)

    // allocate header unit and matrix
    val mtx: FitsMatrix = new FitsMatrix(ESOFits.DOUBLE, naxis)

    // copy doubles into fits matrix
    mtx.setFloatValues(0, data)

    // populate from template
    mtx.setCrpix(template.crpix)
    mtx.setCrval(template.crval)
    mtx.setCdelt(template.cdelt)

    // build header
    var hdr: FitsHeader = mtx.getHeader()
    hdr.addKeyword(new FitsKeyword("", ""))
    var fitsWCS = new FitsWCS(hdr)
    hdr.addKeyword(new FitsKeyword("CTYPE1", "= " + template.ctype(0)))
    hdr.addKeyword(new FitsKeyword("CTYPE2", "= " + template.ctype(1)))

    // construct hd unit
    val hdu = new FitsHDUnit(hdr, mtx)

    // build and write out file
    var file: FitsFile = new FitsFile()
    file.addHDUnit(hdu)
    file.writeFile(path)
  }
}
