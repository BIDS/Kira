import org.eso.fits.*;
import java.util.*;
import java.io.*;

public class Fits{
  public static double[][] load(String path){
  	double[][] dmatrix = null;
  	try{
	  //FitsFile file = new FitsFile("/Users/zhaozhang/projects/scratch/java/test/data/image.fits");
	  FitsFile file = new FitsFile(path);
	  FitsHDUnit hdu = file.getHDUnit(0);
	  FitsMatrix dm = (FitsMatrix)hdu.getData();
	  int[] naxis = dm.getNaxis();
	  int ncol = naxis[0];
	  int nval = dm.getNoValues();
	  int nrow = nval/ncol;

	  float[][] matrix = new float[nrow][ncol];
	  for(int i=0; i<nrow; i++)
		dm.getFloatValues(i*ncol, ncol, matrix[i]);

	  dmatrix = new double[nrow][ncol];
	  for(int i=0; i<matrix.length; i++)
	    for(int j=0; j<matrix[i].length; j++)
	    	dmatrix[i][j] = matrix[i][j];	  
	}
	catch (FitsException e) {
	  System.out.println("Error: is not a FITS file >");
	} catch (IOException e) {
	  System.out.println("Error: cannot open file >");
	}	
	return dmatrix;
  }
  public static void main(String[] args){
  	double[][] matrix = load("/Users/zhaozhang/projects/scratch/java/test/data/image.fits");
  	for(int i=0; i<matrix.length; i++){
	  for(int j=0; j<matrix[i].length; j++)
	    System.out.print(matrix[i][j]+", ");
	  System.out.println("");
	}
  }
}
