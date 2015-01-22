import java.io.*;
import java.lang.*;
import java.nio.ByteBuffer;

public class Background { 

    /*macro definitions from sep.h*/
    final int SEP_TBYTE = 11;
    final int SEP_TINT = 31;
    final int SEP_TFLOAT = 42;
    final int SEP_TDOUBLE = 82;

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
    public native int sep_makeback(byte[] data, Object mask, int dtype, 
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

    public native int sep_sum_circle(Object data, Object error, Object mask, 
				     int dtype, int edtype, int mdtype, int w, int h, 
				     double maskthresh, double gain, short inflags, 
				     double x, double y, double r, int subpix, double sum, 
				     double sumerr, double area, short flag);

    public native int sep_sum_cirann(Object data, Object error, Object mask, 
				     int dtype, int edtype, int mdtype, int w, int h, 
				     double maskthresh, double gain, short inflags, 
				     double x, double y, double rin, double rout, 
				     int subpix, double sum, double sumerr, double area, 
				     short flag);

    public native int sep_sum_ellipse(Object data, Object error, Object mask, 
				      int dtype, int edtype, int mdtype, int w, int h, 
				      double maskthresh, double gain, short inflags, 
				      double x, double y, double a, double b, 
				      double theta, double r, int subpix, double sum, 
				      double sumerr, double area, short flag);

    public native int sep_sum_ellipann(Object data, Object error, Object mask, 
				       int dtype, int edtype, int mdtype, int w, int h, 
				       double maskthresh, double gain, short inflags, 
				       double x, double y, double a, double b, 
				       double theta, double rin, double rout, int subpix, 
				       double sum, double sumerr, double area, short flag);

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

    public Background(double[][] matrix, Object mask, double maskthresh, int bw, int bh, int fw, int fh, double fthresh){
    	byte[] data = flatten(matrix);
		int status = this.sep_makeback(data, (Object)mask, 82, 82, 6, 6, bw, bh, 0.0, fw, fh, 0.0, this.backmap);
		//this.backmap = new Sepbackmap(matrix.length, matrix[0].length, this.globalback, this.globalrms);
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

		Background bkg = new Background(matrix, (Object)null, 0.0, 3, 3, 1, 1, 0.0);
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

    }
}
