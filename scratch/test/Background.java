import java.io.*;
import java.lang.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Background { 

    /*macro definitions from sep.h*/
    final int SEP_TBYTE = 11;
    final int SEP_TINT = 31;
    final int SEP_TFLOAT = 42;
    final int SEP_TDOUBLE = 82;

    final int[] SUPPORTED_IMAGE_DTYPES = new int[]{SEP_TDOUBLE, SEP_TFLOAT, SEP_TINT};

    /*input flag values (C macros)*/
    final int SEP_ERROR_IS_VAR = 0x0001;
    final int SEP_ERROR_IS_ARRAY = 0x0002;
    final int SEP_MASK_IGNORE = 0x0004;

    /*output flag values accessible from Java*/
    final short OBJ_MERGED = 0x0001;
    final short OBJ_TRUNC = 0x0002;
    final short OBJ_DOVERFLOW = 0x0004;
    final short OBJ_SINGU = 0x0008;
    final short APER_TRUNC = 0x0010;
    final short APER_HASMASKED = 0x0020;
    final short APER_ALLMASKED = 0x0040;
    final short APER_NONPOSITIVE = 0x0080;

    /*macro definition from sepcore.h*/
    final int MEMMORY_ALLOC_ERROR = 1;

    class Sepbackmap{
		private int w;
		private int h;
		private float globalback;
		private float globalrms;
		private int bw, bh;
		private int nx, ny;
		private int n;
		private float[] back;
		private float[] dback;
		private float[] sigma;
		private float[] dsigma;
    }

    class Sepobj{
	private float thresh;
	private int npix;
	private int tnpix;
	private int xmin, xmax, ymin, ymax;
	private double x, y;
	private double x2, y2, xy;
	private float a, b, theta;
	private float cxx, cyy, cxy;
	private float cflux;
	private float flux;
	private float cpeak;
	private float peak;
	private int xpeak, ypeak;
	private int xcpeak, ycpeak;
	private short flag;
	private int pix;
	public Sepobj(float thresh, int npix, int tnpix, int xmin, int xmax, int ymin, int ymax, double x, double y, double x2, double y2, double xy, float a, float b, float theta, float cxx, float cyy, float cxy, float cflux, float flux, float cpeak, float peak, int xpeak, int ypeak, int xcpeak, int ycpeak, short flag, int pix){
	    this.thresh = thresh;
	    this.npix = npix;
	    this.tnpix = tnpix;
	    this.xmin = xmin;
	    this.xmax = xmax;
	    this.ymin = ymin;
	    this.ymax = ymax;
	    this.x = x;
	    this.y = y;
	    this.x2 = x2;
	    this.y2 = y2;
	    this.xy = xy;
	    this.a = a;
	    this.b = b;
	    this.theta = theta;
	    this.cxx = cxx;
	    this.cyy = cyy;
	    this.cxy = cxy;
	    this.cflux = cflux;
	    this.flux = flux;
	    this.cpeak = cpeak;
	    this.peak = peak;
	    this.xpeak = xpeak;
	    this.ypeak = ypeak;
	    this.xcpeak = xcpeak;
	    this.ycpeak = ycpeak;
	    this.flag = flag;
	    this.pix = pix;
	}
    }

    /*trying out the opaque pointer for reference in C*/
    public native int sep_makeback(byte[] data, boolean[][] mask, int dtype, 
				   int mdtype, int w, int h, int bw, int bh, 
				   double mthresh, int fw, int fh, double fthresh, 
				   Sepbackmap backmap);

    public native int sep_backarray(Sepbackmap bkmap, byte[] arr, int dtype, float[] back, float[] dback, float[] sigma, float[] dsigma);

    public native int sep_backrmsarray(Sepbackmap bkmap, Object[] arr, int dtype);

    public native int sep_subbackarray(Sepbackmap bkmap, Object[] arr, int dtype);

    public native void sep_freeback(Sepbackmap bkmap);

    public native int sep_extract(Object image, Object noise, int dtype, 
				  int ndtype, short noise_flag, int w, int h, 
				  float thresh, int minarea, float conv, int convw, 
				  int convh, int deblend_nthresh, double deblend_cont, 
				  int clean_flag, double clean_param, Sepobj[][] objects, 
				  int nobj);

    public native void sep_freeobjarray(Sepobj[] objects, int nobj);

    public native int sep_sum_circle(byte[] data, byte[] error, byte[] mask, 
				     int dtype, int edtype, int mdtype, int w, int h, 
				     double maskthresh, double gain, short inflags, 
				     double[] x, double[] y, double r, int subpix, double[] sum, 
				     double[] sumerr, double[] area, short[] flag);

    public native int sep_sum_circann(byte[] data, byte[] error, byte[] mask, 
				     int dtype, int edtype, int mdtype, int w, int h, 
				     double maskthresh, double gain, short inflags, 
				     double[] x, double[] y, double rin, double rout, 
				     int subpix, double[] sum, double[] sumerr, double[] area, 
				     short[] flag);

    public native int sep_sum_ellipse(byte[] data, byte[] error, byte[] mask, 
				      int dtype, int edtype, int mdtype, int w, int h, 
				      double maskthresh, double gain, short inflags, 
				      double x[], double y[], double a, double b, 
				      double theta, double r, int subpix, double[] sum, 
				      double[] sumerr, double[] area, short[] flag);

    public native int sep_sum_ellipann(byte[] data, byte[] error, byte[] mask, 
				       int dtype, int edtype, int mdtype, int w, int h, 
				       double maskthresh, double gain, short inflags, 
				       double[] x, double[] y, double a, double[] b, 
				       double[] theta, double rin, double rout, int subpix, 
				       double[] sum, double[] sumerr, double[] area, short[] flag);

    public native int sep_kron_radius(Object data, Object mask, int dtype, int mdtype, 
				      int w, int h, double maskthresh, double x, double y, 
				      double cxx, double cyy, double cxy, double r, 
				      double kronrad, short flag);

    public native int sep_ellipse_axes(double cxx, double cyy, double cxy, double a, double b, double theta);

    public native void sep_ellipse_coeffs(double a, double b, double theta, double cxx, double cyy, double cxy);

    public native void sep_set_ellipse(String arr, int w, int h, double x, double y, 
				       double cxx, double cyy, double cxy, double r, short val);

    public native void sep_set_extract_pixstack(int val);
    public native int sep_get_extract_pixstack();
    
    public native void sep_get_errmsg(int status, String errtxt);
    public native void sep_get_errdetail(String errtext);

    static { System.loadLibrary("BackgroundImpl"); }
    

    public Sepbackmap backmap = new Sepbackmap();

    public Background(double[][] matrix, boolean[][] maskmatrix, double maskthresh, int bw, int bh, int fw, int fh, double fthresh){
    	byte[] data = flatten(matrix);
		int status = this.sep_makeback(data, maskmatrix, 82, 11, 6, 6, bw, bh, 0.0, fw, fh, 0.0, this.backmap);
    }

    public double[][] back(int dtype){
    	double result[][] = new double[backmap.h][backmap.w];

    	byte[] data = new byte[backmap.h * backmap.w * 8];
    	int status = sep_backarray(this.backmap, data, dtype, this.backmap.back, this.backmap.dback, this.backmap.sigma, this.backmap.dsigma);
    	System.out.println("");

    	result = deflatten(data, backmap.h, backmap.w);
    	return result;
    }

    public byte[] flatten(double[][] matrix){
    	int h = matrix.length;
    	int w = matrix[0].length;
    	byte[] ret = new byte[h*w*8];


    	for(int i=0; i<matrix.length; i++){
    		for(int j=0; j<matrix[i].length; j++){
    			byte[] output = new byte[8];
    			long l = Double.doubleToLongBits(matrix[i][j]);
				for(int k = 0; k < 8; k++) 
					ret[(i*w+j)*8+k] = (byte)((l >> (k * 8)) & 0xff);
    		}
    	}
    	/*for(int i=0; i<h*w*8; i++){
    		Byte b = ret[i];
    		System.out.println(b.intValue()+", ");
    	}*/
    	return ret;
    }

    public void sum_circle(double[][] matrix, double[] x, double[] y, double r, double[] bkgann, int subpix){
    	float var = (float)0.0;
    	float err = (float)0.0;
    	float gain = (float)0.0;
    	double[][] mask = null;
    	double maskthresh = 0.0;


    	/*manually setting the parameters below*/
    	int dtype = SEP_TDOUBLE;
    	int edtype = 0;
    	int mdtype = 0;
    	int h = matrix.length;
    	int w = matrix[0].length;
    	byte[] ptr = flatten(matrix);
    	byte[] eptr = null;
    	byte[] mptr = null;
    	float scalaerr = (float)0.0;
    	short inflag = 0;
    	int status = 0;
    	gain = (float)0.0;

	    double[] sum = new double[x.length];
    	double[] sumerr = new double[x.length];
    	double[] area = new double[x.length];
    	short[] flag = new short[x.length];
    	
    	if(bkgann == null){
    		/*this is the case where bkgann is null*/
    		status = sep_sum_circle(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, r, subpix, sum, sumerr, area, flag);
	    }
	    else{
			double[] flux1 = new double[x.length];
    		double[] fluxerr1 = new double[x.length];
    		double[] area1 = new double[x.length];
    		short[] flag1 = new short[x.length];
    		status = sep_sum_circle(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, r, subpix, flux1, fluxerr1, area1, flag1);

			double[] bkgflux = new double[x.length];
    		double[] bkgfluxerr = new double[x.length];
    		double[] bkgarea = new double[x.length];
    		short[] bkgflag = new short[x.length];
    		double rin = bkgann[0];
    		double rout = bkgann[1];
    		inflag = (short)(inflag | SEP_MASK_IGNORE);
    		status = sep_sum_circann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, rin, rout, subpix, bkgflux, bkgfluxerr, bkgarea, bkgflag);    				    	
	    	
	    	for(int i=0; i<x.length; i++){
	    		sum[i] = flux1[i] - bkgflux[i]/bkgarea[i]*area1[i];
	    		sumerr[i] = fluxerr1[i]*fluxerr1[i]+(bkgfluxerr[i]/bkgarea[i]*area1[i]);
	    		flag[i] = flag1[i];
	    	}
	    }

    	System.out.print("sum: ");
    	for(int i=0; i<10; i++){
    		System.out.print(sum[i]+", ");
    	}
    	System.out.println("");
    }

    public void sum_circann(double[][] matrix, double[] x, double[] y, double rin, double rout){
    	float var = (float)0.0;
    	float err = (float)0.0;
    	float gain = (float)0.0;
    	double[][] mask = null;
    	double maskthresh = 0.0;
    	int[] bkgann = null;
    	int subpix = 5;

    	/*manually setting the parameters below*/
    	int dtype = SEP_TDOUBLE;
    	int edtype = 0;
    	int mdtype = 0;
    	int h = matrix.length;
    	int w = matrix[0].length;
    	byte[] ptr = flatten(matrix);
    	byte[] eptr = null;
    	byte[] mptr = null;
    	float scalaerr = (float)0.0;
    	short inflag = 0;
    	gain = (float)0.0;

    	double[] sum = new double[x.length];
    	double[] sumerr = new double[x.length];
    	double[] area = new double[x.length];
    	short[] flag = new short[x.length];

	    int status = sep_sum_circann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, rin, rout, subpix, sum, sumerr, area, flag);

    	/*System.out.print("sum: ");
    	for(int i=0; i<x.length; i++){
    		System.out.print(sum[i]+", ");
    	}
    	System.out.println("");*/
    }
    public void sum_ellipse(double[][] matrix, double[] x, double[] y, double a, double b, double theta, double r, double[] bkgann, int subpix){
    	float var = (float)0.0;
    	float err = (float)0.0;
    	float gain = (float)0.0;
    	double[][] mask = null;
    	double maskthresh = 0.0;

    	/*manually setting the parameters below*/
    	int dtype = SEP_TDOUBLE;
    	int edtype = 0;
    	int mdtype = 0;
    	int h = matrix.length;
    	int w = matrix[0].length;
    	byte[] ptr = flatten(matrix);
    	byte[] eptr = null;
    	byte[] mptr = null;
    	float scalaerr = (float)0.0;
    	short inflag = 0;
    	gain = (float)0.0;
    	int status = 0;

    	double[] sum = new double[x.length];
    	double[] sumerr = new double[x.length];
    	double[] area = new double[x.length];
    	short[] flag = new short[x.length];

    	if(bkgann == null){
		    status = sep_sum_ellipse(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, a, b, theta, r, subpix, sum, sumerr, area, flag);
		}
		else{
			double[] flux1 = new double[x.length];
    		double[] fluxerr1 = new double[x.length];
    		double[] area1 = new double[x.length];
    		short[] flag1 = new short[x.length];
    		status = sep_sum_ellipse(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, a, b, theta, r, subpix, flux1, fluxerr1, area1, flag1);

			double[] bkgflux = new double[x.length];
    		double[] bkgfluxerr = new double[x.length];
    		double[] bkgarea = new double[x.length];
    		short[] bkgflag = new short[x.length];
    		double rin = bkgann[0];
    		double rout = bkgann[1];
    		double[] barray = new double[x.length];
    		Arrays.fill(barray, b);
    		double[] thetaarray = new double[x.length];
    		Arrays.fill(thetaarray, theta);

    		status = sep_sum_ellipann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, a, barray, thetaarray, rin, rout, subpix, bkgflux, bkgfluxerr, bkgarea, bkgflag);    				    	
	    	
	    	for(int i=0; i<x.length; i++){
	    		sum[i] = flux1[i] - bkgflux[i]/bkgarea[i]*area1[i];
	    		sumerr[i] = fluxerr1[i]*fluxerr1[i]+(bkgfluxerr[i]/bkgarea[i]*area1[i]);
	    		flag[i] = flag1[i];
	    	}
		}    

    	System.out.print("sum: ");
    	for(int i=0; i<10; i++){
    		System.out.print(sum[i]+", ");
    	}
    	System.out.println("");
    }

    public void sum_ellipann(double[][] matrix, double[] x, double[] y, double a, double[] b, double[] theta, double rin, double rout, int subpix){
    	float var = (float)0.0;
    	float err = (float)0.0;
    	float gain = (float)0.0;
    	double[][] mask = null;
    	double maskthresh = 0.0;
    	int[] bkgann = null;
    	/*the default value of subpix should be 5*/
    	double r = 1.0;

    	/*manually setting the parameters below*/
    	int dtype = SEP_TDOUBLE;
    	int edtype = 0;
    	int mdtype = 0;
    	int h = matrix.length;
    	int w = matrix[0].length;
    	byte[] ptr = flatten(matrix);
    	byte[] eptr = null;
    	byte[] mptr = null;
    	float scalaerr = (float)0.0;
    	short inflag = 0;
    	gain = (float)0.0;


    	double[] sum = new double[x.length];
    	double[] sumerr = new double[x.length];
    	double[] area = new double[x.length];
    	short[] flag = new short[x.length];
    	/*this is the case where bkgann is null*/

    	
	    int status = sep_sum_ellipann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, a, b, theta, rin, rout, subpix, sum, sumerr, area, flag);
	    

    	/*System.out.print("sum: ");
    	for(int i=0; i<x.length; i++){
    		System.out.print(sum[i]+", ");
    	}
    	System.out.println("");*/

    	/*Will turn back to the case where bkgann is not null later*/
    }
    public double[][] deflatten(byte[] data, int h, int w){
    	double matrix[][] = new double[h][w];
    	for(int i=0; i<h; i++){
    		for(int j=0; j<w; j++){
    			byte[] bytes = new byte[8];
    			long l = 0L;
    			for(int k=0; k<8; k++){
    				bytes[7-k] = (byte)(data[(i*w+j)*8+k] & 0xff);
    			}
    			matrix[i][j] = ByteBuffer.wrap(bytes).getDouble();
    		}
    		System.out.println("");
    	}
    	return matrix;
    }

	public void setBack(float[] vback){
		this.backmap.back = new float[vback.length];
		for(int i=0; i<this.backmap.back.length; i++)
			this.backmap.back[i] = vback[i];
	}

	public void setDback(float[] vback){
		this.backmap.dback = new float[vback.length];
		for(int i=0; i<this.backmap.dback.length; i++)
			this.backmap.dback[i] = vback[i];
	}

	public void setSigma(float[] vsigma){
		this.backmap.sigma = new float[vsigma.length];
		for(int i=0; i<this.backmap.sigma.length; i++)
			this.backmap.sigma[i] = vsigma[i];
	}

	public void setDsigma(float[] vsigma){
		this.backmap.dsigma = new float[vsigma.length];
		for(int i=0; i<this.backmap.dsigma.length; i++)
			this.backmap.dsigma[i] = vsigma[i];
	}

    public static void main (String[] args) {
		double[][] matrix = new double[6][6];
		for(int i=0; i<6; i++) {
	    	for(int j=0; j<6; j++) {
				matrix[i][j] = 0.1;
	    	}
		}
		matrix[1][1] = 1.0;
		matrix[4][1] = 1.0;
		matrix[1][4] = 1.0;
		matrix[4][4] = 1.0;

		boolean[][] mask = new boolean[6][6];
		for(int i=0; i<6; i++) {
	    	for(int j=0; j<6; j++) {
				mask[i][j] = false;
	    	}
		}
		mask[1][1] = true;
		mask[4][1] = true;
		mask[1][4] = true;
		mask[4][4] = true;

		/*testing background*/
		Background bkg = new Background(matrix, mask, 0.0, 3, 3, 1, 1, 0.0);
		System.out.println("JAVA: Backmap: "+bkg.backmap.globalback+"\t globalrms: "+bkg.backmap.globalrms);
		System.out.println("JAVA: Backmap: w: "+bkg.backmap.w+"\t h: "+bkg.backmap.h);
		System.out.println("JAVA: Backmap: bw: "+bkg.backmap.bw+"\t h: "+bkg.backmap.bh);
		System.out.println("JAVA: Backmap: nx: "+bkg.backmap.nx+"\t ny: "+bkg.backmap.ny+"\t n: "+bkg.backmap.n);
		
		System.out.print("JAVA: Backmap: back: ");
		for(int i=0; i<bkg.backmap.back.length; i++){
			System.out.print(bkg.backmap.back[i]+"\t");
		}
		System.out.println("");
		
		System.out.print("JAVA: Backmap: dback: ");
		for(int i=0; i<bkg.backmap.dback.length; i++){
			System.out.print(bkg.backmap.dback[i]+"\t");
		}
		System.out.println("");
		
		System.out.print("JAVA: Backmap: sigma: ");
		for(int i=0; i<bkg.backmap.sigma.length; i++){
			System.out.print(bkg.backmap.sigma[i]+"\t");
		}
		System.out.println("");
		
		System.out.print("JAVA: Backmap: dsigma: ");
		for(int i=0; i<bkg.backmap.dsigma.length; i++){
			System.out.print(bkg.backmap.dsigma[i]+"\t");
		}
		System.out.println("");


		double[][] back = bkg.back(82);
		for(int i=0; i<back.length; i++){
    		for(int j=0; j<back[i].length; j++){
    			System.out.print(back[i][j]+", ");
    		}
    		System.out.println("");
    	}

    	/*testing aperture with different dtypes*/
    	double pi = 3.14159;
    	int dim = 1000;
    	int naper = dim;
    	double r = 3;
    	double[] x = new double[dim];
    	double[] y = new double[dim];
    	Random random = new Random();
    	for(int i=0; i<dim; i++){
    		x[i] = random.nextDouble()*(800-200)+200.0;
    		y[i] = random.nextDouble()*(800-200)+200.0;
    	}

    	/*for(int i=0; i<dim; i++){
			System.out.print(x[i]+", ");
    	}
    	System.out.println("");*/
    	double[][] matrix2 = new double[dim][dim];
    	for(int i=0; i<dim; i++){
    		for(int j=0; j<dim; j++){
    			matrix2[i][j] = 1.0;
    		}
    	}

		System.out.println("=========sep_sum_circle()=========");
    	bkg.sum_circle(matrix2, x, y, r, null, 5);*/

    	System.out.println("=========sep_sum_ellipse()=========");
    	bkg.sum_ellipse(matrix2, x, x, 0.3, 0.3, 0.0, r, null, 5);

    	System.out.println("=========sep_sum_circann()=========");
    	bkg.sum_circann(matrix2, x, y, 0.0, 3.0);

    	System.out.println("=========sep_sum_ellipann()=========");
    	double[] theta = new double[dim];
    	double[] ratio = new double[dim];
    	for(int i=0; i<dim; i++){
    		theta[i] = random.nextDouble()*(pi)-pi/2;
    		ratio[i] = random.nextDouble()*(0.8)+0.2;
    	}
    	double rin = 3.0;
    	double rout = rin * 1.1;

    	bkg.sum_ellipann(matrix2, x, y, 1.0, ratio, theta, rin, rout, 0);

    	System.out.println("=========sep_sum_circle() with bkgann=========");
    	double[] bkgann = new double[]{0.0, 5.0};
    	bkg.sum_circle(matrix2, x, y, r, bkgann, 1);

    	System.out.println("=========sep_sum_ellipse() with bkgann=========");
    	double[] bkgann = new double[]{0.0, 5.0};
    	bkg.sum_ellipse(matrix2, x, y, 2.0, 1.0, pi/4, r, bkgann, 1);

    }
}
