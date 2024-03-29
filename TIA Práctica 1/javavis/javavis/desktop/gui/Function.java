package javavis.desktop.gui;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import javavis.base.JIPException;
import javavis.jip2d.base.JIPFunction;
import javavis.jip2d.base.JIPImage;
import javavis.jip2d.base.JIPSequence;
import javavis.jip2d.base.geometrics.JIPImgGeometric;
import javavis.jip2d.functions.FGeoToGray;



/**
 * A class containing the JIPFunction which is loaded according to  a string identifier of the desired function.
 * @author     Miguel Cazorla
 * @author     Boyan Bonev
 * @version     0.1
 * @date     5-2006
 */
public class Function {
	
	private static Logger logger = Logger.getLogger(Function.class);

	/**
	 * The JIPFunction itself.
	 */
	public JIPFunction jipfunction = null;

	/**
	 * The resulting JIP image;
	 */
	public JIPImage resultingImage=null;

	/**
	 * The resulting JIP image to show. Used for geometrical data;
	 */
	public JIPImage resultingImageToShow=null;
	
	/**
	 * The resulting JIP sequence;
	 */
	public JIPSequence resultingSequence=null;
	
	/**
	 * Wether to apply to a complete sequence or to a single image.
	 */
	public boolean applyToSequence=false;
	
	/**
	 * @uml.property  name="name"
	 */
	private String name;
	//To be able to access the previous.
	private ArrayList<DrawFunction> functionList;
	private DrawFunction mynode = null;

	
	/**
	 * Constructor. Finds the class corresponding to the name 
	 * of the function, and instantiates it.
	 * @param n the name
	 * @param functionArrayList the functionlist
	 */
	public Function(String n, ArrayList<DrawFunction> functionArrayList) {
		name=n;
		functionList = functionArrayList;
		//Instantiate the JIPFunction class, according to the name  
		try {
			Class fun = Class.forName("javavis.jip2d.functions."+name.trim());
			jipfunction = (JIPFunction)fun.newInstance();
		}
		catch (ClassNotFoundException e) {
			logger.error("Function "+name+" not found.\n");
		}
		catch (Exception ec) {
			logger.error(ec);
		}

	}
	
	/**
	 * Has to be set after construction, refers to the DrawFunction
	 * node which contains this Function. Needed for finding the 
	 * previous node in the list of functions.
	 * @param node the DrawFunction node.
	 */
	public void setParentNode(DrawFunction node){
		mynode = node;
	}
	
	/**
	 * Get the name of the function.
	 * @return  A String containing the name.
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Executes the function, if possible.
	 * @return Code error. See RunResult
	 */
	public RunResult run (){
		resultingSequence = null;
		resultingImage = null;
		if (jipfunction.paramsOK()) {
			Function previous = getPrevious();
			JIPSequence seq=null;
			JIPImage img=null;
			if( previous != null){
				seq = previous.resultingSequence;
				img = previous.resultingImage;
			}
			else { 
				//This is the first function (usually FLoadImage)
				if(name.equalsIgnoreCase("FLoadImage")) {
					try {
						if(applyToSequence)
							resultingSequence = jipfunction.processSeq(seq);
						else
							resultingImage = jipfunction.processImg(img);
					}
					catch (JIPException e) {logger.error(e);}

					if( resultingSequence != null || resultingImage != null) {
						copyImage();
						return RunResult.OK;
					}
					else
						return RunResult.OPENUNSUCCESS;
				}
				else //Not FLoadImage, and first node? no input image!
					return RunResult.NOINPUTIMAGES;
			}
			try {
				if(applyToSequence && seq !=null)
					resultingSequence = jipfunction.processSeq(seq);
				else
					if( img !=null)
						resultingImage = jipfunction.processImg(img);
					else
						return RunResult.NOINPUTIMAGES;
			}
			catch (JIPException e) {logger.error(e);}
			
			if( resultingSequence != null || resultingImage != null) {
				// If the image to show is geometrical, convert it to bitmap
				copyImage();
				return RunResult.OK;
			}
			else
				return RunResult.IMAGERETNULL;
		}
		return RunResult.WRONGPARAM;
	}
	
	private void copyImage () {
		JIPImage imgAux=null;
		if (applyToSequence)
			try {
				imgAux = resultingSequence.getFrame(0);
			} catch (JIPException e1) {logger.error(e1);}
		else
			imgAux = resultingImage;
		if (imgAux instanceof JIPImgGeometric) {
			JIPFunction func = new FGeoToGray();
			try {
				resultingImageToShow =  func.processImg(imgAux);
			}
			catch (JIPException e){logger.error(e);}
		}
		else
			resultingImageToShow = resultingImage;
	}
	
	/**
	 * Obtain the previous function in the list, to get its result.
	 * @return the previous Function in the list.
	 */
	public Function getPrevious(){
		int myindex = functionList.indexOf(mynode);
		if( myindex > 0)
			return functionList.get(myindex-1).func;
		return null;
	}

}
