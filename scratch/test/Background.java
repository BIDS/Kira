import java.io.*;

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
		public float globalback;
		private float globalrms;
		public Sepbackmap(int w, int h, float globalback, float globalrms){
	    	this.w = w;
	    	this.h = h;
	    	this.globalback = globalback;
	    	this.globalrms = globalrms;
		}
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
    public native int sep_makeback(double[][] data, Object mask, int dtype, 
				   int mdtype, int w, int h, int bw, int bh, 
				   double mthresh, int fw, int fh, double fthresh, 
				   Sepbackmap backmap);

    public native int sep_backarray(Sepbackmap bkmap, Object[] arr, int dtype);

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
    

    public Sepbackmap backmap = new Sepbackmap(0, 0, 0, 0);
    public float globalback;
    public float globalrms;

    public Background(double[][] data, Object mask, double maskthresh, int bw, int bh, int fw, int fh, double fthresh){
		int status = this.sep_makeback(data, (Object)mask, 82, 82, 6, 6, bw, bh, 0.0, fw, fh, 0.0, backmap);
    }

    public static void main (String[] args) {
		double[][] data = new double[6][6];
		for(int i=0; i<6; i++) {
	    	for(int j=0; j<6; j++) {
			data[i][j] = 0.1;
	    	}
		}
		data[1][1] = 1.0;
		data[4][1] = 1.0;
		data[1][4] = 1.0;
		data[4][4] = 1.0;

		Background bkg = new Background(data, (Object)null, 0.0, 3, 3, 1, 1, 0.0);
		System.out.println("JAVA: globalback: "+bkg.globalback+"\t globalrms: "+bkg.globalrms);
    }
}
