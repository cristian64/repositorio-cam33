package javavis.jip3d.geom;

import java.io.Serializable;

import javax.vecmath.Matrix4d;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public abstract class MyTransform implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5176946840995115699L;
	public DenseDoubleMatrix2D mat;
	public double trX, trY, trZ, angX, angY, angZ;

	public MyTransform()
	{
		mat = new DenseDoubleMatrix2D(4, 4);
		for(int cont=0;cont<4;cont++) mat.setQuick(cont, cont, 1);
		trX = trY = trZ = angX = angY = angZ = 0;
	}

	public MyTransform(MyTransform t)
	{
		int cont, cont2;
		mat = new DenseDoubleMatrix2D(4, 4);
		for(cont=0;cont<4;cont++)
		{
			for(cont2=0;cont2<4;cont2++)
				mat.set(cont, cont2, t.get(cont, cont2));
		}
		trX = t.trX;
		trY = t.trY;
		trZ = t.trZ;
		angX = t.angX;
		angY = t.angY;
		angZ = t.angZ;
	}

	public MyTransform(DoubleMatrix2D m)
	{
		int row, col;
		int maxrow, maxcol;
		maxrow = m.rows();
		maxcol = m.columns();
		mat = new DenseDoubleMatrix2D(4, 4);
		for(int cont=0;cont<4;cont++) mat.set(cont, cont, 1);

		if(maxcol>3)
		{
			trX = m.get(0, 3);
			trY = m.get(1, 3);
			trZ = m.get(2, 3);
		}
		else trX = trY = trZ = 0;

		angY = Math.asin(m.getQuick(0,2));
		double cosy;
		if (Math.abs(angY-Math.PI/2.0)<0.00001) cosy=1.0;
	    else cosy=Math.cos(angY);
		double sinx=-m.getQuick(1,2)/cosy;
	    double cosx=m.getQuick(2,2)/cosy;
	    double sinz=-m.getQuick(0,1)/cosy;
	    double cosz=m.getQuick(0,0)/cosy;
	    if (Math.abs(cosx)<0.0000000001) {
	        if (sinx<0) angX=-Math.PI/2.0;
	        else angX=Math.PI/2.0;
	    }
	    else angX=Math.atan2(sinx, cosx);
	    if (Math.abs(cosz)<0.0000000001) {
	        if (sinz<0) angZ=-Math.PI/2.0;
	        else angZ=Math.PI/2.0;
	    }
	    else angZ=Math.atan2(sinz, cosz);

		for(row=0;row<maxrow;row++)
			for(col=0;col<maxcol;col++)
				mat.set(row, col, m.get(row, col));
	}


	public void set(int i, int j, double value)
	{
		mat.setQuick(i, j, value);
	}

	public double get(int i, int j)
	{
		return mat.getQuick(i, j);
	}

	public void assign(double [][]values)
	{
		mat.assign(values);
	}

	public void assign(double []values)
	{
		int i, j;
		if(values.length == 9)
		{
			for(i=0;i<3;i++)
				for(j=0;j<3;j++)
					set(i,j,values[i*3+j]);
		}
	}

	public void applyTransform(MyTransform t)
	{
		DoubleMatrix2D result = null;
		result = mat.zMult(t.mat, null);
		mat.assign(result);

		trX = mat.get(0, 3);
		trY = mat.get(1, 3);
		trZ = mat.get(2, 3);

		angY = Math.asin(mat.getQuick(0,2));
		double cosy;
		if (Math.abs(angY-Math.PI/2.0)<0.00001) cosy=1.0;
	    else cosy=Math.cos(angY);
		double sinx=-mat.getQuick(1,2)/cosy;
	    double cosx=mat.getQuick(2,2)/cosy;
	    double sinz=-mat.getQuick(0,1)/cosy;
	    double cosz=mat.getQuick(0,0)/cosy;
	    if (Math.abs(cosx)<0.0000000001) {
	        if (sinx<0) angX=-Math.PI/2.0;
	        else angX=Math.PI/2.0;
	    }
	    else angX=Math.atan2(sinx, cosx);
	    if (Math.abs(cosz)<0.0000000001) {
	        if (sinz<0) angZ=-Math.PI/2.0;
	        else angZ=Math.PI/2.0;
	    }
	    else angZ=Math.atan2(sinz, cosz);
	    
	}

	//este metodo compatibiliza mis transformaciones con Java3D
	public Matrix4d getMatrix4d()
	{
		Matrix4d ret = new Matrix4d();

		ret.m00 = mat.get(0, 0);
		ret.m01 = mat.get(0, 1);
		ret.m02 = mat.get(0, 2);
		ret.m03 = mat.get(0, 3);
		ret.m10 = mat.get(1, 0);
		ret.m11 = mat.get(1, 1);
		ret.m12 = mat.get(1, 2);
		ret.m13 = mat.get(1, 3);
		ret.m20 = mat.get(2, 0);
		ret.m21 = mat.get(2, 1);
		ret.m22 = mat.get(2, 2);
		ret.m23 = mat.get(2, 3);
		ret.m30 = mat.get(3, 0);
		ret.m31 = mat.get(3, 1);
		ret.m32 = mat.get(3, 2);
		ret.m33 = mat.get(3, 3);

		return ret;
	}
	abstract public String toString();

	abstract public MyTransform getInverse();

}
